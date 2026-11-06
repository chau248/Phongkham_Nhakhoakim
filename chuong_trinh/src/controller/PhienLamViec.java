package controller;

import entity.NhanVien;

// Người đang đăng nhập vào chương trình
public class PhienLamViec {

    private static NhanVien nhanVienHienTai;

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
