package database;

import model.GroupMember;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GroupMemberDAO {

    public boolean addUserToGroup(GroupMember groupMember) {
        String sql = "INSERT INTO group_members(group_id, user_id) VALUES(?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, groupMember.getGroupId());
            pstmt.setInt(2, groupMember.getUserId());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Erreur lors de l'ajout de l'utilisateur au groupe !");
            e.printStackTrace();
            return false;
        }
    }

    public boolean removeUserFromGroup(int groupId, int userId) {
        String sql = "DELETE FROM group_members WHERE group_id = ? AND user_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, groupId);
            pstmt.setInt(2, userId);

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Erreur lors de la suppression du membre du groupe !");
            e.printStackTrace();
            return false;
        }
    }

    public List<GroupMember> getMembersByGroupId(int groupId) {
        String sql = "SELECT * FROM group_members WHERE group_id = ?";
        List<GroupMember> members = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, groupId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                members.add(new GroupMember(
                        rs.getInt("group_id"),
                        rs.getInt("user_id")
                ));
            }

        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des membres du groupe !");
            e.printStackTrace();
        }

        return members;
    }

    public List<GroupMember> getGroupsByUserId(int userId) {
        String sql = "SELECT * FROM group_members WHERE user_id = ?";
        List<GroupMember> memberships = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                memberships.add(new GroupMember(
                        rs.getInt("group_id"),
                        rs.getInt("user_id")
                ));
            }

        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des groupes de l'utilisateur !");
            e.printStackTrace();
        }

        return memberships;
    }

    public boolean isUserInGroup(int groupId, int userId) {
        String sql = "SELECT * FROM group_members WHERE group_id = ? AND user_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, groupId);
            pstmt.setInt(2, userId);

            ResultSet rs = pstmt.executeQuery();
            return rs.next();

        } catch (SQLException e) {
            System.out.println("Erreur lors de la vérification de l'appartenance au groupe !");
            e.printStackTrace();
            return false;
        }
    }
}