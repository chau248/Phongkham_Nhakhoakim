USE NhaKhoaKim;
GO
SET QUOTED_IDENTIFIER ON;
SET ANSI_NULLS ON;
GO
-- Bộ đếm mã
MERGE dbo.BoDemMa AS t
USING (VALUES ('NV', 13), ('DV', 12), ('BN', 12), ('HS', 12), ('LH', 25), ('CA', 180), ('TK', 13),
              ('LT', 5), ('LK', 12), ('BG', 11), ('HD', 10), ('CT', 13)) AS s(tienTo, giaTri)
ON t.tienTo = s.tienTo
WHEN NOT MATCHED THEN INSERT (tienTo, giaTriHienTai) VALUES (s.tienTo, s.giaTri)
WHEN MATCHED AND t.giaTriHienTai < s.giaTri THEN UPDATE SET giaTriHienTai = s.giaTri;
GO

-- Nhân viên: 1 Quản lý, 5 Nha sĩ, 4 Y tá, 2 Lễ tân, 1 tài khoản Toàn quyền
INSERT INTO dbo.NhanVien (maNV, hoTen, vaiTro, soChungChiHanhNghe, ngayHetHanChungChi, gioiTinh, sdt, chuyenKhoa, ngayVaoLam, trangThai)
SELECT v.* FROM (VALUES
    (N'NV000001', N'Lê Quốc Bảo',          N'Quản lý', NULL,         NULL,                        N'Nam', '0900000001', NULL,               '2022-01-10', N'Đang làm việc'),
    (N'NV000002', N'Phạm Minh Khoa',        N'Nha sĩ',  N'CCHN-0001', DATEADD(YEAR, 3, GETDATE()), N'Nam', '0900000002', N'Nội nha',         '2022-03-01', N'Đang làm việc'),
    (N'NV000003', N'Đỗ Thu Hà',             N'Nha sĩ',  N'CCHN-0002', DATEADD(DAY, 20, GETDATE()), N'Nữ',  '0900000003', N'Chỉnh nha',       '2023-02-15', N'Đang làm việc'),
    (N'NV000004', N'Võ Ngọc Anh',           N'Y tá',    NULL,         NULL,                        N'Nữ',  '0900000004', NULL,               '2023-06-01', N'Đang làm việc'),
    (N'NV000005', N'Bùi Thanh Tùng',        N'Y tá',    NULL,         NULL,                        N'Nam', '0900000005', NULL,               '2024-01-08', N'Đang làm việc'),
    (N'NV000006', N'Ngô Mỹ Linh',           N'Lễ tân',  NULL,         NULL,                        N'Nữ',  '0900000006', NULL,               '2023-09-20', N'Đang làm việc'),
    (N'NV000007', N'Trần Quang Vinh',       N'Nha sĩ',  N'CCHN-0003', DATEADD(YEAR, 2, GETDATE()), N'Nam', '0900000007', N'Nha tổng quát',   '2024-05-02', N'Đang làm việc'),
    (N'NV000008', N'Nguyễn Thị Hồng Nhung', N'Nha sĩ',  N'CCHN-0004', DATEADD(YEAR, 4, GETDATE()), N'Nữ',  '0900000008', N'Phục hình răng',  '2021-08-16', N'Đang làm việc'),
    (N'NV000009', N'Lâm Gia Huy',           N'Y tá',    NULL,         NULL,                        N'Nam', '0900000009', NULL,               '2024-09-10', N'Đang làm việc'),
    (N'NV000010', N'Tạ Bảo Châu',           N'Lễ tân',  NULL,         NULL,                        N'Nữ',  '0900000010', NULL,               '2025-02-03', N'Đang làm việc'),
    (N'NV000011', N'Huỳnh Thị Yến',         N'Y tá',    NULL,         NULL,                        N'Nữ',  '0900000011', NULL,               '2025-06-16', N'Đang làm việc'),
    (N'NV000012', N'Phan Đức Thịnh',        N'Nha sĩ',  N'CCHN-0005', DATEADD(DAY, -10, GETDATE()), N'Nam', '0900000012', N'Nha chu',        '2020-11-05', N'Nghỉ việc'),
    (N'NV000013', N'Quản trị hệ thống',     N'Toàn quyền', NULL,      NULL,                        N'Khác', '0900000013', NULL,              '2022-01-01', N'Đang làm việc')
) AS v(maNV, hoTen, vaiTro, soChungChiHanhNghe, ngayHetHanChungChi, gioiTinh, sdt, chuyenKhoa, ngayVaoLam, trangThai)
WHERE NOT EXISTS (SELECT 1 FROM dbo.NhanVien n WHERE n.maNV = v.maNV);

-- Chạy lại thì chứng chỉ của hai nhân viên dùng 
UPDATE dbo.NhanVien SET ngayHetHanChungChi = DATEADD(DAY, 20, CAST(GETDATE() AS DATE)) WHERE maNV = N'NV000003';
UPDATE dbo.NhanVien SET ngayHetHanChungChi = DATEADD(DAY, -10, CAST(GETDATE() AS DATE)) WHERE maNV = N'NV000012';
GO

