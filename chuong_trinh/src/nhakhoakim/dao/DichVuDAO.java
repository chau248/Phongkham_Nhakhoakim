package nhakhoakim.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import nhakhoakim.entity.DichVu;

// Truy cập bảng DichVu (chỉ lớp DAO được viết SQL). Không có hàm xóa: dịch vụ chỉ chuyển sang ngừng hoạt động (CH-02).
public class DichVuDAO {

    static final String CAC_COT = "maDichVu, tenDichVu, gia, thoiGianDuKien, moTa, loaiDichVu, donViTinh, trangThaiHoatDong,"
            + " yeuCauLieuTrinh, lienQuanImplantCauRang";
    static final String SQL_TAT_CA = "SELECT " + CAC_COT + " FROM dbo.DichVu ORDER BY maDichVu";
    static final String SQL_THEO_MA = "SELECT " + CAC_COT + " FROM dbo.DichVu WHERE maDichVu = ?";
    static final String SQL_THEO_TEN = "SELECT " + CAC_COT + " FROM dbo.DichVu WHERE tenDichVu = ?";
    static final String SQL_THEM = "INSERT INTO dbo.DichVu (" + CAC_COT + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    static final String SQL_CAP_NHAT = "UPDATE dbo.DichVu SET tenDichVu = ?, gia = ?, thoiGianDuKien = ?, moTa = ?, loaiDichVu = ?,"
            + " donViTinh = ?, trangThaiHoatDong = ?, yeuCauLieuTrinh = ?, lienQuanImplantCauRang = ? WHERE maDichVu = ?";

    // Tất cả dịch vụ (kể cả đã ngừng), theo mã
    public List<DichVu> layTatCa() {
        return layDanhSach(SQL_TAT_CA, null);
    }

    // Tìm dịch vụ theo mã; không có thì trả về null
    public DichVu timTheoMa(String ma) {
        List<DichVu> ds = layDanhSach(SQL_THEO_MA, ma);
        return ds.isEmpty() ? null : ds.get(0);
    }

    // Tìm dịch vụ theo tên (so khớp theo collation của CSDL, không phân biệt hoa thường); không có thì trả về null
    public DichVu timTheoTen(String ten) {
        List<DichVu> ds = layDanhSach(SQL_THEO_TEN, ten);
        return ds.isEmpty() ? null : ds.get(0);
    }

    // Thêm dịch vụ mới
    public void them(DichVu dv) {
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement ps = c.prepareStatement(SQL_THEM)) {
            ps.setString(1, dv.getMaDichVu());
            ps.setString(2, dv.getTenDichVu());
            ps.setLong(3, dv.getGia());
            ps.setInt(4, dv.getThoiGianDuKien());
            ps.setString(5, dv.getMoTa());
            ps.setString(6, dv.getLoaiDichVu());
            ps.setString(7, dv.getDonViTinh());
            ps.setBoolean(8, dv.isTrangThaiHoatDong());
            ps.setBoolean(9, dv.isYeuCauLieuTrinh());
            ps.setBoolean(10, dv.isLienQuanImplantCauRang());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new LoiCSDL("Không thêm được dịch vụ: " + e.getMessage(), e);
        }
    }

    // Cập nhật dịch vụ (không đổi mã)
    public void capNhat(DichVu dv) {
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement ps = c.prepareStatement(SQL_CAP_NHAT)) {
            ps.setString(1, dv.getTenDichVu());
            ps.setLong(2, dv.getGia());
            ps.setInt(3, dv.getThoiGianDuKien());
            ps.setString(4, dv.getMoTa());
            ps.setString(5, dv.getLoaiDichVu());
            ps.setString(6, dv.getDonViTinh());
            ps.setBoolean(7, dv.isTrangThaiHoatDong());
            ps.setBoolean(8, dv.isYeuCauLieuTrinh());
            ps.setBoolean(9, dv.isLienQuanImplantCauRang());
            ps.setString(10, dv.getMaDichVu());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new LoiCSDL("Không cập nhật được dịch vụ: " + e.getMessage(), e);
        }
    }

    // Chạy câu SELECT (có hoặc không có một tham số chuỗi) và đọc tất cả dòng
    private List<DichVu> layDanhSach(String sql, String thamSo) {
        List<DichVu> ds = new ArrayList<>();
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            if (thamSo != null) {
                ps.setString(1, thamSo);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    DichVu dv = new DichVu();
                    dv.setMaDichVu(rs.getString("maDichVu"));
                    dv.setTenDichVu(rs.getString("tenDichVu"));
                    dv.setGia(rs.getLong("gia"));
                    dv.setThoiGianDuKien(rs.getInt("thoiGianDuKien"));
                    dv.setMoTa(rs.getString("moTa"));
                    dv.setLoaiDichVu(rs.getString("loaiDichVu"));
                    dv.setDonViTinh(rs.getString("donViTinh"));
                    dv.setTrangThaiHoatDong(rs.getBoolean("trangThaiHoatDong"));
                    dv.setYeuCauLieuTrinh(rs.getBoolean("yeuCauLieuTrinh"));
                    dv.setLienQuanImplantCauRang(rs.getBoolean("lienQuanImplantCauRang"));
                    ds.add(dv);
                }
            }
        } catch (SQLException e) {
            throw new LoiCSDL("Không đọc được danh mục dịch vụ: " + e.getMessage(), e);
        }
        return ds;
    }
}
