# Hướng dẫn cài đặt và chạy

Dự án là chương trình JavaFX (Maven), làm trên Eclipse. Hiện có phần **menu theo đối tượng**; phần cơ sở dữ liệu sẽ bổ sung khi bắt đầu làm các chức năng.

## 1. Phần mềm cần có

| Phần mềm | Phiên bản | Ghi chú |
|---|---|---|
| Eclipse IDE for Java Developers | 2021-09 (4.21) trở lên | Có sẵn Maven (m2e) và Git (EGit) |
| JDK | 11 trở lên | Eclipse 2021-09 tự dùng JRE 11 đi kèm; không cần cài thêm |
| Git | bất kỳ | |

Eclipse 2021-09 chưa biên dịch được Java 17 trở lên, nên dự án đặt mức **Java 11** và dùng **JavaFX 17**. Nếu nâng Eclipse lên bản mới thì có thể nâng mức Java sau.

## 2. Lấy code

**Quan trọng:** đặt thư mục dự án ở đường dẫn **không có dấu tiếng Việt và không có ký tự đặc biệt** (ví dụ `D:\Phongkham_Nhakhoakim`). Đường dẫn có dấu như `D:\Chí Hậu\...` làm Java không tìm thấy class và báo `ClassNotFoundException`.

```bash
git clone https://github.com/chau248/Phongkham_Nhakhoakim D:\Phongkham_Nhakhoakim
```

Repo có 2 thư mục: `tai_lieu` (tài liệu, thiết kế, mockup) và `chuong_trinh` (dự án Maven).

## 3. Mở trong Eclipse

1. **File > Import > Maven > Existing Maven Projects**.
2. Ở *Root Directory* chọn thư mục **`chuong_trinh`** (không chọn thư mục gốc của repo), tick `pom.xml`, bấm Finish.
3. Chờ Maven tải thư viện lần đầu (cần mạng). Mục *JRE System Library* phải là **JavaSE-11**.

## 4. Chạy

Chuột phải `src/nhakhoakim/Launcher.java` > **Run As > Java Application**.

Chạy `Launcher`, không chạy thẳng `MainApp`, nếu không sẽ báo `JavaFX runtime components are missing`.

## 5. Cấu trúc dự án

```
chuong_trinh/
├── pom.xml
└── src/nhakhoakim/
    ├── Launcher.java          chạy chương trình
    ├── MainApp.java           cửa sổ chính, menu, trang chủ
    ├── ChucNang.java          một chức năng của menu
    └── DanhSachChucNang.java  24 chức năng lõi theo nhóm đối tượng
```

- Thêm hoặc đổi chức năng: sửa `DanhSachChucNang.java`, menu tự cập nhật.
- Khi ai làm xong màn hình của một chức năng: thêm vào hàm `taoManHinh` trong `MainApp.java` (trả về giao diện theo tên chức năng); chưa làm thì hàm trả về `null` và vùng nội dung để trống.

## 6. Lỗi thường gặp

| Hiện tượng | Cách xử lý |
|---|---|
| `Could not find or load main class nhakhoakim.Launcher` | Đường dẫn dự án có dấu hoặc ký tự đặc biệt (mục 2). Chuyển sang đường dẫn không dấu rồi import lại. |
| Mục JRE là `J2SE-1.5`, file báo đỏ | Chuột phải dự án > *Maven > Update Project* (Alt+F5), tích *Force Update*. |
| `JavaFX runtime components are missing` | Chạy `Launcher`, không chạy `MainApp`. |
| Maven không tải được thư viện | Kiểm tra mạng, rồi *Maven > Update Project*. |
| Chữ tiếng Việt bị lỗi | Chuột phải dự án > *Properties > Resource*, đặt *Text file encoding* là UTF-8. |

## 7. Quy trình làm việc hằng ngày

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

Các file riêng của Eclipse (`.classpath`, `.project`, `.settings`, `target`) đã nằm trong `.gitignore`, không đưa lên Git.
