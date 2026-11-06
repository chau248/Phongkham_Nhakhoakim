package ui;

import ui.ThanhPhan.ChonNgay;
import ui.ThanhPhan.LuoiTuan;

import java.awt.BorderLayout;
import java.awt.Color;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import controller.LichHenController;
import controller.NhanVienController;
import controller.PhienLamViec;
import entity.HangSo;
import entity.LichHen;
import entity.NhanVien;
import nhakhoakim.MainApp;

// Lịch hẹn theo tuần
@SuppressWarnings("serial") // Lớp giao diện Swing
public class LichHenTuan extends JPanel implements MainApp.LamMoiDuoc {

    private static final String TAT_CA = "Tất cả nha sĩ";

    private LichHenController lichHenController = new LichHenController();
    private NhanVienController nhanVienController = new NhanVienController();

    private NhanVien nguoiDung = PhienLamViec.getNhanVienHienTai();
    private boolean coQuyenSua = nguoiDung != null && nguoiDung.laVaiTro(HangSo.VAI_TRO_LE_TAN, HangSo.VAI_TRO_QUAN_LY);
    private boolean xemNhieuNhaSi = coQuyenSua;
    private LuoiTuan luoi = new LuoiTuan(this::tai);
    private JComboBox<String> cmbNhaSi = new JComboBox<>();
    private List<NhanVien> dsNhaSi = new ArrayList<>();
    private List<LichHen> dsLich = new ArrayList<>();
    private LichHen dangChon; // Lịch đang bấm chọn
    private JLabel lblChiTiet = new JLabel(" ");
    private JButton btnXacNhan = TienIch.nutChinh("Xác nhận lịch");
    private JButton btnDoi = TienIch.nutPhu("Dời lịch");
    private JButton btnHuy = TienIch.nutDo("Hủy lịch");
    private JButton btnKhongDen = TienIch.nutPhu("Không đến");
    private boolean dangNap = false;

    public LichHenTuan() {
        cmbNhaSi.setFont(TienIch.FONT);
        cmbNhaSi.addItem(TAT_CA);
        if (xemNhieuNhaSi) {
            for (NhanVien nv : nhanVienController.layTatCa()) {
                if (HangSo.VAI_TRO_NHA_SI.equals(nv.getVaiTro()) && NhanVienController.DANG_LAM.equals(nv.getTrangThai())) {
                    dsNhaSi.add(nv);
                    cmbNhaSi.addItem(nv.getHoTen());
                }
            }
        }
        cmbNhaSi.setVisible(xemNhieuNhaSi);
        cmbNhaSi.addActionListener(e -> {
            if (!dangNap) {
                tai();
            }
        });
        JButton btnDat = TienIch.nutChinh("Đặt lịch hẹn mới");
        btnDat.addActionListener(e -> MainApp.doiNoiDung(new DatLichHen()));
        btnDat.setVisible(coQuyenSua);
        btnXacNhan.addActionListener(e -> xacNhan());
        btnDoi.addActionListener(e -> doi());
        btnHuy.addActionListener(e -> huy());
        btnKhongDen.addActionListener(e -> khongDen());
        lblChiTiet.setFont(TienIch.FONT);
        String ghiChu = coQuyenSua ? "Bấm vào một lịch trong lưới để xem chi tiết rồi xác nhận, dời, hủy hoặc đánh dấu không đến."
                : "Lịch hẹn của bạn theo tuần. Chỉ lễ tân và quản lý được sửa lịch.";
        TienIch.xepManHinh(this, TienIch.tieuDe("Lịch hẹn theo tuần"), TienIch.doanChu(ghiChu, TienIch.MAU_XAM),
                TienIch.dong(cmbNhaSi, btnDat), luoi, TienIch.doanChu(chuThich(), TienIch.MAU_XAM),
                TienIch.the(TienIch.chuThuong("Lịch đang chọn", true), lblChiTiet,
                        TienIch.dong(btnXacNhan, btnDoi, btnHuy, btnKhongDen)));
        capNhatNut();
        tai();
    }

    @Override
    public void lamMoi() {
        tai();
    }

    private String chuThich() {
        return "Màu: vàng = chờ xác nhận, xanh = đã xác nhận, xám xanh = hoàn thành, đỏ nhạt = không đến, xám = đã hủy.";
    }

