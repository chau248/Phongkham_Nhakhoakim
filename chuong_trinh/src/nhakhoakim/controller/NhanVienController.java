package nhakhoakim.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import nhakhoakim.dao.KiemTraNhap;
import nhakhoakim.dao.MaGenerator;
import nhakhoakim.dao.NghiepVuException;
import nhakhoakim.dao.NhanVienDAO;
import nhakhoakim.dao.PhienLamViec;
import nhakhoakim.dao.TaiKhoanDAO;
import nhakhoakim.entity.NhanVien;
import nhakhoakim.entity.TaiKhoan;
import nhakhoakim.entity.VaiTro;

// Nghiệp vụ hồ sơ nhân viên (NV-01 đến NV-03): xem, thêm, sửa; không xóa, chỉ chuyển sang Nghỉ việc
public class NhanVienController {

    public static final String DANG_LAM = "Đang làm việc";
    public static final String NGHI_VIEC = "Nghỉ việc";
    public static final int SO_NGAY_CANH_BAO_CHUNG_CHI = 30; // báo trước khi chứng chỉ hành nghề hết hạn (NV-02)

    private NhanVienDAO nhanVienDAO;
    private TaiKhoanDAO taiKhoanDAO;
    private NhatKyController nhatKy = new NhatKyController();
    private Supplier<NhanVien> nguoiDung;

    // Dùng DAO thật và người đăng nhập thật của chương trình
    public NhanVienController() {
        this(new NhanVienDAO(), new TaiKhoanDAO(), PhienLamViec::getNhanVienHienTai);
    }

    // Dùng DAO và người dùng truyền vào (để kiểm thử)
    public NhanVienController(NhanVienDAO nhanVienDAO, TaiKhoanDAO taiKhoanDAO, Supplier<NhanVien> nguoiDung) {
        this.nhanVienDAO = nhanVienDAO;
        this.taiKhoanDAO = taiKhoanDAO;
        this.nguoiDung = nguoiDung;
    }

    // Tìm nhân viên theo mã; không có thì trả về null
    public NhanVien timTheoMa(String ma) {
        return ma == null ? null : nhanVienDAO.timTheoMa(ma);
    }

    // Tất cả nhân viên (kể cả đã nghỉ việc), theo mã
    public List<NhanVien> layTatCa() {
        return nhanVienDAO.layTatCa();
    }

    // Nhân viên đang làm việc theo vai trò (Nha sĩ, Y tá, Lễ tân, Quản lý)
    public List<NhanVien> layTheoVaiTro(String vaiTro) {
        List<NhanVien> kq = new ArrayList<>();
        for (NhanVien nv : nhanVienDAO.layTatCa()) {
            if (nv.getVaiTro().equals(vaiTro) && DANG_LAM.equals(nv.getTrangThai())) {
                kq.add(nv);
            }
        }
        return kq;
    }

    // Nhân viên có chứng chỉ hành nghề còn hạn tại ngày đó không (NV-02); không có chứng chỉ thì coi là không còn hạn
    public boolean chungChiConHan(String maNV, LocalDate ngay) {
        NhanVien nv = timTheoMa(maNV);
        return nv != null && nv.getNgayHetHanChungChi() != null && !nv.getNgayHetHanChungChi().isBefore(ngay);
    }

    // Nha sĩ đang làm việc có chứng chỉ đã hết hạn hoặc sắp hết hạn trong 30 ngày (để cảnh báo quản lý), sắp theo ngày hết hạn
    public List<NhanVien> layCanhBaoChungChi() {
        List<NhanVien> kq = new ArrayList<>();
        LocalDate han = LocalDate.now().plusDays(SO_NGAY_CANH_BAO_CHUNG_CHI);
        for (NhanVien nv : nhanVienDAO.layTatCa()) {
            if (VaiTro.NHA_SI.equals(nv.getVaiTro()) && DANG_LAM.equals(nv.getTrangThai())
                    && nv.getNgayHetHanChungChi() != null && !nv.getNgayHetHanChungChi().isAfter(han)) {
                kq.add(nv);
            }
        }
        kq.sort((a, b) -> a.getNgayHetHanChungChi().compareTo(b.getNgayHetHanChungChi()));
        return kq;
    }

    // Thêm nhân viên mới (chỉ Quản lý); mã do hệ thống sinh; trả về nhân viên đã có mã
    public NhanVien them(NhanVien nv) {
        Quyen.yeuCau(nguoiDung, "thêm hoặc sửa hồ sơ nhân viên", VaiTro.QUAN_LY);
        chuanHoa(nv);
        kiemTra(nv);
        nv.setMaNV(MaGenerator.taoMa("NV"));
        nv.setTrangThai(DANG_LAM);
        nhanVienDAO.them(nv);
        nhatKy.ghiNhatKy(nguoiDung.get().getMaNV(), "Thêm nhân viên", "NhanVien", nv.getMaNV(), nv.getHoTen() + " - " + nv.getVaiTro());
        return nv;
    }

