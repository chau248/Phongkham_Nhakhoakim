package nhakhoakim.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;


//- maLuotKham : String   - gioBatDau : LocalDateTime   - gioKetThuc : LocalDateTime   - lyDoKham : String   - huongDieuTri : String   - donThuoc : String   - ngayTaiKham : LocalDate   - trangThai : String   - chanDoan : String   - ghiChu : String   - danhSachRang : RangDieuTri   - hoaDon : HoaDon   - nhanVienPhuTrach : NhanVien   
public class LuotKham {

    private String maLuotKham; // mã lượt khám, dạng LK000001
    private String maHoSo; // mã hồ sơ bệnh án
    private String maNhanVienPhuTrach; // mã nha sĩ phụ trách
    private String maLieuTrinh; // mã liệu trình (có thể rỗng)
    private String maHoaDon; // mã hóa đơn, rỗng cho đến khi lập hóa đơn
    private String maLichHen; // mã lịch hẹn của lượt khám (rỗng nếu khám vãng lai)
    private LocalDateTime gioBatDau; // giờ bắt đầu khám
    private LocalDateTime gioKetThuc; // giờ kết thúc khám
    private String lyDoKham; // lý do khám
    private String chanDoan; // chẩn đoán
    private String huongDieuTri; // hướng điều trị
    private String donThuoc; // đơn thuốc
    private LocalDate ngayTaiKham; // ngày hẹn tái khám
    private String trangThai; // Đang khám, Hoàn thành hoặc Đã hủy
    private String ghiChu; // ghi chú
    
    
    
    
    

    public LuotKham(String maLuotKham, String maHoSo, String maNhanVienPhuTrach, String maLieuTrinh, String maHoaDon,
			String maLichHen, LocalDateTime gioBatDau, LocalDateTime gioKetThuc, String lyDoKham, String chanDoan,
			String huongDieuTri, String donThuoc, LocalDate ngayTaiKham, String trangThai, String ghiChu) {
		super();
		this.maLuotKham = maLuotKham;
		this.maHoSo = maHoSo;
		this.maNhanVienPhuTrach = maNhanVienPhuTrach;
		this.maLieuTrinh = maLieuTrinh;
		this.maHoaDon = maHoaDon;
		this.maLichHen = maLichHen;
		this.gioBatDau = gioBatDau;
		this.gioKetThuc = gioKetThuc;
		this.lyDoKham = lyDoKham;
		this.chanDoan = chanDoan;
		this.huongDieuTri = huongDieuTri;
		this.donThuoc = donThuoc;
		this.ngayTaiKham = ngayTaiKham;
		this.trangThai = trangThai;
		this.ghiChu = ghiChu;
	}

	public String getMaLuotKham() {
        return maLuotKham;
    }

    public void setMaLuotKham(String maLuotKham) {
        this.maLuotKham = maLuotKham;
    }

    public String getMaHoSo() {
        return maHoSo;
    }

    public void setMaHoSo(String maHoSo) {
        this.maHoSo = maHoSo;
    }

    public String getMaNhanVienPhuTrach() {
        return maNhanVienPhuTrach;
    }

    public void setMaNhanVienPhuTrach(String maNhanVienPhuTrach) {
        this.maNhanVienPhuTrach = maNhanVienPhuTrach;
    }

    public String getMaLieuTrinh() {
        return maLieuTrinh;
    }

    public void setMaLieuTrinh(String maLieuTrinh) {
        this.maLieuTrinh = maLieuTrinh;
    }

    public String getMaHoaDon() {
        return maHoaDon;
    }

    public void setMaHoaDon(String maHoaDon) {
        this.maHoaDon = maHoaDon;
    }

    public String getMaLichHen() {
        return maLichHen;
    }

    public void setMaLichHen(String maLichHen) {
        this.maLichHen = maLichHen;
    }

    public LocalDateTime getGioBatDau() {
        return gioBatDau;
    }

    public void setGioBatDau(LocalDateTime gioBatDau) {
        this.gioBatDau = gioBatDau;
    }

    public LocalDateTime getGioKetThuc() {
        return gioKetThuc;
    }

    public void setGioKetThuc(LocalDateTime gioKetThuc) {
        this.gioKetThuc = gioKetThuc;
    }

    public String getLyDoKham() {
        return lyDoKham;
    }

    public void setLyDoKham(String lyDoKham) {
        this.lyDoKham = lyDoKham;
    }

    public String getChanDoan() {
        return chanDoan;
    }

    public void setChanDoan(String chanDoan) {
        this.chanDoan = chanDoan;
    }

    public String getHuongDieuTri() {
        return huongDieuTri;
    }

    public void setHuongDieuTri(String huongDieuTri) {
        this.huongDieuTri = huongDieuTri;
    }

    public String getDonThuoc() {
        return donThuoc;
    }

    public void setDonThuoc(String donThuoc) {
        this.donThuoc = donThuoc;
    }

    public LocalDate getNgayTaiKham() {
        return ngayTaiKham;
    }

    public void setNgayTaiKham(LocalDate ngayTaiKham) {
        this.ngayTaiKham = ngayTaiKham;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

}