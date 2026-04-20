package model;


public class Utilisateur {
    private int id;
    private String username;
    private String status; 

    public Utilisateur(int id, String username, String status) {
        this.id = id;
        this.username = username;
        this.status = status;
    }

    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getStatus() { return status; }
    public void setStatut(String status) { this.status = status; }

    @Override
    public String toString() {
        return username + " (" + status + ")";
    }
}
