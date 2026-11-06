package entity;

import java.time.LocalDateTime;

// Tình trạng một răng được ghi trong một lượt khám
public class RangDieuTri {

    private String maBanGhi; // Mã bản ghi, dạng BG000001
    private LuotKham maLuotKham; // Lượt khám
    private int soFDI; // Số răng theo hệ FDI
    private String trangThai; // Bình thường, Sâu, Đã trám, Bọc sứ, Đã nhổ, Implant, Cầu răng
    private String vatLieu; // Vật liệu sử dụng
    private LocalDateTime ngayGhiNhan; // Thời điểm ghi nhận
    private DichVu maDichVu; // Dịch vụ đã làm trên răng này (có thể không có)
    private boolean daNho; // True nếu răng đã nhổ
    private String mauHienThi; // Màu hiển thị trên sơ đồ răng
    private String ghiChu; // Ghi chú

    public String getMaBanGhi() {
        return maBanGhi;
    }

    public void setMaBanGhi(String maBanGhi) {
        this.maBanGhi = maBanGhi;
    }

    public LuotKham getMaLuotKham() {
        return maLuotKham;
    }

    public void setMaLuotKham(LuotKham maLuotKham) {
        this.maLuotKham = maLuotKham;
    }

    public int getSoFDI() {
        return soFDI;
    }

    public void setSoFDI(int soFDI) {
        this.soFDI = soFDI;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public String getVatLieu() {
        return vatLieu;
    }

    public void setVatLieu(String vatLieu) {
        this.vatLieu = vatLieu;
    }

    public LocalDateTime getNgayGhiNhan() {
        return ngayGhiNhan;
    }

    public void setNgayGhiNhan(LocalDateTime ngayGhiNhan) {
        this.ngayGhiNhan = ngayGhiNhan;
    }

    public DichVu getMaDichVu() {
        return maDichVu;
    }

    public void setMaDichVu(DichVu maDichVu) {
        this.maDichVu = maDichVu;
    }

    public boolean isDaNho() {
        return daNho;
    }

    public void setDaNho(boolean daNho) {
        this.daNho = daNho;
    }

    public String getMauHienThi() {
        return mauHienThi;
    }

    public void setMauHienThi(String mauHienThi) {
        this.mauHienThi = mauHienThi;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

    // Số răng FDI hợp lệ: răng vĩnh viễn 11-18
    public static boolean kiemTraSoFDI(int so) {
        return laRangVinhVien(so) || laRangSua(so);
    }

    // Răng vĩnh viễn: hàng 1 đến 4
    public static boolean laRangVinhVien(int so) {
        int hang = so / 10;
        int rang = so % 10;
        return hang >= 1 && hang <= 4 && rang >= 1 && rang <= 8;
    }

    // Răng sữa: hàng 5 đến 8
    public static boolean laRangSua(int so) {
        int hang = so / 10;
        int rang = so % 10;
        return hang >= 5 && hang <= 8 && rang >= 1 && rang <= 5;
    }

    // Răng khôn (số 8 của răng vĩnh viễn)
    public static boolean laRangKhon(int so) {
        return laRangVinhVien(so) && so % 10 == 8;
    }

}
