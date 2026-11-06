package controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import dao.CauHinhDAO;
import dao.LieuTrinhDAO;
import dao.LuotKhamDAO;
import dao.NghiepVuException;
import dao.TienIchDAO;
import entity.BenhNhan;
import entity.DanhMuc;
import entity.HangSo;
import entity.LieuTrinh;
import entity.LuotKham;
import entity.NhanVien;

// Nghiệp vụ liệu trình điều trị nhiều buổi: tạo
public class LieuTrinhController {

    public static final String GIAI_DOAN_MAC_DINH = "Mới bắt đầu";

    private LieuTrinhDAO lieuTrinhDAO = new LieuTrinhDAO();
    private LuotKhamDAO luotKhamDAO = new LuotKhamDAO();
    private CauHinhDAO cauHinhDAO = new CauHinhDAO();
    private BenhNhanController benhNhanController = new BenhNhanController();
    private Supplier<NhanVien> nguoiDung = PhienLamViec::getNhanVienHienTai;

    // Mọi liệu trình của bệnh nhân
    public List<LieuTrinh> layTheoBenhNhan(String maBN) {
        return lieuTrinhDAO.layTheoBenhNhan(maBN);
    }

    // Các liệu trình đang thực hiện của bệnh nhân
    public List<LieuTrinh> layDangThucHien(String maBN) {
        List<LieuTrinh> kq = new ArrayList<>();
        for (LieuTrinh lt : lieuTrinhDAO.layTheoBenhNhan(maBN)) {
            if (HangSo.LIEU_TRINH_DANG_THUC_HIEN.equals(lt.getTrangThai())) {
                kq.add(lt);
            }
        }
        return kq;
    }

    public LieuTrinh timTheoMa(String maLieuTrinh) {
        LieuTrinh lt = maLieuTrinh == null ? null : lieuTrinhDAO.timTheoMa(maLieuTrinh);
        if (lt == null) {
            throw new NghiepVuException("Không tìm thấy liệu trình " + maLieuTrinh);
        }
        return lt;
    }

    // Số buổi đã khám xong trong liệu trình
    public int demBuoiDaKham(String maLieuTrinh) {
        return lieuTrinhDAO.demBuoiDaKham(maLieuTrinh);
    }

    // Các buổi (lượt khám) của liệu trình theo thứ tự thời gian
    public List<LuotKham> layCacBuoi(String maLieuTrinh) {
        return luotKhamDAO.layTheoLieuTrinh(maLieuTrinh);
    }

