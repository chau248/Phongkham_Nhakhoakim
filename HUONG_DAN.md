# Hướng dẫn học và chạy chương trình - Nha khoa Kim (Nhóm 09)

Tài liệu này dành cho ba thành viên: **Hậu**, **Trường**, **Tâm**. Mỗi người cần (1) chạy được chương trình, (2) hiểu phần của mình đến mức tự giải thích được từng dòng khi bảo vệ, (3) hiểu sơ qua phần của hai bạn còn lại và chỗ ba phần nối với nhau.

Cách học đề nghị: đọc Phần A trước (cả nhóm), sau đó đọc phần của mình (B, C hoặc D) theo thứ tự, vừa đọc vừa mở code, vừa chạy thử. Cuối cùng đọc Phần E (nối ghép) và Phần F (kịch bản demo, câu hỏi bảo vệ).

Mục lục: [A. Chạy chương trình](#a-chạy-chương-trình) | [B. Kiến thức chung](#b-kiến-thức-chung-cả-nhóm-phải-nắm) | [C. Phần của Hậu](#c-phần-của-hậu-bệnh-nhân-và-lượt-khám) | [D. Phần của Trường](#d-phần-của-trường-lịch-hẹn-và-hóa-đơn) | [E. Phần của Tâm](#e-phần-của-tâm-dịch-vụ-nhân-viên-tài-khoản-đăng-nhập) | [F. Nối ghép](#f-ba-phần-nối-với-nhau-thế-nào) | [G. Kịch bản demo](#g-kịch-bản-demo-cả-nhóm) | [H. Câu hỏi bảo vệ](#h-câu-hỏi-bảo-vệ-thường-gặp) | [I. Lỗi thường gặp](#i-lỗi-thường-gặp-khi-chạy)

---

## A. Chạy chương trình

### A1. Cần có

| Thứ cần | Ghi chú |
| :--- | :--- |
| JDK 11 trở lên | Máy nhóm dùng JDK 21. Kiểm tra: `java -version` |
| SQL Server (bản Express được) | Bật đăng nhập SQL Server (Mixed Mode) và TCP/IP cổng 1433 |
| Eclipse (hoặc Maven) | Thư viện `mssql-jdbc` do Maven tự tải theo `chuong_trinh/pom.xml` |
| Git | Lấy nhánh `hoanthien` |

### A2. Lấy code

```bash
git clone https://github.com/chau248/Phongkham_Nhakhoakim.git
cd Phongkham_Nhakhoakim
git checkout hoanthien
```

(Nếu nhánh `hoanthien` chưa được push thì lấy thư mục từ máy của Hậu.)

### A3. Tạo cơ sở dữ liệu

Chạy hai file theo thứ tự, trong SQL Server Management Studio hoặc bằng `sqlcmd` (chạy lại nhiều lần không bị trùng dữ liệu):

```bash
sqlcmd -S "localhost,1433" -E -C -I -f 65001 -i "chuong_trinh/csdl/01_schema.sql"
sqlcmd -S "localhost,1433" -E -C -I -f 65001 -i "chuong_trinh/csdl/02_seed.sql"
```

- `01_schema.sql`: tạo CSDL `NhaKhoaKim` và 15 bảng.
- `02_seed.sql`: nạp dữ liệu mẫu (12 nhân viên, 12 bệnh nhân, 12 lịch hẹn, 12 lượt khám, 10 hóa đơn, 12 dịch vụ, ca trực 7 ngày...). Ngày trong dữ liệu mẫu tính theo ngày chạy file này, nên lịch hẹn "hôm nay" là hôm chạy seed.

**Muốn làm lại từ đầu** (xóa sạch những gì đã thử khi demo): đóng chương trình, rồi chạy:

```bash
sqlcmd -S "localhost,1433" -E -C -Q "ALTER DATABASE NhaKhoaKim SET SINGLE_USER WITH ROLLBACK IMMEDIATE; DROP DATABASE NhaKhoaKim;"
```

Sau đó chạy lại hai file ở trên. Chỉ làm trên máy của mình, đừng chạy lên máy người khác.

### A4. Cấu hình kết nối

Sao chép `chuong_trinh/config/database.properties.example` thành `chuong_trinh/config/database.properties` (file này nằm trong `.gitignore`, không commit) rồi điền:

```
db.host=localhost
db.port=1433
db.name=NhaKhoaKim
db.user=<tên đăng nhập SQL Server của bạn>
db.password=<mật khẩu>
```

Tài khoản SQL Server này cần quyền đọc ghi CSDL `NhaKhoaKim`. Lưu ý: **Java không dùng được đăng nhập Windows**, nên bắt buộc phải có một login SQL Server (user/mật khẩu). Chưa có thì tạo bằng quyền Windows của bạn trong SSMS: Security, Logins, New Login, chọn SQL Server authentication, rồi ở User Mapping tích `NhaKhoaKim` và `db_owner`.

### A5. Chạy

**Eclipse:** File, Import, Maven, Existing Maven Projects, chọn thư mục `chuong_trinh`. Chuột phải lớp `nhakhoakim.MainApp`, chọn Run As, Java Application. Thư mục làm việc phải là `chuong_trinh` (chương trình tìm `config/database.properties` theo đường dẫn tương đối).

**Dòng lệnh:**

```bash
cd chuong_trinh
mvn -q compile exec:java -Dexec.mainClass=nhakhoakim.MainApp
```

Hộp đăng nhập hiện ra đầu tiên.

### A6. Tài khoản để thử

Mật khẩu của tất cả là `123456`.

| Tên đăng nhập | Là ai | Vai trò | Dùng để thử phần của |
| :--- | :--- | :--- | :--- |
| `quanly` | NV000001 Lê Quốc Bảo | Quản lý | Tâm (và xem tất cả trừ Lượt khám) |
| `nhasi1` | NV000002 Phạm Minh Khoa | Nha sĩ | Hậu |
| `nhasi2` | NV000003 Đỗ Thu Hà | Nha sĩ | Hậu (đang có lượt khám dở LK000012) |
| `nhasi3`, `nhasi4` | NV000007 Trần Quang Vinh, NV000008 Nguyễn Thị Hồng Nhung | Nha sĩ | Hậu |
| `nhasi5` | NV000012 Phan Đức Thịnh | Nha sĩ đã nghỉ việc | Thử bị khóa |
| `yta1` đến `yta4` | NV000004, 005, 009, 011 | Y tá | Hậu (chỉ xem và cập nhật tiền sử) |
| `letan1`, `letan2` | NV000006 Ngô Mỹ Linh, NV000010 Tạ Bảo Châu | Lễ tân | Trường |

Dữ liệu mẫu đáng chú ý:
- Lịch hẹn: LH000001 (BN000001, nhasi1, 09:00), LH000003 (BN000004, nhasi2, 08:30), LH000004 (BN000005, nhasi2, 14:00), LH000006 (BN000009, nhasi1, 15:00) đã xác nhận; LH000002, LH000005 (09:30 hôm nay), LH000007 (14:00 ngày mai), LH000009 (09:00 ngày kia) đang chờ xác nhận.
- Chờ thanh toán: LK000001 và LK000005 đã hoàn thành nhưng chưa có hóa đơn.
- Hóa đơn HD000001 đến HD000010 đã có sẵn.
- Một lượt khám đang khám: LK000012.

---

## B. Kiến thức chung cả nhóm phải nắm

### B1. Chương trình chia 4 lớp

```
ui (màn hình Swing)  ->  controller (quy tắc nghiệp vụ)  ->  dao (chỉ viết SQL)  ->  SQL Server
                                  |
                              entity (lớp dữ liệu: BenhNhan, LichHen, HoaDon...)
```

| Lớp | Việc duy nhất của nó | Không được làm |
| :--- | :--- | :--- |
| `entity` | Chứa dữ liệu (thuộc tính, getter/setter), vài hàm tính nhỏ như `tinhTuoi()` | Không gọi CSDL |
| `dao` | Viết SQL bằng `PreparedStatement`, đọc ra entity | Không kiểm tra quy tắc nghiệp vụ |
| `controller` | Kiểm tra quyền, kiểm tra dữ liệu, áp dụng quy tắc, ghi nhật ký, gọi DAO | Không có mã giao diện |
| `ui` | Vẽ màn hình, bắt sự kiện bấm nút, gọi controller, hiện lỗi | Không viết SQL, không tự kiểm tra quy tắc |

Câu hay bị hỏi: "Vì sao quy tắc nằm ở controller?" Trả lời: vì một quy tắc (ví dụ "không đặt lịch ở quá khứ") phải đúng dù gọi từ màn hình nào, và kiểm thử được mà không cần mở giao diện hay CSDL.

### B2. Đi theo một chức năng từ trên xuống dưới (ví dụ: Đặt lịch hẹn)

1. `MainApp` tạo menu từ `DanhSachChucNang`, người dùng bấm "Đặt lịch hẹn", `taoManHinh` dựng `ManHinhDatLichHen`.
2. Người dùng bấm nút "Đặt lịch" trong `ManHinhDatLichHen` gọi `lichHenController.datLich(...)`.
3. `LichHenController.datLich` kiểm tra quyền (`Quyen.yeuCau`), kiểm tra dữ liệu và quy tắc, rồi gọi `lichHenDAO.them(...)`.
4. `LichHenDAO.them` chạy `INSERT INTO dbo.LichHen ...`.
5. Lỗi nghiệp vụ là `NghiepVuException` (có câu tiếng Việt), lỗi CSDL là `LoiCSDL`. Màn hình bắt cả hai bằng `catch (RuntimeException e)` rồi gọi `TienIch.baoLoi(e)` để hiện hộp thông báo.

Khi đọc code chức năng nào, cứ lần theo đúng 5 bước này.

### B3. Các lớp dùng chung (đọc kỹ, vì cả ba phần đều dùng)

| Lớp | Làm gì |
| :--- | :--- |
| `MainApp` | Cửa sổ chính. `main` gọi `batDau()` hiện hộp đăng nhập rồi `hienCuaSo()`. `taoMenu()` chỉ hiện chức năng đúng vai trò (`duocDung`). `dangKyManHinh()` gắn tên chức năng với màn hình. `doiNoiDung`/`thayNoiDung` đổi màn hình. Giao diện `LamMoiDuoc` cho màn hình tự tải lại khi quay lại. |
| `ui/DanhSachChucNang`, `ChucNang` | Danh sách 25 chức năng: menu nào, vai trò nào được dùng |
| `ui/TienIch` | Hàm dựng giao diện dùng chung: nhãn, nút, bảng (`taoBang`, `napBang`), form, hộp thông báo, ô chọn bệnh nhân (`ChonBenhNhan`), định dạng ngày giờ tiền |
| `dao/PhienLamViec` | Giữ người đang đăng nhập (một biến tĩnh). Chưa đăng nhập thì là `null` |
| `controller/Quyen` | `Quyen.yeuCau(nguoiDung, "tên việc", "Quản lý", "Lễ tân")` trả về người đăng nhập nếu đúng vai trò, nếu sai thì ném lỗi nêu rõ ai được làm |
| `controller/NhatKyController` | `ghiNhatKy(...)` ghi vào bảng `NhatKyHeThong` mỗi thao tác nhạy cảm (đổi giá, sửa dị ứng, hủy lịch, hủy hóa đơn, khóa tài khoản...) |
| `dao/DatabaseConnection` | Đọc `config/database.properties`, mở kết nối. Mọi DAO gọi `DatabaseConnection.getConnection()` trong `try-with-resources` |
| `dao/MaGenerator` | `taoMa("LH")` ra `LH000013`: tăng bộ đếm trong bảng `BoDemMa` rồi ghép tiền tố với 6 chữ số |
| `dao/NghiepVuException`, `LoiCSDL` | Hai loại lỗi của chương trình |
| `entity/HangSo` | Hằng số chuỗi dùng chung: tên vai trò, trạng thái (`"Đang khám"`, `"Hoàn thành"`...), tình trạng răng |

### B4. Cách viết controller để kiểm thử được

Mỗi controller có **hai constructor**: một dùng DAO thật và người đăng nhập thật (`PhienLamViec::getNhanVienHienTai`), một nhận DAO và `Supplier<NhanVien>` truyền vào. Nhờ vậy nhóm có thể chạy toàn bộ quy tắc nghiệp vụ bằng DAO giả trong bộ nhớ, không cần SQL Server. Nếu thầy hỏi "kiểm thử thế nào?" thì đây là câu trả lời.

### B5. Cơ sở dữ liệu: 15 bảng

| Nhóm | Bảng | Người phụ trách chính |
| :--- | :--- | :--- |
| Chung | `BoDemMa` (bộ đếm sinh mã), `NhatKyHeThong` | Cả nhóm |
| Tâm | `NhanVien`, `TaiKhoan`, `DichVu`, `CaTruc` | Tâm |
| Hậu | `BenhNhan`, `HoSoBenhAn`, `LieuTrinh`, `LuotKham`, `RangDieuTri`, `LuotKhamDichVu` | Hậu |
| Trường | `LichHen`, `HoaDon`, `ChiTietHoaDon` | Trường |

Các khóa ngoại quan trọng: `HoSoBenhAn.maBN` (mỗi bệnh nhân một hồ sơ), `LuotKham.maHoSo / maNhanVienPhuTrach / maLichHen / maHoaDon`, `LichHen.maBN / maNhanVien / maDichVu`, `HoaDon.maBN`, `ChiTietHoaDon.maHoaDon / maDichVu`, `TaiKhoan.maNV` (mỗi nhân viên một tài khoản).

Xem dữ liệu nhanh bằng `sqlcmd`:

```bash
sqlcmd -S "localhost,1433" -E -C -d NhaKhoaKim -Q "SELECT TOP 10 * FROM dbo.NhatKyHeThong"
```

### B6. Quyền theo vai trò (nhìn bằng mắt khi demo)

| Vai trò | Thấy gì |
| :--- | :--- |
| Quản lý | Mọi thứ trừ menu Lượt khám |
| Nha sĩ | Bệnh nhân (tìm, xem hồ sơ, liệu trình, dòng thời gian, tiền sử và dị ứng), Lượt khám (5 chức năng), Xem lịch làm việc, Xem dịch vụ, Tài khoản của tôi |
| Y tá | Tìm kiếm, hồ sơ, liệu trình, dòng thời gian, cập nhật tiền sử và dị ứng, Xem lịch làm việc, Xem dịch vụ, Tài khoản của tôi |
| Lễ tân | Bệnh nhân (tìm, thêm/sửa), Lịch hẹn (4 chức năng), Hóa đơn (3 chức năng), Xem dịch vụ, Tài khoản của tôi |

Hai lớp bảo vệ: menu ẩn chức năng không được dùng (`MainApp.duocDung`) **và** controller vẫn kiểm tra lại (`Quyen.yeuCau`). Phải nhớ cả hai, vì ẩn menu chưa đủ an toàn.

---

## C. Phần của Hậu: Bệnh nhân và Lượt khám

11 chức năng. Vai trò chính: Nha sĩ, Y tá (và Lễ tân cho thêm/sửa bệnh nhân).

### C1. Bản đồ file

| Lớp | File | Việc |
| :--- | :--- | :--- |
| entity | `BenhNhan`, `HoSoBenhAn`, `LieuTrinh`, `LuotKham`, `LuotKhamDichVu`, `RangDieuTri`, `DongThoiGian`, `GoiYDieuTri` | Dữ liệu. `HoSoBenhAn` chứa luôn quy tắc chặn thuốc cùng nhóm dị ứng (BN-16) |
| dao | `BenhNhanDAO`, `HoSoBenhAnDAO`, `LieuTrinhDAO`, `LuotKhamDAO`, `LuotKhamDichVuDAO`, `RangDieuTriDAO`, `NhaSiPhuTrachDAO` | SQL |
| controller | `BenhNhanController`, `HoSoBenhAnController`, `LuotKhamController` (lớn nhất), `RangDieuTriController`, `GoiYDieuTriController`, `DongThoiGianController`, `NhaSiPhuTrachController` | Quy tắc |
| ui | `ManHinhTraCuuBenhNhan`, `ManHinhHoSoBenhNhan`, `ManHinhHoSoBenhAn`, `ManHinhDongThoiGian`, `ManHinhKhamDieuTri`, `ManHinhSoDoRang`, `ManHinhChiDinhTaiKham`, `ManHinhGoiYDieuTri`, `HopThoaiChonDichVu` | Màn hình |

### C2. Chức năng → màn hình → hàm chính

| Chức năng | Màn hình | Hàm controller cần đọc |
| :--- | :--- | :--- |
| Tìm kiếm bệnh nhân | `ManHinhTraCuuBenhNhan` | `BenhNhanController.timKiem`, `doiTrangThai` (ngừng theo dõi/mở lại) |
| Thêm / cập nhật hồ sơ bệnh nhân | `ManHinhHoSoBenhNhan` | `BenhNhanController.kiemTra`, `them` (kèm hồ sơ rỗng), `capNhat` |
| Xem hồ sơ bệnh án | `ManHinhHoSoBenhAn` | `HoSoBenhAnController.timTheoBenhNhan`, `layLichSuKham` |
| Xem liệu trình đang thực hiện | `ManHinhHoSoBenhAn` | `HoSoBenhAnController.layLieuTrinhDangThucHien` |
| Cập nhật tiền sử & dị ứng | `ManHinhHoSoBenhAn` | `HoSoBenhAnController.capNhatThongTinYTe` |
| Dòng thời gian điều trị | `ManHinhDongThoiGian` | `DongThoiGianController.xayDung` |
| Khám & ghi nhận điều trị | `ManHinhKhamDieuTri` | `LuotKhamController.batDauKham...`, `luuNoiDungKham`, `themDichVu`, `hoanTatKham`, `huyLuotKham` |
| Kê đơn thuốc | `ManHinhKhamDieuTri` | `LuotKhamController.keDonThuoc` |
| Sơ đồ răng | `ManHinhSoDoRang` | `RangDieuTriController.ghiNhanRang`, `layTinhTrangMoiNhat`, `canhBaoThieuDichVu` |
| Chỉ định tái khám | `ManHinhChiDinhTaiKham` | `LuotKhamController.datTaiKham` → `LichHenController.taoLichTaiKham` |
| Gợi ý điều trị | `ManHinhGoiYDieuTri` | `GoiYDieuTriController.goiY`, `uocTinhChiPhi`, `themNhieuVaoLuotKham` |

Lưu ý: ba chức năng hồ sơ dùng chung một màn hình, hai chức năng khám và kê đơn dùng chung một màn hình. Trong `MainApp.dangKyManHinh()` bạn sẽ thấy nhiều tên chức năng cùng trỏ về một màn hình.

### C3. Quy tắc phải thuộc

**Bệnh nhân** (`BenhNhanController.kiemTra`, BN-01 đến BN-05, BN-12 đến BN-18):
- Họ tên 2 đến 100 ký tự, chỉ chữ cái, khoảng trắng, dấu chấm, nháy, gạch nối. Ngày sinh bắt buộc, không sau hôm nay, không quá 120 tuổi.
- Số điện thoại đúng 10 số bắt đầu bằng 0; số căn cước đúng 12 số; email đúng định dạng.
- **Dưới 18 tuổi** bắt buộc có người liên hệ khẩn cấp (tên và số điện thoại), và số điện thoại này không được trùng số của bệnh nhân.
- Trùng họ tên và ngày sinh bị chặn kèm câu "có thể đã đăng ký rồi" (`kiemTraTrungNguoi`); số căn cước không được trùng (chỉ mục duy nhất `UX_BenhNhan_soCCCD`).
- Thêm bệnh nhân là thêm luôn **hồ sơ bệnh án rỗng trong cùng một transaction** (BN-04): hoặc cả hai được lưu, hoặc không cái nào.
- Ngừng theo dõi: chỉ Lễ tân và Quản lý; không ngừng khi còn lượt khám đang dở; có ghi nhật ký.

**Hồ sơ bệnh án** (`HoSoBenhAnController`, `HoSoBenhAn`):
- Mỗi bệnh nhân đúng một hồ sơ. Đóng/mở hồ sơ chỉ Quản lý.
- Sửa dị ứng được chuẩn hóa và **mỗi giá trị đổi thật sự đều ghi nhật ký** (thông tin nhạy cảm).
- Các giá trị như "không", "ko", "chưa rõ", "n/a" được hiểu là "không dị ứng", không bị coi là tên chất gây dị ứng.
- **Chặn thuốc cùng nhóm (BN-16):** bảng `NHOM_THUOC` trong `HoSoBenhAn` có 4 nhóm (penicillin, cephalosporin, nsaid, sulfa). Dị ứng amoxicillin thì không kê được augmentin, vì cùng nhóm penicillin.
- Cảnh báo (không chặn) khi dịch vụ cần liệu trình mà bệnh nhân chưa có (`canhBaoLieuTrinh`); cảnh báo y tế với thủ thuật chảy máu/xâm lấn (`canhBaoYTe`).

**Lượt khám** (`LuotKhamController`, LK-01 đến LK-21):
- Có 3 cách bắt đầu: theo hồ sơ, theo bệnh nhân (khám vãng lai), theo lịch hẹn. Khám vãng lai tự tìm lịch hẹn Đã xác nhận hôm nay của bệnh nhân để gắn vào, giúp lịch chuyển sang Hoàn thành khi khám xong.
- Khám theo lịch: lịch phải Đã xác nhận, đúng ngày hôm nay, đúng nha sĩ.
- `layDangKham` cho màn hình mở lại lượt khám đang dở của nha sĩ (khi quay lại màn hình thì tiếp tục lượt đó, không tạo lượt mới).
- **Hoàn tất khám** (`hoanTatKham`) cần có chẩn đoán và ít nhất một dịch vụ đã thực hiện. Sau đó lượt khám bị khóa và lịch hẹn chuyển Hoàn thành.
- Kê đơn: thuốc trùng nhóm dị ứng thì chặn và không lưu gì.
- Dịch vụ gắn răng tính theo cặp (răng, dịch vụ): trám nhiều mặt của cùng một răng chỉ tính một lần.
- Tái khám: ngày phải sau ngày khám và sau hôm nay, tối đa 6 tháng, mỗi lượt khám một lịch. Controller **tạo lịch hẹn trước**, lỗi thì không lưu ngày tái khám.
- Hủy lượt khám (chọn nhầm bệnh nhân, bệnh nhân bỏ về) phải có lý do và chỉ khi chưa ghi dịch vụ hay răng nào; nếu có lịch tái khám đã đặt thì hủy luôn; lịch hẹn gốc trở lại Đã xác nhận.

**Gợi ý điều trị** (`GoiYDieuTriController`): răng sâu thì gợi ý trám; sâu từ 2 lượt khám thì gợi ý điều trị tủy; răng đã nhổ chưa phục hình thì gợi ý Implant/cầu răng (không áp dụng cho răng sữa, răng số 8, hoặc bệnh nhân dưới 18); quá 6 tháng chưa lấy cao răng thì gợi ý lấy cao răng. Chỉ là gợi ý, nha sĩ quyết định.

### C4. Thứ tự đọc code (khoảng 3 buổi)

1. `entity/BenhNhan` → `dao/BenhNhanDAO` → `controller/BenhNhanController` → `ui/ManHinhTraCuuBenhNhan` và `ManHinhHoSoBenhNhan`. Đây là chức năng đơn giản nhất, dùng nó để hiểu mẫu 4 lớp.
2. `entity/HoSoBenhAn` (đọc kỹ `NHOM_THUOC`) → `HoSoBenhAnController` → `ManHinhHoSoBenhAn`.
3. `LuotKhamController`: đọc theo vòng đời `batDauKham` → `luuNoiDungKham` → `themDichVu` → `hoanTatKham` → `datTaiKham`. Sau đó `RangDieuTriController`, `GoiYDieuTriController`, `DongThoiGianController`.

### C5. Thực hành (đăng nhập `nhasi1`, và `letan1` cho bước 1)

1. `letan1`: thêm bệnh nhân 10 tuổi, không điền người liên hệ. Phải báo lỗi. Điền người liên hệ trùng số của bệnh nhân. Phải báo lỗi tiếp. Điền đúng thì lưu được. Kiểm tra trong DB có thêm 1 dòng `BenhNhan` và 1 dòng `HoSoBenhAn`.
2. `nhasi1`: cập nhật dị ứng của một bệnh nhân thành "amoxicillin", sau đó vào Kê đơn thuốc ghi "Augmentin 625mg". Phải bị chặn.
3. Khám theo lịch: mở "Khám & ghi nhận điều trị", chọn lịch LH000001 (BN000001, 09:00). Ghi chẩn đoán, thêm dịch vụ, bấm hoàn tất. Thử hoàn tất khi chưa có chẩn đoán để thấy lỗi.
4. Mở "Sơ đồ răng", bấm một răng, chọn tình trạng "Sâu" rồi lưu. Xem cảnh báo thiếu dịch vụ nếu có.
5. "Gợi ý điều trị": xem danh sách gợi ý, tích chọn, xem chi phí tạm tính đã gồm VAT 8%.
6. "Chỉ định tái khám": chọn ngày mai, dịch vụ khám. Sau đó đăng nhập `letan1` mở "Xác nhận lịch hẹn" thấy lịch tái khám đang chờ.
7. Chạy câu SQL xem `LuotKham` vừa hoàn thành có `maHoaDon` rỗng, rồi sang phần của Trường để lập hóa đơn.

---

## D. Phần của Trường: Lịch hẹn và Hóa đơn

8 chức năng: 5 chức năng lịch hẹn (gồm Xem lịch làm việc) và 3 chức năng hóa đơn. Vai trò chính: Lễ tân, Quản lý.

### D1. Bản đồ file

| Lớp | File | Việc |
| :--- | :--- | :--- |
| entity | `LichHen`, `DongLichLamViec`, `HoaDon`, `ChiTietHoaDon` | `HoaDon` chứa danh sách `dsChiTiet` |
| dao | `LichHenDAO`, `HoaDonDAO`, `ChiTietHoaDonDAO` | SQL. `ChiTietHoaDonDAO` chỉ được gọi từ `HoaDonDAO` để chung một transaction |
| controller | `LichHenController`, `HoaDonController`, `ChiTietHoaDonController` | Quy tắc |
| ui | `ManHinhXemLichHen`, `ManHinhDatLichHen`, `ManHinhDoiHuyLichHen`, `ManHinhXacNhanLichHen`, `ManHinhLichLamViec`, `ManHinhChoThanhToan`, `ManHinhLapHoaDon`, `ManHinhThanhToan`, `HopThoaiInHoaDon`, `BangTen` | Màn hình. `BangTen` là bảng tra mã → tên để khỏi hỏi CSDL từng dòng |

### D2. Chức năng → màn hình → hàm chính

| Chức năng | Màn hình | Hàm controller cần đọc |
| :--- | :--- | :--- |
| Xem lịch hẹn (LH-01) | `ManHinhXemLichHen` | `layTheoKhoang`, `layKhungGioTrong` |
| Đặt lịch hẹn (LH-02..04) | `ManHinhDatLichHen` | `layNhaSiCoCaTruc`, `layKhungGioTrong`, `datLich` |
| Dời / hủy lịch hẹn (LH-05, 06) | `ManHinhDoiHuyLichHen` | `doiLich`, `huyLich` |
| Xác nhận lịch hẹn (LH-07) | `ManHinhXacNhanLichHen` | `xacNhan`, `danhDauKhongDen` |
| Xem lịch làm việc (LH-08) | `ManHinhLichLamViec` | `layLichLamViec` |
| Danh sách chờ thanh toán (HD-01) | `ManHinhChoThanhToan` | `HoaDonController.layChoThanhToan` |
| Lập hóa đơn (HD-02..04) | `ManHinhLapHoaDon` | `xemTruoc`, `lapHoaDon`, `tinhTongTien` |
| Thanh toán & in hóa đơn (HD-05, 06) | `ManHinhThanhToan`, `HopThoaiInHoaDon` | `thanhToan`, `huyHoaDon`, `taoNoiDungIn` |

### D3. Quy tắc lịch hẹn

Mọi quy tắc nằm trong `LichHenController`. `datLich` và `doiLich` dùng chung một hàm kiểm tra, nên luật như nhau:

- Hai ca: **Sáng 08:00-12:00, Chiều 13:00-17:00**. Giờ hẹn là bội số của 30 phút, bắt đầu trong ca (`caCuaGio` trả về Sáng, Chiều hoặc null).
- Lịch phải **vừa khít trong một ca** (thời lượng lấy từ dịch vụ): dịch vụ 90 phút không đặt được lúc 11:30.
- Nha sĩ phải **đang làm việc, có ca trực "Có mặt"** đúng ngày và ca đó, và **chứng chỉ hành nghề còn hạn** tại ngày hẹn.
- **Không chồng giờ** với lịch còn hiệu lực của cùng nha sĩ (lịch đã hủy/không đến không tính).
- **Không đặt lịch trong quá khứ.** Bệnh nhân phải đang điều trị.
- Đặt xong lịch ở trạng thái **Chờ xác nhận**; xác nhận (LH-07) kiểm tra lại trùng giờ, ca trực, chứng chỉ vì có thể đã đổi từ lúc đặt.
- **Dời lịch** giữ nguyên trạng thái.
- **Hủy** bắt buộc có lý do, chỉ lịch Chờ xác nhận hoặc Đã xác nhận, không hủy lịch đang có lượt khám Đang khám. Có ghi nhật ký.
- "Không đến": chỉ cho lịch Đã xác nhận đã qua giờ hẹn mà chưa có lượt khám.
- **Tái khám tự chọn khung giờ:** `taoLichTaiKham` (Hậu gọi) tự lấy khung trống đầu tiên của nha sĩ trong ngày đó, vẫn đi qua đủ ràng buộc ở trên.
- **Xem lịch làm việc:** nha sĩ thấy ca và lịch hẹn của mình; y tá thấy ca của mình kèm lịch hẹn của nha sĩ cùng phòng và cùng ca, có tên nha sĩ phụ trách.

Vòng đời trạng thái lịch hẹn: `Chờ xác nhận` → `Đã xác nhận` → `Hoàn thành` (khi nha sĩ hoàn tất khám), hoặc `Đã hủy`, hoặc `Không đến`.

### D4. Quy tắc hóa đơn

Trong `HoaDonController`:

- Chỉ lượt khám **Hoàn thành và chưa có hóa đơn** mới lập được hóa đơn.
- Các dòng hóa đơn **lấy từ dịch vụ đã thực hiện** của lượt khám, đơn giá lấy theo **giá hiện tại** của dịch vụ và **lưu cố định** vào `ChiTietHoaDon` (đổi giá sau này không làm đổi hóa đơn cũ).
- Công thức (`tinhTongTien`): `tổng = (tổng dịch vụ - giảm giá) x (1 + 8%)`, làm tròn đến đồng. Giảm giá không được vượt tổng dịch vụ. Ví dụ HD000005: dịch vụ 2.500.000, giảm 250.000, trước thuế 2.250.000, tổng 2.430.000.
- `lapHoaDon` chạy **một transaction** gồm: thêm hóa đơn, thêm các dòng chi tiết, và `UPDATE LuotKham SET maHoaDon = ... WHERE maHoaDon IS NULL` (điều kiện này chống lập hai hóa đơn cho cùng một lượt khám).
- Thanh toán: 4 hình thức (`Tiền mặt`, `Chuyển khoản`, `Thẻ`, `Ví điện tử`, hằng `HINH_THUC`). Đã thanh toán thì hóa đơn bị **khóa**.
- **Hủy hóa đơn:** chỉ Quản lý, bắt buộc có lý do, chỉ hóa đơn Chưa thanh toán. Hủy xong, lượt khám **quay lại danh sách chờ thanh toán** (`HoaDonDAO.huy` gỡ `maHoaDon` khỏi lượt khám).
- **In:** chỉ in hóa đơn Đã thanh toán (hóa đơn Đã hủy hoặc chưa trả thì không in). `taoNoiDungIn` dựng nội dung chữ; `HopThoaiInHoaDon` hiện xem trước và nút In dùng máy in của hệ thống.

### D5. Thứ tự đọc code (khoảng 3 buổi)

1. `entity/LichHen` → `LichHenDAO` (đọc các hằng `SQL_*`) → `LichHenController.datLich` và hàm kiểm tra dùng chung → `ManHinhDatLichHen`.
2. `layKhungGioTrong` (cách tính các giờ còn trống) → `doiLich`, `huyLich`, `xacNhan` → `layLichLamViec`.
3. `HoaDonController.xemTruoc` → `lapHoaDon` → `HoaDonDAO.themKemChiTiet` (transaction) → `thanhToan`, `huyHoaDon`, `taoNoiDungIn` → ba màn hình hóa đơn.

### D6. Thực hành (đăng nhập `letan1`)

1. Đặt lịch lúc 11:45 cho dịch vụ 60 phút. Phải báo không vừa trong ca. Đặt lúc 10:00 một ngày trong 7 ngày tới mà nha sĩ có ca trực thì được.
2. Đặt hai lịch trùng giờ cùng nha sĩ. Lịch thứ hai phải bị từ chối. Chọn ngày mà nha sĩ không có ca thì không thấy nha sĩ đó trong danh sách.
3. Dời lịch sang giờ khác, kiểm tra trạng thái vẫn như cũ. Hủy lịch không nhập lý do phải bị chặn; nhập lý do thì xem dòng mới trong `NhatKyHeThong`.
4. "Xác nhận lịch hẹn": xác nhận LH000007 và lịch tái khám do nha sĩ tạo ở bước thực hành của Hậu.
5. "Danh sách chờ thanh toán": chọn LK000001, lập hóa đơn với giảm giá 50.000, kiểm tra tổng tiền bằng máy tính tay.
6. "Thanh toán & in": chọn hóa đơn vừa lập, thử in trước khi trả (phải bị chặn), trả bằng Tiền mặt rồi in.
7. Đăng nhập `quanly`, hủy một hóa đơn chưa thanh toán (lập một hóa đơn mới để thử). Kiểm tra lượt khám trở lại danh sách chờ thanh toán. Thử hủy hóa đơn đã thanh toán: phải bị chặn.

---

## E. Phần của Tâm: Dịch vụ, Nhân viên, Tài khoản, Đăng nhập

6 chức năng, cộng nền tảng đăng nhập và phân quyền cho cả chương trình. Vai trò chính: Quản lý (riêng "Xem danh mục dịch vụ" và "Tài khoản của tôi" cả bốn vai trò đều dùng).

### E1. Bản đồ file

| Lớp | File | Việc |
| :--- | :--- | :--- |
| entity | `DichVu`, `NhanVien`, `CaTruc`, `TaiKhoan`, `NhatKyHeThong` | Dữ liệu |
| dao | `DichVuDAO`, `NhanVienDAO`, `CaTrucDAO`, `TaiKhoanDAO`, `NhatKyDAO` | SQL. `TaiKhoanDAO` có `nangCapBam` và `demQuanLyHoatDong` |
| controller | `DichVuController`, `NhanVienController`, `CaTrucController`, `TaiKhoanController`, `MatKhau`, `Quyen`, `NhatKyController` | Quy tắc, băm mật khẩu, phân quyền, nhật ký |
| ui | `ManHinhDanhMucDichVu`, `ManHinhSuaDichVu`, `ManHinhHoSoNhanVien`, `ManHinhPhanCongCaTruc`, `ManHinhTaiKhoan`, `ManHinhTaiKhoanCuaToi`, `HopThoaiDangNhap` | Màn hình |
| gốc | `MainApp` (phần đăng nhập và lọc menu) | Nối đăng nhập với menu |

### E2. Chức năng → màn hình → hàm chính

| Chức năng | Màn hình | Hàm controller cần đọc |
| :--- | :--- | :--- |
| Xem danh mục dịch vụ & giá (DV-01) | `ManHinhDanhMucDichVu` | `DichVuController.layChoVaiTro` |
| Thêm / sửa dịch vụ (DV-02, 03) | `ManHinhSuaDichVu` | `kiemTra`, `them`, `capNhat` |
| Cập nhật hồ sơ nhân viên (NV-01..03) | `ManHinhHoSoNhanVien` | `NhanVienController.kiemTra`, `them`, `capNhat`, `layCanhBaoChungChi` |
| Phân công ca trực (NV-04, 05) | `ManHinhPhanCongCaTruc` | `CaTrucController.phanCong`, `doiTrangThai`, `xoa` |
| Tạo tài khoản & gán vai trò (TK-03, 04) | `ManHinhTaiKhoan` | `TaiKhoanController.taoTaiKhoan`, `khoa`, `moKhoa`, `capLaiMatKhau` |
| Đăng nhập / Đăng xuất (TK-01, 02) | `HopThoaiDangNhap`, `ManHinhTaiKhoanCuaToi` | `dangNhap`, `dangXuat`, `doiMatKhauCuaToi` |

### E3. Quy tắc phải thuộc

**Dịch vụ** (`DichVuController`):
- Ai cũng xem được; **chỉ Quản lý thêm và sửa**. Nha sĩ, Y tá, Lễ tân chỉ thấy dịch vụ đang hoạt động, Quản lý thấy cả dịch vụ đã ngừng.
- Tên không trùng, giá không âm, thời gian dự kiến lớn hơn 0.
- **Không xóa dịch vụ, chỉ chuyển Ngừng hoạt động** (vì lịch hẹn, hóa đơn cũ còn tham chiếu).
- Đổi giá chỉ áp dụng cho lịch và hóa đơn lập sau đó, và **ghi nhật ký giá cũ → giá mới** (DV-03).

**Nhân viên** (`NhanVienController`):
- Thêm/sửa chỉ Quản lý; không xóa, chỉ chuyển **Nghỉ việc**. Nghỉ việc thì tài khoản của họ **bị khóa tự động**.
- Đổi vai trò nhân viên thì **vai trò tài khoản đổi theo** (vai trò tài khoản luôn bằng vai trò nhân viên).
- Nha sĩ cần chứng chỉ hành nghề. Chứng chỉ hết hạn hoặc **sắp hết trong 30 ngày** thì hiện cảnh báo (`layCanhBaoChungChi`).
- **Không được đổi/ngừng Quản lý hoạt động cuối cùng** (nếu không sẽ không còn ai quản trị).

**Ca trực** (`CaTrucController`):
- Quản lý xếp ca theo tuần: ca Sáng hoặc Chiều, trạng thái Có mặt/Vắng/Điều chuyển, mỗi ca có phòng.
- Không hai ca trùng ngày và ca cho một nhân viên. **Mỗi phòng và ca tối đa một nha sĩ.** Lễ tân xếp vào "Quầy lễ tân".
- Không đánh dấu Vắng/Điều chuyển khi nha sĩ còn lịch hẹn trong ca đó; không xóa ca đã diễn ra hoặc đang có lịch hẹn. Phần này liên quan trực tiếp tới Trường: lịch hẹn chỉ đặt được khi nha sĩ có ca.

**Tài khoản và đăng nhập** (`TaiKhoanController`, `MatKhau`):
- Mật khẩu **không lưu chữ thường**. Định dạng lưu: `s1$<muối 16 ký tự hex>$<băm 64 ký tự hex>`, thuật toán PBKDF2-HMAC-SHA256, 20.000 vòng, mỗi mật khẩu có muối riêng. Xem bằng cách mở bảng `TaiKhoan`.
- Tài khoản cũ còn mật khẩu chữ thường **vẫn đăng nhập được và được băm lại ngay** (`nangCapBam`).
- **Sai 5 lần liên tiếp thì khóa tài khoản**; Quản lý mở khóa ở màn hình Tài khoản. Đăng nhập đúng thì số lần sai về 0.
- Tên đăng nhập tối thiểu 4 ký tự, mật khẩu tối thiểu 6 ký tự. Mỗi nhân viên một tài khoản.
- Cấp lại mật khẩu, khóa/mở khóa, gán vai trò đều **ghi nhật ký**.

**Phân quyền cả chương trình** (`Quyen`, `MainApp`):
- `MainApp.duocDung(cn)`: chức năng chỉ hiện nếu vai trò người đăng nhập nằm trong danh sách vai trò của chức năng đó (`DanhSachChucNang`).
- `Quyen.yeuCau(...)`: mỗi hàm nhạy cảm trong controller gọi hàm này. Không đăng nhập thì báo "Chưa đăng nhập".

### E4. Thứ tự đọc code (khoảng 3 buổi)

1. `MatKhau` (ngắn, dễ hiểu) → `TaiKhoanController.dangNhap` → `HopThoaiDangNhap` → `MainApp.batDau`, `taoMenu`, `duocDung`, `dangXuat`.
2. `Quyen` → `DichVuController` → `ManHinhDanhMucDichVu`, `ManHinhSuaDichVu`.
3. `NhanVienController` → `CaTrucController` → `ManHinhHoSoNhanVien`, `ManHinhPhanCongCaTruc`, `ManHinhTaiKhoan`.

### E5. Thực hành (đăng nhập `quanly`)

1. Thêm dịch vụ mới có giá âm: phải bị chặn. Thêm dịch vụ giá 300.000. Đổi giá thành 350.000 rồi xem `NhatKyHeThong` có dòng ghi đổi giá. Ngừng hoạt động dịch vụ đó, rồi đăng nhập `letan1` kiểm tra không còn thấy nó.
2. Nhân viên: thêm một nha sĩ có chứng chỉ hết hạn trong 10 ngày, xem cảnh báo chứng chỉ. Chuyển một nhân viên sang Nghỉ việc, kiểm tra tài khoản của người đó đã bị khóa (đăng nhập thử phải không vào được).
3. Thử đổi vai trò hoặc cho nghỉ việc người Quản lý duy nhất: phải bị chặn.
4. Phân công ca: xếp cùng nha sĩ hai ca cùng ngày cùng ca (bị chặn); xếp hai nha sĩ cùng phòng cùng ca (bị chặn).
5. Tài khoản: tạo tài khoản cho nhân viên chưa có, đăng nhập bằng nó; đăng nhập sai 5 lần liên tiếp để bị khóa, rồi dùng `quanly` mở khóa.
6. Mở bảng `TaiKhoan` bằng `sqlcmd` xem cột `matKhau` có dạng `s1$...$...`. Đăng nhập `nhasi1` đổi mật khẩu của mình ở "Tài khoản của tôi".

---

## F. Ba phần nối với nhau thế nào

```
Tâm: CaTruc (ca trực, chứng chỉ)
        |  nha sĩ có ca và chứng chỉ còn hạn mới đặt được lịch
        v
Trường: LichHen  (Chờ xác nhận -> Đã xác nhận)
        |  nha sĩ mở "Khám & ghi nhận điều trị" theo lịch
        v
Hậu:    LuotKham (Đang khám -> Hoàn thành)  --- hoanTatKham chuyển lịch hẹn sang Hoàn thành
        |  lượt khám Hoàn thành, chưa có hóa đơn
        v
Trường: HoaDon   (Chờ thanh toán -> lập -> Chưa thanh toán -> Đã thanh toán)
        |  giá và tên dịch vụ lấy từ
        v
Tâm:    DichVu   (giá hiện tại, lưu cố định vào ChiTietHoaDon khi lập)
```

Các điểm giao tiếp bằng hàm (nếu đổi tên hàm phải báo cho nhau):

| Ai gọi | Gọi | Để làm gì |
| :--- | :--- | :--- |
| Hậu | `LichHenController.layDaXacNhanTheoBenhNhan`, `capNhatTrangThai` | Gắn lịch hẹn vào lượt khám, chuyển lịch sang Hoàn thành |
| Hậu | `LichHenController.taoLichTaiKham`, `huyLichTaiKham` | Chỉ định và hủy tái khám |
| Hậu | `DichVuController.layDangHoatDong`, `timTheoMa` | Chọn dịch vụ khi khám |
| Trường | `LuotKhamController.layLuotKhamChoThanhToan`, `ganHoaDon` | Danh sách chờ thanh toán và gắn hóa đơn |
| Trường | `LuotKhamController.layDichVuDaThucHien` | Lấy dòng hóa đơn |
| Trường | `NhanVienController.chungChiConHan`, `CaTrucController.layCaCoMat/coCaTruc` | Kiểm tra khi đặt lịch |
| Mọi người | `Quyen.yeuCau`, `NhatKyController.ghiNhatKy`, `MaGenerator.taoMa` | Quyền, nhật ký, sinh mã |

Một điểm khó cần hiểu kỹ: **khi Trường lập hóa đơn thì dữ liệu của cả hai phần bị đổi trong một transaction** (`HoaDon`, `ChiTietHoaDon` của Trường và `LuotKham.maHoaDon` của Hậu). Nếu một bước lỗi thì không bước nào được lưu.

---

## G. Kịch bản demo cả nhóm

Làm theo thứ tự, mỗi người nói phần của mình (khoảng 10 phút):

1. **Tâm** (đăng nhập `quanly`): giới thiệu menu theo vai trò, thêm một dịch vụ, xếp ca trực cho một nha sĩ, xem dòng nhật ký đổi giá. Đăng xuất.
2. **Trường** (`letan1`): thêm bệnh nhân mới (Hậu đã làm chức năng này, chỉ cần dùng), đặt lịch hẹn cho nha sĩ vừa có ca, xác nhận lịch. Cho xem lỗi khi đặt trùng giờ.
3. **Hậu** (`nhasi1`): khám theo lịch vừa xác nhận: ghi chẩn đoán, chọn răng trên sơ đồ, thêm dịch vụ, kê đơn (cho xem chặn thuốc dị ứng), xem gợi ý điều trị, hoàn tất khám, chỉ định tái khám.
4. **Trường** (`letan1`): vào Danh sách chờ thanh toán thấy lượt khám vừa xong, lập hóa đơn với giảm giá, thanh toán, in hóa đơn.
5. **Tâm** (`quanly`): hủy một hóa đơn chưa thanh toán, cho thấy lượt khám trở lại danh sách chờ.
6. Kết: đăng nhập sai 5 lần để cho xem khóa tài khoản, và mở bảng `TaiKhoan` cho thấy mật khẩu đã băm.

Chuẩn bị trước giờ demo: chạy lại `02_seed.sql` hoặc làm lại CSDL từ đầu (mục A3), kiểm tra các nha sĩ có ca trực trong ngày demo, thử đăng nhập trước một lần.

---

## H. Câu hỏi bảo vệ thường gặp

**Chung**
- *Chương trình chia lớp thế nào, vì sao?* Xem B1.
- *Làm sao chống SQL injection?* Mọi câu SQL dùng `PreparedStatement` với tham số `?`, không nối chuỗi dữ liệu người dùng vào SQL. (Chỗ duy nhất có nối chuỗi là hằng số tên cột, không phải dữ liệu người dùng.)
- *Dữ liệu nhạy cảm bảo vệ ra sao?* Mật khẩu băm PBKDF2 có muối; kiểm tra quyền ở cả menu và controller; thao tác nhạy cảm ghi vào `NhatKyHeThong`.
- *Vì sao không xóa dữ liệu mà chỉ đổi trạng thái?* Vì dữ liệu cũ vẫn được tham chiếu (lịch hẹn, hóa đơn) và cần lưu vết; xem E3.
- *Transaction dùng ở đâu?* Thêm bệnh nhân + hồ sơ rỗng (Hậu); lập hóa đơn + chi tiết + gắn vào lượt khám (Trường).

**Hậu**
- Vì sao dị ứng amoxicillin lại chặn được augmentin? Nhóm thuốc `NHOM_THUOC` trong `HoSoBenhAn`.
- Khám vãng lai (không có lịch) khác khám theo lịch thế nào? Vãng lai tự tìm lịch hẹn Đã xác nhận hôm nay để gắn nếu có.
- Điều kiện hoàn tất khám? Có chẩn đoán và ít nhất một dịch vụ.
- Trám nhiều mặt của một răng tính tiền mấy lần? Một lần: tính theo cặp (răng, dịch vụ).

**Trường**
- Một lịch hẹn được chấp nhận khi nào? Kiểm tra theo D3: giờ trong ca, vừa khít, nha sĩ có ca và chứng chỉ, không trùng, không ở quá khứ.
- Tổng tiền hóa đơn tính thế nào? `(tổng dịch vụ - giảm giá) x 1,08`, làm tròn.
- Vì sao đổi giá dịch vụ không ảnh hưởng hóa đơn cũ? Đơn giá được lưu cố định vào `ChiTietHoaDon` lúc lập.
- Hủy hóa đơn thì chuyện gì xảy ra với lượt khám? Quay lại danh sách chờ thanh toán.

**Tâm**
- Mật khẩu lưu thế nào? Xem E3. Tài khoản cũ chữ thường xử lý sao? Tự băm lại lần đăng nhập đầu.
- Điều gì xảy ra khi cho một nhân viên nghỉ việc? Tài khoản bị khóa, và lịch/ca của họ vẫn giữ để tham chiếu.
- Vì sao không cho đổi Quản lý hoạt động cuối cùng? Tránh hệ thống không còn ai quản trị.

---

## I. Lỗi thường gặp khi chạy

| Hiện tượng | Nguyên nhân | Cách xử lý |
| :--- | :--- | :--- |
| "Chưa có file config/database.properties" | Chạy sai thư mục làm việc hoặc chưa tạo file | Làm mục A4; Eclipse: Run Configurations, Arguments, Working directory = thư mục `chuong_trinh` |
| "Không kết nối được CSDL: Login failed for user" | Sai user/mật khẩu hoặc login chưa được cấp quyền vào `NhaKhoaKim` | Kiểm tra `db.user`, `db.password`; cấp `db_owner` cho login trên CSDL `NhaKhoaKim` |
| "The TCP/IP connection to the host localhost, port 1433 has failed" | SQL Server chưa bật TCP/IP hoặc dùng cổng khác | Bật TCP/IP trong SQL Server Configuration Manager, khởi động lại dịch vụ; instance tên (ví dụ `SQLEXPRESS`) có thể dùng cổng động, đổi `db.port` cho đúng |
| Không đăng nhập được dù đúng mật khẩu | Tài khoản đã bị khóa (sai 5 lần hoặc nhân viên nghỉ việc) | `quanly` mở khóa; `nhasi5` bị khóa sẵn vì nghỉ việc |
| Đặt lịch báo "không có nha sĩ nào có ca trực" | Ngày chọn không có ca trực | Chọn trong 7 ngày có ca trực từ ngày chạy seed, hoặc dùng `quanly` xếp thêm ca |
| Xác nhận LH000002 hoặc LH000005 báo ở quá khứ | Lịch 09:30 hôm chạy seed, nay đã qua giờ | Làm lại CSDL (A3) vào ngày demo, hoặc dùng lịch ngày mai (LH000007, LH000009) |
| Chữ tiếng Việt hiện dấu `?` ở cửa sổ lệnh | Do cửa sổ lệnh, không phải lỗi dữ liệu | Thêm `-f 65001` khi dùng `sqlcmd`; chạy Java với `-Dstdout.encoding=UTF-8` |
| `mvn` báo không tìm thấy `exec` | Chưa có plugin | Dùng Eclipse để chạy |

---

*Tài liệu này mô tả nhánh `hoanthien`. Khi sửa code làm đổi hành vi (tên hàm nối giữa các phần, quy tắc nghiệp vụ), cập nhật lại tài liệu và báo cho hai bạn còn lại.*
