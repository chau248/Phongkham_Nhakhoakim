package nhakhoakim.controller;

import java.util.List;
import nhakhoakim.dao.ChiTietHoaDonDAO;
import nhakhoakim.entity.ChiTietHoaDon;

public class ChiTietHoaDonController {

    private ChiTietHoaDonDAO dao = new ChiTietHoaDonDAO();

    public List<ChiTietHoaDon> layTheoHoaDon(String maHoaDon) {
        return dao.layTheoHoaDon(maHoaDon);
    }
}
