package nhakhoakim.dao;

import nhakhoakim.entity.NhanVien;

// Người đang đăng nhập
public class PhienLamViec {

    private static NhanVien nhanVienHienTai; // Người đang đăng nhập

    // Lấy người đang đăng nhập
    public static NhanVien getNhanVienHienTai() {
        return nhanVienHienTai;
    }

    // Đặt người đăng nhập
    public static void datNhanVien(NhanVien nv) {
        nhanVienHienTai = nv;
    }

    // Đăng xuất
    public static void dangXuat() {
        nhanVienHienTai = null;
    }
}
