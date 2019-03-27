package com.howtographql.hackernews;

public class SigninPayload {
    private final String token;
    private final User user;

    public SigninPayload(String token, User user) {
        this.token = token;
        this.user = user;
    }

    public String getToken() {
        return this.token;
    }

    public User getUser() {
        return this.user;
    }
}
