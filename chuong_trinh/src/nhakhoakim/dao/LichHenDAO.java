package nhakhoakim.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;

import nhakhoakim.entity.HoSoBenhAn;
import nhakhoakim.entity.LichHen;
import java.util.ArrayList;
import java.util.List;
public class LichHenDAO {
    public HoSoBenhAnDAO hoSoBenhAnDAO = new HoSoBenhAnDAO();
    public HoSoBenhAn themHoSoBenhAnVaoLichHen(HoSoBenhAn hs,LichHen lh) {
        String maHoSo = "HS" + lh.getMaLichHen().substring(2); // tạo mã hồ sơ từ mã lịch hẹn
        String sql="UPDATE dbo.LichHen SET maHoSo = ? WHERE maLichHen = ?";
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, maHoSo);
            ps.setString(2, lh.getMaLichHen());
            int n = ps.executeUpdate();
            if (n != 1) {
                throw new LoiCSDL("Không cập nhật được mã hồ sơ cho lịch hẹn", new SQLException());
            }
        } catch (SQLException e) {
            throw new LoiCSDL("Không cập nhật được mã hồ sơ cho lịch hẹn: " + e.getMessage(), e);
        }
        return hoSoBenhAnDAO.timTheoMa(maHoSo);
    }
    public void huyLichHen(String maLichHen, String lyDoHuy) {
        String sql = "UPDATE dbo.LichHen SET trangThai = N'Đã hủy', lyDoHuy = ? WHERE maLichHen = ?";
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, lyDoHuy);
            ps.setString(2, maLichHen);
            int n = ps.executeUpdate();
            if (n != 1) {
                throw new LoiCSDL("Không hủy được lịch hẹn", new SQLException());
            }
        } catch (SQLException e) {
            throw new LoiCSDL("Không hủy được lịch hẹn: " + e.getMessage(), e);
        }
    }
    public void capNhatLichHen(LocalTime ngayHen, LocalTime gioHen, String ghiChu, String maLichHen) {
        String sql = "UPDATE dbo.LichHen SET ngayHen = ?, gioHen = ?, ghiChu = ? WHERE maLichHen = ?";
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setObject(1, ngayHen);
            ps.setObject(2, gioHen);
            ps.setString(3, ghiChu);
            ps.setString(4, maLichHen);
            int n = ps.executeUpdate();
            if (n != 1) {
                throw new LoiCSDL("Không cập nhật được lịch hẹn", new SQLException());
            }
        } catch (SQLException e) {
            throw new LoiCSDL("Không cập nhật được lịch hẹn: " + e.getMessage(), e);
        }

    }
    // Lấy danh sách lịch hẹn theo câu lệnh SQL; tham số là các giá trị để điền vào dấu ? trong SQL
    private List<LichHen> layDanhSach(String sql, Object... thamSo) {
        List<LichHen> ds = new ArrayList<>();
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            for (int i = 0; i < thamSo.length; i++) {
                ps.setObject(i + 1, thamSo[i]);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    
                    ds.add(doc(rs));
                }
            }
        } catch (SQLException e) {
            throw new LoiCSDL("Không đọc được danh sách lịch hẹn: " + e.getMessage(), e);
        }
        return ds;
    }
    // doc 1 lich hen tu ResultSet
    private LichHen doc(ResultSet rs) throws SQLException {
        LichHen lh = new LichHen();
        lh.setMaLichHen(rs.getString("maLichHen"));
        lh.setMaBN(rs.getString("maBN"));
        lh.setMaNhanVien(rs.getString("maNhanVien"));
        lh.setMaDichVu(rs.getString("maDichVu"));
        lh.setNgayHen(rs.getObject("ngayHen", LocalDate.class));
        lh.setGioHen(rs.getObject("gioHen", LocalTime.class));
        lh.setTrangThai(rs.getString("trangThai"));
        lh.setPhong(rs.getString("phong"));
        lh.setThoiLuongDuKien(rs.getInt("thoiLuongDuKien"));
        lh.setMaHoSo(rs.getString("maHoSo"));
        lh.setHinhThucDat(rs.getString("hinhThucDat"));
        lh.setTrangThaiNhacHen(rs.getString("trangThaiNhacHen"));
        return lh;
    }
    // Thêm lịch hẹn; trả về đối tượng LichHen vừa thêm (có mã lịch hẹn); nếu lỗi thì ném lỗi LoiCSDL
    private LichHen themLichHen(LichHen lh) {
        String sql = "INSERT INTO dbo.LichHen (maLichHen, maBN, maNhanVien, maDichVu, ngayHen, gioHen, trangThai, phong, thoiLuongDuKien, maHoSo, hinhThucDat, trangThaiNhacHen) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, lh.getMaLichHen());
            ps.setString(2, lh.getMaBN());
            ps.setString(3, lh.getMaNhanVien());
            ps.setString(4, lh.getMaDichVu());
            ps.setObject(5, lh.getNgayHen());
            ps.setObject(6, lh.getGioHen());
            ps.setString(7, lh.getTrangThai());
            ps.setString(8, lh.getPhong());
            ps.setInt(9, lh.getThoiLuongDuKien());
            ps.setString(10, lh.getMaHoSo());
            ps.setString(11, lh.getHinhThucDat());
            ps.setString(12, lh.getTrangThaiNhacHen());
            int n = ps.executeUpdate();
            if (n != 1) {
                throw new LoiCSDL("Không thêm được lịch hẹn", new SQLException());
            }
        } catch (SQLException e) {
            throw new LoiCSDL("Không thêm được lịch hẹn: " + e.getMessage(), e);
        }
        return lh;
    }
}
