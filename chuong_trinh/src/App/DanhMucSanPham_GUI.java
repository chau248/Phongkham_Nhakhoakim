package App;



import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.PopupMenu;
import java.awt.ScrollPane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.concurrent.Flow;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.NumberFormatter;

import Controller.Control_Danhmucdichvu;
import Entity.NhanVien;







public class DanhMucSanPham_GUI extends JFrame {
	
//	luu danh sach cac nut menu
	
	private ArrayList<JButton> menubuttons = new ArrayList<JButton>();
	
//	theo dõi nút đang được chọn
	
	private JButton activeButton = null;

	private JPanel jp_sidebar;

	private CardLayout card;

	private JPanel jp_dichvu;
	
	
	

	public DanhMucSanPham_GUI() {
		
		this.setTitle("Danh Mục Dịch Vụ");
		this.setSize(1400,1800);
		
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setLocationRelativeTo(null);
		
		
		
		
		Control_Danhmucdichvu ac = new Control_Danhmucdichvu(this);
		
		
//		jp_header
		
		
		JPanel jp1_header = new JPanel();
		
		jp1_header.setLayout(new FlowLayout());
		
		JLabel lb_icon = new JLabel("icon",SwingConstants.LEFT);
		
		jp1_header.add(lb_icon);
		
		JLabel lb_Name = new JLabel("Nha Khoa");
		
		jp1_header.add(lb_Name);
		
		
		
		JButton btn_category = new JButton("Danh Mục Sản Phẩm");
		
		
		btn_category.setBackground(new Color(24,43,73));
		
		btn_category.setForeground(Color.WHITE);
		
		
		btn_category.setFocusPainted(false);
		
		btn_category.setFont(new Font("Arial",Font.BOLD,13));
		
		
		btn_category.setBorder(BorderFactory.createEmptyBorder(9,15,8,15));
		
		
		
//		tao popupmenu
		
		JPopupMenu p_menu = new JPopupMenu();
		
		JMenuItem itemtongquat = new JMenuItem("nha khoa tong quat");
		
		JMenuItem itemthammy = new JMenuItem("Nha khoa thẩm mỹ");
		
		JMenuItem itemimplant = new JMenuItem("Cấy ghép implant");
		
		JMenuItem itemchinhnha = new JMenuItem("chỉnh nha-niềng răng");
		
		
//		them vao pop up
		
		
		p_menu.add(itemtongquat);
		p_menu.add(itemthammy);
		
		p_menu.add(itemimplant);
		
		p_menu.add(itemchinhnha);
		
		
		btn_category.addActionListener(
				
				new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						p_menu.show(btn_category,0,btn_category.getHeight());
						
					}
				}
		);
		
		
		
		jp1_header.add(btn_category);
		
		
//		jp2_header
		
		
		JPanel jp2_header = new JPanel();
		
		
		jp2_header.setLayout(new FlowLayout());
		
		
//		button dang nhap
		JButton btn_dangnhap = new JButton("Đăng Nhập");
		
		
		btn_dangnhap.addActionListener(ac);
		btn_dangnhap.setBackground(new Color(0,150,255));
		
		
		btn_dangnhap.setForeground(Color.WHITE);
		
		btn_dangnhap.setFocusPainted(false);
		btn_dangnhap.setFont(new Font("Arial",Font.BOLD,13));
		
		btn_dangnhap.setBorder(BorderFactory.createEmptyBorder(8,15,8,15));
		
		
		jp2_header.add(btn_dangnhap);
		
		
		
//		button dang ky
		
		JButton btn_dangky = new JButton("Đăng ký");
		
		btn_dangky.setBackground(new Color(0,150,255));
		
		
		btn_dangky.setForeground(Color.WHITE);
		
		btn_dangky.setFocusPainted(false);
		
		btn_dangky.setFont(new Font("Arial",Font.BOLD,13));
		
		
		btn_dangky.setBorder(BorderFactory.createEmptyBorder(9,15,8,15));
		
		
		jp2_header.add(btn_dangky);
		
		
		
		
		
		
		
		
		JPanel jpheader = new JPanel();
		
		jpheader.setLayout(new BorderLayout());
		
		
		jpheader.add(jp1_header,BorderLayout.WEST);
		jpheader.add(jp2_header,BorderLayout.EAST);
		
		
		
		
		
//		jpanel_sidebar
		
		jp_sidebar = new JPanel();
		
		
		jp_sidebar.setPreferredSize(new Dimension(220,750));
		
		jp_sidebar.setBackground(new Color(24,43,73));
		
		jp_sidebar.setLayout(new BoxLayout(jp_sidebar,BoxLayout.Y_AXIS));
		
