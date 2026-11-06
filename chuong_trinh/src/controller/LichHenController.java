package controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import dao.BenhNhanDAO;
import dao.CaTrucDAO;
import dao.LichHenDAO;
import dao.TienIchDAO;
import dao.NghiepVuException;
import entity.BenhNhan;
import entity.CaTruc;
import entity.DichVu;
import entity.LichHen;
import entity.NhanVien;
import entity.HangSo;

// Nghiệp vụ lịch hẹn
public class LichHenController {

    public static final String CHO_XAC_NHAN = "Chờ xác nhận";
    public static final String DA_XAC_NHAN = "Đã xác nhận";
    public static final String KHONG_DEN = "Không đến";
    public static final int BUOC_PHUT = 30; // Giờ hẹn tròn 30 phút
    public static final String HINH_THUC_MAC_DINH = "Trực tiếp";
    public static final List<String> HINH_THUC = List.of("Trực tiếp", "Điện thoại", "Online");

    private LichHenDAO lichHenDAO = new LichHenDAO();
    private CaTrucDAO caTrucDAO = new CaTrucDAO();
    private BenhNhanDAO benhNhanDAO = new BenhNhanDAO();
    private NhanVienController nhanVienController = new NhanVienController();
    private CaTrucController caTrucController = new CaTrucController();
    private DichVuController dichVuController = new DichVuController();
    private Supplier<NhanVien> nguoiDung = PhienLamViec::getNhanVienHienTai;

    // Hàm dùng chung và các hàm Hậu gọi

    // Tìm lịch hẹn theo mã
    public LichHen layTheoMa(String maLichHen) {
        return maLichHen == null ? null : lichHenDAO.timTheoMa(maLichHen);
    }

    // Đổi trạng thái lịch hẹn
    public void capNhatTrangThai(String maLichHen, String trangThai) {
        lichHenDAO.doiTrangThai(maLichHen, trangThai, null);
    }

    // Lịch hẹn của một nha sĩ
    public List<LichHen> layTheoNhanVien(String maNV, LocalDate tu, LocalDate den) {
        return lichHenDAO.layTheoNhanVien(maNV, tu, den);
    }

    // Các lịch hẹn Đã xác nhận của một bệnh nhân
    public List<LichHen> layDaXacNhanTheoBenhNhan(String maBN, LocalDate ngay) {
        return lichHenDAO.layDaXacNhanTheoBenhNhan(maBN, ngay);
    }

    // Hủy lịch tái khám của bệnh nhân vào một ngày
    public boolean huyLichTaiKham(String maBN, LocalDate ngay) {
        List<LichHen> ds = lichHenDAO.layConHieuLucTheoBenhNhanVaNgay(maBN, ngay);
        if (ds.isEmpty()) {
            return false;
        }
        LichHen lh = ds.get(0);
        lichHenDAO.doiTrangThai(lh.getMaLichHen(), HangSo.DA_HUY, "Hủy lịch tái khám do nha sĩ đặt nhầm");
        NhanVien nv = nguoiDung.get();
        TienIchController.ghiNhatKy(nv == null ? null : nv.getMaNV(), "Hủy lịch hẹn", "LichHen", lh.getMaLichHen(), "Hủy lịch tái khám do nha sĩ đặt nhầm");
        return true;
    }

    // Tạo lịch tái khám Chờ xác nhận
    public LichHen taoLichTaiKham(String maBN, String maDichVu, LocalDate ngay, String maNV) {
        DichVu dv = layDichVuHoatDong(maDichVu);
        BenhNhan bn = layBenhNhanDangDieuTri(maBN);
        for (LocalTime gio : layKhungGioTrong(maNV, ngay, dv.getThoiGianDuKien())) {
            if (!benhNhanBan(maBN, ngay, gio, dv.getThoiGianDuKien(), null)) {
                return luuLichMoi(bn, maNV, dv, ngay, gio, HINH_THUC_MAC_DINH, DA_XAC_NHAN); // Lịch tái khám do nha sĩ chỉ định thì không cần xác nhận
            }
        }
        throw new NghiepVuException("Ngày " + ngay + " nha sĩ không có ca trực hoặc đã hết khung giờ trống cho dịch vụ \"" + dv.getTenDichVu()
                + "\" (" + dv.getThoiGianDuKien() + " phút). Hãy chọn ngày khác.");
    }

