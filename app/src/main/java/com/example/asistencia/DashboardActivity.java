package com.example.asistencia;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.asistencia.data.repository.AttendanceRepository;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DashboardActivity extends AppCompatActivity {

    private AttendanceRepository attendanceRepository;
    private LinearLayout layoutHistoryContainer;
    private TextInputEditText etUserEmailInput;

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
        etUserEmailInput = findViewById(R.id.etUserEmailInput);
        MaterialButton btnRegisterCheckIn = findViewById(R.id.btnRegisterCheckIn);
        MaterialButton btnRegisterCheckOut = findViewById(R.id.btnRegisterCheckOut);
        MaterialButton btnOpenAdmin = findViewById(R.id.btnOpenAdmin);
        MaterialButton btnLogout = findViewById(R.id.btnLogout);
        layoutHistoryContainer = findViewById(R.id.layoutHistoryContainer);

        tvUserGreeting.setText("Hola, " + name);
        tvUserRut.setText("RUT: " + rut);

        // Prellenar email si está disponible
        if (username != null && username.contains("@")) {
            etUserEmailInput.setText(username);
        }

        btnRegisterCheckIn.setOnClickListener(v -> registerEvent("entrada"));
        btnRegisterCheckOut.setOnClickListener(v -> registerEvent("salida"));

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

        btnLogout.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        // Cargar historial reciente
        loadHistory();
    }

    private void registerEvent(String type) {
        String email = etUserEmailInput.getText() != null ? etUserEmailInput.getText().toString().trim() : "";
        if (email.isEmpty()) {
            Toast.makeText(this, "Ingrese el email del usuario para registrar", Toast.LENGTH_SHORT).show();
            return;
        }

        String nowTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        Toast.makeText(this, "Registrado " + type + " exitosamente para " + email + " a las " + nowTime, Toast.LENGTH_SHORT).show();
        loadHistory();
    }

    private void loadHistory() {
        attendanceRepository.getAttendanceHistory(new AttendanceRepository.HistoryCallback() {
            @Override
            public void onSuccess(List<AttendanceRepository.AttendanceItem> history) {
                renderHistoryTable(history);
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(DashboardActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void renderHistoryTable(List<AttendanceRepository.AttendanceItem> history) {
        layoutHistoryContainer.removeAllViews();

        for (AttendanceRepository.AttendanceItem item : history) {
            MaterialCardView card = new MaterialCardView(this);
            LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            cardParams.setMargins(0, 0, 0, 10);
            card.setLayoutParams(cardParams);
            card.setCardBackgroundColor(ContextCompat.getColor(this, R.color.surface));
            card.setRadius(20f);
            card.setStrokeColor(ContextCompat.getColor(this, R.color.outline));
            card.setStrokeWidth(1);

            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setPadding(20, 20, 20, 20);
            row.setGravity(Gravity.CENTER_VERTICAL);

            // Usuario
            TextView tvUser = new TextView(this);
            LinearLayout.LayoutParams userParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.1f);
            tvUser.setLayoutParams(userParams);
            tvUser.setText(item.getUser());
            tvUser.setTextColor(ContextCompat.getColor(this, R.color.text_primary));
            tvUser.setTextSize(13f);

            // Email
            TextView tvEmail = new TextView(this);
            LinearLayout.LayoutParams emailParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.4f);
            tvEmail.setLayoutParams(emailParams);
            tvEmail.setText(item.getEmail());
            tvEmail.setTextColor(ContextCompat.getColor(this, R.color.text_secondary));
            tvEmail.setTextSize(12f);

            // Tipo (entrada/salida)
            TextView tvType = new TextView(this);
            LinearLayout.LayoutParams typeParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 0.8f);
            tvType.setLayoutParams(typeParams);
            tvType.setText(item.getType());
            tvType.setTextSize(12f);
            if ("entrada".equalsIgnoreCase(item.getType())) {
                tvType.setTextColor(ContextCompat.getColor(this, R.color.success_fg));
            } else {
                tvType.setTextColor(ContextCompat.getColor(this, R.color.warning_fg));
            }

            // Hora
            TextView tvTime = new TextView(this);
            LinearLayout.LayoutParams timeParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.3f);
            tvTime.setLayoutParams(timeParams);
            tvTime.setText(item.getTime());
            tvTime.setGravity(Gravity.END);
            tvTime.setTextColor(ContextCompat.getColor(this, R.color.text_primary));
            tvTime.setTextSize(12f);

            row.addView(tvUser);
            row.addView(tvEmail);
            row.addView(tvType);
            row.addView(tvTime);
            card.addView(row);

            layoutHistoryContainer.addView(card);
        }
    }
}
