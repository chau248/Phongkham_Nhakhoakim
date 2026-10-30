package Controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import App.Jp_body_taikhoan;

public class control_taikhoan implements ActionListener {
	
	private Jp_body_taikhoan tk;
	
	
	public control_taikhoan(Jp_body_taikhoan tk) {
		this.tk = tk;
	}
	
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		String s = e.getActionCommand();
		
		
		
		if(s.equals("Chi tiết tài khoản")) {
			tk.openchitiettaikhoan();
		}
		
	}

	
	
}
