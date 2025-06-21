package edu.uph.m23si3.glucotrack;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AddInsulinNotesActivity extends AppCompatActivity {
    TextView txvSeeRecommendation;
    Button btnSave;
    EditText edtInsulin, edtNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_insulin_notes);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        txvSeeRecommendation = findViewById(R.id.txvSeeRecommendation);
        btnSave = findViewById(R.id.btnSave);
        edtInsulin = findViewById(R.id.edtInsulin);
        edtNote = findViewById(R.id.edtNote);

        txvSeeRecommendation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AddInsulinNotesActivity.this);
                View view = LayoutInflater.from(AddInsulinNotesActivity.this).inflate(R.layout.see_recommendation_modal, null);
                builder.setView(view);
                AlertDialog dialog = builder.create();

                // Inisialisasi spinner
                Spinner spinner = view.findViewById(R.id.spinnerCondition);
                String[] items = {
                        "Based on current condition",
                        "Based on breakfast",
                        "Based on lunch",
                        "Based on dinner"
                };
                ArrayAdapter<String> adapter = new ArrayAdapter<>(AddInsulinNotesActivity.this, android.R.layout.simple_spinner_dropdown_item, items);
                spinner.setAdapter(adapter);

                // Tombol close
                ImageView btnClose = view.findViewById(R.id.btnClose);
                btnClose.setOnClickListener(v1 -> dialog.dismiss());

                // Tombol gunakan rekomendasi
                Button btnUse = view.findViewById(R.id.btnUseRecommendation);
                btnUse.setOnClickListener(v2 -> {
                    String selected = spinner.getSelectedItem().toString();

                    // Set isi edtInsulin berdasarkan pilihan
                    switch (selected) {
                        case "Based on current condition":
                            edtInsulin.setText("2");
                            break;
                        case "Based on breakfast":
                            edtInsulin.setText("3");
                            break;
                        case "Based on lunch":
                            edtInsulin.setText("4");
                            break;
                        case "Based on dinner":
                            edtInsulin.setText("5");
                            break;
                    }

                    dialog.dismiss();
                });

                dialog.show();
            }
        });

    }
}