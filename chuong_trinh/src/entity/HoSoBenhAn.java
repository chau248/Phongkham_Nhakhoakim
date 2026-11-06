package entity;

import java.time.LocalDate;

// Hồ sơ bệnh án của một bệnh nhân
public class HoSoBenhAn {

    private String maHoSo; // Mã hồ sơ, dạng HS000001
    private BenhNhan maBN; // Bệnh nhân sở hữu hồ sơ
    private LocalDate ngayTao; // Ngày tạo hồ sơ
    private String tienSuBenh; // Tiền sử bệnh
    private String nhomMau; // A, B, AB hoặc O
    private String benhNen; // Bệnh nền
    private String thuocDangSuDung; // Thuốc đang dùng
    private String ghiChuDacBiet; // Ghi chú đặc biệt
    private String trangThaiHoSo; // Đang mở hoặc Đã đóng

    public String getMaHoSo() {
        return maHoSo;
    }

    public void setMaHoSo(String maHoSo) {
        this.maHoSo = maHoSo;
    }

    public BenhNhan getMaBN() {
        return maBN;
    }

    public void setMaBN(BenhNhan maBN) {
        this.maBN = maBN;
    }

    public LocalDate getNgayTao() {
        return ngayTao;
    }

    public void setNgayTao(LocalDate ngayTao) {
        this.ngayTao = ngayTao;
    }

    public String getTienSuBenh() {
        return tienSuBenh;
    }

    public void setTienSuBenh(String tienSuBenh) {
        this.tienSuBenh = tienSuBenh;
    }

    public String getNhomMau() {
        return nhomMau;
    }

    public void setNhomMau(String nhomMau) {
        this.nhomMau = nhomMau;
    }

    public String getBenhNen() {
        return benhNen;
    }

    public void setBenhNen(String benhNen) {
        this.benhNen = benhNen;
    }

    public String getThuocDangSuDung() {
        return thuocDangSuDung;
    }

    public void setThuocDangSuDung(String thuocDangSuDung) {
        this.thuocDangSuDung = thuocDangSuDung;
    }

    public String getGhiChuDacBiet() {
        return ghiChuDacBiet;
    }

    public void setGhiChuDacBiet(String ghiChuDacBiet) {
        this.ghiChuDacBiet = ghiChuDacBiet;
    }

    public String getTrangThaiHoSo() {
        return trangThaiHoSo;
    }

    public void setTrangThaiHoSo(String trangThaiHoSo) {
        this.trangThaiHoSo = trangThaiHoSo;
    }

}
