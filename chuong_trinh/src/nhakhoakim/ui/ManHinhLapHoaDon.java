package nhakhoakim.ui;

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
import nhakhoakim.controller.HoaDonController;
import nhakhoakim.entity.ChiTietHoaDon;
import nhakhoakim.entity.HoaDon;

// Màn hình lập hóa đơn (HD-02 đến HD-04) cho một lượt khám đã hoàn thành: các dòng lấy từ dịch vụ đã thực hiện,
// lễ tân chỉ nhập giảm giá và ghi chú; tổng tiền = dịch vụ - giảm giá + VAT 8%
@SuppressWarnings("serial") // lớp giao diện Swing, không cần serialVersionUID
public class ManHinhLapHoaDon extends JPanel {

    private HoaDonController hoaDonController = new HoaDonController();
    private BangTen ten = new BangTen();
    private String maLuotKham;

    private JTable tblChiTiet = TienIch.taoBang(new String[] {"Dịch vụ", "Răng", "Số lượng", "Đơn giá", "Thành tiền"},
            new int[] {280, 70, 80, 120, 130}, "Lượt khám chưa ghi dịch vụ nào.");
    private JTextField txtGiamGia = TienIch.oNhap(12);              // số tiền giảm (đồng)
    private JTextField txtGhiChu = TienIch.oNhap(40);
    private JLabel lblTongDichVu = TienIch.chuThuong(" ", false);
    private JLabel lblTruocThue = TienIch.chuThuong(" ", false);
    private JLabel lblThue = TienIch.chuThuong(" ", false);
    private JLabel lblTong = TienIch.chuThuong(" ", true);

    public ManHinhLapHoaDon(String maLuotKham) {
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

    // Đọc số tiền giảm giá người dùng gõ (bỏ dấu chấm ngăn cách); ô trống là 0
    private BigDecimal docGiamGia() {
        String chu = txtGiamGia.getText().trim().replace(".", "").replace(",", "");
        if (chu.isEmpty()) {
            return BigDecimal.ZERO;
        }
        try {
            return new BigDecimal(chu);
        } catch (NumberFormatException e) {
            throw new nhakhoakim.dao.NghiepVuException("Giảm giá phải là số tiền (đồng), ví dụ 50000");
        }
    }

    // Tính hóa đơn tạm với giảm giá đang nhập và hiện lên bảng
    private void tinh() {
        try {
            HoaDon hd = hoaDonController.xemTruoc(maLuotKham, docGiamGia());
            List<String[]> dong = new ArrayList<>();
            for (ChiTietHoaDon ct : hd.getDsChiTiet()) {
                BigDecimal thanhTien = ct.getDonGia().multiply(BigDecimal.valueOf(ct.getSoLuong()));
                dong.add(new String[] {ten.dichVu(ct.getMaDichVu()), ct.getSoFDIRang() > 0 ? String.valueOf(ct.getSoFDIRang()) : "", String.valueOf(ct.getSoLuong()),
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

    // Lập hóa đơn rồi chuyển sang màn hình thanh toán
    private void lap() {
        try {
            HoaDon hd = hoaDonController.lapHoaDon(maLuotKham, docGiamGia(), txtGhiChu.getText());
            TienIch.thongTin("Đã lập hóa đơn " + hd.getMaHoaDon() + ", tổng " + HoaDonController.dinhDangTien(hd.getTongTien()) + " đồng. Chuyển sang thanh toán.");
            MainApp.thayNoiDung(new ManHinhThanhToan(hd.getMaHoaDon()));
        } catch (RuntimeException e) {
            TienIch.baoLoi(e);
        }
    }
}
