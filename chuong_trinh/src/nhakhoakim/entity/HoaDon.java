package nhakhoakim.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
//Cac ham hien tai de trong (chua ro tong tien nen tinh nhung gi)
public class HoaDon {
    private String trangThai,maHoaDon,hinhThucThanhToan,maBenhNhan,ghiChu;
    private String maNhanVien;
    private LocalDateTime ngayLap, ngayThanhToan;
    private ChiTietHoaDon chiTiet;
    private BigDecimal tongTien,tienGiamGia, tongTienTruocThue, phanTramThue;
    private java.util.List<ChiTietHoaDon> dsChiTiet = new java.util.ArrayList<>(); // các dòng chi tiết của hóa đơn

    public String getMaHoaDon() {
        return maHoaDon;
    }
    public void setMaHoaDon(String maHoaDon) {
        this.maHoaDon = maHoaDon;
    }
    public String getHinhThucThanhToan() {
        return hinhThucThanhToan;
    }
    public void setHinhThucThanhToan(String hinhThucThanhToan) {
        this.hinhThucThanhToan = hinhThucThanhToan;
    }
    public String getMaBenhNhan() {
        return maBenhNhan;
    }
    public void setMaBenhNhan(String maBenhNhan) {
        this.maBenhNhan = maBenhNhan;
    }
    public String getGhiChu() {
        return ghiChu;
    }
    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }
    public String getMaNhanVien() {
        return maNhanVien;
    }
    public void setMaNhanVien(String maNhanVien) {
        this.maNhanVien = maNhanVien;
    }
    public LocalDateTime getNgayLap() {
        return ngayLap;
    }
    public void setNgayLap(LocalDateTime ngayLap) {
        this.ngayLap = ngayLap;
    }
    public ChiTietHoaDon getChiTiet() {
        return chiTiet;
    }
    public void setChiTiet(ChiTietHoaDon chiTiet) {
        this.chiTiet = chiTiet;
    }
    public BigDecimal getTongTien() {
        return tongTien;
    }
    public void setTongTien(BigDecimal tongTien) {
        this.tongTien = tongTien;
    }
    public String getTrangThai() {
        return trangThai;
    }
    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }
    public HoaDon(String maHoaDon, String hinhThucThanhToan, String maBenhNhan, String ghiChu, String maNhanVien,
            LocalDateTime ngayLap, ChiTietHoaDon chiTiet, BigDecimal tongTien, String trangThai, BigDecimal tienGiamGia, BigDecimal tongTienTruocThue, BigDecimal phanTramThue) {
        setMaNhanVien(maNhanVien);
        setMaHoaDon(maHoaDon);
        setChiTiet(chiTiet);
        setGhiChu(ghiChu);
        setHinhThucThanhToan(hinhThucThanhToan);
        setMaBenhNhan(maBenhNhan);
        setNgayLap(ngayLap);
        setTongTien(tongTien);
        setMaNhanVien(maNhanVien);
        setTrangThai(trangThai);
        setTienGiamGia(tienGiamGia);
        setTongTienTruocThue(tongTienTruocThue);
        setPhanTramThue(phanTramThue);
    }
    public HoaDon(String ma, String bn, LocalDateTime ngayLap){
        setMaHoaDon(ma);
        setMaBenhNhan(bn);
        setNgayLap(ngayLap);
    }
    public HoaDon() {
        setMaHoaDon("none");
        setChiTiet(new ChiTietHoaDon());
        setGhiChu("");
        setHinhThucThanhToan("");
        setMaBenhNhan("none");
        setNgayLap(LocalDateTime.now());
        setTongTien(new BigDecimal(0));
        setMaNhanVien("none");
    }
    @Override
    public String toString() {
        return "HoaDon [maHoaDon=" + maHoaDon + ", hinhThucThanhToan=" + hinhThucThanhToan + ", maBenhNhan="
                + maBenhNhan + ", ghiChu=" + ghiChu + ", maNhanVien=" + maNhanVien + ", ngayLap=" + ngayLap
                + ", chiTiet=" + chiTiet + ", tongTien=" + tongTien + "]";
    }
    
    public BigDecimal tinhTongTien(){

        return getTongTien();
    }public void themChiTiet(ChiTietHoaDon ct){
        setChiTiet(ct);
    }
    public void xuatHoaDon(){}
    public BigDecimal getTienGiamGia() {
        return tienGiamGia;
    }
    public void setTienGiamGia(BigDecimal tienGiamGia) {
        this.tienGiamGia = tienGiamGia;
    }
    public BigDecimal getTongTienTruocThue() {
        return tongTienTruocThue;
    }
    public void setTongTienTruocThue(BigDecimal tongTienTruocThue) {
        this.tongTienTruocThue = tongTienTruocThue;
    }
    public BigDecimal getPhanTramThue() {
        return phanTramThue;
    }
    public void setPhanTramThue(BigDecimal phanTramThue) {
        this.phanTramThue = phanTramThue;
    }
    public LocalDateTime getNgayThanhToan() {
        return ngayThanhToan;
    }
    public void setNgayThanhToan(LocalDateTime ngayThanhToan) {
        this.ngayThanhToan = ngayThanhToan;
    }

    public java.util.List<ChiTietHoaDon> getDsChiTiet() {
        return dsChiTiet;
    }

    public void setDsChiTiet(java.util.List<ChiTietHoaDon> dsChiTiet) {
        this.dsChiTiet = dsChiTiet;
    }

}
