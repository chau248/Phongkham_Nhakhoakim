package nhakhoakim.controller;

import nhakhoakim.dao.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import nhakhoakim.entity.BenhNhan;
import nhakhoakim.entity.DichVu;
import nhakhoakim.entity.GoiYDieuTri;
import nhakhoakim.entity.HangSo;
import nhakhoakim.entity.HoSoBenhAn;
import nhakhoakim.entity.LieuTrinh;
import nhakhoakim.entity.LuotKham;
import nhakhoakim.entity.LuotKhamDichVu;
import nhakhoakim.entity.NhanVien;
import nhakhoakim.entity.RangDieuTri;
import nhakhoakim.entity.GoiYDieuTri.UocTinhChiPhi;

// Gợi ý điều trị từ sơ đồ răng và lịch sử khám, kèm ước tính chi phí của lượt khám đang khám.
// Quy tắc gợi ý (chỉ là gợi ý, nha sĩ quyết định):
//  - Răng sâu: gợi ý trám; nếu răng đã bị ghi nhận sâu ở từ 2 lượt khám trở lên thì gợi ý điều trị tủy (áp dụng cả răng sữa).
//  - Răng đã nhổ ở lượt khám trước mà chưa phục hình: gợi ý Implant hoặc cầu răng (trừ khi đang có liệu trình Implant).
//    Không gợi ý Implant / cầu răng cho răng sữa, răng khôn (số 8) hoặc bệnh nhân dưới 18 tuổi.
//  - Chưa lấy cao răng trong 6 tháng gần đây: gợi ý lấy cao răng định kỳ.
public class GoiYDieuTriController {

    private static final int PHAN_TRAM_VAT = 8;
    private static final int SO_THANG_VE_SINH = 6;
    private static final int TUOI_DU_CAY_GHEP = 18;       // dưới tuổi này không gợi ý Implant / cầu răng
    private static final int TOI_DA_SO_LUONG = 10;        // số lượng tối đa của một dịch vụ trong lượt khám (giống LuotKhamController)
    private static final String TU_KHOA_CAU_RANG = "cầu"; // tên dịch vụ chứa từ này là dịch vụ cầu răng

    // Mức độ gợi ý
    private static final String MUC_GAP = "Nên làm gấp";
    private static final String MUC_NEN_LAM = "Nên làm";
    private static final String MUC_CAN_NHAC = "Cân nhắc";
    private static final String MUC_DINH_KY = "Định kỳ";

    private LuotKhamController luotKhamService;
    private HoSoBenhAnController hoSoService;
    private RangDieuTriController rangService;
    private DichVuController dichVuService;
    private BenhNhanDAO benhNhanDAO;
    private Supplier<NhanVien> nguoiDung; // trả về người đang đăng nhập

    // Dùng trong chương trình: các phụ thuộc mặc định, người dùng lấy từ phiên làm việc
    public GoiYDieuTriController() {
        this(new LuotKhamController(), new HoSoBenhAnController(), new RangDieuTriController(), new DichVuController(), new BenhNhanDAO(),
                PhienLamViec::getNhanVienHienTai);
    }

    // Dùng khi kiểm thử: truyền vào các phụ thuộc và người đang đăng nhập
    public GoiYDieuTriController(LuotKhamController luotKhamService, HoSoBenhAnController hoSoService, RangDieuTriController rangService,
            DichVuController dichVuService, BenhNhanDAO benhNhanDAO, Supplier<NhanVien> nguoiDung) {
        this.luotKhamService = luotKhamService;
        this.hoSoService = hoSoService;
        this.rangService = rangService;
        this.dichVuService = dichVuService;
        this.benhNhanDAO = benhNhanDAO;
        this.nguoiDung = nguoiDung;
    }

