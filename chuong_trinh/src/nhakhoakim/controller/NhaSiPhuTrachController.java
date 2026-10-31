package nhakhoakim.controller;

import nhakhoakim.dao.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Cho biết tên nha sĩ phụ trách để các màn hình hiện lên cho người dùng thấy ai đang khám, ai phụ trách liệu trình
public class NhaSiPhuTrachController {

    private NhaSiPhuTrachDAO dao;

    // Dùng DAO thật
    public NhaSiPhuTrachController() {
        this(new NhaSiPhuTrachDAO());
    }

    // Dùng DAO truyền vào (để kiểm thử không cần CSDL)
    public NhaSiPhuTrachController(NhaSiPhuTrachDAO dao) {
        this.dao = dao;
    }

    // Tên một nhân viên; mã rỗng thì trả về chuỗi rỗng, không tìm thấy tên thì trả về chính mã
    public String layTen(String maNV) {
        if (maNV == null || maNV.trim().isEmpty()) {
            return "";
        }
        return tenHoacMa(dao.layTen(List.of(maNV.trim())), maNV.trim());
    }

    // Bảng tra mã nhân viên -> họ tên cho cả danh sách mã (hỏi CSDL một lần)
    public Map<String, String> layTen(Collection<String> dsMaNV) {
        return dao.layTen(dsMaNV);
    }

    // Bảng tra mã bệnh nhân -> họ tên nha sĩ của lượt khám gần nhất (bệnh nhân chưa khám lần nào thì không có trong bảng)
    public Map<String, String> layTenNhaSiKhamGanNhat(Collection<String> dsMaBN) {
        Map<String, String> maNhaSi = dao.layNhaSiKhamGanNhat(dsMaBN);
        Map<String, String> ten = dao.layTen(maNhaSi.values());
        Map<String, String> kq = new HashMap<>();
        for (Map.Entry<String, String> e : maNhaSi.entrySet()) {
            kq.put(e.getKey(), tenHoacMa(ten, e.getValue()));
        }
        return kq;
    }

    // Tên trong bảng tra; không có thì dùng mã để người dùng vẫn biết là nhân viên nào
    public static String tenHoacMa(Map<String, String> bangTen, String maNV) {
        if (maNV == null) {
            return "";
        }
        String ten = bangTen.get(maNV);
        return ten == null ? maNV : ten;
    }
}
