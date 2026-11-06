package controller;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import dao.NghiepVuException;
import dao.TienIchDAO;
import entity.NhanVien;

// Hàm dùng chung cho các controller: kiểm tra quyền, kiểm tra dữ liệu nhập, mật khẩu, nhật ký
public class TienIchController {

    private static final int SO_VONG = 20000;
    private static final Pattern DA_BAM = Pattern.compile("s1\\$[0-9a-f]{16}\\$[0-9a-f]{64}");

    // ----- Kiểm tra quyền -----

    // Người đang đăng nhập phải thuộc một trong các vai trò được phép; trả về người đó
    public static NhanVien yeuCau(Supplier<NhanVien> nguoiDung, String viec, String... vaiTroDuocPhep) {
        NhanVien nv = nguoiDung.get();
        if (nv == null) {
            throw new NghiepVuException("Chưa đăng nhập");
        }
        for (String vt : vaiTroDuocPhep) {
            if (vt.equals(nv.getVaiTro())) {
                return nv;
            }
        }
        throw new NghiepVuException("Chỉ " + String.join(" hoặc ", vaiTroDuocPhep).toLowerCase() + " mới được " + viec);
    }

    // ----- Kiểm tra dữ liệu nhập -----

    // Chuỗi không được dài hơn độ dài cột
    public static void doDai(String giaTri, int toiDa, String tenTruong) {
        if (giaTri != null && giaTri.length() > toiDa) {
            throw new NghiepVuException(tenTruong + " dài tối đa " + toiDa + " ký tự (hiện đang " + giaTri.length() + " ký tự)");
        }
    }

    // Chuỗi rỗng hoặc toàn khoảng trắng thì thành null, ngược lại cắt khoảng trắng hai đầu
    public static String rongThanhNull(String s) {
        return s == null || s.trim().isEmpty() ? null : s.trim();
    }

    // ----- Nhật ký -----

    // Ghi một dòng nhật ký; lỗi ghi nhật ký không làm hỏng thao tác chính
    public static void ghiNhatKy(String maNV, String hanhDong, String doiTuong, String maDoiTuong, String chiTiet) {
        try {
            TienIchDAO.ghiNhatKy(maNV, hanhDong, doiTuong, maDoiTuong, chiTiet);
        } catch (RuntimeException e) {
            System.err.println("Không ghi được nhật ký (" + hanhDong + " " + doiTuong + " " + maDoiTuong + "): " + e.getMessage());
        }
    }

    // ----- Mật khẩu -----

    // Băm mật khẩu: s1$muối$băm
    public static String bamMatKhau(String matKhau) {
        byte[] muoi = new byte[8];
        new SecureRandom().nextBytes(muoi);
        String muoiHex = hex(muoi);
        return "s1$" + muoiHex + "$" + bamVoiMuoi(matKhau, muoiHex);
    }

    // Mật khẩu gõ vào có khớp với bản đã lưu không (bản lưu chữ thường cũ vẫn so được)
    public static boolean khopMatKhau(String matKhauGo, String daLuu) {
        if (matKhauGo == null || daLuu == null) {
            return false;
        }
        if (!daBamMatKhau(daLuu)) {
            return daLuu.equals(matKhauGo);
        }
        String[] phan = daLuu.split("\\$");
        return soSanh(bamVoiMuoi(matKhauGo, phan[1]), phan[2]);
    }

    // Chuỗi đã lưu có phải dạng băm không
    public static boolean daBamMatKhau(String daLuu) {
        return daLuu != null && DA_BAM.matcher(daLuu).matches();
    }

    // So sánh hai chuỗi, thời gian không phụ thuộc vị trí khác nhau
    private static boolean soSanh(String a, String b) {
        if (a.length() != b.length()) {
            return false;
        }
        int khac = 0;
        for (int i = 0; i < a.length(); i++) {
            khac |= a.charAt(i) ^ b.charAt(i);
        }
        return khac == 0;
    }

    private static String bamVoiMuoi(String matKhau, String muoiHex) {
        try {
            KeySpec ks = new PBEKeySpec(matKhau.toCharArray(), muoiHex.getBytes(java.nio.charset.StandardCharsets.UTF_8), SO_VONG, 256);
            return hex(SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(ks).getEncoded());
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new IllegalStateException("Máy không hỗ trợ băm mật khẩu: " + e.getMessage(), e);
        }
    }

    private static String hex(byte[] b) {
        StringBuilder sb = new StringBuilder();
        for (byte x : b) {
            sb.append(String.format("%02x", x));
        }
        return sb.toString();
    }
}
