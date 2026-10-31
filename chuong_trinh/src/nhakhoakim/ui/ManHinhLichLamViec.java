package nhakhoakim.ui;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import nhakhoakim.controller.LichHenController;
import nhakhoakim.dao.PhienLamViec;
import nhakhoakim.entity.DongLichLamViec;
import nhakhoakim.entity.NhanVien;

// Màn hình xem lịch làm việc theo tuần (LH-08), chỉ xem: nha sĩ thấy ca và lịch hẹn của mình, y tá thấy ca hỗ trợ kèm tên nha sĩ phụ trách
@SuppressWarnings("serial") // lớp giao diện Swing, không cần serialVersionUID
public class ManHinhLichLamViec extends JPanel {

    private LichHenController lichHenController = new LichHenController();

    private JTextField txtNgay = TienIch.oNhap(10);                 // một ngày bất kỳ trong tuần cần xem
    private JTable tblLich = TienIch.taoBang(new String[] {"Ngày", "Ca", "Phòng", "Giờ", "Bệnh nhân", "Dịch vụ", "Nha sĩ phụ trách", "Trạng thái"},
            new int[] {90, 60, 90, 60, 190, 180, 170, 110}, "Tuần này chưa có ca trực nào.");
    private JLabel lblKetQua = new JLabel();

    public ManHinhLichLamViec() {
        txtNgay.setText(TienIch.ngay(LocalDate.now()));
        JButton btnXem = TienIch.nutChinh("Xem tuần");
        btnXem.addActionListener(e -> xem());
        txtNgay.addActionListener(e -> xem());
        JButton btnTruoc = TienIch.nutPhu("‹ Tuần trước");
        btnTruoc.addActionListener(e -> doi(-7));
        JButton btnSau = TienIch.nutPhu("Tuần sau ›");
        btnSau.addActionListener(e -> doi(7));
        lblKetQua.setFont(TienIch.FONT);
        lblKetQua.setForeground(TienIch.MAU_XAM);
        TienIch.cotXuongDong(tblLich, 5);
        NhanVien nv = PhienLamViec.getNhanVienHienTai();
        TienIch.xepManHinh(this, TienIch.tieuDe("Lịch làm việc"),
                TienIch.doanChu(nv == null ? "" : nv.getHoTen() + " (" + nv.getVaiTro() + "). Màn hình chỉ để xem, không sửa được lịch.", TienIch.MAU_XAM),
                TienIch.dong(TienIch.nhan("Tuần có ngày"), txtNgay, btnXem, btnTruoc, btnSau), lblKetQua, TienIch.cuonBang(tblLich, 360));
        xem();
    }

    private void doi(int soNgay) {
        try {
            LocalDate d = TienIch.docNgay(txtNgay.getText());
            txtNgay.setText(TienIch.ngay((d == null ? LocalDate.now() : d).plusDays(soNgay)));
            xem();
        } catch (RuntimeException e) {
            TienIch.baoLoi(e);
        }
    }

    // Tải lịch làm việc của tuần chứa ngày đang chọn (thứ hai đến chủ nhật)
    private void xem() {
        try {
            LocalDate ngay = TienIch.docNgay(txtNgay.getText());
            if (ngay == null) {
                ngay = LocalDate.now();
            }
            LocalDate tu = ngay.minusDays(ngay.getDayOfWeek().getValue() - 1L);
            LocalDate den = tu.plusDays(6);
            List<String[]> dong = new ArrayList<>();
            for (DongLichLamViec d : lichHenController.layLichLamViec(tu, den)) {
                dong.add(new String[] {TienIch.ngay(d.getNgay()), d.getCaLam(), TienIch.chu(d.getPhong()), d.getGio() == null ? "" : TienIch.gio(d.getGio()),
                    d.getGio() == null ? "(chưa có lịch hẹn)" : TienIch.chu(d.getTenBenhNhan()), TienIch.chu(d.getTenDichVu()), TienIch.chu(d.getTenNhaSi()),
                    TienIch.chu(d.getTrangThai())});
            }
            TienIch.napBang(tblLich, dong);
            lblKetQua.setText("Tuần " + TienIch.ngay(tu) + " - " + TienIch.ngay(den) + ": " + dong.size() + " dòng.");
        } catch (RuntimeException e) {
            TienIch.baoLoi(e);
        }
    }
}