    // Nạp lịch hẹn của tuần đang xem
    private void tai() {
        try {
            luoi.xoaNoiDung();
            String maNhaSi = null;
            int vt = cmbNhaSi.getSelectedIndex();
            if (xemNhieuNhaSi && vt > 0 && vt - 1 < dsNhaSi.size()) {
                maNhaSi = dsNhaSi.get(vt - 1).getMaNV();
            }
            dsLich = lichHenController.layLichTuan(luoi.getThuHai(), luoi.getChuNhat(), maNhaSi);
            String maDangChon = dangChon == null ? null : dangChon.getMaLichHen();
            dangChon = null;
            for (LichHen lh : dsLich) {
                if (lh.getMaLichHen().equals(maDangChon)) {
                    dangChon = lh; // Giữ lịch đang chọn sau khi nạp lại
                }
                String ca = LichHenController.caCuaGio(lh.getGioHen());
                if (ca == null) {
                    continue;
                }
                luoi.them(lh.getNgayHen(), ca, mucLich(lh));
                luoi.them(lh.getNgayHen(), ca, LuoiTuan.cach());
            }
            hienChiTiet();
            luoi.revalidate();
            luoi.repaint();
        } catch (RuntimeException e) {
            TienIch.baoLoi(e);
        }
    }

    // Một lịch hẹn trong ô
    private JLabel mucLich(LichHen lh) {
        Color nen = Color.WHITE;
        Color chu = TienIch.MAU_DAM;
        switch (lh.getTrangThai()) {
        case LichHenController.CHO_XAC_NHAN:
            nen = TienIch.MAU_VANG_NEN;
            chu = TienIch.MAU_VANG_CHU;
            break;
        case LichHenController.DA_XAC_NHAN:
            nen = new Color(0xE0F2F1);
            chu = TienIch.MAU_CHINH;
            break;
        case HangSo.HOAN_THANH:
            nen = new Color(0xECEFF1);
            chu = TienIch.MAU_DAM;
            break;
        case LichHenController.KHONG_DEN:
            nen = TienIch.MAU_DO_NEN;
            chu = TienIch.MAU_DO_CHU;
            break;
        default: // Đã hủy
            nen = new Color(0xF5F5F5);
            chu = TienIch.MAU_XAM;
        }
        String ten = lh.getMaBN().getHoTen();
        String nhaSi = lh.getMaNhanVien().getHoTen();
        String viet = nhaSi.substring(nhaSi.lastIndexOf(' ') + 1);
        String noi = "<b>" + TienIch.gio(lh.getGioHen()) + "</b> " + ten + "<br>" + lh.getMaDichVu().getTenDichVu();
        if (xemNhieuNhaSi) {
            noi += " · BS " + viet;
        }
        String chuoi = HangSo.DA_HUY.equals(lh.getTrangThai()) ? "<s>" + noi + "</s>" : noi;
        return LuoiTuan.muc(chuoi, nen, chu, () -> chon(lh));
    }

    private void chon(LichHen lh) {
        dangChon = lh;
        hienChiTiet();
    }

    private void hienChiTiet() {
        LichHen lh = dangChon;
        if (lh == null) {
            lblChiTiet.setText("Chưa chọn lịch nào.");
        } else {
            lblChiTiet.setText("<html><b>" + lh.getMaLichHen() + "</b> · " + lh.getMaBN().getHoTen() + " (" + TienIch.chu(lh.getMaBN().getSdt()) + ") · "
                    + lh.getMaDichVu().getTenDichVu() + "<br>Nha sĩ " + lh.getMaNhanVien().getHoTen() + " · " + TienIch.chu(lh.getPhong()) + " · "
                    + TienIch.ngay(lh.getNgayHen()) + " " + TienIch.gio(lh.getGioHen()) + " · <b>" + lh.getTrangThai() + "</b>"
                    + (lh.getLyDoHuy() == null ? "" : "<br>Lý do hủy: " + lh.getLyDoHuy()) + "</html>");
        }
        capNhatNut();
    }

    // Nút nào bấm được tùy trạng thái lịch đang chọn
    private void capNhatNut() {
        LichHen lh = dangChon;
        String tt = lh == null ? "" : lh.getTrangThai();
        btnXacNhan.setEnabled(coQuyenSua && LichHenController.CHO_XAC_NHAN.equals(tt));
        btnDoi.setEnabled(coQuyenSua && (LichHenController.CHO_XAC_NHAN.equals(tt) || LichHenController.DA_XAC_NHAN.equals(tt)));
        btnHuy.setEnabled(btnDoi.isEnabled());
        btnKhongDen.setEnabled(coQuyenSua && LichHenController.DA_XAC_NHAN.equals(tt));
    }

