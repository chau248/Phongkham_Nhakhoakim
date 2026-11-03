package nhakhoakim.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import nhakhoakim.entity.ChiTietHoaDon;
import nhakhoakim.entity.HoaDon;

// Truy cập bảng ChiTietHoaDon
public class ChiTietHoaDonDAO {

    static final String CAC_COT = "maChiTiet, maHoaDon, maDichVu, soLuong, donGia, giamGia, soFDIRang, ghiChu";
    static final String SQL_THEM = "INSERT INTO dbo.ChiTietHoaDon (" + CAC_COT + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    static final String SQL_THEO_HOA_DON = "SELECT " + CAC_COT + " FROM dbo.ChiTietHoaDon WHERE maHoaDon = ? ORDER BY maChiTiet";

    // Thêm một dòng chi tiết
    void them(Connection c, ChiTietHoaDon ct) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(SQL_THEM)) {
            ps.setString(1, ct.getMaChiTiet());
            ps.setString(2, ct.getMaHoaDon().getMaHoaDon());
            ps.setString(3, ct.getMaDichVu());
            ps.setInt(4, ct.getSoLuong());
            ps.setBigDecimal(5, ct.getDonGia());
            ps.setBigDecimal(6, ct.getGiamGia() == null ? BigDecimal.ZERO : ct.getGiamGia());
            if (ct.getSoFDIRang() > 0) {
                ps.setInt(7, ct.getSoFDIRang());
            } else {
                ps.setNull(7, Types.INTEGER);
            }
            ps.setString(8, ct.getGhiChu());
            ps.executeUpdate();
        }
    }

    // Các dòng chi tiết của một hóa đơn
    public List<ChiTietHoaDon> layTheoHoaDon(String maHoaDon) {
        List<ChiTietHoaDon> ds = new ArrayList<>();
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement ps = c.prepareStatement(SQL_THEO_HOA_DON)) {
            ps.setString(1, maHoaDon);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ChiTietHoaDon ct = new ChiTietHoaDon();
                    ct.setMaChiTiet(rs.getString("maChiTiet"));
                    HoaDon hd = new HoaDon();
                    hd.setMaHoaDon(rs.getString("maHoaDon"));
                    ct.setMaHoaDon(hd);
                    ct.setMaDichVu(rs.getString("maDichVu"));
                    ct.setSoLuong(rs.getInt("soLuong"));
                    ct.setDonGia(rs.getBigDecimal("donGia"));
                    ct.setGiamGia(rs.getBigDecimal("giamGia"));
                    ct.setSoFDIRang(rs.getInt("soFDIRang")); // NULL đọc thành 0
                    ct.setGhiChu(rs.getString("ghiChu"));
                    ds.add(ct);
                }
            }
        } catch (SQLException e) {
            throw new LoiCSDL("Không đọc được chi tiết hóa đơn: " + e.getMessage(), e);
        }
        return ds;
    }
}
