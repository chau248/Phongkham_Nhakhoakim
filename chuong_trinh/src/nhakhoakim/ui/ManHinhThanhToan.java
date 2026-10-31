package nhakhoakim.ui;

import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import nhakhoakim.MainApp;
import nhakhoakim.controller.HoaDonController;
import nhakhoakim.dao.PhienLamViec;
import nhakhoakim.entity.ChiTietHoaDon;
import nhakhoakim.entity.HoaDon;
import nhakhoakim.entity.NhanVien;
import nhakhoakim.entity.VaiTro;

// Màn hình thanh toán và in hóa đơn (HD-05, HD-06): chọn hóa đơn, chọn hình thức, thanh toán; in sau khi đã thanh toán; Quản lý được hủy hóa đơn chưa thanh toán
@SuppressWarnings("serial") // lớp giao diện Swing, không cần serialVersionUID
public class ManHinhThanhToan extends JPanel implements MainApp.LamMoiDuoc {

    private static final String TAT_CA = "Tất cả";

    private HoaDonController hoaDonController = new HoaDonController();
    private BangTen ten = new BangTen();

    private JComboBox<String> cmbLoc = new JComboBox<>(new String[] {HoaDonController.CHUA_THANH_TOAN, HoaDonController.DA_THANH_TOAN, "Đã hủy", TAT_CA});
    private JTable tblHoaDon = TienIch.taoBang(new String[] {"Mã hóa đơn", "Bệnh nhân", "Ngày lập", "Tổng tiền", "Hình thức", "Trạng thái"},
            new int[] {100, 220, 130, 120, 110, 120}, "Không có hóa đơn nào.");
    private List<HoaDon> dsHoaDon = new ArrayList<>();
    private JTable tblChiTiet = TienIch.taoBang(new String[] {"Dịch vụ", "Răng", "Số lượng", "Đơn giá", "Thành tiền"},
            new int[] {280, 70, 80, 120, 130}, "Chọn một hóa đơn để xem chi tiết.");
    private JComboBox<String> cmbHinhThuc = new JComboBox<>(HoaDonController.HINH_THUC.toArray(new String[0]));
    private JLabel lblTong = TienIch.chuThuong(" ", true);
    private String maChon; // hóa đơn mở sẵn khi vào từ màn hình lập hóa đơn

    public ManHinhThanhToan() {
        this(null);
    }

