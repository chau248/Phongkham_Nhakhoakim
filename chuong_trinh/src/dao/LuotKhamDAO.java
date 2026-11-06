package dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import entity.HoaDon;
import entity.LichHen;
import entity.LieuTrinh;
import entity.LuotKham;
import entity.LuotKhamDichVu;

// Truy cập bảng LuotKham và bảng LuotKhamDichVu
public class LuotKhamDAO {

    static final String SELECT = "SELECT k.maLuotKham, k.maLieuTrinh, k.maHoaDon, k.maLichHen, k.gioBatDau, k.gioKetThuc, k.lyDoKham,"
            + " k.chanDoan, k.huongDieuTri, k.donThuoc, k.ngayTaiKham, k.trangThai, k.ghiChu, "
            + HoSoBenhAnDAO.COT_NOI + ", " + BenhNhanDAO.COT_NOI + ", " + NhanVienDAO.COT_NOI
            + " FROM dbo.LuotKham k JOIN dbo.HoSoBenhAn h ON h.maHoSo = k.maHoSo JOIN dbo.BenhNhan bn ON bn.maBN = h.maBN"
            + " JOIN dbo.NhanVien nv ON nv.maNV = k.maNhanVienPhuTrach";
    static final String SELECT_DICH_VU = "SELECT d.maDong, d.maLuotKham, d.soLuong, d.ghiChu, " + DichVuDAO.COT_NOI
            + " FROM dbo.LuotKhamDichVu d JOIN dbo.DichVu dv ON dv.maDichVu = d.maDichVu";

    // Tìm lượt khám theo mã
    public LuotKham timTheoMa(String maLuotKham) {
        return TienIchDAO.layMot(SELECT + " WHERE k.maLuotKham = ?", this::doc, maLuotKham);
    }

    // Các lượt khám của một hồ sơ
    public List<LuotKham> layTheoHoSo(String maHoSo) {
        return TienIchDAO.layDanhSach(SELECT + " WHERE k.maHoSo = ? ORDER BY k.gioBatDau DESC", this::doc, maHoSo);
    }

    // Các lượt khám thuộc một liệu trình
    public List<LuotKham> layTheoLieuTrinh(String maLieuTrinh) {
        return TienIchDAO.layDanhSach(SELECT + " WHERE k.maLieuTrinh = ? AND k.trangThai <> N'Đã hủy' ORDER BY k.gioBatDau", this::doc, maLieuTrinh);
    }

    // Các lượt khám của một bệnh nhân
    public List<LuotKham> layTheoBenhNhan(String maBN) {
        return TienIchDAO.layDanhSach(SELECT + " WHERE h.maBN = ? AND k.trangThai <> N'Đã hủy' ORDER BY k.gioBatDau DESC", this::doc, maBN);
    }

    // Lượt đang khám của một nha sĩ
    public LuotKham timDangKham(String maNhanVien) {
        return TienIchDAO.layMot(SELECT + " WHERE k.maNhanVienPhuTrach = ? AND k.trangThai = N'Đang khám' ORDER BY k.gioBatDau DESC",
                this::doc, maNhanVien);
    }

    // Lượt đang khám của một hồ sơ
    public LuotKham timDangKhamTheoHoSo(String maHoSo) {
        return TienIchDAO.layMot(SELECT + " WHERE k.maHoSo = ? AND k.trangThai = N'Đang khám'", this::doc, maHoSo);
    }

    // Lượt đã hoàn thành nhưng chưa có hóa đơn
    public List<LuotKham> layChoThanhToan() {
        return TienIchDAO.layDanhSach(SELECT + " WHERE k.trangThai = N'Hoàn thành' AND k.maHoaDon IS NULL ORDER BY k.gioKetThuc", this::doc);
    }

    // Thêm lượt khám mới
    public void them(LuotKham lk) {
        TienIchDAO.thucThi("INSERT INTO dbo.LuotKham (maLuotKham, maHoSo, maNhanVienPhuTrach, maLieuTrinh, maHoaDon, maLichHen, gioBatDau,"
                + " gioKetThuc, lyDoKham, chanDoan, huongDieuTri, donThuoc, ngayTaiKham, trangThai, ghiChu)"
                + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                lk.getMaLuotKham(), lk.getMaHoSo().getMaHoSo(), lk.getMaNhanVienPhuTrach().getMaNV(), ma(lk.getMaLieuTrinh()),
                ma(lk.getMaHoaDon()), ma(lk.getMaLichHen()), lk.getGioBatDau(), lk.getGioKetThuc(), lk.getLyDoKham(),
                lk.getChanDoan(), lk.getHuongDieuTri(), lk.getDonThuoc(), lk.getNgayTaiKham(), lk.getTrangThai(), lk.getGhiChu());
    }

