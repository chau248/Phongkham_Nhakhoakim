# Ràng buộc nghiệp vụ

Các quy tắc để chức năng của 3 người khớp nhau và đúng logic phòng khám. Người làm chức năng nào thì cài đặt ràng buộc của chức năng đó **ở lớp Service** (không để ở màn hình), báo lỗi bằng thông báo tiếng Việt. Bảng này giống sheet *Ràng buộc* trong `tai_lieu/KeHoach_PhanCong_Code.xlsx`.

## 1. Vòng đời trạng thái

| Đối tượng | Chuỗi trạng thái hợp lệ | Ghi chú |
|---|---|---|
| Lịch hẹn | Chờ xác nhận -> Đã xác nhận -> Hoàn thành / Không đến | Chờ xác nhận và Đã xác nhận có thể -> Đã hủy (bắt buộc lý do). Hoàn thành, Không đến, Đã hủy là trạng thái cuối, không sửa. |
| Lượt khám | Đang khám -> Hoàn thành | Đang khám có thể -> Đã hủy. Hoàn thành thì khóa: không sửa chẩn đoán, đơn thuốc, sơ đồ răng, dịch vụ. |
| Hóa đơn | Chưa thanh toán -> Đã thanh toán | Chưa thanh toán có thể -> Đã hủy. Đã thanh toán thì khóa, không sửa, không hủy. |
| Tài khoản | Hoạt động -> Bị khóa -> Hoạt động | Khóa tự động sau 5 lần đăng nhập sai liên tiếp; chỉ Quản lý mở khóa. |

## 2. Chuỗi phụ thuộc giữa các chức năng

1. Dịch vụ (đang hoạt động) + Nhân viên (Nha sĩ còn chứng chỉ) + Ca trực + Bệnh nhân (đang điều trị)  ->  Đặt lịch hẹn
2. Đặt lịch hẹn  ->  Xác nhận lịch hẹn (Lễ tân)  ->  Bắt đầu khám (Nha sĩ, đúng ngày hẹn)
3. Bắt đầu khám  ->  Sơ đồ răng, Kê đơn, Dịch vụ thực hiện  ->  Hoàn tất khám (phải có chẩn đoán)  ->  Lịch hẹn chuyển Hoàn thành
4. Chỉ định tái khám (trong lúc khám)  ->  tạo lịch hẹn Chờ xác nhận  ->  Lễ tân xác nhận
5. Hoàn tất khám  ->  Danh sách chờ thanh toán  ->  Lập hóa đơn (gắn hóa đơn vào lượt khám)  ->  Thanh toán và in

## 3. Ràng buộc chung

| Mã | Ràng buộc | Nơi kiểm tra |
|---|---|---|
| CH-01 | Phải đăng nhập mới dùng được chương trình; menu và chức năng chỉ hiện đúng vai trò (cột 'Vai trò dùng' trong sheet Chức năng). | Màn hình + Service |
| CH-02 | Không xóa dữ liệu nghiệp vụ (bệnh nhân, hóa đơn, lượt khám, dịch vụ...). Chỉ đổi trạng thái; dữ liệu đã chốt (lượt khám Hoàn thành, hóa đơn Đã thanh toán) thì khóa. | Service |
| CH-03 | Thao tác nhạy cảm phải ghi nhật ký (người làm, giờ, giá trị cũ -> mới): sửa giá dịch vụ, sửa tiền sử/dị ứng, gán vai trò, khóa tài khoản, hủy hóa đơn, hủy lịch. | Service |
| CH-04 | Mọi kiểm tra nằm ở Service và báo bằng thông báo tiếng Việt dễ hiểu (NghiepVuException); màn hình không hiện lỗi SQL thô. | Service + Màn hình |
| CH-05 | Giờ làm việc: ca Sáng 08:00-12:00, ca Chiều 13:00-17:00. Không đặt hay dời lịch về quá khứ. | Service |
| CH-06 | Mã (BN, HS, LK, HD...) do hệ thống sinh, người dùng không nhập và không sửa. | Service |
| CH-07 | Người dùng chỉ sửa dữ liệu thuộc chức năng của mình; muốn đổi dữ liệu của người khác phải gọi Service của người đó (xem sheet Điểm ghép nối). | Service |

