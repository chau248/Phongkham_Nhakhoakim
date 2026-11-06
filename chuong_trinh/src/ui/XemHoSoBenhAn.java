package ui;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import nhakhoakim.MainApp;
import controller.HoSoBenhAnController;
import controller.PhienLamViec;
import entity.BenhNhan;
import entity.HangSo;
import entity.HoSoBenhAn;
import entity.LieuTrinh;
import entity.LuotKham;
import entity.NhanVien;

// Màn hình hồ sơ bệnh án
@SuppressWarnings("serial") // Lớp giao diện Swing
public class XemHoSoBenhAn extends JPanel {

    private HoSoBenhAnController hoSoService = new HoSoBenhAnController();

    private TienIch.ChonBenhNhan chonBenhNhan = new TienIch.ChonBenhNhan(); // Chọn bệnh nhân cần xem
    private JLabel lblThongTin = new JLabel(); // Thông tin hành chính
    private JTextArea txaTienSu = TienIch.oNhieuDong(3, 40); // Tiền sử bệnh
    private JComboBox<String> cmbNhomMau = new JComboBox<>(new String[] {"", "A", "B", "AB", "O"}); // Nhóm máu
    private JTextArea txaBenhNen = TienIch.oNhieuDong(2, 40); // Bệnh nền
    private JTextField txtThuoc = TienIch.oNhap(40); // Thuốc đang dùng
    private JButton btnLuu = TienIch.nutChinh("Lưu thay đổi");
    private JLabel lblQuyen = new JLabel(); // Giải thích
    private JTable tblLieuTrinh = TienIch.taoBang(new String[] {"Loại", "Số buổi", "Giai đoạn hiện tại", "Nha sĩ phụ trách", "Trạng thái", "Dự kiến kết thúc"},
            new int[] {100, 65, 220, 150, 110, 110}, "Bệnh nhân chưa có liệu trình."); // Liệu trình của bệnh nhân
    private JTable tblLichSu = TienIch.taoBang(new String[] {"Ngày khám", "Nha sĩ", "Lý do", "Chẩn đoán", "Trạng thái"},
            new int[] {130, 150, 180, 250, 100}, "Bệnh nhân chưa có lượt khám nào."); // Lịch sử khám
    private JPanel khuNoiDung = new JPanel(); // Phần hiện sau khi chọn bệnh
    private HoSoBenhAn hoSo; // Hồ sơ đang xem
    private HoSoBenhAn hoSoBanDau; // Bản sao hồ sơ lúc mở
    private JButton btnDongHoSo = TienIch.nutPhu("Đóng hồ sơ"); // Chỉ quản lý thấy

    public XemHoSoBenhAn() {
        this(null);
    }

    // Mở sẵn hồ sơ của một bệnh nhân
    public XemHoSoBenhAn(BenhNhan benhNhan) {
        JButton btnXem = TienIch.nutChinh("Xem hồ sơ");
        btnXem.addActionListener(e -> xemHoSo());
        JButton btnThoiGian = TienIch.nutPhu("Dòng thời gian điều trị");
        btnThoiGian.addActionListener(e -> {
            if (chonBenhNhan.getBenhNhan() == null) {
                TienIch.thongTin("Vui lòng chọn một bệnh nhân.");
            } else {
                MainApp.doiNoiDung(new XemDongThoiGian(chonBenhNhan.getBenhNhan()));
            }
        });

        cmbNhomMau.setFont(TienIch.FONT);
        lblQuyen.setFont(TienIch.FONT);
        lblQuyen.setForeground(TienIch.MAU_XAM);
        lblThongTin.setFont(TienIch.FONT_DAM);

        JPanel form = TienIch.taoForm(new String[] {"Tiền sử bệnh", "Nhóm máu", "Bệnh nền", "Thuốc đang dùng"},
                new Component[] {TienIch.cuonOChu(txaTienSu), cmbNhomMau, TienIch.cuonOChu(txaBenhNen), txtThuoc});
        btnLuu.addActionListener(e -> luuThongTin());
        btnDongHoSo.addActionListener(e -> doiTrangThaiHoSo());

        // Đổi bệnh nhân ở ô chọn
        chonBenhNhan.addChonListener(e -> {
            hoSo = null;
            khuNoiDung.setVisible(false);
        });

        JPanel theYTe = TienIch.the(lblThongTin, form, TienIch.dong(btnLuu, btnDongHoSo, lblQuyen));

        TienIch.cotXuongDong(tblLichSu, 3);
        khuNoiDung.setOpaque(false);
        TienIch.xep(khuNoiDung, 14, theYTe, TienIch.chuThuong("Liệu trình", true), TienIch.cuonBang(tblLieuTrinh, 110),
                TienIch.chuThuong("Lịch sử khám (mới nhất trước)", true), TienIch.cuonBang(tblLichSu, 150));
        khuNoiDung.setVisible(false);

        TienIch.xepManHinh(this, TienIch.tieuDe("Hồ sơ bệnh án"),
                TienIch.dong(TienIch.nhan("Bệnh nhân"), chonBenhNhan, btnXem, btnThoiGian), khuNoiDung);

        if (benhNhan != null) {
            chonBenhNhan.setBenhNhan(benhNhan); // Chưa có trong danh sách
            xemHoSo();
        }
    }

