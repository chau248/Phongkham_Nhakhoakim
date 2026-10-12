package nhakhoakim.dao;

// Lỗi vi phạm quy tắc nghiệp vụ; nội dung là câu tiếng Việt hiện thẳng cho người dùng
public class NghiepVuException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public NghiepVuException(String thongBao) {
        super(thongBao);
    }
}
