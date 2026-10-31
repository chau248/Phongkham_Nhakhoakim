package nhakhoakim.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import nhakhoakim.entity.LichHen;

// Truy cập bảng LichHen (chỉ lớp DAO được viết SQL). Quy tắc đặt, dời, hủy nằm ở LichHenController.
public class LichHenDAO {

    static final String CAC_COT = "maLichHen, maBN, maNhanVien, maDichVu, ngayHen, gioHen, trangThai, thoiLuongDuKien, phong, hinhThucDat,"
            + " lyDoHuy, daNhacHen";
    static final String CON_HIEU_LUC = "trangThai IN (N'Chờ xác nhận', N'Đã xác nhận')";
    static final String SQL_THEM = "INSERT INTO dbo.LichHen (" + CAC_COT + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    static final String SQL_THEO_MA = "SELECT " + CAC_COT + " FROM dbo.LichHen WHERE maLichHen = ?";
    static final String SQL_THEO_KHOANG = "SELECT " + CAC_COT + " FROM dbo.LichHen WHERE ngayHen BETWEEN ? AND ? ORDER BY ngayHen, gioHen, maLichHen";
    static final String SQL_THEO_NHAN_VIEN = "SELECT " + CAC_COT + " FROM dbo.LichHen WHERE maNhanVien = ? AND ngayHen BETWEEN ? AND ?"
            + " ORDER BY ngayHen, gioHen, maLichHen";
    static final String SQL_HIEU_LUC_NHA_SI_NGAY = "SELECT " + CAC_COT + " FROM dbo.LichHen WHERE maNhanVien = ? AND ngayHen = ? AND " + CON_HIEU_LUC
            + " ORDER BY gioHen";
    static final String SQL_HIEU_LUC_BENH_NHAN_NGAY = "SELECT " + CAC_COT + " FROM dbo.LichHen WHERE maBN = ? AND ngayHen = ? AND " + CON_HIEU_LUC
            + " ORDER BY gioHen";
    static final String SQL_DA_XAC_NHAN_BENH_NHAN_NGAY = "SELECT " + CAC_COT + " FROM dbo.LichHen WHERE maBN = ? AND ngayHen = ?"
            + " AND trangThai = N'Đã xác nhận' ORDER BY gioHen";
    static final String SQL_DOI_TRANG_THAI = "UPDATE dbo.LichHen SET trangThai = ?, lyDoHuy = ? WHERE maLichHen = ?";
    static final String SQL_DOI_NGAY_GIO = "UPDATE dbo.LichHen SET ngayHen = ?, gioHen = ?, phong = ?, thoiLuongDuKien = ? WHERE maLichHen = ?";
    static final String SQL_DEM_DANG_KHAM = "SELECT COUNT(*) FROM dbo.LuotKham WHERE maLichHen = ? AND trangThai = N'Đang khám'";

