package ui;

import javax.swing.JLabel;
import entity.LuotKham;

// Dòng thông tin ở đầu
class DongThongTinLuotKham {

    // Dựng dòng thông tin; mã nha sĩ để trong tooltip cho ai cần thì rê chuột xem
    static JLabel tao(LuotKham luotKham) {
        JLabel lbl = TienIch.chuThuong("Bệnh nhân: " + luotKham.getMaHoSo().getMaBN().getHoTen() + "   |   Nha sĩ phụ trách: "
                + luotKham.getMaNhanVienPhuTrach().getHoTen() + "   |   Lượt khám " + luotKham.getMaLuotKham()
                + "   |   Bắt đầu " + TienIch.ngayGio(luotKham.getGioBatDau()), true);
        lbl.setToolTipText("Mã nha sĩ: " + luotKham.getMaNhanVienPhuTrach().getMaNV());
        return lbl;
    }
}
