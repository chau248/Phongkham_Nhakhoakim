package nhakhoakim.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import nhakhoakim.dao.DichVuDAO;
import nhakhoakim.dao.KiemTraNhap;
import nhakhoakim.dao.MaGenerator;
import nhakhoakim.dao.NghiepVuException;
import nhakhoakim.dao.PhienLamViec;
import nhakhoakim.entity.DichVu;
import nhakhoakim.entity.NhanVien;
import nhakhoakim.entity.VaiTro;

// Nghiệp vụ danh mục dịch vụ và bảng giá (DV-01 đến DV-03): ai cũng xem được, chỉ Quản lý thêm và sửa; không xóa, chỉ ngừng hoạt động
public class DichVuController {

    public static final List<String> DON_VI_TINH = List.of("Lần", "Răng", "Hàm");

    private DichVuDAO dao;
    private NhatKyController nhatKy = new NhatKyController();
    private Supplier<NhanVien> nguoiDung;

    // Dùng DAO thật và người đăng nhập thật của chương trình
    public DichVuController() {
        this(new DichVuDAO(), PhienLamViec::getNhanVienHienTai);
    }

    // Dùng DAO và người dùng truyền vào (để kiểm thử)
    public DichVuController(DichVuDAO dao, Supplier<NhanVien> nguoiDung) {
        this.dao = dao;
        this.nguoiDung = nguoiDung;
    }

    // Tất cả dịch vụ, kể cả đã ngừng (các màn hình cũ của Hậu tự lọc theo trạng thái khi cần)
    public List<DichVu> layTatCa() {
        return dao.layTatCa();
    }

    // Chỉ dịch vụ đang hoạt động
    public List<DichVu> layDangHoatDong() {
        List<DichVu> kq = new ArrayList<>();
        for (DichVu dv : dao.layTatCa()) {
            if (dv.isTrangThaiHoatDong()) {
                kq.add(dv);
            }
        }
        return kq;
    }

    // Danh mục dịch vụ theo vai trò (DV-01): Quản lý thấy cả dịch vụ đã ngừng, các vai trò khác chỉ thấy dịch vụ đang hoạt động
    public List<DichVu> layChoVaiTro(String vaiTro) {
        return VaiTro.QUAN_LY.equals(vaiTro) ? layTatCa() : layDangHoatDong();
    }

    // Dịch vụ cùng một loại (Khám, Trám, Nội nha...), kể cả đã ngừng
    public List<DichVu> timTheoLoai(String loai) {
        List<DichVu> kq = new ArrayList<>();
        for (DichVu dv : dao.layTatCa()) {
            if (loai != null && loai.equals(dv.getLoaiDichVu())) {
                kq.add(dv);
            }
        }
        return kq;
    }

    // Tìm dịch vụ theo mã; không có thì trả về null
    public DichVu timTheoMa(String ma) {
        return ma == null ? null : dao.timTheoMa(ma);
    }

    // Thêm dịch vụ mới (chỉ Quản lý); mã do hệ thống sinh; trả về dịch vụ đã có mã
    public DichVu them(DichVu dv) {
        NhanVien nguoi = Quyen.yeuCau(nguoiDung, "thêm hoặc sửa dịch vụ", VaiTro.QUAN_LY);
        chuanHoa(dv);
        kiemTra(dv, null);
        dv.setMaDichVu(MaGenerator.taoMa("DV"));
        dao.them(dv);
        nhatKy.ghiNhatKy(nguoi.getMaNV(), "Thêm dịch vụ", "DichVu", dv.getMaDichVu(), dv.getTenDichVu() + ", giá " + dv.getGia());
        return dv;
    }

    // Sửa dịch vụ (chỉ Quản lý). Đổi giá thì chỉ áp dụng cho lịch và hóa đơn lập sau đó (hóa đơn cũ đã lưu giá cố định) và được ghi nhật ký (DV-03).
    public void capNhat(DichVu dv) {
        NhanVien nguoi = Quyen.yeuCau(nguoiDung, "thêm hoặc sửa dịch vụ", VaiTro.QUAN_LY);
        DichVu cu = dao.timTheoMa(dv.getMaDichVu());
        if (cu == null) {
            throw new NghiepVuException("Không tìm thấy dịch vụ " + dv.getMaDichVu());
        }
        chuanHoa(dv);
        kiemTra(dv, dv.getMaDichVu());
        dao.capNhat(dv);
        if (cu.getGia() != dv.getGia()) {
            nhatKy.ghiNhatKy(nguoi.getMaNV(), "Đổi giá dịch vụ", "DichVu", dv.getMaDichVu(), "Giá từ [" + cu.getGia() + "] sang [" + dv.getGia() + "]");
        }
        if (cu.isTrangThaiHoatDong() != dv.isTrangThaiHoatDong()) {
            nhatKy.ghiNhatKy(nguoi.getMaNV(), "Đổi trạng thái dịch vụ", "DichVu", dv.getMaDichVu(),
                    dv.isTrangThaiHoatDong() ? "Mở lại hoạt động" : "Ngừng hoạt động");
        }
    }

    // Cắt khoảng trắng thừa; ô để trống thì đổi thành null
    private void chuanHoa(DichVu dv) {
        dv.setTenDichVu(dv.getTenDichVu() == null ? null : dv.getTenDichVu().trim().replaceAll("\\s+", " "));
        dv.setLoaiDichVu(KiemTraNhap.rongThanhNull(dv.getLoaiDichVu()));
        dv.setMoTa(KiemTraNhap.rongThanhNull(dv.getMoTa()));
        dv.setDonViTinh(KiemTraNhap.rongThanhNull(dv.getDonViTinh()));
    }

    // Kiểm tra thông tin dịch vụ (DV-02); maDangSua là mã dịch vụ đang sửa (để tên không bị coi là trùng với chính nó), thêm mới thì null
    public void kiemTra(DichVu dv, String maDangSua) {
        if (dv.getTenDichVu() == null || dv.getTenDichVu().isEmpty()) {
            throw new NghiepVuException("Tên dịch vụ không được để trống");
        }
        KiemTraNhap.doDai(dv.getTenDichVu(), 150, "Tên dịch vụ");
        DichVu trung = dao.timTheoTen(dv.getTenDichVu());
        if (trung != null && !trung.getMaDichVu().equals(maDangSua)) {
            throw new NghiepVuException("Đã có dịch vụ tên \"" + trung.getTenDichVu() + "\" (" + trung.getMaDichVu() + ")");
        }
        if (dv.getGia() < 0) {
            throw new NghiepVuException("Giá dịch vụ không được âm");
        }
        if (dv.getThoiGianDuKien() <= 0) {
            throw new NghiepVuException("Thời gian dự kiến phải lớn hơn 0 phút");
        }
        if (dv.getLoaiDichVu() == null) {
            throw new NghiepVuException("Phải chọn loại dịch vụ");
        }
        KiemTraNhap.doDai(dv.getLoaiDichVu(), 50, "Loại dịch vụ");
        KiemTraNhap.doDai(dv.getMoTa(), 500, "Mô tả");
        if (dv.getDonViTinh() != null && !DON_VI_TINH.contains(dv.getDonViTinh())) {
            throw new NghiepVuException("Đơn vị tính chỉ có thể là Lần, Răng hoặc Hàm");
        }
    }
}
