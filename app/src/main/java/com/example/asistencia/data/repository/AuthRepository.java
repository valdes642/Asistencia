package com.example.asistencia.data.repository;

import android.os.Handler;
import android.os.Looper;

import com.example.asistencia.data.model.LoginRequest;
import com.example.asistencia.data.model.LoginResponse;

public class AuthRepository {

    public interface LoginCallback {
        void onSuccess(LoginResponse response);
        void onError(String errorMessage);
    }

    public void login(String username, String password, LoginCallback callback) {
        LoginRequest request = new LoginRequest(username, password);

        // Simulación de autenticación (API AP-03: Validación de credenciales y retorno de rol)
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(() -> {
            if (request.getUsername().equalsIgnoreCase("admin") && request.getPassword().equals("060402")) {
                LoginResponse response = new LoginResponse(
                        "mock_token_admin_123",
                        "Autenticación exitosa (Administrador)",
                        true,
                        "admin",
                        "12.345.678-9",
                        "Administrador General"
                );
                callback.onSuccess(response);
            } else if (request.getUsername().equalsIgnoreCase("victor") && request.getPassword().equals("060402")) {
                LoginResponse response = new LoginResponse(
                        "mock_token_worker_456",
                        "Autenticación exitosa (Trabajador)",
                        true,
                        "worker",
                        "18.987.654-3",
                        "Victor Vergara"
                );
                callback.onSuccess(response);
            } else if (request.getUsername().isEmpty() || request.getPassword().isEmpty()) {
                callback.onError("Por favor complete todos los campos");
            } else {
                callback.onError("Usuario o contraseña incorrectos");
            }
        }, 1000);
    }
}