    private void xacNhan() {
        try {
            lichHenController.xacNhan(dangChon.getMaLichHen());
            TienIch.thongTin("Đã xác nhận lịch " + dangChon.getMaLichHen() + " của " + dangChon.getMaBN().getHoTen() + ".");
            tai();
        } catch (RuntimeException e) {
            TienIch.baoLoi(e);
        }
    }

    private void khongDen() {
        try {
            lichHenController.danhDauKhongDen(dangChon.getMaLichHen());
            tai();
        } catch (RuntimeException e) {
            TienIch.baoLoi(e);
        }
    }

    private void huy() {
        String lyDo = TienIch.nhapChu("Hủy lịch hẹn", "Lý do hủy lịch " + dangChon.getMaLichHen() + " của " + dangChon.getMaBN().getHoTen() + ":");
        if (lyDo == null) {
            return;
        }
        try {
            lichHenController.huyLich(dangChon.getMaLichHen(), lyDo);
            TienIch.thongTin("Đã hủy lịch " + dangChon.getMaLichHen() + ".");
            tai();
        } catch (RuntimeException e) {
            TienIch.baoLoi(e);
        }
    }

    // Hộp chọn ngày giờ mới
    private void doi() {
        LichHen lh = dangChon;
        JDialog hop = new JDialog(MainApp.cuaSo(), "Dời lịch " + lh.getMaLichHen(), true);
        ChonNgay txtNgay = new ChonNgay();
        txtNgay.setText(TienIch.ngay(lh.getNgayHen().isBefore(LocalDate.now()) ? LocalDate.now() : lh.getNgayHen()));
        JComboBox<String> cmbGio = new JComboBox<>();
        cmbGio.setFont(TienIch.FONT);
        JButton btnXem = TienIch.nutPhu("Xem giờ trống");
        btnXem.addActionListener(e -> {
            try {
                cmbGio.removeAllItems();
                LocalDate ngay = TienIch.docNgay(txtNgay.getText());
                if (ngay == null) {
                    TienIch.thongTin("Nhập ngày mới (dd/MM/yyyy).");
                    return;
                }
                for (LocalTime g : lichHenController.layKhungGioTrong(lh.getMaNhanVien().getMaNV(), ngay, lh.getMaDichVu().getThoiGianDuKien())) {
                    cmbGio.addItem(TienIch.gio(g));
                }
                if (cmbGio.getItemCount() == 0) {
                    TienIch.thongTin("Nha sĩ " + lh.getMaNhanVien().getHoTen() + " không còn giờ trống ngày " + TienIch.ngay(ngay) + ".");
                }
            } catch (RuntimeException ex) {
                TienIch.baoLoi(ex);
            }
        });
        JButton btnLuu = TienIch.nutChinh("Dời lịch");
        btnLuu.addActionListener(e -> {
            try {
                LocalDate ngay = TienIch.docNgay(txtNgay.getText());
                if (ngay == null || cmbGio.getSelectedItem() == null) {
                    TienIch.thongTin("Nhập ngày rồi bấm Xem giờ trống để chọn giờ mới.");
                    return;
                }
                lichHenController.doiLich(lh.getMaLichHen(), ngay, LocalTime.parse((String) cmbGio.getSelectedItem()));
                hop.dispose();
                TienIch.thongTin("Đã dời lịch " + lh.getMaLichHen() + " sang " + cmbGio.getSelectedItem() + " ngày " + TienIch.ngay(ngay) + ".");
                tai();
            } catch (RuntimeException ex) {
                TienIch.baoLoi(ex);
            }
        });
        JPanel noiDung = new JPanel();
        TienIch.xepManHinh(noiDung, TienIch.chuThuong(lh.getMaBN().getHoTen() + " - " + lh.getMaDichVu().getTenDichVu() + " với "
                + lh.getMaNhanVien().getHoTen(), true),
                TienIch.dong(TienIch.nhan("Ngày mới"), txtNgay, btnXem), TienIch.dong(TienIch.nhan("Giờ mới"), cmbGio, btnLuu));
        hop.setLayout(new BorderLayout());
        hop.add(noiDung, BorderLayout.CENTER);
        hop.pack();
        hop.setLocationRelativeTo(MainApp.cuaSo());
        hop.setVisible(true);
    }
}
