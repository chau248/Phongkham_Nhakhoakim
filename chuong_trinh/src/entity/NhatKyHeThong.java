package entity;

import java.time.LocalDateTime;

public class NhatKyHeThong {
	
	
	    private String maNhatKy; // Mã định danh bản ghi log
	    private LocalDateTime thoiGian; // Thời điểm xảy ra hành động
	    private String tenDangNhap; // Tên tài khoản hoặc mã nhân
	    private String vaiTro; // Vai trò của người thực
	    private String hanhDong; // Loại hành động
	    private String doiTuongTacDong; // Tên bảng hoặc thực thể bị
	    private String maDoiTuong; // Mã bản ghi cụ thể bị tác động
	    private String chiTietThayDoi; // Nội dung chi tiết
	    private String diaChiIP; // Địa chỉ IP của máy thực
	    private String trangThai; // Kết quả thực
	    private String ghiChu; // Ghi chú bổ sung
	    
	    
	    
		public NhatKyHeThong(String maNhatKy, LocalDateTime thoiGian, String tenDangNhap, String vaiTro,
				String hanhDong, String doiTuongTacDong, String maDoiTuong, String chiTietThayDoi, String diaChiIP,
				String trangThai, String ghiChu) {
			super();
			this.maNhatKy = maNhatKy;
			this.thoiGian = thoiGian;
			this.tenDangNhap = tenDangNhap;
			this.vaiTro = vaiTro;
			this.hanhDong = hanhDong;
			this.doiTuongTacDong = doiTuongTacDong;
			this.maDoiTuong = maDoiTuong;
			this.chiTietThayDoi = chiTietThayDoi;
			this.diaChiIP = diaChiIP;
			this.trangThai = trangThai;
			this.ghiChu = ghiChu;
		}



		public String getMaNhatKy() {
			return maNhatKy;
		}



		public void setMaNhatKy(String maNhatKy) {
			this.maNhatKy = maNhatKy;
		}



		public LocalDateTime getThoiGian() {
			return thoiGian;
		}



		public void setThoiGian(LocalDateTime thoiGian) {
			this.thoiGian = thoiGian;
		}



		public String getTenDangNhap() {
			return tenDangNhap;
		}



		public void setTenDangNhap(String tenDangNhap) {
			this.tenDangNhap = tenDangNhap;
		}



		public String getVaiTro() {
			return vaiTro;
		}



		public void setVaiTro(String vaiTro) {
			this.vaiTro = vaiTro;
		}



		public String getHanhDong() {
			return hanhDong;
		}



		public void setHanhDong(String hanhDong) {
			this.hanhDong = hanhDong;
		}



		public String getDoiTuongTacDong() {
			return doiTuongTacDong;
		}



		public void setDoiTuongTacDong(String doiTuongTacDong) {
			this.doiTuongTacDong = doiTuongTacDong;
		}



		public String getMaDoiTuong() {
			return maDoiTuong;
		}



		public void setMaDoiTuong(String maDoiTuong) {
			this.maDoiTuong = maDoiTuong;
		}



		public String getChiTietThayDoi() {
			return chiTietThayDoi;
		}



		public void setChiTietThayDoi(String chiTietThayDoi) {
			this.chiTietThayDoi = chiTietThayDoi;
		}



		public String getDiaChiIP() {
			return diaChiIP;
		}



		public void setDiaChiIP(String diaChiIP) {
			this.diaChiIP = diaChiIP;
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
