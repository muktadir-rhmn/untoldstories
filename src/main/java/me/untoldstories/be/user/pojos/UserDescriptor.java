package me.untoldstories.be.user.pojos;

public class UserDescriptor {
    private final long userID;
    private final String userName;

    public UserDescriptor(long userID, String userName) {
        this.userID = userID;
        this.userName = userName;
    }

    public long getUserID() {
        return userID;
    }

    public String getUserName() {
        return userName;
    }

}
