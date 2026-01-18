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
        
        String seedUsers = """
            INSERT OR IGNORE INTO users (username, password, role) VALUES
            ('student1', 'pass', 'STUDENT'),
            ('eval1', 'pass', 'EVALUATOR'),
            ('coord1', 'pass', 'COORDINATOR'),
            ('admin1', 'pass', 'ADMIN');
        """;

        try (var conn = connect();
             var stmt = conn.createStatement()) {
            stmt.execute(CreateUserTable);
            stmt.execute(seedUsers);
            System.out.println("Database initialized");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
