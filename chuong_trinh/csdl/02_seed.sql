
USE NhaKhoaKim;
GO
SET QUOTED_IDENTIFIER ON;
SET ANSI_NULLS ON;
GO

/* ---------- Bộ đếm mã: đặt bằng số bản ghi mẫu bên dưới (chỉ tăng, không bao giờ giảm) ---------- */
MERGE dbo.BoDemMa AS t
USING (VALUES ('NV', 12), ('DV', 12), ('BN', 12), ('HS', 12), ('LH', 12), ('CA', 84), ('TK', 12),
              ('LT', 5), ('LK', 12), ('BG', 11), ('HD', 10), ('CT', 12)) AS s(tienTo, giaTri)
ON t.tienTo = s.tienTo
WHEN NOT MATCHED THEN INSERT (tienTo, giaTriHienTai) VALUES (s.tienTo, s.giaTri)
WHEN MATCHED AND t.giaTriHienTai < s.giaTri THEN UPDATE SET giaTriHienTai = s.giaTri;
GO

/* ---------- Nhân viên: 1 Quản lý, 5 Nha sĩ, 4 Y tá, 2 Lễ tân ----------
   NV000003 có chứng chỉ sắp hết hạn trong 20 ngày, NV000012 đã nghỉ việc (để thử cảnh báo và khóa tài khoản) */
INSERT INTO dbo.NhanVien (maNV, hoTen, vaiTro, soChungChiHanhNghe, ngayHetHanChungChi, gioiTinh, sdt, chuyenKhoa, ngayVaoLam, trangThai)
SELECT v.* FROM (VALUES
    (N'NV000001', N'Lê Quốc Bảo',       N'Quản lý', NULL,         NULL,                        N'Nam', '0900000001', NULL,               '2022-01-10', N'Đang làm việc'),
    (N'NV000002', N'Phạm Minh Khoa',     N'Nha sĩ',  N'CCHN-0001', DATEADD(YEAR, 3, GETDATE()), N'Nam', '0900000002', N'Nội nha',         '2022-03-01', N'Đang làm việc'),
    (N'NV000003', N'Đỗ Thu Hà',          N'Nha sĩ',  N'CCHN-0002', DATEADD(DAY, 20, GETDATE()), N'Nữ',  '0900000003', N'Chỉnh nha',       '2023-02-15', N'Đang làm việc'),
    (N'NV000004', N'Võ Ngọc Anh',        N'Y tá',    NULL,         NULL,                        N'Nữ',  '0900000004', NULL,               '2023-06-01', N'Đang làm việc'),
    (N'NV000005', N'Bùi Thanh Tùng',     N'Y tá',    NULL,         NULL,                        N'Nam', '0900000005', NULL,               '2024-01-08', N'Đang làm việc'),
    (N'NV000006', N'Ngô Mỹ Linh',        N'Lễ tân',  NULL,         NULL,                        N'Nữ',  '0900000006', NULL,               '2023-09-20', N'Đang làm việc'),
    (N'NV000007', N'Trần Quang Vinh',    N'Nha sĩ',  N'CCHN-0003', DATEADD(YEAR, 2, GETDATE()), N'Nam', '0900000007', N'Nha tổng quát',   '2024-05-02', N'Đang làm việc'),
    (N'NV000008', N'Nguyễn Thị Hồng Nhung', N'Nha sĩ', N'CCHN-0004', DATEADD(YEAR, 4, GETDATE()), N'Nữ', '0900000008', N'Phục hình răng',  '2021-08-16', N'Đang làm việc'),
    (N'NV000009', N'Lâm Gia Huy',        N'Y tá',    NULL,         NULL,                        N'Nam', '0900000009', NULL,               '2024-09-10', N'Đang làm việc'),
    (N'NV000010', N'Tạ Bảo Châu',        N'Lễ tân',  NULL,         NULL,                        N'Nữ',  '0900000010', NULL,               '2025-02-03', N'Đang làm việc'),
    (N'NV000011', N'Huỳnh Thị Yến',      N'Y tá',    NULL,         NULL,                        N'Nữ',  '0900000011', NULL,               '2025-06-16', N'Đang làm việc'),
    (N'NV000012', N'Phan Đức Thịnh',     N'Nha sĩ',  N'CCHN-0005', DATEADD(DAY, -10, GETDATE()), N'Nam', '0900000012', N'Nha chu',        '2020-11-05', N'Nghỉ việc')
) AS v(maNV, hoTen, vaiTro, soChungChiHanhNghe, ngayHetHanChungChi, gioiTinh, sdt, chuyenKhoa, ngayVaoLam, trangThai)
WHERE NOT EXISTS (SELECT 1 FROM dbo.NhanVien n WHERE n.maNV = v.maNV);
GO

