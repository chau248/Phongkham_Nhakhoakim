package nhakhoakim.dao;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import nhakhoakim.entity.DichVu;
import nhakhoakim.entity.HoSoBenhAn;
import nhakhoakim.entity.LichHen;
import nhakhoakim.entity.NhanVien;
public class LichHenService {
    private LichHenDAO lhDAO;
    
    public LichHenService() {
        this.lhDAO = new LichHenDAO();
    }
    public void huyLichHen(String maLichHen, String lyDoHuy) {
        if (lyDoHuy == null || lyDoHuy.trim().length() < 10) {
            throw new NghiepVuException("Lý do hủy phải có ít nhất 10 ký tự");
        }
        if (lyDoHuy.trim().length() > 255) {
            throw new NghiepVuException("Lý do hủy không được vượt quá 255 ký tự");
        }
        if (maLichHen == null || maLichHen.trim().isEmpty()) {
            throw new NghiepVuException("Mã lịch hẹn không được để trống");
        }
        lhDAO.huyLichHen(maLichHen, lyDoHuy.trim());
    }
    public HoSoBenhAn suaThongTinBenhNhan(LichHen lh, String ht, String gt, LocalDate ns, String sdt, String dc, String email, String cccd, String nguoiLH, String sdtNguoiLH) {
        if(lh == null) {
            throw new NghiepVuException("Lịch hẹn không được để trống");
        }
        if(ht == null || ht.trim().isEmpty()) {
            throw new NghiepVuException("Họ tên không được để trống");
        }
        if(ns == null) {
            throw new NghiepVuException("Ngày sinh không được để trống");
        }
        if(gt == null || gt.trim().isEmpty()) {
            throw new NghiepVuException("Giới tính không được để trống");
        }
        if(sdt == null || sdt.trim().isEmpty()) {
            throw new NghiepVuException("Số điện thoại không được để trống");
        }
        if(dc == null || dc.trim().isEmpty()) {
            throw new NghiepVuException("Địa chỉ không được để trống");
        }
        if(email == null || email.trim().isEmpty()) {
            throw new NghiepVuException("Email không được để trống");
        }
        if(cccd == null || cccd.trim().isEmpty()) {
            throw new NghiepVuException("Số căn cước công dân không được để trống");
        }
        if(nguoiLH == null || nguoiLH.trim().isEmpty()) {
            throw new NghiepVuException("Người liên hệ khẩn cấp không được để trống");
        }
        if(sdtNguoiLH == null || sdtNguoiLH.trim().isEmpty()) {
            throw new NghiepVuException("Số điện thoại người liên hệ khẩn cấp không được để trống");
        }
        if(ns.isAfter(LocalDate.now())) {
            throw new NghiepVuException("Ngày sinh không hợp lệ (sinh sau ngày hiện tại)");
        }

        return lhDAO.suaThongTinBenhNhan(lh, ht, gt, ns, sdt, dc, email, cccd, nguoiLH, sdtNguoiLH);
    }
    public List<LichHen> layTheoKhoang(LocalDate tuNgay, LocalDate denNgay) {
        if (tuNgay == null || denNgay == null || denNgay.isBefore(tuNgay)) {
            throw new NghiepVuException("Khoảng ngày xem lịch không hợp lệ");
        }
        return lhDAO.layTheoKhoang(tuNgay, denNgay);
    }

    public List<DichVu> layDichVuHoatDong() {
        return lhDAO.layDichVuHoatDong();
    }

    public List<NhanVien> layNhaSiDangLamViec() {
        return lhDAO.layNhaSiDangLamViec();
    }

