package nhakhoakim.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import nhakhoakim.entity.BenhNhan;
import nhakhoakim.entity.DichVu;
import nhakhoakim.entity.HoSoBenhAn;
import nhakhoakim.entity.LieuTrinh;
import nhakhoakim.entity.LoaiDichVu;
import nhakhoakim.entity.LuotKham;
import nhakhoakim.entity.NhanVien;
import nhakhoakim.entity.TrangThai;
import nhakhoakim.entity.VaiTro;

// Nghiệp vụ hồ sơ bệnh án: xem, cập nhật tiền sử - dị ứng (BN-06 đến BN-09, BN-15), tạo hồ sơ rỗng (BN-04),
// xem liệu trình (BN-10, BN-11) và lịch sử khám. Chặn thuốc cùng nhóm dị ứng (BN-16) nằm ở entity HoSoBenhAn.
public class HoSoBenhAnService {

    // Các danh sách từ khóa dùng cho canhBaoYTe (viết thường, có dấu); muốn thêm từ khóa thì sửa ở đây
    private static final String[] THUOC_CHONG_DONG = {"warfarin", "aspirin", "clopidogrel", "plavix", "heparin", "rivaroxaban",
        "xarelto", "apixaban", "acenocoumarol", "dabigatran", "ticagrelor", "enoxaparin", "chống đông", "loãng máu"};
    private static final String[] TU_KHOA_ROI_LOAN_DONG_MAU = {"đông máu", "khó đông", "hemophilia", "máu loãng"};
    private static final String[] TU_KHOA_TIEU_DUONG = {"tiểu đường", "đái tháo đường"};
    private static final String[] TU_KHOA_TIM_HUYET_AP = {"huyết áp", "tim"};
    private static final String[] THUOC_TE = {"lidocaine", "articaine", "procaine", "mepivacaine", "thuốc tê"};

    private static final int TOI_DA_CHI_TIET_NHAT_KY = 500;

    private HoSoBenhAnDAO hoSoDAO;
    private LieuTrinhDAO lieuTrinhDAO;
    private LuotKhamDAO luotKhamDAO;
    private BenhNhanDAO benhNhanDAO;
    private NhatKyService nhatKyService;
    private Supplier<NhanVien> nguoiDung; // cho biết ai đang đăng nhập (kiểm thử thì truyền người giả)

    // Dùng DAO thật và người đăng nhập thật của chương trình
    public HoSoBenhAnService() {
        this(new HoSoBenhAnDAO(), new LieuTrinhDAO(), new LuotKhamDAO(), new BenhNhanDAO(), new NhatKyService(),
                PhienLamViec::getNhanVienHienTai);
    }

    // Dùng DAO và người dùng truyền vào (để kiểm thử không cần CSDL)
    public HoSoBenhAnService(HoSoBenhAnDAO hoSoDAO, LieuTrinhDAO lieuTrinhDAO, LuotKhamDAO luotKhamDAO, BenhNhanDAO benhNhanDAO,
            NhatKyService nhatKyService, Supplier<NhanVien> nguoiDung) {
        this.hoSoDAO = hoSoDAO;
        this.lieuTrinhDAO = lieuTrinhDAO;
        this.luotKhamDAO = luotKhamDAO;
        this.benhNhanDAO = benhNhanDAO;
        this.nhatKyService = nhatKyService;
        this.nguoiDung = nguoiDung;
    }

    // Lấy hồ sơ của một bệnh nhân; chưa có hồ sơ thì báo lỗi
    public HoSoBenhAn timTheoBenhNhan(String maBN) {
        HoSoBenhAn hs = hoSoDAO.timTheoBenhNhan(maBN);
        if (hs == null) {
            throw new NghiepVuException("Bệnh nhân " + maBN + " chưa có hồ sơ bệnh án");
        }
        return hs;
    }

    // Lấy hồ sơ theo mã hồ sơ; không có thì báo lỗi
    public HoSoBenhAn timTheoMa(String maHoSo) {
        HoSoBenhAn hs = hoSoDAO.timTheoMa(maHoSo);
        if (hs == null) {
            throw new NghiepVuException("Không tìm thấy hồ sơ " + maHoSo);
        }
        return hs;
    }

