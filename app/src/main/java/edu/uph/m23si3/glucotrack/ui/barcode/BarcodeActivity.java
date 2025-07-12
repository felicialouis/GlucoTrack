package edu.uph.m23si3.glucotrack.ui.barcode;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import edu.uph.m23si3.glucotrack.R;

public class BarcodeActivity extends AppCompatActivity {

    private ImageView imgBarcode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode);

        ImageView imgBarcode = findViewById(R.id.img_barcode);
        ImageView btnBack = findViewById(R.id.btn_back);
        ImageView imgScan = findViewById(R.id.img_scan);

        // Menutup activity dan kembali ke fragment sebelumnya
        btnBack.setOnClickListener(v -> finish());
        imgScan.setOnClickListener(v -> finish());
    }
}
