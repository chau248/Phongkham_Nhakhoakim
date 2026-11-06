package ui;

import java.awt.Color;
import java.awt.Component;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import nhakhoakim.MainApp;
import controller.BenhNhanController;
import controller.DichVuController;
import controller.HoSoBenhAnController;
import controller.LuotKhamController;
import controller.NhaSiPhuTrachController;
import dao.PhienLamViec;
import controller.RangDieuTriController;
import entity.BenhNhan;
import entity.DichVu;
import entity.HangSo;
import entity.HoSoBenhAn;
import entity.LichHen;
import entity.LuotKham;
import entity.LuotKhamDichVu;

// Màn hình khám và ghi nhận điều trị
@SuppressWarnings("serial") // Lớp giao diện Swing
public class ManHinhKhamDieuTri extends JPanel {

    private LuotKhamController luotKhamService = new LuotKhamController();
    private HoSoBenhAnController hoSoService = new HoSoBenhAnController();
    private NhaSiPhuTrachController nhaSiService = new NhaSiPhuTrachController();
    private BenhNhanController benhNhanService = new BenhNhanController();
    private DichVuController dichVuService = new DichVuController();
    private RangDieuTriController rangService = new RangDieuTriController();

    private LichHen lichHen; // Lịch hẹn đang khám
    private LuotKham luotKham; // Lượt khám đang khám của nha sĩ
    private TienIch.ChonBenhNhan chonBenhNhan; // Chọn bệnh nhân khám vãng lai
    private JTextField txtLyDo = TienIch.oNhap(50); // Lý do khám
    private JTextArea txaChanDoan = TienIch.oNhieuDong(2, 50); // Chẩn đoán
    private JTextArea txaHuongDieuTri = TienIch.oNhieuDong(2, 50); // Hướng điều trị
    private JTextArea txaDonThuoc = TienIch.oNhieuDong(3, 50); // Đơn thuốc, mỗi dòng một thuốc
    private JTextField txtGhiChu = TienIch.oNhap(50); // Ghi chú
    private JTable tblDichVu = TienIch.taoBang(new String[] {"Mã", "Dịch vụ", "Số lượng", "Đơn giá (đồng)"},
            new int[] {90, 260, 80, 120}, "Chưa chọn dịch vụ nào."); // Dịch vụ đã chọn
    private List<LuotKhamDichVu> dichVuDaChon = new ArrayList<>(); // Dữ liệu của bảng dịch vụ

    public ManHinhKhamDieuTri() {
        this(null);
    }

    // Mở màn hình khám cho một lịch hẹn
    public ManHinhKhamDieuTri(LichHen lichHen) {
        this.lichHen = lichHen;
        dung();
    }

    // Dựng lại màn hình
    private void dung() {
        removeAll();
        List<Component> cacKhoi = new ArrayList<>();
        cacKhoi.add(TienIch.tieuDe("Khám và ghi nhận điều trị"));
        try {
            luotKham = luotKhamService.layDangKham(PhienLamViec.getNhanVienHienTai().getMaNV());
            if (luotKham == null) {
                dungPhanBatDau(cacKhoi);
            } else {
                // Mở theo một lịch hẹn
                if (lichHen != null && !lichHen.getMaLichHen().equals(luotKham.getMaLichHen())) {
                    cacKhoi.add(nhanCanhBao("Bạn đang khám bệnh nhân khác; lịch hẹn này chưa được bắt đầu"));
                }
                dungFormKham(cacKhoi);
            }
        } catch (RuntimeException e) {
            TienIch.baoLoi(e);
        }
        TienIch.xepManHinh(this, cacKhoi.toArray(new Component[0]));
        revalidate();
        repaint();
    }

    // Khối cảnh báo nền vàng
    private JComponent nhanCanhBao(String noiDung) {
        return TienIch.thongBao(noiDung, TienIch.MAU_VANG_NEN, TienIch.MAU_VANG_CHU, false);
    }

