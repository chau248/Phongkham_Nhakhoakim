package nhakhoakim.entity;

import java.time.LocalDate;
import java.util.ArrayList;

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
    
    
    

    public HoSoBenhAn(String maHoSo, String maBN, LocalDate ngayTao, String tienSuBenh, String canhBaoDiUng,
			String nhomMau, String benhNen, String thuocDangSuDung, String ghiChuDacBiet, String trangThaiHoSo) {
		super();
		this.maHoSo = maHoSo;
		this.maBN = maBN;
		this.ngayTao = ngayTao;
		this.tienSuBenh = tienSuBenh;
		this.canhBaoDiUng = canhBaoDiUng;
		this.nhomMau = nhomMau;
		this.benhNen = benhNen;
		this.thuocDangSuDung = thuocDangSuDung;
		this.ghiChuDacBiet = ghiChuDacBiet;
		this.trangThaiHoSo = trangThaiHoSo;
	}

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

    // Các giá trị người dùng hay gõ để nói "không có dị ứng"; không được coi là một tác nhân gây dị ứng
    private static final String[] KHONG_DI_UNG = {"không", "không có", "không dị ứng", "không có dị ứng", "ko", "ko có",
        "chưa có", "chưa ghi nhận", "chưa rõ", "none", "n/a", "-"};

    // Giá trị này có nghĩa là "không dị ứng" (ví dụ "Không", "Không có") chứ không phải tên một chất
    public static boolean laGiaTriKhongDiUng(String giaTri) {
        if (giaTri == null) {
            return true;
        }
        String g = giaTri.trim().toLowerCase();
        for (String k : KHONG_DI_UNG) {
            if (g.equals(k)) {
                return true;
            }
        }
        return g.isEmpty();
    }

    // Hồ sơ có ghi nhận ít nhất một tác nhân dị ứng thật (không tính "Không", "Không có"...)
    public boolean coCanhBaoDiUng() {
        if (canhBaoDiUng == null) {
            return false;
        }
        for (String chat : canhBaoDiUng.split("[,;]")) {
            if (!laGiaTriKhongDiUng(chat)) {
                return true;
            }
        }
        return false;
    }

    // Kiểm tra một thuốc hoặc vật liệu có nằm trong cảnh báo dị ứng không (không phân biệt hoa thường),
    // kể cả khi thuốc đó cùng nhóm với chất bệnh nhân dị ứng (ví dụ dị ứng Penicillin thì không kê Amoxicillin)
    public boolean kiemTraCanhBaoDiUng(String ten) {
        if (canhBaoDiUng == null || ten == null || ten.trim().isEmpty()) {
            return false;
        }
        String cantim = ten.trim().toLowerCase();
        for (String chat : canhBaoDiUng.split("[,;]")) {
            String c = chat.trim().toLowerCase();
            if (laGiaTriKhongDiUng(c)) {
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