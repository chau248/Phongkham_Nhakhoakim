package nhakhoakim.entity;

import java.time.LocalDate;

// Bản tạm do Hậu viết để chạy thử; Trường thay bằng bản đầy đủ của mình (giữ nguyên tên getter/setter).

// Bệnh nhân
public class BenhNhan {

    private String maBN; // mã bệnh nhân, dạng BN000001
    private String hoTen; // họ tên
    private LocalDate ngaySinh; // ngày sinh
    private String gioiTinh; // Nam, Nữ hoặc Khác
    private String sdt; // số điện thoại
    private String diaChi; // địa chỉ
    private String trangThai; // Đang điều trị hoặc Ngừng theo dõi

    public String getMaBN() {
        return maBN;
    }

    public void setMaBN(String maBN) {
        this.maBN = maBN;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public LocalDate getNgaySinh() {
        return ngaySinh;
    }

    public void setNgaySinh(LocalDate ngaySinh) {
        this.ngaySinh = ngaySinh;
    }

    public String getGioiTinh() {
        return gioiTinh;
    }

    public void setGioiTinh(String gioiTinh) {
        this.gioiTinh = gioiTinh;
    }

    public String getSdt() {
        return sdt;
    }

    public void setSdt(String sdt) {
        this.sdt = sdt;
    }

    public String getDiaChi() {
        return diaChi;
    }

    public void setDiaChi(String diaChi) {
        this.diaChi = diaChi;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

}
