package dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import entity.LuotKham;
import entity.RangDieuTri;

// Truy cập bảng RangDieuTri
public class RangDieuTriDAO {

    static final String SELECT = "SELECT r.maBanGhi, r.maLuotKham, r.soFDI, r.trangThai, r.vatLieu, r.ngayGhiNhan, r.daNho, r.mauHienThi,"
            + " r.ghiChu, " + DichVuDAO.COT_NOI + " FROM dbo.RangDieuTri r LEFT JOIN dbo.DichVu dv ON dv.maDichVu = r.maDichVu";

    // Các bản ghi răng của một lượt khám
    public List<RangDieuTri> layTheoLuotKham(String maLuotKham) {
        return TienIchDAO.layDanhSach(SELECT + " WHERE r.maLuotKham = ? ORDER BY r.soFDI, r.ngayGhiNhan DESC, r.maBanGhi DESC",
                this::doc, maLuotKham);
    }

    // Tất cả bản ghi răng của một hồ sơ qua mọi lượt khám
    public List<RangDieuTri> layTheoHoSo(String maHoSo) {
        return TienIchDAO.layDanhSach(SELECT + " JOIN dbo.LuotKham k ON k.maLuotKham = r.maLuotKham WHERE k.maHoSo = ?"
                + " ORDER BY r.ngayGhiNhan DESC, r.maBanGhi DESC", this::doc, maHoSo);
    }

    // Thêm bản ghi răng mới
    public void them(RangDieuTri r) {
        TienIchDAO.thucThi("INSERT INTO dbo.RangDieuTri (maBanGhi, maLuotKham, soFDI, trangThai, vatLieu, ngayGhiNhan, maDichVu, daNho,"
                + " mauHienThi, ghiChu) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                r.getMaBanGhi(), r.getMaLuotKham().getMaLuotKham(), r.getSoFDI(), r.getTrangThai(), r.getVatLieu(), r.getNgayGhiNhan(),
                r.getMaDichVu() == null ? null : r.getMaDichVu().getMaDichVu(), r.isDaNho(), r.getMauHienThi(), r.getGhiChu());
    }

    // Cập nhật bản ghi răng
    public void capNhat(RangDieuTri r) {
        LocalDateTime bayGio = LocalDateTime.now();
        TienIchDAO.thucThi("UPDATE dbo.RangDieuTri SET trangThai = ?, vatLieu = ?, maDichVu = ?, daNho = ?, mauHienThi = ?, ghiChu = ?,"
                + " ngayGhiNhan = ? WHERE maBanGhi = ?",
                r.getTrangThai(), r.getVatLieu(), r.getMaDichVu() == null ? null : r.getMaDichVu().getMaDichVu(), r.isDaNho(),
                r.getMauHienThi(), r.getGhiChu(), bayGio, r.getMaBanGhi());
        r.setNgayGhiNhan(bayGio);
    }

    // Xóa một bản ghi răng của lượt khám
    public boolean xoa(String maLuotKham, String maBanGhi) {
        return TienIchDAO.thucThi("DELETE FROM dbo.RangDieuTri WHERE maBanGhi = ? AND maLuotKham = ?", maBanGhi, maLuotKham) > 0;
    }

    private RangDieuTri doc(ResultSet rs) throws SQLException {
        RangDieuTri r = new RangDieuTri();
        r.setMaBanGhi(rs.getString("maBanGhi"));
        LuotKham lk = new LuotKham(); // chỉ có mã lượt khám
        lk.setMaLuotKham(rs.getString("maLuotKham"));
        r.setMaLuotKham(lk);
        r.setSoFDI(rs.getInt("soFDI"));
        r.setTrangThai(rs.getString("trangThai"));
        r.setVatLieu(rs.getString("vatLieu"));
        r.setNgayGhiNhan(rs.getObject("ngayGhiNhan", LocalDateTime.class));
        r.setMaDichVu(DichVuDAO.docNoi(rs));
        r.setDaNho(rs.getBoolean("daNho"));
        r.setMauHienThi(rs.getString("mauHienThi"));
        r.setGhiChu(rs.getString("ghiChu"));
        return r;
    }
}
