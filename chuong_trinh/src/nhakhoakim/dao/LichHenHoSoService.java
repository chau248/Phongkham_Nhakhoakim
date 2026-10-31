package nhakhoakim.dao;
import nhakhoakim.entity.HoSoBenhAn;
import nhakhoakim.entity.LichHen;
import java.time.LocalDate;
import java.time.LocalTime;
public class LichHenHoSoService {
    private LichHenDAO lhDAO;
    
    public LichHenHoSoService() {
        this.lhDAO = new LichHenDAO();
    }
    public void themHoSoBenhAnVaoLichHen(HoSoBenhAn hs, LichHen lh) {
        if(hs == null) {
            throw new NghiepVuException("Hồ sơ bệnh án không được để trống");
        }
        if(lh == null) {
            throw new NghiepVuException("Lịch hẹn không được để trống");
        }
        lhDAO.themHoSoBenhAnVaoLichHen(hs, lh);
    }
    public void huyLichHen(String maLichHen, String lyDoHuy) {
        if(lyDoHuy == null || lyDoHuy.trim().isEmpty()) {
            throw new NghiepVuException("Lý do hủy không được để trống");
        }
        if(maLichHen == null || maLichHen.trim().isEmpty()) {
            throw new NghiepVuException("Mã lịch hẹn không được để trống");
        }
        lhDAO.huyLichHen(maLichHen, lyDoHuy);
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
    public void capNhatLichHen(LocalTime ngayHen, LocalTime gioHen, String ghiChu, String maLichHen) {
        if(ngayHen == null) {
            throw new NghiepVuException("Ngày hẹn không được để trống");
        }
        if(gioHen == null) {
            throw new NghiepVuException("Giờ hẹn không được để trống");
        }
        if(maLichHen == null || maLichHen.trim().isEmpty()) {
            throw new NghiepVuException("Mã lịch hẹn không được để trống");
        }
        lhDAO.capNhatLichHen(ngayHen, gioHen, ghiChu, maLichHen);
    }

}
