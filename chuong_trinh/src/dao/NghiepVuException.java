package dao;

// Lỗi vi phạm quy tắc nghiệp vụ
public class NghiepVuException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public NghiepVuException(String thongBao) {
        super(thongBao);
    }
}
