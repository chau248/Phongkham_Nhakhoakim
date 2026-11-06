USE master;
GO
IF DB_ID(N'NhaKhoaKim') IS NULL
    CREATE DATABASE NhaKhoaKim COLLATE Vietnamese_CI_AS;
GO
USE NhaKhoaKim;
GO
SET QUOTED_IDENTIFIER ON;
SET ANSI_NULLS ON;
GO

/* ---------- Bộ đếm sinh mã (BN000001, HD000123...) - dùng bởi MaGenerator ---------- */
IF OBJECT_ID(N'dbo.BoDemMa', N'U') IS NULL
CREATE TABLE dbo.BoDemMa (
    tienTo        VARCHAR(5) NOT NULL PRIMARY KEY,
    giaTriHienTai INT        NOT NULL DEFAULT 0
);
GO

/* ---------- Danh mục dùng chung (tên làm khóa nên các bảng khác chỉ cần lưu tên) ---------- */
IF OBJECT_ID(N'dbo.ChuyenKhoa', N'U') IS NULL
CREATE TABLE dbo.ChuyenKhoa (
    tenChuyenKhoa NVARCHAR(100) NOT NULL PRIMARY KEY
);
GO

IF OBJECT_ID(N'dbo.VaiTro', N'U') IS NULL
CREATE TABLE dbo.VaiTro (
    tenVaiTro   NVARCHAR(20)  NOT NULL PRIMARY KEY,
    laVaiTroGoc BIT           NOT NULL DEFAULT 0, -- vai trò gốc thì không được xóa
    moTa        NVARCHAR(200) NULL
);
GO

IF OBJECT_ID(N'dbo.ChucNang', N'U') IS NULL
CREATE TABLE dbo.ChucNang (
    tenChucNang NVARCHAR(60) NOT NULL PRIMARY KEY, -- trùng với tên mục menu của chương trình
    nhom        NVARCHAR(30) NOT NULL,
    thuTu       INT          NOT NULL DEFAULT 0
);
GO

IF OBJECT_ID(N'dbo.VaiTroChucNang', N'U') IS NULL
CREATE TABLE dbo.VaiTroChucNang (
    tenVaiTro   NVARCHAR(20) NOT NULL REFERENCES dbo.VaiTro(tenVaiTro) ON DELETE CASCADE,
    tenChucNang NVARCHAR(60) NOT NULL REFERENCES dbo.ChucNang(tenChucNang) ON DELETE CASCADE,
    PRIMARY KEY (tenVaiTro, tenChucNang)
);
GO

IF OBJECT_ID(N'dbo.LoaiDichVu', N'U') IS NULL
CREATE TABLE dbo.LoaiDichVu (
    tenLoai NVARCHAR(50) NOT NULL PRIMARY KEY
);
GO

IF OBJECT_ID(N'dbo.Phong', N'U') IS NULL
CREATE TABLE dbo.Phong (
    tenPhong    NVARCHAR(20) NOT NULL PRIMARY KEY,
    laPhongKham BIT          NOT NULL DEFAULT 1 -- 0 là quầy lễ tân, không dùng để khám
);
GO

IF OBJECT_ID(N'dbo.ThamSo', N'U') IS NULL
CREATE TABLE dbo.ThamSo (
    tenThamSo NVARCHAR(40)  NOT NULL PRIMARY KEY,
    giaTri    NVARCHAR(255) NOT NULL,
    moTa      NVARCHAR(200) NULL
);
GO

/* ---------- Dữ liệu nền: danh mục và quyền mặc định (chạy lại không bị trùng) ---------- */
INSERT INTO dbo.VaiTro (tenVaiTro, laVaiTroGoc, moTa)
SELECT v.* FROM (VALUES
    (N'Quản lý',    1, N'Quản lý phòng khám'),
    (N'Nha sĩ',     1, N'Khám và điều trị'),
    (N'Y tá',       1, N'Hỗ trợ nha sĩ'),
    (N'Lễ tân',     1, N'Đón tiếp, đặt lịch, thu tiền'),
    (N'Toàn quyền', 1, N'Dùng được mọi chức năng (để kiểm tra, trình diễn)')
) AS v(tenVaiTro, laVaiTroGoc, moTa)
WHERE NOT EXISTS (SELECT 1 FROM dbo.VaiTro x WHERE x.tenVaiTro = v.tenVaiTro);

INSERT INTO dbo.ChuyenKhoa (tenChuyenKhoa)
SELECT v.t FROM (VALUES (N'Nha tổng quát'), (N'Nội nha'), (N'Chỉnh nha'), (N'Phục hình răng'), (N'Nha chu'),
                        (N'Nhổ răng - Tiểu phẫu'), (N'Nha khoa trẻ em')) AS v(t)
WHERE NOT EXISTS (SELECT 1 FROM dbo.ChuyenKhoa x WHERE x.tenChuyenKhoa = v.t);

