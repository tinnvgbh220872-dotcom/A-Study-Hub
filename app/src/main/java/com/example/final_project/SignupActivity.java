package com.example.final_project;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

class SignupActivity extends AppCompatActivity {

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

        signupButton.setOnClickListener(v -> registerUser());
        loginRedirect.setOnClickListener(v -> {
            startActivity(new Intent(SignupActivity.this, LoginActivity.class));
            finish();
        });
        googleIcon.setOnClickListener(v -> Toast.makeText(this, "Google sign-up not available yet", Toast.LENGTH_SHORT).show());
        facebookIcon.setOnClickListener(v -> Toast.makeText(this, "Facebook sign-up not available yet", Toast.LENGTH_SHORT).show());
    }

    private void registerUser() {
        String name = fullNameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            fullNameEditText.setError("Enter your full name");
            return;
        }
        if (TextUtils.isEmpty(email)) {
            emailEditText.setError("Enter your email");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError("Enter password");
            return;
        }
        if (TextUtils.isEmpty(confirmPassword)) {
            confirmPasswordEditText.setError("Confirm your password");
            return;
        }
        if (!password.equals(confirmPassword)) {
            confirmPasswordEditText.setError("Passwords do not match");
            return;
        }

        Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(SignupActivity.this, LoginActivity.class));
        finish();
    }
}
