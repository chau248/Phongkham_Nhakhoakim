package controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import dao.HoSoBenhAnDAO;
import dao.LieuTrinhDAO;
import dao.LuotKhamDAO;
import dao.NghiepVuException;
import dao.TienIchDAO;
import entity.BenhNhan;
import entity.DichVu;
import entity.HangSo;
import entity.HoSoBenhAn;
import entity.LieuTrinh;
import entity.LuotKham;
import entity.NhanVien;

// Nghiệp vụ hồ sơ bệnh án: xem, sửa thông tin y tế, đóng/mở hồ sơ, liệu trình và cảnh báo y tế
public class HoSoBenhAnController {

    // Từ khóa để cảnh báo trước thủ thuật (viết thường)
    private static final String[] THUOC_CHONG_DONG = {"warfarin", "aspirin", "clopidogrel", "plavix", "heparin", "rivaroxaban",
        "xarelto", "apixaban", "acenocoumarol", "dabigatran", "ticagrelor", "enoxaparin", "chống đông", "loãng máu"};
    private static final String[] ROI_LOAN_DONG_MAU = {"đông máu", "khó đông", "hemophilia", "máu loãng"};
    private static final String[] TIEU_DUONG = {"tiểu đường", "đái tháo đường"};
    private static final String[] TIM_HUYET_AP = {"huyết áp", "tim"};

    private HoSoBenhAnDAO hoSoDAO = new HoSoBenhAnDAO();
    private LieuTrinhDAO lieuTrinhDAO = new LieuTrinhDAO();
    private LuotKhamDAO luotKhamDAO = new LuotKhamDAO();

    // ----- Tìm -----

    // Hồ sơ của một bệnh nhân; chưa có thì báo lỗi
    public HoSoBenhAn timTheoBenhNhan(String maBN) {
        HoSoBenhAn hs = hoSoDAO.timTheoBenhNhan(maBN);
        if (hs == null) {
            throw new NghiepVuException("Bệnh nhân " + maBN + " chưa có hồ sơ bệnh án");
        }
        return hs;
    }

    // Hồ sơ theo mã hồ sơ; không có thì báo lỗi
    public HoSoBenhAn timTheoMa(String maHoSo) {
        HoSoBenhAn hs = hoSoDAO.timTheoMa(maHoSo);
        if (hs == null) {
            throw new NghiepVuException("Không tìm thấy hồ sơ " + maHoSo);
        }
        return hs;
    }

    // Lịch sử các lượt khám của hồ sơ, mới nhất trước
    public List<LuotKham> layLichSuKham(String maHoSo) {
        return luotKhamDAO.layTheoHoSo(maHoSo);
    }

    // Tất cả liệu trình của bệnh nhân (đang thực hiện xếp trước)
    public List<LieuTrinh> layLieuTrinh(String maBN) {
        return lieuTrinhDAO.layTheoBenhNhan(maBN);
    }

    // Chỉ các liệu trình đang thực hiện
    public List<LieuTrinh> layLieuTrinhDangThucHien(String maBN) {
        List<LieuTrinh> kq = new ArrayList<>();
        for (LieuTrinh lt : lieuTrinhDAO.layTheoBenhNhan(maBN)) {
            if (HangSo.LIEU_TRINH_DANG_THUC_HIEN.equals(lt.getTrangThai())) {
                kq.add(lt);
            }
        }
        return kq;
    }

    // ----- Thêm -----

    // Dựng hồ sơ rỗng cho bệnh nhân mới (chưa lưu; BenhNhanController lưu cùng bệnh nhân)
    public HoSoBenhAn taoDoiTuongHoSoRong(BenhNhan bn) {
        HoSoBenhAn hs = new HoSoBenhAn();
        hs.setMaHoSo(TienIchDAO.taoMa("HS"));
        hs.setMaBN(bn);
        hs.setNgayTao(LocalDate.now());
        hs.setTrangThaiHoSo(HangSo.HO_SO_DANG_MO);
        return hs;
    }

    // ----- Sửa -----

