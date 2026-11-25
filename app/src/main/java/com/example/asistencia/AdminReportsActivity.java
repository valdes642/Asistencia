package com.example.asistencia;

import android.content.Intent;
import android.os.Bundle;
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

import java.util.List;

public class AdminReportsActivity extends AppCompatActivity {

    private AttendanceRepository attendanceRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.enableEdgeToEdge(getWindow());
        setContentView(R.layout.activity_admin_reports);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        attendanceRepository = new AttendanceRepository();

        TextInputEditText etAdminName = findViewById(R.id.etAdminName);
        TextInputEditText etAdminEmail = findViewById(R.id.etAdminEmail);
        TextInputEditText etAdminStatus = findViewById(R.id.etAdminStatus);
        TextInputEditText etAdminRut = findViewById(R.id.etAdminRut);
        TextInputEditText etAdminDate = findViewById(R.id.etAdminDate);
        MaterialButton btnAdminQuery = findViewById(R.id.btnAdminQuery);
        MaterialButton btnLogout = findViewById(R.id.btnLogout);
        TextView tvAdminQueryResult = findViewById(R.id.tvAdminQueryResult);

        // Cargar lista de trabajadores activos (Simulación API Admin)
        attendanceRepository.getWorkersList(new AttendanceRepository.WorkersCallback() {
            @Override
            public void onSuccess(List<AttendanceRepository.WorkerItem> workers) {
                Toast.makeText(AdminReportsActivity.this, "Se cargaron " + workers.size() + " trabajadores activos del sistema", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(AdminReportsActivity.this, "Error al cargar trabajadores", Toast.LENGTH_SHORT).show();
            }
        });

        // Búsqueda por Nombre, Email, Estado, RUT y Fecha (Administrador)
        btnAdminQuery.setOnClickListener(v -> {
            String name = etAdminName.getText() != null ? etAdminName.getText().toString().trim() : "";
            String email = etAdminEmail.getText() != null ? etAdminEmail.getText().toString().trim() : "";
            String status = etAdminStatus.getText() != null ? etAdminStatus.getText().toString().trim() : "";
            String rut = etAdminRut.getText() != null ? etAdminRut.getText().toString().trim() : "";
            String date = etAdminDate.getText() != null ? etAdminDate.getText().toString().trim() : "";

            if (name.isEmpty() && email.isEmpty() && status.isEmpty() && rut.isEmpty() && date.isEmpty()) {
                Toast.makeText(this, "Por favor ingrese al menos un criterio de búsqueda (Nombre, Email o Estado)", Toast.LENGTH_SHORT).show();
            } else {
                tvAdminQueryResult.setText("Buscando en API (Nombre: " + name + ", Email: " + email + ", Estado: " + status + ")...");
                attendanceRepository.searchAttendance(name, email, status, rut, date, new AttendanceRepository.AttendanceCallback() {
                    @Override
                    public void onSuccess(AttendanceRepository.AttendanceResult result) {
                        String summary = "Resultado Búsqueda API:\n" +
                                "• Nombre: " + (name.isEmpty() ? "Cualquiera" : name) + "\n" +
                                "• Email: " + (email.isEmpty() ? "Cualquiera" : email) + "\n" +
                                "• Estado: " + result.getStatus() + "\n" +
                                "• Entrada: " + result.getCheckIn() + " | Salida: " + result.getCheckOut();
                        tvAdminQueryResult.setText(summary);
                    }

                    @Override
                    public void onError(String errorMessage) {
                        tvAdminQueryResult.setText("Error en búsqueda: " + errorMessage);
                    }
                });
            }
        });

        // Botón Cerrar Sesión
        btnLogout.setOnClickListener(v -> {
            Intent intent = new Intent(AdminReportsActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}
