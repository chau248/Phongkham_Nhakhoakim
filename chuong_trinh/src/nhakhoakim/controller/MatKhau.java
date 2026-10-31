package nhakhoakim.controller;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.regex.Pattern;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

// Băm và kiểm tra mật khẩu: lưu dạng "s1$<muối 16 ký tự hex>$<băm 64 ký tự hex>" (PBKDF2-HMAC-SHA256, 20.000 vòng).
// Mật khẩu cũ lưu chữ thường (không có dạng trên) vẫn đăng nhập được và được băm lại ngay lần đăng nhập đầu.
public class MatKhau {

    private static final int SO_VONG = 20000;
    private static final Pattern DA_BAM = Pattern.compile("s1\\$[0-9a-f]{16}\\$[0-9a-f]{64}");

    // Băm mật khẩu với một "muối" ngẫu nhiên mới
    public static String bam(String matKhau) {
        byte[] muoi = new byte[8];
        new SecureRandom().nextBytes(muoi);
        String muoiHex = hex(muoi);
        return "s1$" + muoiHex + "$" + bamVoiMuoi(matKhau, muoiHex);
    }

    // Mật khẩu người dùng gõ có khớp với chuỗi đã lưu không (chuỗi lưu có thể là dạng băm hoặc mật khẩu chữ thường cũ)
    public static boolean khop(String matKhauGo, String daLuu) {
        if (matKhauGo == null || daLuu == null) {
            return false;
        }
        if (!daBam(daLuu)) {
            return daLuu.equals(matKhauGo);
        }
        String[] phan = daLuu.split("\\$");
        return so(bamVoiMuoi(matKhauGo, phan[1]), phan[2]);
    }

    // Chuỗi đã lưu có phải dạng băm không (không phải thì cần băm lại)
    public static boolean daBam(String daLuu) {
        return daLuu != null && DA_BAM.matcher(daLuu).matches();
    }

    // So sánh hai chuỗi trong thời gian không phụ thuộc vào vị trí khác nhau đầu tiên
    private static boolean so(String a, String b) {
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
