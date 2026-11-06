package dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import entity.DichVu;

// Truy cập bảng DichVu
public class DichVuDAO {

    static final String[] COT = {"maDichVu", "tenDichVu", "gia", "thoiGianDuKien", "moTa", "loaiDichVu", "donViTinh",
        "trangThaiHoatDong", "yeuCauLieuTrinh", "lienQuanImplantCauRang"};
    static final String CAC_COT = String.join(", ", COT);
    // Cột để nối vào câu SELECT của bảng khác (bí danh dv, tên cột có tiền tố dv_)
    static final String COT_NOI = TienIchDAO.cot(COT, "dv", "dv_");

    // Tất cả dịch vụ
    public List<DichVu> layTatCa() {
        return TienIchDAO.layDanhSach("SELECT " + CAC_COT + " FROM dbo.DichVu ORDER BY maDichVu", rs -> doc(rs, ""));
    }

    // Tìm dịch vụ theo mã
    public DichVu timTheoMa(String maDichVu) {
        return TienIchDAO.layMot("SELECT " + CAC_COT + " FROM dbo.DichVu WHERE maDichVu = ?", rs -> doc(rs, ""), maDichVu);
    }

    // Tìm dịch vụ theo tên
    public DichVu timTheoTen(String ten) {
        return TienIchDAO.layMot("SELECT " + CAC_COT + " FROM dbo.DichVu WHERE tenDichVu = ?", rs -> doc(rs, ""), ten);
    }

    // Thêm dịch vụ mới
    public void them(DichVu dv) {
        TienIchDAO.thucThi("INSERT INTO dbo.DichVu (" + CAC_COT + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                dv.getMaDichVu(), dv.getTenDichVu(), dv.getGia(), dv.getThoiGianDuKien(), dv.getMoTa(), dv.getLoaiDichVu(),
                dv.getDonViTinh(), dv.isTrangThaiHoatDong(), dv.isYeuCauLieuTrinh(), dv.isLienQuanImplantCauRang());
    }

    // Cập nhật dịch vụ
    public void capNhat(DichVu dv) {
        TienIchDAO.thucThi("UPDATE dbo.DichVu SET tenDichVu = ?, gia = ?, thoiGianDuKien = ?, moTa = ?, loaiDichVu = ?,"
                + " donViTinh = ?, trangThaiHoatDong = ?, yeuCauLieuTrinh = ?, lienQuanImplantCauRang = ? WHERE maDichVu = ?",
                dv.getTenDichVu(), dv.getGia(), dv.getThoiGianDuKien(), dv.getMoTa(), dv.getLoaiDichVu(), dv.getDonViTinh(),
                dv.isTrangThaiHoatDong(), dv.isYeuCauLieuTrinh(), dv.isLienQuanImplantCauRang(), dv.getMaDichVu());
    }

    // Đọc dịch vụ với tiền tố cột
    static DichVu doc(ResultSet rs, String tienTo) throws SQLException {
        DichVu dv = new DichVu();
        dv.setMaDichVu(rs.getString(tienTo + "maDichVu"));
        dv.setTenDichVu(rs.getString(tienTo + "tenDichVu"));
        dv.setGia(rs.getLong(tienTo + "gia"));
        dv.setThoiGianDuKien(rs.getInt(tienTo + "thoiGianDuKien"));
        dv.setMoTa(rs.getString(tienTo + "moTa"));
        dv.setLoaiDichVu(rs.getString(tienTo + "loaiDichVu"));
        dv.setDonViTinh(rs.getString(tienTo + "donViTinh"));
        dv.setTrangThaiHoatDong(rs.getBoolean(tienTo + "trangThaiHoatDong"));
        dv.setYeuCauLieuTrinh(rs.getBoolean(tienTo + "yeuCauLieuTrinh"));
        dv.setLienQuanImplantCauRang(rs.getBoolean(tienTo + "lienQuanImplantCauRang"));
        return dv;
    }

    // Đọc dịch vụ nối từ bảng khác
    static DichVu docNoi(ResultSet rs) throws SQLException {
        return rs.getString("dv_maDichVu") == null ? null : doc(rs, "dv_");
    }
}
