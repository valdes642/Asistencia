package com.example.asistencia.data.model;

public class LoginResponse {
    private final String token;
    private final String message;
    private final boolean success;

    public LoginResponse(String token, String message, boolean success) {
        this.token = token;
        this.message = message;
        this.success = success;
    }

    public String getToken() {
        return token;
    }

    public String getMessage() {
        return message;
    }

    public boolean isSuccess() {
        return success;
    }
}
