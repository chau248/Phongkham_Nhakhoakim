package nhakhoakim.controller;

import nhakhoakim.dao.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import nhakhoakim.entity.BenhNhan;
import nhakhoakim.entity.DichVu;
import nhakhoakim.entity.HangSo;
import nhakhoakim.entity.HoSoBenhAn;
import nhakhoakim.entity.LichHen;
import nhakhoakim.entity.LuotKham;
import nhakhoakim.entity.LuotKhamDichVu;
import nhakhoakim.entity.NhanVien;
import nhakhoakim.entity.RangDieuTri;

public class LuotKhamController {

    private static final int TOI_DA_SO_LUONG = 10;     
    private static final int TOI_DA_NGAY_TAI_KHAM = 180; 

    // Độ dài tối đa của các cột văn bản
    private static final int DO_DAI_LY_DO_KHAM = 255;
    private static final int DO_DAI_CHAN_DOAN = 500;
    private static final int DO_DAI_HUONG_DIEU_TRI = 500;
    private static final int DO_DAI_DON_THUOC = 1000;
    private static final int DO_DAI_GHI_CHU = 255;
    private static final int DO_DAI_LY_DO_HUY = 150;

    private static final String TIEN_TO_HUY = "Hủy khám: "; 
    private static final String NOI_GHI_CHU = " | "; 

    private final LuotKhamDAO luotKhamDAO;
    private final LuotKhamDichVuDAO dichVuLuotKhamDAO;
    private final RangDieuTriDAO rangDAO;
    private final BenhNhanDAO benhNhanDAO;
    private final HoSoBenhAnController hoSoService;
    private final DichVuController dichVuService;
    private final NhanVienController nhanVienService;
    private final LichHenController lichHenService;
    private final CaTrucController caTrucService;
    private final Supplier<NhanVien> nguoiDung;

    public LuotKhamController() {
        this(new LuotKhamDAO(), new LuotKhamDichVuDAO(), new RangDieuTriDAO(), new BenhNhanDAO(),
                new HoSoBenhAnController(), new DichVuController(), new NhanVienController(), new LichHenController(),
                new CaTrucController(), PhienLamViec::getNhanVienHienTai);
    }

    // Dùng các đối tượng truyền vào
    public LuotKhamController(LuotKhamDAO luotKhamDAO, LuotKhamDichVuDAO dichVuLuotKhamDAO, RangDieuTriDAO rangDAO,
            BenhNhanDAO benhNhanDAO, HoSoBenhAnController hoSoService, DichVuController dichVuService,
            NhanVienController nhanVienService, LichHenController lichHenService, CaTrucController caTrucService,
            Supplier<NhanVien> nguoiDung) {
        this.luotKhamDAO = luotKhamDAO;
        this.dichVuLuotKhamDAO = dichVuLuotKhamDAO;
        this.rangDAO = rangDAO;
        this.benhNhanDAO = benhNhanDAO;
        this.hoSoService = hoSoService;
        this.dichVuService = dichVuService;
        this.nhanVienService = nhanVienService;
        this.lichHenService = lichHenService;
        this.caTrucService = caTrucService;
        this.nguoiDung = nguoiDung;
    }

    // Kiểm tra nha sĩ được bắt đầu khám
    private void kiemTraNhaSi(String maNhaSi) {
        NhanVien dangNhap = nguoiDung.get();
        if (dangNhap == null || !dangNhap.getMaNV().equals(maNhaSi)) {
            throw new NghiepVuException("Chỉ được bắt đầu khám dưới tên của chính mình");
        }
        NhanVien nhaSi = nhanVienService.timTheoMa(maNhaSi);
        if (nhaSi == null || !HangSo.VAI_TRO_NHA_SI.equals(nhaSi.getVaiTro())) {
            throw new NghiepVuException("Chỉ nha sĩ mới được khám bệnh");
        }
        if (!nhanVienService.chungChiConHan(maNhaSi, LocalDate.now())) {
            throw new NghiepVuException("Chứng chỉ hành nghề của nha sĩ đã hết hạn, không thể bắt đầu khám");
        }
        if (!caTrucService.coCaTruc(maNhaSi, LocalDate.now(), null)) {
            throw new NghiepVuException("Hôm nay nha sĩ không có ca trực nên không thể bắt đầu khám");
        }
        if (luotKhamDAO.timDangKham(maNhaSi) != null) {
            throw new NghiepVuException("Bạn đang có một lượt khám chưa hoàn tất, hãy hoàn tất hoặc hủy lượt khám đó trước khi khám người khác");
        }
    }

