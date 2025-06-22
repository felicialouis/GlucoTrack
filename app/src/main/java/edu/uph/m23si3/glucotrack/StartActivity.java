package edu.uph.m23si3.glucotrack;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class StartActivity extends AppCompatActivity {

    Button btnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        btnNext = findViewById(R.id.btnNext);

        btnNext.setOnClickListener(v -> {
            // Pindah ke halaman selanjutnya
            Intent intent = new Intent(StartActivity.this, StartActivity2.class);
            startActivity(intent);
            finish(); // opsional, agar user tidak bisa kembali ke onboarding pertama
        });
    }
}
