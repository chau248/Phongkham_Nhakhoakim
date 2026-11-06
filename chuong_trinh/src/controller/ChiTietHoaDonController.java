package controller;

import java.util.List;
import dao.ChiTietHoaDonDAO;
import entity.ChiTietHoaDon;

public class ChiTietHoaDonController {

    private ChiTietHoaDonDAO dao = new ChiTietHoaDonDAO();

    public List<ChiTietHoaDon> layTheoHoaDon(String maHoaDon) {
        return dao.layTheoHoaDon(maHoaDon);
    }
}
