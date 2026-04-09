package model;

public class Message {
    private int id;
    private int senderId;
    private Integer receiverId;
    private Integer groupId;
    private String content;
    private String timestamp;

    public Message() {
    }

    public Message(int id, int senderId, Integer receiverId, Integer groupId, String content, String timestamp) {
        this.id = id;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.groupId = groupId;
        this.content = content;
        this.timestamp = timestamp;
    }

    public Message(int senderId, Integer receiverId, Integer groupId, String content) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.groupId = groupId;
        this.content = content;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }


    public Integer getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Integer receiverId) {
        this.receiverId = receiverId;
    }


    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }


    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "Message{id=" + id
                + ", senderId=" + senderId
                + ", receiverId=" + receiverId
                + ", groupId=" + groupId
                + ", content='" + content + '\''
                + ", timestamp='" + timestamp + '\''
                + '}';
    }
}