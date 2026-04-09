package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {
    private static final String URL = "jdbc:sqlite:chat_app.db";

    public static Connection getConnection() {
        try {
            Connection conn = DriverManager.getConnection(URL);
            return conn;
        } catch (SQLException e) {
            System.out.println("Erreur de connexion !");
            e.printStackTrace();
            return null;
        }
    }

    public static void createTables() {

        String usersTable = "CREATE TABLE IF NOT EXISTS users ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "username TEXT NOT NULL UNIQUE, "
                + "password TEXT NOT NULL"
                + ");";

        String groupsTable = "CREATE TABLE IF NOT EXISTS groups ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "name TEXT NOT NULL UNIQUE"
                + ");";

        String groupMembersTable = "CREATE TABLE IF NOT EXISTS group_members ("
                + "group_id INTEGER NOT NULL, "
                + "user_id INTEGER NOT NULL, "
                + "PRIMARY KEY (group_id, user_id), "
                + "FOREIGN KEY (group_id) REFERENCES groups(id), "
                + "FOREIGN KEY (user_id) REFERENCES users(id)"
                + ");";

        String messagesTable = "CREATE TABLE IF NOT EXISTS messages ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "sender_id INTEGER NOT NULL, "
                + "receiver_id INTEGER, "
                + "group_id INTEGER, "
                + "content TEXT NOT NULL, "
                + "timestamp DATETIME DEFAULT CURRENT_TIMESTAMP, "
                + "FOREIGN KEY (sender_id) REFERENCES users(id), "
                + "FOREIGN KEY (receiver_id) REFERENCES users(id), "
                + "FOREIGN KEY (group_id) REFERENCES groups(id)"
                + ");";

        String notificationsTable = "CREATE TABLE IF NOT EXISTS notifications ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "user_id INTEGER NOT NULL, "
                + "message_id INTEGER NOT NULL, "
                + "is_read INTEGER DEFAULT 0, "
                + "created_at DATETIME DEFAULT CURRENT_TIMESTAMP, "
                + "FOREIGN KEY (user_id) REFERENCES users(id), "
                + "FOREIGN KEY (message_id) REFERENCES messages(id)"
                + ");";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute(usersTable);
            stmt.execute(groupsTable);
            stmt.execute(groupMembersTable);
            stmt.execute(messagesTable);
            stmt.execute(notificationsTable);

            System.out.println("Toutes les tables ont été créées avec succès !");

        } catch (SQLException e) {
            System.out.println("Erreur lors de la création des tables !");
            e.printStackTrace();
        }
    }
}