package App;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.time.LocalDateTime;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import Controller.control_taikhoan;
import Entity.NhanVien;

public class Jp_body_taikhoan extends JPanel{
	
	
	public Jp_body_taikhoan() {
		
		
		control_taikhoan ac = new control_taikhoan(this);
		
		
		this.setLayout(new BorderLayout());
		
		
		JPanel jp1 = new JPanel();
		jp1.setLayout(new BorderLayout());
		
		
		JPanel jp1_1 = new JPanel();
		jp1_1.setLayout(new BorderLayout());
		
		
		JLabel lb_taikhoan = new JLabel("QUẢN LÝ TÀI KHOẢN ĐĂNG NHẬP");
		
		lb_taikhoan.setFont(new Font("Arial",Font.BOLD,18));
		
		jp1_1.add(lb_taikhoan,BorderLayout.NORTH);
		
		
		JPanel jp1_1_button = new JPanel();
		
		jp1_1_button.setLayout(new FlowLayout(FlowLayout.LEFT));
		
		
		JButton bt_captaikhoan = new JButton("Cấp tài khoản mới");
		
		bt_captaikhoan.setFont(new Font("Segoe UI", Font.BOLD, 13));
        bt_captaikhoan.setBackground(new Color(40, 167, 69)); // Xanh lá
        bt_captaikhoan.setForeground(Color.WHITE);
        bt_captaikhoan.setFocusPainted(false);
        bt_captaikhoan.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        jp1_1_button.add(bt_captaikhoan);
        
        
        JButton bt_chitiettk = new JButton("Chi tiết tài khoản");
        
        bt_chitiettk.addActionListener(ac);
        bt_chitiettk.setFont(new Font("Segoe UI", Font.BOLD, 13));
        bt_chitiettk.setBackground(new Color(40, 167, 69)); // Xanh lá
        bt_chitiettk.setForeground(Color.WHITE);
        bt_chitiettk.setFocusPainted(false);
        bt_chitiettk.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        jp1_1_button.add(bt_chitiettk);
        
        
        JButton bt_khoataikhoan = new JButton("Khóa tài khoản");
        
        bt_khoataikhoan.setFont(new Font("Segoe UI", Font.BOLD, 13));
        bt_khoataikhoan.setBackground(new Color(244, 208, 63)); // vàng
        bt_khoataikhoan.setForeground(Color.WHITE);
        bt_khoataikhoan.setFocusPainted(false);
        bt_khoataikhoan.setCursor(new Cursor(Cursor.HAND_CURSOR));
		
        
        jp1_1_button.add(bt_khoataikhoan);
        
        
        JButton bt_khoahettaikhoan = new JButton("Khóa hàng loạt tài khoản");
        
        bt_khoahettaikhoan.setFont(new Font("Segoe UI", Font.BOLD, 13));
        bt_khoahettaikhoan.setBackground(new Color(220, 53, 69)); // đỏ
        bt_khoahettaikhoan.setForeground(Color.WHITE);
        bt_khoahettaikhoan.setFocusPainted(false);
        bt_khoahettaikhoan.setCursor(new Cursor(Cursor.HAND_CURSOR));
		
		jp1_1_button.add(bt_khoahettaikhoan);
		
		
		JButton bt_intaikhoan = new JButton("In danh sách tài khoản");
		bt_intaikhoan.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        bt_intaikhoan.setBackground(Color.WHITE);
        bt_intaikhoan.setForeground(new Color(33, 37, 41));
        bt_intaikhoan.setFocusPainted(false);
        bt_intaikhoan.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        
        
        jp1_1_button.add(bt_intaikhoan);
        
        jp1_1.add(jp1_1_button,BorderLayout.CENTER);
        
        
        
        JPanel jp1_2 = new JPanel();
        jp1_2.setBackground(Color.WHITE);
        jp1_2.setLayout(new BorderLayout());
        
        JLabel lb_boloc = new JLabel("BỘ LỌC HỆ THỐNG");
        lb_boloc.setFont(new Font("Arial",Font.BOLD,14));
        
        
        jp1_2.add(lb_boloc,BorderLayout.NORTH);
        
        
        
        JPanel jp1_2_search = new JPanel();
        jp1_2_search.setBackground(Color.WHITE);
        
        jp1_2_search.setLayout(new GridLayout(2,3));
        
        JLabel lb_timkiem = new JLabel("Tìm kiếm nhân sự:");
        
        jp1_2_search.add(lb_timkiem);
        
        JLabel lb_quyen = new JLabel("Vai trò:");
        
        jp1_2_search.add(lb_quyen);
        
        
        JLabel lb_trangthai = new JLabel("Trạng thái");
        
        jp1_2_search.add(lb_trangthai);
        
        
        JTextField jt_timkiem = new JTextField();
        
        jt_timkiem.setToolTipText("Nhập Mã TK....");
        
        
        jp1_2_search.add(jt_timkiem);
        
        

//        
        
        JComboBox<String> jc_quyen = new JComboBox<String>(new String[] {"--Tất cả vai trò","Nha sĩ","Y tá","Lễ tân"});
        
        
        jp1_2_search.add(jc_quyen);
        
        
        JComboBox<String> jc_trangthai = new JComboBox<String>(new String[] {"Hoạt động","Bị khóa"});
        
        jp1_2_search.add(jc_trangthai);
        
        
        jp1_2.add(jp1_2_search,BorderLayout.CENTER);
        
        
        
        
        
        
        jp1.add(jp1_1,BorderLayout.NORTH);
        jp1.add(jp1_2,BorderLayout.CENTER);
        
        
        

        
        
        JPanel jp2 = new JPanel();
        
        jp2.setLayout(new BorderLayout());
        
        
        
        JLabel lb_danhsach = new JLabel("DANH SÁCH TÀI KHOẢN TRUY CẬP");
        lb_danhsach.setFont(new Font("Arial",Font.BOLD,14));
        jp2.add(lb_danhsach,BorderLayout.NORTH);
        
        
        String columns[] = new String[] {"Mã tài khoản","Tên đăng nhập","Mã nhân viên","Vai trò","Trạng thái"};
        
        DefaultTableModel model = new DefaultTableModel();
        model.setColumnIdentifiers(columns);
        
        JTable table = new JTable(model);
        
        JScrollPane scroll = new JScrollPane(table);
        
        jp2.add(scroll,BorderLayout.CENTER);
        
        

        
        
        
        
        this.add(jp1,BorderLayout.NORTH);
        
        this.add(jp2,BorderLayout.CENTER);
        

		
	}
	
