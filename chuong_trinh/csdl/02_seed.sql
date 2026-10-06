/* =====================================================================
   Phòng khám Nha khoa Kim - Nhóm 09
   02_seed.sql: dữ liệu mẫu dùng chung để cả nhóm có cùng dữ liệu khi code và test
   - Chạy SAU 01_schema.sql. Chạy lại không bị trùng (có kiểm tra tồn tại).
   - Toàn bộ tên, số điện thoại là dữ liệu giả.
   - Tài khoản mẫu có mật khẩu chữ thường 123456 (không băm), chỉ dùng để thử trên máy mình.
   - Ai cần thêm dữ liệu mẫu: sửa file này trong Pull Request riêng.
   ===================================================================== */
USE NhaKhoaKim;
GO
SET QUOTED_IDENTIFIER ON;
SET ANSI_NULLS ON;
GO

/* ---------- Bộ đếm mã: đặt bằng số bản ghi mẫu đã chèn bên dưới ---------- */
MERGE dbo.BoDemMa AS t
USING (VALUES ('NV', 6), ('DV', 10), ('BN', 3), ('HS', 3), ('LH', 2), ('CA', 6), ('DC', 4), ('TK', 6),
              ('LT', 1), ('LK', 1), ('BG', 1), ('HD', 0), ('CT', 0)) AS s(tienTo, giaTri)
ON t.tienTo = s.tienTo
WHEN NOT MATCHED THEN INSERT (tienTo, giaTriHienTai) VALUES (s.tienTo, s.giaTri);
GO

/* ---------- Nhân viên: 1 Quản lý, 2 Nha sĩ, 2 Y tá, 1 Lễ tân ---------- */
INSERT INTO dbo.NhanVien (maNV, hoTen, vaiTro, soChungChiHanhNghe, ngayHetHanChungChi, gioiTinh, sdt, chuyenKhoa, ngayVaoLam)
SELECT v.* FROM (VALUES
    (N'NV000001', N'Lê Quốc Bảo',    N'Quản lý', NULL,         NULL,                       N'Nam', '0900000001', NULL,           '2022-01-10'),
    (N'NV000002', N'Phạm Minh Khoa',  N'Nha sĩ',  N'CCHN-0001', DATEADD(YEAR, 3, GETDATE()), N'Nam', '0900000002', N'Nội nha',     '2022-03-01'),
    (N'NV000003', N'Đỗ Thu Hà',       N'Nha sĩ',  N'CCHN-0002', DATEADD(DAY, 20, GETDATE()), N'Nữ',  '0900000003', N'Chỉnh nha',   '2023-02-15'),
    (N'NV000004', N'Võ Ngọc Anh',     N'Y tá',    NULL,         NULL,                       N'Nữ',  '0900000004', NULL,           '2023-06-01'),
    (N'NV000005', N'Bùi Thanh Tùng',  N'Y tá',    NULL,         NULL,                       N'Nam', '0900000005', NULL,           '2024-01-08'),
    (N'NV000006', N'Ngô Mỹ Linh',     N'Lễ tân',  NULL,         NULL,                       N'Nữ',  '0900000006', NULL,           '2023-09-20')
) AS v(maNV, hoTen, vaiTro, soChungChiHanhNghe, ngayHetHanChungChi, gioiTinh, sdt, chuyenKhoa, ngayVaoLam)
WHERE NOT EXISTS (SELECT 1 FROM dbo.NhanVien n WHERE n.maNV = v.maNV);
GO

/* ---------- Dịch vụ (NV000003 có chứng chỉ sắp hết hạn trong 20 ngày để thử cảnh báo) ---------- */
INSERT INTO dbo.DichVu (maDichVu, tenDichVu, gia, thoiGianDuKien, loaiDichVu, donViTinh, yeuCauLieuTrinh, lienQuanImplantCauRang)
SELECT v.* FROM (VALUES
    (N'DV000001', N'Khám tổng quát',        100000,  20, N'Khám',     N'Lần', 0, 0),
    (N'DV000002', N'Lấy cao răng',          200000,  30, N'Vệ sinh',  N'Lần', 0, 0),
    (N'DV000003', N'Trám răng composite',   300000,  45, N'Trám',     N'Răng', 0, 0),
    (N'DV000004', N'Nhổ răng',              500000,  45, N'Nhổ răng', N'Răng', 0, 0),
    (N'DV000005', N'Điều trị tủy',         1500000,  90, N'Nội nha',  N'Răng', 1, 0),
    (N'DV000006', N'Tẩy trắng răng',       2500000,  60, N'Thẩm mỹ',  N'Hàm', 0, 0),
    (N'DV000007', N'Niềng răng mắc cài',  30000000,  60, N'Chỉnh nha', N'Hàm', 1, 0),
    (N'DV000008', N'Cấy ghép Implant',    20000000, 120, N'Implant',  N'Răng', 1, 1),
    (N'DV000009', N'Bọc răng sứ',          3000000,  60, N'Phục hình', N'Răng', 0, 0),
    (N'DV000010', N'Làm cầu răng sứ',      8000000,  90, N'Phục hình', N'Răng', 1, 1)
) AS v(maDichVu, tenDichVu, gia, thoiGianDuKien, loaiDichVu, donViTinh, yeuCauLieuTrinh, lienQuanImplantCauRang)
WHERE NOT EXISTS (SELECT 1 FROM dbo.DichVu d WHERE d.maDichVu = v.maDichVu);
GO