    // Cập nhật thông tin y tế (chỉ nha sĩ hoặc y tá); hồ sơ đã đóng thì không sửa được.
    // banDau là hồ sơ lúc mở form: chỉ trường nào người dùng thực sự đổi mới được ghi; trường đã bị người khác sửa thì báo lỗi.
    public void capNhatThongTinYTe(HoSoBenhAn hs, HoSoBenhAn banDau) {
        NhanVien nv = TienIchController.yeuCau(PhienLamViec::getNhanVienHienTai, "sửa thông tin y tế", HangSo.VAI_TRO_NHA_SI, HangSo.VAI_TRO_Y_TA);
        HoSoBenhAn cu = timTheoMa(hs.getMaHoSo());
        if (HangSo.HO_SO_DA_DONG.equals(cu.getTrangThaiHoSo())) {
            throw new NghiepVuException("Hồ sơ đã đóng, không thể sửa thông tin y tế");
        }
        String nhomMau = TienIchController.rongThanhNull(hs.getNhomMau());
        if (nhomMau != null && !List.of("A", "B", "AB", "O").contains(nhomMau)) {
            throw new NghiepVuException("Nhóm máu chỉ có thể là A, B, AB hoặc O");
        }
        String tienSu = TienIchController.rongThanhNull(hs.getTienSuBenh());
        String benhNen = TienIchController.rongThanhNull(hs.getBenhNen());
        String thuoc = TienIchController.rongThanhNull(hs.getThuocDangSuDung());
        String ghiChu = TienIchController.rongThanhNull(hs.getGhiChuDacBiet());
        if (banDau != null) {
            nhomMau = gopTruong(TienIchController.rongThanhNull(banDau.getNhomMau()), nhomMau, TienIchController.rongThanhNull(cu.getNhomMau()), "Nhóm máu");
            tienSu = gopTruong(TienIchController.rongThanhNull(banDau.getTienSuBenh()), tienSu, TienIchController.rongThanhNull(cu.getTienSuBenh()), "Tiền sử bệnh");
            benhNen = gopTruong(TienIchController.rongThanhNull(banDau.getBenhNen()), benhNen, TienIchController.rongThanhNull(cu.getBenhNen()), "Bệnh nền");
            thuoc = gopTruong(TienIchController.rongThanhNull(banDau.getThuocDangSuDung()), thuoc, TienIchController.rongThanhNull(cu.getThuocDangSuDung()), "Thuốc đang sử dụng");
            ghiChu = gopTruong(TienIchController.rongThanhNull(banDau.getGhiChuDacBiet()), ghiChu, TienIchController.rongThanhNull(cu.getGhiChuDacBiet()), "Ghi chú đặc biệt");
        }
        TienIchController.doDai(tienSu, 1000, "Tiền sử bệnh");
        TienIchController.doDai(benhNen, 500, "Bệnh nền");
        TienIchController.doDai(thuoc, 500, "Thuốc đang sử dụng");
        TienIchController.doDai(ghiChu, 500, "Ghi chú đặc biệt");

        HoSoBenhAn moi = new HoSoBenhAn();
        moi.setMaHoSo(cu.getMaHoSo());
        moi.setTienSuBenh(tienSu);
        moi.setNhomMau(nhomMau);
        moi.setBenhNen(benhNen);
        moi.setThuocDangSuDung(thuoc);
        moi.setGhiChuDacBiet(ghiChu);
        hoSoDAO.capNhatThongTinYTe(moi);

        // Ghi nhật ký những giá trị thực sự đổi
        ghiNhatKyNeuDoi(nv.getMaNV(), cu.getMaHoSo(), "Tiền sử bệnh", cu.getTienSuBenh(), tienSu);
        ghiNhatKyNeuDoi(nv.getMaNV(), cu.getMaHoSo(), "Nhóm máu", cu.getNhomMau(), nhomMau);
        ghiNhatKyNeuDoi(nv.getMaNV(), cu.getMaHoSo(), "Bệnh nền", cu.getBenhNen(), benhNen);
        ghiNhatKyNeuDoi(nv.getMaNV(), cu.getMaHoSo(), "Thuốc đang sử dụng", cu.getThuocDangSuDung(), thuoc);
        ghiNhatKyNeuDoi(nv.getMaNV(), cu.getMaHoSo(), "Ghi chú đặc biệt", cu.getGhiChuDacBiet(), ghiChu);

        // Trả giá trị đã chuẩn hóa về đối tượng truyền vào
        hs.setTienSuBenh(tienSu);
        hs.setNhomMau(nhomMau);
        hs.setBenhNen(benhNen);
        hs.setThuocDangSuDung(thuoc);
        hs.setGhiChuDacBiet(ghiChu);
    }

