package nhakhoakim.controller;

import nhakhoakim.dao.NhatKyDAO;

// Ghi nhật ký các thao tác
public class NhatKyController {

    private NhatKyDAO dao = new NhatKyDAO();

    // Ghi một dòng nhật ký
    public void ghiNhatKy(String maNV, String hanhDong, String doiTuong, String maDoiTuong, String chiTiet) {
        try {
            dao.them(maNV, hanhDong, doiTuong, maDoiTuong, chiTiet);
        } catch (RuntimeException e) {
            System.err.println("Không ghi được nhật ký (" + hanhDong + " " + doiTuong + " " + maDoiTuong + "): " + e.getMessage());
        }
    }
}