-- Tài khoản mẫu
INSERT INTO dbo.TaiKhoan (maTaiKhoan, maNV, tenDangNhap, matKhau, vaiTro, trangThaiKhoa)
SELECT v.* FROM (VALUES
    (N'TK000001', N'NV000001', 'quanly', 's1$5eed0000a2c3d4e5$a04d546f626688a5b882b8c63fae96edf77838ff43c361b6442d1c9179876bb1', N'Quản lý', 0),
    (N'TK000002', N'NV000002', 'nhasi1', 's1$5eed0000a3d4e5f6$137616b70a4a14a5c48178a852830bc2390cb438aa3c52980a79be759d34ccfc', N'Nha sĩ',  0),
    (N'TK000003', N'NV000003', 'nhasi2', 's1$5eed0000a4e5f707$2174ea0f5c029fa4f294091f197834be145e601980d89ef52c9e101f9b1b5516', N'Nha sĩ',  0),
    (N'TK000004', N'NV000004', 'yta1',   's1$5eed0000a5f70818$426b03b6819b0176a26dbee80eb519b777d7fcbf4b3e09746b1a925f14788f5c', N'Y tá',    0),
    (N'TK000005', N'NV000005', 'yta2',   's1$5eed0000a7081929$d7eabb0ab0dc3ba2b4cea9f040cf247a4cd27ead3e0841cd9514d881ccfd2d99', N'Y tá',    0),
    (N'TK000006', N'NV000006', 'letan1', 's1$5eed0000a8192a3a$7785fa9cd41f24d38fd3ffdf49fa6bd7153fcf3f21f4c97f49996f0ccfae2e2e', N'Lễ tân',  0),
    (N'TK000007', N'NV000007', 'nhasi3', 's1$5eed0000a92a3b4b$5aeae0a9bb1f69aecc97cddc943d4c280b1786585fc7d9a47c89ae7e74d13609', N'Nha sĩ',  0),
    (N'TK000008', N'NV000008', 'nhasi4', 's1$5eed0000aa3b4c5c$da72cf62b33e0be0531f1b843ce7339530429dc4a9fb9243cc88d096a4e2b464', N'Nha sĩ',  0),
    (N'TK000009', N'NV000009', 'yta3',   's1$5eed0000ab4c5d6d$e59924842b5fe528ddc330225703090954b0235fe6fae1c0050db89469c183bb', N'Y tá',    0),
    (N'TK000012', N'NV000012', 'nhasi5', 's1$5eed0000ae7f90a0$04b08276dfa6a9c9df3c2b54ff5b76c88b16e3252eb9f04f6908fb3ce0c182ce', N'Nha sĩ',  1),
    (N'TK000013', N'NV000013', 'admin',  's1$5eed0000a2c3d4e5$a04d546f626688a5b882b8c63fae96edf77838ff43c361b6442d1c9179876bb1', N'Toàn quyền', 0)
) AS v(maTaiKhoan, maNV, tenDangNhap, matKhau, vaiTro, trangThaiKhoa)
WHERE NOT EXISTS (SELECT 1 FROM dbo.TaiKhoan t WHERE t.maTaiKhoan = v.maTaiKhoan);

-- Hai tài khoản mẫu 
DELETE FROM dbo.TaiKhoan WHERE maTaiKhoan IN (N'TK000010', N'TK000011') AND lanDangNhapCuoi IS NULL;
GO

-- Dịch vụ
INSERT INTO dbo.DichVu (maDichVu, tenDichVu, gia, thoiGianDuKien, loaiDichVu, donViTinh, yeuCauLieuTrinh, lienQuanImplantCauRang)
SELECT v.* FROM (VALUES
    (N'DV000001', N'Khám tổng quát',        100000,  20, N'Khám',      N'Lần', 0, 0),
    (N'DV000002', N'Lấy cao răng',          200000,  30, N'Vệ sinh',   N'Lần', 0, 0),
    (N'DV000003', N'Trám răng composite',   300000,  45, N'Trám',      N'Răng', 0, 0),
    (N'DV000004', N'Nhổ răng',              500000,  45, N'Nhổ răng',  N'Răng', 0, 0),
    (N'DV000005', N'Điều trị tủy',         1500000,  90, N'Nội nha',   N'Răng', 1, 0),
    (N'DV000006', N'Tẩy trắng răng',       2500000,  60, N'Thẩm mỹ',   N'Hàm', 0, 0),
    (N'DV000007', N'Niềng răng mắc cài',  30000000,  60, N'Chỉnh nha', N'Hàm', 1, 0),
    (N'DV000008', N'Cấy ghép Implant',    20000000, 120, N'Implant',   N'Răng', 1, 1),
    (N'DV000009', N'Bọc răng sứ',          3000000,  60, N'Phục hình', N'Răng', 0, 0),
    (N'DV000010', N'Làm cầu răng sứ',      8000000,  90, N'Phục hình', N'Răng', 1, 1),
    (N'DV000011', N'Chụp X-quang răng',     150000,  15, N'Chẩn đoán', N'Lần', 0, 0),
    (N'DV000012', N'Nhổ răng khôn',         800000,  60, N'Nhổ răng',  N'Răng', 0, 0)
) AS v(maDichVu, tenDichVu, gia, thoiGianDuKien, loaiDichVu, donViTinh, yeuCauLieuTrinh, lienQuanImplantCauRang)
WHERE NOT EXISTS (SELECT 1 FROM dbo.DichVu d WHERE d.maDichVu = v.maDichVu);
GO

-- Ca trực
DELETE FROM dbo.CaTruc WHERE maCaTruc BETWEEN N'CA000001' AND N'CA000180';
GO
INSERT INTO dbo.CaTruc (maCaTruc, maNV, ngay, caLam, phong)
SELECT N'CA' + RIGHT('000000' + CAST((d.lech + 7) * 12 + m.stt AS VARCHAR(6)), 6), m.maNV,
       DATEADD(DAY, d.lech, CAST(GETDATE() AS DATE)), m.caLam, m.phong
