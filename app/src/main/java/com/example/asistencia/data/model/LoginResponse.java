package com.example.asistencia.data.model;

public class LoginResponse {
    private final String token;
    private final String message;
    private final boolean success;
    private final String role; // "admin" or "worker"
    private final String rut;
    private final String name;

    public LoginResponse(String token, String message, boolean success, String role, String rut, String name) {
        this.token = token;
        this.message = message;
        this.success = success;
        this.role = role;
        this.rut = rut;
        this.name = name;
    }

    public String getToken() { return token; }
    public String getMessage() { return message; }
    public boolean isSuccess() { return success; }
    public String getRole() { return role; }
    public String getRut() { return rut; }
    public String getName() { return name; }
}
