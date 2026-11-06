package controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import dao.CaTrucDAO;
import dao.LichHenDAO;
import dao.MaGenerator;
import dao.NghiepVuException;
import dao.PhienLamViec;
import entity.CaTruc;
import entity.LichHen;
import entity.NhanVien;
import entity.VaiTro;

// Nghiệp vụ phân công ca trực
public class CaTrucController {

    public static final String SANG = "Sáng";
    public static final String CHIEU = "Chiều";
    public static final String CO_MAT = "Có mặt";
    public static final String VANG = "Vắng";
    public static final String DIEU_CHUYEN = "Điều chuyển";
    public static final String QUAY_LE_TAN = "Quầy lễ tân";
    public static final List<String> PHONG_KHAM = List.of("Phòng 1", "Phòng 2", "Phòng 3");

    private CaTrucDAO caTrucDAO;
    private LichHenDAO lichHenDAO;
    private NhanVienController nhanVienController;
    private NhatKyController nhatKy = new NhatKyController();
    private Supplier<NhanVien> nguoiDung;

    // Dùng DAO thật
    public CaTrucController() {
        this(new CaTrucDAO(), new LichHenDAO(), new NhanVienController(), PhienLamViec::getNhanVienHienTai);
    }

    // Dùng DAO và người dùng truyền vào
    public CaTrucController(CaTrucDAO caTrucDAO, LichHenDAO lichHenDAO, NhanVienController nhanVienController, Supplier<NhanVien> nguoiDung) {
        this.caTrucDAO = caTrucDAO;
        this.lichHenDAO = lichHenDAO;
        this.nhanVienController = nhanVienController;
        this.nguoiDung = nguoiDung;
    }

    // Giờ bắt đầu của ca
    public static LocalTime gioBatDauCa(String caLam) {
        return SANG.equals(caLam) ? LocalTime.of(8, 0) : LocalTime.of(13, 0);
    }

    // Giờ kết thúc của ca
    public static LocalTime gioKetThucCa(String caLam) {
        return SANG.equals(caLam) ? LocalTime.of(12, 0) : LocalTime.of(17, 0);
    }

    // Các ca trực
    public List<CaTruc> layTheoKhoang(LocalDate tu, LocalDate den) {
        return caTrucDAO.layTheoKhoang(tu, den);
    }

    // Các ca trực của một nhân viên
    public List<CaTruc> layTheoNhanVienVaKhoang(String maNV, LocalDate tu, LocalDate den) {
        return caTrucDAO.layTheoNhanVien(maNV, tu, den);
    }

    // Các ca Có mặt của một nhân viên
    public List<CaTruc> layCaCoMat(String maNV, LocalDate ngay, String caLam) {
        List<CaTruc> kq = new ArrayList<>();
        for (CaTruc ca : caTrucDAO.layTheoNhanVien(maNV, ngay, ngay)) {
            if (CO_MAT.equals(ca.getTrangThai()) && (caLam == null || caLam.equals(ca.getCaLam()))) {
                kq.add(ca);
            }
        }
        return kq;
    }

    // Nhân viên có ca trực
    public boolean coCaTruc(String maNV, LocalDate ngay, String caLam) {
        return !layCaCoMat(maNV, ngay, caLam).isEmpty();
    }

    // Tìm ca trực theo mã
    public CaTruc timTheoMa(String maCaTruc) {
        return caTrucDAO.timTheoMa(maCaTruc);
    }

