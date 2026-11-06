package controller;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import dao.CauHinhDAO;
import dao.NghiepVuException;
import entity.DanhMuc;
import entity.HangSo;
import entity.NhanVien;

// Nghiệp vụ cấu hình hệ thống: danh mục
public class CauHinhController {

    // Tên các tham số của phòng khám
    public static final String TEN_PHONG_KHAM = "tenPhongKham";
    public static final String DIA_CHI = "diaChi";
    public static final String SO_DIEN_THOAI = "soDienThoai";
    public static final String MA_SO_THUE = "maSoThue";
    public static final String PHAN_TRAM_VAT = "phanTramVAT";

    private CauHinhDAO cauHinhDAO = new CauHinhDAO();
    private Supplier<NhanVien> nguoiDung = PhienLamViec::getNhanVienHienTai;

    // Tất cả tên trong một danh mục
    public List<String> layDanhMuc(DanhMuc dm) {
        return cauHinhDAO.layTatCa(dm);
    }

    // Các phòng dùng
    public List<String> layPhongKham() {
        return cauHinhDAO.layPhongKham();
    }

    // Thêm một tên vào danh mục
    public void themDanhMuc(DanhMuc dm, String ten) {
        NhanVien nguoi = kiemTraQuyenDanhMuc(dm);
        String t = chuanHoaTen(ten, dm.getNhan(), dm.getDoDaiToiDa());
        if (cauHinhDAO.daCo(dm, t)) {
            throw new NghiepVuException(dm.getNhan() + " \"" + t + "\" đã có trong danh mục");
        }
        cauHinhDAO.them(dm, t);
    }

    // Thêm phòng mới
    public void themPhong(String ten, boolean laPhongKham) {
        NhanVien nguoi = TienIchController.yeuCau(nguoiDung, "sửa danh mục", HangSo.VAI_TRO_QUAN_LY);
        String t = chuanHoaTen(ten, DanhMuc.PHONG.getNhan(), DanhMuc.PHONG.getDoDaiToiDa());
        if (cauHinhDAO.daCo(DanhMuc.PHONG, t)) {
            throw new NghiepVuException("Phòng \"" + t + "\" đã có trong danh mục");
        }
        cauHinhDAO.themPhong(t, laPhongKham);
    }

    // Xóa một tên khỏi danh mục
    public void xoaDanhMuc(DanhMuc dm, String ten) {
        NhanVien nguoi = kiemTraQuyenDanhMuc(dm);
        int dung = cauHinhDAO.demDangDung(dm, ten);
        if (dung > 0) {
            throw new NghiepVuException(dm.getNhan() + " \"" + ten + "\" đang được dùng ở " + dung + " nơi nên không xóa được");
        }
        cauHinhDAO.xoa(dm, ten);
    }

    // Đổi tên một mục gợi ý
    public void doiTenDanhMuc(DanhMuc dm, String tenCu, String tenMoi) {
        NhanVien nguoi = kiemTraQuyenDanhMuc(dm);
        if (!dm.laGoiY()) {
            throw new NghiepVuException(dm.getNhan() + " đang được dùng làm khóa nên không đổi tên, hãy thêm mục mới");
        }
        String t = chuanHoaTen(tenMoi, dm.getNhan(), dm.getDoDaiToiDa());
        if (!t.equals(tenCu) && cauHinhDAO.daCo(dm, t)) {
            throw new NghiepVuException(dm.getNhan() + " \"" + t + "\" đã có trong danh mục");
        }
        cauHinhDAO.doiTen(dm, tenCu, t);
    }

    // Mọi danh mục chỉ quản lý sửa
    private NhanVien kiemTraQuyenDanhMuc(DanhMuc dm) {
        return TienIchController.yeuCau(nguoiDung, "sửa danh mục", HangSo.VAI_TRO_QUAN_LY);
    }

    // Tên các vai trò
    public List<String> layTenVaiTro() {
        List<String> ds = new ArrayList<>();
        for (String[] vt : cauHinhDAO.layVaiTro()) {
            ds.add(vt[0]);
        }
        return ds;
    }

