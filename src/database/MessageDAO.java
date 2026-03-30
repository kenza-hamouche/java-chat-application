package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MessageDAO {

    //Ajouter un message
public static void addMessage(int senderId, int receiverId, String content) {
        String sql = "INSERT INTO messages (sender_id, receiver_id, content, timestamp) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, senderId);
            pstmt.setInt(2, receiverId);
            pstmt.setString(3, content);
           

            pstmt.executeUpdate();

            System.out.println("Message ajouté !");

        } catch (SQLException e) {
            System.out.println("Erreur lors de l'ajout du message !");
            e.printStackTrace();
        }
    }

     //Récupérer messages entre 2 utilisateurs
    public static List<String> getMessagesBetweenUsers(int user1, int user2) {
        List<String> messages = new ArrayList<>();

        String sql = "SELECT * FROM messages WHERE "
                   + "(sender_id = ? AND receiver_id = ?) "
                   + "OR (sender_id = ? AND receiver_id = ?) "
                   + "ORDER BY timestamp";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, user1);
            pstmt.setInt(2, user2);
            pstmt.setInt(3, user2);
            pstmt.setInt(4, user1);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String msg = rs.getInt("sender_id") + " : " + rs.getString("content");
                messages.add(msg);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return messages;
    }

    //Récupérer tous les messages d’un utilisateur
    public static List<String> getMessagesByUser(int userId) {
        List<String> messages = new ArrayList<>();

        String sql = "SELECT * FROM messages WHERE sender_id = ? OR receiver_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setInt(2, userId);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                messages.add(rs.getString("content"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return messages;
    }

     public static void deleteMessage(int id) {
    String sql = "DELETE FROM messages WHERE id = ?";

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {

        pstmt.setInt(1, id);
        pstmt.executeUpdate();

        System.out.println("Message supprimé !");

    } catch (SQLException e) {
        e.printStackTrace();
    }
}
}