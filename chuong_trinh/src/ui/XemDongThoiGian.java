package ui;

import ui.ThanhPhan.ChonNgay;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import nhakhoakim.MainApp;
import controller.DongThoiGianController;
import entity.BenhNhan;
import entity.KetQuaHienThi.DongThoiGian;
import entity.KetQuaHienThi.DongThoiGian.SuKienDieuTri;

// Dòng thời gian điều trị của một bệnh nhân: bảng các sự kiện
@SuppressWarnings("serial") // Lớp giao diện Swing
public class XemDongThoiGian extends JPanel {

    private static final String TAT_CA = "Tất cả";

    private DongThoiGianController dongThoiGianController = new DongThoiGianController();

    private TienIch.ChonBenhNhan chonBenhNhan = new TienIch.ChonBenhNhan();
    private JComboBox<String> cmbLoai = new JComboBox<>(new String[] {TAT_CA, SuKienDieuTri.LOAI_KHAM, SuKienDieuTri.LOAI_LIEU_TRINH,
        SuKienDieuTri.LOAI_TAI_KHAM, SuKienDieuTri.LOAI_DANG_KY});
    private ChonNgay txtTu = new ChonNgay(); // Lọc từ ngày
    private ChonNgay txtDen = new ChonNgay(); // Lọc đến ngày
    private JLabel lblTomTat = new JLabel(" ");
    private JTable tblSuKien = TienIch.taoBang(new String[] {"Ngày giờ", "Loại", "Sự kiện", "Tình trạng"}, new int[] {130, 90, 380, 90},
            "Chọn bệnh nhân rồi bấm Xem để hiện các sự kiện điều trị.");
    private JTextArea txaChiTiet = TienIch.oNhieuDong(5, 60);
    private JButton btnLieuTrinh = TienIch.nutChinh("Xem liệu trình đang thực hiện");
    private DongThoiGian dongThoiGian; // Dữ liệu đang xem
    private List<SuKienDieuTri> dsHienThi = new ArrayList<>(); // Các sự kiện đang nằm trong bảng
    private BenhNhan benhNhanDangXem;

    public XemDongThoiGian() {
        this(null);
    }

