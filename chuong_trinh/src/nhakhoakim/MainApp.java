package nhakhoakim;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import nhakhoakim.ui.LamMoiDuoc;
import nhakhoakim.ui.ManHinhHoSoBenhAn;
import nhakhoakim.ui.ManHinhHoSoBenhNhan;
import nhakhoakim.ui.ManHinhTraCuuBenhNhan;
import nhakhoakim.ui.TienIch;

// Cửa sổ chính: thanh menu theo nhóm đối tượng, mỗi menu là một đối tượng, mỗi mục là một chức năng
public class MainApp extends Application {

    private static MainApp dangChay; // cửa sổ chính đang chạy, để các màn hình chuyển sang màn hình khác
    private static boolean dangBaoLoi = false; // đang hiện hộp báo lỗi chưa bắt được, để không hiện chồng nhiều hộp

    private BorderPane khungChinh = new BorderPane(); // tiêu đề + menu ở trên, nội dung ở giữa
    private Node manHinhHienTai;                      // màn hình đang hiện ở giữa
    private Deque<Node> lichSuManHinh = new ArrayDeque<>(); // các màn hình đã đi qua, để nút Quay lại trở về
    private Map<String, Supplier<Node>> manHinh = new HashMap<>(); // tên chức năng -> hàm tạo màn hình của chức năng đó

    @Override
    public void start(Stage stage) {
        // Lỗi chưa bắt được (ví dụ mất kết nối CSDL khi dựng màn hình) thì hiện hộp báo lỗi thay vì im lặng
        Thread.setDefaultUncaughtExceptionHandler((luong, loi) -> {
            loi.printStackTrace();
            Platform.runLater(() -> baoLoiChuaBat(loi));
        });
        stage.setTitle("Quản lý phòng khám Nha khoa Kim");
        stage.setScene(taoScene());
        stage.show();
    }

    // Hiện lỗi chưa bắt được bằng hộp thông báo; đang hiện một hộp rồi thì bỏ qua lỗi tiếp theo để không chồng hộp
    private static void baoLoiChuaBat(Throwable loi) {
        if (dangBaoLoi) {
            return;
        }
        dangBaoLoi = true;
        try {
            TienIch.baoLoi(loi instanceof RuntimeException ? (RuntimeException) loi : new RuntimeException(loi));
        } finally {
            dangBaoLoi = false;
        }
    }

