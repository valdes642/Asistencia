package com.example.asistencia.data.repository;

import android.os.Handler;
import android.os.Looper;

import java.util.Arrays;
import java.util.List;

public class AttendanceRepository {

    public static class AttendanceResult {
        private final boolean success;
        private final String message;
        private final String checkIn;
        private final String checkOut;
        private final String status;

        public AttendanceResult(boolean success, String message, String checkIn, String checkOut, String status) {
            this.success = success;
            this.message = message;
            this.checkIn = checkIn;
            this.checkOut = checkOut;
            this.status = status;
        }

        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public String getCheckIn() { return checkIn; }
        public String getCheckOut() { return checkOut; }
        public String getStatus() { return status; }
    }

    public static class WorkerItem {
        private final String name;
        private final String rut;
        private final String status;

        public WorkerItem(String name, String rut, String status) {
            this.name = name;
            this.rut = rut;
            this.status = status;
        }

        public String getName() { return name; }
        public String getRut() { return rut; }
        public String getStatus() { return status; }
    }

    public interface AttendanceCallback {
        void onSuccess(AttendanceResult result);
        void onError(String errorMessage);
    }

    public interface WorkersCallback {
        void onSuccess(List<WorkerItem> workers);
        void onError(String errorMessage);
    }

    // Simula API 2 (Consulta de Entrada y Salida por RUT + Fecha)
    public void getAttendance(String rut, String date, AttendanceCallback callback) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(() -> {
            // Validación simulada de existencia de datos en Base de Datos (API 2)
            if (date != null && !date.trim().isEmpty() && rut != null && !rut.trim().isEmpty()) {
                if (date.equals("31/12/2025")) {
                    // Simulación NOK (No existen datos / Error en servidor)
                    callback.onError("NOK: No se registran datos de asistencia para la fecha indicada.");
                } else {
                    // Simulación OK
                    AttendanceResult result = new AttendanceResult(
                            true,
                            "OK: Datos obtenidos correctamente",
                            "08:00 hrs",
                            "17:30 hrs",
                            "A tiempo"
                    );
                    callback.onSuccess(result);
                }
            } else {
                callback.onError("NOK: Parámetros inválidos (RUT o Fecha vacíos)");
            }
        }, 1000);
    }

    // Simula API Consulta de todos los trabajadores activos en el sistema (para Administrador)
    public void getWorkersList(WorkersCallback callback) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(() -> {
            List<WorkerItem> workers = Arrays.asList(
                    new WorkerItem("Victor Vergara", "18.987.654-3", "A tiempo"),
                    new WorkerItem("Ricardo Tapia", "15.123.456-7", "Atraso"),
                    new WorkerItem("Tomas Monjes", "19.876.543-2", "Ausente"),
                    new WorkerItem("Ana Pérez", "16.543.210-9", "A tiempo"),
                    new WorkerItem("Carlos Gómez", "17.111.222-3", "A tiempo")
            );
            callback.onSuccess(workers);
        }, 800);
    }
}
