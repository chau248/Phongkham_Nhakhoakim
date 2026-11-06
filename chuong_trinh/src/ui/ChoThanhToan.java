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
import controller.HoaDonController.DongChoThanhToan;
import controller.HoaDonController;
import entity.ChiTietHoaDon;
import entity.HoaDon;
import nhakhoakim.MainApp;

// Màn hình danh sách chờ thanh toán
@SuppressWarnings("serial") // Lớp giao diện Swing
public class ChoThanhToan extends JPanel implements MainApp.LamMoiDuoc {

    private HoaDonController hoaDonController = new HoaDonController();

    private JTable tblCho = TienIch.taoBang(new String[] {"Lượt khám", "Bệnh nhân", "Nha sĩ", "Khám xong lúc", "Tạm tính (gồm VAT)"},
            new int[] {100, 220, 200, 140, 150}, "Không có lượt khám nào đang chờ thanh toán.");
    private List<DongChoThanhToan> dsCho = new ArrayList<>();
    private JLabel lblKetQua = new JLabel();

    public ChoThanhToan() {
        JButton btnLap = TienIch.nutChinh("Lập hóa đơn cho lượt khám đã chọn");
        btnLap.addActionListener(e -> lap());
        JButton btnTai = TienIch.nutPhu("Tải lại");
        btnTai.addActionListener(e -> tai());
        lblKetQua.setFont(TienIch.FONT);
        lblKetQua.setForeground(TienIch.MAU_XAM);
        TienIch.xepManHinh(this, TienIch.tieuDe("Danh sách chờ thanh toán"),
                TienIch.doanChu("Các lượt khám đã hoàn tất, chưa có hóa đơn. Chọn một lượt khám rồi bấm Lập hóa đơn.", TienIch.MAU_XAM),
                lblKetQua, TienIch.cuonBang(tblCho, 340), TienIch.dong(btnLap, btnTai));
        tai();
    }

    @Override
    public void lamMoi() {
        tai();
    }

    // Tải danh sách lượt khám
    private void tai() {
        try {
            dsCho = hoaDonController.layChoThanhToan();
            List<String[]> dong = new ArrayList<>();
            for (DongChoThanhToan d : dsCho) {
                dong.add(new String[] {d.luotKham.getMaLuotKham(), d.maBN + " - " + d.tenBenhNhan, d.tenNhaSi, TienIch.ngayGio(d.luotKham.getGioKetThuc()),
                    HoaDonController.dinhDangTien(d.tamTinh)});
            }
            TienIch.napBang(tblCho, dong);
            lblKetQua.setText(dong.size() + " lượt khám đang chờ thanh toán.");
        } catch (RuntimeException e) {
            TienIch.baoLoi(e);
        }
    }

    // Mở màn hình lập hóa đơn
    private void lap() {
        int vt = tblCho.getSelectedRow();
        if (vt < 0 || vt >= dsCho.size()) {
            TienIch.thongTin("Vui lòng chọn một lượt khám trong bảng.");
            return;
        }
        MainApp.doiNoiDung(new LapHoaDon(dsCho.get(vt).luotKham.getMaLuotKham()));
    }

    // Màn hình lập hóa đơn
    @SuppressWarnings("serial") // Lớp giao diện Swing
    public static class LapHoaDon extends JPanel {

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
                BigDecimal tongDichVu = hd.getTongTienDichVu();
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
}
