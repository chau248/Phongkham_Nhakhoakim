package nhakhoakim.controller;

import nhakhoakim.dao.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Cho biết tên nha sĩ phụ trách
public class NhaSiPhuTrachController {

    private NhaSiPhuTrachDAO dao;

    // Dùng DAO thật
    public NhaSiPhuTrachController() {
        this(new NhaSiPhuTrachDAO());
    }

    // Dùng DAO truyền vào
    public NhaSiPhuTrachController(NhaSiPhuTrachDAO dao) {
        this.dao = dao;
    }

    // Tên một nhân viên
    public String layTen(String maNV) {
        if (maNV == null || maNV.trim().isEmpty()) {
            return "";
        }
        return tenHoacMa(dao.layTen(List.of(maNV.trim())), maNV.trim());
    }

    // Bảng tra mã nhân viên -> họ tên
    public Map<String, String> layTen(Collection<String> dsMaNV) {
        return dao.layTen(dsMaNV);
    }

    // Bảng tra mã bệnh nhân ->
    public Map<String, String> layTenNhaSiKhamGanNhat(Collection<String> dsMaBN) {
        Map<String, String> maNhaSi = dao.layNhaSiKhamGanNhat(dsMaBN);
        Map<String, String> ten = dao.layTen(maNhaSi.values());
        Map<String, String> kq = new HashMap<>();
        for (Map.Entry<String, String> e : maNhaSi.entrySet()) {
            kq.put(e.getKey(), tenHoacMa(ten, e.getValue()));
        }
        return kq;
    }

    // Tên trong bảng tra
    public static String tenHoacMa(Map<String, String> bangTen, String maNV) {
        if (maNV == null) {
            return "";
        }
        String ten = bangTen.get(maNV);
        return ten == null ? maNV : ten;
    }
}