    // Lịch hẹn của mọi nha sĩ
    public List<LichHen> layTheoKhoang(LocalDate tu, LocalDate den) {
        TienIchController.yeuCau(nguoiDung, "xem lịch hẹn của mọi nha sĩ", HangSo.VAI_TRO_LE_TAN, HangSo.VAI_TRO_QUAN_LY);
        return lichHenDAO.layTheoKhoang(tu, den);
    }

    // Các khung giờ trống của một nha sĩ
    public List<LocalTime> layKhungGioTrong(String maNV, LocalDate ngay, int thoiLuongPhut) {
        List<LocalTime> kq = new ArrayList<>();
        List<LichHen> daDat = lichHenDAO.layConHieuLucTheoNhaSiVaNgay(maNV, ngay);
        for (CaTruc ca : caTrucController.layCaCoMat(maNV, ngay, null)) {
            LocalTime het = CaTrucController.gioKetThucCa(ca.getCaLam());
            for (LocalTime g = CaTrucController.gioBatDauCa(ca.getCaLam()); !g.plusMinutes(thoiLuongPhut).isAfter(het) && g.isBefore(het); g = g.plusMinutes(BUOC_PHUT)) {
                if (LocalDateTime.of(ngay, g).isBefore(LocalDateTime.now()) || coChong(daDat, g, thoiLuongPhut, null)) {
                    continue;
                }
                kq.add(g);
            }
        }
        kq.sort(null);
        return kq;
    }

    // Nha sĩ đang làm việc có ca trực Có mặt
    public List<NhanVien> layNhaSiCoCaTruc(LocalDate ngay) {
        List<NhanVien> kq = new ArrayList<>();
        List<String> daCo = new ArrayList<>();
        for (CaTruc ca : caTrucDAO.layTheoKhoang(ngay, ngay)) {
            NhanVien nv = ca.getMaNV();
            if (HangSo.VAI_TRO_NHA_SI.equals(nv.getVaiTro()) && CaTrucController.CO_MAT.equals(ca.getTrangThai()) && !daCo.contains(nv.getMaNV())) {
                daCo.add(nv.getMaNV());
                NhanVien day = nhanVienController.timTheoMa(nv.getMaNV());
                if (day != null && NhanVienController.DANG_LAM.equals(day.getTrangThai())) {
                    kq.add(day);
                }
            }
        }
        return kq;
    }

    // Đặt lịch hẹn
    public LichHen datLich(String maBN, String maNhaSi, String maDichVu, LocalDate ngay, LocalTime gio, String hinhThucDat) {
        TienIchController.yeuCau(nguoiDung, "đặt lịch hẹn", HangSo.VAI_TRO_LE_TAN, HangSo.VAI_TRO_QUAN_LY);
        BenhNhan bn = layBenhNhanDangDieuTri(maBN);
        DichVu dv = layDichVuHoatDong(maDichVu);
        if (hinhThucDat != null && !HINH_THUC.contains(hinhThucDat)) {
            throw new NghiepVuException("Hình thức đặt chỉ có thể là Trực tiếp, Điện thoại hoặc Online");
        }
        return luuLichMoi(bn, maNhaSi, dv, ngay, gio, hinhThucDat == null ? HINH_THUC_MAC_DINH : hinhThucDat, CHO_XAC_NHAN);
    }

    // Dời lịch sang ngày giờ khác
    public void doiLich(String maLichHen, LocalDate ngayMoi, LocalTime gioMoi) {
        NhanVien nguoi = TienIchController.yeuCau(nguoiDung, "dời lịch hẹn", HangSo.VAI_TRO_LE_TAN, HangSo.VAI_TRO_QUAN_LY);
        LichHen lh = layLichDoiDuoc(maLichHen);
        DichVu dv = lh.getMaDichVu();
        String phong = kiemTraXepLich(lh.getMaBN().getMaBN(), lh.getMaNhanVien().getMaNV(), dv, ngayMoi, gioMoi, lh.getMaLichHen());
        lichHenDAO.doiNgayGio(maLichHen, ngayMoi, gioMoi, phong, dv.getThoiGianDuKien());
    }