    // Đóng hoặc mở lại hồ sơ (chỉ Quản lý); không đóng khi còn lượt khám đang dở
    public void doiTrangThaiHoSo(String maHoSo, String trangThaiMoi) {
        NhanVien nv = TienIchController.yeuCau(PhienLamViec::getNhanVienHienTai, "đóng hoặc mở lại hồ sơ bệnh án", HangSo.VAI_TRO_QUAN_LY);
        if (!HangSo.HO_SO_DANG_MO.equals(trangThaiMoi) && !HangSo.HO_SO_DA_DONG.equals(trangThaiMoi)) {
            throw new NghiepVuException("Trạng thái hồ sơ chỉ có thể là Đang mở hoặc Đã đóng");
        }
        HoSoBenhAn hs = timTheoMa(maHoSo);
        if (trangThaiMoi.equals(hs.getTrangThaiHoSo())) {
            throw new NghiepVuException("Hồ sơ đã ở trạng thái " + trangThaiMoi);
        }
        if (HangSo.HO_SO_DA_DONG.equals(trangThaiMoi)) {
            for (LuotKham lk : luotKhamDAO.layTheoHoSo(maHoSo)) {
                if (HangSo.DANG_KHAM.equals(lk.getTrangThai())) {
                    throw new NghiepVuException("Hồ sơ còn lượt khám chưa hoàn tất (" + lk.getMaLuotKham() + "), không thể đóng");
                }
            }
        }
        hoSoDAO.capNhatTrangThaiHoSo(maHoSo, trangThaiMoi);
        TienIchController.ghiNhatKy(nv.getMaNV(), "Đổi trạng thái hồ sơ", "HoSoBenhAn", maHoSo,
                "Từ [" + hs.getTrangThaiHoSo() + "] sang [" + trangThaiMoi + "]");
    }

    // ----- Cảnh báo (không chặn) -----

    // Dịch vụ cần liệu trình mà bệnh nhân chưa có liệu trình đang thực hiện; không cần cảnh báo thì null
    public String canhBaoLieuTrinh(String maBN, DichVu dv) {
        if (dv == null || !dv.isYeuCauLieuTrinh() || !layLieuTrinhDangThucHien(maBN).isEmpty()) {
            return null;
        }
        return "Dịch vụ \"" + dv.getTenDichVu() + "\" cần theo liệu trình nhưng bệnh nhân chưa có liệu trình đang thực hiện.";
    }

