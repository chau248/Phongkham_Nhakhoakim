package ui;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import nhakhoakim.MainApp;
import controller.BenhNhanController;
import controller.NhanVienController;
import controller.PhienLamViec;
import entity.BenhNhan;
import entity.HangSo;
import entity.NhanVien;

// Màn hình tìm kiếm bệnh nhân theo mã
@SuppressWarnings("serial") // Lớp giao diện Swing
public class TraCuuBenhNhan extends JPanel implements MainApp.LamMoiDuoc {

    private BenhNhanController benhNhanService = new BenhNhanController();
    private NhanVienController nhanVienService = new NhanVienController();

    private JTextField txtTuKhoa = TienIch.oNhap(28); // Từ khóa tìm kiếm
    private JTable tblBenhNhan = TienIch.taoBang(new String[] {"Mã", "Họ tên", "Ngày sinh", "Giới tính", "Số điện thoại", "Địa chỉ", "Nha sĩ khám gần nhất", "Trạng thái"},
            new int[] {90, 180, 95, 75, 115, 200, 170, 110}, "Không có bệnh nhân nào."); // Kết quả
    private JLabel lblKetQua = new JLabel(); // Số kết quả
    private List<BenhNhan> dsBenhNhan = new ArrayList<>(); // Dữ liệu của bảng
    private JButton btnSua = TienIch.nutPhu("Sửa thông tin");
    private JButton btnThem = TienIch.nutPhu("Thêm bệnh nhân mới");
    private JLabel lblQuyen = new JLabel(); // Giải thích
    private JCheckBox chkNgung = new JCheckBox("Gồm cả bệnh nhân ngừng theo dõi");
    private JButton btnNgung = TienIch.nutPhu("Ngừng theo dõi");
    private JButton btnMoLai = TienIch.nutPhu("Mở lại điều trị");

    public TraCuuBenhNhan() {
        txtTuKhoa.setToolTipText("Nhập mã, họ tên hoặc số điện thoại");
        txtTuKhoa.addActionListener(e -> tim());
        JButton btnTim = TienIch.nutChinh("Tìm kiếm");
        btnTim.addActionListener(e -> tim());
        chkNgung.setOpaque(false);
        chkNgung.setFont(TienIch.FONT);
        chkNgung.addActionListener(e -> tim());

        // Bấm đúp chỉ mở hồ sơ
        tblBenhNhan.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && tblBenhNhan.rowAtPoint(e.getPoint()) >= 0) {
                    xemHoSo();
                }
            }
        });

        JButton btnHoSo = TienIch.nutChinh("Xem hồ sơ bệnh án");
        btnHoSo.addActionListener(e -> xemHoSo());
        btnSua.addActionListener(e -> {
            BenhNhan bn = dangChon();
            if (bn != null) {
                MainApp.doiNoiDung(new ThemSuaBenhNhan(bn));
            }
        });
        btnThem.addActionListener(e -> MainApp.doiNoiDung(new ThemSuaBenhNhan(null)));
        btnNgung.addActionListener(e -> doiTrangThai(HangSo.NGUNG_THEO_DOI));
        btnMoLai.addActionListener(e -> doiTrangThai(HangSo.DANG_DIEU_TRI));

        lblKetQua.setFont(TienIch.FONT);
        lblKetQua.setForeground(TienIch.MAU_XAM);
        lblQuyen.setFont(TienIch.FONT);
        lblQuyen.setForeground(TienIch.MAU_XAM);
        TienIch.xepManHinh(this, TienIch.tieuDe("Tìm kiếm bệnh nhân"), TienIch.dong(txtTuKhoa, btnTim, chkNgung), lblKetQua,
                TienIch.cuonBang(tblBenhNhan, 340), TienIch.dong(btnHoSo, btnSua, btnThem, btnNgung, btnMoLai), lblQuyen);
        apDungQuyenSua();
        tim();
    }

    // Quay lại từ màn hình sửa hoặc thêm bệnh nhân
    @Override
    public void lamMoi() {
        apDungQuyenSua();
        tim();
    }

    // Chỉ Lễ tân và Quản lý được thêm
    private void apDungQuyenSua() {
        NhanVien nv = null;
        try {
            nv = PhienLamViec.getNhanVienHienTai();
        } catch (RuntimeException e) {
            nv = null; // Không biết ai đăng nhập
        }
        boolean khoa = nv != null && !HangSo.VAI_TRO_LE_TAN.equals(nv.getVaiTro()) && !HangSo.VAI_TRO_QUAN_LY.equals(nv.getVaiTro());
        btnSua.setEnabled(!khoa);
        btnThem.setEnabled(!khoa);
        btnNgung.setEnabled(!khoa);
        btnMoLai.setEnabled(!khoa);
        lblQuyen.setText(khoa ? nv.getVaiTro() + " chỉ xem, không được thêm hoặc sửa thông tin hành chính của bệnh nhân." : " ");
    }

    // Tìm theo từ khóa và hiện lên bảng
    private void tim() {
        try {
            dsBenhNhan = benhNhanService.timKiem(txtTuKhoa.getText(), chkNgung.isSelected());
            List<String> dsMa = new ArrayList<>();
            for (BenhNhan bn : dsBenhNhan) {
                dsMa.add(bn.getMaBN());
            }
            Map<String, NhanVien> nhaSi = nhanVienService.layNhaSiKhamGanNhat(dsMa); // Hỏi một lần cho cả bảng
            List<String[]> dong = new ArrayList<>();
            for (BenhNhan bn : dsBenhNhan) {
                String tenNhaSi = nhaSi.containsKey(bn.getMaBN()) ? nhaSi.get(bn.getMaBN()).getHoTen() : "Chưa khám";
                dong.add(new String[] {bn.getMaBN(), bn.getHoTen(), TienIch.ngay(bn.getNgaySinh()), TienIch.chu(bn.getGioiTinh()),
                    TienIch.chu(bn.getSdt()), TienIch.chu(bn.getDiaChi()), tenNhaSi, TienIch.chu(bn.getTrangThai())});
            }
            TienIch.napBang(tblBenhNhan, dong);
            lblKetQua.setText(dong.isEmpty() ? "Không tìm thấy bệnh nhân nào." : "Tìm thấy " + dong.size() + " bệnh nhân (tối đa 50).");
        } catch (RuntimeException e) {
            TienIch.baoLoi(e);
        }
    }

    // Bệnh nhân đang chọn trong bảng
    private BenhNhan dangChon() {
        int vt = tblBenhNhan.getSelectedRow();
        if (vt < 0 || vt >= dsBenhNhan.size()) {
            TienIch.thongTin("Vui lòng chọn một bệnh nhân trong bảng.");
            return null;
        }
        return dsBenhNhan.get(vt);
    }

    // Ngừng theo dõi hoặc mở lại
    private void doiTrangThai(String trangThaiMoi) {
        BenhNhan bn = dangChon();
        if (bn == null) {
            return;
        }
        if (!TienIch.xacNhan("Đổi trạng thái của " + bn.getHoTen() + " sang " + trangThaiMoi + "?")) {
            return;
        }
        try {
            benhNhanService.doiTrangThai(bn.getMaBN(), trangThaiMoi);
            tim();
        } catch (RuntimeException e) {
            TienIch.baoLoi(e);
        }
    }

    // Mở hồ sơ bệnh án của bệnh nhân đang chọn
    private void xemHoSo() {
        BenhNhan bn = dangChon();
        if (bn != null) {
            MainApp.doiNoiDung(new XemHoSoBenhAn(bn));
        }
    }
}
