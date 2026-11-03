package nhakhoakim.entity;

import java.time.LocalDateTime;

// MaBanGhi : String

public class RangDieuTri {

    private String maBanGhi; // Mã bản ghi, dạng BG000001
    private String maLuotKham; // Mã lượt khám
    private int soFDI; // Số răng theo hệ FDI
    private String matRang; // Mặt răng
    private String trangThai; // Bình thường, Sâu, Đã trám
    private String vatLieu; // Vật liệu sử dụng
    private LocalDateTime ngayGhiNhan; // Thời điểm ghi nhận
    private String maDichVu; // Dịch vụ đã thực hiện trên
    private boolean daNho; // True nếu răng đã nhổ
    private String mauHienThi; // Màu hiển thị trên sơ đồ răng
    private String ghiChu; // Ghi chú
    
    
    
    
    
    

    public RangDieuTri() {
    }

    public RangDieuTri(String maBanGhi, String maLuotKham, int soFDI, String matRang, String trangThai, String vatLieu,
			LocalDateTime ngayGhiNhan, String maDichVu, boolean daNho, String mauHienThi, String ghiChu) {
		super();
		this.maBanGhi = maBanGhi;
		this.maLuotKham = maLuotKham;
		this.soFDI = soFDI;
		this.matRang = matRang;
		this.trangThai = trangThai;
		this.vatLieu = vatLieu;
		this.ngayGhiNhan = ngayGhiNhan;
		this.maDichVu = maDichVu;
		this.daNho = daNho;
		this.mauHienThi = mauHienThi;
		this.ghiChu = ghiChu;
	}
    
    

	public String getMaBanGhi() {
        return maBanGhi;
    }

    public void setMaBanGhi(String maBanGhi) {
        this.maBanGhi = maBanGhi;
    }

    public String getMaLuotKham() {
        return maLuotKham;
    }

    public void setMaLuotKham(String maLuotKham) {
        this.maLuotKham = maLuotKham;
    }

    public int getSoFDI() {
        return soFDI;
    }

    public void setSoFDI(int soFDI) {
        this.soFDI = soFDI;
    }

    public String getMatRang() {
        return matRang;
    }

    public void setMatRang(String matRang) {
        this.matRang = matRang;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public String getVatLieu() {
        return vatLieu;
    }

    public void setVatLieu(String vatLieu) {
        this.vatLieu = vatLieu;
    }

    public LocalDateTime getNgayGhiNhan() {
        return ngayGhiNhan;
    }

    public void setNgayGhiNhan(LocalDateTime ngayGhiNhan) {
        this.ngayGhiNhan = ngayGhiNhan;
    }

    public String getMaDichVu() {
        return maDichVu;
    }

    public void setMaDichVu(String maDichVu) {
        this.maDichVu = maDichVu;
    }

    public boolean isDaNho() {
        return daNho;
    }

    public void setDaNho(boolean daNho) {
        this.daNho = daNho;
    }

    public String getMauHienThi() {
        return mauHienThi;
    }

    public void setMauHienThi(String mauHienThi) {
        this.mauHienThi = mauHienThi;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

    // Kiểm tra số FDI có hợp lệ không
    public static boolean kiemTraSoFDI(int so) {
        return laRangVinhVien(so) || laRangSua(so);
    }

    // Răng vĩnh viễn
    public static boolean laRangVinhVien(int so) {
        int hang = so / 10;
        int rang = so % 10;
        return hang >= 1 && hang <= 4 && rang >= 1 && rang <= 8;
    }

    // Răng sữa: hàng 5 đến 8, răng số 1 đến 5
    public static boolean laRangSua(int so) {
        int hang = so / 10;
        int rang = so % 10;
        return hang >= 5 && hang <= 8 && rang >= 1 && rang <= 5;
    }

    // Răng khôn
    public static boolean laRangKhon(int so) {
        return laRangVinhVien(so) && so % 10 == 8;
    }
    
}