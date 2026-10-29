package Entity;

import java.time.LocalDate;
import java.util.ArrayList;

public class LieuTrinh {

	
	  private String maLieuTrinh;
	  private String loaiDichVu;
	  private ArrayList<LuotKham> danhSachBuoi;
	  private int soBuoiDuKien;
	  private String giaiDoanHienTai;
	  private LocalDate ngayBatDau;
	  private LocalDate ngayDuKienKetThuc;
	  private LocalDate ngayKetThucThucTe;
	  private Double tongChiPhiDuKien;
	  private String trangThai; // Đang thực hiện / Tạm dừng / Hoàn thành / Đã hủy)
	  private NhanVien maNhaSiPhuTrach;
	  private String ghiChu;
	  
	  
	  
	  public LieuTrinh(String maLieuTrinh, String loaiDichVu, ArrayList<LuotKham> danhSachBuoi, int soBuoiDuKien,
			String giaiDoanHienTai, LocalDate ngayBatDau, LocalDate ngayDuKienKetThuc, LocalDate ngayKetThucThucTe,
			Double tongChiPhiDuKien, String trangThai, NhanVien maNhaSiPhuTrach, String ghiChu) {
		super();
		this.maLieuTrinh = maLieuTrinh;
		this.loaiDichVu = loaiDichVu;
		this.danhSachBuoi = danhSachBuoi;
		this.soBuoiDuKien = soBuoiDuKien;
		this.giaiDoanHienTai = giaiDoanHienTai;
		this.ngayBatDau = ngayBatDau;
		this.ngayDuKienKetThuc = ngayDuKienKetThuc;
		this.ngayKetThucThucTe = ngayKetThucThucTe;
		this.tongChiPhiDuKien = tongChiPhiDuKien;
		this.trangThai = trangThai;
		this.maNhaSiPhuTrach = maNhaSiPhuTrach;
		this.ghiChu = ghiChu;
	  }



	  public String getMaLieuTrinh() {
		  return maLieuTrinh;
	  }



	  public void setMaLieuTrinh(String maLieuTrinh) {
		  this.maLieuTrinh = maLieuTrinh;
	  }



	  public String getLoaiDichVu() {
		  return loaiDichVu;
	  }



	  public void setLoaiDichVu(String loaiDichVu) {
		  this.loaiDichVu = loaiDichVu;
	  }



	  public ArrayList<LuotKham> getDanhSachBuoi() {
		  return danhSachBuoi;
	  }



	  public void setDanhSachBuoi(ArrayList<LuotKham> danhSachBuoi) {
		  this.danhSachBuoi = danhSachBuoi;
	  }



	  public int getSoBuoiDuKien() {
		  return soBuoiDuKien;
	  }



	  public void setSoBuoiDuKien(int soBuoiDuKien) {
		  this.soBuoiDuKien = soBuoiDuKien;
	  }



	  public String getGiaiDoanHienTai() {
		  return giaiDoanHienTai;
	  }



	  public void setGiaiDoanHienTai(String giaiDoanHienTai) {
		  this.giaiDoanHienTai = giaiDoanHienTai;
	  }



	  public LocalDate getNgayBatDau() {
		  return ngayBatDau;
	  }



	  public void setNgayBatDau(LocalDate ngayBatDau) {
		  this.ngayBatDau = ngayBatDau;
	  }



	  public LocalDate getNgayDuKienKetThuc() {
		  return ngayDuKienKetThuc;
	  }



	  public void setNgayDuKienKetThuc(LocalDate ngayDuKienKetThuc) {
		  this.ngayDuKienKetThuc = ngayDuKienKetThuc;
	  }



	  public LocalDate getNgayKetThucThucTe() {
		  return ngayKetThucThucTe;
	  }



	  public void setNgayKetThucThucTe(LocalDate ngayKetThucThucTe) {
		  this.ngayKetThucThucTe = ngayKetThucThucTe;
	  }



	  public Double getTongChiPhiDuKien() {
		  return tongChiPhiDuKien;
	  }



	  public void setTongChiPhiDuKien(Double tongChiPhiDuKien) {
		  this.tongChiPhiDuKien = tongChiPhiDuKien;
	  }



	  public String getTrangThai() {
		  return trangThai;
	  }



	  public void setTrangThai(String trangThai) {
		  this.trangThai = trangThai;
	  }



	  public NhanVien getMaNhaSiPhuTrach() {
		  return maNhaSiPhuTrach;
	  }



	  public void setMaNhaSiPhuTrach(NhanVien maNhaSiPhuTrach) {
		  this.maNhaSiPhuTrach = maNhaSiPhuTrach;
	  }



	  public String getGhiChu() {
		  return ghiChu;
	  }



	  public void setGhiChu(String ghiChu) {
		  this.ghiChu = ghiChu;
	  }
	  
	  
	  
	  
	  
	  


}
