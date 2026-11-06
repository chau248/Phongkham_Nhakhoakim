package ui;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import controller.CauHinhController;
import controller.LieuTrinhController;
import controller.PhienLamViec;
import entity.BenhNhan;
import entity.DanhMuc;
import entity.HangSo;
import entity.LieuTrinh;
import entity.LuotKham;
import entity.NhanVien;
import nhakhoakim.MainApp;

// Liệu trình điều trị của một bệnh nhân: xem tiến độ từng buổi
@SuppressWarnings("serial") // Lớp giao diện Swing
public class QuanLyLieuTrinh extends JPanel implements MainApp.LamMoiDuoc {

    private LieuTrinhController lieuTrinhController = new LieuTrinhController();

    private NhanVien nguoiDung = PhienLamViec.getNhanVienHienTai();
    private boolean duocSua = nguoiDung != null && nguoiDung.laVaiTro(HangSo.VAI_TRO_NHA_SI, HangSo.VAI_TRO_QUAN_LY);
    private boolean duocTao = nguoiDung != null && nguoiDung.laVaiTro(HangSo.VAI_TRO_NHA_SI);

    private TienIch.ChonBenhNhan chonBenhNhan = new TienIch.ChonBenhNhan();
    private JTable tblLieuTrinh = TienIch.taoBang(new String[] {"Mã", "Loại", "Đã khám / dự kiến", "Tiến độ hiện tại", "Bắt đầu", "Trạng thái", "Nha sĩ"},
            new int[] {80, 90, 110, 260, 90, 100, 150}, "Chọn bệnh nhân rồi bấm Xem. Bệnh nhân chưa có liệu trình nào.");
    private JTable tblBuoi = TienIch.taoBang(new String[] {"Buổi", "Ngày", "Nha sĩ", "Chẩn đoán / việc đã làm", "Tình trạng"}, new int[] {50, 120, 150, 330, 100},
            "Chọn một liệu trình để xem các buổi.");
    private JLabel lblChiTiet = new JLabel(" ");
    private List<LieuTrinh> dsLieuTrinh = new ArrayList<>();
    private LieuTrinh dangChon;
    private List<LuotKham> dsBuoi = new ArrayList<>(); // Các buổi của liệu trình đang chọn
    private JButton btnThemBuoi = TienIch.nutChinh("Thêm buổi đã khám");
    private JButton btnBoBuoi = TienIch.nutPhu("Bỏ buổi đang chọn");
    private BenhNhan benhNhan;

    private JTextField txtGiaiDoan = TienIch.oNhap(26);
    private JTextField txtGhiChu = TienIch.oNhap(26);
    private JButton btnCapNhat = TienIch.nutChinh("Lưu tiến độ");
    private JButton btnHoanThanh = TienIch.nutPhu("Kết thúc liệu trình");
    private JButton btnHuy = TienIch.nutDo("Hủy liệu trình");

    private JComboBox<String> cmbLoai = new JComboBox<>(new CauHinhController().layDanhMuc(DanhMuc.LOAI_DICH_VU).toArray(new String[0]));
    private JTextField txtSoBuoi = TienIch.oNhap(5);

    public QuanLyLieuTrinh() {
        this(null);
    }

