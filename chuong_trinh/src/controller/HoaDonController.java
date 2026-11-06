package controller;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import dao.HoaDonDAO;
import dao.LuotKhamDAO;
import dao.NghiepVuException;
import dao.RangDieuTriDAO;
import dao.TienIchDAO;
import entity.BenhNhan;
import entity.ChiTietHoaDon;
import entity.DichVu;
import entity.HoaDon;
import entity.LuotKham;
import entity.LuotKhamDichVu;
import entity.NhanVien;
import entity.RangDieuTri;
import entity.HangSo;

// Nghiệp vụ hóa đơn: danh sách chờ thanh toán
public class HoaDonController {

    public static final String CHUA_THANH_TOAN = "Chưa thanh toán";
    public static final String DA_THANH_TOAN = "Đã thanh toán";
    public static final List<String> HINH_THUC = List.of("Tiền mặt", "Chuyển khoản", "Thẻ", "Ví điện tử");

    // Một lượt khám đã hoàn tất đang chờ lập hóa đơn
    public static class DongChoThanhToan {
        public LuotKham luotKham;
        public String maBN;
        public String tenBenhNhan;
        public String tenNhaSi;
        public BigDecimal tamTinh; // tổng tiền đã gồm VAT
    }

    private CauHinhController cauHinhController = new CauHinhController();
    private HoaDonDAO hoaDonDAO = new HoaDonDAO();
    private LuotKhamDAO luotKhamDAO = new LuotKhamDAO();
    private RangDieuTriDAO rangDAO = new RangDieuTriDAO();
    private Supplier<NhanVien> nguoiDung = PhienLamViec::getNhanVienHienTai;

    // Các lượt khám Hoàn thành chưa có hóa đơn
    public List<DongChoThanhToan> layChoThanhToan() {
        TienIchController.yeuCau(nguoiDung, "xem danh sách chờ thanh toán", HangSo.VAI_TRO_LE_TAN, HangSo.VAI_TRO_QUAN_LY);
        List<DongChoThanhToan> kq = new ArrayList<>();
        for (LuotKham lk : luotKhamDAO.layChoThanhToan()) {
            BenhNhan bn = lk.getMaHoSo().getMaBN();
            DongChoThanhToan d = new DongChoThanhToan();
            d.luotKham = lk;
            d.maBN = bn.getMaBN();
            d.tenBenhNhan = bn.getHoTen();
            d.tenNhaSi = lk.getMaNhanVienPhuTrach().getHoTen();
            HoaDon tam = new HoaDon(); // Hóa đơn tạm chỉ để tính tiền
            tam.setDsChiTiet(tinhDongChiTiet(lk));
            tam.setTienGiamGia(BigDecimal.ZERO);
            tam.setPhanTramThue(layThueVAT());
            d.tamTinh = tam.getTongTien();
            kq.add(d);
        }
        return kq;
    }

    // Tất cả hóa đơn
    public List<HoaDon> layTatCa() {
        TienIchController.yeuCau(nguoiDung, "xem hóa đơn", HangSo.VAI_TRO_LE_TAN, HangSo.VAI_TRO_QUAN_LY);
        return hoaDonDAO.layTatCa();
    }

    // Hóa đơn kèm các dòng chi tiết
    public HoaDon timTheoMa(String maHoaDon) {
        HoaDon hd = maHoaDon == null ? null : hoaDonDAO.timTheoMa(maHoaDon);
        if (hd == null) {
            throw new NghiepVuException("Chưa chọn hóa đơn");
        }
        hd.setDsChiTiet(hoaDonDAO.layChiTiet(maHoaDon));
        return hd;
    }

    // Hóa đơn tạm tính của một lượt khám
    public HoaDon xemTruoc(String maLuotKham, BigDecimal tienGiamGia) {
        return dungHoaDon(layLuotKhamLapDuoc(maLuotKham), "(chưa lập)", tienGiamGia, null);
    }

