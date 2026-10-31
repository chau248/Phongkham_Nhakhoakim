package nhakhoakim.controller;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;
import nhakhoakim.dao.BenhNhanDAO;
import nhakhoakim.dao.ChiTietHoaDonDAO;
import nhakhoakim.dao.HoSoBenhAnDAO;
import nhakhoakim.dao.HoaDonDAO;
import nhakhoakim.dao.KiemTraNhap;
import nhakhoakim.dao.LuotKhamDAO;
import nhakhoakim.dao.LuotKhamDichVuDAO;
import nhakhoakim.dao.MaGenerator;
import nhakhoakim.dao.NghiepVuException;
import nhakhoakim.dao.NhaSiPhuTrachDAO;
import nhakhoakim.dao.PhienLamViec;
import nhakhoakim.dao.RangDieuTriDAO;
import nhakhoakim.entity.BenhNhan;
import nhakhoakim.entity.ChiTietHoaDon;
import nhakhoakim.entity.DichVu;
import nhakhoakim.entity.HoSoBenhAn;
import nhakhoakim.entity.HoaDon;
import nhakhoakim.entity.LuotKham;
import nhakhoakim.entity.LuotKhamDichVu;
import nhakhoakim.entity.NhanVien;
import nhakhoakim.entity.RangDieuTri;
import nhakhoakim.entity.TrangThai;
import nhakhoakim.entity.VaiTro;

// Nghiệp vụ hóa đơn (HD-01 đến HD-06): danh sách chờ thanh toán, lập hóa đơn từ lượt khám đã hoàn tất, thanh toán, hủy, in
public class HoaDonController {

    public static final String CHUA_THANH_TOAN = "Chưa thanh toán";
    public static final String DA_THANH_TOAN = "Đã thanh toán";
    public static final List<String> HINH_THUC = List.of("Tiền mặt", "Chuyển khoản", "Thẻ", "Ví điện tử");
    public static final BigDecimal THUE_VAT = new BigDecimal("8"); // phần trăm VAT

    // Một lượt khám đã hoàn tất đang chờ lập hóa đơn, kèm thông tin để hiện lên bảng
    public static class DongChoThanhToan {
        public LuotKham luotKham;
        public String maBN;
        public String tenBenhNhan;
        public String tenNhaSi;
        public BigDecimal tamTinh; // tổng tiền đã gồm VAT nếu lập hóa đơn ngay (chưa giảm giá)
    }

    private HoaDonDAO hoaDonDAO = new HoaDonDAO();
    private ChiTietHoaDonDAO chiTietDAO = new ChiTietHoaDonDAO();
    private LuotKhamDAO luotKhamDAO = new LuotKhamDAO();
    private LuotKhamDichVuDAO dichVuLuotKhamDAO = new LuotKhamDichVuDAO();
    private RangDieuTriDAO rangDAO = new RangDieuTriDAO();
    private HoSoBenhAnDAO hoSoDAO = new HoSoBenhAnDAO();
    private BenhNhanDAO benhNhanDAO = new BenhNhanDAO();
    private NhaSiPhuTrachDAO nhaSiDAO = new NhaSiPhuTrachDAO();
    private DichVuController dichVuController = new DichVuController();
    private NhanVienController nhanVienController = new NhanVienController();
    private NhatKyController nhatKy = new NhatKyController();
    private Supplier<NhanVien> nguoiDung = PhienLamViec::getNhanVienHienTai;

    // ===================== Danh sách chờ thanh toán (HD-01) =====================

    // Các lượt khám Hoàn thành chưa có hóa đơn, kèm tên bệnh nhân, nha sĩ và số tiền tạm tính
    public List<DongChoThanhToan> layChoThanhToan() {
        Quyen.yeuCau(nguoiDung, "xem danh sách chờ thanh toán", VaiTro.LE_TAN, VaiTro.QUAN_LY);
        List<LuotKham> dsLuotKham = luotKhamDAO.layChoThanhToan();
        List<String> dsMaNhaSi = new ArrayList<>();
        for (LuotKham lk : dsLuotKham) {
            dsMaNhaSi.add(lk.getMaNhanVienPhuTrach());
        }
        Map<String, String> tenNhaSi = nhaSiDAO.layTen(dsMaNhaSi);
        List<DongChoThanhToan> kq = new ArrayList<>();
        for (LuotKham lk : dsLuotKham) {
            HoSoBenhAn hs = hoSoDAO.timTheoMa(lk.getMaHoSo());
            BenhNhan bn = hs == null ? null : benhNhanDAO.timTheoMa(hs.getMaBN());
            DongChoThanhToan d = new DongChoThanhToan();
            d.luotKham = lk;
            d.maBN = hs == null ? "" : hs.getMaBN();
            d.tenBenhNhan = bn == null ? d.maBN : bn.getHoTen();
            d.tenNhaSi = tenNhaSi.getOrDefault(lk.getMaNhanVienPhuTrach(), lk.getMaNhanVienPhuTrach());
            d.tamTinh = tinhTongTien(tinhTruocThue(tinhDongChiTiet(lk), BigDecimal.ZERO), THUE_VAT);
            kq.add(d);
        }
        return kq;
    }

