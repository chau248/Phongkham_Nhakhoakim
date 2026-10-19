package nhakhoakim.ui;

import java.awt.Component;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import nhakhoakim.MainApp;
import nhakhoakim.dao.BenhNhanService;
import nhakhoakim.entity.BenhNhan;

// Màn hình thêm mới hoặc cập nhật thông tin hành chính của bệnh nhân (thêm mới thì hệ thống tạo luôn hồ sơ bệnh án rỗng)
@SuppressWarnings("serial") // lớp giao diện Swing, không cần serialVersionUID
public class ManHinhHoSoBenhNhan extends JPanel {

    private BenhNhanService benhNhanService = new BenhNhanService();

    private BenhNhan benhNhan;                                   // null nghĩa là đang thêm mới
    private JLabel lblMa = new JLabel("(tự sinh khi lưu)");      // mã bệnh nhân
    private JTextField txtHoTen = TienIch.oNhap(32);
    private JTextField txtNgaySinh = TienIch.oNhap(32);          // dd/MM/yyyy
    private JComboBox<String> cmbGioiTinh = new JComboBox<>(new String[] {"", "Nam", "Nữ", "Khác"});
    private JTextField txtSdt = TienIch.oNhap(32);
    private JTextField txtCCCD = TienIch.oNhap(32);
    private JTextField txtEmail = TienIch.oNhap(32);
    private JTextField txtDiaChi = TienIch.oNhap(32);
    private JTextField txtNguoiLienHe = TienIch.oNhap(32);       // người liên hệ khẩn cấp
    private JTextField txtSdtNguoiLienHe = TienIch.oNhap(32);
    private JTextField txtGhiChu = TienIch.oNhap(32);

    // Truyền bệnh nhân để sửa, hoặc null để thêm mới
    public ManHinhHoSoBenhNhan(BenhNhan benhNhan) {
        this.benhNhan = benhNhan;

        lblMa.setFont(TienIch.FONT);
        cmbGioiTinh.setFont(TienIch.FONT);
        txtNgaySinh.setToolTipText("Ngày/tháng/năm, ví dụ 25/12/1990");
        txtSdt.setToolTipText("10 số, bắt đầu bằng 0");
        txtCCCD.setToolTipText("12 số");
        txtSdtNguoiLienHe.setToolTipText("10 số, bắt đầu bằng 0");

        String[] ten = {"Mã bệnh nhân", "Họ tên (*)", "Ngày sinh (*) dd/MM/yyyy", "Giới tính", "Số điện thoại", "Số căn cước (CCCD)", "Email", "Địa chỉ",
            "Người liên hệ khẩn cấp", "SĐT người liên hệ", "Ghi chú"};
        Component[] o = {lblMa, txtHoTen, txtNgaySinh, cmbGioiTinh, txtSdt, txtCCCD, txtEmail, txtDiaChi, txtNguoiLienHe, txtSdtNguoiLienHe, txtGhiChu};

        JButton btnLuu = TienIch.nutChinh(benhNhan == null ? "Thêm bệnh nhân" : "Lưu thay đổi");
        btnLuu.addActionListener(e -> luu());

        JPanel the = TienIch.the(TienIch.taoForm(ten, o),
                TienIch.doanChu("Bệnh nhân dưới 18 tuổi bắt buộc nhập người liên hệ khẩn cấp (tên và số điện thoại).", TienIch.MAU_XAM),
                TienIch.dong(btnLuu));
        JPanel gioiHan = new JPanel(new java.awt.BorderLayout()); // thẻ chỉ rộng vừa phải, không kéo hết chiều ngang cửa sổ
        gioiHan.setOpaque(false);
        gioiHan.add(the, java.awt.BorderLayout.WEST);
        the.setPreferredSize(new java.awt.Dimension(640, the.getPreferredSize().height));
        TienIch.xepManHinh(this, TienIch.tieuDe(benhNhan == null ? "Thêm bệnh nhân mới" : "Cập nhật thông tin bệnh nhân"), gioiHan);

        if (benhNhan != null) {
            napDuLieu();
        }
    }

