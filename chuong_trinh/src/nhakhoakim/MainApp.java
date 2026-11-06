package nhakhoakim;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.Scrollable;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import controller.TaiKhoanController;
import dao.PhienLamViec;
import entity.NhanVien;
import ui.ChucNang;
import ui.DanhSachChucNang;
import ui.HopThoaiDangNhap;
import ui.ManHinhChiDinhTaiKham;
import ui.ManHinhChoThanhToan;
import ui.ManHinhDanhMucDichVu;
import ui.ManHinhDatLichHen;
import ui.ManHinhDoiHuyLichHen;
import ui.ManHinhDongThoiGian;
import ui.ManHinhGoiYDieuTri;
import ui.ManHinhHoSoBenhAn;
import ui.ManHinhHoSoBenhNhan;
import ui.ManHinhHoSoNhanVien;
import ui.ManHinhKhamDieuTri;
import ui.ManHinhLichLamViec;
import ui.ManHinhPhanCongCaTruc;
import ui.ManHinhSoDoRang;
import ui.ManHinhTaiKhoan;
import ui.ManHinhTaiKhoanCuaToi;
import ui.ManHinhThanhToan;
import ui.ManHinhTraCuuBenhNhan;
import ui.ManHinhXacNhanLichHen;
import ui.ManHinhXemLichHen;
import ui.TienIch;

@SuppressWarnings("serial") 
public class MainApp {

    // Màn hình nào cần nạp lại dữ liệu
    public interface LamMoiDuoc {

        void lamMoi();
    }

    private static class KhungCuon extends JPanel implements Scrollable {

        KhungCuon() {
            super(new BorderLayout());
            setBackground(TienIch.MAU_NEN);
        }

        @Override
        public Dimension getPreferredScrollableViewportSize() {
            return getPreferredSize();
        }

        @Override
        public int getScrollableUnitIncrement(Rectangle r, int huong, int chieu) {
            return 24;
        }

        @Override
        public int getScrollableBlockIncrement(Rectangle r, int huong, int chieu) {
            return 120;
        }

        @Override
        public boolean getScrollableTracksViewportWidth() {
            return true;
        }

        @Override
        public boolean getScrollableTracksViewportHeight() {
            return false;
        }
    }

    private static final float CO_CHU_MENU = 18f; // Cỡ chữ tên menu
    private static final float CO_CHU_MUC_MENU = 17f; // Cỡ chữ các mục trong menu

    private static MainApp dangChay; // Chương trình đang chạy
    private static boolean dangBaoLoi = false; // Đang hiện hộp báo lỗi chưa bắt