    // Lập hóa đơn cho lượt khám đã hoàn thành
    public HoaDon lapHoaDon(String maLuotKham, BigDecimal tienGiamGia, String ghiChu) {
        NhanVien nguoi = TienIchController.yeuCau(nguoiDung, "lập hóa đơn", HangSo.VAI_TRO_LE_TAN, HangSo.VAI_TRO_QUAN_LY);
        LuotKham lk = layLuotKhamLapDuoc(maLuotKham);
        String ghiChuGon = TienIchController.rongThanhNull(ghiChu);
        HoaDon hd = dungHoaDon(lk, TienIchDAO.taoMa("HD"), tienGiamGia, nguoi);
        hd.setGhiChu(ghiChuGon);
        for (ChiTietHoaDon ct : hd.getDsChiTiet()) {
            ct.setMaHoaDon(hd);
            ct.setMaChiTiet(TienIchDAO.taoMa("CT"));
        }
        hoaDonDAO.themKemChiTiet(hd, hd.getDsChiTiet(), maLuotKham);
        TienIchController.ghiNhatKy(nguoi.getMaNV(), "Lập hóa đơn", "HoaDon", hd.getMaHoaDon(), "Lượt khám " + maLuotKham + ", tổng " + hd.getTongTien());
        return hd;
    }

    // Thanh toán hóa đơn Chưa thanh toán bằng một trong bốn hình thức
    public void thanhToan(String maHoaDon, String hinhThuc) {
        NhanVien nguoi = TienIchController.yeuCau(nguoiDung, "thanh toán hóa đơn", HangSo.VAI_TRO_LE_TAN, HangSo.VAI_TRO_QUAN_LY);
        HoaDon hd = timTheoMa(maHoaDon);
        if (!CHUA_THANH_TOAN.equals(hd.getTrangThai())) {
            throw new NghiepVuException("Chỉ thanh toán được hóa đơn Chưa thanh toán (hóa đơn này đang \"" + hd.getTrangThai() + "\")");
        }
        if (hinhThuc == null || !HINH_THUC.contains(hinhThuc)) {
            throw new NghiepVuException("Hình thức thanh toán phải là Tiền mặt, Chuyển khoản, Thẻ hoặc Ví điện tử");
        }
        if (!hoaDonDAO.thanhToan(maHoaDon, hinhThuc, LocalDateTime.now(), nguoi.getMaNV())) {
            throw new NghiepVuException("Hóa đơn vừa được xử lý ở máy khác, hãy tải lại danh sách");
        }
        TienIchController.ghiNhatKy(nguoi.getMaNV(), "Thanh toán hóa đơn", "HoaDon", maHoaDon, hinhThuc + ", " + hd.getTongTien());
    }

    // Hủy hóa đơn chưa thanh toán
    public void huyHoaDon(String maHoaDon, String lyDo) {
        NhanVien nguoi = TienIchController.yeuCau(nguoiDung, "hủy hóa đơn", HangSo.VAI_TRO_QUAN_LY);
        String lyDoHuy = TienIchController.rongThanhNull(lyDo);
        if (lyDoHuy == null || lyDoHuy.length() < 5) {
            throw new NghiepVuException("Phải nhập lý do hủy hóa đơn (ít nhất 5 ký tự)");
        }
        HoaDon hd = timTheoMa(maHoaDon);
        if (!CHUA_THANH_TOAN.equals(hd.getTrangThai())) {
            throw new NghiepVuException("Chỉ hủy được hóa đơn Chưa thanh toán (hóa đơn này đang \"" + hd.getTrangThai() + "\")");
        }
        String ghiChu = "Hủy: " + lyDoHuy;
        if (ghiChu.length() > 255) {
            ghiChu = ghiChu.substring(0, 255);
        }
        if (!hoaDonDAO.huy(maHoaDon, ghiChu)) {
            throw new NghiepVuException("Hóa đơn vừa được xử lý ở máy khác, hãy tải lại danh sách");
        }
        TienIchController.ghiNhatKy(nguoi.getMaNV(), "Hủy hóa đơn", "HoaDon", maHoaDon, lyDoHuy);
    }