    // Hủy lịch hẹn
    public void huyLich(String maLichHen, String lyDo) {
        NhanVien nguoi = TienIchController.yeuCau(nguoiDung, "hủy lịch hẹn", HangSo.VAI_TRO_LE_TAN, HangSo.VAI_TRO_QUAN_LY);
        String lyDoHuy = TienIchController.rongThanhNull(lyDo);
        if (lyDoHuy == null || lyDoHuy.length() < 5) {
            throw new NghiepVuException("Phải nhập lý do hủy lịch (ít nhất 5 ký tự)");
        }
        LichHen lh = layLichDoiDuoc(maLichHen);
        lichHenDAO.doiTrangThai(maLichHen, HangSo.DA_HUY, lyDoHuy);
        TienIchController.ghiNhatKy(nguoi.getMaNV(), "Hủy lịch hẹn", "LichHen", maLichHen, lh.getNgayHen() + " " + lh.getGioHen() + ": " + lyDoHuy);
    }

    // Xác nhận lịch Chờ xác nhận
    public void xacNhan(String maLichHen) {
        NhanVien nguoi = TienIchController.yeuCau(nguoiDung, "xác nhận lịch hẹn", HangSo.VAI_TRO_LE_TAN, HangSo.VAI_TRO_QUAN_LY);
        LichHen lh = layLich(maLichHen);
        if (!CHO_XAC_NHAN.equals(lh.getTrangThai())) {
            throw new NghiepVuException("Chỉ xác nhận được lịch đang Chờ xác nhận (lịch này đang \"" + lh.getTrangThai() + "\")");
        }
        DichVu dv = lh.getMaDichVu();
        kiemTraXepLich(lh.getMaBN().getMaBN(), lh.getMaNhanVien().getMaNV(), dv, lh.getNgayHen(), lh.getGioHen(), lh.getMaLichHen());
        lichHenDAO.doiTrangThai(maLichHen, DA_XAC_NHAN, null);
    }

    // Đánh dấu bệnh nhân Không
    public void danhDauKhongDen(String maLichHen) {
        NhanVien nguoi = TienIchController.yeuCau(nguoiDung, "đánh dấu bệnh nhân không đến", HangSo.VAI_TRO_LE_TAN, HangSo.VAI_TRO_QUAN_LY);
        LichHen lh = layLich(maLichHen);
        if (!DA_XAC_NHAN.equals(lh.getTrangThai())) {
            throw new NghiepVuException("Chỉ đánh dấu Không đến cho lịch Đã xác nhận");
        }
        if (LocalDateTime.of(lh.getNgayHen(), lh.getGioHen()).plusMinutes(lh.getThoiLuongDuKien()).isAfter(LocalDateTime.now())) {
            throw new NghiepVuException("Lịch hẹn chưa qua giờ nên chưa thể đánh dấu Không đến");
        }
        if (lichHenDAO.coLuotKhamDangKham(maLichHen)) {
            throw new NghiepVuException("Lịch hẹn đang có lượt khám Đang khám");
        }
        lichHenDAO.doiTrangThai(maLichHen, KHONG_DEN, null);
    }

    // Lịch hẹn trong khoảng ngày
    // nha sĩ chỉ thấy lịch của mình
    public List<LichHen> layLichTuan(LocalDate tu, LocalDate den, String maNhaSiLoc) {
        NhanVien nv = nguoiDung.get();
        if (nv == null) {
            throw new NghiepVuException("Chưa đăng nhập");
        }
        List<LichHen> kq = new ArrayList<>();
        if (nv.laVaiTro(HangSo.VAI_TRO_LE_TAN, HangSo.VAI_TRO_QUAN_LY)) {
            for (LichHen lh : lichHenDAO.layTheoKhoang(tu, den)) {
                if (maNhaSiLoc == null || maNhaSiLoc.equals(lh.getMaNhanVien().getMaNV())) {
                    kq.add(lh);
                }
            }
        } else if (nv.laVaiTro(HangSo.VAI_TRO_NHA_SI)) {
            kq.addAll(lichHenDAO.layTheoNhanVien(nv.getMaNV(), tu, den));
        } else if (nv.laVaiTro(HangSo.VAI_TRO_Y_TA)) {
            List<String> cacCa = new ArrayList<>(); // Khóa "mã nha sĩ|ngày|ca" của các nha sĩ làm cùng phòng với y tá
            for (CaTruc ca : caTrucDAO.layTheoNhanVien(nv.getMaNV(), tu, den)) {
                for (CaTruc khac : caTrucDAO.layTheoNgayVaCa(ca.getNgay(), ca.getCaLam())) {
                    if (khac.getMaNV().laVaiTro(HangSo.VAI_TRO_NHA_SI) && ca.getPhong() != null && ca.getPhong().equals(khac.getPhong())) {
                        cacCa.add(khac.getMaNV().getMaNV() + "|" + ca.getNgay() + "|" + ca.getCaLam());
                    }
                }
            }
            for (LichHen lh : lichHenDAO.layTheoKhoang(tu, den)) {
                if (cacCa.contains(lh.getMaNhanVien().getMaNV() + "|" + lh.getNgayHen() + "|" + caCuaGio(lh.getGioHen()))) {
                    kq.add(lh);
                }
            }
        } else {
            throw new NghiepVuException("Vai trò " + nv.getVaiTro() + " không xem được lịch hẹn");
        }
        return kq;
    }

