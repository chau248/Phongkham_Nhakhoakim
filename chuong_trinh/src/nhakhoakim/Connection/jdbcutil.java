package nhakhoakim.Connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.microsoft.sqlserver.jdbc.SQLServerDriver;

public class jdbcutil {
	public static Connection getconnection() {
		Connection c = null;
		
		try {
			DriverManager.registerDriver(new SQLServerDriver());
			
			
			String url = "jdbc:sqlserver://localhost:1433;databasename=NhaKhoaKim";
			String user = "sa";
			String password = "123456";
			
			c = DriverManager.getConnection(url,user,password);
			
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		
		return c;
	}
	
	
	
	public static void closeconnection(Connection c) {
		try {
			if(c!=null) {
				c.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	

}
