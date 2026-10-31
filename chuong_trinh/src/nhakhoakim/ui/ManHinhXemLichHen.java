package nhakhoakim.ui;

import java.awt.Component;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import nhakhoakim.MainApp;
import nhakhoakim.controller.LichHenController;
import nhakhoakim.entity.DichVu;
import nhakhoakim.entity.LichHen;
import nhakhoakim.entity.NhanVien;
import nhakhoakim.controller.DichVuController;

// Màn hình xem lịch hẹn theo ngày hoặc theo tuần của mọi nha sĩ (LH-01), kèm khung giờ trống của một nha sĩ cho một dịch vụ
@SuppressWarnings("serial") // lớp giao diện Swing, không cần serialVersionUID
public class ManHinhXemLichHen extends JPanel implements MainApp.LamMoiDuoc {

    private LichHenController lichHenController = new LichHenController();
    private DichVuController dichVuController = new DichVuController();
    private BangTen ten = new BangTen();

    private JTextField txtNgay = TienIch.oNhap(10);                 // ngày xem (dd/MM/yyyy)
    private JComboBox<String> cmbKieu = new JComboBox<>(new String[] {"Theo ngày", "Theo tuần"});
    private JTable tblLich = TienIch.taoBang(new String[] {"Ngày", "Giờ", "Bệnh nhân", "Dịch vụ", "Nha sĩ", "Phòng", "Trạng thái", "Mã lịch"},
            new int[] {90, 60, 190, 180, 160, 90, 110, 90}, "Không có lịch hẹn nào trong khoảng này.");
    private List<LichHen> dsLich = new ArrayList<>();
    private JLabel lblKetQua = new JLabel();
    private JComboBox<NhanVien> cmbNhaSi = new JComboBox<>();       // nha sĩ có ca trực trong ngày
    private JComboBox<DichVu> cmbDichVu;                            // dịch vụ cần đặt
    private JLabel lblGioTrong = new JLabel(" ");

    public ManHinhXemLichHen() {
        txtNgay.setText(TienIch.ngay(LocalDate.now()));
        cmbKieu.setFont(TienIch.FONT);
        JButton btnXem = TienIch.nutChinh("Xem lịch");
        btnXem.addActionListener(e -> xem());
        txtNgay.addActionListener(e -> xem());
        JButton btnTruoc = TienIch.nutPhu("‹ Trước");
        btnTruoc.addActionListener(e -> doi(-1));
        JButton btnSau = TienIch.nutPhu("Sau ›");
        btnSau.addActionListener(e -> doi(1));
        JButton btnHomNay = TienIch.nutPhu("Hôm nay");
        btnHomNay.addActionListener(e -> {
            txtNgay.setText(TienIch.ngay(LocalDate.now()));
            xem();
        });
        JButton btnKhongDen = TienIch.nutDo("Đánh dấu không đến");
        btnKhongDen.addActionListener(e -> danhDauKhongDen());
        JButton btnDat = TienIch.nutPhu("Đặt lịch hẹn mới");
        btnDat.addActionListener(e -> MainApp.doiNoiDung(new ManHinhDatLichHen()));

        lblKetQua.setFont(TienIch.FONT);
        lblKetQua.setForeground(TienIch.MAU_XAM);
        TienIch.cotXuongDong(tblLich, 3);

        // Khung giờ trống: chọn nha sĩ (đã có ca trực trong ngày) và dịch vụ rồi xem các giờ còn trống
        cmbDichVu = TienIch.taoChonDichVu(dichVuController.layDangHoatDong(), "Chọn dịch vụ");
        cmbNhaSi.setFont(TienIch.FONT);
        cmbNhaSi.setPreferredSize(new java.awt.Dimension(220, 30));
        cmbDichVu.setPreferredSize(new java.awt.Dimension(220, 30));
        cmbNhaSi.setRenderer(new javax.swing.DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(javax.swing.JList<?> ds, Object v, int vt, boolean chon, boolean coFocus) {
                JLabel l = (JLabel) super.getListCellRendererComponent(ds, v, vt, chon, coFocus);
                l.setText(v == null ? "Chọn nha sĩ" : ((NhanVien) v).getHoTen());
                return l;
            }
        });
        JButton btnNapNhaSi = TienIch.nutPhu("Nha sĩ có ca trong ngày");
        btnNapNhaSi.addActionListener(e -> napNhaSi());
        JButton btnGioTrong = TienIch.nutChinh("Xem giờ trống");
        btnGioTrong.addActionListener(e -> xemGioTrong());
        lblGioTrong.setFont(TienIch.FONT_DAM);
        lblGioTrong.setForeground(TienIch.MAU_DAM);

