package nhakhoakim.entity;

import java.time.LocalDateTime;

public class NhatKyHeThong {
	
	
	    private String maNhatKy;           // Mã định danh bản ghi log (hoặc dùng Long/int tự tăng)
	    private LocalDateTime thoiGian;    // Thời điểm xảy ra hành động
	    private String tenDangNhap;        // Tên tài khoản hoặc mã nhân viên thực hiện thao tác
	    private String vaiTro;             // Vai trò của người thực hiện (Lễ tân, Nha sĩ, Quản trị viên...)
	    private String hanhDong;           // Loại hành động (THEM, SUA, XOA, DANG_NHAP, THANH_TOAN...)
	    private String doiTuongTacDong;    // Tên bảng hoặc thực thể bị tác động (HoaDon, BenhNhan, VatTu...)
	    private String maDoiTuong;         // Mã bản ghi cụ thể bị tác động (ví dụ: HD001, BN005)
	    private String chiTietThayDoi;     // Nội dung chi tiết (hoặc lưu JSON chứa giá trị cũ/mới trước và sau khi sửa)
	    private String diaChiIP;           // Địa chỉ IP của máy thực hiện (nếu triển khai dạng client-server)
	    private String trangThai;          // Kết quả thực hiện ("Thành công" hoặc "Thất bại")
	    private String ghiChu;             // Ghi chú bổ sung nếu có
	    
	    
	    
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