    // ===================== Lập hóa đơn (HD-02 đến HD-04) =====================

    // Hóa đơn tạm tính của một lượt khám (chưa lưu) để xem trước trên màn hình: các dòng lấy từ dịch vụ đã thực hiện, giá theo giá hiện tại
    public HoaDon xemTruoc(String maLuotKham, BigDecimal tienGiamGia) {
        LuotKham lk = layLuotKhamLapDuoc(maLuotKham);
        return dungHoaDon(lk, "(chưa lập)", tienGiamGia, null);
    }

    // Lập hóa đơn cho lượt khám đã hoàn thành và chưa có hóa đơn (Lễ tân, Quản lý).
    // Đơn giá lấy theo giá dịch vụ lúc lập và lưu cố định; giảm giá không vượt tổng tiền dịch vụ; hóa đơn, các dòng chi tiết và việc gắn vào lượt khám
    // nằm trong một transaction. Hóa đơn mới ở trạng thái Chưa thanh toán.
    public HoaDon lapHoaDon(String maLuotKham, BigDecimal tienGiamGia, String ghiChu) {
        NhanVien nguoi = Quyen.yeuCau(nguoiDung, "lập hóa đơn", VaiTro.LE_TAN, VaiTro.QUAN_LY);
        LuotKham lk = layLuotKhamLapDuoc(maLuotKham);
        String ghiChuGon = KiemTraNhap.rongThanhNull(ghiChu);
        KiemTraNhap.doDai(ghiChuGon, 255, "Ghi chú");
        HoaDon hd = dungHoaDon(lk, MaGenerator.taoMa("HD"), tienGiamGia, nguoi.getMaNV());
        hd.setGhiChu(ghiChuGon);
        for (ChiTietHoaDon ct : hd.getDsChiTiet()) {
            ct.setMaHoaDon(hd.getMaHoaDon());
            ct.setMaChiTiet(MaGenerator.taoMa("CT"));
        }
        hoaDonDAO.themKemChiTiet(hd, hd.getDsChiTiet(), maLuotKham);
        nhatKy.ghiNhatKy(nguoi.getMaNV(), "Lập hóa đơn", "HoaDon", hd.getMaHoaDon(), "Lượt khám " + maLuotKham + ", tổng " + hd.getTongTien());
        return hd;
    }

    // Lượt khám phải tồn tại, đã Hoàn thành và chưa gắn hóa đơn (HD-02)
    private LuotKham layLuotKhamLapDuoc(String maLuotKham) {
        LuotKham lk = maLuotKham == null ? null : luotKhamDAO.timTheoMa(maLuotKham);
        if (lk == null) {
            throw new NghiepVuException("Chưa chọn lượt khám");
        }
        if (!TrangThai.HOAN_THANH.equals(lk.getTrangThai())) {
            throw new NghiepVuException("Chỉ lập hóa đơn cho lượt khám đã hoàn thành (lượt " + maLuotKham + " đang \"" + lk.getTrangThai() + "\")");
        }
        if (lk.getMaHoaDon() != null) {
            throw new NghiepVuException("Lượt khám " + maLuotKham + " đã có hóa đơn " + lk.getMaHoaDon());
        }
        return lk;
    }

    // Dựng đối tượng hóa đơn (chưa lưu) từ lượt khám: các dòng chi tiết, giảm giá, thuế, tổng tiền
    private HoaDon dungHoaDon(LuotKham lk, String maHoaDon, BigDecimal tienGiamGia, String maNhanVienLap) {
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
        HoSoBenhAn hs = hoSoDAO.timTheoMa(lk.getMaHoSo());
        HoaDon hd = new HoaDon();
        hd.setMaHoaDon(maHoaDon);
        hd.setMaBenhNhan(hs.getMaBN());
        hd.setMaNhanVien(maNhanVienLap);
        hd.setNgayLap(LocalDateTime.now());
        hd.setTienGiamGia(giam);
        hd.setPhanTramThue(THUE_VAT);
        hd.setTongTienTruocThue(tinhTruocThue(dong, giam));
        hd.setTongTien(tinhTongTien(hd.getTongTienTruocThue(), THUE_VAT));
        hd.setHinhThucThanhToan(HINH_THUC.get(0));
        hd.setTrangThai(CHUA_THANH_TOAN);
        hd.setGhiChu(null);
        hd.setDsChiTiet(dong);
        return hd;
    }