/* ---------- Tài khoản mẫu: mật khẩu của mọi tài khoản là 123456 (lưu dạng băm PBKDF2, xem MatKhau.java); mỗi nhân viên một tài khoản, TK000012 bị khóa ---------- */
INSERT INTO dbo.TaiKhoan (maTaiKhoan, maNV, tenDangNhap, matKhau, vaiTro, trangThaiKhoa)
SELECT v.* FROM (VALUES
    (N'TK000001', N'NV000001', 'quanly', 's1$5eed0000a2c3d4e5$a04d546f626688a5b882b8c63fae96edf77838ff43c361b6442d1c9179876bb1', N'Quản lý', 0),
    (N'TK000002', N'NV000002', 'nhasi1', 's1$5eed0000a3d4e5f6$137616b70a4a14a5c48178a852830bc2390cb438aa3c52980a79be759d34ccfc', N'Nha sĩ',  0),
    (N'TK000003', N'NV000003', 'nhasi2', 's1$5eed0000a4e5f707$2174ea0f5c029fa4f294091f197834be145e601980d89ef52c9e101f9b1b5516', N'Nha sĩ',  0),
    (N'TK000004', N'NV000004', 'yta1', 's1$5eed0000a5f70818$426b03b6819b0176a26dbee80eb519b777d7fcbf4b3e09746b1a925f14788f5c', N'Y tá',    0),
    (N'TK000005', N'NV000005', 'yta2', 's1$5eed0000a7081929$d7eabb0ab0dc3ba2b4cea9f040cf247a4cd27ead3e0841cd9514d881ccfd2d99', N'Y tá',    0),
    (N'TK000006', N'NV000006', 'letan1', 's1$5eed0000a8192a3a$7785fa9cd41f24d38fd3ffdf49fa6bd7153fcf3f21f4c97f49996f0ccfae2e2e', N'Lễ tân',  0),
    (N'TK000007', N'NV000007', 'nhasi3', 's1$5eed0000a92a3b4b$5aeae0a9bb1f69aecc97cddc943d4c280b1786585fc7d9a47c89ae7e74d13609', N'Nha sĩ',  0),
    (N'TK000008', N'NV000008', 'nhasi4', 's1$5eed0000aa3b4c5c$da72cf62b33e0be0531f1b843ce7339530429dc4a9fb9243cc88d096a4e2b464', N'Nha sĩ',  0),
    (N'TK000009', N'NV000009', 'yta3', 's1$5eed0000ab4c5d6d$e59924842b5fe528ddc330225703090954b0235fe6fae1c0050db89469c183bb', N'Y tá',    0),
    (N'TK000010', N'NV000010', 'letan2', 's1$5eed0000ac5d6e7e$2b9c6d957da9049627e6b4bef6ec0b02fdff8a672350adcce49c6fa21241b4ec', N'Lễ tân',  0),
    (N'TK000011', N'NV000011', 'yta4', 's1$5eed0000ad6e7f8f$31b371c28d87771175574bfab38075d41e445183a02b217ce1b9d6f15f4909f5', N'Y tá',    0),
    (N'TK000012', N'NV000012', 'nhasi5', 's1$5eed0000ae7f90a0$04b08276dfa6a9c9df3c2b54ff5b76c88b16e3252eb9f04f6908fb3ce0c182ce', N'Nha sĩ',  1)
) AS v(maTaiKhoan, maNV, tenDangNhap, matKhau, vaiTro, trangThaiKhoa)
WHERE NOT EXISTS (SELECT 1 FROM dbo.TaiKhoan t WHERE t.maTaiKhoan = v.maTaiKhoan);
GO

