package nhakhoakim.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.List;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import nhakhoakim.MainApp;
import nhakhoakim.controller.BenhNhanController;
import nhakhoakim.dao.NghiepVuException;
import nhakhoakim.entity.BenhNhan;
import nhakhoakim.entity.DichVu;

// Các hàm dùng chung cho các màn hình Swing: định dạng ngày giờ, nhãn, nút, bảng, form, hộp thông báo, ô chọn bệnh nhân
@SuppressWarnings("serial") // lớp giao diện Swing, không cần serialVersionUID
public class TienIch {

    public static final Color MAU_CHINH = new Color(0x0E7C86);
    public static final Color MAU_DAM = new Color(0x0B4F6C);
    public static final Color MAU_NEN = new Color(0xF3F6F8);
    public static final Color MAU_XAM = new Color(0x6B7C8A);
    public static final Color MAU_VIEN = new Color(0xD3DFE3);
    public static final Color MAU_DO_NEN = new Color(0xFDECEA);
    public static final Color MAU_DO_CHU = new Color(0xB71C1C);
    public static final Color MAU_VANG_NEN = new Color(0xFFF4E0);
    public static final Color MAU_VANG_CHU = new Color(0x8A5A00);

    public static final Font FONT = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_DAM = new Font("Segoe UI", Font.BOLD, 14);

    private static final DateTimeFormatter NGAY = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    // Dùng khi đọc ngày người dùng gõ: nghiêm ngặt để ngày không có thật (31/02/1995) bị từ chối thay vì tự đổi thành 28/02/1995
    private static final DateTimeFormatter NGAY_NGHIEM = DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter GIO = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter NGAY_GIO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // Chạy kiểm thử không có màn hình thì không hiện được hộp thoại: hộp cuối cùng được ghi vào đây và hàm xacNhan trả về traLoiXacNhan
    public static String hopCuoi = "";
    public static boolean traLoiXacNhan = true;
    public static String nhapTuDong = null;

    // ===================== Định dạng =====================

    // Ngày dạng dd/MM/yyyy; null thì trả về chuỗi rỗng
    public static String ngay(LocalDate d) {
        return d == null ? "" : d.format(NGAY);
    }

    // Giờ dạng HH:mm
    public static String gio(LocalTime t) {
        return t == null ? "" : t.format(GIO);
    }

    // Ngày giờ dạng dd/MM/yyyy HH:mm
    public static String ngayGio(LocalDateTime t) {
        return t == null ? "" : t.format(NGAY_GIO);
    }

    // Chuỗi null thì đổi thành rỗng để hiện lên màn hình
    public static String chu(String s) {
        return s == null ? "" : s;
    }

    // Số tiền có dấu chấm ngăn cách hàng nghìn, ví dụ 1.500.000 (dùng Locale cố định nên không phụ thuộc cài đặt máy)
    public static String tien(long soTien) {
        return String.format(Locale.GERMANY, "%,d", soTien);
    }

    // Đọc ngày người dùng gõ vào ô chữ (dd/MM/yyyy): ô trống thì trả về null, gõ sai thì báo lỗi nghiệp vụ
    public static LocalDate docNgay(String chuoi) {
        if (chuoi == null || chuoi.trim().isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(chuoi.trim(), NGAY_NGHIEM);
        } catch (DateTimeParseException e) {
            throw new NghiepVuException("Ngày \"" + chuoi.trim() + "\" không đúng định dạng dd/MM/yyyy (ví dụ 25/12/1990)");
        }
    }

    // ===================== Chữ, nút, khối =====================

    // Tiêu đề lớn của màn hình
    public static JLabel tieuDe(String chu) {
        JLabel lbl = new JLabel(chu);
        lbl.setFont(FONT.deriveFont(Font.BOLD, 22f));
        return lbl;
    }

    // Nhãn nhỏ màu xám cho tên ô nhập
    public static JLabel nhan(String chu) {
        JLabel lbl = new JLabel(chu);
        lbl.setFont(FONT_DAM);
        lbl.setForeground(MAU_XAM);
        return lbl;
    }

    // Nhãn thường (chữ đậm hoặc không)
    public static JLabel chuThuong(String chu, boolean dam) {
        JLabel lbl = new JLabel(chu);
        lbl.setFont(dam ? FONT_DAM : FONT);
        return lbl;
    }

    // Nút bo góc: chinh = true là nút chính nền xanh chữ trắng, false là nút phụ nền trắng viền xanh
    private static class Nut extends JButton {
        private boolean chinh;
        private Color mau = MAU_CHINH; // màu viền và chữ của nút phụ

