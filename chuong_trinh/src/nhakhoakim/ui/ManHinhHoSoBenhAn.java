package nhakhoakim.ui;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import nhakhoakim.MainApp;
import nhakhoakim.controller.HoSoBenhAnController;
import nhakhoakim.controller.NhaSiPhuTrachController;
import nhakhoakim.dao.PhienLamViec;
import nhakhoakim.entity.BenhNhan;
import nhakhoakim.entity.HangSo;
import nhakhoakim.entity.HoSoBenhAn;
import nhakhoakim.entity.LieuTrinh;
import nhakhoakim.entity.LuotKham;
import nhakhoakim.entity.NhanVien;

// Màn hình hồ sơ bệnh án: xem tiền sử, dị ứng, liệu trình đang thực hiện, lịch sử khám; sửa tiền sử và dị ứng.
// Ba chức năng "Xem hồ sơ bệnh án", "Cập nhật tiền sử & dị ứng", "Xem liệu trình đang thực hiện" dùng chung màn hình này.
@SuppressWarnings("serial") // lớp giao diện Swing, không cần serialVersionUID
public class ManHinhHoSoBenhAn extends JPanel {

    private HoSoBenhAnController hoSoService = new HoSoBenhAnController();
    private NhaSiPhuTrachController nhaSiService = new NhaSiPhuTrachController();

    private TienIch.ChonBenhNhan chonBenhNhan = new TienIch.ChonBenhNhan();   // chọn bệnh nhân cần xem
    private JPanel khuBanner = new JPanel(new java.awt.BorderLayout());      // chỗ đặt banner dị ứng
    private JLabel lblThongTin = new JLabel();                               // thông tin hành chính
    private JTextArea txaTienSu = TienIch.oNhieuDong(3, 40);                 // tiền sử bệnh
    private JTextField txtDiUng = TienIch.oNhap(40);                         // dị ứng, cách nhau dấu phẩy
    private JComboBox<String> cmbNhomMau = new JComboBox<>(new String[] {"", "A", "B", "AB", "O"}); // nhóm máu
    private JTextArea txaBenhNen = TienIch.oNhieuDong(2, 40);                // bệnh nền
    private JTextField txtThuoc = TienIch.oNhap(40);                         // thuốc đang dùng
    private JButton btnLuu = TienIch.nutChinh("Lưu thay đổi");
    private JLabel lblQuyen = new JLabel();                                  // giải thích vì sao không sửa được
    private JTable tblLieuTrinh = TienIch.taoBang(new String[] {"Loại", "Số buổi", "Giai đoạn hiện tại", "Nha sĩ phụ trách", "Trạng thái", "Dự kiến kết thúc"},
            new int[] {100, 65, 220, 150, 110, 110}, "Bệnh nhân chưa có liệu trình.");                // liệu trình của bệnh nhân
    private JTable tblLichSu = TienIch.taoBang(new String[] {"Ngày khám", "Nha sĩ", "Lý do", "Chẩn đoán", "Trạng thái"},
            new int[] {130, 150, 180, 250, 100}, "Bệnh nhân chưa có lượt khám nào.");                  // lịch sử khám
    private JPanel khuNoiDung = new JPanel();                                // phần hiện sau khi chọn bệnh nhân
    private HoSoBenhAn hoSo;                                                 // hồ sơ đang xem
    private HoSoBenhAn hoSoBanDau;                                           // bản sao hồ sơ lúc mở, để Service gộp đúng khi hai người cùng sửa
    private JButton btnDongHoSo = TienIch.nutPhu("Đóng hồ sơ");              // chỉ quản lý thấy

    public ManHinhHoSoBenhAn() {
        this(null);
    }

