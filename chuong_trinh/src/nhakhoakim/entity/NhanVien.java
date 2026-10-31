package nhakhoakim.entity;

import java.time.LocalDate;
import java.util.ArrayList;


//- maNV : String   - hoTen : String   - vaiTro : String   - ngaySinh : LocalDate   - gioiTinh : String   - sdt : String   - email : String   - diaChi : String   - ngayVaoLam : LocalDate   - chuyenKhoa : String (// Áp dụng cho nha sĩ, nội nha...)   - trangThai : String   - soChungChiHanhNghe : String   - ngayHetHanChungChi : LocalDate   - danhSachLuotKham : LuotKham   - danhSachLichHen : LichHen

public class NhanVien {

    private String maNV; // mã nhân viên, dạng NV000001
    private String hoTen; // họ tên
    private String vaiTro; // Nha sĩ, Y tá, Lễ tân hoặc Quản lý
    private String soChungChiHanhNghe; // số chứng chỉ hành nghề (nha sĩ)
    private LocalDate ngayHetHanChungChi; // ngày hết hạn chứng chỉ hành nghề
    private String chuyenKhoa; // chuyên khoa
    private String trangThai; // Đang làm việc hoặc Nghỉ việc
    
    
    
    
    
    public NhanVien() {
    }

    public NhanVien(String maNV, String hoTen, String vaiTro, String soChungChiHanhNghe, LocalDate ngayHetHanChungChi,
			String chuyenKhoa, String trangThai) {
		super();
		this.maNV = maNV;
		this.hoTen = hoTen;
		this.vaiTro = vaiTro;
		this.soChungChiHanhNghe = soChungChiHanhNghe;
		this.ngayHetHanChungChi = ngayHetHanChungChi;
		this.chuyenKhoa = chuyenKhoa;
		this.trangThai = trangThai;
	}

	public String getMaNV() {
        return maNV;
    }

    public void setMaNV(String maNV) {
        this.maNV = maNV;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public String getVaiTro() {
        return vaiTro;
    }

    public void setVaiTro(String vaiTro) {
        this.vaiTro = vaiTro;
    }

    public String getSoChungChiHanhNghe() {
        return soChungChiHanhNghe;
    }

    public void setSoChungChiHanhNghe(String soChungChiHanhNghe) {
        this.soChungChiHanhNghe = soChungChiHanhNghe;
    }

    public LocalDate getNgayHetHanChungChi() {
        return ngayHetHanChungChi;
    }

    public void setNgayHetHanChungChi(LocalDate ngayHetHanChungChi) {
        this.ngayHetHanChungChi = ngayHetHanChungChi;
    }

    public String getChuyenKhoa() {
        return chuyenKhoa;
    }

    public void setChuyenKhoa(String chuyenKhoa) {
        this.chuyenKhoa = chuyenKhoa;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    // Kiểm tra chứng chỉ hành nghề còn hạn không (nhân viên không có chứng chỉ thì coi là không còn hạn)
    public boolean chungChiConHan() {
        return ngayHetHanChungChi != null && !ngayHetHanChungChi.isBefore(LocalDate.now());
    }

}