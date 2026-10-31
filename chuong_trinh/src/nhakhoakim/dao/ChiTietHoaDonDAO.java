package nhakhoakim.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import nhakhoakim.entity.ChiTietHoaDon;

public class ChiTietHoaDonDAO {
    public ChiTietHoaDon taoChiTietHoaDon(String maHoaDon, int soLuong, int soFDIRang, String maDichVu, String ghiChu, String maChiTiet, java.math.BigDecimal donGia,
            java.math.BigDecimal giamGia) {
        String sql = "INSERT INTO dbo.ChiTietHoaDon (maHoaDon, soLuong, soFDIRang, maDichVu, ghiChu, maChiTiet, donGia, giamGia) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, maHoaDon);
            ps.setInt(2, soLuong);
            ps.setInt(3, soFDIRang);
            ps.setString(4, maDichVu);
            ps.setString(5, ghiChu);
            ps.setString(6, maChiTiet);
            ps.setBigDecimal(7, donGia);
            ps.setBigDecimal(8, giamGia);
            int n = ps.executeUpdate();
            if (n != 1) {
                throw new LoiCSDL("Không thêm được chi tiết hóa đơn", new SQLException());
            }
        } catch (SQLException e) {
            throw new LoiCSDL("Không thêm được chi tiết hóa đơn: " + e.getMessage(), e);
        }
        return new ChiTietHoaDon(maHoaDon, soLuong, soFDIRang, maDichVu, ghiChu, maChiTiet, donGia, giamGia);
        
    }
    public List<ChiTietHoaDon> layDanhSachChiTietHoaDon(String maHoaDon) {
        List<ChiTietHoaDon> ds = new ArrayList<>();
        String sql = "SELECT * FROM dbo.ChiTietHoaDon WHERE maHoaDon = ?";
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, maHoaDon);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ds.add(docChiTiet(rs));
                }
            }
        } catch (SQLException e) {
            throw new LoiCSDL("Không đọc được chi tiết hóa đơn: " + e.getMessage(), e);
        }
        return ds;
    }
    private ChiTietHoaDon docChiTiet(ResultSet rs) throws SQLException {
        ChiTietHoaDon ct = new ChiTietHoaDon();
        ct.setMaHoaDon(rs.getString("maHoaDon"));
        ct.setMaDichVu(rs.getString("maDichVu"));
        ct.setSoLuong(rs.getInt("soLuong"));
        ct.setDonGia(rs.getBigDecimal("donGia"));
        ct.setGiamGia(rs.getBigDecimal("giamGia"));
        ct.setGhiChu(rs.getString("ghiChu"));
        ct.setMaChiTiet(rs.getString("maChiTiet"));
        ct.setSoFDIRang(rs.getInt("soFDIRang"));
        return ct;
    }
}
