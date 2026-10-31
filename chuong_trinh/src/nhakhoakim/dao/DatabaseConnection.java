package nhakhoakim.dao;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
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
        try (InputStreamReader in = new InputStreamReader(new FileInputStream(FILE_CAU_HINH), StandardCharsets.UTF_8)) {
            p.load(in);
        } catch (IOException e) {
            throw new LoiCSDL("Chưa có file " + FILE_CAU_HINH + " (sao chép config/database.properties.example thành database.properties rồi điền thông tin SQL Server, xem README.md)", e);
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
}
