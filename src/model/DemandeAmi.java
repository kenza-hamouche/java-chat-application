package model;


public class DemandeAmi {
    private int id;
    private String senderUsername;
    private String receiverUsername;
    private String status;

    public DemandeAmi(int id, String senderUsername,
                         String receiverUsername, String status) {
        this.id = id;
        this.senderUsername = senderUsername;
        this.receiverUsername = receiverUsername;
        this.status = status;
    }

    public int getId() { return id; }
    public String getSenderUsername() { return senderUsername; }
    public String getReceiverUsername() { return receiverUsername; }
    public String getStatus() { return status; }
}