FROM (VALUES (-7), (-6), (-5), (-4), (-3), (-2), (-1), (0), (1), (2), (3), (4), (5), (6), (7)) AS d(lech)
CROSS JOIN (VALUES
    (1,  N'NV000002', N'Sáng',   N'Phòng 1'),
    (2,  N'NV000002', N'Chiều',  N'Phòng 1'),
    (3,  N'NV000003', N'Sáng',   N'Phòng 2'),
    (4,  N'NV000004', N'Sáng',   N'Phòng 1'),
    (5,  N'NV000005', N'Chiều',  N'Phòng 2'),
    (6,  N'NV000006', N'Sáng',   N'Quầy lễ tân'),
    (7,  N'NV000003', N'Chiều',  N'Phòng 2'),
    (8,  N'NV000007', N'Sáng',   N'Phòng 3'),
    (9,  N'NV000008', N'Chiều',  N'Phòng 3'),
    (10, N'NV000009', N'Sáng',   N'Phòng 3'),
    (11, N'NV000010', N'Chiều',  N'Quầy lễ tân'),
    (12, N'NV000011', N'Chiều',  N'Phòng 3')
) AS m(stt, maNV, caLam, phong)
WHERE NOT EXISTS (SELECT 1 FROM dbo.CaTruc c WHERE c.maNV = m.maNV AND c.ngay = DATEADD(DAY, d.lech, CAST(GETDATE() AS DATE)) AND c.caLam = m.caLam);
GO

-- Bệnh nhân
INSERT INTO dbo.BenhNhan (maBN, hoTen, ngaySinh, gioiTinh, sdt, diaChi, email, nguoiLienHeKhanCap, sdtNguoiLienHe, trangThai)
SELECT v.* FROM (VALUES
    (N'BN000001', N'Nguyễn Văn An',          '1990-05-12', N'Nam', '0911000001', N'Quận 1, TP. Hồ Chí Minh',       'an.nguyen@example.com',  NULL,                  NULL,         N'Đang điều trị'),
    (N'BN000002', N'Trần Thị Bình',          '1985-11-30', N'Nữ',  '0911000002', N'Quận 3, TP. Hồ Chí Minh',       NULL,                     NULL,                  NULL,         N'Đang điều trị'),
    (N'BN000003', N'Lê Hoàng Cường',         '2012-02-20', N'Nam', '0911000003', N'Quận 7, TP. Hồ Chí Minh',       NULL,                     N'Lê Văn Hùng (cha)',  '0922000003', N'Đang điều trị'),
    (N'BN000004', N'Phạm Thị Dung',          '1978-03-08', N'Nữ',  '0911000004', N'Quận 5, TP. Hồ Chí Minh',       'dung.pham@example.com',  NULL,                  NULL,         N'Đang điều trị'),
    (N'BN000005', N'Hoàng Văn Em',           '2001-09-17', N'Nam', '0911000005', N'TP. Thủ Đức, TP. Hồ Chí Minh',  NULL,                     NULL,                  NULL,         N'Đang điều trị'),
    (N'BN000006', N'Vũ Thị Phương',          '1995-07-25', N'Nữ',  '0911000006', N'Quận Bình Thạnh, TP. Hồ Chí Minh', 'phuong.vu@example.com', NULL,                  NULL,         N'Đang điều trị'),
    (N'BN000007', N'Đặng Minh Quân',         '1969-12-02', N'Nam', '0911000007', N'Quận Gò Vấp, TP. Hồ Chí Minh',  NULL,                     N'Đặng Thị Lan (vợ)',  '0922000007', N'Đang điều trị'),
    (N'BN000008', N'Bùi Thị Hạnh',           '2015-04-14', N'Nữ',  '0911000008', N'Quận 10, TP. Hồ Chí Minh',      NULL,                     N'Bùi Văn Lợi (cha)',  '0922000008', N'Đang điều trị'),
    (N'BN000009', N'Ngô Thanh Sơn',          '1982-01-29', N'Nam', '0911000009', N'Quận Tân Bình, TP. Hồ Chí Minh', NULL,                    NULL,                  NULL,         N'Đang điều trị'),
    (N'BN000010', N'Dương Thị Kim Ngân',     '1999-10-05', N'Nữ',  '0911000010', N'Quận 4, TP. Hồ Chí Minh',       'ngan.duong@example.com', NULL,                  NULL,         N'Đang điều trị'),
    (N'BN000011', N'Trịnh Văn Tài',          '1955-06-18', N'Nam', '0911000011', N'Quận 6, TP. Hồ Chí Minh',       NULL,                     N'Trịnh Thị Hoa (con)', '0922000011', N'Ngừng theo dõi'),
    (N'BN000012', N'Lý Thị Mai',             '1988-08-08', N'Nữ',  '0911000012', N'Quận Phú Nhuận, TP. Hồ Chí Minh', NULL,                   NULL,                  NULL,         N'Đang điều trị')
) AS v(maBN, hoTen, ngaySinh, gioiTinh, sdt, diaChi, email, nguoiLienHeKhanCap, sdtNguoiLienHe, trangThai)
WHERE NOT EXISTS (SELECT 1 FROM dbo.BenhNhan b WHERE b.maBN = v.maBN);
GO

-- Ngày đăng ký của bệnh nhân mẫu
UPDATE b SET ngayDangKy = DATEADD(DAY, v.lech, CAST(GETDATE() AS DATE))
FROM dbo.BenhNhan b
JOIN (VALUES (N'BN000001', -400), (N'BN000002', -1700), (N'BN000003', -300), (N'BN000004', -500), (N'BN000005', -90), (N'BN000006', -60),
             (N'BN000007', -800), (N'BN000008', -250), (N'BN000009', -120), (N'BN000010', -45), (N'BN000011', -2000), (N'BN000012', -300)) AS v(ma, lech)
  ON v.ma = b.maBN
