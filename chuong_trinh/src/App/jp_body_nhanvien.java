package App;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class jp_body_nhanvien extends JPanel{
	
	
	
	public jp_body_nhanvien() {
		
	       
	       this.setLayout(new BorderLayout());
	       
	       
	       JPanel jp_atri = new JPanel();
	       
	       jp_atri.setLayout(new BorderLayout());
	       
	       JPanel jp1_nhanvien = new JPanel();
	       
	       jp1_nhanvien.setLayout(new BorderLayout());
	       
	       JLabel lb_nhanvien = new JLabel("QUẢN LÝ HỒ SƠ NHÂN VIÊN");
	       
	       lb_nhanvien.setFont(new Font("Arial",Font.BOLD,18));
	       
	       jp1_nhanvien.add(lb_nhanvien,BorderLayout.CENTER);
	       
	       JPanel jp1_1_nhanvien = new JPanel();
	       
	       jp1_1_nhanvien.setLayout(new FlowLayout(FlowLayout.LEFT));
	       
	       
	       JButton bt_themnhanvien = new JButton("Thêm nhân viên mới");
	       
	       bt_themnhanvien.setFont(new Font("Segoe UI", Font.BOLD, 13));
	       bt_themnhanvien.setBackground(new Color(40, 167, 69)); // Xanh lá
	       bt_themnhanvien.setForeground(Color.WHITE);
	       bt_themnhanvien.setFocusPainted(false);
	       bt_themnhanvien.setCursor(new Cursor(Cursor.HAND_CURSOR));
	       
	       jp1_1_nhanvien.add(bt_themnhanvien);
	       
	       
	       JButton bt_suanhanvien = new JButton("Sửa nhân viên");
	       
	       bt_suanhanvien.setFont(new Font("Segoe UI", Font.BOLD, 13));
	       bt_suanhanvien.setBackground(new Color(40, 167, 69)); // Xanh lá
	       bt_suanhanvien.setForeground(Color.WHITE);
	       bt_suanhanvien.setFocusPainted(false);
	       bt_suanhanvien.setCursor(new Cursor(Cursor.HAND_CURSOR));
	       
	       jp1_1_nhanvien.add(bt_suanhanvien);
	       
	       JButton bt_xoanhanvien = new JButton("Xóa nhân viên");
	       
	       bt_xoanhanvien.setFont(new Font("Segoe UI", Font.BOLD, 13));
	       bt_xoanhanvien.setBackground(new Color(40, 167, 69)); // Xanh lá
	       bt_xoanhanvien.setForeground(Color.WHITE);
	       bt_xoanhanvien.setFocusPainted(false);
	       bt_xoanhanvien.setCursor(new Cursor(Cursor.HAND_CURSOR));
	       
	       jp1_1_nhanvien.add(bt_xoanhanvien);
	       
	       
	       
	       JButton bt_xoahetnhanvien = new JButton("Xóa hết nhân viên");
	       
	       bt_xoahetnhanvien.setFont(new Font("Segoe UI", Font.BOLD, 13));
	       bt_xoahetnhanvien.setBackground(new Color(40, 167, 69)); // Xanh lá
	       bt_xoahetnhanvien.setForeground(Color.WHITE);
	       bt_xoahetnhanvien.setFocusPainted(false);
	       bt_xoahetnhanvien.setCursor(new Cursor(Cursor.HAND_CURSOR));
	       
	       jp1_1_nhanvien.add(bt_xoahetnhanvien);
	       
	       
	       
	       JButton bt_xuatexcelnhanvien = new JButton("Xuất danh sách Excel");
	       
	       bt_xuatexcelnhanvien.setFont(new Font("Segoe UI", Font.PLAIN, 13));
	       bt_xuatexcelnhanvien.setBackground(Color.WHITE);
	       bt_xuatexcelnhanvien.setForeground(new Color(33, 37, 41));
	       bt_xuatexcelnhanvien.setFocusPainted(false);
	       bt_xuatexcelnhanvien.setCursor(new Cursor(Cursor.HAND_CURSOR));
	       
	       
	       jp1_1_nhanvien.add(bt_xuatexcelnhanvien);
	       
	       jp1_nhanvien.add(jp1_1_nhanvien,BorderLayout.SOUTH);
	       
	       jp_atri.add(jp1_nhanvien,BorderLayout.NORTH);
	       
	       
	       
	       JPanel jp2_nhanvien = new JPanel();
	       
	       jp2_nhanvien.setBackground(Color.WHITE);
	       
	       jp2_nhanvien.setLayout(new BorderLayout());
	       
	       
	       JLabel lb_bolocnhanvien = new JLabel("BỘ LỌC TÌM KIẾM");
	       lb_bolocnhanvien.setFont(new Font("Arial",Font.BOLD,14));
	       
	       
	       jp2_nhanvien.add(lb_bolocnhanvien,BorderLayout.NORTH);
	       
	       JPanel jp2_1_nhanvien = new JPanel();
	       
	       jp2_1_nhanvien.setLayout(new GridLayout(2,3));
	       
	       jp2_1_nhanvien.setBackground(Color.WHITE);
	       
	       
	       JLabel lb_timkiemnhanvien = new JLabel("Tìm kiếm nhân sự");
	       jp2_1_nhanvien.add(lb_timkiemnhanvien);
	       
	       
	       JLabel lb_vaitronhanvien = new JLabel("Vai trò");
	       
	       jp2_1_nhanvien.add(lb_vaitronhanvien);
	       
	       JLabel lb_trangthai = new JLabel("Trạng thái");
	       
	       jp2_1_nhanvien.add(lb_trangthai);
	       
	       
	       
	       JTextField jt_timkiemnhanvien = new JTextField();
	       
	       jt_timkiemnhanvien.setToolTipText("nhập mã NV");
	       
	       
	       jp2_1_nhanvien.add(jt_timkiemnhanvien);
	       
	       
	       JComboBox<String> jc_vaitronhanvien = new JComboBox<String>(new String[] {"Nha sĩ","Y tá","Lễ Tân"});
	       
	       jp2_1_nhanvien.add(jc_vaitronhanvien);
	       
	       
	       JComboBox<String> jc_trangthai = new JComboBox<String>(new String[] {"Đang làm việc","Đã Nghỉ"});
	       
	       
	       jp2_1_nhanvien.add(jc_trangthai);
	       
	       
	       jp2_nhanvien.add(jp2_1_nhanvien,BorderLayout.CENTER);
	       
	       
	       jp_atri.add(jp1_nhanvien,BorderLayout.NORTH);
	       jp_atri.add(jp2_nhanvien,BorderLayout.CENTER);
	       
	       this.add(jp_atri,BorderLayout.NORTH);
	       
	       
	       

	       
	       JPanel jp_tablenhanvien = new JPanel();
	       
	       jp_tablenhanvien.setLayout(new BorderLayout());
	       
	       
	       JLabel lb_danhsachnhanvien = new JLabel("Danh sách nhân viên");
	       lb_danhsachnhanvien.setFont(new Font("Arial",Font.BOLD,14));
	       
	       jp_tablenhanvien.add(lb_danhsachnhanvien,BorderLayout.NORTH);
	       
	       
	       
	       
	       String columns_nhanvien[] = new String[] {"Mã NV","Họ Tên","Vai Trò","Số điện thoại","Chuyên khoa","Trạng thái"};
	       
	       DefaultTableModel model_nhanvien= new DefaultTableModel();
	       
	       model_nhanvien.setColumnIdentifiers(columns_nhanvien);
	       
	       JTable table_nhanvien = new JTable(model_nhanvien);
	       
	       JScrollPane scroll_nhanvien = new JScrollPane(table_nhanvien);
	       
	       jp_tablenhanvien.add(scroll_nhanvien,BorderLayout.CENTER);
	       
	       
	       this.add(jp_tablenhanvien,BorderLayout.CENTER);
	}
	
	
	

}