    // Kiểm tra hồ sơ được khám
    private HoSoBenhAn kiemTraHoSo(String maHoSo) {
        HoSoBenhAn hs = hoSoService.timTheoMa(maHoSo);
        if (HangSo.HO_SO_DA_DONG.equals(hs.getTrangThaiHoSo())) {
            throw new NghiepVuException("Hồ sơ bệnh án đã đóng, không thể khám");
        }
        BenhNhan bn = benhNhanDAO.timTheoMa(hs.getMaBN());
        if (bn != null && !HangSo.DANG_DIEU_TRI.equals(bn.getTrangThai())) {
            throw new NghiepVuException("Bệnh nhân " + bn.getHoTen() + " đang ở trạng thái " + bn.getTrangThai() + ", không thể bắt đầu khám");
        }
        if (luotKhamDAO.timDangKhamTheoHoSo(maHoSo) != null) {
            throw new NghiepVuException("Bệnh nhân đang được khám ở một lượt khám khác");
        }
        return hs;
    }

    // Tạo và lưu lượt khám mới
    private LuotKham taoLuotKham(String maHoSo, String maNhaSi, String lyDoKham, String maLichHen) {
        LuotKham lk = new LuotKham();
        lk.setMaLuotKham(MaGenerator.taoMa("LK"));
        lk.setMaHoSo(maHoSo);
        lk.setMaNhanVienPhuTrach(maNhaSi);
        lk.setMaLichHen(maLichHen);
        lk.setGioBatDau(LocalDateTime.now());
        lk.setLyDoKham(lyDoKham);
        lk.setTrangThai(HangSo.DANG_KHAM);
        luotKhamDAO.them(lk);
        return lk;
    }

    // Khám vãng lai
    public LuotKham batDauKham(String maHoSo, String maNhaSi, String lyDoKham) {
        String lyDo = rongThanhNull(lyDoKham);
        doDai(lyDo, DO_DAI_LY_DO_KHAM, "Lý do khám");
        kiemTraNhaSi(maNhaSi);
        HoSoBenhAn hs = kiemTraHoSo(maHoSo);
        String maLichHen = null;
        for (LichHen lh : lichHenService.layDaXacNhanTheoBenhNhan(hs.getMaBN(), LocalDate.now())) {
            if (maLichHen == null || lh.getMaNhanVien().equals(maNhaSi)) {
                maLichHen = lh.getMaLichHen();
            }
        }
        return taoLuotKham(maHoSo, maNhaSi, lyDo, maLichHen);
    }

    // Khám vãng lai theo mã bệnh nhân
    public LuotKham batDauKhamTheoBenhNhan(String maBN, String maNhaSi, String lyDoKham) {
        HoSoBenhAn hs = hoSoService.timTheoBenhNhan(maBN);
        return batDauKham(hs.getMaHoSo(), maNhaSi, lyDoKham);
    }

    // Khám theo lịch hẹn
    public LuotKham batDauKhamTheoLich(String maLichHen, String maNhaSi) {
        LichHen lh = lichHenService.layTheoMa(maLichHen);
        if (lh == null) {
            throw new NghiepVuException("Không tìm thấy lịch hẹn " + maLichHen);
        }
        if (!"Đã xác nhận".equals(lh.getTrangThai())) {
            throw new NghiepVuException("Lịch hẹn đang ở trạng thái \"" + lh.getTrangThai() + "\", chỉ khám được lịch đã xác nhận");
        }
        if (!lh.getNgayHen().equals(LocalDate.now())) {
            throw new NghiepVuException("Chỉ bắt đầu khám đúng ngày hẹn (" + lh.getNgayHen() + ")");
        }
        if (!lh.getMaNhanVien().equals(maNhaSi)) {
            throw new NghiepVuException("Lịch hẹn này thuộc nha sĩ khác");
        }
        kiemTraNhaSi(maNhaSi);
        HoSoBenhAn hs = hoSoService.timTheoBenhNhan(lh.getMaBN());
        kiemTraHoSo(hs.getMaHoSo());
        DichVu dv = dichVuService.timTheoMa(lh.getMaDichVu());
        String lyDo = "Theo lịch hẹn" + (dv == null ? "" : ": " + dv.getTenDichVu());
        return taoLuotKham(hs.getMaHoSo(), maNhaSi, lyDo, maLichHen);
    }