WHERE b.ngayDangKy > DATEADD(DAY, v.lech, CAST(GETDATE() AS DATE));
GO

-- Hồ sơ bệnh án (BN000007 tiểu đường, BN000002 huyết áp cao, BN000011 dùng Warfarin để thử cảnh báo bệnh nền / thuốc chống đông)
INSERT INTO dbo.HoSoBenhAn (maHoSo, maBN, tienSuBenh, nhomMau, benhNen, thuocDangSuDung)
SELECT v.* FROM (VALUES
    (N'HS000001', N'BN000001', N'Chưa ghi nhận bệnh lý đặc biệt',        N'O',  NULL,                     NULL),
    (N'HS000002', N'BN000002', N'Đã nhổ răng 36 năm 2022',               N'A',  N'Huyết áp cao',          NULL),
    (N'HS000003', N'BN000003', NULL,                                     NULL,  NULL,                     NULL),
    (N'HS000004', N'BN000004', N'Từng niềng răng năm 2015',              N'B',  NULL,                     NULL),
    (N'HS000005', N'BN000005', NULL,                                     N'O',  NULL,                     NULL),
    (N'HS000006', N'BN000006', N'Chưa ghi nhận bệnh lý đặc biệt',        N'A',  NULL,                     NULL),
    (N'HS000007', N'BN000007', N'Đã làm cầu răng năm 2018',              N'B',  N'Tiểu đường tuýp 2',     N'Metformin 500mg'),
    (N'HS000008', N'BN000008', NULL,                                     N'AB', NULL,                     NULL),
    (N'HS000009', N'BN000009', N'Hay ê buốt khi ăn đồ lạnh',             N'O',  NULL,                     NULL),
    (N'HS000010', N'BN000010', NULL,                                     N'A',  NULL,                     NULL),
    (N'HS000011', N'BN000011', N'Phẫu thuật tim năm 2019',               N'AB', N'Bệnh tim mạch',         N'Warfarin 3mg'),
    (N'HS000012', N'BN000012', N'Đã cấy Implant răng 36 trong năm nay',  N'O',  NULL,                     NULL)
) AS v(maHoSo, maBN, tienSuBenh, nhomMau, benhNen, thuocDangSuDung)
WHERE NOT EXISTS (SELECT 1 FROM dbo.HoSoBenhAn h WHERE h.maHoSo = v.maHoSo);
GO

-- Lịch hẹn: lech = số ngày so với hôm nay (0 hôm nay, 1 ngày mai, -1 hôm qua); mỗi lịch nằm đúng ca trực của nha sĩ
IF OBJECT_ID(N'tempdb..#LichHenMau') IS NOT NULL DROP TABLE #LichHenMau;
SELECT * INTO #LichHenMau FROM (VALUES
    (N'LH000001', N'BN000001', N'NV000002', N'DV000002',  0, '09:00', N'Đã xác nhận',  N'Phòng 1', N'Trực tiếp', NULL),
    (N'LH000002', N'BN000002', N'NV000002', N'DV000008',  0, '09:30', N'Chờ xác nhận', N'Phòng 1', N'Điện thoại', NULL),
    (N'LH000003', N'BN000004', N'NV000003', N'DV000001',  0, '08:30', N'Đã xác nhận',  N'Phòng 2', N'Online',     NULL),
    (N'LH000004', N'BN000005', N'NV000003', N'DV000007',  0, '14:00', N'Đã xác nhận',  N'Phòng 2', N'Trực tiếp',  NULL),
    (N'LH000005', N'BN000006', N'NV000007', N'DV000006',  0, '09:30', N'Chờ xác nhận', N'Phòng 3', N'Online',     NULL),
    (N'LH000006', N'BN000009', N'NV000002', N'DV000005',  0, '15:00', N'Đã xác nhận',  N'Phòng 1', N'Điện thoại', NULL),
    (N'LH000007', N'BN000007', N'NV000008', N'DV000010',  1, '14:00', N'Chờ xác nhận', N'Phòng 3', N'Điện thoại', NULL),
    (N'LH000008', N'BN000008', N'NV000003', N'DV000003',  1, '10:00', N'Đã xác nhận',  N'Phòng 2', N'Trực tiếp',  NULL),
    (N'LH000009', N'BN000010', N'NV000007', N'DV000002',  2, '09:00', N'Chờ xác nhận', N'Phòng 3', N'Online',     NULL),
    (N'LH000010', N'BN000003', N'NV000003', N'DV000001', -6, '08:30', N'Hoàn thành',   N'Phòng 2', N'Trực tiếp',  NULL),
    (N'LH000011', N'BN000012', N'NV000008', N'DV000009', -2, '14:30', N'Không đến',    N'Phòng 3', N'Điện thoại', NULL),
    (N'LH000012', N'BN000001', N'NV000002', N'DV000003', -3, '16:00', N'Đã hủy',       N'Phòng 1', N'Điện thoại', N'Bệnh nhân bận việc đột xuất'),
    (N'LH000013', N'BN000001', N'NV000002', N'DV000002', -7, '09:00', N'Hoàn thành',   N'Phòng 1', N'Trực tiếp',  NULL),
    (N'LH000014', N'BN000010', N'NV000003', N'DV000003', -6, '10:30', N'Hoàn thành',   N'Phòng 2', N'Điện thoại', NULL),
    (N'LH000015', N'BN000006', N'NV000007', N'DV000002', -5, '08:30', N'Hoàn thành',   N'Phòng 3', N'Online',     NULL),
    (N'LH000016', N'BN000004', N'NV000008', N'DV000009', -4, '15:00', N'Không đến',    N'Phòng 3', N'Điện thoại', NULL),
    (N'LH000017', N'BN000002', N'NV000002', N'DV000011', -4, '10:00', N'Hoàn thành',   N'Phòng 1', N'Trực tiếp',  NULL),
    (N'LH000018', N'BN000009', N'NV000002', N'DV000005',  3, '09:00', N'Đã xác nhận',  N'Phòng 1', N'Điện thoại', NULL),
    (N'LH000019', N'BN000012', N'NV000003', N'DV000001',  3, '15:30', N'Chờ xác nhận', N'Phòng 2', N'Online',     NULL),
    (N'LH000020', N'BN000005', N'NV000003', N'DV000007',  4, '09:00', N'Đã xác nhận',  N'Phòng 2', N'Trực tiếp',  NULL),
    (N'LH000021', N'BN000008', N'NV000007', N'DV000003',  5, '10:00', N'Chờ xác nhận', N'Phòng 3', N'Điện thoại', NULL),
    (N'LH000022', N'BN000001', N'NV000008', N'DV000009',  6, '14:00', N'Đã xác nhận',  N'Phòng 3', N'Online',     NULL),
    (N'LH000023', N'BN000003', N'NV000002', N'DV000002',  7, '08:30', N'Chờ xác nhận', N'Phòng 1', N'Trực tiếp',  NULL),
    (N'LH000024', N'BN000007', N'NV000008', N'DV000010',  7, '15:30', N'Chờ xác nhận', N'Phòng 3', N'Điện thoại', NULL),
    (N'LH000025', N'BN000006', N'NV000007', N'DV000006',  2, '10:30', N'Đã hủy',       N'Phòng 3', N'Online',     N'Bệnh nhân xin đổi sang tuần sau')
) AS x(maLichHen, maBN, maNhanVien, maDichVu, lech, gioHen, trangThai, phong, hinhThucDat, lyDoHuy);

