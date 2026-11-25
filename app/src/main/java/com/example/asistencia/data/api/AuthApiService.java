package com.example.asistencia.data.api;

import com.example.asistencia.data.model.LoginRequest;
import com.example.asistencia.data.model.LoginResponse;
import com.example.asistencia.data.repository.AttendanceRepository;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface AuthApiService {
    @POST("api/v1/auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    @POST("api/login")
    Call<LoginResponse> loginAlternative(@Body LoginRequest request);

    @POST("login")
    Call<LoginResponse> loginDirect(@Body LoginRequest request);

    @GET("api/v1/attendance")
    Call<List<AttendanceRepository.AttendanceItem>> getAttendance(
            @Query("rut") String rut,
            @Query("date") String date
    );

    @GET("api/v1/attendance/history")
    Call<List<AttendanceRepository.AttendanceItem>> getAttendanceHistoryByEmail(
            @Query("email") String email
    );

    @GET("api/v1/attendance/search")
    Call<List<AttendanceRepository.AttendanceItem>> searchAttendance(
            @Query("name") String name,
            @Query("email") String email,
            @Query("status") String status,
            @Query("rut") String rut,
            @Query("date") String date
    );
}
