package nhakhoakim.controller;

import nhakhoakim.dao.*;

import java.util.ArrayList;
import java.util.List;
import nhakhoakim.entity.DichVu;

// Danh mục dịch vụ (chỉ đọc)
// Bản tạm do Hậu viết để chạy thử, dữ liệu giống 02_seed.sql.
// Tâm thay bằng bản đọc từ CSDL (giữ nguyên tên hàm).
public class DichVuController {

    private static final List<DichVu> DANH_SACH = new ArrayList<>();

    static {
        them("DV000001", "Khám tổng quát", 100000, 20, "Khám", false, false);
        them("DV000002", "Lấy cao răng", 200000, 30, "Vệ sinh", false, false);
        them("DV000003", "Trám răng composite", 300000, 45, "Trám", false, false);
        them("DV000004", "Nhổ răng", 500000, 45, "Nhổ răng", false, false);
        them("DV000005", "Điều trị tủy", 1500000, 90, "Nội nha", false, true);
        them("DV000006", "Tẩy trắng răng", 2500000, 60, "Thẩm mỹ", false, false);
        them("DV000007", "Niềng răng mắc cài", 30000000, 60, "Chỉnh nha", false, true);
        them("DV000008", "Cấy ghép Implant", 20000000, 120, "Implant", true, true);
        them("DV000009", "Bọc răng sứ", 3000000, 60, "Phục hình", false, false);
        them("DV000010", "Làm cầu răng sứ", 8000000, 90, "Phục hình", true, true);
    }

    // Thêm một dịch vụ mẫu vào danh sách
    private static void them(String ma, String ten, long gia, int phut, String loai, boolean implantCauRang, boolean lieuTrinh) {
        DichVu dv = new DichVu();
        dv.setMaDichVu(ma);
        dv.setTenDichVu(ten);
        dv.setGia(gia);
        dv.setThoiGianDuKien(phut);
        dv.setLoaiDichVu(loai);
        dv.setTrangThaiHoatDong(true);
        dv.setLienQuanImplantCauRang(implantCauRang);
        dv.setYeuCauLieuTrinh(lieuTrinh);
        DANH_SACH.add(dv);
    }

    // Tất cả dịch vụ đang cung cấp
    public List<DichVu> layTatCa() {
        return new ArrayList<>(DANH_SACH);
    }

    // Dịch vụ đang cung cấp theo loại
    public List<DichVu> timTheoLoai(String loai) {
        List<DichVu> kq = new ArrayList<>();
        for (DichVu dv : DANH_SACH) {
            if (dv.getLoaiDichVu().equals(loai)) {
                kq.add(dv);
            }
        }
        return kq;
    }

    // Tìm dịch vụ theo mã; không có thì trả về null
    public DichVu timTheoMa(String ma) {
        for (DichVu dv : DANH_SACH) {
            if (dv.getMaDichVu().equals(ma)) {
                return dv;
            }
        }
        return null;
    }
}