    // Cập nhật tiền sử bệnh, dị ứng và thông tin y tế: chỉ Nha sĩ hoặc Y tá (BN-08, BN-15), hồ sơ Đã đóng thì không sửa (BN-07).
    // Dị ứng được chuẩn hóa (BN-09); mỗi giá trị thực sự thay đổi được ghi nhật ký vì đây là thông tin nhạy cảm.
    public void capNhatThongTinYTe(HoSoBenhAn hs) {
        NhanVien nv = nguoiDung.get();
        if (nv == null) {
            throw new NghiepVuException("Chưa đăng nhập");
        }
        kiemTraQuyenSuaYTe(nv.getVaiTro());
        HoSoBenhAn cu = timTheoMa(hs.getMaHoSo());
        if (TrangThai.HO_SO_DA_DONG.equals(cu.getTrangThaiHoSo())) {
            throw new NghiepVuException("Hồ sơ đã đóng, không thể sửa thông tin y tế");
        }

        // Chuẩn hóa: ô trống thành null, cắt khoảng trắng; dị ứng tách theo dấu phẩy, bỏ mục rỗng và trùng
        String nhomMau = KiemTraNhap.rongThanhNull(hs.getNhomMau());
        if (nhomMau != null && !List.of("A", "B", "AB", "O").contains(nhomMau)) {
            throw new NghiepVuException("Nhóm máu chỉ có thể là A, B, AB hoặc O");
        }
        String tienSu = KiemTraNhap.rongThanhNull(hs.getTienSuBenh());
        String diUng = chuanHoaDiUng(hs.getCanhBaoDiUng());
        String benhNen = KiemTraNhap.rongThanhNull(hs.getBenhNen());
        String thuoc = KiemTraNhap.rongThanhNull(hs.getThuocDangSuDung());
        String ghiChu = KiemTraNhap.rongThanhNull(hs.getGhiChuDacBiet());
        KiemTraNhap.doDai(tienSu, 1000, "Tiền sử bệnh");
        KiemTraNhap.doDai(diUng, 500, "Dị ứng");
        KiemTraNhap.doDai(benhNen, 500, "Bệnh nền");
        KiemTraNhap.doDai(thuoc, 500, "Thuốc đang sử dụng");
        KiemTraNhap.doDai(ghiChu, 500, "Ghi chú đặc biệt");

        HoSoBenhAn moi = new HoSoBenhAn();
        moi.setMaHoSo(cu.getMaHoSo());
        moi.setMaBN(cu.getMaBN());
        moi.setNgayTao(cu.getNgayTao());
        moi.setTrangThaiHoSo(cu.getTrangThaiHoSo());
        moi.setTienSuBenh(tienSu);
        moi.setCanhBaoDiUng(diUng);
        moi.setNhomMau(nhomMau);
        moi.setBenhNen(benhNen);
        moi.setThuocDangSuDung(thuoc);
        moi.setGhiChuDacBiet(ghiChu);
        hoSoDAO.capNhatThongTinYTe(moi);

        // Ghi nhật ký những giá trị thực sự đổi (so sánh sau chuẩn hóa, null và rỗng coi như nhau)
        String maNV = nv.getMaNV();
        ghiNhatKyNeuDoi(maNV, "Sửa dị ứng", cu.getMaHoSo(), "Dị ứng", chuanHoaDiUng(cu.getCanhBaoDiUng()), diUng);
        ghiNhatKyNeuDoi(maNV, "Sửa thông tin y tế", cu.getMaHoSo(), "Tiền sử bệnh", cu.getTienSuBenh(), tienSu);
        ghiNhatKyNeuDoi(maNV, "Sửa thông tin y tế", cu.getMaHoSo(), "Nhóm máu", cu.getNhomMau(), nhomMau);
        ghiNhatKyNeuDoi(maNV, "Sửa thông tin y tế", cu.getMaHoSo(), "Bệnh nền", cu.getBenhNen(), benhNen);
        ghiNhatKyNeuDoi(maNV, "Sửa thông tin y tế", cu.getMaHoSo(), "Thuốc đang sử dụng", cu.getThuocDangSuDung(), thuoc);
        ghiNhatKyNeuDoi(maNV, "Sửa thông tin y tế", cu.getMaHoSo(), "Ghi chú đặc biệt", cu.getGhiChuDacBiet(), ghiChu);

        // Trả giá trị đã chuẩn hóa về đối tượng truyền vào
        hs.setTienSuBenh(tienSu);
        hs.setCanhBaoDiUng(diUng);
        hs.setNhomMau(nhomMau);
        hs.setBenhNen(benhNen);
        hs.setThuocDangSuDung(thuoc);
        hs.setGhiChuDacBiet(ghiChu);
    }

    // Chỉ Nha sĩ hoặc Y tá được sửa thông tin y tế; vai trò khác báo lỗi
    private void kiemTraQuyenSuaYTe(String vaiTro) {
        if (VaiTro.NHA_SI.equals(vaiTro) || VaiTro.Y_TA.equals(vaiTro)) {
            return;
        }
        if (VaiTro.LE_TAN.equals(vaiTro)) {
            throw new NghiepVuException("Lễ tân không được sửa thông tin y tế, chỉ nha sĩ hoặc y tá mới được sửa");
        }
        throw new NghiepVuException("Vai trò " + vaiTro + " không được sửa thông tin y tế, chỉ nha sĩ hoặc y tá mới được sửa");
    }

