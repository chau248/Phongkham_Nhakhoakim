package nhakhoakim.dao;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Supplier;
import nhakhoakim.entity.BenhNhan;
import nhakhoakim.entity.HoSoBenhAn;
import nhakhoakim.entity.NhanVien;
import nhakhoakim.entity.TrangThai;
import nhakhoakim.entity.VaiTro;

// Nghiệp vụ bệnh nhân: tìm kiếm, thêm mới (kèm hồ sơ bệnh án rỗng), cập nhật thông tin hành chính
// Ràng buộc BN-01 đến BN-05 và BN-12 đến BN-14 trong tai_lieu/RANG_BUOC_NGHIEP_VU.md
// (phần hồ sơ bệnh án BN-06 đến BN-11, BN-15, BN-16 nằm ở HoSoBenhAnService và entity HoSoBenhAn)
public class BenhNhanService {

    private static final int TOI_DA_KET_QUA = 50;

    private BenhNhanDAO benhNhanDAO;
    private HoSoBenhAnService hoSoService;
    private Supplier<NhanVien> nguoiDung; // cho biết ai đang đăng nhập (kiểm thử thì truyền người giả)

    // Dùng DAO thật và người đăng nhập thật của chương trình
    public BenhNhanService() {
        this(new BenhNhanDAO(), new HoSoBenhAnService(), PhienLamViec::getNhanVienHienTai);
    }

    // Dùng DAO và người dùng truyền vào (để kiểm thử không cần CSDL)
    public BenhNhanService(BenhNhanDAO benhNhanDAO, HoSoBenhAnService hoSoService, Supplier<NhanVien> nguoiDung) {
        this.benhNhanDAO = benhNhanDAO;
        this.hoSoService = hoSoService;
        this.nguoiDung = nguoiDung;
    }

    // Tìm bệnh nhân theo mã; không có thì trả về null
    public BenhNhan timTheoMa(String maBN) {
        return benhNhanDAO.timTheoMa(maBN);
    }

    // Tìm bệnh nhân đang điều trị theo mã, họ tên hoặc số điện thoại; từ khóa rỗng thì lấy tối đa 50 người đầu (BN-01)
    public List<BenhNhan> timKiem(String tuKhoa) {
        return benhNhanDAO.timKiem(tuKhoa == null ? "" : tuKhoa.trim(), TOI_DA_KET_QUA);
    }

    // Kiểm tra thông tin nhập vào (BN-02, BN-03, BN-12, BN-13); sai thì báo lỗi bằng câu tiếng Việt
    public void kiemTra(BenhNhan bn) {
        if (bn.getHoTen() == null || bn.getHoTen().trim().isEmpty()) {
            throw new NghiepVuException("Họ tên không được để trống");
        }
        if (bn.getHoTen().length() < 2 || bn.getHoTen().length() > 100 || !bn.getHoTen().matches("[\\p{L} .'-]+")) {
            throw new NghiepVuException("Họ tên chỉ gồm chữ cái và khoảng trắng, dài từ 2 đến 100 ký tự");
        }
        KiemTraNhap.doDai(bn.getDiaChi(), 255, "Địa chỉ");
        KiemTraNhap.doDai(bn.getEmail(), 100, "Email");
        KiemTraNhap.doDai(bn.getNguoiLienHeKhanCap(), 100, "Người liên hệ khẩn cấp");
        KiemTraNhap.doDai(bn.getGhiChu(), 255, "Ghi chú");
        if (bn.getGioiTinh() != null && !List.of("Nam", "Nữ", "Khác").contains(bn.getGioiTinh())) {
            throw new NghiepVuException("Giới tính chỉ có thể là Nam, Nữ hoặc Khác");
        }
        if (bn.getSdt() != null && !laSoDienThoai(bn.getSdt())) {
            throw new NghiepVuException("Số điện thoại phải đủ 10 số và bắt đầu bằng 0");
        }
        if (bn.getEmail() != null && !bn.getEmail().matches("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}")) {
            throw new NghiepVuException("Email không đúng định dạng (ví dụ: ten@gmail.com)");
        }
        if (bn.getSoCCCD() != null && !bn.getSoCCCD().matches("[0-9]{12}")) {
            throw new NghiepVuException("Số căn cước phải đủ 12 số");
        }
        // Số điện thoại người liên hệ: có nhập thì phải đúng định dạng, bất kể tuổi
        if (bn.getSdtNguoiLienHe() != null && !laSoDienThoai(bn.getSdtNguoiLienHe())) {
            throw new NghiepVuException("Số điện thoại người liên hệ phải đủ 10 số và bắt đầu bằng 0");
        }
        if (bn.getNgaySinh() != null && bn.getNgaySinh().isAfter(LocalDate.now())) {
            throw new NghiepVuException("Ngày sinh không được sau hôm nay");
        }
        if (bn.getNgaySinh() != null && bn.getNgaySinh().isBefore(LocalDate.now().minusYears(120))) {
            throw new NghiepVuException("Ngày sinh không hợp lệ (tuổi quá 120)");
        }
        if (bn.getNgaySinh() != null && bn.tinhTuoi() < 18) {
            if (bn.getNguoiLienHeKhanCap() == null || bn.getSdtNguoiLienHe() == null) {
                throw new NghiepVuException("Bệnh nhân dưới 18 tuổi phải có người liên hệ khẩn cấp (tên và số điện thoại)");
            }
            if (bn.getSdtNguoiLienHe().equals(bn.getSdt())) {
                throw new NghiepVuException("Số điện thoại người liên hệ phải là của người giám hộ, không được trùng số của bệnh nhân");
            }
        }
    }

