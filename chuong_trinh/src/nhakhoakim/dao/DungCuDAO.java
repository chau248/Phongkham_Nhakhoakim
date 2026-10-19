package nhakhoakim.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import nhakhoakim.entity.DungCu;

// Truy cập bảng DungCu và DichVuDungCu
public class DungCuDAO {

    // Dụng cụ cần cho một dịch vụ, kèm số lượng cần (lấy từ DichVuDungCu)
    public List<DungCu> layTheoDichVu(String maDichVu) {
        List<DungCu> ds = new ArrayList<>();
        String sql = "SELECT d.maDungCu, d.tenDungCu, d.loaiDichVu, d.trangThaiVoTrung, d.ngayTietTrungCuoi, d.phong, x.soLuong"
                + " FROM dbo.DungCu d JOIN dbo.DichVuDungCu x ON x.maDungCu = d.maDungCu WHERE x.maDichVu = ? ORDER BY d.maDungCu";
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, maDichVu);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    DungCu d = new DungCu();
                    d.setMaDungCu(rs.getString("maDungCu"));
                    d.setTenDungCu(rs.getString("tenDungCu"));
                    d.setLoaiDichVu(rs.getString("loaiDichVu"));
                    d.setTrangThaiVoTrung(rs.getString("trangThaiVoTrung"));
                    d.setNgayTietTrungCuoi(rs.getObject("ngayTietTrungCuoi", LocalDateTime.class));
                    d.setPhong(rs.getString("phong"));
                    d.setSoLuongCan(rs.getInt("soLuong"));
                    ds.add(d);
                }
            }
        } catch (SQLException e) {
            throw new LoiCSDL("Không đọc được dụng cụ: " + e.getMessage(), e);
        }
        return ds;
    }

    // Đổi trạng thái tiệt trùng của một dụng cụ, chỉ khi nó đang ở trạng thái mong đợi (tránh hai ca cùng lấy một dụng cụ);
    // trả về true nếu đổi được
    public boolean capNhatTrangThai(String maDungCu, String trangThaiMongDoi, String trangThaiMoi) {
        String sql = "UPDATE dbo.DungCu SET trangThaiVoTrung = ? WHERE maDungCu = ? AND trangThaiVoTrung = ?";
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, trangThaiMoi);
            ps.setString(2, maDungCu);
            ps.setString(3, trangThaiMongDoi);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new LoiCSDL("Không cập nhật được dụng cụ: " + e.getMessage(), e);
        }
    }
}
