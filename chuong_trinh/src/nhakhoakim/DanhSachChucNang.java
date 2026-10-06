package nhakhoakim;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// Danh sách chức năng của chương trình (24 chức năng lõi), xếp theo từng đối tượng.
// Mỗi nhóm là một menu; thêm hoặc đổi chức năng chỉ cần sửa ở đây.
public class DanhSachChucNang {

    // Lấy toàn bộ chức năng, theo thứ tự các menu hiện trên màn hình
    public static List<ChucNang> layTatCa() {
        List<ChucNang> ds = new ArrayList<>();

        // Bệnh nhân: tìm, tạo/sửa hồ sơ, xem bệnh án, tiền sử - dị ứng, liệu trình đang điều trị
        ds.add(new ChucNang("Bệnh nhân", "Tìm kiếm bệnh nhân", "BenhNhan, HoSoBenhAn", "Nha sĩ, Lễ tân", "Trường", "Tìm theo mã, họ tên, số điện thoại"));
        ds.add(new ChucNang("Bệnh nhân", "Thêm / cập nhật hồ sơ bệnh nhân", "BenhNhan, HoSoBenhAn", "Y tá, Lễ tân", "Trường", "Tạo mới và sửa; Y tá khai báo y tế/dị ứng ban đầu"));
        ds.add(new ChucNang("Bệnh nhân", "Xem hồ sơ bệnh án", "BenhNhan, HoSoBenhAn", "Nha sĩ", "Hậu", "Cảnh báo dị ứng hiển thị nổi bật; lịch sử khám"));
        ds.add(new ChucNang("Bệnh nhân", "Cập nhật tiền sử & dị ứng", "HoSoBenhAn", "Nha sĩ, Y tá", "Hậu", "Thay đổi phải ghi nhật ký hệ thống"));
        ds.add(new ChucNang("Bệnh nhân", "Xem liệu trình đang thực hiện", "LieuTrinh", "Nha sĩ", "Hậu", "Hiển thị trong hồ sơ bệnh án"));

        // Lịch hẹn: xem, đặt, dời/hủy, xác nhận; riêng lịch làm việc là của Nha sĩ và Y tá
        ds.add(new ChucNang("Lịch hẹn", "Xem lịch hẹn", "LichHen", "Lễ tân", "Trường", "Theo ngày/tuần, thấy khung giờ trống"));
        ds.add(new ChucNang("Lịch hẹn", "Đặt lịch hẹn", "LichHen", "Lễ tân", "Trường", "Kiểm tra trùng lịch và thời gian dịch vụ"));
        ds.add(new ChucNang("Lịch hẹn", "Dời / hủy lịch hẹn", "LichHen", "Lễ tân", "Trường", "Hủy phải có lý do"));
        ds.add(new ChucNang("Lịch hẹn", "Xác nhận lịch hẹn", "LichHen", "Lễ tân", "Trường", "Từ Chờ xác nhận sang Đã xác nhận, kể cả lịch tái khám do Nha sĩ tạo"));
        ds.add(new ChucNang("Lịch hẹn", "Xem lịch làm việc", "LichHen", "Nha sĩ, Y tá", "Hậu", "Chỉ xem; Nha sĩ thấy ca khám, Y tá thấy ca hỗ trợ"));

        // Lượt khám: khám, sơ đồ răng, kê đơn, tái khám; Y tá chuẩn bị dụng cụ cho ca khám
        ds.add(new ChucNang("Lượt khám", "Khám & ghi nhận điều trị", "LuotKham", "Nha sĩ", "Hậu", "Lý do khám, chẩn đoán, hướng điều trị, dịch vụ thực hiện"));
        ds.add(new ChucNang("Lượt khám", "Sơ đồ răng điều trị", "RangDieuTri", "Nha sĩ", "Hậu", "32 răng theo FDI; răng đã nhổ chỉ chọn dịch vụ implant/cầu răng"));
        ds.add(new ChucNang("Lượt khám", "Kê đơn thuốc", "LuotKham", "Nha sĩ", "Hậu", "Không cho kê thuốc trùng cảnh báo dị ứng"));
        ds.add(new ChucNang("Lượt khám", "Chỉ định tái khám", "LuotKham, LichHen", "Nha sĩ", "Hậu", "Tạo lịch hẹn trạng thái Chờ xác nhận"));
        ds.add(new ChucNang("Lượt khám", "Chuẩn bị dụng cụ theo dịch vụ", "DungCu", "Y tá", "Hậu", "Xác nhận chuẩn bị theo loại dịch vụ của ca khám"));

        // Hóa đơn: từ lượt khám đã hoàn tất đến thanh toán
        ds.add(new ChucNang("Hóa đơn", "Danh sách chờ thanh toán", "HoaDon", "Lễ tân", "Trường", "Các lượt khám đã hoàn tất, chưa lập hóa đơn"));
        ds.add(new ChucNang("Hóa đơn", "Lập hóa đơn", "HoaDon, ChiTietHoaDon", "Lễ tân", "Trường", "Tổng tiền = dịch vụ - giảm giá + thuế VAT"));
        ds.add(new ChucNang("Hóa đơn", "Thanh toán & in hóa đơn", "HoaDon", "Lễ tân", "Trường", "Tiền mặt / Chuyển khoản / Thẻ / Ví điện tử"));

        // Dịch vụ: ai cũng được xem bảng giá, chỉ Quản lý được thêm/sửa
        ds.add(new ChucNang("Dịch vụ", "Xem danh mục dịch vụ & giá", "DichVu", "Nha sĩ, Lễ tân, Quản lý", "Tâm", "Bảng giá và thời gian dự kiến; Nha sĩ tra khi khám, chỉ xem không sửa"));
        ds.add(new ChucNang("Dịch vụ", "Thêm / sửa dịch vụ", "DichVu", "Quản lý", "Tâm", "Mọi thay đổi giá ghi nhật ký hệ thống"));

        // Nhân viên: hồ sơ (kèm chứng chỉ hành nghề) và phân công ca trực
        ds.add(new ChucNang("Nhân viên", "Cập nhật hồ sơ nhân viên", "NhanVien", "Quản lý", "Tâm", "Gồm chứng chỉ hành nghề và ngày hết hạn"));
        ds.add(new ChucNang("Nhân viên", "Phân công ca trực", "NhanVien, CaTruc", "Quản lý", "Tâm", "Lập bảng phân công theo tuần"));

        // Tài khoản: đăng nhập và phân quyền
        ds.add(new ChucNang("Tài khoản", "Đăng nhập / Đăng xuất", "TaiKhoan", "Nha sĩ, Y tá, Lễ tân, Quản lý", "Tâm", "Khóa tài khoản sau 5 lần sai; điều hướng theo vai trò"));
        ds.add(new ChucNang("Tài khoản", "Tạo tài khoản & gán vai trò", "TaiKhoan", "Quản lý", "Tâm", "Mỗi tài khoản gắn đúng 1 nhân viên"));

        return ds;
    }

    // Gom chức năng theo nhóm đối tượng, mỗi nhóm là một menu
    public static Map<String, List<ChucNang>> layTheoNhom() {
        Map<String, List<ChucNang>> theoNhom = new LinkedHashMap<>();
        for (ChucNang cn : layTatCa()) {
            if (!theoNhom.containsKey(cn.getNhom())) {
                theoNhom.put(cn.getNhom(), new ArrayList<>());
            }
            theoNhom.get(cn.getNhom()).add(cn);
        }
        return theoNhom;
    }
}
