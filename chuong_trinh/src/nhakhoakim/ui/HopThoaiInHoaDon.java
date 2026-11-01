package nhakhoakim.ui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.print.PrinterException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import nhakhoakim.MainApp;

// Cửa sổ xem trước hóa đơn dạng chữ kèm nút In (in bằng máy in của hệ thống)
@SuppressWarnings("serial") // lớp giao diện Swing, không cần serialVersionUID
class HopThoaiInHoaDon extends JDialog {

    private JTextArea noiDung;

    private HopThoaiInHoaDon(String chu) {
        super(MainApp.cuaSo(), "Hóa đơn", true);
        noiDung = new JTextArea(chu);
        noiDung.setEditable(false);
        noiDung.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        noiDung.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        JButton btnIn = TienIch.nutChinh("In hóa đơn");
        btnIn.addActionListener(e -> in());
        JButton btnDong = TienIch.nutPhu("Đóng");
        btnDong.addActionListener(e -> dispose());
        JPanel duoi = TienIch.dong(btnIn, btnDong);
        duoi.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        setLayout(new BorderLayout());
        add(new JScrollPane(noiDung), BorderLayout.CENTER);
        add(duoi, BorderLayout.SOUTH);
        setSize(640, 560);
        setLocationRelativeTo(MainApp.cuaSo());
    }

    // Hiện cửa sổ xem trước; chạy không có màn hình (kiểm thử) thì chỉ ghi nội dung vào TienIch.hopCuoi
    static void hien(String chu) {
        if (GraphicsEnvironment.isHeadless()) {
            TienIch.hopCuoi = chu;
            System.out.println("[Xem trước hóa đơn]\n" + chu);
            return;
        }
        new HopThoaiInHoaDon(chu).setVisible(true);
    }

    // Gửi nội dung hóa đơn tới máy in
    private void in() {
        try {
            if (!noiDung.print()) {
                TienIch.thongTin("Đã hủy lệnh in.");
            }
        } catch (PrinterException ex) {
            TienIch.loi("Không in được hóa đơn: " + ex.getMessage());
        }
    }
}
