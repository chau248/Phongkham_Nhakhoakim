package entity;

import java.time.LocalDate;
import java.time.LocalTime;

// Bản tạm do Hậu viết để chạy thử

// Lịch hẹn khám
public class LichHen {

    private String maLichHen; // Mã lịch hẹn, dạng LH000001
    private String maBN; // Mã bệnh nhân
    private String maNhanVien; // Mã nha sĩ
    private String maDichVu; // Mã dịch vụ
    private LocalDate ngayHen; // Ngày hẹn
    private LocalTime gioHen; // Giờ hẹn
    private String trangThai; // Chờ xác nhận, Đã xác nhận
    private String phong; // Phòng khám
    private int thoiLuongDuKien; // Thời lượng dự kiến
    private String maHoSo; // Mã hồ sơ bệnh án
    private String hinhThucDat; // Hình thức đặt
    private boolean daNhacHen; // Trạng thái nhắc hẹn
    private String lyDoHuy; // Lý do hủy
    
    public String getMaLichHen() {
        return maLichHen;
    }

    public void setMaLichHen(String maLichHen) {
        this.maLichHen = maLichHen;
    }

    public String getMaBN() {
        return maBN;
    }

    public void setMaBN(String maBN) {
        this.maBN = maBN;
    }

    public String getMaNhanVien() {
        return maNhanVien;
    }

    public void setMaNhanVien(String maNhanVien) {
        this.maNhanVien = maNhanVien;
    }

    public String getMaDichVu() {
        return maDichVu;
    }

    public void setMaDichVu(String maDichVu) {
        this.maDichVu = maDichVu;
    }

    public LocalDate getNgayHen() {
        return ngayHen;
    }

    public void setNgayHen(LocalDate ngayHen) {
        this.ngayHen = ngayHen;
    }

    public LocalTime getGioHen() {
        return gioHen;
    }

    public void setGioHen(LocalTime gioHen) {
        this.gioHen = gioHen;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public String getPhong() {
        return phong;
    }

    public void setPhong(String phong) {
        this.phong = phong;
    }

    public int getThoiLuongDuKien() {
        return thoiLuongDuKien;
    }

    public void setThoiLuongDuKien(int thoiLuongDuKien) {
        this.thoiLuongDuKien = thoiLuongDuKien;
    }

    public LichHen(String maLichHen, String maBN, String maNhanVien, String maDichVu, LocalDate ngayHen,
            LocalTime gioHen, String hinhThucDat, boolean daNhacHen, String phong, int thoiLuongDuKien) {
        setMaLichHen(maLichHen);
        setGioHen(gioHen);
        setMaBN(maBN);
        setMaDichVu(maDichVu);
        setMaNhanVien(maNhanVien);
        setNgayHen(ngayHen);
        setPhong(phong);
        setThoiLuongDuKien(thoiLuongDuKien);
        setTrangThai("Chưa hoàn thành");
        setMaHoSo(null);
        setHinhThucDat(hinhThucDat);
        setDaNhacHen(daNhacHen);
        setLyDoHuy(null);
    }
    public LichHen() {
        setMaLichHen("none");
        setGioHen(null);
        setMaBN("");
        setMaDichVu("");
        setMaNhanVien("");
        setNgayHen(null);
        setPhong("");
        setTrangThai("Chưa hoàn thành");
        setThoiLuongDuKien(0);
        setMaHoSo(null);
        setHinhThucDat("");
        setDaNhacHen(false);
        setLyDoHuy(null);
    }
    @Override
    public String toString() {
        return "LichHen [maLichHen=" + maLichHen + ", maBN=" + maBN + ", maNhanVien=" + maNhanVien + ", maDichVu="
                + maDichVu + ", ngayHen=" + ngayHen + ", gioHen=" + gioHen + ", trangThai=" + trangThai + ", phong="
                + phong + ", thoiLuongDuKien=" + thoiLuongDuKien + "]";
    }

    public String getMaHoSo() {
        return maHoSo;
    }

    public void setMaHoSo(String maHoSo) {
        this.maHoSo = maHoSo;
    }

    public String getHinhThucDat() {
        return hinhThucDat;
    }

    public void setHinhThucDat(String hinhThucDat) {
        this.hinhThucDat = hinhThucDat;
    }

    public boolean isDaNhacHen() {
        return daNhacHen;
    }

    public void setDaNhacHen(boolean daNhacHen) {
        this.daNhacHen = daNhacHen;
    }

    public String getLyDoHuy() {
        return lyDoHuy;
    }

    public void setLyDoHuy(String lyDoHuy) {
        this.lyDoHuy = lyDoHuy;
    }
    
}
