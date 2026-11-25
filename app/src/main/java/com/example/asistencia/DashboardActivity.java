package com.example.asistencia;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
    private TextView tvLiveStatus;
    private String currentWorkerEmail;
    private String currentWorkerName;

    // Handler para auto-sincronización en tiempo real cada 3 segundos
    private final Handler autoSyncHandler = new Handler(Looper.getMainLooper());
    private final Runnable autoSyncRunnable = new Runnable() {
        @Override
        public void run() {
            syncRealtimeStatus();
            autoSyncHandler.postDelayed(this, 3000);
        }
    };

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

        if (name == null || name.isEmpty()) name = username != null ? username : "Camila Rojas";
        if (role == null) role = "worker";

        currentWorkerName = name;
        currentWorkerEmail = (username != null && username.contains("@")) ? username : "camila.rojas@empresa.cl";

        TextView tvUserGreeting = findViewById(R.id.tvUserGreeting);
        TextView tvUserRut = findViewById(R.id.tvUserRut);
        tvLiveStatus = findViewById(R.id.tvLiveStatus);
        etUserEmailInput = findViewById(R.id.etUserEmailInput);
        MaterialButton btnRefreshLive = findViewById(R.id.btnRefreshLive);
        MaterialButton btnRegisterCheckIn = findViewById(R.id.btnRegisterCheckIn);
        MaterialButton btnRegisterCheckOut = findViewById(R.id.btnRegisterCheckOut);
        MaterialButton btnOpenAdmin = findViewById(R.id.btnOpenAdmin);
        MaterialButton btnLogout = findViewById(R.id.btnLogout);
        layoutHistoryContainer = findViewById(R.id.layoutHistoryContainer);

        tvUserGreeting.setText("Hola, " + currentWorkerName);
        tvUserRut.setText(currentWorkerEmail);
        etUserEmailInput.setText(currentWorkerEmail);

        btnRefreshLive.setOnClickListener(v -> syncRealtimeStatus());

        btnRegisterCheckIn.setOnClickListener(v -> registerEvent("entrada"));
        btnRegisterCheckOut.setOnClickListener(v -> registerEvent("salida"));

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
    }

    @Override
    protected void onResume() {
        super.onResume();
        autoSyncHandler.post(autoSyncRunnable);
    }

    @Override
    protected void onPause() {
        super.onPause();
        autoSyncHandler.removeCallbacks(autoSyncRunnable);
    }

    private void syncRealtimeStatus() {
        attendanceRepository.getWorkerAttendanceRealtime(currentWorkerEmail, new AttendanceRepository.HistoryCallback() {
            @Override
            public void onSuccess(List<AttendanceRepository.AttendanceItem> history) {
                renderHistoryTable(history);

                if (history != null && !history.isEmpty()) {
                    AttendanceRepository.AttendanceItem lastMark = history.get(0);
                    if ("entrada".equalsIgnoreCase(lastMark.getType())) {
                        tvLiveStatus.setText("⚡ Sincronizado en Vivo:\n" +
                                "ENTRADA REGISTRADA (" + lastMark.getTime() + ")\n" +
                                "Usuario: " + lastMark.getUser());
                    } else if ("salida".equalsIgnoreCase(lastMark.getType())) {
                        tvLiveStatus.setText("⚡ Sincronizado en Vivo:\n" +
                                "SALIDA REGISTRADA (" + lastMark.getTime() + ")\n" +
                                "Usuario: " + lastMark.getUser());
                    } else {
                        tvLiveStatus.setText("Sin marcas recientes.");
                    }
                } else {
                    tvLiveStatus.setText("Esperando marcas desde el servidor...");
                }
            }

            @Override
            public void onError(String errorMessage) {
                tvLiveStatus.setText("Sincronizando...");
            }
        });
    }

    private void registerEvent(String type) {
        String email = etUserEmailInput.getText() != null ? etUserEmailInput.getText().toString().trim() : "";
        if (email.isEmpty()) email = currentWorkerEmail;

        String nowTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        attendanceRepository.addAttendanceEvent(currentWorkerName, email, type, nowTime, new AttendanceRepository.ActionCallback() {
            @Override
            public void onSuccess(String message) {
                Toast.makeText(DashboardActivity.this, message, Toast.LENGTH_SHORT).show();
                syncRealtimeStatus();
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