        Nut(String chu, boolean chinh, Color mau) {
            super(chu);
            this.chinh = chinh;
            this.mau = mau;
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setOpaque(false);
            setFont(FONT_DAM);
            setForeground(chinh ? Color.WHITE : mau);
            setBorder(BorderFactory.createEmptyBorder(7, 16, 7, 16));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth() - 1;
            int h = getHeight() - 1;
            if (!isEnabled()) {
                g2.setColor(new Color(0xE3E8EA));
                g2.fillRoundRect(0, 0, w, h, 10, 10);
                setForeground(new Color(0x9AA8AE));
            } else if (chinh) {
                g2.setColor(getModel().isPressed() ? MAU_DAM : MAU_CHINH);
                g2.fillRoundRect(0, 0, w, h, 10, 10);
                setForeground(Color.WHITE);
            } else {
                g2.setColor(getModel().isPressed() ? new Color(0xE3F1F2) : Color.WHITE);
                g2.fillRoundRect(0, 0, w, h, 10, 10);
                g2.setColor(mau);
                g2.drawRoundRect(0, 0, w, h, 10, 10);
                setForeground(mau);
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }

    // Nút chính màu xanh
    public static JButton nutChinh(String chu) {
        return new Nut(chu, true, MAU_CHINH);
    }

    // Đổi một nút giữa dạng chính (nền xanh) và dạng phụ (viền xanh), dùng cho cặp nút công tắc
    public static void datNutChinh(JButton nut, boolean chinh) {
        if (nut instanceof Nut) {
            ((Nut) nut).chinh = chinh;
            nut.repaint();
        }
    }

    // Nút phụ màu trắng viền xanh
    public static JButton nutPhu(String chu) {
        return new Nut(chu, false, MAU_CHINH);
    }

    // Nút phụ màu đỏ cho thao tác hủy, xóa
    public static JButton nutDo(String chu) {
        return new Nut(chu, false, MAU_DO_CHU);
    }

    // Nút trông như đường dẫn (chữ xanh, gạch chân khi trỏ chuột vào)
    public static JButton lienKet(String chu) {
        JButton b = new JButton("<html><u>" + chu + "</u></html>");
        b.setBorderPainted(false);
        b.setContentAreaFilled(false);
        b.setFocusPainted(false);
        b.setForeground(new Color(0x1565C0));
        b.setFont(FONT);
        b.setHorizontalAlignment(JButton.LEFT);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    // Khối chữ nhiều dòng, nền màu (dùng cho cảnh báo đỏ, nhắc vàng, hướng dẫn); chữ tự xuống dòng theo bề rộng
    public static JComponent thongBao(String chu, Color nen, Color mauChu, boolean dam) {
        JTextArea o = new JTextArea(chu);
        o.setEditable(false);
        o.setFocusable(false);
        o.setLineWrap(true);
        o.setWrapStyleWord(true);
        o.setFont(dam ? FONT_DAM : FONT);
        o.setForeground(mauChu);
        o.setBackground(nen);
        o.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        return o;
    }

    // Đoạn chữ nhiều dòng không nền (dùng cho hướng dẫn)
    public static JComponent doanChu(String chu, Color mauChu) {
        JTextArea o = new JTextArea(chu);
        o.setEditable(false);
        o.setFocusable(false);
        o.setLineWrap(true);
        o.setWrapStyleWord(true);
        o.setFont(FONT);
        o.setForeground(mauChu);
        o.setOpaque(false);
        return o;
    }

    // Ô nhập một dòng có độ rộng cho trước
    public static JTextField oNhap(int soCot) {
        JTextField t = new JTextField(soCot);
        t.setFont(FONT);
        return t;
    }

    // Ô nhập nhiều dòng (đặt trong khung cuộn)
    public static JTextArea oNhieuDong(int soHang, int soCot) {
        JTextArea t = new JTextArea(soHang, soCot);
        t.setFont(FONT);
        t.setLineWrap(true);
        t.setWrapStyleWord(true);
        return t;
    }

    // Khung cuộn bao một ô nhập nhiều dòng
    public static JScrollPane cuonOChu(JTextArea o) {
        JScrollPane c = new JScrollPane(o);
        c.setBorder(BorderFactory.createLineBorder(MAU_VIEN));
        return c;
    }

    // Một dòng ngang các thành phần (nút, nhãn...), căn trái
    public static JPanel dong(Component... cacCon) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        p.setOpaque(false);
        for (Component c : cacCon) {
            p.add(c);
        }
        return p;
    }

    // Thẻ trắng viền mảnh chứa nội dung bên trong (xếp dọc các thành phần truyền vào)
    public static JPanel the(Component... cacCon) {
        JPanel p = new JPanel();
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(MAU_VIEN),
                BorderFactory.createEmptyBorder(14, 16, 14, 16)));
        xep(p, 10, cacCon);
        return p;
    }

