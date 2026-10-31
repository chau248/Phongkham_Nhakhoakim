package nhakhoakim.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import nhakhoakim.controller.TaiKhoanController;
import nhakhoakim.entity.NhanVien;

// Hộp đăng nhập hiện khi mở chương trình và sau khi đăng xuất (TK-01): sai 5 lần liên tiếp thì tài khoản bị khóa
@SuppressWarnings("serial") // lớp giao diện Swing, không cần serialVersionUID
public class HopThoaiDangNhap extends JDialog {

    private TaiKhoanController taiKhoanController = new TaiKhoanController();
    private JTextField txtTen = TienIch.oNhap(20);
    private JPasswordField txtMatKhau = new JPasswordField(20);
    private JLabel lblLoi = new JLabel(" ");
    private NhanVien ketQua; // người đăng nhập thành công; bấm Thoát thì null

    private HopThoaiDangNhap() {
        super((java.awt.Frame) null, "Đăng nhập - Nha khoa Kim", true);
        txtMatKhau.setFont(TienIch.FONT);
        JLabel lblTen = new JLabel("NHA KHOA KIM");
        lblTen.setFont(TienIch.FONT.deriveFont(Font.BOLD, 24f));
        lblTen.setForeground(TienIch.MAU_DAM);
        JLabel lblMoTa = new JLabel("Đăng nhập để sử dụng chương trình");
        lblMoTa.setFont(TienIch.FONT);
        lblMoTa.setForeground(TienIch.MAU_XAM);
        lblLoi.setFont(TienIch.FONT_DAM);
        lblLoi.setForeground(TienIch.MAU_DO_CHU);
        JButton btnDangNhap = TienIch.nutChinh("Đăng nhập");
        btnDangNhap.addActionListener(e -> dangNhap());
        txtTen.addActionListener(e -> txtMatKhau.requestFocus());
        txtMatKhau.addActionListener(e -> dangNhap());
        JButton btnThoat = TienIch.nutPhu("Thoát");
        btnThoat.addActionListener(e -> dispose());

        JPanel noiDung = new JPanel();
        noiDung.setBackground(Color.WHITE);
        noiDung.setBorder(BorderFactory.createEmptyBorder(24, 28, 20, 28));
        TienIch.xep(noiDung, 10, lblTen, lblMoTa, TienIch.taoForm(new String[] {"Tên đăng nhập", "Mật khẩu"}, new Component[] {txtTen, txtMatKhau}), lblLoi,
                TienIch.dong(btnDangNhap, btnThoat));
        setLayout(new BorderLayout());
        add(noiDung, BorderLayout.CENTER);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(440, 330);
        setLocationRelativeTo(null);
    }

    private void dangNhap() {
        try {
            ketQua = taiKhoanController.dangNhap(txtTen.getText(), new String(txtMatKhau.getPassword()));
            dispose();
        } catch (nhakhoakim.dao.NghiepVuException e) {
            lblLoi.setText("<html>" + e.getMessage() + "</html>");
            txtMatKhau.setText("");
            txtMatKhau.requestFocus();
        } catch (RuntimeException e) {
            e.printStackTrace();
            lblLoi.setText("<html>Có lỗi hệ thống: " + e.getMessage() + "</html>");
        }
    }

    // Hiện hộp đăng nhập (chờ tới khi đăng nhập xong hoặc bấm Thoát); trả về người đăng nhập hoặc null nếu thoát
    public static NhanVien hien() {
        HopThoaiDangNhap hop = new HopThoaiDangNhap();
        hop.setVisible(true);
        return hop.ketQua;
    }
}
