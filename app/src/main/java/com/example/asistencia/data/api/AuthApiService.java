package com.example.asistencia.data.api;

import com.example.asistencia.data.model.LoginRequest;
import com.example.asistencia.data.model.LoginResponse;
import com.example.asistencia.data.repository.AttendanceRepository;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface AuthApiService {
    @POST("api/v1/auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    @GET("api/v1/attendance")
    Call<AttendanceRepository.AttendanceResult> getAttendance(
            @Query("rut") String rut,
            @Query("date") String date
    );
}
