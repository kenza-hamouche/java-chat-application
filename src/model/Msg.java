package model;


public class Msg {
    private int id;
    private String senderUsername;
    private String receiverUsername; 
    private int groupId;             
    private String content;
    private String sentAt;

    
    public Msg(String senderUsername, String receiverUsername,
                   String content, String sentAt) {
        this.senderUsername = senderUsername;
        this.receiverUsername = receiverUsername;
        this.groupId = 0;
        this.content = content;
        this.sentAt = sentAt;
    }

    
    public Msg(String senderUsername, int groupId,
                   String content, String sentAt) {
        this.senderUsername = senderUsername;
        this.groupId = groupId;
        this.content = content;
        this.sentAt = sentAt;
    }

    public String getSenderUsername() { return senderUsername; }
    public String getReceiverUsername() { return receiverUsername; }
    public int getGroupId() { return groupId; }
    public String getContent() { return content; }
    public String getSentAt() { return sentAt; }
}
