package nhakhoakim.ui;

import java.io.IOException;
import java.net.URL;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.VBox;
import nhakhoakim.dao.BenhNhanService;
import nhakhoakim.dao.LichHenService;
import nhakhoakim.dao.NghiepVuException;
import nhakhoakim.entity.BenhNhan;
import nhakhoakim.entity.DichVu;
import nhakhoakim.entity.LichHen;
import nhakhoakim.entity.NhanVien;

// Điều khiển lịch hẹn; bố cục tĩnh nằm trong UI_TrangChuLeTan.fxml.
public class LichHenController {

    private static final DateTimeFormatter DINH_DANG_GIO = DateTimeFormatter.ofPattern("HH:mm");
    private static final String[] TEN_THU = {"Thứ Hai", "Thứ Ba", "Thứ Tư", "Thứ Năm", "Thứ Sáu", "Thứ Bảy", "Chủ Nhật"};

    private final LichHenService lichHenService = new LichHenService();
    private final BenhNhanService benhNhanService = new BenhNhanService();
    private final List<LichHen> lichHenTrongTuan = new ArrayList<>();
    private LocalDate ngayDauTuan = LocalDate.now().with(DayOfWeek.MONDAY);
    private LichHen lichDangChon;

    @FXML private Label lblNgay0;
    @FXML private Label lblNgay1;
    @FXML private Label lblNgay2;
    @FXML private Label lblNgay3;
    @FXML private Label lblNgay4;
    @FXML private Label lblNgay5;
    @FXML private Label lblNgay6;
    @FXML private VBox slot00;
    @FXML private VBox slot01;
    @FXML private VBox slot02;
    @FXML private VBox slot03;
    @FXML private VBox slot04;
    @FXML private VBox slot05;
    @FXML private VBox slot06;
    @FXML private VBox slot10;
    @FXML private VBox slot11;
    @FXML private VBox slot12;
    @FXML private VBox slot13;
    @FXML private VBox slot14;
    @FXML private VBox slot15;
    @FXML private VBox slot16;
    @FXML private VBox slot20;
    @FXML private VBox slot21;
    @FXML private VBox slot22;
    @FXML private VBox slot23;
    @FXML private VBox slot24;
    @FXML private VBox slot25;
    @FXML private VBox slot26;
    @FXML private Label lblTuan;
    @FXML private Label lblChiTiet;
    @FXML private DatePicker dpNgayHen;
    @FXML private TextField txtGioHen;
    @FXML private TextArea txtGhiChu;
    @FXML private Label lblBenhNhan;
    @FXML private Label lblDichVu;
    @FXML private Label lblNhaSi;
    @FXML private Label lblThoiLuong;
    @FXML private Label lblPhong;
    @FXML private Label lblThongTinThem;
    @FXML private Button btnCapNhat;
    @FXML private Button btnHuy;
    @FXML private Button btnCapNhatHoSo;

    @FXML
    private void initialize() {
        dpNgayHen.setConverter(TienIch.chuyenNgay());
        dpNgayHen.setPromptText("dd/MM/yyyy");
        txtGioHen.setPromptText("HH:mm");
        txtGhiChu.setPromptText("Ghi chú");
        txtGhiChu.setWrapText(true);
        datTrangThaiChiTiet(false);
        taiLich();
    }

    @FXML
    private void tuanTruoc() {
        doiTuan(-7);
    }

    @FXML
    private void tuanTiep() {
        doiTuan(7);
    }

    @FXML
    private void veHomNay() {
        ngayDauTuan = LocalDate.now().with(DayOfWeek.MONDAY);
        taiLich();
    }

    @FXML
    private void capNhatLich() {
        try {
            LocalTime gio = docGio(txtGioHen.getText());
            lichHenService.capNhatLichHen(dpNgayHen.getValue(), gio, txtGhiChu.getText(), lichDangChon);
            TienIch.thongTin("Cập nhật lịch hẹn thành công.");
            taiLich();
        } catch (RuntimeException e) {
            TienIch.baoLoi(e);
        }
    }

