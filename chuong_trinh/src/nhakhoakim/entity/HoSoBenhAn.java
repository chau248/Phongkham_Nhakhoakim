package nhakhoakim.entity;

import java.time.LocalDate;

// Hồ sơ bệnh án của một bệnh nhân (mỗi bệnh nhân có đúng một hồ sơ)
public class HoSoBenhAn {

    private String maHoSo; // mã hồ sơ, dạng HS000001
    private String maBN; // mã bệnh nhân sở hữu hồ sơ
    private LocalDate ngayTao; // ngày tạo hồ sơ
    private String tienSuBenh; // tiền sử bệnh
    private String canhBaoDiUng; // các chất bệnh nhân dị ứng, cách nhau bằng dấu phẩy
    private String nhomMau; // nhóm máu: A, B, AB hoặc O
    private String benhNen; // bệnh nền
    private String thuocDangSuDung; // thuốc đang dùng
    private String ghiChuDacBiet; // ghi chú đặc biệt
    private String trangThaiHoSo; // Đang mở hoặc Đã đóng

    public String getMaHoSo() {
        return maHoSo;
    }

    public void setMaHoSo(String maHoSo) {
        this.maHoSo = maHoSo;
    }

    public String getMaBN() {
        return maBN;
    }

    public void setMaBN(String maBN) {
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

    public String getCanhBaoDiUng() {
        return canhBaoDiUng;
    }

    public void setCanhBaoDiUng(String canhBaoDiUng) {
        this.canhBaoDiUng = canhBaoDiUng;
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

    // Các nhóm thuốc hay gây dị ứng chéo: dị ứng một thuốc trong nhóm thì không dùng các thuốc còn lại của nhóm
    private static final String[][] NHOM_THUOC = {
        {"penicillin", "amoxicillin", "amoxicilin", "ampicillin", "augmentin", "cloxacillin"},
        {"cephalosporin", "cefalexin", "cephalexin", "cefixime", "cefuroxim", "cefadroxil"},
        {"nsaid", "ibuprofen", "diclofenac", "naproxen", "meloxicam", "aspirin"},
        {"sulfa", "sulfamid", "sulfamethoxazole", "bactrim"}
    };

    // Kiểm tra một thuốc hoặc vật liệu có nằm trong cảnh báo dị ứng không (không phân biệt hoa thường),
    // kể cả khi thuốc đó cùng nhóm với chất bệnh nhân dị ứng (ví dụ dị ứng Penicillin thì không kê Amoxicillin)
    public boolean kiemTraCanhBaoDiUng(String ten) {
        if (canhBaoDiUng == null || ten == null || ten.trim().isEmpty()) {
            return false;
        }
        String cantim = ten.trim().toLowerCase();
        for (String chat : canhBaoDiUng.split("[,;]")) {
            String c = chat.trim().toLowerCase();
            if (c.isEmpty()) {
                continue;
            }
            if (cantim.contains(c) || c.contains(cantim)) {
                return true;
            }
            for (String[] nhom : NHOM_THUOC) {
                if (coTrongNhom(nhom, c) && coTrongNhom(nhom, cantim)) {
                    return true;
                }
            }
        }
        return false;
    }

    // Chuỗi s có chứa tên một thuốc nào đó của nhóm không
    private boolean coTrongNhom(String[] nhom, String s) {
        for (String thuoc : nhom) {
            if (s.contains(thuoc)) {
                return true;
            }
        }
        return false;
    }

}
