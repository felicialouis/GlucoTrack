package edu.uph.m23si3.glucotrack;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class StartActivity2 extends AppCompatActivity {

    Button btnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start2); // GANTI layout-nya ke activity_start2

        btnNext = findViewById(R.id.btnNext);

        btnNext.setOnClickListener(v -> {
            // Pindah ke halaman onboarding ke-3
            Intent intent = new Intent(StartActivity2.this, StartActivity3.class);
            startActivity(intent);
            finish(); // Agar tidak bisa kembali ke StartActivity2 dengan tombol back
        });
    }
}
