package nhakhoakim.entity;

import java.time.LocalDate;
import java.util.ArrayList;


//- maNV : String   - hoTen : String   - vaiTro : String   - ngaySinh : LocalDate   - gioiTinh : String   - sdt : String   - email : String   - diaChi : String   - ngayVaoLam : LocalDate   - chuyenKhoa : String (// Áp dụng cho nha sĩ, nội nha...)   - trangThai : String   - soChungChiHanhNghe : String   - ngayHetHanChungChi : LocalDate   - danhSachLuotKham : LuotKham   - danhSachLichHen : LichHen

public class NhanVien {
	
	private String maNV;
	private String hoTen;
	private String vaiTro;
	private LocalDate ngaySinh;
	private String gioiTinh;
	private String sdt;
	private String email;
	private String diaChi;
	private LocalDate ngayVaoLam;
	private String chuyenKhoa;
	private String trangThai;
	private String soChungChiHanhNghe;
	
	private LocalDate ngayHetHanChungChi;
	
//	danhSachLuotKham : LuotKham   - danhSachLichHen : LichHen
	private ArrayList<LuotKham> danhSachLuotKham;
	
	
	private ArrayList<LichHen> danhSachLichHen;
	
	private ArrayList<HoSoBenhAn> danhSachHoSoBenhAn;


	


	public NhanVien(String maNV, String hoTen, String vaiTro, LocalDate ngaySinh, String gioiTinh, String sdt,
			String email, String diaChi, LocalDate ngayVaoLam, String chuyenKhoa, String trangThai,
			String soChungChiHanhNghe, LocalDate ngayHetHanChungChi) {
		super();
		this.maNV = maNV;
		this.hoTen = hoTen;
		this.vaiTro = vaiTro;
		this.ngaySinh = ngaySinh;
		this.gioiTinh = gioiTinh;
		this.sdt = sdt;
		this.email = email;
		this.diaChi = diaChi;
		this.ngayVaoLam = ngayVaoLam;
		this.chuyenKhoa = chuyenKhoa;
		this.trangThai = trangThai;
		this.soChungChiHanhNghe = soChungChiHanhNghe;
		this.ngayHetHanChungChi = ngayHetHanChungChi;
	}


	public String getMaNV() {
		return maNV;
	}


	public void setMaNV(String maNV) {
		this.maNV = maNV;
	}


	public String getHoTen() {
		return hoTen;
	}


	public void setHoTen(String hoTen) {
		this.hoTen = hoTen;
	}


	public String getVaiTro() {
		return vaiTro;
	}


	public void setVaiTro(String vaiTro) {
		this.vaiTro = vaiTro;
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


	public String getEmail() {
		return email;
	}


	public void setEmail(String email) {
		this.email = email;
	}


	public String getDiaChi() {
		return diaChi;
	}


	public void setDiaChi(String diaChi) {
		this.diaChi = diaChi;
	}


	public LocalDate getNgayVaoLam() {
		return ngayVaoLam;
	}


	public void setNgayVaoLam(LocalDate ngayVaoLam) {
		this.ngayVaoLam = ngayVaoLam;
	}


	public String getChuyenKhoa() {
		return chuyenKhoa;
	}


	public void setChuyenKhoa(String chuyenKhoa) {
		this.chuyenKhoa = chuyenKhoa;
	}


	public String getTrangThai() {
		return trangThai;
	}


	public void setTrangThai(String trangThai) {
		this.trangThai = trangThai;
	}


	public String getSoChungChiHanhNghe() {
		return soChungChiHanhNghe;
	}


	public void setSoChungChiHanhNghe(String soChungChiHanhNghe) {
		this.soChungChiHanhNghe = soChungChiHanhNghe;
	}


	public LocalDate getNgayHetHanChungChi() {
		return ngayHetHanChungChi;
	}


	public void setNgayHetHanChungChi(LocalDate ngayHetHanChungChi) {
		this.ngayHetHanChungChi = ngayHetHanChungChi;
	}


	public ArrayList<LuotKham> getDanhSachLuotKham() {
		return danhSachLuotKham;
	}


	public void setDanhSachLuotKham(ArrayList<LuotKham> danhSachLuotKham) {
		this.danhSachLuotKham = danhSachLuotKham;
	}


	public ArrayList<LichHen> getDanhSachLichHen() {
		return danhSachLichHen;
	}


	public void setDanhSachLichHen(ArrayList<LichHen> danhSachLichHen) {
		this.danhSachLichHen = danhSachLichHen;
	}
	
	public ArrayList<HoSoBenhAn> getDanhSachHoSoBenhAn() {
		return danhSachHoSoBenhAn;
	}


	public void setDanhSachHoSoBenhAn(ArrayList<HoSoBenhAn> danhSachHoSoBenhAn) {
		this.danhSachHoSoBenhAn = danhSachHoSoBenhAn;
	}
	
	


}