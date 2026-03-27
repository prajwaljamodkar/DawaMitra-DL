package com.example.dawamitra.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.example.dawamitra.R;

public class LoginActivity extends AppCompatActivity {

    EditText email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        Button btn = findViewById(R.id.loginBtn);

        btn.setOnClickListener(v -> {
            if (email.getText().toString().equals("admin") &&
                    password.getText().toString().equals("1234")) {

                SharedPreferences prefs = getSharedPreferences("user", MODE_PRIVATE);
                prefs.edit().putBoolean("loggedIn", true).apply();

                startActivity(new Intent(this, DashboardActivity.class));
                finish();
            } else {
                Toast.makeText(this, "Wrong Login", Toast.LENGTH_SHORT).show();
            }
        });
    }
}