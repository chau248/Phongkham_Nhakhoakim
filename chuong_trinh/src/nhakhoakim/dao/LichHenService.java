package nhakhoakim.dao;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import nhakhoakim.entity.LichHen;

// Lịch hẹn khám
// Bản tạm do Hậu viết để chạy thử (dữ liệu nằm trong bộ nhớ).
// Trường thay bằng bản đọc/ghi CSDL (giữ nguyên tên hàm).
public class LichHenService {

    private static final List<LichHen> DANH_SACH = new ArrayList<>();
    private static int soThuTu = 2;

    static {
        them("LH000001", "BN000001", "NV000002", "DV000002", LocalDate.now(), LocalTime.of(9, 0), "Đã xác nhận");
        them("LH000002", "BN000002", "NV000002", "DV000008", LocalDate.now(), LocalTime.of(10, 30), "Chờ xác nhận");
    }

    // Thêm một lịch hẹn vào danh sách
    private static LichHen them(String ma, String maBN, String maNV, String maDV, LocalDate ngay, LocalTime gio, String trangThai) {
        LichHen lh = new LichHen();
        lh.setMaLichHen(ma);
        lh.setMaBN(maBN);
        lh.setMaNhanVien(maNV);
        lh.setMaDichVu(maDV);
        lh.setNgayHen(ngay);
        lh.setGioHen(gio);
        lh.setTrangThai(trangThai);
        lh.setPhong("Phòng 1");
        lh.setThoiLuongDuKien(30);
        DANH_SACH.add(lh);
        return lh;
    }

    // Tìm lịch hẹn theo mã; không có thì trả về null
    public LichHen layTheoMa(String maLichHen) {
        for (LichHen lh : DANH_SACH) {
            if (lh.getMaLichHen().equals(maLichHen)) {
                return lh;
            }
        }
        return null;
    }

    // Đổi trạng thái lịch hẹn (Hậu gọi để chuyển sang Hoàn thành khi hoàn tất khám)
    public void capNhatTrangThai(String maLichHen, String trangThai) {
        LichHen lh = layTheoMa(maLichHen);
        if (lh != null) {
            lh.setTrangThai(trangThai);
        }
    }

    // Lịch hẹn của một nhân viên trong khoảng ngày (gồm cả hai đầu), sắp theo ngày giờ
    public List<LichHen> layTheoNhanVien(String maNV, LocalDate tu, LocalDate den) {
        List<LichHen> kq = new ArrayList<>();
        for (LichHen lh : DANH_SACH) {
            if (lh.getMaNhanVien().equals(maNV) && !lh.getNgayHen().isBefore(tu) && !lh.getNgayHen().isAfter(den)) {
                kq.add(lh);
            }
        }
        kq.sort((a, b) -> a.getNgayHen().atTime(a.getGioHen()).compareTo(b.getNgayHen().atTime(b.getGioHen())));
        return kq;
    }

    // Các lịch hẹn Đã xác nhận của một bệnh nhân trong một ngày (Hậu gọi khi khám vãng lai để gắn lịch vào lượt khám)
    public List<LichHen> layDaXacNhanTheoBenhNhan(String maBN, LocalDate ngay) {
        List<LichHen> kq = new ArrayList<>();
        for (LichHen lh : DANH_SACH) {
            if (lh.getMaBN().equals(maBN) && lh.getNgayHen().equals(ngay) && "Đã xác nhận".equals(lh.getTrangThai())) {
                kq.add(lh);
            }
        }
        return kq;
    }

    // Hủy lịch tái khám (Chờ xác nhận hoặc Đã xác nhận) của bệnh nhân vào một ngày; trả về true nếu hủy được
    public boolean huyLichTaiKham(String maBN, LocalDate ngay) {
        for (LichHen lh : DANH_SACH) {
            boolean conHieuLuc = "Chờ xác nhận".equals(lh.getTrangThai()) || "Đã xác nhận".equals(lh.getTrangThai());
            if (lh.getMaBN().equals(maBN) && lh.getNgayHen().equals(ngay) && conHieuLuc) {
                lh.setTrangThai("Đã hủy");
                return true;
            }
        }
        return false;
    }

    // Tạo lịch tái khám, trạng thái Chờ xác nhận (lễ tân sẽ xác nhận sau)
    public LichHen taoLichTaiKham(String maBN, String maDichVu, LocalDate ngay, String maNV) {
        soThuTu++;
        return them(String.format("LH%06d", soThuTu), maBN, maNV, maDichVu, ngay, LocalTime.of(9, 0), "Chờ xác nhận");
    }
}
