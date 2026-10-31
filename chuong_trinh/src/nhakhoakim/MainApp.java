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
import nhakhoakim.controller.TaiKhoanController;
import nhakhoakim.dao.PhienLamViec;
import nhakhoakim.entity.NhanVien;
import nhakhoakim.ui.ChucNang;
import nhakhoakim.ui.DanhSachChucNang;
import nhakhoakim.ui.HopThoaiDangNhap;
import nhakhoakim.ui.ManHinhChiDinhTaiKham;
import nhakhoakim.ui.ManHinhChoThanhToan;
import nhakhoakim.ui.ManHinhDanhMucDichVu;
import nhakhoakim.ui.ManHinhDatLichHen;
import nhakhoakim.ui.ManHinhDoiHuyLichHen;
import nhakhoakim.ui.ManHinhDongThoiGian;
import nhakhoakim.ui.ManHinhGoiYDieuTri;
import nhakhoakim.ui.ManHinhHoSoBenhAn;
import nhakhoakim.ui.ManHinhHoSoBenhNhan;
import nhakhoakim.ui.ManHinhHoSoNhanVien;
import nhakhoakim.ui.ManHinhKhamDieuTri;
import nhakhoakim.ui.ManHinhLichLamViec;
import nhakhoakim.ui.ManHinhPhanCongCaTruc;
import nhakhoakim.ui.ManHinhSoDoRang;
import nhakhoakim.ui.ManHinhTaiKhoan;
import nhakhoakim.ui.ManHinhTaiKhoanCuaToi;
import nhakhoakim.ui.ManHinhThanhToan;
import nhakhoakim.ui.ManHinhTraCuuBenhNhan;
import nhakhoakim.ui.ManHinhXacNhanLichHen;
import nhakhoakim.ui.ManHinhXemLichHen;
import nhakhoakim.ui.TienIch;

@SuppressWarnings("serial") 
public class MainApp {

    // Màn hình nào cần nạp lại dữ liệu khi người dùng bấm Quay lại về nó thì cài đặt interface này
    public interface LamMoiDuoc {

        void lamMoi();
    }

    // Khung chứa màn hình đặt trong thanh cuộn: luôn rộng bằng cửa sổ (không có thanh cuộn ngang), cao tới đâu cuộn dọc tới đó
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

    private static final float CO_CHU_MENU = 18f;      // cỡ chữ tên menu (Bệnh nhân, Lượt khám...), to để dễ nhìn khi mở rộng màn hình
    private static final float CO_CHU_MUC_MENU = 17f;  // cỡ chữ các mục trong menu

    private static MainApp dangChay; // chương trình đang chạy, để các màn hình chuyển sang màn hình khác
    private static boolean dangBaoLoi = false; // đang hiện hộp báo lỗi chưa bắt được, để không hiện chồng nhiều hộp

