package Entity;

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
	private String maBN;
	private String hoTen;
	private LocalDate ngaySinh;
	private String sdt;
	
	private int namSinh;
	
	private String gioiTinh;
	
	private String diaChi;
	
	private String email;
	
	private String soCCCD;
	
	private String nguoiLienHeKhanCap;
	
	private String sdtNguoiLienHe;
	
	private LocalDate ngayDangKy;
	
	private String trangThai;
	
	private String ghiChu;

	
	
	private HoSoBenhAn hoSoBenhAn;
	
	private LieuTrinh danhSachLieuTrinh;
	
	private LichHen danhSachLichHen;
	
	
	private ArrayList<HoaDon> danhSachHoaDon;
	private ArrayList<NhanVien> danhSachNhaSi;
	
	
	
	public BenhNhan(String maBN, String hoTen, LocalDate ngaySinh, String sdt, int namSinh, String gioiTinh,
			String diaChi, String email, String soCCCD, String nguoiLienHeKhanCap, String sdtNguoiLienHe,
			LocalDate ngayDangKy, String trangThai, String ghiChu, HoSoBenhAn hoSoBenhAn, LieuTrinh danhSachLieuTrinh,
			LichHen danhSachLichHen, ArrayList<HoaDon> danhSachHoaDon, ArrayList<NhanVien> danhSachNhaSi) {
		super();
		this.maBN = maBN;
		this.hoTen = hoTen;
		this.ngaySinh = ngaySinh;
		this.sdt = sdt;
		this.namSinh = namSinh;
		this.gioiTinh = gioiTinh;
		this.diaChi = diaChi;
		this.email = email;
		this.soCCCD = soCCCD;
		this.nguoiLienHeKhanCap = nguoiLienHeKhanCap;
		this.sdtNguoiLienHe = sdtNguoiLienHe;
		this.ngayDangKy = ngayDangKy;
		this.trangThai = trangThai;
		this.ghiChu = ghiChu;
		this.hoSoBenhAn = hoSoBenhAn;
		this.danhSachLieuTrinh = danhSachLieuTrinh;
		this.danhSachLichHen = danhSachLichHen;
		this.danhSachHoaDon = danhSachHoaDon;
		this.danhSachNhaSi = danhSachNhaSi;
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



	public String getSdt() {
		return sdt;
	}



	public void setSdt(String sdt) {
		this.sdt = sdt;
	}



	public int getNamSinh() {
		return namSinh;
	}



	public void setNamSinh(int namSinh) {
		this.namSinh = namSinh;
	}



	public String getGioiTinh() {
		return gioiTinh;
	}



	public void setGioiTinh(String gioiTinh) {
		this.gioiTinh = gioiTinh;
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



	public HoSoBenhAn getHoSoBenhAn() {
		return hoSoBenhAn;
	}



	public void setHoSoBenhAn(HoSoBenhAn hoSoBenhAn) {
		this.hoSoBenhAn = hoSoBenhAn;
	}



	public LieuTrinh getDanhSachLieuTrinh() {
		return danhSachLieuTrinh;
	}



	public void setDanhSachLieuTrinh(LieuTrinh danhSachLieuTrinh) {
		this.danhSachLieuTrinh = danhSachLieuTrinh;
	}



	public LichHen getDanhSachLichHen() {
		return danhSachLichHen;
	}



	public void setDanhSachLichHen(LichHen danhSachLichHen) {
		this.danhSachLichHen = danhSachLichHen;
	}



	public ArrayList<HoaDon> getDanhSachHoaDon() {
		return danhSachHoaDon;
	}



	public void setDanhSachHoaDon(ArrayList<HoaDon> danhSachHoaDon) {
		this.danhSachHoaDon = danhSachHoaDon;
	}



	public ArrayList<NhanVien> getDanhSachNhaSi() {
		return danhSachNhaSi;
	}



	public void setDanhSachNhaSi(ArrayList<NhanVien> danhSachNhaSi) {
		this.danhSachNhaSi = danhSachNhaSi;
	}
	
	
	
	
	
	
	

}
