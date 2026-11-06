package controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import dao.LuotKhamDAO;
import dao.NghiepVuException;
import dao.RangDieuTriDAO;
import dao.TienIchDAO;
import entity.BenhNhan;
import entity.DichVu;
import entity.HangSo;
import entity.HoSoBenhAn;
import entity.LichHen;
import entity.LieuTrinh;
import entity.LuotKham;
import entity.LuotKhamDichVu;
import entity.NhanVien;
import entity.RangDieuTri;

// Nghiệp vụ lượt khám: bắt đầu khám
public class LuotKhamController {

    private static final String TIEN_TO_HUY = "Hủy khám: ";

    private LuotKhamDAO luotKhamDAO = new LuotKhamDAO();
    private RangDieuTriDAO rangDAO = new RangDieuTriDAO();
    private HoSoBenhAnController hoSoService = new HoSoBenhAnController();
    private DichVuController dichVuService = new DichVuController();
    private NhanVienController nhanVienService = new NhanVienController();
    private LichHenController lichHenService = new LichHenController();
    private CaTrucController caTrucService = new CaTrucController();
    private LieuTrinhController lieuTrinhService = new LieuTrinhController();

    // Lượt khám theo mã
    public LuotKham timTheoMa(String maLuotKham) {
        LuotKham lk = luotKhamDAO.timTheoMa(maLuotKham);
        if (lk == null) {
            throw new NghiepVuException("Không tìm thấy lượt khám " + maLuotKham);
        }
        return lk;
    }

    // Lượt đang khám của nha sĩ
    public LuotKham layDangKham(String maNhaSi) {
        return luotKhamDAO.timDangKham(maNhaSi);
    }

    // Dịch vụ không gắn răng của lượt khám
    public List<LuotKhamDichVu> layDichVuKhongGanRang(String maLuotKham) {
        return luotKhamDAO.layDichVu(maLuotKham);
    }

    // Mọi dịch vụ đã thực hiện
    // Dịch vụ gắn răng tính theo
    public List<LuotKhamDichVu> layDichVuDaThucHien(String maLuotKham) {
        Map<String, LuotKhamDichVu> theoDichVu = new LinkedHashMap<>();
        Set<String> daTinh = new HashSet<>();
        for (RangDieuTri r : rangDAO.layTheoLuotKham(maLuotKham)) {
            if (r.getMaDichVu() != null && daTinh.add(r.getSoFDI() + "|" + r.getMaDichVu().getMaDichVu())) {
                congDichVu(theoDichVu, maLuotKham, r.getMaDichVu(), 1);
            }
        }
        for (LuotKhamDichVu x : luotKhamDAO.layDichVu(maLuotKham)) {
            congDichVu(theoDichVu, maLuotKham, x.getMaDichVu(), x.getSoLuong());
        }
        return new ArrayList<>(theoDichVu.values());
    }

    // Khám vãng lai
    public LuotKham batDauKham(String maHoSo, String maNhaSi, String lyDoKham) {
        String lyDo = TienIchController.rongThanhNull(lyDoKham);
        NhanVien nhaSi = kiemTraNhaSi(maNhaSi);
        HoSoBenhAn hs = kiemTraHoSo(maHoSo);
        LichHen lichHen = null;
        for (LichHen lh : lichHenService.layDaXacNhanTheoBenhNhan(hs.getMaBN().getMaBN(), LocalDate.now())) {
            if (lichHen == null || lh.getMaNhanVien().getMaNV().equals(maNhaSi)) {
                lichHen = lh;
            }
        }
        return taoLuotKham(hs, nhaSi, lyDo, lichHen);
    }

    // Khám vãng lai theo mã bệnh nhân
    public LuotKham batDauKhamTheoBenhNhan(String maBN, String maNhaSi, String lyDoKham) {
        return batDauKham(hoSoService.timTheoBenhNhan(maBN).getMaHoSo(), maNhaSi, lyDoKham);
    }