    // Chuẩn hóa danh sách dị ứng: tách theo dấu phẩy, cắt khoảng trắng, bỏ mục rỗng và trùng (không phân biệt hoa thường),
    // ghép lại bằng ", "; không còn mục nào thì trả về null
    private String chuanHoaDiUng(String diUng) {
        if (diUng == null) {
            return null;
        }
        List<String> ds = new ArrayList<>();
        for (String muc : diUng.split("[,;]")) {
            String m = muc.trim().replaceAll("\\s+", " ");
            if (m.isEmpty()) {
                continue;
            }
            boolean trung = false;
            for (String da : ds) {
                if (da.equalsIgnoreCase(m)) {
                    trung = true;
                }
            }
            if (!trung) {
                ds.add(m);
            }
        }
        return ds.isEmpty() ? null : String.join(", ", ds);
    }

    // Ghi nhật ký nếu giá trị cũ và mới khác nhau (null và rỗng coi như nhau); chi tiết cắt tối đa 500 ký tự
    private void ghiNhatKyNeuDoi(String maNV, String hanhDong, String maHoSo, String tenTruong, String cu, String moi) {
        String a = KiemTraNhap.rongThanhNull(cu);
        String b = KiemTraNhap.rongThanhNull(moi);
        if (Objects.equals(a, b)) {
            return;
        }
        String chiTiet = tenTruong + ": từ [" + (a == null ? "" : a) + "] sang [" + (b == null ? "" : b) + "]";
        if (chiTiet.length() > TOI_DA_CHI_TIET_NHAT_KY) {
            chiTiet = chiTiet.substring(0, TOI_DA_CHI_TIET_NHAT_KY);
        }
        nhatKyService.ghiNhatKy(maNV, hanhDong, "HoSoBenhAn", maHoSo, chiTiet);
    }

    // Tạo hồ sơ rỗng cho bệnh nhân đã có (ghi vào CSDL); mỗi bệnh nhân chỉ có một hồ sơ
    public HoSoBenhAn taoHoSoRong(String maBN) {
        if (hoSoDAO.timTheoBenhNhan(maBN) != null) {
            throw new NghiepVuException("Bệnh nhân " + maBN + " đã có hồ sơ bệnh án");
        }
        HoSoBenhAn hs = taoDoiTuongHoSoRong(maBN);
        hoSoDAO.them(hs);
        return hs;
    }

    // Như trên nhưng dùng kết nối có sẵn, để tạo bệnh nhân và hồ sơ trong cùng một transaction
    public HoSoBenhAn taoHoSoRong(Connection c, String maBN) throws SQLException {
        HoSoBenhAn hs = taoDoiTuongHoSoRong(maBN);
        hoSoDAO.them(c, hs);
        return hs;
    }

    // Dựng đối tượng hồ sơ rỗng đã có mã mới, chưa ghi vào CSDL (BenhNhanService ghi cùng lúc với bệnh nhân)
    public HoSoBenhAn taoDoiTuongHoSoRong(String maBN) {
        HoSoBenhAn hs = new HoSoBenhAn();
        hs.setMaHoSo(MaGenerator.taoMa("HS"));
        hs.setMaBN(maBN);
        hs.setNgayTao(LocalDate.now());
        hs.setTrangThaiHoSo(TrangThai.HO_SO_DANG_MO);
        return hs;
    }

    // Tất cả liệu trình của bệnh nhân (đang thực hiện xếp trước)
    public List<LieuTrinh> layLieuTrinh(String maBN) {
        return lieuTrinhDAO.layTheoBenhNhan(maBN);
    }

    // Chỉ các liệu trình đang thực hiện
    public List<LieuTrinh> layLieuTrinhDangThucHien(String maBN) {
        List<LieuTrinh> kq = new ArrayList<>();
        for (LieuTrinh lt : lieuTrinhDAO.layTheoBenhNhan(maBN)) {
            if (TrangThai.LIEU_TRINH_DANG_THUC_HIEN.equals(lt.getTrangThai())) {
                kq.add(lt);
            }
        }
        return kq;
    }

    // Cảnh báo (không chặn) khi dịch vụ cần liệu trình mà bệnh nhân chưa có liệu trình đang thực hiện; không cần cảnh báo thì trả về null
    public String canhBaoLieuTrinh(String maBN, DichVu dv) {
        if (dv == null || !dv.isYeuCauLieuTrinh() || !layLieuTrinhDangThucHien(maBN).isEmpty()) {
            return null;
        }
        return "Dịch vụ \"" + dv.getTenDichVu() + "\" cần theo liệu trình nhưng bệnh nhân chưa có liệu trình đang thực hiện.";
    }