    // Tạo liệu trình mới cho bệnh nhân
    public LieuTrinh tao(String maBN, String loaiDichVu, int soBuoiDuKien, String giaiDoan, LocalDate ngayDuKienKetThuc, long tongChiPhiDuKien, String ghiChu) {
        NhanVien nhaSi = TienIchController.yeuCau(nguoiDung, "tạo liệu trình", HangSo.VAI_TRO_NHA_SI);
        BenhNhan bn = benhNhanController.timTheoMa(maBN);
        if (bn == null) {
            throw new NghiepVuException("Chưa chọn bệnh nhân");
        }
        if (!HangSo.DANG_DIEU_TRI.equals(bn.getTrangThai())) {
            throw new NghiepVuException("Bệnh nhân " + bn.getHoTen() + " đang ở trạng thái " + bn.getTrangThai() + ", không tạo liệu trình mới");
        }
        if (loaiDichVu == null || !cauHinhDAO.daCo(DanhMuc.LOAI_DICH_VU, loaiDichVu)) {
            throw new NghiepVuException("Phải chọn loại dịch vụ của liệu trình trong danh mục");
        }
        if (soBuoiDuKien < 1) {
            throw new NghiepVuException("Số buổi dự kiến phải lớn hơn 0");
        }
        if (tongChiPhiDuKien < 0) {
            throw new NghiepVuException("Chi phí dự kiến không được âm");
        }
        if (ngayDuKienKetThuc != null && ngayDuKienKetThuc.isBefore(LocalDate.now())) {
            throw new NghiepVuException("Ngày dự kiến kết thúc không được ở quá khứ");
        }
        String gd = TienIchController.rongThanhNull(giaiDoan);
        gd = gd == null ? GIAI_DOAN_MAC_DINH : gd;
        String gc = TienIchController.rongThanhNull(ghiChu);
        for (LieuTrinh cu : layDangThucHien(maBN)) {
            if (cu.getLoaiDichVu().equals(loaiDichVu)) {
                throw new NghiepVuException("Bệnh nhân đã có liệu trình " + loaiDichVu + " đang thực hiện (" + cu.getMaLieuTrinh() + ")");
            }
        }
        LieuTrinh lt = new LieuTrinh();
        lt.setMaLieuTrinh(TienIchDAO.taoMa("LT"));
        lt.setMaBN(bn);
        lt.setLoaiDichVu(loaiDichVu);
        lt.setSoBuoiDuKien(soBuoiDuKien);
        lt.setGiaiDoanHienTai(gd);
        lt.setNgayBatDau(LocalDate.now());
        lt.setNgayDuKienKetThuc(ngayDuKienKetThuc);
        lt.setTongChiPhiDuKien(tongChiPhiDuKien);
        lt.setTrangThai(HangSo.LIEU_TRINH_DANG_THUC_HIEN);
        lt.setMaNhaSiPhuTrach(nhaSi);
        lt.setGhiChu(gc);
        lieuTrinhDAO.them(lt);
        return lt;
    }

    // Cập nhật giai đoạn hiện tại
    public void capNhatGiaiDoan(String maLieuTrinh, String giaiDoan, String ghiChu) {
        LieuTrinh lt = layDeSua(maLieuTrinh, "cập nhật giai đoạn");
        String gd = TienIchController.rongThanhNull(giaiDoan);
        if (gd == null) {
            throw new NghiepVuException("Giai đoạn không được để trống");
        }
        String gc = TienIchController.rongThanhNull(ghiChu);
        lt.setGiaiDoanHienTai(gd);
        lt.setGhiChu(gc);
        lieuTrinhDAO.capNhat(lt);
    }

    // Kết thúc liệu trình: phải có ít nhất một buổi đã khám và không có buổi đang khám dở
    public void hoanThanh(String maLieuTrinh) {
        LieuTrinh lt = layDeSua(maLieuTrinh, "hoàn thành liệu trình");
        if (lieuTrinhDAO.demBuoiDaKham(maLieuTrinh) == 0) {
            throw new NghiepVuException("Liệu trình chưa có buổi nào hoàn tất nên chưa thể kết thúc (nếu không làm nữa thì dùng Hủy liệu trình)");
        }
        if (lieuTrinhDAO.coLuotKhamDangKham(maLieuTrinh)) {
            throw new NghiepVuException("Đang có một buổi khám của liệu trình chưa hoàn tất");
        }
        lt.setTrangThai(HangSo.HOAN_THANH);
        lt.setGiaiDoanHienTai("Hoàn thành");
        lt.setNgayKetThucThucTe(LocalDate.now());
        lieuTrinhDAO.capNhat(lt);
    }

    // Hủy liệu trình giữa chừng
    public void huy(String maLieuTrinh, String lyDo) {
        LieuTrinh lt = layDeSua(maLieuTrinh, "hủy liệu trình");
        String ld = TienIchController.rongThanhNull(lyDo);
        if (ld == null || ld.length() < 5) {
            throw new NghiepVuException("Phải nhập lý do hủy liệu trình (ít nhất 5 ký tự)");
        }
        if (lieuTrinhDAO.coLuotKhamDangKham(maLieuTrinh)) {
            throw new NghiepVuException("Đang có một buổi khám của liệu trình chưa hoàn tất");
        }
        String ghiChu = (lt.getGhiChu() == null ? "" : lt.getGhiChu() + " | ") + "Hủy: " + ld;
        lt.setGhiChu(ghiChu.length() > 255 ? ghiChu.substring(0, 255) : ghiChu);
        lt.setTrangThai(HangSo.DA_HUY);
        lt.setNgayKetThucThucTe(LocalDate.now());
        lieuTrinhDAO.capNhat(lt);
    }

