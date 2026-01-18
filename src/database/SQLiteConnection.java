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
        // Create Users Table
        String createUserTable = """
            CREATE TABLE IF NOT EXISTS users (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT NOT NULL UNIQUE,
                password TEXT NOT NULL,
                role TEXT NOT NULL
            );
            """;

        // Create Submissions Table
        String createSubmissionsTable = """
            CREATE TABLE IF NOT EXISTS submissions (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                student_id INTEGER NOT NULL,
                research_title TEXT NOT NULL,
                abstract TEXT NOT NULL,
                supervisor_name TEXT,
                presentation_type TEXT CHECK(presentation_type IN ('ORAL', 'POSTER')),
                file_path TEXT,
                submission_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                status TEXT DEFAULT 'PENDING',
                FOREIGN KEY (student_id) REFERENCES users(id) ON DELETE CASCADE
            );
            """;

        // Create Sessions Table
        String createSessionsTable = """
            CREATE TABLE IF NOT EXISTS sessions (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                session_name TEXT NOT NULL,
                session_date DATE NOT NULL,
                start_time TIME,
                end_time TIME,
                venue TEXT,
                session_type TEXT CHECK(session_type IN ('ORAL', 'POSTER')),
                max_presentations INTEGER DEFAULT 10,
                status TEXT DEFAULT 'PLANNED'
            );
            """;

       // Create Session Assignments Table
        String createSessionAssignmentsTable = """
            CREATE TABLE IF NOT EXISTS session_assignments (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                session_id INTEGER NOT NULL,
                submission_id INTEGER NOT NULL,
                presentation_order INTEGER,
                evaluator_id INTEGER,
                FOREIGN KEY (session_id) REFERENCES sessions(id) ON DELETE CASCADE,
                FOREIGN KEY (submission_id) REFERENCES submissions(id) ON DELETE CASCADE,
                FOREIGN KEY (evaluator_id) REFERENCES users(id) ON DELETE SET NULL
            );
            """;

        // Create Evaluations Table with Rubrics
        String createEvaluationsTable = """
            CREATE TABLE IF NOT EXISTS evaluations (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                submission_id INTEGER NOT NULL,
                evaluator_id INTEGER NOT NULL,
                problem_clarity INTEGER CHECK(problem_clarity BETWEEN 1 AND 10),
                methodology INTEGER CHECK(methodology BETWEEN 1 AND 10),
                results INTEGER CHECK(results BETWEEN 1 AND 10),
                presentation_quality INTEGER CHECK(presentation_quality BETWEEN 1 AND 10),
                overall_score REAL,
                comments TEXT,
                evaluation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (submission_id) REFERENCES submissions(id) ON DELETE CASCADE,
                FOREIGN KEY (evaluator_id) REFERENCES users(id) ON DELETE CASCADE,
                UNIQUE(submission_id, evaluator_id)
            );
            """;

        // Create Poster Boards Table
        String createPosterBoardsTable = """
            CREATE TABLE IF NOT EXISTS poster_boards (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                board_id TEXT NOT NULL UNIQUE,
                location TEXT,
                submission_id INTEGER UNIQUE,
                FOREIGN KEY (submission_id) REFERENCES submissions(id) ON DELETE SET NULL
            );
            """;

        // Create Awards Table
        String createAwardsTable = """
            CREATE TABLE IF NOT EXISTS awards (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                award_type TEXT CHECK(award_type IN ('BEST_ORAL', 'BEST_POSTER', 'PEOPLES_CHOICE')),
                submission_id INTEGER NOT NULL,
                winner_id INTEGER NOT NULL,
                ceremony_date DATE,
                FOREIGN KEY (submission_id) REFERENCES submissions(id) ON DELETE CASCADE,
                FOREIGN KEY (winner_id) REFERENCES users(id) ON DELETE CASCADE
            );
            """;

        // Seed Initial Users
        String seedUsers = """
            INSERT OR IGNORE INTO users (username, password, roles) VALUES
            ('ali', '123', 'STUDENT'),
            ('admin', 'admin123', 'ADMIN');
            ('coordinator', 'coordinator123', 'COORDINATOR'),
            ('evaluator', 'evaluator123', 'EVALUATOR'),
        """;

        String seedPosterBoards = """
            INSERT OR IGNORE INTO poster_boards (board_id, location) VALUES
            ('PB-001', 'Main Hall - Aisle 1'),
            ('PB-002', 'Main Hall - Aisle 1'),
            ('PB-003', 'Main Hall - Aisle 2'),
            ('PB-004', 'Main Hall - Aisle 2'),
            ('PB-005', 'Exhibition Area - North');
            """;

        try (var conn = connect();
             var stmt = conn.createStatement()) {
            // Create all tables
            stmt.execute(createUserTable);
            stmt.execute(createSubmissionsTable);
            stmt.execute(createSessionsTable);
            stmt.execute(createSessionAssignmentsTable);
            stmt.execute(createEvaluationsTable);
            stmt.execute(createPosterBoardsTable);
            stmt.execute(createAwardsTable);
            
            // Seed initial data
            stmt.execute(seedUsers);
            stmt.execute(seedPosterBoards);
            
            System.out.println("Database initialized with all required tables");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
