package controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import dao.CauHinhDAO;
import dao.TienIchDAO;
import dao.NghiepVuException;
import dao.NhanVienDAO;
import dao.TaiKhoanDAO;
import entity.DanhMuc;
import entity.NhanVien;
import entity.TaiKhoan;
import entity.HangSo;

// Nghiệp vụ hồ sơ nhân viên
public class NhanVienController {

    public static final String DANG_LAM = "Đang làm việc";
    public static final String NGHI_VIEC = "Nghỉ việc";
    public static final int SO_NGAY_CANH_BAO_CHUNG_CHI = 30; // Báo trước khi chứng chỉ hành

    private NhanVienDAO nhanVienDAO = new NhanVienDAO();
    private TaiKhoanDAO taiKhoanDAO = new TaiKhoanDAO();
    private CauHinhDAO cauHinhDAO = new CauHinhDAO();
    private Supplier<NhanVien> nguoiDung = PhienLamViec::getNhanVienHienTai;

    // Tìm nhân viên theo mã
    public NhanVien timTheoMa(String ma) {
        return ma == null ? null : nhanVienDAO.timTheoMa(ma);
    }

    // Tất cả nhân viên
    public List<NhanVien> layTatCa() {
        return nhanVienDAO.layTatCa();
    }

    // Nha sĩ của lượt khám gần nhất của từng bệnh nhân: mã bệnh nhân -> nha sĩ
    public java.util.Map<String, NhanVien> layNhaSiKhamGanNhat(java.util.Collection<String> dsMaBN) {
        return nhanVienDAO.layNhaSiKhamGanNhat(dsMaBN);
    }

    // Nhân viên đang làm việc theo vai trò
    public List<NhanVien> layTheoVaiTro(String vaiTro) {
        List<NhanVien> kq = new ArrayList<>();
        for (NhanVien nv : nhanVienDAO.layTatCa()) {
            if (nv.getVaiTro().equals(vaiTro) && DANG_LAM.equals(nv.getTrangThai())) {
                kq.add(nv);
            }
        }
        return kq;
    }

    // Nhân viên có chứng chỉ hành
    public boolean chungChiConHan(String maNV, LocalDate ngay) {
        NhanVien nv = timTheoMa(maNV);
        return nv != null && nv.getNgayHetHanChungChi() != null && !nv.getNgayHetHanChungChi().isBefore(ngay);
    }

    // Nha sĩ đang làm việc
    public List<NhanVien> layCanhBaoChungChi() {
        List<NhanVien> kq = new ArrayList<>();
        LocalDate han = LocalDate.now().plusDays(SO_NGAY_CANH_BAO_CHUNG_CHI);
        for (NhanVien nv : nhanVienDAO.layTatCa()) {
            if (HangSo.VAI_TRO_NHA_SI.equals(nv.getVaiTro()) && DANG_LAM.equals(nv.getTrangThai())
                    && nv.getNgayHetHanChungChi() != null && !nv.getNgayHetHanChungChi().isAfter(han)) {
                kq.add(nv);
            }
        }
        kq.sort((a, b) -> a.getNgayHetHanChungChi().compareTo(b.getNgayHetHanChungChi()));
        return kq;
    }

    // Thêm nhân viên mới
    public NhanVien them(NhanVien nv) {
        TienIchController.yeuCau(nguoiDung, "thêm hoặc sửa hồ sơ nhân viên", HangSo.VAI_TRO_QUAN_LY);

        chuanHoa(nv);
        kiemTra(nv);
        nv.setMaNV(TienIchDAO.taoMa("NV"));
        nv.setTrangThai(DANG_LAM);
        nhanVienDAO.them(nv);
        return nv;
    }

