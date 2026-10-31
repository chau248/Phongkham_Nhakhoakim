package nhakhoakim.ui;

import java.awt.Component;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import nhakhoakim.controller.CaTrucController;
import nhakhoakim.controller.NhanVienController;
import nhakhoakim.entity.CaTruc;
import nhakhoakim.entity.NhanVien;
import nhakhoakim.entity.VaiTro;

// Màn hình phân công ca trực theo tuần (NV-04, NV-05): chỉ Quản lý; ca Sáng 08:00-12:00, ca Chiều 13:00-17:00;
// mỗi phòng và ca tối đa một nha sĩ, lễ tân xếp ở quầy lễ tân
@SuppressWarnings("serial") // lớp giao diện Swing, không cần serialVersionUID
public class ManHinhPhanCongCaTruc extends JPanel {

    private CaTrucController caTrucController = new CaTrucController();
    private NhanVienController nhanVienController = new NhanVienController();

    private JTextField txtTuan = TienIch.oNhap(10);                  // một ngày bất kỳ trong tuần cần xem
    private JTable tblCa = TienIch.taoBang(new String[] {"Ngày", "Ca", "Phòng", "Nhân viên", "Vai trò", "Trạng thái", "Mã ca"},
            new int[] {110, 70, 110, 200, 90, 110, 90}, "Tuần này chưa xếp ca nào.");
    private List<CaTruc> dsCa = new ArrayList<>();
    private JLabel lblKetQua = new JLabel();

    private JComboBox<NhanVien> cmbNhanVien = new JComboBox<>();     // nhân viên đang làm (trừ Quản lý)
    private JTextField txtNgay = TienIch.oNhap(10);                  // ngày cần xếp
    private JComboBox<String> cmbCa = new JComboBox<>(new String[] {CaTrucController.SANG, CaTrucController.CHIEU});
    private JComboBox<String> cmbPhong = new JComboBox<>(new String[] {"Phòng 1", "Phòng 2", "Phòng 3", CaTrucController.QUAY_LE_TAN});

