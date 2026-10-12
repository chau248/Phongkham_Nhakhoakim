package nhakhoakim.dao;

import java.time.LocalDateTime;

// Ghi nhật ký các thao tác nhạy cảm (sửa giá, sửa dị ứng, gán vai trò...)
// Bản tạm do Hậu viết để chạy thử: chỉ in ra màn hình Console.
// Tâm thay bằng bản ghi vào bảng NhatKyHeThong (giữ nguyên tên hàm ghiNhatKy).
public class NhatKyService {

    public void ghiNhatKy(String maNV, String hanhDong, String doiTuong, String maDoiTuong, String chiTiet) {
        System.out.println("[Nhật ký tạm] " + LocalDateTime.now() + " | " + maNV + " | " + hanhDong + " | "
                + doiTuong + " " + maDoiTuong + " | " + chiTiet);
    }
}
