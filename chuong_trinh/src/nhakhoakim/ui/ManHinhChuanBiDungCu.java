package nhakhoakim.ui;

import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import nhakhoakim.dao.DichVuService;
import nhakhoakim.dao.DungCuService;
import nhakhoakim.dao.PhienLamViec;
import nhakhoakim.entity.DichVu;
import nhakhoakim.entity.DungCu;
import nhakhoakim.entity.HangSo;

// Màn hình chuẩn bị dụng cụ theo dịch vụ: xem dụng cụ cần dùng, y tá xác nhận đã chuẩn bị (dụng cụ phải đã tiệt trùng)
@SuppressWarnings("serial") // lớp giao diện Swing, không cần serialVersionUID
public class ManHinhChuanBiDungCu extends JPanel {

    private DungCuService dungCuService = new DungCuService();
    private DichVuService dichVuService = new DichVuService();

    private JComboBox<DichVu> cmbDichVu = TienIch.taoChonDichVu(dichVuService.layTatCa(), "Chọn dịch vụ của ca khám"); // dịch vụ của ca khám
    private JTable tblDungCu = TienIch.taoBang(new String[] {"Mã", "Dụng cụ", "Số lượng", "Phòng", "Tình trạng"},
            new int[] {90, 300, 80, 100, 130}, "Chưa có dụng cụ nào để hiển thị.");  // dụng cụ cần cho dịch vụ

    public ManHinhChuanBiDungCu() {
        cmbDichVu.addActionListener(e -> napBang());

        JButton btnXacNhan = TienIch.nutChinh("Xác nhận đã chuẩn bị");
        btnXacNhan.addActionListener(e -> xacNhan());
        JPanel hangNut = TienIch.dong(btnXacNhan);
        if (!HangSo.VAI_TRO_Y_TA.equals(PhienLamViec.getNhanVienHienTai().getVaiTro())) {
            btnXacNhan.setEnabled(false); // chỉ y tá được chuẩn bị dụng cụ
            JLabel lblChiYTa = TienIch.chuThuong("Chỉ y tá chuẩn bị dụng cụ", false);
            lblChiYTa.setForeground(TienIch.MAU_XAM);
            hangNut.add(lblChiYTa);
        }

        TienIch.xepManHinh(this, TienIch.tieuDe("Chuẩn bị dụng cụ theo dịch vụ"),
                TienIch.dong(TienIch.nhan("Dịch vụ"), cmbDichVu),
                TienIch.doanChu("Chỉ dụng cụ \"" + HangSo.DA_TIET_TRUNG + "\" mới được chuyển sang \"" + HangSo.DANG_SU_DUNG + "\".", TienIch.MAU_XAM),
                TienIch.cuonBang(tblDungCu, 240), hangNut);
    }

    // Nạp dụng cụ của dịch vụ đang chọn; chưa chọn dịch vụ hoặc nạp lỗi thì bảng để trống
    private void napBang() {
        TienIch.napBang(tblDungCu, new ArrayList<>());
        DichVu dichVu = (DichVu) cmbDichVu.getSelectedItem();
        if (dichVu == null) {
            return;
        }
        try {
            List<String[]> dong = new ArrayList<>();
            for (DungCu d : dungCuService.layTheoDichVu(dichVu.getMaDichVu())) {
                dong.add(new String[] {d.getMaDungCu(), d.getTenDungCu(), String.valueOf(d.getSoLuongCan()),
                    TienIch.chu(d.getPhong()), d.getTrangThaiVoTrung()});
            }
            TienIch.napBang(tblDungCu, dong);
        } catch (RuntimeException e) {
            TienIch.baoLoi(e);
        }
    }

    // Xác nhận đã chuẩn bị: tất cả dụng cụ phải đã tiệt trùng
    private void xacNhan() {
        DichVu dichVu = (DichVu) cmbDichVu.getSelectedItem();
        if (dichVu == null) {
            TienIch.thongTin("Vui lòng chọn dịch vụ của ca khám.");
            return;
        }
        try {
            int soLuong = dungCuService.chuanBiDungCu(dichVu.getMaDichVu());
            napBang();
            TienIch.thongTin("Đã chuẩn bị " + soLuong + " dụng cụ (chuyển sang " + HangSo.DANG_SU_DUNG + ").");
        } catch (RuntimeException e) {
            TienIch.baoLoi(e);
        }
    }
}
