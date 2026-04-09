package model;

public class GroupMember {
    private int groupId;
    private int userId;

    public GroupMember() {
    }

    public GroupMember(int groupId, int userId) {
        this.groupId = groupId;
        this.userId = userId;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "GroupMember{groupId=" + groupId + ", userId=" + userId + "}";
    }
}