INSERT INTO dbo.LoaiDichVu (tenLoai)
SELECT v.t FROM (VALUES (N'Khám'), (N'Chẩn đoán'), (N'Vệ sinh'), (N'Trám'), (N'Nhổ răng'), (N'Nội nha'),
                        (N'Thẩm mỹ'), (N'Chỉnh nha'), (N'Implant'), (N'Phục hình')) AS v(t)
WHERE NOT EXISTS (SELECT 1 FROM dbo.LoaiDichVu x WHERE x.tenLoai = v.t);

INSERT INTO dbo.Phong (tenPhong, laPhongKham)
SELECT v.* FROM (VALUES (N'Phòng 1', 1), (N'Phòng 2', 1), (N'Phòng 3', 1), (N'Quầy lễ tân', 0)) AS v(tenPhong, laPhongKham)
WHERE NOT EXISTS (SELECT 1 FROM dbo.Phong x WHERE x.tenPhong = v.tenPhong);

INSERT INTO dbo.ThamSo (tenThamSo, giaTri, moTa)
SELECT v.* FROM (VALUES
    (N'tenPhongKham', N'Nha khoa Kim',                                   N'Tên in trên hóa đơn'),
    (N'diaChi',       N'123 Nguyễn Văn Linh, Quận 7, TP. Hồ Chí Minh',   N'Địa chỉ phòng khám'),
    (N'soDienThoai',  N'028 3999 8888',                                  N'Số điện thoại phòng khám'),
    (N'maSoThue',     N'0312345678',                                     N'Mã số thuế (số mẫu)'),
    (N'phanTramVAT',  N'8',                                              N'Thuế VAT áp dụng khi lập hóa đơn (%)')
) AS v(tenThamSo, giaTri, moTa)
WHERE NOT EXISTS (SELECT 1 FROM dbo.ThamSo x WHERE x.tenThamSo = v.tenThamSo);

INSERT INTO dbo.ChucNang (tenChucNang, nhom, thuTu)
SELECT v.* FROM (VALUES
    (N'Tìm kiếm bệnh nhân',                          N'Bệnh nhân', 1),
    (N'Thêm / cập nhật bệnh nhân',                   N'Bệnh nhân', 2),
    (N'Xem hồ sơ bệnh án',                           N'Bệnh nhân', 3),
    (N'Dòng thời gian điều trị',                     N'Bệnh nhân', 4),
    (N'Khám & ghi nhận điều trị',                    N'Lượt khám', 5),
    (N'Sơ đồ răng điều trị',                         N'Lượt khám', 6),
    (N'Chỉ định tái khám',                           N'Lượt khám', 7),
    (N'Gợi ý điều trị & ước tính chi phí',           N'Lượt khám', 8),
    (N'In đơn thuốc',                                N'Lượt khám', 9),
    (N'Liệu trình điều trị',                         N'Lượt khám', 10),
    (N'Lịch hẹn theo tuần',                          N'Lịch hẹn', 11),
    (N'Đặt lịch hẹn',                                N'Lịch hẹn', 12),
    (N'Danh sách chờ thanh toán',                    N'Hóa đơn', 13),
    (N'Lập hóa đơn',                                 N'Hóa đơn', 14),
    (N'Thanh toán & in hóa đơn',                     N'Hóa đơn', 15),
    (N'Báo cáo doanh thu',                           N'Hóa đơn', 16),
    (N'Xem danh mục dịch vụ & giá',                  N'Dịch vụ', 17),
    (N'Thêm / sửa dịch vụ',                          N'Dịch vụ', 18),
    (N'Cập nhật hồ sơ nhân viên',                    N'Nhân viên', 19),
    (N'Thời khóa biểu ca trực',                      N'Nhân viên', 20),
    (N'Tạo tài khoản & gán vai trò',                 N'Tài khoản', 21),
    (N'Danh mục (chuyên khoa, loại dịch vụ, phòng)', N'Hệ thống', 22),
    (N'Phân quyền vai trò',                          N'Hệ thống', 23),
    (N'Thông tin phòng khám & thuế',                 N'Hệ thống', 24)
) AS v(tenChucNang, nhom, thuTu)
WHERE NOT EXISTS (SELECT 1 FROM dbo.ChucNang x WHERE x.tenChucNang = v.tenChucNang);

