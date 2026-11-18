package com.example.asistencia;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class AdminReportsActivity extends AppCompatActivity {

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

        TextInputEditText etAdminRut = findViewById(R.id.etAdminRut);
        TextInputEditText etAdminDate = findViewById(R.id.etAdminDate);
        MaterialButton btnAdminQuery = findViewById(R.id.btnAdminQuery);
        TextView tvAdminQueryResult = findViewById(R.id.tvAdminQueryResult);

        // GU-05: Consultar la información de los ingresos y salidas de esos usuarios mediante su RUT y el ingreso de la fecha
        btnAdminQuery.setOnClickListener(v -> {
            String rut = etAdminRut.getText() != null ? etAdminRut.getText().toString().trim() : "";
            String date = etAdminDate.getText() != null ? etAdminDate.getText().toString().trim() : "";

            if (rut.isEmpty() || date.isEmpty()) {
                Toast.makeText(this, "Ingrese RUT y fecha para consultar", Toast.LENGTH_SHORT).show();
            } else {
                // Simulación de consulta API AP-02 (RUT y Fecha)
                tvAdminQueryResult.setText("Resultado (" + rut + " - " + date + "):\nEntrada: 08:02 hrs | Salida: 17:35 hrs (Estado: A tiempo)");
            }
        });
    }
}
