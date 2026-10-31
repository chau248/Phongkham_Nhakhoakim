package nhakhoakim.dao;
import nhakhoakim.entity.HoaDon;
import nhakhoakim.entity.LuotKham;
import java.util.List;
public class HoaDonService {
    private HoaDonDAO hdDAO;
    public HoaDonService() {
        this.hdDAO = new HoaDonDAO();
    }
    public List<HoaDon> hienDanhSachHoaDon() {
        return hdDAO.layDanhSachHoaDon();
    }
    public HoaDon timHoaDon(String maHoaDon) {
        if (maHoaDon == null || maHoaDon.trim().isEmpty()) {
            throw new NghiepVuException("Mã hóa đơn không được để trống");
        }
        return hdDAO.timHoaDon(maHoaDon);
    }
    public HoaDon xacDinhThanhToan(String maHoaDon, String hinhThucThanhToan, String ghiChu, String maNhanVien, java.math.BigDecimal tongTien, java.math.BigDecimal tienGiamGia, java.math.BigDecimal tongTienTruocThue, java.math.BigDecimal phanTramGiamGia, String trangThai) {
        if (maHoaDon == null || maHoaDon.trim().isEmpty()) {
            throw new NghiepVuException("Mã hóa đơn không được để trống");
        }
        if (hinhThucThanhToan == null || hinhThucThanhToan.trim().isEmpty()) {
            throw new NghiepVuException("Hình thức thanh toán không được để trống");
        }
        if (maNhanVien == null || maNhanVien.trim().isEmpty()) {
            throw new NghiepVuException("Mã nhân viên không được để trống");
        }
        if (tongTien == null || tongTien.compareTo(java.math.BigDecimal.ZERO) < 0) {
            throw new NghiepVuException("Tổng tiền không hợp lệ");
        }
        if (tienGiamGia == null || tienGiamGia.compareTo(java.math.BigDecimal.ZERO) < 0) {
            throw new NghiepVuException("Tiền giảm giá không hợp lệ");
        }
        if (tongTienTruocThue == null || tongTienTruocThue.compareTo(java.math.BigDecimal.ZERO) < 0) {
            throw new NghiepVuException("Tổng tiền trước thuế không hợp lệ");
        }
        if (phanTramGiamGia == null || phanTramGiamGia.compareTo(java.math.BigDecimal.ZERO) < 0) {
            throw new NghiepVuException("Phần trăm giảm giá không hợp lệ");
        }
        if (trangThai == null || trangThai.trim().isEmpty()) {
            throw new NghiepVuException("Trạng thái không được để trống");
        }
        return hdDAO.xacDinhThanhToan(maHoaDon, hinhThucThanhToan, ghiChu, maNhanVien, tongTien, tienGiamGia, tongTienTruocThue, phanTramGiamGia, trangThai);
    }
    public HoaDon taoHoaDon(LuotKham lk) {
        if (lk.getTrangThai()=="Hoàn thành") {
            return hdDAO.taoHoaDon(lk);
        } else {
            throw new NghiepVuException("Chỉ có thể tạo hóa đơn cho lượt khám đã hoàn thành");
        }
    }
}
