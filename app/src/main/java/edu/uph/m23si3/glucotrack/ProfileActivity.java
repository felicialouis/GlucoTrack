package edu.uph.m23si3.glucotrack;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
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
    EditText edtNama, edtEmail, edtAge, edtTarget;
    SharedPreferences prefs;

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

        // Inisialisasi View
        spinnerSex = findViewById(R.id.spinner_sex);
        spinnerDiabetes = findViewById(R.id.spinner_diabetes);
        switchInsulin = findViewById(R.id.switch_insulin);
        edtNama = findViewById(R.id.edtNama);
        edtEmail = findViewById(R.id.edtEmail);
        edtAge = findViewById(R.id.edtAge);
        edtTarget = findViewById(R.id.edtTarget);

        // SharedPreferences
        prefs = getSharedPreferences("user_profile", MODE_PRIVATE);

        // Load data kalau ada
        edtNama.setText(prefs.getString("nama", ""));
        edtEmail.setText(prefs.getString("email", ""));
        edtAge.setText(prefs.getString("age", ""));
        edtTarget.setText(prefs.getString("target", ""));

        // Auto save saat mengetik
        edtNama.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                prefs.edit().putString("nama", s.toString()).apply();
            }
        });

        edtEmail.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                prefs.edit().putString("email", s.toString()).apply();
            }
        });

        edtAge.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                prefs.edit().putString("age", s.toString()).apply();
            }
        });

        edtTarget.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                prefs.edit().putString("target", s.toString()).apply();
            }
        });

        // Data Spinner
        String[] sexOptions = {"Male", "Female", "Other"};
        String[] diabetesTypes = {"Type 1", "Type 2", "Gestational", "Other"};

        ArrayAdapter<String> sexAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, sexOptions);
        sexAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSex.setAdapter(sexAdapter);

        ArrayAdapter<String> diabetesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, diabetesTypes);
        diabetesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDiabetes.setAdapter(diabetesAdapter);

        // Switch handler
        switchInsulin.setOnCheckedChangeListener((buttonView, isChecked) -> {
            String msg = isChecked ? "Insulin Therapy: Yes" : "Insulin Therapy: No";
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
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
            edtNama.setText("");
            edtEmail.setText("");
            edtAge.setText("");
            edtTarget.setText("");
            Toast.makeText(this, "Form dibersihkan", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.mPengaturan) {
            Toast.makeText(this, "Masuk ke Pengaturan", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

