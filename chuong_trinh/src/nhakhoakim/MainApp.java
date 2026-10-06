package nhakhoakim;

import java.util.List;
import java.util.Map;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

// Cửa sổ chính: thanh menu theo nhóm đối tượng, mỗi menu là một đối tượng, mỗi mục là một chức năng
public class MainApp extends Application {

    // Màu chủ đạo của chương trình
    private static final String MAU_CHINH = "#0E7C86";
    private static final String MAU_DAM = "#0B4F6C";

    // Kiểu của thẻ trắng bo góc
    private static final String KIEU_THE = "-fx-background-color: white; -fx-background-radius: 10;"
            + " -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.12), 10, 0, 0, 2);";

    private BorderPane khungChinh = new BorderPane(); // tiêu đề + menu ở trên, nội dung ở giữa

    @Override
    public void start(Stage stage) {
        stage.setTitle("Quản lý phòng khám Nha khoa Kim");
        stage.setScene(taoScene());
        stage.show();
    }

    // Dựng giao diện: tiêu đề, menu và trang chủ
    Scene taoScene() {
        khungChinh.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 14px;"
                + " -fx-background-color: #F3F6F8; -fx-accent: " + MAU_CHINH + ";");
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
        tieuDe.setStyle("-fx-background-color: linear-gradient(to right, " + MAU_DAM + ", " + MAU_CHINH + ");");
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
        Label lblTieuDe = new Label("Chọn chức năng cần dùng");
        lblTieuDe.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

        FlowPane luoi = new FlowPane(16, 16);
        for (Map.Entry<String, List<ChucNang>> nhom : DanhSachChucNang.layTheoNhom().entrySet()) {
            Label lblNhom = new Label(nhom.getKey());
            lblNhom.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + MAU_DAM + ";");

            VBox the = new VBox(4, lblNhom);
            for (ChucNang cn : nhom.getValue()) {
                Hyperlink lnkChucNang = new Hyperlink(cn.getTen());
                lnkChucNang.setOnAction(e -> hienChucNang(cn));
                the.getChildren().add(lnkChucNang);
            }
            the.setPadding(new Insets(16));
            the.setPrefWidth(270);
            the.setStyle(KIEU_THE);
            luoi.getChildren().add(the);
        }

        VBox trang = new VBox(16, lblTieuDe, luoi);
        trang.setPadding(new Insets(24));
        khungChinh.setCenter(trang);
    }

    // Màn hình của từng chức năng. Ai làm xong màn hình nào thì trả về ở đây
    // (so sánh theo cn.getTen()); chưa làm thì trả về null và vùng nội dung để trống.
    private Node taoManHinh(ChucNang cn) {
        return null;
    }

    // Hiện chức năng được chọn: có màn hình thật thì hiện, chưa làm thì để trống
    void hienChucNang(ChucNang cn) {
        Node manHinh = taoManHinh(cn);
        if (manHinh == null) {
            manHinh = new Pane();
        }
        khungChinh.setCenter(manHinh);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
