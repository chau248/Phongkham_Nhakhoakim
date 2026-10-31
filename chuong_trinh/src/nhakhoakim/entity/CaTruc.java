package nhakhoakim.entity;

import java.time.LocalDate;

public class CaTruc {

    private String maCaTruc; // mã ca trực, dạng CA000001
    private NhanVien maNV; // nhân viên trực
    private LocalDate ngay; // ngày trực
    private String caLam; // Sáng hoặc Chiều
    private String phong; // phòng khám hoặc quầy lễ tân
    private String trangThai; // Có mặt, Vắng hoặc Điều chuyển
    
    

    public CaTruc() {
    }

    public CaTruc(String maCaTruc, NhanVien maNV, LocalDate ngay, String caLam, String phong, String trangThai) {
		super();
		this.maCaTruc = maCaTruc;
		this.maNV = maNV;
		this.ngay = ngay;
		this.caLam = caLam;
		this.phong = phong;
		this.trangThai = trangThai;
	}



	public String getMaCaTruc() {
		return maCaTruc;
	}



	public void setMaCaTruc(String maCaTruc) {
		this.maCaTruc = maCaTruc;
	}



	public NhanVien getMaNV() {
		return maNV;
	}



	public void setMaNV(NhanVien maNV) {
		this.maNV = maNV;
	}



	public LocalDate getNgay() {
		return ngay;
	}



	public void setNgay(LocalDate ngay) {
		this.ngay = ngay;
	}



	public String getCaLam() {
		return caLam;
	}



	public void setCaLam(String caLam) {
		this.caLam = caLam;
	}



	public String getPhong() {
		return phong;
	}



	public void setPhong(String phong) {
		this.phong = phong;
	}



	public String getTrangThai() {
		return trangThai;
	}



	public void setTrangThai(String trangThai) {
		this.trangThai = trangThai;
	}

	
}