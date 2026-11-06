package controller;

import dao.*;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Supplier;
import entity.BenhNhan;
import entity.HangSo;
import entity.HoSoBenhAn;
import entity.LuotKham;
import entity.NhanVien;

// Nghiệp vụ bệnh nhân
public class BenhNhanController {

    private static final int TOI_DA_KET_QUA = 50;

    private BenhNhanDAO benhNhanDAO;
    private HoSoBenhAnController hoSoService;
    private Supplier<NhanVien> nguoiDung; 
    private NhatKyController nhatKyService = new NhatKyController();

    public BenhNhanController() {
        this(new BenhNhanDAO(), new HoSoBenhAnController(), PhienLamViec::getNhanVienHienTai);
    }

    public BenhNhanController(BenhNhanDAO benhNhanDAO, HoSoBenhAnController hoSoService, Supplier<NhanVien> nguoiDung) {
        this.benhNhanDAO = benhNhanDAO;
        this.hoSoService = hoSoService;
        this.nguoiDung = nguoiDung;
    }

    // Tìm bệnh nhân theo mã
    public BenhNhan timTheoMa(String maBN) {
        return benhNhanDAO.timTheoMa(maBN);
    }

    // Tìm theo từ khóa
    public List<BenhNhan> timKiem(String tuKhoa) {
        return benhNhanDAO.timKiem(tuKhoa == null ? "" : tuKhoa.trim(), TOI_DA_KET_QUA);
    }

    // Tìm theo từ khóa, có thể gồm cả người ngừng theo dõi
    public List<BenhNhan> timKiem(String tuKhoa, boolean gomNgung) {
        return benhNhanDAO.timKiem(tuKhoa == null ? "" : tuKhoa.trim(), TOI_DA_KET_QUA, gomNgung);
    }

