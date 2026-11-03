package nhakhoakim.entity;

import java.time.LocalDate;
import java.util.ArrayList;

// MaBN : String


public class BenhNhan {

    private String maBN; // Mã bệnh nhân, dạng BN000001
    private String hoTen; // Họ tên
    private LocalDate ngaySinh; // Ngày sinh
    private String gioiTinh; // Nam, Nữ hoặc Khác
    private String sdt; // Số điện thoại
    private String diaChi; // Địa chỉ
    private String email; // Email
    private String soCCCD; // Số căn cước công dân
    private String nguoiLienHeKhanCap; // Người liên hệ khẩn cấp
    private String sdtNguoiLienHe; // Số điện thoại người liên hệ
    private LocalDate ngayDangKy; // Ngày đăng ký
    private String trangThai; // Đang điều trị hoặc Ngừng
    private String ghiChu; // Ghi chú
    

    public BenhNhan() {
    }

    public BenhNhan(String maBN, String hoTen, LocalDate ngaySinh, String gioiTinh, String sdt, String diaChi,
			String email, String soCCCD, String nguoiLienHeKhanCap, String sdtNguoiLienHe, LocalDate ngayDangKy,
			String trangThai, String ghiChu) {
		super();
		this.maBN = maBN;
		this.hoTen = hoTen;
		this.ngaySinh = ngaySinh;
		this.gioiTinh = gioiTinh;
		this.sdt = sdt;
		this.diaChi = diaChi;
		this.email = email;
		this.soCCCD = soCCCD;
		this.nguoiLienHeKhanCap = nguoiLienHeKhanCap;
		this.sdtNguoiLienHe = sdtNguoiLienHe;
		this.ngayDangKy = ngayDangKy;
		this.trangThai = trangThai;
		this.ghiChu = ghiChu;
	}

	public String getMaBN() {
        return maBN;
    }

    public void setMaBN(String maBN) {
        this.maBN = maBN;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public LocalDate getNgaySinh() {
        return ngaySinh;
    }

    public void setNgaySinh(LocalDate ngaySinh) {
        this.ngaySinh = ngaySinh;
    }

    public String getGioiTinh() {
        return gioiTinh;
    }

    public void setGioiTinh(String gioiTinh) {
        this.gioiTinh = gioiTinh;
    }

    public String getSdt() {
        return sdt;
    }

    public void setSdt(String sdt) {
        this.sdt = sdt;
    }

    public String getDiaChi() {
        return diaChi;
    }

    public void setDiaChi(String diaChi) {
        this.diaChi = diaChi;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSoCCCD() {
        return soCCCD;
    }

    public void setSoCCCD(String soCCCD) {
        this.soCCCD = soCCCD;
    }

    public String getNguoiLienHeKhanCap() {
        return nguoiLienHeKhanCap;
    }

    public void setNguoiLienHeKhanCap(String nguoiLienHeKhanCap) {
        this.nguoiLienHeKhanCap = nguoiLienHeKhanCap;
    }

    public String getSdtNguoiLienHe() {
        return sdtNguoiLienHe;
    }

    public void setSdtNguoiLienHe(String sdtNguoiLienHe) {
        this.sdtNguoiLienHe = sdtNguoiLienHe;
    }

    public LocalDate getNgayDangKy() {
        return ngayDangKy;
    }

    public void setNgayDangKy(LocalDate ngayDangKy) {
        this.ngayDangKy = ngayDangKy;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

    // Tuổi của bệnh nhân tại ngày hôm nay
    public int tinhTuoi() {
        if (ngaySinh == null) {
            return -1;
        }
        return java.time.Period.between(ngaySinh, LocalDate.now()).getYears();
    }

}