//		khoang cach phia tren cùng
		
		
		jp_sidebar.add(javax.swing.Box.createVerticalStrut(20));
		
		
//		them cac menu:
		
		createmenuitem("Dịch vụ", true);   
        createmenuitem("Nhân viên", false);
        createmenuitem("Lịch Trực", false);
        createmenuitem("Tài khoản nhân viên", false);
        createmenuitem("Labo", false);
        createmenuitem("Khảo sát", false);
        createmenuitem("Mua hàng", false);
        createmenuitem("Kho vật tư", false);
        createmenuitem("Thu chi", false);
        createmenuitem("Khuyến mãi", false);
        createmenuitem("Cấu hình", false);
        createmenuitem("Báo cáo", false);
        
        
//        jp_content
        
        
        JPanel jp_content_1 = new JPanel();
        
        jp_content_1.setLayout(new BorderLayout());
        
        JLabel lblquanly = new JLabel("QUẢN LÝ DỊCH VỤ VÀ BẢNG GIÁ NIÊM YẾT");
        
        lblquanly.setFont(new Font("Arial",Font.BOLD,18));
        
        
        lblquanly.setForeground(new Color(33,37,41));
        
        
        
        jp_content_1.add(lblquanly,BorderLayout.NORTH);
        
        
        JPanel jp_content_1_1 = new JPanel();
        
        
        jp_content_1_1.setLayout(new FlowLayout(FlowLayout.LEFT));
        
        
        JButton btnAdd = new JButton("Thêm mới dịch vụ");
        
        btnAdd.addActionListener(ac);
        
        btnAdd.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnAdd.setBackground(new Color(40, 167, 69)); // Xanh lá
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setFocusPainted(false);
        btnAdd.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        
       
        
        
        
        jp_content_1_1.add(btnAdd);
        
        
        
        JButton btnupdate = new JButton("Sửa dịch vụ");
        btnupdate.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnupdate.setBackground(new Color(40, 167, 69)); // Xanh lá
        btnupdate.setForeground(Color.WHITE);
        btnupdate.setFocusPainted(false);
        btnupdate.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        jp_content_1_1.add(btnupdate);
        
        
        JButton btnExport = new JButton("Xuất dữ liệu Excel");
        
        btnExport.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnExport.setBackground(Color.WHITE);
        btnExport.setForeground(new Color(33, 37, 41));
        btnExport.setFocusPainted(false);
        btnExport.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        
        
        jp_content_1_1.add(btnExport);
        
        
        jp_content_1.add(jp_content_1_1,BorderLayout.CENTER);
        
        
        
        
        JPanel jp_content_2 = new JPanel();
        
        jp_content_2.setLayout(new BorderLayout());
        jp_content_2.setBackground(Color.WHITE);
        
        jp_content_2.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(222, 226, 230)),
                new EmptyBorder(12, 15, 12, 15)
        ));
        
        
        
        JLabel lbBoloc = new JLabel("Bộ lọc và Tìm Kiếm");
        
        
        lbBoloc.setFont(new Font("Arial",Font.BOLD,14));
        
        
        lbBoloc.setForeground(new Color(33,37,41));
        
        
        jp_content_2.add(lbBoloc,BorderLayout.NORTH);
        
        
        
        JPanel jp_content_2_2 = new JPanel();
        
        jp_content_2_2.setBackground(Color.WHITE);
        
        jp_content_2_2.setLayout(new GridLayout(2,3));
        
        
        JLabel lbsearch = new JLabel("Từ khóa tìm kiếm");
        
        jp_content_2_2.add(lbsearch);
        
        JLabel lbgroup = new JLabel("Nhóm dịch vụ");
        
        jp_content_2_2.add(lbgroup);
        
        JLabel lbcondi = new JLabel("Trạng thái");
        
        jp_content_2_2.add(lbcondi);
        
        
        JTextField jtsearch = new JTextField();
        jtsearch.setToolTipText("Nhập  mã dịch vụ...");
        
        jp_content_2_2.add(jtsearch);
        
        
        JComboBox<String> jcgroup = new JComboBox(new String[] {"-- Tất cả --", "Nha khoa tổng quát", "Tiểu phẫu", "Chỉnh nha", "Thẩm mỹ", "Phục hình"});
        
        jp_content_2_2.add(jcgroup);
        
        
        JComboBox<String> jccondi = new JComboBox(new String[] {"-- Tất cả --", "Đang chạy", "Tạm dừng"});
        
        
        jp_content_2_2.add(jccondi);
        
        
        jp_content_2.add(jp_content_2_2,BorderLayout.CENTER);
        
        
       
        
        JPanel jp_content = new JPanel();
        
        jp_content.setLayout(new BorderLayout());
        
        jp_content.add(jp_content_1,BorderLayout.NORTH);
        
        jp_content.add(jp_content_2,BorderLayout.CENTER);
        
        
        JPanel jp_table = new JPanel();
        
        jp_table.setLayout(new BorderLayout());
        
        jp_table.setBackground(Color.WHITE);
        
        JLabel lblist = new JLabel("Danh sách Dịch vụ Niêm yết");
        
        jp_table.add(lblist,BorderLayout.NORTH);

        
        String[] columnNames = {"Mã DV", "Tên dịch vụ", "Nhóm dịch vụ", "Đơn giá (VNĐ)", "Thời gian (Phút)","mô tả","đơn vị tính", "Trạng thái","yêu cầu liệu trình"};
        
        DefaultTableModel tablemodel =new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Không cho sửa trực tiếp trên ô
            }
        };
        
        
        
        
        JTable table = new JTable(tablemodel);
        table.setRowHeight(38);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(245, 247, 250));
        table.setSelectionBackground(new Color(232, 240, 254));
        table.setShowGrid(true);
        table.setGridColor(new Color(230, 230, 230));
        
        
        
     // Căn giữa các cột mã, thời gian, trạng thái, thao tác
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(6).setCellRenderer(centerRenderer);
        
        
        JScrollPane scrollPane = new JScrollPane(table);
        
        jp_table.add(scrollPane,BorderLayout.CENTER);
        
        
        
        
        
        
        JPanel jp_all = new JPanel();
        
        jp_all.setLayout(new BorderLayout());
        
        jp_all.add(jp_content,BorderLayout.NORTH);
        
        jp_all.add(jp_table,BorderLayout.CENTER);
        
        
    
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
//        end jp_content
        
        
		
		
        
        JPanel jp_body_dichvu = new JPanel();
        
        jp_body_dichvu.setLayout(new BorderLayout());
        
        
        
        
        jp_body_dichvu.add(jp_all,BorderLayout.CENTER);
        
        
      
