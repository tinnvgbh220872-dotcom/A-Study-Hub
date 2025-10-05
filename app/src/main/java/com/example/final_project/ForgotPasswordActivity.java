package com.example.final_project;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText etEmailRequest;
    private Button btnGetCode, btnBackToLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_password_request);

        etEmailRequest = findViewById(R.id.etEmailRequest);
        btnGetCode = findViewById(R.id.btnGetCode);
        btnBackToLogin = findViewById(R.id.btnBackToLogin);

        btnGetCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmailRequest.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    etEmailRequest.setError("Please enter your email");
                    return;
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    etEmailRequest.setError("Invalid email format");
                    return;
                }

                Toast.makeText(ForgotPasswordActivity.this,
                        "Verification code sent to " + email,
                        Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(ForgotPasswordActivity.this, ResetPasswordActivity.class);
                intent.putExtra("email", email);
                startActivity(intent);
            }
        });

        btnBackToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
