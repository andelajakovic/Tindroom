package com.example.tindroom.data.model;

public class Token {
    String token, user;

    public Token(String token, String userId) {
        this.token = token;
        this.user = userId;
    }

    public Token(){

    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUserId() {
        return user;
    }

    public void setUserId(String userId) {
        this.user = userId;
    }
}