//        --------------------------
        
        

        
        
//		--------------------

        
        
        
       
        
        
        
        
         card = new CardLayout();
        
        
        jp_dichvu = new JPanel(card);
        
        jp_dichvu.add(jp_body_dichvu,"manhinhdichvu");
        
        jp_dichvu.add(new jp_body_catruc(),"manhinhcatruc");
        
        jp_dichvu.add(new jp_body_nhanvien(),"manhinhnhanvien");
        jp_dichvu.add(new Jp_body_taikhoan(),"manhinhtaikhoan");
        
        
        

        
		
		
		
		this.setLayout(new BorderLayout());
		
		this.add(jpheader,BorderLayout.NORTH);
		
		this.add(jp_sidebar,BorderLayout.WEST);
		
		this.add(jp_dichvu,BorderLayout.CENTER);
		
		

		
		
		
		
		
		
		
		
		
		
		
		
		
	}
	
	
	public CardLayout getcardlayout() {
		return card;
	}
	
	
	public JPanel getjp_dichvu() {
		return jp_dichvu;
	}
	
	
	
	
//	open sua dichvu
	
	public void openthemdichvu() {
		JDialog dia_update = new JDialog();
		
		dia_update.setSize(900,250);
		dia_update.setTitle("Sửa dịch vụ");
		
		dia_update.setLocationRelativeTo(null);
		
		dia_update.setLayout(new BorderLayout());
		
		
		
		JPanel jp_all = new JPanel();
		
		jp_all.setLayout(new BorderLayout());
		
		
//		jp1 
		
		JPanel jp1 = new JPanel();
		jp1.setLayout(new GridLayout(3,2));
		
		JLabel lb_madv = new JLabel("mã dịch vụ : ");
		jp1.add(lb_madv);
		
		JTextField jt_madv = new JTextField();
		
		jt_madv.setToolTipText("DV....");
		
		
		
		jp1.add(jt_madv);
		
		
		JLabel lb_tendv = new JLabel("Tên dịch vụ : ");
		
		jp1.add(lb_tendv);
		
		JTextField jt_tendv = new JTextField();
		
		
		jp1.add(jt_tendv);
		
		JLabel lb_nhomdv = new JLabel("Nhóm dịch vụ : ");
		
		jp1.add(lb_nhomdv);
		
		JComboBox<String> jc_nhomdv = new JComboBox<String>(new String[] {"Nha khoa tổng quát","Tiểu phẫu","Chỉnh nha","Thẩm mỹ","Phục hình"});
		
		jp1.add(jc_nhomdv);
		
		
		jp_all.add(jp1,BorderLayout.NORTH);
		
		
		
		JPanel jp2_1 = new JPanel();
		
		jp2_1.setLayout(new GridLayout(1,4));
		
		JLabel lb_dongia = new JLabel("Đơn giá : ");
		
		jp2_1.add(lb_dongia);
		
		JTextField jt_dongia = new JTextField();
		
		jp2_1.add(jt_dongia);
		
		JLabel jl_thoigian = new JLabel("Thời gian(phút)");
		
		jp2_1.add(jl_thoigian);
		
		NumberFormatter formatter = new NumberFormatter();
		formatter.setValueClass(Integer.class);
		formatter.setAllowsInvalid(false); // Không cho nhập ký tự không phải số
		formatter.setMinimum(0);           // Giá trị nhỏ nhất

		JFormattedTextField jt_thoigian = new JFormattedTextField(formatter);
		
		jp2_1.add(jt_thoigian);
		
		
		
		JPanel jp2_2 = new JPanel();
		
		jp2_2.setLayout(new GridLayout(1,2));
		
		JLabel lb_mota = new JLabel("Mô tả : ");
		
		jp2_2.add(lb_mota);
		
		JTextArea jt_mota = new JTextArea();
		
		jp2_2.add(jt_mota);
		
		
		JPanel jp2 = new JPanel();
		jp2.setLayout(new GridLayout(2,1));
		
		jp2.add(jp2_1);
		
		jp2.add(jp2_2);
		
		
		jp_all.add(jp2,BorderLayout.CENTER);
		
		
		
		JPanel jp3 = new JPanel();
		
		jp3.setLayout(new GridLayout(1,6));
		
		
		JLabel lb_donvt = new JLabel("Đơn vị tính : ");
		
		jp3.add(lb_donvt);
		
		JTextField jt_donvt = new JTextField();
		
		jp3.add(jt_donvt);
		
		
		JLabel jl_trangthai = new JLabel("Trạng thái : ");
		
		jp3.add(jl_trangthai);
		
		JComboBox<String> jc_trangthai = new JComboBox<String>(new String[] {"Đang chạy","Tạm dừng"});
		
		jp3.add(jc_trangthai);
		
		
		JLabel jl_yeucau = new JLabel("Yêu cầu liệu trình : ");
		jp3.add(jl_yeucau);
		
		JComboBox<String> jc_yeucau = new JComboBox<String>(new String[] {"Có","Không"});
		jp3.add(jc_yeucau);
		
		
		jp_all.add(jp3,BorderLayout.SOUTH);
		
		
		
		JPanel jp_commit = new JPanel();
		
		jp_commit.setLayout(new FlowLayout(FlowLayout.CENTER));
		
		
		JButton bt_huy = new JButton("Hủy bỏ");
		
		bt_huy.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        bt_huy.setBackground(Color.WHITE);
        bt_huy.setForeground(new Color(33, 37, 41));
        bt_huy.setFocusPainted(false);
        bt_huy.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        
        jp_commit.add(bt_huy);
		
		JButton bt_luu = new JButton("xác nhận thêm");
		
		bt_luu.setFont(new Font("Segoe UI", Font.BOLD, 13));
        bt_luu.setBackground(new Color(40, 167, 69)); // Xanh lá
        bt_luu.setForeground(Color.WHITE);
        bt_luu.setFocusPainted(false);
        bt_luu.setCursor(new Cursor(Cursor.HAND_CURSOR));
		
        
        jp_commit.add(bt_luu);
		
		
		
		dia_update.add(jp_all,BorderLayout.CENTER);
		
		dia_update.add(jp_commit,BorderLayout.SOUTH);
		
		
		dia_update.setVisible(true);
		

		
	}
	
	
