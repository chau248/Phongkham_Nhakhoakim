package nhakhoakim.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import nhakhoakim.entity.LuotKham;

// Truy cập bảng LuotKham
public class LuotKhamDAO {

    private static final String CAC_COT = "maLuotKham, maHoSo, maNhanVienPhuTrach, maLieuTrinh, maHoaDon, maLichHen, gioBatDau, gioKetThuc,"
            + " lyDoKham, chanDoan, huongDieuTri, donThuoc, ngayTaiKham, trangThai, ghiChu";

    // Thêm lượt khám mới
    public void them(LuotKham lk) {
        String sql = "INSERT INTO dbo.LuotKham (" + CAC_COT + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, lk.getMaLuotKham());
            ps.setString(2, lk.getMaHoSo());
            ps.setString(3, lk.getMaNhanVienPhuTrach());
            ps.setString(4, lk.getMaLieuTrinh());
            ps.setString(5, lk.getMaHoaDon());
            ps.setString(6, lk.getMaLichHen());
            ps.setObject(7, lk.getGioBatDau());
            ps.setObject(8, lk.getGioKetThuc());
            ps.setString(9, lk.getLyDoKham());
            ps.setString(10, lk.getChanDoan());
            ps.setString(11, lk.getHuongDieuTri());
            ps.setString(12, lk.getDonThuoc());
            ps.setObject(13, lk.getNgayTaiKham());
            ps.setString(14, lk.getTrangThai());
            ps.setString(15, lk.getGhiChu());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new LoiCSDL("Không thêm được lượt khám: " + e.getMessage(), e);
        }
    }

    // Cập nhật nội dung khám, trạng thái và giờ kết thúc của lượt khám
    public void capNhat(LuotKham lk) {
        String sql = "UPDATE dbo.LuotKham SET gioKetThuc = ?, lyDoKham = ?, chanDoan = ?, huongDieuTri = ?, donThuoc = ?,"
                + " ngayTaiKham = ?, trangThai = ?, ghiChu = ?, maLieuTrinh = ? WHERE maLuotKham = ?";
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setObject(1, lk.getGioKetThuc());
            ps.setString(2, lk.getLyDoKham());
            ps.setString(3, lk.getChanDoan());
            ps.setString(4, lk.getHuongDieuTri());
            ps.setString(5, lk.getDonThuoc());
            ps.setObject(6, lk.getNgayTaiKham());
            ps.setString(7, lk.getTrangThai());
            ps.setString(8, lk.getGhiChu());
            ps.setString(9, lk.getMaLieuTrinh());
            ps.setString(10, lk.getMaLuotKham());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new LoiCSDL("Không cập nhật được lượt khám: " + e.getMessage(), e);
        }
    }

    // Gắn mã hóa đơn vào lượt khám chưa có hóa đơn; trả về true nếu gắn được
    public boolean ganHoaDon(String maLuotKham, String maHoaDon) {
        String sql = "UPDATE dbo.LuotKham SET maHoaDon = ? WHERE maLuotKham = ? AND maHoaDon IS NULL";
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, maHoaDon);
            ps.setString(2, maLuotKham);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new LoiCSDL("Không gắn được hóa đơn vào lượt khám: " + e.getMessage(), e);
        }
    }

    // Tìm lượt khám theo mã; không có thì trả về null
    public LuotKham timTheoMa(String maLuotKham) {
        List<LuotKham> ds = layDanhSach("SELECT " + CAC_COT + " FROM dbo.LuotKham WHERE maLuotKham = ?", maLuotKham);
        return ds.isEmpty() ? null : ds.get(0);
    }

    // Các lượt khám của một hồ sơ, mới nhất trước
    public List<LuotKham> layTheoHoSo(String maHoSo) {
        return layDanhSach("SELECT " + CAC_COT + " FROM dbo.LuotKham WHERE maHoSo = ? ORDER BY gioBatDau DESC", maHoSo);
    }

    // Lượt khám đang khám gần nhất của một nha sĩ; không có thì trả về null
    public LuotKham timDangKham(String maNhanVien) {
        List<LuotKham> ds = layDanhSach("SELECT " + CAC_COT + " FROM dbo.LuotKham WHERE maNhanVienPhuTrach = ? AND trangThai = N'Đang khám' ORDER BY gioBatDau DESC", maNhanVien);
        return ds.isEmpty() ? null : ds.get(0);
    }

    // Lượt khám đang khám của một hồ sơ (bệnh nhân); không có thì trả về null
    public LuotKham timDangKhamTheoHoSo(String maHoSo) {
        List<LuotKham> ds = layDanhSach("SELECT " + CAC_COT + " FROM dbo.LuotKham WHERE maHoSo = ? AND trangThai = N'Đang khám'", maHoSo);
        return ds.isEmpty() ? null : ds.get(0);
    }

    // Lượt khám đã hoàn thành nhưng chưa có hóa đơn (cho màn hình chờ thanh toán của Trường)
    public List<LuotKham> layChoThanhToan() {
        return layDanhSach("SELECT " + CAC_COT + " FROM dbo.LuotKham WHERE trangThai = N'Hoàn thành' AND maHoaDon IS NULL ORDER BY gioKetThuc", null);
    }

    // Chạy câu SELECT (có hoặc không có một tham số chuỗi) và đọc tất cả dòng
    private List<LuotKham> layDanhSach(String sql, String thamSo) {
        List<LuotKham> ds = new ArrayList<>();
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            if (thamSo != null) {
                ps.setString(1, thamSo);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ds.add(doc(rs));
                }
            }
        } catch (SQLException e) {
            throw new LoiCSDL("Không đọc được lượt khám: " + e.getMessage(), e);
        }
        return ds;
    }

    // Đổi một dòng kết quả thành đối tượng LuotKham
    private LuotKham doc(ResultSet rs) throws SQLException {
        LuotKham lk = new LuotKham();
        lk.setMaLuotKham(rs.getString("maLuotKham"));
        lk.setMaHoSo(rs.getString("maHoSo"));
        lk.setMaNhanVienPhuTrach(rs.getString("maNhanVienPhuTrach"));
        lk.setMaLieuTrinh(rs.getString("maLieuTrinh"));
        lk.setMaHoaDon(rs.getString("maHoaDon"));
        lk.setMaLichHen(rs.getString("maLichHen"));
        lk.setGioBatDau(rs.getObject("gioBatDau", LocalDateTime.class));
        lk.setGioKetThuc(rs.getObject("gioKetThuc", LocalDateTime.class));
        lk.setLyDoKham(rs.getString("lyDoKham"));
        lk.setChanDoan(rs.getString("chanDoan"));
        lk.setHuongDieuTri(rs.getString("huongDieuTri"));
        lk.setDonThuoc(rs.getString("donThuoc"));
        lk.setNgayTaiKham(rs.getObject("ngayTaiKham", LocalDate.class));
        lk.setTrangThai(rs.getString("trangThai"));
        lk.setGhiChu(rs.getString("ghiChu"));
        return lk;
    }
}
