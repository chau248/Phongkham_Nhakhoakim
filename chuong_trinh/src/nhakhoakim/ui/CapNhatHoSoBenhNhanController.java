package nhakhoakim.ui;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import nhakhoakim.dao.BenhNhanService;
import nhakhoakim.entity.BenhNhan;

// Biểu mẫu chỉ cập nhật thông tin cá nhân, không thay đổi lịch hẹn hay hồ sơ bệnh án.
public class CapNhatHoSoBenhNhanController {

    private final BenhNhanService benhNhanService = new BenhNhanService();
    private BenhNhan benhNhan;
    private boolean daCapNhat;

    @FXML private TextField txtMaBN;
    @FXML private TextField txtHoTen;
    @FXML private DatePicker dpNgaySinh;
    @FXML private ComboBox<String> cmbGioiTinh;
    @FXML private TextField txtSdt;
    @FXML private TextField txtSoCCCD;
    @FXML private TextField txtEmail;
    @FXML private TextField txtDiaChi;
    @FXML private TextField txtNguoiLienHe;
    @FXML private TextField txtSdtNguoiLienHe;
    @FXML private TextField txtGhiChu;

    @FXML
    private void initialize() {
        dpNgaySinh.setConverter(TienIch.chuyenNgay());
        dpNgaySinh.setPromptText("dd/MM/yyyy");
        cmbGioiTinh.getItems().addAll("", "Nam", "Nữ", "Khác");
        txtSdt.setPromptText("10 số, bắt đầu bằng 0");
        txtSoCCCD.setPromptText("12 số");
        txtSdtNguoiLienHe.setPromptText("10 số, bắt đầu bằng 0");
    }

    public void datBenhNhan(BenhNhan benhNhan) {
        this.benhNhan = benhNhan;
        txtMaBN.setText(TienIch.chu(benhNhan.getMaBN()));
        txtHoTen.setText(TienIch.chu(benhNhan.getHoTen()));
        dpNgaySinh.setValue(benhNhan.getNgaySinh());
        cmbGioiTinh.setValue(TienIch.chu(benhNhan.getGioiTinh()));
        txtSdt.setText(TienIch.chu(benhNhan.getSdt()));
        txtSoCCCD.setText(TienIch.chu(benhNhan.getSoCCCD()));
        txtEmail.setText(TienIch.chu(benhNhan.getEmail()));
        txtDiaChi.setText(TienIch.chu(benhNhan.getDiaChi()));
        txtNguoiLienHe.setText(TienIch.chu(benhNhan.getNguoiLienHeKhanCap()));
        txtSdtNguoiLienHe.setText(TienIch.chu(benhNhan.getSdtNguoiLienHe()));
        txtGhiChu.setText(TienIch.chu(benhNhan.getGhiChu()));
    }

    public boolean capNhat() {
        try {
            BenhNhan capNhat = new BenhNhan();
            capNhat.setMaBN(benhNhan.getMaBN());
            capNhat.setNgayDangKy(benhNhan.getNgayDangKy());
            capNhat.setTrangThai(benhNhan.getTrangThai());
            capNhat.setHoTen(txtHoTen.getText());
            capNhat.setNgaySinh(dpNgaySinh.getValue());
            capNhat.setGioiTinh(cmbGioiTinh.getValue());
            capNhat.setSdt(txtSdt.getText());
            capNhat.setSoCCCD(txtSoCCCD.getText());
            capNhat.setEmail(txtEmail.getText());
            capNhat.setDiaChi(txtDiaChi.getText());
            capNhat.setNguoiLienHeKhanCap(txtNguoiLienHe.getText());
            capNhat.setSdtNguoiLienHe(txtSdtNguoiLienHe.getText());
            capNhat.setGhiChu(txtGhiChu.getText());

            benhNhanService.capNhat(capNhat);
            daCapNhat = true;
            TienIch.thongTin("Đã cập nhật thông tin cá nhân của bệnh nhân " + capNhat.getMaBN() + ".");
            return true;
        } catch (RuntimeException e) {
            TienIch.baoLoi(e);
            return false;
        }
    }

    public boolean daCapNhat() {
        return daCapNhat;
    }
}