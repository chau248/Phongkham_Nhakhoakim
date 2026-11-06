package controller;

import dao.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;
import entity.DichVu;
import entity.HangSo;
import entity.HoSoBenhAn;
import entity.LuotKham;
import entity.NhanVien;
import entity.RangDieuTri;

// Nghiệp vụ sơ đồ răng
public class RangDieuTriController {

    private static final int DO_DAI_VAT_LIEU = 100; // Độ dài cột vatLieu
    private static final int DO_DAI_GHI_CHU = 255; // Độ dài cột ghiChu

    // Các trạng thái được chọn
    private static final List<String> TRANG_THAI_RANG_DA_NHO = List.of(HangSo.RANG_DA_NHO, HangSo.RANG_IMPLANT, HangSo.RANG_CAU_RANG);

    // Các trạng thái
    private static final List<String> TRANG_THAI_CAN_DICH_VU = List.of(HangSo.RANG_DA_TRAM, HangSo.RANG_BOC_SU, HangSo.RANG_DA_NHO,
            HangSo.RANG_IMPLANT, HangSo.RANG_CAU_RANG);

    // Trạng thái
    private static final Map<String, String> MAU = new LinkedHashMap<>();

    static {
        MAU.put(HangSo.RANG_BINH_THUONG, "Trắng");
        MAU.put(HangSo.RANG_SAU, "Đỏ");
        MAU.put(HangSo.RANG_DA_TRAM, "Xanh dương");
        MAU.put(HangSo.RANG_BOC_SU, "Vàng");
        MAU.put(HangSo.RANG_DA_NHO, "Xám");
        MAU.put(HangSo.RANG_IMPLANT, "Xanh lá");
        MAU.put(HangSo.RANG_CAU_RANG, "Cam");
    }

    private RangDieuTriDAO rangDAO;
    private LuotKhamController luotKhamService;
    private HoSoBenhAnController hoSoService;
    private DichVuController dichVuService;
    private Supplier<NhanVien> nguoiDung; // Trả về người đang đăng nhập

    // Dùng trong chương trình
    public RangDieuTriController() {
        this(new RangDieuTriDAO(), new LuotKhamController(), new HoSoBenhAnController(), new DichVuController(), PhienLamViec::getNhanVienHienTai);
    }

    // Dùng khi kiểm thử
    public RangDieuTriController(RangDieuTriDAO rangDAO, LuotKhamController luotKhamService, HoSoBenhAnController hoSoService,
            DichVuController dichVuService, Supplier<NhanVien> nguoiDung) {
        this.rangDAO = rangDAO;
        this.luotKhamService = luotKhamService;
        this.hoSoService = hoSoService;
        this.dichVuService = dichVuService;
        this.nguoiDung = nguoiDung;
    }

    // Danh sách trạng thái răng hợp lệ
    public List<String> layDanhSachTrangThai() {
        return new ArrayList<>(MAU.keySet());
    }

    // Tình trạng hiện tại của từng răng
    public List<RangDieuTri> layTinhTrangMoiNhat(String maHoSo) {
        List<RangDieuTri> tatCa = rangDAO.layTheoHoSo(maHoSo); // Mới nhất trước
        Map<Integer, RangDieuTri> moiNhat = new LinkedHashMap<>();
        for (RangDieuTri r : tatCa) {
            if (!moiNhat.containsKey(r.getSoFDI())) {
                moiNhat.put(r.getSoFDI(), r);
            }
        }
        List<RangDieuTri> kq = new ArrayList<>();
        for (RangDieuTri r : moiNhat.values()) {
            kq.add(chonBanGhiDaiDien(tatCa, r));
        }
        return kq;
    }

    // Các bản ghi răng đã ghi
    public List<RangDieuTri> layTheoLuotKham(String maLuotKham) {
        return rangDAO.layTheoLuotKham(maLuotKham);
    }

    // Toàn bộ lịch sử ghi nhận
    public List<RangDieuTri> layTatCaTheoHoSo(String maHoSo) {
        return rangDAO.layTheoHoSo(maHoSo);
    }

    // Gỡ một bản ghi răng đã
    public void goBanGhiRang(String maLuotKham, String maBanGhi) {
        LuotKham lk = luotKhamService.timTheoMa(maLuotKham);
        if (!HangSo.DANG_KHAM.equals(lk.getTrangThai())) {
            throw new NghiepVuException("Chỉ gỡ bản ghi răng của lượt khám đang khám");
        }
        NhanVien nv = nguoiDung.get();
        if (nv == null || lk.getMaNhanVienPhuTrach() == null || !lk.getMaNhanVienPhuTrach().equals(nv.getMaNV())) {
            throw new NghiepVuException("Chỉ nha sĩ phụ trách mới được gỡ bản ghi răng của lượt khám này");
        }
        if (!rangDAO.xoa(maLuotKham, maBanGhi)) {
            throw new NghiepVuException("Bản ghi răng này không thuộc lượt khám đang sửa");
        }
    }

