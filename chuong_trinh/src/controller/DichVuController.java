package controller;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import dao.DichVuDAO;
import dao.KiemTraNhap;
import dao.MaGenerator;
import dao.NghiepVuException;
import dao.PhienLamViec;
import entity.DichVu;
import entity.NhanVien;
import entity.VaiTro;

// Nghiệp vụ danh mục dịch vụ và bảng giá
public class DichVuController {

    public static final List<String> DON_VI_TINH = List.of("Lần", "Răng", "Hàm");

    private DichVuDAO dao;
    private NhatKyController nhatKy = new NhatKyController();
    private Supplier<NhanVien> nguoiDung;

    // Dùng DAO thật
    public DichVuController() {
        this(new DichVuDAO(), PhienLamViec::getNhanVienHienTai);
    }

    // Dùng DAO và người dùng truyền vào
    public DichVuController(DichVuDAO dao, Supplier<NhanVien> nguoiDung) {
        this.dao = dao;
        this.nguoiDung = nguoiDung;
    }

    // Tất cả dịch vụ, kể cả đã ngừng
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

    // Danh mục dịch vụ theo vai trò
    public List<DichVu> layChoVaiTro(String vaiTro) {
        return VaiTro.QUAN_LY.equals(vaiTro) ? layTatCa() : layDangHoatDong();
    }

    // Dịch vụ cùng một loại, kể cả đã ngừng
    public List<DichVu> timTheoLoai(String loai) {
        List<DichVu> kq = new ArrayList<>();
        for (DichVu dv : dao.layTatCa()) {
            if (loai != null && loai.equals(dv.getLoaiDichVu())) {
                kq.add(dv);
            }
        }
        return kq;
    }

    // Tìm dịch vụ theo mã
    public DichVu timTheoMa(String ma) {
        return ma == null ? null : dao.timTheoMa(ma);
    }

    // Thêm dịch vụ mới
    public DichVu them(DichVu dv) {
        NhanVien nguoi = Quyen.yeuCau(nguoiDung, "thêm hoặc sửa dịch vụ", VaiTro.QUAN_LY);
        chuanHoa(dv);
        kiemTra(dv, null);
        dv.setMaDichVu(MaGenerator.taoMa("DV"));
        dao.them(dv);
        nhatKy.ghiNhatKy(nguoi.getMaNV(), "Thêm dịch vụ", "DichVu", dv.getMaDichVu(), dv.getTenDichVu() + ", giá " + dv.getGia());
        return dv;
    }

    // Sửa dịch vụ
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

    // Cắt khoảng trắng thừa
    private void chuanHoa(DichVu dv) {
        dv.setTenDichVu(dv.getTenDichVu() == null ? null : dv.getTenDichVu().trim().replaceAll("\\s+", " "));
        dv.setLoaiDichVu(KiemTraNhap.rongThanhNull(dv.getLoaiDichVu()));
        dv.setMoTa(KiemTraNhap.rongThanhNull(dv.getMoTa()));
        dv.setDonViTinh(KiemTraNhap.rongThanhNull(dv.getDonViTinh()));
    }

    // Kiểm tra thông tin dịch vụ
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
