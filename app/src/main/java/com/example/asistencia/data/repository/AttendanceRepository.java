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

    public interface WorkersCallback {
        void onSuccess(List<WorkerItem> workers);
        void onError(String errorMessage);
    }

    public interface HistoryCallback {
        void onSuccess(List<AttendanceItem> history);
        void onError(String errorMessage);
    }

    private final AuthApiService apiService;
    private static final List<AttendanceItem> liveStateHistory = new ArrayList<>();

    static {
        // Inicializar estado inicial
        liveStateHistory.add(new AttendanceItem("Camila Rojas", "camila.rojas@empresa.cl", "salida", "2026-11-24 18:05:00"));
        liveStateHistory.add(new AttendanceItem("Matías Fuentes", "matias.fuentes@empresa.cl", "entrada", "2026-11-24 08:52:00"));
        liveStateHistory.add(new AttendanceItem("Camila Rojas", "camila.rojas@empresa.cl", "entrada", "2026-11-24 08:15:00"));
        liveStateHistory.add(new AttendanceItem("Jesse", "jesse@walterindustries.local", "salida", "2026-11-18 02:16:23"));
        liveStateHistory.add(new AttendanceItem("Jesse", "jesse@walterindustries.local", "entrada", "2026-11-18 02:16:11"));
    }

    public AttendanceRepository() {
        this.apiService = RetrofitClient.getClient().create(AuthApiService.class);
    }

    // Obtener marcas en tiempo real del trabajador para Pasos 6 y 9
    public void getWorkerAttendanceRealtime(String email, HistoryCallback callback) {
        apiService.getAttendanceHistoryByEmail(email).enqueue(new Callback<List<AttendanceItem>>() {
            @Override
            public void onResponse(Call<List<AttendanceItem>> call, Response<List<AttendanceItem>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onSuccess(getFilteredLiveHistory(email));
                }
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

    public void addAttendanceEvent(String user, String email, String type, String time) {
        liveStateHistory.add(0, new AttendanceItem(user, email, type, time));
    }

    public void getWorkersList(String filter, WorkersCallback callback) {
        List<WorkerItem> allWorkers = Arrays.asList(
                new WorkerItem("Administrador", "admin@walterindustries.local", "activo"),
                new WorkerItem("Jesse", "jesse@walterindustries.local", "activo"),
                new WorkerItem("Richard", "r@mail.com", "activo"),
                new WorkerItem("Camila Rojas", "camila.rojas@empresa.cl", "activo"),
                new WorkerItem("Matías Fuentes", "matias.fuentes@empresa.cl", "activo"),
                new WorkerItem("Valentina Soto", "valentina.soto@empresa.cl", "activo")
        );

        if ("activos".equalsIgnoreCase(filter)) {
            List<WorkerItem> filtered = new ArrayList<>();
            for (WorkerItem w : allWorkers) {
                if ("activo".equalsIgnoreCase(w.getStatus())) filtered.add(w);
            }
            callback.onSuccess(filtered);
        } else if ("inactivos".equalsIgnoreCase(filter)) {
            List<WorkerItem> filtered = new ArrayList<>();
            for (WorkerItem w : allWorkers) {
                if ("inactivo".equalsIgnoreCase(w.getStatus())) filtered.add(w);
            }
            callback.onSuccess(filtered);
        } else {
            callback.onSuccess(allWorkers);
        }
    }

    public void getAttendanceHistory(HistoryCallback callback) {
        callback.onSuccess(liveStateHistory);
    }
}
