package nhakhoakim.entity;

import java.time.LocalDateTime;

// Dụng cụ nha khoa và tình trạng tiệt trùng
public class DungCu {

    private String maDungCu; // mã dụng cụ, dạng DC000001
    private String tenDungCu; // tên dụng cụ
    private String loaiDichVu; // loại dịch vụ dùng dụng cụ này
    private String trangThaiVoTrung; // Đã tiệt trùng, Chờ xử lý hoặc Đang sử dụng
    private LocalDateTime ngayTietTrungCuoi; // lần tiệt trùng gần nhất
    private String phong; // phòng đặt dụng cụ
    private int soLuongCan; // số lượng cần cho một dịch vụ (lấy từ DichVuDungCu)

    public String getMaDungCu() {
        return maDungCu;
    }

    public void setMaDungCu(String maDungCu) {
        this.maDungCu = maDungCu;
    }

    public String getTenDungCu() {
        return tenDungCu;
    }

    public void setTenDungCu(String tenDungCu) {
        this.tenDungCu = tenDungCu;
    }

    public String getLoaiDichVu() {
        return loaiDichVu;
    }

    public void setLoaiDichVu(String loaiDichVu) {
        this.loaiDichVu = loaiDichVu;
    }

    public String getTrangThaiVoTrung() {
        return trangThaiVoTrung;
    }

    public void setTrangThaiVoTrung(String trangThaiVoTrung) {
        this.trangThaiVoTrung = trangThaiVoTrung;
    }

    public LocalDateTime getNgayTietTrungCuoi() {
        return ngayTietTrungCuoi;
    }

    public void setNgayTietTrungCuoi(LocalDateTime ngayTietTrungCuoi) {
        this.ngayTietTrungCuoi = ngayTietTrungCuoi;
    }

    public String getPhong() {
        return phong;
    }

    public void setPhong(String phong) {
        this.phong = phong;
    }

    public int getSoLuongCan() {
        return soLuongCan;
    }

    public void setSoLuongCan(int soLuongCan) {
        this.soLuongCan = soLuongCan;
    }

}
