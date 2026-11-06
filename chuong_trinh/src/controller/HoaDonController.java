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
import entity.TrangThai;
import entity.VaiTro;

// Nghiệp vụ hóa đơn: danh sách chờ thanh toán, lập hóa đơn từ lượt khám đã hoàn tất, thanh toán, hủy, in
public class HoaDonController {

    public static final String CHUA_THANH_TOAN = "Chưa thanh toán";
    public static final String DA_THANH_TOAN = "Đã thanh toán";
    public static final List<String> HINH_THUC = List.of("Tiền mặt", "Chuyển khoản", "Thẻ", "Ví điện tử");
    public static final BigDecimal THUE_VAT = new BigDecimal("8"); // phần trăm VAT

    // Một lượt khám đã hoàn tất đang chờ lập hóa đơn
    public static class DongChoThanhToan {
        public LuotKham luotKham;
        public String maBN;
        public String tenBenhNhan;
        public String tenNhaSi;
        public BigDecimal tamTinh; // tổng tiền đã gồm VAT
    }

    private HoaDonDAO hoaDonDAO = new HoaDonDAO();
    private LuotKhamDAO luotKhamDAO = new LuotKhamDAO();
    private RangDieuTriDAO rangDAO = new RangDieuTriDAO();
    private Supplier<NhanVien> nguoiDung = PhienLamViec::getNhanVienHienTai;

    // ----- Tìm -----

    // Các lượt khám Hoàn thành chưa có hóa đơn
    public List<DongChoThanhToan> layChoThanhToan() {
        TienIchController.yeuCau(nguoiDung, "xem danh sách chờ thanh toán", VaiTro.LE_TAN, VaiTro.QUAN_LY);
        List<DongChoThanhToan> kq = new ArrayList<>();
        for (LuotKham lk : luotKhamDAO.layChoThanhToan()) {
            BenhNhan bn = lk.getMaHoSo().getMaBN();
            DongChoThanhToan d = new DongChoThanhToan();
            d.luotKham = lk;
            d.maBN = bn.getMaBN();
            d.tenBenhNhan = bn.getHoTen();
            d.tenNhaSi = lk.getMaNhanVienPhuTrach().getHoTen();
            d.tamTinh = tinhTongTien(tinhTruocThue(tinhDongChiTiet(lk), BigDecimal.ZERO), THUE_VAT);
            kq.add(d);
        }
        return kq;
    }

    // Tất cả hóa đơn, mới nhất trước
    public List<HoaDon> layTatCa() {
        TienIchController.yeuCau(nguoiDung, "xem hóa đơn", VaiTro.LE_TAN, VaiTro.QUAN_LY);
        return hoaDonDAO.layTatCa();
    }

    // Hóa đơn kèm các dòng chi tiết; không có thì báo lỗi
    public HoaDon timTheoMa(String maHoaDon) {
        HoaDon hd = maHoaDon == null ? null : hoaDonDAO.timTheoMa(maHoaDon);
        if (hd == null) {
            throw new NghiepVuException("Chưa chọn hóa đơn");
        }
        hd.setDsChiTiet(hoaDonDAO.layChiTiet(maHoaDon));
        return hd;
    }

    // Hóa đơn tạm tính của một lượt khám (chưa lưu) để xem trước
    public HoaDon xemTruoc(String maLuotKham, BigDecimal tienGiamGia) {
        return dungHoaDon(layLuotKhamLapDuoc(maLuotKham), "(chưa lập)", tienGiamGia, null);
    }

    // ----- Lập hóa đơn -----

