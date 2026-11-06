package controller;

import java.util.List;
import java.util.function.Supplier;
import dao.TienIchDAO;
import dao.NghiepVuException;
import dao.NhanVienDAO;
import dao.TaiKhoanDAO;
import entity.NhanVien;
import entity.TaiKhoan;
import entity.HangSo;

// Nghiệp vụ tài khoản
public class TaiKhoanController {

    public static final int SO_LAN_SAI_TOI_DA = 5;
    public static final int DO_DAI_TEN_TOI_THIEU = 4;
    public static final int DO_DAI_MAT_KHAU_TOI_THIEU = 6;

    private TaiKhoanDAO taiKhoanDAO;
    private NhanVienDAO nhanVienDAO;
    private Supplier<NhanVien> nguoiDung;

    // Dùng DAO thật
    public TaiKhoanController() {
        this(new TaiKhoanDAO(), new NhanVienDAO(), PhienLamViec::getNhanVienHienTai);
    }

    // Dùng DAO và người dùng truyền vào
    public TaiKhoanController(TaiKhoanDAO taiKhoanDAO, NhanVienDAO nhanVienDAO, Supplier<NhanVien> nguoiDung) {
        this.taiKhoanDAO = taiKhoanDAO;
        this.nhanVienDAO = nhanVienDAO;
        this.nguoiDung = nguoiDung;
    }

    // Đăng nhập: trả về nhân viên đăng nhập
    public NhanVien dangNhap(String tenDangNhap, String matKhau) {
        if (tenDangNhap == null || tenDangNhap.trim().isEmpty() || matKhau == null || matKhau.isEmpty()) {
            throw new NghiepVuException("Hãy nhập tên đăng nhập và mật khẩu");
        }
        String ten = tenDangNhap.trim();
        TaiKhoan tk = taiKhoanDAO.timTheoTenDangNhap(ten);
        if (tk == null) {
            TienIchController.ghiNhatKy(null, "Đăng nhập thất bại", "TaiKhoan", null, "Tên đăng nhập không tồn tại: " + ten);
            throw new NghiepVuException("Sai tên đăng nhập hoặc mật khẩu");
        }
        String maNV = tk.getMaNV().getMaNV();
        if (tk.isTrangThaiKhoa()) {
            TienIchController.ghiNhatKy(maNV, "Đăng nhập thất bại", "TaiKhoan", tk.getMaTaiKhoan(), "Tài khoản đang bị khóa");
            throw new NghiepVuException("Tài khoản đã bị khóa. Hãy liên hệ quản lý để được mở khóa");
        }
        NhanVien nv = nhanVienDAO.timTheoMa(maNV);
        if (nv == null || !NhanVienController.DANG_LAM.equals(nv.getTrangThai())) {
            TienIchController.ghiNhatKy(maNV, "Đăng nhập thất bại", "TaiKhoan", tk.getMaTaiKhoan(), "Nhân viên đã nghỉ việc");
            throw new NghiepVuException("Nhân viên này đã nghỉ việc nên không đăng nhập được");
        }
        if (!TienIchController.khopMatKhau(matKhau, tk.getMatKhauHash())) {
            int soLanSai = tk.getSoLanDangNhapSai() + 1;
            boolean khoa = soLanSai >= SO_LAN_SAI_TOI_DA;
            taiKhoanDAO.ghiNhanDangNhapSai(tk.getMaTaiKhoan(), soLanSai, khoa);
            TienIchController.ghiNhatKy(maNV, khoa ? "Khóa tài khoản do đăng nhập sai" : "Đăng nhập thất bại", "TaiKhoan", tk.getMaTaiKhoan(),
                    "Sai mật khẩu lần " + soLanSai);
            if (khoa) {
                throw new NghiepVuException("Sai mật khẩu " + SO_LAN_SAI_TOI_DA + " lần liên tiếp nên tài khoản đã bị khóa. Hãy liên hệ quản lý");
            }
            throw new NghiepVuException("Sai tên đăng nhập hoặc mật khẩu (còn " + (SO_LAN_SAI_TOI_DA - soLanSai) + " lần thử)");
        }
        if (!TienIchController.daBamMatKhau(tk.getMatKhauHash())) {
            taiKhoanDAO.nangCapBam(tk.getMaTaiKhoan(), TienIchController.bamMatKhau(matKhau)); // Mật khẩu cũ chữ thường
        }
        taiKhoanDAO.ghiNhanDangNhapDung(tk.getMaTaiKhoan());
        TienIchController.ghiNhatKy(maNV, "Đăng nhập", "TaiKhoan", tk.getMaTaiKhoan(), null);
        return nv;
    }

