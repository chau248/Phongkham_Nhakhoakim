package nhakhoakim.ui;

import javax.swing.JLabel;
import nhakhoakim.entity.LuotKham;

// Dòng thông tin ở đầu các màn hình làm việc trên lượt khám đang khám: bệnh nhân, nha sĩ phụ trách, mã lượt khám, giờ bắt đầu
// (để biết ngay đang thao tác trên lượt khám của ai, do nha sĩ nào phụ trách)
class DongThongTinLuotKham {

    // Dựng dòng thông tin; mã nha sĩ được đưa vào tooltip để ai cần thì rê chuột xem
    static JLabel tao(String tenBenhNhan, String tenNhaSi, LuotKham luotKham) {
        JLabel lbl = TienIch.chuThuong("Bệnh nhân: " + tenBenhNhan + "   |   Nha sĩ phụ trách: " + tenNhaSi + "   |   Lượt khám "
                + luotKham.getMaLuotKham() + "   |   Bắt đầu " + TienIch.ngayGio(luotKham.getGioBatDau()), true);
        lbl.setToolTipText("Mã nha sĩ: " + luotKham.getMaNhanVienPhuTrach());
        return lbl;
    }
}