    // Các dòng chi tiết của lượt khám (HD-04): dịch vụ gắn răng tính theo (răng, dịch vụ) mỗi cặp một lần (trám nhiều mặt cùng răng chỉ tính một lần, LK-22),
    // dịch vụ không gắn răng cộng dồn số lượng theo dịch vụ. Đơn giá là giá niêm yết hiện tại.
    List<ChiTietHoaDon> tinhDongChiTiet(LuotKham lk) {
        List<ChiTietHoaDon> kq = new ArrayList<>();
        Set<String> daTinh = new HashSet<>();
        for (RangDieuTri r : rangDAO.layTheoLuotKham(lk.getMaLuotKham())) {
            if (r.getMaDichVu() != null && daTinh.add(r.getSoFDI() + "|" + r.getMaDichVu())) {
                kq.add(dong(r.getMaDichVu(), 1, r.getSoFDI()));
            }
        }
        Map<String, Integer> khongRang = new LinkedHashMap<>();
        for (LuotKhamDichVu x : dichVuLuotKhamDAO.layTheoLuotKham(lk.getMaLuotKham())) {
            khongRang.merge(x.getMaDichVu(), x.getSoLuong(), Integer::sum);
        }
        for (Map.Entry<String, Integer> e : khongRang.entrySet()) {
            kq.add(dong(e.getKey(), e.getValue(), 0));
        }
        return kq;
    }

    private ChiTietHoaDon dong(String maDichVu, int soLuong, int soFDI) {
        DichVu dv = dichVuController.timTheoMa(maDichVu);
        if (dv == null) {
            throw new NghiepVuException("Không tìm thấy dịch vụ " + maDichVu + " để lập hóa đơn");
        }
        ChiTietHoaDon ct = new ChiTietHoaDon();
        ct.setMaDichVu(maDichVu);
        ct.setSoLuong(soLuong);
        ct.setDonGia(BigDecimal.valueOf(dv.getGia()));
        ct.setGiamGia(BigDecimal.ZERO);
        ct.setSoFDIRang(soFDI);
        ct.setGhiChu(null);
        return ct;
    }

    // Tổng (đơn giá x số lượng - giảm giá dòng) của các dòng
    public static BigDecimal tongDichVu(List<ChiTietHoaDon> dong) {
        BigDecimal tong = BigDecimal.ZERO;
        for (ChiTietHoaDon ct : dong) {
            BigDecimal giamDong = ct.getGiamGia() == null ? BigDecimal.ZERO : ct.getGiamGia();
            tong = tong.add(ct.getDonGia().multiply(BigDecimal.valueOf(ct.getSoLuong())).subtract(giamDong));
        }
        return tong;
    }

    // Tổng tiền trước thuế = tổng dịch vụ - giảm giá của cả hóa đơn
    public static BigDecimal tinhTruocThue(List<ChiTietHoaDon> dong, BigDecimal giamGia) {
        return tongDichVu(dong).subtract(giamGia);
    }

    // Tổng tiền thanh toán = trước thuế cộng VAT, làm tròn đến đồng (HD-03)
    public static BigDecimal tinhTongTien(BigDecimal truocThue, BigDecimal phanTramThue) {
        return truocThue.multiply(BigDecimal.valueOf(100).add(phanTramThue)).divide(BigDecimal.valueOf(100), 0, RoundingMode.HALF_UP);
    }

    // ===================== Xem, thanh toán, hủy, in (HD-05, HD-06) =====================

    // Tất cả hóa đơn, mới nhất trước (chưa kèm dòng chi tiết)
    public List<HoaDon> layTatCa() {
        Quyen.yeuCau(nguoiDung, "xem hóa đơn", VaiTro.LE_TAN, VaiTro.QUAN_LY);
        return hoaDonDAO.layTatCa();
    }

    // Hóa đơn kèm các dòng chi tiết; không có thì báo lỗi
    public HoaDon timTheoMa(String maHoaDon) {
        HoaDon hd = maHoaDon == null ? null : hoaDonDAO.timTheoMa(maHoaDon);
        if (hd == null) {
            throw new NghiepVuException("Chưa chọn hóa đơn");
        }
        hd.setDsChiTiet(chiTietDAO.layTheoHoaDon(maHoaDon));
        return hd;
    }