    // Đổ thông tin bệnh nhân đang sửa vào các ô
    private void napDuLieu() {
        lblMa.setText(benhNhan.getMaBN());
        txtHoTen.setText(TienIch.chu(benhNhan.getHoTen()));
        txtNgaySinh.setText(TienIch.ngay(benhNhan.getNgaySinh()));
        cmbGioiTinh.setSelectedItem(TienIch.chu(benhNhan.getGioiTinh()));
        txtSdt.setText(TienIch.chu(benhNhan.getSdt()));
        txtCCCD.setText(TienIch.chu(benhNhan.getSoCCCD()));
        txtEmail.setText(TienIch.chu(benhNhan.getEmail()));
        txtDiaChi.setText(TienIch.chu(benhNhan.getDiaChi()));
        txtNguoiLienHe.setText(TienIch.chu(benhNhan.getNguoiLienHeKhanCap()));
        txtSdtNguoiLienHe.setText(TienIch.chu(benhNhan.getSdtNguoiLienHe()));
        txtGhiChu.setText(TienIch.chu(benhNhan.getGhiChu()));
    }

    // Lấy dữ liệu từ các ô rồi thêm mới hoặc cập nhật. Dữ liệu gán vào đối tượng mới, chỉ khi Service nhận thì mới
    // cập nhật đối tượng đang hiển thị (nhập sai bị từ chối thì danh sách bệnh nhân không bị đổi theo)
    private void luu() {
        try {
            BenhNhan bn = new BenhNhan();
            if (benhNhan != null) {
                bn.setMaBN(benhNhan.getMaBN());
                bn.setNgayDangKy(benhNhan.getNgayDangKy());
                bn.setTrangThai(benhNhan.getTrangThai());
            }
            bn.setHoTen(txtHoTen.getText());
            bn.setNgaySinh(TienIch.docNgay(txtNgaySinh.getText()));
            bn.setGioiTinh((String) cmbGioiTinh.getSelectedItem());
            bn.setSdt(txtSdt.getText());
            bn.setSoCCCD(txtCCCD.getText());
            bn.setEmail(txtEmail.getText());
            bn.setDiaChi(txtDiaChi.getText());
            bn.setNguoiLienHeKhanCap(txtNguoiLienHe.getText());
            bn.setSdtNguoiLienHe(txtSdtNguoiLienHe.getText());
            bn.setGhiChu(txtGhiChu.getText());
            if (benhNhan == null) {
                BenhNhan moi = benhNhanService.them(bn);
                TienIch.thongTin("Đã thêm bệnh nhân " + moi.getMaBN() + " và tạo hồ sơ bệnh án rỗng.");
                MainApp.thayNoiDung(new ManHinhHoSoBenhAn(moi)); // thêm xong thì không cần quay lại form thêm
            } else {
                benhNhanService.capNhat(bn);
                chepThongTin(bn, benhNhan);
                napDuLieu(); // hiện lại giá trị đã được Service chuẩn hóa
                TienIch.thongTin("Đã cập nhật thông tin bệnh nhân " + bn.getMaBN() + ".");
            }
        } catch (RuntimeException e) {
            TienIch.baoLoi(e);
        }
    }

    // Chép các thông tin hành chính từ bệnh nhân đã lưu thành công sang đối tượng đang hiển thị
    private void chepThongTin(BenhNhan nguon, BenhNhan dich) {
        dich.setHoTen(nguon.getHoTen());
        dich.setNgaySinh(nguon.getNgaySinh());
        dich.setGioiTinh(nguon.getGioiTinh());
        dich.setSdt(nguon.getSdt());
        dich.setSoCCCD(nguon.getSoCCCD());
        dich.setEmail(nguon.getEmail());
        dich.setDiaChi(nguon.getDiaChi());
        dich.setNguoiLienHeKhanCap(nguon.getNguoiLienHeKhanCap());
        dich.setSdtNguoiLienHe(nguon.getSdtNguoiLienHe());
        dich.setGhiChu(nguon.getGhiChu());
    }
}