    // Danh sách gợi ý cho một lượt khám, việc gấp xếp trước
    public List<GoiYDieuTri> goiY(String maLuotKham) {
        LuotKham lk = luotKhamService.timTheoMa(maLuotKham);
        HoSoBenhAn hs = hoSoService.timTheoMa(lk.getMaHoSo());
        List<LieuTrinh> dangLam = hoSoService.layLieuTrinhDangThucHien(hs.getMaBN());
        List<RangDieuTri> lichSuRang = rangService.layTatCaTheoHoSo(hs.getMaHoSo());
        Set<String> daCoTrongLuotKham = new HashSet<>();
        for (LuotKhamDichVu x : luotKhamService.layDichVuDaThucHien(maLuotKham)) {
            daCoTrongLuotKham.add(x.getMaDichVu());
        }
        boolean duoi18Tuoi = laDuoi18Tuoi(hs.getMaBN());

        List<GoiYDieuTri> kq = new ArrayList<>();
        for (RangDieuTri r : rangService.layTinhTrangMoiNhat(hs.getMaHoSo())) {
            if (HangSo.RANG_SAU.equals(r.getTrangThai())) {
                int soLuot = demLuotKhamSau(lichSuRang, r.getSoFDI());
                if (soLuot >= 2) {
                    them(kq, r.getSoFDI(), MUC_GAP, "Răng " + r.getSoFDI() + " bị sâu, đã ghi nhận ở " + soLuot
                            + " lượt khám mà chưa xử lý, sâu kéo dài dễ viêm tủy", HangSo.LOAI_NOI_NHA, daCoTrongLuotKham, dangLam);
                } else {
                    them(kq, r.getSoFDI(), MUC_NEN_LAM, "Răng " + r.getSoFDI() + " bị sâu, chưa được điều trị", HangSo.LOAI_TRAM,
                            daCoTrongLuotKham, dangLam);
                }
            } else if (HangSo.RANG_DA_NHO.equals(r.getTrangThai()) && !maLuotKham.equals(r.getMaLuotKham())
                    && !coLieuTrinh(dangLam, HangSo.LOAI_IMPLANT) && duocGoiYCayGhep(r.getSoFDI(), duoi18Tuoi)) {
                String ly = "Răng " + r.getSoFDI() + " đã nhổ ở lượt khám trước, chưa phục hình; mất răng lâu gây tiêu xương và làm răng bên cạnh xô lệch";
                them(kq, r.getSoFDI(), MUC_CAN_NHAC, ly + " (phương án cấy ghép)", HangSo.LOAI_IMPLANT, daCoTrongLuotKham, dangLam);
                them(kq, r.getSoFDI(), MUC_CAN_NHAC, ly + " (phương án cầu răng)", HangSo.LOAI_PHUC_HINH, daCoTrongLuotKham, dangLam, true);
            }
        }
        if (!daVeSinhGanDay(hs.getMaHoSo())) {
            them(kq, 0, MUC_DINH_KY, "Chưa lấy cao răng trong " + SO_THANG_VE_SINH + " tháng gần đây", HangSo.LOAI_VE_SINH,
                    daCoTrongLuotKham, dangLam);
        }
        kq.sort((a, b) -> Integer.compare(thuTu(a.getMucDo()), thuTu(b.getMucDo())));
        return kq;
    }

    // Chi phí tạm tính của lượt khám: các dịch vụ đã ghi (gắn răng và không gắn răng) cộng VAT
    public UocTinhChiPhi uocTinhChiPhi(String maLuotKham) {
        UocTinhChiPhi u = new UocTinhChiPhi();
        Map<String, DichVu> bang = napBangDichVu();
        for (LuotKhamDichVu x : luotKhamService.layDichVuDaThucHien(maLuotKham)) {
            DichVu dv = timDichVu(bang, x.getMaDichVu());
            if (dv != null) {
                u.getCacDong().add(new UocTinhChiPhi.Dong(dv.getTenDichVu(), x.getSoLuong(), dv.getGia()));
            }
        }
        u.tinhTong(PHAN_TRAM_VAT);
        return u;
    }

    // Tiền (đã gồm VAT) nếu thêm các gợi ý được chọn vào lượt khám
    public long tinhTienThem(List<GoiYDieuTri> cacGoiY) {
        long tong = 0;
        for (GoiYDieuTri g : cacGoiY) {
            tong += g.getDichVu().getGia();
        }
        return tong + Math.round(tong * PHAN_TRAM_VAT / 100.0);
    }

