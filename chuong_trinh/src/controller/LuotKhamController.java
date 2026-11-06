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

// Nghiệp vụ lượt khám: bắt đầu khám, ghi nội dung, chọn dịch vụ, hoàn tất, hủy, tái khám
public class LuotKhamController {

    private static final int TOI_DA_SO_LUONG = 10;       // số lượng tối đa của một dịch vụ không gắn răng
    private static final int TOI_DA_NGAY_TAI_KHAM = 180; // hẹn tái khám tối đa 6 tháng sau
    private static final String TIEN_TO_HUY = "Hủy khám: ";

    private LuotKhamDAO luotKhamDAO = new LuotKhamDAO();
    private RangDieuTriDAO rangDAO = new RangDieuTriDAO();
    private HoSoBenhAnController hoSoService = new HoSoBenhAnController();
    private DichVuController dichVuService = new DichVuController();
    private NhanVienController nhanVienService = new NhanVienController();
    private LichHenController lichHenService = new LichHenController();
    private CaTrucController caTrucService = new CaTrucController();

    // ----- Tìm -----

    // Lượt khám theo mã; không có thì báo lỗi
    public LuotKham timTheoMa(String maLuotKham) {
        LuotKham lk = luotKhamDAO.timTheoMa(maLuotKham);
        if (lk == null) {
            throw new NghiepVuException("Không tìm thấy lượt khám " + maLuotKham);
        }
        return lk;
    }

    // Lượt đang khám của nha sĩ; không có thì null
    public LuotKham layDangKham(String maNhaSi) {
        return luotKhamDAO.timDangKham(maNhaSi);
    }

    // Dịch vụ không gắn răng của lượt khám
    public List<LuotKhamDichVu> layDichVuKhongGanRang(String maLuotKham) {
        return luotKhamDAO.layDichVu(maLuotKham);
    }

    // Mọi dịch vụ đã thực hiện (gắn răng và không gắn răng) gộp theo dịch vụ; dùng để lập hóa đơn.
    // Dịch vụ gắn răng tính theo (răng, dịch vụ): ghi nhiều lần trên cùng một răng chỉ tính một lần.
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

    // ----- Bắt đầu khám -----

