package Entity;

import java.time.LocalDate;
import java.util.ArrayList;

public class HoSoBenhAn {
	
	  private String maHoSo;
	  private String tienSuBenh;
	  private String canhBaoDiUng;
	  private ArrayList<LuotKham> danhSachLuotKham;
	  private LocalDate ngayTao;
	  private String nhomMau;
	  private String benhNen;
	  private String thuocDangSuDung;
	  private String ghiChuDacBiet;
	  
	  
	  
	  public HoSoBenhAn(String maHoSo, String tienSuBenh, String canhBaoDiUng, ArrayList<LuotKham> danhSachLuotKham,
			LocalDate ngayTao, String nhomMau, String benhNen, String thuocDangSuDung, String ghiChuDacBiet) {
		super();
		this.maHoSo = maHoSo;
		this.tienSuBenh = tienSuBenh;
		this.canhBaoDiUng = canhBaoDiUng;
		this.danhSachLuotKham = danhSachLuotKham;
		this.ngayTao = ngayTao;
		this.nhomMau = nhomMau;
		this.benhNen = benhNen;
		this.thuocDangSuDung = thuocDangSuDung;
		this.ghiChuDacBiet = ghiChuDacBiet;
	  }



	  public String getMaHoSo() {
		  return maHoSo;
	  }



	  public void setMaHoSo(String maHoSo) {
		  this.maHoSo = maHoSo;
	  }



	  public String getTienSuBenh() {
		  return tienSuBenh;
	  }



	  public void setTienSuBenh(String tienSuBenh) {
		  this.tienSuBenh = tienSuBenh;
	  }



	  public String getCanhBaoDiUng() {
		  return canhBaoDiUng;
	  }



	  public void setCanhBaoDiUng(String canhBaoDiUng) {
		  this.canhBaoDiUng = canhBaoDiUng;
	  }



	  public ArrayList<LuotKham> getDanhSachLuotKham() {
		  return danhSachLuotKham;
	  }



	  public void setDanhSachLuotKham(ArrayList<LuotKham> danhSachLuotKham) {
		  this.danhSachLuotKham = danhSachLuotKham;
	  }



	  public LocalDate getNgayTao() {
		  return ngayTao;
	  }



	  public void setNgayTao(LocalDate ngayTao) {
		  this.ngayTao = ngayTao;
	  }



	  public String getNhomMau() {
		  return nhomMau;
	  }



	  public void setNhomMau(String nhomMau) {
		  this.nhomMau = nhomMau;
	  }



	  public String getBenhNen() {
		  return benhNen;
	  }



	  public void setBenhNen(String benhNen) {
		  this.benhNen = benhNen;
	  }



	  public String getThuocDangSuDung() {
		  return thuocDangSuDung;
	  }



	  public void setThuocDangSuDung(String thuocDangSuDung) {
		  this.thuocDangSuDung = thuocDangSuDung;
	  }



	  public String getGhiChuDacBiet() {
		  return ghiChuDacBiet;
	  }



	  public void setGhiChuDacBiet(String ghiChuDacBiet) {
		  this.ghiChuDacBiet = ghiChuDacBiet;
	  }
	  
	  
	  
	  


}
