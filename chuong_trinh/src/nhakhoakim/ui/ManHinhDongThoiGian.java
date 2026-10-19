package nhakhoakim.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.BasicStroke;
import java.time.LocalDate;
import java.time.Period;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import nhakhoakim.dao.DongThoiGianService;
import nhakhoakim.dao.HoSoBenhAnService;
import nhakhoakim.entity.BenhNhan;
import nhakhoakim.entity.DongThoiGian;
import nhakhoakim.entity.DongThoiGian.SuKienDieuTri;
import nhakhoakim.entity.HoSoBenhAn;

// Màn hình dòng thời gian điều trị: toàn bộ quá trình điều trị của một bệnh nhân trên một trục thời gian (mới nhất ở trên)
@SuppressWarnings("serial") // lớp giao diện Swing, không cần serialVersionUID
public class ManHinhDongThoiGian extends JPanel {

    private static final String TAT_CA = "Tất cả";

    private DongThoiGianService dongThoiGianService = new DongThoiGianService();
    private HoSoBenhAnService hoSoService = new HoSoBenhAnService();

    private TienIch.ChonBenhNhan chonBenhNhan = new TienIch.ChonBenhNhan();
    private JComboBox<String> cmbLoc = new JComboBox<>(new String[] {TAT_CA, SuKienDieuTri.LOAI_KHAM, SuKienDieuTri.LOAI_LIEU_TRINH,
        SuKienDieuTri.LOAI_TAI_KHAM, SuKienDieuTri.LOAI_DANG_KY});   // lọc theo loại sự kiện
    private JPanel khuTomTat = new JPanel();                         // cảnh báo dị ứng và các ô tóm tắt
    private JPanel khuTrucThoiGian = new JPanel();                   // trục thời gian
    private DongThoiGian dongThoiGian;                               // dữ liệu đang xem

    public ManHinhDongThoiGian() {
        this(null);
    }

    // Mở sẵn dòng thời gian của một bệnh nhân (null thì để người dùng tự chọn)
    public ManHinhDongThoiGian(BenhNhan benhNhan) {
        JButton btnXem = TienIch.nutChinh("Xem dòng thời gian");
        btnXem.addActionListener(e -> xem());
        cmbLoc.setFont(TienIch.FONT);
        cmbLoc.addActionListener(e -> veTruc());
        // Đổi bệnh nhân ở ô chọn thì xóa trục đang hiện, tránh nhìn nhầm dữ liệu của người trước
        chonBenhNhan.addChonListener(e -> {
            dongThoiGian = null;
            khuTomTat.removeAll();
            khuTrucThoiGian.removeAll();
            khuTomTat.revalidate();
            khuTrucThoiGian.revalidate();
            repaint();
        });

        khuTomTat.setOpaque(false);
        khuTrucThoiGian.setOpaque(false);
        TienIch.xepManHinh(this, TienIch.tieuDe("Dòng thời gian điều trị"),
                TienIch.dong(TienIch.nhan("Bệnh nhân"), chonBenhNhan, btnXem, TienIch.nhan("Lọc"), cmbLoc), khuTomTat, khuTrucThoiGian);

        if (benhNhan != null) {
            chonBenhNhan.setBenhNhan(benhNhan); // chưa có trong danh sách thì thêm vào đầu rồi chọn
            xem();
        }
    }

    // Nạp dữ liệu của bệnh nhân đang chọn
    private void xem() {
        BenhNhan bn = chonBenhNhan.getBenhNhan();
        if (bn == null) {
            TienIch.thongTin("Vui lòng chọn một bệnh nhân.");
            return;
        }
        try {
            dongThoiGian = dongThoiGianService.xayDung(bn.getMaBN());
            HoSoBenhAn hs = hoSoService.timTheoBenhNhan(bn.getMaBN());
            veTomTat(bn, hs);
            veTruc();
        } catch (RuntimeException e) {
            TienIch.baoLoi(e);
        }
    }