/* Quyền mặc định của 4 vai trò gốc (chỉ cấp khi vai trò đó chưa có quyền nào, để không đè lên phần đã chỉnh) */
INSERT INTO dbo.VaiTroChucNang (tenVaiTro, tenChucNang)
SELECT q.vaiTro, q.chucNang FROM (VALUES
    (N'Quản lý', N'Tìm kiếm bệnh nhân'), (N'Quản lý', N'Thêm / cập nhật bệnh nhân'), (N'Quản lý', N'Xem hồ sơ bệnh án'),
    (N'Quản lý', N'Dòng thời gian điều trị'), (N'Quản lý', N'Liệu trình điều trị'), (N'Quản lý', N'Lịch hẹn theo tuần'),
    (N'Quản lý', N'Đặt lịch hẹn'), (N'Quản lý', N'Danh sách chờ thanh toán'), (N'Quản lý', N'Lập hóa đơn'),
    (N'Quản lý', N'Thanh toán & in hóa đơn'), (N'Quản lý', N'Báo cáo doanh thu'), (N'Quản lý', N'Xem danh mục dịch vụ & giá'),
    (N'Quản lý', N'Thêm / sửa dịch vụ'), (N'Quản lý', N'Cập nhật hồ sơ nhân viên'), (N'Quản lý', N'Thời khóa biểu ca trực'),
    (N'Quản lý', N'Tạo tài khoản & gán vai trò'), (N'Quản lý', N'Danh mục (chuyên khoa, loại dịch vụ, phòng)'),
    (N'Quản lý', N'Phân quyền vai trò'), (N'Quản lý', N'Thông tin phòng khám & thuế'),

    (N'Nha sĩ', N'Tìm kiếm bệnh nhân'), (N'Nha sĩ', N'Xem hồ sơ bệnh án'), (N'Nha sĩ', N'Dòng thời gian điều trị'),
    (N'Nha sĩ', N'Khám & ghi nhận điều trị'), (N'Nha sĩ', N'Sơ đồ răng điều trị'), (N'Nha sĩ', N'Chỉ định tái khám'),
    (N'Nha sĩ', N'Gợi ý điều trị & ước tính chi phí'), (N'Nha sĩ', N'In đơn thuốc'), (N'Nha sĩ', N'Liệu trình điều trị'),
    (N'Nha sĩ', N'Lịch hẹn theo tuần'), (N'Nha sĩ', N'Xem danh mục dịch vụ & giá'), (N'Nha sĩ', N'Thời khóa biểu ca trực'),

    (N'Y tá', N'Tìm kiếm bệnh nhân'), (N'Y tá', N'Xem hồ sơ bệnh án'), (N'Y tá', N'Dòng thời gian điều trị'),
    (N'Y tá', N'Liệu trình điều trị'), (N'Y tá', N'Lịch hẹn theo tuần'), (N'Y tá', N'Xem danh mục dịch vụ & giá'),
    (N'Y tá', N'Thời khóa biểu ca trực'),

    (N'Lễ tân', N'Tìm kiếm bệnh nhân'), (N'Lễ tân', N'Thêm / cập nhật bệnh nhân'), (N'Lễ tân', N'Lịch hẹn theo tuần'),
    (N'Lễ tân', N'Đặt lịch hẹn'), (N'Lễ tân', N'Danh sách chờ thanh toán'), (N'Lễ tân', N'Lập hóa đơn'),
    (N'Lễ tân', N'Thanh toán & in hóa đơn'), (N'Lễ tân', N'Xem danh mục dịch vụ & giá'), (N'Lễ tân', N'Thời khóa biểu ca trực')
) AS q(vaiTro, chucNang)
WHERE NOT EXISTS (SELECT 1 FROM dbo.VaiTroChucNang x WHERE x.tenVaiTro = q.vaiTro);

/* Toàn quyền luôn có mọi chức năng (kể cả chức năng thêm về sau) */
INSERT INTO dbo.VaiTroChucNang (tenVaiTro, tenChucNang)
SELECT N'Toàn quyền', c.tenChucNang FROM dbo.ChucNang c
WHERE NOT EXISTS (SELECT 1 FROM dbo.VaiTroChucNang x WHERE x.tenVaiTro = N'Toàn quyền' AND x.tenChucNang = c.tenChucNang);
GO

/* ---------- Tâm: NhanVien, TaiKhoan, DichVu, CaTruc, NhatKyHeThong ---------- */
IF OBJECT_ID(N'dbo.NhanVien', N'U') IS NULL
CREATE TABLE dbo.NhanVien (
    maNV               NVARCHAR(10)  NOT NULL PRIMARY KEY,
    hoTen              NVARCHAR(100) NOT NULL,
    vaiTro             NVARCHAR(20)  NOT NULL CONSTRAINT FK_NhanVien_VaiTro REFERENCES dbo.VaiTro(tenVaiTro),
    soChungChiHanhNghe NVARCHAR(30)  NULL,
    ngayHetHanChungChi DATE          NULL,
    ngaySinh           DATE          NULL,
    gioiTinh           NVARCHAR(10)  NULL CHECK (gioiTinh IN (N'Nam', N'Nữ', N'Khác')),
    sdt                VARCHAR(10)   NULL,
    email              VARCHAR(100)  NULL,
    diaChi             NVARCHAR(255) NULL,
    ngayVaoLam         DATE          NULL,
    chuyenKhoa         NVARCHAR(100) NULL CONSTRAINT FK_NhanVien_ChuyenKhoa REFERENCES dbo.ChuyenKhoa(tenChuyenKhoa),
    trangThai          NVARCHAR(20)  NOT NULL DEFAULT N'Đang làm việc' CHECK (trangThai IN (N'Đang làm việc', N'Nghỉ việc'))
);
GO

