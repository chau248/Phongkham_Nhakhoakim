package App;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

public class jp_body_catruc extends JPanel {
	
	
	public jp_body_catruc() {

        
        
        this.setLayout(new BorderLayout());
        
        
        JPanel jp1_catruc = new JPanel();
        
        jp1_catruc.setLayout(new BorderLayout());
        
        
        JLabel lb_title_catruc = new JLabel("BẢNG PHÂN CÔNG ĐIỀU PHỐI CA TRỰC-TUẦN");
        
        lb_title_catruc.setHorizontalAlignment(SwingConstants.CENTER);
        
        lb_title_catruc.setFont(new Font("Arial",Font.BOLD,18));
        
        
        JPanel jp1_1_catruc = new JPanel();
        
        jp1_1_catruc.setLayout(new FlowLayout(FlowLayout.LEFT));
        
        
        JButton bt_incatruc = new JButton("In báo cáo ca trực");
        
        bt_incatruc.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        bt_incatruc.setBackground(Color.WHITE);
        bt_incatruc.setForeground(new Color(33, 37, 41));
        bt_incatruc.setFocusPainted(false);
        bt_incatruc.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        
        jp1_1_catruc.add(bt_incatruc);
        
        
        JButton bt_themcatruc = new JButton("Thêm ca trực");
        
        bt_themcatruc.setFont(new Font("Segoe UI", Font.BOLD, 13));
        bt_themcatruc.setBackground(new Color(40, 167, 69)); // Xanh lá
        bt_themcatruc.setForeground(Color.WHITE);
        bt_themcatruc.setFocusPainted(false);
        bt_themcatruc.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        
        jp1_1_catruc.add(bt_themcatruc);
        
        
        JButton bt_suacatruc = new JButton("sửa ca trực");
        
        bt_suacatruc.setFont(new Font("Segoe UI", Font.BOLD, 13));
        bt_suacatruc.setBackground(new Color(40, 167, 69)); // Xanh lá
        bt_suacatruc.setForeground(Color.WHITE);
        bt_suacatruc.setFocusPainted(false);
        bt_suacatruc.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        
        
        jp1_1_catruc.add(bt_suacatruc);
        
        
        JButton bt_xoacatruc = new JButton("Xóa ca trực");
        
        bt_xoacatruc.setFont(new Font("Segoe UI", Font.BOLD, 13));
        bt_xoacatruc.setBackground(new Color(40, 167, 69)); // Xanh lá
        bt_xoacatruc.setForeground(Color.WHITE);
        bt_xoacatruc.setFocusPainted(false);
        bt_xoacatruc.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        
        jp1_1_catruc.add(bt_xoacatruc);
        
        
        JButton bt_xoahetcatruc = new JButton("Xóa hết ca trực");
        
        
        bt_xoahetcatruc.setFont(new Font("Segoe UI", Font.BOLD, 13));
        bt_xoahetcatruc.setBackground(new Color(40, 167, 69)); // Xanh lá
        bt_xoahetcatruc.setForeground(Color.WHITE);
        bt_xoahetcatruc.setFocusPainted(false);
        bt_xoahetcatruc.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        
        
        jp1_1_catruc.add(bt_xoahetcatruc);
        
        
        jp1_catruc.add(jp1_1_catruc,BorderLayout.SOUTH);
        
        
        
        this.add(jp1_catruc,BorderLayout.NORTH);
        
        
        
        
        JPanel jp2_catruc = new JPanel();
        
        jp2_catruc.setLayout(new BorderLayout());
        

        
        JLabel jl_catruc = new JLabel("ca sáng : 8h-16h , ca chiều : 12h-20h");
        
        jl_catruc.setForeground(Color.RED);
        
        jl_catruc.setHorizontalAlignment(SwingConstants.CENTER);
        
        jp2_catruc.add(jl_catruc,BorderLayout.NORTH);
        
        
        String columns_catruc[] = new String[] {"mã ca trực","mã nhân viên","ngày","ca làm","phòng","trạng thái"};
        
       DefaultTableModel model_catruc = new DefaultTableModel();
       
       model_catruc.setColumnIdentifiers(columns_catruc);
       
       JTable table_catruc = new JTable(model_catruc);
       
       JScrollPane scroll_catruc = new JScrollPane(table_catruc);
       
       
       jp2_catruc.add(scroll_catruc,BorderLayout.CENTER);
        
       
       this.add(jp2_catruc,BorderLayout.CENTER);
        
	}

}
