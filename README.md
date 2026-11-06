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
   - `chuong_trinh/csdl/02_seed.sql` nạp dữ liệu mẫu (13 nhân viên, 12 bệnh nhân, ca trực 15 ngày liên tục, 25 lịch hẹn, lượt khám, hóa đơn, liệu trình...). Dữ liệu ngày tháng luôn nằm quanh ngày chạy (từ 7 ngày trước đến 7 ngày sau), chạy lại file này ở ngày khác thì dữ liệu mẫu được dời theo

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
| `yta1`, `yta2`, `yta3` | Y tá | |
| `letan1` | Lễ tân | Còn nhân viên chưa có tài khoản để thử tạo ở menu Tài khoản |
| `admin` | Toàn quyền | Dùng được mọi chức năng, kể cả khám bệnh thử (không cần ca trực) |

Sai mật khẩu 5 lần liên tiếp thì tài khoản bị khóa, Quản lý mở khóa ở menu Tài khoản.

## Chức năng theo vai trò (24 chức năng)

Menu lấy từ CSDL (bảng `ChucNang` và `VaiTroChucNang`). Dưới đây là quyền mặc định.

| Menu | Chức năng | Vai trò mặc định |
| :--- | :--- | :--- |
| Bệnh nhân | Tìm kiếm bệnh nhân | Nha sĩ, Y tá, Lễ tân, Quản lý |
| | Thêm / cập nhật bệnh nhân | Lễ tân, Quản lý |
| | Xem hồ sơ bệnh án, Dòng thời gian điều trị | Nha sĩ, Y tá, Quản lý |
| Lượt khám | Khám & ghi nhận điều trị, Sơ đồ răng điều trị, Chỉ định tái khám, In đơn thuốc | Nha sĩ |
| | Liệu trình điều trị (tạo, theo dõi buổi, cập nhật giai đoạn, hoàn thành, hủy) | Nha sĩ (sửa), Y tá và Quản lý (xem) |
| Lịch hẹn | Lịch hẹn theo tuần (lưới thứ 2 - chủ nhật, sáng / chiều; xác nhận, dời, hủy, không đến) | Lễ tân, Quản lý (sửa); Nha sĩ, Y tá (xem lịch của mình) |
| | Đặt lịch hẹn | Lễ tân, Quản lý |
| Hóa đơn | Danh sách chờ thanh toán, Lập hóa đơn, Thanh toán & in hóa đơn | Lễ tân, Quản lý |
| | Báo cáo doanh thu | Quản lý |
| Dịch vụ | Xem danh mục dịch vụ & giá | Mọi vai trò |
| | Thêm / sửa dịch vụ | Quản lý |
| Nhân viên | Cập nhật hồ sơ nhân viên | Quản lý |
| | Thời khóa biểu ca trực (lưới tuần; Quản lý xếp ca, người khác xem ca của mình) | Quản lý, Nha sĩ, Y tá, Lễ tân |
| Tài khoản | Tài khoản của tôi (đổi mật khẩu) | Mọi vai trò |
| | Tạo tài khoản & gán vai trò | Quản lý |
| | Danh mục (chuyên khoa, loại dịch vụ, phòng, vật liệu, thuốc), Thông tin phòng khám & thuế | Quản lý |

Vai trò **Toàn quyền** (`admin`) luôn có đủ mọi chức năng.

## Quy tắc chính

- Ca Sáng 08:00-12:00, ca Chiều 13:00-17:00. Lịch hẹn tròn 30 phút, nằm trọn trong một ca, nha sĩ phải có ca trực và chứng chỉ hành nghề còn hạn, không trùng giờ với lịch khác của nha sĩ hay của bệnh nhân. Bắt đầu khám cũng phải đúng ca trực của nha sĩ.
- Không xóa dữ liệu nghiệp vụ, chỉ đổi trạng thái (nhân viên Nghỉ việc, dịch vụ Ngừng hoạt động, hóa đơn Đã hủy). Lượt khám Hoàn thành không sửa nữa.
- Dịch vụ tính theo răng (đơn vị tính Răng) chỉ ghi ở màn hình Sơ đồ răng; màn hình Khám chỉ thêm dịch vụ không gắn răng và hiện bảng tổng hợp để không tính tiền hai lần.
- Chuyên khoa, loại dịch vụ, phòng và vai trò là danh mục trong CSDL (chọn trong ô chọn, thêm ở Dịch vụ > Danh mục); danh mục đang được dùng thì không xóa được.
- Hóa đơn: tổng tiền = dịch vụ - giảm giá + VAT. Thuế VAT, tên, địa chỉ, số điện thoại và mã số thuế của phòng khám lấy từ **Thông tin phòng khám & thuế** (bảng `ThamSo`); đơn giá và thuế lưu cố định lúc lập. Hóa đơn in theo mẫu hóa đơn dịch vụ khám chữa bệnh (có số tiền bằng chữ, chữ ký người mua và người lập).
- Thao tác nhạy cảm (đổi giá, gán vai trò, phân quyền, hủy lịch, hủy hóa đơn, khóa tài khoản...) được ghi vào bảng `NhatKyHeThong`.

## Cấu trúc thư mục

```
chuong_trinh/
  config/        cấu hình kết nối CSDL (database.properties không commit)
  csdl/          01_schema.sql (tạo bảng, danh mục, quyền mặc định), 02_seed.sql (dữ liệu mẫu quanh ngày chạy)
  src/
    nhakhoakim/    MainApp.java: cửa sổ chính, đăng nhập, menu theo quyền trong CSDL
    entity/        lớp dữ liệu (BenhNhan, LuotKham, HoaDon...), khóa ngoại là đối tượng
    dao/           chỉ viết SQL (PreparedStatement), kết nối, sinh mã
    controller/    quy tắc nghiệp vụ, kiểm tra quyền, ghi nhật ký
    ui/            các màn hình Swing
```

Luồng gọi: màn hình (`ui`) gọi `controller`, `controller` gọi `dao`, `dao` chạy SQL. Quy tắc nghiệp vụ chỉ nằm ở `controller`.

## Quy ước làm việc

- Không commit trực tiếp lên main.
- Mỗi người làm trên một branch riêng, merge qua Pull Request sau khi đã kiểm tra.
