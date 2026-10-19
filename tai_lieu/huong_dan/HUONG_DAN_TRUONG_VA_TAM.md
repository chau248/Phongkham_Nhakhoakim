# Hướng dẫn cho Trường và Tâm

Tài liệu này chỉ rõ **việc nào làm trước, ràng buộc nào phải cài, hàm nào phải đưa cho người khác, và cách tự kiểm tra** để phần của mỗi người khớp với cả dự án và đúng với thực tế phòng khám. Danh sách ràng buộc đầy đủ nằm trong `RANG_BUOC_NGHIEP_VU.md` (cùng mã BN-, LH-, HD-, DV-, NV-, TK-).

## 0. Chuẩn bị (làm một lần)

1. Clone repo vào thư mục **không có dấu** (ví dụ `D:\Phongkham_Nhakhoakim`), `git checkout develop`, import `chuong_trinh` vào Eclipse, chạy `MainApp` thấy menu (xem `HUONG_DAN_CAI_DAT.md`).
2. Cài SQL Server Express, bật Mixed Mode và TCP/IP cổng 1433, tạo login `nhakhoa`. Chạy trong SSMS lần lượt `chuong_trinh/csdl/01_schema.sql` rồi `02_seed.sql` (chạy lại nhiều lần vẫn an toàn). Hậu đưa hai file này lên `develop` ở việc T05.
3. Tạo file `chuong_trinh/config/database.properties` (không commit) gồm: `db.host`, `db.port`, `db.name=NhaKhoaKim`, `db.user`, `db.password`, `db.encrypt=false`, `db.trustServerCertificate=true`.
4. Mỗi việc làm trên nhánh riêng `feature/<ten_viec>` tách từ `develop`, xong mở Pull Request vào `develop` cho người khác duyệt (Hậu duyệt Tâm, Tâm duyệt Trường, Trường duyệt Hậu).

## 1. Nguyên tắc làm cho đúng dự án

- **Mỗi chức năng có 3 lớp (3 package `entity`, `dao`, `ui`):** entity (dữ liệu), dao (lớp DAO viết SQL bằng `PreparedStatement`, không nối chuỗi; lớp `...Service` chứa nghiệp vụ và **toàn bộ ràng buộc**), ui (màn hình Swing, chỉ gọi Service). Không viết SQL ngoài DAO, không kiểm tra nghiệp vụ trong màn hình.
- **Không sửa dữ liệu của người khác.** Cần dữ liệu của người khác thì gọi hàm Service trong sheet *Điểm ghép nối* của `tai_lieu/ke_hoach/KeHoach_PhanCong_Code.xlsx`.
- **Gắn màn hình vào menu** bằng một dòng `manHinh.put("Tên chức năng", ManHinhX::new)` trong hàm `dangKyManHinh` của `MainApp.java` (màn hình là một `JPanel`). Chưa xong thì không thêm dòng đó, vùng nội dung để trống.
- **Lỗi nghiệp vụ** ném `NghiepVuException` với câu tiếng Việt rõ nghĩa; màn hình bắt và hiện cho người dùng.
- **Không băm mật khẩu** (quyết định của nhóm): lưu chữ thường, chỉ dùng cho bài tập môn học.
- **Thực tế phòng khám:** không xóa dữ liệu (chỉ đổi trạng thái), dữ liệu đã chốt thì khóa, giờ làm việc ca Sáng 08:00-12:00 và Chiều 13:00-17:00, không đặt lịch quá khứ.

## 2. Tâm: thứ tự việc, ràng buộc và kiểm tra

### T06 - Hạ tầng

- **Việc:** Viết DatabaseConnection (đọc config/database.properties), MaGenerator (sinh mã BN000001...), PhienLamViec (người dùng hiện tại)
- **Làm sau:** T02, T04
- **Kết quả:** Lớp dùng chung, có test kết nối

### T07 - Hạ tầng

