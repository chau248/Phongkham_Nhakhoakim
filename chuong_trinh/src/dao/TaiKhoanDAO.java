package dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import entity.TaiKhoan;

// Truy cập bảng TaiKhoan
public class TaiKhoanDAO {

    static final String SELECT = "SELECT t.maTaiKhoan, t.tenDangNhap, t.matKhau, t.vaiTro, t.trangThaiKhoa, t.ngayTao, t.lanDangNhapCuoi,"
            + " t.soLanDangNhapSai, t.thoiGianKhoaDen, t.ngayDoiMatKhauCuoi, t.yeuCauDoiMatKhau, " + NhanVienDAO.COT_NOI
            + " FROM dbo.TaiKhoan t JOIN dbo.NhanVien nv ON nv.maNV = t.maNV";

    // ----- Tìm -----

    // Tất cả tài khoản, theo mã
    public List<TaiKhoan> layTatCa() {
        return TienIchDAO.layDanhSach(SELECT + " ORDER BY t.maTaiKhoan", this::doc);
    }

    // Tìm tài khoản theo mã, không có thì null
    public TaiKhoan timTheoMa(String maTaiKhoan) {
        return TienIchDAO.layMot(SELECT + " WHERE t.maTaiKhoan = ?", this::doc, maTaiKhoan);
    }

    // Tìm tài khoản theo tên đăng nhập
    public TaiKhoan timTheoTenDangNhap(String ten) {
        return TienIchDAO.layMot(SELECT + " WHERE t.tenDangNhap = ?", this::doc, ten);
    }

    // Tài khoản của một nhân viên
    public TaiKhoan timTheoNhanVien(String maNV) {
        return TienIchDAO.layMot(SELECT + " WHERE t.maNV = ?", this::doc, maNV);
    }

    // Số tài khoản Quản lý đang hoạt động
    public int demQuanLyHoatDong() {
        Integer so = TienIchDAO.layMot("SELECT COUNT(*) FROM dbo.TaiKhoan t JOIN dbo.NhanVien n ON n.maNV = t.maNV"
                + " WHERE t.vaiTro = N'Quản lý' AND t.trangThaiKhoa = 0 AND n.trangThai = N'Đang làm việc'", rs -> rs.getInt(1));
        return so == null ? 0 : so;
    }

    // ----- Thêm, sửa -----

    // Thêm tài khoản mới
    public void them(TaiKhoan tk) {
        TienIchDAO.thucThi("INSERT INTO dbo.TaiKhoan (maTaiKhoan, maNV, tenDangNhap, matKhau, vaiTro, trangThaiKhoa, yeuCauDoiMatKhau)"
                + " VALUES (?, ?, ?, ?, ?, 0, ?)", tk.getMaTaiKhoan(), tk.getMaNV().getMaNV(), tk.getTenDangNhap(), tk.getMatKhauHash(),
                tk.getVaiTro(), tk.isYeuCauDoiMatKhau());
    }

    // Đặt mật khẩu mới (đã băm)
    public void doiMatKhau(String maTaiKhoan, String matKhauDaBam, boolean yeuCauDoi) {
        TienIchDAO.thucThi("UPDATE dbo.TaiKhoan SET matKhau = ?, ngayDoiMatKhauCuoi = CAST(SYSDATETIME() AS DATE), yeuCauDoiMatKhau = ?"
                + " WHERE maTaiKhoan = ?", matKhauDaBam, yeuCauDoi, maTaiKhoan);
    }

    // Thay mật khẩu chữ thường cũ bằng bản băm
    public void nangCapBam(String maTaiKhoan, String matKhauDaBam) {
        TienIchDAO.thucThi("UPDATE dbo.TaiKhoan SET matKhau = ? WHERE maTaiKhoan = ?", matKhauDaBam, maTaiKhoan);
    }

    // Ghi nhận đăng nhập sai (có thể khóa luôn tài khoản)
    public void ghiNhanDangNhapSai(String maTaiKhoan, int soLanSai, boolean khoa) {
        TienIchDAO.thucThi("UPDATE dbo.TaiKhoan SET soLanDangNhapSai = ?, trangThaiKhoa = ? WHERE maTaiKhoan = ?", soLanSai, khoa, maTaiKhoan);
    }

    // Ghi nhận đăng nhập đúng
    public void ghiNhanDangNhapDung(String maTaiKhoan) {
        TienIchDAO.thucThi("UPDATE dbo.TaiKhoan SET soLanDangNhapSai = 0, thoiGianKhoaDen = NULL, lanDangNhapCuoi = SYSDATETIME()"
                + " WHERE maTaiKhoan = ?", maTaiKhoan);
    }

    // Khóa hoặc mở khóa tài khoản
    public void datKhoa(String maTaiKhoan, boolean khoa) {
        TienIchDAO.thucThi("UPDATE dbo.TaiKhoan SET trangThaiKhoa = ?, soLanDangNhapSai = 0 WHERE maTaiKhoan = ?", khoa, maTaiKhoan);
    }

    // Đổi vai trò tài khoản theo nhân viên
    public void doiVaiTroTheoNhanVien(String maNV, String vaiTro) {
        TienIchDAO.thucThi("UPDATE dbo.TaiKhoan SET vaiTro = ? WHERE maNV = ?", vaiTro, maNV);
    }

    // Khóa tài khoản của nhân viên
    public void khoaTheoNhanVien(String maNV) {
        TienIchDAO.thucThi("UPDATE dbo.TaiKhoan SET trangThaiKhoa = 1 WHERE maNV = ?", maNV);
    }

    // ----- Đọc dòng kết quả -----

    private TaiKhoan doc(ResultSet rs) throws SQLException {
        TaiKhoan tk = new TaiKhoan();
        tk.setMaTaiKhoan(rs.getString("maTaiKhoan"));
        tk.setMaNV(NhanVienDAO.docNoi(rs));
        tk.setTenDangNhap(rs.getString("tenDangNhap"));
        tk.setMatKhauHash(rs.getString("matKhau"));
        tk.setVaiTro(rs.getString("vaiTro"));
        tk.setTrangThaiKhoa(rs.getBoolean("trangThaiKhoa"));
        tk.setNgayTao(rs.getObject("ngayTao", LocalDateTime.class));
        tk.setLanDangNhapCuoi(rs.getObject("lanDangNhapCuoi", LocalDateTime.class));
        tk.setSoLanDangNhapSai(rs.getInt("soLanDangNhapSai"));
        tk.setThoiGianKhoaDen(rs.getObject("thoiGianKhoaDen", LocalDateTime.class));
        LocalDate doiMk = rs.getObject("ngayDoiMatKhauCuoi", LocalDate.class);
        tk.setNgayDoiMatKhauCuoi(doiMk == null ? null : doiMk.atStartOfDay());
        tk.setYeuCauDoiMatKhau(rs.getBoolean("yeuCauDoiMatKhau"));
        return tk;
    }
}
