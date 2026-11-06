package ui;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import controller.CauHinhController;
import controller.DichVuController;
import controller.PhienLamViec;
import dao.NghiepVuException;
import entity.DanhMuc;
import entity.DichVu;
import entity.HangSo;
import entity.NhanVien;
import nhakhoakim.MainApp;

// Màn hình danh mục dịch vụ và bảng giá
@SuppressWarnings("serial") // Lớp giao diện Swing
public class DanhMucDichVu extends JPanel implements MainApp.LamMoiDuoc {

    private static final String TAT_CA = "Tất cả";

    private DichVuController dichVuController = new DichVuController();
    private boolean laQuanLy;

    private JTextField txtTim = TienIch.oNhap(24); // Tìm theo tên dịch vụ
    private JComboBox<String> cmbLoai = new JComboBox<>(); // Lọc theo loại
    private JComboBox<String> cmbTrangThai = new JComboBox<>(new String[] {TAT_CA, "Đang hoạt động", "Đã ngừng"}); // Chỉ Quản lý
    private JTable tblDichVu = TienIch.taoBang(new String[] {"Mã", "Tên dịch vụ", "Loại", "Giá (đồng)", "Thời gian (phút)", "Đơn vị", "Cần liệu trình", "Trạng thái"},
            new int[] {90, 240, 100, 110, 100, 70, 100, 110}, "Không có dịch vụ nào.");
    private List<DichVu> dsHienThi = new ArrayList<>();
    private JLabel lblKetQua = new JLabel();

    public DanhMucDichVu() {
        NhanVien nv = PhienLamViec.getNhanVienHienTai();
        laQuanLy = nv != null && nv.laVaiTro(HangSo.VAI_TRO_QUAN_LY);
        cmbLoai.addItem(TAT_CA);
        for (String loai : new controller.CauHinhController().layDanhMuc(entity.DanhMuc.LOAI_DICH_VU)) {
            cmbLoai.addItem(loai);
        }
        cmbLoai.setFont(TienIch.FONT);
        cmbTrangThai.setFont(TienIch.FONT);
        JButton btnLoc = TienIch.nutChinh("Lọc");
        btnLoc.addActionListener(e -> loc());
        txtTim.addActionListener(e -> loc());
        cmbLoai.addActionListener(e -> loc());
        cmbTrangThai.addActionListener(e -> loc());
        JButton btnThem = TienIch.nutPhu("Thêm dịch vụ");
        btnThem.addActionListener(e -> MainApp.doiNoiDung(new SuaDichVu(null)));
        JButton btnSua = TienIch.nutPhu("Sửa dịch vụ đã chọn");
        btnSua.addActionListener(e -> sua());
        btnThem.setVisible(laQuanLy);
        btnSua.setVisible(laQuanLy);
        cmbTrangThai.setVisible(laQuanLy);
        lblKetQua.setFont(TienIch.FONT);
        lblKetQua.setForeground(TienIch.MAU_XAM);
        TienIch.xepManHinh(this, TienIch.tieuDe("Danh mục dịch vụ và giá"),
                TienIch.dong(TienIch.nhan("Tên dịch vụ"), txtTim, TienIch.nhan("Loại"), cmbLoai, cmbTrangThai, btnLoc), lblKetQua,
                TienIch.cuonBang(tblDichVu, 360), TienIch.dong(btnThem, btnSua),
                TienIch.doanChu(laQuanLy ? "Đổi giá chỉ áp dụng cho lịch hẹn và hóa đơn lập sau đó; mọi thay đổi giá được ghi nhật ký."
                        : "Bảng giá chỉ để xem. Việc thêm hoặc sửa dịch vụ do Quản lý thực hiện.", TienIch.MAU_XAM));
        loc();
    }

    @Override
    public void lamMoi() {
        loc();
    }

    // Tải danh mục theo vai trò
    private void loc() {
        try {
            NhanVien nv = PhienLamViec.getNhanVienHienTai();
            String tu = txtTim.getText().trim().toLowerCase();
            String loai = (String) cmbLoai.getSelectedItem();
            int tt = cmbTrangThai.getSelectedIndex();
            dsHienThi = new ArrayList<>();
            List<String[]> dong = new ArrayList<>();
            for (DichVu dv : dichVuController.layChoVaiTro(nv == null ? HangSo.VAI_TRO_QUAN_LY : nv.getVaiTro())) {
                if (!tu.isEmpty() && !dv.getTenDichVu().toLowerCase().contains(tu)) {
                    continue;
                }
                if (loai != null && !TAT_CA.equals(loai) && !loai.equals(dv.getLoaiDichVu())) {
                    continue;
                }
                if ((tt == 1 && !dv.isTrangThaiHoatDong()) || (tt == 2 && dv.isTrangThaiHoatDong())) {
                    continue;
                }
                dsHienThi.add(dv);
                dong.add(new String[] {dv.getMaDichVu(), dv.getTenDichVu(), TienIch.chu(dv.getLoaiDichVu()), TienIch.tien(dv.getGia()),
                    String.valueOf(dv.getThoiGianDuKien()), TienIch.chu(dv.getDonViTinh()), dv.isYeuCauLieuTrinh() ? "Có" : "Không",
                    dv.isTrangThaiHoatDong() ? "Đang hoạt động" : "Đã ngừng"});
            }
            TienIch.napBang(tblDichVu, dong);
            lblKetQua.setText(dong.size() + " dịch vụ.");
        } catch (RuntimeException e) {
            TienIch.baoLoi(e);
        }
    }

    // Mở màn hình sửa cho dịch vụ đang chọn
    private void sua() {
        int vt = tblDichVu.getSelectedRow();
        if (vt < 0 || vt >= dsHienThi.size()) {
            TienIch.thongTin("Vui lòng chọn một dịch vụ trong bảng.");
            return;
        }
        MainApp.doiNoiDung(new SuaDichVu(dsHienThi.get(vt)));
    }

