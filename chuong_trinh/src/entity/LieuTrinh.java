package entity;

import java.time.LocalDate;

// Liệu trình điều trị nhiều buổi (niềng răng, implant, nội nha...)
public class LieuTrinh {

    private String maLieuTrinh; // Mã liệu trình, dạng LT000001
    private BenhNhan maBN; // Bệnh nhân
    private String loaiDichVu; // Loại dịch vụ của liệu trình
    private int soBuoiDuKien; // Số buổi dự kiến
    private String giaiDoanHienTai; // Giai đoạn hiện tại
    private LocalDate ngayBatDau; // Ngày bắt đầu
    private LocalDate ngayDuKienKetThuc; // Ngày dự kiến kết thúc
    private LocalDate ngayKetThucThucTe; // Ngày kết thúc thực tế
    private long tongChiPhiDuKien; // Tổng chi phí dự kiến
    private String trangThai; // Đang thực hiện, Hoàn thành hoặc Đã hủy
    private NhanVien maNhaSiPhuTrach; // Nha sĩ phụ trách
    private String ghiChu; // Ghi chú

    public String getMaLieuTrinh() {
        return maLieuTrinh;
    }

    public void setMaLieuTrinh(String maLieuTrinh) {
        this.maLieuTrinh = maLieuTrinh;
    }

    public BenhNhan getMaBN() {
        return maBN;
    }

    public void setMaBN(BenhNhan maBN) {
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

    public NhanVien getMaNhaSiPhuTrach() {
        return maNhaSiPhuTrach;
    }

    public void setMaNhaSiPhuTrach(NhanVien maNhaSiPhuTrach) {
        this.maNhaSiPhuTrach = maNhaSiPhuTrach;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

}
