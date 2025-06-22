package edu.uph.m23si3.glucotrack;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
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

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

import edu.uph.m23si3.glucotrack.Model.InsulinNotes;
import edu.uph.m23si3.glucotrack.Utils.InsulinNotesStorage;

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

        Intent intent = getIntent();
        if (intent.hasExtra("edit_position")) {
            int position = intent.getIntExtra("edit_position", -1);
            if (position != -1) {
                ArrayList<InsulinNotes> notes = InsulinNotesStorage.loadNotes(this);
                InsulinNotes note = notes.get(position);
                edtInsulin.setText(String.valueOf(note.getNumberOfUnitOfInsulin()));
                edtNote.setText(note.getNotes());

                btnSave.setOnClickListener(v -> {
                    String insulinStr = edtInsulin.getText().toString();
                    if (insulinStr.isEmpty()) {
                        edtInsulin.setError("Masukkan jumlah insulin");
                        return;
                    }
                    int insulin = Integer.parseInt(insulinStr);
                    String noteText = edtNote.getText().toString();

                    InsulinNotes newNote = new InsulinNotes(insulin, noteText, note.getDate(), note.getTime());

                    notes.set(position, newNote);
                    InsulinNotesStorage.saveNotes(this, notes);
                    finish();
                });

                return; // keluar dari onCreate agar tombol save normal tidak ditambahkan lagi
            }
        }


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

                // Mengubah txvDose
                TextView txvDose = view.findViewById(R.id.txvDose);
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String selected = parent.getItemAtPosition(position).toString();

                        switch (selected) {
                            case "Based on current condition":
                                txvDose.setText("20");
                                break;
                            case "Based on breakfast":
                                txvDose.setText("30");
                                break;
                            case "Based on lunch":
                                txvDose.setText("40");
                                break;
                            case "Based on dinner":
                                txvDose.setText("50");
                                break;
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        txvDose.setText(""); // atau bisa dibiarkan kosong
                    }
                });


                // Tombol close
                ImageView btnClose = view.findViewById(R.id.btnClose);
                btnClose.setOnClickListener(v1 -> dialog.dismiss());

                // Tombol gunakan rekomendasi
                Button btnUse = view.findViewById(R.id.btnUseRecommendation);
                btnUse.setOnClickListener(v2 -> {
                    edtInsulin.setText(txvDose.getText());
                    dialog.dismiss();
                });

                dialog.show();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strInsulin = edtInsulin.getText().toString();
                String note = edtNote.getText().toString();

                if (strInsulin.isEmpty()) {
                    edtInsulin.setError("Masukkan jumlah insulin");
                    return;
                }

                int insulin = Integer.parseInt(strInsulin);

                if (note.isEmpty()){
                    note = "(None)";
                }

                InsulinNotes newNote = new InsulinNotes(insulin, note, LocalDate.now(), LocalTime.now());

                ArrayList<InsulinNotes> existingNotes = InsulinNotesStorage.loadNotes(getApplicationContext());
                existingNotes.add(0, newNote); // Tambahkan di atas
                InsulinNotesStorage.saveNotes(getApplicationContext(), existingNotes);

                finish(); // Kembali ke HomeFragment

            }
        });

    }
}