    // Đăng xuất
    public void dangXuat() {
        NhanVien nv = nguoiDung.get();
        if (nv != null) {
            TienIchController.ghiNhatKy(nv.getMaNV(), "Đăng xuất", "TaiKhoan", null, null);
        }
        PhienLamViec.dangXuat();
    }

    // Tất cả tài khoản
    public List<TaiKhoan> layTatCa() {
        TienIchController.yeuCau(nguoiDung, "xem danh sách tài khoản", HangSo.VAI_TRO_QUAN_LY);
        return taiKhoanDAO.layTatCa();
    }

    // Tài khoản của người đang đăng nhập
    public TaiKhoan layTaiKhoanCuaToi() {
        NhanVien nv = TienIchController.yeuCau(nguoiDung, "xem tài khoản của mình", HangSo.VAI_TRO_NHA_SI, HangSo.VAI_TRO_Y_TA, HangSo.VAI_TRO_LE_TAN, HangSo.VAI_TRO_QUAN_LY);
        return taiKhoanDAO.timTheoNhanVien(nv.getMaNV());
    }

    // Tạo tài khoản cho một nhân viên
    public TaiKhoan taoTaiKhoan(String maNV, String tenDangNhap, String matKhau) {
        NhanVien nguoi = TienIchController.yeuCau(nguoiDung, "tạo tài khoản", HangSo.VAI_TRO_QUAN_LY);
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
        tk.setMaTaiKhoan(TienIchDAO.taoMa("TK"));
        tk.setMaNV(nv);
        tk.setTenDangNhap(ten);
        tk.setMatKhauHash(TienIchController.bamMatKhau(matKhau));
        tk.setVaiTro(nv.getVaiTro());
        tk.setYeuCauDoiMatKhau(false);
        taiKhoanDAO.them(tk);
        TienIchController.ghiNhatKy(nguoi.getMaNV(), "Tạo tài khoản và gán vai trò", "TaiKhoan", tk.getMaTaiKhoan(),
                ten + " - " + nv.getHoTen() + " - " + nv.getVaiTro());
        return tk;
    }

    // Người đang đăng nhập đổi mật
    public void doiMatKhauCuaToi(String matKhauCu, String matKhauMoi) {
        NhanVien nv = TienIchController.yeuCau(nguoiDung, "đổi mật khẩu", HangSo.VAI_TRO_NHA_SI, HangSo.VAI_TRO_Y_TA, HangSo.VAI_TRO_LE_TAN, HangSo.VAI_TRO_QUAN_LY);
        TaiKhoan tk = taiKhoanDAO.timTheoNhanVien(nv.getMaNV());
        if (tk == null) {
            throw new NghiepVuException("Nhân viên này chưa có tài khoản");
        }
        if (!TienIchController.khopMatKhau(matKhauCu, tk.getMatKhauHash())) {
            throw new NghiepVuException("Mật khẩu hiện tại không đúng");
        }
        kiemTraMatKhauMoi(matKhauMoi);
        if (matKhauMoi.equals(matKhauCu)) {
            throw new NghiepVuException("Mật khẩu mới phải khác mật khẩu hiện tại");
        }
        taiKhoanDAO.doiMatKhau(tk.getMaTaiKhoan(), TienIchController.bamMatKhau(matKhauMoi), false);
        TienIchController.ghiNhatKy(nv.getMaNV(), "Đổi mật khẩu", "TaiKhoan", tk.getMaTaiKhoan(), null);
    }

