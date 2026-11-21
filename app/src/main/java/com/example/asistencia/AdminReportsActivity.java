package com.example.asistencia;

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

        TextInputEditText etAdminRut = findViewById(R.id.etAdminRut);
        TextInputEditText etAdminDate = findViewById(R.id.etAdminDate);
        MaterialButton btnAdminQuery = findViewById(R.id.btnAdminQuery);
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

        // GU-05 / API 2: Consultar la información de los ingresos y salidas mediante RUT y Fecha
        btnAdminQuery.setOnClickListener(v -> {
            String rut = etAdminRut.getText() != null ? etAdminRut.getText().toString().trim() : "";
            String date = etAdminDate.getText() != null ? etAdminDate.getText().toString().trim() : "";

            if (rut.isEmpty() || date.isEmpty()) {
                Toast.makeText(this, "Ingrese RUT y fecha para consultar", Toast.LENGTH_SHORT).show();
            } else {
                tvAdminQueryResult.setText("Consultando a la API 2 (RUT: " + rut + ", Fecha: " + date + ")...");
                attendanceRepository.getAttendance(rut, date, new AttendanceRepository.AttendanceCallback() {
                    @Override
                    public void onSuccess(AttendanceRepository.AttendanceResult result) {
                        // OK: muestra datos en pantalla
                        String resText = "OK: " + result.getMessage() + "\n" +
                                "Trabajador RUT: " + rut + "\n" +
                                "Entrada: " + result.getCheckIn() + " | Salida: " + result.getCheckOut() + "\n" +
                                "Estado: " + result.getStatus();
                        tvAdminQueryResult.setText(resText);
                    }

                    @Override
                    public void onError(String errorMessage) {
                        // NOK: muestra error con opción de reintento
                        tvAdminQueryResult.setText("Error [NOK]: " + errorMessage + "\n(Toque 'Consultar por RUT y Fecha' para reintentar)");
                        Toast.makeText(AdminReportsActivity.this, "Error en consulta API 2", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
