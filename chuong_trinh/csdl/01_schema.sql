/* =====================================================================
   Phòng khám Nha khoa Kim - Nhóm 09
   01_schema.sql: tạo CSDL NhaKhoaKim và toàn bộ bảng (SQL Server)
   - Chạy trong SSMS bằng tài khoản quản trị (Windows Authentication).
   - Chạy lại nhiều lần được: chỉ tạo khi chưa có.
   - Tên cột trùng tên thuộc tính trong package nhakhoakim.entity.
   - Người sở hữu từng bảng: xem sheet "Entity và CSDL" trong tai_lieu/KeHoach_PhanCong_Code.xlsx
   - Mật khẩu tài khoản lưu dạng chữ thường (không băm) vì đây là bài tập môn học.
   ===================================================================== */
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

/* ---------- Tâm: NhanVien, TaiKhoan, DichVu, CaTruc, NhatKyHeThong ---------- */
IF OBJECT_ID(N'dbo.NhanVien', N'U') IS NULL
CREATE TABLE dbo.NhanVien (
    maNV               NVARCHAR(10)  NOT NULL PRIMARY KEY,
    hoTen              NVARCHAR(100) NOT NULL,
    vaiTro             NVARCHAR(20)  NOT NULL CHECK (vaiTro IN (N'Nha sĩ', N'Y tá', N'Lễ tân', N'Quản lý')),
    soChungChiHanhNghe NVARCHAR(30)  NULL,
    ngayHetHanChungChi DATE          NULL,
    ngaySinh           DATE          NULL,
    gioiTinh           NVARCHAR(10)  NULL CHECK (gioiTinh IN (N'Nam', N'Nữ', N'Khác')),
    sdt                VARCHAR(10)   NULL,
    email              VARCHAR(100)  NULL,
    diaChi             NVARCHAR(255) NULL,
    ngayVaoLam         DATE          NULL,
    chuyenKhoa         NVARCHAR(100) NULL,
    trangThai          NVARCHAR(20)  NOT NULL DEFAULT N'Đang làm việc' CHECK (trangThai IN (N'Đang làm việc', N'Nghỉ việc'))
);
GO

IF OBJECT_ID(N'dbo.TaiKhoan', N'U') IS NULL
CREATE TABLE dbo.TaiKhoan (
    maTaiKhoan         NVARCHAR(10) NOT NULL PRIMARY KEY,
    maNV               NVARCHAR(10) NOT NULL UNIQUE REFERENCES dbo.NhanVien(maNV),
    tenDangNhap        VARCHAR(50)  NOT NULL UNIQUE,
    matKhau            VARCHAR(100) NOT NULL,
    vaiTro             NVARCHAR(20) NOT NULL CHECK (vaiTro IN (N'Nha sĩ', N'Y tá', N'Lễ tân', N'Quản lý')),
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
    loaiDichVu             NVARCHAR(50)  NOT NULL,
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
    phong     NVARCHAR(20) NULL,
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
    ghiChu             NVARCHAR(255) NULL
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
    phong           NVARCHAR(20)  NULL,
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

/* ---------- Hậu: HoSoBenhAn, LieuTrinh, LuotKham, RangDieuTri, DungCu, DichVuDungCu ---------- */
IF OBJECT_ID(N'dbo.HoSoBenhAn', N'U') IS NULL
CREATE TABLE dbo.HoSoBenhAn (
    maHoSo          NVARCHAR(10)  NOT NULL PRIMARY KEY,
    maBN            NVARCHAR(10)  NOT NULL UNIQUE REFERENCES dbo.BenhNhan(maBN),
    ngayTao         DATE          NOT NULL DEFAULT CAST(GETDATE() AS DATE),
    tienSuBenh      NVARCHAR(1000) NULL,
    canhBaoDiUng    NVARCHAR(500) NULL,
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
    loaiDichVu         NVARCHAR(50)  NOT NULL,
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
    soFDI       INT           NOT NULL CHECK (soFDI BETWEEN 11 AND 18 OR soFDI BETWEEN 21 AND 28
                                              OR soFDI BETWEEN 31 AND 38 OR soFDI BETWEEN 41 AND 48),
    matRang     NVARCHAR(10)  NULL CHECK (matRang IN (N'Gần', N'Xa', N'Nhai', N'Ngoài', N'Trong')),
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
   Dịch vụ gắn răng được ghi ở RangDieuTri.maDichVu; LuotKhamService gộp cả hai khi lập hóa đơn. */
IF OBJECT_ID(N'dbo.LuotKhamDichVu', N'U') IS NULL
CREATE TABLE dbo.LuotKhamDichVu (
    maDong     INT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    maLuotKham NVARCHAR(10)  NOT NULL REFERENCES dbo.LuotKham(maLuotKham) ON DELETE CASCADE,
    maDichVu   NVARCHAR(10)  NOT NULL REFERENCES dbo.DichVu(maDichVu),
    soLuong    INT           NOT NULL DEFAULT 1 CHECK (soLuong > 0),
    ghiChu     NVARCHAR(255) NULL
);
GO

IF OBJECT_ID(N'dbo.DungCu', N'U') IS NULL
CREATE TABLE dbo.DungCu (
    maDungCu           NVARCHAR(10)  NOT NULL PRIMARY KEY,
    tenDungCu          NVARCHAR(150) NOT NULL,
    loaiDichVu         NVARCHAR(50)  NULL,
    trangThaiVoTrung   NVARCHAR(20)  NOT NULL DEFAULT N'Chờ xử lý'
        CHECK (trangThaiVoTrung IN (N'Đã tiệt trùng', N'Chờ xử lý', N'Đang sử dụng')),
    ngayTietTrungCuoi  DATETIME2     NULL,
    phong              NVARCHAR(20)  NULL
);
GO

IF OBJECT_ID(N'dbo.DichVuDungCu', N'U') IS NULL
CREATE TABLE dbo.DichVuDungCu (
    maDichVu NVARCHAR(10) NOT NULL REFERENCES dbo.DichVu(maDichVu),
    maDungCu NVARCHAR(10) NOT NULL REFERENCES dbo.DungCu(maDungCu),
    soLuong  INT          NOT NULL DEFAULT 1 CHECK (soLuong > 0),
    PRIMARY KEY (maDichVu, maDungCu)
);
GO

/* ---------- Nâng cấp CSDL tạo từ bản cũ (chạy lại an toàn) ---------- */
IF COL_LENGTH(N'dbo.LuotKham', N'maLichHen') IS NULL
    ALTER TABLE dbo.LuotKham ADD maLichHen NVARCHAR(10) NULL REFERENCES dbo.LichHen(maLichHen);
GO
IF COL_LENGTH(N'dbo.TaiKhoan', N'matKhauHash') IS NOT NULL AND COL_LENGTH(N'dbo.TaiKhoan', N'matKhau') IS NULL
    EXEC sp_rename N'dbo.TaiKhoan.matKhauHash', N'matKhau', N'COLUMN';
GO

PRINT N'Hoàn tất tạo CSDL NhaKhoaKim.';
GO
