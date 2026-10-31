package nhakhoakim.ui;

import java.util.HashMap;
import java.util.Map;
import nhakhoakim.controller.BenhNhanController;
import nhakhoakim.controller.DichVuController;
import nhakhoakim.controller.NhaSiPhuTrachController;
import nhakhoakim.entity.BenhNhan;
import nhakhoakim.entity.DichVu;

// Tra tên từ mã (bệnh nhân, nha sĩ, dịch vụ) để hiện lên bảng; mỗi mã chỉ hỏi CSDL một lần trong suốt đời của đối tượng này
class BangTen {

    private BenhNhanController benhNhanController = new BenhNhanController();
    private NhaSiPhuTrachController nhaSiController = new NhaSiPhuTrachController();
    private DichVuController dichVuController = new DichVuController();
    private Map<String, String> benhNhan = new HashMap<>();
    private Map<String, String> nhaSi = new HashMap<>();
    private Map<String, String> dichVu;

    // Họ tên bệnh nhân; không tìm thấy thì trả về chính mã
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

    // Họ tên nhân viên (nha sĩ); không tìm thấy thì trả về chính mã
    String nhaSi(String ma) {
        if (ma == null) {
            return "";
        }
        if (!nhaSi.containsKey(ma)) {
            nhaSi.put(ma, nhaSiController.layTen(ma));
        }
        return nhaSi.get(ma);
    }

    // Tên dịch vụ; không tìm thấy thì trả về chính mã
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