/* ---------- Dịch vụ ---------- */
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

/* ---------- Ca trực: 7 ngày liên tiếp từ hôm nay, mỗi ngày cùng một mẫu 12 ca (mã CA000001-CA000084) ---------- */
INSERT INTO dbo.CaTruc (maCaTruc, maNV, ngay, caLam, phong)
SELECT N'CA' + RIGHT('000000' + CAST(d.lech * 12 + m.stt AS VARCHAR(6)), 6), m.maNV, DATEADD(DAY, d.lech, CAST(GETDATE() AS DATE)), m.caLam, m.phong
FROM (VALUES (0), (1), (2), (3), (4), (5), (6)) AS d(lech)
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

/* ---------- Bệnh nhân (BN000008 là trẻ em có người liên hệ khẩn cấp, BN000011 đã ngừng theo dõi) ---------- */
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

-- CSDL cũ đã có BN000003 mà chưa có người liên hệ khẩn cấp thì bổ sung
UPDATE dbo.BenhNhan SET nguoiLienHeKhanCap = N'Lê Văn Hùng (cha)', sdtNguoiLienHe = '0922000003'
WHERE maBN = N'BN000003' AND nguoiLienHeKhanCap IS NULL;
GO

/* ---------- Hồ sơ bệnh án (BN000002, BN000007, BN000011 có cảnh báo dị ứng để thử kiểm tra khi kê đơn) ---------- */
INSERT INTO dbo.HoSoBenhAn (maHoSo, maBN, tienSuBenh, canhBaoDiUng, nhomMau, benhNen, thuocDangSuDung)
SELECT v.* FROM (VALUES
    (N'HS000001', N'BN000001', N'Chưa ghi nhận bệnh lý đặc biệt',        NULL,                      N'O',  NULL,                     NULL),
    (N'HS000002', N'BN000002', N'Đã nhổ răng 36 năm 2022',               N'Penicillin, Lidocaine',  N'A',  N'Huyết áp cao',          NULL),
    (N'HS000003', N'BN000003', NULL,                                     NULL,                      NULL,  NULL,                     NULL),
    (N'HS000004', N'BN000004', N'Từng niềng răng năm 2015',              NULL,                      N'B',  NULL,                     NULL),
    (N'HS000005', N'BN000005', NULL,                                     NULL,                      N'O',  NULL,                     NULL),
    (N'HS000006', N'BN000006', N'Chưa ghi nhận bệnh lý đặc biệt',        N'Sulfa',                  N'A',  NULL,                     NULL),
    (N'HS000007', N'BN000007', N'Đã làm cầu răng năm 2018',              N'Aspirin',                N'B',  N'Tiểu đường tuýp 2',     N'Metformin 500mg'),
    (N'HS000008', N'BN000008', NULL,                                     NULL,                      N'AB', NULL,                     NULL),
    (N'HS000009', N'BN000009', N'Hay ê buốt khi ăn đồ lạnh',             NULL,                      N'O',  NULL,                     NULL),
    (N'HS000010', N'BN000010', NULL,                                     N'Latex',                  N'A',  NULL,                     NULL),
    (N'HS000011', N'BN000011', N'Phẫu thuật tim năm 2019',               N'Penicillin',             N'AB', N'Bệnh tim mạch',         N'Warfarin 3mg'),
    (N'HS000012', N'BN000012', N'Đã cấy Implant răng 36 trong năm nay',  NULL,                      N'O',  NULL,                     NULL)
) AS v(maHoSo, maBN, tienSuBenh, canhBaoDiUng, nhomMau, benhNen, thuocDangSuDung)
WHERE NOT EXISTS (SELECT 1 FROM dbo.HoSoBenhAn h WHERE h.maHoSo = v.maHoSo);
GO

