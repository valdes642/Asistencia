package com.example.asistencia.data.model;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {
    @SerializedName(value = "token", alternate = {"access_token", "api_token"})
    private String token;

    @SerializedName(value = "message", alternate = {"msg", "status_message", "detail"})
    private String message;

    @SerializedName(value = "success", alternate = {"ok", "status"})
    private boolean success;

    @SerializedName(value = "role", alternate = {"perfil", "type", "rol"})
    private String role; // "admin" or "worker"

    @SerializedName(value = "rut", alternate = {"run", "dni"})
    private String rut;

    @SerializedName(value = "name", alternate = {"nombre", "user_name"})
    private String name;

    @SerializedName(value = "data", alternate = {"user", "usuario"})
    private UserData data;

    public static class UserData {
        @SerializedName(value = "token", alternate = {"access_token"})
        private String token;

        @SerializedName(value = "role", alternate = {"perfil", "type", "rol"})
        private String role;

        @SerializedName(value = "rut", alternate = {"run"})
        private String rut;

        @SerializedName(value = "name", alternate = {"nombre"})
        private String name;

        public String getToken() { return token; }
        public String getRole() { return role; }
        public String getRut() { return rut; }
        public String getName() { return name; }
    }

    public LoginResponse(String token, String message, boolean success, String role, String rut, String name) {
        this.token = token;
        this.message = message;
        this.success = success;
        this.role = role;
        this.rut = rut;
        this.name = name;
    }

    public String getToken() {
        if (token != null && !token.isEmpty()) return token;
        return data != null ? data.getToken() : null;
    }

    public String getMessage() {
        if (message != null && !message.isEmpty()) return message;
        return success ? "Autenticación exitosa" : "Error en autenticación";
    }

    public boolean isSuccess() {
        return success || (token != null && !token.isEmpty()) || (data != null && data.getToken() != null);
    }

    public String getRole() {
        if (role != null && !role.isEmpty()) return role;
        return data != null && data.getRole() != null ? data.getRole() : "worker";
    }

    public String getRut() {
        if (rut != null && !rut.isEmpty()) return rut;
        return data != null && data.getRut() != null ? data.getRut() : "";
    }

    public String getName() {
        if (name != null && !name.isEmpty()) return name;
        return data != null && data.getName() != null ? data.getName() : "";
    }
}