- **Việc:** NhatKyService.ghiNhatKy(...) bản đầu để mọi người gọi khi sửa giá, sửa dị ứng, gán vai trò
- **Làm sau:** T05
- **Kết quả:** API ghi nhật ký dùng được từ cuối Tuần 1
- **Ràng buộc phải cài (ở Service):**
  - **CH-03**: Thao tác nhạy cảm phải ghi nhật ký (người làm, giờ, giá trị cũ -> mới): sửa giá dịch vụ, sửa tiền sử/dị ứng, gán vai trò, khóa tài khoản, hủy hóa đơn, hủy lịch.

### T08 - Entity/DAO

- **Việc:** NhanVien, TaiKhoan, DichVu, CaTruc, NhatKyHeThong: entity, DAO, kiểm thử
- **Làm sau:** T06, T05
- **Kết quả:** DAO chạy với dữ liệu mẫu

### T09 - Dịch vụ

- **Việc:** Đăng nhập: MatKhauUtil (BCrypt), TaiKhoanService.dangNhap, khóa tài khoản sau 5 lần sai; MH01 Đăng nhập; sau đăng nhập mở chương trình theo vai trò
- **Làm sau:** T08
- **Kết quả:** Đăng nhập thật cho 4 vai trò
- **Ràng buộc phải cài (ở Service):**
  - **TK-01**: Sai mật khẩu 5 lần liên tiếp thì khóa tài khoản; tài khoản khóa không đăng nhập được; đăng nhập đúng thì xóa bộ đếm sai; ghi nhật ký đăng nhập thất bại.
  - **TK-02**: Chỉ nhân viên Đang làm việc đăng nhập được; sau đăng nhập mở menu theo vai trò (CH-01); đăng xuất xóa người dùng hiện tại.
  - **CH-01**: Phải đăng nhập mới dùng được chương trình; menu và chức năng chỉ hiện đúng vai trò (cột 'Vai trò dùng' trong sheet Chức năng).

### T10 - Màn hình

- **Việc:** MH27 Danh mục dịch vụ & giá, MH28 Thêm/sửa dịch vụ, MH24 Phân công ca trực, MH37 Hồ sơ nhân viên, MH38 Tạo tài khoản & gán vai trò; gắn vào taoManHinh
- **Làm sau:** T08
- **Kết quả:** Dữ liệu dịch vụ, nhân viên, ca trực để người khác dùng
- **Ràng buộc phải cài (ở Service):**
  - **DV-01**: Mọi vai trò được xem; Nha sĩ và Lễ tân chỉ thấy dịch vụ đang hoạt động; chỉ xem giá và thời gian, không sửa.
  - **DV-02**: Chỉ Quản lý; tên dịch vụ bắt buộc và không trùng; giá không âm; thời gian dự kiến lớn hơn 0.
  - **DV-03**: Đổi giá chỉ áp dụng cho lịch và hóa đơn lập sau đó; mọi thay đổi giá ghi nhật ký.
  - **NV-01**: Chỉ Quản lý; họ tên, vai trò bắt buộc; số điện thoại 10 số; Nha sĩ bắt buộc có số và ngày hết hạn chứng chỉ hành nghề.
  - **NV-02**: Nha sĩ hết hạn chứng chỉ thì không được xếp ca, nhận lịch hay bắt đầu khám; báo cảnh báo khi còn 30 ngày.
  - **NV-03**: Không xóa nhân viên, chỉ chuyển sang Nghỉ việc; nhân viên Nghỉ việc không được xếp ca và tài khoản bị khóa.
  - **NV-04**: Chỉ xếp nhân viên Đang làm việc; không xếp ca trong quá khứ; một nhân viên không có hai ca trùng ngày và ca.
  - **NV-05**: Mỗi phòng và ca tối đa một Nha sĩ; Lễ tân xếp ở quầy lễ tân; không xóa ca đã có lịch hẹn của Nha sĩ đó.
  - **TK-03**: Chỉ Quản lý; mỗi nhân viên đúng một tài khoản; tên đăng nhập không trùng, từ 4 ký tự, không dấu cách; mật khẩu từ 6 ký tự.
  - **TK-04**: Vai trò tài khoản luôn bằng vai trò của nhân viên; không tự hạ quyền hay khóa chính mình; luôn còn ít nhất một tài khoản Quản lý hoạt động.