    private JFrame cuaSo;                              // cửa sổ chính (null khi chỉ dựng nội dung để kiểm thử)
    private JPanel noiDungChinh = new JPanel(new BorderLayout()); // tiêu đề ở trên, vùng màn hình ở giữa
    private JPanel vungGiua = new JPanel(new BorderLayout());      // nơi đặt từng màn hình
    private JMenuBar thanhMenu = new JMenuBar();
    private JComponent manHinhHienTai;                  // màn hình đang hiện ở giữa
    private Deque<JComponent> lichSuManHinh = new ArrayDeque<>(); // các màn hình đã đi qua, để nút Quay lại trở về
    private Map<String, Supplier<JComponent>> manHinh = new HashMap<>(); // tên chức năng -> hàm tạo màn hình của chức năng đó

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (Exception e) {
                // dùng giao diện mặc định nếu không đặt được
            }
            // Lỗi chưa bắt được (ví dụ mất kết nối CSDL khi dựng màn hình) thì hiện hộp báo lỗi thay vì im lặng
            Thread.setDefaultUncaughtExceptionHandler((luong, loi) -> {
                loi.printStackTrace();
                SwingUtilities.invokeLater(() -> baoLoiChuaBat(loi));
            });
            batDau();
        });
    }

    // Hiện hộp đăng nhập; đăng nhập xong mới mở cửa sổ chính (bấm Thoát ở hộp đăng nhập thì kết thúc chương trình)
    private static void batDau() {
        NhanVien nv = HopThoaiDangNhap.hien();
        if (nv == null) {
            System.exit(0);
            return;
        }
        PhienLamViec.datNhanVien(nv);
        new MainApp().hienCuaSo();
    }

    // Đăng xuất (TK-02): ghi nhật ký, xóa người dùng hiện tại, đóng cửa sổ chính rồi hiện lại hộp đăng nhập
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

    // Cửa sổ chính đang chạy (để hộp thoại biết hiện ở giữa cửa sổ nào); chưa có thì null
    public static JFrame cuaSo() {
        return dangChay == null ? null : dangChay.cuaSo;
    }

    // Hiện lỗi chưa bắt được bằng hộp thông báo; đang hiện một hộp rồi thì bỏ qua lỗi tiếp theo để không chồng hộp
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

    // Dựng giao diện: tiêu đề, menu và trang chủ (tách riêng khỏi cửa sổ để có thể kiểm thử không cần màn hình)
    JPanel taoNoiDung() {
        dangChay = this;
        dangKyManHinh();
        taoMenu();
        noiDungChinh.setBackground(TienIch.MAU_NEN);
        JPanel phanDau = new JPanel(new BorderLayout()); // tiêu đề xanh, bên dưới là thanh menu
        phanDau.add(taoTieuDe(), BorderLayout.NORTH);
        phanDau.add(thanhMenu, BorderLayout.SOUTH);
        noiDungChinh.add(phanDau, BorderLayout.NORTH);
        vungGiua.setBackground(TienIch.MAU_NEN);
        noiDungChinh.add(vungGiua, BorderLayout.CENTER);
        hienTrangChu();
        return noiDungChinh;
    }

    // Thanh tiêu đề màu xanh phía trên cùng; bấm vào để về trang chủ. Bên phải hiện người đang đăng nhập và nút Đăng xuất.
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

    // Thanh menu: mỗi nhóm đối tượng là một menu, mỗi chức năng là một mục
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
                    continue; // menu và chức năng chỉ hiện đúng vai trò (CH-01)
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

    // Người đang đăng nhập có được dùng chức năng này không (theo cột vai trò dùng); chưa đăng nhập (chạy kiểm thử) thì cho dùng hết
    private boolean duocDung(ChucNang cn) {
        NhanVien nv = PhienLamViec.getNhanVienHienTai();
        return nv == null || java.util.Arrays.asList(cn.getVaiTro().split(",\\s*")).contains(nv.getVaiTro());
    }

    // Trang chủ: chỉ có một dòng gợi ý, các chức năng chọn ở thanh menu phía trên
    void hienTrangChu() {
        lichSuManHinh.clear();
        JPanel trang = new JPanel();
        NhanVien nv = PhienLamViec.getNhanVienHienTai();
        TienIch.xepManHinh(trang, TienIch.tieuDe(nv == null ? "Xin chào" : "Xin chào, " + nv.getHoTen()),
                TienIch.doanChu("Chọn một mục trên thanh menu để bắt đầu.", TienIch.MAU_XAM));
        datGiua(trang);
    }

    // Đăng ký màn hình cho từng chức năng: mỗi dòng là manHinh.put("Tên chức năng", ManHinhX::new).
    // Chức năng chưa đăng ký thì vùng nội dung để trống.
    private void dangKyManHinh() {
        // ----- Hậu: menu Bệnh nhân và menu Lượt khám
        manHinh.put("Tìm kiếm bệnh nhân", ManHinhTraCuuBenhNhan::new);
        manHinh.put("Thêm / cập nhật hồ sơ bệnh nhân", () -> new ManHinhHoSoBenhNhan(null));
        manHinh.put("Xem hồ sơ bệnh án", ManHinhHoSoBenhAn::new);            // ba chức năng dùng chung một màn hình
        manHinh.put("Cập nhật tiền sử & dị ứng", ManHinhHoSoBenhAn::new);
        manHinh.put("Xem liệu trình đang thực hiện", ManHinhHoSoBenhAn::new);
        manHinh.put("Khám & ghi nhận điều trị", ManHinhKhamDieuTri::new);    // hai chức năng dùng chung một màn hình
        manHinh.put("Kê đơn thuốc", ManHinhKhamDieuTri::new);
        manHinh.put("Sơ đồ răng điều trị", ManHinhSoDoRang::new);
        manHinh.put("Chỉ định tái khám", ManHinhChiDinhTaiKham::new);
        manHinh.put("Dòng thời gian điều trị", ManHinhDongThoiGian::new);
        manHinh.put("Gợi ý điều trị & ước tính chi phí", ManHinhGoiYDieuTri::new);

        // ----- Trường: menu Lịch hẹn và menu Hóa đơn
        manHinh.put("Xem lịch hẹn", ManHinhXemLichHen::new);
        manHinh.put("Đặt lịch hẹn", ManHinhDatLichHen::new);
        manHinh.put("Dời / hủy lịch hẹn", ManHinhDoiHuyLichHen::new);
        manHinh.put("Xác nhận lịch hẹn", ManHinhXacNhanLichHen::new);
        manHinh.put("Xem lịch làm việc", ManHinhLichLamViec::new);
        manHinh.put("Danh sách chờ thanh toán", ManHinhChoThanhToan::new);
        manHinh.put("Lập hóa đơn", ManHinhChoThanhToan::new);                // chọn lượt khám chờ thanh toán rồi lập hóa đơn
        manHinh.put("Thanh toán & in hóa đơn", ManHinhThanhToan::new);

        // ----- Tâm: menu Dịch vụ, Nhân viên, Tài khoản
        manHinh.put("Xem danh mục dịch vụ & giá", ManHinhDanhMucDichVu::new);
        manHinh.put("Thêm / sửa dịch vụ", ManHinhDanhMucDichVu::new);        // Quản lý thấy thêm nút Thêm và Sửa trên cùng màn hình
        manHinh.put("Cập nhật hồ sơ nhân viên", ManHinhHoSoNhanVien::new);
        manHinh.put("Phân công ca trực", ManHinhPhanCongCaTruc::new);
        manHinh.put("Đăng nhập / Đăng xuất", ManHinhTaiKhoanCuaToi::new);
        manHinh.put("Tạo tài khoản & gán vai trò", ManHinhTaiKhoan::new);
    }

    // Dựng màn hình của chức năng; chưa đăng ký màn hình thì trả về null
    private JComponent taoManHinh(ChucNang cn) {
        Supplier<JComponent> tao = manHinh.get(cn.getTen());
        return tao == null ? null : tao.get();
    }

    // Hiện chức năng được chọn: có màn hình thật thì hiện, chưa làm thì để trống
    void hienChucNang(ChucNang cn) {
        JComponent noiDung = null;
        try {
            noiDung = taoManHinh(cn);
        } catch (RuntimeException e) {
            TienIch.baoLoi(e); // dựng màn hình lỗi (ví dụ mất kết nối CSDL) thì báo và để vùng giữa trống
        }
        if (noiDung == null) {
            noiDung = new JPanel();
            noiDung.setOpaque(false);
        }
        lichSuManHinh.clear(); // chọn từ menu là bắt đầu một chuỗi màn hình mới
        datGiua(noiDung);
    }

    // Đặt một màn hình vào giữa cửa sổ, có thanh cuộn khi màn hình dài hơn cửa sổ;
    // nếu đây là màn hình đi tiếp từ một màn hình khác thì có thêm nút Quay lại ở trên cùng
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

    // Quay về màn hình trước đó (nếu màn hình đó có hàm lamMoi thì nạp lại dữ liệu cho đúng)
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

    // Đi tiếp sang một màn hình khác và nhớ màn hình hiện tại để nút Quay lại trở về
    public static void doiNoiDung(JComponent noiDung) {
        if (dangChay.manHinhHienTai != null) {
            dangChay.lichSuManHinh.push(dangChay.manHinhHienTai);
        }
        dangChay.datGiua(noiDung);
    }

    // Thay màn hình hiện tại bằng màn hình khác, không nhớ màn hình cũ (dùng khi màn hình cũ đã xong việc, ví dụ vừa thêm xong bệnh nhân)
    public static void thayNoiDung(JComponent noiDung) {
        dangChay.datGiua(noiDung);
    }

    // ----- Phần dưới đây chỉ để chương trình kiểm thử xem được trạng thái hiện tại -----

    // Màn hình đang hiện (không tính khung bao ngoài)
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

    // Dựng lại menu theo vai trò của người đang đăng nhập (kiểm thử gọi sau khi đổi người đăng nhập)
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
