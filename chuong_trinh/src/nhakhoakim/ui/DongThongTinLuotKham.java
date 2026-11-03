package nhakhoakim.ui;

import javax.swing.JLabel;
import nhakhoakim.entity.LuotKham;

// Dòng thông tin ở đầu
class DongThongTinLuotKham {

    // Dựng dòng thông tin
    static JLabel tao(String tenBenhNhan, String tenNhaSi, LuotKham luotKham) {
        JLabel lbl = TienIch.chuThuong("Bệnh nhân: " + tenBenhNhan + "   |   Nha sĩ phụ trách: " + tenNhaSi + "   |   Lượt khám "
                + luotKham.getMaLuotKham() + "   |   Bắt đầu " + TienIch.ngayGio(luotKham.getGioBatDau()), true);
        lbl.setToolTipText("Mã nha sĩ: " + luotKham.getMaNhanVienPhuTrach());
        return lbl;
    }
}