### T11 - Liên thông

- **Việc:** Cung cấp DichVuService.layTatCa/timTheoLoai/timTheoMa, NhanVienService.layTheoVaiTro, CaTrucService.layTheoNhanVienVaKhoang cho Hậu và Trường
- **Làm sau:** Bảng 'Điểm ghép nối'
- **Kết quả:** Hậu và Trường gọi được từ đầu Tuần 2

### T12 - Màn hình

- **Việc:** Phân quyền menu theo vai trò (ẩn mục không thuộc vai trò của người đăng nhập); ghi nhật ký khi sửa giá dịch vụ và gán vai trò
- **Làm sau:** T09, T10
- **Kết quả:** Phân quyền và nhật ký chạy toàn hệ thống
- **Ràng buộc phải cài (ở Service):**
  - **CH-01**: Phải đăng nhập mới dùng được chương trình; menu và chức năng chỉ hiện đúng vai trò (cột 'Vai trò dùng' trong sheet Chức năng).
  - **CH-03**: Thao tác nhạy cảm phải ghi nhật ký (người làm, giờ, giá trị cũ -> mới): sửa giá dịch vụ, sửa tiền sử/dị ứng, gán vai trò, khóa tài khoản, hủy hóa đơn, hủy lịch.

### T30 - Tài liệu

- **Việc:** Hướng dẫn cài đặt và sử dụng cho Quản lý + phần nền tảng trong báo cáo
- **Làm sau:** Mọi phần
- **Kết quả:** Hướng dẫn sử dụng

### Tâm phải đưa cho người khác (đúng tên hàm)

| Dùng bởi | Hàm | Ý nghĩa |
|---|---|---|
| Cả nhóm | `DatabaseConnection.getConnection()` | Lấy kết nối SQL Server từ config/database.properties |
| Cả nhóm | `MaGenerator.taoMa(String tienTo)` | Sinh mã dạng BN000001, HS000001... |
| Cả nhóm | `PhienLamViec.getNhanVienHienTai() / getVaiTro()` | Người dùng đang đăng nhập |
| Cả nhóm | `NhatKyService.ghiNhatKy(maNV, hanhDong, doiTuong, maDoiTuong, chiTiet)` | Ghi nhật ký cho mọi thao tác nhạy cảm |
| Hậu, Trường | `DichVuService.layTatCa() / timTheoLoai(loai) / timTheoMa(ma)` | Danh mục dịch vụ (chỉ đọc cho Hậu, Trường) |
| Hậu, Trường | `NhanVienService.timTheoMa(ma) / layTheoVaiTro(vaiTro)` | Chọn Nha sĩ khi khám, đặt lịch |
| Hậu | `CaTrucService.layTheoNhanVienVaKhoang(maNV, tu, den)` | Dữ liệu cho MH06 Lịch làm việc |
| Hậu, Trường | `CaTrucService.coCaTruc(maNV, ngay, caLam)` | Kiểm tra Nha sĩ có ca trực khi đặt lịch và khi bắt đầu khám (LH-02, LK-01) |
| Hậu, Trường | `NhanVienService.chungChiConHan(maNV, ngay)` | Nha sĩ còn chứng chỉ hành nghề tại ngày đó (LH-02, LK-01, NV-02) |

### Kiểm tra trước khi mở Pull Request (Tâm)

Làm từng kịch bản trên dữ liệu mẫu; mỗi kịch bản phải ra kết quả như mô tả:

