package nhakhoakim.entity;

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

    private String maDichVu; // mã dịch vụ, dạng DV000001
    private String tenDichVu; // tên dịch vụ
    private long gia; // giá niêm yết (đồng)
    private int thoiGianDuKien; // thời gian dự kiến (phút)
    private String loaiDichVu; // loại dịch vụ
    private boolean trangThaiHoatDong; // true nếu đang cung cấp
    private boolean yeuCauLieuTrinh; // true nếu cần theo liệu trình
    private boolean lienQuanImplantCauRang; // true nếu là dịch vụ Implant hoặc cầu răng
    
    
    
    
    

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

}