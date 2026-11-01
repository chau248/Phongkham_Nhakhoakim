package nhakhoakim.controller;

import java.util.List;
import java.util.function.Supplier;
import nhakhoakim.dao.MaGenerator;
import nhakhoakim.dao.NghiepVuException;
import nhakhoakim.dao.NhanVienDAO;
import nhakhoakim.dao.PhienLamViec;
import nhakhoakim.dao.TaiKhoanDAO;
import nhakhoakim.entity.NhanVien;
import nhakhoakim.entity.TaiKhoan;
import nhakhoakim.entity.VaiTro;

// Nghiệp vụ tài khoản (TK-01 đến TK-04): đăng nhập, khóa sau 5 lần sai, tạo tài khoản, đổi và cấp lại mật khẩu, khóa và mở khóa
public class TaiKhoanController {

    public static final int SO_LAN_SAI_TOI_DA = 5;
    public static final int DO_DAI_TEN_TOI_THIEU = 4;
    public static final int DO_DAI_MAT_KHAU_TOI_THIEU = 6;

    private TaiKhoanDAO taiKhoanDAO;
    private NhanVienDAO nhanVienDAO;
    private NhatKyController nhatKy = new NhatKyController();
    private Supplier<NhanVien> nguoiDung;

    // Dùng DAO thật và người đăng nhập thật của chương trình
    public TaiKhoanController() {
        this(new TaiKhoanDAO(), new NhanVienDAO(), PhienLamViec::getNhanVienHienTai);
    }

    // Dùng DAO và người dùng truyền vào (để kiểm thử)
    public TaiKhoanController(TaiKhoanDAO taiKhoanDAO, NhanVienDAO nhanVienDAO, Supplier<NhanVien> nguoiDung) {
        this.taiKhoanDAO = taiKhoanDAO;
        this.nhanVienDAO = nhanVienDAO;
        this.nguoiDung = nguoiDung;
    }

    // Đăng nhập (TK-01, TK-02): trả về nhân viên đăng nhập; sai thì báo lỗi. Sai 5 lần liên tiếp thì khóa tài khoản.
    // Câu báo "sai tên đăng nhập hoặc mật khẩu" không cho biết tên đăng nhập có tồn tại hay không.
    public NhanVien dangNhap(String tenDangNhap, String matKhau) {
        if (tenDangNhap == null || tenDangNhap.trim().isEmpty() || matKhau == null || matKhau.isEmpty()) {
            throw new NghiepVuException("Hãy nhập tên đăng nhập và mật khẩu");
        }
        String ten = tenDangNhap.trim();
        TaiKhoan tk = taiKhoanDAO.timTheoTenDangNhap(ten);
        if (tk == null) {
            nhatKy.ghiNhatKy(null, "Đăng nhập thất bại", "TaiKhoan", null, "Tên đăng nhập không tồn tại: " + ten);
            throw new NghiepVuException("Sai tên đăng nhập hoặc mật khẩu");
        }
        String maNV = tk.getMaNV().getMaNV();
        if (tk.isTrangThaiKhoa()) {
            nhatKy.ghiNhatKy(maNV, "Đăng nhập thất bại", "TaiKhoan", tk.getMaTaiKhoan(), "Tài khoản đang bị khóa");
            throw new NghiepVuException("Tài khoản đã bị khóa. Hãy liên hệ quản lý để được mở khóa");
        }
        NhanVien nv = nhanVienDAO.timTheoMa(maNV);
        if (nv == null || !NhanVienController.DANG_LAM.equals(nv.getTrangThai())) {
            nhatKy.ghiNhatKy(maNV, "Đăng nhập thất bại", "TaiKhoan", tk.getMaTaiKhoan(), "Nhân viên đã nghỉ việc");
            throw new NghiepVuException("Nhân viên này đã nghỉ việc nên không đăng nhập được");
        }
        if (!MatKhau.khop(matKhau, tk.getMatKhauHash())) {
            int soLanSai = tk.getSoLanDangNhapSai() + 1;
            boolean khoa = soLanSai >= SO_LAN_SAI_TOI_DA;
            taiKhoanDAO.ghiNhanDangNhapSai(tk.getMaTaiKhoan(), soLanSai, khoa);
            nhatKy.ghiNhatKy(maNV, khoa ? "Khóa tài khoản do đăng nhập sai" : "Đăng nhập thất bại", "TaiKhoan", tk.getMaTaiKhoan(),
                    "Sai mật khẩu lần " + soLanSai);
            if (khoa) {
                throw new NghiepVuException("Sai mật khẩu " + SO_LAN_SAI_TOI_DA + " lần liên tiếp nên tài khoản đã bị khóa. Hãy liên hệ quản lý");
            }
            throw new NghiepVuException("Sai tên đăng nhập hoặc mật khẩu (còn " + (SO_LAN_SAI_TOI_DA - soLanSai) + " lần thử)");
        }
        if (!MatKhau.daBam(tk.getMatKhauHash())) {
            taiKhoanDAO.nangCapBam(tk.getMaTaiKhoan(), MatKhau.bam(matKhau)); // mật khẩu cũ chữ thường được băm lại ngay lần đăng nhập đầu
        }
        taiKhoanDAO.ghiNhanDangNhapDung(tk.getMaTaiKhoan());
        nhatKy.ghiNhatKy(maNV, "Đăng nhập", "TaiKhoan", tk.getMaTaiKhoan(), null);
        return nv;
    }