    // Cảnh báo (không chặn) về tình trạng sức khỏe và tuổi của bệnh nhân khi làm dịch vụ này; không có gì cần cảnh báo thì trả về null.
    // Chỉ xét thủ thuật có chảy máu hoặc xâm lấn (nhổ răng, nội nha, implant), riêng tuổi còn xét implant và thẩm mỹ.
    public String canhBaoYTe(String maBN, DichVu dv) {
        if (dv == null) {
            return null;
        }
        HoSoBenhAn hs = hoSoDAO.timTheoBenhNhan(maBN);
        BenhNhan bn = benhNhanDAO.timTheoMa(maBN);
        String loai = dv.getLoaiDichVu() == null ? "" : dv.getLoaiDichVu();
        boolean xamLan = loai.equals(LoaiDichVu.NHO_RANG) || loai.equals(LoaiDichVu.NOI_NHA) || loai.equals(LoaiDichVu.IMPLANT);
        List<String> ds = new ArrayList<>();
        if (xamLan && hs != null) {
            String thuoc = hs.getThuocDangSuDung() == null ? "" : hs.getThuocDangSuDung().toLowerCase();
            String chongDong = timTuKhoa(thuoc, THUOC_CHONG_DONG);
            if (chongDong != null) {
                ds.add("Bệnh nhân đang dùng thuốc chống đông / kháng tiểu cầu (" + chongDong + "): có thể khó cầm máu, cần hỏi bác sĩ điều trị hoặc xét nghiệm đông máu trước khi làm \"" + dv.getTenDichVu() + "\".");
            }
            String benhNen = hs.getBenhNen() == null ? "" : hs.getBenhNen().toLowerCase();
            if (timTuKhoa(benhNen, TU_KHOA_TIEU_DUONG) != null) {
                ds.add("Bệnh nhân tiểu đường: kiểm tra đường huyết trước khi làm, vết thương lâu lành và dễ nhiễm trùng.");
            }
            if (timTuKhoa(benhNen, TU_KHOA_TIM_HUYET_AP) != null) {
                ds.add("Bệnh nhân có bệnh tim mạch hoặc huyết áp: đo huyết áp trước khi làm, thận trọng với thuốc tê có adrenaline.");
            }
            if (timTuKhoa(benhNen, TU_KHOA_ROI_LOAN_DONG_MAU) != null) {
                ds.add("Bệnh nhân bị rối loạn đông máu: cần hội chẩn bác sĩ chuyên khoa trước khi làm thủ thuật chảy máu.");
            }
            // Dị ứng thuốc tê: so với danh sách dị ứng của hồ sơ (kể cả thuốc cùng nhóm)
            List<String> thuocTeDiUng = new ArrayList<>();
            for (String t : THUOC_TE) {
                if (hs.kiemTraCanhBaoDiUng(t)) {
                    thuocTeDiUng.add(t);
                }
            }
            if (!thuocTeDiUng.isEmpty()) {
                ds.add("Bệnh nhân dị ứng thuốc tê (" + String.join(", ", thuocTeDiUng) + "): cần chọn thuốc tê khác hoặc thử phản ứng trước khi làm \"" + dv.getTenDichVu() + "\".");
            }
        }
        if (bn != null && bn.getNgaySinh() != null) {
            int tuoi = bn.tinhTuoi();
            if (loai.equals(LoaiDichVu.IMPLANT) && tuoi < 18) {
                ds.add("Bệnh nhân " + tuoi + " tuổi: xương hàm còn phát triển, thường chưa chỉ định cấy ghép Implant.");
            }
            if (loai.equals(LoaiDichVu.THAM_MY) && tuoi < 16) {
                ds.add("Bệnh nhân " + tuoi + " tuổi: không khuyến cáo làm dịch vụ thẩm mỹ như tẩy trắng răng.");
            }
        } else if (bn != null && (loai.equals(LoaiDichVu.IMPLANT) || loai.equals(LoaiDichVu.THAM_MY))) {
            ds.add("Chưa có ngày sinh nên không kiểm tra được giới hạn tuổi.");
        }
        return ds.isEmpty() ? null : String.join("\n", ds);
    }

    // Từ khóa đầu tiên trong danh sách mà chuỗi (đã viết thường) có chứa; không có thì trả về null
    private String timTuKhoa(String chuoi, String[] tuKhoa) {
        for (String t : tuKhoa) {
            if (chuoi.contains(t)) {
                return t;
            }
        }
        return null;
    }

    // Lịch sử các lượt khám của hồ sơ, mới nhất trước
    public List<LuotKham> layLichSuKham(String maHoSo) {
        return luotKhamDAO.layTheoHoSo(maHoSo);
    }
}
