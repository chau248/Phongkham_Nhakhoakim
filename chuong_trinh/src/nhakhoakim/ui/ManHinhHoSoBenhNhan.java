package nhakhoakim.ui;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import nhakhoakim.MainApp;
import nhakhoakim.dao.BenhNhanService;
import nhakhoakim.entity.BenhNhan;

// Màn hình thêm mới hoặc cập nhật thông tin hành chính của bệnh nhân (thêm mới thì hệ thống tạo luôn hồ sơ bệnh án rỗng)
public class ManHinhHoSoBenhNhan extends VBox {

    private BenhNhanService benhNhanService = new BenhNhanService();

    private BenhNhan benhNhan;                                   // null nghĩa là đang thêm mới
    private Label lblMa = new Label("(tự sinh khi lưu)");        // mã bệnh nhân
    private TextField txtHoTen = new TextField();
    private DatePicker dtpNgaySinh = new DatePicker();
    private ComboBox<String> cmbGioiTinh = new ComboBox<>();
    private TextField txtSdt = new TextField();
    private TextField txtCCCD = new TextField();
    private TextField txtEmail = new TextField();
    private TextField txtDiaChi = new TextField();
    private TextField txtNguoiLienHe = new TextField();          // người liên hệ khẩn cấp
    private TextField txtSdtNguoiLienHe = new TextField();
    private TextField txtGhiChu = new TextField();

    // Truyền bệnh nhân để sửa, hoặc null để thêm mới
    public ManHinhHoSoBenhNhan(BenhNhan benhNhan) {
        this.benhNhan = benhNhan;
        setSpacing(14);
        setPadding(new Insets(24));

        cmbGioiTinh.getItems().addAll("", "Nam", "Nữ", "Khác");
        cmbGioiTinh.setValue("");
        txtSdt.setPromptText("10 số, bắt đầu bằng 0");
        txtCCCD.setPromptText("12 số");
        txtSdtNguoiLienHe.setPromptText("10 số, bắt đầu bằng 0");

        GridPane luoi = new GridPane();
        luoi.setHgap(14);
        luoi.setVgap(10);
        String[] ten = {"Mã bệnh nhân", "Họ tên (*)", "Ngày sinh", "Giới tính", "Số điện thoại", "Số căn cước (CCCD)", "Email", "Địa chỉ",
            "Người liên hệ khẩn cấp", "SĐT người liên hệ", "Ghi chú"};
        javafx.scene.Node[] o = {lblMa, txtHoTen, dtpNgaySinh, cmbGioiTinh, txtSdt, txtCCCD, txtEmail, txtDiaChi, txtNguoiLienHe,
            txtSdtNguoiLienHe, txtGhiChu};
        for (int i = 0; i < ten.length; i++) {
            luoi.add(TienIch.nhan(ten[i]), 0, i);
            luoi.add(o[i], 1, i);
            if (o[i] instanceof TextField) {
                ((TextField) o[i]).setPrefWidth(380);
            }
        }
        Label ghiChuTuoi = new Label("Bệnh nhân dưới 18 tuổi bắt buộc nhập người liên hệ khẩn cấp (tên và số điện thoại).");
        ghiChuTuoi.setStyle("-fx-text-fill: #6B7C8A;");

        Button btnLuu = TienIch.nutChinh(benhNhan == null ? "Thêm bệnh nhân" : "Lưu thay đổi");
        btnLuu.setOnAction(e -> luu());

        VBox the = new VBox(12, luoi, ghiChuTuoi, btnLuu);
        the.setPadding(new Insets(18));
        the.setStyle(TienIch.THE);
        the.setMaxWidth(640);
        getChildren().addAll(TienIch.tieuDe(benhNhan == null ? "Thêm bệnh nhân mới" : "Cập nhật thông tin bệnh nhân"), the);

        if (benhNhan != null) {
            napDuLieu();
        }
    }

    // Đổ thông tin bệnh nhân đang sửa vào các ô
    private void napDuLieu() {
        lblMa.setText(benhNhan.getMaBN());
        txtHoTen.setText(TienIch.chu(benhNhan.getHoTen()));
        dtpNgaySinh.setValue(benhNhan.getNgaySinh());
        cmbGioiTinh.setValue(TienIch.chu(benhNhan.getGioiTinh()));
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
            bn.setNgaySinh(dtpNgaySinh.getValue());
            bn.setGioiTinh(cmbGioiTinh.getValue());
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