    // Dựng giao diện: tiêu đề, menu và trang chủ
    Scene taoScene() {
        dangChay = this;
        dangKyManHinh();
        khungChinh.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 14px;"
                + " -fx-background-color: #F3F6F8; -fx-accent: " + TienIch.MAU_CHINH + ";");
        khungChinh.setTop(new VBox(taoTieuDe(), taoMenu()));
        hienTrangChu();
        return new Scene(khungChinh, 1200, 720);
    }

    // Thanh tiêu đề màu xanh phía trên cùng
    private VBox taoTieuDe() {
        Label lblTen = new Label("NHA KHOA KIM");
        lblTen.setStyle("-fx-text-fill: white; -fx-font-size: 22px; -fx-font-weight: bold;");
        Label lblMoTa = new Label("Chương trình quản lý hệ thống phòng khám");
        lblMoTa.setStyle("-fx-text-fill: #CFE9EC;");

        VBox tieuDe = new VBox(2, lblTen, lblMoTa);
        tieuDe.setPadding(new Insets(14, 24, 14, 24));
        tieuDe.setStyle("-fx-background-color: linear-gradient(to right, " + TienIch.MAU_DAM + ", " + TienIch.MAU_CHINH + ");");
        tieuDe.setCursor(Cursor.HAND);
        tieuDe.setOnMouseClicked(e -> hienTrangChu()); // bấm vào tiêu đề để về trang chủ
        return tieuDe;
    }

    // Thanh menu: mỗi nhóm đối tượng là một menu, mỗi chức năng là một mục
    private MenuBar taoMenu() {
        MenuBar menuBar = new MenuBar();
        menuBar.setStyle("-fx-background-color: white;");
        for (Map.Entry<String, List<ChucNang>> nhom : DanhSachChucNang.layTheoNhom().entrySet()) {
            Menu menu = new Menu(nhom.getKey());
            for (ChucNang cn : nhom.getValue()) {
                MenuItem muc = new MenuItem(cn.getTen());
                muc.setOnAction(e -> hienChucNang(cn));
                menu.getItems().add(muc);
            }
            menuBar.getMenus().add(menu);
        }
        return menuBar;
    }

    // Trang chủ: mỗi đối tượng là một thẻ, trong thẻ liệt kê các chức năng
    void hienTrangChu() {
        lichSuManHinh.clear();
        Label lblTieuDe = new Label("Chọn chức năng cần dùng");
        lblTieuDe.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

        FlowPane luoi = new FlowPane(16, 16);
        for (Map.Entry<String, List<ChucNang>> nhom : DanhSachChucNang.layTheoNhom().entrySet()) {
            Label lblNhom = new Label(nhom.getKey());
            lblNhom.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + TienIch.MAU_DAM + ";");

            VBox the = new VBox(4, lblNhom);
            for (ChucNang cn : nhom.getValue()) {
                Hyperlink lnkChucNang = new Hyperlink(cn.getTen());
                lnkChucNang.setOnAction(e -> hienChucNang(cn));
                the.getChildren().add(lnkChucNang);
            }
            the.setPadding(new Insets(16));
            the.setPrefWidth(270);
            the.setStyle(TienIch.THE);
            luoi.getChildren().add(the);
        }

        VBox trang = new VBox(16, lblTieuDe, luoi);
        trang.setPadding(new Insets(24));
        manHinhHienTai = trang;
        khungChinh.setCenter(trang);
    }

    // Đăng ký màn hình cho từng chức năng: mỗi dòng là manHinh.put("Tên chức năng", ManHinhX::new).
    // Chức năng chưa đăng ký thì vùng nội dung để trống.
    private void dangKyManHinh() {
        // ----- Hậu: menu Bệnh nhân (tìm kiếm, hồ sơ); các màn hình khám sẽ thêm ở đợt sau
        manHinh.put("Tìm kiếm bệnh nhân", ManHinhTraCuuBenhNhan::new);
        manHinh.put("Thêm / cập nhật hồ sơ bệnh nhân", () -> new ManHinhHoSoBenhNhan(null));
        manHinh.put("Xem hồ sơ bệnh án", ManHinhHoSoBenhAn::new);            // ba chức năng dùng chung một màn hình
        manHinh.put("Cập nhật tiền sử & dị ứng", ManHinhHoSoBenhAn::new);
        manHinh.put("Xem liệu trình đang thực hiện", ManHinhHoSoBenhAn::new);

        // ----- Trường: thêm các màn hình menu Lịch hẹn và menu Hóa đơn ở đây

        // ----- Tâm: thêm các màn hình menu Dịch vụ, Nhân viên, Tài khoản ở đây
    }

    // Dựng màn hình của chức năng; chưa đăng ký màn hình thì trả về null
    private Node taoManHinh(ChucNang cn) {
        Supplier<Node> tao = manHinh.get(cn.getTen());
        return tao == null ? null : tao.get();
    }

    // Hiện chức năng được chọn: có màn hình thật thì hiện, chưa làm thì để trống
    void hienChucNang(ChucNang cn) {
        Node noiDung = null;
        try {
            noiDung = taoManHinh(cn);
        } catch (RuntimeException e) {
            TienIch.baoLoi(e); // dựng màn hình lỗi (ví dụ mất kết nối CSDL) thì báo và để vùng giữa trống
        }
        if (noiDung == null) {
            noiDung = new Pane();
        }
        lichSuManHinh.clear(); // chọn từ menu là bắt đầu một chuỗi màn hình mới
        datGiua(noiDung);
    }

    // Đặt một màn hình vào giữa cửa sổ, có thanh cuộn khi màn hình dài hơn cửa sổ;
    // nếu đây là màn hình đi tiếp từ một màn hình khác thì có thêm nút Quay lại ở trên cùng
    private void datGiua(Node noiDung) {
        manHinhHienTai = noiDung;
        VBox khung = new VBox(noiDung);
        if (!lichSuManHinh.isEmpty()) {
            Button btnQuayLai = new Button("‹ Quay lại");
            btnQuayLai.setStyle("-fx-background-color: white; -fx-text-fill: " + TienIch.MAU_CHINH + "; -fx-border-color: " + TienIch.MAU_CHINH
                    + "; -fx-border-radius: 6; -fx-background-radius: 6; -fx-cursor: hand;");
            btnQuayLai.setOnAction(e -> quayLai());
            VBox.setMargin(btnQuayLai, new Insets(14, 0, 0, 24));
            khung.getChildren().add(0, btnQuayLai);
        }
        ScrollPane cuon = new ScrollPane(khung);
        cuon.setFitToWidth(true);
        cuon.setStyle("-fx-background: #F3F6F8; -fx-background-color: #F3F6F8;");
        khungChinh.setCenter(cuon);
    }

    // Quay về màn hình trước đó (nếu màn hình đó có hàm lamMoi thì nạp lại dữ liệu cho đúng)
    private void quayLai() {
        if (lichSuManHinh.isEmpty()) {
            return;
        }
        Node truoc = lichSuManHinh.pop();
        if (truoc instanceof LamMoiDuoc) {
            ((LamMoiDuoc) truoc).lamMoi();
        }
        datGiua(truoc);
    }

    // Đi tiếp sang một màn hình khác và nhớ màn hình hiện tại để nút Quay lại trở về
    public static void doiNoiDung(Node noiDung) {
        if (dangChay.manHinhHienTai != null) {
            dangChay.lichSuManHinh.push(dangChay.manHinhHienTai);
        }
        dangChay.datGiua(noiDung);
    }

    // Thay màn hình hiện tại bằng màn hình khác, không nhớ màn hình cũ (dùng khi màn hình cũ đã xong việc, ví dụ vừa thêm xong bệnh nhân)
    public static void thayNoiDung(Node noiDung) {
        dangChay.datGiua(noiDung);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