    // Đăng xuất: ghi nhật ký rồi xóa người dùng hiện tại (TK-02)
    public void dangXuat() {
        NhanVien nv = nguoiDung.get();
        if (nv != null) {
            nhatKy.ghiNhatKy(nv.getMaNV(), "Đăng xuất", "TaiKhoan", null, null);
        }
        PhienLamViec.dangXuat();
    }

    // Tất cả tài khoản (chỉ Quản lý)
    public List<TaiKhoan> layTatCa() {
        Quyen.yeuCau(nguoiDung, "xem danh sách tài khoản", VaiTro.QUAN_LY);
        return taiKhoanDAO.layTatCa();
    }

    // Tài khoản của người đang đăng nhập (để hiện thông tin và đổi mật khẩu); không có thì trả về null
    public TaiKhoan layTaiKhoanCuaToi() {
        NhanVien nv = Quyen.yeuCau(nguoiDung, "xem tài khoản của mình", VaiTro.NHA_SI, VaiTro.Y_TA, VaiTro.LE_TAN, VaiTro.QUAN_LY);
        return taiKhoanDAO.timTheoNhanVien(nv.getMaNV());
    }

    // Tạo tài khoản cho một nhân viên (chỉ Quản lý, TK-03, TK-04): mỗi nhân viên một tài khoản, vai trò tài khoản bằng vai trò nhân viên
    public TaiKhoan taoTaiKhoan(String maNV, String tenDangNhap, String matKhau) {
        NhanVien nguoi = Quyen.yeuCau(nguoiDung, "tạo tài khoản", VaiTro.QUAN_LY);
        NhanVien nv = maNV == null ? null : nhanVienDAO.timTheoMa(maNV);
        if (nv == null) {
            throw new NghiepVuException("Chưa chọn nhân viên");
        }
        if (!NhanVienController.DANG_LAM.equals(nv.getTrangThai())) {
            throw new NghiepVuException("Không tạo tài khoản cho nhân viên đã nghỉ việc");
        }
        if (taiKhoanDAO.timTheoNhanVien(maNV) != null) {
            throw new NghiepVuException(nv.getHoTen() + " đã có tài khoản (mỗi nhân viên chỉ một tài khoản)");
        }
        String ten = tenDangNhap == null ? "" : tenDangNhap.trim();
        kiemTraTenDangNhap(ten);
        if (taiKhoanDAO.timTheoTenDangNhap(ten) != null) {
            throw new NghiepVuException("Tên đăng nhập \"" + ten + "\" đã có người dùng");
        }
        kiemTraMatKhauMoi(matKhau);
        TaiKhoan tk = new TaiKhoan();
        tk.setMaTaiKhoan(MaGenerator.taoMa("TK"));
        tk.setMaNV(nv);
        tk.setTenDangNhap(ten);
        tk.setMatKhauHash(MatKhau.bam(matKhau));
        tk.setVaiTro(nv.getVaiTro());
        tk.setYeuCauDoiMatKhau(false);
        taiKhoanDAO.them(tk);
        nhatKy.ghiNhatKy(nguoi.getMaNV(), "Tạo tài khoản và gán vai trò", "TaiKhoan", tk.getMaTaiKhoan(),
                ten + " - " + nv.getHoTen() + " - " + nv.getVaiTro());
        return tk;
    }

    // Người đang đăng nhập đổi mật khẩu của chính mình: phải nhập đúng mật khẩu cũ
    public void doiMatKhauCuaToi(String matKhauCu, String matKhauMoi) {
        NhanVien nv = Quyen.yeuCau(nguoiDung, "đổi mật khẩu", VaiTro.NHA_SI, VaiTro.Y_TA, VaiTro.LE_TAN, VaiTro.QUAN_LY);
        TaiKhoan tk = taiKhoanDAO.timTheoNhanVien(nv.getMaNV());
        if (tk == null) {
            throw new NghiepVuException("Nhân viên này chưa có tài khoản");
        }
        if (!MatKhau.khop(matKhauCu, tk.getMatKhauHash())) {
            throw new NghiepVuException("Mật khẩu hiện tại không đúng");
        }
        kiemTraMatKhauMoi(matKhauMoi);
        if (matKhauMoi.equals(matKhauCu)) {
            throw new NghiepVuException("Mật khẩu mới phải khác mật khẩu hiện tại");
        }
        taiKhoanDAO.doiMatKhau(tk.getMaTaiKhoan(), MatKhau.bam(matKhauMoi), false);
        nhatKy.ghiNhatKy(nv.getMaNV(), "Đổi mật khẩu", "TaiKhoan", tk.getMaTaiKhoan(), null);
    }

