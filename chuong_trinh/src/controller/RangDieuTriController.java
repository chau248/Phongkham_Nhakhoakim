package controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import dao.NghiepVuException;
import dao.RangDieuTriDAO;
import dao.TienIchDAO;
import entity.DichVu;
import entity.HangSo;
import entity.HoSoBenhAn;
import entity.LuotKham;
import entity.NhanVien;
import entity.RangDieuTri;

// Nghiệp vụ sơ đồ răng: tình trạng mới nhất của từng răng và ghi nhận răng trong lượt khám
public class RangDieuTriController {

    // Các trạng thái được chọn cho răng đã nhổ ở lượt khám trước
    private static final List<String> TRANG_THAI_RANG_DA_NHO = List.of(HangSo.RANG_DA_NHO, HangSo.RANG_IMPLANT, HangSo.RANG_CAU_RANG);

    // Các trạng thái có nghĩa là đã làm một dịch vụ
    private static final List<String> TRANG_THAI_CAN_DICH_VU = List.of(HangSo.RANG_DA_TRAM, HangSo.RANG_BOC_SU, HangSo.RANG_DA_NHO,
            HangSo.RANG_IMPLANT, HangSo.RANG_CAU_RANG);

    // Trạng thái -> màu hiển thị trên sơ đồ răng
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

    private RangDieuTriDAO rangDAO = new RangDieuTriDAO();
    private LuotKhamController luotKhamService = new LuotKhamController();
    private HoSoBenhAnController hoSoService = new HoSoBenhAnController();
    private DichVuController dichVuService = new DichVuController();

    // Danh sách trạng thái răng hợp lệ
    public List<String> layDanhSachTrangThai() {
        return new ArrayList<>(MAU.keySet());
    }

    // Tình trạng hiện tại của từng răng trong cả hồ sơ: mỗi răng lấy bản ghi mới nhất
    public List<RangDieuTri> layTinhTrangMoiNhat(String maHoSo) {
        Map<Integer, RangDieuTri> moiNhat = new LinkedHashMap<>();
        for (RangDieuTri r : rangDAO.layTheoHoSo(maHoSo)) { // đã sắp mới nhất trước
            moiNhat.putIfAbsent(r.getSoFDI(), r);
        }
        return new ArrayList<>(moiNhat.values());
    }

    // Các bản ghi răng đã ghi trong một lượt khám
    public List<RangDieuTri> layTheoLuotKham(String maLuotKham) {
        return rangDAO.layTheoLuotKham(maLuotKham);
    }

    // Toàn bộ lịch sử ghi nhận răng của hồ sơ qua mọi lượt khám
    public List<RangDieuTri> layTatCaTheoHoSo(String maHoSo) {
        return rangDAO.layTheoHoSo(maHoSo);
    }

    // Ghi nhận tình trạng một răng trong lượt khám đang khám
    // khác dịch vụ
    public RangDieuTri ghiNhanRang(String maLuotKham, RangDieuTri rang) {
        LuotKham lk = layLuotKhamDeSua(maLuotKham);
        if (!RangDieuTri.kiemTraSoFDI(rang.getSoFDI())) {
            throw new NghiepVuException("Số răng không hợp lệ (răng vĩnh viễn 11-18, 21-28, 31-38, 41-48; răng sữa 51-55, 61-65, 71-75, 81-85)");
        }
        if (!MAU.containsKey(rang.getTrangThai())) {
            throw new NghiepVuException("Trạng thái răng không hợp lệ");
        }
        rang.setVatLieu(TienIchController.rongThanhNull(rang.getVatLieu()));
        rang.setGhiChu(TienIchController.rongThanhNull(rang.getGhiChu()));

        DichVu dv = null;
        if (rang.getMaDichVu() != null) {
            dv = dichVuService.timTheoMa(rang.getMaDichVu().getMaDichVu());
            if (dv == null || !dv.isTrangThaiHoatDong()) {
                throw new NghiepVuException("Dịch vụ không tồn tại hoặc đã ngừng cung cấp");
            }
        }
        String maHoSo = lk.getMaHoSo().getMaHoSo();

        // Răng sữa không cấy ghép Implant hay làm cầu răng
        if (RangDieuTri.laRangSua(rang.getSoFDI())) {
            boolean trangThaiCam = HangSo.RANG_IMPLANT.equals(rang.getTrangThai()) || HangSo.RANG_CAU_RANG.equals(rang.getTrangThai());
            boolean dichVuCam = dv != null && dv.isLienQuanImplantCauRang();
            if (trangThaiCam || dichVuCam) {
                throw new NghiepVuException("Răng sữa không cấy ghép Implant hoặc làm cầu răng");
            }
        }

        // Răng đã nhổ ở lượt khám trước: chỉ được chọn Đã nhổ
        RangDieuTri hienTai = timTinhTrangHienTai(maHoSo, rang.getSoFDI());
        if (hienTai != null && HangSo.RANG_DA_NHO.equals(hienTai.getTrangThai())
                && !maLuotKham.equals(hienTai.getMaLuotKham().getMaLuotKham())) {
            boolean trangThaiDuoc = TRANG_THAI_RANG_DA_NHO.contains(rang.getTrangThai());
            boolean dichVuDuoc = dv == null || dv.isLienQuanImplantCauRang();
            if (!trangThaiDuoc || !dichVuDuoc) {
                throw new NghiepVuException("Răng " + rang.getSoFDI() + " đã nhổ, chỉ được chọn dịch vụ Implant hoặc Cầu răng");
            }
        }

        rang.setMaDichVu(dv);
        rang.setDaNho(HangSo.RANG_DA_NHO.equals(rang.getTrangThai()));
        rang.setMauHienThi(MAU.get(rang.getTrangThai()));
        rang.setMaLuotKham(lk);

        RangDieuTri cu = timBanGhiCanSua(rangDAO.layTheoLuotKham(maLuotKham), rang);
        if (cu != null) {
            rang.setMaBanGhi(cu.getMaBanGhi());
            rang.setMaDichVu(cu.getMaDichVu()); // trùng dịch vụ thì như cũ; để trống thì giữ dịch vụ của bản ghi được sửa
            rangDAO.capNhat(rang);
            return rang;
        }
        rang.setMaBanGhi(TienIchDAO.taoMa("BG"));
        rang.setNgayGhiNhan(LocalDateTime.now());
        rangDAO.them(rang);
        return rang;
    }

