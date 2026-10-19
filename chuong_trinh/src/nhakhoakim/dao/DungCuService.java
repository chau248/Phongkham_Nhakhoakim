package nhakhoakim.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import nhakhoakim.entity.DungCu;
import nhakhoakim.entity.HangSo;
import nhakhoakim.entity.NhanVien;

// Nghiệp vụ dụng cụ: xem dụng cụ cần cho dịch vụ, xác nhận chuẩn bị cho ca khám và trả dụng cụ về chờ tiệt trùng lại
public class DungCuService {

    // Trạng thái "Chờ xử lý" chưa có hằng trong TrangThai nên khai báo tại đây (khớp CHECK của cột trangThaiVoTrung)
    private static final String CHO_XU_LY = "Chờ xử lý";

    private final DungCuDAO dungCuDAO;
    private final Supplier<NhanVien> nguoiDung; // người đang đăng nhập

    // Dùng DAO thật và người đăng nhập lấy từ PhienLamViec
    public DungCuService() {
        this(new DungCuDAO(), PhienLamViec::getNhanVienHienTai);
    }

    // Dùng các đối tượng truyền vào (để thay bản giả khi kiểm thử)
    public DungCuService(DungCuDAO dungCuDAO, Supplier<NhanVien> nguoiDung) {
        this.dungCuDAO = dungCuDAO;
        this.nguoiDung = nguoiDung;
    }

    // Dụng cụ cần cho một dịch vụ, kèm tình trạng tiệt trùng
    public List<DungCu> layTheoDichVu(String maDichVu) {
        return dungCuDAO.layTheoDichVu(maDichVu);
    }

    // Xác nhận chuẩn bị dụng cụ cho dịch vụ (LK-12, LK-13): chỉ Y tá thao tác,
    // tất cả dụng cụ phải ở trạng thái Đã tiệt trùng, sau đó chuyển sang Đang sử dụng; trả về số dụng cụ đã chuẩn bị
    // Lưu ý: vai trò y tá được kiểm ngay đầu hàm (trước đây hàm này chưa kiểm vai trò)
    public int chuanBiDungCu(String maDichVu) {
        NhanVien nguoi = nguoiDung.get();
        if (nguoi == null || !HangSo.VAI_TRO_Y_TA.equals(nguoi.getVaiTro())) {
            throw new NghiepVuException("Chỉ y tá mới được chuẩn bị dụng cụ");
        }
        List<DungCu> ds = dungCuDAO.layTheoDichVu(maDichVu);
        if (ds.isEmpty()) {
            throw new NghiepVuException("Dịch vụ này chưa khai báo dụng cụ cần chuẩn bị");
        }
        List<String> chuaTietTrung = new ArrayList<>();
        for (DungCu d : ds) {
            if (!HangSo.DA_TIET_TRUNG.equals(d.getTrangThaiVoTrung())) {
                chuaTietTrung.add(d.getTenDungCu() + " (" + d.getTrangThaiVoTrung() + ")");
            }
        }
        if (!chuaTietTrung.isEmpty()) {
            throw new NghiepVuException("Dụng cụ chưa tiệt trùng, không thể dùng: " + String.join("; ", chuaTietTrung));
        }
        List<DungCu> daChuyen = new ArrayList<>();
        for (DungCu d : ds) {
            boolean duoc = dungCuDAO.capNhatTrangThai(d.getMaDungCu(), HangSo.DA_TIET_TRUNG, HangSo.DANG_SU_DUNG);
            if (!duoc) {
                // Có người khác vừa lấy dụng cụ này: trả lại những dụng cụ đã chuyển rồi báo lỗi
                for (DungCu da : daChuyen) {
                    dungCuDAO.capNhatTrangThai(da.getMaDungCu(), HangSo.DANG_SU_DUNG, HangSo.DA_TIET_TRUNG);
                }
                throw new NghiepVuException("Dụng cụ \"" + d.getTenDungCu() + "\" không còn ở trạng thái Đã tiệt trùng (có thể ca khác vừa lấy), không thể chuẩn bị");
            }
            daChuyen.add(d);
        }
        return ds.size();
    }

    // Trả dụng cụ của dịch vụ sau ca khám: chuyển từ Đang sử dụng về Chờ xử lý để tiệt trùng lại; trả về số dụng cụ đã trả
    // Dụng cụ nào không ở trạng thái Đang sử dụng thì bỏ qua
    public int traDungCu(String maDichVu) {
        List<DungCu> ds = dungCuDAO.layTheoDichVu(maDichVu);
        int soDaTra = 0;
        for (DungCu d : ds) {
            if (dungCuDAO.capNhatTrangThai(d.getMaDungCu(), HangSo.DANG_SU_DUNG, CHO_XU_LY)) {
                soDaTra++;
            }
        }
        return soDaTra;
    }
}
