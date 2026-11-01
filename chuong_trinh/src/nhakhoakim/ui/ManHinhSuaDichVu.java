package nhakhoakim.ui;

import java.awt.Component;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import nhakhoakim.MainApp;
import nhakhoakim.controller.DichVuController;
import nhakhoakim.dao.NghiepVuException;
import nhakhoakim.entity.DichVu;
import nhakhoakim.entity.LoaiDichVu;

// Màn hình thêm hoặc sửa một dịch vụ (DV-02, DV-03): chỉ Quản lý; tên không trùng, giá không âm, thời gian lớn hơn 0
@SuppressWarnings("serial") // lớp giao diện Swing, không cần serialVersionUID
public class ManHinhSuaDichVu extends JPanel {

    private DichVuController dichVuController = new DichVuController();
    private DichVu dichVu; // dịch vụ đang sửa (null là thêm mới)

    private JTextField txtTen = TienIch.oNhap(32);
    private JComboBox<String> cmbLoai = new JComboBox<>(new String[] {LoaiDichVu.KHAM, LoaiDichVu.VE_SINH, LoaiDichVu.TRAM, LoaiDichVu.NHO_RANG,
        LoaiDichVu.NOI_NHA, LoaiDichVu.THAM_MY, LoaiDichVu.IMPLANT, LoaiDichVu.PHUC_HINH, "Chỉnh nha", "Chẩn đoán"});
    private JTextField txtGia = TienIch.oNhap(14);                  // giá (đồng)
    private JTextField txtThoiGian = TienIch.oNhap(6);              // thời gian dự kiến (phút)
    private JComboBox<String> cmbDonVi = new JComboBox<>(DichVuController.DON_VI_TINH.toArray(new String[0]));
    private JTextArea txaMoTa = TienIch.oNhieuDong(3, 32);
    private JCheckBox chkLieuTrinh = new JCheckBox("Cần theo liệu trình (nhiều buổi)");
    private JCheckBox chkImplantCauRang = new JCheckBox("Là dịch vụ Implant hoặc cầu răng (dùng cho răng đã nhổ)");
    private JCheckBox chkHoatDong = new JCheckBox("Đang hoạt động (bỏ chọn là ngừng cung cấp)");

    public ManHinhSuaDichVu(DichVu dichVu) {
        this.dichVu = dichVu;
        cmbLoai.setEditable(true); // cho gõ loại mới ngoài danh sách gợi ý
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

    // Đọc số nguyên người dùng gõ; gõ sai thì báo lỗi nghiệp vụ nêu tên ô
    private long docSo(String chu, String tenO) {
        try {
            return Long.parseLong(chu.trim().replace(".", "").replace(",", ""));
        } catch (NumberFormatException e) {
            throw new NghiepVuException(tenO + " phải là một số (ví dụ 300000)");
        }
    }

    // Lưu dịch vụ (thêm mới hoặc cập nhật)
    private void luu() {
        try {
            DichVu dv = new DichVu(); // đối tượng mới để nhập sai thì dịch vụ đang xem không bị đổi theo
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
            MainApp.thayNoiDung(new ManHinhDanhMucDichVu());
        } catch (RuntimeException e) {
            TienIch.baoLoi(e);
        }
    }
}
