package ui;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import controller.CauHinhController;

// Thông tin phòng khám in trên hóa đơn và thuế VAT
@SuppressWarnings("serial") // Lớp giao diện Swing
public class ThongTinPhongKham extends JPanel {

    private CauHinhController cauHinhController = new CauHinhController();

    private JTextField txtTen = TienIch.oNhap(30);
    private JTextField txtDiaChi = TienIch.oNhap(30);
    private JTextField txtSdt = TienIch.oNhap(30);
    private JTextField txtMaSoThue = TienIch.oNhap(30);
    private JTextField txtVat = TienIch.oNhap(30);

    public ThongTinPhongKham() {
        JButton btnLuu = TienIch.nutChinh("Lưu");
        btnLuu.addActionListener(e -> luu());
        txtTen.setText(cauHinhController.layThamSo(CauHinhController.TEN_PHONG_KHAM));
        txtDiaChi.setText(cauHinhController.layThamSo(CauHinhController.DIA_CHI));
        txtSdt.setText(cauHinhController.layThamSo(CauHinhController.SO_DIEN_THOAI));
        txtMaSoThue.setText(cauHinhController.layThamSo(CauHinhController.MA_SO_THUE));
        txtVat.setText(cauHinhController.layThamSo(CauHinhController.PHAN_TRAM_VAT));
        TienIch.xepManHinh(this, TienIch.tieuDe("Thông tin phòng khám & thuế"),
                TienIch.doanChu("Các thông tin này in ở đầu hóa đơn. Thuế VAT áp dụng cho các hóa đơn lập từ lúc lưu (hóa đơn cũ giữ nguyên thuế đã tính).",
                        TienIch.MAU_XAM),
                TienIch.the(TienIch.taoForm(new String[] {"Tên phòng khám", "Địa chỉ", "Số điện thoại", "Mã số thuế", "Thuế VAT (%)"},
                        new java.awt.Component[] {txtTen, txtDiaChi, txtSdt, txtMaSoThue, txtVat}), TienIch.dong(btnLuu)));
    }

    private void luu() {
        try {
            cauHinhController.luuThongTin(txtTen.getText(), txtDiaChi.getText(), txtSdt.getText(), txtMaSoThue.getText(), txtVat.getText());
            TienIch.thongTin("Đã lưu thông tin phòng khám.");
        } catch (RuntimeException e) {
            TienIch.baoLoi(e);
        }
    }
}
