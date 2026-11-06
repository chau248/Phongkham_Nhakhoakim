package dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.List;
import entity.ChiTietHoaDon;
import entity.HoaDon;

// Truy cập bảng HoaDon và bảng ChiTietHoaDon
public class HoaDonDAO {

    static final String SELECT = "SELECT h.maHoaDon, h.ngayLap, h.ngayThanhToan, h.tienGiamGia, h.phanTramThue,"
            + " h.hinhThucThanhToan, h.trangThaiThanhToan, h.ghiChu, " + BenhNhanDAO.COT_NOI + ", " + NhanVienDAO.COT_NOI
            + " FROM dbo.HoaDon h JOIN dbo.BenhNhan bn ON bn.maBN = h.maBN LEFT JOIN dbo.NhanVien nv ON nv.maNV = h.maNhanVienLap";
    static final String SELECT_CHI_TIET = "SELECT c.maChiTiet, c.maHoaDon, c.soLuong, c.donGia, c.giamGia, c.soFDIRang, c.ghiChu, "
            + DichVuDAO.COT_NOI + " FROM dbo.ChiTietHoaDon c JOIN dbo.DichVu dv ON dv.maDichVu = c.maDichVu";

    // Tìm hóa đơn theo mã
    public HoaDon timTheoMa(String maHoaDon) {
        return TienIchDAO.layMot(SELECT + " WHERE h.maHoaDon = ?", this::doc, maHoaDon);
    }

    // Tất cả hóa đơn
    public List<HoaDon> layTatCa() {
        List<HoaDon> ds = TienIchDAO.layDanhSach(SELECT + " ORDER BY h.ngayLap DESC, h.maHoaDon DESC", this::doc);
        java.util.Map<String, HoaDon> theoMa = new java.util.HashMap<>();
        for (HoaDon hd : ds) {
            theoMa.put(hd.getMaHoaDon(), hd);
        }
        for (ChiTietHoaDon ct : TienIchDAO.layDanhSach(SELECT_CHI_TIET + " ORDER BY c.maChiTiet", this::docChiTiet)) {
            theoMa.get(ct.getMaHoaDon().getMaHoaDon()).getDsChiTiet().add(ct);
        }
        return ds;
    }

    // Các dòng chi tiết của một hóa đơn
    public List<ChiTietHoaDon> layChiTiet(String maHoaDon) {
        return TienIchDAO.layDanhSach(SELECT_CHI_TIET + " WHERE c.maHoaDon = ? ORDER BY c.maChiTiet", this::docChiTiet, maHoaDon);
    }

    // Lưu hóa đơn, các dòng chi tiết và gắn hóa đơn vào lượt khám trong một giao dịch
    public void themKemChiTiet(HoaDon hd, List<ChiTietHoaDon> dsChiTiet, String maLuotKham) {
        TienIchDAO.giaoDich(c -> {
            TienIchDAO.thucThi(c, "INSERT INTO dbo.HoaDon (maHoaDon, maBN, maNhanVienLap, ngayLap, tienGiamGia, phanTramThue,"
                    + " hinhThucThanhToan, trangThaiThanhToan, ghiChu) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                    hd.getMaHoaDon(), hd.getMaBenhNhan().getMaBN(), hd.getMaNhanVien() == null ? null : hd.getMaNhanVien().getMaNV(),
                    hd.getNgayLap(), hd.getTienGiamGia(), hd.getPhanTramThue(),
                    hd.getHinhThucThanhToan(), hd.getTrangThai(), hd.getGhiChu());
            for (ChiTietHoaDon ct : dsChiTiet) {
                // Số răng 0 nghĩa là dịch vụ không gắn răng
                TienIchDAO.thucThi(c, "INSERT INTO dbo.ChiTietHoaDon (maChiTiet, maHoaDon, maDichVu, soLuong, donGia, giamGia, soFDIRang, ghiChu)"
                        + " VALUES (?, ?, ?, ?, ?, ?, ?, ?)", ct.getMaChiTiet(), ct.getMaHoaDon().getMaHoaDon(), ct.getMaDichVu().getMaDichVu(),
                        ct.getSoLuong(), ct.getDonGia(), ct.getGiamGia() == null ? BigDecimal.ZERO : ct.getGiamGia(),
                        ct.getSoFDIRang() > 0 ? ct.getSoFDIRang() : null, ct.getGhiChu());
            }
            int soDong = TienIchDAO.thucThi(c, "UPDATE dbo.LuotKham SET maHoaDon = ? WHERE maLuotKham = ? AND maHoaDon IS NULL",
                    hd.getMaHoaDon(), maLuotKham);
            if (soDong != 1) {
                throw new NghiepVuException("Lượt khám " + maLuotKham + " đã có hóa đơn");
            }
        });
    }

