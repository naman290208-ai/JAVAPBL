import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    
    // Ye method connection banayega aur dusri classes ko dega
    public static Connection getConnection() {
        Connection conn = null;
        try {
            String url = "jdbc:mysql://localhost:3306/smart_parking";
            String user = "root";
            String password = "Naman@22"; // Updated correct password
            
            conn = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            System.out.println("Database connection error!");
            e.printStackTrace();
        }
        return conn;
    }
}