/* ---------- Lịch hẹn: lech = số ngày so với hôm nay (0 hôm nay, 1 ngày mai, -1 hôm qua) ---------- */
INSERT INTO dbo.LichHen (maLichHen, maBN, maNhanVien, maDichVu, ngayHen, gioHen, trangThai, phong, hinhThucDat, lyDoHuy)
SELECT v.maLichHen, v.maBN, v.maNhanVien, v.maDichVu, DATEADD(DAY, v.lech, CAST(GETDATE() AS DATE)), v.gioHen, v.trangThai,
       v.phong, v.hinhThucDat, v.lyDoHuy FROM (VALUES
    (N'LH000001', N'BN000001', N'NV000002', N'DV000002',  0, '09:00', N'Đã xác nhận',  N'Phòng 1', N'Trực tiếp', NULL),
    (N'LH000002', N'BN000002', N'NV000002', N'DV000008',  0, '09:30', N'Chờ xác nhận', N'Phòng 1', N'Điện thoại', NULL),
    (N'LH000003', N'BN000004', N'NV000003', N'DV000001',  0, '08:30', N'Đã xác nhận',  N'Phòng 2', N'Online',     NULL),
    (N'LH000004', N'BN000005', N'NV000003', N'DV000007',  0, '14:00', N'Đã xác nhận',  N'Phòng 2', N'Trực tiếp',  NULL),
    (N'LH000005', N'BN000006', N'NV000007', N'DV000006',  0, '09:30', N'Chờ xác nhận', N'Phòng 3', N'Online',     NULL),
    (N'LH000006', N'BN000009', N'NV000002', N'DV000005',  0, '15:00', N'Đã xác nhận',  N'Phòng 1', N'Điện thoại', NULL),
    (N'LH000007', N'BN000007', N'NV000008', N'DV000010',  1, '14:00', N'Chờ xác nhận', N'Phòng 3', N'Điện thoại', NULL),
    (N'LH000008', N'BN000008', N'NV000003', N'DV000003',  1, '10:00', N'Đã xác nhận',  N'Phòng 2', N'Trực tiếp',  NULL),
    (N'LH000009', N'BN000010', N'NV000007', N'DV000002',  2, '09:00', N'Chờ xác nhận', N'Phòng 3', N'Online',     NULL),
    (N'LH000010', N'BN000003', N'NV000002', N'DV000001', -1, '08:30', N'Hoàn thành',   N'Phòng 1', N'Trực tiếp',  NULL),
    (N'LH000011', N'BN000012', N'NV000008', N'DV000009', -2, '14:30', N'Không đến',    N'Phòng 3', N'Điện thoại', NULL),
    (N'LH000012', N'BN000001', N'NV000002', N'DV000003', -3, '16:00', N'Đã hủy',       N'Phòng 1', N'Điện thoại', N'Bệnh nhân bận việc đột xuất')
) AS v(maLichHen, maBN, maNhanVien, maDichVu, lech, gioHen, trangThai, phong, hinhThucDat, lyDoHuy)
WHERE NOT EXISTS (SELECT 1 FROM dbo.LichHen l WHERE l.maLichHen = v.maLichHen);
GO

/* ---------- Liệu trình: LT000001 của BN000002 đang cấy Implant (3 buổi); các liệu trình khác cho dịch vụ nhiều buổi ---------- */
INSERT INTO dbo.LieuTrinh (maLieuTrinh, maBN, loaiDichVu, soBuoiDuKien, giaiDoanHienTai, ngayDuKienKetThuc, tongChiPhiDuKien, maNhaSiPhuTrach)
SELECT N'LT000001', N'BN000002', N'Implant', 3, N'Đã nhổ răng 36, chờ cấy trụ', DATEADD(MONTH, 4, GETDATE()), 20000000, N'NV000002'
WHERE NOT EXISTS (SELECT 1 FROM dbo.LieuTrinh WHERE maLieuTrinh = N'LT000001');
GO

INSERT INTO dbo.LieuTrinh (maLieuTrinh, maBN, loaiDichVu, soBuoiDuKien, giaiDoanHienTai, ngayBatDau, ngayDuKienKetThuc,
                           ngayKetThucThucTe, tongChiPhiDuKien, trangThai, maNhaSiPhuTrach)
