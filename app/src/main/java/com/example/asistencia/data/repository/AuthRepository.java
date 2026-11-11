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

    // Aquí se inyectará o instanciará Retrofit en el futuro (ej. RetrofitClient.getApiService())
    // private final AuthApiService apiService;

    public AuthRepository() {
        // this.apiService = RetrofitClient.getClient().create(AuthApiService.class);
    }

    public void login(String username, String password, LoginCallback callback) {
        LoginRequest request = new LoginRequest(username, password);

        // TODO: Reemplazar con llamada real a la API usando Retrofit (ej. apiService.login(request).enqueue(...))
        // Simulación de petición de red asíncrona:
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(() -> {
            if (request.getUsername().equals("admin") && request.getPassword().equals("123456")) {
                LoginResponse response = new LoginResponse("mock_token_abc123", "Inicio de sesión exitoso", true);
                callback.onSuccess(response);
            } else if (request.getUsername().isEmpty() || request.getPassword().isEmpty()) {
                callback.onError("Por favor complete todos los campos");
            } else {
                // Simulación de respuesta de error de API (ej. credenciales inválidas)
                callback.onError("Usuario o contraseña incorrectos");
            }
        }, 1200); // Retardo simulado de red (1.2 segundos)
    }
}
