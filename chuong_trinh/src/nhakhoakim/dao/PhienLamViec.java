package nhakhoakim.dao;

import nhakhoakim.entity.NhanVien;

// Người đang đăng nhập (dùng chung cho cả chương trình). Đăng nhập thành công thì màn hình đăng nhập gọi datNhanVien(...),
// đăng xuất thì gọi dangXuat(). Chưa đăng nhập thì getNhanVienHienTai() trả về null và các controller báo "Chưa đăng nhập".
public class PhienLamViec {

    private static NhanVien nhanVienHienTai; // người đang đăng nhập

    // Lấy người đang đăng nhập; chưa có ai thì trả về null
    public static NhanVien getNhanVienHienTai() {
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
}
