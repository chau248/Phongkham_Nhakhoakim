package ui;

import java.awt.Component;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import nhakhoakim.MainApp;
import controller.HoaDonController;
import entity.ChiTietHoaDon;
import entity.HoaDon;

// Màn hình lập hóa đơn
@SuppressWarnings("serial") // Lớp giao diện Swing
public class LapHoaDon extends JPanel {

    private HoaDonController hoaDonController = new HoaDonController();
    private String maLuotKham;

    private JTable tblChiTiet = TienIch.taoBang(new String[] {"Dịch vụ", "Răng", "Số lượng", "Đơn giá", "Thành tiền"},
            new int[] {280, 70, 80, 120, 130}, "Lượt khám chưa ghi dịch vụ nào.");
    private JTextField txtGiamGia = TienIch.oNhap(12); // Số tiền giảm
    private JTextField txtGhiChu = TienIch.oNhap(40);
    private JLabel lblTongDichVu = TienIch.chuThuong(" ", false);
    private JLabel lblTruocThue = TienIch.chuThuong(" ", false);
    private JLabel lblThue = TienIch.chuThuong(" ", false);
    private JLabel lblTong = TienIch.chuThuong(" ", true);

    public LapHoaDon(String maLuotKham) {
        this.maLuotKham = maLuotKham;
        txtGiamGia.setText("0");
        JButton btnXem = TienIch.nutPhu("Tính lại");
        btnXem.addActionListener(e -> tinh());
        txtGiamGia.addActionListener(e -> tinh());
        JButton btnLap = TienIch.nutChinh("Lập hóa đơn");
        btnLap.addActionListener(e -> lap());
        lblTong.setForeground(TienIch.MAU_DAM);
        TienIch.xepManHinh(this, TienIch.tieuDe("Lập hóa đơn"),
                TienIch.chuThuong("Lượt khám " + maLuotKham, true), TienIch.cuonBang(tblChiTiet, 200),
                TienIch.the(TienIch.taoForm(new String[] {"Giảm giá (đồng)", "Ghi chú"}, new Component[] {TienIch.dong(txtGiamGia, btnXem), txtGhiChu}),
                        lblTongDichVu, lblTruocThue, lblThue, lblTong, TienIch.dong(btnLap)));
        tinh();
    }

    // Đọc số tiền giảm giá người dùng gõ
    private BigDecimal docGiamGia() {
        String chu = txtGiamGia.getText().trim().replace(".", "").replace(",", "");
        if (chu.isEmpty()) {
            return BigDecimal.ZERO;
        }
        try {
            return new BigDecimal(chu);
        } catch (NumberFormatException e) {
            throw new dao.NghiepVuException("Giảm giá phải là số tiền (đồng), ví dụ 50000");
        }
    }

    // Tính hóa đơn tạm với giảm giá đang nhập
    private void tinh() {
        try {
            HoaDon hd = hoaDonController.xemTruoc(maLuotKham, docGiamGia());
            List<String[]> dong = new ArrayList<>();
            for (ChiTietHoaDon ct : hd.getDsChiTiet()) {
                BigDecimal thanhTien = ct.getDonGia().multiply(BigDecimal.valueOf(ct.getSoLuong()));
                dong.add(new String[] {ct.getMaDichVu().getTenDichVu(), ct.getSoFDIRang() > 0 ? String.valueOf(ct.getSoFDIRang()) : "", String.valueOf(ct.getSoLuong()),
                    HoaDonController.dinhDangTien(ct.getDonGia()), HoaDonController.dinhDangTien(thanhTien)});
            }
            TienIch.napBang(tblChiTiet, dong);
            BigDecimal tongDichVu = HoaDonController.tongDichVu(hd.getDsChiTiet());
            lblTongDichVu.setText("Cộng tiền dịch vụ: " + HoaDonController.dinhDangTien(tongDichVu) + " đồng");
            lblTruocThue.setText("Sau giảm giá (trước thuế): " + HoaDonController.dinhDangTien(hd.getTongTienTruocThue()) + " đồng");
            lblThue.setText("Thuế VAT " + hd.getPhanTramThue().toPlainString() + "%: "
                    + HoaDonController.dinhDangTien(hd.getTongTien().subtract(hd.getTongTienTruocThue())) + " đồng");
            lblTong.setText("TỔNG THANH TOÁN: " + HoaDonController.dinhDangTien(hd.getTongTien()) + " đồng");
        } catch (RuntimeException e) {
            TienIch.baoLoi(e);
        }
    }

    // Lập hóa đơn
    private void lap() {
        try {
            HoaDon hd = hoaDonController.lapHoaDon(maLuotKham, docGiamGia(), txtGhiChu.getText());
            TienIch.thongTin("Đã lập hóa đơn " + hd.getMaHoaDon() + ", tổng " + HoaDonController.dinhDangTien(hd.getTongTien()) + " đồng. Chuyển sang thanh toán.");
            MainApp.thayNoiDung(new ThanhToan(hd.getMaHoaDon()));
        } catch (RuntimeException e) {
            TienIch.baoLoi(e);
        }
    }
}
