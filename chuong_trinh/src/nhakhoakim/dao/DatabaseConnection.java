package nhakhoakim.dao;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

// Kết nối SQL Server, đọc thông tin từ file config/database.properties (file này không commit lên Git)
// Bản tạm do Hậu viết để chạy thử; Tâm thay bằng bản đầy đủ (giữ nguyên tên hàm getConnection).
public class DatabaseConnection {

    private static final String FILE_CAU_HINH = "config/database.properties";

    // Mở một kết nối mới; nơi gọi tự đóng bằng try-with-resources
    public static Connection getConnection() {
        Properties p = new Properties();
        Path fileCauHinh = timFileCauHinh();
        try (InputStreamReader in = new InputStreamReader(new FileInputStream(fileCauHinh.toFile()), StandardCharsets.UTF_8)) {
            p.load(in);
        } catch (IOException e) {
            throw new LoiCSDL("Không đọc được file cấu hình " + fileCauHinh + ": " + e.getMessage(), e);
        }
        String url = "jdbc:sqlserver://" + p.getProperty("db.host", "localhost") + ":" + p.getProperty("db.port", "1433")
                + ";databaseName=" + p.getProperty("db.name", "NhaKhoaKim")
                + ";encrypt=false;trustServerCertificate=true";
        try {
            return DriverManager.getConnection(url, p.getProperty("db.user"), p.getProperty("db.password"));
        } catch (SQLException e) {
            throw new LoiCSDL("Không kết nối được CSDL: " + e.getMessage(), e);
        }
    }

    private static Path timFileCauHinh() {
        Path thuMuc = Paths.get(System.getProperty("user.dir")).toAbsolutePath().normalize();
        while (thuMuc != null) {
            Path trongDuAn = thuMuc.resolve(FILE_CAU_HINH);
            if (Files.isRegularFile(trongDuAn)) {
                return trongDuAn;
            }

            Path trongThuMucChuongTrinh = thuMuc.resolve("chuong_trinh").resolve(FILE_CAU_HINH);
            if (Files.isRegularFile(trongThuMucChuongTrinh)) {
                return trongThuMucChuongTrinh;
            }
            thuMuc = thuMuc.getParent();
        }

        throw new LoiCSDL("Không tìm thấy " + FILE_CAU_HINH + " hoặc chuong_trinh/" + FILE_CAU_HINH
                + ". Thư mục chạy hiện tại: " + System.getProperty("user.dir")
                + ". Hãy tạo file từ database.properties.example và điền cấu hình SQL Server.", new IOException());
    }
}