    // Mở sẵn một hóa đơn (vừa lập xong) hoặc để trống để người dùng tự chọn
    public ManHinhThanhToan(String maHoaDon) {
        this.maChon = maHoaDon;
        cmbLoc.setFont(TienIch.FONT);
        cmbHinhThuc.setFont(TienIch.FONT);
        cmbLoc.addActionListener(e -> tai());
        tblHoaDon.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int vt = tblHoaDon.getSelectedRow();
                if (vt >= 0 && vt < dsHoaDon.size()) {
                    maChon = dsHoaDon.get(vt).getMaHoaDon(); // nhớ hóa đơn đang chọn để tải lại vẫn chọn đúng dòng đó
                }
                xemChiTiet();
            }
        });
        JButton btnThanhToan = TienIch.nutChinh("Thanh toán");
        btnThanhToan.addActionListener(e -> thanhToan());
        JButton btnIn = TienIch.nutPhu("In hóa đơn");
        btnIn.addActionListener(e -> in());
        JButton btnHuy = TienIch.nutDo("Hủy hóa đơn");
        btnHuy.addActionListener(e -> huy());
        NhanVien nv = PhienLamViec.getNhanVienHienTai();
        btnHuy.setVisible(nv != null && VaiTro.QUAN_LY.equals(nv.getVaiTro()));
        lblTong.setForeground(TienIch.MAU_DAM);
        TienIch.xepManHinh(this, TienIch.tieuDe("Thanh toán & in hóa đơn"),
                TienIch.dong(TienIch.nhan("Hiện"), cmbLoc), TienIch.cuonBang(tblHoaDon, 200),
                TienIch.chuThuong("Chi tiết hóa đơn", true), TienIch.cuonBang(tblChiTiet, 150), lblTong,
                TienIch.dong(TienIch.nhan("Hình thức thanh toán"), cmbHinhThuc, btnThanhToan, btnIn, btnHuy));
        if (maHoaDon != null) {
            cmbLoc.setSelectedItem(TAT_CA);
        }
        tai();
    }

    @Override
    public void lamMoi() {
        tai();
    }

    // Tải các hóa đơn theo bộ lọc trạng thái; có hóa đơn mở sẵn thì chọn luôn dòng đó
    private void tai() {
        try {
            String loc = (String) cmbLoc.getSelectedItem();
            dsHoaDon = new ArrayList<>();
            List<String[]> dong = new ArrayList<>();
            int chon = -1;
            for (HoaDon hd : hoaDonController.layTatCa()) {
                if (TAT_CA.equals(loc) || hd.getTrangThai().equals(loc)) {
                    if (hd.getMaHoaDon().equals(maChon)) {
                        chon = dsHoaDon.size();
                    }
                    dsHoaDon.add(hd);
                    dong.add(new String[] {hd.getMaHoaDon(), ten.benhNhan(hd.getMaBenhNhan()), TienIch.ngayGio(hd.getNgayLap()),
                        HoaDonController.dinhDangTien(hd.getTongTien()), hd.getHinhThucThanhToan(), hd.getTrangThai()});
                }
            }
            TienIch.napBang(tblHoaDon, dong);
            if (chon >= 0) {
                tblHoaDon.setRowSelectionInterval(chon, chon);
            }
            if (dsHoaDon.isEmpty()) {
                TienIch.napBang(tblChiTiet, new ArrayList<>());
                lblTong.setText(" ");
            }
        } catch (RuntimeException e) {
            TienIch.baoLoi(e);
        }
    }

    private HoaDon dangChon() {
        int vt = tblHoaDon.getSelectedRow();
        if (vt < 0 || vt >= dsHoaDon.size()) {
            TienIch.thongTin("Vui lòng chọn một hóa đơn trong bảng.");
            return null;
        }
        return dsHoaDon.get(vt);
    }

    // Hiện các dòng chi tiết và tổng tiền của hóa đơn đang chọn
    private void xemChiTiet() {
        int vt = tblHoaDon.getSelectedRow();
        if (vt < 0 || vt >= dsHoaDon.size()) {
            return;
        }
        try {
            HoaDon hd = hoaDonController.timTheoMa(dsHoaDon.get(vt).getMaHoaDon());
            List<String[]> dong = new ArrayList<>();
            for (ChiTietHoaDon ct : hd.getDsChiTiet()) {
                dong.add(new String[] {ten.dichVu(ct.getMaDichVu()), ct.getSoFDIRang() > 0 ? String.valueOf(ct.getSoFDIRang()) : "", String.valueOf(ct.getSoLuong()),
                    HoaDonController.dinhDangTien(ct.getDonGia()),
                    HoaDonController.dinhDangTien(ct.getDonGia().multiply(java.math.BigDecimal.valueOf(ct.getSoLuong())).subtract(ct.getGiamGia()))});
            }
            TienIch.napBang(tblChiTiet, dong);
            lblTong.setText("Giảm giá " + HoaDonController.dinhDangTien(hd.getTienGiamGia()) + " | VAT " + hd.getPhanTramThue().toPlainString() + "% | TỔNG "
                    + HoaDonController.dinhDangTien(hd.getTongTien()) + " đồng" + (hd.getGhiChu() == null ? "" : " | " + hd.getGhiChu()));
        } catch (RuntimeException e) {
            TienIch.baoLoi(e);
        }
    }

    // Thanh toán hóa đơn đang chọn bằng hình thức đang chọn
    private void thanhToan() {
        HoaDon hd = dangChon();
        if (hd == null) {
            return;
        }
        String hinhThuc = (String) cmbHinhThuc.getSelectedItem();
        if (!TienIch.xacNhan("Thanh toán hóa đơn " + hd.getMaHoaDon() + " (" + HoaDonController.dinhDangTien(hd.getTongTien()) + " đồng) bằng " + hinhThuc
                + "? Sau khi thanh toán sẽ không sửa được.")) {
            return;
        }
        try {
            hoaDonController.thanhToan(hd.getMaHoaDon(), hinhThuc);
            maChon = hd.getMaHoaDon();
            TienIch.thongTin("Đã thanh toán hóa đơn " + hd.getMaHoaDon() + ". Bạn có thể bấm In hóa đơn.");
            cmbLoc.setSelectedItem(HoaDonController.DA_THANH_TOAN); // chuyển sang danh sách Đã thanh toán để hóa đơn vừa trả vẫn được chọn
            tai();
        } catch (RuntimeException e) {
            TienIch.baoLoi(e);
        }
    }

    // Xem trước và in hóa đơn đang chọn (chỉ hóa đơn đã thanh toán)
    private void in() {
        HoaDon hd = dangChon();
        if (hd == null) {
            return;
        }
        try {
            HopThoaiInHoaDon.hien(hoaDonController.taoNoiDungIn(hd.getMaHoaDon()));
        } catch (RuntimeException e) {
            TienIch.baoLoi(e);
        }
    }

    // Hủy hóa đơn chưa thanh toán (Quản lý), hỏi lý do
    private void huy() {
        HoaDon hd = dangChon();
        if (hd == null) {
            return;
        }
        String lyDo = TienIch.nhapChu("Hủy hóa đơn", "Lý do hủy hóa đơn " + hd.getMaHoaDon() + ":");
        if (lyDo == null) {
            return;
        }
        try {
            hoaDonController.huyHoaDon(hd.getMaHoaDon(), lyDo);
            TienIch.thongTin("Đã hủy hóa đơn " + hd.getMaHoaDon() + ". Lượt khám quay về danh sách chờ thanh toán.");
            tai();
        } catch (RuntimeException e) {
            TienIch.baoLoi(e);
        }
    }
}
