package nhakhoakim.ui;

import java.awt.Component;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextField;
import nhakhoakim.dao.BenhNhanService;
import nhakhoakim.dao.DichVuService;
import nhakhoakim.dao.HoSoBenhAnService;
import nhakhoakim.dao.LuotKhamService;
import nhakhoakim.dao.NhaSiPhuTrachService;
import nhakhoakim.dao.PhienLamViec;
import nhakhoakim.entity.BenhNhan;
import nhakhoakim.entity.DichVu;
import nhakhoakim.entity.HoSoBenhAn;
import nhakhoakim.entity.LuotKham;

// Màn hình chỉ định tái khám: nha sĩ chọn ngày và dịch vụ, hệ thống tạo lịch hẹn Chờ xác nhận cho lễ tân xác nhận
@SuppressWarnings("serial") // lớp giao diện Swing, không cần serialVersionUID
public class ManHinhChiDinhTaiKham extends JPanel {

    private LuotKhamService luotKhamService = new LuotKhamService();
    private DichVuService dichVuService = new DichVuService();
    private HoSoBenhAnService hoSoService = new HoSoBenhAnService();
    private BenhNhanService benhNhanService = new BenhNhanService();
    private NhaSiPhuTrachService nhaSiService = new NhaSiPhuTrachService();

    private LuotKham luotKham;                                      // lượt khám đang khám
    private JTextField txtNgay = TienIch.oNhap(14);                 // ngày tái khám dd/MM/yyyy
    private JComboBox<DichVu> cmbDichVu;                            // dịch vụ của buổi tái khám
    private JButton btnHuy = TienIch.nutPhu("Hủy lịch tái khám đã tạo"); // chỉ hiện khi lượt khám đã đặt tái khám

    public ManHinhChiDinhTaiKham() {
        List<Component> cacKhoi = new ArrayList<>();
        cacKhoi.add(TienIch.tieuDe("Chỉ định tái khám"));
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
    }

    // Dựng form chọn ngày và dịch vụ
    private void dung(List<Component> cacKhoi) {
        HoSoBenhAn hs = hoSoService.timTheoMa(luotKham.getMaHoSo());
        BenhNhan bn = benhNhanService.timTheoMa(hs.getMaBN());
        cacKhoi.add(DongThongTinLuotKham.tao(bn == null ? hs.getMaBN() : bn.getHoTen(),
                nhaSiService.layTen(luotKham.getMaNhanVienPhuTrach()), luotKham));
        cmbDichVu =TienIch.taoChonDichVu(dichVuService.layTatCa(), "Chọn dịch vụ");
        txtNgay.setText(TienIch.ngay(LocalDate.now().plusDays(7)));
        txtNgay.setToolTipText("Ngày/tháng/năm, ví dụ 25/12/2026");

        JPanel form = TienIch.taoForm(new String[] {"Ngày tái khám (dd/MM/yyyy)", "Dịch vụ"}, new Component[] {txtNgay, cmbDichVu});
        JButton btnTao = TienIch.nutChinh("Tạo lịch tái khám");
        btnTao.addActionListener(e -> taoLich());
        btnHuy.addActionListener(e -> huyLich());
        capNhatNutHuy();
        JPanel the = TienIch.the(TienIch.doanChu("Lượt khám " + luotKham.getMaLuotKham() + " (khám ngày "
                + TienIch.ngay(luotKham.getGioBatDau().toLocalDate()) + "). Ngày tái khám phải sau ngày khám và sau hôm nay.", java.awt.Color.BLACK),
                form, TienIch.dong(btnTao, btnHuy));
        JPanel gioiHan = new JPanel(new java.awt.BorderLayout()); // thẻ chỉ rộng vừa phải
        gioiHan.setOpaque(false);
        gioiHan.add(the, java.awt.BorderLayout.WEST);
        the.setPreferredSize(new java.awt.Dimension(560, the.getPreferredSize().height));
        cacKhoi.add(gioiHan);
    }

    // Nút hủy chỉ hiện khi lượt khám đã có lịch tái khám
    private void capNhatNutHuy() {
        btnHuy.setVisible(luotKham.getNgayTaiKham() != null);
    }

    // Hủy lịch tái khám đã tạo (đặt nhầm ngày hoặc nhầm dịch vụ) để đặt lại
    private void huyLich() {
        if (!TienIch.xacNhan("Hủy lịch tái khám ngày " + TienIch.ngay(luotKham.getNgayTaiKham()) + "?")) {
            return;
        }
        try {
            luotKhamService.huyTaiKham(luotKham.getMaLuotKham());
            luotKham.setNgayTaiKham(null);
            capNhatNutHuy();
            TienIch.thongTin("Đã hủy lịch tái khám.");
        } catch (RuntimeException e) {
            TienIch.baoLoi(e);
        }
    }

    // Tạo lịch tái khám (trạng thái Chờ xác nhận)
    private void taoLich() {
        DichVu dichVu = (DichVu) cmbDichVu.getSelectedItem();
        if (dichVu == null) {
            TienIch.thongTin("Vui lòng chọn dịch vụ cho buổi tái khám.");
            return;
        }
        try {
            LocalDate ngay = TienIch.docNgay(txtNgay.getText());
            luotKhamService.datTaiKham(luotKham.getMaLuotKham(), ngay, dichVu.getMaDichVu());
            luotKham.setNgayTaiKham(ngay);
            capNhatNutHuy();
            TienIch.thongTin("Đã tạo lịch tái khám ngày " + TienIch.ngay(ngay) + " (chờ lễ tân xác nhận).");
        } catch (RuntimeException e) {
            TienIch.baoLoi(e);
        }
    }
}