    // Khám vãng lai (không bấm từ lịch hẹn); nếu bệnh nhân có lịch đã xác nhận hôm nay thì gắn lịch đó vào lượt khám
    public LuotKham batDauKham(String maHoSo, String maNhaSi, String lyDoKham) {
        String lyDo = TienIchController.rongThanhNull(lyDoKham);
        TienIchController.doDai(lyDo, 255, "Lý do khám");
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

    // Khám theo lịch hẹn: lịch phải đã xác nhận, đúng ngày hôm nay và đúng nha sĩ
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

    // ----- Ghi nội dung khám -----

    // Lưu một lần lý do, chẩn đoán, hướng điều trị, đơn thuốc, ghi chú (ô để trống thì xóa nội dung)
    public void luuNoiDungKham(String maLuotKham, String lyDoKham, String chanDoan, String huongDieuTri, String donThuoc, String ghiChu) {
        LuotKham lk = layLuotKhamDeSua(maLuotKham);
        lk.setLyDoKham(rongThanhNull(lyDoKham, 255, "Lý do khám"));
        lk.setChanDoan(rongThanhNull(chanDoan, 500, "Chẩn đoán"));
        lk.setHuongDieuTri(rongThanhNull(huongDieuTri, 500, "Hướng điều trị"));
        lk.setDonThuoc(rongThanhNull(donThuoc, 1000, "Đơn thuốc"));
        lk.setGhiChu(rongThanhNull(ghiChu, 255, "Ghi chú"));
        luotKhamDAO.capNhat(lk);
    }

    // Thêm một dịch vụ không gắn răng (khám tổng quát, lấy cao răng...). Dịch vụ tính theo răng phải ghi ở Sơ đồ răng.
    public void themDichVu(String maLuotKham, String maDichVu, int soLuong) {
        layLuotKhamDeSua(maLuotKham);
        DichVu dv = layDichVuConHoatDong(maDichVu);
        if ("Răng".equals(dv.getDonViTinh())) {
            throw new NghiepVuException("Dịch vụ \"" + dv.getTenDichVu() + "\" tính theo răng, hãy ghi ở màn hình Sơ đồ răng");
        }
        if (soLuong <= 0 || soLuong > TOI_DA_SO_LUONG) {
            throw new NghiepVuException("Số lượng phải từ 1 đến " + TOI_DA_SO_LUONG);
        }
        int daCo = 0; // cùng dịch vụ đã thêm trước đó trong lượt khám này
        for (LuotKhamDichVu cu : luotKhamDAO.layDichVu(maLuotKham)) {
            if (cu.getMaDichVu().getMaDichVu().equals(maDichVu)) {
                daCo += cu.getSoLuong();
            }
        }
        if (daCo + soLuong > TOI_DA_SO_LUONG) {
            throw new NghiepVuException("Tổng số lượng của một dịch vụ trong một lượt khám không quá " + TOI_DA_SO_LUONG + " (đã có " + daCo + ")");
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

    // Cảnh báo (không chặn) khi chọn dịch vụ: thiếu liệu trình, bệnh nền, thuốc đang dùng, tuổi; không có thì null
    public String canhBaoThuThuat(String maLuotKham, String maDichVu) {
        LuotKham lk = timTheoMa(maLuotKham);
        DichVu dv = dichVuService.timTheoMa(maDichVu);
        String maBN = lk.getMaHoSo().getMaBN().getMaBN();
        List<String> ds = new ArrayList<>();
        String lieuTrinh = hoSoService.canhBaoLieuTrinh(maBN, dv);
        if (lieuTrinh != null) {
            ds.add(lieuTrinh);
        }
        String yTe = hoSoService.canhBaoYTe(maBN, dv);
        if (yTe != null) {
            ds.add(yTe);
        }
        return ds.isEmpty() ? null : String.join("\n\n", ds);
    }

    // ----- Hoàn tất, hủy -----

    // Hoàn tất khám: cần chẩn đoán và ít nhất một dịch vụ; khóa lượt khám và hoàn thành lịch hẹn
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

    // Hủy lượt khám đang khám (chọn nhầm người, bệnh nhân bỏ về...): cần lý do, và chưa ghi dịch vụ hay răng nào
    public void huyLuotKham(String maLuotKham, String lyDo) {
        LuotKham lk = layLuotKhamDeSua(maLuotKham);
        String lyDoHuy = TienIchController.rongThanhNull(lyDo);
        if (lyDoHuy == null || lyDoHuy.length() < 5) {
            throw new NghiepVuException("Phải nhập lý do hủy lượt khám (ít nhất 5 ký tự)");
        }
        TienIchController.doDai(lyDoHuy, 150, "Lý do hủy khám");
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

    // ----- Tái khám -----

    // Chỉ định tái khám: ngày phải sau hôm nay, trong vòng 180 ngày; tạo lịch hẹn Chờ xác nhận
    public void datTaiKham(String maLuotKham, LocalDate ngayTaiKham, String maDichVu) {
        LuotKham lk = layLuotKhamDeSua(maLuotKham);
        if (lk.getNgayTaiKham() != null) {
            throw new NghiepVuException("Lượt khám này đã có lịch tái khám ngày " + lk.getNgayTaiKham());
        }
        if (ngayTaiKham == null || !ngayTaiKham.isAfter(lk.getGioBatDau().toLocalDate()) || !ngayTaiKham.isAfter(LocalDate.now())) {
            throw new NghiepVuException("Ngày tái khám phải sau ngày khám và sau hôm nay");
        }
        if (ngayTaiKham.isAfter(lk.getGioBatDau().toLocalDate().plusDays(TOI_DA_NGAY_TAI_KHAM))) {
            throw new NghiepVuException("Chỉ hẹn tái khám trong vòng " + TOI_DA_NGAY_TAI_KHAM + " ngày (6 tháng) kể từ ngày khám");
        }
        layDichVuConHoatDong(maDichVu);
        // Tạo lịch hẹn trước: lịch lỗi (trùng giờ, không có ca trực...) thì không lưu ngày tái khám
        lichHenService.taoLichTaiKham(lk.getMaHoSo().getMaBN().getMaBN(), maDichVu, ngayTaiKham, lk.getMaNhanVienPhuTrach().getMaNV());
        lk.setNgayTaiKham(ngayTaiKham);
        luotKhamDAO.capNhat(lk);
    }

    // Hủy lịch tái khám đã đặt (đặt nhầm ngày hoặc nhầm dịch vụ) để đặt lại
    public void huyTaiKham(String maLuotKham) {
        LuotKham lk = layLuotKhamDeSua(maLuotKham);
        if (lk.getNgayTaiKham() == null) {
            throw new NghiepVuException("Lượt khám này chưa đặt lịch tái khám");
        }
        huyLichTaiKhamCuaLuot(lk);
        luotKhamDAO.capNhat(lk);
    }

    // ----- Kiểm tra -----

    // Nha sĩ được bắt đầu khám: đúng người đăng nhập, đúng vai trò, còn chứng chỉ, có ca trực buổi này, không có lượt đang dở
    private NhanVien kiemTraNhaSi(String maNhaSi) {
        NhanVien dangNhap = PhienLamViec.getNhanVienHienTai();
        if (dangNhap == null || !dangNhap.getMaNV().equals(maNhaSi)) {
            throw new NghiepVuException("Chỉ được bắt đầu khám dưới tên của chính mình");
        }
        NhanVien nhaSi = nhanVienService.timTheoMa(maNhaSi);
        if (nhaSi == null || !HangSo.VAI_TRO_NHA_SI.equals(nhaSi.getVaiTro())) {
            throw new NghiepVuException("Chỉ nha sĩ mới được khám bệnh");
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

    // Hồ sơ được khám: còn mở, bệnh nhân đang điều trị, chưa có lượt khám nào đang dở
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

    // ----- Hàm phụ -----

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

    // Giờ kết thúc là bây giờ nhưng luôn sau giờ bắt đầu (CSDL bắt gioKetThuc > gioBatDau)
    private LocalDateTime tinhGioKetThuc(LuotKham lk) {
        LocalDateTime bayGio = LocalDateTime.now();
        return bayGio.isAfter(lk.getGioBatDau()) ? bayGio : lk.getGioBatDau().plusMinutes(1);
    }

    // Hủy lịch hẹn tái khám tương ứng và xóa ngày tái khám trên lượt khám (người gọi tự lưu)
    private void huyLichTaiKhamCuaLuot(LuotKham lk) {
        lichHenService.huyLichTaiKham(lk.getMaHoSo().getMaBN().getMaBN(), lk.getNgayTaiKham());
        lk.setNgayTaiKham(null);
    }

    // Ghép ghi chú cũ với phần hủy khám, không vượt 255 ký tự
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

    // Cắt khoảng trắng, rỗng thì null, kiểm tra độ dài cột
    private static String rongThanhNull(String s, int toiDa, String tenTruong) {
        String gon = TienIchController.rongThanhNull(s);
        TienIchController.doDai(gon, toiDa, tenTruong);
        return gon;
    }
}
