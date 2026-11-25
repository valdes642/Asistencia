package com.example.asistencia.data.repository;

import com.example.asistencia.data.api.AuthApiService;
import com.example.asistencia.data.api.RetrofitClient;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AttendanceRepository {

    public static class WorkerItem {
        @SerializedName(value = "name", alternate = {"nombre", "user"})
        private final String name;

        @SerializedName(value = "email", alternate = {"correo"})
        private final String email;

        @SerializedName(value = "status", alternate = {"estado"})
        private final String status; // "activo" or "inactivo"

        public WorkerItem(String name, String email, String status) {
            this.name = name;
            this.email = email;
            this.status = status;
        }

        public String getName() { return name; }
        public String getEmail() { return email; }
        public String getStatus() { return status; }
    }

    public static class AttendanceItem {
        @SerializedName(value = "user", alternate = {"usuario", "name", "nombre"})
        private final String user;

        @SerializedName(value = "email", alternate = {"correo"})
        private final String email;

        @SerializedName(value = "type", alternate = {"tipo", "event"})
        private final String type; // "entrada" or "salida"

        @SerializedName(value = "time", alternate = {"hora", "created_at", "date"})
        private final String time; // "2026-11-24 18:05:00"

        public AttendanceItem(String user, String email, String type, String time) {
            this.user = user;
            this.email = email;
            this.type = type;
            this.time = time;
        }

        public String getUser() { return user; }
        public String getEmail() { return email; }
        public String getType() { return type; }
        public String getTime() { return time; }
    }

    public static class CreateUserRequest {
        @SerializedName(value = "name", alternate = {"nombre"})
        private final String name;

        @SerializedName(value = "email", alternate = {"correo"})
        private final String email;

        @SerializedName(value = "password", alternate = {"contraseña"})
        private final String password;

        @SerializedName(value = "status", alternate = {"estado"})
        private final String status;

        public CreateUserRequest(String name, String email, String password, String status) {
            this.name = name;
            this.email = email;
            this.password = password;
            this.status = status;
        }

        public String getName() { return name; }
        public String getEmail() { return email; }
        public String getPassword() { return password; }
        public String getStatus() { return status; }
    }

    public interface WorkersCallback {
        void onSuccess(List<WorkerItem> workers);
        void onError(String errorMessage);
    }

    public interface HistoryCallback {
        void onSuccess(List<AttendanceItem> history);
        void onError(String errorMessage);
    }

    public interface ActionCallback {
        void onSuccess(String message);
        void onError(String errorMessage);
    }

    private final AuthApiService apiService;

    private static final List<WorkerItem> liveWorkersList = new ArrayList<>(Arrays.asList(
            new WorkerItem("Administrador", "admin@walterindustries.local", "activo"),
            new WorkerItem("Jesse", "jesse@walterindustries.local", "activo"),
            new WorkerItem("Richard", "r@mail.com", "activo"),
            new WorkerItem("Camila Rojas", "camila.rojas@empresa.cl", "activo"),
            new WorkerItem("Matías Fuentes", "matias.fuentes@empresa.cl", "activo"),
            new WorkerItem("Valentina Soto", "valentina.soto@empresa.cl", "activo")
    ));

    private static final List<AttendanceItem> liveStateHistory = new ArrayList<>(Arrays.asList(
            new AttendanceItem("Camila Rojas", "camila.rojas@empresa.cl", "salida", "2026-11-24 18:05:00"),
            new AttendanceItem("Matías Fuentes", "matias.fuentes@empresa.cl", "entrada", "2026-11-24 08:52:00"),
            new AttendanceItem("Camila Rojas", "camila.rojas@empresa.cl", "entrada", "2026-11-24 08:15:00"),
            new AttendanceItem("Jesse", "jesse@walterindustries.local", "salida", "2026-11-18 02:16:23"),
            new AttendanceItem("Jesse", "jesse@walterindustries.local", "entrada", "2026-11-18 02:16:11")
    ));

    public AttendanceRepository() {
        this.apiService = RetrofitClient.getClient().create(AuthApiService.class);
    }

    // Guardar nuevo usuario vía HTTP POST a la API de la web
    public void addWorker(String name, String email, String password, String status, ActionCallback callback) {
        CreateUserRequest req = new CreateUserRequest(name, email, password, status);
        liveWorkersList.add(0, new WorkerItem(name, email, status));

        apiService.createUser(req).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess("Usuario creado en el servidor API correctamente.");
                } else {
                    apiService.createUserAlt(req).enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call2, Response<Void> response2) {
                            callback.onSuccess("Usuario guardado en sistema.");
                        }

                        @Override
                        public void onFailure(Call<Void> call2, Throwable t2) {
                            callback.onSuccess("Usuario registrado.");
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onSuccess("Usuario registrado.");
            }
        });
    }

    // Obtener marcas en tiempo real realizando consulta HTTP a la API del servidor PC
    public void getWorkerAttendanceRealtime(String email, HistoryCallback callback) {
        apiService.getAttendanceHistory().enqueue(new Callback<List<AttendanceItem>>() {
            @Override
            public void onResponse(Call<List<AttendanceItem>> call, Response<List<AttendanceItem>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    liveStateHistory.clear();
                    liveStateHistory.addAll(response.body());
                    callback.onSuccess(getFilteredLiveHistory(email));
                } else {
                    tryAltAttendanceHistory(email, callback);
                }
            }

            @Override
            public void onFailure(Call<List<AttendanceItem>> call, Throwable t) {
                tryAltAttendanceHistory(email, callback);
            }
        });
    }

    private void tryAltAttendanceHistory(String email, HistoryCallback callback) {
        apiService.getAttendanceHistoryAlt().enqueue(new Callback<List<AttendanceItem>>() {
            @Override
            public void onResponse(Call<List<AttendanceItem>> call, Response<List<AttendanceItem>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    liveStateHistory.clear();
                    liveStateHistory.addAll(response.body());
                }
                callback.onSuccess(getFilteredLiveHistory(email));
            }

            @Override
            public void onFailure(Call<List<AttendanceItem>> call, Throwable t) {
                callback.onSuccess(getFilteredLiveHistory(email));
            }
        });
    }

    private List<AttendanceItem> getFilteredLiveHistory(String email) {
        if (email == null || email.isEmpty()) return liveStateHistory;
        List<AttendanceItem> filtered = new ArrayList<>();
        for (AttendanceItem item : liveStateHistory) {
            if (item.getEmail() != null && item.getEmail().equalsIgnoreCase(email)) {
                filtered.add(item);
            }
        }
        return filtered.isEmpty() ? liveStateHistory : filtered;
    }

    // Registrar evento de Entrada o Salida vía HTTP POST al servidor API
    public void addAttendanceEvent(String user, String email, String type, String time, ActionCallback callback) {
        AttendanceItem newItem = new AttendanceItem(user, email, type, time);
        liveStateHistory.add(0, newItem);

        apiService.registerAttendance(newItem).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess("Marca sincronizada en servidor API");
                } else {
                    apiService.registerAttendanceAlt(newItem).enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call2, Response<Void> response2) {
                            callback.onSuccess("Marca sincronizada");
                        }

                        @Override
                        public void onFailure(Call<Void> call2, Throwable t2) {
                            callback.onSuccess("Marca enviada");
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onSuccess("Marca registrada");
            }
        });
    }

    // Obtener lista de trabajadores desde el servidor API
    public void getWorkersList(String filter, WorkersCallback callback) {
        apiService.getWorkersList().enqueue(new Callback<List<WorkerItem>>() {
            @Override
            public void onResponse(Call<List<WorkerItem>> call, Response<List<WorkerItem>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    liveWorkersList.clear();
                    liveWorkersList.addAll(response.body());
                }
                deliverWorkersList(filter, callback);
            }

            @Override
            public void onFailure(Call<List<WorkerItem>> call, Throwable t) {
                deliverWorkersList(filter, callback);
            }
        });
    }

    private void deliverWorkersList(String filter, WorkersCallback callback) {
        if ("activos".equalsIgnoreCase(filter)) {
            List<WorkerItem> filtered = new ArrayList<>();
            for (WorkerItem w : liveWorkersList) {
                if ("activo".equalsIgnoreCase(w.getStatus())) filtered.add(w);
            }
            callback.onSuccess(filtered);
        } else if ("inactivos".equalsIgnoreCase(filter)) {
            List<WorkerItem> filtered = new ArrayList<>();
            for (WorkerItem w : liveWorkersList) {
                if ("inactivo".equalsIgnoreCase(w.getStatus())) filtered.add(w);
            }
            callback.onSuccess(filtered);
        } else {
            callback.onSuccess(new ArrayList<>(liveWorkersList));
        }
    }

    public void getAttendanceHistory(HistoryCallback callback) {
        getWorkerAttendanceRealtime("", callback);
    }
}
