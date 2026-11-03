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

// Gợi ý điều trị từ sơ đồ răng
public class GoiYDieuTriController {

    private static final int PHAN_TRAM_VAT = 8;
    private static final int SO_THANG_VE_SINH = 6;
    private static final int TUOI_DU_CAY_GHEP = 18; // Dưới tuổi này không gợi ý
    private static final int TOI_DA_SO_LUONG = 10; // Số lượng tối đa của một dịch vụ
    private static final String TU_KHOA_CAU_RANG = "cầu"; // Tên dịch vụ chứa từ này

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
    private Supplier<NhanVien> nguoiDung; // Trả về người đang đăng nhập

    // Dùng trong chương trình
    public GoiYDieuTriController() {
        this(new LuotKhamController(), new HoSoBenhAnController(), new RangDieuTriController(), new DichVuController(), new BenhNhanDAO(),
                PhienLamViec::getNhanVienHienTai);
    }

    // Dùng khi kiểm thử
    public GoiYDieuTriController(LuotKhamController luotKhamService, HoSoBenhAnController hoSoService, RangDieuTriController rangService,
            DichVuController dichVuService, BenhNhanDAO benhNhanDAO, Supplier<NhanVien> nguoiDung) {
        this.luotKhamService = luotKhamService;
        this.hoSoService = hoSoService;
        this.rangService = rangService;
        this.dichVuService = dichVuService;
        this.benhNhanDAO = benhNhanDAO;
        this.nguoiDung = nguoiDung;
    }

    // Danh sách gợi ý cho một lượt khám
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

    // Chi phí tạm tính của lượt khám
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

    // Tiền nếu thêm các gợi ý
    public long tinhTienThem(List<GoiYDieuTri> cacGoiY) {
        long tong = 0;
        for (GoiYDieuTri g : cacGoiY) {
            tong += g.getDichVu().getGia();
        }
        return tong + Math.round(tong * PHAN_TRAM_VAT / 100.0);
    }

    // Thêm dịch vụ của một gợi
    public void themVaoLuotKham(String maLuotKham, GoiYDieuTri goiY) {
        List<GoiYDieuTri> mot = new ArrayList<>();
        mot.add(goiY);
        themNhieuVaoLuotKham(maLuotKham, mot);
    }

    // Thêm dịch vụ của nhiều gợi
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
        Map<String, Integer> soLuong = new LinkedHashMap<>(); // Mã dịch vụ -> số lượng sẽ thêm
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

    // Phần trăm VAT đang dùng
    public int getPhanTramThue() {
        return PHAN_TRAM_VAT;
    }

    // Có được gợi ý Implant / cầu răng
    private boolean duocGoiYCayGhep(int soFDI, boolean duoi18Tuoi) {
        return !RangDieuTri.laRangSua(soFDI) && !RangDieuTri.laRangKhon(soFDI) && !duoi18Tuoi;
    }

    // Bệnh nhân dưới 18 tuổi không
    private boolean laDuoi18Tuoi(String maBN) {
        BenhNhan bn = benhNhanDAO.timTheoMa(maBN);
        if (bn == null || bn.getNgaySinh() == null) {
            return false;
        }
        return bn.tinhTuoi() < TUOI_DU_CAY_GHEP;
    }

    // Thêm một gợi ý theo loại dịch vụ
    private void them(List<GoiYDieuTri> kq, int soRang, String mucDo, String lyDo, String loai, Set<String> daCo, List<LieuTrinh> dangLam) {
        them(kq, soRang, mucDo, lyDo, loai, daCo, dangLam, false);
    }

    // Như trên
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

    // Số lượt khám khác nhau
    private int demLuotKhamSau(List<RangDieuTri> lichSu, int soFDI) {
        Set<String> luot = new HashSet<>();
        for (RangDieuTri x : lichSu) {
            if (x.getSoFDI() == soFDI && HangSo.RANG_SAU.equals(x.getTrangThai())) {
                luot.add(x.getMaLuotKham());
            }
        }
        return luot.size();
    }

    // Bệnh nhân có liệu trình
    private boolean coLieuTrinh(List<LieuTrinh> dangLam, String loai) {
        for (LieuTrinh lt : dangLam) {
            if (loai.equals(lt.getLoaiDichVu())) {
                return true;
            }
        }
        return false;
    }

    // Đã lấy cao răng
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

    // Nạp một lần danh mục dịch vụ vào bảng tra
    private Map<String, DichVu> napBangDichVu() {
        Map<String, DichVu> bang = new HashMap<>();
        for (DichVu dv : dichVuService.layTatCa()) {
            bang.put(dv.getMaDichVu(), dv);
        }
        return bang;
    }

    // Tra dịch vụ trong bảng đã nạp
    private DichVu timDichVu(Map<String, DichVu> bang, String ma) {
        if (!bang.containsKey(ma)) {
            bang.put(ma, dichVuService.timTheoMa(ma));
        }
        return bang.get(ma);
    }

    // Thứ tự ưu tiên của mức độ
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
