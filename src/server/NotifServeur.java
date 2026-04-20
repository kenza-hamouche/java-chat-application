package server;

import dao.Base;

import java.io.PrintWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;


public class NotifServeur {

    private static final Map<Integer, PrintWriter> connectedClients = new ConcurrentHashMap<>();

    public static void ajouterClient(int userId, PrintWriter writer) {
        connectedClients.put(userId, writer);
    }

    public static void retirerClient(int userId) {
        connectedClients.remove(userId);
    }

    public static void envoyerNotif(int targetUserId, String type, String content) {
        PrintWriter writer = connectedClients.get(targetUserId);
        if (writer != null) {
            writer.println("NOTIFICATION|" + type + "|" + content);
        } else {
            stockerNotif(targetUserId, type, content);
        }
    }

    public static boolean envoyerDirect(int targetUserId, String message) {
        PrintWriter writer = connectedClients.get(targetUserId);
        if (writer != null) {
            writer.println(message);
            return true;
        }
        return false;
    }

    public static void envoyerAUser(int userId, Consumer<PrintWriter> sender) {
        PrintWriter writer = connectedClients.get(userId);
        if (writer != null) {
            sender.accept(writer);
        }
    }

    public static void livrerNotifAttente(int userId, PrintWriter writer) {
        String sql = "SELECT id, type, content FROM notifications WHERE user_id = ? AND is_read = 0 ORDER BY created_at ASC";
        try (PreparedStatement ps = Base.getConnexion().prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                writer.println("NOTIFICATION|" + rs.getString("type") + "|" + rs.getString("content"));
            }
            String markSql = "UPDATE notifications SET is_read = 1 WHERE user_id = ? AND is_read = 0";
            try (PreparedStatement ps2 = Base.getConnexion().prepareStatement(markSql)) {
                ps2.setInt(1, userId);
                ps2.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println("[NotifDispatcher] Erreur SQL : " + e.getMessage());
        }
    }

    private static void stockerNotif(int userId, String type, String content) {
        String sql = "INSERT INTO notifications (user_id, type, content) VALUES (?, ?, ?)";
        try (PreparedStatement ps = Base.getConnexion().prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, type);
            ps.setString(3, content);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[NotifDispatcher] Erreur stockage : " + e.getMessage());
        }
    }
}
