package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.TemporalAdjusters;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import controller.CauHinhController;
import entity.DanhMuc;

// Các thành phần giao diện dùng chung
public class ThanhPhan {

    // Ô chọn ngày: bấm vào ô
    @SuppressWarnings("serial") // Lớp giao diện Swing
    public static class ChonNgay extends JTextField {

        private static final String[] THU = {"T2", "T3", "T4", "T5", "T6", "T7", "CN"};

        private YearMonth thangDangXem = YearMonth.now();

        public ChonNgay() {
            super(10);
            setEditable(false);
            setFont(TienIch.FONT);
            setBackground(Color.WHITE);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setToolTipText("Bấm để chọn ngày");
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (isEnabled()) {
                        hienLich();
                    }
                }
            });
        }

        // Ngày đang chọn
        public LocalDate getNgay() {
            return TienIch.docNgay(getText());
        }

        public void setNgay(LocalDate ngay) {
            setText(TienIch.ngay(ngay));
        }

        // Mở lịch tháng của ngày đang chọn
        private void hienLich() {
            LocalDate dangChon = null;
            try {
                dangChon = getNgay();
            } catch (RuntimeException e) {
                // Ô đang rỗng hoặc sai
            }
            thangDangXem = dangChon == null ? YearMonth.now() : YearMonth.from(dangChon);
            JPopupMenu menu = new JPopupMenu();
            dungLich(menu);
            menu.show(this, 0, getHeight());
        }

        // Dựng lại các ô ngày của tháng đang xem
        private void dungLich(JPopupMenu menu) {
            menu.removeAll();
            LocalDate dangChon = null;
            try {
                dangChon = getNgay();
            } catch (RuntimeException e) {
                // Bỏ qua
            }
            JButton truoc = nutNho("‹");
            truoc.addActionListener(e -> {
                thangDangXem = thangDangXem.minusMonths(1);
                dungLich(menu);
            });
            JButton sau = nutNho("›");
            sau.addActionListener(e -> {
                thangDangXem = thangDangXem.plusMonths(1);
                dungLich(menu);
            });
            // Chọn nhanh tháng và năm
            javax.swing.JComboBox<Integer> cmbThang = new javax.swing.JComboBox<>();
            for (int m = 1; m <= 12; m++) {
                cmbThang.addItem(m);
            }
            javax.swing.JComboBox<Integer> cmbNam = new javax.swing.JComboBox<>();
            for (int y = 1920; y <= LocalDate.now().getYear() + 5; y++) {
                cmbNam.addItem(y);
            }
            cmbThang.setSelectedItem(thangDangXem.getMonthValue());
            cmbNam.setSelectedItem(thangDangXem.getYear());
            java.awt.event.ActionListener doiThang = e -> {
                thangDangXem = YearMonth.of((Integer) cmbNam.getSelectedItem(), (Integer) cmbThang.getSelectedItem());
                dungLich(menu);
            };
            cmbThang.addActionListener(doiThang);
            cmbNam.addActionListener(doiThang);
            JPanel giua = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 4, 0));
            giua.add(new JLabel("Tháng"));
            giua.add(cmbThang);
            giua.add(cmbNam);
            JPanel dau = new JPanel(new java.awt.BorderLayout());
            dau.add(truoc, java.awt.BorderLayout.WEST);
            dau.add(giua, java.awt.BorderLayout.CENTER);
            dau.add(sau, java.awt.BorderLayout.EAST);

            JPanel luoi = new JPanel(new GridLayout(0, 7, 2, 2));
            for (String t : THU) {
                JLabel l = new JLabel(t, SwingConstants.CENTER);
                l.setFont(TienIch.FONT_DAM);
                l.setForeground(TienIch.MAU_XAM);
                luoi.add(l);
            }
            int lech = thangDangXem.atDay(1).getDayOfWeek().getValue() - 1;
            for (int i = 0; i < lech; i++) {
                luoi.add(new JLabel(""));
            }
            for (int d = 1; d <= thangDangXem.lengthOfMonth(); d++) {
                LocalDate ngay = thangDangXem.atDay(d);
                JButton b = nutNho(String.valueOf(d));
                if (ngay.equals(dangChon)) {
                    b.setBackground(TienIch.MAU_CHINH);
                    b.setForeground(Color.WHITE);
                    b.setOpaque(true);
                    b.setContentAreaFilled(true);
                } else if (ngay.equals(LocalDate.now())) {
                    b.setForeground(TienIch.MAU_CHINH);
                }
                b.addActionListener(e -> {
                    setNgay(ngay);
                    menu.setVisible(false);
                });
                luoi.add(b);
            }
            JButton homNay = nutNho("Hôm nay");
            homNay.addActionListener(e -> {
                setNgay(LocalDate.now());
                menu.setVisible(false);
            });
            JButton xoa = nutNho("Xóa");
            xoa.addActionListener(e -> {
                setText("");
                menu.setVisible(false);
            });
            JPanel chan = new JPanel(new GridLayout(1, 2, 4, 0));
            chan.add(homNay);
            chan.add(xoa);

            JPanel noi = new JPanel(new java.awt.BorderLayout(0, 4));
            noi.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
            noi.add(dau, java.awt.BorderLayout.NORTH);
            noi.add(luoi, java.awt.BorderLayout.CENTER);
            noi.add(chan, java.awt.BorderLayout.SOUTH);
            menu.add(noi);
            menu.revalidate();
            menu.pack();
        }

        private JButton nutNho(String chu) {
            JButton b = new JButton(chu);
            b.setFont(TienIch.FONT);
            b.setMargin(new java.awt.Insets(2, 4, 2, 4));
            b.setFocusPainted(false);
            b.setContentAreaFilled(false);
            b.setBorder(BorderFactory.createEmptyBorder(3, 4, 3, 4));
            b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            return b;
        }
    }

    // Ô chọn lấy từ một danh mục gợi ý
    @SuppressWarnings("serial") // Lớp giao diện Swing
    public static class ChonGoiY extends JPanel {

        private CauHinhController cauHinhController = new CauHinhController();
        private DanhMuc danhMuc;
        private JComboBox<String> cmb = new JComboBox<>();

        public ChonGoiY(DanhMuc danhMuc) {
            super(new FlowLayout(FlowLayout.LEFT, 8, 0));
            this.danhMuc = danhMuc;
            setOpaque(false);
            cmb.setFont(TienIch.FONT);
            add(cmb);
            nap();
        }

        public JComboBox<String> getComboBox() {
            return cmb;
        }

        // Nạp lại danh sách
        private void nap() {
            Object dangChon = cmb.getSelectedItem();
            cmb.removeAllItems();
            cmb.addItem("");
            for (String ten : cauHinhController.layDanhMuc(danhMuc)) {
                cmb.addItem(ten);
            }
            cmb.setSelectedItem(dangChon == null ? "" : dangChon);
        }

        // Giá trị đang chọn
        public String getGiaTri() {
            String gt = (String) cmb.getSelectedItem();
            return gt == null || gt.isEmpty() ? null : gt;
        }

        // Chọn giá trị
        public void setGiaTri(String giaTri) {
            if (giaTri == null || giaTri.isEmpty()) {
                cmb.setSelectedItem("");
                return;
            }
            boolean co = false;
            for (int i = 0; i < cmb.getItemCount(); i++) {
                co |= giaTri.equals(cmb.getItemAt(i));
            }
            if (!co) {
                cmb.addItem(giaTri);
            }
            cmb.setSelectedItem(giaTri);
        }

        @Override
        public void setEnabled(boolean bat) {
            super.setEnabled(bat);
            cmb.setEnabled(bat);
        }
    }

    // Lưới một tuần như thời khóa biểu: 7 cột
    @SuppressWarnings("serial") // Lớp giao diện Swing
    public static class LuoiTuan extends JPanel {

        public static final String[] CA = {"Sáng", "Chiều"};
        private static final String[] THU = {"Thứ 2", "Thứ 3", "Thứ 4", "Thứ 5", "Thứ 6", "Thứ 7", "Chủ nhật"};
        private static final Color MAU_HOM_NAY = new Color(0xFFF9E1);

        private LocalDate thuHai; // Ngày đầu tuần đang xem
        private Runnable khiDoiTuan; // Việc cần làm khi chuyển sang tuần khác (nạp lại dữ liệu)
        private JLabel lblTuan = new JLabel("", SwingConstants.CENTER);
        private JPanel luoi = new JPanel(new GridBagLayout());
        private JPanel[][] o = new JPanel[2][7]; // Ô theo (ca, thứ)

        public LuoiTuan(Runnable khiDoiTuan) {
            super(new BorderLayout(0, 8));
            this.khiDoiTuan = khiDoiTuan;
            setOpaque(false);
            luoi.setOpaque(false);

            JButton btnTruoc = TienIch.nutPhu("‹ Tuần trước");
            JButton btnNay = TienIch.nutPhu("Tuần này");
            JButton btnSau = TienIch.nutPhu("Tuần sau ›");
            btnTruoc.addActionListener(e -> doiTuan(-7));
            btnNay.addActionListener(e -> datTuan(LocalDate.now()));
            btnSau.addActionListener(e -> doiTuan(7));
            lblTuan.setFont(TienIch.FONT_DAM);
            JPanel thanh = new JPanel(new BorderLayout(10, 0));
            thanh.setOpaque(false);
            thanh.add(btnTruoc, BorderLayout.WEST);
            thanh.add(lblTuan, BorderLayout.CENTER);
            JPanel phai = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 8, 0));
            phai.setOpaque(false);
            phai.add(btnNay);
            phai.add(btnSau);
            thanh.add(phai, BorderLayout.EAST);
            add(thanh, BorderLayout.NORTH);
            add(luoi, BorderLayout.CENTER);
            thuHai = layThuHai(LocalDate.now());
            dung();
        }

        // Thứ hai của tuần chứa ngày này
        public static LocalDate layThuHai(LocalDate ngay) {
            return ngay.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        }

        public LocalDate getThuHai() {
            return thuHai;
        }

        public LocalDate getChuNhat() {
            return thuHai.plusDays(6);
        }

        // Chuyển sang tuần chứa ngày này
        public void datTuan(LocalDate ngay) {
            thuHai = layThuHai(ngay);
            khiDoiTuan.run();
        }

        private void doiTuan(int soNgay) {
            thuHai = thuHai.plusDays(soNgay);
            khiDoiTuan.run();
        }

        // Xóa nội dung các ô
        public void xoaNoiDung() {
            lblTuan.setText("Tuần " + TienIch.ngay(thuHai) + " - " + TienIch.ngay(getChuNhat()));
            dung();
        }

        // Ô của một ngày và một ca
        public JPanel o(LocalDate ngay, String ca) {
            long lech = java.time.temporal.ChronoUnit.DAYS.between(thuHai, ngay);
            if (lech < 0 || lech > 6) {
                return null;
            }
            return o["Sáng".equals(ca) ? 0 : 1][(int) lech];
        }

        // Thêm một thành phần vào ô
        public void them(LocalDate ngay, String ca, Component c) {
            JPanel p = o(ngay, ca);
            if (p != null) {
                p.add(c);
            }
        }

        // Dựng lại khung lưới
        private void dung() {
            luoi.removeAll();
            GridBagConstraints g = new GridBagConstraints();
            g.fill = GridBagConstraints.BOTH;
            g.insets = new Insets(1, 1, 1, 1);
            g.gridy = 0;
            g.gridx = 0;
            g.weightx = 0;
            luoi.add(tieuDeCot("", null), g);
            for (int c = 0; c < 7; c++) {
                g.gridx = c + 1;
                g.weightx = 1;
                LocalDate ngay = thuHai.plusDays(c);
                luoi.add(tieuDeCot("<html><center>" + THU[c] + "<br>" + String.format("%02d/%02d", ngay.getDayOfMonth(), ngay.getMonthValue()) + "</center></html>", ngay), g);
            }
            for (int h = 0; h < 2; h++) {
                g.gridy = h + 1;
                g.gridx = 0;
                g.weightx = 0;
                g.weighty = 1;
                luoi.add(tieuDeCot(CA[h], null), g);
                for (int c = 0; c < 7; c++) {
                    JPanel p = new JPanel() {
                        @Override
                        public Dimension getPreferredSize() {
                            Dimension d = super.getPreferredSize();
                            return new Dimension(118, Math.max(d.height, 110)); // Ô rộng cố định, cao theo nội dung (tối thiểu 110)
                        }
                    };
                    p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
                    p.setBackground(thuHai.plusDays(c).equals(LocalDate.now()) ? MAU_HOM_NAY : Color.WHITE);
                    p.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(TienIch.MAU_VIEN), BorderFactory.createEmptyBorder(4, 4, 4, 4)));
                    o[h][c] = p;
                    g.gridx = c + 1;
                    g.weightx = 1;
                    luoi.add(p, g);
                }
            }
            luoi.revalidate();
            luoi.repaint();
        }

        private JLabel tieuDeCot(String chu, LocalDate ngay) {
            JLabel l = new JLabel(chu, SwingConstants.CENTER);
            l.setOpaque(true);
            l.setFont(TienIch.FONT_DAM);
            boolean homNay = ngay != null && ngay.equals(LocalDate.now());
            l.setBackground(homNay ? TienIch.MAU_CHINH : TienIch.MAU_DAM);
            l.setForeground(Color.WHITE);
            l.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
            if (chu.isEmpty()) {
                l.setPreferredSize(new Dimension(60, 30));
            } else if (ngay != null) {
                l.setPreferredSize(new Dimension(120, 44)); // Cột ngày không rộng quá ô bên dưới
            }
            return l;
        }
        // Một mục nhỏ trong ô
        public static JLabel muc(String chuHtml, Color nen, Color chu, Runnable khiBam) {
            JLabel l = new JLabel("<html><body style='width:98px'>" + chuHtml + "</body></html>");
            l.setOpaque(true);
            l.setBackground(nen);
            l.setForeground(chu);
            l.setFont(TienIch.FONT.deriveFont(12f));
            l.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 3, 0, 0, chu),
                    BorderFactory.createEmptyBorder(2, 5, 2, 3)));
            l.setAlignmentX(Component.LEFT_ALIGNMENT);
            if (khiBam != null) {
                l.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
                l.addMouseListener(new java.awt.event.MouseAdapter() {
                    @Override
                    public void mouseClicked(java.awt.event.MouseEvent e) {
                        khiBam.run();
                    }
                });
            }
            return l;
        }

        // Khoảng cách nhỏ giữa các mục trong ô
        public static Component cach() {
            return javax.swing.Box.createVerticalStrut(3);
        }
    }
}
