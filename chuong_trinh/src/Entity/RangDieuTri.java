package Entity;

//- maBanGhi : String
//
//- soFDI : int
//
//- trangThai : String
//
//- vatLieu : String
//
//- matRang : String (Mặt răng (gần, xa, nhai, ngoài, trong))
//
//- ngayGhiNhan : LocalDateTime
//
//- maDichVu : String (Dịch vụ áp dụng cho răng này)
//
//- daNho : boolean
//
//- mauHienThi : String (Màu/ký hiệu hiển thị trên sơ đồ răng)
//
//- ghiChu : String

public class RangDieuTri {
	
	private String maBanGhi;
	private int soFDI;
	private String trangThai;
	private String vatLieu;
	private String matRang;
	private String ngayGhiNhan;
	private String maDichVu;
	private boolean daNho;
	private String mauHienThi;
	private String ghiChu;
	
	
	public RangDieuTri(String maBanGhi, int soFDI, String trangThai, String vatLieu, String matRang, String ngayGhiNhan,
			String maDichVu, boolean daNho, String mauHienThi, String ghiChu) {
		
		this.maBanGhi = maBanGhi;
		this.soFDI = soFDI;
		this.trangThai = trangThai;
		this.vatLieu = vatLieu;
		this.matRang = matRang;
		this.ngayGhiNhan = ngayGhiNhan;
		this.maDichVu = maDichVu;
		this.daNho = daNho;
		this.mauHienThi = mauHienThi;
		this.ghiChu = ghiChu;
	}


	public String getMaBanGhi() {
		return maBanGhi;
	}


	public void setMaBanGhi(String maBanGhi) {
		this.maBanGhi = maBanGhi;
	}


	public int getSoFDI() {
		return soFDI;
	}


	public void setSoFDI(int soFDI) {
		this.soFDI = soFDI;
	}


	public String getTrangThai() {
		return trangThai;
	}


	public void setTrangThai(String trangThai) {
		this.trangThai = trangThai;
	}


	public String getVatLieu() {
		return vatLieu;
	}


	public void setVatLieu(String vatLieu) {
		this.vatLieu = vatLieu;
	}


	public String getMatRang() {
		return matRang;
	}


	public void setMatRang(String matRang) {
		this.matRang = matRang;
	}


	public String getNgayGhiNhan() {
		return ngayGhiNhan;
	}


	public void setNgayGhiNhan(String ngayGhiNhan) {
		this.ngayGhiNhan = ngayGhiNhan;
	}


	public String getMaDichVu() {
		return maDichVu;
	}


	public void setMaDichVu(String maDichVu) {
		this.maDichVu = maDichVu;
	}


	public boolean isDaNho() {
		return daNho;
	}


	public void setDaNho(boolean daNho) {
		this.daNho = daNho;
	}


	public String getMauHienThi() {
		return mauHienThi;
	}


	public void setMauHienThi(String mauHienThi) {
		this.mauHienThi = mauHienThi;
	}


	public String getGhiChu() {
		return ghiChu;
	}


	public void setGhiChu(String ghiChu) {
		this.ghiChu = ghiChu;
	}
	
	
	
	

}
