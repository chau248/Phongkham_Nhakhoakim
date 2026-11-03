package nhakhoakim.entity;

// Dịch vụ không gắn với răng cụ thể
public class LuotKhamDichVu {

    private int maDong; // Số thứ tự dòng
    private String maLuotKham; // Mã lượt khám
    private String maDichVu; // Mã dịch vụ
    private int soLuong; // Số lượng
    private String ghiChu; // Ghi chú

    public int getMaDong() {
        return maDong;
    }

    public void setMaDong(int maDong) {
        this.maDong = maDong;
    }

    public String getMaLuotKham() {
        return maLuotKham;
    }

    public void setMaLuotKham(String maLuotKham) {
        this.maLuotKham = maLuotKham;
    }

    public String getMaDichVu() {
        return maDichVu;
    }

    public void setMaDichVu(String maDichVu) {
        this.maDichVu = maDichVu;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

}
