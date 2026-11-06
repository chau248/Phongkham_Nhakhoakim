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

// Truy cập bảng LuotKham và bảng LuotKhamDichVu (dịch vụ không gắn răng của lượt khám)
public class LuotKhamDAO {

    static final String SELECT = "SELECT k.maLuotKham, k.maLieuTrinh, k.maHoaDon, k.maLichHen, k.gioBatDau, k.gioKetThuc, k.lyDoKham,"
            + " k.chanDoan, k.huongDieuTri, k.donThuoc, k.ngayTaiKham, k.trangThai, k.ghiChu, "
            + HoSoBenhAnDAO.COT_NOI + ", " + BenhNhanDAO.COT_NOI + ", " + NhanVienDAO.COT_NOI
            + " FROM dbo.LuotKham k JOIN dbo.HoSoBenhAn h ON h.maHoSo = k.maHoSo JOIN dbo.BenhNhan bn ON bn.maBN = h.maBN"
            + " JOIN dbo.NhanVien nv ON nv.maNV = k.maNhanVienPhuTrach";
    static final String SELECT_DICH_VU = "SELECT d.maDong, d.maLuotKham, d.soLuong, d.ghiChu, " + DichVuDAO.COT_NOI
            + " FROM dbo.LuotKhamDichVu d JOIN dbo.DichVu dv ON dv.maDichVu = d.maDichVu";

    // ----- Tìm -----

    // Tìm lượt khám theo mã, không có thì null
    public LuotKham timTheoMa(String maLuotKham) {
        return TienIchDAO.layMot(SELECT + " WHERE k.maLuotKham = ?", this::doc, maLuotKham);
    }

    // Các lượt khám của một hồ sơ, mới nhất trước
    public List<LuotKham> layTheoHoSo(String maHoSo) {
        return TienIchDAO.layDanhSach(SELECT + " WHERE k.maHoSo = ? ORDER BY k.gioBatDau DESC", this::doc, maHoSo);
    }

    // Lượt đang khám của một nha sĩ, không có thì null
    public LuotKham timDangKham(String maNhanVien) {
        return TienIchDAO.layMot(SELECT + " WHERE k.maNhanVienPhuTrach = ? AND k.trangThai = N'Đang khám' ORDER BY k.gioBatDau DESC",
                this::doc, maNhanVien);
    }

    // Lượt đang khám của một hồ sơ, không có thì null
    public LuotKham timDangKhamTheoHoSo(String maHoSo) {
        return TienIchDAO.layMot(SELECT + " WHERE k.maHoSo = ? AND k.trangThai = N'Đang khám'", this::doc, maHoSo);
    }

    // Lượt đã hoàn thành nhưng chưa có hóa đơn
    public List<LuotKham> layChoThanhToan() {
        return TienIchDAO.layDanhSach(SELECT + " WHERE k.trangThai = N'Hoàn thành' AND k.maHoaDon IS NULL ORDER BY k.gioKetThuc", this::doc);
    }

    // ----- Thêm, sửa -----

    // Thêm lượt khám mới
    public void them(LuotKham lk) {
        TienIchDAO.thucThi("INSERT INTO dbo.LuotKham (maLuotKham, maHoSo, maNhanVienPhuTrach, maLieuTrinh, maHoaDon, maLichHen, gioBatDau,"
                + " gioKetThuc, lyDoKham, chanDoan, huongDieuTri, donThuoc, ngayTaiKham, trangThai, ghiChu)"
                + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                lk.getMaLuotKham(), lk.getMaHoSo().getMaHoSo(), lk.getMaNhanVienPhuTrach().getMaNV(), ma(lk.getMaLieuTrinh()),
                ma(lk.getMaHoaDon()), ma(lk.getMaLichHen()), lk.getGioBatDau(), lk.getGioKetThuc(), lk.getLyDoKham(),
                lk.getChanDoan(), lk.getHuongDieuTri(), lk.getDonThuoc(), lk.getNgayTaiKham(), lk.getTrangThai(), lk.getGhiChu());
    }

    // Cập nhật nội dung khám, trạng thái, giờ kết thúc
    public void capNhat(LuotKham lk) {
        TienIchDAO.thucThi("UPDATE dbo.LuotKham SET gioKetThuc = ?, lyDoKham = ?, chanDoan = ?, huongDieuTri = ?, donThuoc = ?,"
                + " ngayTaiKham = ?, trangThai = ?, ghiChu = ?, maLieuTrinh = ? WHERE maLuotKham = ?",
                lk.getGioKetThuc(), lk.getLyDoKham(), lk.getChanDoan(), lk.getHuongDieuTri(), lk.getDonThuoc(), lk.getNgayTaiKham(),
                lk.getTrangThai(), lk.getGhiChu(), ma(lk.getMaLieuTrinh()), lk.getMaLuotKham());
    }

    // Gắn hóa đơn vào lượt khám chưa có hóa đơn; trả về true nếu gắn được
    public boolean ganHoaDon(String maLuotKham, String maHoaDon) {
        return TienIchDAO.thucThi("UPDATE dbo.LuotKham SET maHoaDon = ? WHERE maLuotKham = ? AND maHoaDon IS NULL", maHoaDon, maLuotKham) > 0;
    }

    // ----- Dịch vụ không gắn răng -----

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

    // Xóa một dòng dịch vụ; trả về true nếu có xóa
    public boolean xoaDichVu(String maLuotKham, int maDong) {
        return TienIchDAO.thucThi("DELETE FROM dbo.LuotKhamDichVu WHERE maDong = ? AND maLuotKham = ?", maDong, maLuotKham) > 0;
    }

    // ----- Đọc dòng kết quả -----

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

    // Mã của liệu trình, hóa đơn, lịch hẹn (null nếu chưa có); chỉ dùng khi ghi xuống CSDL
    private static String ma(LieuTrinh lt) {
        return lt == null ? null : lt.getMaLieuTrinh();
    }

    private static String ma(HoaDon hd) {
        return hd == null ? null : hd.getMaHoaDon();
    }

    private static String ma(LichHen lh) {
        return lh == null ? null : lh.getMaLichHen();
    }

    // Đối tượng chỉ có mã (liệu trình, hóa đơn, lịch hẹn): muốn đầy đủ thì tìm lại bằng DAO tương ứng
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