    // Các lượt khám đã hoàn thành của bệnh nhân chưa thuộc liệu trình nào
    public List<LuotKham> layLuotKhamChuaThuocLieuTrinh(String maBN) {
        List<LuotKham> kq = new ArrayList<>();
        for (LuotKham lk : luotKhamDAO.layTheoBenhNhan(maBN)) {
            if (HangSo.HOAN_THANH.equals(lk.getTrangThai()) && lk.getMaLieuTrinh() == null) {
                kq.add(lk);
            }
        }
        return kq;
    }

    // Thêm một lượt khám đã hoàn thành vào liệu trình đang thực hiện
    public void themBuoi(String maLieuTrinh, String maLuotKham) {
        LieuTrinh lt = layDeSua(maLieuTrinh, "thêm buổi vào liệu trình");
        LuotKham lk = luotKhamDAO.timTheoMa(maLuotKham);
        if (lk == null) {
            throw new NghiepVuException("Chưa chọn lượt khám");
        }
        if (!lk.getMaHoSo().getMaBN().getMaBN().equals(lt.getMaBN().getMaBN())) {
            throw new NghiepVuException("Lượt khám này không phải của bệnh nhân trong liệu trình");
        }
        if (!HangSo.HOAN_THANH.equals(lk.getTrangThai())) {
            throw new NghiepVuException("Chỉ thêm lượt khám đã hoàn thành (lượt đang khám thì chọn liệu trình ngay ở màn hình Khám)");
        }
        if (lk.getMaLieuTrinh() != null) {
            throw new NghiepVuException("Lượt khám này đã thuộc liệu trình " + lk.getMaLieuTrinh().getMaLieuTrinh());
        }
        luotKhamDAO.ganLieuTrinh(maLuotKham, maLieuTrinh);
    }

    // Bỏ một buổi ra khỏi liệu trình
    public void boBuoi(String maLieuTrinh, String maLuotKham) {
        layDeSua(maLieuTrinh, "bỏ buổi khỏi liệu trình");
        LuotKham lk = luotKhamDAO.timTheoMa(maLuotKham);
        if (lk == null || lk.getMaLieuTrinh() == null || !lk.getMaLieuTrinh().getMaLieuTrinh().equals(maLieuTrinh)) {
            throw new NghiepVuException("Lượt khám này không thuộc liệu trình " + maLieuTrinh);
        }
        luotKhamDAO.ganLieuTrinh(maLuotKham, null);
    }

    // Liệu trình đang thực hiện và người sửa là nha sĩ phụ trách
    private LieuTrinh layDeSua(String maLieuTrinh, String viec) {
        NhanVien nv = TienIchController.yeuCau(nguoiDung, viec, HangSo.VAI_TRO_NHA_SI, HangSo.VAI_TRO_QUAN_LY);
        LieuTrinh lt = timTheoMa(maLieuTrinh);
        if (!HangSo.LIEU_TRINH_DANG_THUC_HIEN.equals(lt.getTrangThai())) {
            throw new NghiepVuException("Liệu trình đang ở trạng thái \"" + lt.getTrangThai() + "\", không sửa được");
        }
        boolean laNguoiPhuTrach = lt.getMaNhaSiPhuTrach() != null && lt.getMaNhaSiPhuTrach().getMaNV().equals(nv.getMaNV());
        if (!laNguoiPhuTrach && !nv.laVaiTro(HangSo.VAI_TRO_QUAN_LY)) {
            throw new NghiepVuException("Chỉ nha sĩ phụ trách hoặc quản lý mới được " + viec);
        }
        return lt;
    }
}
