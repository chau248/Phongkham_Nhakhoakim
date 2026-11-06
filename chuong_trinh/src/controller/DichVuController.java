package controller;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import dao.DichVuDAO;
import dao.TienIchDAO;
import dao.NghiepVuException;
import entity.DichVu;
import entity.NhanVien;
import entity.HangSo;

// Nghiệp vụ danh mục dịch vụ và bảng giá
public class DichVuController {

    public static final List<String> DON_VI_TINH = List.of("Lần", "Răng", "Hàm");

    private DichVuDAO dao = new DichVuDAO();
    private Supplier<NhanVien> nguoiDung = PhienLamViec::getNhanVienHienTai;

    // Tất cả dịch vụ
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
        return HangSo.VAI_TRO_QUAN_LY.equals(vaiTro) || HangSo.VAI_TRO_TOAN_QUYEN.equals(vaiTro) ? layTatCa() : layDangHoatDong();
    }

    // Dịch vụ cùng một loại
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
        NhanVien nguoi = TienIchController.yeuCau(nguoiDung, "thêm hoặc sửa dịch vụ", HangSo.VAI_TRO_QUAN_LY);

        chuanHoa(dv);
        kiemTra(dv, null);
        dv.setMaDichVu(TienIchDAO.taoMa("DV"));
        dao.them(dv);
        return dv;
    }

    // Sửa dịch vụ
    public void capNhat(DichVu dv) {
        NhanVien nguoi = TienIchController.yeuCau(nguoiDung, "thêm hoặc sửa dịch vụ", HangSo.VAI_TRO_QUAN_LY);
        DichVu cu = dao.timTheoMa(dv.getMaDichVu());
        if (cu == null) {
            throw new NghiepVuException("Không tìm thấy dịch vụ " + dv.getMaDichVu());
        }
        chuanHoa(dv);
        kiemTra(dv, dv.getMaDichVu());
        dao.capNhat(dv);
        if (cu.getGia() != dv.getGia()) {
            TienIchController.ghiNhatKy(nguoi.getMaNV(), "Đổi giá dịch vụ", "DichVu", dv.getMaDichVu(), "Giá từ [" + cu.getGia() + "] sang [" + dv.getGia() + "]");
        }
        if (cu.isTrangThaiHoatDong() != dv.isTrangThaiHoatDong()) {
            TienIchController.ghiNhatKy(nguoi.getMaNV(), "Đổi trạng thái dịch vụ", "DichVu", dv.getMaDichVu(),
                    dv.isTrangThaiHoatDong() ? "Mở lại hoạt động" : "Ngừng hoạt động");
        }
    }

    // Cắt khoảng trắng thừa
    private void chuanHoa(DichVu dv) {
        dv.setTenDichVu(dv.getTenDichVu() == null ? null : dv.getTenDichVu().trim().replaceAll("\\s+", " "));
        dv.setLoaiDichVu(TienIchController.rongThanhNull(dv.getLoaiDichVu()));
        dv.setMoTa(TienIchController.rongThanhNull(dv.getMoTa()));
        dv.setDonViTinh(TienIchController.rongThanhNull(dv.getDonViTinh()));
    }

    // Kiểm tra thông tin dịch vụ
    public void kiemTra(DichVu dv, String maDangSua) {
        if (dv.getTenDichVu() == null || dv.getTenDichVu().isEmpty()) {
            throw new NghiepVuException("Tên dịch vụ không được để trống");
        }
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
        if (dv.getLoaiDichVu() == null || !new dao.CauHinhDAO().daCo(entity.DanhMuc.LOAI_DICH_VU, dv.getLoaiDichVu())) {
            throw new NghiepVuException("Loại dịch vụ phải chọn trong danh mục loại dịch vụ");
        }
        if (dv.getDonViTinh() != null && !DON_VI_TINH.contains(dv.getDonViTinh())) {
            throw new NghiepVuException("Đơn vị tính chỉ có thể là Lần, Răng hoặc Hàm");
        }
    }
}
