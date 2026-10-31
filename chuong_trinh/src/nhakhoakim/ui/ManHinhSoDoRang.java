package nhakhoakim.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import nhakhoakim.controller.BenhNhanController;
import nhakhoakim.controller.DichVuController;
import nhakhoakim.controller.HoSoBenhAnController;
import nhakhoakim.controller.LuotKhamController;
import nhakhoakim.controller.NhaSiPhuTrachController;
import nhakhoakim.dao.PhienLamViec;
import nhakhoakim.controller.RangDieuTriController;
import nhakhoakim.entity.BenhNhan;
import nhakhoakim.entity.DichVu;
import nhakhoakim.entity.HangSo;
import nhakhoakim.entity.HoSoBenhAn;
import nhakhoakim.entity.LuotKham;
import nhakhoakim.entity.RangDieuTri;

// Màn hình sơ đồ răng điều trị: răng vĩnh viễn (32 răng) và răng sữa (20 răng) theo hệ FDI,
// bấm một răng để ghi nhận tình trạng trong lượt khám đang khám
@SuppressWarnings("serial") // lớp giao diện Swing, không cần serialVersionUID
public class ManHinhSoDoRang extends JPanel {

    private static final int TUOI_HIEN_RANG_SUA = 6;          // bệnh nhân dưới tuổi này thì mặc định hiện răng sữa

    // Màu tô cho từng trạng thái răng
    private static final Map<String, Color> MAU = new HashMap<>();

    static {
        MAU.put(HangSo.RANG_BINH_THUONG, Color.WHITE);
        MAU.put(HangSo.RANG_SAU, new Color(0xEF9A9A));
        MAU.put(HangSo.RANG_DA_TRAM, new Color(0x90CAF9));
        MAU.put(HangSo.RANG_BOC_SU, new Color(0xFFE082));
        MAU.put(HangSo.RANG_DA_NHO, new Color(0xBDBDBD));
        MAU.put(HangSo.RANG_IMPLANT, new Color(0xA5D6A7));
        MAU.put(HangSo.RANG_CAU_RANG, new Color(0xFFCC80));
    }

    // Nút của một răng: ô vuông tô màu theo tình trạng, viền xanh đậm khi đang được chọn
    private static class NutRang extends JButton {
        private Color mau = Color.WHITE;
        private boolean dangChon = false;

        NutRang(int so) {
            super(String.valueOf(so));
            setPreferredSize(new Dimension(48, 48));
            setFont(TienIch.FONT_DAM);
            setMargin(new java.awt.Insets(0, 0, 0, 0)); // bỏ lề mặc định để số răng không bị cắt thành dấu ba chấm
            setFocusPainted(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setForeground(Color.BLACK);
        }

        void datTrangThai(Color mau, boolean dangChon) {
            this.mau = mau;
            this.dangChon = dangChon;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(mau);
            g2.fillRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 8, 8);
            g2.setColor(dangChon ? TienIch.MAU_CHINH : new Color(0x888888));
            g2.setStroke(new BasicStroke(dangChon ? 3f : 1f));
            g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 8, 8);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private RangDieuTriController rangService = new RangDieuTriController();
    private LuotKhamController luotKhamService = new LuotKhamController();
    private DichVuController dichVuService = new DichVuController();
    private HoSoBenhAnController hoSoService = new HoSoBenhAnController();
    private BenhNhanController benhNhanService = new BenhNhanController();
    private NhaSiPhuTrachController nhaSiService = new NhaSiPhuTrachController();

    private LuotKham luotKham;                                       // lượt khám đang khám
    private Map<Integer, NutRang> nutRang = new HashMap<>();         // nút của từng răng (cả hai bộ), khóa là số FDI
    private Map<Integer, RangDieuTri> tinhTrang = new HashMap<>();   // tình trạng hiện tại của từng răng
    private Map<String, String> tenDichVu = new HashMap<>();         // mã dịch vụ -> tên dịch vụ
    private int rangDangChon = 0;                                    // số FDI đang chọn (0 là chưa chọn)

    private JPanel khungVinhVien = new JPanel();                     // sơ đồ răng vĩnh viễn
    private JPanel khungSua = new JPanel();                          // sơ đồ răng sữa
    private JButton btnVinhVien = TienIch.nutChinh("Răng vĩnh viễn");
    private JButton btnSua = TienIch.nutPhu("Răng sữa");

