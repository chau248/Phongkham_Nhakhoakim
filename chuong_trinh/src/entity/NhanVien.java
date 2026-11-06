package entity;

import java.time.LocalDate;

// MaNV : String

public class NhanVien {

    private String maNV; // Mã nhân viên, dạng NV000001
    private String hoTen; // Họ tên
    private String vaiTro; // Nha sĩ, Y tá
    private String soChungChiHanhNghe; // Số chứng chỉ hành nghề
    private LocalDate ngayHetHanChungChi; // Ngày hết hạn chứng chỉ hành
    private String chuyenKhoa; // Chuyên khoa
    private String trangThai; // Đang làm việc hoặc Nghỉ việc
    private String sdt; // Số điện thoại
    private String gioiTinh; // Nam, Nữ hoặc Khác
    private LocalDate ngayVaoLam; // Ngày vào làm
    
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

    // Có thuộc một trong các vai trò này không
    public boolean laVaiTro(String... cacVaiTro) {
        if (HangSo.VAI_TRO_TOAN_QUYEN.equals(vaiTro)) {
            return true;
        }
        for (String vt : cacVaiTro) {
            if (vt.equals(vaiTro)) {
                return true;
            }
        }
        return false;
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

    // Kiểm tra chứng chỉ hành nghề
    public boolean chungChiConHan() {
        return ngayHetHanChungChi != null && !ngayHetHanChungChi.isBefore(LocalDate.now());
    }

    public String getSdt() {
        return sdt;
    }

    public void setSdt(String sdt) {
        this.sdt = sdt;
    }

    public String getGioiTinh() {
        return gioiTinh;
    }

    public void setGioiTinh(String gioiTinh) {
        this.gioiTinh = gioiTinh;
    }

    public LocalDate getNgayVaoLam() {
        return ngayVaoLam;
    }

    public void setNgayVaoLam(LocalDate ngayVaoLam) {
        this.ngayVaoLam = ngayVaoLam;
    }

}