SELECT v.* FROM (VALUES
    (N'LT000002', N'BN000005', N'Chỉnh nha', 24, N'Đang siết dây cung',
        DATEADD(DAY, -60, CAST(GETDATE() AS DATE)), DATEADD(MONTH, 20, GETDATE()), NULL, 30000000, N'Đang thực hiện', N'NV000003'),
    (N'LT000003', N'BN000009', N'Nội nha', 3, N'Đã lấy tủy, chờ trám bít ống tủy',
        DATEADD(DAY, -14, CAST(GETDATE() AS DATE)), DATEADD(DAY, 14, GETDATE()), NULL, 1500000, N'Đang thực hiện', N'NV000002'),
    (N'LT000004', N'BN000007', N'Phục hình', 3, N'Đã mài răng, chờ gắn cầu sứ',
        DATEADD(DAY, -21, CAST(GETDATE() AS DATE)), DATEADD(DAY, 10, GETDATE()), NULL, 8000000, N'Đang thực hiện', N'NV000008'),
    (N'LT000005', N'BN000012', N'Implant', 3, N'Hoàn tất gắn mão sứ',
        DATEADD(DAY, -150, CAST(GETDATE() AS DATE)), DATEADD(DAY, -20, GETDATE()), DATEADD(DAY, -20, CAST(GETDATE() AS DATE)),
        23000000, N'Hoàn thành', N'NV000008')
) AS v(maLieuTrinh, maBN, loaiDichVu, soBuoiDuKien, giaiDoanHienTai, ngayBatDau, ngayDuKienKetThuc, ngayKetThucThucTe,
       tongChiPhiDuKien, trangThai, maNhaSiPhuTrach)
WHERE NOT EXISTS (SELECT 1 FROM dbo.LieuTrinh l WHERE l.maLieuTrinh = v.maLieuTrinh);
GO

/* ---------- Hóa đơn và chi tiết (tổng tiền được tính lại từ chi tiết ngay sau khi chèn) ---------- */
INSERT INTO dbo.HoaDon (maHoaDon, maBN, maNhanVienLap, ngayLap, tienGiamGia, hinhThucThanhToan, trangThaiThanhToan, ghiChu)
SELECT v.maHoaDon, v.maBN, v.maNhanVienLap, DATEADD(DAY, v.lech, SYSDATETIME()), v.tienGiamGia, v.hinhThuc, v.trangThai, v.ghiChu FROM (VALUES
    (N'HD000001', N'BN000001', N'NV000006', -40, 0,      N'Tiền mặt',    N'Đã thanh toán',  NULL),
    (N'HD000002', N'BN000003', N'NV000006', -35, 0,      N'Chuyển khoản', N'Đã thanh toán', NULL),
    (N'HD000003', N'BN000004', N'NV000010', -30, 0,      N'Thẻ',          N'Đã thanh toán', NULL),
    (N'HD000004', N'BN000009', N'NV000006', -14, 0,      N'Tiền mặt',    N'Đã thanh toán',  NULL),
    (N'HD000005', N'BN000006', N'NV000010', -10, 250000, N'Ví điện tử',   N'Đã thanh toán', N'Giảm giá khách hàng mới'),
    (N'HD000006', N'BN000007', N'NV000006', -21, 0,      N'Chuyển khoản', N'Chưa thanh toán', N'Bệnh nhân hẹn thanh toán khi gắn cầu sứ'),
    (N'HD000007', N'BN000008', N'NV000010',  -7, 0,      N'Thẻ',          N'Đã thanh toán', NULL),
    (N'HD000008', N'BN000012', N'NV000006', -20, 0,      N'Ví điện tử',   N'Đã thanh toán', NULL),
    (N'HD000009', N'BN000010', N'NV000010',  -5, 0,      N'Tiền mặt',    N'Đã hủy',         N'Hủy do bệnh nhân không lấy kết quả'),
    (N'HD000010', N'BN000003', N'NV000006',  -1, 0,      N'Tiền mặt',    N'Chưa thanh toán', NULL)
) AS v(maHoaDon, maBN, maNhanVienLap, lech, tienGiamGia, hinhThuc, trangThai, ghiChu)
WHERE NOT EXISTS (SELECT 1 FROM dbo.HoaDon h WHERE h.maHoaDon = v.maHoaDon);
GO

