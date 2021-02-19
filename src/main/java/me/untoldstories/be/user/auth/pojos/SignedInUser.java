package me.untoldstories.be.user.auth.pojos;

public class SignedInUser {
    private final long userID;

    public SignedInUser(long userID) {
        this.userID = userID;
    }

    public long getUserID() {
        return userID;
    }

}