INSERT INTO dbo.LichHen (maLichHen, maBN, maNhanVien, maDichVu, ngayHen, gioHen, trangThai, phong, hinhThucDat, lyDoHuy)
SELECT v.maLichHen, v.maBN, v.maNhanVien, v.maDichVu, DATEADD(DAY, v.lech, CAST(GETDATE() AS DATE)), v.gioHen, v.trangThai,
       v.phong, v.hinhThucDat, v.lyDoHuy
FROM #LichHenMau v
WHERE NOT EXISTS (SELECT 1 FROM dbo.LichHen l WHERE l.maLichHen = v.maLichHen);

-- Chạy lại thì dời ngày của các lịch mẫu theo hôm nay
UPDATE l SET ngayHen = DATEADD(DAY, v.lech, CAST(GETDATE() AS DATE))
FROM dbo.LichHen l JOIN #LichHenMau v ON v.maLichHen = l.maLichHen;
GO

-- Liệu trình: LT000001 của BN000002 đang cấy Implant; các liệu trình khác cho dịch vụ nhiều buổi
IF OBJECT_ID(N'tempdb..#LieuTrinhMau') IS NOT NULL DROP TABLE #LieuTrinhMau;
SELECT * INTO #LieuTrinhMau FROM (VALUES
    (N'LT000001', N'BN000002', N'Implant',   3,  N'Đã nhổ răng 36, chờ cấy trụ',          -45,  120, NULL, 20000000, N'Đang thực hiện', N'NV000002'),
    (N'LT000002', N'BN000005', N'Chỉnh nha', 24, N'Đang siết dây cung',                   -60,  600, NULL, 30000000, N'Đang thực hiện', N'NV000003'),
    (N'LT000003', N'BN000009', N'Nội nha',   3,  N'Đã lấy tủy, chờ trám bít ống tủy',     -14,   14, NULL,  1500000, N'Đang thực hiện', N'NV000002'),
    (N'LT000004', N'BN000007', N'Phục hình', 3,  N'Đã mài răng, chờ gắn cầu sứ',          -21,   10, NULL,  8000000, N'Đang thực hiện', N'NV000008'),
    (N'LT000005', N'BN000012', N'Implant',   3,  N'Hoàn tất gắn mão sứ',                 -150,   -6,   -6, 23000000, N'Hoàn thành',     N'NV000008')
) AS x(maLieuTrinh, maBN, loaiDichVu, soBuoiDuKien, giaiDoanHienTai, batDau, duKien, ketThuc, tongChiPhiDuKien, trangThai, maNhaSiPhuTrach);

INSERT INTO dbo.LieuTrinh (maLieuTrinh, maBN, loaiDichVu, soBuoiDuKien, giaiDoanHienTai, ngayBatDau, ngayDuKienKetThuc,
                           ngayKetThucThucTe, tongChiPhiDuKien, trangThai, maNhaSiPhuTrach)
SELECT v.maLieuTrinh, v.maBN, v.loaiDichVu, v.soBuoiDuKien, v.giaiDoanHienTai,
       DATEADD(DAY, v.batDau, CAST(GETDATE() AS DATE)), DATEADD(DAY, v.duKien, CAST(GETDATE() AS DATE)),
       CASE WHEN v.ketThuc IS NULL THEN NULL ELSE DATEADD(DAY, v.ketThuc, CAST(GETDATE() AS DATE)) END,
       v.tongChiPhiDuKien, v.trangThai, v.maNhaSiPhuTrach
FROM #LieuTrinhMau v
WHERE NOT EXISTS (SELECT 1 FROM dbo.LieuTrinh l WHERE l.maLieuTrinh = v.maLieuTrinh);

