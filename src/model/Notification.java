package model;

public class Notification {
    private int id;
    private int userId;
    private int messageId;
    private int isRead;
    private String createdAt;

    public Notification() {
    }

    public Notification(int id, int userId, int messageId, int isRead, String createdAt) {
        this.id = id;
        this.userId = userId;
        this.messageId = messageId;
        this.isRead = isRead;
        this.createdAt = createdAt;
    }

    public Notification(int userId, int messageId) {
        this.userId = userId;
        this.messageId = messageId;
        this.isRead = 0;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }


    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }


    public int getIsRead() {
        return isRead;
    }

    public void setIsRead(int isRead) {
        this.isRead = isRead;
    }


    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Notification{id=" + id
                + ", userId=" + userId
                + ", messageId=" + messageId
                + ", isRead=" + isRead
                + ", createdAt='" + createdAt + '\''
                + '}';
    }
}