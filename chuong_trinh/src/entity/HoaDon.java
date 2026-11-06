package entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// Hóa đơn thanh toán của một lượt khám
public class HoaDon {

    private String maHoaDon; // Mã hóa đơn, dạng HD000001
    private BenhNhan maBenhNhan; // Bệnh nhân
    private NhanVien maNhanVien; // Nhân viên lập hóa đơn
    private LocalDateTime ngayLap; // Ngày lập
    private LocalDateTime ngayThanhToan; // Ngày thanh toán
    private BigDecimal tienGiamGia; // Tiền giảm giá của cả hóa đơn
    private BigDecimal phanTramThue; // Phần trăm thuế VAT
    private BigDecimal tongTienTruocThue; // Tổng tiền trước thuế
    private BigDecimal tongTien; // Tổng tiền phải trả
    private String hinhThucThanhToan; // Tiền mặt, Chuyển khoản, Thẻ hoặc Ví điện tử
    private String trangThai; // Chưa thanh toán, Đã thanh toán hoặc Đã hủy
    private String ghiChu; // Ghi chú
    private List<ChiTietHoaDon> dsChiTiet = new ArrayList<>(); // Các dòng chi tiết

    public String getMaHoaDon() {
        return maHoaDon;
    }

    public void setMaHoaDon(String maHoaDon) {
        this.maHoaDon = maHoaDon;
    }

    public BenhNhan getMaBenhNhan() {
        return maBenhNhan;
    }

    public void setMaBenhNhan(BenhNhan maBenhNhan) {
        this.maBenhNhan = maBenhNhan;
    }

    public NhanVien getMaNhanVien() {
        return maNhanVien;
    }

    public void setMaNhanVien(NhanVien maNhanVien) {
        this.maNhanVien = maNhanVien;
    }

    public LocalDateTime getNgayLap() {
        return ngayLap;
    }

    public void setNgayLap(LocalDateTime ngayLap) {
        this.ngayLap = ngayLap;
    }

    public LocalDateTime getNgayThanhToan() {
        return ngayThanhToan;
    }

    public void setNgayThanhToan(LocalDateTime ngayThanhToan) {
        this.ngayThanhToan = ngayThanhToan;
    }

    public BigDecimal getTienGiamGia() {
        return tienGiamGia;
    }

    public void setTienGiamGia(BigDecimal tienGiamGia) {
        this.tienGiamGia = tienGiamGia;
    }

    public BigDecimal getPhanTramThue() {
        return phanTramThue;
    }

    public void setPhanTramThue(BigDecimal phanTramThue) {
        this.phanTramThue = phanTramThue;
    }

    public BigDecimal getTongTienTruocThue() {
        return tongTienTruocThue;
    }

    public void setTongTienTruocThue(BigDecimal tongTienTruocThue) {
        this.tongTienTruocThue = tongTienTruocThue;
    }

    public BigDecimal getTongTien() {
        return tongTien;
    }

    public void setTongTien(BigDecimal tongTien) {
        this.tongTien = tongTien;
    }

    public String getHinhThucThanhToan() {
        return hinhThucThanhToan;
    }

    public void setHinhThucThanhToan(String hinhThucThanhToan) {
        this.hinhThucThanhToan = hinhThucThanhToan;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

    public List<ChiTietHoaDon> getDsChiTiet() {
        return dsChiTiet;
    }

    public void setDsChiTiet(List<ChiTietHoaDon> dsChiTiet) {
        this.dsChiTiet = dsChiTiet;
    }

}
