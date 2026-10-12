package nhakhoakim.ui;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.StringConverter;
import nhakhoakim.MainApp;
import nhakhoakim.dao.BenhNhanService;
import nhakhoakim.dao.NghiepVuException;
import nhakhoakim.entity.BenhNhan;
import nhakhoakim.entity.DichVu;

// Các hàm dùng chung cho các màn hình: định dạng ngày giờ, tạo nhãn, nút, cột bảng, hộp thông báo
public class TienIch {

    public static final String MAU_CHINH = "#0E7C86";
    public static final String MAU_DAM = "#0B4F6C";

    // Kiểu thẻ trắng bo góc
    public static final String THE = "-fx-background-color: white; -fx-background-radius: 10;"
            + " -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.12), 10, 0, 0, 2);";

    private static final DateTimeFormatter NGAY = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter GIO = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter NGAY_GIO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // Ngày dạng dd/MM/yyyy; null thì trả về chuỗi rỗng
    public static String ngay(LocalDate d) {
        return d == null ? "" : d.format(NGAY);
    }

    // Giờ dạng HH:mm
    public static String gio(LocalTime t) {
        return t == null ? "" : t.format(GIO);
    }

    // Ngày giờ dạng dd/MM/yyyy HH:mm
    public static String ngayGio(LocalDateTime t) {
        return t == null ? "" : t.format(NGAY_GIO);
    }

    // Chuỗi null thì đổi thành rỗng để hiện lên màn hình
    public static String chu(String s) {
        return s == null ? "" : s;
    }

    // Số tiền có dấu chấm ngăn cách hàng nghìn, ví dụ 1.500.000 (dùng Locale cố định nên không phụ thuộc cài đặt máy)
    public static String tien(long soTien) {
        return String.format(Locale.GERMANY, "%,d", soTien);
    }

    // Bộ chuyển đổi để ComboBox dịch vụ hiện tên dịch vụ; null thì hiện rỗng
    public static StringConverter<DichVu> chuyenDichVu() {
        return new StringConverter<DichVu>() {
            @Override
            public String toString(DichVu dv) {
                return dv == null ? "" : dv.getTenDichVu();
            }

            @Override
            public DichVu fromString(String s) {
                return null;
            }
        };
    }

    // Bộ chuyển đổi để DatePicker hiện và nhập ngày dạng dd/MM/yyyy; nhập sai thì coi như chưa chọn ngày
    public static StringConverter<LocalDate> chuyenNgay() {
        return new StringConverter<LocalDate>() {
            @Override
            public String toString(LocalDate d) {
                return ngay(d);
            }

            @Override
            public LocalDate fromString(String s) {
                if (s == null || s.trim().isEmpty()) {
                    return null;
                }
                try {
                    return LocalDate.parse(s.trim(), NGAY);
                } catch (DateTimeParseException e) {
                    return null;
                }
            }
        };
    }

    // Tiêu đề lớn của màn hình
    public static Label tieuDe(String chu) {
        Label lbl = new Label(chu);
        lbl.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");
        return lbl;
    }

    // Nhãn nhỏ màu xám cho tên ô nhập
    public static Label nhan(String chu) {
        Label lbl = new Label(chu);
        lbl.setStyle("-fx-font-weight: bold; -fx-text-fill: #6B7C8A;");
        return lbl;
    }

    // Nút chính màu xanh
    public static Button nutChinh(String chu) {
        Button btn = new Button(chu);
        btn.setStyle("-fx-background-color: " + MAU_CHINH + "; -fx-text-fill: white; -fx-font-weight: bold;"
                + " -fx-background-radius: 6; -fx-padding: 7 16 7 16; -fx-cursor: hand;");
        return btn;
    }

    // Nút phụ màu trắng viền xanh
    public static Button nutPhu(String chu) {
        Button btn = new Button(chu);
        btn.setStyle("-fx-background-color: white; -fx-text-fill: " + MAU_CHINH + "; -fx-border-color: " + MAU_CHINH
                + "; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 6 14 6 14; -fx-cursor: hand;");
        return btn;
    }

    // Cột bảng lấy giá trị từ mảng chuỗi (mỗi dòng của bảng là một String[]): tên cột, độ rộng, vị trí trong mảng
    public static TableColumn<String[], String> cot(String ten, double rong, int chiSo) {
        TableColumn<String[], String> c = new TableColumn<>(ten);
        c.setCellValueFactory(d -> new SimpleStringProperty(d.getValue()[chiSo]));
        c.setPrefWidth(rong);
        c.setSortable(false); // không cho đảo thứ tự dòng, để vị trí dòng chọn luôn khớp với danh sách dữ liệu
        return c;
    }

    // Như cột thường nhưng chữ dài thì xuống dòng thay vì bị cắt bằng dấu ba chấm
    public static TableColumn<String[], String> cotXuongDong(String ten, double rong, int chiSo) {
        TableColumn<String[], String> c = cot(ten, rong, chiSo);
        c.setCellFactory(tc -> {
            TableCell<String[], String> o = new TableCell<>();
            Text chu = new Text();
            chu.wrappingWidthProperty().bind(tc.widthProperty().subtract(12));
            chu.textProperty().bind(o.itemProperty());
            o.setGraphic(chu);
            o.setPrefHeight(Region.USE_COMPUTED_SIZE);
            return o;
        });
        return c;
    }

