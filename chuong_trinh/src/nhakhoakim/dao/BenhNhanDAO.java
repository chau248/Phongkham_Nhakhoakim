package nhakhoakim.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import nhakhoakim.entity.BenhNhan;
import nhakhoakim.entity.HoSoBenhAn;
import nhakhoakim.entity.TrangThai;

// Truy cập bảng BenhNhan (chỉ lớp DAO được viết SQL)
public class BenhNhanDAO {

    private static final String CAC_COT = "maBN, hoTen, ngaySinh, gioiTinh, sdt, diaChi, email, soCCCD, nguoiLienHeKhanCap,"
            + " sdtNguoiLienHe, ngayDangKy, trangThai, ghiChu";

    // Tìm bệnh nhân theo mã; không có thì trả về null
    public BenhNhan timTheoMa(String maBN) {
        List<BenhNhan> ds = layDanhSach("SELECT " + CAC_COT + " FROM dbo.BenhNhan WHERE maBN = ?", maBN, null, null, 0);
        return ds.isEmpty() ? null : ds.get(0);
    }

    // Tìm bệnh nhân theo số căn cước; không có thì trả về null
    public BenhNhan timTheoCCCD(String soCCCD) {
        List<BenhNhan> ds = layDanhSach("SELECT " + CAC_COT + " FROM dbo.BenhNhan WHERE soCCCD = ?", soCCCD, null, null, 0);
        return ds.isEmpty() ? null : ds.get(0);
    }

    // Tìm bệnh nhân đang điều trị theo mã, họ tên hoặc số điện thoại (không phân biệt hoa thường), tối đa toiDa dòng
    public List<BenhNhan> timKiem(String tuKhoa, int toiDa) {
        String mau = "%" + tuKhoa + "%";
        return layDanhSach("SELECT TOP (?) " + CAC_COT + " FROM dbo.BenhNhan WHERE trangThai = N'" + TrangThai.DANG_DIEU_TRI + "'"
                + " AND (maBN LIKE ? OR hoTen LIKE ? OR sdt LIKE ?) ORDER BY hoTen", mau, mau, mau, toiDa);
    }

    // Tìm mọi bệnh nhân (kể cả đã ngừng theo dõi) trùng họ tên và ngày sinh; họ tên so khớp theo collation của CSDL
    public List<BenhNhan> timTheoHoTenNgaySinh(String hoTen, LocalDate ngaySinh) {
        List<BenhNhan> ds = new ArrayList<>();
        String sql = "SELECT " + CAC_COT + " FROM dbo.BenhNhan WHERE hoTen = ? AND ngaySinh = ?";
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, hoTen);
            ps.setObject(2, ngaySinh);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ds.add(doc(rs));
                }
            }
        } catch (SQLException e) {
            throw new LoiCSDL("Không đọc được bệnh nhân: " + e.getMessage(), e);
        }
        return ds;
    }

    // Thêm bệnh nhân và hồ sơ bệnh án rỗng trong cùng một transaction (BN-04): một trong hai lỗi thì hủy cả hai
    public void themKemHoSo(BenhNhan bn, HoSoBenhAn hs) {
        try (Connection c = DatabaseConnection.getConnection()) {
            c.setAutoCommit(false);
            try {
                them(c, bn);
                new HoSoBenhAnDAO().them(c, hs);
                c.commit();
            } catch (SQLException | RuntimeException e) {
                c.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new LoiCSDL("Không thêm được bệnh nhân: " + e.getMessage(), e);
        }
    }

    // Thêm bệnh nhân bằng kết nối có sẵn (dùng trong themKemHoSo)
    public void them(Connection c, BenhNhan bn) throws SQLException {
        String sql = "INSERT INTO dbo.BenhNhan (" + CAC_COT + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, bn.getMaBN());
            ps.setString(2, bn.getHoTen());
            ps.setObject(3, bn.getNgaySinh());
            ps.setString(4, bn.getGioiTinh());
            ps.setString(5, bn.getSdt());
            ps.setString(6, bn.getDiaChi());
            ps.setString(7, bn.getEmail());
            ps.setString(8, bn.getSoCCCD());
            ps.setString(9, bn.getNguoiLienHeKhanCap());
            ps.setString(10, bn.getSdtNguoiLienHe());
            ps.setObject(11, bn.getNgayDangKy());
            ps.setString(12, bn.getTrangThai());
            ps.setString(13, bn.getGhiChu());
            ps.executeUpdate();
        }
    }

    // Cập nhật thông tin hành chính của bệnh nhân (không đổi mã và ngày đăng ký)
    public void capNhat(BenhNhan bn) {
        String sql = "UPDATE dbo.BenhNhan SET hoTen = ?, ngaySinh = ?, gioiTinh = ?, sdt = ?, diaChi = ?, email = ?, soCCCD = ?,"
                + " nguoiLienHeKhanCap = ?, sdtNguoiLienHe = ?, ghiChu = ? WHERE maBN = ?";
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, bn.getHoTen());
            ps.setObject(2, bn.getNgaySinh());
            ps.setString(3, bn.getGioiTinh());
            ps.setString(4, bn.getSdt());
            ps.setString(5, bn.getDiaChi());
            ps.setString(6, bn.getEmail());
            ps.setString(7, bn.getSoCCCD());
            ps.setString(8, bn.getNguoiLienHeKhanCap());
            ps.setString(9, bn.getSdtNguoiLienHe());
            ps.setString(10, bn.getGhiChu());
            ps.setString(11, bn.getMaBN());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new LoiCSDL("Không cập nhật được bệnh nhân: " + e.getMessage(), e);
        }
    }

    // Chạy câu SELECT: nếu toiDa > 0 thì tham số đầu tiên là TOP, sau đó lần lượt các tham số chuỗi (bỏ qua tham số null)
    private List<BenhNhan> layDanhSach(String sql, String t1, String t2, String t3, int toiDa) {
        List<BenhNhan> ds = new ArrayList<>();
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            int vt = 1;
            if (toiDa > 0) {
                ps.setInt(vt++, toiDa);
            }
            for (String t : new String[] {t1, t2, t3}) {
                if (t != null) {
                    ps.setString(vt++, t);
                }
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ds.add(doc(rs));
                }
            }
        } catch (SQLException e) {
            throw new LoiCSDL("Không đọc được bệnh nhân: " + e.getMessage(), e);
        }
        return ds;
    }

    // Đổi dòng hiện tại của kết quả thành đối tượng BenhNhan
    private BenhNhan doc(ResultSet rs) throws SQLException {
        BenhNhan bn = new BenhNhan();
        bn.setMaBN(rs.getString("maBN"));
        bn.setHoTen(rs.getString("hoTen"));
        bn.setNgaySinh(rs.getObject("ngaySinh", LocalDate.class));
        bn.setGioiTinh(rs.getString("gioiTinh"));
        bn.setSdt(rs.getString("sdt"));
        bn.setDiaChi(rs.getString("diaChi"));
        bn.setEmail(rs.getString("email"));
        bn.setSoCCCD(rs.getString("soCCCD"));
        bn.setNguoiLienHeKhanCap(rs.getString("nguoiLienHeKhanCap"));
        bn.setSdtNguoiLienHe(rs.getString("sdtNguoiLienHe"));
        bn.setNgayDangKy(rs.getObject("ngayDangKy", LocalDate.class));
        bn.setTrangThai(rs.getString("trangThai"));
        bn.setGhiChu(rs.getString("ghiChu"));
        return bn;
    }
}
