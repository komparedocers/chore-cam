package com.choreocam.app.activities;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.choreocam.app.ChoreoCamApplication;
import com.choreocam.app.R;
import com.choreocam.app.database.AppDatabase;
import com.choreocam.app.models.User;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import java.util.UUID;

public class AuthActivity extends AppCompatActivity {

    private TextInputEditText emailInput;
    private TextInputEditText passwordInput;
    private TextInputEditText usernameInput;
    private MaterialButton loginBtn;
    private MaterialButton registerBtn;
    private MaterialButton skipBtn;

    private AppDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        database = ChoreoCamApplication.getDatabase();

        initializeViews();
        setupToolbar();
        setupClickListeners();
    }

    private void initializeViews() {
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        usernameInput = findViewById(R.id.usernameInput);
        loginBtn = findViewById(R.id.loginBtn);
        registerBtn = findViewById(R.id.registerBtn);
        skipBtn = findViewById(R.id.skipBtn);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Account");
        }
    }

    private void setupClickListeners() {
        loginBtn.setOnClickListener(v -> login());
        registerBtn.setOnClickListener(v -> register());
        skipBtn.setOnClickListener(v -> skipAuth());
    }

    private void login() {
        String email = emailInput.getText() != null ? emailInput.getText().toString() : "";
        String password = passwordInput.getText() != null ? passwordInput.getText().toString() : "";

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, "Login functionality - would connect to backend", Toast.LENGTH_SHORT).show();
    }

    private void register() {
        String email = emailInput.getText() != null ? emailInput.getText().toString() : "";
        String password = passwordInput.getText() != null ? passwordInput.getText().toString() : "";
        String username = usernameInput.getText() != null ? usernameInput.getText().toString() : "";

        if (email.isEmpty() || password.isEmpty() || username.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create local user for offline mode
        new Thread(() -> {
            User user = new User();
            user.setUserId(UUID.randomUUID().toString());
            user.setEmail(email);
            user.setUsername(username);
            user.setPro(false);
            user.setNeedsSync(true);

            database.userDao().insert(user);

            runOnUiThread(() -> {
                Toast.makeText(this, "Account created! Will sync when online.", Toast.LENGTH_LONG).show();
                finish();
            });
        }).start();
    }

    private void skipAuth() {
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