	public void openchitiettaikhoan() {

		JDialog dia = new JDialog();
		
		dia.setLayout(new BorderLayout());
		dia.setTitle("Chi tiết tài khoản");
		dia.setSize(1200,200);
		
		dia.setLocationRelativeTo(null);
		
		
		JPanel jp1 = new JPanel();
		
		
		
		jp1.setLayout(new GridLayout(1,3,40,0));
		
		
		
		
		
		JPanel jp1_1 = new JPanel();
		
		jp1_1.setLayout(new BorderLayout());
		
		JLabel lb_thongtin = new JLabel("Thông tin định danh");
		lb_thongtin.setFont(new Font("Arial",Font.BOLD,14));
		
		jp1_1.add(lb_thongtin,BorderLayout.NORTH);
		
		
		
		JPanel jp1_1_thongtin = new JPanel();
		
		jp1_1_thongtin.setLayout(new GridLayout(4,2));
		
		JLabel lb_ma = new JLabel("Mã tài khoản : ");
		jp1_1_thongtin.add(lb_ma);
		
		JTextField jt_ma = new JTextField();
		jp1_1_thongtin.add(jt_ma);
		
		
		
		JLabel lb_ten = new JLabel("Tên đăng nhập : ");
		jp1_1_thongtin.add(lb_ten);
		
		JTextField jt_ten = new JTextField();
		jp1_1_thongtin.add(jt_ten);
		
		
		JLabel lb_sohuu = new JLabel("Nhân viên sở hữu");
		jp1_1_thongtin.add(lb_sohuu);
		
		String sohuu[] = new String[] {"NV1","NV2","NV3"};
		
		JComboBox<String> jc_sohuu = new JComboBox<String>(sohuu);
		
		jp1_1_thongtin.add(jc_sohuu);
		
		
		JLabel lb_vaitro = new JLabel("Vai trò");
		jp1_1_thongtin.add(lb_vaitro);
		
		String vaitro[]= new String[] {"Nha sĩ","Y tá","Lễ tân"};
		
		
		JComboBox<String> jc_vaitro = new JComboBox<String>(vaitro);
		
		
		jp1_1_thongtin.add(jc_vaitro);
		
		
		jp1_1.add(jp1_1_thongtin,BorderLayout.CENTER);
		
		
		
		jp1.add(jp1_1);
		
		
		
//		jp1_2
		
		JPanel jp1_2 = new JPanel();
		
		jp1_2.setLayout(new GridLayout(5,1));
		
		
		JLabel lb_cauhinh = new JLabel("CẤU HÌNH BẢO MẬT");
		lb_cauhinh.setFont(new Font("Arial",Font.BOLD,14));
		
		jp1_2.add(lb_cauhinh);
		
		
		JCheckBox cb_hoatdong = new JCheckBox("Tài khoản đang hoạt động",true);
		
		jp1_2.add(cb_hoatdong);
		
		JCheckBox cb_doimk = new JCheckBox("Bắt buộc đổi mật khẩu lần sau");
		jp1_2.add(cb_doimk);
		
		
		
		JButton bt_caplai = new JButton("Cấp lại mật khẩu");
		
		bt_caplai.setBackground(new Color(13, 110, 253)); //xanh dương
		
		bt_caplai.setForeground(Color.WHITE);
		
		jp1_2.add(bt_caplai);
		
		
		JButton bt_khoa = new JButton("Khóa tài khoản");
		bt_khoa.setBackground(new Color(220, 53, 69));
		
		bt_khoa.setForeground(Color.WHITE);
		
		
		jp1_2.add(bt_khoa);
		
		
		
		
		jp1.add(jp1_2);
		
		
		
//		---jp1-3
		
		JPanel jp1_3 = new JPanel();
		
		jp1_3.setLayout(new BorderLayout());
		
		JLabel lb_lichsu = new JLabel("LỊCH SỬ VÀ AN TOÀN HỆ THỐNG");
		
		jp1_3.add(lb_lichsu,BorderLayout.NORTH);
		
		
		JPanel jp1_3_lichsu = new JPanel();
		
		jp1_3_lichsu.setLayout(new GridLayout(3,2));
		
		JLabel lb_ngaytao = new JLabel("Ngày tạo:");
		jp1_3_lichsu.add(lb_ngaytao);
		
		JTextField jt_ngaytao = new JTextField();
		jp1_3_lichsu.add(jt_ngaytao);
		
		
		JLabel lb_dangnhapcuoi = new JLabel("Đăng nhập cuối:");
		
		jp1_3_lichsu.add(lb_dangnhapcuoi);
		
		JTextField jt_dangnhapcuoi = new JTextField();
		
		jp1_3_lichsu.add(jt_dangnhapcuoi);
		
		
		JLabel lb_nhapsai = new JLabel("Nhập sai hiện tại:");
		
		jp1_3_lichsu.add(lb_nhapsai);
		
		JTextField jt_nhapsai = new JTextField();
		
		jp1_3_lichsu.add(jt_nhapsai);
		
		
		jp1_3.add(jp1_3_lichsu,BorderLayout.CENTER);
		
		
		JButton bt_reset = new JButton("Reset bộ đếm sai");
		
		bt_reset.setBackground(new Color(40, 167, 69)); // Xanh lá
        bt_reset.setForeground(Color.WHITE);
        bt_reset.setFocusPainted(false);
        bt_reset.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        
        
        
        jp1_3.add(bt_reset,BorderLayout.SOUTH);
        
        
        jp1.add(jp1_3);
        
        
        
//        jp2
        
        
        JPanel jp2 = new JPanel();
        
        jp2.setLayout(new FlowLayout());
        
        JButton bt_luu = new JButton("Lưu thay đổi");
        
        bt_luu.setFont(new Font("Segoe UI", Font.BOLD, 13));
        bt_luu.setBackground(new Color(40, 167, 69)); // Xanh lá
        bt_luu.setForeground(Color.WHITE);
        bt_luu.setFocusPainted(false);
        bt_luu.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        jp2.add(bt_luu);
        
        
        
        JButton bt_huy = new JButton("Hủy bỏ");
        
        bt_huy.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        bt_huy.setBackground(Color.WHITE);
        bt_huy.setForeground(new Color(33, 37, 41));
        bt_huy.setFocusPainted(false);
        bt_huy.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        
        jp2.add(bt_huy);
        
        
        dia.add(jp1,BorderLayout.CENTER);
        dia.add(jp2,BorderLayout.SOUTH);
        
        dia.setVisible(true);
		
		
	}
	
	

}