    // Vai trò có phải vai trò gốc
    public boolean laVaiTroGoc(String tenVaiTro) {
        return cauHinhDAO.laVaiTroGoc(tenVaiTro);
    }

    // Mọi chức năng gom theo nhóm
    public Map<String, List<String>> layChucNangTheoNhom() {
        return gomTheoNhom(cauHinhDAO.layChucNang());
    }

    // Chức năng của một vai trò gom theo nhóm
    public Map<String, List<String>> layChucNangCuaVaiTro(String tenVaiTro) {
        return gomTheoNhom(cauHinhDAO.layChucNangCuaVaiTro(tenVaiTro));
    }

    // Tên các chức năng mà vai trò được dùng
    public List<String> layTenChucNangCuaVaiTro(String tenVaiTro) {
        List<String> ds = new ArrayList<>();
        for (String[] cn : cauHinhDAO.layChucNangCuaVaiTro(tenVaiTro)) {
            ds.add(cn[1]);
        }
        return ds;
    }

    // Giá trị một tham số
    public String layThamSo(String ten) {
        String gt = cauHinhDAO.layThamSo(ten);
        return gt == null ? "" : gt;
    }

    // Thuế VAT (%) dùng khi lập hóa đơn; tham số lỗi thì dùng 8
    public double layPhanTramVAT() {
        try {
            double v = Double.parseDouble(layThamSo(PHAN_TRAM_VAT).replace(',', '.'));
            return v < 0 || v > 100 ? 8 : v;
        } catch (NumberFormatException e) {
            return 8;
        }
    }

    // Lưu thông tin phòng khám và thuế
    public void luuThongTin(String tenPhongKham, String diaChi, String soDienThoai, String maSoThue, String phanTramVAT) {
        NhanVien nguoi = TienIchController.yeuCau(nguoiDung, "sửa thông tin phòng khám", HangSo.VAI_TRO_QUAN_LY);
        String ten = TienIchController.rongThanhNull(tenPhongKham);
        if (ten == null) {
            throw new NghiepVuException("Tên phòng khám không được để trống");
        }
        String dc = TienIchController.rongThanhNull(diaChi);
        String sdt = TienIchController.rongThanhNull(soDienThoai);
        String mst = TienIchController.rongThanhNull(maSoThue);
        if (mst != null && !mst.matches("[0-9]{10}(-[0-9]{3})?")) {
            throw new NghiepVuException("Mã số thuế gồm 10 số (hoặc 10 số, dấu gạch, 3 số)");
        }
        double vat;
        try {
            vat = Double.parseDouble(String.valueOf(phanTramVAT).trim().replace(',', '.'));
        } catch (NumberFormatException e) {
            throw new NghiepVuException("Thuế VAT phải là số");
        }
        if (vat < 0 || vat > 100) {
            throw new NghiepVuException("Thuế VAT phải từ 0 đến 100 (%)");
        }
        cauHinhDAO.datThamSo(TEN_PHONG_KHAM, ten);
        cauHinhDAO.datThamSo(DIA_CHI, dc == null ? "" : dc);
        cauHinhDAO.datThamSo(SO_DIEN_THOAI, sdt == null ? "" : sdt);
        cauHinhDAO.datThamSo(MA_SO_THUE, mst == null ? "" : mst);
        cauHinhDAO.datThamSo(PHAN_TRAM_VAT, vat == Math.floor(vat) ? String.valueOf((long) vat) : String.valueOf(vat));
    }

    // Tên không rỗng
    private String chuanHoaTen(String ten, String nhan, int toiDa) {
        String t = ten == null ? null : ten.trim().replaceAll("\\s+", " ");
        if (t == null || t.isEmpty()) {
            throw new NghiepVuException(nhan + " không được để trống");
        }
        return t;
    }

    private Map<String, List<String>> gomTheoNhom(List<String[]> dong) {
        Map<String, List<String>> kq = new LinkedHashMap<>();
        for (String[] d : dong) {
            kq.computeIfAbsent(d[0], k -> new ArrayList<>()).add(d[1]);
        }
        return kq;
    }
}