    // Lượt khám đang khám của nha sĩ
    public LuotKham layDangKham(String maNhaSi) {
        return luotKhamDAO.timDangKham(maNhaSi);
    }

    // Lấy lượt khám theo mã
    public LuotKham timTheoMa(String maLuotKham) {
        LuotKham lk = luotKhamDAO.timTheoMa(maLuotKham);
        if (lk == null) {
            throw new NghiepVuException("Không tìm thấy lượt khám " + maLuotKham);
        }
        return lk;
    }

    // Lấy lượt khám để sửa
    private LuotKham layLuotKhamDeSua(String maLuotKham) {
        LuotKham lk = timTheoMa(maLuotKham);
        if (!HangSo.DANG_KHAM.equals(lk.getTrangThai())) {
            throw new NghiepVuException("Lượt khám đang ở trạng thái \"" + lk.getTrangThai() + "\", không thể sửa");
        }
        NhanVien nguoiSua = nguoiDung.get();
        if (nguoiSua == null || !lk.getMaNhanVienPhuTrach().equals(nguoiSua.getMaNV())) {
            throw new NghiepVuException("Chỉ nha sĩ phụ trách mới được sửa lượt khám này");
        }
        return lk;
    }

    // Kiểm tra độ dài rồi gán lý do khám
    private void ganNoiDung(LuotKham lk, String lyDoKham, String chanDoan, String huongDieuTri, String ghiChu) {
        String lyDo = rongThanhNull(lyDoKham);
        String chanDoanMoi = rongThanhNull(chanDoan);
        String huongMoi = rongThanhNull(huongDieuTri);
        String ghiChuMoi = rongThanhNull(ghiChu);
        doDai(lyDo, DO_DAI_LY_DO_KHAM, "Lý do khám");
        doDai(chanDoanMoi, DO_DAI_CHAN_DOAN, "Chẩn đoán");
        doDai(huongMoi, DO_DAI_HUONG_DIEU_TRI, "Hướng điều trị");
        doDai(ghiChuMoi, DO_DAI_GHI_CHU, "Ghi chú");
        lk.setLyDoKham(lyDo);
        lk.setChanDoan(chanDoanMoi);
        lk.setHuongDieuTri(huongMoi);
        lk.setGhiChu(ghiChuMoi);
    }

    // Kiểm tra đơn thuốc
    private String kiemTraDonThuoc(LuotKham lk, String donThuoc) {
        String don = rongThanhNull(donThuoc);
        if (don == null) {
            return null;
        }
        doDai(don, DO_DAI_DON_THUOC, "Đơn thuốc");
        HoSoBenhAn hs = hoSoService.timTheoMa(lk.getMaHoSo());
        for (String thuoc : don.split("[\\n;]")) {
            if (thuoc.trim().isEmpty()) {
                continue;
            }
            if (hs.kiemTraCanhBaoDiUng(thuoc)) {
                throw new NghiepVuException("Thuốc \"" + thuoc.trim() + "\" nằm trong cảnh báo dị ứng của bệnh nhân (" + hs.getCanhBaoDiUng() + ")");
            }
        }
        return don;
    }

    // Lưu một lần toàn bộ nội
    public void luuNoiDungKham(String maLuotKham, String lyDoKham, String chanDoan, String huongDieuTri, String donThuoc, String ghiChu) {
        LuotKham lk = layLuotKhamDeSua(maLuotKham);
        ganNoiDung(lk, lyDoKham, chanDoan, huongDieuTri, ghiChu);
        lk.setDonThuoc(kiemTraDonThuoc(lk, donThuoc));
        luotKhamDAO.capNhat(lk);
    }

