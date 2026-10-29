package Entity;

import java.time.LocalDateTime;

//- maHoaDon : String
//
//- ngayLap : LocalDateTime
//
//- maBenhNhan : BenhNhan
//
//- trangThaiThanhToan : String (// Chưa thanh toán / Đã thanh toán / Đã hủy)
//
//- ghiChu : String
//
//- hinhThucThanhToan : String
//
//- chiTiet : ChiTietHoaDon
//
//- maNhanVien : NhanVien

public class HoaDon {
	
	private String maHoaDon;
	private LocalDateTime ngayLap;
	private BenhNhan maBenhNhan;
	
	private String trangThaiThanhToan;
	private String ghiChu;
	private String hinhThucThanhToan;
	private ChiTietHoaDon chitiet;
	
	private NhanVien maNhanVien;

	public HoaDon(String maHoaDon, LocalDateTime ngayLap, BenhNhan maBenhNhan, String trangThaiThanhToan, String ghiChu,
			String hinhThucThanhToan, ChiTietHoaDon chitiet, NhanVien maNhanVien) {
		super();
		this.maHoaDon = maHoaDon;
		this.ngayLap = ngayLap;
		this.maBenhNhan = maBenhNhan;
		this.trangThaiThanhToan = trangThaiThanhToan;
		this.ghiChu = ghiChu;
		this.hinhThucThanhToan = hinhThucThanhToan;
		this.chitiet = chitiet;
		this.maNhanVien = maNhanVien;
	}

	public String getMaHoaDon() {
		return maHoaDon;
	}

	public void setMaHoaDon(String maHoaDon) {
		this.maHoaDon = maHoaDon;
	}

	public LocalDateTime getNgayLap() {
		return ngayLap;
	}

	public void setNgayLap(LocalDateTime ngayLap) {
		this.ngayLap = ngayLap;
	}

	public BenhNhan getMaBenhNhan() {
		return maBenhNhan;
	}

	public void setMaBenhNhan(BenhNhan maBenhNhan) {
		this.maBenhNhan = maBenhNhan;
	}

	public String getTrangThaiThanhToan() {
		return trangThaiThanhToan;
	}

	public void setTrangThaiThanhToan(String trangThaiThanhToan) {
		this.trangThaiThanhToan = trangThaiThanhToan;
	}

	public String getGhiChu() {
		return ghiChu;
	}

	public void setGhiChu(String ghiChu) {
		this.ghiChu = ghiChu;
	}

	public String getHinhThucThanhToan() {
		return hinhThucThanhToan;
	}

	public void setHinhThucThanhToan(String hinhThucThanhToan) {
		this.hinhThucThanhToan = hinhThucThanhToan;
	}

	public ChiTietHoaDon getChitiet() {
		return chitiet;
	}

	public void setChitiet(ChiTietHoaDon chitiet) {
		this.chitiet = chitiet;
	}

	public NhanVien getMaNhanVien() {
		return maNhanVien;
	}

	public void setMaNhanVien(NhanVien maNhanVien) {
		this.maNhanVien = maNhanVien;
	}
	
	
	
	
	

}
