package nhakhoakim.entity;

import java.util.ArrayList;
import java.util.List;

// Một gợi ý điều trị do hệ thống đưa ra từ tình trạng răng và lịch sử khám của bệnh nhân
public class GoiYDieuTri {

    private int soRang;         // số răng liên quan (0 nếu gợi ý chung, không gắn răng)
    private String mucDo;       // Nên làm gấp, Nên làm, Cân nhắc hoặc Định kỳ
    private String lyDo;        // vì sao hệ thống gợi ý
    private DichVu dichVu;      // dịch vụ được gợi ý
    private String luuY;        // lưu ý thêm (ví dụ dịch vụ cần theo liệu trình); có thể rỗng

    public GoiYDieuTri(int soRang, String mucDo, String lyDo, DichVu dichVu, String luuY) {
        this.soRang = soRang;
        this.mucDo = mucDo;
        this.lyDo = lyDo;
        this.dichVu = dichVu;
        this.luuY = luuY;
    }

    public int getSoRang() {
        return soRang;
    }

    public String getMucDo() {
        return mucDo;
    }

    public String getLyDo() {
        return lyDo;
    }

    public DichVu getDichVu() {
        return dichVu;
    }

    public String getLuuY() {
        return luuY;
    }

    // Chi phí tạm tính của một lượt khám: các dòng dịch vụ, tiền trước thuế, thuế và tổng
    public static class UocTinhChiPhi {

        // Một dòng dịch vụ trong bảng chi phí
        public static class Dong {
            private String tenDichVu; // tên dịch vụ
            private int soLuong;      // số lượng (răng hoặc lần)
            private long donGia;      // đơn giá (đồng)

            public Dong(String tenDichVu, int soLuong, long donGia) {
                this.tenDichVu = tenDichVu;
                this.soLuong = soLuong;
                this.donGia = donGia;
            }

            public String getTenDichVu() {
                return tenDichVu;
            }

            public int getSoLuong() {
                return soLuong;
            }

            public long getDonGia() {
                return donGia;
            }

            // Thành tiền của dòng = số lượng nhân đơn giá
            public long getThanhTien() {
                return soLuong * donGia;
            }
        }

        private List<Dong> cacDong = new ArrayList<>(); // các dòng dịch vụ
        private long tamTinh;      // tổng tiền các dịch vụ, chưa thuế
        private int phanTramThue;  // phần trăm VAT
        private long tienThue;     // tiền thuế
        private long tong;         // tổng cộng = tạm tính + thuế

        public List<Dong> getCacDong() {
            return cacDong;
        }

        public long getTamTinh() {
            return tamTinh;
        }

        public int getPhanTramThue() {
            return phanTramThue;
        }

        public long getTienThue() {
            return tienThue;
        }

        public long getTong() {
            return tong;
        }

        // Tính lại tạm tính, thuế và tổng từ các dòng hiện có
        public void tinhTong(int phanTramThue) {
            long tong = 0;
            for (Dong d : cacDong) {
                tong += d.getThanhTien();
            }
            this.phanTramThue = phanTramThue;
            this.tamTinh = tong;
            this.tienThue = Math.round(tong * phanTramThue / 100.0);
            this.tong = this.tamTinh + this.tienThue;
        }
    }
}