    // Ghi lý do khám, chẩn đoán
    public void capNhatNoiDung(String maLuotKham, String lyDoKham, String chanDoan, String huongDieuTri, String ghiChu) {
        LuotKham lk = layLuotKhamDeSua(maLuotKham);
        ganNoiDung(lk, lyDoKham, chanDoan, huongDieuTri, ghiChu);
        luotKhamDAO.capNhat(lk);
    }

    // Kê đơn thuốc: đơn không được rỗng
    public void keDonThuoc(String maLuotKham, String donThuoc) {
        LuotKham lk = layLuotKhamDeSua(maLuotKham);
        if (rongThanhNull(donThuoc) == null) {
            throw new NghiepVuException("Đơn thuốc không được để trống");
        }
        lk.setDonThuoc(kiemTraDonThuoc(lk, donThuoc));
        luotKhamDAO.capNhat(lk);
    }

    // Lấy dịch vụ theo mã, phải tồn
    private DichVu layDichVuConHoatDong(String maDichVu) {
        DichVu dv = dichVuService.timTheoMa(maDichVu);
        if (dv == null || !dv.isTrangThaiHoatDong()) {
            throw new NghiepVuException("Dịch vụ không tồn tại hoặc đã ngừng cung cấp");
        }
        return dv;
    }

    // Tính giờ kết thúc khám
    private LocalDateTime tinhGioKetThuc(LuotKham lk) {
        LocalDateTime bay = LocalDateTime.now();
        return bay.isAfter(lk.getGioBatDau()) ? bay : lk.getGioBatDau().plusMinutes(1);
    }

    // Thêm một dịch vụ vào lượt khám
    public void themDichVu(String maLuotKham, String maDichVu, int soLuong) {
        layLuotKhamDeSua(maLuotKham);
        layDichVuConHoatDong(maDichVu);
        if (soLuong <= 0 || soLuong > TOI_DA_SO_LUONG) {
            throw new NghiepVuException("Số lượng phải từ 1 đến " + TOI_DA_SO_LUONG);
        }
        int daCo = 0; // Cùng dịch vụ đã thêm trước đó
        for (LuotKhamDichVu cu : dichVuLuotKhamDAO.layTheoLuotKham(maLuotKham)) {
            if (cu.getMaDichVu().equals(maDichVu)) {
                daCo += cu.getSoLuong();
            }
        }
        if (daCo + soLuong > TOI_DA_SO_LUONG) {
            throw new NghiepVuException("Tổng số lượng của một dịch vụ trong một lượt khám không quá " + TOI_DA_SO_LUONG + " (đã có " + daCo + ")");
        }
        LuotKhamDichVu x = new LuotKhamDichVu();
        x.setMaLuotKham(maLuotKham);
        x.setMaDichVu(maDichVu);
        x.setSoLuong(soLuong);
        dichVuLuotKhamDAO.them(x);
    }

    // Cảnh báo khi dịch vụ cần liệu trình
    public String canhBaoLieuTrinh(String maLuotKham, String maDichVu) {
        LuotKham lk = timTheoMa(maLuotKham);
        DichVu dv = dichVuService.timTheoMa(maDichVu);
        HoSoBenhAn hs = hoSoService.timTheoMa(lk.getMaHoSo());
        return hoSoService.canhBaoLieuTrinh(hs.getMaBN(), dv);
    }

    // Gom các cảnh báo khi chọn một dịch vụ
    public String canhBaoThuThuat(String maLuotKham, String maDichVu) {
        LuotKham lk = timTheoMa(maLuotKham);
        DichVu dv = dichVuService.timTheoMa(maDichVu);
        HoSoBenhAn hs = hoSoService.timTheoMa(lk.getMaHoSo());
        List<String> ds = new ArrayList<>();
        String lieuTrinh = hoSoService.canhBaoLieuTrinh(hs.getMaBN(), dv);
        if (lieuTrinh != null) {
            ds.add(lieuTrinh);
        }
        String yTe = hoSoService.canhBaoYTe(hs.getMaBN(), dv);
        if (yTe != null) {
            ds.add(yTe);
        }
        return ds.isEmpty() ? null : String.join("\n\n", ds);
    }

