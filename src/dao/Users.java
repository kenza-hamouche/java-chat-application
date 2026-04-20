package dao;

import model.Utilisateur;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;


public class Users {

    public boolean inscrire(String username, String password) throws SQLException {
        String sql = "INSERT INTO users (username, password) VALUES (?, ?)";
        try (PreparedStatement ps = Base.getConnexion().prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            if (e.getErrorCode() == 19) return false;
            throw e;
        }
    }

    public Utilisateur connecter(String username, String password) throws SQLException {
        String sql = "SELECT id, username, status FROM users WHERE username = ? AND password = ?";
        try (PreparedStatement ps = Base.getConnexion().prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Utilisateur(rs.getInt("id"), rs.getString("username"), rs.getString("status"));
            }
        }
        return null;
    }

    public void setStatut(int userId, String status) throws SQLException {
        String sql = "UPDATE users SET status = ? WHERE id = ?";
        try (PreparedStatement ps = Base.getConnexion().prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, userId);
            ps.executeUpdate();
        }
    }

    public int getIdParNom(String username) throws SQLException {
        String sql = "SELECT id FROM users WHERE username = ?";
        try (PreparedStatement ps = Base.getConnexion().prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("id");
        }
        return -1;
    }

    public String getNomParId(int userId) throws SQLException {
        String sql = "SELECT username FROM users WHERE id = ?";
        try (PreparedStatement ps = Base.getConnexion().prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getString("username");
        }
        return null;
    }

    public List<Utilisateur> getTous() throws SQLException {
        List<Utilisateur> users = new ArrayList<>();
        String sql = "SELECT id, username, status FROM users";
        try (Statement stmt = Base.getConnexion().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                users.add(new Utilisateur(rs.getInt("id"), rs.getString("username"), rs.getString("status")));
            }
        }
        return users;
    }
}
