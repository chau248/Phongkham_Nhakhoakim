package nhakhoakim.entity;

import java.math.BigDecimal;
// TinhThanhTien chua hoan thanh do chua
public class ChiTietHoaDon {
    private int soLuong,soFDIRang;
    private String maDichVu;
    private String ghiChu,maChiTiet;
    private HoaDon maHoaDon;
    private BigDecimal donGia;
    private BigDecimal giamGia;
    public int getSoLuong() {
        return soLuong;
    }
    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
    }
    public int getSoFDIRang() {
        return soFDIRang;
    }
    public void setSoFDIRang(int soFDIRang) {
        this.soFDIRang = soFDIRang;
    }
    public String getMaDichVu() {
        return maDichVu;
    }
    public void setMaDichVu(String maDichVu) {
        this.maDichVu = maDichVu;
    }
    public String getGhiChu() {
        return ghiChu;
    }
    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }
    public String getMaChiTiet() {
        return maChiTiet;
    }
    public void setMaChiTiet(String maChiTiet) {
        this.maChiTiet = maChiTiet;
    }
    public BigDecimal getDonGia() {
        return donGia;
    }
    public void setDonGia(BigDecimal donGia) {
        this.donGia = donGia;
    }
    public BigDecimal getGiamGia() {
        return giamGia;
    }
    public void setGiamGia(BigDecimal giamGia) {
        this.giamGia = giamGia;
    }

    public HoaDon getMaHoaDon() {
        return maHoaDon;
    }
    public void setMaHoaDon(HoaDon maHoaDon) {
        this.maHoaDon = maHoaDon;
    }
    public ChiTietHoaDon(HoaDon maHoaDon, int soLuong, int soFDIRang, String 
        maDichVu, String ghiChu, String maChiTiet, BigDecimal donGia,
            BigDecimal giamGia) {
        setMaHoaDon(maHoaDon);
        setMaDichVu(maDichVu);
        setDonGia(donGia);
        setGhiChu(ghiChu);
        setGiamGia(giamGia);
        setMaChiTiet(maChiTiet);
        setSoLuong(soLuong);
        setSoFDIRang(soFDIRang);

    }
    public ChiTietHoaDon() {
        setMaDichVu("none");
        setDonGia(new BigDecimal(0));
        setGhiChu("");
        setGiamGia(new BigDecimal(0));
        setMaChiTiet("none");
        setSoLuong(0);
        setSoFDIRang(0);
    }
    @Override
    public String toString() {
        return "maChiTiet=" + getMaChiTiet() + ", maHoaDon=" + (getMaHoaDon() == null ? null : getMaHoaDon().getMaHoaDon()) + ", maDichVu=" + getMaDichVu() + ", soLuong="
                + getSoLuong() + ", donGia=" + getDonGia() + ", giamGia=" + getGiamGia() + ", ghiChu=" + getGhiChu()
                + ", soFDIRang=" + getSoFDIRang();
    }
    public BigDecimal tinhThanhTien(){
        BigDecimal GiaDichVu=new BigDecimal(0);
        
        return GiaDichVu;
    }
    
}