1. Sai mật khẩu 5 lần liên tiếp -> tài khoản bị khóa; lần thứ 6 nhập đúng vẫn bị từ chối; Quản lý mở khóa thì đăng nhập lại được (TK-01).
2. Tạo tài khoản cho nhân viên đã có tài khoản, hoặc tên đăng nhập trùng, hoặc mật khẩu 5 ký tự -> bị từ chối với thông báo rõ ràng (TK-03).
3. Chọn vai trò khác với vai trò của nhân viên khi tạo tài khoản -> hệ thống tự lấy theo nhân viên hoặc từ chối (TK-04).
4. Nha sĩ NV000003 (chứng chỉ chỉ còn 20 ngày) -> hiện cảnh báo; sửa ngày hết hạn về quá khứ rồi xếp ca -> bị chặn (NV-02).
5. Xếp hai Nha sĩ vào cùng phòng, cùng ca -> bị chặn; xếp một nhân viên hai ca trùng -> bị chặn (NV-04, NV-05).
6. Sửa giá dịch vụ thành số âm -> bị chặn; sửa giá hợp lệ -> có dòng trong nhật ký; hóa đơn lập trước đó không đổi (DV-02, DV-03).
7. Chuyển nhân viên sang Nghỉ việc -> tài khoản bị khóa và không đăng nhập được (NV-03).
8. Đăng nhập bằng Lễ tân -> menu chỉ hiện các chức năng có vai trò Lễ tân (CH-01).

## 3. Trường: thứ tự việc, ràng buộc và kiểm tra

### T20 - Entity/DAO

- **Việc:** BenhNhan, LichHen, HoaDon, ChiTietHoaDon: entity, DAO, kiểm thử
- **Làm sau:** T06, T05
- **Kết quả:** DAO chạy với dữ liệu mẫu

### T21 - Màn hình

- **Việc:** Dựng bản tĩnh (chưa nối dữ liệu) cho MH08, MH13, MH17, MH18, MH19, MH20, MH21, MH22 theo menu và luồng màn hình
- **Làm sau:** Menu hướng đối tượng, luồng màn hình
- **Kết quả:** Lớp giao diện khung, đăng ký được trong dangKyManHinh của MainApp

### T22 - Dịch vụ

- **Việc:** BenhNhanService (tạo, sửa, tìm kiếm), LichHenService (đặt, dời, hủy, xác nhận, kiểm tra trùng lịch)
- **Làm sau:** T20, T11
- **Kết quả:** Nghiệp vụ lễ tân chạy
- **Ràng buộc phải cài (ở Service):**
  - **BN-01**: Tìm theo mã, họ tên (không phân biệt hoa thường) hoặc số điện thoại; chỉ hiện bệnh nhân Đang điều trị; hiện tối đa 50 dòng.
  - **BN-02**: Họ tên bắt buộc; số điện thoại đúng 10 số bắt đầu bằng 0; CCCD đúng 12 số và không trùng bệnh nhân khác; ngày sinh không sau hôm nay.
  - **BN-03**: Bệnh nhân dưới 18 tuổi bắt buộc có người liên hệ khẩn cấp (tên và số điện thoại).
  - **BN-04**: Tạo bệnh nhân mới thì tạo luôn một hồ sơ bệnh án rỗng (gọi Hậu) trong cùng một giao dịch; một trong hai lỗi thì hủy cả hai.
  - **BN-05**: Lễ tân sửa thông tin hành chính; Y tá chỉ khai báo y tế và dị ứng ban đầu; Nha sĩ không sửa thông tin hành chính.
  - **LH-01**: Xem theo ngày hoặc tuần; chỉ Lễ tân thấy lịch của mọi Nha sĩ; khung giờ trống tính từ ca trực trừ các lịch đã đặt.
  - **LH-02**: Bệnh nhân phải Đang điều trị; dịch vụ phải đang hoạt động; Nha sĩ phải có ca trực Có mặt vào ngày và ca đó và chứng chỉ hành nghề còn hạn.
  - **LH-03**: Không trùng giờ: cùng Nha sĩ không có hai lịch chồng thời lượng dịch vụ; cùng bệnh nhân không có hai lịch chồng nhau. Giờ hẹn nằm trong giờ làm việc (CH-05).
  - **LH-04**: Lịch mới ở trạng thái Chờ xác nhận; thời lượng lấy từ thời gian dự kiến của dịch vụ.
  - **LH-05**: Chỉ lịch Chờ xác nhận hoặc Đã xác nhận mới dời hoặc hủy; lịch đã Hoàn thành, Không đến, Đã hủy không đổi được.
  - **LH-06**: Hủy bắt buộc có lý do; không hủy hoặc dời lịch đang có lượt khám Đang khám; dời phải qua đủ ràng buộc LH-02, LH-03.
  - **LH-07**: Chỉ xác nhận lịch Chờ xác nhận (kể cả lịch tái khám do Nha sĩ tạo); kiểm tra lại trùng giờ và ca trực lúc xác nhận.

