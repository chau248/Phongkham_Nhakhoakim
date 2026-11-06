package dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import entity.CaTruc;

// Truy cập bảng CaTruc
public class CaTrucDAO {

    static final String SELECT = "SELECT c.maCaTruc, c.ngay, c.caLam, c.phong, c.trangThai, " + NhanVienDAO.COT_NOI
            + " FROM dbo.CaTruc c JOIN dbo.NhanVien nv ON nv.maNV = c.maNV";

    // Các ca trực của mọi nhân viên trong khoảng ngày
    public List<CaTruc> layTheoKhoang(LocalDate tu, LocalDate den) {
        return TienIchDAO.layDanhSach(SELECT + " WHERE c.ngay BETWEEN ? AND ? ORDER BY c.ngay, c.caLam, c.phong, c.maNV", this::doc, tu, den);
    }

    // Các ca trực của một nhân viên trong khoảng ngày
    public List<CaTruc> layTheoNhanVien(String maNV, LocalDate tu, LocalDate den) {
        return TienIchDAO.layDanhSach(SELECT + " WHERE c.maNV = ? AND c.ngay BETWEEN ? AND ? ORDER BY c.ngay, c.caLam", this::doc, maNV, tu, den);
    }

    // Tất cả ca trực của một ngày và một ca
    public List<CaTruc> layTheoNgayVaCa(LocalDate ngay, String caLam) {
        return TienIchDAO.layDanhSach(SELECT + " WHERE c.ngay = ? AND c.caLam = ? ORDER BY c.maNV", this::doc, ngay, caLam);
    }

    // Tìm ca trực theo mã
    public CaTruc timTheoMa(String maCaTruc) {
        return TienIchDAO.layMot(SELECT + " WHERE c.maCaTruc = ?", this::doc, maCaTruc);
    }

    // Thêm một ca trực
    public void them(CaTruc ca) {
        TienIchDAO.thucThi("INSERT INTO dbo.CaTruc (maCaTruc, maNV, ngay, caLam, phong, trangThai) VALUES (?, ?, ?, ?, ?, ?)",
                ca.getMaCaTruc(), ca.getMaNV().getMaNV(), ca.getNgay(), ca.getCaLam(), ca.getPhong(), ca.getTrangThai());
    }

    // Đổi trạng thái ca trực
    public void doiTrangThai(String maCaTruc, String trangThai) {
        TienIchDAO.thucThi("UPDATE dbo.CaTruc SET trangThai = ? WHERE maCaTruc = ?", trangThai, maCaTruc);
    }

    // Xóa một ca trực
    public void xoa(String maCaTruc) {
        TienIchDAO.thucThi("DELETE FROM dbo.CaTruc WHERE maCaTruc = ?", maCaTruc);
    }

    private CaTruc doc(ResultSet rs) throws SQLException {
        CaTruc ca = new CaTruc();
        ca.setMaCaTruc(rs.getString("maCaTruc"));
        ca.setMaNV(NhanVienDAO.docNoi(rs));
        ca.setNgay(rs.getObject("ngay", LocalDate.class));
        ca.setCaLam(rs.getString("caLam"));
        ca.setPhong(rs.getString("phong"));
        ca.setTrangThai(rs.getString("trangThai"));
        return ca;
    }
}
