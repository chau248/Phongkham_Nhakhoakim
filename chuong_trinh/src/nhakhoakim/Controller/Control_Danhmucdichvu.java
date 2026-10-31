package nhakhoakim.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import nhakhoakim.ui.DanhMucSanPham_GUI;

public class Control_Danhmucdichvu implements ActionListener{
	
	
	private DanhMucSanPham_GUI dm;
	
	public Control_Danhmucdichvu(DanhMucSanPham_GUI dm) {
		this.dm = dm;
	}
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		String s = e.getActionCommand();
		
		if(s.equals("Thêm mới dịch vụ")) {
			dm.openthemdichvu();
		}
		
		else if(s.equals("Đăng Nhập")) {
			dm.opendangnhap();
		}
		
		
		else if(s.equals("Dịch vụ")) {
			dm.getcardlayout().show(dm.getjp_dichvu(), "manhinhdichvu");
		}
		
		
		else if(s.equals("Nhân viên")) {
			dm.getcardlayout().show(dm.getjp_dichvu(), "manhinhnhanvien");
		}
		
		else if(s.equals("Lịch Trực")) {
			dm.getcardlayout().show(dm.getjp_dichvu(), "manhinhcatruc");
		}
		
		else if(s.equals("Tài khoản nhân viên")) {
			dm.getcardlayout().show(dm.getjp_dichvu(), "manhinhtaikhoan");
		}
		
	}

}
