package DAO;

import java.util.ArrayList;

public interface Dao_interface<T>{
	
	public int insert(T t);
	
	public int update (T t);
	
	public int delete(T t);
	
	public ArrayList<T> selectall();
	
	
	public T selectbyid(T t);
	
	public ArrayList<T> selectbycondition(String condition);
	
	
	
	

}
