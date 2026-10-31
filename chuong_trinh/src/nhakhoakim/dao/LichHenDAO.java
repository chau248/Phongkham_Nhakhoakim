package nhakhoakim.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

import nhakhoakim.entity.HoSoBenhAn;
import nhakhoakim.entity.LichHen;
import nhakhoakim.entity.DichVu;
import nhakhoakim.entity.NhanVien;
import java.util.ArrayList;
import java.util.List;
public class LichHenDAO {
    public HoSoBenhAnDAO hoSoBenhAnDAO = new HoSoBenhAnDAO();
    public HoSoBenhAn suaThongTinBenhNhan(LichHen lh, String ht, String gt, LocalDate ns, String sdt, String dc, String email, String cccd, String nguoiLH, String sdtNguoiLH) {
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
            int n = ps.executeUpdate();
            if (n != 1) {
                throw new LoiCSDL("Không cập nhật được thông tin bệnh nhân", new SQLException());
            }
            return hoSoBenhAnDAO.timTheoBenhNhan(maBN);
        } catch (SQLException e) {
            throw new LoiCSDL("Không cập nhật được thông tin bệnh nhân: " + e.getMessage(), e);
        }

    }
    public List<LichHen> layTheoKhoang(LocalDate tuNgay, LocalDate denNgay) {
        String sql = "SELECT l.*, b.hoTen AS tenBenhNhan, b.sdt AS sdtBenhNhan, d.tenDichVu, n.hoTen AS tenNhanVien "
                + "FROM dbo.LichHen l "
                + "JOIN dbo.BenhNhan b ON b.maBN = l.maBN "
                + "JOIN dbo.DichVu d ON d.maDichVu = l.maDichVu "
                + "JOIN dbo.NhanVien n ON n.maNV = l.maNhanVien "
                + "WHERE l.ngayHen >= ? AND l.ngayHen <= ? ORDER BY l.ngayHen, l.gioHen";
        return layDanhSach(sql, tuNgay, denNgay);
    }

    public List<DichVu> layDichVuHoatDong() {
        List<DichVu> ds = new ArrayList<>();
        String sql = "SELECT maDichVu, tenDichVu, thoiGianDuKien FROM dbo.DichVu "
                + "WHERE trangThaiHoatDong = 1 ORDER BY tenDichVu";
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                DichVu dv = new DichVu();
                dv.setMaDichVu(rs.getString("maDichVu"));
                dv.setTenDichVu(rs.getString("tenDichVu"));
                dv.setThoiGianDuKien(rs.getInt("thoiGianDuKien"));
                ds.add(dv);
            }
        } catch (SQLException e) {
            throw new LoiCSDL("Không đọc được danh sách dịch vụ: " + e.getMessage(), e);
        }
        return ds;
    }

    public List<NhanVien> layNhaSiDangLamViec() {
        List<NhanVien> ds = new ArrayList<>();
        String sql = "SELECT maNV, hoTen FROM dbo.NhanVien "
                + "WHERE vaiTro = N'Nha sĩ' AND trangThai = N'Đang làm việc' ORDER BY hoTen";
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                NhanVien nv = new NhanVien();
                nv.setMaNV(rs.getString("maNV"));
                nv.setHoTen(rs.getString("hoTen"));
                ds.add(nv);
            }
        } catch (SQLException e) {
            throw new LoiCSDL("Không đọc được danh sách nha sĩ: " + e.getMessage(), e);
        }
        return ds;
    }

    public boolean coTrungLich(String maNhanVien, LocalDate ngay, LocalTime gio, int thoiLuong,
            String maLichHenBoQua) {
        String sql = "SELECT gioHen, thoiLuongDuKien FROM dbo.LichHen "
                + "WHERE maNhanVien = ? AND ngayHen = ? AND trangThai <> N'Đã hủy'";
        if (maLichHenBoQua != null) {
            sql += " AND maLichHen <> ?";
        }
        LocalDateTime batDauMoi = LocalDateTime.of(ngay, gio);
        LocalDateTime ketThucMoi = batDauMoi.plusMinutes(thoiLuong);
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, maNhanVien);
            ps.setObject(2, ngay);
            if (maLichHenBoQua != null) {
                ps.setString(3, maLichHenBoQua);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    LocalTime gioCu = rs.getObject("gioHen", LocalTime.class);
                    LocalDateTime batDauCu = LocalDateTime.of(ngay, gioCu);
                    LocalDateTime ketThucCu = batDauCu.plusMinutes(rs.getInt("thoiLuongDuKien"));
                    if (batDauMoi.isBefore(ketThucCu) && ketThucMoi.isAfter(batDauCu)) {
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            throw new LoiCSDL("Không kiểm tra được thời gian trống: " + e.getMessage(), e);
        }
        return false;
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

    public LichHen timTheoMa(String maLichHen) {
        String sql = "SELECT l.*, b.hoTen AS tenBenhNhan, b.sdt AS sdtBenhNhan, d.tenDichVu, n.hoTen AS tenNhanVien "
                + "FROM dbo.LichHen l JOIN dbo.BenhNhan b ON b.maBN = l.maBN "
                + "JOIN dbo.DichVu d ON d.maDichVu = l.maDichVu "
                + "JOIN dbo.NhanVien n ON n.maNV = l.maNhanVien WHERE l.maLichHen = ?";
        List<LichHen> ds = layDanhSach(sql, maLichHen);
        return ds.isEmpty() ? null : ds.get(0);
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
        lh.setNgayTao(rs.getObject("ngayTao", LocalDateTime.class));
        lh.setPhong(rs.getString("phong"));
        lh.setThoiLuongDuKien(rs.getInt("thoiLuongDuKien"));
        lh.setHinhThucDat(rs.getString("hinhThucDat"));
        lh.setDaNhacHen(rs.getBoolean("daNhacHen"));
        lh.setLyDoHuy(rs.getString("lyDoHuy"));
        lh.setGhiChu(rs.getString("ghiChu"));
        lh.setTenBenhNhan(rs.getString("tenBenhNhan"));
        lh.setSdtBenhNhan(rs.getString("sdtBenhNhan"));
        lh.setTenDichVu(rs.getString("tenDichVu"));
        lh.setTenNhanVien(rs.getString("tenNhanVien"));
        return lh;
    }
    // Thêm lịch hẹn; trả về đối tượng LichHen vừa thêm (có mã lịch hẹn); nếu lỗi thì ném lỗi LoiCSDL
    public LichHen themLichHen(LichHen lh) {
        String sql = "INSERT INTO dbo.LichHen (maLichHen, maBN, maNhanVien, maDichVu, ngayHen, gioHen, trangThai, thoiLuongDuKien, phong, hinhThucDat, daNhacHen, ghiChu) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, lh.getMaLichHen());
            ps.setString(2, lh.getMaBN());
            ps.setString(3, lh.getMaNhanVien());
            ps.setString(4, lh.getMaDichVu());
            ps.setObject(5, lh.getNgayHen());
            ps.setObject(6, lh.getGioHen());
            ps.setString(7, lh.getTrangThai());
            ps.setInt(8, lh.getThoiLuongDuKien());
            ps.setString(9, lh.getPhong());
            ps.setString(10, lh.getHinhThucDat());
            ps.setBoolean(11, lh.isDaNhacHen());
            ps.setString(12, lh.getGhiChu());
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