    // Vẽ cảnh báo dị ứng và bốn ô tóm tắt
    private void veTomTat(BenhNhan bn, HoSoBenhAn hs) {
        khuTomTat.removeAll();
        String diUng = hs.coCanhBaoDiUng() ? TienIch.chu(hs.getCanhBaoDiUng()).trim() : "";
        JLabel ten = TienIch.chuThuong(bn.getMaBN() + " - " + bn.getHoTen(), true);
        String gan = dongThoiGian.getLanKhamGanNhat() == null ? "Chưa khám lần nào"
                : TienIch.ngay(dongThoiGian.getLanKhamGanNhat()) + " (" + cachDay(dongThoiGian.getLanKhamGanNhat()) + ")";
        JPanel hang = new JPanel(new java.awt.GridLayout(1, 4, 12, 0));
        hang.setOpaque(false);
        hang.add(oTomTat("Lượt khám đã hoàn thành", String.valueOf(dongThoiGian.getSoLuotKham())));
        hang.add(oTomTat("Lần khám gần nhất", gan));
        hang.add(oTomTat("Răng đã nhổ / đã phục hình", dongThoiGian.getSoRangDaNho() + " / " + dongThoiGian.getSoRangImplantCauRang()));
        hang.add(oTomTat("Liệu trình đang thực hiện", String.valueOf(dongThoiGian.getSoLieuTrinhDangLam())));
        if (diUng.isEmpty()) {
            TienIch.xep(khuTomTat, 10, ten, hang);
        } else {
            TienIch.xep(khuTomTat, 10, TienIch.thongBao("CẢNH BÁO DỊ ỨNG: " + diUng, TienIch.MAU_DO_NEN, TienIch.MAU_DO_CHU, true), ten, hang);
        }
        khuTomTat.revalidate();
    }

