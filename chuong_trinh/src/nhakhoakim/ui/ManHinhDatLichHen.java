package nhakhoakim.ui;

import java.awt.Component;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import nhakhoakim.MainApp;
import nhakhoakim.controller.DichVuController;
import nhakhoakim.controller.LichHenController;
import nhakhoakim.entity.BenhNhan;
import nhakhoakim.entity.DichVu;
import nhakhoakim.entity.LichHen;
import nhakhoakim.entity.NhanVien;

// Màn hình đặt lịch hẹn (LH-02 đến LH-04): chọn bệnh nhân, dịch vụ, ngày, nha sĩ có ca trực và một giờ còn trống
@SuppressWarnings("serial") // lớp giao diện Swing, không cần serialVersionUID
public class ManHinhDatLichHen extends JPanel {

    private LichHenController lichHenController = new LichHenController();
    private DichVuController dichVuController = new DichVuController();

    private TienIch.ChonBenhNhan chonBenhNhan = new TienIch.ChonBenhNhan();
    private JComboBox<DichVu> cmbDichVu;                                  // dịch vụ đặt
    private JTextField txtNgay = TienIch.oNhap(10);                       // ngày hẹn (dd/MM/yyyy)
    private JComboBox<NhanVien> cmbNhaSi = new JComboBox<>();             // nha sĩ có ca trực trong ngày
    private JComboBox<String> cmbGio = new JComboBox<>();                 // giờ còn trống
    private JComboBox<String> cmbHinhThuc = new JComboBox<>(LichHenController.HINH_THUC.toArray(new String[0]));
    private JLabel lblThongTin = new JLabel(" ");