    // Ghép ghi chú cũ với phần hủy khám sao
    private String ghepGhiChuHuy(String ghiChuCu, String lyDoHuy) {
        String phanHuy = TIEN_TO_HUY + lyDoHuy;
        String cu = rongThanhNull(ghiChuCu);
        if (cu == null) {
            return phanHuy;
        }
        int conLai = DO_DAI_GHI_CHU - NOI_GHI_CHU.length() - phanHuy.length();
        if (conLai <= 0) {
            return phanHuy;
        }
        if (cu.length() > conLai) {
            cu = cu.substring(0, conLai).trim();
        }
        return cu.isEmpty() ? phanHuy : cu + NOI_GHI_CHU + phanHuy;
    }

    // Hủy lượt khám đang khám
    public void huyLuotKham(String maLuotKham, String lyDo) {
        LuotKham lk = layLuotKhamDeSua(maLuotKham);
        String lyDoHuy = rongThanhNull(lyDo);
        if (lyDoHuy == null || lyDoHuy.length() < 5) {
            throw new NghiepVuException("Phải nhập lý do hủy lượt khám (ít nhất 5 ký tự)");
        }
        doDai(lyDoHuy, DO_DAI_LY_DO_HUY, "Lý do hủy khám");
        if (!rangDAO.layTheoLuotKham(maLuotKham).isEmpty() || !dichVuLuotKhamDAO.layTheoLuotKham(maLuotKham).isEmpty()) {
            throw new NghiepVuException("Lượt khám đã ghi nhận dịch vụ hoặc răng điều trị nên không thể hủy, hãy hoàn tất khám");
        }
        if (lk.getNgayTaiKham() != null) {
            huyLichTaiKhamCuaLuot(lk);
        }
        lk.setGioKetThuc(tinhGioKetThuc(lk));
        lk.setTrangThai(HangSo.DA_HUY);
        lk.setGhiChu(ghepGhiChuHuy(lk.getGhiChu(), lyDoHuy));
        luotKhamDAO.capNhat(lk);
        if (lk.getMaLichHen() != null) {
            lichHenService.capNhatTrangThai(lk.getMaLichHen(), "Đã xác nhận");
        }
    }

    // Hủy lịch tái khám đã đặt của lượt khám đang khám
    public void huyTaiKham(String maLuotKham) {
        LuotKham lk = layLuotKhamDeSua(maLuotKham);
        if (lk.getNgayTaiKham() == null) {
            throw new NghiepVuException("Lượt khám này chưa đặt lịch tái khám");
        }
        huyLichTaiKhamCuaLuot(lk);
        luotKhamDAO.capNhat(lk);
    }

    // Hủy lịch hẹn tái khám tương ứng
    private void huyLichTaiKhamCuaLuot(LuotKham lk) {
        HoSoBenhAn hs = hoSoService.timTheoMa(lk.getMaHoSo());
        lichHenService.huyLichTaiKham(hs.getMaBN(), lk.getNgayTaiKham());
        lk.setNgayTaiKham(null);
    }

    // Bỏ một dịch vụ khỏi lượt khám
    public void xoaDichVu(String maLuotKham, int maDong) {
        layLuotKhamDeSua(maLuotKham);
        if (!dichVuLuotKhamDAO.xoa(maLuotKham, maDong)) {
            throw new NghiepVuException("Dịch vụ này không thuộc lượt khám đang sửa");
        }
    }

    // Dịch vụ không gắn răng của lượt khám
    public List<LuotKhamDichVu> layDichVuKhongGanRang(String maLuotKham) {
        return dichVuLuotKhamDAO.layTheoLuotKham(maLuotKham);
    }

    // Tất cả dịch vụ đã thực hiện, gộp
    public List<LuotKhamDichVu> layDichVuDaThucHien(String maLuotKham) {
        Map<String, Integer> tong = new LinkedHashMap<>();
        Set<String> daTinh = new HashSet<>(); // Một răng làm một dịch vụ
        for (RangDieuTri r : rangDAO.layTheoLuotKham(maLuotKham)) {
            if (r.getMaDichVu() != null && daTinh.add(r.getSoFDI() + "|" + r.getMaDichVu())) {
                tong.merge(r.getMaDichVu(), 1, Integer::sum);
            }
        }
        for (LuotKhamDichVu x : dichVuLuotKhamDAO.layTheoLuotKham(maLuotKham)) {
            tong.merge(x.getMaDichVu(), x.getSoLuong(), Integer::sum);
        }
        List<LuotKhamDichVu> kq = new ArrayList<>();
        for (Map.Entry<String, Integer> e : tong.entrySet()) {
            LuotKhamDichVu x = new LuotKhamDichVu();
            x.setMaLuotKham(maLuotKham);
            x.setMaDichVu(e.getKey());
            x.setSoLuong(e.getValue());
            kq.add(x);
        }
        return kq;
    }

