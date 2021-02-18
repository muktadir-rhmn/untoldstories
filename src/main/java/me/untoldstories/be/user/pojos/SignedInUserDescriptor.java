package me.untoldstories.be.user.pojos;

public class SignedInUserDescriptor {
    private final long userID;

    public SignedInUserDescriptor(long userID) {
        this.userID = userID;
    }

    public long getUserID() {
        return userID;
    }

}
