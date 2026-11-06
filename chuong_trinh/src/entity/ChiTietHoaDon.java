package entity;

import java.math.BigDecimal;

// Một dòng dịch vụ trong hóa đơn
public class ChiTietHoaDon {

    private String maChiTiet; // Mã dòng, dạng CT000001
    private HoaDon maHoaDon; // Hóa đơn chứa dòng này
    private DichVu maDichVu; // Dịch vụ
    private int soLuong; // Số lượng
    private BigDecimal donGia; // Đơn giá lúc lập hóa đơn
    private BigDecimal giamGia; // Giảm giá của dòng
    private int soFDIRang; // Số răng (0 nếu dịch vụ không gắn răng)
    private String ghiChu; // Ghi chú

    public String getMaChiTiet() {
        return maChiTiet;
    }

    public void setMaChiTiet(String maChiTiet) {
        this.maChiTiet = maChiTiet;
    }

    public HoaDon getMaHoaDon() {
        return maHoaDon;
    }

    public void setMaHoaDon(HoaDon maHoaDon) {
        this.maHoaDon = maHoaDon;
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

    public int getSoFDIRang() {
        return soFDIRang;
    }

    public void setSoFDIRang(int soFDIRang) {
        this.soFDIRang = soFDIRang;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }


    // Thành tiền của dòng = đơn giá x số lượng - giảm giá
    public BigDecimal tinhThanhTien() {
        BigDecimal tien = donGia.multiply(BigDecimal.valueOf(soLuong));
        return giamGia == null ? tien : tien.subtract(giamGia);
    }

}
