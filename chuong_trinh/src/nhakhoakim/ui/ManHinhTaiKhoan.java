package nhakhoakim.ui;

import java.awt.Component;
import java.awt.GraphicsEnvironment;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTable;
import javax.swing.JTextField;
import nhakhoakim.MainApp;
import nhakhoakim.controller.NhanVienController;
import nhakhoakim.controller.TaiKhoanController;
import nhakhoakim.entity.NhanVien;
import nhakhoakim.entity.TaiKhoan;

// Màn hình tạo tài khoản và gán vai trò (TK-03, TK-04): chỉ Quản lý; mỗi nhân viên một tài khoản, vai trò tài khoản luôn bằng vai trò nhân viên;
// khóa, mở khóa và cấp lại mật khẩu
@SuppressWarnings("serial") // lớp giao diện Swing, không cần serialVersionUID
public class ManHinhTaiKhoan extends JPanel {

    private TaiKhoanController taiKhoanController = new TaiKhoanController();
    private NhanVienController nhanVienController = new NhanVienController();

    private JTable tblTaiKhoan = TienIch.taoBang(new String[] {"Mã", "Tên đăng nhập", "Nhân viên", "Vai trò", "Trạng thái", "Đăng nhập cuối", "Sai liên tiếp"},
            new int[] {90, 130, 200, 90, 100, 140, 90}, "Chưa có tài khoản nào.");
    private List<TaiKhoan> dsTaiKhoan = new ArrayList<>();
    private JLabel lblKetQua = new JLabel();

    private JComboBox<NhanVien> cmbNhanVien = new JComboBox<>();     // nhân viên đang làm chưa có tài khoản
    private JTextField txtTen = TienIch.oNhap(20);
    private JPasswordField txtMatKhau = new JPasswordField(20);
    private JLabel lblVaiTro = TienIch.chuThuong(" ", false);

