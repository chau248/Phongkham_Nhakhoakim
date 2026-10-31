package nhakhoakim.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

import nhakhoakim.Connection.jdbcutil;
import nhakhoakim.entity.DichVu;
import nhakhoakim.entity.NhanVien;

public class DichVu_DAO implements Dao_interface<DichVu>{

	@Override
	public int insert(DichVu t) {
		int kq =0;
		
		try {
			
			Connection con = jdbcutil.getconnection();
			
			
			String sql = "INSERT INTO DichVu (\r\n"
					+ "    maDichVu, tenDichVu, gia, thoiGianDuKien, moTa, \r\n"
					+ "    donViTinh, trangThaiHoatDong, yeuCauLieuTrinh, lienQuanImplantCauRang\r\n"
					+ ") \r\n"
					+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
			
			
			PreparedStatement st = con.prepareStatement(sql);
			
			st.setString(1,t.getMaDichVu());
			
			st.setString(2,t.getTenDichVu());
			
			st.setBigDecimal(3,t.getGia());
			
			st.setObject(4, t.getThoiGianDuKien());
			
			st.setString(5,t.getMoTa());
			st.setString(6, t.getDonViTinh());
			
			st.setBoolean(7, t.isTrangThaiHoatDong());
			
			st.setBoolean(8,t.isYeuCauLieuTrinh());
			st.setBoolean(9,t.isLienQuanImplantCauRang());
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return kq;
	}

	@Override
	public int update(DichVu t) {
		int kq =0;
		
		try {
			
			Connection con = jdbcutil.getconnection();
			
			
			String sql = "UPDATE DichVu \r\n"
					+ "SET \r\n"
					+ "    tenDichVu = ?, \r\n"
					+ "    gia = ?, \r\n"
					+ "    thoiGianDuKien = ?, \r\n"
					+ "    moTa = ?, \r\n"
					+ "    donViTinh = ?, \r\n"
					+ "    trangThaiHoatDong = ?, \r\n"
					+ "    yeuCauLieuTrinh = ?, \r\n"
					+ "    lienQuanImplantCauRang = ?\r\n"
					+ "WHERE maDichVu = ?; ";
			
			
			PreparedStatement st = con.prepareStatement(sql);
			
			
			
			st.setString(1,t.getTenDichVu());
			
			st.setBigDecimal(2,t.getGia());
			
			st.setObject(3, t.getThoiGianDuKien());
			
			st.setString(4,t.getMoTa());
			st.setString(5, t.getDonViTinh());
			
			st.setBoolean(6, t.isTrangThaiHoatDong());
			
			st.setBoolean(7,t.isYeuCauLieuTrinh());
			st.setBoolean(8,t.isLienQuanImplantCauRang());
			st.setString(9,t.getMaDichVu());
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return kq;
	}

	@Override
	public int delete(DichVu t) {
		int kq =0;
		
		try {
			
			Connection con = jdbcutil.getconnection();
			
			
			String sql = "UPDATE DichVu SET trangThaiHoatDong = 0 WHERE maDichVu = ?";
			
			
			PreparedStatement st = con.prepareStatement(sql);
			
			st.setString(1,t.getMaDichVu());
			
			
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return kq;
	}

	@Override
	public ArrayList<DichVu> selectall() {
		ArrayList<DichVu> kq = new ArrayList<DichVu>();
		
		try {
			Connection con = jdbcutil.getconnection();
			
			String sql = "select * from NhanVien";
			
			PreparedStatement st = con.prepareStatement(sql);
			
			ResultSet rs = st.executeQuery();
			
			
			
			while(rs.next()) {
				String maDichVu = rs.getString("maDichVu");
			    String tenDichVu = rs.getString("tenDichVu");
			    BigDecimal gia = rs.getBigDecimal("gia");
			    int thoiGianDuKien = rs.getInt("thoiGianDuKien");
			    String moTa = rs.getString("moTa");
			    String donViTinh = rs.getString("donViTinh");
			    boolean trangThaiHoatDong = rs.getBoolean("trangThaiHoatDong");
			    boolean yeuCauLieuTrinh = rs.getBoolean("yeuCauLieuTrinh");
			    boolean lienQuanImplantCauRang = rs.getBoolean("lienQuanImplantCauRang");

			    // Khởi tạo đối tượng DichVu và đưa vào danh sách
			    DichVu dv = new DichVu(maDichVu, tenDichVu, gia, thoiGianDuKien, moTa, donViTinh, trangThaiHoatDong, yeuCauLieuTrinh, lienQuanImplantCauRang);
			    kq.add(dv);
			}
			
			jdbcutil.closeconnection(con);
			
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		
		
		
		
		return kq;
		
	}

	@Override
	public DichVu selectbyid(DichVu t) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<DichVu> selectbycondition(String condition) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	
	
	
}
