package nhakhoakim.ui;

import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import nhakhoakim.MainApp;
import nhakhoakim.controller.HoaDonController;
import nhakhoakim.controller.HoaDonController.DongChoThanhToan;

// Màn hình danh sách chờ thanh toán (HD-01): các lượt khám đã hoàn thành nhưng chưa lập hóa đơn
@SuppressWarnings("serial") // lớp giao diện Swing, không cần serialVersionUID
public class ManHinhChoThanhToan extends JPanel implements MainApp.LamMoiDuoc {

    private HoaDonController hoaDonController = new HoaDonController();

    private JTable tblCho = TienIch.taoBang(new String[] {"Lượt khám", "Bệnh nhân", "Nha sĩ", "Khám xong lúc", "Tạm tính (gồm VAT)"},
            new int[] {100, 220, 200, 140, 150}, "Không có lượt khám nào đang chờ thanh toán.");
    private List<DongChoThanhToan> dsCho = new ArrayList<>();
    private JLabel lblKetQua = new JLabel();

    public ManHinhChoThanhToan() {
        JButton btnLap = TienIch.nutChinh("Lập hóa đơn cho lượt khám đã chọn");
        btnLap.addActionListener(e -> lap());
        JButton btnTai = TienIch.nutPhu("Tải lại");
        btnTai.addActionListener(e -> tai());
        lblKetQua.setFont(TienIch.FONT);
        lblKetQua.setForeground(TienIch.MAU_XAM);
        TienIch.xepManHinh(this, TienIch.tieuDe("Danh sách chờ thanh toán"),
                TienIch.doanChu("Các lượt khám đã hoàn tất, chưa có hóa đơn. Chọn một lượt khám rồi bấm Lập hóa đơn.", TienIch.MAU_XAM),
                lblKetQua, TienIch.cuonBang(tblCho, 340), TienIch.dong(btnLap, btnTai));
        tai();
    }

    @Override
    public void lamMoi() {
        tai();
    }

    // Tải danh sách lượt khám đang chờ lập hóa đơn
    private void tai() {
        try {
            dsCho = hoaDonController.layChoThanhToan();
            List<String[]> dong = new ArrayList<>();
            for (DongChoThanhToan d : dsCho) {
                dong.add(new String[] {d.luotKham.getMaLuotKham(), d.maBN + " - " + d.tenBenhNhan, d.tenNhaSi, TienIch.ngayGio(d.luotKham.getGioKetThuc()),
                    HoaDonController.dinhDangTien(d.tamTinh)});
            }
            TienIch.napBang(tblCho, dong);
            lblKetQua.setText(dong.size() + " lượt khám đang chờ thanh toán.");
        } catch (RuntimeException e) {
            TienIch.baoLoi(e);
        }
    }

    // Mở màn hình lập hóa đơn cho lượt khám đang chọn
    private void lap() {
        int vt = tblCho.getSelectedRow();
        if (vt < 0 || vt >= dsCho.size()) {
            TienIch.thongTin("Vui lòng chọn một lượt khám trong bảng.");
            return;
        }
        MainApp.doiNoiDung(new ManHinhLapHoaDon(dsCho.get(vt).luotKham.getMaLuotKham()));
    }
}
