package nhakhoakim.dao;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import nhakhoakim.entity.BenhNhan;
import nhakhoakim.entity.DichVu;
import nhakhoakim.entity.DongThoiGian;
import nhakhoakim.entity.HangSo;
import nhakhoakim.entity.HoSoBenhAn;
import nhakhoakim.entity.LieuTrinh;
import nhakhoakim.entity.LuotKham;
import nhakhoakim.entity.LuotKhamDichVu;
import nhakhoakim.entity.RangDieuTri;
import nhakhoakim.entity.DongThoiGian.SuKienDieuTri;

// Dòng thời gian điều trị: gom đăng ký, liệu trình, lượt khám (kèm răng, dịch vụ, đơn thuốc) và hẹn tái khám
// của một bệnh nhân thành một trục thời gian, mới nhất trước. Chỉ đọc dữ liệu có sẵn, không ghi gì.
public class DongThoiGianService {

    private BenhNhanService benhNhanService = new BenhNhanService();
    private HoSoBenhAnService hoSoService = new HoSoBenhAnService();
    private LuotKhamService luotKhamService = new LuotKhamService();
    private RangDieuTriService rangService = new RangDieuTriService();
    private DichVuService dichVuService = new DichVuService();
    private NhaSiPhuTrachService nhaSiService = new NhaSiPhuTrachService();

    // Dựng dòng thời gian của một bệnh nhân
    public DongThoiGian xayDung(String maBN) {
        BenhNhan bn = benhNhanService.timTheoMa(maBN);
        if (bn == null) {
            throw new NghiepVuException("Không tìm thấy bệnh nhân " + maBN);
        }
        HoSoBenhAn hs = hoSoService.timTheoBenhNhan(maBN);
        DongThoiGian ketQua = new DongThoiGian();
        List<SuKienDieuTri> ds = ketQua.getSuKien();
        Map<String, DichVu> bangDichVu = layBangDichVu(); // nạp danh mục dịch vụ một lần rồi tra, không gọi lại trong vòng lặp
        List<LieuTrinh> dsLieuTrinh = hoSoService.layLieuTrinh(maBN);
        List<LuotKham> dsLuotKham = hoSoService.layLichSuKham(hs.getMaHoSo());
        List<String> dsMaNhaSi = new ArrayList<>(); // hỏi tên nha sĩ một lần cho cả liệu trình lẫn lượt khám
        for (LieuTrinh lt : dsLieuTrinh) {
            dsMaNhaSi.add(lt.getMaNhaSiPhuTrach());
        }
        for (LuotKham lk : dsLuotKham) {
            dsMaNhaSi.add(lk.getMaNhanVienPhuTrach());
        }
        Map<String, String> tenNhaSi = nhaSiService.layTen(dsMaNhaSi);

        if (bn.getNgayDangKy() != null) {
            ds.add(new SuKienDieuTri(bn.getNgayDangKy().atStartOfDay(), SuKienDieuTri.LOAI_DANG_KY, "Đăng ký bệnh nhân",
                    "Mã bệnh nhân " + bn.getMaBN() + ", mã hồ sơ " + hs.getMaHoSo()));
        }
        for (LieuTrinh lt : dsLieuTrinh) {
            if (lt.getNgayBatDau() != null) {
                ds.add(new SuKienDieuTri(lt.getNgayBatDau().atStartOfDay(), SuKienDieuTri.LOAI_LIEU_TRINH, "Bắt đầu liệu trình " + lt.getLoaiDichVu(),
                        "Dự kiến " + lt.getSoBuoiDuKien() + " buổi. Giai đoạn: " + chu(lt.getGiaiDoanHienTai()) + ". Trạng thái: " + lt.getTrangThai()
                        + dongNhaSi(" Nha sĩ phụ trách: ", tenNhaSi, lt.getMaNhaSiPhuTrach())));
            }
            if (lt.getNgayKetThucThucTe() != null) {
                ds.add(new SuKienDieuTri(lt.getNgayKetThucThucTe().atStartOfDay(), SuKienDieuTri.LOAI_LIEU_TRINH, "Kết thúc liệu trình " + lt.getLoaiDichVu(),
                        "Trạng thái: " + lt.getTrangThai()));
            }
        }

        LocalDate homNay = LocalDate.now();
        LocalDate ganNhat = null;
        int soLuotKham = 0;
        for (LuotKham lk : dsLuotKham) {
            ds.add(new SuKienDieuTri(lk.getGioBatDau(), SuKienDieuTri.LOAI_KHAM,
                    "Lượt khám " + lk.getMaLuotKham() + " (" + lk.getTrangThai() + ")", moTaLuotKham(lk, bangDichVu, tenNhaSi)));
            if (lk.getNgayTaiKham() != null) {
                // Hẹn tái khám chỉ có ngày: so theo ngày để hẹn hôm nay vẫn là "sắp tới" chứ không bị coi là đã qua
                boolean daQua = lk.getNgayTaiKham().isBefore(homNay);
                ds.add(new SuKienDieuTri(lk.getNgayTaiKham().atStartOfDay(), SuKienDieuTri.LOAI_TAI_KHAM, "Hẹn tái khám",
                        "Theo chỉ định của lượt khám " + lk.getMaLuotKham(), daQua));
            }
            if (HangSo.HOAN_THANH.equals(lk.getTrangThai())) {
                soLuotKham++;
                LocalDate ngay = lk.getGioBatDau().toLocalDate();
                if (ganNhat == null || ngay.isAfter(ganNhat)) {
                    ganNhat = ngay;
                }
            }
        }
        ds.sort((a, b) -> b.getThoiGian().compareTo(a.getThoiGian()));

        ketQua.setSoLuotKham(soLuotKham);
        ketQua.setLanKhamGanNhat(ganNhat);
        for (RangDieuTri r : rangService.layTinhTrangMoiNhat(hs.getMaHoSo())) {
            if (HangSo.RANG_DA_NHO.equals(r.getTrangThai())) {
                ketQua.setSoRangDaNho(ketQua.getSoRangDaNho() + 1);
            } else if (HangSo.RANG_IMPLANT.equals(r.getTrangThai()) || HangSo.RANG_CAU_RANG.equals(r.getTrangThai())) {
                ketQua.setSoRangImplantCauRang(ketQua.getSoRangImplantCauRang() + 1);
            }
        }
        ketQua.setSoLieuTrinhDangLam(hoSoService.layLieuTrinhDangThucHien(maBN).size());
        return ketQua;
    }

