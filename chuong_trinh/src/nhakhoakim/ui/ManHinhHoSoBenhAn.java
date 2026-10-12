package nhakhoakim.ui;

import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import nhakhoakim.MainApp;
import nhakhoakim.dao.HoSoBenhAnService;
import nhakhoakim.dao.PhienLamViec;
import nhakhoakim.entity.BenhNhan;
import nhakhoakim.entity.HoSoBenhAn;
import nhakhoakim.entity.LieuTrinh;
import nhakhoakim.entity.LuotKham;
import nhakhoakim.entity.NhanVien;
import nhakhoakim.entity.TrangThai;
import nhakhoakim.entity.VaiTro;

// Màn hình hồ sơ bệnh án: cảnh báo dị ứng, tiền sử bệnh (sửa được), liệu trình đang thực hiện và lịch sử khám
public class ManHinhHoSoBenhAn extends VBox {

    private HoSoBenhAnService hoSoService = new HoSoBenhAnService();

    private ComboBox<BenhNhan> cmbBenhNhan = TienIch.taoChonBenhNhan(); // chọn bệnh nhân cần xem
    private Label lblCanhBao = new Label();                              // cảnh báo dị ứng nổi bật
    private Label lblThongTin = new Label();                             // thông tin hành chính
    private TextArea txaTienSu = new TextArea();                         // tiền sử bệnh
    private TextField txtDiUng = new TextField();                        // dị ứng, cách nhau dấu phẩy
    private ComboBox<String> cmbNhomMau = new ComboBox<>();              // nhóm máu
    private TextArea txaBenhNen = new TextArea();                        // bệnh nền
    private TextField txtThuoc = new TextField();                        // thuốc đang dùng
    private Button btnLuu = TienIch.nutChinh("Lưu thay đổi");
    private Label lblQuyen = new Label();                                // giải thích vì sao không sửa được
    private TableView<String[]> tblLieuTrinh = TienIch.taoBang("Bệnh nhân chưa có liệu trình.");        // liệu trình của bệnh nhân
    private TableView<String[]> tblLichSu = TienIch.taoBang("Bệnh nhân chưa có lượt khám nào.");           // lịch sử khám
    private VBox khuNoiDung = new VBox(14);                              // phần hiện sau khi chọn bệnh nhân
    private HoSoBenhAn hoSo;                                             // hồ sơ đang xem

    public ManHinhHoSoBenhAn() {
        this(null);
    }