    // Thanh toán hóa đơn Chưa thanh toán bằng một trong bốn hình thức; thanh toán xong thì hóa đơn bị khóa (HD-05)
    public void thanhToan(String maHoaDon, String hinhThuc) {
        NhanVien nguoi = Quyen.yeuCau(nguoiDung, "thanh toán hóa đơn", VaiTro.LE_TAN, VaiTro.QUAN_LY);
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
        nhatKy.ghiNhatKy(nguoi.getMaNV(), "Thanh toán hóa đơn", "HoaDon", maHoaDon, hinhThuc + ", " + hd.getTongTien());
    }

    // Hủy hóa đơn chưa thanh toán (chỉ Quản lý, bắt buộc có lý do); lượt khám quay về danh sách chờ thanh toán. Hóa đơn đã thanh toán thì khóa (CH-02).
    public void huyHoaDon(String maHoaDon, String lyDo) {
        NhanVien nguoi = Quyen.yeuCau(nguoiDung, "hủy hóa đơn", VaiTro.QUAN_LY);
        String lyDoHuy = KiemTraNhap.rongThanhNull(lyDo);
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
        nhatKy.ghiNhatKy(nguoi.getMaNV(), "Hủy hóa đơn", "HoaDon", maHoaDon, lyDoHuy);
    }

    // Nội dung hóa đơn dạng chữ để xem trước và in; chỉ in hóa đơn Đã thanh toán, hóa đơn Đã hủy không in (HD-06)
    public String taoNoiDungIn(String maHoaDon) {
        Quyen.yeuCau(nguoiDung, "in hóa đơn", VaiTro.LE_TAN, VaiTro.QUAN_LY);
        HoaDon hd = timTheoMa(maHoaDon);
        if (TrangThai.DA_HUY.equals(hd.getTrangThai())) {
            throw new NghiepVuException("Hóa đơn đã hủy không được in");
        }
        if (!DA_THANH_TOAN.equals(hd.getTrangThai())) {
            throw new NghiepVuException("Chỉ in hóa đơn sau khi đã thanh toán");
        }
        BenhNhan bn = benhNhanDAO.timTheoMa(hd.getMaBenhNhan());
        NhanVien thuNgan = hd.getMaNhanVien() == null ? null : nhanVienController.timTheoMa(hd.getMaNhanVien());
        DateTimeFormatter dinhDang = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        StringBuilder sb = new StringBuilder();
        sb.append("               NHA KHOA KIM\n");
        sb.append("           HÓA ĐƠN THANH TOÁN\n");
        sb.append("------------------------------------------------------\n");
        sb.append("Mã hóa đơn : ").append(hd.getMaHoaDon()).append('\n');
        sb.append("Ngày lập   : ").append(hd.getNgayLap().format(dinhDang)).append('\n');
        sb.append("Bệnh nhân  : ").append(hd.getMaBenhNhan()).append(" - ").append(bn == null ? "" : bn.getHoTen()).append('\n');
        sb.append("Thu ngân   : ").append(thuNgan == null ? "" : thuNgan.getHoTen()).append('\n');
        sb.append("------------------------------------------------------\n");
        sb.append(String.format("%-3s %-24s %-4s %3s %10s %12s%n", "STT", "Dịch vụ", "Răng", "SL", "Đơn giá", "Thành tiền"));
        int stt = 1;
        for (ChiTietHoaDon ct : hd.getDsChiTiet()) {
            DichVu dv = dichVuController.timTheoMa(ct.getMaDichVu());
            String ten = dv == null ? ct.getMaDichVu() : dv.getTenDichVu();
            BigDecimal thanhTien = ct.getDonGia().multiply(BigDecimal.valueOf(ct.getSoLuong())).subtract(ct.getGiamGia());
            sb.append(String.format("%-3d %-24s %-4s %3d %10s %12s%n", stt++, cat(ten, 24), ct.getSoFDIRang() > 0 ? String.valueOf(ct.getSoFDIRang()) : "", ct.getSoLuong(),
                    dinhDangTien(ct.getDonGia()), dinhDangTien(thanhTien)));
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

    // Số tiền có dấu chấm ngăn cách hàng nghìn, ví dụ 1.500.000 (Locale cố định nên không phụ thuộc cài đặt máy)
    public static String dinhDangTien(BigDecimal so) {
        return String.format(Locale.GERMANY, "%,d", so.setScale(0, RoundingMode.HALF_UP).longValue());
    }

    private static String cat(String s, int toiDa) {
        return s.length() <= toiDa ? s : s.substring(0, toiDa - 1) + "…";
    }
}