    // Mở sẵn hồ sơ của một bệnh nhân (null thì để người dùng tự chọn)
    public ManHinhHoSoBenhAn(BenhNhan benhNhan) {
        JButton btnXem = TienIch.nutChinh("Xem hồ sơ");
        btnXem.addActionListener(e -> xemHoSo());
        JButton btnThoiGian = TienIch.nutPhu("Dòng thời gian điều trị");
        btnThoiGian.addActionListener(e -> {
            if (chonBenhNhan.getBenhNhan() == null) {
                TienIch.thongTin("Vui lòng chọn một bệnh nhân.");
            } else {
                MainApp.doiNoiDung(new ManHinhDongThoiGian(chonBenhNhan.getBenhNhan()));
            }
        });

        cmbNhomMau.setFont(TienIch.FONT);
        txtDiUng.setToolTipText("Ví dụ: Penicillin, Lidocaine");
        lblQuyen.setFont(TienIch.FONT);
        lblQuyen.setForeground(TienIch.MAU_XAM);
        lblThongTin.setFont(TienIch.FONT_DAM);

        JPanel form = TienIch.taoForm(new String[] {"Tiền sử bệnh", "Dị ứng", "Nhóm máu", "Bệnh nền", "Thuốc đang dùng"},
                new Component[] {TienIch.cuonOChu(txaTienSu), txtDiUng, cmbNhomMau, TienIch.cuonOChu(txaBenhNen), txtThuoc});
        btnLuu.addActionListener(e -> luuThongTin());
        btnDongHoSo.addActionListener(e -> doiTrangThaiHoSo());

        // Đổi bệnh nhân ở ô chọn thì ẩn hồ sơ đang hiện và bỏ hồ sơ cũ, để không lưu nhầm vào bệnh nhân khác
        chonBenhNhan.addChonListener(e -> {
            hoSo = null;
            khuNoiDung.setVisible(false);
        });

        JPanel theYTe = TienIch.the(lblThongTin, form, TienIch.dong(btnLuu, btnDongHoSo, lblQuyen));

        khuBanner.setOpaque(false);
        TienIch.cotXuongDong(tblLichSu, 3);
        khuNoiDung.setOpaque(false);
        TienIch.xep(khuNoiDung, 14, khuBanner, theYTe, TienIch.chuThuong("Liệu trình", true), TienIch.cuonBang(tblLieuTrinh, 110),
                TienIch.chuThuong("Lịch sử khám (mới nhất trước)", true), TienIch.cuonBang(tblLichSu, 150));
        khuNoiDung.setVisible(false);

        TienIch.xepManHinh(this, TienIch.tieuDe("Hồ sơ bệnh án"),
                TienIch.dong(TienIch.nhan("Bệnh nhân"), chonBenhNhan, btnXem, btnThoiGian), khuNoiDung);

        if (benhNhan != null) {
            chonBenhNhan.setBenhNhan(benhNhan); // chưa có trong danh sách thì thêm vào đầu rồi chọn
            xemHoSo();
        }
    }

    // Nạp hồ sơ của bệnh nhân đang chọn lên màn hình
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

