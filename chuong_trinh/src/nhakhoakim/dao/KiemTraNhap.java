package nhakhoakim.dao;

// Các hàm kiểm tra dữ liệu nhập dùng chung cho các Service
public class KiemTraNhap {

    // Chuỗi không được dài hơn độ dài cột trong CSDL, nếu không SQL Server báo lỗi cắt chuỗi khó hiểu
    public static void doDai(String giaTri, int toiDa, String tenTruong) {
        if (giaTri != null && giaTri.length() > toiDa) {
            throw new NghiepVuException(tenTruong + " dài tối đa " + toiDa + " ký tự (hiện đang " + giaTri.length() + " ký tự)");
        }
    }

    // Chuỗi rỗng hoặc toàn khoảng trắng thì trả về null, ngược lại cắt khoảng trắng hai đầu
    public static String rongThanhNull(String s) {
        return s == null || s.trim().isEmpty() ? null : s.trim();
    }
}