IF OBJECT_ID(N'dbo.TaiKhoan', N'U') IS NULL
CREATE TABLE dbo.TaiKhoan (
    maTaiKhoan         NVARCHAR(10) NOT NULL PRIMARY KEY,
    maNV               NVARCHAR(10) NOT NULL UNIQUE REFERENCES dbo.NhanVien(maNV),
    tenDangNhap        VARCHAR(50)  NOT NULL UNIQUE,
    matKhau            VARCHAR(100) NOT NULL,
    vaiTro             NVARCHAR(20) NOT NULL CONSTRAINT FK_TaiKhoan_VaiTro REFERENCES dbo.VaiTro(tenVaiTro),
    trangThaiKhoa      BIT          NOT NULL DEFAULT 0,
    ngayTao            DATETIME2    NOT NULL DEFAULT SYSDATETIME(),
    lanDangNhapCuoi    DATETIME2    NULL,
    soLanDangNhapSai   INT          NOT NULL DEFAULT 0,
    thoiGianKhoaDen    DATETIME2    NULL,
    ngayDoiMatKhauCuoi DATE         NULL,
    yeuCauDoiMatKhau   BIT          NOT NULL DEFAULT 0
);
GO

IF OBJECT_ID(N'dbo.DichVu', N'U') IS NULL
CREATE TABLE dbo.DichVu (
    maDichVu               NVARCHAR(10)  NOT NULL PRIMARY KEY,
    tenDichVu              NVARCHAR(150) NOT NULL,
    gia                    DECIMAL(18,0) NOT NULL CHECK (gia >= 0),
    thoiGianDuKien         INT           NOT NULL CHECK (thoiGianDuKien > 0),
    moTa                   NVARCHAR(500) NULL,
    loaiDichVu             NVARCHAR(50)  NOT NULL CONSTRAINT FK_DichVu_LoaiDichVu REFERENCES dbo.LoaiDichVu(tenLoai),
    donViTinh              NVARCHAR(20)  NULL,
    trangThaiHoatDong      BIT           NOT NULL DEFAULT 1,
    yeuCauLieuTrinh        BIT           NOT NULL DEFAULT 0,
    lienQuanImplantCauRang BIT           NOT NULL DEFAULT 0
);
GO

IF OBJECT_ID(N'dbo.CaTruc', N'U') IS NULL
CREATE TABLE dbo.CaTruc (
    maCaTruc  NVARCHAR(10) NOT NULL PRIMARY KEY,
    maNV      NVARCHAR(10) NOT NULL REFERENCES dbo.NhanVien(maNV),
    ngay      DATE         NOT NULL,
    caLam     NVARCHAR(10) NOT NULL CHECK (caLam IN (N'Sáng', N'Chiều')),
    phong     NVARCHAR(20) NULL CONSTRAINT FK_CaTruc_Phong REFERENCES dbo.Phong(tenPhong),
    trangThai NVARCHAR(20) NOT NULL DEFAULT N'Có mặt' CHECK (trangThai IN (N'Có mặt', N'Vắng', N'Điều chuyển')),
    CONSTRAINT UQ_CaTruc UNIQUE (maNV, ngay, caLam)
);
GO

IF OBJECT_ID(N'dbo.NhatKyHeThong', N'U') IS NULL
CREATE TABLE dbo.NhatKyHeThong (
    maNhatKy   BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    thoiGian   DATETIME2     NOT NULL DEFAULT SYSDATETIME(),
    maNhanVien NVARCHAR(10)  NULL REFERENCES dbo.NhanVien(maNV),
    hanhDong   NVARCHAR(50)  NOT NULL,
    doiTuong   NVARCHAR(50)  NOT NULL,
    maDoiTuong NVARCHAR(30)  NULL,
    chiTiet    NVARCHAR(500) NULL
);
GO

