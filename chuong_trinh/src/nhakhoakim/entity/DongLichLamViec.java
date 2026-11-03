package nhakhoakim.entity;

import java.time.LocalDate;
import java.time.LocalTime;

// Một dòng của màn hình "Xem
public class DongLichLamViec {

    private LocalDate ngay;
    private String caLam;
    private String phong;
    private LocalTime gio; // Giờ hẹn
    private String maLichHen;
    private String tenBenhNhan;
    private String tenDichVu;
    private String tenNhaSi; // Nha sĩ phụ trách
    private String trangThai; // Trạng thái lịch hẹn

    public DongLichLamViec(LocalDate ngay, String caLam, String phong) {
        this.ngay = ngay;
        this.caLam = caLam;
        this.phong = phong;
    }

    public LocalDate getNgay() {
        return ngay;
    }

    public String getCaLam() {
        return caLam;
    }

    public String getPhong() {
        return phong;
    }

    public LocalTime getGio() {
        return gio;
    }

    public void setGio(LocalTime gio) {
        this.gio = gio;
    }

    public String getMaLichHen() {
        return maLichHen;
    }

    public void setMaLichHen(String maLichHen) {
        this.maLichHen = maLichHen;
    }

    public String getTenBenhNhan() {
        return tenBenhNhan;
    }

    public void setTenBenhNhan(String tenBenhNhan) {
        this.tenBenhNhan = tenBenhNhan;
    }

    public String getTenDichVu() {
        return tenDichVu;
    }

    public void setTenDichVu(String tenDichVu) {
        this.tenDichVu = tenDichVu;
    }

    public String getTenNhaSi() {
        return tenNhaSi;
    }

    public void setTenNhaSi(String tenNhaSi) {
        this.tenNhaSi = tenNhaSi;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }
}
