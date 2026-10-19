package nhakhoakim.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

// Dòng thời gian điều trị của một bệnh nhân: các sự kiện (mới nhất trước) và vài con số tóm tắt
public class DongThoiGian {

    private List<SuKienDieuTri> suKien = new ArrayList<>(); // các sự kiện, mới nhất trước
    private int soLuotKham;           // số lượt khám đã hoàn thành
    private LocalDate lanKhamGanNhat; // ngày của lượt khám gần nhất (null nếu chưa khám lần nào)
    private int soRangDaNho;          // số răng hiện đang ở trạng thái Đã nhổ
    private int soRangImplantCauRang; // số răng đã phục hình (Implant hoặc Cầu răng)
    private int soLieuTrinhDangLam;   // số liệu trình đang thực hiện

    public List<SuKienDieuTri> getSuKien() {
        return suKien;
    }

    public int getSoLuotKham() {
        return soLuotKham;
    }

    public void setSoLuotKham(int soLuotKham) {
        this.soLuotKham = soLuotKham;
    }

    public LocalDate getLanKhamGanNhat() {
        return lanKhamGanNhat;
    }

    public void setLanKhamGanNhat(LocalDate lanKhamGanNhat) {
        this.lanKhamGanNhat = lanKhamGanNhat;
    }

    public int getSoRangDaNho() {
        return soRangDaNho;
    }

    public void setSoRangDaNho(int soRangDaNho) {
        this.soRangDaNho = soRangDaNho;
    }

    public int getSoRangImplantCauRang() {
        return soRangImplantCauRang;
    }

    public void setSoRangImplantCauRang(int soRangImplantCauRang) {
        this.soRangImplantCauRang = soRangImplantCauRang;
    }

    public int getSoLieuTrinhDangLam() {
        return soLieuTrinhDangLam;
    }

    public void setSoLieuTrinhDangLam(int soLieuTrinhDangLam) {
        this.soLieuTrinhDangLam = soLieuTrinhDangLam;
    }

    // Một sự kiện trên dòng thời gian điều trị của bệnh nhân (đăng ký, lượt khám, liệu trình, hẹn tái khám)
    public static class SuKienDieuTri {

        // Các loại sự kiện (cũng là nhãn bộ lọc trên màn hình dòng thời gian)
        public static final String LOAI_DANG_KY = "Đăng ký";
        public static final String LOAI_KHAM = "Khám";
        public static final String LOAI_LIEU_TRINH = "Liệu trình";
        public static final String LOAI_TAI_KHAM = "Tái khám";

        private LocalDateTime thoiGian; // thời điểm xảy ra sự kiện
        private String loai;            // Đăng ký, Khám, Liệu trình hoặc Tái khám
        private String tieuDe;          // dòng tiêu đề ngắn
        private String chiTiet;         // nội dung chi tiết, nhiều dòng
        private boolean daQua;          // true nếu sự kiện đã xảy ra, false nếu còn ở tương lai (hẹn tái khám)

        // Tự tính đã qua hay chưa theo giờ hiện tại
        public SuKienDieuTri(LocalDateTime thoiGian, String loai, String tieuDe, String chiTiet) {
            this(thoiGian, loai, tieuDe, chiTiet, !thoiGian.isAfter(LocalDateTime.now()));
        }

        // Truyền sẵn đã qua hay chưa (hẹn tái khám chỉ có ngày nên phải so theo ngày, hẹn hôm nay chưa tính là đã qua)
        public SuKienDieuTri(LocalDateTime thoiGian, String loai, String tieuDe, String chiTiet, boolean daQua) {
            this.thoiGian = thoiGian;
            this.loai = loai;
            this.tieuDe = tieuDe;
            this.chiTiet = chiTiet;
            this.daQua = daQua;
        }

        public LocalDateTime getThoiGian() {
            return thoiGian;
        }

        public String getLoai() {
            return loai;
        }

        public String getTieuDe() {
            return tieuDe;
        }

        public String getChiTiet() {
            return chiTiet;
        }

        public boolean isDaQua() {
            return daQua;
        }
    }
}
