package ui;

import java.awt.Component;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import nhakhoakim.MainApp;
import controller.TaiKhoanController;
import controller.PhienLamViec;
import entity.NhanVien;
import entity.TaiKhoan;

// Màn hình "Đăng nhập / Đăng xuất"
@SuppressWarnings("serial") // Lớp giao diện Swing
public class ManHinhTaiKhoanCuaToi extends JPanel {

    private TaiKhoanController taiKhoanController = new TaiKhoanController();

    private JPasswordField txtCu = new JPasswordField(20);
    private JPasswordField txtMoi = new JPasswordField(20);
    private JPasswordField txtNhapLai = new JPasswordField(20);

    public ManHinhTaiKhoanCuaToi() {
        NhanVien nv = PhienLamViec.getNhanVienHienTai();
        String ten = "";
        String lanCuoi = "";
        try {
            TaiKhoan tk = taiKhoanController.layTaiKhoanCuaToi();
            if (tk != null) {
                ten = tk.getTenDangNhap();
                lanCuoi = TienIch.ngayGio(tk.getLanDangNhapCuoi());
            }
        } catch (RuntimeException e) {
            TienIch.baoLoi(e);
        }
        for (JPasswordField p : new JPasswordField[] {txtCu, txtMoi, txtNhapLai}) {
            p.setFont(TienIch.FONT);
        }
        JButton btnDoi = TienIch.nutChinh("Đổi mật khẩu");
        btnDoi.addActionListener(e -> doiMatKhau());
        JButton btnDangXuat = TienIch.nutDo("Đăng xuất");
        btnDangXuat.addActionListener(e -> MainApp.dangXuat());
        TienIch.xepManHinh(this, TienIch.tieuDe("Tài khoản của tôi"),
                TienIch.the(TienIch.chuThuong(nv == null ? "Chưa đăng nhập" : nv.getHoTen() + " - " + nv.getVaiTro() + " (" + nv.getMaNV() + ")", true),
                        TienIch.chuThuong("Tên đăng nhập: " + ten + (lanCuoi.isEmpty() ? "" : "   |   Lần đăng nhập trước: " + lanCuoi), false), TienIch.dong(btnDangXuat)),
                TienIch.the(TienIch.chuThuong("Đổi mật khẩu", true),
                        TienIch.taoForm(new String[] {"Mật khẩu hiện tại", "Mật khẩu mới (từ 6 ký tự)", "Nhập lại mật khẩu mới"}, new Component[] {txtCu, txtMoi, txtNhapLai}),
                        TienIch.dong(btnDoi)));
    }

    // Đổi mật khẩu của người đang đăng nhập
    private void doiMatKhau() {
        String moi = new String(txtMoi.getPassword());
        if (!moi.equals(new String(txtNhapLai.getPassword()))) {
            TienIch.loi("Hai ô mật khẩu mới không giống nhau.");
            return;
        }
        try {
            taiKhoanController.doiMatKhauCuaToi(new String(txtCu.getPassword()), moi);
            TienIch.thongTin("Đã đổi mật khẩu.");
            txtCu.setText("");
            txtMoi.setText("");
            txtNhapLai.setText("");
        } catch (RuntimeException e) {
            TienIch.baoLoi(e);
        }
    }
}