## 4. Ràng buộc theo chức năng


### Bệnh nhân

| Mã | Chức năng | Người làm | Ràng buộc | Nơi kiểm tra |
|---|---|---|---|---|
| BN-01 | Tìm kiếm bệnh nhân | Hậu | Tìm theo mã, họ tên (không phân biệt hoa thường) hoặc số điện thoại; chỉ hiện bệnh nhân Đang điều trị; hiện tối đa 50 dòng. | Service |
| BN-02 | Thêm / cập nhật hồ sơ bệnh nhân | Hậu | Họ tên bắt buộc; số điện thoại đúng 10 số bắt đầu bằng 0; CCCD đúng 12 số và không trùng bệnh nhân khác; ngày sinh không sau hôm nay. | Service + CSDL |
| BN-03 | Thêm / cập nhật hồ sơ bệnh nhân | Hậu | Bệnh nhân dưới 18 tuổi bắt buộc có người liên hệ khẩn cấp (tên và số điện thoại). | Service |
| BN-04 | Thêm / cập nhật hồ sơ bệnh nhân | Hậu | Tạo bệnh nhân mới thì tạo luôn một hồ sơ bệnh án rỗng (gọi Hậu) trong cùng một giao dịch; một trong hai lỗi thì hủy cả hai. | Service |
| BN-05 | Thêm / cập nhật hồ sơ bệnh nhân | Hậu | Lễ tân sửa thông tin hành chính; Y tá chỉ khai báo y tế và dị ứng ban đầu; Nha sĩ không sửa thông tin hành chính. | Service |
| BN-06 | Xem hồ sơ bệnh án | Hậu | Chỉ Nha sĩ xem đầy đủ hồ sơ; có cảnh báo dị ứng thì hiện nổi bật ở đầu trang; lịch sử khám sắp mới nhất trước. | Service + Màn hình |
| BN-07 | Xem hồ sơ bệnh án | Hậu | Hồ sơ Đã đóng chỉ xem, không sửa tiền sử, dị ứng. | Service |
| BN-08 | Cập nhật tiền sử & dị ứng | Hậu | Chỉ Nha sĩ và Y tá được sửa; mỗi thay đổi ghi nhật ký (giá trị cũ -> mới). | Service |
| BN-09 | Cập nhật tiền sử & dị ứng | Hậu | Danh sách dị ứng được chuẩn hóa: các tác nhân cách nhau dấu phẩy, bỏ khoảng trắng thừa, bỏ trùng lặp. Dị ứng mới có hiệu lực ngay cho kê đơn và chọn vật liệu. | Service |
| BN-12 | Thêm / cập nhật hồ sơ bệnh nhân | Hậu | Họ tên chỉ gồm chữ cái và khoảng trắng, dài 2 đến 100 ký tự; email đúng định dạng ten@tenmien.xx; tuổi không quá 120. | Service |
| BN-13 | Thêm / cập nhật hồ sơ bệnh nhân | Hậu | Bệnh nhân dưới 18 tuổi: số điện thoại người liên hệ phải là của người giám hộ, không được trùng số của chính bệnh nhân. | Service |
| BN-14 | Thêm / cập nhật hồ sơ bệnh nhân | Hậu | Thêm mới mà trùng họ tên và ngày sinh với bệnh nhân đã có thì chặn, báo mã bệnh nhân đã đăng ký (trẻ em chưa có CCCD vẫn bị phát hiện trùng). | Service |
| BN-15 | Cập nhật tiền sử & dị ứng | Hậu | Lễ tân không được sửa thông tin y tế (tiền sử, dị ứng, bệnh nền, thuốc đang dùng). | Service |
| BN-16 | Cập nhật tiền sử & dị ứng | Hậu | Dị ứng một thuốc thì chặn cả các thuốc cùng nhóm (Penicillin chặn Amoxicillin, Augmentin; Aspirin chặn Ibuprofen, Diclofenac; Cephalosporin; Sulfa). | Entity |
| BN-17 | Thêm / cập nhật hồ sơ bệnh nhân | Hậu | Ngày sinh bắt buộc (để biết bệnh nhân có phải trẻ em, kiểm tra trùng người và giới hạn tuổi của dịch vụ). | Service |
| BN-18 | Tìm kiếm bệnh nhân | Hậu | Ngừng theo dõi hoặc mở lại điều trị một bệnh nhân: chỉ Lễ tân hoặc Quản lý; không ngừng khi còn lượt khám đang dở; có ghi nhật ký; tìm kiếm có ô "gồm cả bệnh nhân ngừng theo dõi" để mở lại. Bệnh nhân Ngừng theo dõi không khám được (LK-13). | Service + Màn hình |
| BN-19 | Xem hồ sơ bệnh án | Hậu | Đóng hoặc mở lại hồ sơ bệnh án: chỉ Quản lý; không đóng khi còn lượt khám đang dở; có ghi nhật ký. Hồ sơ Đã đóng chỉ xem (BN-07). | Service + Màn hình |
| BN-20 | Cập nhật tiền sử & dị ứng | Hậu | Dị ứng ghi "Không", "Không có", "Chưa có"... không phải là một tác nhân: bị bỏ khi chuẩn hóa, không hiện cảnh báo đỏ và không chặn thuốc. Chữ phủ định đứng trước bệnh nền ("Không tim mạch") thì không sinh cảnh báo thủ thuật. | Entity + Service |
| BN-21 | Cập nhật tiền sử & dị ứng | Hậu | Hai người cùng sửa hồ sơ y tế: chỉ trường mình thực sự đổi mới được ghi, trường khác giữ giá trị hiện có; nếu cùng sửa một trường thành hai giá trị khác nhau thì báo xung đột để xem lại hồ sơ, không ghi đè mất dữ liệu. | Service |
| BN-10 | Xem liệu trình đang thực hiện | Hậu | Chỉ hiện liệu trình của đúng bệnh nhân đang xem, liệu trình Đang thực hiện xếp trước; chỉ xem, không sửa. | Service |
| BN-11 | Xem liệu trình đang thực hiện | Hậu | Cảnh báo (không chặn) khi dịch vụ yêu cầu liệu trình (điều trị tủy, niềng, implant, cầu răng) mà bệnh nhân chưa có liệu trình Đang thực hiện. | Service |