/* ---------- Ca trực hôm nay ---------- */
INSERT INTO dbo.CaTruc (maCaTruc, maNV, ngay, caLam, phong)
SELECT v.maCaTruc, v.maNV, CAST(GETDATE() AS DATE), v.caLam, v.phong FROM (VALUES
    (N'CA000001', N'NV000002', N'Sáng',   N'Phòng 1'),
    (N'CA000002', N'NV000002', N'Chiều',  N'Phòng 1'),
    (N'CA000003', N'NV000003', N'Sáng',   N'Phòng 2'),
    (N'CA000004', N'NV000004', N'Sáng',   N'Phòng 1'),
    (N'CA000005', N'NV000005', N'Chiều',  N'Phòng 2'),
    (N'CA000006', N'NV000006', N'Sáng',   N'Quầy lễ tân')
) AS v(maCaTruc, maNV, caLam, phong)
WHERE NOT EXISTS (SELECT 1 FROM dbo.CaTruc c WHERE c.maCaTruc = v.maCaTruc);
GO

/* ---------- Bệnh nhân và hồ sơ bệnh án (BN000002 có cảnh báo dị ứng để thử kiểm tra khi kê đơn) ---------- */
INSERT INTO dbo.BenhNhan (maBN, hoTen, ngaySinh, gioiTinh, sdt, diaChi)
SELECT v.* FROM (VALUES
    (N'BN000001', N'Nguyễn Văn An',   '1990-05-12', N'Nam', '0911000001', N'Quận 1, TP. Hồ Chí Minh'),
    (N'BN000002', N'Trần Thị Bình',   '1985-11-30', N'Nữ',  '0911000002', N'Quận 3, TP. Hồ Chí Minh'),
    (N'BN000003', N'Lê Hoàng Cường',  '2012-02-20', N'Nam', '0911000003', N'Quận 7, TP. Hồ Chí Minh')
) AS v(maBN, hoTen, ngaySinh, gioiTinh, sdt, diaChi)
WHERE NOT EXISTS (SELECT 1 FROM dbo.BenhNhan b WHERE b.maBN = v.maBN);
GO

UPDATE dbo.BenhNhan SET nguoiLienHeKhanCap = N'Lê Văn Hùng (cha)', sdtNguoiLienHe = '0922000003'
WHERE maBN = N'BN000003' AND nguoiLienHeKhanCap IS NULL;
GO

INSERT INTO dbo.HoSoBenhAn (maHoSo, maBN, tienSuBenh, canhBaoDiUng, nhomMau, benhNen)
SELECT v.* FROM (VALUES
    (N'HS000001', N'BN000001', N'Chưa ghi nhận bệnh lý đặc biệt', NULL,                      N'O', NULL),
    (N'HS000002', N'BN000002', N'Đã nhổ răng 36 năm 2022',        N'Penicillin, Lidocaine', N'A', N'Huyết áp cao'),
    (N'HS000003', N'BN000003', NULL,                              NULL,                      NULL, NULL)
) AS v(maHoSo, maBN, tienSuBenh, canhBaoDiUng, nhomMau, benhNen)
WHERE NOT EXISTS (SELECT 1 FROM dbo.HoSoBenhAn h WHERE h.maHoSo = v.maHoSo);
GO

/* ---------- Lịch hẹn hôm nay ---------- */
INSERT INTO dbo.LichHen (maLichHen, maBN, maNhanVien, maDichVu, ngayHen, gioHen, trangThai, phong, hinhThucDat)
SELECT v.maLichHen, v.maBN, v.maNhanVien, v.maDichVu, CAST(GETDATE() AS DATE), v.gioHen, v.trangThai, v.phong, v.hinhThucDat FROM (VALUES
    (N'LH000001', N'BN000001', N'NV000002', N'DV000002', '09:00', N'Đã xác nhận', N'Phòng 1', N'Trực tiếp'),
    (N'LH000002', N'BN000002', N'NV000002', N'DV000008', '10:30', N'Chờ xác nhận', N'Phòng 1', N'Điện thoại')
) AS v(maLichHen, maBN, maNhanVien, maDichVu, gioHen, trangThai, phong, hinhThucDat)
WHERE NOT EXISTS (SELECT 1 FROM dbo.LichHen l WHERE l.maLichHen = v.maLichHen);
GO