    // Phần bắt đầu khám
    private void dungPhanBatDau(List<Component> cacKhoi) {
        txtLyDo.setText(""); // Không giữ lại lý
        boolean laNhaSi = HangSo.VAI_TRO_NHA_SI.equals(PhienLamViec.getNhanVienHienTai().getVaiTro());

        List<Component> trongThe = new ArrayList<>();
        if (lichHen != null) {
            BenhNhan bn = benhNhanService.timTheoMa(lichHen.getMaBN());
            DichVu dv = dichVuService.timTheoMa(lichHen.getMaDichVu());
            trongThe.add(TienIch.doanChu("Lịch hẹn " + lichHen.getMaLichHen() + ": " + (bn == null ? lichHen.getMaBN() : bn.getHoTen())
                    + " - " + (dv == null ? lichHen.getMaDichVu() : dv.getTenDichVu()) + " lúc " + TienIch.gio(lichHen.getGioHen()), Color.BLACK));
            JButton btnTheoLich = TienIch.nutChinh("Bắt đầu khám theo lịch");
            btnTheoLich.setEnabled(laNhaSi);
            btnTheoLich.addActionListener(e -> {
                try {
                    luotKhamService.batDauKhamTheoLich(lichHen.getMaLichHen(), PhienLamViec.getNhanVienHienTai().getMaNV());
                    dung();
                } catch (RuntimeException ex) {
                    TienIch.baoLoi(ex);
                }
            });
            trongThe.add(TienIch.dong(btnTheoLich));
        } else {
            chonBenhNhan = new TienIch.ChonBenhNhan(); // Tạo mới để không còn chọn sẵn người cũ
            trongThe.add(TienIch.doanChu("Chưa có lượt khám đang khám. Chọn bệnh nhân (khám vãng lai) rồi bấm Bắt đầu khám.", Color.BLACK));
            trongThe.add(TienIch.dong(TienIch.nhan("Bệnh nhân"), chonBenhNhan));
            trongThe.add(TienIch.dong(TienIch.nhan("Lý do khám"), txtLyDo));
            JButton btnBatDau = TienIch.nutChinh("Bắt đầu khám");
            btnBatDau.setEnabled(laNhaSi);
            btnBatDau.addActionListener(e -> {
                BenhNhan bn = chonBenhNhan.getBenhNhan();
                if (bn == null) {
                    TienIch.thongTin("Vui lòng chọn bệnh nhân.");
                    return;
                }
                try {
                    luotKhamService.batDauKhamTheoBenhNhan(bn.getMaBN(), PhienLamViec.getNhanVienHienTai().getMaNV(), txtLyDo.getText().trim());
                    dung();
                } catch (RuntimeException ex) {
                    TienIch.baoLoi(ex);
                }
            });
            trongThe.add(TienIch.dong(btnBatDau));
        }
        if (!laNhaSi) {
            trongThe.add(TienIch.doanChu("Chỉ nha sĩ mới bắt đầu khám", TienIch.MAU_XAM));
        }
        cacKhoi.add(TienIch.the(trongThe.toArray(new Component[0])));
    }