    public ManHinhDatLichHen() {
        cmbDichVu = TienIch.taoChonDichVu(dichVuController.layDangHoatDong(), "Chọn dịch vụ");
        txtNgay.setText(TienIch.ngay(LocalDate.now().plusDays(1)));
        cmbNhaSi.setFont(TienIch.FONT);
        cmbGio.setFont(TienIch.FONT);
        cmbHinhThuc.setFont(TienIch.FONT);
        cmbNhaSi.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> ds, Object v, int vt, boolean chon, boolean coFocus) {
                JLabel l = (JLabel) super.getListCellRendererComponent(ds, v, vt, chon, coFocus);
                l.setText(v == null ? "Chọn nha sĩ" : ((NhanVien) v).getHoTen());
                return l;
            }
        });

        JButton btnTimNhaSi = TienIch.nutPhu("Tìm nha sĩ có ca trực");
        btnTimNhaSi.addActionListener(e -> napNhaSi());
        cmbNhaSi.addActionListener(e -> napGio());
        cmbDichVu.addActionListener(e -> napGio());
        JButton btnDat = TienIch.nutChinh("Đặt lịch hẹn");
        btnDat.addActionListener(e -> dat());

        lblThongTin.setFont(TienIch.FONT);
        lblThongTin.setForeground(TienIch.MAU_XAM);
        JPanel form = TienIch.taoForm(new String[] {"Bệnh nhân", "Dịch vụ", "Ngày hẹn (dd/MM/yyyy)", "Nha sĩ", "Giờ hẹn", "Hình thức đặt"},
                new Component[] {chonBenhNhan, cmbDichVu, TienIch.dong(txtNgay, btnTimNhaSi), cmbNhaSi, cmbGio, cmbHinhThuc});
        TienIch.xepManHinh(this, TienIch.tieuDe("Đặt lịch hẹn"),
                TienIch.doanChu("Chọn bệnh nhân, dịch vụ và ngày, bấm Tìm nha sĩ có ca trực, rồi chọn nha sĩ và giờ còn trống. Giờ hẹn tròn 30 phút trong ca Sáng 08:00-12:00 "
                        + "hoặc ca Chiều 13:00-17:00. Lịch mới ở trạng thái Chờ xác nhận.", TienIch.MAU_XAM),
                TienIch.the(form, lblThongTin, TienIch.dong(btnDat)));
    }

    // Đổ danh sách nha sĩ có ca trực Có mặt trong ngày đang chọn
    private void napNhaSi() {
        try {
            LocalDate ngay = TienIch.docNgay(txtNgay.getText());
            if (ngay == null) {
                TienIch.thongTin("Vui lòng nhập ngày hẹn.");
                return;
            }
            List<NhanVien> ds = lichHenController.layNhaSiCoCaTruc(ngay);
            cmbNhaSi.setModel(new DefaultComboBoxModel<>(ds.toArray(new NhanVien[0])));
            cmbNhaSi.setSelectedIndex(-1);
            lblThongTin.setText(ds.isEmpty() ? "Ngày " + TienIch.ngay(ngay) + " chưa có nha sĩ nào có ca trực." : "Có " + ds.size() + " nha sĩ có ca trực ngày " + TienIch.ngay(ngay) + ".");
            napGio();
        } catch (RuntimeException e) {
            TienIch.baoLoi(e);
        }
    }

    // Đổ các giờ còn trống của nha sĩ và dịch vụ đang chọn
    private void napGio() {
        NhanVien nv = (NhanVien) cmbNhaSi.getSelectedItem();
        DichVu dv = (DichVu) cmbDichVu.getSelectedItem();
        cmbGio.setModel(new DefaultComboBoxModel<>(new String[0]));
        if (nv == null || dv == null) {
            return;
        }
        try {
            LocalDate ngay = TienIch.docNgay(txtNgay.getText());
            List<LocalTime> gio = lichHenController.layKhungGioTrong(nv.getMaNV(), ngay, dv.getThoiGianDuKien());
            String[] chu = new String[gio.size()];
            for (int i = 0; i < chu.length; i++) {
                chu[i] = TienIch.gio(gio.get(i));
            }
            cmbGio.setModel(new DefaultComboBoxModel<>(chu));
            cmbGio.setSelectedIndex(chu.length == 0 ? -1 : 0);
            lblThongTin.setText(chu.length == 0 ? nv.getHoTen() + " không còn giờ trống cho dịch vụ này (" + dv.getThoiGianDuKien() + " phút)."
                    : nv.getHoTen() + " còn " + chu.length + " giờ trống, dịch vụ " + dv.getThoiGianDuKien() + " phút.");
        } catch (RuntimeException e) {
            TienIch.baoLoi(e);
        }
    }

    // Đặt lịch với các lựa chọn hiện tại
    private void dat() {
        BenhNhan bn = chonBenhNhan.getBenhNhan();
        DichVu dv = (DichVu) cmbDichVu.getSelectedItem();
        NhanVien nv = (NhanVien) cmbNhaSi.getSelectedItem();
        String gioChu = (String) cmbGio.getSelectedItem();
        if (bn == null || dv == null || nv == null || gioChu == null) {
            TienIch.thongTin("Vui lòng chọn đủ bệnh nhân, dịch vụ, nha sĩ và giờ hẹn.");
            return;
        }
        try {
            LichHen lh = lichHenController.datLich(bn.getMaBN(), nv.getMaNV(), dv.getMaDichVu(), TienIch.docNgay(txtNgay.getText()),
                    LocalTime.parse(gioChu), (String) cmbHinhThuc.getSelectedItem());
            TienIch.thongTin("Đã đặt lịch " + lh.getMaLichHen() + " cho " + bn.getHoTen() + " lúc " + TienIch.gio(lh.getGioHen()) + " ngày "
                    + TienIch.ngay(lh.getNgayHen()) + " (" + lh.getPhong() + "). Lịch đang Chờ xác nhận.");
            MainApp.thayNoiDung(new ManHinhXemLichHen());
        } catch (RuntimeException e) {
            TienIch.baoLoi(e);
        }
    }
}
