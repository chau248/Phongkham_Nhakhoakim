package nhakhoakim.entity;

import java.time.LocalDateTime;

// Tình trạng một răng được ghi nhận trong một lượt khám
public class RangDieuTri {

    private String maBanGhi; // mã bản ghi, dạng BG000001
    private String maLuotKham; // mã lượt khám
    private int soFDI; // số răng theo hệ FDI (11-18, 21-28, 31-38, 41-48)
    private String matRang; // mặt răng: Gần, Xa, Nhai, Ngoài, Trong
    private String trangThai; // Bình thường, Sâu, Đã trám, Bọc sứ, Đã nhổ, Implant, Cầu răng
    private String vatLieu; // vật liệu sử dụng
    private LocalDateTime ngayGhiNhan; // thời điểm ghi nhận
    private String maDichVu; // dịch vụ đã thực hiện trên răng này
    private boolean daNho; // true nếu răng đã nhổ
    private String mauHienThi; // màu hiển thị trên sơ đồ răng
    private String ghiChu; // ghi chú

    public String getMaBanGhi() {
        return maBanGhi;
    }

    public void setMaBanGhi(String maBanGhi) {
        this.maBanGhi = maBanGhi;
    }

    public String getMaLuotKham() {
        return maLuotKham;
    }

    public void setMaLuotKham(String maLuotKham) {
        this.maLuotKham = maLuotKham;
    }

    public int getSoFDI() {
        return soFDI;
    }

    public void setSoFDI(int soFDI) {
        this.soFDI = soFDI;
    }

    public String getMatRang() {
        return matRang;
    }

    public void setMatRang(String matRang) {
        this.matRang = matRang;
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

    public String getMaDichVu() {
        return maDichVu;
    }

    public void setMaDichVu(String maDichVu) {
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

    // Kiểm tra số FDI có hợp lệ không
    public static boolean kiemTraSoFDI(int so) {
        int hang = so / 10;
        int rang = so % 10;
        return hang >= 1 && hang <= 4 && rang >= 1 && rang <= 8;
    }

}
