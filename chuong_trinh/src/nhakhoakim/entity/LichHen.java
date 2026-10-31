package nhakhoakim.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

// Bản tạm do Hậu viết để chạy thử; Trường thay bằng bản đầy đủ của mình (giữ nguyên tên getter/setter).

// Lịch hẹn khám
public class LichHen {

    private String maLichHen; // mã lịch hẹn, dạng LH000001
    private String maBN; // mã bệnh nhân
    private String maNhanVien; // mã nha sĩ
    private String maDichVu; // mã dịch vụ
    private LocalDate ngayHen; // ngày hẹn
    private LocalTime gioHen; // giờ hẹn
    private String trangThai; // Chờ xác nhận, Đã xác nhận, Hoàn thành, Không đến hoặc Đã hủy
    private LocalDateTime ngayTao; // thời điểm tạo, do CSDL tự sinh
    private String phong; // phòng khám
    private int thoiLuongDuKien; // thời lượng dự kiến (phút)
    private String hinhThucDat; // Trực tiếp, Điện thoại hoặc Online
    private boolean daNhacHen; // trạng thái nhắc hẹn: chưa nhắc, đã nhắc, không nhắc (bệnh nhân không muốn nhận nhắc hẹn)
    private String lyDoHuy; // lý do hủy (có thể null nếu chưa hủy)
    private String ghiChu;
    private String tenBenhNhan; // thông tin JOIN để hiển thị, không phải cột dbo.LichHen
    private String sdtBenhNhan;
    private String tenDichVu;
    private String tenNhanVien;
    
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
        setTrangThai("Chờ xác nhận");
        setNgayTao(null);
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
        setTrangThai("Chờ xác nhận");
        setThoiLuongDuKien(0);
        setNgayTao(null);
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

    public LocalDateTime getNgayTao() {
        return ngayTao;
    }

    public void setNgayTao(LocalDateTime ngayTao) {
        this.ngayTao = ngayTao;
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

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

    public String getTenBenhNhan() {
        return tenBenhNhan;
    }

    public void setTenBenhNhan(String tenBenhNhan) {
        this.tenBenhNhan = tenBenhNhan;
    }

    public String getSdtBenhNhan() {
        return sdtBenhNhan;
    }

    public void setSdtBenhNhan(String sdtBenhNhan) {
        this.sdtBenhNhan = sdtBenhNhan;
    }

    public String getTenDichVu() {
        return tenDichVu;
    }

    public void setTenDichVu(String tenDichVu) {
        this.tenDichVu = tenDichVu;
    }

    public String getTenNhanVien() {
        return tenNhanVien;
    }

    public void setTenNhanVien(String tenNhanVien) {
        this.tenNhanVien = tenNhanVien;
    }
    
}
