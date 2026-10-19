package nhakhoakim.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import nhakhoakim.entity.HoSoBenhAn;

// Truy cập bảng HoSoBenhAn (chỉ lớp DAO được viết SQL)
public class HoSoBenhAnDAO {

    private static final String CAC_COT = "maHoSo, maBN, ngayTao, tienSuBenh, canhBaoDiUng, nhomMau, benhNen, thuocDangSuDung, ghiChuDacBiet, trangThaiHoSo";

    // Tìm hồ sơ theo mã hồ sơ; không có thì trả về null
    public HoSoBenhAn timTheoMa(String maHoSo) {
        return timMot("SELECT " + CAC_COT + " FROM dbo.HoSoBenhAn WHERE maHoSo = ?", maHoSo);
    }

    // Tìm hồ sơ theo mã bệnh nhân (mỗi bệnh nhân một hồ sơ); không có thì trả về null
    public HoSoBenhAn timTheoBenhNhan(String maBN) {
        return timMot("SELECT " + CAC_COT + " FROM dbo.HoSoBenhAn WHERE maBN = ?", maBN);
    }

    // Thêm hồ sơ mới bằng kết nối riêng
    public void them(HoSoBenhAn hs) {
        try (Connection c = DatabaseConnection.getConnection()) {
            them(c, hs);
        } catch (SQLException e) {
            throw new LoiCSDL("Không thêm được hồ sơ bệnh án: " + e.getMessage(), e);
        }
    }

    // Thêm hồ sơ mới bằng kết nối có sẵn (để BenhNhanDAO.themKemHoSo tạo bệnh nhân và hồ sơ cùng một transaction)
    public void them(Connection c, HoSoBenhAn hs) throws SQLException {
        String sql = "INSERT INTO dbo.HoSoBenhAn (maHoSo, maBN, ngayTao, tienSuBenh, canhBaoDiUng, nhomMau, benhNen, thuocDangSuDung, ghiChuDacBiet, trangThaiHoSo)"
                + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, hs.getMaHoSo());
            ps.setString(2, hs.getMaBN());
            ps.setObject(3, hs.getNgayTao());
            ps.setString(4, hs.getTienSuBenh());
            ps.setString(5, hs.getCanhBaoDiUng());
            ps.setString(6, hs.getNhomMau());
            ps.setString(7, hs.getBenhNen());
            ps.setString(8, hs.getThuocDangSuDung());
            ps.setString(9, hs.getGhiChuDacBiet());
            ps.setString(10, hs.getTrangThaiHoSo());
            ps.executeUpdate();
        }
    }

    // Cập nhật tiền sử bệnh, dị ứng và các thông tin y tế của hồ sơ
    public void capNhatThongTinYTe(HoSoBenhAn hs) {
        String sql = "UPDATE dbo.HoSoBenhAn SET tienSuBenh = ?, canhBaoDiUng = ?, nhomMau = ?, benhNen = ?, thuocDangSuDung = ?, ghiChuDacBiet = ? WHERE maHoSo = ?";
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, hs.getTienSuBenh());
            ps.setString(2, hs.getCanhBaoDiUng());
            ps.setString(3, hs.getNhomMau());
            ps.setString(4, hs.getBenhNen());
            ps.setString(5, hs.getThuocDangSuDung());
            ps.setString(6, hs.getGhiChuDacBiet());
            ps.setString(7, hs.getMaHoSo());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new LoiCSDL("Không cập nhật được hồ sơ bệnh án: " + e.getMessage(), e);
        }
    }

    // Đổi trạng thái hồ sơ (Đang mở / Đã đóng)
    public void capNhatTrangThaiHoSo(String maHoSo, String trangThai) {
        try (Connection c = DatabaseConnection.getConnection();
                PreparedStatement ps = c.prepareStatement("UPDATE dbo.HoSoBenhAn SET trangThaiHoSo = ? WHERE maHoSo = ?")) {
            ps.setString(1, trangThai);
            ps.setString(2, maHoSo);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new LoiCSDL("Không đổi được trạng thái hồ sơ: " + e.getMessage(), e);
        }
    }

    // Chạy câu SELECT có một tham số và đọc dòng đầu tiên
    private HoSoBenhAn timMot(String sql, String giaTri) {
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, giaTri);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? doc(rs) : null;
            }
        } catch (SQLException e) {
            throw new LoiCSDL("Không đọc được hồ sơ bệnh án: " + e.getMessage(), e);
        }
    }

    // Đổi một dòng kết quả thành đối tượng HoSoBenhAn
    private HoSoBenhAn doc(ResultSet rs) throws SQLException {
        HoSoBenhAn hs = new HoSoBenhAn();
        hs.setMaHoSo(rs.getString("maHoSo"));
        hs.setMaBN(rs.getString("maBN"));
        hs.setNgayTao(rs.getObject("ngayTao", LocalDate.class));
        hs.setTienSuBenh(rs.getString("tienSuBenh"));
        hs.setCanhBaoDiUng(rs.getString("canhBaoDiUng"));
        hs.setNhomMau(rs.getString("nhomMau"));
        hs.setBenhNen(rs.getString("benhNen"));
        hs.setThuocDangSuDung(rs.getString("thuocDangSuDung"));
        hs.setGhiChuDacBiet(rs.getString("ghiChuDacBiet"));
        hs.setTrangThaiHoSo(rs.getString("trangThaiHoSo"));
        return hs;
    }
}