    // Kiểm tra một lịch có xếp được không
    private String kiemTraXepLich(String maBN, String maNhaSi, DichVu dv, LocalDate ngay, LocalTime gio, String boQuaMa) {
        if (ngay == null || gio == null) {
            throw new NghiepVuException("Phải chọn ngày và giờ hẹn");
        }
        if (LocalDateTime.of(ngay, gio).isBefore(LocalDateTime.now())) {
            throw new NghiepVuException("Không đặt, dời hoặc xác nhận lịch ở thời điểm đã qua");
        }
        if (gio.getMinute() % BUOC_PHUT != 0 || gio.getSecond() != 0) {
            throw new NghiepVuException("Giờ hẹn phải tròn " + BUOC_PHUT + " phút (ví dụ 08:00, 08:30)");
        }
        String ca = caCuaGio(gio);
        if (ca == null) {
            throw new NghiepVuException("Giờ hẹn nằm ngoài giờ làm việc (ca Sáng 08:00-12:00, ca Chiều 13:00-17:00)");
        }
        if (gio.plusMinutes(dv.getThoiGianDuKien()).isAfter(CaTrucController.gioKetThucCa(ca))) {
            throw new NghiepVuException("Dịch vụ \"" + dv.getTenDichVu() + "\" kéo dài " + dv.getThoiGianDuKien() + " phút nên không kịp trước "
                    + CaTrucController.gioKetThucCa(ca) + " (hết ca " + ca.toLowerCase() + "). Hãy chọn giờ sớm hơn.");
        }
        NhanVien nhaSi = maNhaSi == null ? null : nhanVienController.timTheoMa(maNhaSi);
        if (nhaSi == null || !HangSo.VAI_TRO_NHA_SI.equals(nhaSi.getVaiTro())) {
            throw new NghiepVuException("Phải chọn một nha sĩ");
        }
        if (!NhanVienController.DANG_LAM.equals(nhaSi.getTrangThai())) {
            throw new NghiepVuException("Nha sĩ " + nhaSi.getHoTen() + " đã nghỉ việc");
        }
        if (!nhanVienController.chungChiConHan(maNhaSi, ngay)) {
            throw new NghiepVuException("Chứng chỉ hành nghề của nha sĩ " + nhaSi.getHoTen() + " đã hết hạn trước ngày " + ngay + ", không nhận lịch hẹn");
        }
        List<CaTruc> caTruc = caTrucController.layCaCoMat(maNhaSi, ngay, ca);
        if (caTruc.isEmpty()) {
            throw new NghiepVuException("Nha sĩ " + nhaSi.getHoTen() + " không có ca trực " + ca.toLowerCase() + " ngày " + ngay);
        }
        LichHen chong = timLichChong(lichHenDAO.layConHieuLucTheoNhaSiVaNgay(maNhaSi, ngay), gio, dv.getThoiGianDuKien(), boQuaMa);
        if (chong != null) {
            throw new NghiepVuException("Nha sĩ " + nhaSi.getHoTen() + " đã có lịch hẹn " + chong.getMaLichHen() + " lúc " + chong.getGioHen() + " (" + chong.getThoiLuongDuKien() + " phút)");
        }
        LichHen chongBN = timLichChong(lichHenDAO.layConHieuLucTheoBenhNhanVaNgay(maBN, ngay), gio, dv.getThoiGianDuKien(), boQuaMa);
        if (chongBN != null) {
            throw new NghiepVuException("Bệnh nhân đã có lịch hẹn " + chongBN.getMaLichHen() + " lúc " + chongBN.getGioHen() + " trùng thời gian này");
        }
        return caTruc.get(0).getPhong();
    }

