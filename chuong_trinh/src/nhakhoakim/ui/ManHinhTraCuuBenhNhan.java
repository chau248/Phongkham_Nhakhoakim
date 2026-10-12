package nhakhoakim.ui;

import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import nhakhoakim.MainApp;
import nhakhoakim.dao.BenhNhanService;
import nhakhoakim.dao.PhienLamViec;
import nhakhoakim.entity.BenhNhan;
import nhakhoakim.entity.NhanVien;
import nhakhoakim.entity.VaiTro;

// Màn hình tìm kiếm bệnh nhân theo mã, họ tên hoặc số điện thoại; từ đây mở hồ sơ bệnh án hoặc sửa thông tin
public class ManHinhTraCuuBenhNhan extends VBox implements LamMoiDuoc {

    private BenhNhanService benhNhanService = new BenhNhanService();

    private TextField txtTuKhoa = new TextField();               // từ khóa tìm kiếm
    private TableView<String[]> tblBenhNhan = TienIch.taoBang("Không có bệnh nhân nào."); // kết quả
    private Label lblKetQua = new Label();                       // số kết quả
    private List<BenhNhan> dsBenhNhan = new ArrayList<>();       // dữ liệu của bảng
    private Button btnSua = TienIch.nutPhu("Sửa thông tin");
    private Button btnThem = TienIch.nutPhu("Thêm bệnh nhân mới");
    private Label lblQuyen = new Label();                        // giải thích vì sao không sửa được

    public ManHinhTraCuuBenhNhan() {
        setSpacing(14);
        setPadding(new Insets(24));

        txtTuKhoa.setPromptText("Nhập mã, họ tên hoặc số điện thoại");
        txtTuKhoa.setPrefWidth(320);
        txtTuKhoa.setOnAction(e -> tim());
        Button btnTim = TienIch.nutChinh("Tìm kiếm");
        btnTim.setOnAction(e -> tim());

        tblBenhNhan.getColumns().add(TienIch.cot("Mã", 90, 0));
        tblBenhNhan.getColumns().add(TienIch.cot("Họ tên", 200, 1));
        tblBenhNhan.getColumns().add(TienIch.cot("Ngày sinh", 100, 2));
        tblBenhNhan.getColumns().add(TienIch.cot("Giới tính", 80, 3));
        tblBenhNhan.getColumns().add(TienIch.cot("Số điện thoại", 120, 4));
        tblBenhNhan.getColumns().add(TienIch.cot("Địa chỉ", 280, 5));
        tblBenhNhan.setPrefHeight(340);
        // Bấm đúp chỉ mở hồ sơ khi bấm trúng một dòng có dữ liệu (bấm vào vùng trống của bảng thì bỏ qua)
        tblBenhNhan.setRowFactory(bang -> {
            TableRow<String[]> dong = new TableRow<>();
            dong.setOnMouseClicked(e -> {
                if (e.getClickCount() == 2 && !dong.isEmpty()) {
                    tblBenhNhan.getSelectionModel().select(dong.getIndex());
                    xemHoSo();
                }
            });
            return dong;
        });

        Button btnHoSo = TienIch.nutChinh("Xem hồ sơ bệnh án");
        btnHoSo.setOnAction(e -> xemHoSo());
        btnSua.setOnAction(e -> {
            BenhNhan bn = dangChon();
            if (bn != null) {
                MainApp.doiNoiDung(new ManHinhHoSoBenhNhan(bn));
            }
        });
        btnThem.setOnAction(e -> MainApp.doiNoiDung(new ManHinhHoSoBenhNhan(null)));

        lblKetQua.setStyle("-fx-text-fill: #6B7C8A;");
        lblQuyen.setStyle("-fx-text-fill: #6B7C8A;");
        getChildren().addAll(TienIch.tieuDe("Tìm kiếm bệnh nhân"), new HBox(10, txtTuKhoa, btnTim), lblKetQua, tblBenhNhan,
                new HBox(10, btnHoSo, btnSua, btnThem), lblQuyen);
        apDungQuyenSua();
        tim();
    }

    // Quay lại từ màn hình sửa hoặc thêm bệnh nhân thì tìm lại để bảng có dữ liệu mới
    @Override
    public void lamMoi() {
        apDungQuyenSua();
        tim();
    }

    // Chỉ Lễ tân và Quản lý được thêm, sửa thông tin hành chính (BN-05); vai trò khác thì khóa hai nút và nói rõ lý do
    private void apDungQuyenSua() {
        NhanVien nv = null;
        try {
            nv = PhienLamViec.getNhanVienHienTai();
        } catch (RuntimeException e) {
            nv = null; // không biết ai đăng nhập thì để Service báo lỗi khi lưu
        }
        boolean khoa = nv != null && !VaiTro.LE_TAN.equals(nv.getVaiTro()) && !VaiTro.QUAN_LY.equals(nv.getVaiTro());
        btnSua.setDisable(khoa);
        btnThem.setDisable(khoa);
        lblQuyen.setText(khoa ? nv.getVaiTro() + " chỉ xem, không được thêm hoặc sửa thông tin hành chính của bệnh nhân." : "");
        lblQuyen.setVisible(khoa);
        lblQuyen.setManaged(khoa);
    }

    // Tìm theo từ khóa và hiện lên bảng (từ khóa rỗng thì hiện tối đa 50 bệnh nhân đầu)
    private void tim() {
        try {
            dsBenhNhan = benhNhanService.timKiem(txtTuKhoa.getText());
            List<String[]> dong = new ArrayList<>();
            for (BenhNhan bn : dsBenhNhan) {
                dong.add(new String[] {bn.getMaBN(), bn.getHoTen(), TienIch.ngay(bn.getNgaySinh()), TienIch.chu(bn.getGioiTinh()),
                    TienIch.chu(bn.getSdt()), TienIch.chu(bn.getDiaChi())});
            }
            tblBenhNhan.setItems(FXCollections.observableArrayList(dong));
            lblKetQua.setText(dong.isEmpty() ? "Không tìm thấy bệnh nhân nào." : "Tìm thấy " + dong.size() + " bệnh nhân (tối đa 50).");
        } catch (RuntimeException e) {
            TienIch.baoLoi(e);
        }
    }

    // Bệnh nhân đang chọn trong bảng; chưa chọn thì nhắc và trả về null
    private BenhNhan dangChon() {
        int vt = tblBenhNhan.getSelectionModel().getSelectedIndex();
        if (vt < 0 || vt >= dsBenhNhan.size()) {
            TienIch.thongTin("Vui lòng chọn một bệnh nhân trong bảng.");
            return null;
        }
        return dsBenhNhan.get(vt);
    }

    // Mở hồ sơ bệnh án của bệnh nhân đang chọn
    private void xemHoSo() {
        BenhNhan bn = dangChon();
        if (bn != null) {
            MainApp.doiNoiDung(new ManHinhHoSoBenhAn(bn));
        }
    }
}
