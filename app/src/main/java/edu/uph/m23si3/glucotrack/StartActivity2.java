package edu.uph.m23si3.glucotrack;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class StartActivity2 extends AppCompatActivity {

    ImageView imgOnboarding;
    TextView tvTitle, tvDescription;
    Button btnNext;

    int pageIndex = 0;

    // Data onboarding
    String[] titles = {
            "Pantau Glukosa Harianmu",
            "Rekomendasi Insulin Real-Time",
            "Catat Semua Aktivitas Kesehatanmu",
            "Terkoneksi dengan Alat CGM"
    };

    String[] descriptions = {
            "Aplikasi ini membantumu mencatat konsumsi glukosa harian dan saran insulin.",
            "Lihat data CGM secara real-time dan dapatkan saran dosis insulin otomatis.",
            "Pantau rekapan glukosa, insulin yang disuntikkan, dan penggunaan alat medis lainnya.",
            "Hubungkan aplikasi ini dengan alat CGM melalui scan barcode dan pantau kesehatanmu lebih efisien."
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        imgOnboarding = findViewById(R.id.imgOnboarding);
        tvTitle = findViewById(R.id.tvTitle);
        tvDescription = findViewById(R.id.tvDescription);
        btnNext = findViewById(R.id.btnNext);

        // Tampilkan halaman pertama
        updateOnboardingPage();

        btnNext.setOnClickListener(v -> {
            pageIndex++;
            if (pageIndex < titles.length) {
                updateOnboardingPage();
            } else {
                // Kalau sudah selesai onboarding, lanjut ke login/signup
                Intent intent = new Intent(StartActivity2.this, StartActivity3.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void updateOnboardingPage() {
        tvTitle.setText(titles[pageIndex]);
        tvDescription.setText(descriptions[pageIndex]);

        if (pageIndex == titles.length - 1) {
            btnNext.setText("Mulai");
        }
    }
}
