create login letan1 with password='123456',CHECK_POLICY=OFF
create login quanly with password='123456',CHECK_POLICY=OFF
create login nhasi1 with password='123456',CHECK_POLICY=OFF
create login nhasi2 with password='123456',CHECK_POLICY=OFF
create login yta1 with password='123456',CHECK_POLICY=OFF
create login yta2 with password='123456',CHECK_POLICY=OFF

create user yta1 for login yta1
create user yta2 for login yta2
create user nhasi1 for login nhasi1
create user nhasi2 for login nhasi2
create user quanly for login quanly
create user letan1 for login letan1


create role YTa
create role NhaSi
create role QuanLy
create role LeTan



exec sp_addrolemember 'YTa','yta1'
exec sp_addrolemember 'YTa','yta2'
exec sp_addrolemember 'LeTan','letan1'
exec sp_addrolemember 'QuanLy','quanly'
exec sp_addrolemember 'NhaSi','nhasi1'
exec sp_addrolemember 'NhaSi','nhasi2'


alter role db_owner add member QuanLy



grant select,insert on [dbo].[HoaDon] to LeTan
grant select,insert,update,delete on [dbo].[LichHen] to LeTan
grant select, insert on [dbo].[ChiTietHoaDon] to LeTan
grant select, update on [dbo].[HoSoBenhAn] to LeTan
grant select on [dbo].[LuotKham] to LeTan
grant select on [dbo].[NhanVien] to LeTan