    private JLabel lblRang = TienIch.chuThuong("<html>Chưa chọn răng</html>", true);
    private JComboBox<String> cmbTrangThai = new JComboBox<>();
    private JComboBox<String> cmbMatRang = new JComboBox<>(new String[] {"", "Gần", "Xa", "Nhai", "Ngoài", "Trong"});
    private JTextField txtVatLieu = TienIch.oNhap(26);
    private JComboBox<DichVu> cmbDichVu;                             // dịch vụ đã làm (chưa chọn nghĩa là không có dịch vụ)
    private JTextField txtGhiChu = TienIch.oNhap(26);
    private JComboBox<RangDieuTri> cmbBanGhi = new JComboBox<>();    // các bản ghi của răng đang chọn trong lượt khám này
    private JButton btnGo = TienIch.nutPhu("Gỡ bản ghi đã chọn");

    public ManHinhSoDoRang() {
        List<Component> cacKhoi = new ArrayList<>();
        cacKhoi.add(TienIch.tieuDe("Sơ đồ răng điều trị"));
        try {
            luotKham = luotKhamService.layDangKham(PhienLamViec.getNhanVienHienTai().getMaNV());
            if (luotKham == null) {
                cacKhoi.add(TienIch.khoiChuaCoLuotKham());
            } else {
                dung(cacKhoi);
            }
        } catch (RuntimeException e) {
            TienIch.baoLoi(e);
        }
        TienIch.xepManHinh(this, cacKhoi.toArray(new Component[0]));
        if (luotKham != null) {
            hienBoRang(laTreDuoiSauTuoi());
            napTinhTrang();
        }
    }

    // Dựng công tắc răng vĩnh viễn / răng sữa, hai sơ đồ và form ghi nhận
    private void dung(List<Component> cacKhoi) {
        HoSoBenhAn hoSoKham = hoSoService.timTheoMa(luotKham.getMaHoSo());
        BenhNhan benhNhanKham = benhNhanService.timTheoMa(hoSoKham.getMaBN());
        cacKhoi.add(DongThongTinLuotKham.tao(benhNhanKham == null ? hoSoKham.getMaBN() : benhNhanKham.getHoTen(),
                nhaSiService.layTen(luotKham.getMaNhanVienPhuTrach()), luotKham));
        // răng vĩnh viễn, hàm trên: 18..11 | 21..28 ; hàm dưới: 48..41 | 31..38
        khungVinhVien.setOpaque(false);
        TienIch.xep(khungVinhVien, 8, taoHang(new int[] {18, 17, 16, 15, 14, 13, 12, 11}, new int[] {21, 22, 23, 24, 25, 26, 27, 28}),
                taoHang(new int[] {48, 47, 46, 45, 44, 43, 42, 41}, new int[] {31, 32, 33, 34, 35, 36, 37, 38}));
        // răng sữa, hàm trên: 55..51 | 61..65 ; hàm dưới: 85..81 | 71..75
        khungSua.setOpaque(false);
        TienIch.xep(khungSua, 8, taoHang(new int[] {55, 54, 53, 52, 51}, new int[] {61, 62, 63, 64, 65}),
                taoHang(new int[] {85, 84, 83, 82, 81}, new int[] {71, 72, 73, 74, 75}));

        btnVinhVien.addActionListener(e -> hienBoRang(false));
        btnSua.addActionListener(e -> hienBoRang(true));
        cacKhoi.add(TienIch.dong(btnVinhVien, btnSua));
        cacKhoi.add(khungVinhVien);
        cacKhoi.add(khungSua);

        JPanel chuThich = TienIch.dong();
        for (String tt : rangService.layDanhSachTrangThai()) {
            JLabel o = new JLabel("    ");
            o.setOpaque(true);
            o.setBackground(MAU.getOrDefault(tt, Color.WHITE));
            o.setBorder(BorderFactory.createLineBorder(new Color(0x888888)));
            chuThich.add(TienIch.dong(o, TienIch.chuThuong(tt, false)));
        }
        cacKhoi.add(chuThich);

        for (String tt : rangService.layDanhSachTrangThai()) {
            cmbTrangThai.addItem(tt);
        }
        cmbTrangThai.setFont(TienIch.FONT);
        cmbMatRang.setFont(TienIch.FONT);
        List<DichVu> tatCaDichVu = dichVuService.layTatCa();
        for (DichVu dv : tatCaDichVu) {
            tenDichVu.put(dv.getMaDichVu(), dv.getTenDichVu());
        }
        cmbDichVu = TienIch.taoChonDichVu(tatCaDichVu, "(không có dịch vụ)");

        cmbBanGhi.setFont(TienIch.FONT);
        cmbBanGhi.setPreferredSize(new Dimension(300, 28));
        cmbBanGhi.setRenderer(new ListCellRenderer<RangDieuTri>() {
            private javax.swing.DefaultListCellRenderer goc = new javax.swing.DefaultListCellRenderer();

            @Override
            public Component getListCellRendererComponent(JList<? extends RangDieuTri> ds, RangDieuTri b, int vt, boolean chon, boolean coFocus) {
                JLabel l = (JLabel) goc.getListCellRendererComponent(ds, b, vt, chon, coFocus);
                if (b == null) {
                    l.setText("(chọn bản ghi ghi nhầm)");
                } else {
                    String dv = b.getMaDichVu() == null ? "không dịch vụ" : tenDichVu.getOrDefault(b.getMaDichVu(), b.getMaDichVu());
                    l.setText(b.getTrangThai() + " | " + dv + (b.getMatRang() == null ? "" : " | mặt " + b.getMatRang()));
                }
                return l;
            }
        });
        cmbBanGhi.setSelectedIndex(-1);
        btnGo.addActionListener(e -> goBanGhi());

        JPanel form = TienIch.taoForm(new String[] {"Trạng thái", "Mặt răng", "Vật liệu", "Dịch vụ đã làm", "Ghi chú"},
                new Component[] {cmbTrangThai, cmbMatRang, txtVatLieu, cmbDichVu, txtGhiChu});
        JButton btnGhi = TienIch.nutChinh("Ghi nhận răng");
        btnGhi.addActionListener(e -> ghiNhan());
        JPanel the = TienIch.the(lblRang, form, TienIch.dong(btnGhi), TienIch.nhan("Đã ghi trên răng này trong lượt khám (gỡ nếu ghi nhầm)"),
                TienIch.dong(cmbBanGhi, btnGo));
        JPanel gioiHan = new JPanel(new java.awt.BorderLayout()); // thẻ chỉ rộng vừa phải, không kéo hết chiều ngang cửa sổ
        gioiHan.setOpaque(false);
        gioiHan.add(the, java.awt.BorderLayout.WEST);
        the.setPreferredSize(new Dimension(560, the.getPreferredSize().height));
        cacKhoi.add(gioiHan);
    }

