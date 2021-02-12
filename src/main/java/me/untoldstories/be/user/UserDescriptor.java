package me.untoldstories.be.user;

public class UserDescriptor {

    private final long userID;
    private final String userEmail;
    private final String userName;

    public UserDescriptor(long userID, String userEmail, String userName) {
        this.userID = userID;
        this.userEmail = userEmail;
        this.userName = userName;
    }

    public long getUserID() {
        return userID;
    }

    public String getUserEmail() {return userEmail;}

    public String getUserName() {
        return userName;
    }

}
