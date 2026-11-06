package ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GraphicsEnvironment;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTable;
import nhakhoakim.MainApp;
import controller.DichVuController;
import entity.DichVu;

// Hộp thoại chọn một dịch vụ điều trị
public class HopThoaiChonDichVu {

    private static final String TAT_CA = "Tất cả loại";

    // Chạy kiểm thử không có màn hình
    public static DichVu traLoiTuDong = null;

    private static DichVu daChon; // Dịch vụ người dùng chọn
    private static List<DichVu> dangHien; // Danh sách dịch vụ

    // Mở hộp thoại chọn dịch vụ không gắn răng (dịch vụ tính theo răng phải ghi ở Sơ đồ răng)
    public static DichVu chon() {
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
