package nhakhoakim.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import nhakhoakim.entity.NhanVien;
import nhakhoakim.entity.TaiKhoan;

// Truy cập bảng TaiKhoan (chỉ lớp DAO được viết SQL). Mỗi tài khoản đọc kèm nhân viên sở hữu.
public class TaiKhoanDAO {

    static final String CAC_COT = "t.maTaiKhoan, t.maNV, n.hoTen, n.vaiTro AS vaiTroNV, n.trangThai AS trangThaiNV, t.tenDangNhap, t.matKhau,"
            + " t.vaiTro, t.trangThaiKhoa, t.ngayTao, t.lanDangNhapCuoi, t.soLanDangNhapSai, t.thoiGianKhoaDen, t.ngayDoiMatKhauCuoi,"
            + " t.yeuCauDoiMatKhau";
    static final String TU_BANG = " FROM dbo.TaiKhoan t JOIN dbo.NhanVien n ON n.maNV = t.maNV";
    static final String SQL_TAT_CA = "SELECT " + CAC_COT + TU_BANG + " ORDER BY t.maTaiKhoan";
    static final String SQL_THEO_MA = "SELECT " + CAC_COT + TU_BANG + " WHERE t.maTaiKhoan = ?";
    static final String SQL_THEO_TEN = "SELECT " + CAC_COT + TU_BANG + " WHERE t.tenDangNhap = ?";
    static final String SQL_THEO_NHAN_VIEN = "SELECT " + CAC_COT + TU_BANG + " WHERE t.maNV = ?";
    static final String SQL_THEM = "INSERT INTO dbo.TaiKhoan (maTaiKhoan, maNV, tenDangNhap, matKhau, vaiTro, trangThaiKhoa, yeuCauDoiMatKhau)"
            + " VALUES (?, ?, ?, ?, ?, 0, ?)";
    static final String SQL_DOI_MAT_KHAU = "UPDATE dbo.TaiKhoan SET matKhau = ?, ngayDoiMatKhauCuoi = CAST(SYSDATETIME() AS DATE),"
            + " yeuCauDoiMatKhau = ? WHERE maTaiKhoan = ?";
    static final String SQL_NANG_CAP_BAM = "UPDATE dbo.TaiKhoan SET matKhau = ? WHERE maTaiKhoan = ?";
    static final String SQL_DANG_NHAP_SAI = "UPDATE dbo.TaiKhoan SET soLanDangNhapSai = ?, trangThaiKhoa = ? WHERE maTaiKhoan = ?";
    static final String SQL_DANG_NHAP_DUNG = "UPDATE dbo.TaiKhoan SET soLanDangNhapSai = 0, thoiGianKhoaDen = NULL,"
            + " lanDangNhapCuoi = SYSDATETIME() WHERE maTaiKhoan = ?";
    static final String SQL_DAT_KHOA = "UPDATE dbo.TaiKhoan SET trangThaiKhoa = ?, soLanDangNhapSai = 0 WHERE maTaiKhoan = ?";
    static final String SQL_DOI_VAI_TRO_THEO_NV = "UPDATE dbo.TaiKhoan SET vaiTro = ? WHERE maNV = ?";
    static final String SQL_KHOA_THEO_NV = "UPDATE dbo.TaiKhoan SET trangThaiKhoa = 1 WHERE maNV = ?";
    static final String SQL_DEM_QUAN_LY = "SELECT COUNT(*) FROM dbo.TaiKhoan t JOIN dbo.NhanVien n ON n.maNV = t.maNV"
            + " WHERE t.vaiTro = N'Quản lý' AND t.trangThaiKhoa = 0 AND n.trangThai = N'Đang làm việc'";

    // Tất cả tài khoản, theo mã
    public List<TaiKhoan> layTatCa() {
        return layDanhSach(SQL_TAT_CA, null);
    }

    // Tìm tài khoản theo mã; không có thì trả về null
    public TaiKhoan timTheoMa(String maTaiKhoan) {
        return mot(layDanhSach(SQL_THEO_MA, maTaiKhoan));
    }

    // Tìm tài khoản theo tên đăng nhập (không phân biệt hoa thường theo collation CSDL); không có thì trả về null
    public TaiKhoan timTheoTenDangNhap(String ten) {
        return mot(layDanhSach(SQL_THEO_TEN, ten));
    }

    // Tài khoản của một nhân viên (mỗi nhân viên đúng một tài khoản); chưa có thì trả về null
    public TaiKhoan timTheoNhanVien(String maNV) {
        return mot(layDanhSach(SQL_THEO_NHAN_VIEN, maNV));
    }