### T23 - Màn hình

- **Việc:** Nối dữ liệu thật cho MH13, MH08, MH17, MH18, MH19
- **Làm sau:** T21, T22
- **Kết quả:** Lễ tân tạo hồ sơ và đặt lịch được

### T24 - Liên thông

- **Việc:** Cung cấp LichHenService.layTheoNhanVien và taoLichTaiKham cho Hậu
- **Làm sau:** Bảng 'Điểm ghép nối'
- **Kết quả:** Hậu gọi được từ Tuần 3

### T25 - Dịch vụ

- **Việc:** HoaDonService: taoTuLuotKham, tinhTongTien, thanh toán; danh sách chờ thanh toán lấy từ lượt khám hoàn tất của Hậu
- **Làm sau:** T18
- **Kết quả:** Hóa đơn tạo từ lượt khám
- **Ràng buộc phải cài (ở Service):**
  - **HD-01**: Chỉ hiện lượt khám Hoàn thành chưa có hóa đơn; lượt khám đã gắn hóa đơn không hiện lại.
  - **HD-02**: Chỉ lập từ lượt khám Hoàn thành chưa gắn hóa đơn; mỗi lượt khám đúng một hóa đơn (gắn một lần); hóa đơn có ít nhất một dòng chi tiết.
  - **HD-03**: Tổng tiền = tổng (đơn giá x số lượng) - giảm giá + thuế VAT; giảm giá không vượt tổng tiền dịch vụ; đơn giá lấy theo giá dịch vụ lúc lập và lưu cố định (đổi giá sau không ảnh hưởng hóa đơn cũ).
  - **HD-04**: Dòng chi tiết lấy từ dịch vụ đã thực hiện của lượt khám (gồm dịch vụ gắn răng và không gắn răng), không tự thêm dịch vụ ngoài lượt khám.
  - **HD-05**: Chỉ thanh toán hóa đơn Chưa thanh toán; hình thức: Tiền mặt, Chuyển khoản, Thẻ, Ví điện tử; thanh toán xong thì khóa hóa đơn.
  - **HD-06**: Chỉ in hóa đơn khi đã thanh toán; hóa đơn Đã hủy không in.

### T26 - Màn hình

- **Việc:** MH20 Danh sách chờ thanh toán, MH21 Lập hóa đơn, MH22 Thanh toán và in hóa đơn
- **Làm sau:** T25
- **Kết quả:** Lễ tân thu tiền được

### T31 - Tài liệu

- **Việc:** Hướng dẫn sử dụng cho Lễ tân + phần hóa đơn/lịch hẹn trong báo cáo
- **Làm sau:** Mọi phần
- **Kết quả:** Hướng dẫn sử dụng

### Trường phải đưa cho người khác (đúng tên hàm)