    // Ghi nhận tình trạng một răng
    public RangDieuTri ghiNhanRang(String maLuotKham, RangDieuTri rang) {
        LuotKham lk = luotKhamService.timTheoMa(maLuotKham);
        if (!HangSo.DANG_KHAM.equals(lk.getTrangThai())) {
            throw new NghiepVuException("Chỉ ghi nhận răng cho lượt khám đang khám");
        }
        NhanVien nv = nguoiDung.get();
        if (nv == null || lk.getMaNhanVienPhuTrach() == null || !lk.getMaNhanVienPhuTrach().equals(nv.getMaNV())) {
            throw new NghiepVuException("Chỉ nha sĩ phụ trách mới được ghi nhận răng của lượt khám này");
        }
        if (!RangDieuTri.kiemTraSoFDI(rang.getSoFDI())) {
            throw new NghiepVuException("Số răng không hợp lệ (răng vĩnh viễn 11-18, 21-28, 31-38, 41-48; răng sữa 51-55, 61-65, 71-75, 81-85)");
        }
        if (!MAU.containsKey(rang.getTrangThai())) {
            throw new NghiepVuException("Trạng thái răng không hợp lệ");
        }
        doDai(rang.getVatLieu(), DO_DAI_VAT_LIEU, "Vật liệu");
        doDai(rang.getGhiChu(), DO_DAI_GHI_CHU, "Ghi chú");
        rang.setVatLieu(rongThanhNull(rang.getVatLieu()));
        rang.setGhiChu(rongThanhNull(rang.getGhiChu()));
        rang.setMaDichVu(rongThanhNull(rang.getMaDichVu()));

        HoSoBenhAn hoSo = hoSoService.timTheoMa(lk.getMaHoSo());
        if (rang.getVatLieu() != null && hoSo.kiemTraCanhBaoDiUng(rang.getVatLieu())) {
            throw new NghiepVuException("Vật liệu \"" + rang.getVatLieu() + "\" nằm trong cảnh báo dị ứng của bệnh nhân");
        }
        DichVu dv = null;
        if (rang.getMaDichVu() != null) {
            dv = dichVuService.timTheoMa(rang.getMaDichVu());
            if (dv == null || !dv.isTrangThaiHoatDong()) {
                throw new NghiepVuException("Dịch vụ không tồn tại hoặc đã ngừng cung cấp");
            }
        }

        // Răng sữa không cấy ghép Implant
        if (RangDieuTri.laRangSua(rang.getSoFDI())) {
            boolean trangThaiCam = HangSo.RANG_IMPLANT.equals(rang.getTrangThai()) || HangSo.RANG_CAU_RANG.equals(rang.getTrangThai());
            boolean dichVuCam = dv != null && dv.isLienQuanImplantCauRang();
            if (trangThaiCam || dichVuCam) {
                throw new NghiepVuException("Răng sữa không cấy ghép Implant hoặc làm cầu răng");
            }
        }

        // Răng đã nhổ ở lượt khám trước
        RangDieuTri hienTai = timTinhTrangHienTai(hoSo.getMaHoSo(), rang.getSoFDI());
        if (hienTai != null && HangSo.RANG_DA_NHO.equals(hienTai.getTrangThai()) && !maLuotKham.equals(hienTai.getMaLuotKham())) {
            boolean trangThaiDuoc = TRANG_THAI_RANG_DA_NHO.contains(rang.getTrangThai());
            boolean dichVuDuoc = dv == null || dv.isLienQuanImplantCauRang();
            if (!trangThaiDuoc || !dichVuDuoc) {
                throw new NghiepVuException("Răng " + rang.getSoFDI() + " đã nhổ, chỉ được chọn dịch vụ Implant hoặc Cầu răng");
            }
        }

        rang.setDaNho(HangSo.RANG_DA_NHO.equals(rang.getTrangThai()));
        rang.setMauHienThi(MAU.get(rang.getTrangThai()));
        rang.setMaLuotKham(maLuotKham);

        RangDieuTri cu = timBanGhiCanSua(rangDAO.layTheoLuotKham(maLuotKham), rang);
        if (cu != null) {
            rang.setMaBanGhi(cu.getMaBanGhi());
            rang.setMaDichVu(cu.getMaDichVu()); // Trùng dịch vụ thì như cũ
            rangDAO.capNhat(rang); // CapNhat đặt lại ngayGhiNhan
            return rang;
        }
        rang.setMaBanGhi(MaGenerator.taoMa("BG"));
        rang.setNgayGhiNhan(LocalDateTime.now());
        rangDAO.them(rang);
        return rang;
    }

