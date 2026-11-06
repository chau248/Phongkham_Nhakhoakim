package entity;

// Các danh mục một cột dùng cho ô chọn
public enum DanhMuc {

    CHUYEN_KHOA("Chuyên khoa", "ChuyenKhoa", "tenChuyenKhoa", 100, "SELECT COUNT(*) FROM dbo.NhanVien WHERE chuyenKhoa = ?"),
    LOAI_DICH_VU("Loại dịch vụ", "LoaiDichVu", "tenLoai", 50,
            "SELECT (SELECT COUNT(*) FROM dbo.DichVu WHERE loaiDichVu = ?) + (SELECT COUNT(*) FROM dbo.LieuTrinh WHERE loaiDichVu = ?)"),
    PHONG("Phòng", "Phong", "tenPhong", 20,
            "SELECT (SELECT COUNT(*) FROM dbo.CaTruc WHERE phong = ?) + (SELECT COUNT(*) FROM dbo.LichHen WHERE phong = ?)"),
    // Vật liệu và thuốc chỉ là gợi ý
    VAT_LIEU("Vật liệu", "VatLieu", "tenVatLieu", 100, "SELECT 0 WHERE ? IS NOT NULL"),
    THUOC("Thuốc", "Thuoc", "tenThuoc", 100, "SELECT 0 WHERE ? IS NOT NULL");

    private final String nhan; // Tên hiển thị
    private final String bang; // Tên bảng
    private final String cot; // Tên cột khóa
    private final int doDaiToiDa; // Độ dài tối đa của tên
    private final String sqlDem; // Đếm số nơi đang dùng (tham số ? là tên danh mục)

    DanhMuc(String nhan, String bang, String cot, int doDaiToiDa, String sqlDem) {
        this.nhan = nhan;
        this.bang = bang;
        this.cot = cot;
        this.doDaiToiDa = doDaiToiDa;
        this.sqlDem = sqlDem;
    }

    // Danh mục gợi ý
    public boolean laGoiY() {
        return this == VAT_LIEU || this == THUOC;
    }

    public String getNhan() {
        return nhan;
    }

    public String getBang() {
        return bang;
    }

    public String getCot() {
        return cot;
    }

    public int getDoDaiToiDa() {
        return doDaiToiDa;
    }

    public String getSqlDem() {
        return sqlDem;
    }
}
