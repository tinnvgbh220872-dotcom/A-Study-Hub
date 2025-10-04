package com.example.final_project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    private EditText fullNameEditText, emailEditText, passwordEditText, confirmPasswordEditText;
    private Button signupButton;
    private TextView loginRedirect;
    private ImageView googleIcon, facebookIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_activity);

        fullNameEditText = findViewById(R.id.fullNameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        signupButton = findViewById(R.id.signupButton);
        loginRedirect = findViewById(R.id.loginRedirect);
        googleIcon = findViewById(R.id.googleIcon);
        facebookIcon = findViewById(R.id.facebookIcon);

        signupButton.setOnClickListener(v -> handleRegister());
        loginRedirect.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        googleIcon.setOnClickListener(v ->
                Toast.makeText(this, "Google Sign-In coming soon", Toast.LENGTH_SHORT).show());

        facebookIcon.setOnClickListener(v ->
                Toast.makeText(this, "Facebook Sign-In coming soon", Toast.LENGTH_SHORT).show());
    }

    private void handleRegister() {
        String fullName = fullNameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();

        if (fullName.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Invalid email address", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        intent.putExtra("registeredEmail", email);
        startActivity(intent);
        finish();
    }
}
