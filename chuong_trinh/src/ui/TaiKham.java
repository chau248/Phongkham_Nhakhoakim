package ui;

import ui.ThanhPhan.ChonNgay;

import java.awt.Component;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextField;
import controller.DichVuController;
import controller.LuotKhamController;
import controller.PhienLamViec;
import entity.DichVu;
import entity.LuotKham;

// Màn hình chỉ định tái khám
@SuppressWarnings("serial") // Lớp giao diện Swing
public class TaiKham extends JPanel {

    private LuotKhamController luotKhamService = new LuotKhamController();
    private DichVuController dichVuService = new DichVuController();

    private LuotKham luotKham; // Lượt khám đang khám
    private ChonNgay txtNgay = new ChonNgay(); // Ngày tái khám dd/MM/yyyy
    private JComboBox<DichVu> cmbDichVu; // Dịch vụ của buổi tái khám
    private JButton btnHuy = TienIch.nutPhu("Hủy lịch tái khám đã tạo"); // Chỉ hiện khi lượt khám đã

    public TaiKham() {
        List<Component> cacKhoi = new ArrayList<>();
        cacKhoi.add(TienIch.tieuDe("Chỉ định tái khám"));
        try {
            luotKham = luotKhamService.layDangKham(PhienLamViec.getNhanVienHienTai().getMaNV());
            if (luotKham == null) {
                cacKhoi.add(TienIch.khoiChuaCoLuotKham());
            } else {
                dung(cacKhoi);
            }
        } catch (RuntimeException e) {
            TienIch.baoLoi(e);
        }
        TienIch.xepManHinh(this, cacKhoi.toArray(new Component[0]));
    }

    // Dựng form chọn ngày và dịch vụ
    private void dung(List<Component> cacKhoi) {
        cacKhoi.add(TienIch.dongThongTinLuotKham(luotKham));
        cmbDichVu =TienIch.taoChonDichVu(dichVuService.layTatCa(), "Chọn dịch vụ");
        txtNgay.setText(TienIch.ngay(LocalDate.now().plusDays(7)));

        JPanel form = TienIch.taoForm(new String[] {"Ngày tái khám", "Dịch vụ"}, new Component[] {txtNgay, cmbDichVu});
        JButton btnTao = TienIch.nutChinh("Tạo lịch tái khám");
        btnTao.addActionListener(e -> taoLich());
        btnHuy.addActionListener(e -> huyLich());
        capNhatNutHuy();
        JPanel the = TienIch.the(TienIch.doanChu("Lượt khám " + luotKham.getMaLuotKham() + " (khám ngày "
                + TienIch.ngay(luotKham.getGioBatDau().toLocalDate()) + "). Chọn ngày tái khám bất kỳ sau ngày khám.", java.awt.Color.BLACK),
                form, TienIch.dong(btnTao, btnHuy));
        JPanel gioiHan = new JPanel(new java.awt.BorderLayout()); // Thẻ chỉ rộng vừa phải
        gioiHan.setOpaque(false);
        gioiHan.add(the, java.awt.BorderLayout.WEST);
        the.setPreferredSize(new java.awt.Dimension(560, the.getPreferredSize().height));
        cacKhoi.add(gioiHan);
    }

    // Nút hủy
    private void capNhatNutHuy() {
        btnHuy.setVisible(luotKham.getNgayTaiKham() != null);
    }

    // Hủy lịch tái khám đã tạo
    private void huyLich() {
        if (!TienIch.xacNhan("Hủy lịch tái khám ngày " + TienIch.ngay(luotKham.getNgayTaiKham()) + "?")) {
            return;
        }
        try {
            luotKhamService.huyTaiKham(luotKham.getMaLuotKham());
            luotKham.setNgayTaiKham(null);
            capNhatNutHuy();
            TienIch.thongTin("Đã hủy lịch tái khám.");
        } catch (RuntimeException e) {
            TienIch.baoLoi(e);
        }
    }

    // Tạo lịch tái khám
    private void taoLich() {
        DichVu dichVu = (DichVu) cmbDichVu.getSelectedItem();
        if (dichVu == null) {
            TienIch.thongTin("Vui lòng chọn dịch vụ cho buổi tái khám.");
            return;
        }
        try {
            LocalDate ngay = TienIch.docNgay(txtNgay.getText());
            boolean coLich = luotKhamService.datTaiKham(luotKham.getMaLuotKham(), ngay, dichVu.getMaDichVu());
            luotKham.setNgayTaiKham(ngay);
            capNhatNutHuy();
            TienIch.thongTin(coLich ? "Đã tạo lịch tái khám ngày " + TienIch.ngay(ngay) + " (lịch đã xác nhận, không cần ai duyệt thêm)."
                    : "Đã lưu ngày tái khám " + TienIch.ngay(ngay) + ". Ngày đó nha sĩ chưa có ca trực nên chưa có giờ hẹn, lễ tân sẽ đặt giờ khi bệnh nhân liên hệ.");
        } catch (RuntimeException e) {
            TienIch.baoLoi(e);
        }
    }
}
