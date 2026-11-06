package ui;

import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTable;
import controller.LuotKhamController;
import entity.BenhNhan;
import entity.LuotKham;

// In đơn thuốc: chọn bệnh nhân
@SuppressWarnings("serial") // Lớp giao diện Swing
public class InDonThuoc extends JPanel {

    private LuotKhamController luotKhamController = new LuotKhamController();

    private TienIch.ChonBenhNhan chonBenhNhan = new TienIch.ChonBenhNhan();
    private JTable tblLuot = TienIch.taoBang(new String[] {"Ngày khám", "Nha sĩ", "Chẩn đoán", "Đơn thuốc"}, new int[] {130, 150, 260, 300},
            "Chọn bệnh nhân rồi bấm Xem. Bệnh nhân chưa có lượt khám nào.");
    private List<LuotKham> dsLuot = new ArrayList<>();

    public InDonThuoc() {
        JButton btnXem = TienIch.nutChinh("Xem");
        btnXem.addActionListener(e -> tai());
        JButton btnIn = TienIch.nutChinh("Xem trước và in đơn thuốc");
        btnIn.addActionListener(e -> in());
        chonBenhNhan.addChonListener(e -> {
            dsLuot = new ArrayList<>();
            TienIch.napBang(tblLuot, new ArrayList<>());
        });
        TienIch.cotXuongDong(tblLuot, 3);
        TienIch.xepManHinh(this, TienIch.tieuDe("In đơn thuốc"),
                TienIch.doanChu("Chọn lượt khám có kê đơn để in. Mỗi thuốc ghi một dòng (hoặc cách nhau dấu chấm phẩy) ở màn hình Khám.", TienIch.MAU_XAM),
                TienIch.dong(TienIch.nhan("Bệnh nhân"), chonBenhNhan, btnXem), TienIch.cuonBang(tblLuot, 300), TienIch.dong(btnIn));
    }

    private void tai() {
        BenhNhan bn = chonBenhNhan.getBenhNhan();
        if (bn == null) {
            TienIch.thongTin("Vui lòng chọn một bệnh nhân.");
            return;
        }
        try {
            dsLuot = luotKhamController.layTheoBenhNhan(bn.getMaBN());
            List<String[]> dong = new ArrayList<>();
            for (LuotKham lk : dsLuot) {
                dong.add(new String[] {TienIch.ngayGio(lk.getGioBatDau()), lk.getMaNhanVienPhuTrach().getHoTen(), TienIch.chu(lk.getChanDoan()),
                    lk.getDonThuoc() == null || lk.getDonThuoc().trim().isEmpty() ? "(không có đơn thuốc)" : lk.getDonThuoc()});
            }
            TienIch.napBang(tblLuot, dong);
        } catch (RuntimeException e) {
            TienIch.baoLoi(e);
        }
    }

    private void in() {
        int vt = tblLuot.getSelectedRow();
        if (vt < 0 || vt >= dsLuot.size()) {
            TienIch.thongTin("Vui lòng chọn một lượt khám trong bảng.");
            return;
        }
        try {
            HopThoai.inVanBan("Đơn thuốc", luotKhamController.taoNoiDungDonThuoc(dsLuot.get(vt).getMaLuotKham()));
        } catch (RuntimeException e) {
            TienIch.baoLoi(e);
        }
    }
}
