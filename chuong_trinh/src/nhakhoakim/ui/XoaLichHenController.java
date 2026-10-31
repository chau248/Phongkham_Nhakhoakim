package nhakhoakim.ui;

import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import nhakhoakim.entity.LichHen;

// Điều khiển nội dung xác nhận hủy lịch trong UI_XoaLichHen.fxml.
public class XoaLichHenController {

    @FXML private Label lblThongTinLich;
    @FXML private TextArea txtLyDo;

    public void datLichHen(LichHen lh) {
        lblThongTinLich.setText("Mã lịch: " + lh.getMaLichHen()
                + "\nThời gian: " + TienIch.ngay(lh.getNgayHen()) + ", " + TienIch.gio(lh.getGioHen())
                + "\nBệnh nhân: " + TienIch.chu(lh.getTenBenhNhan())
                + " - SĐT: " + TienIch.chu(lh.getSdtBenhNhan())
                + "\nDịch vụ: " + TienIch.chu(lh.getTenDichVu()));
    }

    public StringProperty lyDoProperty() {
        return txtLyDo.textProperty();
    }

    public String layLyDo() {
        return txtLyDo.getText();
    }
}
