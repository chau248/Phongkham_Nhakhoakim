package ui;

import ui.ThanhPhan.LuoiTuan;

import java.awt.BorderLayout;
import java.awt.Color;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import controller.CaTrucController;
import controller.CauHinhController;
import controller.NhanVienController;
import controller.PhienLamViec;
import entity.CaTruc;
import entity.DanhMuc;
import entity.HangSo;
import entity.NhanVien;
import nhakhoakim.MainApp;

// Thời khóa biểu ca trực theo tuần: quản lý xếp và sửa ca
@SuppressWarnings("serial") // Lớp giao diện Swing
public class ThoiKhoaBieu extends JPanel implements MainApp.LamMoiDuoc {

    private CaTrucController caTrucController = new CaTrucController();
    private NhanVienController nhanVienController = new NhanVienController();
    private CauHinhController cauHinhController = new CauHinhController();

    private NhanVien nguoiDung = PhienLamViec.getNhanVienHienTai();
    private boolean laQuanLy = nguoiDung != null && nguoiDung.laVaiTro(HangSo.VAI_TRO_QUAN_LY);
    private LuoiTuan luoi = new LuoiTuan(this::tai);
    private List<CaTruc> dsCa = new ArrayList<>(); // Ca trực của tuần đang xem

    public ThoiKhoaBieu() {
        String ghiChu = laQuanLy ? "Bấm vào một ô (ngày + ca) để xếp thêm người, đổi trạng thái hoặc xóa ca. Mỗi phòng một nha sĩ trong cùng ca."
                : "Các ca trực của bạn trong tuần. Cần đổi ca xin liên hệ quản lý.";
        TienIch.xepManHinh(this, TienIch.tieuDe(laQuanLy ? "Thời khóa biểu ca trực" : "Ca trực của tôi"),
                TienIch.doanChu(ghiChu, TienIch.MAU_XAM), luoi);
        tai();
    }

    @Override
    public void lamMoi() {
        tai();
    }

    // Nạp ca trực của tuần đang xem vào lưới
    private void tai() {
        try {
            luoi.xoaNoiDung();
            LocalDate tu = luoi.getThuHai();
            LocalDate den = luoi.getChuNhat();
            dsCa = laQuanLy ? caTrucController.layTheoKhoang(tu, den)
                    : caTrucController.layTheoNhanVienVaKhoang(nguoiDung == null ? "" : nguoiDung.getMaNV(), tu, den);
            for (CaTruc ca : dsCa) {
                luoi.them(ca.getNgay(), ca.getCaLam(), mucCa(ca));
                luoi.them(ca.getNgay(), ca.getCaLam(), LuoiTuan.cach());
            }
            if (laQuanLy) {
                for (int i = 0; i < 7; i++) {
                    for (String ca : LuoiTuan.CA) {
                        gan(tu.plusDays(i), ca);
                    }
                }
            }
            luoi.revalidate();
            luoi.repaint();
        } catch (RuntimeException e) {
            TienIch.baoLoi(e);
        }
    }

    // Một ca trực trong ô: tên
    private JLabel mucCa(CaTruc ca) {
        NhanVien nv = ca.getMaNV();
        Color nen = new Color(0xECEFF1);
        Color chu = TienIch.MAU_XAM;
        if (nv.laVaiTro(HangSo.VAI_TRO_NHA_SI) && !HangSo.VAI_TRO_TOAN_QUYEN.equals(nv.getVaiTro())) {
            nen = new Color(0xE3F2FD);
            chu = TienIch.MAU_DAM;
        } else if (HangSo.VAI_TRO_Y_TA.equals(nv.getVaiTro())) {
            nen = new Color(0xE8F5E9);
            chu = new Color(0x2E7D32);
        } else if (HangSo.VAI_TRO_LE_TAN.equals(nv.getVaiTro())) {
            nen = new Color(0xFFF3E0);
            chu = TienIch.MAU_VANG_CHU;
        }
        String trangThai = CaTrucController.CO_MAT.equals(ca.getTrangThai()) ? "" : " <i>(" + ca.getTrangThai().toLowerCase() + ")</i>";
        if (!CaTrucController.CO_MAT.equals(ca.getTrangThai())) {
            nen = new Color(0xF5F5F5);
            chu = TienIch.MAU_XAM;
        }
        return LuoiTuan.muc("<b>" + nv.getHoTen() + "</b>" + trangThai + "<br>" + nv.getVaiTro() + " · " + TienIch.chu(ca.getPhong()), nen, chu, null);
    }

    // Quản lý bấm vào ô
    private void gan(LocalDate ngay, String ca) {
        luoi.them(ngay, ca, LuoiTuan.muc("<i>+ Sửa ca</i>", Color.WHITE, TienIch.MAU_CHINH, () -> moHopCa(ngay, ca)));
    }

