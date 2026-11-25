package com.example.asistencia.data.model;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {
    @SerializedName(value = "token", alternate = {"access_token"})
    private final String token;

    @SerializedName("message")
    private final String message;

    @SerializedName(value = "success", alternate = {"ok"})
    private final boolean success;

    @SerializedName(value = "role", alternate = {"perfil", "type", "rol"})
    private final String role; // "admin" or "worker"

    @SerializedName(value = "rut", alternate = {"run"})
    private final String rut;

    @SerializedName(value = "name", alternate = {"nombre"})
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