    // Khám theo lịch hẹn: lịch phải đã xác nhận
    public LuotKham batDauKhamTheoLich(String maLichHen, String maNhaSi) {
        LichHen lh = lichHenService.layTheoMa(maLichHen);
        if (lh == null) {
            throw new NghiepVuException("Không tìm thấy lịch hẹn " + maLichHen);
        }
        if (!"Đã xác nhận".equals(lh.getTrangThai())) {
            throw new NghiepVuException("Lịch hẹn đang ở trạng thái \"" + lh.getTrangThai() + "\", chỉ khám được lịch đã xác nhận");
        }
        if (!lh.getNgayHen().equals(LocalDate.now())) {
            throw new NghiepVuException("Chỉ bắt đầu khám đúng ngày hẹn (" + lh.getNgayHen() + ")");
        }
        if (!lh.getMaNhanVien().getMaNV().equals(maNhaSi)) {
            throw new NghiepVuException("Lịch hẹn này thuộc nha sĩ khác");
        }
        NhanVien nhaSi = kiemTraNhaSi(maNhaSi);
        HoSoBenhAn hs = hoSoService.timTheoBenhNhan(lh.getMaBN().getMaBN());
        kiemTraHoSo(hs.getMaHoSo());
        return taoLuotKham(hs, nhaSi, "Theo lịch hẹn: " + lh.getMaDichVu().getTenDichVu(), lh);
    }

    // Lưu một lần lý do
    public void luuNoiDungKham(String maLuotKham, String lyDoKham, String chanDoan, String huongDieuTri, String donThuoc, String ghiChu) {
        LuotKham lk = layLuotKhamDeSua(maLuotKham);
        lk.setLyDoKham(rongThanhNull(lyDoKham, 255, "Lý do khám"));
        lk.setChanDoan(rongThanhNull(chanDoan, 500, "Chẩn đoán"));
        lk.setHuongDieuTri(rongThanhNull(huongDieuTri, 500, "Hướng điều trị"));
        lk.setDonThuoc(rongThanhNull(donThuoc, 1000, "Đơn thuốc"));
        lk.setGhiChu(rongThanhNull(ghiChu, 255, "Ghi chú"));
        luotKhamDAO.capNhat(lk);
    }

    // Gắn lượt khám vào một liệu trình đang thực hiện của cùng bệnh nhân
    public void datLieuTrinh(String maLuotKham, String maLieuTrinh) {
        LuotKham lk = layLuotKhamDeSua(maLuotKham);
        if (maLieuTrinh == null) {
            lk.setMaLieuTrinh(null);
        } else {
            LieuTrinh lt = lieuTrinhService.timTheoMa(maLieuTrinh);
            if (!lt.getMaBN().getMaBN().equals(lk.getMaHoSo().getMaBN().getMaBN())) {
                throw new NghiepVuException("Liệu trình " + maLieuTrinh + " không phải của bệnh nhân này");
            }
            if (!HangSo.LIEU_TRINH_DANG_THUC_HIEN.equals(lt.getTrangThai())) {
                throw new NghiepVuException("Liệu trình " + maLieuTrinh + " đã " + lt.getTrangThai().toLowerCase() + ", không gắn thêm buổi khám");
            }
            lk.setMaLieuTrinh(lt);
        }
        luotKhamDAO.capNhat(lk);
    }

    // Các lượt khám của một bệnh nhân
    public List<LuotKham> layTheoBenhNhan(String maBN) {
        return luotKhamDAO.layTheoBenhNhan(maBN);
    }