    // Bệnh nhân của lượt khám có dưới 6 tuổi không (chưa biết ngày sinh thì coi như không)
    private boolean laTreDuoiSauTuoi() {
        HoSoBenhAn hs = hoSoService.timTheoMa(luotKham.getMaHoSo());
        BenhNhan bn = benhNhanService.timTheoMa(hs.getMaBN());
        if (bn == null || bn.getNgaySinh() == null) {
            return false;
        }
        int tuoi = bn.tinhTuoi();
        return tuoi >= 0 && tuoi < TUOI_HIEN_RANG_SUA;
    }

    // Hiện sơ đồ răng sữa (sua = true) hoặc răng vĩnh viễn, nút đang chọn được tô đậm
    private void hienBoRang(boolean sua) {
        khungVinhVien.setVisible(!sua);
        khungSua.setVisible(sua);
        TienIch.datNutChinh(btnVinhVien, !sua);
        TienIch.datNutChinh(btnSua, sua);
        revalidate();
        repaint();
    }

    // Một hàng răng gồm hai nửa (phải và trái) cách nhau một khoảng
    private JPanel taoHang(int[] nuaPhai, int[] nuaTrai) {
        JPanel hang = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        hang.setOpaque(false);
        for (int so : nuaPhai) {
            hang.add(taoNutRang(so));
        }
        hang.add(Box.createHorizontalStrut(20));
        for (int so : nuaTrai) {
            hang.add(taoNutRang(so));
        }
        return hang;
    }

    // Nút của một răng; bấm vào thì chọn răng đó
    private NutRang taoNutRang(int so) {
        NutRang btn = new NutRang(so);
        btn.addActionListener(e -> chonRang(so));
        nutRang.put(so, btn);
        return btn;
    }

    // Đọc tình trạng hiện tại của các răng và tô màu lại sơ đồ
    private void napTinhTrang() {
        tinhTrang.clear();
        for (RangDieuTri r : rangService.layTinhTrangMoiNhat(luotKham.getMaHoSo())) {
            tinhTrang.put(r.getSoFDI(), r);
        }
        for (Map.Entry<Integer, NutRang> e : nutRang.entrySet()) {
            RangDieuTri r = tinhTrang.get(e.getKey());
            Color mau = r == null ? Color.WHITE : MAU.getOrDefault(r.getTrangThai(), Color.WHITE);
            e.getValue().datTrangThai(mau, e.getKey() == rangDangChon);
        }
    }

    // Tên các dịch vụ đã ghi cho răng này trong lượt khám đang khám (không trùng tên); chưa có thì chuỗi rỗng
    private String dichVuDaGhi(int so) {
        List<String> ten = new ArrayList<>();
        for (RangDieuTri r : rangService.layTheoLuotKham(luotKham.getMaLuotKham())) {
            if (r.getSoFDI() == so && r.getMaDichVu() != null) {
                String t = tenDichVu.getOrDefault(r.getMaDichVu(), r.getMaDichVu());
                if (!ten.contains(t)) {
                    ten.add(t);
                }
            }
        }
        return String.join(", ", ten);
    }