    // Form khám của lượt khám đang khám
    private void dungFormKham(List<Component> cacKhoi) {
        HoSoBenhAn hs = hoSoService.timTheoMa(luotKham.getMaHoSo());
        BenhNhan bn = benhNhanService.timTheoMa(hs.getMaBN());

        cacKhoi.add(DongThongTinLuotKham.tao(bn == null ? hs.getMaBN() : bn.getHoTen(),
                nhaSiService.layTen(luotKham.getMaNhanVienPhuTrach()), luotKham));

        if (luotKham.getGioBatDau().toLocalDate().isBefore(LocalDate.now())) {
            cacKhoi.add(nhanCanhBao("Lượt khám này mở từ ngày " + TienIch.ngay(luotKham.getGioBatDau().toLocalDate())
                    + " nhưng chưa hoàn tất. Hãy hoàn tất khám nếu đã khám xong, hoặc hủy lượt khám nếu bệnh nhân không còn ở phòng khám."));
        }

        String diUng = hs.coCanhBaoDiUng() ? TienIch.chu(hs.getCanhBaoDiUng()).trim() : "";
        if (!diUng.isEmpty()) {
            cacKhoi.add(TienIch.thongBao("CẢNH BÁO DỊ ỨNG: " + diUng, TienIch.MAU_DO_NEN, TienIch.MAU_DO_CHU, true));
        }

        txtLyDo.setText(TienIch.chu(luotKham.getLyDoKham()));
        txaChanDoan.setText(TienIch.chu(luotKham.getChanDoan()));
        txaHuongDieuTri.setText(TienIch.chu(luotKham.getHuongDieuTri()));
        txaDonThuoc.setText(TienIch.chu(luotKham.getDonThuoc()));
        txtGhiChu.setText(TienIch.chu(luotKham.getGhiChu()));
        txaDonThuoc.setToolTipText("Mỗi dòng một thuốc, ví dụ: Paracetamol 500mg x 10 viên");

        JPanel form = TienIch.taoForm(new String[] {"Lý do khám", "Chẩn đoán", "Hướng điều trị", "Đơn thuốc (mỗi dòng một thuốc)", "Ghi chú"},
                new Component[] {txtLyDo, TienIch.cuonOChu(txaChanDoan), TienIch.cuonOChu(txaHuongDieuTri), TienIch.cuonOChu(txaDonThuoc), txtGhiChu});
        cacKhoi.add(TienIch.the(form));

        napBangDichVu();
        JButton btnThemDV = TienIch.nutPhu("Thêm dịch vụ");
        btnThemDV.addActionListener(e -> themDichVu());
        JButton btnXoaDV = TienIch.nutPhu("Xóa dịch vụ đã chọn");
        btnXoaDV.addActionListener(e -> xoaDichVu());

        JButton btnLuu = TienIch.nutChinh("Lưu nội dung khám");
        btnLuu.addActionListener(e -> luuNoiDung(true));
        JButton btnSoDo = TienIch.nutPhu("Sơ đồ răng");
        btnSoDo.addActionListener(e -> MainApp.doiNoiDung(new ManHinhSoDoRang()));
        JButton btnTaiKham = TienIch.nutPhu("Chỉ định tái khám");
        btnTaiKham.addActionListener(e -> MainApp.doiNoiDung(new ManHinhChiDinhTaiKham()));
        JButton btnHoanTat = TienIch.nutChinh("Hoàn tất khám");
        btnHoanTat.addActionListener(e -> hoanTat());
        JButton btnHuy = TienIch.nutDo("Hủy lượt khám");
        btnHuy.addActionListener(e -> huyLuotKham());

        cacKhoi.add(TienIch.chuThuong("Dịch vụ thực hiện (không gắn răng cụ thể; dịch vụ gắn răng ghi ở Sơ đồ răng)", false));
        cacKhoi.add(TienIch.cuonBang(tblDichVu, 120));
        cacKhoi.add(TienIch.dong(btnThemDV, btnXoaDV));
        cacKhoi.add(TienIch.dong(btnLuu, btnSoDo, btnTaiKham, btnHoanTat, btnHuy));
    }

    // Nạp lại bảng dịch vụ của lượt khám
    private void napBangDichVu() {
        dichVuDaChon = luotKhamService.layDichVuKhongGanRang(luotKham.getMaLuotKham());
        Map<String, DichVu> danhMuc = new HashMap<>();
        for (DichVu dv : dichVuService.layTatCa()) {
            danhMuc.put(dv.getMaDichVu(), dv);
        }
        List<String[]> dong = new ArrayList<>();
        for (LuotKhamDichVu x : dichVuDaChon) {
            DichVu dv = danhMuc.get(x.getMaDichVu());
            dong.add(new String[] {x.getMaDichVu(), dv == null ? "" : dv.getTenDichVu(), String.valueOf(x.getSoLuong()),
                dv == null ? "" : TienIch.tien(dv.getGia())});
        }
        TienIch.napBang(tblDichVu, dong);
    }

