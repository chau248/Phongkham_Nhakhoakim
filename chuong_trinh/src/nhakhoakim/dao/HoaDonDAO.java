package nhakhoakim.dao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import nhakhoakim.entity.HoaDon;
import nhakhoakim.entity.LuotKham;
public class HoaDonDAO {
    HoSoBenhAnDAO hoSoBenhAnDAO = new HoSoBenhAnDAO();
    public HoaDon taoHoaDon(LuotKham lk){ 
            String maBN=hoSoBenhAnDAO.timTheoMa(lk.getMaHoSo()).getMaBN();
            String sql="insert into dbo.HoaDon(maHoaDon,maBN,ngayLap) values(?,?,?)";
            String maHoaDon = "HD" + lk.getMaLichHen().substring(2); // tạo mã hóa đơn từ mã lịch hẹn
            try (Connection c = DatabaseConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setString(1, maHoaDon);
                ps.setString(2, maBN);
                ps.setObject(3, lk.getGioKetThuc());
                int n = ps.executeUpdate();
                if (n != 1) {
                    throw new LoiCSDL("Không thêm được hóa đơn", new SQLException());
                }
                return new HoaDon(maHoaDon, maBN, lk.getGioKetThuc());
            } catch (SQLException e) {
                throw new LoiCSDL("Không thêm được hóa đơn: " + e.getMessage(), e);
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
        return timHoaDon(maHoaDon);
    }
    // Thêm hóa đơn và chi tiết hóa đơn trong cùng một transaction (BN-04): một trong hai lỗi thì hủy cả hai
    public HoaDon timHoaDon(String maHoaDon) {
        String sql = "SELECT * FROM dbo.HoaDon WHERE maHoaDon = ?";
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, maHoaDon);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return docHoaDon(rs);
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            throw new LoiCSDL("Không đọc được hóa đơn: " + e.getMessage(), e);
        }
    }
    List<HoaDon> layDanhSachHoaDon() {
        List<HoaDon> ds = new ArrayList<>();
        String sql = "SELECT * FROM dbo.HoaDon";
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
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
    
}
