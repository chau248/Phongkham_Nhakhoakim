package nhakhoakim.ui;

import java.awt.Component;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import nhakhoakim.dao.BenhNhanService;
import nhakhoakim.dao.GoiYDieuTriService;
import nhakhoakim.dao.HoSoBenhAnService;
import nhakhoakim.dao.LuotKhamService;
import nhakhoakim.dao.PhienLamViec;
import nhakhoakim.entity.BenhNhan;
import nhakhoakim.entity.GoiYDieuTri;
import nhakhoakim.entity.GoiYDieuTri.UocTinhChiPhi;
import nhakhoakim.entity.HoSoBenhAn;
import nhakhoakim.entity.LuotKham;

// Màn hình gợi ý điều trị và ước tính chi phí cho lượt khám đang khám: hệ thống đọc sơ đồ răng và lịch sử khám để gợi ý dịch vụ,
// nha sĩ tích chọn gợi ý muốn thêm và xem trước chi phí tạm tính (đã gồm VAT)
@SuppressWarnings("serial") // lớp giao diện Swing, không cần serialVersionUID
public class ManHinhGoiYDieuTri extends JPanel {

    private GoiYDieuTriService goiYService = new GoiYDieuTriService();
    private LuotKhamService luotKhamService = new LuotKhamService();
    private HoSoBenhAnService hoSoService = new HoSoBenhAnService();
    private BenhNhanService benhNhanService = new BenhNhanService();

    private LuotKham luotKham;                                  // lượt khám đang khám
    private List<GoiYDieuTri> dsGoiY = new ArrayList<>();       // dữ liệu bảng gợi ý
    private JTable tblGoiY = TienIch.taoBang(new String[] {"Mức độ", "Răng", "Lý do gợi ý", "Dịch vụ gợi ý", "Giá (đồng)", "Lưu ý"},
            new int[] {95, 50, 400, 150, 90, 260}, "Không có gợi ý nào, tình trạng răng ổn.");    // bảng gợi ý
    private JTable tblChiPhi = TienIch.taoBang(new String[] {"Dịch vụ đã ghi trong lượt khám", "Số lượng", "Đơn giá (đồng)", "Thành tiền (đồng)"},
            new int[] {330, 80, 130, 150}, "Chưa ghi dịch vụ nào trong lượt khám này.");        // bảng chi phí hiện tại
    private JLabel lblTong = TienIch.chuThuong(" ", true);      // tổng chi phí hiện tại
    private JLabel lblThem = TienIch.chuThuong(" ", true);      // chi phí nếu thêm các gợi ý đã chọn

    public ManHinhGoiYDieuTri() {
        List<Component> cacKhoi = new ArrayList<>();
        cacKhoi.add(TienIch.tieuDe("Gợi ý điều trị và ước tính chi phí"));
        try {
            luotKham = luotKhamService.layDangKham(PhienLamViec.getNhanVienHienTai().getMaNV());
            if (luotKham == null) {
                cacKhoi.add(TienIch.khoiChuaCoLuotKham());
            } else {
                dung(cacKhoi);
            }
        } catch (RuntimeException e) {
            TienIch.baoLoi(e);
        }
        TienIch.xepManHinh(this, cacKhoi.toArray(new Component[0]));
        if (luotKham != null) {
            napDuLieu();
        }
    }