    public ManHinhTaiKhoan() {
        cmbNhanVien.setFont(TienIch.FONT);
        txtMatKhau.setFont(TienIch.FONT);
        cmbNhanVien.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> ds, Object v, int vt, boolean chon, boolean coFocus) {
                JLabel l = (JLabel) super.getListCellRendererComponent(ds, v, vt, chon, coFocus);
                NhanVien nv = (NhanVien) v;
                l.setText(nv == null ? "Chọn nhân viên" : nv.getMaNV() + " - " + nv.getHoTen() + " (" + nv.getVaiTro() + ")");
                return l;
            }
        });
        cmbNhanVien.addActionListener(e -> {
            NhanVien nv = (NhanVien) cmbNhanVien.getSelectedItem();
            lblVaiTro.setText(nv == null ? " " : "Vai trò tài khoản sẽ là " + nv.getVaiTro() + " (theo vai trò nhân viên).");
        });
        JButton btnTao = TienIch.nutChinh("Tạo tài khoản");
        btnTao.addActionListener(e -> tao());
        JButton btnKhoa = TienIch.nutDo("Khóa tài khoản");
        btnKhoa.addActionListener(e -> khoa(true));
        JButton btnMoKhoa = TienIch.nutPhu("Mở khóa");
        btnMoKhoa.addActionListener(e -> khoa(false));
        JButton btnCapLai = TienIch.nutPhu("Cấp lại mật khẩu");
        btnCapLai.addActionListener(e -> capLai());
        lblKetQua.setFont(TienIch.FONT);
        lblKetQua.setForeground(TienIch.MAU_XAM);
        TienIch.cotXuongDong(tblTaiKhoan, 2);

        JPanel form = TienIch.taoForm(new String[] {"Nhân viên", "Tên đăng nhập (từ 4 ký tự, không dấu cách)", "Mật khẩu (từ 6 ký tự)"},
                new Component[] {cmbNhanVien, txtTen, txtMatKhau});
        TienIch.xepManHinh(this, TienIch.tieuDe("Tài khoản đăng nhập"), lblKetQua, TienIch.cuonBang(tblTaiKhoan, 240),
                TienIch.dong(btnKhoa, btnMoKhoa, btnCapLai),
                TienIch.the(TienIch.chuThuong("Tạo tài khoản mới", true), form, lblVaiTro, TienIch.dong(btnTao)));
        tai();
    }

    // Tải danh sách tài khoản và danh sách nhân viên chưa có tài khoản
    private void tai() {
        try {
            dsTaiKhoan = taiKhoanController.layTatCa();
            List<String> daCo = new ArrayList<>();
            List<String[]> dong = new ArrayList<>();
            for (TaiKhoan tk : dsTaiKhoan) {
                daCo.add(tk.getMaNV().getMaNV());
                dong.add(new String[] {tk.getMaTaiKhoan(), tk.getTenDangNhap(), tk.getMaNV().getHoTen(), tk.getVaiTro(), tk.isTrangThaiKhoa() ? "Bị khóa" : "Hoạt động",
                    TienIch.ngayGio(tk.getLanDangNhapCuoi()), String.valueOf(tk.getSoLanDangNhapSai())});
            }
            TienIch.napBang(tblTaiKhoan, dong);
            lblKetQua.setText(dong.size() + " tài khoản.");
            List<NhanVien> chuaCo = new ArrayList<>();
            for (NhanVien nv : nhanVienController.layTatCa()) {
                if (NhanVienController.DANG_LAM.equals(nv.getTrangThai()) && !daCo.contains(nv.getMaNV())) {
                    chuaCo.add(nv);
                }
            }
            cmbNhanVien.setModel(new DefaultComboBoxModel<>(chuaCo.toArray(new NhanVien[0])));
            cmbNhanVien.setSelectedIndex(-1);
        } catch (RuntimeException e) {
            TienIch.baoLoi(e);
        }
    }

    private TaiKhoan dangChon() {
        int vt = tblTaiKhoan.getSelectedRow();
        if (vt < 0 || vt >= dsTaiKhoan.size()) {
            TienIch.thongTin("Vui lòng chọn một tài khoản trong bảng.");
            return null;
        }
        return dsTaiKhoan.get(vt);
    }

    // Tạo tài khoản cho nhân viên đang chọn
    private void tao() {
        NhanVien nv = (NhanVien) cmbNhanVien.getSelectedItem();
        if (nv == null) {
            TienIch.thongTin("Vui lòng chọn nhân viên (chỉ hiện nhân viên đang làm và chưa có tài khoản).");
            return;
        }
        try {
            TaiKhoan tk = taiKhoanController.taoTaiKhoan(nv.getMaNV(), txtTen.getText(), new String(txtMatKhau.getPassword()));
            TienIch.thongTin("Đã tạo tài khoản " + tk.getTenDangNhap() + " cho " + nv.getHoTen() + " với vai trò " + tk.getVaiTro() + ".");
            txtTen.setText("");
            txtMatKhau.setText("");
            tai();
        } catch (RuntimeException e) {
            TienIch.baoLoi(e);
        }
    }

    // Khóa hoặc mở khóa tài khoản đang chọn
    private void khoa(boolean khoa) {
        TaiKhoan tk = dangChon();
        if (tk == null) {
            return;
        }
        if (khoa && !TienIch.xacNhan("Khóa tài khoản " + tk.getTenDangNhap() + " của " + tk.getMaNV().getHoTen() + "? Người này sẽ không đăng nhập được.")) {
            return;
        }
        try {
            if (khoa) {
                taiKhoanController.khoa(tk.getMaTaiKhoan());
            } else {
                taiKhoanController.moKhoa(tk.getMaTaiKhoan());
            }
            tai();
        } catch (RuntimeException e) {
            TienIch.baoLoi(e);
        }
    }

    // Cấp lại mật khẩu cho tài khoản đang chọn (hỏi mật khẩu mới trong hộp có ô che ký tự)
    private void capLai() {
        TaiKhoan tk = dangChon();
        if (tk == null) {
            return;
        }
        String moi = hoiMatKhau("Cấp lại mật khẩu cho " + tk.getTenDangNhap() + " (từ 6 ký tự):");
        if (moi == null) {
            return;
        }
        try {
            taiKhoanController.capLaiMatKhau(tk.getMaTaiKhoan(), moi);
            TienIch.thongTin("Đã cấp lại mật khẩu cho " + tk.getTenDangNhap() + ".");
        } catch (RuntimeException e) {
            TienIch.baoLoi(e);
        }
    }

    // Hộp nhập mật khẩu có che ký tự; bấm Hủy thì trả về null (chạy không có màn hình thì lấy TienIch.nhapTuDong)
    static String hoiMatKhau(String loiNhac) {
        if (GraphicsEnvironment.isHeadless()) {
            return TienIch.nhapTuDong;
        }
        JPasswordField o = new JPasswordField(20);
        int kq = JOptionPane.showConfirmDialog(MainApp.cuaSo(), new Object[] {loiNhac, o}, "Nhập mật khẩu", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        return kq == JOptionPane.OK_OPTION ? new String(o.getPassword()) : null;
    }
}