    // Cập nhật hồ sơ nhân viên (chỉ Quản lý). Đổi vai trò thì đổi luôn vai trò tài khoản; chuyển sang Nghỉ việc thì khóa tài khoản (NV-03).
    public void capNhat(NhanVien nv) {
        Quyen.yeuCau(nguoiDung, "thêm hoặc sửa hồ sơ nhân viên", VaiTro.QUAN_LY);
        NhanVien cu = nhanVienDAO.timTheoMa(nv.getMaNV());
        if (cu == null) {
            throw new NghiepVuException("Không tìm thấy nhân viên " + nv.getMaNV());
        }
        chuanHoa(nv);
        kiemTra(nv);
        boolean doiVaiTro = !cu.getVaiTro().equals(nv.getVaiTro());
        boolean nghiViec = NGHI_VIEC.equals(nv.getTrangThai()) && !NGHI_VIEC.equals(cu.getTrangThai());
        TaiKhoan tk = taiKhoanDAO.timTheoNhanVien(nv.getMaNV());
        // Luôn còn ít nhất một Quản lý hoạt động (TK-04): không cho đổi vai trò hay cho nghỉ việc người quản lý cuối cùng
        boolean laQuanLyHoatDong = tk != null && VaiTro.QUAN_LY.equals(tk.getVaiTro()) && !tk.isTrangThaiKhoa();
        if (laQuanLyHoatDong && (doiVaiTro || nghiViec) && taiKhoanDAO.demQuanLyHoatDong() <= 1) {
            throw new NghiepVuException("Đây là Quản lý hoạt động cuối cùng, không thể đổi vai trò hoặc cho nghỉ việc (hệ thống phải luôn có một Quản lý)");
        }
        nhanVienDAO.capNhat(nv);
        if (tk != null && doiVaiTro) {
            taiKhoanDAO.doiVaiTroTheoNhanVien(nv.getMaNV(), nv.getVaiTro());
        }
        if (tk != null && nghiViec) {
            taiKhoanDAO.khoaTheoNhanVien(nv.getMaNV());
        }
        String maNguoiLam = nguoiDung.get().getMaNV();
        if (doiVaiTro) {
            nhatKy.ghiNhatKy(maNguoiLam, "Đổi vai trò nhân viên", "NhanVien", nv.getMaNV(), "Từ [" + cu.getVaiTro() + "] sang [" + nv.getVaiTro() + "]");
        }
        if (!cu.getTrangThai().equals(nv.getTrangThai())) {
            nhatKy.ghiNhatKy(maNguoiLam, "Đổi trạng thái nhân viên", "NhanVien", nv.getMaNV(), "Từ [" + cu.getTrangThai() + "] sang [" + nv.getTrangThai() + "]");
        }
    }

    // Cắt khoảng trắng thừa; ô để trống thì đổi thành null để lưu NULL vào CSDL
    private void chuanHoa(NhanVien nv) {
        nv.setHoTen(nv.getHoTen() == null ? null : nv.getHoTen().trim().replaceAll("\\s+", " "));
        nv.setSdt(KiemTraNhap.rongThanhNull(nv.getSdt()));
        nv.setGioiTinh(KiemTraNhap.rongThanhNull(nv.getGioiTinh()));
        nv.setChuyenKhoa(KiemTraNhap.rongThanhNull(nv.getChuyenKhoa()));
        nv.setSoChungChiHanhNghe(KiemTraNhap.rongThanhNull(nv.getSoChungChiHanhNghe()));
        if (!VaiTro.NHA_SI.equals(nv.getVaiTro())) {
            nv.setSoChungChiHanhNghe(null); // chỉ nha sĩ có chứng chỉ hành nghề
            nv.setNgayHetHanChungChi(null);
        }
    }

    // Kiểm tra thông tin nhập vào (NV-01); sai thì báo lỗi bằng câu tiếng Việt
    public void kiemTra(NhanVien nv) {
        if (nv.getHoTen() == null || nv.getHoTen().isEmpty()) {
            throw new NghiepVuException("Họ tên không được để trống");
        }
        if (nv.getHoTen().length() < 2 || nv.getHoTen().length() > 100 || !nv.getHoTen().matches("[\\p{L} .'-]+")) {
            throw new NghiepVuException("Họ tên chỉ gồm chữ cái và khoảng trắng, dài từ 2 đến 100 ký tự");
        }
        if (!List.of(VaiTro.NHA_SI, VaiTro.Y_TA, VaiTro.LE_TAN, VaiTro.QUAN_LY).contains(nv.getVaiTro())) {
            throw new NghiepVuException("Vai trò phải là Nha sĩ, Y tá, Lễ tân hoặc Quản lý");
        }
        if (nv.getGioiTinh() != null && !List.of("Nam", "Nữ", "Khác").contains(nv.getGioiTinh())) {
            throw new NghiepVuException("Giới tính chỉ có thể là Nam, Nữ hoặc Khác");
        }
        if (nv.getSdt() != null && !nv.getSdt().matches("0[0-9]{9}")) {
            throw new NghiepVuException("Số điện thoại phải đủ 10 số và bắt đầu bằng 0");
        }
        KiemTraNhap.doDai(nv.getChuyenKhoa(), 100, "Chuyên khoa");
        if (VaiTro.NHA_SI.equals(nv.getVaiTro())) {
            if (nv.getSoChungChiHanhNghe() == null) {
                throw new NghiepVuException("Nha sĩ bắt buộc có số chứng chỉ hành nghề");
            }
            KiemTraNhap.doDai(nv.getSoChungChiHanhNghe(), 30, "Số chứng chỉ hành nghề");
            if (nv.getNgayHetHanChungChi() == null) {
                throw new NghiepVuException("Nha sĩ bắt buộc có ngày hết hạn chứng chỉ hành nghề");
            }
        }
        if (nv.getTrangThai() != null && !List.of(DANG_LAM, NGHI_VIEC).contains(nv.getTrangThai())) {
            throw new NghiepVuException("Trạng thái chỉ có thể là Đang làm việc hoặc Nghỉ việc");
        }
    }
}