    @FXML
    private void moXacNhanHuy() {
        if (lichDangChon == null) {
            return;
        }
        try {
            URL fxml = getClass().getResource("/nhakhoakim/ui/resource/UI_XoaLichHen.fxml");
            if (fxml == null) {
                throw new IllegalStateException("Không tìm thấy giao diện UI_XoaLichHen.fxml");
            }
            FXMLLoader loader = new FXMLLoader(fxml);
            VBox form = loader.load();
            XoaLichHenController formController = loader.getController();
            formController.datLichHen(lichDangChon);

            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Xóa lịch hẹn");
            ButtonType nutHuyLich = new ButtonType("Hủy lịch", ButtonData.OK_DONE);
            ButtonType nutThoat = new ButtonType("Thoát", ButtonData.CANCEL_CLOSE);
            dialog.getDialogPane().getButtonTypes().addAll(nutHuyLich, nutThoat);
            dialog.getDialogPane().setContent(form);
            Node nutThucHien = dialog.getDialogPane().lookupButton(nutHuyLich);
            nutThucHien.disableProperty().bind(formController.lyDoProperty().length().lessThan(10));

            dialog.showAndWait().ifPresent(chon -> {
                if (chon == nutHuyLich) {
                    try {
                        lichHenService.huyLichHen(lichDangChon, formController.layLyDo());
                        TienIch.thongTin("Hủy lịch hẹn thành công.");
                        taiLich();
                    } catch (RuntimeException e) {
                        TienIch.baoLoi(e);
                    }
                }
            });
        } catch (IOException e) {
            TienIch.baoLoi(new IllegalStateException("Không tải được biểu mẫu hủy lịch hẹn", e));
        } catch (RuntimeException e) {
            TienIch.baoLoi(e);
        }
    }

    @FXML
    private void moCapNhatHoSo() {
        if (lichDangChon == null) {
            return;
        }
        try {
            BenhNhan benhNhan = benhNhanService.timTheoMa(lichDangChon.getMaBN());
            if (benhNhan == null) {
                TienIch.loi("Không tìm thấy hồ sơ bệnh nhân " + lichDangChon.getMaBN() + ".");
                return;
            }

            URL fxml = getClass().getResource("/nhakhoakim/ui/resource/UI_CapNhatHoSoBenhNhan.fxml");
            if (fxml == null) {
                throw new IllegalStateException("Không tìm thấy giao diện UI_CapNhatHoSoBenhNhan.fxml");
            }
            FXMLLoader loader = new FXMLLoader(fxml);
            VBox form = loader.load();
            CapNhatHoSoBenhNhanController formController = loader.getController();
            formController.datBenhNhan(benhNhan);

            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Cập nhật hồ sơ bệnh nhân");
            dialog.setHeaderText("Thông tin cá nhân");
            ButtonType nutLuu = new ButtonType("Lưu thay đổi", ButtonData.OK_DONE);
            ButtonType nutHuy = new ButtonType("Hủy", ButtonData.CANCEL_CLOSE);
            dialog.getDialogPane().getButtonTypes().addAll(nutLuu, nutHuy);
            dialog.getDialogPane().setContent(form);
            dialog.getDialogPane().setPrefWidth(620);
            Node nutLuuTrongDialog = dialog.getDialogPane().lookupButton(nutLuu);
            nutLuuTrongDialog.addEventFilter(ActionEvent.ACTION, e -> {
                if (!formController.capNhat()) {
                    e.consume();
                }
            });

            dialog.showAndWait().ifPresent(chon -> {
                if (chon == nutLuu && formController.daCapNhat()) {
                    taiLich();
                }
            });
        } catch (IOException e) {
            TienIch.baoLoi(new IllegalStateException("Không tải được biểu mẫu cập nhật hồ sơ bệnh nhân", e));
        } catch (RuntimeException e) {
            TienIch.baoLoi(e);
        }
    }