    // Lập hóa đơn cho lượt khám đã hoàn thành (Lễ tân, Quản lý). Đơn giá lấy theo giá dịch vụ lúc lập và lưu cố định.
    public HoaDon lapHoaDon(String maLuotKham, BigDecimal tienGiamGia, String ghiChu) {
        NhanVien nguoi = TienIchController.yeuCau(nguoiDung, "lập hóa đơn", VaiTro.LE_TAN, VaiTro.QUAN_LY);
        LuotKham lk = layLuotKhamLapDuoc(maLuotKham);
        String ghiChuGon = TienIchController.rongThanhNull(ghiChu);
        TienIchController.doDai(ghiChuGon, 255, "Ghi chú");
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

    // ----- Thanh toán, hủy -----

    // Thanh toán hóa đơn Chưa thanh toán bằng một trong bốn hình thức
    public void thanhToan(String maHoaDon, String hinhThuc) {
        NhanVien nguoi = TienIchController.yeuCau(nguoiDung, "thanh toán hóa đơn", VaiTro.LE_TAN, VaiTro.QUAN_LY);
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

    // Hủy hóa đơn chưa thanh toán (chỉ Quản lý, cần lý do); lượt khám quay về danh sách chờ thanh toán
    public void huyHoaDon(String maHoaDon, String lyDo) {
        NhanVien nguoi = TienIchController.yeuCau(nguoiDung, "hủy hóa đơn", VaiTro.QUAN_LY);
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

    // ----- In hóa đơn -----

    // Nội dung hóa đơn dạng chữ để xem trước và in; chỉ in hóa đơn đã thanh toán
    public String taoNoiDungIn(String maHoaDon) {
        TienIchController.yeuCau(nguoiDung, "in hóa đơn", VaiTro.LE_TAN, VaiTro.QUAN_LY);
        HoaDon hd = timTheoMa(maHoaDon);
        if (TrangThai.DA_HUY.equals(hd.getTrangThai())) {
            throw new NghiepVuException("Hóa đơn đã hủy không được in");
        }
        if (!DA_THANH_TOAN.equals(hd.getTrangThai())) {
            throw new NghiepVuException("Chỉ in hóa đơn sau khi đã thanh toán");
        }
        BenhNhan bn = hd.getMaBenhNhan();
        NhanVien thuNgan = hd.getMaNhanVien();
        DateTimeFormatter dinhDang = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        StringBuilder sb = new StringBuilder();
        sb.append("               NHA KHOA KIM\n");
        sb.append("           HÓA ĐƠN THANH TOÁN\n");
        sb.append("------------------------------------------------------\n");
        sb.append("Mã hóa đơn : ").append(hd.getMaHoaDon()).append('\n');
        sb.append("Ngày lập   : ").append(hd.getNgayLap().format(dinhDang)).append('\n');
        sb.append("Bệnh nhân  : ").append(bn.getMaBN()).append(" - ").append(bn.getHoTen()).append('\n');
        sb.append("Thu ngân   : ").append(thuNgan == null ? "" : thuNgan.getHoTen()).append('\n');
        sb.append("------------------------------------------------------\n");
        sb.append(String.format("%-3s %-24s %-4s %3s %10s %12s%n", "STT", "Dịch vụ", "Răng", "SL", "Đơn giá", "Thành tiền"));
        int stt = 1;
        for (ChiTietHoaDon ct : hd.getDsChiTiet()) {
            sb.append(String.format("%-3d %-24s %-4s %3d %10s %12s%n", stt++, cat(ct.getMaDichVu().getTenDichVu(), 24),
                    ct.getSoFDIRang() > 0 ? String.valueOf(ct.getSoFDIRang()) : "", ct.getSoLuong(),
                    dinhDangTien(ct.getDonGia()), dinhDangTien(ct.tinhThanhTien())));
        }
        sb.append("------------------------------------------------------\n");
        sb.append(String.format("%-30s %23s%n", "Cộng tiền dịch vụ", dinhDangTien(tongDichVu(hd.getDsChiTiet()))));
        sb.append(String.format("%-30s %23s%n", "Giảm giá", dinhDangTien(hd.getTienGiamGia())));
        sb.append(String.format("%-30s %23s%n", "Tổng trước thuế", dinhDangTien(hd.getTongTienTruocThue())));
        sb.append(String.format("%-30s %23s%n", "Thuế VAT " + hd.getPhanTramThue().stripTrailingZeros().toPlainString() + "%",
                dinhDangTien(hd.getTongTien().subtract(hd.getTongTienTruocThue()))));
        sb.append(String.format("%-30s %23s%n", "TỔNG THANH TOÁN (đồng)", dinhDangTien(hd.getTongTien())));
        sb.append("------------------------------------------------------\n");
        sb.append("Hình thức  : ").append(hd.getHinhThucThanhToan()).append('\n');
        sb.append("Thanh toán : ").append(hd.getNgayThanhToan() == null ? "" : hd.getNgayThanhToan().format(dinhDang)).append('\n');
        sb.append("\n          Cảm ơn quý khách!\n");
        return sb.toString();
    }

    // ----- Tính tiền -----

    // Tổng (đơn giá x số lượng - giảm giá dòng) của các dòng
    public static BigDecimal tongDichVu(List<ChiTietHoaDon> dong) {
        BigDecimal tong = BigDecimal.ZERO;
        for (ChiTietHoaDon ct : dong) {
            tong = tong.add(ct.tinhThanhTien());
        }
        return tong;
    }

    // Tổng tiền trước thuế = tổng dịch vụ - giảm giá cả hóa đơn
    public static BigDecimal tinhTruocThue(List<ChiTietHoaDon> dong, BigDecimal giamGia) {
        return tongDichVu(dong).subtract(giamGia);
    }

    // Tổng tiền thanh toán = trước thuế cộng VAT, làm tròn đến đồng
    public static BigDecimal tinhTongTien(BigDecimal truocThue, BigDecimal phanTramThue) {
        return truocThue.multiply(BigDecimal.valueOf(100).add(phanTramThue)).divide(BigDecimal.valueOf(100), 0, RoundingMode.HALF_UP);
    }

    // Số tiền có dấu chấm ngăn cách hàng nghìn, ví dụ 1.500.000
    public static String dinhDangTien(BigDecimal so) {
        return String.format(Locale.GERMANY, "%,d", so.setScale(0, RoundingMode.HALF_UP).longValue());
    }

    // ----- Hàm phụ -----

    // Lượt khám phải tồn tại, đã hoàn thành và chưa có hóa đơn
    private LuotKham layLuotKhamLapDuoc(String maLuotKham) {
        LuotKham lk = maLuotKham == null ? null : luotKhamDAO.timTheoMa(maLuotKham);
        if (lk == null) {
            throw new NghiepVuException("Chưa chọn lượt khám");
        }
        if (!TrangThai.HOAN_THANH.equals(lk.getTrangThai())) {
            throw new NghiepVuException("Chỉ lập hóa đơn cho lượt khám đã hoàn thành (lượt " + maLuotKham + " đang \"" + lk.getTrangThai() + "\")");
        }
        if (lk.getMaHoaDon() != null) {
            throw new NghiepVuException("Lượt khám " + maLuotKham + " đã có hóa đơn " + lk.getMaHoaDon().getMaHoaDon());
        }
        return lk;
    }

    // Dựng đối tượng hóa đơn (chưa lưu) từ lượt khám
    private HoaDon dungHoaDon(LuotKham lk, String maHoaDon, BigDecimal tienGiamGia, NhanVien nguoiLap) {
        List<ChiTietHoaDon> dong = tinhDongChiTiet(lk);
        if (dong.isEmpty()) {
            throw new NghiepVuException("Lượt khám này chưa ghi dịch vụ nào nên không lập được hóa đơn");
        }
        BigDecimal giam = tienGiamGia == null ? BigDecimal.ZERO : tienGiamGia;
        BigDecimal tongDichVu = tongDichVu(dong);
        if (giam.signum() < 0) {
            throw new NghiepVuException("Tiền giảm giá không được âm");
        }
        if (giam.compareTo(tongDichVu) > 0) {
            throw new NghiepVuException("Giảm giá (" + dinhDangTien(giam) + ") không được vượt tổng tiền dịch vụ (" + dinhDangTien(tongDichVu) + ")");
        }
        HoaDon hd = new HoaDon();
        hd.setMaHoaDon(maHoaDon);
        hd.setMaBenhNhan(lk.getMaHoSo().getMaBN());
        hd.setMaNhanVien(nguoiLap);
        hd.setNgayLap(LocalDateTime.now());
        hd.setTienGiamGia(giam);
        hd.setPhanTramThue(THUE_VAT);
        hd.setTongTienTruocThue(tinhTruocThue(dong, giam));
        hd.setTongTien(tinhTongTien(hd.getTongTienTruocThue(), THUE_VAT));
        hd.setHinhThucThanhToan(HINH_THUC.get(0));
        hd.setTrangThai(CHUA_THANH_TOAN);
        hd.setDsChiTiet(dong);
        return hd;
    }

    // Các dòng chi tiết của lượt khám: dịch vụ gắn răng tính theo (răng, dịch vụ) mỗi cặp một dòng, dịch vụ không gắn răng cộng dồn số lượng
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

    private static String cat(String s, int toiDa) {
        return s.length() <= toiDa ? s : s.substring(0, toiDa - 1) + "…";
    }
}