    // Bệnh nhân đã có lịch
    private boolean benhNhanBan(String maBN, LocalDate ngay, LocalTime gio, int phut, String boQuaMa) {
        return timLichChong(lichHenDAO.layConHieuLucTheoBenhNhanVaNgay(maBN, ngay), gio, phut, boQuaMa) != null;
    }

    private boolean coChong(List<LichHen> ds, LocalTime gio, int phut, String boQuaMa) {
        return timLichChong(ds, gio, phut, boQuaMa) != null;
    }

    // Lịch đầu tiên
    private LichHen timLichChong(List<LichHen> ds, LocalTime gio, int phut, String boQuaMa) {
        for (LichHen lh : ds) {
            if (lh.getMaLichHen().equals(boQuaMa)) {
                continue;
            }
            if (gio.isBefore(lh.getGioHen().plusMinutes(lh.getThoiLuongDuKien())) && lh.getGioHen().isBefore(gio.plusMinutes(phut))) {
                return lh;
            }
        }
        return null;
    }

    // Ca của một giờ bắt đầu
    public static String caCuaGio(LocalTime gio) {
        if (!gio.isBefore(LocalTime.of(8, 0)) && gio.isBefore(LocalTime.of(12, 0))) {
            return CaTrucController.SANG;
        }
        if (!gio.isBefore(LocalTime.of(13, 0)) && gio.isBefore(LocalTime.of(17, 0))) {
            return CaTrucController.CHIEU;
        }
        return null;
    }

    // Lấy lịch hẹn theo mã
    private LichHen layLich(String maLichHen) {
        LichHen lh = layTheoMa(maLichHen);
        if (lh == null) {
            throw new NghiepVuException("Chưa chọn lịch hẹn");
        }
        return lh;
    }

    // Lịch hẹn được phép dời hoặc hủy
    private LichHen layLichDoiDuoc(String maLichHen) {
        LichHen lh = layLich(maLichHen);
        if (!CHO_XAC_NHAN.equals(lh.getTrangThai()) && !DA_XAC_NHAN.equals(lh.getTrangThai())) {
            throw new NghiepVuException("Lịch hẹn đang \"" + lh.getTrangThai() + "\" nên không dời hoặc hủy được");
        }
        if (lichHenDAO.coLuotKhamDangKham(maLichHen)) {
            throw new NghiepVuException("Lịch hẹn đang có lượt khám Đang khám nên không dời hoặc hủy được");
        }
        return lh;
    }

    private BenhNhan layBenhNhanDangDieuTri(String maBN) {
        BenhNhan bn = maBN == null ? null : benhNhanDAO.timTheoMa(maBN);
        if (bn == null) {
            throw new NghiepVuException("Chưa chọn bệnh nhân");
        }
        if (!HangSo.DANG_DIEU_TRI.equals(bn.getTrangThai())) {
            throw new NghiepVuException("Bệnh nhân " + bn.getHoTen() + " đang ở trạng thái " + bn.getTrangThai() + ", không đặt lịch hẹn được");
        }
        return bn;
    }

    private DichVu layDichVuHoatDong(String maDichVu) {
        DichVu dv = maDichVu == null ? null : dichVuController.timTheoMa(maDichVu);
        if (dv == null) {
            throw new NghiepVuException("Chưa chọn dịch vụ");
        }
        if (!dv.isTrangThaiHoatDong()) {
            throw new NghiepVuException("Dịch vụ \"" + dv.getTenDichVu() + "\" đã ngừng cung cấp");
        }
        return dv;
    }

    // Lưu lịch mới với trạng thái cho trước
    private LichHen luuLichMoi(BenhNhan bn, String maNhaSi, DichVu dv, LocalDate ngay, LocalTime gio, String hinhThuc, String trangThai) {
        String phong = kiemTraXepLich(bn.getMaBN(), maNhaSi, dv, ngay, gio, null);
        LichHen lh = new LichHen();
        lh.setMaLichHen(TienIchDAO.taoMa("LH"));
        lh.setMaBN(bn);
        lh.setMaNhanVien(nhanVienController.timTheoMa(maNhaSi));
        lh.setMaDichVu(dv);
        lh.setNgayHen(ngay);
        lh.setGioHen(gio);
        lh.setTrangThai(trangThai);
        lh.setThoiLuongDuKien(dv.getThoiGianDuKien());
        lh.setPhong(phong);
        lh.setHinhThucDat(hinhThuc);
        lh.setDaNhacHen(false);
        lh.setLyDoHuy(null);
        lichHenDAO.them(lh);
        return lh;
    }
}
