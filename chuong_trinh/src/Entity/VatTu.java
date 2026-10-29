package Entity;

import java.time.LocalDate;

public class VatTu {
	  private String maVatTu;
	  private String tenVatTu;
	  private int soLuongTon;
	  private int nguongToiThieu;
	  private String donViTinh;
	  private LocalDate hanSuDung;
	  private String nhaCungCap;
	  private Double giaNhap;
	  private String viTriLuuTru;
	  private LocalDate ngayNhapGanNhat;
	  
	  
	  
	  public VatTu(String maVatTu, String tenVatTu, int soLuongTon, int nguongToiThieu, String donViTinh,
			LocalDate hanSuDung, String nhaCungCap, Double giaNhap, String viTriLuuTru, LocalDate ngayNhapGanNhat) {
		super();
		this.maVatTu = maVatTu;
		this.tenVatTu = tenVatTu;
		this.soLuongTon = soLuongTon;
		this.nguongToiThieu = nguongToiThieu;
		this.donViTinh = donViTinh;
		this.hanSuDung = hanSuDung;
		this.nhaCungCap = nhaCungCap;
		this.giaNhap = giaNhap;
		this.viTriLuuTru = viTriLuuTru;
		this.ngayNhapGanNhat = ngayNhapGanNhat;
	  }
	  
	  
	  
	  
	  
	  public String getMaVatTu() {
		  return maVatTu;
	  }
	  public void setMaVatTu(String maVatTu) {
		  this.maVatTu = maVatTu;
	  }
	  public String getTenVatTu() {
		  return tenVatTu;
	  }
	  public void setTenVatTu(String tenVatTu) {
		  this.tenVatTu = tenVatTu;
	  }
	  public int getSoLuongTon() {
		  return soLuongTon;
	  }
	  public void setSoLuongTon(int soLuongTon) {
		  this.soLuongTon = soLuongTon;
	  }
	  public int getNguongToiThieu() {
		  return nguongToiThieu;
	  }
	  public void setNguongToiThieu(int nguongToiThieu) {
		  this.nguongToiThieu = nguongToiThieu;
	  }
	  public String getDonViTinh() {
		  return donViTinh;
	  }
	  public void setDonViTinh(String donViTinh) {
		  this.donViTinh = donViTinh;
	  }
	  public LocalDate getHanSuDung() {
		  return hanSuDung;
	  }
	  public void setHanSuDung(LocalDate hanSuDung) {
		  this.hanSuDung = hanSuDung;
	  }
	  public String getNhaCungCap() {
		  return nhaCungCap;
	  }
	  public void setNhaCungCap(String nhaCungCap) {
		  this.nhaCungCap = nhaCungCap;
	  }
	  public Double getGiaNhap() {
		  return giaNhap;
	  }
	  public void setGiaNhap(Double giaNhap) {
		  this.giaNhap = giaNhap;
	  }
	  public String getViTriLuuTru() {
		  return viTriLuuTru;
	  }
	  public void setViTriLuuTru(String viTriLuuTru) {
		  this.viTriLuuTru = viTriLuuTru;
	  }
	  public LocalDate getNgayNhapGanNhat() {
		  return ngayNhapGanNhat;
	  }
	  public void setNgayNhapGanNhat(LocalDate ngayNhapGanNhat) {
		  this.ngayNhapGanNhat = ngayNhapGanNhat;
	  }
	  
	  
	  
	  


}