    // Mở sẵn hồ sơ của một bệnh nhân (null thì để người dùng tự chọn)
    public ManHinhHoSoBenhAn(BenhNhan benhNhan) {
        setSpacing(14);
        setPadding(new Insets(24));

        Button btnXem = TienIch.nutChinh("Xem hồ sơ");
        btnXem.setOnAction(e -> xemHoSo());
        HBox hangChon = new HBox(10, TienIch.nhan("Bệnh nhân"), cmbBenhNhan, btnXem);

        lblCanhBao.setStyle("-fx-background-color: #FDECEA; -fx-text-fill: #B71C1C; -fx-font-weight: bold; -fx-padding: 10 14 10 14;");
        lblCanhBao.setWrapText(true);
        lblCanhBao.setMaxWidth(Double.MAX_VALUE);

        cmbNhomMau.getItems().addAll("", "A", "B", "AB", "O");
        txaTienSu.setPrefRowCount(3);
        txaBenhNen.setPrefRowCount(2);
        txtDiUng.setPromptText("Ví dụ: Penicillin, Lidocaine");

        GridPane luoi = new GridPane();
        luoi.setHgap(14);
        luoi.setVgap(10);
        luoi.add(TienIch.nhan("Tiền sử bệnh"), 0, 0);
        luoi.add(txaTienSu, 1, 0);
        luoi.add(TienIch.nhan("Dị ứng"), 0, 1);
        luoi.add(txtDiUng, 1, 1);
        luoi.add(TienIch.nhan("Nhóm máu"), 0, 2);
        luoi.add(cmbNhomMau, 1, 2);
        luoi.add(TienIch.nhan("Bệnh nền"), 0, 3);
        luoi.add(txaBenhNen, 1, 3);
        luoi.add(TienIch.nhan("Thuốc đang dùng"), 0, 4);
        luoi.add(txtThuoc, 1, 4);
        txaTienSu.setPrefWidth(520);
        txtDiUng.setPrefWidth(520);
        txaBenhNen.setPrefWidth(520);
        txtThuoc.setPrefWidth(520);
        btnLuu.setOnAction(e -> luuThongTin());
        lblQuyen.setStyle("-fx-text-fill: #6B7C8A;");
        lblQuyen.setVisible(false);
        lblQuyen.setManaged(false);

        // Đổi bệnh nhân ở ô chọn thì ẩn hồ sơ đang hiện và bỏ hồ sơ cũ, để không lưu nhầm vào bệnh nhân khác
        cmbBenhNhan.valueProperty().addListener((obs, cu, moi) -> {
            hoSo = null;
            khuNoiDung.setVisible(false);
            khuNoiDung.setManaged(false);
        });

        VBox theYTe = new VBox(12, lblThongTin, luoi, new HBox(12, btnLuu, lblQuyen));
        theYTe.setPadding(new Insets(16));
        theYTe.setStyle(TienIch.THE);

        tblLieuTrinh.getColumns().add(TienIch.cot("Loại", 110, 0));
        tblLieuTrinh.getColumns().add(TienIch.cot("Số buổi", 70, 1));
        tblLieuTrinh.getColumns().add(TienIch.cot("Giai đoạn hiện tại", 240, 2));
        tblLieuTrinh.getColumns().add(TienIch.cot("Trạng thái", 120, 3));
        tblLieuTrinh.getColumns().add(TienIch.cot("Dự kiến kết thúc", 120, 4));
        tblLieuTrinh.setPrefHeight(110);
        tblLichSu.getColumns().add(TienIch.cot("Ngày khám", 130, 0));
        tblLichSu.getColumns().add(TienIch.cot("Lý do", 200, 1));
        tblLichSu.getColumns().add(TienIch.cot("Chẩn đoán", 280, 2));
        tblLichSu.getColumns().add(TienIch.cot("Trạng thái", 100, 3));
        tblLichSu.setPrefHeight(150);

        khuNoiDung.getChildren().addAll(lblCanhBao, theYTe, new Label("Liệu trình"), tblLieuTrinh,
                new Label("Lịch sử khám (mới nhất trước)"), tblLichSu);
        khuNoiDung.setVisible(false);
        khuNoiDung.setManaged(false);

        getChildren().addAll(TienIch.tieuDe("Hồ sơ bệnh án"), hangChon, khuNoiDung);

        if (benhNhan != null) {
            TienIch.chonTrongCombo(cmbBenhNhan, benhNhan); // chưa có trong danh sách thì thêm vào đầu rồi chọn
            xemHoSo();
        }
    }

    // Nạp hồ sơ của bệnh nhân đang chọn lên màn hình
    private void xemHoSo() {
        BenhNhan bn = cmbBenhNhan.getValue();
        if (bn == null) {
            TienIch.thongTin("Vui lòng chọn một bệnh nhân.");
            return;
        }
        try {
            hoSo = hoSoService.timTheoBenhNhan(bn.getMaBN());
            hienHoSo(bn);
        } catch (RuntimeException e) {
            TienIch.baoLoi(e);
        }
    }

