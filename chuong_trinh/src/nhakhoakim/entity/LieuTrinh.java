package nhakhoakim.entity;

import java.time.LocalDate;
import java.util.ArrayList;

public class LieuTrinh {

    private String maLieuTrinh; // Mã liệu trình, dạng LT000001
    private String maBN; // Mã bệnh nhân
    private String loaiDichVu; // Loại dịch vụ của liệu trình
    private int soBuoiDuKien; // Số buổi dự kiến
    private String giaiDoanHienTai; // Giai đoạn
    private LocalDate ngayBatDau; // Ngày bắt đầu
    private LocalDate ngayDuKienKetThuc; // Ngày dự kiến kết thúc
    private LocalDate ngayKetThucThucTe; // Ngày kết thúc thực tế
    private long tongChiPhiDuKien; // Tổng chi phí dự kiến
    private String trangThai; // Đang thực
    private String maNhaSiPhuTrach; // Mã nha sĩ phụ trách
    private String ghiChu; // Ghi chú
    
    
    
    

    public LieuTrinh() {
    }

    public LieuTrinh(String maLieuTrinh, String maBN, String loaiDichVu, int soBuoiDuKien, String giaiDoanHienTai,
			LocalDate ngayBatDau, LocalDate ngayDuKienKetThuc, LocalDate ngayKetThucThucTe, long tongChiPhiDuKien,
			String trangThai, String maNhaSiPhuTrach, String ghiChu) {
		super();
		this.maLieuTrinh = maLieuTrinh;
		this.maBN = maBN;
		this.loaiDichVu = loaiDichVu;
		this.soBuoiDuKien = soBuoiDuKien;
		this.giaiDoanHienTai = giaiDoanHienTai;
		this.ngayBatDau = ngayBatDau;
		this.ngayDuKienKetThuc = ngayDuKienKetThuc;
		this.ngayKetThucThucTe = ngayKetThucThucTe;
		this.tongChiPhiDuKien = tongChiPhiDuKien;
		this.trangThai = trangThai;
		this.maNhaSiPhuTrach = maNhaSiPhuTrach;
		this.ghiChu = ghiChu;
	}

	public String getMaLieuTrinh() {
        return maLieuTrinh;
    }

    public void setMaLieuTrinh(String maLieuTrinh) {
        this.maLieuTrinh = maLieuTrinh;
    }

    public String getMaBN() {
        return maBN;
    }

    public void setMaBN(String maBN) {
        this.maBN = maBN;
    }

    public String getLoaiDichVu() {
        return loaiDichVu;
    }

    public void setLoaiDichVu(String loaiDichVu) {
        this.loaiDichVu = loaiDichVu;
    }

    public int getSoBuoiDuKien() {
        return soBuoiDuKien;
    }

    public void setSoBuoiDuKien(int soBuoiDuKien) {
        this.soBuoiDuKien = soBuoiDuKien;
    }

    public String getGiaiDoanHienTai() {
        return giaiDoanHienTai;
    }

    public void setGiaiDoanHienTai(String giaiDoanHienTai) {
        this.giaiDoanHienTai = giaiDoanHienTai;
    }

    public LocalDate getNgayBatDau() {
        return ngayBatDau;
    }

    public void setNgayBatDau(LocalDate ngayBatDau) {
        this.ngayBatDau = ngayBatDau;
    }

    public LocalDate getNgayDuKienKetThuc() {
        return ngayDuKienKetThuc;
    }

    public void setNgayDuKienKetThuc(LocalDate ngayDuKienKetThuc) {
        this.ngayDuKienKetThuc = ngayDuKienKetThuc;
    }

    public LocalDate getNgayKetThucThucTe() {
        return ngayKetThucThucTe;
    }

    public void setNgayKetThucThucTe(LocalDate ngayKetThucThucTe) {
        this.ngayKetThucThucTe = ngayKetThucThucTe;
    }

    public long getTongChiPhiDuKien() {
        return tongChiPhiDuKien;
    }

    public void setTongChiPhiDuKien(long tongChiPhiDuKien) {
        this.tongChiPhiDuKien = tongChiPhiDuKien;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public String getMaNhaSiPhuTrach() {
        return maNhaSiPhuTrach;
    }

    public void setMaNhaSiPhuTrach(String maNhaSiPhuTrach) {
        this.maNhaSiPhuTrach = maNhaSiPhuTrach;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

}