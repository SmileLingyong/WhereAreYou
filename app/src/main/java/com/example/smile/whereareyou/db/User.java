package com.example.smile.whereareyou.db;

/**
 * Created by Ryan on 2017/10/14.
 */

public class User {
    private String userId;
    private String userPwd;
    private String userName;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserPwd() {
        return userPwd;
    }

    public void setUserPwd(String userPwd) {
        this.userPwd = userPwd;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public User(String uid, String upwd, String uname) {
        this.userId = uid;
        this.userPwd = upwd;
        this.userName = uname;
    }

    public User(String uid, String upwd) {
        this.userId = uid;
        this.userPwd = upwd;
    }
}
