package entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

// Dòng thời gian điều trị
public class DongThoiGian {

    private List<SuKienDieuTri> suKien = new ArrayList<>(); // Các sự kiện, mới nhất trước
    private int soLuotKham; // Số lượt khám đã hoàn thành
    private LocalDate lanKhamGanNhat; // Ngày của lượt khám gần nhất
    private int soRangDaNho; // Số răng hiện đang ở trạng
    private int soRangImplantCauRang; // Số răng đã phục hình
    private int soLieuTrinhDangLam; // Số liệu trình đang thực

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

    // Một sự kiện trên dòng thời
    public static class SuKienDieuTri {

        // Các loại sự kiện
        public static final String LOAI_DANG_KY = "Đăng ký";
        public static final String LOAI_KHAM = "Khám";
        public static final String LOAI_LIEU_TRINH = "Liệu trình";
        public static final String LOAI_TAI_KHAM = "Tái khám";

        private LocalDateTime thoiGian; // Thời điểm xảy ra sự kiện
        private String loai; // Đăng ký, Khám
        private String tieuDe; // Dòng tiêu đề ngắn
        private String chiTiet; // Nội dung chi tiết, nhiều dòng
        private boolean daQua; // True nếu sự kiện đã xảy ra

        // Tự tính đã qua hay
        public SuKienDieuTri(LocalDateTime thoiGian, String loai, String tieuDe, String chiTiet) {
            this(thoiGian, loai, tieuDe, chiTiet, !thoiGian.isAfter(LocalDateTime.now()));
        }

        // Truyền sẵn đã qua hay
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
