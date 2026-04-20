package dao;

import model.Msg;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class Messages {

    public void sauverPrive(int senderId, int receiverId, String content) throws SQLException {
        String sql = "INSERT INTO messages (sender_id, receiver_id, content) VALUES (?, ?, ?)";
        try (PreparedStatement ps = Base.getConnexion().prepareStatement(sql)) {
            ps.setInt(1, senderId);
            ps.setInt(2, receiverId);
            ps.setString(3, content);
            ps.executeUpdate();
        }
    }

    public void sauverGroupe(int senderId, int groupId, String content) throws SQLException {
        String sql = "INSERT INTO messages (sender_id, group_id, content) VALUES (?, ?, ?)";
        try (PreparedStatement ps = Base.getConnexion().prepareStatement(sql)) {
            ps.setInt(1, senderId);
            ps.setInt(2, groupId);
            ps.setString(3, content);
            ps.executeUpdate();
        }
    }

    public List<Msg> getHistPrive(int userId1, int userId2) throws SQLException {
        List<Msg> messages = new ArrayList<>();
        String sql = "SELECT u.username AS sender, m.content, m.sent_at FROM messages m JOIN users u ON u.id = m.sender_id WHERE (m.sender_id = ? AND m.receiver_id = ?) OR (m.sender_id = ? AND m.receiver_id = ?) ORDER BY m.sent_at ASC LIMIT 100";
        try (PreparedStatement ps = Base.getConnexion().prepareStatement(sql)) {
            ps.setInt(1, userId1);
            ps.setInt(2, userId2);
            ps.setInt(3, userId2);
            ps.setInt(4, userId1);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                messages.add(new Msg(rs.getString("sender"), (String) null, rs.getString("content"), rs.getString("sent_at")));
            }
        }
        return messages;
    }

    public List<Msg> getHistGroupe(int groupId) throws SQLException {
        List<Msg> messages = new ArrayList<>();
        String sql = "SELECT u.username AS sender, m.content, m.sent_at FROM messages m JOIN users u ON u.id = m.sender_id WHERE m.group_id = ? ORDER BY m.sent_at ASC LIMIT 100";
        try (PreparedStatement ps = Base.getConnexion().prepareStatement(sql)) {
            ps.setInt(1, groupId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                messages.add(new Msg(rs.getString("sender"), groupId, rs.getString("content"), rs.getString("sent_at")));
            }
        }
        return messages;
    }
}
