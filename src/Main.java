import java.sql.Connection;

import database.DatabaseConnection;

public class Main {
public static void main(String[] args) {
    Connection c= DatabaseConnection.getConnection();
    DatabaseConnection.createTables();
}

}