    // Mở sẵn dòng thời gian của một bệnh nhân
    public XemDongThoiGian(BenhNhan benhNhan) {
        JButton btnXem = TienIch.nutChinh("Xem");
        btnXem.addActionListener(e -> xem());
        JButton btnLoc = TienIch.nutPhu("Lọc");
        btnLoc.addActionListener(e -> loc());
        cmbLoai.setFont(TienIch.FONT);
        cmbLoai.addActionListener(e -> loc());
        txtTu.setToolTipText("dd/MM/yyyy");
        txtDen.setToolTipText("dd/MM/yyyy");
        txaChiTiet.setEditable(false);
        txaChiTiet.setLineWrap(true);
        txaChiTiet.setWrapStyleWord(true);
        lblTomTat.setFont(TienIch.FONT);
        btnLieuTrinh.setVisible(false);
        btnLieuTrinh.addActionListener(e -> {
            if (benhNhanDangXem != null) {
                MainApp.doiNoiDung(new QuanLyLieuTrinh(benhNhanDangXem));
            }
        });
        tblSuKien.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                hienChiTiet();
            }
        });
        chonBenhNhan.addChonListener(e -> xoaKetQua());

        TienIch.xepManHinh(this, TienIch.tieuDe("Dòng thời gian điều trị"),
                TienIch.dong(TienIch.nhan("Bệnh nhân"), chonBenhNhan, btnXem),
                lblTomTat, TienIch.dong(btnLieuTrinh),
                TienIch.dong(TienIch.nhan("Loại"), cmbLoai, TienIch.nhan("Từ ngày"), txtTu, TienIch.nhan("đến"), txtDen, btnLoc),
                TienIch.cuonBang(tblSuKien, 260),
                TienIch.nhan("Chi tiết sự kiện đang chọn"), TienIch.cuonOChu(txaChiTiet));
        if (benhNhan != null) {
            chonBenhNhan.setBenhNhan(benhNhan); // Chưa có trong danh sách
            xem();
        }
    }

    private void xoaKetQua() {
        dongThoiGian = null;
        benhNhanDangXem = null;
        dsHienThi = new ArrayList<>();
        TienIch.napBang(tblSuKien, new ArrayList<>());
        lblTomTat.setText(" ");
        txaChiTiet.setText("");
        btnLieuTrinh.setVisible(false);
    }

    // Nạp dữ liệu của bệnh nhân đang chọn
    private void xem() {
        BenhNhan bn = chonBenhNhan.getBenhNhan();
        if (bn == null) {
            TienIch.thongTin("Vui lòng chọn một bệnh nhân.");
            return;
        }
        try {
            benhNhanDangXem = bn;
            dongThoiGian = dongThoiGianController.xayDung(bn.getMaBN());
            String gan = dongThoiGian.getLanKhamGanNhat() == null ? "chưa khám lần nào"
                    : TienIch.ngay(dongThoiGian.getLanKhamGanNhat()) + " (" + cachDay(dongThoiGian.getLanKhamGanNhat()) + ")";
            lblTomTat.setText("<html><b>" + bn.getMaBN() + " - " + bn.getHoTen() + "</b> · " + dongThoiGian.getSoLuotKham() + " lượt khám hoàn thành · khám gần nhất: "
                    + gan + " · răng đã nhổ " + dongThoiGian.getSoRangDaNho() + ", đã phục hình " + dongThoiGian.getSoRangImplantCauRang() + "</html>");
            int soLieuTrinh = dongThoiGian.getSoLieuTrinhDangLam();
            btnLieuTrinh.setText("Xem liệu trình đang thực hiện (" + soLieuTrinh + ")");
            btnLieuTrinh.setVisible(soLieuTrinh > 0);
            loc();
        } catch (RuntimeException e) {
            TienIch.baoLoi(e);
        }
    }

    // Đưa các sự kiện qua bộ lọc loại và khoảng ngày lên bảng
    private void loc() {
        if (dongThoiGian == null) {
            return;
        }
        try {
            LocalDate tu = TienIch.docNgay(txtTu.getText());
            LocalDate den = TienIch.docNgay(txtDen.getText());
            String loai = (String) cmbLoai.getSelectedItem();
            dsHienThi = new ArrayList<>();
            List<String[]> dong = new ArrayList<>();
            for (SuKienDieuTri sk : dongThoiGian.getSuKien()) {
                LocalDate ngay = sk.getThoiGian().toLocalDate();
                if ((!TAT_CA.equals(loai) && !sk.getLoai().equals(loai)) || (tu != null && ngay.isBefore(tu)) || (den != null && ngay.isAfter(den))) {
                    continue;
                }
                boolean coGio = !(sk.getThoiGian().getHour() == 0 && sk.getThoiGian().getMinute() == 0);
                dsHienThi.add(sk);
                dong.add(new String[] {coGio ? TienIch.ngayGio(sk.getThoiGian()) : TienIch.ngay(ngay), sk.getLoai(), sk.getTieuDe(), sk.isDaQua() ? "Đã qua" : "Sắp tới"});
            }
            TienIch.napBang(tblSuKien, dong);
            txaChiTiet.setText("");
        } catch (RuntimeException e) {
            TienIch.baoLoi(e);
        }
    }

    private void hienChiTiet() {
        int vt = tblSuKien.getSelectedRow();
        if (vt < 0 || vt >= dsHienThi.size()) {
            return;
        }
        SuKienDieuTri sk = dsHienThi.get(vt);
        txaChiTiet.setText(sk.getTieuDe() + "\n" + sk.getChiTiet());
        txaChiTiet.setCaretPosition(0);
    }

    // Cách đây bao lâu
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
