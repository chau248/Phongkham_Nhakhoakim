package nhakhoakim.controller;

import nhakhoakim.dao.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import nhakhoakim.entity.CaTruc;

// Ca trực của nhân viên
// Bản tạm do Hậu viết để chạy thử: ca trực hôm nay giống 02_seed.sql (dữ liệu nằm trong bộ nhớ).
// Tâm thay bằng bản đọc từ CSDL (giữ nguyên tên hàm).
public class CaTrucController {

    private static final List<CaTruc> DANH_SACH = new ArrayList<>();

    static {
        them("CA000001", "NV000002", "Sáng", "Phòng 1");
        them("CA000002", "NV000002", "Chiều", "Phòng 1");
        them("CA000003", "NV000003", "Sáng", "Phòng 2");
        them("CA000004", "NV000004", "Sáng", "Phòng 1");
        them("CA000005", "NV000005", "Chiều", "Phòng 2");
        them("CA000006", "NV000006", "Sáng", "Quầy lễ tân");
    }

    // Thêm một ca trực mẫu của hôm nay
    private static void them(String ma, String maNV, String caLam, String phong) {
        CaTruc ca = new CaTruc();
        ca.setMaCaTruc(ma);
        ca.setMaNV(maNV);
        ca.setNgay(LocalDate.now());
        ca.setCaLam(caLam);
        ca.setPhong(phong);
        ca.setTrangThai("Có mặt");
        DANH_SACH.add(ca);
    }

    // Các ca trực của một nhân viên trong khoảng ngày (gồm cả hai đầu)
    public List<CaTruc> layTheoNhanVienVaKhoang(String maNV, LocalDate tu, LocalDate den) {
        List<CaTruc> kq = new ArrayList<>();
        for (CaTruc ca : DANH_SACH) {
            if (ca.getMaNV().equals(maNV) && !ca.getNgay().isBefore(tu) && !ca.getNgay().isAfter(den)) {
                kq.add(ca);
            }
        }
        return kq;
    }

    // Nhân viên có ca trực Có mặt vào ngày đó không; caLam là Sáng hoặc Chiều, null nghĩa là ca nào cũng được
    public boolean coCaTruc(String maNV, LocalDate ngay, String caLam) {
        for (CaTruc ca : layTheoNhanVienVaKhoang(maNV, ngay, ngay)) {
            if ("Có mặt".equals(ca.getTrangThai()) && (caLam == null || caLam.equals(ca.getCaLam()))) {
                return true;
            }
        }
        return false;
    }
}
