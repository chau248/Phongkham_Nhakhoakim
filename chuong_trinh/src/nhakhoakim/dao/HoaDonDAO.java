package nhakhoakim.dao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import nhakhoakim.entity.ChiTietHoaDon;
import nhakhoakim.entity.HoaDon;
import nhakhoakim.entity.LuotKham;
public class HoaDonDAO {
    HoSoBenhAnDAO hoSoBenhAnDAO = new HoSoBenhAnDAO();
    public void taoHoaDon(LuotKham lk){
        if (lk.getTrangThai()=="Hoàn thành") {
            String maBN=hoSoBenhAnDAO.timTheoMa(lk.getMaHoSo()).getMaBN();
            String sql="insert into dbo.HoaDon(maHoaDon,maBN,ngayLap) values(?,?,?)";
            try (Connection c = DatabaseConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setString(1, lk.getMaHoaDon());
                ps.setString(2, maBN);
                ps.setObject(3, lk.getGioKetThuc());
                int n = ps.executeUpdate();
                if (n != 1) {
                    throw new LoiCSDL("Không thêm được hóa đơn", new SQLException());
                }
            } catch (SQLException e) {
                throw new LoiCSDL("Không thêm được hóa đơn: " + e.getMessage(), e);
            }
        }
    }
    // Xác định thanh toán cho hóa đơn (cập nhật hinhThucThanhToan, ghiChu, maNhanVien, tongTien, trangThai)
    public HoaDon xacDinhThanhToan(String maHoaDon, String hinhThucThanhToan, String ghiChu, String maNhanVien, java.math.BigDecimal tongTien, java.math.BigDecimal tienGiamGia, java.math.BigDecimal tongTienTruocThue, java.math.BigDecimal phanTramGiamGia, String trangThai) {
        String sql = "UPDATE dbo.HoaDon SET hinhThucThanhToan = ?, ghiChu = ?, maNhanVien = ?, tongTien = ?, tienGiamGia = ?, tongTienTruocThue = ?, phanTramGiamGia = ?, trangThai = ? WHERE maHoaDon = ?";
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, hinhThucThanhToan);
            ps.setString(2, ghiChu);
            ps.setString(3, maNhanVien);
            ps.setBigDecimal(4, tongTien);
            ps.setString(5, trangThai);
            ps.setString(6, maHoaDon);
            int n = ps.executeUpdate();
            if (n != 1) {
                throw new LoiCSDL("Không cập nhật được hóa đơn", new SQLException());
            }
        } catch (SQLException e) {
            throw new LoiCSDL("Không cập nhật được hóa đơn: " + e.getMessage(), e);
        }
        return layDanhSachHoaDon(maHoaDon).get(0);
    }
    // Thêm hóa đơn và chi tiết hóa đơn trong cùng một transaction (BN-04): một trong hai lỗi thì hủy cả hai
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
    private List<HoaDon> layDanhSachHoaDon(String maBN) {
        List<HoaDon> ds = new ArrayList<>();
        String sql = "SELECT * FROM dbo.HoaDon WHERE maBN = ?";
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, maBN);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ds.add(docHoaDon(rs));
                }
            }
        } catch (SQLException e) {
            throw new LoiCSDL("Không đọc được hóa đơn: " + e.getMessage(), e);
        }
        return ds;
    }
    private HoaDon docHoaDon(ResultSet rs) throws SQLException {
        HoaDon hd = new HoaDon();
        hd.setMaHoaDon(rs.getString("maHoaDon"));
        hd.setMaBenhNhan(rs.getString("maBN"));
        hd.setNgayLap(rs.getObject("ngayLap", java.time.LocalDateTime.class));
        hd.setTongTien(rs.getBigDecimal("tongTien"));
        hd.setTrangThai(rs.getString("trangThai"));
        hd.setGhiChu(rs.getString("ghiChu"));
        return hd;
    }
    private List<ChiTietHoaDon> layDanhSachChiTietHoaDon(String maHoaDon) {
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
