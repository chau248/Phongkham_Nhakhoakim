package nhakhoakim.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;

import nhakhoakim.entity.BenhNhan;
import nhakhoakim.entity.HoSoBenhAn;
import nhakhoakim.entity.LichHen;
import java.util.ArrayList;
import java.util.List;
public class LichHenDAO {
    public HoSoBenhAnDAO hoSoBenhAnDAO = new HoSoBenhAnDAO();
    private final BenhNhanDAO benhNhanDAO = new BenhNhanDAO();
    // Sửa thông tin hành chính của bệnh nhân đặt lịch hẹn (các cột này nằm ở bảng BenhNhan)
    public BenhNhan suaThongTinBenhNhan(LichHen lh, String ht, String gt, LocalDate ns, String sdt, String dc, String email, String cccd, String nguoiLH, String sdtNguoiLH) {
        String maBN = lh.getMaBN();
        String sql = "UPDATE dbo.BenhNhan SET hoTen = ?, ngaySinh = ?, gioiTinh = ?, sdt = ?, diaChi = ?, email = ?, soCCCD = ?, nguoiLienHeKhanCap = ?, sdtNguoiLienHe = ? WHERE maBN = ?";
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, ht);
            ps.setObject(2, ns);
            ps.setString(3, gt);
            ps.setString(4, sdt);
            ps.setString(5, dc);
            ps.setString(6, email);
            ps.setString(7, cccd);
            ps.setString(8, nguoiLH);
            ps.setString(9, sdtNguoiLH);
            ps.setString(10, maBN);
            if (ps.executeUpdate() != 1) {
                throw new LoiCSDL("Không cập nhật được thông tin bệnh nhân", new SQLException());
            }
        } catch (SQLException e) {
            throw new LoiCSDL("Không cập nhật được thông tin bệnh nhân: " + e.getMessage(), e);
        }
        return benhNhanDAO.timTheoMa(maBN);
    }
    // Hồ sơ bệnh án của bệnh nhân đặt lịch hẹn (bảng LichHen không có cột maHoSo nên tìm theo maBN)
    public HoSoBenhAn layHoSoCuaLichHen(LichHen lh) {
        return hoSoBenhAnDAO.timTheoBenhNhan(lh.getMaBN());
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
    public void capNhatLichHen(LocalDate ngayHen, LocalTime gioHen, String ghiChu, String maLichHen) {
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
        lh.setHinhThucDat(rs.getString("hinhThucDat"));
        lh.setDaNhacHen(rs.getBoolean("daNhacHen"));
        return lh;
    }
    // Thêm lịch hẹn; trả về đối tượng LichHen vừa thêm (có mã lịch hẹn); nếu lỗi thì ném lỗi LoiCSDL
    public LichHen themLichHen(LichHen lh) {
        String sql = "INSERT INTO dbo.LichHen (maLichHen, maBN, maNhanVien, maDichVu, ngayHen, gioHen, trangThai, phong, thoiLuongDuKien, hinhThucDat, daNhacHen) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
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
            ps.setString(10, lh.getHinhThucDat());
            ps.setBoolean(11, lh.isDaNhacHen());
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