    // Bảng dữ liệu mỗi dòng là một String[]; khi chưa có dòng nào thì hiện câu tiếng Việt thay cho câu mặc định bằng tiếng Anh
    public static TableView<String[]> taoBang(String khiTrong) {
        TableView<String[]> bang = new TableView<>();
        bang.setPlaceholder(new Label(khiTrong));
        return bang;
    }

    // Ô chọn bệnh nhân (hiện "mã - họ tên"): gõ mã, tên hoặc SĐT rồi nhấn Enter để tìm lại danh sách;
    // chưa chọn ai thì getValue() trả về null
    public static ComboBox<BenhNhan> taoChonBenhNhan() {
        ComboBox<BenhNhan> cmb = new ComboBox<>();
        cmb.setEditable(true);
        cmb.getItems().addAll(new BenhNhanService().timKiem(""));
        cmb.setPromptText("Gõ mã, tên hoặc SĐT rồi nhấn Enter để tìm");
        cmb.setPrefWidth(360);
        cmb.setConverter(new StringConverter<BenhNhan>() {
            @Override
            public String toString(BenhNhan bn) {
                return bn == null ? "" : bn.getMaBN() + " - " + bn.getHoTen();
            }

            // Chỉ trả về người có trong danh sách đang hiện (khớp đúng chữ "mã - họ tên"), còn lại là null;
            // cần vậy vì ComboBox gọi hàm này khi ô nhập mất tiêu điểm, trả null sẽ làm mất người vừa chọn
            @Override
            public BenhNhan fromString(String s) {
                if (s == null) {
                    return null;
                }
                for (BenhNhan bn : cmb.getItems()) {
                    if (toString(bn).equals(s.trim())) {
                        return bn;
                    }
                }
                return null;
            }
        });
        cmb.getEditor().addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            if (e.getCode() != KeyCode.ENTER) {
                return;
            }
            String tuKhoa = cmb.getEditor().getText() == null ? "" : cmb.getEditor().getText().trim();
            BenhNhan dangChon = cmb.getValue();
            if (dangChon != null && tuKhoa.equals(cmb.getConverter().toString(dangChon))) {
                return; // đang là chữ của người đã chọn, không tìm lại
            }
            e.consume();
            Platform.runLater(() -> timBenhNhanChoCombo(cmb, tuKhoa));
        });
        return cmb;
    }

    // Tìm bệnh nhân theo từ khóa, thay danh sách của ô chọn bằng kết quả và mở danh sách ra; không có ai thì báo
    private static void timBenhNhanChoCombo(ComboBox<BenhNhan> cmb, String tuKhoa) {
        try {
            List<BenhNhan> ketQua = new BenhNhanService().timKiem(tuKhoa);
            cmb.setValue(null);
            cmb.getItems().setAll(ketQua);
            cmb.getEditor().setText(tuKhoa);
            cmb.getEditor().positionCaret(tuKhoa.length());
            if (ketQua.isEmpty()) {
                thongTin("Không tìm thấy bệnh nhân nào khớp với \"" + tuKhoa + "\".");
            } else {
                cmb.show();
            }
        } catch (RuntimeException ex) {
            baoLoi(ex);
        }
    }

    // Chọn trong ô chọn bệnh nhân người có cùng mã; chưa có trong danh sách thì thêm vào đầu rồi chọn; bn null thì bỏ chọn
    public static void chonTrongCombo(ComboBox<BenhNhan> cmb, BenhNhan bn) {
        if (bn == null) {
            cmb.setValue(null);
            return;
        }
        for (BenhNhan x : cmb.getItems()) {
            if (x.getMaBN().equals(bn.getMaBN())) {
                cmb.setValue(x);
                return;
            }
        }
        cmb.getItems().add(0, bn);
        cmb.setValue(bn);
    }

    // Báo lỗi bắt được từ service: lỗi nghiệp vụ hiện nguyên câu, lỗi hệ thống thì ghi Console và hiện câu chung
    public static void baoLoi(RuntimeException e) {
        if (e instanceof NghiepVuException) {
            loi(e.getMessage());
        } else {
            e.printStackTrace();
            loi("Có lỗi hệ thống, vui lòng thử lại. (" + e.getMessage() + ")");
        }
    }

    // Hộp thông báo lỗi
    public static void loi(String noiDung) {
        Alert a = new Alert(Alert.AlertType.ERROR, noiDung, ButtonType.OK);
        a.setTitle("Có lỗi");
        a.setHeaderText(null);
        a.showAndWait();
    }

    // Hộp thông báo thông tin
    public static void thongTin(String noiDung) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, noiDung, ButtonType.OK);
        a.setTitle("Thông báo");
        a.setHeaderText(null);
        a.showAndWait();
    }

    // Hộp xác nhận Có / Không; trả về true nếu chọn Có
    public static boolean xacNhan(String noiDung) {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION, noiDung, ButtonType.YES, ButtonType.NO);
        a.setTitle("Xác nhận");
        a.setHeaderText(null);
        return a.showAndWait().orElse(ButtonType.NO) == ButtonType.YES;
    }
}
