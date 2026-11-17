package com.example.asistencia.data.model;

public class AttendanceRecord {
    private final String date;
    private final String checkInTime;
    private final String checkOutTime;
    private final String status; // "A tiempo", "Atraso", "Ausente"

    public AttendanceRecord(String date, String checkInTime, String checkOutTime, String status) {
        this.date = date;
        this.checkInTime = checkInTime;
        this.checkOutTime = checkOutTime;
        this.status = status;
    }

    public String getDate() { return date; }
    public String getCheckInTime() { return checkInTime; }
    public String getCheckOutTime() { return checkOutTime; }
    public String getStatus() { return status; }
}
