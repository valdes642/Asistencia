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

import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DashboardActivity extends AppCompatActivity {

    private boolean isCheckedIn = false;
    private TextView tvClockStatus;
    private MaterialButton btnClockAction;

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

        String username = getIntent().getStringExtra("USERNAME");
        if (username == null || username.isEmpty()) {
            username = "Colaborador";
        }

        TextView tvUserGreeting = findViewById(R.id.tvUserGreeting);
        TextView tvCurrentDate = findViewById(R.id.tvCurrentDate);
        TextView tvCurrentTime = findViewById(R.id.tvCurrentTime);
        tvClockStatus = findViewById(R.id.tvClockStatus);
        btnClockAction = findViewById(R.id.btnClockAction);
        MaterialButton btnOpenAdmin = findViewById(R.id.btnOpenAdmin);

        tvUserGreeting.setText("Hola, " + username);

        // Current Date
        String currentDate = new SimpleDateFormat("EEEE, dd 'de' MMMM 'de' yyyy", new Locale("es", "ES")).format(new Date());
        tvCurrentDate.setText(currentDate);

        // Current Time
        String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
        tvCurrentTime.setText(currentTime);

        btnClockAction.setOnClickListener(v -> {
            if (!isCheckedIn) {
                isCheckedIn = true;
                tvClockStatus.setText("Estado: Entrada registrada a las " + currentTime);
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

        btnOpenAdmin.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, AdminReportsActivity.class);
            startActivity(intent);
        });
    }
}
