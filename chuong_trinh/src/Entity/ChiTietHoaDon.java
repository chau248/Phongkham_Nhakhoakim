package Entity;

//maHoaDon : HoaDon 
//maBenhNhan : BenhNhan 
//soFDIRang : int (// Răng được áp dụng dịch vụ (nếu có)) 
//giamGia : Double (// Giảm giá riêng trên dòng này) 
//ghiChu : String 
//soLuong : int 
//donGia : Double 
//dichVu : DichVu




public class ChiTietHoaDon {
	private HoaDon maHoaDon;
	private BenhNhan maBenhNhan;
	private int soFDIRang;
	private double giamGia;
	private String ghiChu;
	private int soLuong;
	private double donGia;
	
	private DichVu dichVu;

	public ChiTietHoaDon(HoaDon maHoaDon, BenhNhan maBenhNhan, int soFDIRang, double giamGia, String ghiChu,
			int soLuong, double donGia, DichVu dichVu) {
		super();
		this.maHoaDon = maHoaDon;
		this.maBenhNhan = maBenhNhan;
		this.soFDIRang = soFDIRang;
		this.giamGia = giamGia;
		this.ghiChu = ghiChu;
		this.soLuong = soLuong;
		this.donGia = donGia;
		this.dichVu = dichVu;
	}

	public HoaDon getMaHoaDon() {
		return maHoaDon;
	}

	public void setMaHoaDon(HoaDon maHoaDon) {
		this.maHoaDon = maHoaDon;
	}

	public BenhNhan getMaBenhNhan() {
		return maBenhNhan;
	}

	public void setMaBenhNhan(BenhNhan maBenhNhan) {
		this.maBenhNhan = maBenhNhan;
	}

	public int getSoFDIRang() {
		return soFDIRang;
	}

	public void setSoFDIRang(int soFDIRang) {
		this.soFDIRang = soFDIRang;
	}

	public double getGiamGia() {
		return giamGia;
	}

	public void setGiamGia(double giamGia) {
		this.giamGia = giamGia;
	}

	public String getGhiChu() {
		return ghiChu;
	}

	public void setGhiChu(String ghiChu) {
		this.ghiChu = ghiChu;
	}

	public int getSoLuong() {
		return soLuong;
	}

	public void setSoLuong(int soLuong) {
		this.soLuong = soLuong;
	}

	public double getDonGia() {
		return donGia;
	}

	public void setDonGia(double donGia) {
		this.donGia = donGia;
	}

	public DichVu getDichVu() {
		return dichVu;
	}

	public void setDichVu(DichVu dichVu) {
		this.dichVu = dichVu;
	}
	
	
	
	
	

}
