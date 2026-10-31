package nhakhoakim.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import nhakhoakim.entity.CaTruc;
import nhakhoakim.entity.NhanVien;

// Truy cập bảng CaTruc (chỉ lớp DAO được viết SQL). Mỗi ca đọc kèm họ tên và vai trò nhân viên để hiện lên bảng phân công.
public class CaTrucDAO {

    static final String CAC_COT = "c.maCaTruc, c.maNV, n.hoTen, n.vaiTro, c.ngay, c.caLam, c.phong, c.trangThai";
    static final String TU_BANG = " FROM dbo.CaTruc c JOIN dbo.NhanVien n ON n.maNV = c.maNV";
    static final String SQL_THEO_KHOANG = "SELECT " + CAC_COT + TU_BANG + " WHERE c.ngay BETWEEN ? AND ? ORDER BY c.ngay, c.caLam, c.phong, c.maNV";
    static final String SQL_THEO_NHAN_VIEN = "SELECT " + CAC_COT + TU_BANG + " WHERE c.maNV = ? AND c.ngay BETWEEN ? AND ? ORDER BY c.ngay, c.caLam";
    static final String SQL_THEO_MA = "SELECT " + CAC_COT + TU_BANG + " WHERE c.maCaTruc = ?";
    static final String SQL_THEO_NGAY_CA = "SELECT " + CAC_COT + TU_BANG + " WHERE c.ngay = ? AND c.caLam = ? ORDER BY c.maNV";
    static final String SQL_THEM = "INSERT INTO dbo.CaTruc (maCaTruc, maNV, ngay, caLam, phong, trangThai) VALUES (?, ?, ?, ?, ?, ?)";
    static final String SQL_DOI_TRANG_THAI = "UPDATE dbo.CaTruc SET trangThai = ? WHERE maCaTruc = ?";
    static final String SQL_XOA = "DELETE FROM dbo.CaTruc WHERE maCaTruc = ?";

    // Các ca trực trong khoảng ngày (gồm cả hai đầu) của mọi nhân viên
    public List<CaTruc> layTheoKhoang(LocalDate tu, LocalDate den) {
        return layDanhSach(SQL_THEO_KHOANG, tu, den);
    }

    // Các ca trực của một nhân viên trong khoảng ngày (gồm cả hai đầu)
    public List<CaTruc> layTheoNhanVien(String maNV, LocalDate tu, LocalDate den) {
        return layDanhSach(SQL_THEO_NHAN_VIEN, maNV, tu, den);
    }

    // Tìm ca trực theo mã; không có thì trả về null
    public CaTruc timTheoMa(String maCaTruc) {
        List<CaTruc> ds = layDanhSach(SQL_THEO_MA, maCaTruc);
        return ds.isEmpty() ? null : ds.get(0);
    }

    // Tất cả ca trực của một ngày và một ca (Sáng hoặc Chiều)
    public List<CaTruc> layTheoNgayVaCa(LocalDate ngay, String caLam) {
        return layDanhSach(SQL_THEO_NGAY_CA, ngay, caLam);
    }

    // Thêm một ca trực mới
    public void them(CaTruc ca) {
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement ps = c.prepareStatement(SQL_THEM)) {
            ps.setString(1, ca.getMaCaTruc());
            ps.setString(2, ca.getMaNV().getMaNV());
            ps.setObject(3, ca.getNgay());
            ps.setString(4, ca.getCaLam());
            ps.setString(5, ca.getPhong());
            ps.setString(6, ca.getTrangThai());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new LoiCSDL("Không thêm được ca trực: " + e.getMessage(), e);
        }
    }

    // Đổi trạng thái ca trực (Có mặt, Vắng, Điều chuyển)
    public void doiTrangThai(String maCaTruc, String trangThai) {
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement ps = c.prepareStatement(SQL_DOI_TRANG_THAI)) {
            ps.setString(1, trangThai);
            ps.setString(2, maCaTruc);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new LoiCSDL("Không đổi được trạng thái ca trực: " + e.getMessage(), e);
        }
    }

    // Xóa một ca trực (chỉ dùng khi xếp nhầm: ca chưa diễn ra và chưa có lịch hẹn, Controller đã kiểm tra)
    public void xoa(String maCaTruc) {
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement ps = c.prepareStatement(SQL_XOA)) {
            ps.setString(1, maCaTruc);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new LoiCSDL("Không xóa được ca trực: " + e.getMessage(), e);
        }
    }

    // Chạy câu SELECT với các tham số bất kỳ và đọc tất cả dòng
    private List<CaTruc> layDanhSach(String sql, Object... thamSo) {
        List<CaTruc> ds = new ArrayList<>();
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            for (int i = 0; i < thamSo.length; i++) {
                ps.setObject(i + 1, thamSo[i]);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    NhanVien nv = new NhanVien();
                    nv.setMaNV(rs.getString("maNV"));
                    nv.setHoTen(rs.getString("hoTen"));
                    nv.setVaiTro(rs.getString("vaiTro"));
                    CaTruc ca = new CaTruc();
                    ca.setMaCaTruc(rs.getString("maCaTruc"));
                    ca.setMaNV(nv);
                    ca.setNgay(rs.getObject("ngay", LocalDate.class));
                    ca.setCaLam(rs.getString("caLam"));
                    ca.setPhong(rs.getString("phong"));
                    ca.setTrangThai(rs.getString("trangThai"));
                    ds.add(ca);
                }
            }
        } catch (SQLException e) {
            throw new LoiCSDL("Không đọc được ca trực: " + e.getMessage(), e);
        }
        return ds;
    }
}