    // Cập nhật hồ sơ nhân viên
    public void capNhat(NhanVien nv) {
        TienIchController.yeuCau(nguoiDung, "thêm hoặc sửa hồ sơ nhân viên", HangSo.VAI_TRO_QUAN_LY);
        NhanVien cu = nhanVienDAO.timTheoMa(nv.getMaNV());
        if (cu == null) {
            throw new NghiepVuException("Không tìm thấy nhân viên " + nv.getMaNV());
        }
        chuanHoa(nv);
        kiemTra(nv);
        boolean doiVaiTro = !cu.getVaiTro().equals(nv.getVaiTro());
        boolean nghiViec = NGHI_VIEC.equals(nv.getTrangThai()) && !NGHI_VIEC.equals(cu.getTrangThai());
        TaiKhoan tk = taiKhoanDAO.timTheoNhanVien(nv.getMaNV());
        nhanVienDAO.capNhat(nv);
        if (tk != null && doiVaiTro) {
            taiKhoanDAO.doiVaiTroTheoNhanVien(nv.getMaNV(), nv.getVaiTro());
        }
        if (tk != null && nghiViec) {
            taiKhoanDAO.khoaTheoNhanVien(nv.getMaNV());
        }
        String maNguoiLam = nguoiDung.get().getMaNV();
        if (doiVaiTro) {
            TienIchController.ghiNhatKy(maNguoiLam, "Đổi vai trò nhân viên", "NhanVien", nv.getMaNV(), "Từ [" + cu.getVaiTro() + "] sang [" + nv.getVaiTro() + "]");
        }
        if (!cu.getTrangThai().equals(nv.getTrangThai())) {
            TienIchController.ghiNhatKy(maNguoiLam, "Đổi trạng thái nhân viên", "NhanVien", nv.getMaNV(), "Từ [" + cu.getTrangThai() + "] sang [" + nv.getTrangThai() + "]");
        }
    }

    // Cắt khoảng trắng thừa
    private void chuanHoa(NhanVien nv) {
        nv.setHoTen(nv.getHoTen() == null ? null : nv.getHoTen().trim().replaceAll("\\s+", " "));
        nv.setSdt(TienIchController.rongThanhNull(nv.getSdt()));
        nv.setGioiTinh(TienIchController.rongThanhNull(nv.getGioiTinh()));
        nv.setChuyenKhoa(TienIchController.rongThanhNull(nv.getChuyenKhoa()));
        nv.setSoChungChiHanhNghe(TienIchController.rongThanhNull(nv.getSoChungChiHanhNghe()));
        if (!HangSo.VAI_TRO_NHA_SI.equals(nv.getVaiTro())) {
            nv.setSoChungChiHanhNghe(null); // Chỉ nha sĩ có chứng
            nv.setNgayHetHanChungChi(null);
        }
    }

    // Kiểm tra thông tin nhập vào
    public void kiemTra(NhanVien nv) {
        if (nv.getHoTen() == null || nv.getHoTen().isEmpty()) {
            throw new NghiepVuException("Họ tên không được để trống");
        }
        if (nv.getHoTen().length() < 2 || nv.getHoTen().length() > 100 || !nv.getHoTen().matches("[\\p{L} .'-]+")) {
            throw new NghiepVuException("Họ tên chỉ gồm chữ cái và khoảng trắng, dài từ 2 đến 100 ký tự");
        }
        if (nv.getVaiTro() == null || !cauHinhDAO.coVaiTro(nv.getVaiTro())) {
            throw new NghiepVuException("Vai trò \"" + nv.getVaiTro() + "\" không có trong danh mục vai trò");
        }
        if (nv.getGioiTinh() != null && !List.of("Nam", "Nữ", "Khác").contains(nv.getGioiTinh())) {
            throw new NghiepVuException("Giới tính chỉ có thể là Nam, Nữ hoặc Khác");
        }
        if (nv.getSdt() != null && !nv.getSdt().matches("0[0-9]{9}")) {
            throw new NghiepVuException("Số điện thoại phải đủ 10 số và bắt đầu bằng 0");
        }
        if (nv.getChuyenKhoa() != null && !cauHinhDAO.daCo(DanhMuc.CHUYEN_KHOA, nv.getChuyenKhoa())) {
            throw new NghiepVuException("Chuyên khoa \"" + nv.getChuyenKhoa() + "\" không có trong danh mục chuyên khoa");
        }
        if (HangSo.VAI_TRO_NHA_SI.equals(nv.getVaiTro())) {
            if (nv.getSoChungChiHanhNghe() == null) {
                throw new NghiepVuException("Nha sĩ bắt buộc có số chứng chỉ hành nghề");
            }
            if (nv.getNgayHetHanChungChi() == null) {
                throw new NghiepVuException("Nha sĩ bắt buộc có ngày hết hạn chứng chỉ hành nghề");
            }
        }
        if (nv.getTrangThai() != null && !List.of(DANG_LAM, NGHI_VIEC).contains(nv.getTrangThai())) {
            throw new NghiepVuException("Trạng thái chỉ có thể là Đang làm việc hoặc Nghỉ việc");
        }
    }
}
