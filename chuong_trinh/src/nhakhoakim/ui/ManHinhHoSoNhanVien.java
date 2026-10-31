package nhakhoakim.ui;

import java.awt.Component;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import nhakhoakim.controller.NhanVienController;
import nhakhoakim.entity.NhanVien;
import nhakhoakim.entity.VaiTro;

// Màn hình cập nhật hồ sơ nhân viên (NV-01 đến NV-03): chỉ Quản lý; có cảnh báo chứng chỉ hành nghề sắp hết hạn;
// không xóa nhân viên, chỉ chuyển sang Nghỉ việc (tài khoản của họ bị khóa)
@SuppressWarnings("serial") // lớp giao diện Swing, không cần serialVersionUID
public class ManHinhHoSoNhanVien extends JPanel {

    private static final String TAT_CA = "Tất cả";

    private NhanVienController nhanVienController = new NhanVienController();

    private JPanel khuCanhBao = new JPanel(new java.awt.BorderLayout());     // chỗ đặt cảnh báo chứng chỉ
    private JTextField txtTim = TienIch.oNhap(20);                           // tìm theo mã hoặc họ tên
    private JComboBox<String> cmbLocVaiTro = new JComboBox<>(new String[] {TAT_CA, VaiTro.NHA_SI, VaiTro.Y_TA, VaiTro.LE_TAN, VaiTro.QUAN_LY});
    private JComboBox<String> cmbLocTrangThai = new JComboBox<>(new String[] {TAT_CA, NhanVienController.DANG_LAM, NhanVienController.NGHI_VIEC});
    private JTable tblNhanVien = TienIch.taoBang(new String[] {"Mã", "Họ tên", "Vai trò", "SĐT", "Chuyên khoa", "Hạn chứng chỉ", "Trạng thái"},
            new int[] {90, 190, 90, 100, 130, 110, 110}, "Không có nhân viên nào.");
    private List<NhanVien> dsHienThi = new ArrayList<>();

    private JTextField txtHoTen = TienIch.oNhap(26);
    private JComboBox<String> cmbVaiTro = new JComboBox<>(new String[] {VaiTro.NHA_SI, VaiTro.Y_TA, VaiTro.LE_TAN, VaiTro.QUAN_LY});
    private JComboBox<String> cmbGioiTinh = new JComboBox<>(new String[] {"", "Nam", "Nữ", "Khác"});
    private JTextField txtSdt = TienIch.oNhap(12);
    private JTextField txtChuyenKhoa = TienIch.oNhap(20);
    private JTextField txtNgayVaoLam = TienIch.oNhap(10);
    private JTextField txtSoCCHN = TienIch.oNhap(16);
    private JTextField txtHanCCHN = TienIch.oNhap(10);
    private JComboBox<String> cmbTrangThai = new JComboBox<>(new String[] {NhanVienController.DANG_LAM, NhanVienController.NGHI_VIEC});
    private NhanVien dangSua; // nhân viên đang chọn để sửa (null là đang nhập người mới)

