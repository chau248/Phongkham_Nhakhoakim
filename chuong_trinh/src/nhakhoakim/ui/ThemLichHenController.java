package nhakhoakim.ui;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import javafx.fxml.FXML;
import javafx.beans.property.ObjectProperty;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;
import nhakhoakim.dao.BenhNhanService;
import nhakhoakim.dao.LichHenService;
import nhakhoakim.dao.NghiepVuException;
import nhakhoakim.entity.BenhNhan;
import nhakhoakim.entity.DichVu;
import nhakhoakim.entity.LichHen;
import nhakhoakim.entity.NhanVien;

// Điều khiển biểu mẫu thêm lịch hẹn trong UI_ThemLichHen.fxml.
public class ThemLichHenController {

    private static final DateTimeFormatter DINH_DANG_GIO = DateTimeFormatter.ofPattern("HH:mm");

    @FXML private ComboBox<BenhNhan> cmbBenhNhan;
    @FXML private ComboBox<DichVu> cmbDichVu;
    @FXML private ComboBox<NhanVien> cmbNhaSi;
    @FXML private DatePicker dpNgayHen;
    @FXML private TextField txtGioHen;
    @FXML private TextField txtThoiLuong;
    @FXML private TextField txtPhong;
    @FXML private ComboBox<String> cmbHinhThucDat;
    @FXML private CheckBox chkDaNhacHen;
    @FXML private TextArea txtGhiChu;

    @FXML
    private void initialize() {
        dpNgayHen.setValue(LocalDate.now());
        dpNgayHen.setConverter(TienIch.chuyenNgay());
        txtGioHen.setText("09:00");
        cmbHinhThucDat.getItems().setAll("Trực tiếp", "Điện thoại", "Online");
        cmbHinhThucDat.setValue("Trực tiếp");

        cmbBenhNhan.setEditable(true);
        cmbBenhNhan.setItems(javafx.collections.FXCollections.observableArrayList(
                new BenhNhanService().timKiem("")));
        cmbBenhNhan.setConverter(new StringConverter<BenhNhan>() {
            @Override
            public String toString(BenhNhan bn) {
                return bn == null ? "" : bn.getMaBN() + " - " + bn.getHoTen();
            }

            @Override
            public BenhNhan fromString(String value) {
                if (value == null) {
                    return null;
                }
                for (BenhNhan bn : cmbBenhNhan.getItems()) {
                    if (toString(bn).equals(value.trim())) {
                        return bn;
                    }
                }
                return null;
            }
        });
        cmbBenhNhan.getEditor().setOnAction(e -> timBenhNhan());

        cmbDichVu.setConverter(TienIch.chuyenDichVu());
        cmbDichVu.valueProperty().addListener((observable, cu, moi) -> {
            if (moi != null) {
                txtThoiLuong.setText(Integer.toString(moi.getThoiGianDuKien()));
            }
        });
        cmbNhaSi.setConverter(new StringConverter<NhanVien>() {
            @Override
            public String toString(NhanVien nv) {
                return nv == null ? "" : nv.getHoTen();
            }

            @Override
            public NhanVien fromString(String value) {
                return null;
            }
        });
    }

    public void datDanhSach(List<DichVu> dichVus, List<NhanVien> nhaSis) {
        cmbDichVu.getItems().setAll(dichVus);
        cmbNhaSi.getItems().setAll(nhaSis);
    }

    public ObjectProperty<BenhNhan> benhNhanProperty() {
        return cmbBenhNhan.valueProperty();
    }

    public ObjectProperty<DichVu> dichVuProperty() {
        return cmbDichVu.valueProperty();
    }

    public ObjectProperty<NhanVien> nhaSiProperty() {
        return cmbNhaSi.valueProperty();
    }

    public LichHen taoLichHen() {
        BenhNhan bn = cmbBenhNhan.getValue();
        DichVu dv = cmbDichVu.getValue();
        NhanVien nv = cmbNhaSi.getValue();
        if (bn == null || dv == null || nv == null) {
            throw new NghiepVuException("Vui lòng chọn bệnh nhân, dịch vụ và nha sĩ");
        }

        LichHen lh = new LichHen();
        lh.setMaBN(bn.getMaBN());
        lh.setMaDichVu(dv.getMaDichVu());
        lh.setMaNhanVien(nv.getMaNV());
        lh.setNgayHen(dpNgayHen.getValue());
        try {
            lh.setGioHen(LocalTime.parse(txtGioHen.getText().trim(), DINH_DANG_GIO));
        } catch (DateTimeParseException e) {
            throw new NghiepVuException("Giờ hẹn phải theo định dạng HH:mm, ví dụ 09:30");
        }
        try {
            lh.setThoiLuongDuKien(Integer.parseInt(txtThoiLuong.getText().trim()));
        } catch (NumberFormatException e) {
            throw new NghiepVuException("Thời lượng phải là số phút hợp lệ");
        }
        lh.setPhong(txtPhong.getText().trim().isEmpty() ? null : txtPhong.getText().trim());
        lh.setHinhThucDat(cmbHinhThucDat.getValue());
        lh.setDaNhacHen(chkDaNhacHen.isSelected());
        lh.setGhiChu(txtGhiChu.getText());
        return lh;
    }

    private void timBenhNhan() {
        String tuKhoa = cmbBenhNhan.getEditor().getText().trim();
        BenhNhan dangChon = cmbBenhNhan.getValue();
        if (dangChon != null && cmbBenhNhan.getConverter().toString(dangChon).equals(tuKhoa)) {
            return;
        }
        try {
            List<BenhNhan> ketQua = new BenhNhanService().timKiem(tuKhoa);
            cmbBenhNhan.setValue(null);
            cmbBenhNhan.getItems().setAll(ketQua);
            cmbBenhNhan.getEditor().setText(tuKhoa);
            if (ketQua.isEmpty()) {
                TienIch.thongTin("Không tìm thấy bệnh nhân nào khớp với \"" + tuKhoa + "\".");
            } else {
                cmbBenhNhan.show();
            }
        } catch (RuntimeException e) {
            TienIch.baoLoi(e);
        }
    }
}
