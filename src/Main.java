import java.sql.Connection;

import client.ChatClient;
import database.DatabaseConnection;
import database.GroupDAO;
import database.MessageDAO;
import database.UserDAO;
import server.ChatServer;

public class Main {
public static void main(String[] args) {
             // 🔹 Initialiser la base (facultatif mais recommandé)
        DatabaseConnection.createTables();

        // 🔹 Si tu passes "server" → lance serveur
        if (args.length > 0 && args[0].equals("server")) {

            System.out.println("=== SERVEUR ===");
            ChatServer server = new ChatServer();
            server.startServer();

        } else {
            // 🔹 Sinon → client
            System.out.println("=== CLIENT ===");
            ChatClient client = new ChatClient();
            client.startClient();
        }

   // Connection c= DatabaseConnection.getConnection();
   // DatabaseConnection.createTables();

    //UserDAO.addUser("sofia", "1234");
    //MessageDAO.addMessage(1, 2, "Salut !");
    //GroupDAO.addGroup("Groupe 1");

   // boolean login = UserDAO.checkUser("malak", "1234");
       // System.out.println("Login: " + login);

       // String user = UserDAO.getUserById(1);
       // System.out.println("User: " + user);

        //System.out.println(UserDAO.getAllUsers());

        //UserDAO.deleteUser(2);

        //GroupDAO.addGroup("Amis");
        //GroupDAO.addGroup("Travail");

        //System.out.println(GroupDAO.getGroupById(3));
        //System.out.println(GroupDAO.getAllGroups());
}

}