    // Thêm tài khoản mới (mật khẩu đã băm)
    public void them(TaiKhoan tk) {
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement ps = c.prepareStatement(SQL_THEM)) {
            ps.setString(1, tk.getMaTaiKhoan());
            ps.setString(2, tk.getMaNV().getMaNV());
            ps.setString(3, tk.getTenDangNhap());
            ps.setString(4, tk.getMatKhauHash());
            ps.setString(5, tk.getVaiTro());
            ps.setBoolean(6, tk.isYeuCauDoiMatKhau());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new LoiCSDL("Không thêm được tài khoản: " + e.getMessage(), e);
        }
    }

    // Đặt mật khẩu mới (đã băm); yeuCauDoi = true thì lần đăng nhập sau phải đổi mật khẩu
    public void doiMatKhau(String maTaiKhoan, String matKhauDaBam, boolean yeuCauDoi) {
        chay(SQL_DOI_MAT_KHAU, matKhauDaBam, yeuCauDoi, maTaiKhoan);
    }

    // Thay mật khẩu chữ thường cũ bằng bản băm của cùng mật khẩu (không tính là đổi mật khẩu)
    public void nangCapBam(String maTaiKhoan, String matKhauDaBam) {
        chay(SQL_NANG_CAP_BAM, matKhauDaBam, maTaiKhoan);
    }

    // Ghi nhận đăng nhập sai: đặt lại bộ đếm, khóa tài khoản nếu khoa = true
    public void ghiNhanDangNhapSai(String maTaiKhoan, int soLanSai, boolean khoa) {
        chay(SQL_DANG_NHAP_SAI, soLanSai, khoa, maTaiKhoan);
    }

    // Ghi nhận đăng nhập đúng: xóa bộ đếm sai, ghi giờ đăng nhập cuối
    public void ghiNhanDangNhapDung(String maTaiKhoan) {
        chay(SQL_DANG_NHAP_DUNG, maTaiKhoan);
    }

    // Khóa hoặc mở khóa tài khoản (cả hai đều xóa bộ đếm sai)
    public void datKhoa(String maTaiKhoan, boolean khoa) {
        chay(SQL_DAT_KHOA, khoa, maTaiKhoan);
    }

    // Đổi vai trò tài khoản theo vai trò mới của nhân viên (vai trò tài khoản luôn bằng vai trò nhân viên, TK-04)
    public void doiVaiTroTheoNhanVien(String maNV, String vaiTro) {
        chay(SQL_DOI_VAI_TRO_THEO_NV, vaiTro, maNV);
    }

    // Khóa tài khoản của nhân viên đã nghỉ việc (NV-03)
    public void khoaTheoNhanVien(String maNV) {
        chay(SQL_KHOA_THEO_NV, maNV);
    }

    // Số tài khoản Quản lý đang hoạt động (không bị khóa, nhân viên còn làm việc)
    public int demQuanLyHoatDong() {
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement ps = c.prepareStatement(SQL_DEM_QUAN_LY);
                ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) {
            throw new LoiCSDL("Không đếm được tài khoản quản lý: " + e.getMessage(), e);
        }
    }

    // Chạy một câu UPDATE với các tham số bất kỳ
    private void chay(String sql, Object... thamSo) {
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            for (int i = 0; i < thamSo.length; i++) {
                ps.setObject(i + 1, thamSo[i]);
            }
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new LoiCSDL("Không cập nhật được tài khoản: " + e.getMessage(), e);
        }
    }

    private static TaiKhoan mot(List<TaiKhoan> ds) {
        return ds.isEmpty() ? null : ds.get(0);
    }

    // Chạy câu SELECT (có hoặc không có một tham số chuỗi) và đọc tất cả dòng
    private List<TaiKhoan> layDanhSach(String sql, String thamSo) {
        List<TaiKhoan> ds = new ArrayList<>();
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            if (thamSo != null) {
                ps.setString(1, thamSo);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    NhanVien nv = new NhanVien();
                    nv.setMaNV(rs.getString("maNV"));
                    nv.setHoTen(rs.getString("hoTen"));
                    nv.setVaiTro(rs.getString("vaiTroNV"));
                    nv.setTrangThai(rs.getString("trangThaiNV"));
                    TaiKhoan tk = new TaiKhoan();
                    tk.setMaTaiKhoan(rs.getString("maTaiKhoan"));
                    tk.setMaNV(nv);
                    tk.setTenDangNhap(rs.getString("tenDangNhap"));
                    tk.setMatKhauHash(rs.getString("matKhau"));
                    tk.setVaiTro(rs.getString("vaiTro"));
                    tk.setTrangThaiKhoa(rs.getBoolean("trangThaiKhoa"));
                    tk.setNgayTao(thoiGian(rs.getTimestamp("ngayTao")));
                    tk.setLanDangNhapCuoi(thoiGian(rs.getTimestamp("lanDangNhapCuoi")));
                    tk.setSoLanDangNhapSai(rs.getInt("soLanDangNhapSai"));
                    tk.setThoiGianKhoaDen(thoiGian(rs.getTimestamp("thoiGianKhoaDen")));
                    java.sql.Date doiMk = rs.getDate("ngayDoiMatKhauCuoi");
                    tk.setNgayDoiMatKhauCuoi(doiMk == null ? null : doiMk.toLocalDate().atStartOfDay());
                    tk.setYeuCauDoiMatKhau(rs.getBoolean("yeuCauDoiMatKhau"));
                    ds.add(tk);
                }
            }
        } catch (SQLException e) {
            throw new LoiCSDL("Không đọc được tài khoản: " + e.getMessage(), e);
        }
        return ds;
    }

    private static java.time.LocalDateTime thoiGian(Timestamp t) {
        return t == null ? null : t.toLocalDateTime();
    }
}
