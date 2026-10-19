package nhakhoakim.dao;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;
import nhakhoakim.entity.DichVu;
import nhakhoakim.entity.HangSo;
import nhakhoakim.entity.HoSoBenhAn;
import nhakhoakim.entity.LuotKham;
import nhakhoakim.entity.NhanVien;
import nhakhoakim.entity.RangDieuTri;

// Nghiệp vụ sơ đồ răng: tình trạng mới nhất của từng răng và ghi nhận răng trong lượt khám
public class RangDieuTriService {

    private static final int DO_DAI_VAT_LIEU = 100;  // độ dài cột vatLieu
    private static final int DO_DAI_GHI_CHU = 255;   // độ dài cột ghiChu

    // Các trạng thái được chọn cho răng đã nhổ ở lượt khám trước
    private static final List<String> TRANG_THAI_RANG_DA_NHO = List.of(HangSo.RANG_DA_NHO, HangSo.RANG_IMPLANT, HangSo.RANG_CAU_RANG);

    // Các trạng thái mà việc chuyển sang đó là do đã làm một dịch vụ (nên cần chọn dịch vụ để tính tiền)
    private static final List<String> TRANG_THAI_CAN_DICH_VU = List.of(HangSo.RANG_DA_TRAM, HangSo.RANG_BOC_SU, HangSo.RANG_DA_NHO,
            HangSo.RANG_IMPLANT, HangSo.RANG_CAU_RANG);

    // Trạng thái và màu hiển thị trên sơ đồ răng
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
    private LuotKhamService luotKhamService;
    private HoSoBenhAnService hoSoService;
    private DichVuService dichVuService;
    private Supplier<NhanVien> nguoiDung; // trả về người đang đăng nhập

    // Dùng trong chương trình: các phụ thuộc mặc định, người dùng lấy từ phiên làm việc
    public RangDieuTriService() {
        this(new RangDieuTriDAO(), new LuotKhamService(), new HoSoBenhAnService(), new DichVuService(), PhienLamViec::getNhanVienHienTai);
    }

    // Dùng khi kiểm thử: truyền vào các phụ thuộc và người đang đăng nhập
    public RangDieuTriService(RangDieuTriDAO rangDAO, LuotKhamService luotKhamService, HoSoBenhAnService hoSoService,
            DichVuService dichVuService, Supplier<NhanVien> nguoiDung) {
        this.rangDAO = rangDAO;
        this.luotKhamService = luotKhamService;
        this.hoSoService = hoSoService;
        this.dichVuService = dichVuService;
        this.nguoiDung = nguoiDung;
    }

    // Danh sách trạng thái răng hợp lệ (để đổ vào ComboBox)
    public List<String> layDanhSachTrangThai() {
        return new ArrayList<>(MAU.keySet());
    }

