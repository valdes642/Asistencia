package com.example.asistencia;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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

import java.util.List;

public class AdminReportsActivity extends AppCompatActivity {

    private AttendanceRepository attendanceRepository;
    private LinearLayout layoutWorkersContainer;
    private AutoCompleteTextView spNewUserStatus;

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

        TextInputEditText etNewUserName = findViewById(R.id.etNewUserName);
        TextInputEditText etNewUserEmail = findViewById(R.id.etNewUserEmail);
        TextInputEditText etNewUserPassword = findViewById(R.id.etNewUserPassword);
        spNewUserStatus = findViewById(R.id.spNewUserStatus);
        MaterialButton btnAddUser = findViewById(R.id.btnAddUser);

        MaterialButton btnFilterAll = findViewById(R.id.btnFilterAll);
        MaterialButton btnFilterActive = findViewById(R.id.btnFilterActive);
        MaterialButton btnFilterInactive = findViewById(R.id.btnFilterInactive);

        layoutWorkersContainer = findViewById(R.id.layoutWorkersContainer);
        MaterialButton btnLogout = findViewById(R.id.btnLogout);

        // Configurar selector de estado (Activo / Inactivo)
        String[] statusOptions = new String[]{"Activo", "Inactivo"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, statusOptions);
        spNewUserStatus.setAdapter(adapter);

        // Guardar Usuario con Estado
        btnAddUser.setOnClickListener(v -> {
            String name = etNewUserName.getText() != null ? etNewUserName.getText().toString().trim() : "";
            String email = etNewUserEmail.getText() != null ? etNewUserEmail.getText().toString().trim() : "";
            String pass = etNewUserPassword.getText() != null ? etNewUserPassword.getText().toString().trim() : "";
            String status = spNewUserStatus.getText() != null ? spNewUserStatus.getText().toString().trim().toLowerCase() : "activo";

            if (name.isEmpty() || email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Por favor complete todos los campos para añadir usuario", Toast.LENGTH_SHORT).show();
            } else {
                attendanceRepository.addWorker(name, email, pass, status, new AttendanceRepository.ActionCallback() {
                    @Override
                    public void onSuccess(String message) {
                        Toast.makeText(AdminReportsActivity.this, message, Toast.LENGTH_SHORT).show();
                        etNewUserName.setText("");
                        etNewUserEmail.setText("");
                        etNewUserPassword.setText("");
                        loadWorkers("todos");
                    }

                    @Override
                    public void onError(String errorMessage) {
                        Toast.makeText(AdminReportsActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        // Filtros
        btnFilterAll.setOnClickListener(v -> loadWorkers("todos"));
        btnFilterActive.setOnClickListener(v -> loadWorkers("activos"));
        btnFilterInactive.setOnClickListener(v -> loadWorkers("inactivos"));

        // Cargar lista inicial
        loadWorkers("todos");

        // Botón Cerrar Sesión
        btnLogout.setOnClickListener(v -> {
            Intent intent = new Intent(AdminReportsActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void loadWorkers(String filter) {
        attendanceRepository.getWorkersList(filter, new AttendanceRepository.WorkersCallback() {
            @Override
            public void onSuccess(List<AttendanceRepository.WorkerItem> workers) {
                renderWorkersTable(workers);
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(AdminReportsActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void renderWorkersTable(List<AttendanceRepository.WorkerItem> workers) {
        layoutWorkersContainer.removeAllViews();

        for (AttendanceRepository.WorkerItem worker : workers) {
            MaterialCardView card = new MaterialCardView(this);
            LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            cardParams.setMargins(0, 0, 0, 12);
            card.setLayoutParams(cardParams);
            card.setCardBackgroundColor(ContextCompat.getColor(this, R.color.surface));
            card.setRadius(24f);
            card.setStrokeColor(ContextCompat.getColor(this, R.color.outline));
            card.setStrokeWidth(1);

            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setPadding(24, 24, 24, 24);
            row.setGravity(Gravity.CENTER_VERTICAL);

            // Nombre
            TextView tvName = new TextView(this);
            LinearLayout.LayoutParams nameParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.2f);
            tvName.setLayoutParams(nameParams);
            tvName.setText(worker.getName());
            tvName.setTextColor(ContextCompat.getColor(this, R.color.text_primary));
            tvName.setTextSize(14f);

            // Email
            TextView tvEmail = new TextView(this);
            LinearLayout.LayoutParams emailParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.6f);
            tvEmail.setLayoutParams(emailParams);
            tvEmail.setText(worker.getEmail());
            tvEmail.setTextColor(ContextCompat.getColor(this, R.color.text_secondary));
            tvEmail.setTextSize(13f);

            // Estado (Badge Activo / Inactivo)
            TextView tvStatus = new TextView(this);
            LinearLayout.LayoutParams statusParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 0.8f);
            tvStatus.setLayoutParams(statusParams);
            tvStatus.setText(worker.getStatus());
            tvStatus.setGravity(Gravity.END);
            tvStatus.setTextSize(13f);

            if ("activo".equalsIgnoreCase(worker.getStatus())) {
                tvStatus.setTextColor(ContextCompat.getColor(this, R.color.success_fg));
            } else {
                tvStatus.setTextColor(ContextCompat.getColor(this, R.color.danger_fg));
            }

            row.addView(tvName);
            row.addView(tvEmail);
            row.addView(tvStatus);
            card.addView(row);

            layoutWorkersContainer.addView(card);
        }
    }
}
