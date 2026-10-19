/* =====================================================================
   Phòng khám Nha khoa Kim - Nhóm 09
   03_nang_cap_rang_sua.sql: nâng cấp CSDL đã tạo từ bản cũ
   - Cho phép ghi nhận răng sữa (51-55, 61-65, 71-75, 81-85) trong RangDieuTri.soFDI.
   - Tạo index duy nhất: một lượt khám chỉ có một bản ghi cho mỗi (răng, mặt răng, dịch vụ).
   - Chạy lại nhiều lần được. Chạy sau 01_schema.sql.
   ===================================================================== */
USE NhaKhoaKim;
GO
SET QUOTED_IDENTIFIER ON;
SET ANSI_NULLS ON;
GO

/* ---------- 1. Ràng buộc CHECK của soFDI ---------- */
IF OBJECT_ID(N'dbo.RangDieuTri', N'U') IS NULL
BEGIN
    PRINT N'Chưa có bảng dbo.RangDieuTri, hãy chạy 01_schema.sql trước.';
END
ELSE
BEGIN
    DECLARE @tenCu SYSNAME;
    DECLARE @sql NVARCHAR(400);

    -- Ràng buộc CHECK cũ của soFDI (chưa có răng sữa, tức chưa có số 51)
    SELECT TOP 1 @tenCu = cc.name
    FROM sys.check_constraints cc
    WHERE cc.parent_object_id = OBJECT_ID(N'dbo.RangDieuTri')
      AND cc.definition LIKE N'%soFDI%'
      AND cc.definition NOT LIKE N'%(51)%';

    IF @tenCu IS NOT NULL
    BEGIN
        SET @sql = N'ALTER TABLE dbo.RangDieuTri DROP CONSTRAINT ' + QUOTENAME(@tenCu);
        EXEC sp_executesql @sql;
    END

    -- Chưa có ràng buộc nào cho soFDI (vừa xóa bản cũ, hoặc bảng chưa từng có) thì thêm bản mới
    IF NOT EXISTS (SELECT 1 FROM sys.check_constraints
                   WHERE parent_object_id = OBJECT_ID(N'dbo.RangDieuTri') AND definition LIKE N'%soFDI%')
        ALTER TABLE dbo.RangDieuTri ADD CONSTRAINT CK_RangDieuTri_soFDI
            CHECK (soFDI BETWEEN 11 AND 18 OR soFDI BETWEEN 21 AND 28 OR soFDI BETWEEN 31 AND 38 OR soFDI BETWEEN 41 AND 48
                OR soFDI BETWEEN 51 AND 55 OR soFDI BETWEEN 61 AND 65 OR soFDI BETWEEN 71 AND 75 OR soFDI BETWEEN 81 AND 85);
END
GO

/* ---------- 2. Index duy nhất (lượt khám, răng, mặt răng, dịch vụ) ---------- */
IF OBJECT_ID(N'dbo.RangDieuTri', N'U') IS NOT NULL
   AND NOT EXISTS (SELECT 1 FROM sys.indexes
                   WHERE name = N'UX_RangDieuTri_Luot_Rang_Mat_DichVu' AND object_id = OBJECT_ID(N'dbo.RangDieuTri'))
BEGIN
    IF EXISTS (SELECT 1 FROM dbo.RangDieuTri GROUP BY maLuotKham, soFDI, matRang, maDichVu HAVING COUNT(*) > 1)
    BEGIN
        -- Liệt kê các nhóm trùng để người quản trị gộp hoặc xóa bớt trước khi chạy lại
        SELECT maLuotKham, soFDI, matRang, maDichVu, COUNT(*) AS soBanGhi
        FROM dbo.RangDieuTri
        GROUP BY maLuotKham, soFDI, matRang, maDichVu
        HAVING COUNT(*) > 1;
        PRINT N'Chưa tạo UX_RangDieuTri_Luot_Rang_Mat_DichVu vì dữ liệu đang có bản ghi trùng (xem danh sách ở trên). Hãy gộp dữ liệu trùng rồi chạy lại.';
    END
    ELSE
    BEGIN
        CREATE UNIQUE INDEX UX_RangDieuTri_Luot_Rang_Mat_DichVu ON dbo.RangDieuTri(maLuotKham, soFDI, matRang, maDichVu);
        PRINT N'Đã tạo UX_RangDieuTri_Luot_Rang_Mat_DichVu.';
    END
END
GO

PRINT N'Hoàn tất nâng cấp răng sữa.';
GO