    // Đổ dữ liệu hồ sơ vào các ô; nếu có dị ứng thì hiện khung cảnh báo đỏ ở đầu
    private void hienHoSo(BenhNhan bn) {
        String diUng = hoSo.coCanhBaoDiUng() ? TienIch.chu(hoSo.getCanhBaoDiUng()).trim() : "";
        khuBanner.removeAll();
        if (!diUng.isEmpty()) {
            khuBanner.add(TienIch.thongBao("CẢNH BÁO DỊ ỨNG: " + diUng, TienIch.MAU_DO_NEN, TienIch.MAU_DO_CHU, true), java.awt.BorderLayout.CENTER);
        }

        lblThongTin.setText(bn.getMaBN() + " - " + bn.getHoTen() + "   |   Mã hồ sơ: " + hoSo.getMaHoSo()
                + "   |   Ngày tạo: " + TienIch.ngay(hoSo.getNgayTao()) + "   |   " + hoSo.getTrangThaiHoSo());
        txaTienSu.setText(TienIch.chu(hoSo.getTienSuBenh()));
        txtDiUng.setText(TienIch.chu(hoSo.getCanhBaoDiUng()));
        cmbNhomMau.setSelectedItem(TienIch.chu(hoSo.getNhomMau()));
        txaBenhNen.setText(TienIch.chu(hoSo.getBenhNen()));
        txtThuoc.setText(TienIch.chu(hoSo.getThuocDangSuDung()));
        hoSoBanDau = saoChep(hoSo);
        apDungQuyenSua();

        // Lấy liệu trình và lịch sử khám trước, rồi hỏi tên nha sĩ một lần cho cả hai bảng
        List<LieuTrinh> dsLieuTrinh = hoSoService.layLieuTrinh(bn.getMaBN());
        List<LuotKham> dsLichSu = hoSoService.layLichSuKham(hoSo.getMaHoSo());
        List<String> dsMaNhaSi = new ArrayList<>();
        for (LieuTrinh lt : dsLieuTrinh) {
            dsMaNhaSi.add(lt.getMaNhaSiPhuTrach());
        }
        for (LuotKham lk : dsLichSu) {
            dsMaNhaSi.add(lk.getMaNhanVienPhuTrach());
        }
        Map<String, String> tenNhaSi = nhaSiService.layTen(dsMaNhaSi);

        List<String[]> dongLieuTrinh = new ArrayList<>();
        for (LieuTrinh lt : dsLieuTrinh) {
            dongLieuTrinh.add(new String[] {lt.getLoaiDichVu(), String.valueOf(lt.getSoBuoiDuKien()),
                TienIch.chu(lt.getGiaiDoanHienTai()), NhaSiPhuTrachController.tenHoacMa(tenNhaSi, lt.getMaNhaSiPhuTrach()),
                lt.getTrangThai(), TienIch.ngay(lt.getNgayDuKienKetThuc())});
        }
        TienIch.napBang(tblLieuTrinh, dongLieuTrinh);

        List<String[]> dongLichSu = new ArrayList<>();
        for (LuotKham lk : dsLichSu) {
            dongLichSu.add(new String[] {TienIch.ngayGio(lk.getGioBatDau()), NhaSiPhuTrachController.tenHoacMa(tenNhaSi, lk.getMaNhanVienPhuTrach()),
                TienIch.chu(lk.getLyDoKham()), TienIch.chu(lk.getChanDoan()), lk.getTrangThai()});
        }
        TienIch.napBang(tblLichSu, dongLichSu);

        khuNoiDung.setVisible(true);
        khuNoiDung.revalidate();
        khuNoiDung.repaint();
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
        if (nv != null && !HangSo.VAI_TRO_NHA_SI.equals(nv.getVaiTro()) && !HangSo.VAI_TRO_Y_TA.equals(nv.getVaiTro())) {
            lyDo = nv.getVaiTro() + " chỉ xem phần y tế";
        } else if (HangSo.HO_SO_DA_DONG.equals(hoSo.getTrangThaiHoSo())) {
            lyDo = "Hồ sơ đã đóng, chỉ xem";
        }
        btnLuu.setEnabled(lyDo == null);
        boolean quanLy = nv != null && HangSo.VAI_TRO_QUAN_LY.equals(nv.getVaiTro());
        btnDongHoSo.setVisible(quanLy);
        btnDongHoSo.setText(HangSo.HO_SO_DA_DONG.equals(hoSo.getTrangThaiHoSo()) ? "Mở lại hồ sơ" : "Đóng hồ sơ");
        lblQuyen.setText(lyDo == null ? "" : lyDo);
    }

    // Sao chép các trường y tế của hồ sơ (để so sánh khi lưu)
    private HoSoBenhAn saoChep(HoSoBenhAn goc) {
        HoSoBenhAn b = new HoSoBenhAn();
        b.setMaHoSo(goc.getMaHoSo());
        b.setMaBN(goc.getMaBN());
        b.setTienSuBenh(goc.getTienSuBenh());
        b.setCanhBaoDiUng(goc.getCanhBaoDiUng());
        b.setNhomMau(goc.getNhomMau());
        b.setBenhNen(goc.getBenhNen());
        b.setThuocDangSuDung(goc.getThuocDangSuDung());
        b.setGhiChuDacBiet(goc.getGhiChuDacBiet());
        return b;
    }

    // Quản lý đóng hồ sơ (chỉ xem) hoặc mở lại hồ sơ đã đóng
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