    public ManHinhPhanCongCaTruc() {
        txtTuan.setText(TienIch.ngay(LocalDate.now()));
        txtNgay.setText(TienIch.ngay(LocalDate.now()));
        cmbCa.setFont(TienIch.FONT);
        cmbPhong.setFont(TienIch.FONT);
        cmbNhanVien.setFont(TienIch.FONT);
        cmbNhanVien.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> ds, Object v, int vt, boolean chon, boolean coFocus) {
                JLabel l = (JLabel) super.getListCellRendererComponent(ds, v, vt, chon, coFocus);
                NhanVien nv = (NhanVien) v;
                l.setText(nv == null ? "Chọn nhân viên" : nv.getHoTen() + " (" + nv.getVaiTro() + ")");
                return l;
            }
        });
        List<NhanVien> dsNhanVien = new ArrayList<>();
        for (NhanVien nv : nhanVienController.layTatCa()) {
            if (NhanVienController.DANG_LAM.equals(nv.getTrangThai()) && !VaiTro.QUAN_LY.equals(nv.getVaiTro())) {
                dsNhanVien.add(nv);
            }
        }
        cmbNhanVien.setModel(new DefaultComboBoxModel<>(dsNhanVien.toArray(new NhanVien[0])));
        cmbNhanVien.setSelectedIndex(-1);

        JButton btnXem = TienIch.nutChinh("Xem tuần");
        btnXem.addActionListener(e -> xem());
        txtTuan.addActionListener(e -> xem());
        JButton btnTruoc = TienIch.nutPhu("‹ Tuần trước");
        btnTruoc.addActionListener(e -> doiTuan(-7));
        JButton btnSau = TienIch.nutPhu("Tuần sau ›");
        btnSau.addActionListener(e -> doiTuan(7));
        JButton btnThem = TienIch.nutChinh("Thêm ca");
        btnThem.addActionListener(e -> them());
        JButton btnCoMat = TienIch.nutPhu("Đặt Có mặt");
        btnCoMat.addActionListener(e -> doiTrangThai(CaTrucController.CO_MAT));
        JButton btnVang = TienIch.nutPhu("Đặt Vắng");
        btnVang.addActionListener(e -> doiTrangThai(CaTrucController.VANG));
        JButton btnXoa = TienIch.nutDo("Xóa ca đã chọn");
        btnXoa.addActionListener(e -> xoa());
        lblKetQua.setFont(TienIch.FONT);
        lblKetQua.setForeground(TienIch.MAU_XAM);

        JPanel form = TienIch.taoForm(new String[] {"Nhân viên", "Ngày (dd/MM/yyyy)", "Ca", "Phòng"}, new Component[] {cmbNhanVien, txtNgay, cmbCa, cmbPhong});
        TienIch.xepManHinh(this, TienIch.tieuDe("Phân công ca trực"),
                TienIch.doanChu("Ca Sáng 08:00-12:00, ca Chiều 13:00-17:00. Mỗi phòng và ca có tối đa một nha sĩ. Lễ tân luôn xếp ở quầy lễ tân (phòng được chọn sẽ bỏ qua).", TienIch.MAU_XAM),
                TienIch.dong(TienIch.nhan("Tuần có ngày"), txtTuan, btnXem, btnTruoc, btnSau), lblKetQua, TienIch.cuonBang(tblCa, 260),
                TienIch.dong(btnCoMat, btnVang, btnXoa), TienIch.the(TienIch.chuThuong("Thêm ca trực", true), form, TienIch.dong(btnThem)));
        xem();
    }

    private void doiTuan(int soNgay) {
        try {
            LocalDate d = TienIch.docNgay(txtTuan.getText());
            txtTuan.setText(TienIch.ngay((d == null ? LocalDate.now() : d).plusDays(soNgay)));
            xem();
        } catch (RuntimeException e) {
            TienIch.baoLoi(e);
        }
    }

    // Tải các ca của tuần chứa ngày đang chọn (thứ hai đến chủ nhật)
    private void xem() {
        try {
            LocalDate ngay = TienIch.docNgay(txtTuan.getText());
            if (ngay == null) {
                ngay = LocalDate.now();
            }
            LocalDate tu = ngay.minusDays(ngay.getDayOfWeek().getValue() - 1L);
            LocalDate den = tu.plusDays(6);
            dsCa = caTrucController.layTheoKhoang(tu, den);
            List<String[]> dong = new ArrayList<>();
            for (CaTruc ca : dsCa) {
                dong.add(new String[] {TienIch.ngay(ca.getNgay()), ca.getCaLam(), TienIch.chu(ca.getPhong()), ca.getMaNV().getHoTen(), ca.getMaNV().getVaiTro(),
                    ca.getTrangThai(), ca.getMaCaTruc()});
            }
            TienIch.napBang(tblCa, dong);
            lblKetQua.setText("Tuần " + TienIch.ngay(tu) + " - " + TienIch.ngay(den) + ": " + dong.size() + " ca.");
        } catch (RuntimeException e) {
            TienIch.baoLoi(e);
        }
    }

    private CaTruc dangChon() {
        int vt = tblCa.getSelectedRow();
        if (vt < 0 || vt >= dsCa.size()) {
            TienIch.thongTin("Vui lòng chọn một ca trong bảng.");
            return null;
        }
        return dsCa.get(vt);
    }

    // Thêm ca theo form
    private void them() {
        NhanVien nv = (NhanVien) cmbNhanVien.getSelectedItem();
        if (nv == null) {
            TienIch.thongTin("Vui lòng chọn nhân viên.");
            return;
        }
        try {
            CaTruc ca = caTrucController.phanCong(nv.getMaNV(), TienIch.docNgay(txtNgay.getText()), (String) cmbCa.getSelectedItem(), (String) cmbPhong.getSelectedItem());
            txtTuan.setText(txtNgay.getText());
            TienIch.thongTin("Đã xếp " + nv.getHoTen() + " ca " + ca.getCaLam().toLowerCase() + " ngày " + TienIch.ngay(ca.getNgay()) + " (" + ca.getPhong() + ").");
            xem();
        } catch (RuntimeException e) {
            TienIch.baoLoi(e);
        }
    }

    private void doiTrangThai(String trangThai) {
        CaTruc ca = dangChon();
        if (ca == null) {
            return;
        }
        try {
            caTrucController.doiTrangThai(ca.getMaCaTruc(), trangThai);
            xem();
        } catch (RuntimeException e) {
            TienIch.baoLoi(e);
        }
    }

    private void xoa() {
        CaTruc ca = dangChon();
        if (ca == null) {
            return;
        }
        if (!TienIch.xacNhan("Xóa ca " + ca.getCaLam().toLowerCase() + " ngày " + TienIch.ngay(ca.getNgay()) + " của " + ca.getMaNV().getHoTen() + "?")) {
            return;
        }
        try {
            caTrucController.xoa(ca.getMaCaTruc());
            xem();
        } catch (RuntimeException e) {
            TienIch.baoLoi(e);
        }
    }
}
