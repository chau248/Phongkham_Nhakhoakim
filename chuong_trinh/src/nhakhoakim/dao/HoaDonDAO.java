package nhakhoakim.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import nhakhoakim.entity.ChiTietHoaDon;
import nhakhoakim.entity.HoaDon;

// Truy cập bảng HoaDon (chỉ lớp DAO được viết SQL). Hóa đơn được lập cùng dòng chi tiết và gắn vào lượt khám trong một transaction.
public class HoaDonDAO {

    private ChiTietHoaDonDAO chiTietDAO = new ChiTietHoaDonDAO();

    static final String CAC_COT = "maHoaDon, maBN, maNhanVienLap, ngayLap, tienGiamGia, phanTramThue, tongTienTruocThue, tongTien,"
            + " hinhThucThanhToan, trangThaiThanhToan, ghiChu, ngayThanhToan";
    static final String SQL_THEM = "INSERT INTO dbo.HoaDon (maHoaDon, maBN, maNhanVienLap, ngayLap, tienGiamGia, phanTramThue, tongTienTruocThue,"
            + " tongTien, hinhThucThanhToan, trangThaiThanhToan, ghiChu) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    static final String SQL_GAN_LUOT_KHAM = "UPDATE dbo.LuotKham SET maHoaDon = ? WHERE maLuotKham = ? AND maHoaDon IS NULL";
    static final String SQL_THEO_MA = "SELECT " + CAC_COT + " FROM dbo.HoaDon WHERE maHoaDon = ?";
    static final String SQL_TAT_CA = "SELECT " + CAC_COT + " FROM dbo.HoaDon ORDER BY ngayLap DESC, maHoaDon DESC";
    static final String SQL_THANH_TOAN = "UPDATE dbo.HoaDon SET trangThaiThanhToan = N'Đã thanh toán', hinhThucThanhToan = ?, ngayThanhToan = ?,"
            + " maNhanVienLap = COALESCE(maNhanVienLap, ?) WHERE maHoaDon = ? AND trangThaiThanhToan = N'Chưa thanh toán'";
    static final String SQL_HUY = "UPDATE dbo.HoaDon SET trangThaiThanhToan = N'Đã hủy', ghiChu = ? WHERE maHoaDon = ? AND trangThaiThanhToan = N'Chưa thanh toán'";
    static final String SQL_GO_LUOT_KHAM = "UPDATE dbo.LuotKham SET maHoaDon = NULL WHERE maHoaDon = ?";

    // Lưu hóa đơn, các dòng chi tiết và gắn hóa đơn vào lượt khám trong một transaction.
    // Lượt khám đã có hóa đơn (người khác vừa lập) thì hủy tất cả và báo lỗi nghiệp vụ.
    public void themKemChiTiet(HoaDon hd, List<ChiTietHoaDon> dsChiTiet, String maLuotKham) {
        try (Connection c = DatabaseConnection.getConnection()) {
            c.setAutoCommit(false);
            try {
                try (PreparedStatement ps = c.prepareStatement(SQL_THEM)) {
                    ps.setString(1, hd.getMaHoaDon());
                    ps.setString(2, hd.getMaBenhNhan());
                    ps.setString(3, hd.getMaNhanVien());
                    ps.setObject(4, hd.getNgayLap());
                    ps.setBigDecimal(5, hd.getTienGiamGia());
                    ps.setBigDecimal(6, hd.getPhanTramThue());
                    ps.setBigDecimal(7, hd.getTongTienTruocThue());
                    ps.setBigDecimal(8, hd.getTongTien());
                    ps.setString(9, hd.getHinhThucThanhToan());
                    ps.setString(10, hd.getTrangThai());
                    ps.setString(11, hd.getGhiChu());
                    ps.executeUpdate();
                }
                for (ChiTietHoaDon ct : dsChiTiet) {
                    chiTietDAO.them(c, ct);
                }
                try (PreparedStatement ps = c.prepareStatement(SQL_GAN_LUOT_KHAM)) {
                    ps.setString(1, hd.getMaHoaDon());
                    ps.setString(2, maLuotKham);
                    if (ps.executeUpdate() != 1) {
                        throw new NghiepVuException("Lượt khám " + maLuotKham + " đã có hóa đơn");
                    }
                }
                c.commit();
            } catch (SQLException | RuntimeException e) {
                c.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new LoiCSDL("Không lập được hóa đơn: " + e.getMessage(), e);
        }
    }

    // Tìm hóa đơn theo mã (chưa kèm dòng chi tiết); không có thì trả về null
    public HoaDon timTheoMa(String maHoaDon) {
        List<HoaDon> ds = layDanhSach(SQL_THEO_MA, maHoaDon);
        return ds.isEmpty() ? null : ds.get(0);
    }

    // Tất cả hóa đơn, mới nhất trước (chưa kèm dòng chi tiết)
    public List<HoaDon> layTatCa() {
        return layDanhSach(SQL_TAT_CA, null);
    }

    // Ghi nhận thanh toán cho hóa đơn Chưa thanh toán; trả về false nếu hóa đơn không còn ở trạng thái đó
    public boolean thanhToan(String maHoaDon, String hinhThuc, LocalDateTime luc, String maNhanVien) {
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement ps = c.prepareStatement(SQL_THANH_TOAN)) {
            ps.setString(1, hinhThuc);
            ps.setTimestamp(2, Timestamp.valueOf(luc));
            ps.setString(3, maNhanVien);
            ps.setString(4, maHoaDon);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new LoiCSDL("Không ghi nhận được thanh toán: " + e.getMessage(), e);
        }
    }

    // Hủy hóa đơn Chưa thanh toán và trả lượt khám về danh sách chờ thanh toán (cùng một transaction); trả về false nếu hóa đơn không hủy được
    public boolean huy(String maHoaDon, String ghiChu) {
        try (Connection c = DatabaseConnection.getConnection()) {
            c.setAutoCommit(false);
            try {
                boolean duoc;
                try (PreparedStatement ps = c.prepareStatement(SQL_HUY)) {
                    ps.setString(1, ghiChu);
                    ps.setString(2, maHoaDon);
                    duoc = ps.executeUpdate() == 1;
                }
                if (duoc) {
                    try (PreparedStatement ps = c.prepareStatement(SQL_GO_LUOT_KHAM)) {
                        ps.setString(1, maHoaDon);
                        ps.executeUpdate();
                    }
                }
                c.commit();
                return duoc;
            } catch (SQLException | RuntimeException e) {
                c.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new LoiCSDL("Không hủy được hóa đơn: " + e.getMessage(), e);
        }
    }

    // Chạy câu SELECT (có hoặc không có một tham số chuỗi) và đọc tất cả dòng
    private List<HoaDon> layDanhSach(String sql, String thamSo) {
        List<HoaDon> ds = new ArrayList<>();
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            if (thamSo != null) {
                ps.setString(1, thamSo);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
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
                    ds.add(hd);
                }
            }
        } catch (SQLException e) {
            throw new LoiCSDL("Không đọc được hóa đơn: " + e.getMessage(), e);
        }
        return ds;
    }
}
