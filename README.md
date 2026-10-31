# Phongkham_Nhakhoakim

Chương trình quản lý hệ thống phòng khám nha khoa Kim - Môn Phát triển ứng dụng - Nhóm 09

## Thành viên nhóm 9

| Thành viên | Phần phụ trách |
| :--- | :--- |
| Nguyễn Chí Hậu (nhóm trưởng) | Bệnh nhân, Lượt khám (Nha sĩ, Y tá) |
| Đặng Phi Trường | Lịch hẹn, Hóa đơn (Lễ tân) |
| Nguyễn Trần Trí Tâm | Dịch vụ, Nhân viên, Tài khoản (Quản lý) |

## Công nghệ

Java 11 trở lên, giao diện Java Swing, SQL Server (JDBC thuần, không dùng framework), Maven hoặc Eclipse.

## Cài đặt và chạy

1. **Tạo CSDL:** mở SQL Server, chạy lần lượt hai file (chạy lại nhiều lần được, không bị trùng):
   - `chuong_trinh/csdl/01_schema.sql` tạo CSDL `NhaKhoaKim` và các bảng
   - `chuong_trinh/csdl/02_seed.sql` nạp dữ liệu mẫu (12 nhân viên, 12 bệnh nhân, ca trực 7 ngày, lịch hẹn, lượt khám, hóa đơn...)

   Dùng `sqlcmd`: `sqlcmd -S localhost -E -C -I -f 65001 -i 01_schema.sql` rồi tương tự với `02_seed.sql`.
2. **Cấu hình kết nối:** sao chép `chuong_trinh/config/database.properties.example` thành `database.properties` (cùng thư mục), điền `db.user` và `db.password` của tài khoản SQL Server (tài khoản này cần quyền đọc ghi CSDL `NhaKhoaKim`). File này nằm trong `.gitignore`, không commit.
3. **Chạy:** chạy lớp `nhakhoakim.MainApp` (Eclipse: Run As Java Application, thư mục làm việc là `chuong_trinh`). Hộp đăng nhập hiện ra trước.

Hướng dẫn chi tiết cho từng thành viên (cài đặt, đọc code theo phần phụ trách, thực hành, demo, câu hỏi bảo vệ): xem [HUONG_DAN.md](HUONG_DAN.md).

## Tài khoản mẫu

Mật khẩu của tất cả tài khoản mẫu là `123456` (lưu dạng băm). Hãy đổi mật khẩu trước khi dùng thật.

| Tên đăng nhập | Vai trò | Ghi chú |
| :--- | :--- | :--- |
| `quanly` | Quản lý | |
| `nhasi1`, `nhasi2`, `nhasi3`, `nhasi4` | Nha sĩ | `nhasi5` bị khóa (nhân viên đã nghỉ việc) |
| `yta1`, `yta2`, `yta3`, `yta4` | Y tá | |
| `letan1`, `letan2` | Lễ tân | |

Sai mật khẩu 5 lần liên tiếp thì tài khoản bị khóa, Quản lý mở khóa ở menu Tài khoản.

## Chức năng theo vai trò (25 chức năng)

Menu chỉ hiện đúng chức năng của vai trò đang đăng nhập.

| Menu | Chức năng | Vai trò |
| :--- | :--- | :--- |
| Bệnh nhân | Tìm kiếm bệnh nhân | Nha sĩ, Y tá, Lễ tân, Quản lý |
| | Thêm / cập nhật hồ sơ bệnh nhân | Lễ tân, Quản lý |
| | Xem hồ sơ bệnh án, Xem liệu trình, Dòng thời gian điều trị | Nha sĩ, Y tá, Quản lý |
| | Cập nhật tiền sử & dị ứng | Nha sĩ, Y tá |
| Lịch hẹn | Xem lịch hẹn, Đặt lịch hẹn, Dời / hủy lịch hẹn, Xác nhận lịch hẹn | Lễ tân, Quản lý |
| | Xem lịch làm việc | Nha sĩ, Y tá |
| Lượt khám | Khám & ghi nhận điều trị, Kê đơn thuốc, Sơ đồ răng, Chỉ định tái khám, Gợi ý điều trị | Nha sĩ |
| Hóa đơn | Danh sách chờ thanh toán, Lập hóa đơn, Thanh toán & in hóa đơn | Lễ tân, Quản lý |
| Dịch vụ | Xem danh mục dịch vụ & giá | Mọi vai trò |
| | Thêm / sửa dịch vụ | Quản lý |
| Nhân viên | Cập nhật hồ sơ nhân viên, Phân công ca trực | Quản lý |
| Tài khoản | Đăng nhập / Đăng xuất (tài khoản của tôi, đổi mật khẩu) | Mọi vai trò |
| | Tạo tài khoản & gán vai trò | Quản lý |

## Quy tắc chính

- Ca Sáng 08:00-12:00, ca Chiều 13:00-17:00. Lịch hẹn tròn 30 phút, nằm trọn trong một ca, nha sĩ phải có ca trực và chứng chỉ hành nghề còn hạn, không trùng giờ.
- Không xóa dữ liệu nghiệp vụ, chỉ đổi trạng thái (nhân viên Nghỉ việc, dịch vụ Ngừng hoạt động, hóa đơn Đã hủy). Lượt khám Hoàn thành và hóa đơn Đã thanh toán bị khóa.
- Hóa đơn: tổng tiền = dịch vụ - giảm giá + VAT 8%, đơn giá lưu cố định lúc lập.
- Thao tác nhạy cảm (đổi giá, sửa dị ứng, gán vai trò, hủy lịch, hủy hóa đơn, khóa tài khoản...) được ghi vào bảng `NhatKyHeThong`.

## Cấu trúc thư mục

```
chuong_trinh/
  config/        cấu hình kết nối CSDL (database.properties không commit)
  csdl/          01_schema.sql (tạo bảng), 02_seed.sql (dữ liệu mẫu)
  src/nhakhoakim/
    MainApp.java   cửa sổ chính, đăng nhập, menu theo vai trò
    entity/        lớp dữ liệu (BenhNhan, LuotKham, HoaDon...)
    dao/           chỉ viết SQL (PreparedStatement), kết nối, sinh mã
    controller/    quy tắc nghiệp vụ, kiểm tra quyền, ghi nhật ký
    ui/            các màn hình Swing
```

Luồng gọi: màn hình (`ui`) gọi `controller`, `controller` gọi `dao`, `dao` chạy SQL. Quy tắc nghiệp vụ chỉ nằm ở `controller`.

## Quy ước làm việc

- Không commit trực tiếp lên main.
- Mỗi người làm trên một branch riêng, merge qua Pull Request sau khi đã kiểm tra.
