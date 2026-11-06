package dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import entity.BenhNhan;
import entity.HangSo;
import entity.HoSoBenhAn;

// Truy cập bảng BenhNhan
public class BenhNhanDAO {

    static final String[] COT = {"maBN", "hoTen", "ngaySinh", "gioiTinh", "sdt", "diaChi", "email", "soCCCD", "nguoiLienHeKhanCap",
        "sdtNguoiLienHe", "ngayDangKy", "trangThai", "ghiChu"};
    static final String CAC_COT = String.join(", ", COT);
    // Cột để nối vào câu SELECT của bảng khác (bí danh bn, tên cột có tiền tố bn_)
    static final String COT_NOI = TienIchDAO.cot(COT, "bn", "bn_");

    // ----- Tìm -----

    // Tìm bệnh nhân theo mã, không có thì null
    public BenhNhan timTheoMa(String maBN) {
        return TienIchDAO.layMot("SELECT " + CAC_COT + " FROM dbo.BenhNhan WHERE maBN = ?", rs -> doc(rs, ""), maBN);
    }

    // Tìm bệnh nhân theo số căn cước
    public BenhNhan timTheoCCCD(String soCCCD) {
        return TienIchDAO.layMot("SELECT " + CAC_COT + " FROM dbo.BenhNhan WHERE soCCCD = ?", rs -> doc(rs, ""), soCCCD);
    }

    // Tìm bệnh nhân đang điều trị theo mã, họ tên hoặc số điện thoại
    public List<BenhNhan> timKiem(String tuKhoa, int toiDa) {
        return timKiem(tuKhoa, toiDa, false);
    }

    // Như trên; gomNgung = true thì lấy cả bệnh nhân đã ngừng theo dõi
    public List<BenhNhan> timKiem(String tuKhoa, int toiDa, boolean gomNgung) {
        String mau = "%" + tuKhoa + "%";
        String loc = gomNgung ? "" : "trangThai = N'" + HangSo.DANG_DIEU_TRI + "' AND ";
        return TienIchDAO.layDanhSach("SELECT TOP (?) " + CAC_COT + " FROM dbo.BenhNhan WHERE " + loc
                + "(maBN LIKE ? OR hoTen LIKE ? OR sdt LIKE ?) ORDER BY hoTen", rs -> doc(rs, ""), toiDa, mau, mau, mau);
    }

    // Mọi bệnh nhân (kể cả đã ngừng theo dõi) trùng họ tên và ngày sinh
    public List<BenhNhan> timTheoHoTenNgaySinh(String hoTen, LocalDate ngaySinh) {
        return TienIchDAO.layDanhSach("SELECT " + CAC_COT + " FROM dbo.BenhNhan WHERE hoTen = ? AND ngaySinh = ?",
                rs -> doc(rs, ""), hoTen, ngaySinh);
    }

    // ----- Thêm, sửa -----

    // Thêm bệnh nhân và hồ sơ bệnh án rỗng trong một giao dịch
    public void themKemHoSo(BenhNhan bn, HoSoBenhAn hs) {
        TienIchDAO.giaoDich(c -> {
            TienIchDAO.thucThi(c, "INSERT INTO dbo.BenhNhan (" + CAC_COT + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                    bn.getMaBN(), bn.getHoTen(), bn.getNgaySinh(), bn.getGioiTinh(), bn.getSdt(), bn.getDiaChi(), bn.getEmail(),
                    bn.getSoCCCD(), bn.getNguoiLienHeKhanCap(), bn.getSdtNguoiLienHe(), bn.getNgayDangKy(), bn.getTrangThai(),
                    bn.getGhiChu());
            HoSoBenhAnDAO.them(c, hs);
        });
    }

    // Cập nhật thông tin hành chính
    public void capNhat(BenhNhan bn) {
        TienIchDAO.thucThi("UPDATE dbo.BenhNhan SET hoTen = ?, ngaySinh = ?, gioiTinh = ?, sdt = ?, diaChi = ?, email = ?, soCCCD = ?,"
                + " nguoiLienHeKhanCap = ?, sdtNguoiLienHe = ?, ghiChu = ? WHERE maBN = ?",
                bn.getHoTen(), bn.getNgaySinh(), bn.getGioiTinh(), bn.getSdt(), bn.getDiaChi(), bn.getEmail(), bn.getSoCCCD(),
                bn.getNguoiLienHeKhanCap(), bn.getSdtNguoiLienHe(), bn.getGhiChu(), bn.getMaBN());
    }

    // Đổi trạng thái (Đang điều trị / Ngừng theo dõi)
    public void capNhatTrangThai(String maBN, String trangThai) {
        TienIchDAO.thucThi("UPDATE dbo.BenhNhan SET trangThai = ? WHERE maBN = ?", trangThai, maBN);
    }

    // ----- Đọc dòng kết quả -----

    // Đọc bệnh nhân với tiền tố cột ("" nếu đọc từ bảng BenhNhan, "bn_" nếu nối từ bảng khác)
    static BenhNhan doc(ResultSet rs, String tienTo) throws SQLException {
        BenhNhan bn = new BenhNhan();
        bn.setMaBN(rs.getString(tienTo + "maBN"));
        bn.setHoTen(rs.getString(tienTo + "hoTen"));
        bn.setNgaySinh(rs.getObject(tienTo + "ngaySinh", LocalDate.class));
        bn.setGioiTinh(rs.getString(tienTo + "gioiTinh"));
        bn.setSdt(rs.getString(tienTo + "sdt"));
        bn.setDiaChi(rs.getString(tienTo + "diaChi"));
        bn.setEmail(rs.getString(tienTo + "email"));
        bn.setSoCCCD(rs.getString(tienTo + "soCCCD"));
        bn.setNguoiLienHeKhanCap(rs.getString(tienTo + "nguoiLienHeKhanCap"));
        bn.setSdtNguoiLienHe(rs.getString(tienTo + "sdtNguoiLienHe"));
        bn.setNgayDangKy(rs.getObject(tienTo + "ngayDangKy", LocalDate.class));
        bn.setTrangThai(rs.getString(tienTo + "trangThai"));
        bn.setGhiChu(rs.getString(tienTo + "ghiChu"));
        return bn;
    }

    // Đọc bệnh nhân nối từ bảng khác
    static BenhNhan docNoi(ResultSet rs) throws SQLException {
        return doc(rs, "bn_");
    }
}
