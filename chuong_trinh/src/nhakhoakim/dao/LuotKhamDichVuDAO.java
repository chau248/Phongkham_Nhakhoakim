package nhakhoakim.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import nhakhoakim.entity.LuotKhamDichVu;

// Truy cập bảng LuotKhamDichVu (dịch vụ không gắn răng của lượt khám)
public class LuotKhamDichVuDAO {

    // Thêm một dịch vụ vào lượt khám
    public void them(LuotKhamDichVu dv) {
        String sql = "INSERT INTO dbo.LuotKhamDichVu (maLuotKham, maDichVu, soLuong, ghiChu) VALUES (?, ?, ?, ?)";
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, dv.getMaLuotKham());
            ps.setString(2, dv.getMaDichVu());
            ps.setInt(3, dv.getSoLuong());
            ps.setString(4, dv.getGhiChu());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new LoiCSDL("Không thêm được dịch vụ vào lượt khám: " + e.getMessage(), e);
        }
    }

    // Xóa một dòng dịch vụ theo số thứ tự dòng và mã lượt khám (để không xóa nhầm dòng của lượt khác);
    // trả về true nếu xóa được một dòng
    public boolean xoa(String maLuotKham, int maDong) {
        String sql = "DELETE FROM dbo.LuotKhamDichVu WHERE maDong = ? AND maLuotKham = ?";
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, maDong);
            ps.setString(2, maLuotKham);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new LoiCSDL("Không xóa được dịch vụ khỏi lượt khám: " + e.getMessage(), e);
        }
    }

    // Các dịch vụ không gắn răng của một lượt khám
    public List<LuotKhamDichVu> layTheoLuotKham(String maLuotKham) {
        List<LuotKhamDichVu> ds = new ArrayList<>();
        String sql = "SELECT maDong, maLuotKham, maDichVu, soLuong, ghiChu FROM dbo.LuotKhamDichVu WHERE maLuotKham = ? ORDER BY maDong";
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, maLuotKham);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    LuotKhamDichVu dv = new LuotKhamDichVu();
                    dv.setMaDong(rs.getInt("maDong"));
                    dv.setMaLuotKham(rs.getString("maLuotKham"));
                    dv.setMaDichVu(rs.getString("maDichVu"));
                    dv.setSoLuong(rs.getInt("soLuong"));
                    dv.setGhiChu(rs.getString("ghiChu"));
                    ds.add(dv);
                }
            }
        } catch (SQLException e) {
            throw new LoiCSDL("Không đọc được dịch vụ của lượt khám: " + e.getMessage(), e);
        }
        return ds;
    }
}