    // Thêm bệnh nhân mới và tạo luôn hồ sơ bệnh án rỗng trong cùng một transaction (BN-04); trả về bệnh nhân đã có mã
    public BenhNhan them(BenhNhan bn) {
        kiemTraQuyenSua();
        chuanHoa(bn);
        kiemTra(bn);
        kiemTraTrungCCCD(bn);
        kiemTraTrungNguoi(bn);
        bn.setMaBN(MaGenerator.taoMa("BN"));
        bn.setNgayDangKy(LocalDate.now());
        bn.setTrangThai(TrangThai.DANG_DIEU_TRI);
        HoSoBenhAn hs = hoSoService.taoDoiTuongHoSoRong(bn.getMaBN());
        benhNhanDAO.themKemHoSo(bn, hs); // DAO lo transaction: lỗi thì hủy cả bệnh nhân lẫn hồ sơ
        return bn;
    }

    // Cập nhật thông tin hành chính của bệnh nhân đã có
    public void capNhat(BenhNhan bn) {
        kiemTraQuyenSua();
        if (benhNhanDAO.timTheoMa(bn.getMaBN()) == null) {
            throw new NghiepVuException("Không tìm thấy bệnh nhân " + bn.getMaBN());
        }
        chuanHoa(bn);
        kiemTra(bn);
        kiemTraTrungCCCD(bn);
        benhNhanDAO.capNhat(bn);
    }

    // Chỉ Lễ tân hoặc Quản lý được thêm, sửa thông tin hành chính (BN-05)
    private void kiemTraQuyenSua() {
        NhanVien nv = nguoiDung.get();
        if (nv == null) {
            throw new NghiepVuException("Chưa đăng nhập");
        }
        String vaiTro = nv.getVaiTro();
        if (VaiTro.LE_TAN.equals(vaiTro) || VaiTro.QUAN_LY.equals(vaiTro)) {
            return;
        }
        if (VaiTro.NHA_SI.equals(vaiTro)) {
            throw new NghiepVuException("Nha sĩ không được sửa thông tin hành chính của bệnh nhân");
        }
        throw new NghiepVuException("Vai trò " + vaiTro + " không được sửa thông tin hành chính của bệnh nhân");
    }

    // CCCD không được trùng bệnh nhân khác (BN-02)
    private void kiemTraTrungCCCD(BenhNhan bn) {
        if (bn.getSoCCCD() == null) {
            return;
        }
        BenhNhan khac = benhNhanDAO.timTheoCCCD(bn.getSoCCCD());
        if (khac != null && !khac.getMaBN().equals(bn.getMaBN())) {
            throw new NghiepVuException("Số căn cước này đã thuộc bệnh nhân " + khac.getMaBN() + " - " + khac.getHoTen());
        }
    }

    // Cùng họ tên và cùng ngày sinh với một bệnh nhân đã có (kể cả đã ngừng theo dõi) thì coi là đăng ký trùng (BN-14);
    // trẻ em chưa có CCCD cũng bị chặn ở đây. Họ tên đã được gộp khoảng trắng thừa trong chuanHoa.
    private void kiemTraTrungNguoi(BenhNhan bn) {
        if (bn.getNgaySinh() == null) {
            return;
        }
        for (BenhNhan khac : benhNhanDAO.timTheoHoTenNgaySinh(bn.getHoTen(), bn.getNgaySinh())) {
            if (khac.getMaBN() != null && khac.getMaBN().equals(bn.getMaBN())) {
                continue; // chính người này
            }
            String ghiChuTrangThai = "";
            if (khac.getTrangThai() != null && !TrangThai.DANG_DIEU_TRI.equals(khac.getTrangThai())) {
                ghiChuTrangThai = ", trạng thái: " + khac.getTrangThai();
            }
            throw new NghiepVuException("Bệnh nhân này có thể đã đăng ký rồi: " + khac.getMaBN() + " - " + khac.getHoTen()
                    + " (cùng họ tên và ngày sinh" + ghiChuTrangThai + "). Hãy tìm lại trong danh sách.");
        }
    }

    // Cắt khoảng trắng thừa (họ tên gộp nhiều khoảng trắng thành một); ô để trống thì đổi thành null để lưu NULL vào CSDL
    private void chuanHoa(BenhNhan bn) {
        bn.setHoTen(bn.getHoTen() == null ? null : bn.getHoTen().trim().replaceAll("\\s+", " "));
        bn.setSdt(KiemTraNhap.rongThanhNull(bn.getSdt()));
        bn.setDiaChi(KiemTraNhap.rongThanhNull(bn.getDiaChi()));
        bn.setEmail(KiemTraNhap.rongThanhNull(bn.getEmail()));
        bn.setSoCCCD(KiemTraNhap.rongThanhNull(bn.getSoCCCD()));
        bn.setNguoiLienHeKhanCap(KiemTraNhap.rongThanhNull(bn.getNguoiLienHeKhanCap()));
        bn.setSdtNguoiLienHe(KiemTraNhap.rongThanhNull(bn.getSdtNguoiLienHe()));
        bn.setGhiChu(KiemTraNhap.rongThanhNull(bn.getGhiChu()));
        bn.setGioiTinh(KiemTraNhap.rongThanhNull(bn.getGioiTinh()));
    }

    // Số điện thoại hợp lệ: đúng 10 số, bắt đầu bằng 0
    private boolean laSoDienThoai(String s) {
        return s.matches("0[0-9]{9}");
    }
}
