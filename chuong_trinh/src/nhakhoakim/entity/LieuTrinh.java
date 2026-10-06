package nhakhoakim.entity;

import java.time.LocalDate;

// Liệu trình điều trị nhiều buổi của một bệnh nhân
public class LieuTrinh {

    private String maLieuTrinh; // mã liệu trình, dạng LT000001
    private String maBN; // mã bệnh nhân
    private String loaiDichVu; // loại dịch vụ của liệu trình
    private int soBuoiDuKien; // số buổi dự kiến
    private String giaiDoanHienTai; // giai đoạn hiện tại
    private LocalDate ngayBatDau; // ngày bắt đầu
    private LocalDate ngayDuKienKetThuc; // ngày dự kiến kết thúc
    private LocalDate ngayKetThucThucTe; // ngày kết thúc thực tế
    private long tongChiPhiDuKien; // tổng chi phí dự kiến
    private String trangThai; // Đang thực hiện, Hoàn thành hoặc Đã hủy
    private String maNhaSiPhuTrach; // mã nha sĩ phụ trách
    private String ghiChu; // ghi chú

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