    // Gỡ một bản ghi răng ghi nhầm khỏi lượt khám đang khám
    public void goBanGhiRang(String maLuotKham, String maBanGhi) {
        layLuotKhamDeSua(maLuotKham);
        if (!rangDAO.xoa(maLuotKham, maBanGhi)) {
            throw new NghiepVuException("Bản ghi răng này không thuộc lượt khám đang sửa");
        }
    }

    // Răng chuyển sang trạng thái do đã làm dịch vụ
    public String canhBaoThieuDichVu(String maLuotKham, RangDieuTri rang) {
        if (rang.getMaDichVu() != null || !TRANG_THAI_CAN_DICH_VU.contains(rang.getTrangThai())) {
            return null;
        }
        LuotKham lk = luotKhamService.timTheoMa(maLuotKham);
        RangDieuTri hienTai = timTinhTrangHienTai(lk.getMaHoSo().getMaHoSo(), rang.getSoFDI());
        String ttHienTai = hienTai == null ? HangSo.RANG_BINH_THUONG : hienTai.getTrangThai();
        if (rang.getTrangThai().equals(ttHienTai)) {
            return null;
        }
        // Bản ghi sắp sửa đã có dịch vụ
        RangDieuTri cu = timBanGhiCanSua(rangDAO.layTheoLuotKham(maLuotKham), rang);
        if (cu != null && cu.getMaDichVu() != null) {
            return null;
        }
        return "Chưa chọn dịch vụ đã làm cho răng " + rang.getSoFDI() + " nên dịch vụ này sẽ không được tính vào hóa đơn";
    }

    // Lượt khám phải đang khám và người thao tác là nha sĩ phụ trách
    private LuotKham layLuotKhamDeSua(String maLuotKham) {
        LuotKham lk = luotKhamService.timTheoMa(maLuotKham);
        if (!HangSo.DANG_KHAM.equals(lk.getTrangThai())) {
            throw new NghiepVuException("Chỉ ghi nhận răng cho lượt khám đang khám");
        }
        NhanVien nv = PhienLamViec.getNhanVienHienTai();
        if (nv == null || !lk.getMaNhanVienPhuTrach().getMaNV().equals(nv.getMaNV())) {
            throw new NghiepVuException("Chỉ nha sĩ phụ trách mới được ghi nhận răng của lượt khám này");
        }
        return lk;
    }

    // Bản ghi trong lượt khám sẽ bị sửa khi ghi nhận răng này
    private RangDieuTri timBanGhiCanSua(List<RangDieuTri> dsLuotKham, RangDieuTri rang) {
        String maDichVu = rang.getMaDichVu() == null ? null : rang.getMaDichVu().getMaDichVu();
        RangDieuTri moiNhatCoDichVu = null; // bản ghi mới nhất cùng răng có dịch vụ (dùng khi dịch vụ để trống)
        for (RangDieuTri cu : dsLuotKham) {
            if (cu.getSoFDI() != rang.getSoFDI()) {
                continue;
            }
            String maCu = cu.getMaDichVu() == null ? null : cu.getMaDichVu().getMaDichVu();
            if ((maCu == null && maDichVu == null) || (maCu != null && maCu.equals(maDichVu))) {
                return cu;
            }
            if (moiNhatCoDichVu == null && maCu != null) {
                moiNhatCoDichVu = cu;
            }
        }
        return maDichVu == null ? moiNhatCoDichVu : null;
    }

    // Tình trạng hiện tại của một răng trong hồ sơ
    private RangDieuTri timTinhTrangHienTai(String maHoSo, int soFDI) {
        for (RangDieuTri r : layTinhTrangMoiNhat(maHoSo)) {
            if (r.getSoFDI() == soFDI) {
                return r;
            }
        }
        return null;
    }
}
