package dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import entity.HangSo;
import entity.LieuTrinh;

// Truy cập bảng LieuTrinh
public class LieuTrinhDAO {

    static final String SELECT = "SELECT l.maLieuTrinh, l.loaiDichVu, l.soBuoiDuKien, l.giaiDoanHienTai, l.ngayBatDau, l.ngayDuKienKetThuc,"
            + " l.ngayKetThucThucTe, l.tongChiPhiDuKien, l.trangThai, l.ghiChu, " + BenhNhanDAO.COT_NOI + ", " + NhanVienDAO.COT_NOI
            + " FROM dbo.LieuTrinh l JOIN dbo.BenhNhan bn ON bn.maBN = l.maBN LEFT JOIN dbo.NhanVien nv ON nv.maNV = l.maNhaSiPhuTrach";

    // Các liệu trình của một bệnh nhân: đang thực hiện xếp trước
    public List<LieuTrinh> layTheoBenhNhan(String maBN) {
        return TienIchDAO.layDanhSach(SELECT + " WHERE l.maBN = ? ORDER BY CASE WHEN l.trangThai = N'" + HangSo.LIEU_TRINH_DANG_THUC_HIEN
                + "' THEN 0 ELSE 1 END, l.ngayBatDau DESC", this::doc, maBN);
    }

    // Tìm liệu trình theo mã
    public LieuTrinh timTheoMa(String maLieuTrinh) {
        return TienIchDAO.layMot(SELECT + " WHERE l.maLieuTrinh = ?", this::doc, maLieuTrinh);
    }

    // Số buổi đã khám xong của liệu trình
    public int demBuoiDaKham(String maLieuTrinh) {
        Integer so = TienIchDAO.layMot("SELECT COUNT(*) FROM dbo.LuotKham WHERE maLieuTrinh = ? AND trangThai = N'" + HangSo.HOAN_THANH + "'",
                rs -> rs.getInt(1), maLieuTrinh);
        return so == null ? 0 : so;
    }

    // Liệu trình có lượt khám đang khám gắn vào không
    public boolean coLuotKhamDangKham(String maLieuTrinh) {
        return TienIchDAO.layMot("SELECT 1 FROM dbo.LuotKham WHERE maLieuTrinh = ? AND trangThai = N'" + HangSo.DANG_KHAM + "'",
                rs -> 1, maLieuTrinh) != null;
    }

    // Thêm liệu trình mới
    public void them(LieuTrinh lt) {
        TienIchDAO.thucThi("INSERT INTO dbo.LieuTrinh (maLieuTrinh, maBN, loaiDichVu, soBuoiDuKien, giaiDoanHienTai, ngayBatDau, ngayDuKienKetThuc,"
                + " tongChiPhiDuKien, trangThai, maNhaSiPhuTrach, ghiChu) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                lt.getMaLieuTrinh(), lt.getMaBN().getMaBN(), lt.getLoaiDichVu(), lt.getSoBuoiDuKien(), lt.getGiaiDoanHienTai(),
                lt.getNgayBatDau(), lt.getNgayDuKienKetThuc(), lt.getTongChiPhiDuKien(), lt.getTrangThai(),
                lt.getMaNhaSiPhuTrach() == null ? null : lt.getMaNhaSiPhuTrach().getMaNV(), lt.getGhiChu());
    }

    // Cập nhật giai đoạn
    public void capNhat(LieuTrinh lt) {
        TienIchDAO.thucThi("UPDATE dbo.LieuTrinh SET giaiDoanHienTai = ?, ghiChu = ?, trangThai = ?, ngayKetThucThucTe = ? WHERE maLieuTrinh = ?",
                lt.getGiaiDoanHienTai(), lt.getGhiChu(), lt.getTrangThai(), lt.getNgayKetThucThucTe(), lt.getMaLieuTrinh());
    }

    private LieuTrinh doc(ResultSet rs) throws SQLException {
        LieuTrinh lt = new LieuTrinh();
        lt.setMaLieuTrinh(rs.getString("maLieuTrinh"));
        lt.setMaBN(BenhNhanDAO.docNoi(rs));
        lt.setLoaiDichVu(rs.getString("loaiDichVu"));
        lt.setSoBuoiDuKien(rs.getInt("soBuoiDuKien"));
        lt.setGiaiDoanHienTai(rs.getString("giaiDoanHienTai"));
        lt.setNgayBatDau(rs.getObject("ngayBatDau", LocalDate.class));
        lt.setNgayDuKienKetThuc(rs.getObject("ngayDuKienKetThuc", LocalDate.class));
        lt.setNgayKetThucThucTe(rs.getObject("ngayKetThucThucTe", LocalDate.class));
        lt.setTongChiPhiDuKien(rs.getLong("tongChiPhiDuKien"));
        lt.setTrangThai(rs.getString("trangThai"));
        lt.setMaNhaSiPhuTrach(NhanVienDAO.docNoi(rs));
        lt.setGhiChu(rs.getString("ghiChu"));
        return lt;
    }
}