| Dùng bởi | Hàm | Ý nghĩa |
|---|---|---|
| Hậu | `BenhNhanService.timTheoMa(maBN) / timKiem(tuKhoa)` | Hậu lấy thông tin hành chính bệnh nhân |
| Hậu | `LichHenService.layTheoNhanVien(maNV, tu, den)` | Hậu hiển thị MH06, MH07 |
| Hậu | `LichHenService.taoLichTaiKham(maBN, maDichVu, ngay, maNV)` | MH12 tạo lịch tái khám, trạng thái Chờ xác nhận |
| Hậu | `HoaDonService.taoTuLuotKham(maLuotKham)` | Tạo hóa đơn và gán maHoaDon vào lượt khám |
| Hậu | `LichHenService.layTheoMa(maLichHen)` | Hậu kiểm tra lịch Đã xác nhận, đúng ngày khi bắt đầu khám (LK-02) |
| Hậu | `LichHenService.capNhatTrangThai(maLichHen, trangThai)` | Hậu chuyển lịch sang Hoàn thành khi hoàn tất khám (LK-05) |

### Kiểm tra trước khi mở Pull Request (Trường)

Làm từng kịch bản trên dữ liệu mẫu; mỗi kịch bản phải ra kết quả như mô tả:

1. Tạo bệnh nhân 8 tuổi mà không có người liên hệ khẩn cấp -> bị từ chối; có đủ thì tạo được và có hồ sơ bệnh án rỗng đi kèm (BN-03, BN-04).
2. Nhập số điện thoại 9 số hoặc CCCD trùng bệnh nhân khác -> bị từ chối (BN-02).
3. Đặt lịch 09:00 Lấy cao răng (30 phút) cho Nha sĩ A; đặt thêm lịch 09:15 cùng Nha sĩ -> bị chặn; đặt 09:30 -> được (LH-03).
4. Đặt lịch cho Nha sĩ không có ca trực hôm đó, hoặc hết hạn chứng chỉ, hoặc giờ ngoài 08:00-12:00 và 13:00-17:00 -> bị chặn (LH-02, CH-05).
5. Hủy lịch không nhập lý do -> bị chặn; hủy lịch đã Hoàn thành -> bị chặn (LH-05, LH-06).
6. Lịch tái khám do Nha sĩ tạo xuất hiện ở trạng thái Chờ xác nhận; xác nhận lần hai trên cùng lịch -> bị từ chối (LH-07).
7. Lượt khám Hoàn thành xuất hiện ở danh sách chờ thanh toán; lập hóa đơn xong thì biến mất; lập lần hai cho cùng lượt khám -> bị chặn (HD-01, HD-02).
8. Giảm giá lớn hơn tổng tiền dịch vụ -> bị chặn; đổi giá dịch vụ sau khi lập hóa đơn -> hóa đơn cũ giữ nguyên số tiền (HD-03).
9. Thanh toán xong rồi cố sửa hoặc hủy hóa đơn -> bị chặn; in hóa đơn chưa thanh toán -> bị chặn (HD-05, HD-06).

## 4. Dữ liệu mẫu có sẵn để thử (từ `02_seed.sql`)

| Loại | Nội dung |
|---|---|
| Nhân viên | NV000001 Quản lý; NV000002 và NV000003 Nha sĩ (NV000003 chứng chỉ còn 20 ngày); NV000004, NV000005 Y tá; NV000006 Lễ tân |
| Tài khoản | `quanly`, `nhasi1`, `nhasi2`, `yta1`, `yta2`, `letan1`, mật khẩu `123456` (chỉ để thử trên máy mình) |
| Bệnh nhân | BN000001 An; BN000002 Bình (dị ứng Penicillin, Lidocaine; đã nhổ răng 36); BN000003 Cường (12 tuổi, có người liên hệ khẩn cấp) |
| Dịch vụ | 10 dịch vụ, từ Khám tổng quát đến Implant và Cầu răng |
| Ca trực, lịch hẹn | Ca trực hôm nay của 5 nhân viên; 2 lịch hẹn hôm nay (một Đã xác nhận, một Chờ xác nhận) |
