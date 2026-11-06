package entity;

// Dịch vụ không gắn răng đã làm trong một lượt khám
public class LuotKhamDichVu {

    private int maDong; // Số thứ tự dòng
    private LuotKham maLuotKham; // Lượt khám
    private DichVu maDichVu; // Dịch vụ
    private int soLuong; // Số lượng
    private String ghiChu; // Ghi chú

    public int getMaDong() {
        return maDong;
    }

    public void setMaDong(int maDong) {
        this.maDong = maDong;
    }

    public LuotKham getMaLuotKham() {
        return maLuotKham;
    }

    public void setMaLuotKham(LuotKham maLuotKham) {
        this.maLuotKham = maLuotKham;
    }

    public DichVu getMaDichVu() {
        return maDichVu;
    }

    public void setMaDichVu(DichVu maDichVu) {
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
