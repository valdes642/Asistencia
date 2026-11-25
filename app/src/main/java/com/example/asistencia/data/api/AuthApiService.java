package com.example.asistencia.data.api;

import com.example.asistencia.data.model.LoginRequest;
import com.example.asistencia.data.model.LoginResponse;
import com.example.asistencia.data.repository.AttendanceRepository;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface AuthApiService {
    @POST("api/v1/auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    @POST("api/login")
    Call<LoginResponse> loginAlternative(@Body LoginRequest request);

    @POST("login")
    Call<LoginResponse> loginDirect(@Body LoginRequest request);

    // Historial y Marcas de Asistencia (API 2)
    @GET("api/v1/attendance")
    Call<List<AttendanceRepository.AttendanceItem>> getAttendanceHistory();

    @GET("api/attendance")
    Call<List<AttendanceRepository.AttendanceItem>> getAttendanceHistoryAlt();

    @POST("api/v1/attendance")
    Call<Void> registerAttendance(@Body AttendanceRepository.AttendanceItem item);

    @POST("api/attendance")
    Call<Void> registerAttendanceAlt(@Body AttendanceRepository.AttendanceItem item);

    // Lista de Usuarios y Creación (Admin)
    @GET("api/v1/users")
    Call<List<AttendanceRepository.WorkerItem>> getWorkersList();

    @GET("api/users")
    Call<List<AttendanceRepository.WorkerItem>> getWorkersListAlt();

    @POST("api/v1/users")
    Call<Void> createUser(@Body AttendanceRepository.CreateUserRequest request);

    @POST("api/users")
    Call<Void> createUserAlt(@Body AttendanceRepository.CreateUserRequest request);
}