/* ---------- Dụng cụ và dụng cụ cần cho dịch vụ ---------- */
INSERT INTO dbo.DungCu (maDungCu, tenDungCu, loaiDichVu, trangThaiVoTrung, phong)
SELECT v.* FROM (VALUES
    (N'DC000001', N'Bộ khám cơ bản (gương, thám châm, kẹp)', N'Khám',     N'Đã tiệt trùng', N'Phòng 1'),
    (N'DC000002', N'Máy lấy cao răng siêu âm',                N'Vệ sinh',  N'Đã tiệt trùng', N'Phòng 1'),
    (N'DC000003', N'Bộ nhổ răng (kìm, bẩy)',                  N'Nhổ răng', N'Chờ xử lý',     N'Phòng 2'),
    (N'DC000004', N'Bộ cấy ghép Implant',                     N'Implant',  N'Đã tiệt trùng', N'Phòng 1')
) AS v(maDungCu, tenDungCu, loaiDichVu, trangThaiVoTrung, phong)
WHERE NOT EXISTS (SELECT 1 FROM dbo.DungCu d WHERE d.maDungCu = v.maDungCu);
GO

INSERT INTO dbo.DichVuDungCu (maDichVu, maDungCu, soLuong)
SELECT v.* FROM (VALUES
    (N'DV000001', N'DC000001', 1), (N'DV000002', N'DC000001', 1), (N'DV000002', N'DC000002', 1),
    (N'DV000004', N'DC000003', 1), (N'DV000008', N'DC000004', 1)
) AS v(maDichVu, maDungCu, soLuong)
WHERE NOT EXISTS (SELECT 1 FROM dbo.DichVuDungCu x WHERE x.maDichVu = v.maDichVu AND x.maDungCu = v.maDungCu);
GO

/* ---------- Tài khoản mẫu (mật khẩu 123456, không băm) ---------- */
INSERT INTO dbo.TaiKhoan (maTaiKhoan, maNV, tenDangNhap, matKhau, vaiTro)
SELECT v.* FROM (VALUES
    (N'TK000001', N'NV000001', 'quanly', '123456', N'Quản lý'),
    (N'TK000002', N'NV000002', 'nhasi1', '123456', N'Nha sĩ'),
    (N'TK000003', N'NV000003', 'nhasi2', '123456', N'Nha sĩ'),
    (N'TK000004', N'NV000004', 'yta1',   '123456', N'Y tá'),
    (N'TK000005', N'NV000005', 'yta2',   '123456', N'Y tá'),
    (N'TK000006', N'NV000006', 'letan1', '123456', N'Lễ tân')
) AS v(maTaiKhoan, maNV, tenDangNhap, matKhau, vaiTro)
WHERE NOT EXISTS (SELECT 1 FROM dbo.TaiKhoan t WHERE t.maTaiKhoan = v.maTaiKhoan);
GO

/* ---------- Liệu trình đang thực hiện của BN000002 (cấy ghép Implant, 3 buổi) ---------- */
INSERT INTO dbo.LieuTrinh (maLieuTrinh, maBN, loaiDichVu, soBuoiDuKien, giaiDoanHienTai, ngayDuKienKetThuc, tongChiPhiDuKien, maNhaSiPhuTrach)
SELECT N'LT000001', N'BN000002', N'Implant', 3, N'Đã nhổ răng 36, chờ cấy trụ', DATEADD(MONTH, 4, GETDATE()), 20000000, N'NV000002'
WHERE NOT EXISTS (SELECT 1 FROM dbo.LieuTrinh WHERE maLieuTrinh = N'LT000001');
GO

/* ---------- Lượt khám cũ của BN000002: đã nhổ răng 36 (để thử quy tắc răng đã nhổ) ---------- */
INSERT INTO dbo.LuotKham (maLuotKham, maHoSo, maNhanVienPhuTrach, maLieuTrinh, gioBatDau, gioKetThuc, lyDoKham, chanDoan, huongDieuTri, trangThai)
SELECT N'LK000001', N'HS000002', N'NV000002', N'LT000001', '2022-06-10 09:00', '2022-06-10 09:45',
       N'Đau răng hàm dưới', N'Sâu răng 36 không thể trám', N'Nhổ răng 36, sau đó cấy Implant', N'Hoàn thành'
WHERE NOT EXISTS (SELECT 1 FROM dbo.LuotKham WHERE maLuotKham = N'LK000001');
GO

INSERT INTO dbo.RangDieuTri (maBanGhi, maLuotKham, soFDI, trangThai, ngayGhiNhan, maDichVu, daNho, mauHienThi)
SELECT N'BG000001', N'LK000001', 36, N'Đã nhổ', '2022-06-10 09:30', N'DV000004', 1, N'Xám'
WHERE NOT EXISTS (SELECT 1 FROM dbo.RangDieuTri WHERE maBanGhi = N'BG000001');
GO

PRINT N'Hoàn tất nạp dữ liệu mẫu.';
GO
