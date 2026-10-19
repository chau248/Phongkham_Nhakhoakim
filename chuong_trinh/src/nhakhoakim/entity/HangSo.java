package nhakhoakim.entity;

// Các hằng chuỗi dùng chung: vai trò nhân viên, trạng thái lưu trong CSDL và loại dịch vụ.
// Dùng hằng này thay vì gõ chuỗi để không sai chính tả.
public class HangSo {

    // Vai trò của nhân viên (cột NhanVien.vaiTro)
    public static final String VAI_TRO_NHA_SI = "Nha sĩ";
    public static final String VAI_TRO_Y_TA = "Y tá";
    public static final String VAI_TRO_LE_TAN = "Lễ tân";
    public static final String VAI_TRO_QUAN_LY = "Quản lý";

    // Trạng thái của bệnh nhân, hồ sơ, lượt khám, răng, dụng cụ, liệu trình
    public static final String DANG_DIEU_TRI = "Đang điều trị";
    public static final String NGUNG_THEO_DOI = "Ngừng theo dõi";
    public static final String HO_SO_DANG_MO = "Đang mở";
    public static final String HO_SO_DA_DONG = "Đã đóng";
    public static final String DANG_KHAM = "Đang khám";
    public static final String HOAN_THANH = "Hoàn thành";
    public static final String DA_HUY = "Đã hủy";
    public static final String RANG_BINH_THUONG = "Bình thường";
    public static final String RANG_SAU = "Sâu";
    public static final String RANG_DA_TRAM = "Đã trám";
    public static final String RANG_BOC_SU = "Bọc sứ";
    public static final String RANG_DA_NHO = "Đã nhổ";
    public static final String RANG_IMPLANT = "Implant";
    public static final String RANG_CAU_RANG = "Cầu răng";
    public static final String DA_TIET_TRUNG = "Đã tiệt trùng";
    public static final String DANG_SU_DUNG = "Đang sử dụng";
    public static final String LIEU_TRINH_DANG_THUC_HIEN = "Đang thực hiện";

    // Loại dịch vụ (cột DichVu.loaiDichVu) mà chương trình cần so sánh khi cảnh báo và gợi ý
    public static final String LOAI_KHAM = "Khám";
    public static final String LOAI_VE_SINH = "Vệ sinh";
    public static final String LOAI_TRAM = "Trám";
    public static final String LOAI_NHO_RANG = "Nhổ răng";
    public static final String LOAI_NOI_NHA = "Nội nha";
    public static final String LOAI_THAM_MY = "Thẩm mỹ";
    public static final String LOAI_IMPLANT = "Implant";
    public static final String LOAI_PHUC_HINH = "Phục hình";
}