    private JFrame cuaSo; // Cửa sổ chính
    private JPanel noiDungChinh = new JPanel(new BorderLayout()); // Tiêu đề ở trên
    private JPanel vungGiua = new JPanel(new BorderLayout()); // Nơi đặt từng màn hình
    private JMenuBar thanhMenu = new JMenuBar();
    private JComponent manHinhHienTai; // Màn hình đang hiện ở giữa
    private Deque<JComponent> lichSuManHinh = new ArrayDeque<>(); // Các màn hình đã đi qua
    private Map<String, Supplier<JComponent>> manHinh = new HashMap<>(); // Tên chức năng -> hàm tạo

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (Exception e) {
                // Dùng giao diện mặc định
            }
            // Lỗi chưa bắt được thì hiện hộp báo lỗi
            Thread.setDefaultUncaughtExceptionHandler((luong, loi) -> {
                loi.printStackTrace();
                SwingUtilities.invokeLater(() -> baoLoiChuaBat(loi));
            });
            batDau();
        });
    }

    // Hiện hộp đăng nhập
    private static void batDau() {
        NhanVien nv = HopThoaiDangNhap.hien();
        if (nv == null) {
            System.exit(0);
            return;
        }
        PhienLamViec.datNhanVien(nv);
        new MainApp().hienCuaSo();
    }

    // Đăng xuất
    public static void dangXuat() {
        new TaiKhoanController().dangXuat();
        JFrame cu = dangChay == null ? null : dangChay.cuaSo;
        dangChay = null;
        SwingUtilities.invokeLater(() -> {
            if (cu != null) {
                cu.dispose();
            }
            batDau();
        });
    }

    // Cửa sổ chính đang chạy
    public static JFrame cuaSo() {
        return dangChay == null ? null : dangChay.cuaSo;
    }

    // Hiện lỗi chưa bắt
    private static void baoLoiChuaBat(Throwable loi) {
        if (dangBaoLoi) {
            return;
        }
        dangBaoLoi = true;
        try {
            TienIch.baoLoi(loi instanceof RuntimeException ? (RuntimeException) loi : new RuntimeException(loi));
        } finally {
            dangBaoLoi = false;
        }
    }

    // Dựng và hiện cửa sổ
    private void hienCuaSo() {
        taoNoiDung();
        cuaSo = new JFrame("Quản lý phòng khám Nha khoa Kim");
        cuaSo.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        cuaSo.setContentPane(noiDungChinh);
        cuaSo.setSize(1200, 760);
        cuaSo.setLocationRelativeTo(null);
        cuaSo.setVisible(true);
    }

    // Dựng giao diện
    JPanel taoNoiDung() {
        dangChay = this;
        dangKyManHinh();
        taoMenu();
        noiDungChinh.setBackground(TienIch.MAU_NEN);
        JPanel phanDau = new JPanel(new BorderLayout()); // Tiêu đề xanh
        phanDau.add(taoTieuDe(), BorderLayout.NORTH);
        phanDau.add(thanhMenu, BorderLayout.SOUTH);
        noiDungChinh.add(phanDau, BorderLayout.NORTH);
        vungGiua.setBackground(TienIch.MAU_NEN);
        noiDungChinh.add(vungGiua, BorderLayout.CENTER);
        hienTrangChu();
        return noiDungChinh;
    }

    // Thanh tiêu đề màu xanh phía trên cùng
    private JComponent taoTieuDe() {
        JPanel tieuDe = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setPaint(new GradientPaint(0, 0, TienIch.MAU_DAM, getWidth(), 0, TienIch.MAU_CHINH));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        tieuDe.setBorder(BorderFactory.createEmptyBorder(14, 24, 14, 24));
        JLabel lblTen = new JLabel("NHA KHOA KIM");
        lblTen.setForeground(Color.WHITE);
        lblTen.setFont(TienIch.FONT.deriveFont(Font.BOLD, 22f));
        JLabel lblMoTa = new JLabel("Chương trình quản lý hệ thống phòng khám");
        lblMoTa.setForeground(new Color(0xCFE9EC));
        lblMoTa.setFont(TienIch.FONT);
        JPanel trai = new JPanel(new GridLayout(2, 1, 0, 2));
        trai.setOpaque(false);
        trai.add(lblTen);
        trai.add(lblMoTa);
        tieuDe.add(trai, BorderLayout.WEST);
        NhanVien nv = PhienLamViec.getNhanVienHienTai();
        if (nv != null) {
            JLabel lblNguoi = new JLabel(nv.getHoTen() + " - " + nv.getVaiTro());
            lblNguoi.setForeground(Color.WHITE);
            lblNguoi.setFont(TienIch.FONT_DAM);
            JButton btnDangXuat = new JButton("<html><u>Đăng xuất</u></html>");
            btnDangXuat.setForeground(new Color(0xCFE9EC));
            btnDangXuat.setFont(TienIch.FONT);
            btnDangXuat.setBorderPainted(false);
            btnDangXuat.setContentAreaFilled(false);
            btnDangXuat.setFocusPainted(false);
            btnDangXuat.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btnDangXuat.addActionListener(e -> dangXuat());
            JPanel phai = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 10, 0));
            phai.setOpaque(false);
            phai.add(lblNguoi);
            phai.add(btnDangXuat);
            tieuDe.add(phai, BorderLayout.EAST);
        }
        tieuDe.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        tieuDe.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                hienTrangChu();
            }
        });
        return tieuDe;
    }

    // Thanh menu
    private void taoMenu() {
        thanhMenu.removeAll();
        thanhMenu.setBackground(Color.WHITE);
        thanhMenu.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, TienIch.MAU_VIEN), BorderFactory.createEmptyBorder(2, 18, 2, 18)));
        for (Map.Entry<String, List<ChucNang>> nhom : DanhSachChucNang.layTheoNhom().entrySet()) {
            JMenu menu = new JMenu(nhom.getKey());
            menu.setFont(TienIch.FONT.deriveFont(Font.BOLD, CO_CHU_MENU));
            menu.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
            for (ChucNang cn : nhom.getValue()) {
                if (!duocDung(cn)) {
                    continue; // Menu chỉ hiện đúng vai trò
                }
                JMenuItem muc = new JMenuItem(cn.getTen());
                muc.setFont(TienIch.FONT.deriveFont(CO_CHU_MUC_MENU));
                muc.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 24));
                muc.addActionListener(e -> hienChucNang(cn));
                menu.add(muc);
            }
            if (menu.getItemCount() > 0) {
                thanhMenu.add(menu);
            }
        }
    }

    // Người đang đăng nhập
    private boolean duocDung(ChucNang cn) {
        NhanVien nv = PhienLamViec.getNhanVienHienTai();
        return nv == null || java.util.Arrays.asList(cn.getVaiTro().split(",\\s*")).contains(nv.getVaiTro());
    }

    // Trang chủ
    void hienTrangChu() {
        lichSuManHinh.clear();
        JPanel trang = new JPanel();
        NhanVien nv = PhienLamViec.getNhanVienHienTai();
        TienIch.xepManHinh(trang, TienIch.tieuDe(nv == null ? "Xin chào" : "Xin chào, " + nv.getHoTen()),
                TienIch.doanChu("Chọn một mục trên thanh menu để bắt đầu.", TienIch.MAU_XAM));
        datGiua(trang);
    }

    // Đăng ký màn hình cho từng chức năng
    private void dangKyManHinh() {
        // Hậu: menu Bệnh nhân và menu Lượt khám
        manHinh.put("Tìm kiếm bệnh nhân", ManHinhTraCuuBenhNhan::new);
        manHinh.put("Thêm / cập nhật hồ sơ bệnh nhân", () -> new ManHinhHoSoBenhNhan(null));
        manHinh.put("Xem hồ sơ bệnh án", ManHinhHoSoBenhAn::new); // Ba chức năng dùng chung
        manHinh.put("Cập nhật tiền sử & dị ứng", ManHinhHoSoBenhAn::new);
        manHinh.put("Xem liệu trình đang thực hiện", ManHinhHoSoBenhAn::new);
        manHinh.put("Khám & ghi nhận điều trị", ManHinhKhamDieuTri::new); // Hai chức năng dùng chung
        manHinh.put("Kê đơn thuốc", ManHinhKhamDieuTri::new);
        manHinh.put("Sơ đồ răng điều trị", ManHinhSoDoRang::new);
        manHinh.put("Chỉ định tái khám", ManHinhChiDinhTaiKham::new);
        manHinh.put("Dòng thời gian điều trị", ManHinhDongThoiGian::new);
        manHinh.put("Gợi ý điều trị & ước tính chi phí", ManHinhGoiYDieuTri::new);

        // Trường: menu Lịch hẹn và menu Hóa đơn
        manHinh.put("Xem lịch hẹn", ManHinhXemLichHen::new);
        manHinh.put("Đặt lịch hẹn", ManHinhDatLichHen::new);
        manHinh.put("Dời / hủy lịch hẹn", ManHinhDoiHuyLichHen::new);
        manHinh.put("Xác nhận lịch hẹn", ManHinhXacNhanLichHen::new);
        manHinh.put("Xem lịch làm việc", ManHinhLichLamViec::new);
        manHinh.put("Danh sách chờ thanh toán", ManHinhChoThanhToan::new);
        manHinh.put("Lập hóa đơn", ManHinhChoThanhToan::new); // Chọn lượt khám chờ thanh toán
        manHinh.put("Thanh toán & in hóa đơn", ManHinhThanhToan::new);

        // Tâm: menu Dịch vụ, Nhân viên, Tài khoản
        manHinh.put("Xem danh mục dịch vụ & giá", ManHinhDanhMucDichVu::new);
        manHinh.put("Thêm / sửa dịch vụ", ManHinhDanhMucDichVu::new); // Quản lý thấy thêm nút Thêm
        manHinh.put("Cập nhật hồ sơ nhân viên", ManHinhHoSoNhanVien::new);
        manHinh.put("Phân công ca trực", ManHinhPhanCongCaTruc::new);
        manHinh.put("Đăng nhập / Đăng xuất", ManHinhTaiKhoanCuaToi::new);
        manHinh.put("Tạo tài khoản & gán vai trò", ManHinhTaiKhoan::new);
    }

    // Dựng màn hình của chức năng
    private JComponent taoManHinh(ChucNang cn) {
        Supplier<JComponent> tao = manHinh.get(cn.getTen());
        return tao == null ? null : tao.get();
    }

    // Hiện chức năng được chọn
    void hienChucNang(ChucNang cn) {
        JComponent noiDung = null;
        try {
            noiDung = taoManHinh(cn);
        } catch (RuntimeException e) {
            TienIch.baoLoi(e); // Dựng màn hình lỗi thì báo
        }
        if (noiDung == null) {
            noiDung = new JPanel();
            noiDung.setOpaque(false);
        }
        lichSuManHinh.clear(); // Chọn từ menu là bắt đầu
        datGiua(noiDung);
    }

    // Đặt một màn hình vào giữa cửa sổ
    private void datGiua(JComponent noiDung) {
        manHinhHienTai = noiDung;
        KhungCuon khung = new KhungCuon();
        khung.add(noiDung, BorderLayout.CENTER);
        if (!lichSuManHinh.isEmpty()) {
            JButton btnQuayLai = TienIch.nutPhu("‹ Quay lại");
            btnQuayLai.addActionListener(e -> quayLai());
            JPanel thanhTren = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 24, 14));
            thanhTren.setOpaque(false);
            thanhTren.add(btnQuayLai);
            khung.add(thanhTren, BorderLayout.NORTH);
        }
        JScrollPane cuon = new JScrollPane(khung);
        cuon.setBorder(null);
        cuon.getVerticalScrollBar().setUnitIncrement(24);
        vungGiua.removeAll();
        vungGiua.add(cuon, BorderLayout.CENTER);
        vungGiua.revalidate();
        vungGiua.repaint();
    }

    // Quay về màn hình trước đó
    private void quayLai() {
        if (lichSuManHinh.isEmpty()) {
            return;
        }
        JComponent truoc = lichSuManHinh.pop();
        if (truoc instanceof LamMoiDuoc) {
            ((LamMoiDuoc) truoc).lamMoi();
        }
        datGiua(truoc);
    }

    // Đi tiếp sang một màn hình khác
    public static void doiNoiDung(JComponent noiDung) {
        if (dangChay.manHinhHienTai != null) {
            dangChay.lichSuManHinh.push(dangChay.manHinhHienTai);
        }
        dangChay.datGiua(noiDung);
    }

    // Thay màn hình hiện tại bằng màn hình khác
    public static void thayNoiDung(JComponent noiDung) {
        dangChay.datGiua(noiDung);
    }

    // Mấy hàm cho test xem trạng thái

    // Màn hình đang hiện
    JComponent manHinhDangHien() {
        return manHinhHienTai;
    }

    // Có đang có nút Quay lại không
    boolean coQuayLai() {
        return !lichSuManHinh.isEmpty();
    }

    JMenuBar thanhMenu() {
        return thanhMenu;
    }

    // Dựng lại menu
    void lamMoiMenu() {
        taoMenu();
    }

    // Bấm nút Quay lại như người dùng
    void bamQuayLai() {
        quayLai();
    }

    Component vungNoiDung() {
        return vungGiua;
    }
}
