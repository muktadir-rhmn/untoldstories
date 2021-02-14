package me.untoldstories.be.user.entities;

public class UserEntity {
    public Long id;
    public String userName;
    public String password;
    public Long cTime;
    public Long mTime;

    public UserEntity(Long userID, String userName, String password, Long cTime, Long mTime) {
        this.id = userID;
        this.userName = userName;
        this.password = password;
        this.cTime = cTime;
        this.mTime = mTime;
    }
}