    @FXML
    private void moCuaSoThem() {
        try {
            List<DichVu> dichVus = lichHenService.layDichVuHoatDong();
            List<NhanVien> nhaSis = lichHenService.layNhaSiDangLamViec();
            if (dichVus.isEmpty() || nhaSis.isEmpty()) {
                TienIch.loi("Cần có ít nhất một dịch vụ đang hoạt động và một nha sĩ đang làm việc để đặt lịch.");
                return;
            }
            URL fxml = getClass().getResource("/nhakhoakim/ui/resource/UI_ThemLichHen.fxml");
            if (fxml == null) {
                throw new IllegalStateException("Không tìm thấy giao diện UI_ThemLichHen.fxml");
            }
            FXMLLoader loader = new FXMLLoader(fxml);
            VBox form = loader.load();
            ThemLichHenController formController = loader.getController();
            formController.datDanhSach(dichVus, nhaSis);

            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Thêm lịch hẹn");
            dialog.setHeaderText("Tạo lịch hẹn mới");
            ButtonType nutThem = new ButtonType("Thêm lịch hẹn", ButtonData.OK_DONE);
            ButtonType nutHuy = new ButtonType("Hủy", ButtonData.CANCEL_CLOSE);
            dialog.getDialogPane().getButtonTypes().addAll(nutThem, nutHuy);
            dialog.getDialogPane().setContent(form);
            dialog.getDialogPane().setPrefWidth(570);
            Node nutTao = dialog.getDialogPane().lookupButton(nutThem);
            nutTao.disableProperty().bind(formController.benhNhanProperty().isNull()
                    .or(formController.dichVuProperty().isNull())
                    .or(formController.nhaSiProperty().isNull()));
            dialog.showAndWait().ifPresent(lh -> {
                if (lh == nutThem) {
                    try {
                        LichHen lichMoi = formController.taoLichHen();
                        lichHenService.themLichHen(lichMoi);
                        TienIch.thongTin("Thêm lịch hẹn thành công.");
                        ngayDauTuan = lichMoi.getNgayHen().with(DayOfWeek.MONDAY);
                        lichDangChon = lichMoi;
                        taiLich();
                    } catch (RuntimeException e) {
                        TienIch.baoLoi(e);
                    }
                }
            });
        } catch (IOException e) {
            TienIch.baoLoi(new IllegalStateException("Không tải được biểu mẫu thêm lịch hẹn", e));
        } catch (RuntimeException e) {
            TienIch.baoLoi(e);
        }
    }

    private void doiTuan(int soNgay) {
        ngayDauTuan = ngayDauTuan.plusDays(soNgay);
        taiLich();
    }

    private void taiLich() {
        try {
            lichHenTrongTuan.clear();
            lichHenTrongTuan.addAll(lichHenService.layTheoKhoang(ngayDauTuan, ngayDauTuan.plusDays(6)));
            lblTuan.setText(TienIch.ngay(ngayDauTuan) + " - " + TienIch.ngay(ngayDauTuan.plusDays(6)));
            veBangLich();
            if (lichDangChon != null) {
                LichHen moiNhat = timTrongDanhSach(lichDangChon.getMaLichHen());
                if (moiNhat != null) {
                    chonLich(moiNhat);
                } else {
                    boChonLich();
                }
            }
        } catch (RuntimeException e) {
            TienIch.baoLoi(e);
        }
    }

    private void veBangLich() {
        List<Label> cotNgay = Arrays.asList(lblNgay0, lblNgay1, lblNgay2, lblNgay3, lblNgay4, lblNgay5, lblNgay6);
        for (int i = 0; i < cotNgay.size(); i++) {
            LocalDate ngay = ngayDauTuan.plusDays(i);
            cotNgay.get(i).setText(TEN_THU[i] + "\n" + TienIch.ngay(ngay));
        }
        List<VBox> cacO = Arrays.asList(slot00, slot01, slot02, slot03, slot04, slot05, slot06,
                slot10, slot11, slot12, slot13, slot14, slot15, slot16,
                slot20, slot21, slot22, slot23, slot24, slot25, slot26);
        for (VBox o : cacO) {
            o.getChildren().clear();
            o.setPadding(new Insets(6));
        }
        for (LichHen lh : lichHenTrongTuan) {
            if (lh.getNgayHen() == null || lh.getGioHen() == null) {
                continue;
            }
            int buoi = layBuoi(lh.getGioHen());
            int thu = lh.getNgayHen().getDayOfWeek().getValue() - 1;
            long ngayChenhLech = java.time.temporal.ChronoUnit.DAYS.between(ngayDauTuan, lh.getNgayHen());
            if (buoi >= 0 && ngayChenhLech >= 0 && ngayChenhLech < 7 && thu == ngayChenhLech) {
                cacO.get(buoi * 7 + thu).getChildren().add(taoNutLich(lh));
            }
        }
    }

    private Button taoNutLich(LichHen lh) {
        String ten = lh.getTenBenhNhan() == null ? lh.getMaBN() : lh.getTenBenhNhan();
        String dichVu = lh.getTenDichVu() == null ? "" : lh.getTenDichVu();
        Button nut = new Button(TienIch.gio(lh.getGioHen()) + "\n" + ten + "\n" + dichVu
                + "\n" + lh.getTrangThai());
        nut.setMaxWidth(Double.MAX_VALUE);
        nut.setWrapText(true);
        nut.setAlignment(Pos.CENTER_LEFT);
        boolean daHuy = "Đã hủy".equals(lh.getTrangThai());
        nut.setStyle("-fx-background-color: " + (daHuy ? "#E0E0E0" : "#78E3F3")
                + "; -fx-text-fill: #263238; -fx-padding: 7; -fx-background-radius: 2;"
                + "-fx-font-size: 12px; -fx-cursor: hand;");
        nut.setOnAction(e -> chonLich(lh));
        return nut;
    }