    // Kiểm tra thông tin nhập vào
    public void kiemTra(BenhNhan bn) {
        if (bn.getHoTen() == null || bn.getHoTen().trim().isEmpty()) {
            throw new NghiepVuException("Họ tên không được để trống");
        }
        if (bn.getNgaySinh() == null) {
            throw new NghiepVuException("Ngày sinh không được để trống (cần để biết bệnh nhân có phải trẻ em và để kiểm tra trùng)");
        }
        if (bn.getHoTen().length() < 2 || bn.getHoTen().length() > 100 || !bn.getHoTen().matches("[\\p{L} .'-]+")) {
            throw new NghiepVuException("Họ tên chỉ gồm chữ cái và khoảng trắng, dài từ 2 đến 100 ký tự");
        }
        doDai(bn.getDiaChi(), 255, "Địa chỉ");
        doDai(bn.getEmail(), 100, "Email");
        doDai(bn.getNguoiLienHeKhanCap(), 100, "Người liên hệ khẩn cấp");
        doDai(bn.getGhiChu(), 255, "Ghi chú");
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
        // Số điện thoại người liên hệ
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

    // Thêm bệnh nhân mới
    public BenhNhan them(BenhNhan bn) {
        kiemTraQuyenSua();
        chuanHoa(bn);
        kiemTra(bn);
        kiemTraTrungCCCD(bn);
        kiemTraTrungNguoi(bn);
        bn.setMaBN(MaGenerator.taoMa("BN"));
        bn.setNgayDangKy(LocalDate.now());
        bn.setTrangThai(HangSo.DANG_DIEU_TRI);
        HoSoBenhAn hs = hoSoService.taoDoiTuongHoSoRong(bn.getMaBN());
        benhNhanDAO.themKemHoSo(bn, hs);
        return bn;
    }

    // Cập nhật thông tin bệnh nhân
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

    // Ngừng theo dõi hoặc mở lại bệnh nhân
    public void doiTrangThai(String maBN, String trangThaiMoi) {
        kiemTraQuyenSua();
        if (!HangSo.DANG_DIEU_TRI.equals(trangThaiMoi) && !HangSo.NGUNG_THEO_DOI.equals(trangThaiMoi)) {
            throw new NghiepVuException("Trạng thái bệnh nhân chỉ có thể là Đang điều trị hoặc Ngừng theo dõi");
        }
        BenhNhan bn = benhNhanDAO.timTheoMa(maBN);
        if (bn == null) {
            throw new NghiepVuException("Không tìm thấy bệnh nhân " + maBN);
        }
        if (trangThaiMoi.equals(bn.getTrangThai())) {
            throw new NghiepVuException("Bệnh nhân đã ở trạng thái " + trangThaiMoi);
        }
        if (HangSo.NGUNG_THEO_DOI.equals(trangThaiMoi)) {
            HoSoBenhAn hs = hoSoService.timTheoBenhNhan(maBN);
            for (LuotKham lk : hoSoService.layLichSuKham(hs.getMaHoSo())) {
                if (HangSo.DANG_KHAM.equals(lk.getTrangThai())) {
                    throw new NghiepVuException("Bệnh nhân đang có lượt khám chưa hoàn tất (" + lk.getMaLuotKham() + "), hãy hoàn tất hoặc hủy trước");
                }
            }
        }
        benhNhanDAO.capNhatTrangThai(maBN, trangThaiMoi);
        nhatKyService.ghiNhatKy(nguoiDung.get().getMaNV(), "Đổi trạng thái bệnh nhân", "BenhNhan", maBN,
                "Từ [" + bn.getTrangThai() + "] sang [" + trangThaiMoi + "]");
    }

    // Chỉ LT & QL được thêm
    private void kiemTraQuyenSua() {
        NhanVien nv = nguoiDung.get();
        if (nv == null) {
            throw new NghiepVuException("Chưa đăng nhập");
        }
        String vaiTro = nv.getVaiTro();
        if (HangSo.VAI_TRO_LE_TAN.equals(vaiTro) || HangSo.VAI_TRO_QUAN_LY.equals(vaiTro)) {
            return;
        }
        if (HangSo.VAI_TRO_NHA_SI.equals(vaiTro)) {
            throw new NghiepVuException("Nha sĩ không được sửa thông tin hành chính của bệnh nhân");
        }
        throw new NghiepVuException("Vai trò " + vaiTro + " không được sửa thông tin hành chính của bệnh nhân");
    }

    // CCCD không được trùng bệnh nhân khác
    private void kiemTraTrungCCCD(BenhNhan bn) {
        if (bn.getSoCCCD() == null) {
            return;
        }
        BenhNhan khac = benhNhanDAO.timTheoCCCD(bn.getSoCCCD());
        if (khac != null && !khac.getMaBN().equals(bn.getMaBN())) {
            throw new NghiepVuException("Số căn cước này đã thuộc bệnh nhân " + khac.getMaBN() + " - " + khac.getHoTen());
        }
    }

    // Cùng họ tên và cùng ngày sinh
    private void kiemTraTrungNguoi(BenhNhan bn) {
        if (bn.getNgaySinh() == null) {
            return;
        }
        for (BenhNhan khac : benhNhanDAO.timTheoHoTenNgaySinh(bn.getHoTen(), bn.getNgaySinh())) {
            if (khac.getMaBN() != null && khac.getMaBN().equals(bn.getMaBN())) {
                continue; // Chính người này
            }
            String ghiChuTrangThai = "";
            if (khac.getTrangThai() != null && !HangSo.DANG_DIEU_TRI.equals(khac.getTrangThai())) {
                ghiChuTrangThai = ", trạng thái: " + khac.getTrangThai();
            }
            throw new NghiepVuException("Bệnh nhân này có thể đã đăng ký rồi: " + khac.getMaBN() + " - " + khac.getHoTen()
                    + " (cùng họ tên và ngày sinh" + ghiChuTrangThai + "). Hãy tìm lại trong danh sách.");
        }
    }

    // Cắt khoảng thừa
    private void chuanHoa(BenhNhan bn) {
        bn.setHoTen(bn.getHoTen() == null ? null : bn.getHoTen().trim().replaceAll("\\s+", " "));
        bn.setSdt(rongThanhNull(bn.getSdt()));
        bn.setDiaChi(rongThanhNull(bn.getDiaChi()));
        bn.setEmail(rongThanhNull(bn.getEmail()));
        bn.setSoCCCD(rongThanhNull(bn.getSoCCCD()));
        bn.setNguoiLienHeKhanCap(rongThanhNull(bn.getNguoiLienHeKhanCap()));
        bn.setSdtNguoiLienHe(rongThanhNull(bn.getSdtNguoiLienHe()));
        bn.setGhiChu(rongThanhNull(bn.getGhiChu()));
        bn.setGioiTinh(rongThanhNull(bn.getGioiTinh()));
    }

    // Số điện thoại hợp lệ
    private boolean laSoDienThoai(String s) {
        return s.matches("0[0-9]{9}");
    }

    // Chuỗi không được dài hơn độ dài cột
    private static void doDai(String giaTri, int toiDa, String tenTruong) {
        if (giaTri != null && giaTri.length() > toiDa) {
            throw new NghiepVuException(tenTruong + " dài tối đa " + toiDa + " ký tự (hiện đang " + giaTri.length() + " ký tự)");
        }
    }


    // Chuỗi rỗng hoặc toàn khoảng trắng
    private static String rongThanhNull(String s) {
        return s == null || s.trim().isEmpty() ? null : s.trim();
    }
}
