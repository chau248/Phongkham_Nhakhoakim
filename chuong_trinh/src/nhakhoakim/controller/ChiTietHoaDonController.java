package nhakhoakim.controller;

import java.util.List;
import nhakhoakim.dao.ChiTietHoaDonDAO;
import nhakhoakim.entity.ChiTietHoaDon;

// Dòng chi tiết của hóa đơn (chỉ đọc). Dòng chi tiết được tạo cùng hóa đơn trong HoaDonController.lapHoaDon.
public class ChiTietHoaDonController {

    private ChiTietHoaDonDAO dao = new ChiTietHoaDonDAO();

    // Các dòng chi tiết của một hóa đơn
    public List<ChiTietHoaDon> layTheoHoaDon(String maHoaDon) {
        return dao.layTheoHoaDon(maHoaDon);
    }
}
