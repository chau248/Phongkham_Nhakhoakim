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

// Nghiệp vụ hồ sơ bệnh án: xem
public class HoSoBenhAnController {

    private HoSoBenhAnDAO hoSoDAO = new HoSoBenhAnDAO();
    private LieuTrinhDAO lieuTrinhDAO = new LieuTrinhDAO();
    private LuotKhamDAO luotKhamDAO = new LuotKhamDAO();

    // Hồ sơ của một bệnh nhân
    public HoSoBenhAn timTheoBenhNhan(String maBN) {
        HoSoBenhAn hs = hoSoDAO.timTheoBenhNhan(maBN);
        if (hs == null) {
            throw new NghiepVuException("Bệnh nhân " + maBN + " chưa có hồ sơ bệnh án");
        }
        return hs;
    }

    // Hồ sơ theo mã hồ sơ
    public HoSoBenhAn timTheoMa(String maHoSo) {
        HoSoBenhAn hs = hoSoDAO.timTheoMa(maHoSo);
        if (hs == null) {
            throw new NghiepVuException("Không tìm thấy hồ sơ " + maHoSo);
        }
        return hs;
    }

    // Lịch sử các lượt khám của hồ sơ
    public List<LuotKham> layLichSuKham(String maHoSo) {
        return luotKhamDAO.layTheoHoSo(maHoSo);
    }

    // Tất cả liệu trình của bệnh nhân
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

    // Dựng hồ sơ rỗng cho bệnh nhân mới
    public HoSoBenhAn taoDoiTuongHoSoRong(BenhNhan bn) {
        HoSoBenhAn hs = new HoSoBenhAn();
        hs.setMaHoSo(TienIchDAO.taoMa("HS"));
        hs.setMaBN(bn);
        hs.setNgayTao(LocalDate.now());
        hs.setTrangThaiHoSo(HangSo.HO_SO_DANG_MO);
        return hs;
    }

    // Cập nhật thông tin y tế
    // banDau là hồ sơ lúc mở form: chỉ trường nào người dùng thực sự đổi mới được ghi
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

    // Đóng hoặc mở lại hồ sơ
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
    }

    // Dịch vụ cần liệu trình mà bệnh nhân chưa có liệu trình đang thực hiện
    public String canhBaoLieuTrinh(String maBN, DichVu dv) {
        if (dv == null || !dv.isYeuCauLieuTrinh() || !layLieuTrinhDangThucHien(maBN).isEmpty()) {
            return null;
        }
        return "Dịch vụ \"" + dv.getTenDichVu() + "\" cần theo liệu trình nhưng bệnh nhân chưa có liệu trình đang thực hiện.";
    }

    // Gộp một trường khi hai người cùng sửa: mình không đổi
    // cả hai cùng đổi khác nhau
    private String gopTruong(String banDau, String moi, String hienCo, String tenTruong) {
        if (Objects.equals(moi, banDau)) {
            return hienCo;
        }
        if (Objects.equals(hienCo, banDau) || Objects.equals(hienCo, moi)) {
            return moi;
        }
        throw new NghiepVuException("Trường \"" + tenTruong + "\" đã được người khác sửa từ lúc bạn mở hồ sơ. Hãy xem lại hồ sơ rồi sửa lại.");
    }

    // Ghi nhật ký nếu giá trị cũ và mới khác nhau
    private void ghiNhatKyNeuDoi(String maNV, String maHoSo, String tenTruong, String cu, String moi) {
        String a = TienIchController.rongThanhNull(cu);
        String b = TienIchController.rongThanhNull(moi);
        if (!Objects.equals(a, b)) {
            TienIchController.ghiNhatKy(maNV, "Sửa thông tin y tế", "HoSoBenhAn", maHoSo,
                    tenTruong + ": từ [" + (a == null ? "" : a) + "] sang [" + (b == null ? "" : b) + "]");
        }
    }

}