INSERT INTO dbo.ChiTietHoaDon (maChiTiet, maHoaDon, maDichVu, soLuong, donGia, soFDIRang)
SELECT v.* FROM (VALUES
    (N'CT000001', N'HD000001', N'DV000002', 1,  200000, NULL),
    (N'CT000002', N'HD000002', N'DV000003', 1,  300000, 16),
    (N'CT000003', N'HD000002', N'DV000001', 1,  100000, NULL),
    (N'CT000004', N'HD000003', N'DV000012', 1,  800000, 38),
    (N'CT000005', N'HD000004', N'DV000005', 1, 1500000, 36),
    (N'CT000006', N'HD000005', N'DV000006', 1, 2500000, NULL),
    (N'CT000007', N'HD000006', N'DV000010', 1, 8000000, 46),
    (N'CT000008', N'HD000007', N'DV000003', 1,  300000, 75),
    (N'CT000009', N'HD000008', N'DV000009', 1, 3000000, 36),
    (N'CT000010', N'HD000009', N'DV000011', 1,  150000, NULL),
    (N'CT000011', N'HD000009', N'DV000001', 1,  100000, NULL),
    (N'CT000012', N'HD000010', N'DV000001', 1,  100000, NULL)
) AS v(maChiTiet, maHoaDon, maDichVu, soLuong, donGia, soFDIRang)
WHERE NOT EXISTS (SELECT 1 FROM dbo.ChiTietHoaDon c WHERE c.maChiTiet = v.maChiTiet);
GO

-- Tổng trước thuế = tổng chi tiết - giảm giá; tổng tiền = tổng trước thuế cộng thuế (8%)
UPDATE h SET tongTienTruocThue = x.tong - h.tienGiamGia,
             tongTien = ROUND((x.tong - h.tienGiamGia) * (100 + h.phanTramThue) / 100, 0)
FROM dbo.HoaDon h
CROSS APPLY (SELECT SUM(c.soLuong * c.donGia - c.giamGia) AS tong FROM dbo.ChiTietHoaDon c WHERE c.maHoaDon = h.maHoaDon) x
WHERE h.maHoaDon BETWEEN N'HD000001' AND N'HD000010' AND h.tongTien = 0 AND x.tong IS NOT NULL;
GO

/* ---------- Lượt khám cũ của BN000002: đã nhổ răng 36 (để thử quy tắc răng đã nhổ) ---------- */
INSERT INTO dbo.LuotKham (maLuotKham, maHoSo, maNhanVienPhuTrach, maLieuTrinh, gioBatDau, gioKetThuc, lyDoKham, chanDoan, huongDieuTri, trangThai)
SELECT N'LK000001', N'HS000002', N'NV000002', N'LT000001', '2022-06-10 09:00', '2022-06-10 09:45',
       N'Đau răng hàm dưới', N'Sâu răng 36 không thể trám', N'Nhổ răng 36, sau đó cấy Implant', N'Hoàn thành'
WHERE NOT EXISTS (SELECT 1 FROM dbo.LuotKham WHERE maLuotKham = N'LK000001');
GO

/* ---------- Các lượt khám khác: lech = số ngày so với hôm nay, phut = thời gian khám (NULL = đang khám),
   taiKham = số ngày đến lần tái khám (NULL = không hẹn). LK000012 đang khám hôm nay theo lịch hẹn LH000003 ---------- */
INSERT INTO dbo.LuotKham (maLuotKham, maHoSo, maNhanVienPhuTrach, maLieuTrinh, maHoaDon, gioBatDau, gioKetThuc, lyDoKham, chanDoan,
                          huongDieuTri, donThuoc, ngayTaiKham, trangThai)
SELECT v.maLuotKham, v.maHoSo, v.maNV, v.maLieuTrinh, v.maHoaDon, x.gioBatDau,
       CASE WHEN v.phut IS NULL THEN NULL ELSE DATEADD(MINUTE, v.phut, x.gioBatDau) END,
       v.lyDoKham, v.chanDoan, v.huongDieuTri, v.donThuoc,
       CASE WHEN v.taiKham IS NULL THEN NULL ELSE DATEADD(DAY, v.taiKham, CAST(GETDATE() AS DATE)) END, v.trangThai
