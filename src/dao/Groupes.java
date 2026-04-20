package dao;

import model.Groupe;
import model.Utilisateur;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;


public class Groupes {

    public int creerGroupe(String name, int createdById) throws SQLException {
        String sql = "INSERT INTO groups (name, created_by) VALUES (?, ?)";
        try (PreparedStatement ps = Base.getConnexion().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, name);
            ps.setInt(2, createdById);
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) {
                int groupId = keys.getInt(1);
                ajouterMembre(groupId, createdById);
                return groupId;
            }
        }
        return -1;
    }

    public boolean ajouterMembre(int groupId, int userId) throws SQLException {
        String sql = "INSERT OR IGNORE INTO group_members (group_id, user_id) VALUES (?, ?)";
        try (PreparedStatement ps = Base.getConnexion().prepareStatement(sql)) {
            ps.setInt(1, groupId);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean estMembre(int groupId, int userId) throws SQLException {
        String sql = "SELECT 1 FROM group_members WHERE group_id = ? AND user_id = ?";
        try (PreparedStatement ps = Base.getConnexion().prepareStatement(sql)) {
            ps.setInt(1, groupId);
            ps.setInt(2, userId);
            return ps.executeQuery().next();
        }
    }

    public List<Groupe> getGroupesUser(int userId) throws SQLException {
        List<Groupe> groups = new ArrayList<>();
        String sql = "SELECT g.id, g.name, u.username AS creator FROM groups g JOIN group_members gm ON gm.group_id = g.id JOIN users u ON u.id = g.created_by WHERE gm.user_id = ?";
        try (PreparedStatement ps = Base.getConnexion().prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                groups.add(new Groupe(rs.getInt("id"), rs.getString("name"), rs.getString("creator")));
            }
        }
        return groups;
    }

    public List<Utilisateur> getMembres(int groupId) throws SQLException {
        List<Utilisateur> members = new ArrayList<>();
        String sql = "SELECT u.id, u.username, u.status FROM users u JOIN group_members gm ON gm.user_id = u.id WHERE gm.group_id = ?";
        try (PreparedStatement ps = Base.getConnexion().prepareStatement(sql)) {
            ps.setInt(1, groupId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                members.add(new Utilisateur(rs.getInt("id"), rs.getString("username"), rs.getString("status")));
            }
        }
        return members;
    }
}
