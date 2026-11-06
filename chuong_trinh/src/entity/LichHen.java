package entity;

import java.time.LocalDate;
import java.time.LocalTime;

// Lịch hẹn khám của bệnh nhân với một nha sĩ
public class LichHen {

    private String maLichHen; // Mã lịch hẹn, dạng LH000001
    private BenhNhan maBN; // Bệnh nhân
    private NhanVien maNhanVien; // Nha sĩ
    private DichVu maDichVu; // Dịch vụ
    private LocalDate ngayHen; // Ngày hẹn
    private LocalTime gioHen; // Giờ hẹn
    private String trangThai; // Chờ xác nhận, Đã xác nhận, Hoàn thành, Không đến hoặc Đã hủy
    private String phong; // Phòng khám
    private int thoiLuongDuKien; // Thời lượng dự kiến (phút)
    private String hinhThucDat; // Trực tiếp, Điện thoại hoặc Online
    private boolean daNhacHen; // Đã nhắc hẹn chưa
    private String lyDoHuy; // Lý do hủy

    public String getMaLichHen() {
        return maLichHen;
    }

    public void setMaLichHen(String maLichHen) {
        this.maLichHen = maLichHen;
    }

    public BenhNhan getMaBN() {
        return maBN;
    }

    public void setMaBN(BenhNhan maBN) {
        this.maBN = maBN;
    }

    public NhanVien getMaNhanVien() {
        return maNhanVien;
    }

    public void setMaNhanVien(NhanVien maNhanVien) {
        this.maNhanVien = maNhanVien;
    }

    public DichVu getMaDichVu() {
        return maDichVu;
    }

    public void setMaDichVu(DichVu maDichVu) {
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
