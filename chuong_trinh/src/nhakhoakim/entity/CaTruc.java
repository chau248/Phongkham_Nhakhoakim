package nhakhoakim.entity;

import java.time.LocalDate;

// Bản tạm do Hậu viết để chạy thử; Tâm thay bằng bản đầy đủ của mình (giữ nguyên tên getter/setter).

// Một ca trực của nhân viên
public class CaTruc {

    private String maCaTruc; // mã ca trực, dạng CA000001
    private String maNV; // nhân viên trực
    private LocalDate ngay; // ngày trực
    private String caLam; // Sáng hoặc Chiều
    private String phong; // phòng khám hoặc quầy lễ tân
    private String trangThai; // Có mặt, Vắng hoặc Điều chuyển

    public String getMaCaTruc() {
        return maCaTruc;
    }

    public void setMaCaTruc(String maCaTruc) {
        this.maCaTruc = maCaTruc;
    }

    public String getMaNV() {
        return maNV;
    }

    public void setMaNV(String maNV) {
        this.maNV = maNV;
    }

    public LocalDate getNgay() {
        return ngay;
    }

    public void setNgay(LocalDate ngay) {
        this.ngay = ngay;
    }

    public String getCaLam() {
        return caLam;
    }

    public void setCaLam(String caLam) {
        this.caLam = caLam;
    }

    public String getPhong() {
        return phong;
    }

    public void setPhong(String phong) {
        this.phong = phong;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }
}