UPDATE l SET ngayBatDau = DATEADD(DAY, v.batDau, CAST(GETDATE() AS DATE)),
             ngayDuKienKetThuc = DATEADD(DAY, v.duKien, CAST(GETDATE() AS DATE)),
             ngayKetThucThucTe = CASE WHEN v.ketThuc IS NULL THEN l.ngayKetThucThucTe ELSE DATEADD(DAY, v.ketThuc, CAST(GETDATE() AS DATE)) END
FROM dbo.LieuTrinh l JOIN #LieuTrinhMau v ON v.maLieuTrinh = l.maLieuTrinh;
GO

-- Hóa đơn và chi tiết (lập lúc 17:00 cùng ngày với lượt khám; tổng tiền được tính lại từ chi tiết ngay sau khi chèn)
IF OBJECT_ID(N'tempdb..#HoaDonMau') IS NOT NULL DROP TABLE #HoaDonMau;
SELECT * INTO #HoaDonMau FROM (VALUES
    (N'HD000001', N'BN000001', N'NV000006', -7, 0,      N'Tiền mặt',     N'Đã thanh toán',  NULL),
    (N'HD000002', N'BN000003', N'NV000006', -6, 0,      N'Chuyển khoản', N'Đã thanh toán',  NULL),
    (N'HD000003', N'BN000004', N'NV000010', -5, 0,      N'Thẻ',          N'Đã thanh toán',  NULL),
    (N'HD000004', N'BN000009', N'NV000006', -3, 0,      N'Tiền mặt',     N'Đã thanh toán',  NULL),
    (N'HD000005', N'BN000006', N'NV000010', -2, 250000, N'Ví điện tử',   N'Đã thanh toán',  N'Giảm giá khách hàng mới'),
    (N'HD000006', N'BN000007', N'NV000006', -7, 0,      N'Chuyển khoản', N'Chưa thanh toán', N'Bệnh nhân hẹn thanh toán khi gắn cầu sứ'),
    (N'HD000007', N'BN000008', N'NV000010', -1, 0,      N'Thẻ',          N'Đã thanh toán',  NULL),
    (N'HD000008', N'BN000012', N'NV000006', -6, 0,      N'Ví điện tử',   N'Đã thanh toán',  NULL),
    (N'HD000009', N'BN000010', N'NV000010', -2, 0,      N'Tiền mặt',     N'Đã hủy',         N'Hủy do bệnh nhân không lấy kết quả'),
    (N'HD000010', N'BN000003', N'NV000006', -1, 0,      N'Tiền mặt',     N'Chưa thanh toán', NULL)
) AS x(maHoaDon, maBN, maNhanVienLap, lech, tienGiamGia, hinhThuc, trangThai, ghiChu);

INSERT INTO dbo.HoaDon (maHoaDon, maBN, maNhanVienLap, ngayLap, tienGiamGia, hinhThucThanhToan, trangThaiThanhToan, ghiChu, ngayThanhToan)
SELECT v.maHoaDon, v.maBN, v.maNhanVienLap, x.gio, v.tienGiamGia, v.hinhThuc, v.trangThai, v.ghiChu,
       CASE WHEN v.trangThai = N'Đã thanh toán' THEN x.gio ELSE NULL END
FROM #HoaDonMau v
CROSS APPLY (SELECT DATEADD(HOUR, 17, CAST(DATEADD(DAY, v.lech, CAST(GETDATE() AS DATE)) AS DATETIME2)) AS gio) x
WHERE NOT EXISTS (SELECT 1 FROM dbo.HoaDon h WHERE h.maHoaDon = v.maHoaDon);

-- Chạy lại thì dời ngày lập / ngày thanh toán của hóa đơn mẫu
UPDATE h SET ngayLap = x.gio, ngayThanhToan = CASE WHEN h.ngayThanhToan IS NULL THEN NULL ELSE x.gio END
FROM dbo.HoaDon h JOIN #HoaDonMau v ON v.maHoaDon = h.maHoaDon
CROSS APPLY (SELECT DATEADD(HOUR, 17, CAST(DATEADD(DAY, v.lech, CAST(GETDATE() AS DATE)) AS DATETIME2)) AS gio) x;
GO

INSERT INTO dbo.ChiTietHoaDon (maChiTiet, maHoaDon, maDichVu, soLuong, donGia, soFDIRang)
SELECT v.* FROM (VALUES
    (N'CT000001', N'HD000001', N'DV000002', 1,  200000, NULL),
    (N'CT000002', N'HD000002', N'DV000003', 1,  300000, 16),
    (N'CT000003', N'HD000002', N'DV000001', 1,  100000, NULL),
    (N'CT000004', N'HD000003', N'DV000012', 1,  800000, 38),
    (N'CT000005', N'HD000004', N'DV000005', 1, 1500000, 36),
    (N'CT000006', N'HD000005', N'DV000006', 1, 2500000, NULL),
    (N'CT000007', N'HD000006', N'DV000010', 1, 8000000, 45),
    (N'CT000008', N'HD000007', N'DV000003', 1,  300000, 75),
    (N'CT000009', N'HD000008', N'DV000009', 1, 3000000, 36),
    (N'CT000010', N'HD000009', N'DV000011', 1,  150000, NULL),
    (N'CT000011', N'HD000009', N'DV000001', 1,  100000, NULL),
    (N'CT000012', N'HD000010', N'DV000001', 1,  100000, NULL),
    (N'CT000013', N'HD000006', N'DV000010', 1, 8000000, 47)
) AS v(maChiTiet, maHoaDon, maDichVu, soLuong, donGia, soFDIRang)
WHERE NOT EXISTS (SELECT 1 FROM dbo.ChiTietHoaDon c WHERE c.maChiTiet = v.maChiTiet);
GO

