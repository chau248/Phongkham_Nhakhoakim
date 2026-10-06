package nhakhoakim.entity;

// Bản tạm do Hậu viết để chạy thử; Tâm thay bằng bản đầy đủ của mình (giữ nguyên tên getter/setter).

// Dịch vụ nha khoa
public class DichVu {

    private String maDichVu; // mã dịch vụ, dạng DV000001
    private String tenDichVu; // tên dịch vụ
    private long gia; // giá niêm yết (đồng)
    private int thoiGianDuKien; // thời gian dự kiến (phút)
    private String loaiDichVu; // loại dịch vụ
    private boolean trangThaiHoatDong; // true nếu đang cung cấp
    private boolean yeuCauLieuTrinh; // true nếu cần theo liệu trình
    private boolean lienQuanImplantCauRang; // true nếu là dịch vụ Implant hoặc cầu răng

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