    // Chọn một răng: điền sẵn form bằng tình trạng hiện tại của răng đó
    private void chonRang(int so) {
        rangDangChon = so;
        RangDieuTri r = tinhTrang.get(so);
        String nhan = "Răng số " + so + (r == null ? " (chưa ghi nhận)" : " (hiện tại: " + r.getTrangThai() + ")");
        String daGhi = dichVuDaGhi(so);
        if (!daGhi.isEmpty()) {
            nhan += "<br>Đã ghi trong lượt này: " + daGhi;
        }
        lblRang.setText("<html>" + nhan + "</html>");
        cmbTrangThai.setSelectedItem(r == null ? HangSo.RANG_BINH_THUONG : r.getTrangThai());
        cmbMatRang.setSelectedItem(r == null || r.getMatRang() == null ? "" : r.getMatRang());
        txtVatLieu.setText(r == null ? "" : TienIch.chu(r.getVatLieu()));
        txtGhiChu.setText(r == null ? "" : TienIch.chu(r.getGhiChu()));
        cmbDichVu.setSelectedIndex(-1);
        List<RangDieuTri> banGhi = new ArrayList<>();
        for (RangDieuTri b : rangService.layTheoLuotKham(luotKham.getMaLuotKham())) {
            if (b.getSoFDI() == so) {
                banGhi.add(b);
            }
        }
        cmbBanGhi.setModel(new DefaultComboBoxModel<>(banGhi.toArray(new RangDieuTri[0])));
        cmbBanGhi.setSelectedIndex(-1);
        napTinhTrang();
    }

    // Gỡ bản ghi răng đã chọn (ghi nhầm trạng thái hoặc dịch vụ) khỏi lượt khám đang khám
    private void goBanGhi() {
        RangDieuTri b = (RangDieuTri) cmbBanGhi.getSelectedItem();
        if (b == null) {
            TienIch.thongTin("Vui lòng chọn bản ghi cần gỡ.");
            return;
        }
        if (!TienIch.xacNhan("Gỡ bản ghi này khỏi lượt khám? Dịch vụ của bản ghi sẽ không còn tính vào hóa đơn.")) {
            return;
        }
        try {
            rangService.goBanGhiRang(luotKham.getMaLuotKham(), b.getMaBanGhi());
            napTinhTrang();
            chonRang(rangDangChon);
        } catch (RuntimeException e) {
            TienIch.baoLoi(e);
        }
    }

    // Ghi nhận tình trạng răng đang chọn vào lượt khám
    private void ghiNhan() {
        if (rangDangChon == 0) {
            TienIch.thongTin("Vui lòng bấm chọn một răng trên sơ đồ.");
            return;
        }
        try {
            RangDieuTri r = new RangDieuTri();
            String matRang = (String) cmbMatRang.getSelectedItem();
            DichVu dichVu = (DichVu) cmbDichVu.getSelectedItem();
            r.setSoFDI(rangDangChon);
            r.setTrangThai((String) cmbTrangThai.getSelectedItem());
            r.setMatRang(matRang == null || matRang.isEmpty() ? null : matRang);
            r.setVatLieu(txtVatLieu.getText().trim().isEmpty() ? null : txtVatLieu.getText().trim());
            r.setMaDichVu(dichVu == null ? null : dichVu.getMaDichVu());
            r.setGhiChu(txtGhiChu.getText().trim().isEmpty() ? null : txtGhiChu.getText().trim());

            // Gom các cảnh báo (không chặn) rồi hỏi xác nhận một lần
            String canhBao = r.getMaDichVu() == null ? null : luotKhamService.canhBaoThuThuat(luotKham.getMaLuotKham(), r.getMaDichVu());
            String thieuDichVu = rangService.canhBaoThieuDichVu(luotKham.getMaLuotKham(), r);
            if (canhBao != null && thieuDichVu != null) {
                canhBao = canhBao + "\n\n" + thieuDichVu;
            } else if (thieuDichVu != null) {
                canhBao = thieuDichVu;
            }
            if (canhBao != null && !TienIch.xacNhan(canhBao + "\n\nVẫn ghi nhận?")) {
                return;
            }
            rangService.ghiNhanRang(luotKham.getMaLuotKham(), r);
            napTinhTrang();
            chonRang(rangDangChon);
            TienIch.thongTin("Đã ghi nhận răng " + r.getSoFDI() + ".");
        } catch (RuntimeException e) {
            TienIch.baoLoi(e);
        }
    }
}