FROM (VALUES
    (N'LK000002', N'HS000001', N'NV000002', NULL,        N'HD000001', -40, '09:00', 30,   N'Khám định kỳ',            N'Cao răng nhiều, sâu răng 26 mặt nhai', N'Lấy cao răng, hẹn trám răng 26', NULL, 14, N'Hoàn thành'),
    (N'LK000003', N'HS000003', N'NV000003', NULL,        N'HD000002', -35, '08:30', 40,   N'Răng hàm đau khi nhai',    N'Sâu răng 16 mặt nhai',                  N'Trám composite răng 16',       N'Paracetamol 500mg x 6 viên', NULL, N'Hoàn thành'),
    (N'LK000004', N'HS000004', N'NV000007', NULL,        N'HD000003', -30, '15:00', 60,   N'Răng khôn mọc lệch gây đau', N'Răng khôn 38 mọc lệch',               N'Nhổ răng khôn 38',             N'Amoxicillin 500mg x 15 viên', 7, N'Hoàn thành'),
    (N'LK000005', N'HS000005', N'NV000003', N'LT000002', NULL,        -28, '14:00', 50,   N'Tái khám niềng răng',       N'Răng di chuyển tốt',                    N'Siết dây cung, hẹn tái khám',  NULL, 3,  N'Hoàn thành'),
    (N'LK000006', N'HS000009', N'NV000002', N'LT000003', N'HD000004', -14, '10:00', 60,   N'Đau răng 36 nhiều ngày',  N'Viêm tủy răng 36',                      N'Lấy tủy buổi 1',               N'Ibuprofen 400mg x 10 viên', 0, N'Hoàn thành'),
    (N'LK000007', N'HS000006', N'NV000007', NULL,        N'HD000005', -10, '09:00', 45,   N'Muốn làm trắng răng',      N'Răng ố vàng nhẹ',                       N'Tẩy trắng răng bằng đèn LED',  NULL, NULL, N'Hoàn thành'),
    (N'LK000008', N'HS000007', N'NV000008', N'LT000004', N'HD000006', -21, '14:00', 60,   N'Mài răng làm cầu',         N'Mất răng 46, cần làm cầu răng',         N'Mài răng 45, 47 làm trụ cầu',  NULL, 10, N'Hoàn thành'),
    (N'LK000009', N'HS000008', N'NV000003', NULL,        N'HD000007',  -7, '10:00', 30,   N'Răng sữa bị sâu',         N'Sâu răng sữa 75',                       N'Trám composite răng 75',       NULL, 90, N'Hoàn thành'),
    (N'LK000010', N'HS000012', N'NV000008', N'LT000005', N'HD000008', -20, '09:00', 90,   N'Gắn mão sứ trên Implant', N'Implant răng 36 tích hợp xương tốt',  N'Gắn mão sứ Zirconia',          NULL, NULL, N'Hoàn thành'),
    (N'LK000011', N'HS000010', N'NV000007', NULL,        N'HD000009',  -5, '16:00', 20,   N'Đau răng thoáng qua',     N'Chưa phát hiện bất thường',             N'Theo dõi thêm',                NULL, NULL, N'Hoàn thành'),
    (N'LK000012', N'HS000004', N'NV000003', NULL,        NULL,          0, '08:30', NULL, N'Khám tổng quát theo lịch hẹn', NULL,                                    NULL,                            NULL, NULL, N'Đang khám')
) AS v(maLuotKham, maHoSo, maNV, maLieuTrinh, maHoaDon, lech, gio, phut, lyDoKham, chanDoan, huongDieuTri, donThuoc, taiKham, trangThai)
CROSS APPLY (SELECT DATEADD(MINUTE, DATEDIFF(MINUTE, CAST('00:00' AS TIME), CAST(v.gio AS TIME)),
                            CAST(DATEADD(DAY, v.lech, CAST(GETDATE() AS DATE)) AS DATETIME2)) AS gioBatDau) x
