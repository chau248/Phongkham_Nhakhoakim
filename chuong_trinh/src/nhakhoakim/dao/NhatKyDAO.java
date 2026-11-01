package nhakhoakim.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

// Ghi bảng NhatKyHeThong: ai làm gì, lúc nào, giá trị cũ và mới (chỉ ghi thêm, không sửa, không xóa)
public class NhatKyDAO {

    static final String SQL_THEM = "INSERT INTO dbo.NhatKyHeThong (maNhanVien, hanhDong, doiTuong, maDoiTuong, chiTiet) VALUES (?, ?, ?, ?, ?)";

    // Thêm một dòng nhật ký; chuỗi dài hơn độ dài cột thì cắt bớt để không bị lỗi cắt chuỗi của SQL Server
    public void them(String maNV, String hanhDong, String doiTuong, String maDoiTuong, String chiTiet) {
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement ps = c.prepareStatement(SQL_THEM)) {
            ps.setString(1, maNV);
            ps.setString(2, cat(hanhDong, 50));
            ps.setString(3, cat(doiTuong, 50));
            ps.setString(4, cat(maDoiTuong, 30));
            ps.setString(5, cat(chiTiet, 500));
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new LoiCSDL("Không ghi được nhật ký: " + e.getMessage(), e);
        }
    }

    // Cắt chuỗi cho vừa độ dài cột (null giữ nguyên)
    private static String cat(String s, int toiDa) {
        return s == null || s.length() <= toiDa ? s : s.substring(0, toiDa);
    }
}
