package Entity;

import java.math.BigDecimal;

//maDichVu : String 
//tenDichVu : String 
// gia : BigDecimal 
// thoiGianDuKien : int 
// moTa : .. 
// donViTinh : String (// Răng / Lần / Hàm) 
//trangThaiHoatDong : boolean 
// yeuCauLieuTrinh : boolean (// Cần nhiều buổi điều trị hay không) 
//lienQuanImplantCauRang : boolean (// Dùng cho ràng buộc sơ đồ răng "đã nhổ") 

public class DichVu {
	
	private String maDichVu;
	private String tenDichVu;
	private BigDecimal gia;
	private int thoiGianDuKien;
	
	private String moTa;
	private String donViTinh;
	private boolean trangThaiHoatDong;
	private boolean yeuCauLieuTrinh;
	
	private boolean lienQuanImplantCauRang;

	public DichVu(String maDichVu, String tenDichVu, BigDecimal gia, int thoiGianDuKien, String moTa, String donViTinh,
			boolean trangThaiHoatDong, boolean yeuCauLieuTrinh, boolean lienQuanImplantCauRang) {
		super();
		this.maDichVu = maDichVu;
		this.tenDichVu = tenDichVu;
		this.gia = gia;
		this.thoiGianDuKien = thoiGianDuKien;
		this.moTa = moTa;
		this.donViTinh = donViTinh;
		this.trangThaiHoatDong = trangThaiHoatDong;
		this.yeuCauLieuTrinh = yeuCauLieuTrinh;
		this.lienQuanImplantCauRang = lienQuanImplantCauRang;
	}

	public String getMaDichVu() {
		return maDichVu;
	}

	public void setMaDichVu(String maDichVu) {
		this.maDichVu = maDichVu;
	}

	public String getTenDichVu() {
		return tenDichVu;
	}

	public void setTenDichVu(String tenDichVu) {
		this.tenDichVu = tenDichVu;
	}

	public BigDecimal getGia() {
		return gia;
	}

	public void setGia(BigDecimal gia) {
		this.gia = gia;
	}

	public int getThoiGianDuKien() {
		return thoiGianDuKien;
	}

	public void setThoiGianDuKien(int thoiGianDuKien) {
		this.thoiGianDuKien = thoiGianDuKien;
	}

	public String getMoTa() {
		return moTa;
	}

	public void setMoTa(String moTa) {
		this.moTa = moTa;
	}

	public String getDonViTinh() {
		return donViTinh;
	}

	public void setDonViTinh(String donViTinh) {
		this.donViTinh = donViTinh;
	}

	public boolean isTrangThaiHoatDong() {
		return trangThaiHoatDong;
	}

	public void setTrangThaiHoatDong(boolean trangThaiHoatDong) {
		this.trangThaiHoatDong = trangThaiHoatDong;
	}

	public boolean isYeuCauLieuTrinh() {
		return yeuCauLieuTrinh;
	}

	public void setYeuCauLieuTrinh(boolean yeuCauLieuTrinh) {
		this.yeuCauLieuTrinh = yeuCauLieuTrinh;
	}

	public boolean isLienQuanImplantCauRang() {
		return lienQuanImplantCauRang;
	}

	public void setLienQuanImplantCauRang(boolean lienQuanImplantCauRang) {
		this.lienQuanImplantCauRang = lienQuanImplantCauRang;
	}
	
	
	
	
	

}