    private static final int DO_RONG = 78; // Số ký tự mỗi dòng của hóa đơn in

    // Nội dung hóa đơn dạng chữ
    public String taoNoiDungIn(String maHoaDon) {
        TienIchController.yeuCau(nguoiDung, "in hóa đơn", HangSo.VAI_TRO_LE_TAN, HangSo.VAI_TRO_QUAN_LY);
        HoaDon hd = timTheoMa(maHoaDon);
        if (HangSo.DA_HUY.equals(hd.getTrangThai())) {
            throw new NghiepVuException("Hóa đơn đã hủy không được in");
        }
        if (!DA_THANH_TOAN.equals(hd.getTrangThai())) {
            throw new NghiepVuException("Chỉ in hóa đơn sau khi đã thanh toán");
        }
        BenhNhan bn = hd.getMaBenhNhan();
        NhanVien nguoiLap = hd.getMaNhanVien();
        LocalDateTime ngayLap = hd.getNgayLap();
        String duongKe = "-".repeat(DO_RONG) + "\n";
        StringBuilder sb = new StringBuilder();

        // Đầu hóa đơn: thông tin phòng khám
        sb.append(giua(cauHinhController.layThamSo(CauHinhController.TEN_PHONG_KHAM).toUpperCase())).append('\n');
        themDongNeuCo(sb, "Địa chỉ", cauHinhController.layThamSo(CauHinhController.DIA_CHI));
        themDongNeuCo(sb, "Điện thoại", cauHinhController.layThamSo(CauHinhController.SO_DIEN_THOAI));
        themDongNeuCo(sb, "Mã số thuế", cauHinhController.layThamSo(CauHinhController.MA_SO_THUE));
        sb.append("=".repeat(DO_RONG)).append('\n');
        sb.append(giua("HÓA ĐƠN DỊCH VỤ KHÁM CHỮA BỆNH")).append('\n');
        sb.append(giua(String.format("Ngày %02d tháng %02d năm %d", ngayLap.getDayOfMonth(), ngayLap.getMonthValue(), ngayLap.getYear()))).append('\n');
        sb.append(giua("Số: " + hd.getMaHoaDon() + "      Ký hiệu: NK" + String.format("%02d", ngayLap.getYear() % 100) + "E")).append('\n');
        sb.append(duongKe);

        // Người mua
        sb.append("Họ tên người mua : ").append(bn.getHoTen()).append(" (").append(bn.getMaBN()).append(")\n");
        if (bn.getDiaChi() != null) {
            sb.append("Địa chỉ          : ").append(bn.getDiaChi()).append('\n');
        }
        if (bn.getSdt() != null) {
            sb.append("Điện thoại       : ").append(bn.getSdt()).append('\n');
        }
        sb.append("Hình thức thanh toán: ").append(hd.getHinhThucThanhToan()).append('\n');
        sb.append(duongKe);

        // Bảng dịch vụ
        sb.append(String.format("%-3s %-34s %-5s %3s %12s %14s\n", "STT", "Tên dịch vụ", "ĐVT", "SL", "Đơn giá", "Thành tiền"));
        sb.append(duongKe);
        int stt = 1;
        for (ChiTietHoaDon ct : hd.getDsChiTiet()) {
            String ten = ct.getMaDichVu().getTenDichVu() + (ct.getSoFDIRang() > 0 ? " (răng " + ct.getSoFDIRang() + ")" : "");
            String donVi = ct.getMaDichVu().getDonViTinh() == null ? "" : ct.getMaDichVu().getDonViTinh();
            java.util.List<String> cacDong = xuongDong(ten, 34);
            sb.append(String.format("%-3d %-34s %-5s %3d %12s %14s\n", stt++, cacDong.get(0), donVi, ct.getSoLuong(),
                    dinhDangTien(ct.getDonGia()), dinhDangTien(ct.tinhThanhTien())));
            for (int k = 1; k < cacDong.size(); k++) {
                sb.append(String.format("%-3s %-34s\n", "", cacDong.get(k)));
            }
        }
        sb.append(duongKe);

        // Tiền
        BigDecimal tienThue = hd.getTongTien().subtract(hd.getTongTienTruocThue());
        sb.append(dongTien("Cộng tiền dịch vụ", dinhDangTien(hd.getTongTienDichVu())));
        sb.append(dongTien("Giảm giá", dinhDangTien(hd.getTienGiamGia())));
        sb.append(dongTien("Cộng tiền hàng (chưa thuế)", dinhDangTien(hd.getTongTienTruocThue())));
        sb.append(dongTien("Thuế suất GTGT " + hd.getPhanTramThue().stripTrailingZeros().toPlainString() + "% - Tiền thuế GTGT", dinhDangTien(tienThue)));
        sb.append(dongTien("TỔNG CỘNG TIỀN THANH TOÁN", dinhDangTien(hd.getTongTien())));
        sb.append(duongKe);
        for (String dong : xuongDong("Số tiền viết bằng chữ: " + tienBangChu(hd.getTongTien()), DO_RONG)) {
            sb.append(dong).append('\n');
        }
        sb.append("Đã thanh toán lúc: ").append(hd.getNgayThanhToan() == null ? "" : hd.getNgayThanhToan().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))).append("\n\n");

        // Chữ ký: người mua và người lập hóa đơn
        sb.append(String.format("%-39s%39s\n", "      NGƯỜI MUA HÀNG", "NGƯỜI LẬP HÓA ĐƠN      "));
        sb.append(String.format("%-39s%39s\n", "   (Ký, ghi rõ họ tên)", "(Ký, ghi rõ họ tên)   "));
        sb.append("\n\n\n");
        sb.append(String.format("%-39s%39s\n", "   " + bn.getHoTen(), (nguoiLap == null ? "" : nguoiLap.getHoTen()) + "   "));
        sb.append(duongKe);
        sb.append(giua("(Cần kiểm tra, đối chiếu khi lập, giao, nhận hóa đơn)")).append('\n');
        sb.append(giua("Cảm ơn quý khách!")).append('\n');
        return sb.toString();
    }

    // Số tiền viết bằng chữ
    public static String tienBangChu(BigDecimal soTien) {
        long n = soTien.setScale(0, RoundingMode.HALF_UP).longValue();
        if (n <= 0) {
            return "Không đồng";
        }
        String[] bac = {"", " nghìn", " triệu", " tỷ", " nghìn tỷ"};
        StringBuilder kq = new StringBuilder();
        boolean daBatDau = false;
        for (int b = 4; b >= 0; b--) {
            long nhom = (long) (n / Math.pow(1000, b)) % 1000;
            if (nhom == 0) {
                continue;
            }
            kq.append(' ').append(docBaSo((int) nhom, daBatDau)).append(bac[b]);
            daBatDau = true;
        }
        String chu = kq.toString().trim() + " đồng chẵn";
        return Character.toUpperCase(chu.charAt(0)) + chu.substring(1);
    }

    // Đọc một nhóm ba chữ số
    private static String docBaSo(int n, boolean docDayDu) {
        String[] so = {"không", "một", "hai", "ba", "bốn", "năm", "sáu", "bảy", "tám", "chín"};
        int tram = n / 100;
        int chuc = n / 10 % 10;
        int dv = n % 10;
        StringBuilder s = new StringBuilder();
        if (tram > 0 || docDayDu) {
            s.append(so[tram]).append(" trăm");
        }
        if (chuc > 1) {
            s.append(' ').append(so[chuc]).append(" mươi");
            if (dv == 1) {
                s.append(" mốt");
            } else if (dv == 5) {
                s.append(" lăm");
            } else if (dv > 0) {
                s.append(' ').append(so[dv]);
            }
        } else if (chuc == 1) {
            s.append(" mười");
            if (dv == 5) {
                s.append(" lăm");
            } else if (dv > 0) {
                s.append(' ').append(so[dv]);
            }
        } else if (dv > 0) {
            if (tram > 0 || docDayDu) {
                s.append(" lẻ");
            }
            s.append(' ').append(so[dv]);
        }
        return s.toString().trim();
    }

    // Thuế VAT hiện hành
    private BigDecimal layThueVAT() {
        return BigDecimal.valueOf(cauHinhController.layPhanTramVAT());
    }

    private static String giua(String chu) {
        int le = Math.max(0, (DO_RONG - chu.length()) / 2);
        return " ".repeat(le) + chu;
    }

    private static void themDongNeuCo(StringBuilder sb, String nhan, String giaTri) {
        if (giaTri != null && !giaTri.isEmpty()) {
            sb.append(nhan).append(": ").append(giaTri).append('\n');
        }
    }

    private static String dongTien(String nhan, String soTien) {
        return String.format("%-" + (DO_RONG - 16) + "s%16s\n", nhan, soTien);
    }

    // Cắt một chuỗi dài thành nhiều dòng không quá doRong ký tự
    private static java.util.List<String> xuongDong(String chu, int doRong) {
        java.util.List<String> dong = new ArrayList<>();
        StringBuilder hienTai = new StringBuilder();
        for (String tu : chu.split(" ")) {
            if (hienTai.length() > 0 && hienTai.length() + 1 + tu.length() > doRong) {
                dong.add(hienTai.toString());
                hienTai.setLength(0);
            }
            hienTai.append(hienTai.length() == 0 ? "" : " ").append(tu);
        }
        dong.add(hienTai.toString());
        return dong;
    }

    // Số tiền có dấu chấm ngăn cách hàng nghìn
    public static String dinhDangTien(BigDecimal so) {
        return String.format(Locale.GERMANY, "%,d", so.setScale(0, RoundingMode.HALF_UP).longValue());
    }

    // Lượt khám phải tồn tại
    private LuotKham layLuotKhamLapDuoc(String maLuotKham) {
        LuotKham lk = maLuotKham == null ? null : luotKhamDAO.timTheoMa(maLuotKham);
        if (lk == null) {
            throw new NghiepVuException("Chưa chọn lượt khám");
        }
        if (!HangSo.HOAN_THANH.equals(lk.getTrangThai())) {
            throw new NghiepVuException("Chỉ lập hóa đơn cho lượt khám đã hoàn thành (lượt " + maLuotKham + " đang \"" + lk.getTrangThai() + "\")");
        }
        if (lk.getMaHoaDon() != null) {
            throw new NghiepVuException("Lượt khám " + maLuotKham + " đã có hóa đơn " + lk.getMaHoaDon().getMaHoaDon());
        }
        return lk;
    }

    // Dựng đối tượng hóa đơn
    private HoaDon dungHoaDon(LuotKham lk, String maHoaDon, BigDecimal tienGiamGia, NhanVien nguoiLap) {
        List<ChiTietHoaDon> dong = tinhDongChiTiet(lk);
        if (dong.isEmpty()) {
            throw new NghiepVuException("Lượt khám này chưa ghi dịch vụ nào nên không lập được hóa đơn");
        }
        BigDecimal giam = tienGiamGia == null ? BigDecimal.ZERO : tienGiamGia;
        if (giam.signum() < 0) {
            throw new NghiepVuException("Tiền giảm giá không được âm");
        }
        HoaDon hd = new HoaDon();
        hd.setMaHoaDon(maHoaDon);
        hd.setMaBenhNhan(lk.getMaHoSo().getMaBN());
        hd.setMaNhanVien(nguoiLap);
        hd.setNgayLap(LocalDateTime.now());
        hd.setTienGiamGia(giam);
        hd.setPhanTramThue(layThueVAT());
        hd.setDsChiTiet(dong);
        if (giam.compareTo(hd.getTongTienDichVu()) > 0) {
            throw new NghiepVuException("Giảm giá (" + dinhDangTien(giam) + ") không được vượt tổng tiền dịch vụ (" + dinhDangTien(hd.getTongTienDichVu()) + ")");
        }
        hd.setHinhThucThanhToan(HINH_THUC.get(0));
        hd.setTrangThai(CHUA_THANH_TOAN);
        return hd;
    }

    // Các dòng chi tiết của lượt khám: dịch vụ gắn răng tính theo
    List<ChiTietHoaDon> tinhDongChiTiet(LuotKham lk) {
        List<ChiTietHoaDon> kq = new ArrayList<>();
        Set<String> daTinh = new HashSet<>();
        for (RangDieuTri r : rangDAO.layTheoLuotKham(lk.getMaLuotKham())) {
            if (r.getMaDichVu() != null && daTinh.add(r.getSoFDI() + "|" + r.getMaDichVu().getMaDichVu())) {
                kq.add(dong(r.getMaDichVu(), 1, r.getSoFDI()));
            }
        }
        Map<String, ChiTietHoaDon> khongRang = new LinkedHashMap<>();
        for (LuotKhamDichVu x : luotKhamDAO.layDichVu(lk.getMaLuotKham())) {
            ChiTietHoaDon ct = khongRang.get(x.getMaDichVu().getMaDichVu());
            if (ct == null) {
                khongRang.put(x.getMaDichVu().getMaDichVu(), dong(x.getMaDichVu(), x.getSoLuong(), 0));
            } else {
                ct.setSoLuong(ct.getSoLuong() + x.getSoLuong());
            }
        }
        kq.addAll(khongRang.values());
        return kq;
    }

    // Một dòng chi tiết với đơn giá lấy theo giá dịch vụ hiện tại
    private ChiTietHoaDon dong(DichVu dv, int soLuong, int soFDI) {
        ChiTietHoaDon ct = new ChiTietHoaDon();
        ct.setMaDichVu(dv);
        ct.setSoLuong(soLuong);
        ct.setDonGia(BigDecimal.valueOf(dv.getGia()));
        ct.setGiamGia(BigDecimal.ZERO);
        ct.setSoFDIRang(soFDI);
        return ct;
    }

    // Kết quả báo cáo: mỗi dòng là {nhãn
    public static class BaoCaoDoanhThu {
        public int soHoaDon;
        public long tongDoanhThu; // Đã gồm thuế
        public List<Object[]> theoNgay = new ArrayList<>();
        public List<Object[]> theoHinhThuc = new ArrayList<>();
        public List<Object[]> theoLoaiDichVu = new ArrayList<>();
        public List<Object[]> dichVuBanChay = new ArrayList<>();
    }

    // Báo cáo doanh thu các hóa đơn đã thanh toán trong khoảng ngày
    public BaoCaoDoanhThu baoCaoDoanhThu(java.time.LocalDate tu, java.time.LocalDate den) {
        TienIchController.yeuCau(nguoiDung, "xem báo cáo doanh thu", HangSo.VAI_TRO_QUAN_LY);
        if (tu == null || den == null) {
            throw new NghiepVuException("Phải chọn khoảng ngày của báo cáo");
        }
        if (den.isBefore(tu)) {
            throw new NghiepVuException("Ngày kết thúc phải sau hoặc bằng ngày bắt đầu");
        }
        if (java.time.temporal.ChronoUnit.DAYS.between(tu, den) > 366) {
            throw new NghiepVuException("Báo cáo tối đa một năm");
        }
        BaoCaoDoanhThu bc = new BaoCaoDoanhThu();
        bc.theoNgay = hoaDonDAO.doanhThuTheoNgay(tu, den);
        for (Object[] d : bc.theoNgay) {
            bc.soHoaDon += (Integer) d[1];
            bc.tongDoanhThu += (Long) d[2];
        }
        bc.theoHinhThuc = hoaDonDAO.doanhThuTheoHinhThuc(tu, den);
        bc.theoLoaiDichVu = hoaDonDAO.doanhThuTheoLoaiDichVu(tu, den);
        bc.dichVuBanChay = hoaDonDAO.dichVuBanChay(tu, den);
        return bc;
    }
}