    // Thêm dịch vụ của một gợi ý vào lượt khám đang khám
    public void themVaoLuotKham(String maLuotKham, GoiYDieuTri goiY) {
        List<GoiYDieuTri> mot = new ArrayList<>();
        mot.add(goiY);
        themNhieuVaoLuotKham(maLuotKham, mot);
    }

    // Thêm dịch vụ của nhiều gợi ý vào lượt khám đang khám. Kiểm tra toàn bộ trước (lượt khám đang khám và đúng nha sĩ phụ trách,
    // dịch vụ còn hoạt động, chưa có trong lượt khám) rồi mới thêm, nên lỗi không làm thêm dở. Hai gợi ý cùng một dịch vụ
    // (ví dụ Implant cho hai răng) được cộng thành số lượng 2.
    public void themNhieuVaoLuotKham(String maLuotKham, List<GoiYDieuTri> ds) {
        if (ds == null || ds.isEmpty()) {
            throw new NghiepVuException("Chưa chọn gợi ý nào để thêm");
        }
        LuotKham lk = luotKhamService.timTheoMa(maLuotKham);
        if (!HangSo.DANG_KHAM.equals(lk.getTrangThai())) {
            throw new NghiepVuException("Chỉ thêm dịch vụ cho lượt khám đang khám");
        }
        NhanVien nv = nguoiDung.get();
        if (nv == null || lk.getMaNhanVienPhuTrach() == null || !lk.getMaNhanVienPhuTrach().equals(nv.getMaNV())) {
            throw new NghiepVuException("Chỉ nha sĩ phụ trách mới được thêm dịch vụ vào lượt khám này");
        }
        Set<String> daCo = new HashSet<>();
        for (LuotKhamDichVu x : luotKhamService.layDichVuDaThucHien(maLuotKham)) {
            daCo.add(x.getMaDichVu());
        }
        Map<String, Integer> soLuong = new LinkedHashMap<>(); // mã dịch vụ -> số lượng sẽ thêm
        for (GoiYDieuTri g : ds) {
            String ma = g.getDichVu().getMaDichVu();
            DichVu dv = dichVuService.timTheoMa(ma);
            if (dv == null || !dv.isTrangThaiHoatDong()) {
                throw new NghiepVuException("Dịch vụ \"" + g.getDichVu().getTenDichVu() + "\" không tồn tại hoặc đã ngừng cung cấp");
            }
            if (daCo.contains(ma)) {
                throw new NghiepVuException("Dịch vụ \"" + dv.getTenDichVu() + "\" đã có trong lượt khám này");
            }
            int moi = soLuong.containsKey(ma) ? soLuong.get(ma) + 1 : 1;
            if (moi > TOI_DA_SO_LUONG) {
                throw new NghiepVuException("Số lượng dịch vụ \"" + dv.getTenDichVu() + "\" tối đa " + TOI_DA_SO_LUONG);
            }
            soLuong.put(ma, moi);
        }
        for (Map.Entry<String, Integer> e : soLuong.entrySet()) {
            luotKhamService.themDichVu(maLuotKham, e.getKey(), e.getValue());
        }
    }

    // Phần trăm VAT đang dùng để hiện trên màn hình
    public int getPhanTramThue() {
        return PHAN_TRAM_VAT;
    }

    // Có được gợi ý Implant / cầu răng cho răng này không: răng sữa, răng khôn và bệnh nhân dưới 18 tuổi thì không
    private boolean duocGoiYCayGhep(int soFDI, boolean duoi18Tuoi) {
        return !RangDieuTri.laRangSua(soFDI) && !RangDieuTri.laRangKhon(soFDI) && !duoi18Tuoi;
    }

    // Bệnh nhân dưới 18 tuổi không; chưa biết ngày sinh thì coi như không dưới 18
    private boolean laDuoi18Tuoi(String maBN) {
        BenhNhan bn = benhNhanDAO.timTheoMa(maBN);
        if (bn == null || bn.getNgaySinh() == null) {
            return false;
        }
        return bn.tinhTuoi() < TUOI_DU_CAY_GHEP;
    }

