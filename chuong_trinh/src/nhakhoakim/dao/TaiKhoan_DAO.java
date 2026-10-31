package nhakhoakim.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

import nhakhoakim.connection.jdbcutil;
import nhakhoakim.entity.TaiKhoan;

public class TaiKhoan_DAO implements Dao_interface<TaiKhoan> {
	
	
	
	public static TaiKhoan_DAO getinstance() {
		return new TaiKhoan_DAO();
	}

	@Override
	public int insert(TaiKhoan t) {
		int kq =0;
		
		try {
			
			Connection con = jdbcutil.getconnection();
			
			
			String sql = "INSERT INTO TaiKhoan (\r\n"
					+ "    maTaiKhoan, tenDangNhap, matKhau, vaiTro, trangThaiKhoa, \r\n"
					+ "    ngayTao, lanDangNhapCuoi, soLanDangNhapSai, thoiGianKhoaDen, \r\n"
					+ "    ngayDoiMatKhauCuoi, yeuCauDoiMatKhau\r\n"
					+ ") \r\n"
					+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
			
			
			PreparedStatement st = con.prepareStatement(sql);
			
			st.setString(1, t.getMaTaiKhoan());
	        st.setString(2, t.getTenDangNhap());
	        st.setString(3, t.getMatKhauHash());
	        st.setString(4, t.getVaiTro());
	        st.setBoolean(5, t.isTrangThaiKhoa());
	        
	        
	        st.setTimestamp(6, t.getNgayTao()!=null?Timestamp.valueOf(t.getNgayTao()) : Timestamp.valueOf(LocalDateTime.now()));
	        
	        
	        st.setTimestamp(7, t.getLanDangNhapCuoi()!=null?Timestamp.valueOf(t.getLanDangNhapCuoi()):Timestamp.valueOf(LocalDateTime.now()));
			
	        
	        st.setInt(8, t.getSoLanDangNhapSai());
	        
	        st.setTimestamp(9, t.getThoiGianKhoaDen()!=null?Timestamp.valueOf(t.getThoiGianKhoaDen()):Timestamp.valueOf(LocalDateTime.now()));
	        
	        st.setTimestamp(10, t.getNgayDoiMatKhauCuoi()!=null?Timestamp.valueOf(t.getNgayDoiMatKhauCuoi()):null);
	        
	        
	        
	        st.setBoolean(11, t.isYeuCauDoiMatKhau());
	        
	        
	        kq = st.executeUpdate();
	        
	        System.out.println("co" + kq + "dòng bi thay doi");
	        
	        
	        jdbcutil.closeconnection(con);
	        
	        
	        
	        
			
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		
		return kq;
	}

	@Override
	public int update(TaiKhoan t) {
		int kq =0;
		
		
		try {
			Connection con = jdbcutil.getconnection();
			
			String sql = "UPDATE TaiKhoan \r\n"
					+ "SET \r\n"
					+ "    tenDangNhap = ?, \r\n"
					+ "    matKhau = ?, \r\n"
					+ "    vaiTro = ?, \r\n"
					+ "    trangThaiKhoa = ?, \r\n"
					+ "    ngayTao = ?, \r\n"
					+ "    lanDangNhapCuoi = ?, \r\n"
					+ "    soLanDangNhapSai = ?, \r\n"
					+ "    thoiGianKhoaDen = ?, \r\n"
					+ "    ngayDoiMatKhauCuoi = ?, \r\n"
					+ "    yeuCauDoiMatKhau = ?\r\n"
					+ "WHERE maTaiKhoan = ?";
			
			PreparedStatement st = con.prepareStatement(sql);
			
			
	        st.setString(1, t.getTenDangNhap());
	        st.setString(2, t.getMatKhauHash());
	        st.setString(3, t.getVaiTro());
	        st.setBoolean(4, t.isTrangThaiKhoa());
	        
	        
	        st.setTimestamp(5, t.getNgayTao()!=null?Timestamp.valueOf(t.getNgayTao()) : Timestamp.valueOf(LocalDateTime.now()));
	        
	        
	        st.setTimestamp(6, t.getLanDangNhapCuoi()!=null?Timestamp.valueOf(t.getLanDangNhapCuoi()):Timestamp.valueOf(LocalDateTime.now()));
			
	        
	        st.setInt(7, t.getSoLanDangNhapSai());
	        
	        st.setTimestamp(8, t.getThoiGianKhoaDen()!=null?Timestamp.valueOf(t.getThoiGianKhoaDen()):Timestamp.valueOf(LocalDateTime.now()));
	        
	        st.setTimestamp(9, t.getNgayDoiMatKhauCuoi()!=null?Timestamp.valueOf(t.getNgayDoiMatKhauCuoi()):null);
	        
	        
	        
	        st.setBoolean(10, t.isYeuCauDoiMatKhau());
			
	        st.setString(11, t.getMaTaiKhoan());
	        
	        
	        kq = st.executeUpdate();
	        
	        System.out.println("co"+kq+"dong bi thay doi");
			
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return kq;
	}

	@Override
	public int delete(TaiKhoan t) {
		int kq =0;
		
		try {
			
			
			Connection con = jdbcutil.getconnection();
			
			String sql ="UPDATE TaiKhoan SET trangThaiKhoa = 1 WHERE maTaiKhoan = ?";
			
			
			
			PreparedStatement st = con.prepareStatement(sql);
			
			
			st.setString(1, t.getMaTaiKhoan());
			
			kq = st.executeUpdate();
			
			System.out.println("co"+kq+" dong bi thay doi");
			
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return kq;
	}

	@Override
	public ArrayList<TaiKhoan> selectall() {
		ArrayList<TaiKhoan> kq = new ArrayList<TaiKhoan>();
		
		
		try {
			
			Connection con = jdbcutil.getconnection();
			
			String sql = "select * from TaiKhoan";
			
			
			PreparedStatement st = con.prepareStatement(sql);
			
			
			ResultSet rs = st.executeQuery();
			
			
			while(rs.next()) {
				String maTaiKhoan = rs.getString("maTaiKhoan");
	            String tenDangNhap = rs.getString("tenDangNhap");
	            String matKhauHash = rs.getString("matKhau");
	            String vaiTro = rs.getString("vaiTro");
	            boolean trangThaiKhoa = rs.getBoolean("trangThaiKhoa");
	            
	            // Xử lý DATETIME2 (SQL) sang LocalDateTime (Java)
	            Timestamp tsNgayTao = rs.getTimestamp("ngayTao");
	            LocalDateTime ngayTao = (tsNgayTao != null) ? tsNgayTao.toLocalDateTime() : null;
	            
	            Timestamp tsLanCuoi = rs.getTimestamp("lanDangNhapCuoi");
	            LocalDateTime lanDangNhapCuoi = (tsLanCuoi != null) ? tsLanCuoi.toLocalDateTime() : null;
	            
	            int soLanDangNhapSai = rs.getInt("soLanDangNhapSai");
	            
	            Timestamp tsKhoaDen = rs.getTimestamp("thoiGianKhoaDen");
	            LocalDateTime thoiGianKhoaDen = (tsKhoaDen != null) ? tsKhoaDen.toLocalDateTime() : null;
	            
	            
	            Timestamp sqlNgayDoi = rs.getTimestamp("ngayDoiMatKhauCuoi");
	            LocalDateTime ngayDoiMatKhauCuoi = (sqlNgayDoi != null) ? sqlNgayDoi.toLocalDateTime() : null;
	            
	            
	            boolean yeuCauDoiMatKhau = rs.getBoolean("yeuCauDoiMatKhau");
	            
	            
	            
	            
//	            TaiKhoan tk = new TaiKhoan(maTaiKhoan, tenDangNhap, matKhauHash, vaiTro, trangThaiKhoa, ngayTao, lanDangNhapCuoi, soLanDangNhapSai, thoiGianKhoaDen, ngayDoiMatKhauCuoi, yeuCauDoiMatKhau);
	            
//	            kq.add(tk);
	            
	            
	            
	            
			}
			
			
			jdbcutil.closeconnection(con);
			
			
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return kq;
	}

	@Override
	public TaiKhoan selectbyid(TaiKhoan t) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<TaiKhoan> selectbycondition(String condition) {
		// TODO Auto-generated method stub
		return null;
	}
	
	

}