    // Ghi nhận thanh toán cho hóa đơn Chưa thanh toán
    public boolean thanhToan(String maHoaDon, String hinhThuc, LocalDateTime luc, String maNhanVien) {
        return TienIchDAO.thucThi("UPDATE dbo.HoaDon SET trangThaiThanhToan = N'Đã thanh toán', hinhThucThanhToan = ?, ngayThanhToan = ?,"
                + " maNhanVienLap = COALESCE(maNhanVienLap, ?) WHERE maHoaDon = ? AND trangThaiThanhToan = N'Chưa thanh toán'",
                hinhThuc, luc, maNhanVien, maHoaDon) == 1;
    }

    // Hủy hóa đơn Chưa thanh toán và gỡ khỏi lượt khám
    public boolean huy(String maHoaDon, String ghiChu) {
        boolean[] duoc = new boolean[1];
        TienIchDAO.giaoDich(c -> {
            duoc[0] = TienIchDAO.thucThi(c, "UPDATE dbo.HoaDon SET trangThaiThanhToan = N'Đã hủy', ghiChu = ? WHERE maHoaDon = ?"
                    + " AND trangThaiThanhToan = N'Chưa thanh toán'", ghiChu, maHoaDon) == 1;
            if (duoc[0]) {
                TienIchDAO.thucThi(c, "UPDATE dbo.LuotKham SET maHoaDon = NULL WHERE maHoaDon = ?", maHoaDon);
            }
        });
        return duoc[0];
    }

    private HoaDon doc(ResultSet rs) throws SQLException {
        HoaDon hd = new HoaDon();
        hd.setMaHoaDon(rs.getString("maHoaDon"));
        hd.setMaBenhNhan(BenhNhanDAO.docNoi(rs));
        hd.setMaNhanVien(NhanVienDAO.docNoi(rs));
        hd.setNgayLap(rs.getObject("ngayLap", LocalDateTime.class));
        hd.setNgayThanhToan(rs.getObject("ngayThanhToan", LocalDateTime.class));
        hd.setTienGiamGia(rs.getBigDecimal("tienGiamGia"));
        hd.setPhanTramThue(rs.getBigDecimal("phanTramThue"));
        hd.setHinhThucThanhToan(rs.getString("hinhThucThanhToan"));
        hd.setTrangThai(rs.getString("trangThaiThanhToan"));
        hd.setGhiChu(rs.getString("ghiChu"));
        return hd;
    }

    private ChiTietHoaDon docChiTiet(ResultSet rs) throws SQLException {
        ChiTietHoaDon ct = new ChiTietHoaDon();
        ct.setMaChiTiet(rs.getString("maChiTiet"));
        HoaDon hd = new HoaDon(); // chỉ có mã hóa đơn
        hd.setMaHoaDon(rs.getString("maHoaDon"));
        ct.setMaHoaDon(hd);
        ct.setMaDichVu(DichVuDAO.docNoi(rs));
        ct.setSoLuong(rs.getInt("soLuong"));
        ct.setDonGia(rs.getBigDecimal("donGia"));
        ct.setGiamGia(rs.getBigDecimal("giamGia"));
        ct.setSoFDIRang(rs.getInt("soFDIRang")); // NULL đọc thành 0
        ct.setGhiChu(rs.getString("ghiChu"));
        return ct;
    }

