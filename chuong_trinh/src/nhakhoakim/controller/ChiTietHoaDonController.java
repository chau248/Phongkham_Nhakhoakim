package nhakhoakim.controller;

import nhakhoakim.dao.*;
import nhakhoakim.entity.ChiTietHoaDon;
import java.math.BigDecimal;
import java.util.List;
public class ChiTietHoaDonController {
    private ChiTietHoaDonDAO cthdDAO;
    public ChiTietHoaDonController() {
        this.cthdDAO = new ChiTietHoaDonDAO();
    }
    public void taoChiTietHoaDon(String maHoaDon, int soLuong, int soFDIRang, String maDichVu, String ghiChu, String maChiTiet, BigDecimal donGia,
            BigDecimal giamGia) {
        cthdDAO.taoChiTietHoaDon(maHoaDon, soLuong, soFDIRang, maDichVu, ghiChu, maChiTiet, donGia, giamGia);
    }
    public List<ChiTietHoaDon> layDanhSachChiTietHoaDon(String maHoaDon) {
        return cthdDAO.layDanhSachChiTietHoaDon(maHoaDon);
    }
}
