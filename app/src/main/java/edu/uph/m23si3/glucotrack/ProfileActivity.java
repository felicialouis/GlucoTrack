package edu.uph.m23si3.glucotrack;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ProfileActivity extends AppCompatActivity {

    Spinner spinnerSex, spinnerDiabetes;
    Switch switchInsulin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inisialisasi view
        spinnerSex = findViewById(R.id.spinner_sex);
        spinnerDiabetes = findViewById(R.id.spinner_diabetes);
        switchInsulin = findViewById(R.id.switch_insulin); // Pastikan ID-nya sama di XML

        // Data spinner
        String[] sexOptions = {"Male", "Female", "Other"};
        String[] diabetesTypes = {"Type 1", "Type 2", "Gestational", "Other"};

        // Adapter spinner sex
        ArrayAdapter<String> sexAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, sexOptions);
        sexAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSex.setAdapter(sexAdapter);

        // Adapter spinner diabetes
        ArrayAdapter<String> diabetesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, diabetesTypes);
        diabetesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDiabetes.setAdapter(diabetesAdapter);

        // Switch listener
        switchInsulin.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                Toast.makeText(this, "Insulin Therapy: Yes", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Insulin Therapy: No", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.mBersihkanForm) {
            Toast.makeText(this, "Form dibersihkan", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.mPengaturan) {
            Toast.makeText(this, "Masuk ke Pengaturan", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
