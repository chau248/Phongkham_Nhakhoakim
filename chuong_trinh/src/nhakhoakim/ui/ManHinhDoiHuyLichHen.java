package nhakhoakim.ui;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import nhakhoakim.MainApp;
import nhakhoakim.controller.LichHenController;
import nhakhoakim.entity.LichHen;

// Màn hình dời hoặc hủy lịch hẹn (LH-05, LH-06): chọn lịch Chờ xác nhận hoặc Đã xác nhận, dời sang ngày giờ khác hoặc hủy kèm lý do
@SuppressWarnings("serial") // lớp giao diện Swing, không cần serialVersionUID
public class ManHinhDoiHuyLichHen extends JPanel implements MainApp.LamMoiDuoc {

    private LichHenController lichHenController = new LichHenController();
    private BangTen ten = new BangTen();

    private JTextField txtTu = TienIch.oNhap(10);                   // lọc từ ngày
    private JTextField txtDen = TienIch.oNhap(10);                  // lọc đến ngày
    private JTable tblLich = TienIch.taoBang(new String[] {"Ngày", "Giờ", "Bệnh nhân", "Dịch vụ", "Nha sĩ", "Trạng thái", "Mã lịch"},
            new int[] {90, 60, 200, 190, 170, 110, 90}, "Không có lịch nào có thể dời hoặc hủy trong khoảng này.");
    private List<LichHen> dsLich = new ArrayList<>();
    private JTextField txtNgayMoi = TienIch.oNhap(10);              // ngày mới khi dời
    private JComboBox<String> cmbGioMoi = new JComboBox<>(cacGio()); // giờ mới khi dời

    public ManHinhDoiHuyLichHen() {
        txtTu.setText(TienIch.ngay(LocalDate.now()));
        txtDen.setText(TienIch.ngay(LocalDate.now().plusDays(30)));
        txtNgayMoi.setText(TienIch.ngay(LocalDate.now().plusDays(1)));
        cmbGioMoi.setFont(TienIch.FONT);
        JButton btnLoc = TienIch.nutChinh("Tìm lịch");
        btnLoc.addActionListener(e -> tai());
        JButton btnDoi = TienIch.nutChinh("Dời lịch");
        btnDoi.addActionListener(e -> doi());
        JButton btnHuy = TienIch.nutDo("Hủy lịch");
        btnHuy.addActionListener(e -> huy());
        TienIch.cotXuongDong(tblLich, 3);
        TienIch.xepManHinh(this, TienIch.tieuDe("Dời / hủy lịch hẹn"),
                TienIch.dong(TienIch.nhan("Từ ngày"), txtTu, TienIch.nhan("đến ngày"), txtDen, btnLoc),
                TienIch.cuonBang(tblLich, 280),
                TienIch.the(TienIch.chuThuong("Dời lịch đang chọn sang", true),
                        TienIch.dong(TienIch.nhan("Ngày mới"), txtNgayMoi, TienIch.nhan("Giờ mới"), cmbGioMoi, btnDoi)),
                TienIch.dong(btnHuy));
        tai();
    }

    @Override
    public void lamMoi() {
        tai();
    }

    // Các giờ hẹn hợp lệ: mỗi 30 phút trong ca Sáng và ca Chiều
    private static String[] cacGio() {
        List<String> kq = new ArrayList<>();
        for (LocalTime g = LocalTime.of(8, 0); g.isBefore(LocalTime.of(17, 0)); g = g.plusMinutes(LichHenController.BUOC_PHUT)) {
            if (LichHenController.caCuaGio(g) != null) {
                kq.add(TienIch.gio(g));
            }
        }
        return kq.toArray(new String[0]);
    }

    // Tải các lịch Chờ xác nhận và Đã xác nhận trong khoảng ngày đang chọn
    private void tai() {
        try {
            LocalDate tu = TienIch.docNgay(txtTu.getText());
            LocalDate den = TienIch.docNgay(txtDen.getText());
            if (tu == null || den == null) {
                TienIch.thongTin("Vui lòng nhập đủ khoảng ngày.");
                return;
            }
            dsLich = new ArrayList<>();
            List<String[]> dong = new ArrayList<>();
            for (LichHen lh : lichHenController.layTheoKhoang(tu, den)) {
                if (LichHenController.CHO_XAC_NHAN.equals(lh.getTrangThai()) || LichHenController.DA_XAC_NHAN.equals(lh.getTrangThai())) {
                    dsLich.add(lh);
                    dong.add(new String[] {TienIch.ngay(lh.getNgayHen()), TienIch.gio(lh.getGioHen()), ten.benhNhan(lh.getMaBN()), ten.dichVu(lh.getMaDichVu()),
                        ten.nhaSi(lh.getMaNhanVien()), lh.getTrangThai(), lh.getMaLichHen()});
                }
            }
            TienIch.napBang(tblLich, dong);
        } catch (RuntimeException e) {
            TienIch.baoLoi(e);
        }
    }

    private LichHen dangChon() {
        int vt = tblLich.getSelectedRow();
        if (vt < 0 || vt >= dsLich.size()) {
            TienIch.thongTin("Vui lòng chọn một lịch hẹn trong bảng.");
            return null;
        }
        return dsLich.get(vt);
    }

    // Dời lịch đang chọn sang ngày giờ mới
    private void doi() {
        LichHen lh = dangChon();
        if (lh == null) {
            return;
        }
        try {
            LocalDate ngay = TienIch.docNgay(txtNgayMoi.getText());
            if (ngay == null) {
                TienIch.thongTin("Vui lòng nhập ngày mới.");
                return;
            }
            lichHenController.doiLich(lh.getMaLichHen(), ngay, LocalTime.parse((String) cmbGioMoi.getSelectedItem()));
            TienIch.thongTin("Đã dời lịch " + lh.getMaLichHen() + " sang " + TienIch.gio(LocalTime.parse((String) cmbGioMoi.getSelectedItem())) + " ngày " + TienIch.ngay(ngay) + ".");
            tai();
        } catch (RuntimeException e) {
            TienIch.baoLoi(e);
        }
    }

    // Hủy lịch đang chọn, hỏi lý do
    private void huy() {
        LichHen lh = dangChon();
        if (lh == null) {
            return;
        }
        String lyDo = TienIch.nhapChu("Hủy lịch hẹn", "Lý do hủy lịch " + lh.getMaLichHen() + " của " + ten.benhNhan(lh.getMaBN()) + ":");
        if (lyDo == null) {
            return;
        }
        try {
            lichHenController.huyLich(lh.getMaLichHen(), lyDo);
            TienIch.thongTin("Đã hủy lịch " + lh.getMaLichHen() + ".");
            tai();
        } catch (RuntimeException e) {
            TienIch.baoLoi(e);
        }
    }
}
