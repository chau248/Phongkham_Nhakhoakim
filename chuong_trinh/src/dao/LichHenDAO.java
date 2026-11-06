package dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import entity.LichHen;

// Truy cập bảng LichHen
public class LichHenDAO {

    static final String SELECT = "SELECT l.maLichHen, l.ngayHen, l.gioHen, l.trangThai, l.thoiLuongDuKien, l.phong, l.hinhThucDat,"
            + " l.lyDoHuy, l.daNhacHen, " + BenhNhanDAO.COT_NOI + ", " + NhanVienDAO.COT_NOI + ", " + DichVuDAO.COT_NOI
            + " FROM dbo.LichHen l JOIN dbo.BenhNhan bn ON bn.maBN = l.maBN JOIN dbo.NhanVien nv ON nv.maNV = l.maNhanVien"
            + " JOIN dbo.DichVu dv ON dv.maDichVu = l.maDichVu";
    static final String CON_HIEU_LUC = "l.trangThai IN (N'Chờ xác nhận', N'Đã xác nhận')";

    // ----- Tìm -----

    // Tìm lịch hẹn theo mã, không có thì null
    public LichHen timTheoMa(String maLichHen) {
        return TienIchDAO.layMot(SELECT + " WHERE l.maLichHen = ?", this::doc, maLichHen);
    }

    // Lịch hẹn của mọi nha sĩ trong khoảng ngày (gồm cả hai đầu)
    public List<LichHen> layTheoKhoang(LocalDate tu, LocalDate den) {
        return TienIchDAO.layDanhSach(SELECT + " WHERE l.ngayHen BETWEEN ? AND ? ORDER BY l.ngayHen, l.gioHen, l.maLichHen", this::doc, tu, den);
    }

    // Lịch hẹn của một nha sĩ trong khoảng ngày
    public List<LichHen> layTheoNhanVien(String maNV, LocalDate tu, LocalDate den) {
        return TienIchDAO.layDanhSach(SELECT + " WHERE l.maNhanVien = ? AND l.ngayHen BETWEEN ? AND ? ORDER BY l.ngayHen, l.gioHen, l.maLichHen",
                this::doc, maNV, tu, den);
    }

    // Lịch còn hiệu lực (chờ xác nhận hoặc đã xác nhận) của một nha sĩ trong một ngày
    public List<LichHen> layConHieuLucTheoNhaSiVaNgay(String maNV, LocalDate ngay) {
        return TienIchDAO.layDanhSach(SELECT + " WHERE l.maNhanVien = ? AND l.ngayHen = ? AND " + CON_HIEU_LUC + " ORDER BY l.gioHen",
                this::doc, maNV, ngay);
    }

    // Lịch còn hiệu lực của một bệnh nhân trong một ngày
    public List<LichHen> layConHieuLucTheoBenhNhanVaNgay(String maBN, LocalDate ngay) {
        return TienIchDAO.layDanhSach(SELECT + " WHERE l.maBN = ? AND l.ngayHen = ? AND " + CON_HIEU_LUC + " ORDER BY l.gioHen",
                this::doc, maBN, ngay);
    }

    // Lịch đã xác nhận của một bệnh nhân trong một ngày
    public List<LichHen> layDaXacNhanTheoBenhNhan(String maBN, LocalDate ngay) {
        return TienIchDAO.layDanhSach(SELECT + " WHERE l.maBN = ? AND l.ngayHen = ? AND l.trangThai = N'Đã xác nhận' ORDER BY l.gioHen",
                this::doc, maBN, ngay);
    }

    // Lịch hẹn này đang có lượt khám đang diễn ra không
    public boolean coLuotKhamDangKham(String maLichHen) {
        Integer so = TienIchDAO.layMot("SELECT COUNT(*) FROM dbo.LuotKham WHERE maLichHen = ? AND trangThai = N'Đang khám'",
                rs -> rs.getInt(1), maLichHen);
        return so != null && so > 0;
    }

    // ----- Thêm, sửa -----

    // Thêm lịch hẹn mới
    public void them(LichHen lh) {
        TienIchDAO.thucThi("INSERT INTO dbo.LichHen (maLichHen, maBN, maNhanVien, maDichVu, ngayHen, gioHen, trangThai, thoiLuongDuKien,"
                + " phong, hinhThucDat, lyDoHuy, daNhacHen) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                lh.getMaLichHen(), lh.getMaBN().getMaBN(), lh.getMaNhanVien().getMaNV(), lh.getMaDichVu().getMaDichVu(), lh.getNgayHen(),
                lh.getGioHen(), lh.getTrangThai(), lh.getThoiLuongDuKien(), lh.getPhong(), lh.getHinhThucDat(), lh.getLyDoHuy(),
                lh.isDaNhacHen());
    }

    // Đổi trạng thái (kèm lý do hủy nếu có)
    public void doiTrangThai(String maLichHen, String trangThai, String lyDoHuy) {
        TienIchDAO.thucThi("UPDATE dbo.LichHen SET trangThai = ?, lyDoHuy = ? WHERE maLichHen = ?", trangThai, lyDoHuy, maLichHen);
    }

    // Dời lịch sang ngày giờ khác
    public void doiNgayGio(String maLichHen, LocalDate ngay, LocalTime gio, String phong, int thoiLuongPhut) {
        TienIchDAO.thucThi("UPDATE dbo.LichHen SET ngayHen = ?, gioHen = ?, phong = ?, thoiLuongDuKien = ? WHERE maLichHen = ?",
                ngay, gio, phong, thoiLuongPhut, maLichHen);
    }

    // ----- Đọc dòng kết quả -----

    private LichHen doc(ResultSet rs) throws SQLException {
        LichHen lh = new LichHen();
        lh.setMaLichHen(rs.getString("maLichHen"));
        lh.setMaBN(BenhNhanDAO.docNoi(rs));
        lh.setMaNhanVien(NhanVienDAO.docNoi(rs));
        lh.setMaDichVu(DichVuDAO.docNoi(rs));
        lh.setNgayHen(rs.getObject("ngayHen", LocalDate.class));
        lh.setGioHen(rs.getObject("gioHen", LocalTime.class));
        lh.setTrangThai(rs.getString("trangThai"));
        lh.setThoiLuongDuKien(rs.getInt("thoiLuongDuKien"));
        lh.setPhong(rs.getString("phong"));
        lh.setHinhThucDat(rs.getString("hinhThucDat"));
        lh.setLyDoHuy(rs.getString("lyDoHuy"));
        lh.setDaNhacHen(rs.getBoolean("daNhacHen"));
        return lh;
    }
}