### Lịch hẹn

| Mã | Chức năng | Người làm | Ràng buộc | Nơi kiểm tra |
|---|---|---|---|---|
| LH-01 | Xem lịch hẹn | Trường | Xem theo ngày hoặc tuần; chỉ Lễ tân thấy lịch của mọi Nha sĩ; khung giờ trống tính từ ca trực trừ các lịch đã đặt. | Service |
| LH-02 | Đặt lịch hẹn | Trường | Bệnh nhân phải Đang điều trị; dịch vụ phải đang hoạt động; Nha sĩ phải có ca trực Có mặt vào ngày và ca đó và chứng chỉ hành nghề còn hạn. | Service |
| LH-03 | Đặt lịch hẹn | Trường | Không trùng giờ: cùng Nha sĩ không có hai lịch chồng thời lượng dịch vụ; cùng bệnh nhân không có hai lịch chồng nhau. Giờ hẹn nằm trong giờ làm việc (CH-05). | Service |
| LH-04 | Đặt lịch hẹn | Trường | Lịch mới ở trạng thái Chờ xác nhận; thời lượng lấy từ thời gian dự kiến của dịch vụ. | Service |
| LH-05 | Dời / hủy lịch hẹn | Trường | Chỉ lịch Chờ xác nhận hoặc Đã xác nhận mới dời hoặc hủy; lịch đã Hoàn thành, Không đến, Đã hủy không đổi được. | Service |
| LH-06 | Dời / hủy lịch hẹn | Trường | Hủy bắt buộc có lý do; không hủy hoặc dời lịch đang có lượt khám Đang khám; dời phải qua đủ ràng buộc LH-02, LH-03. | Service |
| LH-07 | Xác nhận lịch hẹn | Trường | Chỉ xác nhận lịch Chờ xác nhận (kể cả lịch tái khám do Nha sĩ tạo); kiểm tra lại trùng giờ và ca trực lúc xác nhận. | Service |
| LH-08 | Xem lịch làm việc | Hậu | Nha sĩ chỉ thấy lịch của chính mình; Y tá chỉ thấy ca hỗ trợ trong ca trực của mình kèm tên Nha sĩ phụ trách; chỉ xem, không sửa. | Service |

