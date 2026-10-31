package nhakhoakim.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class TaiKhoan {
	
	  private String maTaiKhoan;
	  private String tenDangNhap;
	  
	  private NhanVien maNV;
	  private String matKhauHash;
	  private String vaiTro;
	  private boolean trangThaiKhoa;
	  private LocalDateTime ngayTao;
	  private LocalDateTime lanDangNhapCuoi;
	  private int soLanDangNhapSai;
	  private LocalDateTime thoiGianKhoaDen;
	  private LocalDateTime ngayDoiMatKhauCuoi;
	  private boolean yeuCauDoiMatKhau;
	  public TaiKhoan(String maTaiKhoan, String tenDangNhap, NhanVien maNV, String matKhauHash, String vaiTro,
			boolean trangThaiKhoa, LocalDateTime ngayTao, LocalDateTime lanDangNhapCuoi, int soLanDangNhapSai,
			LocalDateTime thoiGianKhoaDen, LocalDateTime ngayDoiMatKhauCuoi, boolean yeuCauDoiMatKhau) {
		super();
		this.maTaiKhoan = maTaiKhoan;
		this.tenDangNhap = tenDangNhap;
		this.maNV = maNV;
		this.matKhauHash = matKhauHash;
		this.vaiTro = vaiTro;
		this.trangThaiKhoa = trangThaiKhoa;
		this.ngayTao = ngayTao;
		this.lanDangNhapCuoi = lanDangNhapCuoi;
		this.soLanDangNhapSai = soLanDangNhapSai;
		this.thoiGianKhoaDen = thoiGianKhoaDen;
		this.ngayDoiMatKhauCuoi = ngayDoiMatKhauCuoi;
		this.yeuCauDoiMatKhau = yeuCauDoiMatKhau;
	  }
	  public String getMaTaiKhoan() {
		  return maTaiKhoan;
	  }
	  public void setMaTaiKhoan(String maTaiKhoan) {
		  this.maTaiKhoan = maTaiKhoan;
	  }
	  public String getTenDangNhap() {
		  return tenDangNhap;
	  }
	  public void setTenDangNhap(String tenDangNhap) {
		  this.tenDangNhap = tenDangNhap;
	  }
	  public NhanVien getMaNV() {
		  return maNV;
	  }
	  public void setMaNV(NhanVien maNV) {
		  this.maNV = maNV;
	  }
	  public String getMatKhauHash() {
		  return matKhauHash;
	  }
	  public void setMatKhauHash(String matKhauHash) {
		  this.matKhauHash = matKhauHash;
	  }
	  public String getVaiTro() {
		  return vaiTro;
	  }
	  public void setVaiTro(String vaiTro) {
		  this.vaiTro = vaiTro;
	  }
	  public boolean isTrangThaiKhoa() {
		  return trangThaiKhoa;
	  }
	  public void setTrangThaiKhoa(boolean trangThaiKhoa) {
		  this.trangThaiKhoa = trangThaiKhoa;
	  }
	  public LocalDateTime getNgayTao() {
		  return ngayTao;
	  }
	  public void setNgayTao(LocalDateTime ngayTao) {
		  this.ngayTao = ngayTao;
	  }
	  public LocalDateTime getLanDangNhapCuoi() {
		  return lanDangNhapCuoi;
	  }
	  public void setLanDangNhapCuoi(LocalDateTime lanDangNhapCuoi) {
		  this.lanDangNhapCuoi = lanDangNhapCuoi;
	  }
	  public int getSoLanDangNhapSai() {
		  return soLanDangNhapSai;
	  }
	  public void setSoLanDangNhapSai(int soLanDangNhapSai) {
		  this.soLanDangNhapSai = soLanDangNhapSai;
	  }
	  public LocalDateTime getThoiGianKhoaDen() {
		  return thoiGianKhoaDen;
	  }
	  public void setThoiGianKhoaDen(LocalDateTime thoiGianKhoaDen) {
		  this.thoiGianKhoaDen = thoiGianKhoaDen;
	  }
	  public LocalDateTime getNgayDoiMatKhauCuoi() {
		  return ngayDoiMatKhauCuoi;
	  }
	  public void setNgayDoiMatKhauCuoi(LocalDateTime ngayDoiMatKhauCuoi) {
		  this.ngayDoiMatKhauCuoi = ngayDoiMatKhauCuoi;
	  }
	  public boolean isYeuCauDoiMatKhau() {
		  return yeuCauDoiMatKhau;
	  }
	  public void setYeuCauDoiMatKhau(boolean yeuCauDoiMatKhau) {
		  this.yeuCauDoiMatKhau = yeuCauDoiMatKhau;
	  }
	  
	  
	  
	  
	  
}