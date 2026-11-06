package entity;

import java.math.BigDecimal;

// MaDichVu : String

public class DichVu {

    private String maDichVu; // Mã dịch vụ, dạng DV000001
    private String tenDichVu; // Tên dịch vụ
    private long gia; // Giá niêm yết
    private int thoiGianDuKien; // Thời gian dự kiến
    private String loaiDichVu; // Loại dịch vụ
    private boolean trangThaiHoatDong; // True nếu đang cung cấp
    private boolean yeuCauLieuTrinh; // True nếu cần theo liệu trình
    private boolean lienQuanImplantCauRang; // True nếu là dịch vụ Implant
    private String moTa; // Mô tả ngắn
    private String donViTinh; // Lần, Răng hoặc Hàm
    
    public DichVu() {
    }

    public DichVu(String maDichVu, String tenDichVu, long gia, int thoiGianDuKien, String loaiDichVu,
			boolean trangThaiHoatDong, boolean yeuCauLieuTrinh, boolean lienQuanImplantCauRang) {
		super();
		this.maDichVu = maDichVu;
		this.tenDichVu = tenDichVu;
		this.gia = gia;
		this.thoiGianDuKien = thoiGianDuKien;
		this.loaiDichVu = loaiDichVu;
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

    public long getGia() {
        return gia;
    }

    public void setGia(long gia) {
        this.gia = gia;
    }

    public int getThoiGianDuKien() {
        return thoiGianDuKien;
    }

    public void setThoiGianDuKien(int thoiGianDuKien) {
        this.thoiGianDuKien = thoiGianDuKien;
    }

    public String getLoaiDichVu() {
        return loaiDichVu;
    }

    public void setLoaiDichVu(String loaiDichVu) {
        this.loaiDichVu = loaiDichVu;
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

}