    // Cảnh báo về bệnh nền, thuốc đang dùng và tuổi khi làm dịch vụ; không cần cảnh báo thì null
    public String canhBaoYTe(String maBN, DichVu dv) {
        if (dv == null) {
            return null;
        }
        HoSoBenhAn hs = hoSoDAO.timTheoBenhNhan(maBN);
        BenhNhan bn = hs == null ? null : hs.getMaBN();
        String loai = dv.getLoaiDichVu() == null ? "" : dv.getLoaiDichVu();
        boolean xamLan = loai.equals(HangSo.LOAI_NHO_RANG) || loai.equals(HangSo.LOAI_NOI_NHA) || loai.equals(HangSo.LOAI_IMPLANT);
        List<String> ds = new ArrayList<>();
        if (xamLan && hs != null) {
            String thuoc = hs.getThuocDangSuDung() == null ? "" : hs.getThuocDangSuDung().toLowerCase();
            String chongDong = timTuKhoa(thuoc, THUOC_CHONG_DONG);
            if (chongDong != null) {
                ds.add("Bệnh nhân đang dùng thuốc chống đông (" + chongDong + "): có thể khó cầm máu, cần hỏi bác sĩ điều trị trước khi làm \"" + dv.getTenDichVu() + "\".");
            }
            String benhNen = hs.getBenhNen() == null ? "" : hs.getBenhNen().toLowerCase();
            if (timTuKhoa(benhNen, TIEU_DUONG) != null) {
                ds.add("Bệnh nhân tiểu đường: kiểm tra đường huyết trước khi làm, vết thương lâu lành và dễ nhiễm trùng.");
            }
            if (timTuKhoa(benhNen, TIM_HUYET_AP) != null) {
                ds.add("Bệnh nhân có bệnh tim mạch hoặc huyết áp: đo huyết áp trước khi làm, thận trọng với thuốc tê có adrenaline.");
            }
            if (timTuKhoa(benhNen, ROI_LOAN_DONG_MAU) != null) {
                ds.add("Bệnh nhân bị rối loạn đông máu: cần hội chẩn bác sĩ chuyên khoa trước khi làm thủ thuật chảy máu.");
            }
        }
        if (bn != null && bn.getNgaySinh() != null) {
            int tuoi = bn.tinhTuoi();
            if (loai.equals(HangSo.LOAI_IMPLANT) && tuoi < 18) {
                ds.add("Bệnh nhân " + tuoi + " tuổi: xương hàm còn phát triển, thường chưa chỉ định cấy ghép Implant.");
            }
            if (loai.equals(HangSo.LOAI_THAM_MY) && tuoi < 16) {
                ds.add("Bệnh nhân " + tuoi + " tuổi: không khuyến cáo làm dịch vụ thẩm mỹ như tẩy trắng răng.");
            }
        } else if (bn != null && (loai.equals(HangSo.LOAI_IMPLANT) || loai.equals(HangSo.LOAI_THAM_MY))) {
            ds.add("Chưa có ngày sinh nên không kiểm tra được giới hạn tuổi.");
        }
        return ds.isEmpty() ? null : String.join("\n", ds);
    }

    // ----- Hàm phụ -----

    // Gộp một trường khi hai người cùng sửa: mình không đổi thì giữ giá trị hiện có; mình đổi mà không ai khác đổi thì lấy giá trị mới;
    // cả hai cùng đổi khác nhau thì báo lỗi
    private String gopTruong(String banDau, String moi, String hienCo, String tenTruong) {
        if (Objects.equals(moi, banDau)) {
            return hienCo;
        }
        if (Objects.equals(hienCo, banDau) || Objects.equals(hienCo, moi)) {
            return moi;
        }
        throw new NghiepVuException("Trường \"" + tenTruong + "\" đã được người khác sửa từ lúc bạn mở hồ sơ. Hãy xem lại hồ sơ rồi sửa lại.");
    }

    // Ghi nhật ký nếu giá trị cũ và mới khác nhau (null và rỗng coi như nhau)
    private void ghiNhatKyNeuDoi(String maNV, String maHoSo, String tenTruong, String cu, String moi) {
        String a = TienIchController.rongThanhNull(cu);
        String b = TienIchController.rongThanhNull(moi);
        if (!Objects.equals(a, b)) {
            TienIchController.ghiNhatKy(maNV, "Sửa thông tin y tế", "HoSoBenhAn", maHoSo,
                    tenTruong + ": từ [" + (a == null ? "" : a) + "] sang [" + (b == null ? "" : b) + "]");
        }
    }

    // Từ khóa đầu tiên trong danh sách mà chuỗi có chứa (bỏ qua chỗ đứng sau chữ phủ định như "không", "chưa")
    private String timTuKhoa(String chuoi, String[] tuKhoa) {
        for (String t : tuKhoa) {
            int vt = chuoi.indexOf(t);
            while (vt >= 0) {
                String truoc = chuoi.substring(Math.max(0, vt - 10), vt);
                if (!truoc.contains("không") && !truoc.contains("chưa") && !truoc.contains("ko ")) {
                    return t;
                }
                vt = chuoi.indexOf(t, vt + 1);
            }
        }
        return null;
    }
}
