package nhakhoakim.dao;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import nhakhoakim.entity.NhanVien;

// Thông tin nhân viên (chỉ đọc)
// Bản tạm do Hậu viết để chạy thử, dữ liệu giống 02_seed.sql.
// Tâm thay bằng bản đọc từ CSDL (giữ nguyên tên hàm).
public class NhanVienService {

    private static final List<NhanVien> DANH_SACH = new ArrayList<>();

    static {
        them("NV000001", "Lê Quốc Bảo", "Quản lý", null, null);
        them("NV000002", "Phạm Minh Khoa", "Nha sĩ", "Nội nha", LocalDate.now().plusYears(3));
        them("NV000003", "Đỗ Thu Hà", "Nha sĩ", "Chỉnh nha", LocalDate.now().plusDays(20));
        them("NV000004", "Võ Ngọc Anh", "Y tá", null, null);
        them("NV000005", "Bùi Thanh Tùng", "Y tá", null, null);
        them("NV000006", "Ngô Mỹ Linh", "Lễ tân", null, null);
    }

    // Thêm một nhân viên mẫu vào danh sách
    private static void them(String ma, String ten, String vaiTro, String chuyenKhoa, LocalDate hetHanChungChi) {
        NhanVien nv = new NhanVien();
        nv.setMaNV(ma);
        nv.setHoTen(ten);
        nv.setVaiTro(vaiTro);
        nv.setChuyenKhoa(chuyenKhoa);
        nv.setNgayHetHanChungChi(hetHanChungChi);
        nv.setTrangThai("Đang làm việc");
        DANH_SACH.add(nv);
    }

    // Tìm nhân viên theo mã; không có thì trả về null
    public NhanVien timTheoMa(String ma) {
        for (NhanVien nv : DANH_SACH) {
            if (nv.getMaNV().equals(ma)) {
                return nv;
            }
        }
        return null;
    }

    // Nhân viên có chứng chỉ hành nghề còn hạn tại ngày đó không (nha sĩ)
    public boolean chungChiConHan(String maNV, LocalDate ngay) {
        NhanVien nv = timTheoMa(maNV);
        return nv != null && nv.getNgayHetHanChungChi() != null && !nv.getNgayHetHanChungChi().isBefore(ngay);
    }

    // Nhân viên theo vai trò (Nha sĩ, Y tá, Lễ tân, Quản lý)
    public List<NhanVien> layTheoVaiTro(String vaiTro) {
        List<NhanVien> kq = new ArrayList<>();
        for (NhanVien nv : DANH_SACH) {
            if (nv.getVaiTro().equals(vaiTro)) {
                kq.add(nv);
            }
        }
        return kq;
    }
}