/* ---------- Trường: BenhNhan, LichHen, HoaDon, ChiTietHoaDon ---------- */
IF OBJECT_ID(N'dbo.BenhNhan', N'U') IS NULL
CREATE TABLE dbo.BenhNhan (
    maBN               NVARCHAR(10)  NOT NULL PRIMARY KEY,
    hoTen              NVARCHAR(100) NOT NULL,
    ngaySinh           DATE          NULL,
    gioiTinh           NVARCHAR(10)  NULL CHECK (gioiTinh IN (N'Nam', N'Nữ', N'Khác')),
    sdt                VARCHAR(10)   NULL CHECK (sdt IS NULL OR (LEN(sdt) = 10 AND sdt LIKE '0%' AND sdt NOT LIKE '%[^0-9]%')),
    diaChi             NVARCHAR(255) NULL,
    email              VARCHAR(100)  NULL,
    soCCCD             VARCHAR(12)   NULL CHECK (soCCCD IS NULL OR (LEN(soCCCD) = 12 AND soCCCD NOT LIKE '%[^0-9]%')),
    nguoiLienHeKhanCap NVARCHAR(100) NULL,
    sdtNguoiLienHe     VARCHAR(10)   NULL,
    ngayDangKy         DATE          NOT NULL DEFAULT CAST(GETDATE() AS DATE),
    trangThai          NVARCHAR(20)  NOT NULL DEFAULT N'Đang điều trị' CHECK (trangThai IN (N'Đang điều trị', N'Ngừng theo dõi')),
    ghiChu             NVARCHAR(255) NULL
);
GO
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = N'UX_BenhNhan_soCCCD')
    CREATE UNIQUE INDEX UX_BenhNhan_soCCCD ON dbo.BenhNhan(soCCCD) WHERE soCCCD IS NOT NULL;
GO

IF OBJECT_ID(N'dbo.HoaDon', N'U') IS NULL
CREATE TABLE dbo.HoaDon (
    maHoaDon           NVARCHAR(10)  NOT NULL PRIMARY KEY,
    maBN               NVARCHAR(10)  NOT NULL REFERENCES dbo.BenhNhan(maBN),
    maNhanVienLap      NVARCHAR(10)  NULL REFERENCES dbo.NhanVien(maNV),
    ngayLap            DATETIME2     NOT NULL DEFAULT SYSDATETIME(),
    tienGiamGia        DECIMAL(18,0) NOT NULL DEFAULT 0 CHECK (tienGiamGia >= 0),
    phanTramThue       DECIMAL(5,2)  NOT NULL DEFAULT 8 CHECK (phanTramThue BETWEEN 0 AND 100),
    tongTienTruocThue  DECIMAL(18,0) NOT NULL DEFAULT 0 CHECK (tongTienTruocThue >= 0),
    tongTien           DECIMAL(18,0) NOT NULL DEFAULT 0 CHECK (tongTien >= 0),
    hinhThucThanhToan  NVARCHAR(20)  NOT NULL DEFAULT N'Tiền mặt'
        CHECK (hinhThucThanhToan IN (N'Tiền mặt', N'Chuyển khoản', N'Thẻ', N'Ví điện tử')),
    trangThaiThanhToan NVARCHAR(20)  NOT NULL DEFAULT N'Chưa thanh toán'
        CHECK (trangThaiThanhToan IN (N'Chưa thanh toán', N'Đã thanh toán', N'Đã hủy')),
    ghiChu             NVARCHAR(255) NULL,
    ngayThanhToan       DATETIME2     NULL
);
GO

IF OBJECT_ID(N'dbo.ChiTietHoaDon', N'U') IS NULL
CREATE TABLE dbo.ChiTietHoaDon (
    maChiTiet  NVARCHAR(10)  NOT NULL PRIMARY KEY,
    maHoaDon   NVARCHAR(10)  NOT NULL REFERENCES dbo.HoaDon(maHoaDon) ON DELETE CASCADE,
    maDichVu   NVARCHAR(10)  NOT NULL REFERENCES dbo.DichVu(maDichVu),
    soLuong    INT           NOT NULL CHECK (soLuong > 0),
    donGia     DECIMAL(18,0) NOT NULL CHECK (donGia >= 0),
    giamGia    DECIMAL(18,0) NOT NULL DEFAULT 0 CHECK (giamGia >= 0),
    soFDIRang  INT           NULL,
    ghiChu     NVARCHAR(255) NULL
);
GO

IF OBJECT_ID(N'dbo.LichHen', N'U') IS NULL
CREATE TABLE dbo.LichHen (
    maLichHen       NVARCHAR(10)  NOT NULL PRIMARY KEY,
    maBN            NVARCHAR(10)  NOT NULL REFERENCES dbo.BenhNhan(maBN),
    maNhanVien      NVARCHAR(10)  NOT NULL REFERENCES dbo.NhanVien(maNV),
    maDichVu        NVARCHAR(10)  NOT NULL REFERENCES dbo.DichVu(maDichVu),
    ngayHen         DATE          NOT NULL,
    gioHen          TIME(0)       NOT NULL,
    trangThai       NVARCHAR(20)  NOT NULL DEFAULT N'Chờ xác nhận'
        CHECK (trangThai IN (N'Chờ xác nhận', N'Đã xác nhận', N'Hoàn thành', N'Không đến', N'Đã hủy')),
    ngayTao         DATETIME2     NOT NULL DEFAULT SYSDATETIME(),
    thoiLuongDuKien INT           NOT NULL DEFAULT 30 CHECK (thoiLuongDuKien > 0),
    phong           NVARCHAR(20)  NULL CONSTRAINT FK_LichHen_Phong REFERENCES dbo.Phong(tenPhong),
    hinhThucDat     NVARCHAR(20)  NULL CHECK (hinhThucDat IN (N'Trực tiếp', N'Điện thoại', N'Online')),
    lyDoHuy         NVARCHAR(255) NULL,
    daNhacHen       BIT           NOT NULL DEFAULT 0,
    ghiChu          NVARCHAR(255) NULL
);
GO
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = N'IX_LichHen_NhanVien_Ngay')
    CREATE INDEX IX_LichHen_NhanVien_Ngay ON dbo.LichHen(maNhanVien, ngayHen);
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = N'IX_LichHen_BenhNhan')
    CREATE INDEX IX_LichHen_BenhNhan ON dbo.LichHen(maBN);
