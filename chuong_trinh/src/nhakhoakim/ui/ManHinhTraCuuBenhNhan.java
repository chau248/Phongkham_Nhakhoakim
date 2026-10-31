package nhakhoakim.ui;

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
import nhakhoakim.controller.BenhNhanController;
import nhakhoakim.controller.NhaSiPhuTrachController;
import nhakhoakim.dao.PhienLamViec;
import nhakhoakim.entity.BenhNhan;
import nhakhoakim.entity.HangSo;
import nhakhoakim.entity.NhanVien;

// Màn hình tìm kiếm bệnh nhân theo mã, họ tên hoặc số điện thoại; từ kết quả mở hồ sơ bệnh án, sửa thông tin, thêm mới,
// ngừng theo dõi hoặc mở lại điều trị
@SuppressWarnings("serial") // lớp giao diện Swing, không cần serialVersionUID
public class ManHinhTraCuuBenhNhan extends JPanel implements MainApp.LamMoiDuoc {

    private BenhNhanController benhNhanService = new BenhNhanController();
    private NhaSiPhuTrachController nhaSiService = new NhaSiPhuTrachController();

    private JTextField txtTuKhoa = TienIch.oNhap(28);              // từ khóa tìm kiếm
    private JTable tblBenhNhan = TienIch.taoBang(new String[] {"Mã", "Họ tên", "Ngày sinh", "Giới tính", "Số điện thoại", "Địa chỉ", "Nha sĩ khám gần nhất", "Trạng thái"},
            new int[] {90, 180, 95, 75, 115, 200, 170, 110}, "Không có bệnh nhân nào."); // kết quả
    private JLabel lblKetQua = new JLabel();                       // số kết quả
    private List<BenhNhan> dsBenhNhan = new ArrayList<>();         // dữ liệu của bảng
    private JButton btnSua = TienIch.nutPhu("Sửa thông tin");
    private JButton btnThem = TienIch.nutPhu("Thêm bệnh nhân mới");
    private JLabel lblQuyen = new JLabel();                        // giải thích vì sao không sửa được
    private JCheckBox chkNgung = new JCheckBox("Gồm cả bệnh nhân ngừng theo dõi");
    private JButton btnNgung = TienIch.nutPhu("Ngừng theo dõi");
    private JButton btnMoLai = TienIch.nutPhu("Mở lại điều trị");

    public ManHinhTraCuuBenhNhan() {
        txtTuKhoa.setToolTipText("Nhập mã, họ tên hoặc số điện thoại");
        txtTuKhoa.addActionListener(e -> tim());
        JButton btnTim = TienIch.nutChinh("Tìm kiếm");
        btnTim.addActionListener(e -> tim());
        chkNgung.setOpaque(false);
        chkNgung.setFont(TienIch.FONT);
        chkNgung.addActionListener(e -> tim());

        // Bấm đúp chỉ mở hồ sơ khi bấm trúng một dòng có dữ liệu (bấm vào vùng trống của bảng thì bỏ qua)
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
                MainApp.doiNoiDung(new ManHinhHoSoBenhNhan(bn));
            }
        });
        btnThem.addActionListener(e -> MainApp.doiNoiDung(new ManHinhHoSoBenhNhan(null)));
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

    // Quay lại từ màn hình sửa hoặc thêm bệnh nhân thì tìm lại để bảng có dữ liệu mới
    @Override
    public void lamMoi() {
        apDungQuyenSua();
        tim();
    }

    // Chỉ Lễ tân và Quản lý được thêm, sửa thông tin hành chính (BN-05); vai trò khác thì khóa các nút và nói rõ lý do
    private void apDungQuyenSua() {
        NhanVien nv = null;
        try {
            nv = PhienLamViec.getNhanVienHienTai();
        } catch (RuntimeException e) {
            nv = null; // không biết ai đăng nhập thì để Service báo lỗi khi lưu
        }
        boolean khoa = nv != null && !HangSo.VAI_TRO_LE_TAN.equals(nv.getVaiTro()) && !HangSo.VAI_TRO_QUAN_LY.equals(nv.getVaiTro());
        btnSua.setEnabled(!khoa);
        btnThem.setEnabled(!khoa);
        btnNgung.setEnabled(!khoa);
        btnMoLai.setEnabled(!khoa);
        lblQuyen.setText(khoa ? nv.getVaiTro() + " chỉ xem, không được thêm hoặc sửa thông tin hành chính của bệnh nhân." : " ");
    }

    // Tìm theo từ khóa và hiện lên bảng (từ khóa rỗng thì hiện tối đa 50 bệnh nhân đầu)
    private void tim() {
        try {
            dsBenhNhan = benhNhanService.timKiem(txtTuKhoa.getText(), chkNgung.isSelected());
            List<String> dsMa = new ArrayList<>();
            for (BenhNhan bn : dsBenhNhan) {
                dsMa.add(bn.getMaBN());
            }
            Map<String, String> nhaSi = nhaSiService.layTenNhaSiKhamGanNhat(dsMa); // hỏi một lần cho cả bảng
            List<String[]> dong = new ArrayList<>();
            for (BenhNhan bn : dsBenhNhan) {
                String tenNhaSi = nhaSi.containsKey(bn.getMaBN()) ? nhaSi.get(bn.getMaBN()) : "Chưa khám";
                dong.add(new String[] {bn.getMaBN(), bn.getHoTen(), TienIch.ngay(bn.getNgaySinh()), TienIch.chu(bn.getGioiTinh()),
                    TienIch.chu(bn.getSdt()), TienIch.chu(bn.getDiaChi()), tenNhaSi, TienIch.chu(bn.getTrangThai())});
            }
            TienIch.napBang(tblBenhNhan, dong);
            lblKetQua.setText(dong.isEmpty() ? "Không tìm thấy bệnh nhân nào." : "Tìm thấy " + dong.size() + " bệnh nhân (tối đa 50).");
        } catch (RuntimeException e) {
            TienIch.baoLoi(e);
        }
    }

    // Bệnh nhân đang chọn trong bảng; chưa chọn thì nhắc và trả về null
    private BenhNhan dangChon() {
        int vt = tblBenhNhan.getSelectedRow();
        if (vt < 0 || vt >= dsBenhNhan.size()) {
            TienIch.thongTin("Vui lòng chọn một bệnh nhân trong bảng.");
            return null;
        }
        return dsBenhNhan.get(vt);
    }

    // Ngừng theo dõi hoặc mở lại bệnh nhân đang chọn (Service kiểm quyền và điều kiện)
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
            MainApp.doiNoiDung(new ManHinhHoSoBenhAn(bn));
        }
    }
}