    // Cảnh báo khi răng chuyển sang
    public String canhBaoThieuDichVu(String maLuotKham, RangDieuTri rang) {
        if (rongThanhNull(rang.getMaDichVu()) != null) {
            return null;
        }
        if (!TRANG_THAI_CAN_DICH_VU.contains(rang.getTrangThai())) {
            return null;
        }
        LuotKham lk = luotKhamService.timTheoMa(maLuotKham);
        RangDieuTri hienTai = timTinhTrangHienTai(lk.getMaHoSo(), rang.getSoFDI());
        String ttHienTai = hienTai == null ? HangSo.RANG_BINH_THUONG : hienTai.getTrangThai();
        if (rang.getTrangThai().equals(ttHienTai)) {
            return null;
        }
        // Nếu bản ghi sẽ được sửa là bản đã có dịch vụ
        RangDieuTri cu = timBanGhiCanSua(rangDAO.layTheoLuotKham(maLuotKham), rang);
        if (cu != null && cu.getMaDichVu() != null) {
            return null;
        }
        return "Chưa chọn dịch vụ đã làm cho răng " + rang.getSoFDI() + " nên dịch vụ này sẽ không được tính vào hóa đơn";
    }

    // Tìm bản ghi trong lượt khám sẽ bị sửa
    private RangDieuTri timBanGhiCanSua(List<RangDieuTri> dsLuotKham, RangDieuTri rang) {
        String maDichVu = rongThanhNull(rang.getMaDichVu());
        RangDieuTri moiNhatCoDichVu = null; // Bản ghi mới nhất cùng răng
        for (RangDieuTri cu : dsLuotKham) {
            if (cu.getSoFDI() != rang.getSoFDI() || !Objects.equals(cu.getMatRang(), rang.getMatRang())) {
                continue;
            }
            if (Objects.equals(cu.getMaDichVu(), maDichVu)) {
                return cu;
            }
            if (moiNhatCoDichVu == null && cu.getMaDichVu() != null) {
                moiNhatCoDichVu = cu;
            }
        }
        return maDichVu == null ? moiNhatCoDichVu : null;
    }

    // Bản ghi đại diện
    private RangDieuTri timTinhTrangHienTai(String maHoSo, int soFDI) {
        for (RangDieuTri r : layTinhTrangMoiNhat(maHoSo)) {
            if (r.getSoFDI() == soFDI) {
                return r;
            }
        }
        return null;
    }

    // Từ bản ghi mới nhất của một răng
    private RangDieuTri chonBanGhiDaiDien(List<RangDieuTri> tatCa, RangDieuTri moiNhat) {
        RangDieuTri chon = moiNhat;
        Set<String> matDaXet = new HashSet<>();
        for (RangDieuTri r : tatCa) {
            if (r.getSoFDI() != moiNhat.getSoFDI() || !Objects.equals(r.getMaLuotKham(), moiNhat.getMaLuotKham())
                    || !cungNgay(r, moiNhat)) {
                continue;
            }
            String mat = r.getMatRang() == null ? "" : r.getMatRang();
            if (!matDaXet.add(mat)) {
                continue; // Mặt này đã có bản ghi mới hơn
            }
            if (mucDoNang(r.getTrangThai()) > mucDoNang(chon.getTrangThai())) {
                chon = r;
            }
            if (mat.isEmpty()) {
                break; // Bản ghi cho cả răng
            }
        }
        return chon;
    }

    // Hai bản ghi có cùng ngày ghi nhận không
    private boolean cungNgay(RangDieuTri a, RangDieuTri b) {
        if (a.getNgayGhiNhan() == null || b.getNgayGhiNhan() == null) {
            return true;
        }
        return a.getNgayGhiNhan().toLocalDate().equals(b.getNgayGhiNhan().toLocalDate());
    }

    // Mức độ nặng của trạng thái răng
    private int mucDoNang(String trangThai) {
        if (HangSo.RANG_DA_NHO.equals(trangThai)) {
            return 5;
        }
        if (HangSo.RANG_SAU.equals(trangThai)) {
            return 4;
        }
        if (HangSo.RANG_BOC_SU.equals(trangThai)) {
            return 3;
        }
        if (HangSo.RANG_DA_TRAM.equals(trangThai)) {
            return 2;
        }
        if (HangSo.RANG_IMPLANT.equals(trangThai) || HangSo.RANG_CAU_RANG.equals(trangThai)) {
            return 1;
        }
        return 0;
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
