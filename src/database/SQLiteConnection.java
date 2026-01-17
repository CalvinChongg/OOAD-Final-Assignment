package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLiteConnection {

    private static final String URL = "jdbc:sqlite:database.db";

    public static Connection connect() throws SQLException {
        try {
            // CRITICAL FIX: Forces the driver to load
            Class.forName("org.sqlite.JDBC");
            return DriverManager.getConnection(URL);
        } catch (ClassNotFoundException e) {
            System.out.println("SQLite Driver Library missing! Check your lib folder.");
            e.printStackTrace();
            return null;
        }
    }

    public static void initializeDatabase() {
        // Creates the table with the ROLES column
        var sql = """
            CREATE TABLE IF NOT EXISTS users (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT NOT NULL UNIQUE,
                password TEXT NOT NULL,
                roles TEXT
            );
            """;

        try (var conn = connect();
             var stmt = conn.createStatement()) {
            if (conn != null) {
                stmt.execute(sql);
                System.out.println("Database initialized successfully.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}