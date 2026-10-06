package nhakhoakim.entity;

// Dịch vụ không gắn với răng cụ thể trong một lượt khám (khám tổng quát, lấy cao răng...)
public class LuotKhamDichVu {

    private int maDong; // số thứ tự dòng (tự tăng)
    private String maLuotKham; // mã lượt khám
    private String maDichVu; // mã dịch vụ
    private int soLuong; // số lượng
    private String ghiChu; // ghi chú

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
