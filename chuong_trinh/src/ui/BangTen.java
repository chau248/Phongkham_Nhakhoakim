package ui;

import java.util.HashMap;
import java.util.Map;
import controller.BenhNhanController;
import controller.DichVuController;
import controller.NhaSiPhuTrachController;
import entity.BenhNhan;
import entity.DichVu;

// Tra tên từ mã để hiện lên bảng
class BangTen {

    private BenhNhanController benhNhanController = new BenhNhanController();
    private NhaSiPhuTrachController nhaSiController = new NhaSiPhuTrachController();
    private DichVuController dichVuController = new DichVuController();
    private Map<String, String> benhNhan = new HashMap<>();
    private Map<String, String> nhaSi = new HashMap<>();
    private Map<String, String> dichVu;

    // Họ tên bệnh nhân
    String benhNhan(String ma) {
        if (ma == null) {
            return "";
        }
        if (!benhNhan.containsKey(ma)) {
            BenhNhan bn = benhNhanController.timTheoMa(ma);
            benhNhan.put(ma, bn == null ? ma : bn.getHoTen());
        }
        return benhNhan.get(ma);
    }

    // Họ tên nhân viên
    String nhaSi(String ma) {
        if (ma == null) {
            return "";
        }
        if (!nhaSi.containsKey(ma)) {
            nhaSi.put(ma, nhaSiController.layTen(ma));
        }
        return nhaSi.get(ma);
    }

    // Tên dịch vụ
    String dichVu(String ma) {
        if (ma == null) {
            return "";
        }
        if (dichVu == null) {
            dichVu = new HashMap<>();
            for (DichVu dv : dichVuController.layTatCa()) {
                dichVu.put(dv.getMaDichVu(), dv.getTenDichVu());
            }
        }
        return dichVu.getOrDefault(ma, ma);
    }
}
