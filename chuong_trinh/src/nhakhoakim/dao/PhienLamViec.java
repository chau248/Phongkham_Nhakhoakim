package nhakhoakim.dao;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import nhakhoakim.entity.NhanVien;

// Người đang đăng nhập (dùng chung cho cả chương trình)
// Bản tạm do Hậu viết để chạy thử: lấy mã nhân viên từ dòng dev.maNhanVien trong config/database.properties
// (NV000002 nha sĩ, NV000004 y tá, NV000006 lễ tân, NV000001 quản lý), không có dòng đó thì dùng NV000002.
// Khi có đăng nhập thật, Tâm gọi datNhanVien(...) sau khi đăng nhập thành công (giữ nguyên tên hàm getNhanVienHienTai).
public class PhienLamViec {

    private static NhanVien nhanVienHienTai; // người đang đăng nhập

    // Lấy người đang đăng nhập; chưa có ai thì dùng nhân viên mặc định trong cấu hình
    public static NhanVien getNhanVienHienTai() {
        if (nhanVienHienTai == null) {
            nhanVienHienTai = new NhanVienService().timTheoMa(docMaMacDinh());
        }
        return nhanVienHienTai;
    }

    // Đặt người đăng nhập (gọi sau khi đăng nhập thành công)
    public static void datNhanVien(NhanVien nv) {
        nhanVienHienTai = nv;
    }

    // Đăng xuất
    public static void dangXuat() {
        nhanVienHienTai = null;
    }

    // Đọc mã nhân viên mặc định từ file cấu hình; không đọc được thì dùng nha sĩ NV000002
    private static String docMaMacDinh() {
        Properties p = new Properties();
        try (InputStreamReader in = new InputStreamReader(new FileInputStream("config/database.properties"), StandardCharsets.UTF_8)) {
            p.load(in);
        } catch (IOException e) {
            return "NV000002";
        }
        return p.getProperty("dev.maNhanVien", "NV000002").trim();
    }
}
