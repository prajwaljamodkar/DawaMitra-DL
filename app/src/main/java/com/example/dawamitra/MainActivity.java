package com.example.dawamitra;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.dawamitra.R;
import com.example.dawamitra.activities.DashboardActivity;
import com.example.dawamitra.activities.LoginActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences prefs = getSharedPreferences("user", MODE_PRIVATE);
        boolean logged = prefs.getBoolean("loggedIn", false);

        if (logged) {
            startActivity(new Intent(this, DashboardActivity.class));
        } else {
            startActivity(new Intent(this, LoginActivity.class));
        }

        finish();
    }
}