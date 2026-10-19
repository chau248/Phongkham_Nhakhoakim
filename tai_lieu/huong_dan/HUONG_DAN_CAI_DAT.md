# Hướng dẫn cài đặt và chạy

Dự án là chương trình **Java Swing** (Maven), làm trên Eclipse, kết nối SQL Server bằng JDBC thuần. Swing có sẵn trong JDK nên không cần cài thêm thư viện giao diện.

## 1. Phần mềm cần có

| Phần mềm | Phiên bản | Ghi chú |
|---|---|---|
| Eclipse IDE for Java Developers | 2021-09 (4.21) trở lên | Có sẵn Maven (m2e) và Git (EGit) |
| JDK | 11 trở lên | Dự án đặt mức Java 11; cài JDK 11 đến 21 đều chạy được |
| SQL Server Express | 2019 trở lên | Bật Mixed Mode và TCP/IP cổng 1433 |
| Git | bất kỳ | |

## 2. Lấy code

**Quan trọng:** đặt thư mục dự án ở đường dẫn **không có dấu tiếng Việt và không có ký tự đặc biệt** (ví dụ `D:\Phongkham_Nhakhoakim`). Đường dẫn có dấu làm Java không tìm thấy class và báo `ClassNotFoundException`.

```bash
git clone https://github.com/chau248/Phongkham_Nhakhoakim D:\Phongkham_Nhakhoakim
```

## 3. Cấu trúc thư mục

```
Phongkham_Nhakhoakim/
├── README.md
├── chuong_trinh/                 dự án Eclipse (Maven)
│   ├── pom.xml
│   ├── config/                   cấu hình kết nối CSDL
│   │   ├── database.properties.example   file mẫu (có trên Git)
│   │   └── database.properties           file thật của từng máy (KHÔNG đưa lên Git)
│   ├── csdl/                     script SQL: 01_schema.sql (tạo bảng), 02_seed.sql (dữ liệu mẫu)
│   └── src/nhakhoakim/
│       ├── MainApp.java          chạy chương trình: cửa sổ, menu, trang chủ, nút Quay lại
│       ├── entity/               dữ liệu: mỗi bảng một lớp (BenhNhan, LuotKham...)
│       ├── dao/                  truy cập dữ liệu: lớp ...DAO viết SQL, lớp ...Service chứa nghiệp vụ
│       └── ui/                   giao diện Swing: mỗi chức năng một lớp ManHinh...
└── tai_lieu/
    ├── huong_dan/                hướng dẫn cài đặt, hướng dẫn làm việc
    ├── quy_dinh/                 quy ước viết code, ràng buộc nghiệp vụ
    ├── ke_hoach/                 bảng phân công
    ├── thiet_ke/                 tài liệu thiết kế, menu, luồng màn hình
    └── mockup/                   bản vẽ giao diện
```

Chương trình chia **3 lớp**, mỗi lớp một package:

| Package | Việc của lớp | Không được làm |
|---|---|---|
| `entity` | Giữ dữ liệu (thuộc tính, get/set) | Không gọi CSDL, không có giao diện |
| `dao` | Viết SQL (`...DAO`) và kiểm tra nghiệp vụ (`...Service`) | Không có giao diện |
| `ui` | Màn hình Swing, chỉ gọi `...Service` | Không viết SQL, không kiểm tra nghiệp vụ |

## 4. Mở trong Eclipse

1. **File > Import > Maven > Existing Maven Projects**.
2. Ở *Root Directory* chọn thư mục **`chuong_trinh`** (không chọn thư mục gốc của repo), tick `pom.xml`, bấm Finish.
3. Chờ Maven tải driver SQL Server lần đầu (cần mạng). Mục *JRE System Library* phải là **JavaSE-11** trở lên.

## 5. Tạo cơ sở dữ liệu và cấu hình kết nối

1. Trong SSMS chạy lần lượt `chuong_trinh/csdl/01_schema.sql` rồi `02_seed.sql` (chạy lại nhiều lần vẫn an toàn).
2. Chép `chuong_trinh/config/database.properties.example` thành `database.properties` (cùng thư mục), sửa `db.user` và `db.password` cho đúng login SQL Server của máy bạn.
3. Dòng `dev.maNhanVien` chọn người đang dùng chương trình (khi chưa có màn hình đăng nhập): `NV000002` nha sĩ, `NV000004` y tá, `NV000006` lễ tân, `NV000001` quản lý.

## 6. Chạy

Chuột phải `src/nhakhoakim/MainApp.java` > **Run As > Java Application**.

Chương trình dùng Swing nên **chạy thẳng `MainApp`**, không cần lớp trung gian hay cấu hình JavaFX.

## 7. Lỗi thường gặp

| Hiện tượng | Cách xử lý |
|---|---|
| `Could not find or load main class nhakhoakim.MainApp` | Đường dẫn dự án có dấu hoặc ký tự đặc biệt (mục 2). Chuyển sang đường dẫn không dấu rồi import lại. |
| Mục JRE là `J2SE-1.5`, file báo đỏ | Chuột phải dự án > *Maven > Update Project* (Alt+F5), tích *Force Update*. |
| `Không kết nối được CSDL: Login failed` | Sai `db.user` hoặc `db.password` trong `database.properties`. |
| `Connection refused` | SQL Server chưa chạy. Bật dịch vụ **SQL Server (SQLEXPRESS)** trong Services. |
| `Chưa có file config/database.properties` | Làm bước 2 của mục 5. |
| Chữ tiếng Việt bị lỗi | Chuột phải dự án > *Properties > Resource*, đặt *Text file encoding* là UTF-8. |

## 8. Quy trình làm việc hằng ngày

Mỗi người làm trên một nhánh riêng.

```bash
git checkout develop
git pull
git checkout -b feature/ten_viec_ngan        # ví dụ feature/dat_lich_hen
# ... code, chạy thử ...
git add <các file đã sửa>
git commit -m "feat: mo ta ngan viec da lam"
git push -u origin feature/ten_viec_ngan
```

Sau đó mở **Pull Request vào `develop`** trên GitHub, nhờ người review theo vòng: Hậu duyệt Tâm, Tâm duyệt Trường, Trường duyệt Hậu. Không commit trực tiếp lên `main`.

Các file riêng của Eclipse (`.classpath`, `.project`, `.settings`, `target`) và `database.properties` đã nằm trong `.gitignore`, không đưa lên Git.
