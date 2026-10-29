package Entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

//- maLuotKham : String   - gioBatDau : LocalDateTime   - gioKetThuc : LocalDateTime   - lyDoKham : String   - huongDieuTri : String   - donThuoc : String   - ngayTaiKham : LocalDate   - trangThai : String   - chanDoan : String   - ghiChu : String   - danhSachRang : RangDieuTri   - hoaDon : HoaDon   - nhanVienPhuTrach : NhanVien   
public class LuotKham {
	
	private String maLuotKham;
	private LocalDateTime gioBatDau;
	private LocalDateTime gioKetThuc;
	
	private String lyDoKham;
	private String huongDieuTri;
	private String donThuoc;
	private LocalDate ngayTaiKham;
	private String trangthai;
	private String chanDoan;
	private String ghiChu;
	
//	danhSachRang : RangDieuTri   - hoaDon : HoaDon   - nhanVienPhuTrach : NhanVien 
	
	private ArrayList<RangDieuTri> danhSachRang;
	
	private HoaDon hoaDon;
	
	private NhanVien nhanVienPhuTrach;

	public LuotKham(String maLuotKham, LocalDateTime gioBatDau, LocalDateTime gioKetThuc, String lyDoKham,
			String huongDieuTri, String donThuoc, LocalDate ngayTaiKham, String trangthai, String chanDoan,
			String ghiChu, ArrayList<RangDieuTri> danhSachRang, HoaDon hoaDon, NhanVien nhanVienPhuTrach) {
		super();
		this.maLuotKham = maLuotKham;
		this.gioBatDau = gioBatDau;
		this.gioKetThuc = gioKetThuc;
		this.lyDoKham = lyDoKham;
		this.huongDieuTri = huongDieuTri;
		this.donThuoc = donThuoc;
		this.ngayTaiKham = ngayTaiKham;
		this.trangthai = trangthai;
		this.chanDoan = chanDoan;
		this.ghiChu = ghiChu;
		this.danhSachRang = danhSachRang;
		this.hoaDon = hoaDon;
		this.nhanVienPhuTrach = nhanVienPhuTrach;
	}

	public String getMaLuotKham() {
		return maLuotKham;
	}

	public void setMaLuotKham(String maLuotKham) {
		this.maLuotKham = maLuotKham;
	}

	public LocalDateTime getGioBatDau() {
		return gioBatDau;
	}

	public void setGioBatDau(LocalDateTime gioBatDau) {
		this.gioBatDau = gioBatDau;
	}

	public LocalDateTime getGioKetThuc() {
		return gioKetThuc;
	}

	public void setGioKetThuc(LocalDateTime gioKetThuc) {
		this.gioKetThuc = gioKetThuc;
	}

	public String getLyDoKham() {
		return lyDoKham;
	}

	public void setLyDoKham(String lyDoKham) {
		this.lyDoKham = lyDoKham;
	}

	public String getHuongDieuTri() {
		return huongDieuTri;
	}

	public void setHuongDieuTri(String huongDieuTri) {
		this.huongDieuTri = huongDieuTri;
	}

	public String getDonThuoc() {
		return donThuoc;
	}

	public void setDonThuoc(String donThuoc) {
		this.donThuoc = donThuoc;
	}

	public LocalDate getNgayTaiKham() {
		return ngayTaiKham;
	}

	public void setNgayTaiKham(LocalDate ngayTaiKham) {
		this.ngayTaiKham = ngayTaiKham;
	}

	public String getTrangthai() {
		return trangthai;
	}

	public void setTrangthai(String trangthai) {
		this.trangthai = trangthai;
	}

	public String getChanDoan() {
		return chanDoan;
	}

	public void setChanDoan(String chanDoan) {
		this.chanDoan = chanDoan;
	}

	public String getGhiChu() {
		return ghiChu;
	}

	public void setGhiChu(String ghiChu) {
		this.ghiChu = ghiChu;
	}

	public ArrayList<RangDieuTri> getDanhSachRang() {
		return danhSachRang;
	}

	public void setDanhSachRang(ArrayList<RangDieuTri> danhSachRang) {
		this.danhSachRang = danhSachRang;
	}

	public HoaDon getHoaDon() {
		return hoaDon;
	}

	public void setHoaDon(HoaDon hoaDon) {
		this.hoaDon = hoaDon;
	}

	public NhanVien getNhanVienPhuTrach() {
		return nhanVienPhuTrach;
	}

	public void setNhanVienPhuTrach(NhanVien nhanVienPhuTrach) {
		this.nhanVienPhuTrach = nhanVienPhuTrach;
	}
	
	
	
	
	
	
	

}