### Lượt khám

| Mã | Chức năng | Người làm | Ràng buộc | Nơi kiểm tra |
|---|---|---|---|---|
| LK-01 | Khám & ghi nhận điều trị | Hậu | Chỉ Nha sĩ được bắt đầu khám; Nha sĩ phải có ca trực hôm nay và chứng chỉ hành nghề còn hạn. | Service |
| LK-02 | Khám & ghi nhận điều trị | Hậu | Khám theo lịch: lịch phải Đã xác nhận, đúng ngày hôm nay và đúng Nha sĩ phụ trách. Khám vãng lai (không có lịch) vẫn được nhưng ghi rõ. | Service |
| LK-03 | Khám & ghi nhận điều trị | Hậu | Một bệnh nhân chỉ có một lượt Đang khám; một Nha sĩ chỉ khám một bệnh nhân tại một thời điểm. | Service |
| LK-04 | Khám & ghi nhận điều trị | Hậu | Chỉ Nha sĩ phụ trách sửa lượt khám Đang khám; hoàn tất khám phải có chẩn đoán và ít nhất một dịch vụ đã thực hiện (khám tổng quát cũng là một dịch vụ); chỉ ghi tình trạng răng mà không có dịch vụ thì chưa đủ. | Service |
| LK-05 | Khám & ghi nhận điều trị | Hậu | Khi hoàn tất khám: ghi giờ kết thúc (sau giờ bắt đầu), khóa lượt khám (CH-02) và chuyển lịch hẹn liên kết sang Hoàn thành. | Service |
| LK-06 | Kê đơn thuốc | Hậu | Chỉ kê khi lượt khám Đang khám; đơn không được rỗng khi lưu; thuốc trùng cảnh báo dị ứng của bệnh nhân bị chặn kèm tên tác nhân. | Service |
| LK-07 | Sơ đồ răng điều trị | Hậu | Số răng theo FDI hợp lệ: răng vĩnh viễn 11-18, 21-28, 31-38, 41-48 hoặc răng sữa 51-55, 61-65, 71-75, 81-85. Trong một lượt khám, cùng răng, cùng mặt răng, cùng dịch vụ thì chỉ một bản ghi (ghi lại là sửa); khác dịch vụ thì thêm bản ghi mới (một răng ghi được nhiều dịch vụ). | Service + CSDL |
| LK-08 | Sơ đồ răng điều trị | Hậu | Răng đã nhổ ở lượt khám trước chỉ được ghi Đã nhổ, Implant, Cầu răng và chỉ chọn dịch vụ Implant hoặc Cầu răng. | Service |
| LK-09 | Sơ đồ răng điều trị | Hậu | Dịch vụ chọn phải đang hoạt động; vật liệu không được trùng cảnh báo dị ứng. | Service |
| LK-10 | Chỉ định tái khám | Hậu | Ngày tái khám phải sau ngày khám và sau hôm nay; mỗi lượt khám có tối đa một lịch tái khám; đặt nhầm thì hủy lịch tái khám để đặt lại. | Service |
| LK-11 | Chỉ định tái khám | Hậu | Tái khám tạo lịch hẹn Chờ xác nhận (gọi Trường) và đi qua đủ ràng buộc đặt lịch LH-02, LH-03; nếu tạo lịch lỗi thì không lưu ngày tái khám. | Service |
| LK-12 | Khám & ghi nhận điều trị | Hậu | Hủy lượt khám: chỉ nha sĩ phụ trách, bắt buộc nhập lý do (ít nhất 5 ký tự), chỉ hủy được khi chưa ghi dịch vụ hay răng nào; lượt khám thành Đã hủy, lịch hẹn (nếu có) trở lại Đã xác nhận để khám lại, lịch tái khám đã đặt (nếu có) cũng bị hủy. | Service |
| LK-13 | Khám & ghi nhận điều trị | Hậu | Không bắt đầu khám cho bệnh nhân đã Ngừng theo dõi; lượt khám mở qua ngày hiện cảnh báo yêu cầu hoàn tất hoặc hủy. | Service + Màn hình |
| LK-14 | Khám & ghi nhận điều trị / Sơ đồ răng | Hậu | Cảnh báo (hỏi xác nhận, không chặn) khi chọn thủ thuật xâm lấn (Nhổ răng, Nội nha, Implant) cho bệnh nhân dùng thuốc chống đông hoặc có bệnh nền tiểu đường, tim mạch, huyết áp, rối loạn đông máu; Implant cho dưới 18 tuổi; thẩm mỹ cho dưới 16 tuổi. | Service + Màn hình |
| LK-15 | Khám & ghi nhận điều trị | Hậu | Số lượng mỗi dịch vụ không gắn răng từ 1 đến 10. | Service |
| LK-16 | Chỉ định tái khám | Hậu | Chỉ hẹn tái khám trong vòng 180 ngày kể từ ngày khám. | Service |
| LK-17 | Sơ đồ răng điều trị | Hậu | Gỡ bản ghi răng ghi nhầm khỏi lượt khám đang khám: chỉ nha sĩ phụ trách, bản ghi phải thuộc đúng lượt khám; bản ghi gỡ không còn tính tiền. | Service |
| LK-18 | Khám & ghi nhận điều trị | Hậu | Khám vãng lai mà bệnh nhân có lịch hẹn Đã xác nhận trong hôm nay thì lịch đó được gắn vào lượt khám và chuyển Hoàn thành khi khám xong (ưu tiên lịch của chính nha sĩ này). | Service |
| LK-19 | Khám & ghi nhận điều trị | Hậu | Tổng số lượng của một dịch vụ không gắn răng trong một lượt khám tối đa 10 (cộng dồn các lần thêm). | Service |
| LK-20 | Khám & ghi nhận điều trị | Hậu | Dịch vụ gắn răng tính theo (răng, dịch vụ): trám nhiều mặt của cùng một răng chỉ tính một lần trên hóa đơn. | Service |
| LK-21 | Khám & ghi nhận điều trị | Hậu | Chỉ được bắt đầu khám dưới tên của chính người đang đăng nhập. | Service |