//	man hinh dang nhap
	
	public void opendangnhap() {
		
		JDialog dia_dangnhap = new JDialog();
		dia_dangnhap.setSize(600,170);
		
		dia_dangnhap.setTitle("Đăng nhập");
		
		dia_dangnhap.setLocationRelativeTo(null);
		
		
		dia_dangnhap.setLayout(new BorderLayout());
		
		
		JPanel jp1 = new JPanel();
		
		jp1.setLayout(new GridLayout(2,1));
		
		
		JLabel lb_name = new JLabel("NHA KHOA KIM");
		
		lb_name.setHorizontalAlignment(SwingConstants.CENTER);
		
		lb_name.setForeground(new Color(24,43,73));
		
		lb_name.setFont(new Font("Arial",Font.BOLD,18));
		
		
		
		
		jp1.add(lb_name);
		
		
		JLabel lb_dangnhap = new JLabel("Đăng Nhập Hệ Thống");
		
		
		lb_dangnhap.setHorizontalAlignment(SwingConstants.CENTER);
		
		
		lb_dangnhap.setFont(new Font("Arial",Font.BOLD,18));
		
		
		
		jp1.add(lb_dangnhap);
		
		
		
		
		JPanel jp2 = new JPanel();
		
		jp2.setLayout(new GridLayout(2,4));
		
		JLabel lb_user = new JLabel("Mã nhân viên : ");
		
		jp2.add(lb_user);
		
		JTextField jt_user = new JTextField();
		
		jp2.add(jt_user);
		
		
		JLabel lb_pass = new JLabel("Mật khẩu");
		
		jp2.add(lb_pass);
		
		
		JPasswordField jt_pass = new JPasswordField();
		
		jp2.add(jt_pass);
		
		
		JPanel jp3 = new JPanel();
		
		jp3.setLayout(new FlowLayout());
		
		
		JButton bt_dangnhap = new JButton("ĐĂNG NHẬP");
		
		bt_dangnhap.setBackground(new Color(40, 167, 69));
		
		
		jp3.add(bt_dangnhap);
		
		
		
		
		
		
		
		
		
		JPanel jp_all = new JPanel();
		
		jp_all.setLayout(new BorderLayout());
		
		jp_all.add(jp1,BorderLayout.NORTH);
		jp_all.add(jp2,BorderLayout.CENTER);
		
		jp_all.add(jp3,BorderLayout.SOUTH);
		
		
		dia_dangnhap.add(jp_all,BorderLayout.CENTER);
		
		
		dia_dangnhap.setVisible(true);
		
		
		
	}

	
	
	
	
