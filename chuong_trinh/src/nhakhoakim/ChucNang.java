package nhakhoakim;

// Một chức năng của chương trình (một mục trong menu)
public class ChucNang {

    private String nhom;          // nhóm đối tượng, cũng là tên menu
    private String ten;           // tên chức năng
    private String lop;           // các lớp liên quan
    private String vaiTro;        // vai trò được dùng chức năng
    private String nguoiPhuTrach; // thành viên làm chức năng này
    private String ghiChu;        // quy tắc nghiệp vụ cần nhớ

    public ChucNang(String nhom, String ten, String lop, String vaiTro, String nguoiPhuTrach, String ghiChu) {
        this.nhom = nhom;
        this.ten = ten;
        this.lop = lop;
        this.vaiTro = vaiTro;
        this.nguoiPhuTrach = nguoiPhuTrach;
        this.ghiChu = ghiChu;
    }

    public String getNhom() {
        return nhom;
    }

    public String getTen() {
        return ten;
    }

    public String getLop() {
        return lop;
    }

    public String getVaiTro() {
        return vaiTro;
    }

    public String getNguoiPhuTrach() {
        return nguoiPhuTrach;
    }

    public String getGhiChu() {
        return ghiChu;
    }
}
