package model;

public class Groupe {
    private int id;
    private String name;
    private String createdBy;

    public Groupe(int id, String name, String createdBy) {
        this.id = id;
        this.name = name;
        this.createdBy = createdBy;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getCreatedBy() { return createdBy; }

    @Override
    public String toString() {
        return name + " (créé par " + createdBy + ")";
    }
}
