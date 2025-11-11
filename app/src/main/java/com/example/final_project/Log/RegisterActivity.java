package com.example.final_project.Log;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.final_project.SQL.UserDatabase;
import com.example.final_project.MainScreen.MainScreenActivity;
import com.example.final_project.R;

public class RegisterActivity extends AppCompatActivity {

    private EditText fullNameEditText, emailEditText, phoneEditText, passwordEditText, confirmPasswordEditText;
    private Button signupButton;
    private TextView loginRedirect;
    private ImageView googleIcon, facebookIcon;
    private UserDatabase userDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_activity);

        initViews();
        userDatabase = new UserDatabase(this);

        signupButton.setOnClickListener(v -> handleRegister());
        loginRedirect.setOnClickListener(v -> navigateToLogin());
        googleIcon.setOnClickListener(v -> startActivity(new Intent(this, GoogleLoginActivity.class)));
        facebookIcon.setOnClickListener(v -> handleFacebookLogin());
    }

    private void initViews() {
        fullNameEditText = findViewById(R.id.fullNameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        signupButton = findViewById(R.id.signupButton);
        loginRedirect = findViewById(R.id.loginRedirect);
        googleIcon = findViewById(R.id.googleIcon);
        facebookIcon = findViewById(R.id.facebookIcon);
    }

    private void handleRegister() {
        String fullName = fullNameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();

        if (!validateInputs(fullName, email, phone, password, confirmPassword)) return;

        boolean inserted = userDatabase.insertUser(fullName, email, password, phone);

        if (inserted) {
            Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.putExtra("email", email);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Email already registered. Try logging in.", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validateInputs(String fullName, String email, String phone, String password, String confirmPassword) {
        if (fullName.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showMessage("Please fill in all fields");
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showMessage("Invalid email address");
            return false;
        }

        if (!Patterns.PHONE.matcher(phone).matches()) {
            showMessage("Invalid phone number");
            return false;
        }

        if (password.length() < 6) {
            showMessage("Password must be at least 6 characters");
            return false;
        }

        if (!password.equals(confirmPassword)) {
            showMessage("Passwords do not match");
            return false;
        }

        return true;
    }

    private void navigateToLogin() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    private void handleFacebookLogin() {
        startActivity(new Intent(this, MainScreenActivity.class));
        showMessage("Facebook Login Successful");
        finish();
    }

    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