    // Mở sẵn liệu trình của một bệnh nhân
    public QuanLyLieuTrinh(BenhNhan bn) {
        JButton btnXem = TienIch.nutChinh("Xem");
        btnXem.addActionListener(e -> xem());
        chonBenhNhan.addChonListener(e -> xoaKetQua());
        cmbLoai.setFont(TienIch.FONT);
        cmbLoai.setSelectedIndex(-1);
        lblChiTiet.setFont(TienIch.FONT);
        tblLieuTrinh.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                chonDong();
            }
        });
        btnThemBuoi.addActionListener(e -> themBuoi());
        btnBoBuoi.addActionListener(e -> boBuoi());
        btnCapNhat.addActionListener(e -> capNhat());
        btnHoanThanh.addActionListener(e -> hoanThanh());
        btnHuy.addActionListener(e -> huy());
        JButton btnTao = TienIch.nutChinh("Tạo liệu trình");
        btnTao.addActionListener(e -> tao());

        JPanel khuSua = TienIch.the(TienIch.chuThuong("Liệu trình đang chọn", true),
                TienIch.dong(TienIch.nhan("Tiến độ hiện tại"), txtGiaiDoan, TienIch.nhan("Ghi chú"), txtGhiChu),
                TienIch.dong(btnCapNhat, btnHoanThanh, btnHuy), TienIch.dong(btnThemBuoi, btnBoBuoi));
        khuSua.setVisible(duocSua);
        JPanel khuTao = TienIch.the(TienIch.chuThuong("Tạo liệu trình mới cho bệnh nhân đã chọn", true),
                TienIch.dong(TienIch.nhan("Loại"), cmbLoai, TienIch.nhan("Số buổi dự kiến"), txtSoBuoi, btnTao));
        khuTao.setVisible(duocTao);
        TienIch.xepManHinh(this, TienIch.tieuDe("Liệu trình điều trị"),
                TienIch.doanChu("Liệu trình là điều trị nhiều buổi (niềng răng, implant, nội nha...). Tạo liệu trình với loại và số buổi dự kiến; mỗi lần khám, "
                        + "nha sĩ chọn liệu trình ở màn hình Khám để tính một buổi. Đủ buổi thì kết thúc liệu trình.", TienIch.MAU_XAM),
                TienIch.dong(TienIch.nhan("Bệnh nhân"), chonBenhNhan, btnXem),
                TienIch.cuonBang(tblLieuTrinh, 150), lblChiTiet, TienIch.nhan("Các buổi đã điều trị"), TienIch.cuonBang(tblBuoi, 150), khuSua, khuTao);
        if (bn != null) {
            chonBenhNhan.setBenhNhan(bn);
            xem();
        }
    }

    @Override
    public void lamMoi() {
        if (benhNhan != null) {
            taiLieuTrinh();
        }
    }

    private void xoaKetQua() {
        benhNhan = null;
        dangChon = null;
        dsLieuTrinh = new ArrayList<>();
        TienIch.napBang(tblLieuTrinh, new ArrayList<>());
        TienIch.napBang(tblBuoi, new ArrayList<>());
        lblChiTiet.setText(" ");
    }

    private void xem() {
        BenhNhan bn = chonBenhNhan.getBenhNhan();
        if (bn == null) {
            TienIch.thongTin("Vui lòng chọn một bệnh nhân.");
            return;
        }
        benhNhan = bn;
        taiLieuTrinh();
    }

    // Nạp danh sách liệu trình của bệnh nhân
    private void taiLieuTrinh() {
        try {
            String maDangChon = dangChon == null ? null : dangChon.getMaLieuTrinh();
            dsLieuTrinh = lieuTrinhController.layTheoBenhNhan(benhNhan.getMaBN());
            List<String[]> dong = new ArrayList<>();
            int chon = -1;
            for (int i = 0; i < dsLieuTrinh.size(); i++) {
                LieuTrinh lt = dsLieuTrinh.get(i);
                if (lt.getMaLieuTrinh().equals(maDangChon)) {
                    chon = i;
                }
                dong.add(new String[] {lt.getMaLieuTrinh(), lt.getLoaiDichVu(), lieuTrinhController.demBuoiDaKham(lt.getMaLieuTrinh()) + "/" + lt.getSoBuoiDuKien(),
                    lt.getGiaiDoanHienTai(), TienIch.ngay(lt.getNgayBatDau()), lt.getTrangThai(),
                    lt.getMaNhaSiPhuTrach() == null ? "" : lt.getMaNhaSiPhuTrach().getHoTen()});
            }
            TienIch.napBang(tblLieuTrinh, dong);
            dangChon = null;
            if (chon >= 0) {
                tblLieuTrinh.setRowSelectionInterval(chon, chon);
            } else if (!dsLieuTrinh.isEmpty()) {
                tblLieuTrinh.setRowSelectionInterval(0, 0);
            } else {
                chonDong();
            }
        } catch (RuntimeException e) {
            TienIch.baoLoi(e);
        }
    }

    // Hiện chi tiết và các buổi của liệu trình đang chọn
    private void chonDong() {
        int vt = tblLieuTrinh.getSelectedRow();
        dangChon = vt < 0 || vt >= dsLieuTrinh.size() ? null : dsLieuTrinh.get(vt);
        List<String[]> dong = new ArrayList<>();
        if (dangChon == null) {
            lblChiTiet.setText(benhNhan == null ? " " : "Bệnh nhân " + benhNhan.getHoTen() + " chưa có liệu trình nào.");
            TienIch.napBang(tblBuoi, dong);
        } else {
            int daKham = lieuTrinhController.demBuoiDaKham(dangChon.getMaLieuTrinh());
            boolean duBuoi = daKham >= dangChon.getSoBuoiDuKien() && HangSo.LIEU_TRINH_DANG_THUC_HIEN.equals(dangChon.getTrangThai());
            lblChiTiet.setText("<html><b>" + dangChon.getMaLieuTrinh() + " - " + dangChon.getLoaiDichVu() + "</b>: đã khám " + daKham + "/" + dangChon.getSoBuoiDuKien()
                    + " buổi" + (duBuoi ? " - <b>đã đủ buổi, có thể kết thúc liệu trình</b>" : "")
                    + (dangChon.getGhiChu() == null ? "" : "<br>Ghi chú: " + dangChon.getGhiChu()) + "</html>");
            int buoi = 1;
            dsBuoi = lieuTrinhController.layCacBuoi(dangChon.getMaLieuTrinh());
            for (LuotKham lk : dsBuoi) {
                dong.add(new String[] {String.valueOf(buoi++), TienIch.ngayGio(lk.getGioBatDau()), lk.getMaNhanVienPhuTrach().getHoTen(),
                    TienIch.chu(lk.getChanDoan() != null ? lk.getChanDoan() : lk.getLyDoKham()), lk.getTrangThai()});
            }
            TienIch.napBang(tblBuoi, dong);
            txtGiaiDoan.setText(dangChon.getGiaiDoanHienTai());
            txtGhiChu.setText(TienIch.chu(dangChon.getGhiChu()));
        }
        boolean dangLam = dangChon != null && HangSo.LIEU_TRINH_DANG_THUC_HIEN.equals(dangChon.getTrangThai());
        btnCapNhat.setEnabled(dangLam);
        btnHoanThanh.setEnabled(dangLam);
        btnHuy.setEnabled(dangLam);
        btnThemBuoi.setEnabled(dangLam);
        btnBoBuoi.setEnabled(dangLam);
    }

    // Chọn một lượt khám đã hoàn thành của bệnh nhân
    private void themBuoi() {
        try {
            List<LuotKham> ds = lieuTrinhController.layLuotKhamChuaThuocLieuTrinh(benhNhan.getMaBN());
            if (ds.isEmpty()) {
                TienIch.thongTin("Bệnh nhân không còn lượt khám đã hoàn thành nào chưa thuộc liệu trình. Lượt đang khám thì chọn liệu trình ngay ở màn hình Khám.");
                return;
            }
            javax.swing.JComboBox<String> cmb = new javax.swing.JComboBox<>();
            for (LuotKham lk : ds) {
                cmb.addItem(TienIch.ngayGio(lk.getGioBatDau()) + " - " + lk.getMaNhanVienPhuTrach().getHoTen() + " - " + TienIch.chu(lk.getChanDoan()));
            }
            int kq = javax.swing.JOptionPane.showConfirmDialog(MainApp.cuaSo(), cmb, "Chọn lượt khám làm một buổi của " + dangChon.getMaLieuTrinh(),
                    javax.swing.JOptionPane.OK_CANCEL_OPTION, javax.swing.JOptionPane.QUESTION_MESSAGE);
            if (kq == javax.swing.JOptionPane.OK_OPTION && cmb.getSelectedIndex() >= 0) {
                lieuTrinhController.themBuoi(dangChon.getMaLieuTrinh(), ds.get(cmb.getSelectedIndex()).getMaLuotKham());
                taiLieuTrinh();
            }
        } catch (RuntimeException e) {
            TienIch.baoLoi(e);
        }
    }

    private void boBuoi() {
        int vt = tblBuoi.getSelectedRow();
        if (vt < 0 || vt >= dsBuoi.size()) {
            TienIch.thongTin("Chọn một buổi trong bảng các buổi đã điều trị.");
            return;
        }
        try {
            lieuTrinhController.boBuoi(dangChon.getMaLieuTrinh(), dsBuoi.get(vt).getMaLuotKham());
            taiLieuTrinh();
        } catch (RuntimeException e) {
            TienIch.baoLoi(e);
        }
    }

    private void capNhat() {
        try {
            lieuTrinhController.capNhatGiaiDoan(dangChon.getMaLieuTrinh(), txtGiaiDoan.getText(), txtGhiChu.getText());
            taiLieuTrinh();
        } catch (RuntimeException e) {
            TienIch.baoLoi(e);
        }
    }

    private void hoanThanh() {
        if (!TienIch.xacNhan("Kết thúc liệu trình " + dangChon.getMaLieuTrinh() + "? Sau đó không gắn thêm buổi khám vào liệu trình này.")) {
            return;
        }
        try {
            lieuTrinhController.hoanThanh(dangChon.getMaLieuTrinh());
            taiLieuTrinh();
        } catch (RuntimeException e) {
            TienIch.baoLoi(e);
        }
    }

    private void huy() {
        String lyDo = TienIch.nhapChu("Hủy liệu trình", "Lý do hủy liệu trình " + dangChon.getMaLieuTrinh() + ":");
        if (lyDo == null) {
            return;
        }
        try {
            lieuTrinhController.huy(dangChon.getMaLieuTrinh(), lyDo);
            taiLieuTrinh();
        } catch (RuntimeException e) {
            TienIch.baoLoi(e);
        }
    }

    private void tao() {
        if (benhNhan == null) {
            TienIch.thongTin("Chọn bệnh nhân rồi bấm Xem trước khi tạo liệu trình.");
            return;
        }
        try {
            int soBuoi = Integer.parseInt(txtSoBuoi.getText().trim());
            LieuTrinh lt = lieuTrinhController.tao(benhNhan.getMaBN(), (String) cmbLoai.getSelectedItem(), soBuoi, null, null, 0, null);
            TienIch.thongTin("Đã tạo liệu trình " + lt.getMaLieuTrinh() + ". Mỗi lần khám nhớ chọn liệu trình này ở màn hình Khám.");
            txtSoBuoi.setText("");
            dangChon = lt;
            taiLieuTrinh();
        } catch (NumberFormatException e) {
            TienIch.loi("Số buổi dự kiến phải là số nguyên.");
        } catch (RuntimeException e) {
            TienIch.baoLoi(e);
        }
    }
}