    // Dựng màn hình: thông tin lượt khám, bảng gợi ý, bảng chi phí
    private void dung(List<Component> cacKhoi) {
        HoSoBenhAn hs = hoSoService.timTheoMa(luotKham.getMaHoSo());
        BenhNhan bn = benhNhanService.timTheoMa(hs.getMaBN());
        cacKhoi.add(TienIch.chuThuong("Bệnh nhân: " + (bn == null ? hs.getMaBN() : bn.getHoTen()) + "   |   Lượt khám " + luotKham.getMaLuotKham(), true));
        cacKhoi.add(TienIch.doanChu("Gợi ý chỉ để tham khảo, nha sĩ quyết định. Chọn các dòng muốn thêm (giữ Ctrl để chọn nhiều) rồi bấm Thêm vào lượt khám.",
                TienIch.MAU_XAM));

        TienIch.cotXuongDong(tblGoiY, 2);
        TienIch.cotXuongDong(tblGoiY, 5);
        tblGoiY.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        tblGoiY.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                capNhatTienThem();
            }
        });
        cacKhoi.add(TienIch.cuonBang(tblGoiY, 230));

        JButton btnThem = TienIch.nutChinh("Thêm vào lượt khám");
        btnThem.addActionListener(e -> themDaChon());
        lblThem.setForeground(TienIch.MAU_DAM);
        cacKhoi.add(TienIch.dong(btnThem, lblThem));
        cacKhoi.add(TienIch.nhan("Chi phí tạm tính của lượt khám này"));
        cacKhoi.add(TienIch.cuonBang(tblChiPhi, 170));
        lblTong.setFont(TienIch.FONT.deriveFont(java.awt.Font.BOLD, 15f));
        cacKhoi.add(lblTong);
    }

    // Nạp lại gợi ý và chi phí
    private void napDuLieu() {
        try {
            dsGoiY = goiYService.goiY(luotKham.getMaLuotKham());
            List<String[]> dong = new ArrayList<>();
            for (GoiYDieuTri g : dsGoiY) {
                dong.add(new String[] {g.getMucDo(), g.getSoRang() == 0 ? "" : String.valueOf(g.getSoRang()), g.getLyDo(),
                    g.getDichVu().getTenDichVu(), TienIch.tien(g.getDichVu().getGia()), g.getLuuY()});
            }
            TienIch.napBang(tblGoiY, dong);

            UocTinhChiPhi u = goiYService.uocTinhChiPhi(luotKham.getMaLuotKham());
            List<String[]> cp = new ArrayList<>();
            for (UocTinhChiPhi.Dong d : u.getCacDong()) {
                cp.add(new String[] {d.getTenDichVu(), String.valueOf(d.getSoLuong()), TienIch.tien(d.getDonGia()),
                    TienIch.tien(d.getThanhTien())});
            }
            TienIch.napBang(tblChiPhi, cp);
            lblTong.setText("Tạm tính " + TienIch.tien(u.getTamTinh()) + " + VAT " + u.getPhanTramThue() + "% ("
                    + TienIch.tien(u.getTienThue()) + ") = " + TienIch.tien(u.getTong()) + " đồng");
            capNhatTienThem();
        } catch (RuntimeException e) {
            TienIch.baoLoi(e);
        }
    }

    // Các gợi ý đang được chọn trong bảng
    private List<GoiYDieuTri> dangChon() {
        List<GoiYDieuTri> kq = new ArrayList<>();
        for (int vt : tblGoiY.getSelectedRows()) {
            if (vt >= 0 && vt < dsGoiY.size()) {
                kq.add(dsGoiY.get(vt));
            }
        }
        return kq;
    }

    // Hiện số tiền phát sinh nếu thêm các gợi ý đã chọn
    private void capNhatTienThem() {
        List<GoiYDieuTri> chon = dangChon();
        lblThem.setText(chon.isEmpty() ? " " : "Thêm " + chon.size() + " dịch vụ sẽ tăng thêm "
                + TienIch.tien(goiYService.tinhTienThem(chon)) + " đồng (đã gồm VAT)");
    }

    // Thêm các gợi ý đã chọn vào lượt khám: gom cảnh báo của từng dịch vụ và hỏi xác nhận một lần, rồi thêm tất cả cùng lúc
    private void themDaChon() {
        List<GoiYDieuTri> chon = dangChon();
        if (chon.isEmpty()) {
            TienIch.thongTin("Vui lòng chọn ít nhất một gợi ý trong bảng.");
            return;
        }
        try {
            StringBuilder canhBao = new StringBuilder();
            Set<String> daXet = new HashSet<>(); // một dịch vụ chỉ cảnh báo một lần dù được gợi ý cho nhiều răng
            for (GoiYDieuTri g : chon) {
                String ma = g.getDichVu().getMaDichVu();
                if (!daXet.add(ma)) {
                    continue;
                }
                String cb = luotKhamService.canhBaoThuThuat(luotKham.getMaLuotKham(), ma);
                if (cb != null) {
                    canhBao.append("Dịch vụ \"").append(g.getDichVu().getTenDichVu()).append("\":\n").append(cb).append("\n\n");
                }
            }
            if (canhBao.length() > 0 && !TienIch.xacNhan(canhBao + "Vẫn thêm các dịch vụ đã chọn vào lượt khám?")) {
                return;
            }
            goiYService.themNhieuVaoLuotKham(luotKham.getMaLuotKham(), chon);
            napDuLieu();
            TienIch.thongTin("Đã thêm " + chon.size() + " dịch vụ vào lượt khám.");
        } catch (RuntimeException e) {
            TienIch.baoLoi(e);
        }
    }
}