    // Đổ dữ liệu hồ sơ vào các ô; nếu có dị ứng thì hiện khung cảnh báo đỏ ở đầu
    private void hienHoSo(BenhNhan bn) {
        String diUng = TienIch.chu(hoSo.getCanhBaoDiUng()).trim();
        lblCanhBao.setText("CẢNH BÁO DỊ ỨNG: " + diUng);
        lblCanhBao.setVisible(!diUng.isEmpty());
        lblCanhBao.setManaged(!diUng.isEmpty());

        lblThongTin.setText(bn.getMaBN() + " - " + bn.getHoTen() + "   |   Mã hồ sơ: " + hoSo.getMaHoSo()
                + "   |   Ngày tạo: " + TienIch.ngay(hoSo.getNgayTao()) + "   |   " + hoSo.getTrangThaiHoSo());
        lblThongTin.setStyle("-fx-font-weight: bold;");
        txaTienSu.setText(TienIch.chu(hoSo.getTienSuBenh()));
        txtDiUng.setText(TienIch.chu(hoSo.getCanhBaoDiUng()));
        cmbNhomMau.setValue(TienIch.chu(hoSo.getNhomMau()));
        txaBenhNen.setText(TienIch.chu(hoSo.getBenhNen()));
        txtThuoc.setText(TienIch.chu(hoSo.getThuocDangSuDung()));
        apDungQuyenSua();

        List<String[]> dongLieuTrinh = new ArrayList<>();
        for (LieuTrinh lt : hoSoService.layLieuTrinh(bn.getMaBN())) {
            dongLieuTrinh.add(new String[] {lt.getLoaiDichVu(), String.valueOf(lt.getSoBuoiDuKien()),
                TienIch.chu(lt.getGiaiDoanHienTai()), lt.getTrangThai(), TienIch.ngay(lt.getNgayDuKienKetThuc())});
        }
        tblLieuTrinh.setItems(FXCollections.observableArrayList(dongLieuTrinh));

        List<String[]> dongLichSu = new ArrayList<>();
        for (LuotKham lk : hoSoService.layLichSuKham(hoSo.getMaHoSo())) {
            dongLichSu.add(new String[] {TienIch.ngayGio(lk.getGioBatDau()), TienIch.chu(lk.getLyDoKham()),
                TienIch.chu(lk.getChanDoan()), lk.getTrangThai()});
        }
        tblLichSu.setItems(FXCollections.observableArrayList(dongLichSu));

        khuNoiDung.setVisible(true);
        khuNoiDung.setManaged(true);
    }

    // Chỉ Nha sĩ và Y tá được sửa thông tin y tế (BN-08); hồ sơ đã đóng chỉ xem (BN-07). Vai trò khác thì khóa nút Lưu và nói rõ lý do.
    private void apDungQuyenSua() {
        String lyDo = null;
        NhanVien nv = null;
        try {
            nv = PhienLamViec.getNhanVienHienTai();
        } catch (RuntimeException e) {
            nv = null; // không biết ai đăng nhập thì để Service báo lỗi khi lưu
        }
        if (nv != null && !VaiTro.NHA_SI.equals(nv.getVaiTro()) && !VaiTro.Y_TA.equals(nv.getVaiTro())) {
            lyDo = nv.getVaiTro() + " chỉ xem phần y tế";
        } else if (TrangThai.HO_SO_DA_DONG.equals(hoSo.getTrangThaiHoSo())) {
            lyDo = "Hồ sơ đã đóng, chỉ xem";
        }
        btnLuu.setDisable(lyDo != null);
        lblQuyen.setText(lyDo == null ? "" : lyDo);
        lblQuyen.setVisible(lyDo != null);
        lblQuyen.setManaged(lyDo != null);
    }

    // Lưu tiền sử, dị ứng và thông tin y tế: gán vào bản sao, Service kiểm tra và chuẩn hóa (dị ứng, ô trống)
    private void luuThongTin() {
        if (hoSo == null) {
            TienIch.thongTin("Vui lòng chọn bệnh nhân rồi bấm Xem hồ sơ trước khi lưu.");
            return;
        }
        try {
            HoSoBenhAn moi = new HoSoBenhAn();
            moi.setMaHoSo(hoSo.getMaHoSo());
            moi.setMaBN(hoSo.getMaBN());
            moi.setNgayTao(hoSo.getNgayTao());
            moi.setTrangThaiHoSo(hoSo.getTrangThaiHoSo());
            moi.setGhiChuDacBiet(hoSo.getGhiChuDacBiet());
            moi.setTienSuBenh(txaTienSu.getText());
            moi.setCanhBaoDiUng(txtDiUng.getText());
            moi.setNhomMau(cmbNhomMau.getValue() == null ? "" : cmbNhomMau.getValue());
            moi.setBenhNen(txaBenhNen.getText());
            moi.setThuocDangSuDung(txtThuoc.getText());
            hoSoService.capNhatThongTinYTe(moi);
            xemHoSo();
            TienIch.thongTin("Đã lưu thông tin y tế.");
        } catch (RuntimeException e) {
            TienIch.baoLoi(e);
        }
    }
}
