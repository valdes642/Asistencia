package com.example.asistencia;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.asistencia.data.repository.AttendanceRepository;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DashboardActivity extends AppCompatActivity {

    private boolean isCheckedIn = false;
    private TextView tvClockStatus;
    private MaterialButton btnClockAction;
    private AttendanceRepository attendanceRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.enableEdgeToEdge(getWindow());
        setContentView(R.layout.activity_dashboard);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        attendanceRepository = new AttendanceRepository();

        String username = getIntent().getStringExtra("USERNAME");
        String role = getIntent().getStringExtra("ROLE");
        String rut = getIntent().getStringExtra("RUT");
        String name = getIntent().getStringExtra("NAME");

        if (name == null || name.isEmpty()) name = username != null ? username : "Colaborador";
        if (rut == null || rut.isEmpty()) rut = "18.987.654-3";
        if (role == null) role = "worker";

        TextView tvUserGreeting = findViewById(R.id.tvUserGreeting);
        TextView tvUserRut = findViewById(R.id.tvUserRut);
        tvClockStatus = findViewById(R.id.tvClockStatus);
        btnClockAction = findViewById(R.id.btnClockAction);
        MaterialButton btnOpenAdmin = findViewById(R.id.btnOpenAdmin);
        MaterialButton btnLogout = findViewById(R.id.btnLogout);
        TextInputEditText etQueryDate = findViewById(R.id.etQueryDate);
        MaterialButton btnQueryAttendance = findViewById(R.id.btnQueryAttendance);
        TextView tvQueryResult = findViewById(R.id.tvQueryResult);

        tvUserGreeting.setText("Hola, " + name);
        tvUserRut.setText("RUT: " + rut);

        String currentTimeStr = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());

        btnClockAction.setOnClickListener(v -> {
            if (!isCheckedIn) {
                isCheckedIn = true;
                tvClockStatus.setText("Estado: Entrada registrada a las " + currentTimeStr);
                btnClockAction.setText("Marcar Salida");
                Toast.makeText(this, "¡Entrada registrada exitosamente!", Toast.LENGTH_SHORT).show();
            } else {
                isCheckedIn = false;
                tvClockStatus.setText("Estado: Jornada finalizada");
                btnClockAction.setText("Marcar Entrada");
                btnClockAction.setEnabled(false);
                Toast.makeText(this, "¡Salida registrada exitosamente!", Toast.LENGTH_SHORT).show();
            }
        });

        // GU-05 / API 2: Consultar asistencia enviando RUT + Fecha
        final String workerRut = rut;
        btnQueryAttendance.setOnClickListener(v -> {
            String queryDate = etQueryDate.getText() != null ? etQueryDate.getText().toString().trim() : "";
            if (queryDate.isEmpty()) {
                Toast.makeText(this, "Ingrese una fecha para consultar", Toast.LENGTH_SHORT).show();
            } else {
                tvQueryResult.setText("Consultando a la API 2 (RUT: " + workerRut + ", Fecha: " + queryDate + ")...");
                attendanceRepository.getAttendance(workerRut, queryDate, new AttendanceRepository.AttendanceCallback() {
                    @Override
                    public void onSuccess(AttendanceRepository.AttendanceResult result) {
                        // OK: muestra datos en pantalla
                        String resText = "OK: " + result.getMessage() + "\n" +
                                "Entrada: " + result.getCheckIn() + " | Salida: " + result.getCheckOut() + "\n" +
                                "Estado: " + result.getStatus();
                        tvQueryResult.setText(resText);
                    }

                    @Override
                    public void onError(String errorMessage) {
                        // NOK: muestra mensaje de error con opción de reintento
                        tvQueryResult.setText("Error [NOK]: " + errorMessage + "\n(Toque 'Buscar Registro' para reintentar)");
                        Toast.makeText(DashboardActivity.this, "Error en consulta API 2", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        // Mostrar botón de Admin si el rol es admin
        if ("admin".equalsIgnoreCase(role)) {
            btnOpenAdmin.setVisibility(View.VISIBLE);
        } else {
            btnOpenAdmin.setVisibility(View.GONE);
        }

        btnOpenAdmin.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, AdminReportsActivity.class);
            startActivity(intent);
        });

        // Botón Cerrar Sesión
        btnLogout.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}
