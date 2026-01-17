package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLiteConnection {

    private static final String URL = "jdbc:sqlite:database.db";

    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    public static void initializeDatabase() {
        String CreateUserTable = """
            CREATE TABLE IF NOT EXISTS users (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT NOT NULL UNIQUE,
                password TEXT NOT NULL,
                role TEXT NOT NULL
            );
            """;
        
        String seedAdmin = """
                INSERT OR IGNORE INTO users (username, password, role)
                VALUES ('admin', 'admin123', 'COORDINATOR');
            """;

        try (var conn = connect();
             var stmt = conn.createStatement()) {
            stmt.execute(CreateUserTable);
            stmt.execute(seedAdmin);
            System.out.println("Database initialized");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