    // Xếp các thành phần theo chiều dọc: mỗi thành phần chiếm hết bề rộng, cao đúng bằng nội dung, cách nhau khoangCach điểm ảnh
    public static void xep(JPanel p, int khoangCach, Component... cacCon) {
        p.setLayout(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0;
        g.weightx = 1;
        g.fill = GridBagConstraints.HORIZONTAL;
        g.anchor = GridBagConstraints.NORTHWEST;
        int hang = 0;
        for (Component c : cacCon) {
            g.gridy = hang++;
            g.insets = new Insets(0, 0, khoangCach, 0);
            p.add(c, g);
        }
        GridBagConstraints day = new GridBagConstraints();
        day.gridx = 0;
        day.gridy = hang;
        day.weighty = 1;
        JPanel phanDem = new JPanel(); // phần đệm đẩy các thành phần lên trên
        phanDem.setOpaque(false);
        p.add(phanDem, day);
    }

    // Xếp màn hình: viền lề 24 điểm ảnh, nền trong suốt, các thành phần cách nhau 14 điểm ảnh
    public static void xepManHinh(JPanel p, Component... cacCon) {
        p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        xep(p, 14, cacCon);
    }

    // Form hai cột: cột trái là tên ô, cột phải là ô nhập
    public static JPanel taoForm(String[] ten, Component[] oNhap) {
        JPanel p = new JPanel(new GridBagLayout());
        p.setOpaque(false);
        for (int i = 0; i < ten.length; i++) {
            GridBagConstraints g = new GridBagConstraints();
            g.gridx = 0;
            g.gridy = i;
            g.anchor = GridBagConstraints.NORTHWEST;
            g.insets = new Insets(6, 0, 6, 14);
            p.add(nhan(ten[i]), g);
            GridBagConstraints h = new GridBagConstraints();
            h.gridx = 1;
            h.gridy = i;
            h.weightx = 1;
            h.fill = GridBagConstraints.HORIZONTAL;
            h.anchor = GridBagConstraints.WEST;
            h.insets = new Insets(4, 0, 4, 0);
            p.add(oNhap[i], h);
        }
        return p;
    }

    // ===================== Bảng =====================

    // Bảng dữ liệu mỗi dòng là một String[]; không sửa trực tiếp, không sắp xếp (để vị trí dòng chọn luôn khớp với danh sách dữ liệu);
    // khi chưa có dòng nào thì vẽ câu khiTrong ở giữa bảng
    public static JTable taoBang(String[] tenCot, int[] rong, String khiTrong) {
        DefaultTableModel mo = new DefaultTableModel(tenCot, 0) {
            @Override
            public boolean isCellEditable(int hang, int cot) {
                return false;
            }
        };
        JTable bang = new JTable(mo) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getRowCount() == 0 && khiTrong != null) {
                    g.setColor(MAU_XAM);
                    g.setFont(FONT);
                    g.drawString(khiTrong, 12, 24);
                }
            }
        };
        bang.setFont(FONT);
        bang.setRowHeight(26);
        bang.getTableHeader().setFont(FONT_DAM);
        bang.getTableHeader().setReorderingAllowed(false);
        bang.setAutoCreateRowSorter(false);
        bang.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        bang.setFillsViewportHeight(true);
        bang.setGridColor(MAU_VIEN);
        for (int i = 0; i < rong.length; i++) {
            bang.getColumnModel().getColumn(i).setPreferredWidth(rong[i]);
        }
        return bang;
    }

    // Thay toàn bộ dòng của bảng bằng danh sách mới
    public static void napBang(JTable bang, List<String[]> dong) {
        DefaultTableModel mo = (DefaultTableModel) bang.getModel();
        mo.setRowCount(0);
        for (String[] d : dong) {
            mo.addRow(d);
        }
    }

    // Khung cuộn bao một bảng (hoặc danh sách), cao cho trước
    public static JScrollPane cuonBang(JComponent bang, int cao) {
        JScrollPane c = new JScrollPane(bang);
        c.setPreferredSize(new Dimension(900, cao));
        c.setBorder(BorderFactory.createLineBorder(MAU_VIEN));
        return c;
    }

    // Cột có chữ dài thì xuống dòng thay vì bị cắt bằng dấu ba chấm: chiSo là vị trí cột trong bảng
    public static void cotXuongDong(JTable bang, int chiSo) {
        bang.getColumnModel().getColumn(chiSo).setCellRenderer(new TableCellRenderer() {
            private JTextArea o = new JTextArea();

            @Override
            public Component getTableCellRendererComponent(JTable b, Object giaTri, boolean chon, boolean coFocus, int hang, int cot) {
                o.setFont(FONT);
                o.setLineWrap(true);
                o.setWrapStyleWord(true);
                o.setText(giaTri == null ? "" : giaTri.toString());
                o.setBackground(chon ? b.getSelectionBackground() : b.getBackground());
                o.setForeground(chon ? b.getSelectionForeground() : b.getForeground());
                o.setSize(b.getColumnModel().getColumn(cot).getWidth(), Short.MAX_VALUE);
                int cao = Math.max(26, o.getPreferredSize().height + 2);
                if (b.getRowHeight(hang) < cao) {
                    b.setRowHeight(hang, cao);
                }
                return o;
            }
        });
    }

    // ===================== Ô chọn =====================

    // Ô chọn dịch vụ: hiện tên dịch vụ; chưa chọn (chỉ số -1) thì hiện câu khiChuaChon
    public static JComboBox<DichVu> taoChonDichVu(List<DichVu> danhSach, String khiChuaChon) {
        JComboBox<DichVu> cmb = new JComboBox<>(new DefaultComboBoxModel<>(danhSach.toArray(new DichVu[0])));
        cmb.setFont(FONT);
        cmb.setRenderer(new ListCellRenderer<DichVu>() {
            private javax.swing.DefaultListCellRenderer goc = new javax.swing.DefaultListCellRenderer();

            @Override
            public Component getListCellRendererComponent(JList<? extends DichVu> ds, DichVu dv, int vt, boolean chon, boolean coFocus) {
                JLabel l = (JLabel) goc.getListCellRendererComponent(ds, dv, vt, chon, coFocus);
                l.setText(dv == null ? khiChuaChon : dv.getTenDichVu());
                return l;
            }
        });
        cmb.setSelectedIndex(-1);
        return cmb;
    }

    // Ô chọn bệnh nhân: một ô gõ để tìm (mã, họ tên hoặc số điện thoại, nhấn Enter hoặc bấm Tìm) và một danh sách kết quả để chọn
    public static class ChonBenhNhan extends JPanel {
        private JTextField txtTim = oNhap(20);
        private JComboBox<BenhNhan> cmb = new JComboBox<>();
        private BenhNhanController benhNhanService = new BenhNhanController();

        public ChonBenhNhan() {
            super(new FlowLayout(FlowLayout.LEFT, 8, 0));
            setOpaque(false);
            txtTim.setToolTipText("Gõ mã, họ tên hoặc số điện thoại rồi nhấn Enter để tìm");
            JButton btnTim = nutPhu("Tìm");
            ActionListener tim = e -> tim();
            txtTim.addActionListener(tim);
            btnTim.addActionListener(tim);
            cmb.setFont(FONT);
            cmb.setPreferredSize(new Dimension(300, 30));
            cmb.setRenderer(new ListCellRenderer<BenhNhan>() {
                private javax.swing.DefaultListCellRenderer goc = new javax.swing.DefaultListCellRenderer();

                @Override
                public Component getListCellRendererComponent(JList<? extends BenhNhan> ds, BenhNhan bn, int vt, boolean chon, boolean coFocus) {
                    JLabel l = (JLabel) goc.getListCellRendererComponent(ds, bn, vt, chon, coFocus);
                    l.setText(bn == null ? "(chọn bệnh nhân)" : bn.getMaBN() + " - " + bn.getHoTen());
                    return l;
                }
            });
            add(txtTim);
            add(btnTim);
            add(cmb);
            napDanhSach(benhNhanService.timKiem(""));
        }

        // Tìm theo chữ đang gõ, thay danh sách bằng kết quả; không có ai thì báo
        private void tim() {
            try {
                String tuKhoa = txtTim.getText().trim();
                List<BenhNhan> ketQua = benhNhanService.timKiem(tuKhoa);
                if (ketQua.isEmpty()) {
                    thongTin("Không tìm thấy bệnh nhân nào khớp với \"" + tuKhoa + "\".");
                    return;
                }
                napDanhSach(ketQua);
                cmb.showPopup();
            } catch (RuntimeException e) {
                baoLoi(e);
            }
        }

        private void napDanhSach(List<BenhNhan> ds) {
            cmb.setModel(new DefaultComboBoxModel<>(ds.toArray(new BenhNhan[0])));
            cmb.setSelectedIndex(-1);
        }

        // Bệnh nhân đang chọn; chưa chọn thì trả về null
        public BenhNhan getBenhNhan() {
            return (BenhNhan) cmb.getSelectedItem();
        }

        // Chọn người có cùng mã; chưa có trong danh sách thì thêm vào đầu rồi chọn; bn null thì bỏ chọn
        public void setBenhNhan(BenhNhan bn) {
            if (bn == null) {
                cmb.setSelectedIndex(-1);
                return;
            }
            DefaultComboBoxModel<BenhNhan> mo = (DefaultComboBoxModel<BenhNhan>) cmb.getModel();
            for (int i = 0; i < mo.getSize(); i++) {
                if (mo.getElementAt(i).getMaBN().equals(bn.getMaBN())) {
                    cmb.setSelectedIndex(i);
                    return;
                }
            }
            mo.insertElementAt(bn, 0);
            cmb.setSelectedIndex(0);
        }

        // Gọi hàm này mỗi khi người dùng chọn một bệnh nhân khác trong danh sách
        public void addChonListener(ActionListener l) {
            cmb.addActionListener(l);
        }
    }

    // Khối thông báo "chưa có lượt khám đang khám" kèm nút mở màn hình khám (dùng cho màn hình cần lượt khám)
    public static JComponent khoiChuaCoLuotKham() {
        JPanel p = new JPanel(new BorderLayout(0, 10));
        p.setOpaque(false);
        p.add(doanChu("Chưa có lượt khám đang khám. Hãy bắt đầu khám ở \"Khám và ghi nhận điều trị\" trước.", Color.BLACK), BorderLayout.NORTH);
        JButton btnKham = nutChinh("Đến màn hình khám");
        btnKham.addActionListener(e -> MainApp.doiNoiDung(new ManHinhKhamDieuTri()));
        p.add(dong(btnKham), BorderLayout.CENTER);
        return p;
    }

    // ===================== Hộp thoại =====================

    // Báo lỗi bắt được từ service: lỗi nghiệp vụ hiện nguyên câu, lỗi hệ thống thì ghi Console và hiện câu chung
    public static void baoLoi(RuntimeException e) {
        if (e instanceof NghiepVuException) {
            loi(e.getMessage());
        } else {
            e.printStackTrace();
            loi("Có lỗi hệ thống, vui lòng thử lại. (" + e.getMessage() + ")");
        }
    }

    // Hộp thông báo lỗi
    public static void loi(String noiDung) {
        hienHop(noiDung, "Có lỗi", JOptionPane.ERROR_MESSAGE);
    }

    // Hộp thông báo thông tin
    public static void thongTin(String noiDung) {
        hienHop(noiDung, "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }

    // Hộp xác nhận Có / Không; trả về true nếu chọn Có
    public static boolean xacNhan(String noiDung) {
        if (GraphicsEnvironment.isHeadless()) {
            hopCuoi = noiDung;
            System.out.println("[Hộp xác nhận] " + noiDung);
            return traLoiXacNhan;
        }
        Object[] nut = {"Có", "Không"};
        int kq = JOptionPane.showOptionDialog(MainApp.cuaSo(), noiDungHop(noiDung), "Xác nhận", JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE, null, nut, nut[1]);
        return kq == JOptionPane.YES_OPTION;
    }

    // Hộp nhập một dòng chữ (ví dụ lý do hủy); bấm Hủy thì trả về null
    public static String nhapChu(String tieuDe, String loiNhac) {
        if (GraphicsEnvironment.isHeadless()) {
            System.out.println("[Hộp nhập] " + loiNhac);
            return nhapTuDong;
        }
        return JOptionPane.showInputDialog(MainApp.cuaSo(), loiNhac, tieuDe, JOptionPane.QUESTION_MESSAGE);
    }

    private static void hienHop(String noiDung, String tieuDe, int loai) {
        hopCuoi = noiDung;
        if (GraphicsEnvironment.isHeadless()) {
            System.out.println("[Hộp thoại] " + tieuDe + ": " + noiDung);
            return;
        }
        JOptionPane.showMessageDialog(MainApp.cuaSo(), noiDungHop(noiDung), tieuDe, loai);
    }

    // Nội dung dài thì cho xuống dòng trong hộp thoại (JOptionPane không tự xuống dòng)
    private static Object noiDungHop(String noiDung) {
        JTextArea o = new JTextArea(noiDung);
        o.setEditable(false);
        o.setLineWrap(true);
        o.setWrapStyleWord(true);
        o.setOpaque(false);
        o.setFont(FONT);
        o.setColumns(40);
        o.setRows(Math.min(14, Math.max(2, noiDung.length() / 40 + noiDung.split("\n").length)));
        return o;
    }
}