    // Cập nhật nội dung khám
    public void capNhat(LuotKham lk) {
        TienIchDAO.thucThi("UPDATE dbo.LuotKham SET gioKetThuc = ?, lyDoKham = ?, chanDoan = ?, huongDieuTri = ?, donThuoc = ?,"
                + " ngayTaiKham = ?, trangThai = ?, ghiChu = ?, maLieuTrinh = ? WHERE maLuotKham = ?",
                lk.getGioKetThuc(), lk.getLyDoKham(), lk.getChanDoan(), lk.getHuongDieuTri(), lk.getDonThuoc(), lk.getNgayTaiKham(),
                lk.getTrangThai(), lk.getGhiChu(), ma(lk.getMaLieuTrinh()), lk.getMaLuotKham());
    }

    // Gắn lượt khám vào liệu trình
    public void ganLieuTrinh(String maLuotKham, String maLieuTrinh) {
        TienIchDAO.thucThi("UPDATE dbo.LuotKham SET maLieuTrinh = ? WHERE maLuotKham = ?", maLieuTrinh, maLuotKham);
    }

    // Gắn hóa đơn vào lượt khám chưa có hóa đơn
    public boolean ganHoaDon(String maLuotKham, String maHoaDon) {
        return TienIchDAO.thucThi("UPDATE dbo.LuotKham SET maHoaDon = ? WHERE maLuotKham = ? AND maHoaDon IS NULL", maHoaDon, maLuotKham) > 0;
    }

    // Các dịch vụ không gắn răng của lượt khám
    public List<LuotKhamDichVu> layDichVu(String maLuotKham) {
        return TienIchDAO.layDanhSach(SELECT_DICH_VU + " WHERE d.maLuotKham = ? ORDER BY d.maDong", rs -> {
            LuotKhamDichVu d = new LuotKhamDichVu();
            d.setMaDong(rs.getInt("maDong"));
            LuotKham lk = new LuotKham();
            lk.setMaLuotKham(rs.getString("maLuotKham"));
            d.setMaLuotKham(lk);
            d.setMaDichVu(DichVuDAO.docNoi(rs));
            d.setSoLuong(rs.getInt("soLuong"));
            d.setGhiChu(rs.getString("ghiChu"));
            return d;
        }, maLuotKham);
    }

    // Thêm một dịch vụ vào lượt khám
    public void themDichVu(LuotKhamDichVu d) {
        TienIchDAO.thucThi("INSERT INTO dbo.LuotKhamDichVu (maLuotKham, maDichVu, soLuong, ghiChu) VALUES (?, ?, ?, ?)",
                d.getMaLuotKham().getMaLuotKham(), d.getMaDichVu().getMaDichVu(), d.getSoLuong(), d.getGhiChu());
    }

    // Xóa một dòng dịch vụ
    public boolean xoaDichVu(String maLuotKham, int maDong) {
        return TienIchDAO.thucThi("DELETE FROM dbo.LuotKhamDichVu WHERE maDong = ? AND maLuotKham = ?", maDong, maLuotKham) > 0;
    }

    private LuotKham doc(ResultSet rs) throws SQLException {
        LuotKham lk = new LuotKham();
        lk.setMaLuotKham(rs.getString("maLuotKham"));
        lk.setMaHoSo(HoSoBenhAnDAO.docNoi(rs));
        lk.setMaNhanVienPhuTrach(NhanVienDAO.docNoi(rs));
        lk.setMaLieuTrinh(lieuTrinh(rs.getString("maLieuTrinh")));
        lk.setMaHoaDon(hoaDon(rs.getString("maHoaDon")));
        lk.setMaLichHen(lichHen(rs.getString("maLichHen")));
        lk.setGioBatDau(rs.getObject("gioBatDau", LocalDateTime.class));
        lk.setGioKetThuc(rs.getObject("gioKetThuc", LocalDateTime.class));
        lk.setLyDoKham(rs.getString("lyDoKham"));
        lk.setChanDoan(rs.getString("chanDoan"));
        lk.setHuongDieuTri(rs.getString("huongDieuTri"));
        lk.setDonThuoc(rs.getString("donThuoc"));
        lk.setNgayTaiKham(rs.getObject("ngayTaiKham", LocalDate.class));
        lk.setTrangThai(rs.getString("trangThai"));
        lk.setGhiChu(rs.getString("ghiChu"));
        return lk;
    }

    // Mã của liệu trình
    private static String ma(LieuTrinh lt) {
        return lt == null ? null : lt.getMaLieuTrinh();
    }

    private static String ma(HoaDon hd) {
        return hd == null ? null : hd.getMaHoaDon();
    }

    private static String ma(LichHen lh) {
        return lh == null ? null : lh.getMaLichHen();
    }

    // Đối tượng chỉ có mã
    private static LieuTrinh lieuTrinh(String ma) {
        if (ma == null) {
            return null;
        }
        LieuTrinh lt = new LieuTrinh();
        lt.setMaLieuTrinh(ma);
        return lt;
    }

    private static HoaDon hoaDon(String ma) {
        if (ma == null) {
            return null;
        }
        HoaDon hd = new HoaDon();
        hd.setMaHoaDon(ma);
        return hd;
    }

    private static LichHen lichHen(String ma) {
        if (ma == null) {
            return null;
        }
        LichHen lh = new LichHen();
        lh.setMaLichHen(ma);
        return lh;
    }
}