    // Mở hộp thoại chọn dịch vụ
    private void themDichVu() {
        DichVu dv = HopThoaiChonDichVu.chon();
        if (dv == null) {
            return;
        }
        try {
            String canhBao = luotKhamService.canhBaoThuThuat(luotKham.getMaLuotKham(), dv.getMaDichVu());
            if (canhBao != null && !TienIch.xacNhan(canhBao + "\n\nVẫn thêm dịch vụ này?")) {
                return;
            }
            luotKhamService.themDichVu(luotKham.getMaLuotKham(), dv.getMaDichVu(), 1);
            napBangDichVu();
        } catch (RuntimeException e) {
            TienIch.baoLoi(e);
        }
    }

    // Xóa dịch vụ đang chọn trong bảng
    private void xoaDichVu() {
        int vt = tblDichVu.getSelectedRow();
        if (vt < 0 || vt >= dichVuDaChon.size()) {
            TienIch.thongTin("Vui lòng chọn một dịch vụ trong bảng.");
            return;
        }
        try {
            luotKhamService.xoaDichVu(luotKham.getMaLuotKham(), dichVuDaChon.get(vt).getMaDong());
            napBangDichVu();
        } catch (RuntimeException e) {
            TienIch.baoLoi(e);
        }
    }

    // Lưu lý do, chẩn đoán, hướng điều trị
    private boolean luuNoiDung(boolean hienThongBao) {
        try {
            luotKhamService.luuNoiDungKham(luotKham.getMaLuotKham(), txtLyDo.getText().trim(), txaChanDoan.getText().trim(),
                    txaHuongDieuTri.getText().trim(), txaDonThuoc.getText().trim(), txtGhiChu.getText().trim());
            if (hienThongBao) {
                TienIch.thongTin("Đã lưu nội dung khám.");
            }
            return true;
        } catch (RuntimeException e) {
            TienIch.baoLoi(e);
            return false;
        }
    }

    // Lượt khám đã ghi nhận dịch
    private boolean daGhiDichVuHoacRang() {
        String maLuotKham = luotKham.getMaLuotKham();
        return !luotKhamService.layDichVuKhongGanRang(maLuotKham).isEmpty() || !rangService.layTheoLuotKham(maLuotKham).isEmpty();
    }

    // Hủy lượt khám
    private void huyLuotKham() {
        String maLuotKham = luotKham.getMaLuotKham();
        try {
            if (daGhiDichVuHoacRang()) {
                TienIch.loi("Lượt khám đã ghi nhận dịch vụ hoặc răng điều trị nên không thể hủy, hãy hoàn tất khám.");
                return;
            }
        } catch (RuntimeException e) {
            TienIch.baoLoi(e);
            return;
        }
        String lyDo = TienIch.nhapChu("Hủy lượt khám", "Lý do hủy khám:");
        if (lyDo == null) {
            return;
        }
        try {
            luotKhamService.huyLuotKham(maLuotKham, lyDo.trim());
            TienIch.thongTin("Đã hủy lượt khám " + maLuotKham + ".");
            lichHen = null;
            dung();
        } catch (RuntimeException e) {
            TienIch.baoLoi(e);
        }
    }

    // Hoàn tất khám
    private void hoanTat() {
        if (txaChanDoan.getText().trim().isEmpty()) {
            TienIch.thongTin("Phải nhập chẩn đoán trước khi hoàn tất khám.");
            return;
        }
        try {
            if (luotKhamService.layDichVuDaThucHien(luotKham.getMaLuotKham()).isEmpty()) {
                TienIch.thongTin("Phải ghi ít nhất một dịch vụ đã thực hiện (khám tổng quát cũng là một dịch vụ) trước khi hoàn tất khám.");
                return;
            }
        } catch (RuntimeException e) {
            TienIch.baoLoi(e);
            return;
        }
        if (!TienIch.xacNhan("Hoàn tất lượt khám? Sau khi hoàn tất sẽ không sửa được nữa.")) {
            return;
        }
        if (!luuNoiDung(false)) {
            return;
        }
        try {
            luotKhamService.hoanTatKham(luotKham.getMaLuotKham());
            TienIch.thongTin("Đã hoàn tất khám. Lượt khám chuyển sang danh sách chờ thanh toán.");
            lichHen = null;
            dung();
        } catch (RuntimeException e) {
            TienIch.baoLoi(e);
        }
    }
}