WHERE NOT EXISTS (SELECT 1 FROM dbo.LuotKham k WHERE k.maLuotKham = v.maLuotKham);
GO

/* ---------- Dịch vụ không gắn răng trong lượt khám ---------- */
INSERT INTO dbo.LuotKhamDichVu (maLuotKham, maDichVu, soLuong)
SELECT v.* FROM (VALUES
    (N'LK000002', N'DV000002', 1),
    (N'LK000007', N'DV000006', 1),
    (N'LK000011', N'DV000011', 1),
    (N'LK000011', N'DV000001', 1),
    (N'LK000012', N'DV000001', 1)
) AS v(maLuotKham, maDichVu, soLuong)
WHERE NOT EXISTS (SELECT 1 FROM dbo.LuotKhamDichVu x WHERE x.maLuotKham = v.maLuotKham AND x.maDichVu = v.maDichVu);
GO

/* ---------- Sơ đồ răng: BG000001 là răng 36 đã nhổ của BN000002; các bản ghi khác ở các lượt khám còn lại ---------- */
INSERT INTO dbo.RangDieuTri (maBanGhi, maLuotKham, soFDI, matRang, trangThai, vatLieu, ngayGhiNhan, maDichVu, daNho, mauHienThi)
SELECT v.* FROM (VALUES
    (N'BG000001', N'LK000001', 36, NULL,      N'Đã nhổ',      NULL,                     CAST('2022-06-10 09:30' AS DATETIME2),   N'DV000004', 1, N'Xám'),
    (N'BG000002', N'LK000003', 16, N'Nhai',   N'Đã trám',     N'Composite',             DATEADD(DAY, -35, SYSDATETIME()),        N'DV000003', 0, N'Xanh dương'),
    (N'BG000003', N'LK000004', 38, NULL,      N'Đã nhổ',      NULL,                     DATEADD(DAY, -30, SYSDATETIME()),        N'DV000012', 1, N'Xám'),
    (N'BG000004', N'LK000006', 36, NULL,      N'Sâu',         NULL,                     DATEADD(DAY, -14, SYSDATETIME()),        N'DV000005', 0, N'Đỏ'),
    (N'BG000005', N'LK000008', 45, NULL,      N'Cầu răng',    N'Sứ',                    DATEADD(DAY, -21, SYSDATETIME()),        N'DV000010', 0, N'Cam'),
    (N'BG000006', N'LK000008', 47, NULL,      N'Cầu răng',    N'Sứ',                    DATEADD(DAY, -21, SYSDATETIME()),        N'DV000010', 0, N'Cam'),
    (N'BG000007', N'LK000009', 75, N'Nhai',   N'Đã trám',     N'Composite',             DATEADD(DAY, -7, SYSDATETIME()),         N'DV000003', 0, N'Xanh dương'),
    (N'BG000008', N'LK000010', 36, NULL,      N'Bọc sứ',      N'Sứ Zirconia',           DATEADD(DAY, -20, SYSDATETIME()),        N'DV000009', 0, N'Vàng'),
    (N'BG000009', N'LK000002', 26, N'Nhai',   N'Sâu',         NULL,                     DATEADD(DAY, -40, SYSDATETIME()),        NULL,        0, N'Đỏ'),
    (N'BG000010', N'LK000012', 47, N'Nhai',   N'Sâu',         NULL,                     SYSDATETIME(),                           NULL,        0, N'Đỏ'),
    (N'BG000011', N'LK000011', 14, NULL,      N'Bình thường', NULL,                     DATEADD(DAY, -5, SYSDATETIME()),         NULL,        0, N'Trắng')
) AS v(maBanGhi, maLuotKham, soFDI, matRang, trangThai, vatLieu, ngayGhiNhan, maDichVu, daNho, mauHienThi)
WHERE NOT EXISTS (SELECT 1 FROM dbo.RangDieuTri r WHERE r.maBanGhi = v.maBanGhi);
GO

PRINT N'Hoàn tất nạp dữ liệu mẫu.';
GO
