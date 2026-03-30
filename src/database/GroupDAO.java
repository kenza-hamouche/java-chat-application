package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GroupDAO {

//AJouter un groupe
public static void addGroup(String name) {
        String sql = "INSERT INTO groups (name) VALUES (?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, name);

            pstmt.executeUpdate();

            System.out.println("Groupe ajouté !");

        } catch (SQLException e) {
            System.out.println("Erreur lors de l'ajout du groupe !");
            e.printStackTrace();
        }
    }

    //Récupérer un groupe par son id
    public static String getGroupById(int id) {
        String sql = "SELECT name FROM groups WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getString("name");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    //Récupérer tous les groupes
    public static List<String> getAllGroups() {
        List<String> groups = new ArrayList<>();
        String sql = "SELECT name FROM groups";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                groups.add(rs.getString("name"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return groups;
    }

//Supprimer un groupe 
 public static void deleteGroup(int id) {
    String sql = "DELETE FROM groups WHERE id = ?";

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {

        pstmt.setInt(1, id);
        pstmt.executeUpdate();

        System.out.println("Groupe supprimé !");

    } catch (SQLException e) {
        e.printStackTrace();
    }
}
}