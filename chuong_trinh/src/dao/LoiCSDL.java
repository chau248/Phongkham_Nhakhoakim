package dao;

// Lỗi khi làm việc với cơ sở dữ liệu
public class LoiCSDL extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public LoiCSDL(String thongBao, Throwable nguyenNhan) {
        super(thongBao, nguyenNhan);
    }
}
