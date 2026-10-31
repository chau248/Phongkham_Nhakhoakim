package nhakhoakim.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import nhakhoakim.entity.NhanVien;

// Truy cập bảng NhanVien (chỉ lớp DAO được viết SQL). Không có hàm xóa: nhân viên chỉ chuyển sang Nghỉ việc (NV-03).
public class NhanVienDAO {

    static final String CAC_COT = "maNV, hoTen, vaiTro, soChungChiHanhNghe, ngayHetHanChungChi, gioiTinh, sdt, chuyenKhoa, ngayVaoLam, trangThai";
    static final String SQL_TAT_CA = "SELECT " + CAC_COT + " FROM dbo.NhanVien ORDER BY maNV";
    static final String SQL_THEO_MA = "SELECT " + CAC_COT + " FROM dbo.NhanVien WHERE maNV = ?";
    static final String SQL_THEM = "INSERT INTO dbo.NhanVien (" + CAC_COT + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    static final String SQL_CAP_NHAT = "UPDATE dbo.NhanVien SET hoTen = ?, vaiTro = ?, soChungChiHanhNghe = ?, ngayHetHanChungChi = ?,"
            + " gioiTinh = ?, sdt = ?, chuyenKhoa = ?, ngayVaoLam = ?, trangThai = ? WHERE maNV = ?";

    // Tất cả nhân viên, theo mã
    public List<NhanVien> layTatCa() {
        List<NhanVien> ds = new ArrayList<>();
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement ps = c.prepareStatement(SQL_TAT_CA);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                ds.add(doc(rs));
            }
        } catch (SQLException e) {
            throw new LoiCSDL("Không đọc được danh sách nhân viên: " + e.getMessage(), e);
        }
        return ds;
    }

    // Tìm nhân viên theo mã; không có thì trả về null
    public NhanVien timTheoMa(String maNV) {
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement ps = c.prepareStatement(SQL_THEO_MA)) {
            ps.setString(1, maNV);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? doc(rs) : null;
            }
        } catch (SQLException e) {
            throw new LoiCSDL("Không đọc được nhân viên: " + e.getMessage(), e);
        }
    }

    // Thêm nhân viên mới
    public void them(NhanVien nv) {
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement ps = c.prepareStatement(SQL_THEM)) {
            ps.setString(1, nv.getMaNV());
            ps.setString(2, nv.getHoTen());
            ps.setString(3, nv.getVaiTro());
            ps.setString(4, nv.getSoChungChiHanhNghe());
            ps.setObject(5, nv.getNgayHetHanChungChi());
            ps.setString(6, nv.getGioiTinh());
            ps.setString(7, nv.getSdt());
            ps.setString(8, nv.getChuyenKhoa());
            ps.setObject(9, nv.getNgayVaoLam());
            ps.setString(10, nv.getTrangThai());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new LoiCSDL("Không thêm được nhân viên: " + e.getMessage(), e);
        }
    }

    // Cập nhật hồ sơ nhân viên (không đổi mã)
    public void capNhat(NhanVien nv) {
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement ps = c.prepareStatement(SQL_CAP_NHAT)) {
            ps.setString(1, nv.getHoTen());
            ps.setString(2, nv.getVaiTro());
            ps.setString(3, nv.getSoChungChiHanhNghe());
            ps.setObject(4, nv.getNgayHetHanChungChi());
            ps.setString(5, nv.getGioiTinh());
            ps.setString(6, nv.getSdt());
            ps.setString(7, nv.getChuyenKhoa());
            ps.setObject(8, nv.getNgayVaoLam());
            ps.setString(9, nv.getTrangThai());
            ps.setString(10, nv.getMaNV());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new LoiCSDL("Không cập nhật được nhân viên: " + e.getMessage(), e);
        }
    }

    // Đổi một dòng kết quả thành đối tượng NhanVien
    private NhanVien doc(ResultSet rs) throws SQLException {
        NhanVien nv = new NhanVien();
        nv.setMaNV(rs.getString("maNV"));
        nv.setHoTen(rs.getString("hoTen"));
        nv.setVaiTro(rs.getString("vaiTro"));
        nv.setSoChungChiHanhNghe(rs.getString("soChungChiHanhNghe"));
        nv.setNgayHetHanChungChi(rs.getObject("ngayHetHanChungChi", LocalDate.class));
        nv.setGioiTinh(rs.getString("gioiTinh"));
        nv.setSdt(rs.getString("sdt"));
        nv.setChuyenKhoa(rs.getString("chuyenKhoa"));
        nv.setNgayVaoLam(rs.getObject("ngayVaoLam", LocalDate.class));
        nv.setTrangThai(rs.getString("trangThai"));
        return nv;
    }
}