GO

/* ---------- Hậu: HoSoBenhAn, LieuTrinh, LuotKham, RangDieuTri, LuotKhamDichVu ---------- */
IF OBJECT_ID(N'dbo.HoSoBenhAn', N'U') IS NULL
CREATE TABLE dbo.HoSoBenhAn (
    maHoSo          NVARCHAR(10)  NOT NULL PRIMARY KEY,
    maBN            NVARCHAR(10)  NOT NULL UNIQUE REFERENCES dbo.BenhNhan(maBN),
    ngayTao         DATE          NOT NULL DEFAULT CAST(GETDATE() AS DATE),
    tienSuBenh      NVARCHAR(1000) NULL,
    nhomMau         NVARCHAR(3)   NULL CHECK (nhomMau IN (N'A', N'B', N'AB', N'O')),
    benhNen         NVARCHAR(500) NULL,
    thuocDangSuDung NVARCHAR(500) NULL,
    ghiChuDacBiet   NVARCHAR(500) NULL,
    trangThaiHoSo   NVARCHAR(20)  NOT NULL DEFAULT N'Đang mở' CHECK (trangThaiHoSo IN (N'Đang mở', N'Đã đóng'))
);
GO

IF OBJECT_ID(N'dbo.LieuTrinh', N'U') IS NULL
CREATE TABLE dbo.LieuTrinh (
    maLieuTrinh        NVARCHAR(10)  NOT NULL PRIMARY KEY,
    maBN               NVARCHAR(10)  NOT NULL REFERENCES dbo.BenhNhan(maBN),
    loaiDichVu         NVARCHAR(50)  NOT NULL CONSTRAINT FK_LieuTrinh_LoaiDichVu REFERENCES dbo.LoaiDichVu(tenLoai),
    soBuoiDuKien       INT           NOT NULL CHECK (soBuoiDuKien > 0),
    giaiDoanHienTai    NVARCHAR(100) NOT NULL DEFAULT N'Mới bắt đầu',
    ngayBatDau         DATE          NOT NULL DEFAULT CAST(GETDATE() AS DATE),
    ngayDuKienKetThuc  DATE          NULL,
    ngayKetThucThucTe  DATE          NULL,
    tongChiPhiDuKien   DECIMAL(18,0) NULL CHECK (tongChiPhiDuKien >= 0),
    trangThai          NVARCHAR(20)  NOT NULL DEFAULT N'Đang thực hiện'
        CHECK (trangThai IN (N'Đang thực hiện', N'Hoàn thành', N'Đã hủy')),
    maNhaSiPhuTrach    NVARCHAR(10)  NULL REFERENCES dbo.NhanVien(maNV),
    ghiChu             NVARCHAR(255) NULL
);
GO

IF OBJECT_ID(N'dbo.LuotKham', N'U') IS NULL
CREATE TABLE dbo.LuotKham (
    maLuotKham         NVARCHAR(10)  NOT NULL PRIMARY KEY,
    maHoSo             NVARCHAR(10)  NOT NULL REFERENCES dbo.HoSoBenhAn(maHoSo),
    maNhanVienPhuTrach NVARCHAR(10)  NOT NULL REFERENCES dbo.NhanVien(maNV),
    maLieuTrinh        NVARCHAR(10)  NULL REFERENCES dbo.LieuTrinh(maLieuTrinh),
    maHoaDon           NVARCHAR(10)  NULL REFERENCES dbo.HoaDon(maHoaDon),
    maLichHen          NVARCHAR(10)  NULL REFERENCES dbo.LichHen(maLichHen),
    gioBatDau          DATETIME2     NOT NULL DEFAULT SYSDATETIME(),
    gioKetThuc         DATETIME2     NULL,
    lyDoKham           NVARCHAR(255) NULL,
    chanDoan           NVARCHAR(500) NULL,
    huongDieuTri       NVARCHAR(500) NULL,
    donThuoc           NVARCHAR(1000) NULL,
    ngayTaiKham        DATE          NULL,
    trangThai          NVARCHAR(20)  NOT NULL DEFAULT N'Đang khám' CHECK (trangThai IN (N'Đang khám', N'Hoàn thành', N'Đã hủy')),
    ghiChu             NVARCHAR(255) NULL,
    CONSTRAINT CK_LuotKham_Gio CHECK (gioKetThuc IS NULL OR gioKetThuc > gioBatDau)
);
GO
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = N'IX_LuotKham_HoSo')
    CREATE INDEX IX_LuotKham_HoSo ON dbo.LuotKham(maHoSo, gioBatDau DESC);
