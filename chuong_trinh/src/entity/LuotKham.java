package entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

// Một lần bệnh nhân đến khám
public class LuotKham {

    private String maLuotKham; // Mã lượt khám, dạng LK000001
    private HoSoBenhAn maHoSo; // Hồ sơ bệnh án
    private NhanVien maNhanVienPhuTrach; // Nha sĩ phụ trách
    private LieuTrinh maLieuTrinh; // Liệu trình (có thể không có)
    private HoaDon maHoaDon; // Hóa đơn (chưa lập thì null)
    private LichHen maLichHen; // Lịch hẹn của lượt khám (khám vãng lai thì null)
    private LocalDateTime gioBatDau; // Giờ bắt đầu khám
    private LocalDateTime gioKetThuc; // Giờ kết thúc khám
    private String lyDoKham; // Lý do khám
    private String chanDoan; // Chẩn đoán
    private String huongDieuTri; // Hướng điều trị
    private String donThuoc; // Đơn thuốc
    private LocalDate ngayTaiKham; // Ngày hẹn tái khám
    private String trangThai; // Đang khám, Hoàn thành hoặc Đã hủy
    private String ghiChu; // Ghi chú

    public String getMaLuotKham() {
        return maLuotKham;
    }

    public void setMaLuotKham(String maLuotKham) {
        this.maLuotKham = maLuotKham;
    }

    public HoSoBenhAn getMaHoSo() {
        return maHoSo;
    }

    public void setMaHoSo(HoSoBenhAn maHoSo) {
        this.maHoSo = maHoSo;
    }

    public NhanVien getMaNhanVienPhuTrach() {
        return maNhanVienPhuTrach;
    }

    public void setMaNhanVienPhuTrach(NhanVien maNhanVienPhuTrach) {
        this.maNhanVienPhuTrach = maNhanVienPhuTrach;
    }

    public LieuTrinh getMaLieuTrinh() {
        return maLieuTrinh;
    }

    public void setMaLieuTrinh(LieuTrinh maLieuTrinh) {
        this.maLieuTrinh = maLieuTrinh;
    }

    public HoaDon getMaHoaDon() {
        return maHoaDon;
    }

    public void setMaHoaDon(HoaDon maHoaDon) {
        this.maHoaDon = maHoaDon;
    }

    public LichHen getMaLichHen() {
        return maLichHen;
    }

    public void setMaLichHen(LichHen maLichHen) {
        this.maLichHen = maLichHen;
    }

    public LocalDateTime getGioBatDau() {
        return gioBatDau;
    }

    public void setGioBatDau(LocalDateTime gioBatDau) {
        this.gioBatDau = gioBatDau;
    }

    public LocalDateTime getGioKetThuc() {
        return gioKetThuc;
    }

    public void setGioKetThuc(LocalDateTime gioKetThuc) {
        this.gioKetThuc = gioKetThuc;
    }

    public String getLyDoKham() {
        return lyDoKham;
    }

    public void setLyDoKham(String lyDoKham) {
        this.lyDoKham = lyDoKham;
    }

    public String getChanDoan() {
        return chanDoan;
    }

    public void setChanDoan(String chanDoan) {
        this.chanDoan = chanDoan;
    }

    public String getHuongDieuTri() {
        return huongDieuTri;
    }

    public void setHuongDieuTri(String huongDieuTri) {
        this.huongDieuTri = huongDieuTri;
    }

    public String getDonThuoc() {
        return donThuoc;
    }

    public void setDonThuoc(String donThuoc) {
        this.donThuoc = donThuoc;
    }

    public LocalDate getNgayTaiKham() {
        return ngayTaiKham;
    }

    public void setNgayTaiKham(LocalDate ngayTaiKham) {
        this.ngayTaiKham = ngayTaiKham;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

}
