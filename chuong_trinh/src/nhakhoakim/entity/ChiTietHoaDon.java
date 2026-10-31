package nhakhoakim.entity;

import java.math.BigDecimal;
//tinhThanhTien chua hoan thanh do chua co cong thuc
public class ChiTietHoaDon {
    private int soLuong,soFDIRang;
    private String maDichVu;
    private String ghiChu,maChiTiet, maHoaDon;
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

    public String getMaHoaDon() {
        return maHoaDon;
    }
    public void setMaHoaDon(String maHoaDon) {
        this.maHoaDon = maHoaDon;
    }
    public ChiTietHoaDon(String maHoaDon, int soLuong, int soFDIRang, String 
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
        return "maChiTiet=" + getMaChiTiet() + ", maHoaDon=" + getMaHoaDon() + ", maDichVu=" + getMaDichVu() + ", soLuong="
                + getSoLuong() + ", donGia=" + getDonGia() + ", giamGia=" + getGiamGia() + ", ghiChu=" + getGhiChu()
                + ", soFDIRang=" + getSoFDIRang();
    }
    public BigDecimal tinhThanhTien(){
        BigDecimal GiaDichVu=new BigDecimal(0);
        
        return GiaDichVu;
    }
    
}
