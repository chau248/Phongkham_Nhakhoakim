package nhakhoakim.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

//maLichHen : String 
//ngayHen : LocalDate 
//gioHen : LocalTime 
//trangThai : String 
//dichVu : DichVu 
//ngayTao : LocalDateTime 
//thoiLuongDuKien : int (// Phút) 
//hinhThucDat : String (// Trực tiếp / Điện thoại / Online) 
//lyDoHuy : String 
//daNhacHen : boolean 
//ghiChu : String 
//
//

public class LichHen {
	
	private String maLichHen;
	
	private LocalDate ngayHen;
	
	private LocalTime gioHen;
	
	private String trangThai;
	
	private DichVu dichVu;
	
	private LocalDateTime ngayTao;
	
	private int thoiLuongDuKien;
	
	private String hinhThucDat;
	
	private String lyDoHuy;
	
	private boolean daNhacHen;
	
	private String ghiChu;

	public LichHen(String maLichHen, LocalDate ngayHen, LocalTime gioHen, String trangThai, DichVu dichVu,
			LocalDateTime ngayTao, int thoiLuongDuKien, String hinhThucDat, String lyDoHuy, boolean daNhacHen,
			String ghiChu) {
		super();
		this.maLichHen = maLichHen;
		this.ngayHen = ngayHen;
		this.gioHen = gioHen;
		this.trangThai = trangThai;
		this.dichVu = dichVu;
		this.ngayTao = ngayTao;
		this.thoiLuongDuKien = thoiLuongDuKien;
		this.hinhThucDat = hinhThucDat;
		this.lyDoHuy = lyDoHuy;
		this.daNhacHen = daNhacHen;
		this.ghiChu = ghiChu;
	}

	public String getMaLichHen() {
		return maLichHen;
	}

	public void setMaLichHen(String maLichHen) {
		this.maLichHen = maLichHen;
	}

	public LocalDate getNgayHen() {
		return ngayHen;
	}

	public void setNgayHen(LocalDate ngayHen) {
		this.ngayHen = ngayHen;
	}

	public LocalTime getGioHen() {
		return gioHen;
	}

	public void setGioHen(LocalTime gioHen) {
		this.gioHen = gioHen;
	}

	public String getTrangThai() {
		return trangThai;
	}

	public void setTrangThai(String trangThai) {
		this.trangThai = trangThai;
	}

	public DichVu getDichVu() {
		return dichVu;
	}

	public void setDichVu(DichVu dichVu) {
		this.dichVu = dichVu;
	}

	public LocalDateTime getNgayTao() {
		return ngayTao;
	}

	public void setNgayTao(LocalDateTime ngayTao) {
		this.ngayTao = ngayTao;
	}

	public int getThoiLuongDuKien() {
		return thoiLuongDuKien;
	}

	public void setThoiLuongDuKien(int thoiLuongDuKien) {
		this.thoiLuongDuKien = thoiLuongDuKien;
	}

	public String getHinhThucDat() {
		return hinhThucDat;
	}

	public void setHinhThucDat(String hinhThucDat) {
		this.hinhThucDat = hinhThucDat;
	}

	public String getLyDoHuy() {
		return lyDoHuy;
	}

	public void setLyDoHuy(String lyDoHuy) {
		this.lyDoHuy = lyDoHuy;
	}

	public boolean isDaNhacHen() {
		return daNhacHen;
	}

	public void setDaNhacHen(boolean daNhacHen) {
		this.daNhacHen = daNhacHen;
	}

	public String getGhiChu() {
		return ghiChu;
	}

	public void setGhiChu(String ghiChu) {
		this.ghiChu = ghiChu;
	}
	
	
	
	
	
	
	

}
