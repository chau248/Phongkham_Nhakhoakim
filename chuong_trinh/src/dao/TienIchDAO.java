package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// Hàm dùng chung cho các DAO: chạy SQL, giao dịch, sinh mã, ghi nhật ký
public class TienIchDAO {

    // Cách đọc một dòng kết quả thành đối tượng
    public interface DocDong<T> {
        T doc(ResultSet rs) throws SQLException;
    }

    // Việc cần làm trong một giao dịch
    public interface ViecGiaoDich {
        void chay(Connection c) throws SQLException;
    }

    // ----- Chạy SQL -----

    // SELECT nhiều dòng
    public static <T> List<T> layDanhSach(String sql, DocDong<T> docDong, Object... thamSo) {
        List<T> ds = new ArrayList<>();
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            datThamSo(ps, thamSo);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ds.add(docDong.doc(rs));
                }
            }
        } catch (SQLException e) {
            throw new LoiCSDL("Lỗi đọc dữ liệu: " + e.getMessage(), e);
        }
        return ds;
    }

    // SELECT một dòng, không có thì trả về null
    public static <T> T layMot(String sql, DocDong<T> docDong, Object... thamSo) {
        List<T> ds = layDanhSach(sql, docDong, thamSo);
        return ds.isEmpty() ? null : ds.get(0);
    }

    // INSERT, UPDATE, DELETE; trả về số dòng bị tác động
    public static int thucThi(String sql, Object... thamSo) {
        try (Connection c = DatabaseConnection.getConnection()) {
            return thucThi(c, sql, thamSo);
        } catch (SQLException e) {
            throw new LoiCSDL("Lỗi ghi dữ liệu: " + e.getMessage(), e);
        }
    }

    // Như trên nhưng dùng kết nối có sẵn (trong giao dịch)
    public static int thucThi(Connection c, String sql, Object... thamSo) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            datThamSo(ps, thamSo);
            return ps.executeUpdate();
        }
    }

    // Chạy nhiều lệnh trong một giao dịch: lỗi ở đâu thì hủy hết
    public static void giaoDich(ViecGiaoDich viec) {
        try (Connection c = DatabaseConnection.getConnection()) {
            c.setAutoCommit(false);
            try {
                viec.chay(c);
                c.commit();
            } catch (SQLException | RuntimeException e) {
                c.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new LoiCSDL("Lỗi ghi dữ liệu: " + e.getMessage(), e);
        }
    }

    // Gán tham số ? theo thứ tự
    private static void datThamSo(PreparedStatement ps, Object[] thamSo) throws SQLException {
        for (int i = 0; i < thamSo.length; i++) {
            ps.setObject(i + 1, thamSo[i]);
        }
    }

    // Danh sách cột kèm bí danh để nối bảng: cot(CAC_COT, "nv", "nv_") cho "nv.maNV AS nv_maNV, ..."
    public static String cot(String[] cacCot, String bang, String tienTo) {
        StringBuilder sb = new StringBuilder();
        for (String c : cacCot) {
            sb.append(sb.length() == 0 ? "" : ", ").append(bang).append('.').append(c).append(" AS ").append(tienTo).append(c);
        }
        return sb.toString();
    }

    // ----- Sinh mã -----

    // Mã mới dạng tiền tố + 6 chữ số (BN000001, HD000123...)
    public static String taoMa(String tienTo) {
        Integer so = layMot("UPDATE dbo.BoDemMa SET giaTriHienTai = giaTriHienTai + 1 OUTPUT INSERTED.giaTriHienTai WHERE tienTo = ?",
                rs -> rs.getInt(1), tienTo);
        if (so == null) {
            throw new NghiepVuException("Chưa có bộ đếm mã cho tiền tố " + tienTo);
        }
        return tienTo + String.format("%06d", so);
    }

    // ----- Nhật ký hệ thống -----

    // Ghi một dòng nhật ký (chuỗi quá dài thì cắt cho vừa cột)
    public static void ghiNhatKy(String maNV, String hanhDong, String doiTuong, String maDoiTuong, String chiTiet) {
        thucThi("INSERT INTO dbo.NhatKyHeThong (maNhanVien, hanhDong, doiTuong, maDoiTuong, chiTiet) VALUES (?, ?, ?, ?, ?)",
                maNV, cat(hanhDong, 50), cat(doiTuong, 50), cat(maDoiTuong, 30), cat(chiTiet, 500));
    }

    private static String cat(String s, int toiDa) {
        return s == null || s.length() <= toiDa ? s : s.substring(0, toiDa);
    }
}
