package nhakhoakim.ui;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import nhakhoakim.MainApp;
import nhakhoakim.controller.LichHenController;
import nhakhoakim.entity.LichHen;

// Màn hình xác nhận lịch hẹn (LH-07): các lịch Chờ xác nhận từ hôm nay trở đi, kể cả lịch tái khám do nha sĩ tạo
@SuppressWarnings("serial") // lớp giao diện Swing, không cần serialVersionUID
public class ManHinhXacNhanLichHen extends JPanel implements MainApp.LamMoiDuoc {

    private static final int SO_NGAY_TOI = 90; // xem các lịch chờ trong 90 ngày tới

    private LichHenController lichHenController = new LichHenController();
    private BangTen ten = new BangTen();

    private JTable tblLich = TienIch.taoBang(new String[] {"Ngày", "Giờ", "Bệnh nhân", "Dịch vụ", "Nha sĩ", "Phòng", "Mã lịch"},
            new int[] {90, 60, 200, 190, 170, 90, 90}, "Không có lịch nào đang chờ xác nhận.");
    private List<LichHen> dsLich = new ArrayList<>();
    private JLabel lblKetQua = new JLabel();

    public ManHinhXacNhanLichHen() {
        JButton btnXacNhan = TienIch.nutChinh("Xác nhận lịch đã chọn");
        btnXacNhan.addActionListener(e -> xacNhan());
        JButton btnTai = TienIch.nutPhu("Tải lại");
        btnTai.addActionListener(e -> tai());
        lblKetQua.setFont(TienIch.FONT);
        lblKetQua.setForeground(TienIch.MAU_XAM);
        TienIch.cotXuongDong(tblLich, 3);
        TienIch.xepManHinh(this, TienIch.tieuDe("Xác nhận lịch hẹn"),
                TienIch.doanChu("Gọi điện xác nhận với bệnh nhân rồi bấm Xác nhận. Hệ thống kiểm tra lại trùng giờ, ca trực và chứng chỉ của nha sĩ lúc xác nhận.", TienIch.MAU_XAM),
                lblKetQua, TienIch.cuonBang(tblLich, 320), TienIch.dong(btnXacNhan, btnTai));
        tai();
    }

    @Override
    public void lamMoi() {
        tai();
    }

    // Tải các lịch Chờ xác nhận từ hôm nay đến 90 ngày tới
    private void tai() {
        try {
            dsLich = new ArrayList<>();
            List<String[]> dong = new ArrayList<>();
            for (LichHen lh : lichHenController.layTheoKhoang(LocalDate.now(), LocalDate.now().plusDays(SO_NGAY_TOI))) {
                if (LichHenController.CHO_XAC_NHAN.equals(lh.getTrangThai())) {
                    dsLich.add(lh);
                    dong.add(new String[] {TienIch.ngay(lh.getNgayHen()), TienIch.gio(lh.getGioHen()), ten.benhNhan(lh.getMaBN()), ten.dichVu(lh.getMaDichVu()),
                        ten.nhaSi(lh.getMaNhanVien()), TienIch.chu(lh.getPhong()), lh.getMaLichHen()});
                }
            }
            TienIch.napBang(tblLich, dong);
            lblKetQua.setText(dong.size() + " lịch đang chờ xác nhận.");
        } catch (RuntimeException e) {
            TienIch.baoLoi(e);
        }
    }

    // Xác nhận lịch đang chọn
    private void xacNhan() {
        int vt = tblLich.getSelectedRow();
        if (vt < 0 || vt >= dsLich.size()) {
            TienIch.thongTin("Vui lòng chọn một lịch hẹn trong bảng.");
            return;
        }
        LichHen lh = dsLich.get(vt);
        try {
            lichHenController.xacNhan(lh.getMaLichHen());
            TienIch.thongTin("Đã xác nhận lịch " + lh.getMaLichHen() + " của " + ten.benhNhan(lh.getMaBN()) + ".");
            tai();
        } catch (RuntimeException e) {
            TienIch.baoLoi(e);
        }
    }
}
