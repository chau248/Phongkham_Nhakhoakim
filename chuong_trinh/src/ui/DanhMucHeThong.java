package ui;

import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import nhakhoakim.MainApp;
import controller.CauHinhController;
import entity.DanhMuc;

// Quản lý các danh mục dùng cho ô chọn: chuyên khoa
@SuppressWarnings("serial") // Lớp giao diện Swing
public class DanhMucHeThong extends JPanel implements MainApp.LamMoiDuoc {

    private CauHinhController cauHinhController = new CauHinhController();
    private List<Khoi> cacKhoi = new ArrayList<>();

    // Một danh mục: bảng tên
    private class Khoi extends JPanel {

        private DanhMuc danhMuc;
        private JTable bang;
        private JTextField txtTen = TienIch.oNhap(18);
        private JCheckBox chkPhongKham = new JCheckBox("Là phòng khám");
        private List<String> dsTen = new ArrayList<>();

        Khoi(DanhMuc danhMuc) {
            this.danhMuc = danhMuc;
            bang = TienIch.taoBang(new String[] {danhMuc.getNhan(), "Ghi chú"}, new int[] {260, 160}, "Danh mục đang trống.");
            JButton btnThem = TienIch.nutChinh("Thêm");
            btnThem.addActionListener(e -> them());
            JButton btnXoa = TienIch.nutDo("Xóa dòng chọn");
            btnXoa.addActionListener(e -> xoa());
            JButton btnDoiTen = TienIch.nutPhu("Đổi tên dòng chọn");
            btnDoiTen.addActionListener(e -> doiTen());
            btnDoiTen.setVisible(danhMuc.laGoiY());
            chkPhongKham.setOpaque(false);
            chkPhongKham.setFont(TienIch.FONT);
            chkPhongKham.setSelected(true);
            chkPhongKham.setVisible(danhMuc == DanhMuc.PHONG);
            setOpaque(false);
            TienIch.xep(this, 8, TienIch.the(TienIch.chuThuong(danhMuc.getNhan(), true), khungBang(TienIch.cuonBang(bang, 170)),
                    TienIch.dong(txtTen, chkPhongKham, btnThem, btnDoiTen, btnXoa)));
            tai();
        }

        // Thu hẹp độ rộng ưa thích vì ba bảng nằm cạnh nhau
        private javax.swing.JScrollPane khungBang(javax.swing.JScrollPane cuon) {
            cuon.setPreferredSize(new java.awt.Dimension(600, cuon.getPreferredSize().height));
            return cuon;
        }

        void tai() {
            dsTen = cauHinhController.layDanhMuc(danhMuc);
            List<String> phongKham = danhMuc == DanhMuc.PHONG ? cauHinhController.layPhongKham() : new ArrayList<>();
            List<String[]> dong = new ArrayList<>();
            for (String ten : dsTen) {
                dong.add(new String[] {ten, danhMuc == DanhMuc.PHONG ? (phongKham.contains(ten) ? "Phòng khám" : "Không khám") : ""});
            }
            TienIch.napBang(bang, dong);
        }

        void them() {
            try {
                if (danhMuc == DanhMuc.PHONG) {
                    cauHinhController.themPhong(txtTen.getText(), chkPhongKham.isSelected());
                } else {
                    cauHinhController.themDanhMuc(danhMuc, txtTen.getText());
                }
                txtTen.setText("");
                tai();
            } catch (RuntimeException e) {
                TienIch.baoLoi(e);
            }
        }

        void doiTen() {
            int vt = bang.getSelectedRow();
            if (vt < 0 || vt >= dsTen.size()) {
                TienIch.thongTin("Chọn một dòng trong bảng để đổi tên.");
                return;
            }
            String moi = TienIch.nhapChu("Đổi tên", "Tên mới cho \"" + dsTen.get(vt) + "\":");
            if (moi == null) {
                return;
            }
            try {
                cauHinhController.doiTenDanhMuc(danhMuc, dsTen.get(vt), moi);
                tai();
            } catch (RuntimeException e) {
                TienIch.baoLoi(e);
            }
        }

        void xoa() {
            int vt = bang.getSelectedRow();
            if (vt < 0 || vt >= dsTen.size()) {
                TienIch.thongTin("Chọn một dòng trong bảng để xóa.");
                return;
            }
            if (!TienIch.xacNhan("Xóa " + danhMuc.getNhan().toLowerCase() + " \"" + dsTen.get(vt) + "\"?")) {
                return;
            }
            try {
                cauHinhController.xoaDanhMuc(danhMuc, dsTen.get(vt));
                tai();
            } catch (RuntimeException e) {
                TienIch.baoLoi(e);
            }
        }
    }

    public DanhMucHeThong() {
        for (DanhMuc dm : DanhMuc.values()) {
            if (dm == DanhMuc.LOAI_DICH_VU) {
                continue; // Loại dịch vụ cố định, không cần sửa
            }
            cacKhoi.add(new Khoi(dm));
        }
        JPanel cacBang = new JPanel(new java.awt.GridLayout(0, 1, 0, 14));
        cacBang.setOpaque(false);
        for (Khoi k : cacKhoi) {
            cacBang.add(k);
        }
        TienIch.xepManHinh(this, TienIch.tieuDe("Danh mục"),
                TienIch.doanChu("Các danh mục này hiện trong ô chọn của màn hình nhân viên, dịch vụ và ca trực. "
                        + "Danh mục đang được dùng thì không xóa được.", TienIch.MAU_XAM),
                cacBang);
    }

    @Override
    public void lamMoi() {
        for (Khoi k : cacKhoi) {
            k.tai();
        }
    }
}
