package nhakhoakim.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import nhakhoakim.entity.RangDieuTri;

// Truy cập bảng RangDieuTri
public class RangDieuTriDAO {

    private static final String CAC_COT = "r.maBanGhi, r.maLuotKham, r.soFDI, r.matRang, r.trangThai, r.vatLieu, r.ngayGhiNhan,"
            + " r.maDichVu, r.daNho, r.mauHienThi, r.ghiChu";

    // Thêm bản ghi răng mới
    public void them(RangDieuTri r) {
        String sql = "INSERT INTO dbo.RangDieuTri (maBanGhi, maLuotKham, soFDI, matRang, trangThai, vatLieu, ngayGhiNhan, maDichVu, daNho, mauHienThi, ghiChu)"
                + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, r.getMaBanGhi());
            ps.setString(2, r.getMaLuotKham());
            ps.setInt(3, r.getSoFDI());
            ps.setString(4, r.getMatRang());
            ps.setString(5, r.getTrangThai());
            ps.setString(6, r.getVatLieu());
            ps.setObject(7, r.getNgayGhiNhan());
            ps.setString(8, r.getMaDichVu());
            ps.setBoolean(9, r.isDaNho());
            ps.setString(10, r.getMauHienThi());
            ps.setString(11, r.getGhiChu());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new LoiCSDL("Không thêm được bản ghi răng: " + e.getMessage(), e);
        }
    }

    // Cập nhật bản ghi răng đã có; thời điểm ghi nhận được đặt lại là bây giờ để bản vừa sửa là bản mới nhất của răng
    public void capNhat(RangDieuTri r) {
        String sql = "UPDATE dbo.RangDieuTri SET matRang = ?, trangThai = ?, vatLieu = ?, maDichVu = ?, daNho = ?, mauHienThi = ?, ghiChu = ?,"
                + " ngayGhiNhan = ? WHERE maBanGhi = ?";
        LocalDateTime bay = LocalDateTime.now();
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, r.getMatRang());
            ps.setString(2, r.getTrangThai());
            ps.setString(3, r.getVatLieu());
            ps.setString(4, r.getMaDichVu());
            ps.setBoolean(5, r.isDaNho());
            ps.setString(6, r.getMauHienThi());
            ps.setString(7, r.getGhiChu());
            ps.setObject(8, bay);
            ps.setString(9, r.getMaBanGhi());
            ps.executeUpdate();
            r.setNgayGhiNhan(bay);
        } catch (SQLException e) {
            throw new LoiCSDL("Không cập nhật được bản ghi răng: " + e.getMessage(), e);
        }
    }

    // Xóa một bản ghi răng của lượt khám (ràng cả mã lượt khám để không xóa nhầm lượt khác); trả về true nếu xóa được
    public boolean xoa(String maLuotKham, String maBanGhi) {
        try (Connection c = DatabaseConnection.getConnection();
                PreparedStatement ps = c.prepareStatement("DELETE FROM dbo.RangDieuTri WHERE maBanGhi = ? AND maLuotKham = ?")) {
            ps.setString(1, maBanGhi);
            ps.setString(2, maLuotKham);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new LoiCSDL("Không xóa được bản ghi răng: " + e.getMessage(), e);
        }
    }

    // Các bản ghi răng của một lượt khám, theo số FDI; cùng một răng thì bản mới nhất trước
    public List<RangDieuTri> layTheoLuotKham(String maLuotKham) {
        return layDanhSach("SELECT " + CAC_COT + " FROM dbo.RangDieuTri r WHERE r.maLuotKham = ?"
                + " ORDER BY r.soFDI, r.ngayGhiNhan DESC, r.maBanGhi DESC", maLuotKham);
    }

    // Tất cả bản ghi răng của một hồ sơ qua mọi lượt khám, mới nhất trước (để biết tình trạng hiện tại của từng răng);
    // cùng thời điểm thì mã bản ghi lớn hơn (ghi sau) đứng trước
    public List<RangDieuTri> layTheoHoSo(String maHoSo) {
        return layDanhSach("SELECT " + CAC_COT + " FROM dbo.RangDieuTri r JOIN dbo.LuotKham k ON k.maLuotKham = r.maLuotKham"
                + " WHERE k.maHoSo = ? ORDER BY r.ngayGhiNhan DESC, r.maBanGhi DESC", maHoSo);
    }

    // Chạy câu SELECT có một tham số chuỗi và đọc tất cả dòng
    private List<RangDieuTri> layDanhSach(String sql, String thamSo) {
        List<RangDieuTri> ds = new ArrayList<>();
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, thamSo);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    RangDieuTri r = new RangDieuTri();
                    r.setMaBanGhi(rs.getString("maBanGhi"));
                    r.setMaLuotKham(rs.getString("maLuotKham"));
                    r.setSoFDI(rs.getInt("soFDI"));
                    r.setMatRang(rs.getString("matRang"));
                    r.setTrangThai(rs.getString("trangThai"));
                    r.setVatLieu(rs.getString("vatLieu"));
                    r.setNgayGhiNhan(rs.getObject("ngayGhiNhan", LocalDateTime.class));
                    r.setMaDichVu(rs.getString("maDichVu"));
                    r.setDaNho(rs.getBoolean("daNho"));
                    r.setMauHienThi(rs.getString("mauHienThi"));
                    r.setGhiChu(rs.getString("ghiChu"));
                    ds.add(r);
                }
            }
        } catch (SQLException e) {
            throw new LoiCSDL("Không đọc được sơ đồ răng: " + e.getMessage(), e);
        }
        return ds;
    }
}