    // Màn hình thêm hoặc sửa một dịch vụ
    @SuppressWarnings("serial") // Lớp giao diện Swing
    public static class SuaDichVu extends JPanel {

        private DichVuController dichVuController = new DichVuController();
        private DichVu dichVu; // Dịch vụ đang sửa

        private JTextField txtTen = TienIch.oNhap(32);
        private JComboBox<String> cmbLoai = new JComboBox<>(new CauHinhController().layDanhMuc(DanhMuc.LOAI_DICH_VU).toArray(new String[0])); // Loại chọn từ danh mục
        private JTextField txtGia = TienIch.oNhap(14); // Giá
        private JTextField txtThoiGian = TienIch.oNhap(6); // Thời gian dự kiến
        private JComboBox<String> cmbDonVi = new JComboBox<>(DichVuController.DON_VI_TINH.toArray(new String[0]));
        private JTextArea txaMoTa = TienIch.oNhieuDong(3, 32);
        private JCheckBox chkLieuTrinh = new JCheckBox("Cần theo liệu trình (nhiều buổi)");
        private JCheckBox chkImplantCauRang = new JCheckBox("Là dịch vụ Implant hoặc cầu răng (dùng cho răng đã nhổ)");
        private JCheckBox chkHoatDong = new JCheckBox("Đang hoạt động (bỏ chọn là ngừng cung cấp)");

        public SuaDichVu(DichVu dichVu) {
            this.dichVu = dichVu;
            cmbLoai.setFont(TienIch.FONT);
            cmbDonVi.setFont(TienIch.FONT);
            for (JCheckBox c : new JCheckBox[] {chkLieuTrinh, chkImplantCauRang, chkHoatDong}) {
                c.setOpaque(false);
                c.setFont(TienIch.FONT);
            }
            chkHoatDong.setSelected(true);
            if (dichVu != null) {
                txtTen.setText(dichVu.getTenDichVu());
                cmbLoai.setSelectedItem(dichVu.getLoaiDichVu());
                txtGia.setText(String.valueOf(dichVu.getGia()));
                txtThoiGian.setText(String.valueOf(dichVu.getThoiGianDuKien()));
                cmbDonVi.setSelectedItem(dichVu.getDonViTinh());
                txaMoTa.setText(TienIch.chu(dichVu.getMoTa()));
                chkLieuTrinh.setSelected(dichVu.isYeuCauLieuTrinh());
                chkImplantCauRang.setSelected(dichVu.isLienQuanImplantCauRang());
                chkHoatDong.setSelected(dichVu.isTrangThaiHoatDong());
            } else {
                cmbDonVi.setSelectedItem("Lần");
            }
            JButton btnLuu = TienIch.nutChinh(dichVu == null ? "Thêm dịch vụ" : "Lưu thay đổi");
            btnLuu.addActionListener(e -> luu());
            JPanel form = TienIch.taoForm(new String[] {"Mã dịch vụ", "Tên dịch vụ", "Loại", "Giá (đồng)", "Thời gian dự kiến (phút)", "Đơn vị tính", "Mô tả", "", "", ""},
                    new Component[] {TienIch.chuThuong(dichVu == null ? "(hệ thống tự sinh)" : dichVu.getMaDichVu(), false), txtTen, cmbLoai, txtGia, txtThoiGian, cmbDonVi,
                        TienIch.cuonOChu(txaMoTa), chkLieuTrinh, chkImplantCauRang, chkHoatDong});
            TienIch.xepManHinh(this, TienIch.tieuDe(dichVu == null ? "Thêm dịch vụ" : "Sửa dịch vụ"), TienIch.the(form, TienIch.dong(btnLuu)));
        }

        // Đọc số nguyên người dùng gõ
        private long docSo(String chu, String tenO) {
            try {
                return Long.parseLong(chu.trim().replace(".", "").replace(",", ""));
            } catch (NumberFormatException e) {
                throw new NghiepVuException(tenO + " phải là một số (ví dụ 300000)");
            }
        }

        // Lưu dịch vụ
        private void luu() {
            try {
                DichVu dv = new DichVu(); // Đối tượng mới để nhập sai
                if (dichVu != null) {
                    dv.setMaDichVu(dichVu.getMaDichVu());
                }
                dv.setTenDichVu(txtTen.getText());
                dv.setLoaiDichVu(cmbLoai.getSelectedItem() == null ? null : cmbLoai.getSelectedItem().toString());
                dv.setGia(docSo(txtGia.getText(), "Giá"));
                dv.setThoiGianDuKien((int) docSo(txtThoiGian.getText(), "Thời gian dự kiến"));
                dv.setDonViTinh((String) cmbDonVi.getSelectedItem());
                dv.setMoTa(txaMoTa.getText());
                dv.setYeuCauLieuTrinh(chkLieuTrinh.isSelected());
                dv.setLienQuanImplantCauRang(chkImplantCauRang.isSelected());
                dv.setTrangThaiHoatDong(chkHoatDong.isSelected());
                if (dichVu == null) {
                    DichVu moi = dichVuController.them(dv);
                    TienIch.thongTin("Đã thêm dịch vụ " + moi.getMaDichVu() + " - " + moi.getTenDichVu() + ".");
                } else {
                    dichVuController.capNhat(dv);
                    TienIch.thongTin("Đã lưu dịch vụ " + dv.getMaDichVu() + ".");
                }
                MainApp.thayNoiDung(new DanhMucDichVu());
            } catch (RuntimeException e) {
                TienIch.baoLoi(e);
            }
        }
    }
}
