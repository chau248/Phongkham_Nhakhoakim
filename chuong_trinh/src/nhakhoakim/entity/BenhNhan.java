package nhakhoakim.entity;

import java.time.LocalDate;
import java.util.ArrayList;

//maBN : String 
//hoTen : String 
//ngaySinh : LocalDate 
//sdt : String 
//namSinh : int 
//gioiTinh : String 
//diaChi : String 
//email : String 
//soCCCD : String 
//nguoiLienHeKhanCap : String 
//sdtNguoiLienHe : String 
//ngayDangKy : LocalDate 
//trangThai : String 
//ghiChu : String 
//hoSoBenhAn : HoSoBenHAn 
//danhSachLieuTrinh : LieuTrinh 
//danhSachLichHen : LichHen 
//danhSachHoaDon : HoaDon 
//danhSachNhaSi : NhanVien 


public class BenhNhan {

    private String maBN; // mã bệnh nhân, dạng BN000001
    private String hoTen; // họ tên
    private LocalDate ngaySinh; // ngày sinh
    private String gioiTinh; // Nam, Nữ hoặc Khác
    private String sdt; // số điện thoại (10 số, bắt đầu bằng 0)
    private String diaChi; // địa chỉ
    private String email; // email
    private String soCCCD; // số căn cước công dân (12 số)
    private String nguoiLienHeKhanCap; // người liên hệ khẩn cấp
    private String sdtNguoiLienHe; // số điện thoại người liên hệ khẩn cấp
    private LocalDate ngayDangKy; // ngày đăng ký
    private String trangThai; // Đang điều trị hoặc Ngừng theo dõi
    private String ghiChu; // ghi chú
    

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