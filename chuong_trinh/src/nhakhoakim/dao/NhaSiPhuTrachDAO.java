package nhakhoakim.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import nhakhoakim.entity.HangSo;

// Truy vấn để hiện tên nha sĩ phụ trách trên màn hình bệnh nhân và lượt khám (chỉ đọc)
// Hỏi một lần cho cả danh sách, không hỏi từng dòng, để bảng nhiều dòng vẫn nhanh
public class NhaSiPhuTrachDAO {

    // Họ tên của các nhân viên theo mã: trả về bảng tra mã -> họ tên (mã không có trong CSDL thì không có trong bảng)
    public Map<String, String> layTen(Collection<String> dsMaNV) {
        Map<String, String> kq = new HashMap<>();
        List<String> ds = boTrungVaRong(dsMaNV);
        if (ds.isEmpty()) {
            return kq;
        }
        String sql = "SELECT maNV, hoTen FROM dbo.NhanVien WHERE maNV IN (" + dauHoi(ds.size()) + ")";
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            for (int i = 0; i < ds.size(); i++) {
                ps.setString(i + 1, ds.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    kq.put(rs.getString("maNV"), rs.getString("hoTen"));
                }
            }
        } catch (SQLException e) {
            throw new LoiCSDL("Không đọc được tên nhân viên: " + e.getMessage(), e);
        }
        return kq;
    }

    // Mã nha sĩ của lượt khám gần nhất (không tính lượt đã hủy) của từng bệnh nhân: trả về bảng tra mã bệnh nhân -> mã nha sĩ
    // Bệnh nhân chưa khám lần nào thì không có trong bảng
    public Map<String, String> layNhaSiKhamGanNhat(Collection<String> dsMaBN) {
        Map<String, String> kq = new HashMap<>();
        List<String> ds = boTrungVaRong(dsMaBN);
        if (ds.isEmpty()) {
            return kq;
        }
        String sql = "SELECT h.maBN, k.maNhanVienPhuTrach FROM dbo.HoSoBenhAn h"
                + " CROSS APPLY (SELECT TOP 1 x.maNhanVienPhuTrach FROM dbo.LuotKham x"
                + " WHERE x.maHoSo = h.maHoSo AND x.trangThai <> N'" + HangSo.DA_HUY + "' ORDER BY x.gioBatDau DESC) k"
                + " WHERE h.maBN IN (" + dauHoi(ds.size()) + ")";
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            for (int i = 0; i < ds.size(); i++) {
                ps.setString(i + 1, ds.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    kq.put(rs.getString("maBN"), rs.getString("maNhanVienPhuTrach"));
                }
            }
        } catch (SQLException e) {
            throw new LoiCSDL("Không đọc được nha sĩ khám gần nhất: " + e.getMessage(), e);
        }
        return kq;
    }

    // Bỏ mã rỗng và mã trùng (giữ nguyên thứ tự)
    private static List<String> boTrungVaRong(Collection<String> ds) {
        LinkedHashSet<String> gon = new LinkedHashSet<>();
        if (ds != null) {
            for (String s : ds) {
                if (s != null && !s.trim().isEmpty()) {
                    gon.add(s.trim());
                }
            }
        }
        return new ArrayList<>(gon);
    }

    // Chuỗi "?, ?, ?" với n dấu hỏi để dùng trong IN (...)
    private static String dauHoi(int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) {
            sb.append(i == 0 ? "?" : ", ?");
        }
        return sb.toString();
    }
}