        TienIch.xepManHinh(this, TienIch.tieuDe("Xem lịch hẹn"),
                TienIch.dong(TienIch.nhan("Ngày"), txtNgay, cmbKieu, btnXem, btnTruoc, btnSau, btnHomNay), lblKetQua,
                TienIch.cuonBang(tblLich, 300), TienIch.dong(btnKhongDen, btnDat),
                TienIch.chuThuong("Khung giờ trống của nha sĩ (ngày đang chọn ở trên)", true),
                TienIch.dong(btnNapNhaSi, cmbNhaSi, cmbDichVu, btnGioTrong), lblGioTrong);
        xem();
    }

    @Override
    public void lamMoi() {
        xem();
    }

    // Đọc ngày ở ô nhập; ô trống thì lấy hôm nay
    private LocalDate ngayDangChon() {
        LocalDate d = TienIch.docNgay(txtNgay.getText());
        return d == null ? LocalDate.now() : d;
    }

    // Lùi hoặc tiến một ngày (hoặc một tuần khi xem theo tuần)
    private void doi(int huong) {
        try {
            long soNgay = cmbKieu.getSelectedIndex() == 1 ? 7L * huong : huong;
            txtNgay.setText(TienIch.ngay(ngayDangChon().plusDays(soNgay)));
            xem();
        } catch (RuntimeException e) {
            TienIch.baoLoi(e);
        }
    }

    // Tải lịch của ngày hoặc tuần đang chọn (tuần tính từ thứ hai đến chủ nhật)
    private void xem() {
        try {
            LocalDate ngay = ngayDangChon();
            LocalDate tu = ngay;
            LocalDate den = ngay;
            if (cmbKieu.getSelectedIndex() == 1) {
                tu = ngay.minusDays(ngay.getDayOfWeek().getValue() - 1L);
                den = tu.plusDays(6);
            }
            dsLich = lichHenController.layTheoKhoang(tu, den);
            List<String[]> dong = new ArrayList<>();
            for (LichHen lh : dsLich) {
                dong.add(new String[] {TienIch.ngay(lh.getNgayHen()), TienIch.gio(lh.getGioHen()), ten.benhNhan(lh.getMaBN()),
                    ten.dichVu(lh.getMaDichVu()), ten.nhaSi(lh.getMaNhanVien()), TienIch.chu(lh.getPhong()), lh.getTrangThai(), lh.getMaLichHen()});
            }
            TienIch.napBang(tblLich, dong);
            lblKetQua.setText((cmbKieu.getSelectedIndex() == 1 ? "Tuần " + TienIch.ngay(tu) + " - " + TienIch.ngay(den) : "Ngày " + TienIch.ngay(tu))
                    + ": " + dong.size() + " lịch hẹn.");
        } catch (RuntimeException e) {
            TienIch.baoLoi(e);
        }
    }

    // Đổ danh sách nha sĩ có ca trực Có mặt trong ngày đang chọn vào ô chọn
    private void napNhaSi() {
        try {
            List<NhanVien> ds = lichHenController.layNhaSiCoCaTruc(ngayDangChon());
            cmbNhaSi.setModel(new javax.swing.DefaultComboBoxModel<>(ds.toArray(new NhanVien[0])));
            cmbNhaSi.setSelectedIndex(-1);
            lblGioTrong.setText(ds.isEmpty() ? "Ngày này chưa có nha sĩ nào có ca trực." : "Có " + ds.size() + " nha sĩ có ca trực, hãy chọn nha sĩ và dịch vụ.");
        } catch (RuntimeException e) {
            TienIch.baoLoi(e);
        }
    }

    // Hiện các giờ còn trống của nha sĩ đang chọn cho dịch vụ đang chọn
    private void xemGioTrong() {
        NhanVien nv = (NhanVien) cmbNhaSi.getSelectedItem();
        DichVu dv = (DichVu) cmbDichVu.getSelectedItem();
        if (nv == null || dv == null) {
            TienIch.thongTin("Hãy chọn nha sĩ (bấm \"Nha sĩ có ca trong ngày\" trước) và dịch vụ.");
            return;
        }
        try {
            List<LocalTime> gio = lichHenController.layKhungGioTrong(nv.getMaNV(), ngayDangChon(), dv.getThoiGianDuKien());
            if (gio.isEmpty()) {
                lblGioTrong.setText(nv.getHoTen() + " không còn giờ trống cho dịch vụ " + dv.getTenDichVu() + " (" + dv.getThoiGianDuKien() + " phút) ngày này.");
                return;
            }
            StringBuilder sb = new StringBuilder(nv.getHoTen() + " còn trống: ");
            for (int i = 0; i < gio.size(); i++) {
                sb.append(i == 0 ? "" : ", ").append(TienIch.gio(gio.get(i)));
            }
            lblGioTrong.setText("<html>" + sb + "</html>");
        } catch (RuntimeException e) {
            TienIch.baoLoi(e);
        }
    }

    // Đánh dấu lịch đang chọn là bệnh nhân không đến
    private void danhDauKhongDen() {
        int vt = tblLich.getSelectedRow();
        if (vt < 0 || vt >= dsLich.size()) {
            TienIch.thongTin("Vui lòng chọn một lịch hẹn trong bảng.");
            return;
        }
        LichHen lh = dsLich.get(vt);
        if (!TienIch.xacNhan("Đánh dấu bệnh nhân " + ten.benhNhan(lh.getMaBN()) + " không đến lịch " + lh.getMaLichHen() + "?")) {
            return;
        }
        try {
            lichHenController.danhDauKhongDen(lh.getMaLichHen());
            xem();
        } catch (RuntimeException e) {
            TienIch.baoLoi(e);
        }
    }
}