    private int layBuoi(LocalTime gio) {
        if (!gio.isBefore(LocalTime.of(5, 0)) && gio.isBefore(LocalTime.of(12, 0))) {
            return 0;
        }
        if (!gio.isBefore(LocalTime.of(12, 0)) && gio.isBefore(LocalTime.of(18, 0))) {
            return 1;
        }
        if (!gio.isBefore(LocalTime.of(18, 0)) && gio.isBefore(LocalTime.of(20, 0))) {
            return 2;
        }
        return -1;
    }

    private void chonLich(LichHen lh) {
        lichDangChon = lh;
        lblChiTiet.setText("Chi tiết lịch hẹn: " + lh.getMaLichHen());
        dpNgayHen.setValue(lh.getNgayHen());
        txtGioHen.setText(TienIch.gio(lh.getGioHen()));
        txtGhiChu.setText(TienIch.chu(lh.getGhiChu()));
        lblBenhNhan.setText(TienIch.chu(lh.getMaBN()) + " - " + TienIch.chu(lh.getTenBenhNhan()));
        lblDichVu.setText(TienIch.chu(lh.getMaDichVu()) + " - " + TienIch.chu(lh.getTenDichVu()));
        lblNhaSi.setText(TienIch.chu(lh.getMaNhanVien()) + " - " + TienIch.chu(lh.getTenNhanVien()));
        lblThoiLuong.setText(lh.getThoiLuongDuKien() + " phút");
        lblPhong.setText(TienIch.chu(lh.getPhong()).isEmpty() ? "Chưa phân phòng" : lh.getPhong());
        StringBuilder thongTinThem = new StringBuilder();
        thongTinThem.append("Ngày tạo: ")
                .append(lh.getNgayTao() == null ? "-" : TienIch.ngayGio(lh.getNgayTao()))
                .append("\nHình thức đặt: ").append(TienIch.chu(lh.getHinhThucDat()))
                .append("\nNhắc hẹn: ").append(lh.isDaNhacHen() ? "Đã nhắc" : "Chưa nhắc")
                .append("\nTrạng thái: ").append(TienIch.chu(lh.getTrangThai()));
        if ("Đã hủy".equals(lh.getTrangThai())) {
            thongTinThem.append("\nLý do hủy: ")
                    .append(lh.getLyDoHuy() == null || lh.getLyDoHuy().trim().isEmpty()
                            ? "Chưa ghi nhận" : lh.getLyDoHuy());
        }
        lblThongTinThem.setText(thongTinThem.toString());
        datTrangThaiChiTiet(!"Đã hủy".equals(lh.getTrangThai()));
    }

    private void datTrangThaiChiTiet(boolean coTheSua) {
        boolean coLich = lichDangChon != null;
        dpNgayHen.setDisable(!coLich || !coTheSua);
        txtGioHen.setDisable(!coLich || !coTheSua);
        txtGhiChu.setDisable(!coLich || !coTheSua);
        btnCapNhat.setDisable(!coLich || !coTheSua);
        btnHuy.setDisable(!coLich || !coTheSua);
        btnCapNhatHoSo.setDisable(!coLich);
    }

    private void boChonLich() {
        lichDangChon = null;
        lblChiTiet.setText("Chọn lịch hẹn trên bảng để xem chi tiết");
        dpNgayHen.setValue(null);
        txtGioHen.clear();
        txtGhiChu.clear();
        lblBenhNhan.setText("-");
        lblDichVu.setText("-");
        lblNhaSi.setText("-");
        lblThoiLuong.setText("-");
        lblPhong.setText("-");
        lblThongTinThem.setText("");
        datTrangThaiChiTiet(false);
    }

    private LichHen timTrongDanhSach(String maLichHen) {
        for (LichHen lh : lichHenTrongTuan) {
            if (lh.getMaLichHen().equals(maLichHen)) {
                return lh;
            }
        }
        return null;
    }

    private LocalTime docGio(String giaTri) {
        try {
            return LocalTime.parse(giaTri == null ? "" : giaTri.trim(), DINH_DANG_GIO);
        } catch (java.time.format.DateTimeParseException e) {
            throw new NghiepVuException("Giờ hẹn phải theo định dạng HH:mm, ví dụ 09:30");
        }
    }

    public void lamMoi() {
        taiLich();
    }
}