-- CSDL cũ: cầu răng của HD000006 gắn răng 46, nay đổi thành hai răng trụ 45 và 47
UPDATE dbo.ChiTietHoaDon SET soFDIRang = 45 WHERE maChiTiet = N'CT000007' AND soFDIRang = 46;


-- Lượt khám cũ của BN000002: đã nhổ răng 36 (để thử quy tắc răng đã nhổ)
INSERT INTO dbo.LuotKham (maLuotKham, maHoSo, maNhanVienPhuTrach, maLieuTrinh, gioBatDau, gioKetThuc, lyDoKham, chanDoan, huongDieuTri, trangThai)
SELECT N'LK000001', N'HS000002', N'NV000002', N'LT000001', '2022-06-10 09:00', '2022-06-10 09:45',
       N'Đau răng hàm dưới', N'Sâu răng 36 không thể trám', N'Nhổ răng 36, sau đó cấy Implant', N'Hoàn thành'
WHERE NOT EXISTS (SELECT 1 FROM dbo.LuotKham WHERE maLuotKham = N'LK000001');
GO

-- Các lượt khám khác: lech = số ngày so với hôm nay, phut = thời gian khám (NULL = đang khám), taiKham = số ngày đến lần tái khám (NULL = không hẹn)
IF OBJECT_ID(N'tempdb..#LuotKhamMau') IS NOT NULL DROP TABLE #LuotKhamMau;
SELECT * INTO #LuotKhamMau FROM (VALUES
    (N'LK000002', N'HS000001', N'NV000002', NULL,        N'HD000001', -7, '09:00', 30,   N'Khám định kỳ',            N'Cao răng nhiều, sâu răng 26',          N'Lấy cao răng, hẹn trám răng 26', NULL, 5, N'Hoàn thành'),
    (N'LK000003', N'HS000003', N'NV000003', NULL,        N'HD000002', -6, '08:30', 40,   N'Răng hàm đau khi nhai',    N'Sâu răng 16 mặt nhai',                 N'Trám composite răng 16',         N'Paracetamol 500mg x 6 viên', NULL, N'Hoàn thành'),
    (N'LK000004', N'HS000004', N'NV000007', NULL,        N'HD000003', -5, '09:30', 60,   N'Răng khôn mọc lệch gây đau', N'Răng khôn 38 mọc lệch',              N'Nhổ răng khôn 38',               N'Amoxicillin 500mg x 15 viên', 7, N'Hoàn thành'),
    (N'LK000005', N'HS000005', N'NV000003', N'LT000002', NULL,        -4, '14:00', 50,   N'Tái khám niềng răng',       N'Răng di chuyển tốt',                   N'Siết dây cung, hẹn tái khám',    NULL, 3, N'Hoàn thành'),
    (N'LK000006', N'HS000009', N'NV000002', N'LT000003', N'HD000004', -3, '10:00', 60,   N'Đau răng 36 nhiều ngày',   N'Viêm tủy răng 36',                     N'Lấy tủy buổi 1',                 N'Ibuprofen 400mg x 10 viên', 2, N'Hoàn thành'),
    (N'LK000007', N'HS000006', N'NV000007', NULL,        N'HD000005', -2, '09:00', 45,   N'Muốn làm trắng răng',      N'Răng ố vàng nhẹ',                      N'Tẩy trắng răng bằng đèn LED',    NULL, NULL, N'Hoàn thành'),
    (N'LK000008', N'HS000007', N'NV000008', N'LT000004', N'HD000006', -7, '14:00', 60,   N'Mài răng làm cầu',         N'Mất răng 46, cần làm cầu răng',        N'Mài răng 45, 47 làm trụ cầu',    NULL, 6, N'Hoàn thành'),
    (N'LK000009', N'HS000008', N'NV000003', NULL,        N'HD000007', -1, '10:00', 30,   N'Răng sữa bị sâu',          N'Sâu răng sữa 75',                      N'Trám composite răng 75',         NULL, 7, N'Hoàn thành'),
    (N'LK000010', N'HS000012', N'NV000008', N'LT000005', N'HD000008', -6, '14:00', 90,   N'Gắn mão sứ trên Implant', N'Implant răng 36 tích hợp xương tốt', N'Gắn mão sứ Zirconia',            NULL, NULL, N'Hoàn thành'),
    (N'LK000011', N'HS000010', N'NV000007', NULL,        N'HD000009', -2, '10:00', 20,   N'Đau răng thoáng qua',      N'Chưa phát hiện bất thường',            N'Theo dõi thêm',                  NULL, NULL, N'Hoàn thành'),
    (N'LK000012', N'HS000004', N'NV000003', NULL,        NULL,         0, '08:30', NULL, N'Khám tổng quát theo lịch hẹn', NULL,                                   NULL,                              NULL, NULL, N'Đang khám')
) AS x(maLuotKham, maHoSo, maNV, maLieuTrinh, maHoaDon, lech, gio, phut, lyDoKham, chanDoan, huongDieuTri, donThuoc, taiKham, trangThai);

INSERT INTO dbo.LuotKham (maLuotKham, maHoSo, maNhanVienPhuTrach, maLieuTrinh, maHoaDon, gioBatDau, gioKetThuc, lyDoKham, chanDoan,
                          huongDieuTri, donThuoc, ngayTaiKham, trangThai)
SELECT v.maLuotKham, v.maHoSo, v.maNV, v.maLieuTrinh, v.maHoaDon, x.gioBatDau,
       CASE WHEN v.phut IS NULL THEN NULL ELSE DATEADD(MINUTE, v.phut, x.gioBatDau) END,
       v.lyDoKham, v.chanDoan, v.huongDieuTri, v.donThuoc,
       CASE WHEN v.taiKham IS NULL THEN NULL ELSE DATEADD(DAY, v.taiKham, CAST(GETDATE() AS DATE)) END, v.trangThai
