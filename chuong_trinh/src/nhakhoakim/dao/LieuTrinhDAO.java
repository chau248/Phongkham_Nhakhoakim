package nhakhoakim.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import nhakhoakim.entity.LieuTrinh;
import nhakhoakim.entity.TrangThai;

// Truy cập bảng LieuTrinh (hiện chỉ cần đọc để hiện trong hồ sơ bệnh án)
public class LieuTrinhDAO {

    // Các liệu trình của một bệnh nhân: đang thực hiện xếp trước (BN-10), trong cùng nhóm thì mới bắt đầu trước
    public List<LieuTrinh> layTheoBenhNhan(String maBN) {
        List<LieuTrinh> ds = new ArrayList<>();
        String sql = "SELECT maLieuTrinh, maBN, loaiDichVu, soBuoiDuKien, giaiDoanHienTai, ngayBatDau, ngayDuKienKetThuc,"
                + " ngayKetThucThucTe, tongChiPhiDuKien, trangThai, maNhaSiPhuTrach, ghiChu"
                + " FROM dbo.LieuTrinh WHERE maBN = ?"
                + " ORDER BY CASE WHEN trangThai = N'" + TrangThai.LIEU_TRINH_DANG_THUC_HIEN + "' THEN 0 ELSE 1 END, ngayBatDau DESC";
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, maBN);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    LieuTrinh lt = new LieuTrinh();
                    lt.setMaLieuTrinh(rs.getString("maLieuTrinh"));
                    lt.setMaBN(rs.getString("maBN"));
                    lt.setLoaiDichVu(rs.getString("loaiDichVu"));
                    lt.setSoBuoiDuKien(rs.getInt("soBuoiDuKien"));
                    lt.setGiaiDoanHienTai(rs.getString("giaiDoanHienTai"));
                    lt.setNgayBatDau(rs.getObject("ngayBatDau", LocalDate.class));
                    lt.setNgayDuKienKetThuc(rs.getObject("ngayDuKienKetThuc", LocalDate.class));
                    lt.setNgayKetThucThucTe(rs.getObject("ngayKetThucThucTe", LocalDate.class));
                    lt.setTongChiPhiDuKien(rs.getLong("tongChiPhiDuKien"));
                    lt.setTrangThai(rs.getString("trangThai"));
                    lt.setMaNhaSiPhuTrach(rs.getString("maNhaSiPhuTrach"));
                    lt.setGhiChu(rs.getString("ghiChu"));
                    ds.add(lt);
                }
            }
        } catch (SQLException e) {
            throw new LoiCSDL("Không đọc được liệu trình: " + e.getMessage(), e);
        }
        return ds;
    }
}