//	hàm phụ trợ de tao tung dong menu 
	
	private void createmenuitem( String text, boolean isactive) {
		
		Control_Danhmucdichvu ac = new Control_Danhmucdichvu(this);
		
		
		JButton btn = new JButton(text);
		
		btn.addActionListener(ac);
		
		
		btn.setMaximumSize(new Dimension(200,40));
		
		btn.setPreferredSize(new Dimension(200,40));
		
		btn.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		
		
//		căn chu trai
		
		
		btn.setHorizontalAlignment(SwingConstants.LEFT);
		btn.setFont(new Font("Arial",Font.PLAIN,14));
		
		btn.setFocusPainted(false);
		
		btn.setBorderPainted(false);
		
		
		
//		neu active thì màu sáng hơn để nhận diện
		
		
		if(isactive == true) {
			setactivestyle(btn, true);
			
			activeButton = btn;
		}
		else {
			setactivestyle(btn, false);
		}
		
		
		btn.addActionListener(
				e->{
					if(activeButton!= btn) {
						if(activeButton!=null) {
							setactivestyle(activeButton, false);
						}
						
						setactivestyle(btn, true);
						activeButton = btn;
						
					}
				}
		);
		
		
//		hieu ứng rê chuột
		
		btn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent evt) {
				
				
				if(activeButton!= btn) {
					btn.setBackground(new Color(33,58,97));
					
					
				}
			}
			
			@Override
			public void mouseExited(MouseEvent evt) {
				if(activeButton!=btn) {
					btn.setBackground(new Color(24,43,73));
				}
				
			}
			
		});
		
		menubuttons.add(btn);
		jp_sidebar.add(btn);
		
		
		
	}
	
	
	
	
	
	
//	ham doi mau sac trang thai true/false
	
	private void setactivestyle(JButton btn, boolean isactive) {
		if(isactive == true) {
			btn.setBackground(new Color(41,75,122));
			
			btn.setForeground(Color.WHITE);
		}
		
		else {
			btn.setBackground(new Color(24,43,73));
			btn.setForeground(new Color(200,210,225));
		}
	}
	
	
	
	
	public static void main(String[] args) {
		
		
		DanhMucSanPham_GUI dm = new DanhMucSanPham_GUI();
		
		
		dm.setVisible(true);
	}
	
	
	
	
	

}
