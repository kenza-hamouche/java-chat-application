package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;


public class Base {

    private static final String DB_URL = "jdbc:sqlite:chat.db";
    private static Connection connection;

    private Base() {}

    public static synchronized Connection getConnexion() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(DB_URL);
            connection.createStatement().execute("PRAGMA foreign_keys = ON");
        }
        return connection;
    }

    public static void initBase() throws SQLException {
        Connection conn = getConnexion();
        Statement stmt = conn.createStatement();

        stmt.execute("""
            CREATE TABLE IF NOT EXISTS users (
                id       INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT    NOT NULL UNIQUE,
                password TEXT    NOT NULL,
                status   TEXT    NOT NULL DEFAULT 'offline'
            )
        """);

        stmt.execute("""
            CREATE TABLE IF NOT EXISTS friendships (
                id          INTEGER PRIMARY KEY AUTOINCREMENT,
                sender_id   INTEGER NOT NULL REFERENCES users(id),
                receiver_id INTEGER NOT NULL REFERENCES users(id),
                status      TEXT    NOT NULL DEFAULT 'pending',
                created_at  TEXT    NOT NULL DEFAULT (datetime('now')),
                UNIQUE(sender_id, receiver_id)
            )
        """);

        stmt.execute("""
            CREATE TABLE IF NOT EXISTS groups (
                id         INTEGER PRIMARY KEY AUTOINCREMENT,
                name       TEXT    NOT NULL,
                created_by INTEGER NOT NULL REFERENCES users(id),
                created_at TEXT    NOT NULL DEFAULT (datetime('now'))
            )
        """);

        stmt.execute("""
            CREATE TABLE IF NOT EXISTS messages (
                id          INTEGER PRIMARY KEY AUTOINCREMENT,
                sender_id   INTEGER NOT NULL REFERENCES users(id),
                receiver_id INTEGER REFERENCES users(id),
                group_id    INTEGER REFERENCES groups(id),
                content     TEXT    NOT NULL,
                sent_at     TEXT    NOT NULL DEFAULT (datetime('now'))
            )
        """);

        stmt.execute("""
            CREATE TABLE IF NOT EXISTS group_members (
                group_id INTEGER NOT NULL REFERENCES groups(id),
                user_id  INTEGER NOT NULL REFERENCES users(id),
                PRIMARY KEY (group_id, user_id)
            )
        """);

        stmt.execute("""
            CREATE TABLE IF NOT EXISTS notifications (
                id         INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id    INTEGER NOT NULL REFERENCES users(id),
                type       TEXT    NOT NULL,
                content    TEXT    NOT NULL,
                is_read    INTEGER NOT NULL DEFAULT 0,
                created_at TEXT    NOT NULL DEFAULT (datetime('now'))
            )
        """);

        stmt.execute("CREATE INDEX IF NOT EXISTS idx_friendships_receiver ON friendships(receiver_id, status)");
        stmt.execute("CREATE INDEX IF NOT EXISTS idx_messages_receiver ON messages(receiver_id, sent_at)");
        stmt.execute("CREATE INDEX IF NOT EXISTS idx_notif_user ON notifications(user_id, is_read)");

        stmt.close();
        System.out.println("[DB] Base de données initialisée.");
    }
}