    // Một ô tóm tắt gồm nhãn nhỏ và giá trị lớn
    private JComponent oTomTat(String nhan, String giaTri) {
        JLabel lblNhan = new JLabel(nhan);
        lblNhan.setFont(TienIch.FONT.deriveFont(12f));
        lblNhan.setForeground(TienIch.MAU_XAM);
        JLabel lblGiaTri = new JLabel("<html>" + giaTri + "</html>");
        lblGiaTri.setFont(TienIch.FONT.deriveFont(java.awt.Font.BOLD, 15f));
        lblGiaTri.setForeground(TienIch.MAU_DAM);
        JPanel o = new JPanel(new BorderLayout(0, 4));
        o.setBackground(Color.WHITE);
        o.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(TienIch.MAU_VIEN),
                BorderFactory.createEmptyBorder(10, 14, 10, 14)));
        o.add(lblNhan, BorderLayout.NORTH);
        o.add(lblGiaTri, BorderLayout.CENTER);
        return o;
    }

    // Vẽ trục thời gian theo bộ lọc đang chọn
    private void veTruc() {
        khuTrucThoiGian.removeAll();
        if (dongThoiGian == null) {
            khuTrucThoiGian.revalidate();
            return;
        }
        String loc = (String) cmbLoc.getSelectedItem();
        java.util.List<java.awt.Component> cacDong = new java.util.ArrayList<>();
        for (SuKienDieuTri sk : dongThoiGian.getSuKien()) {
            if (TAT_CA.equals(loc) || sk.getLoai().equals(loc)) {
                cacDong.add(taoDong(sk));
            }
        }
        if (cacDong.isEmpty()) {
            cacDong.add(TienIch.chuThuong("Không có sự kiện nào.", false));
        }
        TienIch.xep(khuTrucThoiGian, 0, cacDong.toArray(new java.awt.Component[0]));
        khuTrucThoiGian.revalidate();
        khuTrucThoiGian.repaint();
    }

    // Cột chấm tròn nối bằng đường dọc của trục thời gian (chấm đặc nếu đã qua, chấm rỗng nếu sắp tới)
    private static class CotCham extends JComponent {
        private Color mau;
        private boolean daQua;

        CotCham(Color mau, boolean daQua) {
            this.mau = mau;
            this.daQua = daQua;
            setPreferredSize(new Dimension(24, 10));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int giua = getWidth() / 2;
            g2.setColor(new Color(0xD9E2E7));
            g2.fillRect(giua - 1, 14, 2, getHeight());
            if (daQua) {
                g2.setColor(mau);
                g2.fillOval(giua - 7, 2, 14, 14);
            } else {
                g2.setColor(Color.WHITE);
                g2.fillOval(giua - 7, 2, 14, 14);
                g2.setColor(mau);
                g2.setStroke(new BasicStroke(2f));
                g2.drawOval(giua - 6, 3, 12, 12);
            }
            g2.dispose();
        }
    }

    // Một dòng của trục thời gian: ngày giờ, chấm màu nối bằng đường dọc, thẻ nội dung
    private JComponent taoDong(SuKienDieuTri sk) {
        Color mau = mauTheoLoai(sk.getLoai());
        boolean coGio = !(sk.getThoiGian().getHour() == 0 && sk.getThoiGian().getMinute() == 0);
        JLabel lblNgay = new JLabel("<html><div style='text-align:right'>" + TienIch.ngay(sk.getThoiGian().toLocalDate())
                + (coGio ? "<br>" + TienIch.gio(sk.getThoiGian().toLocalTime()) : "") + "</div></html>");
        lblNgay.setFont(TienIch.FONT.deriveFont(12f));
        lblNgay.setForeground(TienIch.MAU_XAM);
        lblNgay.setHorizontalAlignment(JLabel.RIGHT);
        lblNgay.setVerticalAlignment(JLabel.TOP);
        lblNgay.setPreferredSize(new Dimension(100, 20));

        JLabel lblTieuDe = new JLabel(sk.getTieuDe() + (sk.isDaQua() ? "" : "  (sắp tới)"));
        lblTieuDe.setFont(TienIch.FONT_DAM);
        lblTieuDe.setForeground(mau);
        JComponent lblChiTiet = TienIch.doanChu(sk.getChiTiet(), Color.BLACK);
        JPanel the = new JPanel(new BorderLayout(0, 4));
        the.setBackground(Color.WHITE);
        the.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(TienIch.MAU_VIEN),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        the.add(lblTieuDe, BorderLayout.NORTH);
        the.add(lblChiTiet, BorderLayout.CENTER);

        JPanel viTriThe = new JPanel(new BorderLayout());
        viTriThe.setOpaque(false);
        viTriThe.setBorder(BorderFactory.createEmptyBorder(0, 0, 14, 0));
        viTriThe.add(the, BorderLayout.CENTER);

        JPanel traiVaCot = new JPanel(new BorderLayout(10, 0)); // ngày giờ bên trái, cột chấm tròn bên phải
        traiVaCot.setOpaque(false);
        traiVaCot.add(lblNgay, BorderLayout.WEST);
        traiVaCot.add(new CotCham(mau, sk.isDaQua()), BorderLayout.CENTER);
        JPanel dong = new JPanel(new BorderLayout(10, 0));
        dong.setOpaque(false);
        dong.add(traiVaCot, BorderLayout.WEST);
        dong.add(viTriThe, BorderLayout.CENTER);
        return dong;
    }

    // Màu của từng loại sự kiện
    private Color mauTheoLoai(String loai) {
        if (SuKienDieuTri.LOAI_KHAM.equals(loai)) {
            return new Color(0x0E7C86);
        }
        if (SuKienDieuTri.LOAI_LIEU_TRINH.equals(loai)) {
            return new Color(0x7E57C2);
        }
        if (SuKienDieuTri.LOAI_TAI_KHAM.equals(loai)) {
            return new Color(0xE09B00);
        }
        return new Color(0x78909C);
    }

    // Cách đây bao lâu, ví dụ "2 năm 3 tháng trước", "hôm nay"
    private String cachDay(LocalDate ngay) {
        Period p = Period.between(ngay, LocalDate.now());
        if (p.isNegative() || (p.getYears() == 0 && p.getMonths() == 0 && p.getDays() == 0)) {
            return "hôm nay";
        }
        if (p.getYears() > 0) {
            return p.getYears() + " năm" + (p.getMonths() > 0 ? " " + p.getMonths() + " tháng" : "") + " trước";
        }
        if (p.getMonths() > 0) {
            return p.getMonths() + " tháng" + (p.getDays() > 0 ? " " + p.getDays() + " ngày" : "") + " trước";
        }
        return p.getDays() + " ngày trước";
    }
}
