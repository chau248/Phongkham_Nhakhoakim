package nhakhoakim.controller;

import nhakhoakim.dao.NhatKyDAO;

// Ghi nhật ký các thao tác nhạy cảm (sửa giá, sửa dị ứng, gán vai trò, hủy hóa đơn, hủy lịch...) vào bảng NhatKyHeThong (CH-03)
public class NhatKyController {

    private NhatKyDAO dao = new NhatKyDAO();

    // Ghi một dòng nhật ký. Ghi nhật ký lỗi thì chỉ báo ở Console, không làm hỏng thao tác chính người dùng vừa làm xong.
    public void ghiNhatKy(String maNV, String hanhDong, String doiTuong, String maDoiTuong, String chiTiet) {
        try {
            dao.them(maNV, hanhDong, doiTuong, maDoiTuong, chiTiet);
        } catch (RuntimeException e) {
            System.err.println("Không ghi được nhật ký (" + hanhDong + " " + doiTuong + " " + maDoiTuong + "): " + e.getMessage());
        }
    }
}
