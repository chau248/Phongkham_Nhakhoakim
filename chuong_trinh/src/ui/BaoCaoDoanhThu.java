package ui;

import ui.ThanhPhan.ChonNgay;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import controller.HoaDonController;

// Báo cáo doanh thu: các hóa đơn đã thanh toán trong khoảng ngày
@SuppressWarnings("serial") // Lớp giao diện Swing
public class BaoCaoDoanhThu extends JPanel {

    private HoaDonController hoaDonController = new HoaDonController();

    private ChonNgay txtTu = new ChonNgay();
    private ChonNgay txtDen = new ChonNgay();
    private JLabel lblTong = new JLabel(" ");
    private JTable tblNgay = TienIch.taoBang(new String[] {"Ngày", "Số hóa đơn", "Doanh thu (đồng)"}, new int[] {110, 90, 140}, "Không có hóa đơn nào trong khoảng này.");
    private JTable tblHinhThuc = TienIch.taoBang(new String[] {"Hình thức", "Số hóa đơn", "Doanh thu (đồng)"}, new int[] {110, 90, 140}, "Chưa có.");
    private JTable tblLoai = TienIch.taoBang(new String[] {"Loại dịch vụ", "Số lượng", "Tiền dịch vụ (đồng)"}, new int[] {110, 90, 140}, "Chưa có.");
    private JTable tblBanChay = TienIch.taoBang(new String[] {"Dịch vụ bán chạy", "Số lượng", "Tiền dịch vụ (đồng)"}, new int[] {160, 70, 140}, "Chưa có.");

    public BaoCaoDoanhThu() {
        LocalDate homNay = LocalDate.now();
        txtTu.setText(TienIch.ngay(homNay.withDayOfMonth(1)));
        txtDen.setText(TienIch.ngay(homNay));
        JButton btnXem = TienIch.nutChinh("Xem báo cáo");
        btnXem.addActionListener(e -> xem());
        JButton btn7 = TienIch.nutPhu("7 ngày qua");
        btn7.addActionListener(e -> {
            txtTu.setText(TienIch.ngay(LocalDate.now().minusDays(6)));
            txtDen.setText(TienIch.ngay(LocalDate.now()));
            xem();
        });
        JButton btnThang = TienIch.nutPhu("Tháng này");
        btnThang.addActionListener(e -> {
            txtTu.setText(TienIch.ngay(LocalDate.now().withDayOfMonth(1)));
            txtDen.setText(TienIch.ngay(LocalDate.now()));
            xem();
        });
        lblTong.setFont(TienIch.FONT.deriveFont(java.awt.Font.BOLD, 16f));
        lblTong.setForeground(TienIch.MAU_DAM);
        JPanel hai = new JPanel(new java.awt.GridLayout(1, 2, 14, 0));
        hai.setOpaque(false);
        hai.add(nhoLai(TienIch.cuonBang(tblHinhThuc, 130)));
        hai.add(nhoLai(TienIch.cuonBang(tblLoai, 130)));
        TienIch.xepManHinh(this, TienIch.tieuDe("Báo cáo doanh thu"),
                TienIch.doanChu("Chỉ tính hóa đơn đã thanh toán, theo ngày thanh toán. Doanh thu đã gồm thuế VAT; bảng loại dịch vụ và dịch vụ bán chạy là tiền dịch vụ "
                        + "trước thuế và trước giảm giá cả hóa đơn.", TienIch.MAU_XAM),
                TienIch.dong(TienIch.nhan("Từ ngày"), txtTu, TienIch.nhan("đến"), txtDen, btnXem, btn7, btnThang),
                lblTong, TienIch.cuonBang(tblNgay, 200), hai, TienIch.cuonBang(tblBanChay, 150));
        xem();
    }

    // Bảng nằm cạnh nhau
    private javax.swing.JScrollPane nhoLai(javax.swing.JScrollPane cuon) {
        cuon.setPreferredSize(new java.awt.Dimension(420, cuon.getPreferredSize().height));
        return cuon;
    }

    private void xem() {
        try {
            HoaDonController.BaoCaoDoanhThu bc = hoaDonController.baoCaoDoanhThu(TienIch.docNgay(txtTu.getText()), TienIch.docNgay(txtDen.getText()));
            lblTong.setText("Tổng doanh thu: " + TienIch.tien(bc.tongDoanhThu) + " đồng (" + bc.soHoaDon + " hóa đơn"
                    + (bc.soHoaDon > 0 ? ", trung bình " + TienIch.tien(bc.tongDoanhThu / bc.soHoaDon) + " đồng/hóa đơn" : "") + ")");
            TienIch.napBang(tblNgay, doiDong(bc.theoNgay));
            TienIch.napBang(tblHinhThuc, doiDong(bc.theoHinhThuc));
            TienIch.napBang(tblLoai, doiDong(bc.theoLoaiDichVu));
            TienIch.napBang(tblBanChay, doiDong(bc.dichVuBanChay));
        } catch (RuntimeException e) {
            TienIch.baoLoi(e);
        }
    }

    // Đổi dòng {nhãn
    private List<String[]> doiDong(List<Object[]> dong) {
        List<String[]> kq = new ArrayList<>();
        for (Object[] d : dong) {
            String nhan = d[0] instanceof LocalDate ? TienIch.ngay((LocalDate) d[0]) : String.valueOf(d[0]);
            kq.add(new String[] {nhan, String.valueOf(d[1]), TienIch.tien((Long) d[2])});
        }
        return kq;
    }
}