GO

IF OBJECT_ID(N'dbo.RangDieuTri', N'U') IS NULL
CREATE TABLE dbo.RangDieuTri (
    maBanGhi    NVARCHAR(10)  NOT NULL PRIMARY KEY,
    maLuotKham  NVARCHAR(10)  NOT NULL REFERENCES dbo.LuotKham(maLuotKham) ON DELETE CASCADE,
    soFDI       INT           NOT NULL CONSTRAINT CK_RangDieuTri_soFDI
        CHECK (soFDI BETWEEN 11 AND 18 OR soFDI BETWEEN 21 AND 28 OR soFDI BETWEEN 31 AND 38 OR soFDI BETWEEN 41 AND 48
            OR soFDI BETWEEN 51 AND 55 OR soFDI BETWEEN 61 AND 65 OR soFDI BETWEEN 71 AND 75 OR soFDI BETWEEN 81 AND 85),
    trangThai   NVARCHAR(30)  NOT NULL DEFAULT N'Bình thường'
        CHECK (trangThai IN (N'Bình thường', N'Sâu', N'Đã trám', N'Bọc sứ', N'Đã nhổ', N'Implant', N'Cầu răng')),
    vatLieu     NVARCHAR(100) NULL,
    ngayGhiNhan DATETIME2     NOT NULL DEFAULT SYSDATETIME(),
    maDichVu    NVARCHAR(10)  NULL REFERENCES dbo.DichVu(maDichVu),
    daNho       BIT           NOT NULL DEFAULT 0,
    mauHienThi  NVARCHAR(20)  NULL,
    ghiChu      NVARCHAR(255) NULL
);
GO
/* Dịch vụ không gắn với răng cụ thể trong một lượt khám (khám tổng quát, lấy cao răng...).
   Dịch vụ gắn răng được ghi ở RangDieuTri.maDichVu; LuotKhamController gộp cả hai khi lập hóa đơn. */
IF OBJECT_ID(N'dbo.LuotKhamDichVu', N'U') IS NULL
CREATE TABLE dbo.LuotKhamDichVu (
    maDong     INT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    maLuotKham NVARCHAR(10)  NOT NULL REFERENCES dbo.LuotKham(maLuotKham) ON DELETE CASCADE,
    maDichVu   NVARCHAR(10)  NOT NULL REFERENCES dbo.DichVu(maDichVu),
    soLuong    INT           NOT NULL DEFAULT 1 CHECK (soLuong > 0),
    ghiChu     NVARCHAR(255) NULL
);
GO

/* ---------- Nâng cấp CSDL đã có từ bản cũ (CSDL mới tạo thì các bước dưới không làm gì) ---------- */

-- Giá trị cũ chưa có trong danh mục thì thêm vào danh mục, để tạo khóa ngoại không lỗi
INSERT INTO dbo.ChuyenKhoa (tenChuyenKhoa)
SELECT DISTINCT n.chuyenKhoa FROM dbo.NhanVien n
WHERE n.chuyenKhoa IS NOT NULL AND NOT EXISTS (SELECT 1 FROM dbo.ChuyenKhoa c WHERE c.tenChuyenKhoa = n.chuyenKhoa);
INSERT INTO dbo.LoaiDichVu (tenLoai)
SELECT DISTINCT x.loai FROM (SELECT loaiDichVu AS loai FROM dbo.DichVu UNION SELECT loaiDichVu FROM dbo.LieuTrinh) x
WHERE NOT EXISTS (SELECT 1 FROM dbo.LoaiDichVu l WHERE l.tenLoai = x.loai);
INSERT INTO dbo.Phong (tenPhong, laPhongKham)
SELECT DISTINCT x.phong, 1 FROM (SELECT phong FROM dbo.CaTruc UNION SELECT phong FROM dbo.LichHen) x
WHERE x.phong IS NOT NULL AND NOT EXISTS (SELECT 1 FROM dbo.Phong p WHERE p.tenPhong = x.phong);
GO

-- Bỏ ràng buộc CHECK cũ của vai trò (tên do SQL Server tự đặt nên phải tìm theo cột)
DECLARE @sql NVARCHAR(MAX) = N'';
SELECT @sql += N'ALTER TABLE ' + QUOTENAME(OBJECT_SCHEMA_NAME(cc.parent_object_id)) + N'.' + QUOTENAME(OBJECT_NAME(cc.parent_object_id))
             + N' DROP CONSTRAINT ' + QUOTENAME(cc.name) + N';' + CHAR(10)
