package dao;

import java.util.ArrayList;
import java.util.List;
import entity.DanhMuc;

// Truy cập cấu hình hệ thống: danh mục
public class CauHinhDAO {

    // Tất cả tên trong một danh mục
    public List<String> layTatCa(DanhMuc dm) {
        return TienIchDAO.layDanhSach("SELECT " + dm.getCot() + " FROM dbo." + dm.getBang() + " ORDER BY " + dm.getCot(), rs -> rs.getString(1));
    }

    // Các phòng dùng
    public List<String> layPhongKham() {
        return TienIchDAO.layDanhSach("SELECT tenPhong FROM dbo.Phong WHERE laPhongKham = 1 ORDER BY tenPhong", rs -> rs.getString(1));
    }

    // Phòng có phải phòng khám không
    public boolean laPhongKham(String tenPhong) {
        Boolean kq = TienIchDAO.layMot("SELECT laPhongKham FROM dbo.Phong WHERE tenPhong = ?", rs -> rs.getBoolean(1), tenPhong);
        return kq != null && kq;
    }

    // Tên đã có trong danh mục chưa
    public boolean daCo(DanhMuc dm, String ten) {
        return TienIchDAO.layMot("SELECT 1 FROM dbo." + dm.getBang() + " WHERE " + dm.getCot() + " = ?", rs -> 1, ten) != null;
    }

    // Số nơi đang dùng tên này
    public int demDangDung(DanhMuc dm, String ten) {
        Object[] tham = dm.getSqlDem().chars().filter(c -> c == '?').count() == 2 ? new Object[] {ten, ten} : new Object[] {ten};
        Integer so = TienIchDAO.layMot(dm.getSqlDem(), rs -> rs.getInt(1), tham);
        return so == null ? 0 : so;
    }

    public void them(DanhMuc dm, String ten) {
        TienIchDAO.thucThi("INSERT INTO dbo." + dm.getBang() + " (" + dm.getCot() + ") VALUES (?)", ten);
    }

    // Thêm phòng, kèm cờ phòng khám
    public void themPhong(String ten, boolean laPhongKham) {
        TienIchDAO.thucThi("INSERT INTO dbo.Phong (tenPhong, laPhongKham) VALUES (?, ?)", ten, laPhongKham);
    }

    public void doiTen(DanhMuc dm, String tenCu, String tenMoi) {
        TienIchDAO.thucThi("UPDATE dbo." + dm.getBang() + " SET " + dm.getCot() + " = ? WHERE " + dm.getCot() + " = ?", tenMoi, tenCu);
    }

    public void xoa(DanhMuc dm, String ten) {
        TienIchDAO.thucThi("DELETE FROM dbo." + dm.getBang() + " WHERE " + dm.getCot() + " = ?", ten);
    }

    // Tất cả vai trò: {tên
    public List<String[]> layVaiTro() {
        return TienIchDAO.layDanhSach("SELECT tenVaiTro, laVaiTroGoc, moTa FROM dbo.VaiTro ORDER BY laVaiTroGoc DESC, tenVaiTro",
                rs -> new String[] {rs.getString(1), rs.getBoolean(2) ? "1" : "0", rs.getString(3)});
    }

    public boolean laVaiTroGoc(String tenVaiTro) {
        Boolean kq = TienIchDAO.layMot("SELECT laVaiTroGoc FROM dbo.VaiTro WHERE tenVaiTro = ?", rs -> rs.getBoolean(1), tenVaiTro);
        return kq != null && kq;
    }

    public boolean coVaiTro(String tenVaiTro) {
        return TienIchDAO.layMot("SELECT 1 FROM dbo.VaiTro WHERE tenVaiTro = ?", rs -> 1, tenVaiTro) != null;
    }

    public void themVaiTro(String tenVaiTro, String moTa) {
        TienIchDAO.thucThi("INSERT INTO dbo.VaiTro (tenVaiTro, laVaiTroGoc, moTa) VALUES (?, 0, ?)", tenVaiTro, moTa);
    }

    // Số nhân viên và tài khoản đang mang vai trò này
    public int demDangDungVaiTro(String tenVaiTro) {
        Integer so = TienIchDAO.layMot("SELECT (SELECT COUNT(*) FROM dbo.NhanVien WHERE vaiTro = ?) + (SELECT COUNT(*) FROM dbo.TaiKhoan WHERE vaiTro = ?)",
                rs -> rs.getInt(1), tenVaiTro, tenVaiTro);
        return so == null ? 0 : so;
    }

    public void xoaVaiTro(String tenVaiTro) {
        TienIchDAO.thucThi("DELETE FROM dbo.VaiTro WHERE tenVaiTro = ?", tenVaiTro);
    }

    // Mọi chức năng: {nhóm
    public List<String[]> layChucNang() {
        return TienIchDAO.layDanhSach("SELECT nhom, tenChucNang FROM dbo.ChucNang ORDER BY thuTu", rs -> new String[] {rs.getString(1), rs.getString(2)});
    }

    // Chức năng của một vai trò: {nhóm
    public List<String[]> layChucNangCuaVaiTro(String tenVaiTro) {
        return TienIchDAO.layDanhSach("SELECT c.nhom, c.tenChucNang FROM dbo.VaiTroChucNang q JOIN dbo.ChucNang c ON c.tenChucNang = q.tenChucNang"
                + " WHERE q.tenVaiTro = ? ORDER BY c.thuTu", rs -> new String[] {rs.getString(1), rs.getString(2)}, tenVaiTro);
    }

    // Thay toàn bộ quyền của vai trò bằng danh sách mới
    public void datChucNangChoVaiTro(String tenVaiTro, List<String> dsChucNang) {
        TienIchDAO.giaoDich(c -> {
            TienIchDAO.thucThi(c, "DELETE FROM dbo.VaiTroChucNang WHERE tenVaiTro = ?", tenVaiTro);
            for (String cn : new ArrayList<>(dsChucNang)) {
                TienIchDAO.thucThi(c, "INSERT INTO dbo.VaiTroChucNang (tenVaiTro, tenChucNang) VALUES (?, ?)", tenVaiTro, cn);
            }
        });
    }

    // Giá trị một tham số
    public String layThamSo(String ten) {
        return TienIchDAO.layMot("SELECT giaTri FROM dbo.ThamSo WHERE tenThamSo = ?", rs -> rs.getString(1), ten);
    }

    // Lưu giá trị tham số
    public void datThamSo(String ten, String giaTri) {
        int so = TienIchDAO.thucThi("UPDATE dbo.ThamSo SET giaTri = ? WHERE tenThamSo = ?", giaTri, ten);
        if (so == 0) {
            TienIchDAO.thucThi("INSERT INTO dbo.ThamSo (tenThamSo, giaTri) VALUES (?, ?)", ten, giaTri);
        }
    }
}