### Hóa đơn

| Mã | Chức năng | Người làm | Ràng buộc | Nơi kiểm tra |
|---|---|---|---|---|
| HD-01 | Danh sách chờ thanh toán | Trường | Chỉ hiện lượt khám Hoàn thành chưa có hóa đơn; lượt khám đã gắn hóa đơn không hiện lại. | Service |
| HD-02 | Lập hóa đơn | Trường | Chỉ lập từ lượt khám Hoàn thành chưa gắn hóa đơn; mỗi lượt khám đúng một hóa đơn (gắn một lần); hóa đơn có ít nhất một dòng chi tiết. | Service |
| HD-03 | Lập hóa đơn | Trường | Tổng tiền = tổng (đơn giá x số lượng) - giảm giá + thuế VAT; giảm giá không vượt tổng tiền dịch vụ; đơn giá lấy theo giá dịch vụ lúc lập và lưu cố định (đổi giá sau không ảnh hưởng hóa đơn cũ). | Service |
| HD-04 | Lập hóa đơn | Trường | Dòng chi tiết lấy từ dịch vụ đã thực hiện của lượt khám (gồm dịch vụ gắn răng và không gắn răng), không tự thêm dịch vụ ngoài lượt khám. | Service |
| HD-05 | Thanh toán & in hóa đơn | Trường | Chỉ thanh toán hóa đơn Chưa thanh toán; hình thức: Tiền mặt, Chuyển khoản, Thẻ, Ví điện tử; thanh toán xong thì khóa hóa đơn. | Service |
| HD-06 | Thanh toán & in hóa đơn | Trường | Chỉ in hóa đơn khi đã thanh toán; hóa đơn Đã hủy không in. | Màn hình + Service |

### Dịch vụ

| Mã | Chức năng | Người làm | Ràng buộc | Nơi kiểm tra |
|---|---|---|---|---|
| DV-01 | Xem danh mục dịch vụ & giá | Tâm | Mọi vai trò được xem; Nha sĩ và Lễ tân chỉ thấy dịch vụ đang hoạt động; chỉ xem giá và thời gian, không sửa. | Service |
| DV-02 | Thêm / sửa dịch vụ | Tâm | Chỉ Quản lý; tên dịch vụ bắt buộc và không trùng; giá không âm; thời gian dự kiến lớn hơn 0. | Service + CSDL |
| DV-03 | Thêm / sửa dịch vụ | Tâm | Đổi giá chỉ áp dụng cho lịch và hóa đơn lập sau đó; mọi thay đổi giá ghi nhật ký. | Service |