FROM #LuotKhamMau v
CROSS APPLY (SELECT DATEADD(MINUTE, DATEDIFF(MINUTE, CAST('00:00' AS TIME), CAST(v.gio AS TIME)),
                            CAST(DATEADD(DAY, v.lech, CAST(GETDATE() AS DATE)) AS DATETIME2)) AS gioBatDau) x
WHERE NOT EXISTS (SELECT 1 FROM dbo.LuotKham k WHERE k.maLuotKham = v.maLuotKham);

-- Chạy lại thì dời giờ khám và ngày tái khám theo hôm nay
UPDATE k SET gioBatDau = x.gioBatDau,
             gioKetThuc = CASE WHEN k.gioKetThuc IS NULL OR v.phut IS NULL THEN k.gioKetThuc ELSE DATEADD(MINUTE, v.phut, x.gioBatDau) END,
             ngayTaiKham = CASE WHEN v.taiKham IS NULL THEN k.ngayTaiKham ELSE DATEADD(DAY, v.taiKham, CAST(GETDATE() AS DATE)) END
FROM dbo.LuotKham k JOIN #LuotKhamMau v ON v.maLuotKham = k.maLuotKham
CROSS APPLY (SELECT DATEADD(MINUTE, DATEDIFF(MINUTE, CAST('00:00' AS TIME), CAST(v.gio AS TIME)),
                            CAST(DATEADD(DAY, v.lech, CAST(GETDATE() AS DATE)) AS DATETIME2)) AS gioBatDau) x;
GO

-- Dịch vụ không gắn răng trong lượt khám (dịch vụ tính theo răng chỉ ghi ở Sơ đồ răng)
INSERT INTO dbo.LuotKhamDichVu (maLuotKham, maDichVu, soLuong)
SELECT v.* FROM (VALUES
    (N'LK000002', N'DV000002', 1),
    (N'LK000003', N'DV000001', 1),
    (N'LK000007', N'DV000006', 1),
    (N'LK000011', N'DV000011', 1),
    (N'LK000011', N'DV000001', 1),
    (N'LK000012', N'DV000001', 1)
) AS v(maLuotKham, maDichVu, soLuong)
WHERE NOT EXISTS (SELECT 1 FROM dbo.LuotKhamDichVu x WHERE x.maLuotKham = v.maLuotKham AND x.maDichVu = v.maDichVu);
GO

-- Sơ đồ răng: BG000001 là răng 36 đã nhổ của BN000002; các bản ghi khác ở các lượt khám còn lại
INSERT INTO dbo.RangDieuTri (maBanGhi, maLuotKham, soFDI, trangThai, vatLieu, ngayGhiNhan, maDichVu, daNho, mauHienThi)
SELECT v.* FROM (VALUES
    (N'BG000001', N'LK000001', 36, N'Đã nhổ',      NULL,          CAST('2022-06-10 09:30' AS DATETIME2), N'DV000004', 1, N'Xám'),
    (N'BG000002', N'LK000003', 16, N'Đã trám',     N'Composite',  SYSDATETIME(), N'DV000003', 0, N'Xanh dương'),
    (N'BG000003', N'LK000004', 38, N'Đã nhổ',      NULL,          SYSDATETIME(), N'DV000012', 1, N'Xám'),
    (N'BG000004', N'LK000006', 36, N'Sâu',         NULL,          SYSDATETIME(), N'DV000005', 0, N'Đỏ'),
    (N'BG000005', N'LK000008', 45, N'Cầu răng',    N'Sứ',         SYSDATETIME(), N'DV000010', 0, N'Cam'),
    (N'BG000006', N'LK000008', 47, N'Cầu răng',    N'Sứ',         SYSDATETIME(), N'DV000010', 0, N'Cam'),
    (N'BG000007', N'LK000009', 75, N'Đã trám',     N'Composite',  SYSDATETIME(), N'DV000003', 0, N'Xanh dương'),
    (N'BG000008', N'LK000010', 36, N'Bọc sứ',      N'Sứ Zirconia', SYSDATETIME(), N'DV000009', 0, N'Vàng'),
    (N'BG000009', N'LK000002', 26, N'Sâu',         NULL,          SYSDATETIME(), NULL,        0, N'Đỏ'),
    (N'BG000010', N'LK000012', 47, N'Sâu',         NULL,          SYSDATETIME(), NULL,        0, N'Đỏ'),
    (N'BG000011', N'LK000011', 14, N'Bình thường', NULL,          SYSDATETIME(), NULL,        0, N'Trắng')
) AS v(maBanGhi, maLuotKham, soFDI, trangThai, vatLieu, ngayGhiNhan, maDichVu, daNho, mauHienThi)
WHERE NOT EXISTS (SELECT 1 FROM dbo.RangDieuTri r WHERE r.maBanGhi = v.maBanGhi);

-- Giờ ghi nhận của các bản ghi răng mẫu
UPDATE r SET ngayGhiNhan = DATEADD(MINUTE, 10, k.gioBatDau)
FROM dbo.RangDieuTri r JOIN dbo.LuotKham k ON k.maLuotKham = r.maLuotKham
WHERE r.maBanGhi BETWEEN N'BG000002' AND N'BG000011';
GO

DROP TABLE #LichHenMau, #LieuTrinhMau, #HoaDonMau, #LuotKhamMau;
GO

PRINT N'Hoàn tất nạp dữ liệu mẫu.';
GO
