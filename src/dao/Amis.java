package dao;

import model.DemandeAmi;
import model.Utilisateur;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;


public class Amis {

    public int envoyerDemande(int senderId, int receiverId) throws SQLException {
        String checkSql = "SELECT id FROM friendships WHERE (sender_id = ? AND receiver_id = ?) OR (sender_id = ? AND receiver_id = ?)";
        try (PreparedStatement ps = Base.getConnexion().prepareStatement(checkSql)) {
            ps.setInt(1, senderId);
            ps.setInt(2, receiverId);
            ps.setInt(3, receiverId);
            ps.setInt(4, senderId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return -1;
        }

        String sql = "INSERT INTO friendships (sender_id, receiver_id, status) VALUES (?, ?, 'pending')";
        try (PreparedStatement ps = Base.getConnexion().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, senderId);
            ps.setInt(2, receiverId);
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) return keys.getInt(1);
        }
        return -1;
    }

    public boolean accepterDemande(int requestId, int receiverId) throws SQLException {
        String sql = "UPDATE friendships SET status = 'accepted' WHERE id = ? AND receiver_id = ? AND status = 'pending'";
        try (PreparedStatement ps = Base.getConnexion().prepareStatement(sql)) {
            ps.setInt(1, requestId);
            ps.setInt(2, receiverId);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean refuserDemande(int requestId, int receiverId) throws SQLException {
        String sql = "UPDATE friendships SET status = 'declined' WHERE id = ? AND receiver_id = ? AND status = 'pending'";
        try (PreparedStatement ps = Base.getConnexion().prepareStatement(sql)) {
            ps.setInt(1, requestId);
            ps.setInt(2, receiverId);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean sontAmis(int userId1, int userId2) throws SQLException {
        String sql = "SELECT 1 FROM friendships WHERE ((sender_id = ? AND receiver_id = ?) OR (sender_id = ? AND receiver_id = ?)) AND status = 'accepted'";
        try (PreparedStatement ps = Base.getConnexion().prepareStatement(sql)) {
            ps.setInt(1, userId1);
            ps.setInt(2, userId2);
            ps.setInt(3, userId2);
            ps.setInt(4, userId1);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        }
    }

    public List<DemandeAmi> getDemandes(int receiverId) throws SQLException {
        List<DemandeAmi> requests = new ArrayList<>();
        String sql = "SELECT f.id, u.username AS sender_username FROM friendships f JOIN users u ON u.id = f.sender_id WHERE f.receiver_id = ? AND f.status = 'pending'";
        try (PreparedStatement ps = Base.getConnexion().prepareStatement(sql)) {
            ps.setInt(1, receiverId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                requests.add(new DemandeAmi(rs.getInt("id"), rs.getString("sender_username"), null, "pending"));
            }
        }
        return requests;
    }

    public List<Utilisateur> getAmis(int userId) throws SQLException {
        List<Utilisateur> friends = new ArrayList<>();
        String sql = "SELECT u.id, u.username, u.status FROM users u JOIN friendships f ON (f.sender_id = u.id OR f.receiver_id = u.id) WHERE (f.sender_id = ? OR f.receiver_id = ?) AND f.status = 'accepted' AND u.id != ?";
        try (PreparedStatement ps = Base.getConnexion().prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, userId);
            ps.setInt(3, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                friends.add(new Utilisateur(rs.getInt("id"), rs.getString("username"), rs.getString("status")));
            }
        }
        return friends;
    }

    public int getIdEnvoyeur(int requestId) throws SQLException {
        String sql = "SELECT sender_id FROM friendships WHERE id = ?";
        try (PreparedStatement ps = Base.getConnexion().prepareStatement(sql)) {
            ps.setInt(1, requestId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("sender_id");
        }
        return -1;
    }
}