    // Tình trạng hiện tại của từng răng trong cả hồ sơ (răng chưa từng ghi nhận thì không có trong danh sách).
    // Mỗi răng lấy bản ghi mới nhất; riêng khi trong lượt khám gần nhất của răng đó có ghi nhiều mặt răng khác nhau
    // trong cùng một ngày thì lấy bản ghi nặng hơn (Đã nhổ > Sâu > Bọc sứ > Đã trám > Implant/Cầu răng > Bình thường)
    // để mặt "Gần" đã trám không che mất mặt "Nhai" còn sâu.
    public List<RangDieuTri> layTinhTrangMoiNhat(String maHoSo) {
        List<RangDieuTri> tatCa = rangDAO.layTheoHoSo(maHoSo); // mới nhất trước nên bản đầu tiên của mỗi răng là mới nhất
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

    // Các bản ghi răng đã ghi trong một lượt khám
    public List<RangDieuTri> layTheoLuotKham(String maLuotKham) {
        return rangDAO.layTheoLuotKham(maLuotKham);
    }

    // Toàn bộ lịch sử ghi nhận răng của hồ sơ qua mọi lượt khám, mới nhất trước (dùng cho dòng thời gian và gợi ý điều trị)
    public List<RangDieuTri> layTatCaTheoHoSo(String maHoSo) {
        return rangDAO.layTheoHoSo(maHoSo);
    }

    // Gỡ một bản ghi răng đã ghi nhầm khỏi lượt khám đang khám (ví dụ chọn nhầm dịch vụ): chỉ nha sĩ phụ trách; bản ghi phải thuộc lượt khám này
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

    // Ghi nhận (thêm mới hoặc sửa) tình trạng một răng trong lượt khám đang khám.
    // Trong một lượt khám, cùng răng + cùng mặt răng + cùng dịch vụ thì sửa bản ghi cũ, khác dịch vụ thì thêm bản ghi mới
    // (một răng ghi được nhiều dịch vụ). Để trống dịch vụ mà răng và mặt đó đã có bản ghi kèm dịch vụ thì sửa bản ghi mới nhất
    // và giữ nguyên dịch vụ của nó (chỉ đổi trạng thái, vật liệu, ghi chú).
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

        // Răng sữa không cấy ghép Implant hay làm cầu răng
        if (RangDieuTri.laRangSua(rang.getSoFDI())) {
            boolean trangThaiCam = HangSo.RANG_IMPLANT.equals(rang.getTrangThai()) || HangSo.RANG_CAU_RANG.equals(rang.getTrangThai());
            boolean dichVuCam = dv != null && dv.isLienQuanImplantCauRang();
            if (trangThaiCam || dichVuCam) {
                throw new NghiepVuException("Răng sữa không cấy ghép Implant hoặc làm cầu răng");
            }
        }

        // Răng đã nhổ ở lượt khám trước: chỉ được chọn Đã nhổ, Implant, Cầu răng và dịch vụ Implant / Cầu răng
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
            rang.setMaDichVu(cu.getMaDichVu()); // trùng dịch vụ thì như cũ; để trống thì giữ dịch vụ của bản ghi được sửa
            rangDAO.capNhat(rang);              // capNhat đặt lại ngayGhiNhan để bản vừa sửa là bản mới nhất của răng
            return rang;
        }
        rang.setMaBanGhi(MaGenerator.taoMa("BG"));
        rang.setNgayGhiNhan(LocalDateTime.now());
        rangDAO.them(rang);
        return rang;
    }

    // Cảnh báo (không chặn) khi răng chuyển sang một trạng thái do đã làm dịch vụ (Đã trám, Bọc sứ, Đã nhổ, Implant, Cầu răng)
    // mà chưa chọn dịch vụ nên khoản đó sẽ không vào hóa đơn; không có gì cần cảnh báo thì trả về null
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
        // Nếu bản ghi sẽ được sửa là bản đã có dịch vụ thì dịch vụ đó được giữ lại và vẫn tính tiền
        RangDieuTri cu = timBanGhiCanSua(rangDAO.layTheoLuotKham(maLuotKham), rang);
        if (cu != null && cu.getMaDichVu() != null) {
            return null;
        }
        return "Chưa chọn dịch vụ đã làm cho răng " + rang.getSoFDI() + " nên dịch vụ này sẽ không được tính vào hóa đơn";
    }

    // Tìm bản ghi trong lượt khám sẽ bị sửa khi ghi nhận răng này; không có thì trả về null (nghĩa là thêm bản ghi mới).
    // Danh sách đầu vào đã sắp theo răng rồi bản mới nhất trước.
    private RangDieuTri timBanGhiCanSua(List<RangDieuTri> dsLuotKham, RangDieuTri rang) {
        String maDichVu = rongThanhNull(rang.getMaDichVu());
        RangDieuTri moiNhatCoDichVu = null; // bản ghi mới nhất cùng răng, cùng mặt, có dịch vụ (dùng khi dịch vụ để trống)
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

    // Bản ghi đại diện cho tình trạng hiện tại của một răng trong hồ sơ (như layTinhTrangMoiNhat); chưa từng ghi nhận thì null
    private RangDieuTri timTinhTrangHienTai(String maHoSo, int soFDI) {
        for (RangDieuTri r : layTinhTrangMoiNhat(maHoSo)) {
            if (r.getSoFDI() == soFDI) {
                return r;
            }
        }
        return null;
    }

    // Từ bản ghi mới nhất của một răng, chọn bản ghi đại diện: xét các bản ghi cùng răng, cùng lượt khám, cùng ngày;
    // mỗi mặt răng lấy bản mới nhất (bản ghi không có mặt răng là ghi cho cả răng nên thay thế mọi bản cũ hơn),
    // rồi lấy bản có trạng thái nặng nhất. Danh sách tatCa được sắp mới nhất trước.
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
                continue; // mặt này đã có bản ghi mới hơn
            }
            if (mucDoNang(r.getTrangThai()) > mucDoNang(chon.getTrangThai())) {
                chon = r;
            }
            if (mat.isEmpty()) {
                break; // bản ghi cho cả răng: các bản cũ hơn không còn ý nghĩa
            }
        }
        return chon;
    }

    // Hai bản ghi có cùng ngày ghi nhận không (thiếu thời điểm thì coi là cùng ngày)
    private boolean cungNgay(RangDieuTri a, RangDieuTri b) {
        if (a.getNgayGhiNhan() == null || b.getNgayGhiNhan() == null) {
            return true;
        }
        return a.getNgayGhiNhan().toLocalDate().equals(b.getNgayGhiNhan().toLocalDate());
    }

    // Mức độ nặng của trạng thái răng: lớn hơn là nặng hơn
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

    // Chuỗi không được dài hơn độ dài cột trong CSDL, nếu không SQL Server báo lỗi cắt chuỗi khó hiểu
    private static void doDai(String giaTri, int toiDa, String tenTruong) {
        if (giaTri != null && giaTri.length() > toiDa) {
            throw new NghiepVuException(tenTruong + " dài tối đa " + toiDa + " ký tự (hiện đang " + giaTri.length() + " ký tự)");
        }
    }


    // Chuỗi rỗng hoặc toàn khoảng trắng thì trả về null, ngược lại cắt khoảng trắng hai đầu
    private static String rongThanhNull(String s) {
        return s == null || s.trim().isEmpty() ? null : s.trim();
    }
}
