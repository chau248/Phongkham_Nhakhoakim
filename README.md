# Phongkham_Nhakhoakim

Chương trình quản lý hệ thống phòng khám nha khoa Kim - Môn Phát triển ứng dụng - Nhóm 09.

Viết bằng **Java Swing** + **SQL Server** (JDBC thuần), làm trên Eclipse.

## Thành viên
- Nguyễn Chí Hậu (Nhóm trưởng): menu Bệnh nhân, menu Lượt khám
- Nguyễn Phi Trường: menu Hóa đơn, menu Lịch hẹn
- Nguyễn Trần Trí Tâm: menu Dịch vụ, Nhân viên, Tài khoản

## Cấu trúc

```
chuong_trinh/                dự án Eclipse (Maven)
  src/nhakhoakim/
    MainApp.java             chạy chương trình
    entity/                  dữ liệu (mỗi bảng một lớp)
    dao/                     SQL (…DAO) và nghiệp vụ (…Service)
    ui/                      giao diện Swing (mỗi chức năng một màn hình)
  csdl/                      script SQL tạo bảng và dữ liệu mẫu
  config/                    cấu hình kết nối CSDL
tai_lieu/                    hướng dẫn, quy định, kế hoạch, thiết kế, mockup
```

Cách cài đặt và chạy: xem `tai_lieu/huong_dan/HUONG_DAN_CAI_DAT.md`.

## Quy ước
- Không commit trực tiếp lên main
- Mỗi người làm trên 1 branch feature/...
- Merge vào develop sau khi hoàn thành và đã kiểm tra
