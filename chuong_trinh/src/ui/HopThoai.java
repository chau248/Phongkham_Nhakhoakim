package ui;

import controller.DichVuController;
import controller.TaiKhoanController;
import entity.DichVu;
import entity.NhanVien;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.print.PrinterException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import nhakhoakim.MainApp;

// Các hộp thoại của chương trình: đăng nhập
public class HopThoai {

    // Hiện hộp đăng nhập
    public static NhanVien dangNhap() {
        HopDangNhap hop = new HopDangNhap();
        hop.setVisible(true);
        return hop.ketQua;
    }

    // Hộp đăng nhập hiện khi mở chương trình
    @SuppressWarnings("serial") // Lớp giao diện Swing
    private static class HopDangNhap extends JDialog {

        private TaiKhoanController taiKhoanController = new TaiKhoanController();
        private JTextField txtTen = TienIch.oNhap(20);
        private JPasswordField txtMatKhau = new JPasswordField(20);
        private JLabel lblLoi = new JLabel(" ");
        private NhanVien ketQua; // Người đăng nhập thành công

        private HopDangNhap() {
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
            } catch (dao.NghiepVuException e) {
                lblLoi.setText("<html>" + e.getMessage() + "</html>");
                txtMatKhau.setText("");
                txtMatKhau.requestFocus();
            } catch (RuntimeException e) {
                e.printStackTrace();
                lblLoi.setText("<html>Có lỗi hệ thống: " + e.getMessage() + "</html>");
            }
        }

    }

    // Hiện cửa sổ xem trước hóa đơn
    static void inHoaDon(String chu) {
        inVanBan("Hóa đơn", chu);
    }

    // Hiện cửa sổ xem trước một văn bản
    static void inVanBan(String tieuDe, String chu) {
        if (GraphicsEnvironment.isHeadless()) {
            TienIch.hopCuoi = chu;
            System.out.println("[Xem trước " + tieuDe + "]\n" + chu);
            return;
        }
        new HopXemVanBan(tieuDe, chu).setVisible(true);
    }

    // Cửa sổ xem trước văn bản
    @SuppressWarnings("serial") // Lớp giao diện Swing
    private static class HopXemVanBan extends JDialog {

        private JTextArea noiDung;
        private String tieuDe;

        private HopXemVanBan(String tieuDe, String chu) {
            super(MainApp.cuaSo(), "Xem trước " + tieuDe.toLowerCase(), true);
            this.tieuDe = tieuDe;
            noiDung = new JTextArea(chu);
            noiDung.setEditable(false);
            noiDung.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
            noiDung.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
            JButton btnIn = TienIch.nutChinh("In " + tieuDe.toLowerCase());
            btnIn.addActionListener(e -> in());
            JButton btnDong = TienIch.nutPhu("Đóng");
            btnDong.addActionListener(e -> dispose());
            JPanel duoi = TienIch.dong(btnIn, btnDong);
            duoi.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
            setLayout(new BorderLayout());
            add(new JScrollPane(noiDung), BorderLayout.CENTER);
            add(duoi, BorderLayout.SOUTH);
            setSize(800, 640);
            setLocationRelativeTo(MainApp.cuaSo());
        }

        // Gửi nội dung tới máy in
        private void in() {
            try {
                if (!noiDung.print()) {
                    TienIch.thongTin("Đã hủy lệnh in.");
                }
            } catch (PrinterException ex) {
                TienIch.loi("Không in được " + tieuDe.toLowerCase() + ": " + ex.getMessage());
            }
        }
    }

        private static final String TAT_CA = "Tất cả loại";

        // Chạy kiểm thử không có màn hình
        public static DichVu traLoiTuDong = null;

        private static DichVu daChon; // Dịch vụ người dùng chọn
        private static List<DichVu> dangHien; // Danh sách dịch vụ

        // Mở hộp thoại chọn dịch vụ không gắn răng
        public static DichVu chonDichVu() {
            if (GraphicsEnvironment.isHeadless()) {
                return traLoiTuDong;
            }
            DichVuController dichVuService = new DichVuController();
            List<DichVu> tatCa = new ArrayList<>();
            for (DichVu dv : dichVuService.layDangHoatDong()) {
                if (!"Răng".equals(dv.getDonViTinh())) {
                    tatCa.add(dv);
                }
            }
            Set<String> cacLoai = new LinkedHashSet<>();
            cacLoai.add(TAT_CA);
            for (DichVu dv : tatCa) {
                cacLoai.add(dv.getLoaiDichVu());
            }
            JComboBox<String> cmbLoai = new JComboBox<>(cacLoai.toArray(new String[0]));
            cmbLoai.setFont(TienIch.FONT);

            JTable tbl = TienIch.taoBang(new String[] {"Mã", "Tên dịch vụ", "Loại", "Giá (đồng)", "Thời gian (phút)"},
                    new int[] {90, 220, 100, 110, 120}, "Không có dịch vụ nào.");
            dangHien = new ArrayList<>(tatCa);
            napBang(tbl);
            cmbLoai.addActionListener(e -> {
                String loai = (String) cmbLoai.getSelectedItem();
                dangHien = new ArrayList<>();
                for (DichVu dv : tatCa) {
                    if (TAT_CA.equals(loai) || dv.getLoaiDichVu().equals(loai)) {
                        dangHien.add(dv);
                    }
                }
                napBang(tbl);
            });

            daChon = null;
            JDialog hop = new JDialog(MainApp.cuaSo(), "Chọn dịch vụ điều trị", true);
            JButton btnOk = TienIch.nutChinh("OK");
            btnOk.setEnabled(false); // Chưa chọn dòng nào
            tbl.getSelectionModel().addListSelectionListener(e -> btnOk.setEnabled(tbl.getSelectedRow() >= 0));
            btnOk.addActionListener(e -> {
                int vt = tbl.getSelectedRow();
                if (vt >= 0 && vt < dangHien.size()) {
                    daChon = dangHien.get(vt);
                }
                hop.dispose();
            });
            JButton btnHuy = TienIch.nutPhu("Hủy");
            btnHuy.addActionListener(e -> hop.dispose());

            JPanel dau = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
            dau.add(TienIch.chuThuong("Loại dịch vụ:", false));
            dau.add(cmbLoai);
            JPanel duoi = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
            duoi.add(btnOk);
            duoi.add(btnHuy);
            hop.getContentPane().setLayout(new BorderLayout());
            hop.getContentPane().add(dau, BorderLayout.NORTH);
            hop.getContentPane().add(TienIch.cuonBang(tbl, 300), BorderLayout.CENTER);
            hop.getContentPane().add(duoi, BorderLayout.SOUTH);
            hop.pack();
            hop.setLocationRelativeTo(MainApp.cuaSo());
            hop.setVisible(true); // Dừng ở đây cho tới
            return daChon;
        }

        // Đổ danh sách đang hiện vào bảng
        private static void napBang(JTable tbl) {
            List<String[]> dong = new ArrayList<>();
            for (DichVu dv : dangHien) {
                dong.add(new String[] {dv.getMaDichVu(), dv.getTenDichVu(), dv.getLoaiDichVu(), TienIch.tien(dv.getGia()),
                    String.valueOf(dv.getThoiGianDuKien())});
            }
            TienIch.napBang(tbl, dong);
        }

}