    // Đơn thuốc của một lượt khám dạng chữ
    public String taoNoiDungDonThuoc(String maLuotKham) {
        TienIchController.yeuCau(PhienLamViec::getNhanVienHienTai, "in đơn thuốc", HangSo.VAI_TRO_NHA_SI, HangSo.VAI_TRO_QUAN_LY);
        LuotKham lk = timTheoMa(maLuotKham);
        if (HangSo.DA_HUY.equals(lk.getTrangThai())) {
            throw new NghiepVuException("Lượt khám đã hủy, không có đơn thuốc");
        }
        if (lk.getDonThuoc() == null || lk.getDonThuoc().trim().isEmpty()) {
            throw new NghiepVuException("Lượt khám này chưa kê đơn thuốc");
        }
        BenhNhan bn = lk.getMaHoSo().getMaBN();
        CauHinhController cauHinh = new CauHinhController();
        StringBuilder sb = new StringBuilder();
        sb.append(giuaDong(cauHinh.layThamSo(CauHinhController.TEN_PHONG_KHAM).toUpperCase())).append('\n');
        sb.append(giuaDong(cauHinh.layThamSo(CauHinhController.DIA_CHI))).append('\n');
        sb.append(giuaDong("Điện thoại: " + cauHinh.layThamSo(CauHinhController.SO_DIEN_THOAI))).append('\n');
        sb.append("=".repeat(DO_RONG_DON)).append('\n');
        sb.append(giuaDong("ĐƠN THUỐC")).append("\n\n");
        sb.append("Họ tên    : ").append(bn.getHoTen()).append("   ").append(bn.getGioiTinh() == null ? "" : bn.getGioiTinh())
                .append(bn.getNgaySinh() == null ? "" : "   Tuổi: " + bn.tinhTuoi()).append('\n');
        if (bn.getDiaChi() != null) {
            sb.append("Địa chỉ   : ").append(bn.getDiaChi()).append('\n');
        }
        sb.append("Mã bệnh nhân: ").append(bn.getMaBN()).append("   Lượt khám: ").append(lk.getMaLuotKham()).append('\n');
        sb.append("Chẩn đoán : ").append(lk.getChanDoan() == null ? "" : lk.getChanDoan()).append('\n');
        sb.append("-".repeat(DO_RONG_DON)).append('\n');
        int stt = 1;
        for (String dong : lk.getDonThuoc().split("\\r?\\n|;")) {
            if (!dong.trim().isEmpty()) {
                sb.append(stt++).append(". ").append(dong.trim()).append('\n');
            }
        }
        sb.append("-".repeat(DO_RONG_DON)).append('\n');
        if (lk.getNgayTaiKham() != null) {
            sb.append("Hẹn tái khám: ").append(lk.getNgayTaiKham().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"))).append('\n');
        }
        sb.append("Lời dặn: uống thuốc đúng liều, có bất thường liên hệ phòng khám.\n\n");
        LocalDateTime ngay = lk.getGioKetThuc() == null ? lk.getGioBatDau() : lk.getGioKetThuc();
        sb.append(String.format("%" + DO_RONG_DON + "s\n", "Ngày " + String.format("%02d tháng %02d năm %d", ngay.getDayOfMonth(), ngay.getMonthValue(), ngay.getYear())));
        sb.append(String.format("%" + DO_RONG_DON + "s\n", "Bác sĩ điều trị"));
        sb.append("\n\n");
        sb.append(String.format("%" + DO_RONG_DON + "s\n", lk.getMaNhanVienPhuTrach().getHoTen()));
        return sb.toString();
    }

    private static final int DO_RONG_DON = 64; // Số ký tự mỗi dòng của đơn thuốc in

    private static String giuaDong(String chu) {
        return " ".repeat(Math.max(0, (DO_RONG_DON - chu.length()) / 2)) + chu;
    }

    // Thêm một dịch vụ không gắn răng
    public void themDichVu(String maLuotKham, String maDichVu, int soLuong) {
        layLuotKhamDeSua(maLuotKham);
        DichVu dv = layDichVuConHoatDong(maDichVu);
        if ("Răng".equals(dv.getDonViTinh())) {
            throw new NghiepVuException("Dịch vụ \"" + dv.getTenDichVu() + "\" tính theo răng, hãy ghi ở màn hình Sơ đồ răng");
        }
        if (soLuong <= 0) {
            throw new NghiepVuException("Số lượng phải lớn hơn 0");
        }
        LuotKhamDichVu x = new LuotKhamDichVu();
        x.setMaLuotKham(timTheoMa(maLuotKham));
        x.setMaDichVu(dv);
        x.setSoLuong(soLuong);
        luotKhamDAO.themDichVu(x);
    }

    // Bỏ một dịch vụ không gắn răng khỏi lượt khám
    public void xoaDichVu(String maLuotKham, int maDong) {
        layLuotKhamDeSua(maLuotKham);
        if (!luotKhamDAO.xoaDichVu(maLuotKham, maDong)) {
            throw new NghiepVuException("Dịch vụ này không thuộc lượt khám đang sửa");
        }
    }

    // Cảnh báo (không chặn) khi chọn dịch vụ cần liệu trình; không có thì null
    public String canhBaoThuThuat(String maLuotKham, String maDichVu) {
        LuotKham lk = timTheoMa(maLuotKham);
        DichVu dv = dichVuService.timTheoMa(maDichVu);
        String maBN = lk.getMaHoSo().getMaBN().getMaBN();
        List<String> ds = new ArrayList<>();
        String lieuTrinh = hoSoService.canhBaoLieuTrinh(maBN, dv);
        if (lieuTrinh != null) {
            ds.add(lieuTrinh);
        }
        return ds.isEmpty() ? null : String.join("\n\n", ds);
    }

    // Hoàn tất khám: cần chẩn đoán và ít nhất một dịch vụ
    public void hoanTatKham(String maLuotKham) {
        LuotKham lk = layLuotKhamDeSua(maLuotKham);
        if (lk.getChanDoan() == null || lk.getChanDoan().trim().isEmpty()) {
            throw new NghiepVuException("Phải nhập chẩn đoán trước khi hoàn tất khám");
        }
        if (layDichVuDaThucHien(maLuotKham).isEmpty()) {
            throw new NghiepVuException("Phải ghi ít nhất một dịch vụ đã thực hiện (khám tổng quát cũng là một dịch vụ) trước khi hoàn tất khám");
        }
        lk.setGioKetThuc(tinhGioKetThuc(lk));
        lk.setTrangThai(HangSo.HOAN_THANH);
        luotKhamDAO.capNhat(lk);
        if (lk.getMaLichHen() != null) {
            lichHenService.capNhatTrangThai(lk.getMaLichHen().getMaLichHen(), HangSo.HOAN_THANH);
        }
    }

    // Hủy lượt khám đang khám
    public void huyLuotKham(String maLuotKham, String lyDo) {
        LuotKham lk = layLuotKhamDeSua(maLuotKham);
        String lyDoHuy = TienIchController.rongThanhNull(lyDo);
        if (lyDoHuy == null || lyDoHuy.length() < 5) {
            throw new NghiepVuException("Phải nhập lý do hủy lượt khám (ít nhất 5 ký tự)");
        }
        if (!rangDAO.layTheoLuotKham(maLuotKham).isEmpty() || !luotKhamDAO.layDichVu(maLuotKham).isEmpty()) {
            throw new NghiepVuException("Lượt khám đã ghi nhận dịch vụ hoặc răng điều trị nên không thể hủy, hãy hoàn tất khám");
        }
        if (lk.getNgayTaiKham() != null) {
            huyLichTaiKhamCuaLuot(lk);
        }
        lk.setGioKetThuc(tinhGioKetThuc(lk));
        lk.setTrangThai(HangSo.DA_HUY);
        lk.setGhiChu(ghepGhiChuHuy(lk.getGhiChu(), lyDoHuy));
        luotKhamDAO.capNhat(lk);
        if (lk.getMaLichHen() != null) {
            lichHenService.capNhatTrangThai(lk.getMaLichHen().getMaLichHen(), "Đã xác nhận");
        }
    }

    // Chỉ định tái khám: ngày phải sau hôm nay
    // Trả về true nếu tạo được lịch hẹn chờ xác nhận
    public boolean datTaiKham(String maLuotKham, LocalDate ngayTaiKham, String maDichVu) {
        LuotKham lk = layLuotKhamDeSua(maLuotKham);
        if (lk.getNgayTaiKham() != null) {
            throw new NghiepVuException("Lượt khám này đã có lịch tái khám ngày " + lk.getNgayTaiKham());
        }
        if (ngayTaiKham == null || !ngayTaiKham.isAfter(lk.getGioBatDau().toLocalDate())) {
            throw new NghiepVuException("Ngày tái khám phải sau ngày khám");
        }
        layDichVuConHoatDong(maDichVu);
        boolean coLich = true;
        try {
            lichHenService.taoLichTaiKham(lk.getMaHoSo().getMaBN().getMaBN(), maDichVu, ngayTaiKham, lk.getMaNhanVienPhuTrach().getMaNV());
        } catch (NghiepVuException e) {
            coLich = false; // Chưa có ca trực hoặc hết giờ thì chỉ lưu ngày tái khám
        }
        lk.setNgayTaiKham(ngayTaiKham);
        luotKhamDAO.capNhat(lk);
        return coLich;
    }

    // Hủy lịch tái khám đã đặt
    public void huyTaiKham(String maLuotKham) {
        LuotKham lk = layLuotKhamDeSua(maLuotKham);
        if (lk.getNgayTaiKham() == null) {
            throw new NghiepVuException("Lượt khám này chưa đặt lịch tái khám");
        }
        huyLichTaiKhamCuaLuot(lk);
        luotKhamDAO.capNhat(lk);
    }

    // Nha sĩ được bắt đầu khám: đúng người đăng nhập
    private NhanVien kiemTraNhaSi(String maNhaSi) {
        NhanVien dangNhap = PhienLamViec.getNhanVienHienTai();
        if (dangNhap == null || !dangNhap.getMaNV().equals(maNhaSi)) {
            throw new NghiepVuException("Chỉ được bắt đầu khám dưới tên của chính mình");
        }
        NhanVien nhaSi = nhanVienService.timTheoMa(maNhaSi);
        if (nhaSi == null || !nhaSi.laVaiTro(HangSo.VAI_TRO_NHA_SI)) {
            throw new NghiepVuException("Chỉ nha sĩ mới được khám bệnh");
        }
        if (HangSo.VAI_TRO_TOAN_QUYEN.equals(nhaSi.getVaiTro())) {
            // Tài khoản Toàn quyền khám thử không cần chứng chỉ hay ca trực
            if (luotKhamDAO.timDangKham(maNhaSi) != null) {
                throw new NghiepVuException("Bạn đang có một lượt khám chưa hoàn tất, hãy hoàn tất hoặc hủy lượt khám đó trước khi khám người khác");
            }
            return nhaSi;
        }
        if (!nhanVienService.chungChiConHan(maNhaSi, LocalDate.now())) {
            throw new NghiepVuException("Chứng chỉ hành nghề của nha sĩ đã hết hạn, không thể bắt đầu khám");
        }
        String ca = LocalTime.now().isBefore(LocalTime.NOON) ? "Sáng" : "Chiều";
        if (!caTrucService.coCaTruc(maNhaSi, LocalDate.now(), ca)) {
            throw new NghiepVuException("Nha sĩ không có ca trực buổi " + ca.toLowerCase() + " hôm nay nên không thể bắt đầu khám");
        }
        if (luotKhamDAO.timDangKham(maNhaSi) != null) {
            throw new NghiepVuException("Bạn đang có một lượt khám chưa hoàn tất, hãy hoàn tất hoặc hủy lượt khám đó trước khi khám người khác");
        }
        return nhaSi;
    }

    // Hồ sơ được khám: còn mở
    private HoSoBenhAn kiemTraHoSo(String maHoSo) {
        HoSoBenhAn hs = hoSoService.timTheoMa(maHoSo);
        if (HangSo.HO_SO_DA_DONG.equals(hs.getTrangThaiHoSo())) {
            throw new NghiepVuException("Hồ sơ bệnh án đã đóng, không thể khám");
        }
        BenhNhan bn = hs.getMaBN();
        if (!HangSo.DANG_DIEU_TRI.equals(bn.getTrangThai())) {
            throw new NghiepVuException("Bệnh nhân " + bn.getHoTen() + " đang ở trạng thái " + bn.getTrangThai() + ", không thể bắt đầu khám");
        }
        if (luotKhamDAO.timDangKhamTheoHoSo(maHoSo) != null) {
            throw new NghiepVuException("Bệnh nhân đang được khám ở một lượt khám khác");
        }
        return hs;
    }

    // Lượt khám để sửa: phải đang khám và người sửa là nha sĩ phụ trách
    private LuotKham layLuotKhamDeSua(String maLuotKham) {
        LuotKham lk = timTheoMa(maLuotKham);
        if (!HangSo.DANG_KHAM.equals(lk.getTrangThai())) {
            throw new NghiepVuException("Lượt khám đang ở trạng thái \"" + lk.getTrangThai() + "\", không thể sửa");
        }
        NhanVien nguoiSua = PhienLamViec.getNhanVienHienTai();
        if (nguoiSua == null || !lk.getMaNhanVienPhuTrach().getMaNV().equals(nguoiSua.getMaNV())) {
            throw new NghiepVuException("Chỉ nha sĩ phụ trách mới được sửa lượt khám này");
        }
        return lk;
    }

    // Dịch vụ phải tồn tại và còn cung cấp
    private DichVu layDichVuConHoatDong(String maDichVu) {
        DichVu dv = dichVuService.timTheoMa(maDichVu);
        if (dv == null || !dv.isTrangThaiHoatDong()) {
            throw new NghiepVuException("Dịch vụ không tồn tại hoặc đã ngừng cung cấp");
        }
        return dv;
    }

    // Tạo và lưu lượt khám mới ở trạng thái Đang khám
    private LuotKham taoLuotKham(HoSoBenhAn hs, NhanVien nhaSi, String lyDoKham, LichHen lichHen) {
        LuotKham lk = new LuotKham();
        lk.setMaLuotKham(TienIchDAO.taoMa("LK"));
        lk.setMaHoSo(hs);
        lk.setMaNhanVienPhuTrach(nhaSi);
        lk.setMaLichHen(lichHen);
        lk.setGioBatDau(LocalDateTime.now());
        lk.setLyDoKham(lyDoKham);
        lk.setTrangThai(HangSo.DANG_KHAM);
        luotKhamDAO.them(lk);
        return lk;
    }

    // Giờ kết thúc là bây giờ nhưng luôn sau giờ bắt đầu
    private LocalDateTime tinhGioKetThuc(LuotKham lk) {
        LocalDateTime bayGio = LocalDateTime.now();
        return bayGio.isAfter(lk.getGioBatDau()) ? bayGio : lk.getGioBatDau().plusMinutes(1);
    }

    // Hủy lịch hẹn tái khám tương ứng và xóa ngày tái khám trên lượt khám
    private void huyLichTaiKhamCuaLuot(LuotKham lk) {
        lichHenService.huyLichTaiKham(lk.getMaHoSo().getMaBN().getMaBN(), lk.getNgayTaiKham());
        lk.setNgayTaiKham(null);
    }

    // Ghép ghi chú cũ với phần hủy khám
    private String ghepGhiChuHuy(String ghiChuCu, String lyDoHuy) {
        String phanHuy = TIEN_TO_HUY + lyDoHuy;
        String cu = TienIchController.rongThanhNull(ghiChuCu);
        if (cu == null) {
            return phanHuy;
        }
        int conLai = 255 - 3 - phanHuy.length();
        if (conLai <= 0) {
            return phanHuy;
        }
        if (cu.length() > conLai) {
            cu = cu.substring(0, conLai).trim();
        }
        return cu.isEmpty() ? phanHuy : cu + " | " + phanHuy;
    }

    // Cộng thêm số lượng một dịch vụ vào bảng gộp
    private void congDichVu(Map<String, LuotKhamDichVu> bang, String maLuotKham, DichVu dv, int soLuong) {
        LuotKhamDichVu x = bang.get(dv.getMaDichVu());
        if (x == null) {
            x = new LuotKhamDichVu();
            LuotKham lk = new LuotKham();
            lk.setMaLuotKham(maLuotKham);
            x.setMaLuotKham(lk);
            x.setMaDichVu(dv);
            bang.put(dv.getMaDichVu(), x);
        }
        x.setSoLuong(x.getSoLuong() + soLuong);
    }

    // Cắt khoảng trắng
    private static String rongThanhNull(String s, int toiDa, String tenTruong) {
        String gon = TienIchController.rongThanhNull(s);
        return gon;
    }
}