    // Nạp hồ sơ của bệnh nhân
    private void xemHoSo() {
        BenhNhan bn = chonBenhNhan.getBenhNhan();
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

    // Đổ dữ liệu hồ sơ vào các ô
    private void hienHoSo(BenhNhan bn) {

        lblThongTin.setText(bn.getMaBN() + " - " + bn.getHoTen() + "   |   Mã hồ sơ: " + hoSo.getMaHoSo()
                + "   |   Ngày tạo: " + TienIch.ngay(hoSo.getNgayTao()) + "   |   " + hoSo.getTrangThaiHoSo());
        txaTienSu.setText(TienIch.chu(hoSo.getTienSuBenh()));
        cmbNhomMau.setSelectedItem(TienIch.chu(hoSo.getNhomMau()));
        txaBenhNen.setText(TienIch.chu(hoSo.getBenhNen()));
        txtThuoc.setText(TienIch.chu(hoSo.getThuocDangSuDung()));
        hoSoBanDau = saoChep(hoSo);
        apDungQuyenSua();

                List<LieuTrinh> dsLieuTrinh = hoSoService.layLieuTrinh(bn.getMaBN());
        List<LuotKham> dsLichSu = hoSoService.layLichSuKham(hoSo.getMaHoSo());

        List<String[]> dongLieuTrinh = new ArrayList<>();
        for (LieuTrinh lt : dsLieuTrinh) {
            dongLieuTrinh.add(new String[] {lt.getLoaiDichVu(), String.valueOf(lt.getSoBuoiDuKien()),
                TienIch.chu(lt.getGiaiDoanHienTai()), lt.getMaNhaSiPhuTrach() == null ? "" : lt.getMaNhaSiPhuTrach().getHoTen(),
                lt.getTrangThai(), TienIch.ngay(lt.getNgayDuKienKetThuc())});
        }
        TienIch.napBang(tblLieuTrinh, dongLieuTrinh);

        List<String[]> dongLichSu = new ArrayList<>();
        for (LuotKham lk : dsLichSu) {
            dongLichSu.add(new String[] {TienIch.ngayGio(lk.getGioBatDau()), lk.getMaNhanVienPhuTrach().getHoTen(),
                TienIch.chu(lk.getLyDoKham()), TienIch.chu(lk.getChanDoan()), lk.getTrangThai()});
        }
        TienIch.napBang(tblLichSu, dongLichSu);

        khuNoiDung.setVisible(true);
        khuNoiDung.revalidate();
        khuNoiDung.repaint();
    }

    // Chỉ Nha sĩ
    private void apDungQuyenSua() {
        String lyDo = null;
        NhanVien nv = null;
        try {
            nv = PhienLamViec.getNhanVienHienTai();
        } catch (RuntimeException e) {
            nv = null; // Không biết ai đăng nhập
        }
        if (nv != null && !nv.laVaiTro(HangSo.VAI_TRO_NHA_SI, HangSo.VAI_TRO_Y_TA)) {
            lyDo = nv.getVaiTro() + " chỉ xem phần y tế";
        } else if (HangSo.HO_SO_DA_DONG.equals(hoSo.getTrangThaiHoSo())) {
            lyDo = "Hồ sơ đã đóng, chỉ xem";
        }
        btnLuu.setEnabled(lyDo == null);
        boolean quanLy = nv != null && nv.laVaiTro(HangSo.VAI_TRO_QUAN_LY);
        btnDongHoSo.setVisible(quanLy);
        btnDongHoSo.setText(HangSo.HO_SO_DA_DONG.equals(hoSo.getTrangThaiHoSo()) ? "Mở lại hồ sơ" : "Đóng hồ sơ");
        lblQuyen.setText(lyDo == null ? "" : lyDo);
    }

    // Sao chép các trường y tế của hồ sơ
    private HoSoBenhAn saoChep(HoSoBenhAn goc) {
        HoSoBenhAn b = new HoSoBenhAn();
        b.setMaHoSo(goc.getMaHoSo());
        b.setMaBN(goc.getMaBN());
        b.setTienSuBenh(goc.getTienSuBenh());
        b.setNhomMau(goc.getNhomMau());
        b.setBenhNen(goc.getBenhNen());
        b.setThuocDangSuDung(goc.getThuocDangSuDung());
        b.setGhiChuDacBiet(goc.getGhiChuDacBiet());
        return b;
    }

    // Quản lý đóng hồ sơ
    private void doiTrangThaiHoSo() {
        if (hoSo == null) {
            return;
        }
        boolean dongHienTai = HangSo.HO_SO_DA_DONG.equals(hoSo.getTrangThaiHoSo());
        String moi = dongHienTai ? HangSo.HO_SO_DANG_MO : HangSo.HO_SO_DA_DONG;
        if (!TienIch.xacNhan((dongHienTai ? "Mở lại" : "Đóng") + " hồ sơ " + hoSo.getMaHoSo() + "?")) {
            return;
        }
        try {
            hoSoService.doiTrangThaiHoSo(hoSo.getMaHoSo(), moi);
            xemHoSo();
        } catch (RuntimeException e) {
            TienIch.baoLoi(e);
        }
    }

    // Lưu tiền sử, dị ứng và thông tin y tế
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
            moi.setNhomMau(cmbNhomMau.getSelectedItem() == null ? "" : (String) cmbNhomMau.getSelectedItem());
            moi.setBenhNen(txaBenhNen.getText());
            moi.setThuocDangSuDung(txtThuoc.getText());
            hoSoService.capNhatThongTinYTe(moi, hoSoBanDau);
            xemHoSo();
            TienIch.thongTin("Đã lưu thông tin y tế.");
        } catch (RuntimeException e) {
            TienIch.baoLoi(e);
        }
    }
}
