package nhakhoakim.entity;

import java.util.ArrayList;
import java.util.List;

// Một gợi ý điều trị
public class GoiYDieuTri {

    private int soRang; // Số răng liên quan
    private String mucDo; // Nên làm gấp, Nên làm
    private String lyDo; // Vì sao hệ thống gợi ý
    private DichVu dichVu; // Dịch vụ được gợi ý
    private String luuY; // Lưu ý thêm

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

    // Chi phí tạm tính của một lượt khám
    public static class UocTinhChiPhi {

        // Một dòng dịch vụ trong bảng chi phí
        public static class Dong {
            private String tenDichVu; // Tên dịch vụ
            private int soLuong; // Số lượng
            private long donGia; // Đơn giá

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

            // Thành tiền của dòng là số
            public long getThanhTien() {
                return soLuong * donGia;
            }
        }

        private List<Dong> cacDong = new ArrayList<>(); // Các dòng dịch vụ
        private long tamTinh; // Tổng tiền các dịch vụ
        private int phanTramThue; // Phần trăm VAT
        private long tienThue; // Tiền thuế
        private long tong; // Tổng cộng là tạm tính + thuế

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

        // Tính lại tạm tính, thuế
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
