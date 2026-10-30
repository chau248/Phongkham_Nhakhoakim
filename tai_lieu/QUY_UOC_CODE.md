# Quy ước viết code và làm việc nhóm

Theo slide môn học *Review 4: Coding Convention*, áp dụng cho cả 3 thành viên. Bảng này giống sheet *Quy ước code* trong `tai_lieu/KeHoach_PhanCong_Code.xlsx`.

| Hạng mục | Quy ước | Ví dụ |
|---|---|---|
| Thư mục | Chữ thường, không dấu cách, không dấu tiếng Việt; dùng gạch dưới nếu cần. Đường dẫn dự án trên máy cũng phải không dấu | tai_lieu, chuong_trinh, csdl, ho_so |
| Tên file (ngoài Java) | Chữ thường, không dấu cách/tiếng Việt, dùng gạch dưới; mô tả nội dung | 01_schema.sql, database.properties |
| Package | Chữ thường, không gạch dưới | nhakhoakim.entity, nhakhoakim.dao, nhakhoakim.service, nhakhoakim.ui, nhakhoakim.util |
| Class / Interface | PascalCase, danh từ, bắt đầu bằng chữ cái; tên miền dùng tiếng Việt không dấu | BenhNhan, LuotKham, BenhNhanService |
| Phương thức | camelCase, bắt đầu bằng động từ | timTheoMa, taoLichTaiKham, ghiNhatKy |
| Biến, tham số, thuộc tính | camelCase, mô tả rõ nội dung; hằng số viết hoa với gạch dưới | maBN, tongTien, TOI_DA_LAN_SAI |
| Exception | PascalCase kết thúc bằng Exception | NghiepVuException |
| Control JavaFX | Tiền tố 3 chữ: txt (TextField), lbl, btn, cmb (ComboBox), tbl (TableView), dtp (DatePicker), chk, rad, pnl, tab, lst, tre, lnk (Hyperlink) | txtTenDangNhap, btnDangNhap, tblLichHen, cmbDichVu, lblTen |
| Màn hình | Mỗi màn hình là một lớp Java tên ManHinh + tên màn hình, viết bằng code JavaFX và gắn vào hàm taoManHinh trong MainApp; mỗi màn hình chỉ một công việc | ManHinhDatLichHen, ManHinhHoSoBenhAn |
| Comment | Bắt buộc cho mọi class, phương thức, thuộc tính và đoạn xử lý khó; viết ngắn, một dòng // hoặc /** */, nói rõ mục đích | Xem mẫu trong ChucNang.java và MainApp.java |
| Mã số bảng | Chuỗi dạng tiền tố + 6 chữ số, sinh bằng MaGenerator | BN000001, HD000123 |
| Branch | Không commit trực tiếp lên main. Mỗi người một nhánh feature/<ten_viec> tách từ develop; merge vào develop sau khi test và được 1 người khác review | feature/dat_lich_hen |
| Commit | Một dòng, thể hiện việc đã làm: loại: nội dung ngắn | feat: them dat lich hen, fix: sua kiem tra trung lich |
| Review chéo | Mỗi Pull Request được 1 thành viên khác duyệt (Hậu duyệt Tâm, Tâm duyệt Trường, Trường duyệt Hậu); cuối Tuần 4 họp review cả nhóm | Theo slide Review 4 |
| Giao diện | Theo 6 loại màn hình của slide Review 3 (chính, nhập dữ liệu, nhập liệu xử lý, kết quả, thông báo, tra cứu); nhất quán vị trí, ngôn ngữ, màu sắc (màu chủ đạo khai báo ở MainApp) | Slide Review 3 |
| Truy cập dữ liệu | Chỉ DAO được viết SQL (PreparedStatement, không nối chuỗi); Service chứa nghiệp vụ và kiểm tra ràng buộc; màn hình chỉ gọi Service | Chống SQL injection |

## Mẫu comment (bắt buộc cho class, phương thức, thuộc tính)

```java
// Một chức năng của chương trình (một mục trong menu)
public class ChucNang {

    private String ten; // tên chức năng

    // Lấy tên chức năng
    public String getTen() {
        return ten;
    }
}
```

Phương thức có tham số hoặc kết quả khó hiểu thì ghi thêm bằng `/** ... @param ... @return ... */`.

## Lưu ý về giao diện (slide Review 3)

- 6 loại màn hình: chính, nhập dữ liệu, nhập liệu xử lý, kết quả, thông báo, tra cứu.
- Mỗi màn hình gói gọn một công việc; không nhúng hai công việc trên một màn hình.
- Hạn chế lỗi cho người dùng: dùng ComboBox, DatePicker thay cho ô nhập tự do khi có thể; có thông báo lỗi dễ hiểu bằng tiếng Việt.
- Nhất quán vị trí, ngôn ngữ, hình dáng, màu sắc và cách kích hoạt giữa các màn hình.
