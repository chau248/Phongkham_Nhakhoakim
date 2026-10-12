package nhakhoakim.dao;

// Lỗi khi làm việc với cơ sở dữ liệu (bọc SQLException để các lớp trên không phải try/catch)
public class LoiCSDL extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public LoiCSDL(String thongBao, Throwable nguyenNhan) {
        super(thongBao, nguyenNhan);
    }
}