    private void moHopCa(LocalDate ngay, String ca) {
        JDialog hop = new JDialog(MainApp.cuaSo(), "Ca " + ca.toLowerCase() + " ngày " + TienIch.ngay(ngay), true);
        JTable bang = TienIch.taoBang(new String[] {"Nhân viên", "Vai trò", "Phòng", "Trạng thái"}, new int[] {200, 90, 100, 100}, "Chưa có ai trực ca này.");
        List<CaTruc> trongO = new ArrayList<>();
        JComboBox<String> cmbNhanVien = new JComboBox<>();
        List<NhanVien> dsNv = new ArrayList<>();
        JComboBox<String> cmbPhong = new JComboBox<>(layPhong());
        JComboBox<String> cmbTrangThai = new JComboBox<>(new String[] {CaTrucController.CO_MAT, CaTrucController.VANG, CaTrucController.DIEU_CHUYEN});
        for (NhanVien nv : nhanVienController.layTatCa()) {
            if (!NhanVienController.DANG_LAM.equals(nv.getTrangThai()) || nv.getVaiTro().equals(HangSo.VAI_TRO_QUAN_LY)
                    || nv.getVaiTro().equals(HangSo.VAI_TRO_TOAN_QUYEN)) {
                continue; // Chỉ xếp ca cho người đang làm và có ca trực
            }
            dsNv.add(nv);
            cmbNhanVien.addItem(nv.getHoTen() + " (" + nv.getVaiTro() + ")");
        }
        cmbNhanVien.setSelectedIndex(-1);
        // Chọn nhân viên
        cmbNhanVien.addActionListener(e -> {
            int vt = cmbNhanVien.getSelectedIndex();
            if (vt < 0) {
                return;
            }
            String vaiTroNv = dsNv.get(vt).getVaiTro();
            if (HangSo.VAI_TRO_LE_TAN.equals(vaiTroNv)) {
                cmbPhong.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] {CaTrucController.QUAY_LE_TAN}));
                cmbPhong.setEnabled(false);
            } else if (HangSo.VAI_TRO_NHA_SI.equals(vaiTroNv) || HangSo.VAI_TRO_Y_TA.equals(vaiTroNv)) {
                cmbPhong.setModel(new javax.swing.DefaultComboBoxModel<>(cauHinhController.layPhongKham().toArray(new String[0])));
                cmbPhong.setEnabled(true);
            } else {
                cmbPhong.setModel(new javax.swing.DefaultComboBoxModel<>(layPhong()));
                cmbPhong.setEnabled(true);
            }
        });
        Runnable nap = () -> {
            trongO.clear();
            List<String[]> dong = new ArrayList<>();
            for (CaTruc c : caTrucController.layTheoKhoang(ngay, ngay)) {
                if (c.getCaLam().equals(ca)) {
                    trongO.add(c);
                    dong.add(new String[] {c.getMaNV().getHoTen(), c.getMaNV().getVaiTro(), TienIch.chu(c.getPhong()), c.getTrangThai()});
                }
            }
            TienIch.napBang(bang, dong);
        };
        nap.run();

        JButton btnThem = TienIch.nutChinh("Thêm vào ca");
        btnThem.addActionListener(e -> {
            int vt = cmbNhanVien.getSelectedIndex();
            if (vt < 0) {
                TienIch.thongTin("Chọn nhân viên cần xếp vào ca.");
                return;
            }
            try {
                caTrucController.phanCong(dsNv.get(vt).getMaNV(), ngay, ca, (String) cmbPhong.getSelectedItem());
                nap.run();
                tai();
            } catch (RuntimeException ex) {
                TienIch.baoLoi(ex);
            }
        });
        JButton btnDoi = TienIch.nutPhu("Đổi trạng thái");
        btnDoi.addActionListener(e -> {
            int vt = bang.getSelectedRow();
            if (vt < 0 || vt >= trongO.size()) {
                TienIch.thongTin("Chọn một dòng trong bảng.");
                return;
            }
            try {
                caTrucController.doiTrangThai(trongO.get(vt).getMaCaTruc(), (String) cmbTrangThai.getSelectedItem());
                nap.run();
                tai();
            } catch (RuntimeException ex) {
                TienIch.baoLoi(ex);
            }
        });
        JButton btnXoa = TienIch.nutDo("Xóa khỏi ca");
        btnXoa.addActionListener(e -> {
            int vt = bang.getSelectedRow();
            if (vt < 0 || vt >= trongO.size()) {
                TienIch.thongTin("Chọn một dòng trong bảng.");
                return;
            }
            try {
                caTrucController.xoa(trongO.get(vt).getMaCaTruc());
                nap.run();
                tai();
            } catch (RuntimeException ex) {
                TienIch.baoLoi(ex);
            }
        });
        JButton btnDong = TienIch.nutPhu("Đóng");
        btnDong.addActionListener(e -> hop.dispose());

        JPanel noiDung = new JPanel();
        javax.swing.JScrollPane khungBang = TienIch.cuonBang(bang, 180);
        khungBang.setPreferredSize(new java.awt.Dimension(700, 180));
        TienIch.xepManHinh(noiDung, khungBang,
                TienIch.dong(TienIch.nhan("Nhân viên"), cmbNhanVien, TienIch.nhan("Phòng"), cmbPhong, btnThem),
                TienIch.dong(TienIch.nhan("Dòng đang chọn"), cmbTrangThai, btnDoi, btnXoa),
                TienIch.doanChu("Lễ tân luôn xếp vào quầy lễ tân. Không xếp ca trong quá khứ, không xóa ca đã có lịch hẹn.", TienIch.MAU_XAM));
        hop.setLayout(new BorderLayout());
        hop.add(noiDung, BorderLayout.CENTER);
        JPanel chan = TienIch.dong(btnDong);
        hop.add(chan, BorderLayout.SOUTH);
        hop.pack(); // Cỡ hộp theo nội dung, tránh bảng bị ép nhỏ không chọn được dòng
        hop.setLocationRelativeTo(MainApp.cuaSo());
        hop.setVisible(true);
    }

    // Các phòng trong danh mục
    private String[] layPhong() {
        List<String> ds = new ArrayList<>(cauHinhController.layPhongKham());
        for (String p : cauHinhController.layDanhMuc(DanhMuc.PHONG)) {
            if (!ds.contains(p)) {
                ds.add(p);
            }
        }
        return ds.toArray(new String[0]);
    }
}
