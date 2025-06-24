package edu.uph.m23si3.glucotrack;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class StartActivity3 extends AppCompatActivity {

    Button btnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start3); // Ganti ke layout activity_start3.xml

        btnNext = findViewById(R.id.btnNext);

        btnNext.setOnClickListener(v -> {
            // Lanjut ke halaman welcome/login
            Intent intent = new Intent(StartActivity3.this, WelcomeActivity.class);
            startActivity(intent);
            finish(); // agar tidak bisa kembali ke onboarding
        });
    }
}