    // Tổng tiền của một hóa đơn tính ngay trong SQL: (tiền dịch vụ - giảm giá) cộng VAT
    private static final String TONG_TIEN = "ROUND((ISNULL((SELECT SUM(c.soLuong * c.donGia - c.giamGia) FROM dbo.ChiTietHoaDon c"
            + " WHERE c.maHoaDon = h.maHoaDon), 0) - h.tienGiamGia) * (100 + h.phanTramThue) / 100, 0)";
    // Bảng hóa đơn kèm cột tong để cộng được trong SQL
    private static final String HOA_DON_CO_TONG = "(SELECT h.ngayThanhToan, h.hinhThucThanhToan, h.trangThaiThanhToan, " + TONG_TIEN + " AS tong FROM dbo.HoaDon h) h";
    private static final String DA_THANH_TOAN_TRONG_KHOANG = " h.trangThaiThanhToan = N'Đã thanh toán' AND CAST(h.ngayThanhToan AS DATE) BETWEEN ? AND ?";

    // Doanh thu từng ngày: {ngày
    public List<Object[]> doanhThuTheoNgay(java.time.LocalDate tu, java.time.LocalDate den) {
        return TienIchDAO.layDanhSach("SELECT CAST(h.ngayThanhToan AS DATE), COUNT(*), SUM(h.tong) FROM " + HOA_DON_CO_TONG + " WHERE"
                + DA_THANH_TOAN_TRONG_KHOANG + " GROUP BY CAST(h.ngayThanhToan AS DATE) ORDER BY 1",
                rs -> new Object[] {rs.getObject(1, java.time.LocalDate.class), rs.getInt(2), rs.getLong(3)}, tu, den);
    }

    // Doanh thu theo hình thức thanh toán: {hình thức
    public List<Object[]> doanhThuTheoHinhThuc(java.time.LocalDate tu, java.time.LocalDate den) {
        return TienIchDAO.layDanhSach("SELECT h.hinhThucThanhToan, COUNT(*), SUM(h.tong) FROM " + HOA_DON_CO_TONG + " WHERE"
                + DA_THANH_TOAN_TRONG_KHOANG + " GROUP BY h.hinhThucThanhToan ORDER BY 3 DESC",
                rs -> new Object[] {rs.getString(1), rs.getInt(2), rs.getLong(3)}, tu, den);
    }

    // Tiền dịch vụ theo loại dịch vụ: {loại
    public List<Object[]> doanhThuTheoLoaiDichVu(java.time.LocalDate tu, java.time.LocalDate den) {
        return TienIchDAO.layDanhSach("SELECT dv.loaiDichVu, SUM(ct.soLuong), SUM(ct.soLuong * ct.donGia - ct.giamGia) FROM dbo.ChiTietHoaDon ct"
                + " JOIN dbo.HoaDon h ON h.maHoaDon = ct.maHoaDon JOIN dbo.DichVu dv ON dv.maDichVu = ct.maDichVu WHERE"
                + DA_THANH_TOAN_TRONG_KHOANG + " GROUP BY dv.loaiDichVu ORDER BY 3 DESC",
                rs -> new Object[] {rs.getString(1), rs.getInt(2), rs.getLong(3)}, tu, den);
    }

    // Năm dịch vụ bán chạy nhất
    public List<Object[]> dichVuBanChay(java.time.LocalDate tu, java.time.LocalDate den) {
        return TienIchDAO.layDanhSach("SELECT TOP 5 dv.tenDichVu, SUM(ct.soLuong), SUM(ct.soLuong * ct.donGia - ct.giamGia) FROM dbo.ChiTietHoaDon ct"
                + " JOIN dbo.HoaDon h ON h.maHoaDon = ct.maHoaDon JOIN dbo.DichVu dv ON dv.maDichVu = ct.maDichVu WHERE"
                + DA_THANH_TOAN_TRONG_KHOANG + " GROUP BY dv.tenDichVu ORDER BY 2 DESC, 3 DESC",
                rs -> new Object[] {rs.getString(1), rs.getInt(2), rs.getLong(3)}, tu, den);
    }
}
