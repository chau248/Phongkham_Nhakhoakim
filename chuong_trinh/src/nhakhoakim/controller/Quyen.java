package nhakhoakim.controller;

import java.util.function.Supplier;
import nhakhoakim.dao.NghiepVuException;
import nhakhoakim.entity.NhanVien;

// Kiểm tra người đang đăng nhập có đúng vai trò
final class Quyen {

    private Quyen() {
    }

    // Trả về người đang đăng nhập
    static NhanVien yeuCau(Supplier<NhanVien> nguoiDung, String viec, String... vaiTroDuocPhep) {
        NhanVien nv = nguoiDung.get();
        if (nv == null) {
            throw new NghiepVuException("Chưa đăng nhập");
        }
        for (String vt : vaiTroDuocPhep) {
            if (vt.equals(nv.getVaiTro())) {
                return nv;
            }
        }
        throw new NghiepVuException("Chỉ " + String.join(" hoặc ", vaiTroDuocPhep).toLowerCase() + " mới được " + viec);
    }
}
