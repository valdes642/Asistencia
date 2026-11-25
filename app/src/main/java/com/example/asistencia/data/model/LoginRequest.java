package com.example.asistencia.data.model;

import com.google.gson.annotations.SerializedName;

public class LoginRequest {
    @SerializedName(value = "username", alternate = {"email", "user"})
    private final String username;

    @SerializedName("password")
    private final String password;

    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
