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

    private String maLichHen; // mã lịch hẹn, dạng LH000001
    private String maBN; // mã bệnh nhân
    private String maNhanVien; // mã nha sĩ
    private String maDichVu; // mã dịch vụ
    private LocalDate ngayHen; // ngày hẹn
    private LocalTime gioHen; // giờ hẹn
    private String trangThai; // Chờ xác nhận, Đã xác nhận, Hoàn thành, Không đến hoặc Đã hủy
    private String phong; // phòng khám
    private int thoiLuongDuKien; // thời lượng dự kiến (phút)
    
    
    
    

    public LichHen(String maLichHen, String maBN, String maNhanVien, String maDichVu, LocalDate ngayHen,
			LocalTime gioHen, String trangThai, String phong, int thoiLuongDuKien) {
		super();
		this.maLichHen = maLichHen;
		this.maBN = maBN;
		this.maNhanVien = maNhanVien;
		this.maDichVu = maDichVu;
		this.ngayHen = ngayHen;
		this.gioHen = gioHen;
		this.trangThai = trangThai;
		this.phong = phong;
		this.thoiLuongDuKien = thoiLuongDuKien;
	}

	public String getMaLichHen() {
        return maLichHen;
    }

    public void setMaLichHen(String maLichHen) {
        this.maLichHen = maLichHen;
    }

    public String getMaBN() {
        return maBN;
    }

    public void setMaBN(String maBN) {
        this.maBN = maBN;
    }

    public String getMaNhanVien() {
        return maNhanVien;
    }

    public void setMaNhanVien(String maNhanVien) {
        this.maNhanVien = maNhanVien;
    }

    public String getMaDichVu() {
        return maDichVu;
    }

    public void setMaDichVu(String maDichVu) {
        this.maDichVu = maDichVu;
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

    public String getPhong() {
        return phong;
    }

    public void setPhong(String phong) {
        this.phong = phong;
    }

    public int getThoiLuongDuKien() {
        return thoiLuongDuKien;
    }

    public void setThoiLuongDuKien(int thoiLuongDuKien) {
        this.thoiLuongDuKien = thoiLuongDuKien;
    }

}