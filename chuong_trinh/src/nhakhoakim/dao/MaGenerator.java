package nhakhoakim.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

// Sinh mã dạng tiền tố + 6 chữ số (BN000001, HS000001...) dựa vào bảng BoDemMa
// Bản tạm do Hậu viết để chạy thử; Tâm thay bằng bản đầy đủ (giữ nguyên tên hàm taoMa).
public class MaGenerator {

    // Tăng bộ đếm của tiền tố lên 1 và trả về mã mới
    public static String taoMa(String tienTo) {
        String sql = "UPDATE dbo.BoDemMa SET giaTriHienTai = giaTriHienTai + 1 OUTPUT INSERTED.giaTriHienTai WHERE tienTo = ?";
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, tienTo);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new NghiepVuException("Chưa có bộ đếm mã cho tiền tố " + tienTo);
                }
                return tienTo + String.format("%06d", rs.getInt(1));
            }
        } catch (SQLException e) {
            throw new LoiCSDL("Không sinh được mã " + tienTo + ": " + e.getMessage(), e);
        }
    }
}