    public ManHinhHoSoNhanVien() {
        for (JComboBox<String> c : List.of(cmbLocVaiTro, cmbLocTrangThai, cmbVaiTro, cmbGioiTinh, cmbTrangThai)) {
            c.setFont(TienIch.FONT);
        }
        khuCanhBao.setOpaque(false);
        JButton btnLoc = TienIch.nutChinh("Lọc");
        btnLoc.addActionListener(e -> loc());
        txtTim.addActionListener(e -> loc());
        cmbLocVaiTro.addActionListener(e -> loc());
        cmbLocTrangThai.addActionListener(e -> loc());
        tblNhanVien.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                chonDong();
            }
        });
        JButton btnThem = TienIch.nutChinh("Thêm nhân viên mới");
        btnThem.addActionListener(e -> them());
        JButton btnLuu = TienIch.nutPhu("Lưu thay đổi cho nhân viên đã chọn");
        btnLuu.addActionListener(e -> luu());
        JButton btnXoaO = TienIch.nutPhu("Xóa ô nhập");
        btnXoaO.addActionListener(e -> xoaForm());
        txtNgayVaoLam.setToolTipText("dd/MM/yyyy");
        txtHanCCHN.setToolTipText("dd/MM/yyyy (bắt buộc với nha sĩ)");

        JPanel form = TienIch.taoForm(new String[] {"Họ tên", "Vai trò", "Giới tính", "Số điện thoại", "Chuyên khoa", "Ngày vào làm (dd/MM/yyyy)",
            "Số chứng chỉ hành nghề (nha sĩ)", "Hạn chứng chỉ (dd/MM/yyyy)", "Trạng thái"},
                new Component[] {txtHoTen, cmbVaiTro, cmbGioiTinh, txtSdt, txtChuyenKhoa, txtNgayVaoLam, txtSoCCHN, txtHanCCHN, cmbTrangThai});
        TienIch.xepManHinh(this, TienIch.tieuDe("Hồ sơ nhân viên"), khuCanhBao,
                TienIch.dong(TienIch.nhan("Tìm"), txtTim, TienIch.nhan("Vai trò"), cmbLocVaiTro, TienIch.nhan("Trạng thái"), cmbLocTrangThai, btnLoc),
                TienIch.cuonBang(tblNhanVien, 230), TienIch.the(TienIch.chuThuong("Thông tin nhân viên (chọn một dòng ở trên để sửa)", true), form,
                        TienIch.dong(btnThem, btnLuu, btnXoaO)));
        tai();
    }

    // Tải lại cảnh báo chứng chỉ và danh sách nhân viên
    private void tai() {
        try {
            khuCanhBao.removeAll();
            List<NhanVien> sapHet = nhanVienController.layCanhBaoChungChi();
            if (!sapHet.isEmpty()) {
                StringBuilder sb = new StringBuilder("CHỨNG CHỈ HÀNH NGHỀ: ");
                for (NhanVien nv : sapHet) {
                    boolean hetHan = nv.getNgayHetHanChungChi().isBefore(LocalDate.now());
                    sb.append(nv.getHoTen()).append(hetHan ? " ĐÃ HẾT HẠN " : " sắp hết hạn ").append(TienIch.ngay(nv.getNgayHetHanChungChi())).append("; ");
                }
                khuCanhBao.add(TienIch.thongBao(sb.toString(), TienIch.MAU_VANG_NEN, TienIch.MAU_VANG_CHU, true), java.awt.BorderLayout.CENTER);
            }
            khuCanhBao.revalidate();
            khuCanhBao.repaint();
            loc();
        } catch (RuntimeException e) {
            TienIch.baoLoi(e);
        }
    }

    // Lọc danh sách theo từ khóa, vai trò, trạng thái
    private void loc() {
        try {
            String tu = txtTim.getText().trim().toLowerCase();
            String vt = (String) cmbLocVaiTro.getSelectedItem();
            String tt = (String) cmbLocTrangThai.getSelectedItem();
            dsHienThi = new ArrayList<>();
            List<String[]> dong = new ArrayList<>();
            for (NhanVien nv : nhanVienController.layTatCa()) {
                if (!tu.isEmpty() && !nv.getMaNV().toLowerCase().contains(tu) && !nv.getHoTen().toLowerCase().contains(tu)) {
                    continue;
                }
                if (!TAT_CA.equals(vt) && !nv.getVaiTro().equals(vt)) {
                    continue;
                }
                if (!TAT_CA.equals(tt) && !nv.getTrangThai().equals(tt)) {
                    continue;
                }
                dsHienThi.add(nv);
                dong.add(new String[] {nv.getMaNV(), nv.getHoTen(), nv.getVaiTro(), TienIch.chu(nv.getSdt()), TienIch.chu(nv.getChuyenKhoa()),
                    TienIch.ngay(nv.getNgayHetHanChungChi()), nv.getTrangThai()});
            }
            TienIch.napBang(tblNhanVien, dong);
        } catch (RuntimeException e) {
            TienIch.baoLoi(e);
        }
    }

    // Đổ nhân viên đang chọn vào form
    private void chonDong() {
        int vt = tblNhanVien.getSelectedRow();
        if (vt < 0 || vt >= dsHienThi.size()) {
            return;
        }
        dangSua = dsHienThi.get(vt);
        txtHoTen.setText(dangSua.getHoTen());
        cmbVaiTro.setSelectedItem(dangSua.getVaiTro());
        cmbGioiTinh.setSelectedItem(TienIch.chu(dangSua.getGioiTinh()));
        txtSdt.setText(TienIch.chu(dangSua.getSdt()));
        txtChuyenKhoa.setText(TienIch.chu(dangSua.getChuyenKhoa()));
        txtNgayVaoLam.setText(TienIch.ngay(dangSua.getNgayVaoLam()));
        txtSoCCHN.setText(TienIch.chu(dangSua.getSoChungChiHanhNghe()));
        txtHanCCHN.setText(TienIch.ngay(dangSua.getNgayHetHanChungChi()));
        cmbTrangThai.setSelectedItem(dangSua.getTrangThai());
    }

    private void xoaForm() {
        dangSua = null;
        tblNhanVien.clearSelection();
        for (JTextField t : new JTextField[] {txtHoTen, txtSdt, txtChuyenKhoa, txtNgayVaoLam, txtSoCCHN, txtHanCCHN}) {
            t.setText("");
        }
        cmbVaiTro.setSelectedIndex(0);
        cmbGioiTinh.setSelectedIndex(0);
        cmbTrangThai.setSelectedIndex(0);
    }

    // Dựng đối tượng NhanVien từ các ô nhập
    private NhanVien docForm() {
        NhanVien nv = new NhanVien();
        nv.setHoTen(txtHoTen.getText());
        nv.setVaiTro((String) cmbVaiTro.getSelectedItem());
        nv.setGioiTinh((String) cmbGioiTinh.getSelectedItem());
        nv.setSdt(txtSdt.getText());
        nv.setChuyenKhoa(txtChuyenKhoa.getText());
        nv.setNgayVaoLam(TienIch.docNgay(txtNgayVaoLam.getText()));
        nv.setSoChungChiHanhNghe(txtSoCCHN.getText());
        nv.setNgayHetHanChungChi(TienIch.docNgay(txtHanCCHN.getText()));
        nv.setTrangThai((String) cmbTrangThai.getSelectedItem());
        return nv;
    }

    // Thêm nhân viên mới từ form
    private void them() {
        try {
            NhanVien nv = nhanVienController.them(docForm());
            TienIch.thongTin("Đã thêm nhân viên " + nv.getMaNV() + " - " + nv.getHoTen() + ". Vào menu Tài khoản để tạo tài khoản đăng nhập cho nhân viên này.");
            xoaForm();
            tai();
        } catch (RuntimeException e) {
            TienIch.baoLoi(e);
        }
    }

    // Lưu thay đổi cho nhân viên đang chọn
    private void luu() {
        if (dangSua == null) {
            TienIch.thongTin("Vui lòng chọn một nhân viên trong bảng để sửa.");
            return;
        }
        try {
            NhanVien nv = docForm();
            nv.setMaNV(dangSua.getMaNV());
            if (NhanVienController.NGHI_VIEC.equals(nv.getTrangThai()) && !NhanVienController.NGHI_VIEC.equals(dangSua.getTrangThai())
                    && !TienIch.xacNhan("Cho " + nv.getHoTen() + " nghỉ việc? Tài khoản của nhân viên này sẽ bị khóa và không được xếp ca nữa.")) {
                return;
            }
            nhanVienController.capNhat(nv);
            TienIch.thongTin("Đã lưu hồ sơ " + nv.getMaNV() + ".");
            tai();
        } catch (RuntimeException e) {
            TienIch.baoLoi(e);
        }
    }
}