    // Hoàn tất khám
    public void hoanTatKham(String maLuotKham) {
        LuotKham lk = layLuotKhamDeSua(maLuotKham);
        if (lk.getChanDoan() == null || lk.getChanDoan().trim().isEmpty()) {
            throw new NghiepVuException("Phải nhập chẩn đoán trước khi hoàn tất khám");
        }
        if (layDichVuDaThucHien(maLuotKham).isEmpty()) {
            throw new NghiepVuException("Phải ghi ít nhất một dịch vụ đã thực hiện (khám tổng quát cũng là một dịch vụ) trước khi hoàn tất khám");
        }
        lk.setGioKetThuc(tinhGioKetThuc(lk));
        lk.setTrangThai(HangSo.HOAN_THANH);
        luotKhamDAO.capNhat(lk);
        if (lk.getMaLichHen() != null) {
            lichHenService.capNhatTrangThai(lk.getMaLichHen(), HangSo.HOAN_THANH);
        }
    }

    // Chỉ định tái khám
    public void datTaiKham(String maLuotKham, LocalDate ngayTaiKham, String maDichVu) {
        LuotKham lk = layLuotKhamDeSua(maLuotKham);
        if (lk.getNgayTaiKham() != null) {
            throw new NghiepVuException("Lượt khám này đã có lịch tái khám ngày " + lk.getNgayTaiKham());
        }
        if (ngayTaiKham == null || !ngayTaiKham.isAfter(lk.getGioBatDau().toLocalDate()) || !ngayTaiKham.isAfter(LocalDate.now())) {
            throw new NghiepVuException("Ngày tái khám phải sau ngày khám và sau hôm nay");
        }
        if (ngayTaiKham.isAfter(lk.getGioBatDau().toLocalDate().plusDays(TOI_DA_NGAY_TAI_KHAM))) {
            throw new NghiepVuException("Chỉ hẹn tái khám trong vòng " + TOI_DA_NGAY_TAI_KHAM + " ngày (6 tháng) kể từ ngày khám");
        }
        layDichVuConHoatDong(maDichVu);
        HoSoBenhAn hs = hoSoService.timTheoMa(lk.getMaHoSo());
        // Tạo lịch trước
        lichHenService.taoLichTaiKham(hs.getMaBN(), maDichVu, ngayTaiKham, lk.getMaNhanVienPhuTrach());
        lk.setNgayTaiKham(ngayTaiKham);
        luotKhamDAO.capNhat(lk);
    }

    // Lượt khám đã hoàn thành
    public List<LuotKham> layLuotKhamChoThanhToan() {
        return luotKhamDAO.layChoThanhToan();
    }

    // Gắn hóa đơn vào lượt khám
    public void ganHoaDon(String maLuotKham, String maHoaDon) {
        LuotKham lk = timTheoMa(maLuotKham);
        if (!HangSo.HOAN_THANH.equals(lk.getTrangThai())) {
            throw new NghiepVuException("Chỉ lập hóa đơn cho lượt khám đã hoàn thành");
        }
        if (!luotKhamDAO.ganHoaDon(maLuotKham, maHoaDon)) {
            throw new NghiepVuException("Lượt khám " + maLuotKham + " đã có hóa đơn");
        }
    }

    // Chuỗi không được dài hơn độ dài cột
    private static void doDai(String giaTri, int toiDa, String tenTruong) {
        if (giaTri != null && giaTri.length() > toiDa) {
            throw new NghiepVuException(tenTruong + " dài tối đa " + toiDa + " ký tự (hiện đang " + giaTri.length() + " ký tự)");
        }
    }


    // Chuỗi rỗng hoặc toàn khoảng trắng
    private static String rongThanhNull(String s) {
        return s == null || s.trim().isEmpty() ? null : s.trim();
    }
}
