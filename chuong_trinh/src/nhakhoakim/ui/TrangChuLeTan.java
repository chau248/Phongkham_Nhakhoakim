package nhakhoakim.ui;

import java.io.IOException;
import java.net.URL;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;

// Vỏ màn hình tải bố cục FXML và chuyển yêu cầu làm mới sang controller.
public class TrangChuLeTan extends BorderPane implements LamMoiDuoc {

    private final LichHenController controller;

    public TrangChuLeTan() {
        URL fxml = TrangChuLeTan.class.getResource("/nhakhoakim/ui/resource/UI_TrangChuLeTan.fxml");
        if (fxml == null) {
            throw new IllegalStateException("Không tìm thấy giao diện UI_TrangChuLeTan.fxml");
        }
        FXMLLoader loader = new FXMLLoader(fxml);
        try {
            Parent giaoDien = loader.load();
            controller = loader.getController();
            setCenter(giaoDien);
        } catch (IOException e) {
            throw new IllegalStateException("Không tải được giao diện lịch hẹn", e);
        }
    }

    @Override
    public void lamMoi() {
        controller.lamMoi();
    }
}