### Nhân viên

| Mã | Chức năng | Người làm | Ràng buộc | Nơi kiểm tra |
|---|---|---|---|---|
| NV-01 | Cập nhật hồ sơ nhân viên | Tâm | Chỉ Quản lý; họ tên, vai trò bắt buộc; số điện thoại 10 số; Nha sĩ bắt buộc có số và ngày hết hạn chứng chỉ hành nghề. | Service |
| NV-02 | Cập nhật hồ sơ nhân viên | Tâm | Nha sĩ hết hạn chứng chỉ thì không được xếp ca, nhận lịch hay bắt đầu khám; báo cảnh báo khi còn 30 ngày. | Service |
| NV-03 | Cập nhật hồ sơ nhân viên | Tâm | Không xóa nhân viên, chỉ chuyển sang Nghỉ việc; nhân viên Nghỉ việc không được xếp ca và tài khoản bị khóa. | Service |
| NV-04 | Phân công ca trực | Tâm | Chỉ xếp nhân viên Đang làm việc; không xếp ca trong quá khứ; một nhân viên không có hai ca trùng ngày và ca. | Service + CSDL |
| NV-05 | Phân công ca trực | Tâm | Mỗi phòng và ca tối đa một Nha sĩ; Lễ tân xếp ở quầy lễ tân; không xóa ca đã có lịch hẹn của Nha sĩ đó. | Service |

### Tài khoản

| Mã | Chức năng | Người làm | Ràng buộc | Nơi kiểm tra |
|---|---|---|---|---|
| TK-01 | Đăng nhập / Đăng xuất | Tâm | Sai mật khẩu 5 lần liên tiếp thì khóa tài khoản; tài khoản khóa không đăng nhập được; đăng nhập đúng thì xóa bộ đếm sai; ghi nhật ký đăng nhập thất bại. | Service |
| TK-02 | Đăng nhập / Đăng xuất | Tâm | Chỉ nhân viên Đang làm việc đăng nhập được; sau đăng nhập mở menu theo vai trò (CH-01); đăng xuất xóa người dùng hiện tại. | Service |
| TK-03 | Tạo tài khoản & gán vai trò | Tâm | Chỉ Quản lý; mỗi nhân viên đúng một tài khoản; tên đăng nhập không trùng, từ 4 ký tự, không dấu cách; mật khẩu từ 6 ký tự. | Service + CSDL |
| TK-04 | Tạo tài khoản & gán vai trò | Tâm | Vai trò tài khoản luôn bằng vai trò của nhân viên; không tự hạ quyền hay khóa chính mình; luôn còn ít nhất một tài khoản Quản lý hoạt động. | Service |

## 5. Hàm ghép nối bổ sung

Các ràng buộc trên cần thêm những hàm sau (đã ghi vào sheet *Điểm ghép nối*):

| Bên cung cấp | Bên dùng | Hàm | Ý nghĩa | Hạn |
|---|---|---|---|---|
| Trường | Hậu | LichHenService.layTheoMa(maLichHen) | Hậu kiểm tra lịch Đã xác nhận, đúng ngày khi bắt đầu khám (LK-02) | Tuần 2 |
| Trường | Hậu | LichHenService.capNhatTrangThai(maLichHen, trangThai) | Hậu chuyển lịch sang Hoàn thành khi hoàn tất khám (LK-05) | Tuần 2 |
| Tâm | Hậu, Trường | CaTrucService.coCaTruc(maNV, ngay, caLam) | Kiểm tra Nha sĩ có ca trực khi đặt lịch và khi bắt đầu khám (LH-02, LK-01) | Tuần 2 |
| Tâm | Hậu, Trường | NhanVienService.chungChiConHan(maNV, ngay) | Nha sĩ còn chứng chỉ hành nghề tại ngày đó (LH-02, LK-01, NV-02) | Tuần 2 |
| Hậu | Trường | HoSoBenhAnService.taoHoSoRong(Connection, maBN) | Tạo hồ sơ rỗng cùng giao dịch với việc tạo bệnh nhân (BN-04) | Tuần 2 |