FROM sys.check_constraints cc
JOIN sys.columns col ON col.object_id = cc.parent_object_id AND col.column_id = cc.parent_column_id
WHERE col.name = N'vaiTro' AND OBJECT_NAME(cc.parent_object_id) IN (N'NhanVien', N'TaiKhoan');
EXEC (@sql);
GO

-- Khóa ngoại sang danh mục (chỉ thêm khi chưa có)
IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE name = N'FK_NhanVien_VaiTro')
    ALTER TABLE dbo.NhanVien ADD CONSTRAINT FK_NhanVien_VaiTro FOREIGN KEY (vaiTro) REFERENCES dbo.VaiTro(tenVaiTro);
IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE name = N'FK_NhanVien_ChuyenKhoa')
    ALTER TABLE dbo.NhanVien ADD CONSTRAINT FK_NhanVien_ChuyenKhoa FOREIGN KEY (chuyenKhoa) REFERENCES dbo.ChuyenKhoa(tenChuyenKhoa);
IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE name = N'FK_TaiKhoan_VaiTro')
    ALTER TABLE dbo.TaiKhoan ADD CONSTRAINT FK_TaiKhoan_VaiTro FOREIGN KEY (vaiTro) REFERENCES dbo.VaiTro(tenVaiTro);
IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE name = N'FK_DichVu_LoaiDichVu')
    ALTER TABLE dbo.DichVu ADD CONSTRAINT FK_DichVu_LoaiDichVu FOREIGN KEY (loaiDichVu) REFERENCES dbo.LoaiDichVu(tenLoai);
IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE name = N'FK_CaTruc_Phong')
    ALTER TABLE dbo.CaTruc ADD CONSTRAINT FK_CaTruc_Phong FOREIGN KEY (phong) REFERENCES dbo.Phong(tenPhong);
IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE name = N'FK_LichHen_Phong')
    ALTER TABLE dbo.LichHen ADD CONSTRAINT FK_LichHen_Phong FOREIGN KEY (phong) REFERENCES dbo.Phong(tenPhong);
IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE name = N'FK_LieuTrinh_LoaiDichVu')
    ALTER TABLE dbo.LieuTrinh ADD CONSTRAINT FK_LieuTrinh_LoaiDichVu FOREIGN KEY (loaiDichVu) REFERENCES dbo.LoaiDichVu(tenLoai);
GO

-- Bỏ cảnh báo dị ứng khỏi hồ sơ bệnh án
IF COL_LENGTH(N'dbo.HoSoBenhAn', N'canhBaoDiUng') IS NOT NULL
    ALTER TABLE dbo.HoSoBenhAn DROP COLUMN canhBaoDiUng;
GO

-- Bỏ mặt răng khỏi sơ đồ răng: bỏ index và ràng buộc cũ, gộp bản ghi trùng (giữ bản mới nhất), rồi tạo lại index
IF EXISTS (SELECT 1 FROM sys.indexes WHERE name = N'UX_RangDieuTri_Luot_Rang_Mat_DichVu' AND object_id = OBJECT_ID(N'dbo.RangDieuTri'))
    DROP INDEX UX_RangDieuTri_Luot_Rang_Mat_DichVu ON dbo.RangDieuTri;
GO
IF COL_LENGTH(N'dbo.RangDieuTri', N'matRang') IS NOT NULL
BEGIN
    DECLARE @sql NVARCHAR(MAX) = N'';
    SELECT @sql += N'ALTER TABLE dbo.RangDieuTri DROP CONSTRAINT ' + QUOTENAME(cc.name) + N';' + CHAR(10)
    FROM sys.check_constraints cc
    JOIN sys.columns col ON col.object_id = cc.parent_object_id AND col.column_id = cc.parent_column_id
    WHERE cc.parent_object_id = OBJECT_ID(N'dbo.RangDieuTri') AND col.name = N'matRang';
    EXEC (@sql);
    ALTER TABLE dbo.RangDieuTri DROP COLUMN matRang;
END
GO
DELETE r FROM dbo.RangDieuTri r
WHERE r.maBanGhi <> (SELECT TOP 1 x.maBanGhi FROM dbo.RangDieuTri x
                     WHERE x.maLuotKham = r.maLuotKham AND x.soFDI = r.soFDI
                       AND ((x.maDichVu IS NULL AND r.maDichVu IS NULL) OR x.maDichVu = r.maDichVu)
                     ORDER BY x.ngayGhiNhan DESC, x.maBanGhi DESC);
GO
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = N'UX_RangDieuTri_Luot_Rang_DichVu' AND object_id = OBJECT_ID(N'dbo.RangDieuTri'))
    CREATE UNIQUE INDEX UX_RangDieuTri_Luot_Rang_DichVu ON dbo.RangDieuTri(maLuotKham, soFDI, maDichVu);
GO
