package com.example.final_project;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.final_project.R;

public class GoogleLoginActivity extends AppCompatActivity {

    private EditText edtEmail;
    private Button btnLogin, btnCancel;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.google_login_activity);

        edtEmail = findViewById(R.id.edtEmail);
        btnLogin = findViewById(R.id.btnLogin);
        btnCancel = findViewById(R.id.btnCancel);

        btnLogin.setOnClickListener(v -> {
            String email = edtEmail.getText().toString().trim();

            if (TextUtils.isEmpty(email)) {
                Toast.makeText(this, "Please enter your Gmail address", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!email.contains("@") || !email.endsWith(".com")) {
                Toast.makeText(this, "Invalid email format. Please enter a valid Gmail.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!email.endsWith("@gmail.com")) {
                Toast.makeText(this, "Please use a Gmail address (@gmail.com)", Toast.LENGTH_SHORT).show();
                return;
            }

            Toast.makeText(this, "Login successful: " + email, Toast.LENGTH_SHORT).show();
        });

        btnCancel.setOnClickListener(v -> {
            Toast.makeText(this, "Login cancelled", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}