    // Danh mục dịch vụ dưới dạng bảng tra theo mã dịch vụ
    private Map<String, DichVu> layBangDichVu() {
        Map<String, DichVu> bang = new HashMap<>();
        for (DichVu dv : dichVuService.layTatCa()) {
            bang.put(dv.getMaDichVu(), dv);
        }
        return bang;
    }

    // Nội dung chi tiết của một lượt khám: lý do, chẩn đoán, hướng điều trị, đơn thuốc, răng và dịch vụ đã làm
    private String moTaLuotKham(LuotKham lk, Map<String, DichVu> bangDichVu, Map<String, String> tenNhaSi) {
        StringBuilder sb = new StringBuilder();
        them(sb, "Nha sĩ", NhaSiPhuTrachService.tenHoacMa(tenNhaSi, lk.getMaNhanVienPhuTrach()));
        them(sb, "Lý do", lk.getLyDoKham());
        them(sb, "Chẩn đoán", lk.getChanDoan());
        them(sb, "Hướng điều trị", lk.getHuongDieuTri());
        them(sb, "Đơn thuốc", lk.getDonThuoc() == null ? null : lk.getDonThuoc().replace("\n", "; "));
        StringBuilder rang = new StringBuilder();
        for (RangDieuTri r : rangService.layTheoLuotKham(lk.getMaLuotKham())) {
            if (rang.length() > 0) {
                rang.append("; ");
            }
            rang.append(r.getSoFDI()).append(": ").append(r.getTrangThai());
            DichVu dv = r.getMaDichVu() == null ? null : bangDichVu.get(r.getMaDichVu());
            if (dv != null) {
                rang.append(" (").append(dv.getTenDichVu()).append(")");
            } else if (r.getMaDichVu() != null) {
                rang.append(" (").append(r.getMaDichVu()).append(")"); // dịch vụ không còn trong danh mục thì hiện mã
            }
        }
        them(sb, "Răng", rang.toString());
        StringBuilder dichVu = new StringBuilder();
        for (LuotKhamDichVu x : luotKhamService.layDichVuKhongGanRang(lk.getMaLuotKham())) {
            DichVu dv = bangDichVu.get(x.getMaDichVu());
            if (dichVu.length() > 0) {
                dichVu.append("; ");
            }
            dichVu.append(dv == null ? x.getMaDichVu() : dv.getTenDichVu()).append(" x").append(x.getSoLuong());
        }
        them(sb, "Dịch vụ", dichVu.toString());
        if (lk.getGioKetThuc() != null && lk.getGioBatDau() != null) {
            LocalDateTime bd = lk.getGioBatDau();
            them(sb, "Thời gian khám", Duration.between(bd, lk.getGioKetThuc()).toMinutes() + " phút");
        }
        return sb.length() == 0 ? "(chưa ghi nội dung)" : sb.toString();
    }

    // Thêm một dòng "nhãn: giá trị" nếu giá trị không rỗng
    private void them(StringBuilder sb, String nhan, String giaTri) {
        if (giaTri != null && !giaTri.trim().isEmpty()) {
            if (sb.length() > 0) {
                sb.append("\n");
            }
            sb.append(nhan).append(": ").append(giaTri.trim());
        }
    }

    // Đoạn chữ ghi tên nha sĩ để nối vào mô tả; không có mã nha sĩ thì không thêm gì
    private String dongNhaSi(String nhan, Map<String, String> tenNhaSi, String maNhaSi) {
        return maNhaSi == null ? "" : "." + nhan + NhaSiPhuTrachService.tenHoacMa(tenNhaSi, maNhaSi);
    }

    // Chuỗi null thì đổi thành rỗng
    private String chu(String s) {
        return s == null ? "" : s;
    }
}
