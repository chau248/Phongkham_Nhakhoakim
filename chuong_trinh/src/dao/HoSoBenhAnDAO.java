package dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import entity.HoSoBenhAn;

// Truy cập bảng HoSoBenhAn
public class HoSoBenhAnDAO {

    static final String CAC_COT = "h.maHoSo, h.ngayTao, h.tienSuBenh, h.nhomMau, h.benhNen, h.thuocDangSuDung, h.ghiChuDacBiet, h.trangThaiHoSo";
    static final String SELECT = "SELECT " + CAC_COT + ", " + BenhNhanDAO.COT_NOI
            + " FROM dbo.HoSoBenhAn h JOIN dbo.BenhNhan bn ON bn.maBN = h.maBN";
    // Cột rút gọn để nối vào câu SELECT của bảng khác (bí danh h, tiền tố h_); khi đó phải nối cả bảng BenhNhan (bn)
    static final String COT_NOI = "h.maHoSo AS h_maHoSo, h.trangThaiHoSo AS h_trangThaiHoSo";

    // Tìm hồ sơ theo mã hồ sơ
    public HoSoBenhAn timTheoMa(String maHoSo) {
        return TienIchDAO.layMot(SELECT + " WHERE h.maHoSo = ?", this::doc, maHoSo);
    }

    // Tìm hồ sơ theo mã bệnh nhân
    public HoSoBenhAn timTheoBenhNhan(String maBN) {
        return TienIchDAO.layMot(SELECT + " WHERE h.maBN = ?", this::doc, maBN);
    }

    // Thêm hồ sơ mới
    public void them(HoSoBenhAn hs) {
        TienIchDAO.giaoDich(c -> them(c, hs));
    }

    // Thêm hồ sơ mới bằng kết nối có sẵn
    static void them(Connection c, HoSoBenhAn hs) throws SQLException {
        TienIchDAO.thucThi(c, "INSERT INTO dbo.HoSoBenhAn (maHoSo, maBN, ngayTao, tienSuBenh, nhomMau, benhNen, thuocDangSuDung,"
                + " ghiChuDacBiet, trangThaiHoSo) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                hs.getMaHoSo(), hs.getMaBN().getMaBN(), hs.getNgayTao(), hs.getTienSuBenh(), hs.getNhomMau(), hs.getBenhNen(),
                hs.getThuocDangSuDung(), hs.getGhiChuDacBiet(), hs.getTrangThaiHoSo());
    }

    // Cập nhật thông tin y tế
    public void capNhatThongTinYTe(HoSoBenhAn hs) {
        TienIchDAO.thucThi("UPDATE dbo.HoSoBenhAn SET tienSuBenh = ?, nhomMau = ?, benhNen = ?, thuocDangSuDung = ?,"
                + " ghiChuDacBiet = ? WHERE maHoSo = ?",
                hs.getTienSuBenh(), hs.getNhomMau(), hs.getBenhNen(), hs.getThuocDangSuDung(), hs.getGhiChuDacBiet(), hs.getMaHoSo());
    }

    // Đóng hoặc mở lại hồ sơ
    public void capNhatTrangThaiHoSo(String maHoSo, String trangThai) {
        TienIchDAO.thucThi("UPDATE dbo.HoSoBenhAn SET trangThaiHoSo = ? WHERE maHoSo = ?", trangThai, maHoSo);
    }

    private HoSoBenhAn doc(ResultSet rs) throws SQLException {
        HoSoBenhAn hs = new HoSoBenhAn();
        hs.setMaHoSo(rs.getString("maHoSo"));
        hs.setMaBN(BenhNhanDAO.docNoi(rs));
        hs.setNgayTao(rs.getObject("ngayTao", LocalDate.class));
        hs.setTienSuBenh(rs.getString("tienSuBenh"));
        hs.setNhomMau(rs.getString("nhomMau"));
        hs.setBenhNen(rs.getString("benhNen"));
        hs.setThuocDangSuDung(rs.getString("thuocDangSuDung"));
        hs.setGhiChuDacBiet(rs.getString("ghiChuDacBiet"));
        hs.setTrangThaiHoSo(rs.getString("trangThaiHoSo"));
        return hs;
    }

    // Hồ sơ rút gọn
    static HoSoBenhAn docNoi(ResultSet rs) throws SQLException {
        HoSoBenhAn hs = new HoSoBenhAn();
        hs.setMaHoSo(rs.getString("h_maHoSo"));
        hs.setTrangThaiHoSo(rs.getString("h_trangThaiHoSo"));
        hs.setMaBN(BenhNhanDAO.docNoi(rs));
        return hs;
    }
}
