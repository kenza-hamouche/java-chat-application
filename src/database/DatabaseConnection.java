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
            System.out.println("Connexion à la base réussie !");
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
            + "username TEXT NOT NULL, "
            + "password TEXT NOT NULL"
            + ");";

    String messagesTable = "CREATE TABLE IF NOT EXISTS messages ("
            + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
            + "sender_id INTEGER, "
            + "receiver_id INTEGER, "
            + "content TEXT, "
            + "timestamp TEXT"
            + ");";

    String groupsTable = "CREATE TABLE IF NOT EXISTS groups ("
            + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
            + "name TEXT"
            + ");";

    try (Connection conn = getConnection();
         Statement stmt = conn.createStatement()) {

        stmt.execute(usersTable);
        stmt.execute(messagesTable);
        stmt.execute(groupsTable);

        System.out.println("Tables créées avec succès !");

    } catch (SQLException e) {
        System.out.println("Erreur lors de la création des tables !");
        e.printStackTrace();
    }
}
}
