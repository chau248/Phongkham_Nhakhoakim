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
public class DatabaseConnection {

    private static final String TEN_FILE_CAU_HINH = "database.properties";

    // Mở một kết nối mới; nơi gọi tự đóng bằng try-with-resources
    public static Connection getConnection() {
        Path thuMucChay = Paths.get("").toAbsolutePath().normalize();
        Path[] cacFileCauHinh = {
            thuMucChay.resolve("config").resolve(TEN_FILE_CAU_HINH),
            thuMucChay.resolve("chuong_trinh").resolve("config").resolve(TEN_FILE_CAU_HINH)
        };
        Path fileCauHinh = cacFileCauHinh[0];
        for (Path ungVien : cacFileCauHinh) {
            if (Files.isRegularFile(ungVien)) {
                fileCauHinh = ungVien;
                break;
            }
        }

        Properties p = new Properties();
        try (InputStreamReader in = new InputStreamReader(new FileInputStream(fileCauHinh.toFile()), StandardCharsets.UTF_8)) {
            p.load(in);
        } catch (IOException e) {
            throw new LoiCSDL("Không tìm thấy hoặc không đọc được file " + TEN_FILE_CAU_HINH
                    + ". Đã kiểm tra: " + cacFileCauHinh[0] + " và " + cacFileCauHinh[1]
                    + " (sao chép config/database.properties.example thành database.properties rồi điền thông tin SQL Server, xem README.md)", e);
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
