package nhakhoakim.dao;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import nhakhoakim.entity.HoaDon;
import nhakhoakim.entity.LuotKham;
public class HoaDonDAO {
    HoSoBenhAnDAO hoSoBenhAnDAO = new HoSoBenhAnDAO();
    // Tạo hóa đơn rỗng cho lượt khám đã có lịch hẹn; mã hóa đơn suy ra từ mã lịch hẹn (LH000001 -> HD000001)
    public HoaDon taoHoaDon(LuotKham lk) {
        String maLichHen = lk.getMaLichHen();
        if (maLichHen == null || maLichHen.length() <= 2) {
            throw new NghiepVuException("Lượt khám không có lịch hẹn nên chưa tạo được hóa đơn tự động");
        }
        String maBN = hoSoBenhAnDAO.timTheoMa(lk.getMaHoSo()).getMaBN();
        String maHoaDon = "HD" + maLichHen.substring(2);
        LocalDateTime ngayLap = lk.getGioKetThuc() != null ? lk.getGioKetThuc() : LocalDateTime.now();
        String sql = "INSERT INTO dbo.HoaDon (maHoaDon, maBN, ngayLap) VALUES (?, ?, ?)";
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, maHoaDon);
            ps.setString(2, maBN);
            ps.setObject(3, ngayLap);
            if (ps.executeUpdate() != 1) {
                throw new LoiCSDL("Không thêm được hóa đơn", new SQLException());
            }
            return new HoaDon(maHoaDon, maBN, ngayLap);
        } catch (SQLException e) {
            throw new LoiCSDL("Không thêm được hóa đơn: " + e.getMessage(), e);
        }
    }
    // Xác định thanh toán: cập nhật hình thức, nhân viên lập, các khoản tiền, trạng thái; có ngày thanh toán khi "Đã thanh toán"
    public HoaDon xacDinhThanhToan(String maHoaDon, String hinhThucThanhToan, String ghiChu, String maNhanVien,
            BigDecimal tongTien, BigDecimal tienGiamGia, BigDecimal tongTienTruocThue, BigDecimal phanTramThue, String trangThai) {
        String sql = "UPDATE dbo.HoaDon SET hinhThucThanhToan = ?, ghiChu = ?, maNhanVienLap = ?, tongTien = ?, tienGiamGia = ?, "
                + "tongTienTruocThue = ?, phanTramThue = ?, trangThaiThanhToan = ?, ngayThanhToan = ? WHERE maHoaDon = ?";
        Timestamp ngayThanhToan = "Đã thanh toán".equals(trangThai) ? Timestamp.valueOf(LocalDateTime.now()) : null;
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, hinhThucThanhToan);
            ps.setString(2, ghiChu);
            ps.setString(3, maNhanVien);
            ps.setBigDecimal(4, tongTien);
            ps.setBigDecimal(5, tienGiamGia);
            ps.setBigDecimal(6, tongTienTruocThue);
            ps.setBigDecimal(7, phanTramThue);
            ps.setString(8, trangThai);
            ps.setTimestamp(9, ngayThanhToan);
            ps.setString(10, maHoaDon);
            if (ps.executeUpdate() != 1) {
                throw new LoiCSDL("Không cập nhật được hóa đơn", new SQLException());
            }
        } catch (SQLException e) {
            throw new LoiCSDL("Không cập nhật được hóa đơn: " + e.getMessage(), e);
        }
        return timHoaDon(maHoaDon);
    }
    public HoaDon timHoaDon(String maHoaDon) {
        String sql = "SELECT * FROM dbo.HoaDon WHERE maHoaDon = ?";
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, maHoaDon);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? docHoaDon(rs) : null;
            }
        } catch (SQLException e) {
            throw new LoiCSDL("Không đọc được hóa đơn: " + e.getMessage(), e);
        }
    }
    List<HoaDon> layDanhSachHoaDon() {
        List<HoaDon> ds = new ArrayList<>();
        String sql = "SELECT * FROM dbo.HoaDon";
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                ds.add(docHoaDon(rs));
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
        hd.setMaNhanVien(rs.getString("maNhanVienLap"));
        hd.setNgayLap(rs.getObject("ngayLap", LocalDateTime.class));
        hd.setNgayThanhToan(rs.getObject("ngayThanhToan", LocalDateTime.class));
        hd.setTienGiamGia(rs.getBigDecimal("tienGiamGia"));
        hd.setPhanTramThue(rs.getBigDecimal("phanTramThue"));
        hd.setTongTienTruocThue(rs.getBigDecimal("tongTienTruocThue"));
        hd.setTongTien(rs.getBigDecimal("tongTien"));
        hd.setHinhThucThanhToan(rs.getString("hinhThucThanhToan"));
        hd.setTrangThai(rs.getString("trangThaiThanhToan"));
        hd.setGhiChu(rs.getString("ghiChu"));
        return hd;
    }
}