    // Quản lý cấp lại mật khẩu
    public void capLaiMatKhau(String maTaiKhoan, String matKhauMoi) {
        NhanVien nguoi = TienIchController.yeuCau(nguoiDung, "cấp lại mật khẩu", HangSo.VAI_TRO_QUAN_LY);
        TaiKhoan tk = layTaiKhoan(maTaiKhoan);
        kiemTraMatKhauMoi(matKhauMoi);
        taiKhoanDAO.doiMatKhau(tk.getMaTaiKhoan(), TienIchController.bamMatKhau(matKhauMoi), false);
        TienIchController.ghiNhatKy(nguoi.getMaNV(), "Cấp lại mật khẩu", "TaiKhoan", tk.getMaTaiKhoan(), tk.getTenDangNhap());
    }

    // Khóa tài khoản
    public void khoa(String maTaiKhoan) {
        NhanVien nguoi = TienIchController.yeuCau(nguoiDung, "khóa tài khoản", HangSo.VAI_TRO_QUAN_LY);
        TaiKhoan tk = layTaiKhoan(maTaiKhoan);
        if (tk.getMaNV().getMaNV().equals(nguoi.getMaNV())) {
            throw new NghiepVuException("Không thể tự khóa tài khoản của chính mình");
        }
        if (tk.isTrangThaiKhoa()) {
            throw new NghiepVuException("Tài khoản này đã bị khóa");
        }
        if (HangSo.VAI_TRO_QUAN_LY.equals(tk.getVaiTro()) && taiKhoanDAO.demQuanLyHoatDong() <= 1) {
            throw new NghiepVuException("Đây là tài khoản Quản lý hoạt động cuối cùng, không thể khóa");
        }
        taiKhoanDAO.datKhoa(maTaiKhoan, true);
        TienIchController.ghiNhatKy(nguoi.getMaNV(), "Khóa tài khoản", "TaiKhoan", maTaiKhoan, tk.getTenDangNhap());
    }

    // Mở khóa tài khoản
    public void moKhoa(String maTaiKhoan) {
        NhanVien nguoi = TienIchController.yeuCau(nguoiDung, "mở khóa tài khoản", HangSo.VAI_TRO_QUAN_LY);
        TaiKhoan tk = layTaiKhoan(maTaiKhoan);
        if (!tk.isTrangThaiKhoa()) {
            throw new NghiepVuException("Tài khoản này đang hoạt động, không cần mở khóa");
        }
        if (!NhanVienController.DANG_LAM.equals(tk.getMaNV().getTrangThai())) {
            throw new NghiepVuException("Nhân viên đã nghỉ việc nên không mở khóa tài khoản");
        }
        taiKhoanDAO.datKhoa(maTaiKhoan, false);
        TienIchController.ghiNhatKy(nguoi.getMaNV(), "Mở khóa tài khoản", "TaiKhoan", maTaiKhoan, tk.getTenDangNhap());
    }

    private TaiKhoan layTaiKhoan(String maTaiKhoan) {
        TaiKhoan tk = maTaiKhoan == null ? null : taiKhoanDAO.timTheoMa(maTaiKhoan);
        if (tk == null) {
            throw new NghiepVuException("Chưa chọn tài khoản");
        }
        return tk;
    }

    // Tên đăng nhập từ 4 ký tự
    private void kiemTraTenDangNhap(String ten) {
        if (ten.length() < DO_DAI_TEN_TOI_THIEU) {
            throw new NghiepVuException("Tên đăng nhập phải từ " + DO_DAI_TEN_TOI_THIEU + " ký tự");
        }
        if (ten.length() > 50 || !ten.matches("[A-Za-z0-9._-]+")) {
            throw new NghiepVuException("Tên đăng nhập chỉ gồm chữ không dấu, số, dấu chấm, gạch dưới hoặc gạch ngang, tối đa 50 ký tự (không có khoảng trắng)");
        }
    }

    // Mật khẩu từ 6 ký tự
    private void kiemTraMatKhauMoi(String matKhau) {
        if (matKhau == null || matKhau.length() < DO_DAI_MAT_KHAU_TOI_THIEU) {
            throw new NghiepVuException("Mật khẩu phải từ " + DO_DAI_MAT_KHAU_TOI_THIEU + " ký tự");
        }
        if (matKhau.length() > 50) {
            throw new NghiepVuException("Mật khẩu dài tối đa 50 ký tự");
        }
    }
}
