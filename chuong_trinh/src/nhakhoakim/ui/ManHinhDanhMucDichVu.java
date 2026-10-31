package nhakhoakim.ui;

import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import nhakhoakim.MainApp;
import nhakhoakim.controller.DichVuController;
import nhakhoakim.dao.PhienLamViec;
import nhakhoakim.entity.DichVu;
import nhakhoakim.entity.LoaiDichVu;
import nhakhoakim.entity.NhanVien;
import nhakhoakim.entity.VaiTro;

// Màn hình danh mục dịch vụ và bảng giá (DV-01): mọi vai trò được xem; Nha sĩ, Lễ tân, Y tá chỉ thấy dịch vụ đang hoạt động;
// Quản lý thấy cả dịch vụ đã ngừng và có nút Thêm, Sửa
@SuppressWarnings("serial") // lớp giao diện Swing, không cần serialVersionUID
public class ManHinhDanhMucDichVu extends JPanel implements MainApp.LamMoiDuoc {

    private static final String TAT_CA = "Tất cả";

    private DichVuController dichVuController = new DichVuController();
    private boolean laQuanLy;

    private JTextField txtTim = TienIch.oNhap(24);                 // tìm theo tên dịch vụ
    private JComboBox<String> cmbLoai = new JComboBox<>();         // lọc theo loại
    private JComboBox<String> cmbTrangThai = new JComboBox<>(new String[] {TAT_CA, "Đang hoạt động", "Đã ngừng"}); // chỉ Quản lý có
    private JTable tblDichVu = TienIch.taoBang(new String[] {"Mã", "Tên dịch vụ", "Loại", "Giá (đồng)", "Thời gian (phút)", "Đơn vị", "Cần liệu trình", "Trạng thái"},
            new int[] {90, 240, 100, 110, 100, 70, 100, 110}, "Không có dịch vụ nào.");
    private List<DichVu> dsHienThi = new ArrayList<>();
    private JLabel lblKetQua = new JLabel();

    public ManHinhDanhMucDichVu() {
        NhanVien nv = PhienLamViec.getNhanVienHienTai();
        laQuanLy = nv != null && VaiTro.QUAN_LY.equals(nv.getVaiTro());
        cmbLoai.addItem(TAT_CA);
        for (String loai : new String[] {LoaiDichVu.KHAM, LoaiDichVu.VE_SINH, LoaiDichVu.TRAM, LoaiDichVu.NHO_RANG, LoaiDichVu.NOI_NHA, LoaiDichVu.THAM_MY,
            LoaiDichVu.IMPLANT, LoaiDichVu.PHUC_HINH, "Chỉnh nha", "Chẩn đoán"}) {
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
        btnThem.addActionListener(e -> MainApp.doiNoiDung(new ManHinhSuaDichVu(null)));
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

    // Tải danh mục theo vai trò rồi lọc theo tên, loại, trạng thái đang chọn
    private void loc() {
        try {
            NhanVien nv = PhienLamViec.getNhanVienHienTai();
            String tu = txtTim.getText().trim().toLowerCase();
            String loai = (String) cmbLoai.getSelectedItem();
            int tt = cmbTrangThai.getSelectedIndex();
            dsHienThi = new ArrayList<>();
            List<String[]> dong = new ArrayList<>();
            for (DichVu dv : dichVuController.layChoVaiTro(nv == null ? VaiTro.QUAN_LY : nv.getVaiTro())) {
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
        MainApp.doiNoiDung(new ManHinhSuaDichVu(dsHienThi.get(vt)));
    }
}
