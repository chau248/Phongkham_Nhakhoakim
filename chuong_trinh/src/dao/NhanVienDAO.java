package dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import entity.NhanVien;

// Truy cập bảng NhanVien
public class NhanVienDAO {

    static final String[] COT = {"maNV", "hoTen", "vaiTro", "soChungChiHanhNghe", "ngayHetHanChungChi", "gioiTinh", "sdt", "chuyenKhoa",
        "ngayVaoLam", "trangThai"};
    static final String CAC_COT = String.join(", ", COT);
    // Cột để nối vào câu SELECT của bảng khác (bí danh nv, tên cột có tiền tố nv_)
    static final String COT_NOI = TienIchDAO.cot(COT, "nv", "nv_");

    // Tất cả nhân viên
    public List<NhanVien> layTatCa() {
        return TienIchDAO.layDanhSach("SELECT " + CAC_COT + " FROM dbo.NhanVien ORDER BY maNV", rs -> doc(rs, ""));
    }

    // Tìm nhân viên theo mã
    public NhanVien timTheoMa(String maNV) {
        return TienIchDAO.layMot("SELECT " + CAC_COT + " FROM dbo.NhanVien WHERE maNV = ?", rs -> doc(rs, ""), maNV);
    }

    // Nha sĩ của lượt khám gần nhất
    public Map<String, NhanVien> layNhaSiKhamGanNhat(Collection<String> dsMaBN) {
        Map<String, NhanVien> kq = new HashMap<>();
        if (dsMaBN.isEmpty()) {
            return kq;
        }
        String dauHoi = String.join(", ", java.util.Collections.nCopies(dsMaBN.size(), "?"));
        String sql = "SELECT h.maBN AS maBN_khach, " + COT_NOI + " FROM dbo.HoSoBenhAn h"
                + " CROSS APPLY (SELECT TOP 1 x.maNhanVienPhuTrach FROM dbo.LuotKham x"
                + " WHERE x.maHoSo = h.maHoSo AND x.trangThai <> N'Đã hủy' ORDER BY x.gioBatDau DESC) k"
                + " JOIN dbo.NhanVien nv ON nv.maNV = k.maNhanVienPhuTrach WHERE h.maBN IN (" + dauHoi + ")";
        TienIchDAO.layDanhSach(sql, rs -> kq.put(rs.getString("maBN_khach"), doc(rs, "nv_")), dsMaBN.toArray());
        return kq;
    }

    // Thêm nhân viên mới
    public void them(NhanVien nv) {
        TienIchDAO.thucThi("INSERT INTO dbo.NhanVien (" + CAC_COT + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                nv.getMaNV(), nv.getHoTen(), nv.getVaiTro(), nv.getSoChungChiHanhNghe(), nv.getNgayHetHanChungChi(),
                nv.getGioiTinh(), nv.getSdt(), nv.getChuyenKhoa(), nv.getNgayVaoLam(), nv.getTrangThai());
    }

    // Cập nhật hồ sơ nhân viên
    public void capNhat(NhanVien nv) {
        TienIchDAO.thucThi("UPDATE dbo.NhanVien SET hoTen = ?, vaiTro = ?, soChungChiHanhNghe = ?, ngayHetHanChungChi = ?,"
                + " gioiTinh = ?, sdt = ?, chuyenKhoa = ?, ngayVaoLam = ?, trangThai = ? WHERE maNV = ?",
                nv.getHoTen(), nv.getVaiTro(), nv.getSoChungChiHanhNghe(), nv.getNgayHetHanChungChi(), nv.getGioiTinh(),
                nv.getSdt(), nv.getChuyenKhoa(), nv.getNgayVaoLam(), nv.getTrangThai(), nv.getMaNV());
    }

    // Đọc nhân viên với tiền tố cột
    static NhanVien doc(ResultSet rs, String tienTo) throws SQLException {
        NhanVien nv = new NhanVien();
        nv.setMaNV(rs.getString(tienTo + "maNV"));
        nv.setHoTen(rs.getString(tienTo + "hoTen"));
        nv.setVaiTro(rs.getString(tienTo + "vaiTro"));
        nv.setSoChungChiHanhNghe(rs.getString(tienTo + "soChungChiHanhNghe"));
        nv.setNgayHetHanChungChi(rs.getObject(tienTo + "ngayHetHanChungChi", LocalDate.class));
        nv.setGioiTinh(rs.getString(tienTo + "gioiTinh"));
        nv.setSdt(rs.getString(tienTo + "sdt"));
        nv.setChuyenKhoa(rs.getString(tienTo + "chuyenKhoa"));
        nv.setNgayVaoLam(rs.getObject(tienTo + "ngayVaoLam", LocalDate.class));
        nv.setTrangThai(rs.getString(tienTo + "trangThai"));
        return nv;
    }

    // Đọc nhân viên nối từ bảng khác
    static NhanVien docNoi(ResultSet rs) throws SQLException {
        return rs.getString("nv_maNV") == null ? null : doc(rs, "nv_");
    }
}
