package database;

import model.Notification;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class NotificationDAO {

    public boolean createNotification(Notification notification) {
        String sql = "INSERT INTO notifications(user_id, message_id, is_read) VALUES(?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, notification.getUserId());
            pstmt.setInt(2, notification.getMessageId());
            pstmt.setInt(3, notification.getIsRead());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Erreur lors de la création de la notification !");
            e.printStackTrace();
            return false;
        }
    }

    public List<Notification> getNotificationsByUserId(int userId) {
        String sql = "SELECT * FROM notifications WHERE user_id = ?";
        List<Notification> notifications = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                notifications.add(new Notification(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getInt("message_id"),
                        rs.getInt("is_read"),
                        rs.getString("created_at")
                ));
            }

        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des notifications !");
            e.printStackTrace();
        }

        return notifications;
    }

    public List<Notification> getUnreadNotifications(int userId) {
        String sql = "SELECT * FROM notifications WHERE user_id = ? AND is_read = 0";
        List<Notification> notifications = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                notifications.add(new Notification(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getInt("message_id"),
                        rs.getInt("is_read"),
                        rs.getString("created_at")
                ));
            }

        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des notifications non lues !");
            e.printStackTrace();
        }

        return notifications;
    }

    public boolean markAsRead(int notificationId) {
        String sql = "UPDATE notifications SET is_read = 1 WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, notificationId);

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Erreur lors de la mise à jour de la notification !");
            e.printStackTrace();
            return false;
        }
    }

    public boolean markAllAsReadByUser(int userId) {
        String sql = "UPDATE notifications SET is_read = 1 WHERE user_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Erreur lors de la mise à jour des notifications utilisateur !");
            e.printStackTrace();
            return false;
        }
    }
}