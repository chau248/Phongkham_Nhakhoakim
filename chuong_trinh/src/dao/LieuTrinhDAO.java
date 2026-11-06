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

    // ----- Tìm -----

    // Các liệu trình của một bệnh nhân: đang thực hiện xếp trước, trong cùng nhóm thì mới bắt đầu trước
    public List<LieuTrinh> layTheoBenhNhan(String maBN) {
        return TienIchDAO.layDanhSach(SELECT + " WHERE l.maBN = ? ORDER BY CASE WHEN l.trangThai = N'" + HangSo.LIEU_TRINH_DANG_THUC_HIEN
                + "' THEN 0 ELSE 1 END, l.ngayBatDau DESC", this::doc, maBN);
    }

    // ----- Đọc dòng kết quả -----

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