    // Thêm một lịch hẹn mới
    public void them(LichHen lh) {
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement ps = c.prepareStatement(SQL_THEM)) {
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
            ps.setString(11, lh.getLyDoHuy());
            ps.setBoolean(12, lh.isDaNhacHen());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new LoiCSDL("Không thêm được lịch hẹn: " + e.getMessage(), e);
        }
    }

    // Tìm lịch hẹn theo mã; không có thì trả về null
    public LichHen timTheoMa(String maLichHen) {
        List<LichHen> ds = layDanhSach(SQL_THEO_MA, maLichHen);
        return ds.isEmpty() ? null : ds.get(0);
    }

    // Lịch hẹn của mọi nha sĩ trong khoảng ngày (gồm cả hai đầu), theo ngày giờ
    public List<LichHen> layTheoKhoang(LocalDate tu, LocalDate den) {
        return layDanhSach(SQL_THEO_KHOANG, tu, den);
    }

    // Lịch hẹn của một nha sĩ trong khoảng ngày (gồm cả hai đầu), theo ngày giờ
    public List<LichHen> layTheoNhanVien(String maNV, LocalDate tu, LocalDate den) {
        return layDanhSach(SQL_THEO_NHAN_VIEN, maNV, tu, den);
    }

    // Lịch còn hiệu lực (Chờ xác nhận hoặc Đã xác nhận) của một nha sĩ trong một ngày, dùng để kiểm tra trùng giờ
    public List<LichHen> layConHieuLucTheoNhaSiVaNgay(String maNV, LocalDate ngay) {
        return layDanhSach(SQL_HIEU_LUC_NHA_SI_NGAY, maNV, ngay);
    }

    // Lịch còn hiệu lực của một bệnh nhân trong một ngày, dùng để kiểm tra bệnh nhân không có hai lịch chồng nhau
    public List<LichHen> layConHieuLucTheoBenhNhanVaNgay(String maBN, LocalDate ngay) {
        return layDanhSach(SQL_HIEU_LUC_BENH_NHAN_NGAY, maBN, ngay);
    }

    // Lịch Đã xác nhận của một bệnh nhân trong một ngày (khám vãng lai gắn lịch này vào lượt khám)
    public List<LichHen> layDaXacNhanTheoBenhNhan(String maBN, LocalDate ngay) {
        return layDanhSach(SQL_DA_XAC_NHAN_BENH_NHAN_NGAY, maBN, ngay);
    }

    // Đổi trạng thái lịch hẹn; lyDoHuy chỉ có khi hủy (các trạng thái khác truyền null)
    public void doiTrangThai(String maLichHen, String trangThai, String lyDoHuy) {
        chay(SQL_DOI_TRANG_THAI, trangThai, lyDoHuy, maLichHen);
    }

    // Dời lịch sang ngày giờ khác (kèm phòng và thời lượng tính lại)
    public void doiNgayGio(String maLichHen, LocalDate ngay, LocalTime gio, String phong, int thoiLuongPhut) {
        chay(SQL_DOI_NGAY_GIO, ngay, gio, phong, thoiLuongPhut, maLichHen);
    }

    // Lịch hẹn này đang có lượt khám Đang khám không (không được dời hoặc hủy lịch lúc đó, LH-06)
    public boolean coLuotKhamDangKham(String maLichHen) {
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement ps = c.prepareStatement(SQL_DEM_DANG_KHAM)) {
            ps.setString(1, maLichHen);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new LoiCSDL("Không kiểm tra được lượt khám của lịch hẹn: " + e.getMessage(), e);
        }
    }

    // Chạy một câu UPDATE với các tham số bất kỳ
    private void chay(String sql, Object... thamSo) {
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            for (int i = 0; i < thamSo.length; i++) {
                ps.setObject(i + 1, thamSo[i]);
            }
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new LoiCSDL("Không cập nhật được lịch hẹn: " + e.getMessage(), e);
        }
    }

    // Chạy câu SELECT với các tham số bất kỳ và đọc tất cả dòng
    private List<LichHen> layDanhSach(String sql, Object... thamSo) {
        List<LichHen> ds = new ArrayList<>();
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            for (int i = 0; i < thamSo.length; i++) {
                ps.setObject(i + 1, thamSo[i]);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    LichHen lh = new LichHen();
                    lh.setMaLichHen(rs.getString("maLichHen"));
                    lh.setMaBN(rs.getString("maBN"));
                    lh.setMaNhanVien(rs.getString("maNhanVien"));
                    lh.setMaDichVu(rs.getString("maDichVu"));
                    lh.setNgayHen(rs.getObject("ngayHen", LocalDate.class));
                    lh.setGioHen(rs.getObject("gioHen", LocalTime.class));
                    lh.setTrangThai(rs.getString("trangThai"));
                    lh.setThoiLuongDuKien(rs.getInt("thoiLuongDuKien"));
                    lh.setPhong(rs.getString("phong"));
                    lh.setHinhThucDat(rs.getString("hinhThucDat"));
                    lh.setLyDoHuy(rs.getString("lyDoHuy"));
                    lh.setDaNhacHen(rs.getBoolean("daNhacHen"));
                    ds.add(lh);
                }
            }
        } catch (SQLException e) {
            throw new LoiCSDL("Không đọc được lịch hẹn: " + e.getMessage(), e);
        }
        return ds;
    }
}
