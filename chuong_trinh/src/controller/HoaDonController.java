package controller;

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
import dao.BenhNhanDAO;
import dao.ChiTietHoaDonDAO;
import dao.HoSoBenhAnDAO;
import dao.HoaDonDAO;
import dao.KiemTraNhap;
import dao.LuotKhamDAO;
import dao.LuotKhamDichVuDAO;
import dao.MaGenerator;
import dao.NghiepVuException;
import dao.NhaSiPhuTrachDAO;
import dao.PhienLamViec;
import dao.RangDieuTriDAO;
import entity.BenhNhan;
import entity.ChiTietHoaDon;
import entity.DichVu;
import entity.HoSoBenhAn;
import entity.HoaDon;
import entity.LuotKham;
import entity.LuotKhamDichVu;
import entity.NhanVien;
import entity.RangDieuTri;
import entity.TrangThai;
import entity.VaiTro;

// Nghiệp vụ hóa đơn
public class HoaDonController {

    public static final String CHUA_THANH_TOAN = "Chưa thanh toán";
    public static final String DA_THANH_TOAN = "Đã thanh toán";
    public static final List<String> HINH_THUC = List.of("Tiền mặt", "Chuyển khoản", "Thẻ", "Ví điện tử");
    public static final BigDecimal THUE_VAT = new BigDecimal("8"); // Phần trăm VAT

    // Một lượt khám đã hoàn tất đang chờ lập hóa đơn
    public static class DongChoThanhToan {
        public LuotKham luotKham;
        public String maBN;
        public String tenBenhNhan;
        public String tenNhaSi;
        public BigDecimal tamTinh; // Tổng tiền đã gồm VAT
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

    // Các lượt khám Hoàn thành chưa có hóa đơn
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


    // Hóa đơn tạm tính của một lượt khám
    public HoaDon xemTruoc(String maLuotKham, BigDecimal tienGiamGia) {
        LuotKham lk = layLuotKhamLapDuoc(maLuotKham);
        return dungHoaDon(lk, "(chưa lập)", tienGiamGia, null);
    }

    // Lập hóa đơn cho lượt khám đã hoàn thành
    public HoaDon lapHoaDon(String maLuotKham, BigDecimal tienGiamGia, String ghiChu) {
        NhanVien nguoi = Quyen.yeuCau(nguoiDung, "lập hóa đơn", VaiTro.LE_TAN, VaiTro.QUAN_LY);
        LuotKham lk = layLuotKhamLapDuoc(maLuotKham);
        String ghiChuGon = KiemTraNhap.rongThanhNull(ghiChu);
        KiemTraNhap.doDai(ghiChuGon, 255, "Ghi chú");
        HoaDon hd = dungHoaDon(lk, MaGenerator.taoMa("HD"), tienGiamGia, nguoi.getMaNV());
        hd.setGhiChu(ghiChuGon);
        for (ChiTietHoaDon ct : hd.getDsChiTiet()) {
            ct.setMaHoaDon(hd);
            ct.setMaChiTiet(MaGenerator.taoMa("CT"));
        }
        hoaDonDAO.themKemChiTiet(hd, hd.getDsChiTiet(), maLuotKham);
        nhatKy.ghiNhatKy(nguoi.getMaNV(), "Lập hóa đơn", "HoaDon", hd.getMaHoaDon(), "Lượt khám " + maLuotKham + ", tổng " + hd.getTongTien());
        return hd;
    }

    // Kiểm tra lượt khám
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

    // Dựng đối tượng hóa đơn
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

    // Các dòng chi tiết của lượt khám
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

    // Tổng của các dòng
    public static BigDecimal tongDichVu(List<ChiTietHoaDon> dong) {
        BigDecimal tong = BigDecimal.ZERO;
        for (ChiTietHoaDon ct : dong) {
            BigDecimal giamDong = ct.getGiamGia() == null ? BigDecimal.ZERO : ct.getGiamGia();
            tong = tong.add(ct.getDonGia().multiply(BigDecimal.valueOf(ct.getSoLuong())).subtract(giamDong));
        }
        return tong;
    }

    // Tổng tiền trước thuế là tổng dịch vụ
    public static BigDecimal tinhTruocThue(List<ChiTietHoaDon> dong, BigDecimal giamGia) {
        return tongDichVu(dong).subtract(giamGia);
    }

    // Tổng tiền thanh toán là trước thuế cộng VAT
    public static BigDecimal tinhTongTien(BigDecimal truocThue, BigDecimal phanTramThue) {
        return truocThue.multiply(BigDecimal.valueOf(100).add(phanTramThue)).divide(BigDecimal.valueOf(100), 0, RoundingMode.HALF_UP);
    }

    // Tất cả hóa đơn, mới nhất trước
    public List<HoaDon> layTatCa() {
        Quyen.yeuCau(nguoiDung, "xem hóa đơn", VaiTro.LE_TAN, VaiTro.QUAN_LY);
        return hoaDonDAO.layTatCa();
    }

    // Hóa đơn kèm các dòng chi tiết
    public HoaDon timTheoMa(String maHoaDon) {
        HoaDon hd = maHoaDon == null ? null : hoaDonDAO.timTheoMa(maHoaDon);
        if (hd == null) {
            throw new NghiepVuException("Chưa chọn hóa đơn");
        }
        hd.setDsChiTiet(chiTietDAO.layTheoHoaDon(maHoaDon));
        return hd;
    }

    // Thanh toán hóa đơn Chưa thanh toán
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

    // Hủy hóa đơn chưa thanh toán
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

    // Nội dung hóa đơn dạng chữ để xem trước
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

    // Số tiền có dấu chấm ngăn cách hàng nghìn
    public static String dinhDangTien(BigDecimal so) {
        return String.format(Locale.GERMANY, "%,d", so.setScale(0, RoundingMode.HALF_UP).longValue());
    }

    private static String cat(String s, int toiDa) {
        return s.length() <= toiDa ? s : s.substring(0, toiDa - 1) + "…";
    }
}
