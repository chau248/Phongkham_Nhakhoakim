package nhakhoakim.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

import nhakhoakim.connection.jdbcutil;
import nhakhoakim.entity.NhanVien;

public class NhanVien_DAO implements Dao_interface<NhanVien>{
	
	public static NhanVien_DAO getinstance() {
		return new NhanVien_DAO();
	}
	
	
	
	
	
	
	@Override
	public int insert(NhanVien t) {
		int kq=0;
		
		
		try {
			Connection con = jdbcutil.getconnection();
			
			
			String sql = "INSERT INTO dbo.NhanVien (\r\n"
					+ "    maNV, hoTen, vaiTro, ngaySinh, gioiTinh, sdt, email, diaChi, \r\n"
					+ "    ngayVaoLam, chuyenKhoa, trangThai, soChungChiHanhNghe, ngayHetHanChungChi\r\n"
					+ ") \r\n"
					+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
			
			
			PreparedStatement st = con.prepareStatement(sql);
			
			
//			st.setString(1, t.getMaNV());
//			st.setString(2, t.getHoTen());
//			
//			st.setString(3,t.getVaiTro());
//			
//			st.setObject(4,t.getNgaySinh());
//			
//			
//			st.setString(5,t.getGioiTinh());
//			
//			st.setString(6, t.getSdt());
//			
//			st.setString(7, t.getEmail());
//			
//			st.setString(8,t.getDiaChi());
//			
//			st.setObject(9, t.getNgayVaoLam());
//			
//			
//			st.setString(10,t.getChuyenKhoa());
//			
//			st.setString(11,t.getTrangThai());
//			
//			
//			st.setString(12, t.getSoChungChiHanhNghe());
//			st.setObject(13,t.getNgayHetHanChungChi());
//			
//			
//			kq=st.executeUpdate();
//			
//			System.out.println("co" + kq +"bi thay doi");
//			
//			jdbcutil.closeconnection(con);
			
			
			
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		
		return kq;
	}

	@Override
	public int update(NhanVien t) {
		int kq=0;
		
		try {
			
			Connection con = jdbcutil.getconnection();
			String sql = "UPDATE dbo.NhanVien \r\n"
					+ "SET \r\n"
					+ "    hoTen = ?, \r\n"
					+ "    vaiTro = ?, \r\n"
					+ "    ngaySinh = ?, \r\n"
					+ "    gioiTinh = ?, \r\n"
					+ "    sdt = ?, \r\n"
					+ "    email = ?, \r\n"
					+ "    diaChi = ?, \r\n"
					+ "    ngayVaoLam = ?, \r\n"
					+ "    chuyenKhoa = ?, \r\n"
					+ "    trangThai = ?, \r\n"
					+ "    soChungChiHanhNghe = ?, \r\n"
					+ "    ngayHetHanChungChi = ?\r\n"
					+ "WHERE maNV = ?;";
			
			PreparedStatement st = con.prepareStatement(sql);
			
			
			
//			st.setString(1, t.getHoTen());
//			
//			st.setString(2,t.getVaiTro());
//			
//			st.setObject(3,t.getNgaySinh());
//			
//			
//			st.setString(4,t.getGioiTinh());
//			
//			st.setString(5, t.getSdt());
//			
//			st.setString(6, t.getEmail());
//			
//			st.setString(7,t.getDiaChi());
//			
//			st.setObject(8, t.getNgayVaoLam());
			
			
			st.setString(9,t.getChuyenKhoa());
			
			st.setString(10,t.getTrangThai());
			
			
			st.setString(11, t.getSoChungChiHanhNghe());
			st.setObject(12,t.getNgayHetHanChungChi());
			st.setString(13, t.getMaNV());
			
			
			kq=st.executeUpdate();
			
			System.out.println("co" + kq +"bi thay doi");
			
			jdbcutil.closeconnection(con);
			
			
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		return kq;
	}

	@Override
	public int delete(NhanVien t) {
		
		int kq=0;
		try {
			Connection con = jdbcutil.getconnection();
			
			String sql = "DELETE FROM dbo.NhanVien WHERE maNV = ?";
			
			PreparedStatement st = con.prepareStatement(sql);
			
			st.setString(1, t.getMaNV());
			
			
			kq=st.executeUpdate();
			
			System.out.println("co" + kq +"bi thay doi");
			
			jdbcutil.closeconnection(con);
			
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return kq;
	}

	@Override
	public ArrayList<NhanVien> selectall() {
		ArrayList<NhanVien> kq = new ArrayList<NhanVien>();
		
		try {
			Connection con = jdbcutil.getconnection();
			
			String sql = "select * from NhanVien";
			
			PreparedStatement st = con.prepareStatement(sql);
			
			ResultSet rs = st.executeQuery();
			
			
			
			while(rs.next()) {
				String maNV = rs.getString("maNV");
	            String hoTen = rs.getString("hoTen");
	            String vaiTro = rs.getString("vaiTro");
	            
	            
	            LocalDate NgaySinh = rs.getDate("ngaySinh").toLocalDate();
	            
	            String gioiTinh = rs.getString("gioiTinh");
	            String sdt = rs.getString("sdt");
	            String email = rs.getString("email");
	            String diaChi = rs.getString("diaChi");
	            
	            LocalDate NgayVaoLam = rs.getDate("ngayVaoLam").toLocalDate();
	            
	            
	            String chuyenKhoa = rs.getString("chuyenKhoa");
	            String trangThai = rs.getString("trangThai");
	            String soChungChiHanhNghe = rs.getString("soChungChiHanhNghe");
	            
	            
	            LocalDate NgayHetHan = rs.getDate("ngayHetHanChungChi").toLocalDate();
	            
	            
//	            NhanVien nv = new NhanVien(maNV, hoTen, vaiTro, NgaySinh, gioiTinh, sdt, email, diaChi, NgayVaoLam, chuyenKhoa, trangThai, soChungChiHanhNghe, NgayHetHan);
//				kq.add(nv);
			}
			
			jdbcutil.closeconnection(con);
			
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		
		
		
		
		return kq;
	}

	@Override
	public NhanVien selectbyid(NhanVien t) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<NhanVien> selectbycondition(String condition) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	
	
	

}