    public LichHen themLichHen(LichHen lh) {
        kiemTraLichHen(lh);
        if (lh.getMaBN() == null || lh.getMaBN().trim().isEmpty()
                || lh.getMaNhanVien() == null || lh.getMaNhanVien().trim().isEmpty()
                || lh.getMaDichVu() == null || lh.getMaDichVu().trim().isEmpty()) {
            throw new NghiepVuException("Vui lòng chọn bệnh nhân, nha sĩ và dịch vụ");
        }
        if (lh.getThoiLuongDuKien() <= 0) {
            throw new NghiepVuException("Thời lượng dịch vụ không hợp lệ");
        }
        if (lh.getPhong() != null && lh.getPhong().length() > 20) {
            throw new NghiepVuException("Tên phòng không được vượt quá 20 ký tự");
        }
        if (lh.getHinhThucDat() == null || lh.getHinhThucDat().trim().isEmpty()) {
            lh.setHinhThucDat("Trực tiếp");
        }
        if (!Arrays.asList("Trực tiếp", "Điện thoại", "Online").contains(lh.getHinhThucDat())) {
            throw new NghiepVuException("Hình thức đặt phải là Trực tiếp, Điện thoại hoặc Online");
        }
        if (lh.getGhiChu() != null && lh.getGhiChu().length() > 255) {
            throw new NghiepVuException("Ghi chú không được vượt quá 255 ký tự");
        }
        kiemTraGioHoatDong(lh);
        if (lhDAO.coTrungLich(lh.getMaNhanVien(), lh.getNgayHen(), lh.getGioHen(),
                lh.getThoiLuongDuKien(), null)) {
            throw new NghiepVuException("Nha sĩ đã có lịch hẹn khác trong khung giờ này");
        }
        lh.setMaLichHen(MaGenerator.taoMa("LH"));
        lh.setTrangThai("Chờ xác nhận");
        return lhDAO.themLichHen(lh);
    }

    public void capNhatLichHen(LocalDate ngayHen, LocalTime gioHen, String ghiChu, LichHen lichHen) {
        if (lichHen == null || lichHen.getMaLichHen() == null || lichHen.getMaLichHen().trim().isEmpty()) {
            throw new NghiepVuException("Vui lòng chọn lịch hẹn cần cập nhật");
        }
        if (ngayHen == null) {
            throw new NghiepVuException("Ngày hẹn không được để trống");
        }
        if (gioHen == null) {
            throw new NghiepVuException("Giờ hẹn không được để trống");
        }
        if (ghiChu != null && ghiChu.length() > 255) {
            throw new NghiepVuException("Ghi chú không được vượt quá 255 ký tự");
        }
        LichHen thayDoi = new LichHen();
        thayDoi.setNgayHen(ngayHen);
        thayDoi.setGioHen(gioHen);
        thayDoi.setThoiLuongDuKien(lichHen.getThoiLuongDuKien());
        kiemTraGioHoatDong(thayDoi);
        if (lhDAO.coTrungLich(lichHen.getMaNhanVien(), ngayHen, gioHen,
                lichHen.getThoiLuongDuKien(), lichHen.getMaLichHen())) {
            throw new NghiepVuException("Nha sĩ đã có lịch hẹn khác trong khung giờ này");
        }
        lhDAO.capNhatLichHen(ngayHen, gioHen, ghiChu, lichHen.getMaLichHen());
    }

    public void huyLichHen(LichHen lichHen, String lyDoHuy) {
        if (lichHen == null || lichHen.getMaLichHen() == null || lichHen.getMaLichHen().trim().isEmpty()) {
            throw new NghiepVuException("Vui lòng chọn lịch hẹn cần hủy");
        }
        if (lyDoHuy == null || lyDoHuy.trim().length() < 10) {
            throw new NghiepVuException("Lý do hủy phải có ít nhất 10 ký tự");
        }
        if (lyDoHuy.trim().length() > 255) {
            throw new NghiepVuException("Lý do hủy không được vượt quá 255 ký tự");
        }
        if ("Đã hủy".equals(lichHen.getTrangThai())) {
            throw new NghiepVuException("Lịch hẹn này đã được hủy");
        }
        lhDAO.huyLichHen(lichHen.getMaLichHen(), lyDoHuy.trim());
    }

    private void kiemTraLichHen(LichHen lh) {
        if (lh == null) {
            throw new NghiepVuException("Thông tin lịch hẹn không được để trống");
        }
        if (lh.getNgayHen() == null || lh.getGioHen() == null) {
            throw new NghiepVuException("Ngày và giờ hẹn không được để trống");
        }
        if (lh.getGhiChu() != null && lh.getGhiChu().length() > 255) {
            throw new NghiepVuException("Ghi chú không được vượt quá 255 ký tự");
        }
    }

    private void kiemTraGioHoatDong(LichHen lh) {
        LocalTime moCua = LocalTime.of(5, 0);
        LocalTime dongCua = LocalTime.of(20, 0);
        LocalTime ketThuc = lh.getGioHen().plusMinutes(lh.getThoiLuongDuKien());
        if (lh.getGioHen().isBefore(moCua) || ketThuc.isAfter(dongCua) || !ketThuc.isAfter(lh.getGioHen())) {
            throw new NghiepVuException("Lịch hẹn phải nằm trong khung giờ hoạt động từ 05:00 đến 20:00");
        }
    }

}