    // Thêm một gợi ý theo loại dịch vụ (lấy dịch vụ đầu tiên của loại đó); bỏ qua nếu không có dịch vụ hoặc lượt khám đã có
    private void them(List<GoiYDieuTri> kq, int soRang, String mucDo, String lyDo, String loai, Set<String> daCo, List<LieuTrinh> dangLam) {
        them(kq, soRang, mucDo, lyDo, loai, daCo, dangLam, false);
    }

    // Như trên; chiCauRang = true thì chỉ chọn dịch vụ cầu răng, không chọn Implant
    private void them(List<GoiYDieuTri> kq, int soRang, String mucDo, String lyDo, String loai, Set<String> daCo, List<LieuTrinh> dangLam,
            boolean chiCauRang) {
        for (DichVu dv : dichVuService.timTheoLoai(loai)) {
            if (!dv.isTrangThaiHoatDong() || daCo.contains(dv.getMaDichVu())) {
                continue;
            }
            if (chiCauRang && !dv.getTenDichVu().toLowerCase().contains(TU_KHOA_CAU_RANG)) {
                continue;
            }
            String luuY = dv.isYeuCauLieuTrinh() && dangLam.isEmpty()
                    ? "Dịch vụ này cần theo liệu trình, bệnh nhân chưa có liệu trình đang thực hiện" : "";
            kq.add(new GoiYDieuTri(soRang, mucDo, lyDo, dv, luuY));
            return;
        }
    }

    // Số lượt khám khác nhau mà răng này được ghi nhận là Sâu
    private int demLuotKhamSau(List<RangDieuTri> lichSu, int soFDI) {
        Set<String> luot = new HashSet<>();
        for (RangDieuTri x : lichSu) {
            if (x.getSoFDI() == soFDI && HangSo.RANG_SAU.equals(x.getTrangThai())) {
                luot.add(x.getMaLuotKham());
            }
        }
        return luot.size();
    }

    // Bệnh nhân có liệu trình đang thực hiện thuộc loại dịch vụ này không
    private boolean coLieuTrinh(List<LieuTrinh> dangLam, String loai) {
        for (LieuTrinh lt : dangLam) {
            if (loai.equals(lt.getLoaiDichVu())) {
                return true;
            }
        }
        return false;
    }

    // Đã lấy cao răng (dịch vụ loại Vệ sinh) trong 6 tháng gần đây chưa
    private boolean daVeSinhGanDay(String maHoSo) {
        LocalDate moc = LocalDate.now().minusMonths(SO_THANG_VE_SINH);
        Map<String, DichVu> bang = napBangDichVu();
        for (LuotKham lk : hoSoService.layLichSuKham(maHoSo)) {
            if (lk.getGioBatDau().toLocalDate().isBefore(moc)) {
                continue;
            }
            for (LuotKhamDichVu x : luotKhamService.layDichVuDaThucHien(lk.getMaLuotKham())) {
                DichVu dv = timDichVu(bang, x.getMaDichVu());
                if (dv != null && HangSo.LOAI_VE_SINH.equals(dv.getLoaiDichVu())) {
                    return true;
                }
            }
        }
        return false;
    }

    // Nạp một lần danh mục dịch vụ vào bảng tra theo mã (tránh gọi tìm từng dịch vụ trong vòng lặp)
    private Map<String, DichVu> napBangDichVu() {
        Map<String, DichVu> bang = new HashMap<>();
        for (DichVu dv : dichVuService.layTatCa()) {
            bang.put(dv.getMaDichVu(), dv);
        }
        return bang;
    }

    // Tra dịch vụ trong bảng đã nạp; không có (ví dụ dịch vụ đã ngừng) thì tìm theo mã và nhớ lại; vẫn không có thì null
    private DichVu timDichVu(Map<String, DichVu> bang, String ma) {
        if (!bang.containsKey(ma)) {
            bang.put(ma, dichVuService.timTheoMa(ma));
        }
        return bang.get(ma);
    }

    // Thứ tự ưu tiên của mức độ: nhỏ hơn thì hiện trước
    private int thuTu(String mucDo) {
        if (MUC_GAP.equals(mucDo)) {
            return 0;
        }
        if (MUC_NEN_LAM.equals(mucDo)) {
            return 1;
        }
        if (MUC_CAN_NHAC.equals(mucDo)) {
            return 2;
        }
        return 3;
    }
}
