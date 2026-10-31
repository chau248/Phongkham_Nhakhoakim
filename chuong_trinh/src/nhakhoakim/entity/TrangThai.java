package nhakhoakim.entity;

// Các giá trị trạng thái lưu trong CSDL của bệnh nhân, hồ sơ, lượt khám và răng; dùng hằng này thay vì gõ chuỗi
public class TrangThai {

    // Bệnh nhân
    public static final String DANG_DIEU_TRI = "Đang điều trị";
    public static final String NGUNG_THEO_DOI = "Ngừng theo dõi";

    // Hồ sơ bệnh án
    public static final String HO_SO_DANG_MO = "Đang mở";
    public static final String HO_SO_DA_DONG = "Đã đóng";

    // Lượt khám
    public static final String DANG_KHAM = "Đang khám";
    public static final String HOAN_THANH = "Hoàn thành";
    public static final String DA_HUY = "Đã hủy";

    // Răng
    public static final String RANG_BINH_THUONG = "Bình thường";
    public static final String RANG_SAU = "Sâu";
    public static final String RANG_DA_TRAM = "Đã trám";
    public static final String RANG_BOC_SU = "Bọc sứ";
    public static final String RANG_DA_NHO = "Đã nhổ";
    public static final String RANG_IMPLANT = "Implant";
    public static final String RANG_CAU_RANG = "Cầu răng";

    // Dụng cụ
    public static final String DA_TIET_TRUNG = "Đã tiệt trùng";
    public static final String DANG_SU_DUNG = "Đang sử dụng";

    // Liệu trình
    public static final String LIEU_TRINH_DANG_THUC_HIEN = "Đang thực hiện";
}