    // Quản lý cấp lại mật khẩu cho một tài khoản (quên mật khẩu)
    public void capLaiMatKhau(String maTaiKhoan, String matKhauMoi) {
        NhanVien nguoi = Quyen.yeuCau(nguoiDung, "cấp lại mật khẩu", VaiTro.QUAN_LY);
        TaiKhoan tk = layTaiKhoan(maTaiKhoan);
        kiemTraMatKhauMoi(matKhauMoi);
        taiKhoanDAO.doiMatKhau(tk.getMaTaiKhoan(), MatKhau.bam(matKhauMoi), false);
        nhatKy.ghiNhatKy(nguoi.getMaNV(), "Cấp lại mật khẩu", "TaiKhoan", tk.getMaTaiKhoan(), tk.getTenDangNhap());
    }

    // Khóa tài khoản (chỉ Quản lý): không tự khóa chính mình và luôn còn ít nhất một Quản lý hoạt động (TK-04)
    public void khoa(String maTaiKhoan) {
        NhanVien nguoi = Quyen.yeuCau(nguoiDung, "khóa tài khoản", VaiTro.QUAN_LY);
        TaiKhoan tk = layTaiKhoan(maTaiKhoan);
        if (tk.getMaNV().getMaNV().equals(nguoi.getMaNV())) {
            throw new NghiepVuException("Không thể tự khóa tài khoản của chính mình");
        }
        if (tk.isTrangThaiKhoa()) {
            throw new NghiepVuException("Tài khoản này đã bị khóa");
        }
        if (VaiTro.QUAN_LY.equals(tk.getVaiTro()) && taiKhoanDAO.demQuanLyHoatDong() <= 1) {
            throw new NghiepVuException("Đây là tài khoản Quản lý hoạt động cuối cùng, không thể khóa");
        }
        taiKhoanDAO.datKhoa(maTaiKhoan, true);
        nhatKy.ghiNhatKy(nguoi.getMaNV(), "Khóa tài khoản", "TaiKhoan", maTaiKhoan, tk.getTenDangNhap());
    }

    // Mở khóa tài khoản và xóa bộ đếm đăng nhập sai (chỉ Quản lý); không mở khóa cho nhân viên đã nghỉ việc
    public void moKhoa(String maTaiKhoan) {
        NhanVien nguoi = Quyen.yeuCau(nguoiDung, "mở khóa tài khoản", VaiTro.QUAN_LY);
        TaiKhoan tk = layTaiKhoan(maTaiKhoan);
        if (!tk.isTrangThaiKhoa()) {
            throw new NghiepVuException("Tài khoản này đang hoạt động, không cần mở khóa");
        }
        if (!NhanVienController.DANG_LAM.equals(tk.getMaNV().getTrangThai())) {
            throw new NghiepVuException("Nhân viên đã nghỉ việc nên không mở khóa tài khoản");
        }
        taiKhoanDAO.datKhoa(maTaiKhoan, false);
        nhatKy.ghiNhatKy(nguoi.getMaNV(), "Mở khóa tài khoản", "TaiKhoan", maTaiKhoan, tk.getTenDangNhap());
    }

    private TaiKhoan layTaiKhoan(String maTaiKhoan) {
        TaiKhoan tk = maTaiKhoan == null ? null : taiKhoanDAO.timTheoMa(maTaiKhoan);
        if (tk == null) {
            throw new NghiepVuException("Chưa chọn tài khoản");
        }
        return tk;
    }

    // Tên đăng nhập từ 4 ký tự, không có khoảng trắng, chỉ gồm chữ, số, dấu chấm, gạch dưới, gạch ngang (TK-03)
    private void kiemTraTenDangNhap(String ten) {
        if (ten.length() < DO_DAI_TEN_TOI_THIEU) {
            throw new NghiepVuException("Tên đăng nhập phải từ " + DO_DAI_TEN_TOI_THIEU + " ký tự");
        }
        if (ten.length() > 50 || !ten.matches("[A-Za-z0-9._-]+")) {
            throw new NghiepVuException("Tên đăng nhập chỉ gồm chữ không dấu, số, dấu chấm, gạch dưới hoặc gạch ngang, tối đa 50 ký tự (không có khoảng trắng)");
        }
    }

    // Mật khẩu từ 6 ký tự (TK-03)
    private void kiemTraMatKhauMoi(String matKhau) {
        if (matKhau == null || matKhau.length() < DO_DAI_MAT_KHAU_TOI_THIEU) {
            throw new NghiepVuException("Mật khẩu phải từ " + DO_DAI_MAT_KHAU_TOI_THIEU + " ký tự");
        }
        if (matKhau.length() > 50) {
            throw new NghiepVuException("Mật khẩu dài tối đa 50 ký tự");
        }
    }
}
