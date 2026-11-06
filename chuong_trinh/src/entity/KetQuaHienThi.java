package entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// Các lớp dùng để hiện kết quả lên màn hình (không lưu CSDL): gợi ý điều trị và dòng thời gian điều trị
public class KetQuaHienThi {

    // Một gợi ý điều trị
    public static class GoiYDieuTri {

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

    // Dòng thời gian điều trị
    public static class DongThoiGian {

        private List<SuKienDieuTri> suKien = new ArrayList<>(); // Các sự kiện, mới nhất trước
        private int soLuotKham; // Số lượt khám đã hoàn thành
        private LocalDate lanKhamGanNhat; // Ngày của lượt khám gần nhất
        private int soRangDaNho; // Số răng hiện đang ở trạng
        private int soRangImplantCauRang; // Số răng đã phục hình
        private int soLieuTrinhDangLam; // Số liệu trình đang thực

        public List<SuKienDieuTri> getSuKien() {
            return suKien;
        }

        public int getSoLuotKham() {
            return soLuotKham;
        }

        public void setSoLuotKham(int soLuotKham) {
            this.soLuotKham = soLuotKham;
        }

        public LocalDate getLanKhamGanNhat() {
            return lanKhamGanNhat;
        }

        public void setLanKhamGanNhat(LocalDate lanKhamGanNhat) {
            this.lanKhamGanNhat = lanKhamGanNhat;
        }

        public int getSoRangDaNho() {
            return soRangDaNho;
        }

        public void setSoRangDaNho(int soRangDaNho) {
            this.soRangDaNho = soRangDaNho;
        }

        public int getSoRangImplantCauRang() {
            return soRangImplantCauRang;
        }

        public void setSoRangImplantCauRang(int soRangImplantCauRang) {
            this.soRangImplantCauRang = soRangImplantCauRang;
        }

        public int getSoLieuTrinhDangLam() {
            return soLieuTrinhDangLam;
        }

        public void setSoLieuTrinhDangLam(int soLieuTrinhDangLam) {
            this.soLieuTrinhDangLam = soLieuTrinhDangLam;
        }

        // Một sự kiện trên dòng thời
        public static class SuKienDieuTri {

            // Các loại sự kiện
            public static final String LOAI_DANG_KY = "Đăng ký";
            public static final String LOAI_KHAM = "Khám";
            public static final String LOAI_LIEU_TRINH = "Liệu trình";
            public static final String LOAI_TAI_KHAM = "Tái khám";

            private LocalDateTime thoiGian; // Thời điểm xảy ra sự kiện
            private String loai; // Đăng ký, Khám
            private String tieuDe; // Dòng tiêu đề ngắn
            private String chiTiet; // Nội dung chi tiết, nhiều dòng
            private boolean daQua; // True nếu sự kiện đã xảy ra

            // Tự tính đã qua hay
            public SuKienDieuTri(LocalDateTime thoiGian, String loai, String tieuDe, String chiTiet) {
                this(thoiGian, loai, tieuDe, chiTiet, !thoiGian.isAfter(LocalDateTime.now()));
            }

            // Truyền sẵn đã qua hay
            public SuKienDieuTri(LocalDateTime thoiGian, String loai, String tieuDe, String chiTiet, boolean daQua) {
                this.thoiGian = thoiGian;
                this.loai = loai;
                this.tieuDe = tieuDe;
                this.chiTiet = chiTiet;
                this.daQua = daQua;
            }

            public LocalDateTime getThoiGian() {
                return thoiGian;
            }

            public String getLoai() {
                return loai;
            }

            public String getTieuDe() {
                return tieuDe;
            }

            public String getChiTiet() {
                return chiTiet;
            }

            public boolean isDaQua() {
                return daQua;
            }
        }
    }
}