    // Phân công một ca trực
    public CaTruc phanCong(String maNV, LocalDate ngay, String caLam, String phong) {
        NhanVien nguoi = Quyen.yeuCau(nguoiDung, "phân công ca trực", VaiTro.QUAN_LY);
        NhanVien nv = nhanVienController.timTheoMa(maNV);
        if (nv == null) {
            throw new NghiepVuException("Chưa chọn nhân viên");
        }
        if (!NhanVienController.DANG_LAM.equals(nv.getTrangThai())) {
            throw new NghiepVuException("Chỉ xếp ca cho nhân viên đang làm việc (" + nv.getHoTen() + " đã nghỉ việc)");
        }
        if (VaiTro.QUAN_LY.equals(nv.getVaiTro())) {
            throw new NghiepVuException("Quản lý không có ca trực, chỉ xếp ca cho Nha sĩ, Y tá và Lễ tân");
        }
        if (ngay == null) {
            throw new NghiepVuException("Chưa chọn ngày");
        }
        if (ngay.isBefore(LocalDate.now())) {
            throw new NghiepVuException("Không xếp ca trong quá khứ");
        }
        if (!SANG.equals(caLam) && !CHIEU.equals(caLam)) {
            throw new NghiepVuException("Ca làm chỉ có thể là Sáng hoặc Chiều");
        }
        if (VaiTro.NHA_SI.equals(nv.getVaiTro()) && !nhanVienController.chungChiConHan(maNV, ngay)) {
            throw new NghiepVuException("Chứng chỉ hành nghề của " + nv.getHoTen() + " đã hết hạn trước ngày " + ngay + ", không được xếp ca");
        }
        if (VaiTro.LE_TAN.equals(nv.getVaiTro())) {
            phong = QUAY_LE_TAN;
        } else if (phong == null || !PHONG_KHAM.contains(phong)) {
            throw new NghiepVuException("Nha sĩ và Y tá phải xếp vào Phòng 1, Phòng 2 hoặc Phòng 3");
        }
        for (CaTruc ca : caTrucDAO.layTheoNgayVaCa(ngay, caLam)) {
            if (ca.getMaNV().getMaNV().equals(maNV)) {
                throw new NghiepVuException(nv.getHoTen() + " đã có ca " + caLam.toLowerCase() + " ngày " + ngay + " (" + ca.getPhong() + ")");
            }
            if (VaiTro.NHA_SI.equals(nv.getVaiTro()) && VaiTro.NHA_SI.equals(ca.getMaNV().getVaiTro()) && phong.equals(ca.getPhong())) {
                throw new NghiepVuException(phong + " ca " + caLam.toLowerCase() + " ngày " + ngay + " đã có nha sĩ " + ca.getMaNV().getHoTen() + " (mỗi phòng một nha sĩ)");
            }
        }
        CaTruc ca = new CaTruc();
        ca.setMaCaTruc(MaGenerator.taoMa("CA"));
        ca.setMaNV(nv);
        ca.setNgay(ngay);
        ca.setCaLam(caLam);
        ca.setPhong(phong);
        ca.setTrangThai(CO_MAT);
        caTrucDAO.them(ca);
        nhatKy.ghiNhatKy(nguoi.getMaNV(), "Phân công ca trực", "CaTruc", ca.getMaCaTruc(), nv.getHoTen() + ", " + ngay + ", " + caLam + ", " + phong);
        return ca;
    }

    // Đổi trạng thái ca
    public void doiTrangThai(String maCaTruc, String trangThai) {
        NhanVien nguoi = Quyen.yeuCau(nguoiDung, "đổi trạng thái ca trực", VaiTro.QUAN_LY);
        CaTruc ca = layCa(maCaTruc);
        if (!List.of(CO_MAT, VANG, DIEU_CHUYEN).contains(trangThai)) {
            throw new NghiepVuException("Trạng thái ca chỉ có thể là Có mặt, Vắng hoặc Điều chuyển");
        }
        if (!CO_MAT.equals(trangThai)) {
            kiemTraKhongCoLichHen(ca, "đổi trạng thái");
        }
        caTrucDAO.doiTrangThai(maCaTruc, trangThai);
        nhatKy.ghiNhatKy(nguoi.getMaNV(), "Đổi trạng thái ca trực", "CaTruc", maCaTruc, "Từ [" + ca.getTrangThai() + "] sang [" + trangThai + "]");
    }

    // Xóa ca đã xếp nhầm
    public void xoa(String maCaTruc) {
        NhanVien nguoi = Quyen.yeuCau(nguoiDung, "xóa ca trực", VaiTro.QUAN_LY);
        CaTruc ca = layCa(maCaTruc);
        if (ca.getNgay().isBefore(LocalDate.now())) {
            throw new NghiepVuException("Không xóa ca trực đã qua (cần giữ lịch sử làm việc)");
        }
        kiemTraKhongCoLichHen(ca, "xóa");
        caTrucDAO.xoa(maCaTruc);
        nhatKy.ghiNhatKy(nguoi.getMaNV(), "Xóa ca trực", "CaTruc", maCaTruc,
                ca.getMaNV().getHoTen() + ", " + ca.getNgay() + ", " + ca.getCaLam() + ", " + ca.getPhong());
    }

    private CaTruc layCa(String maCaTruc) {
        CaTruc ca = caTrucDAO.timTheoMa(maCaTruc);
        if (ca == null) {
            throw new NghiepVuException("Không tìm thấy ca trực " + maCaTruc);
        }
        return ca;
    }

    // Nha sĩ có lịch hẹn còn hiệu lực
    private void kiemTraKhongCoLichHen(CaTruc ca, String viec) {
        if (!VaiTro.NHA_SI.equals(ca.getMaNV().getVaiTro())) {
            return;
        }
        LocalTime tu = gioBatDauCa(ca.getCaLam());
        LocalTime den = gioKetThucCa(ca.getCaLam());
        for (LichHen lh : lichHenDAO.layConHieuLucTheoNhaSiVaNgay(ca.getMaNV().getMaNV(), ca.getNgay())) {
            if (!lh.getGioHen().isBefore(tu) && lh.getGioHen().isBefore(den)) {
                throw new NghiepVuException("Không thể " + viec + " ca này vì " + ca.getMaNV().getHoTen() + " đã có lịch hẹn " + lh.getMaLichHen()
                        + " lúc " + lh.getGioHen() + ". Hãy dời hoặc hủy lịch hẹn trước.");
            }
        }
    }
}
