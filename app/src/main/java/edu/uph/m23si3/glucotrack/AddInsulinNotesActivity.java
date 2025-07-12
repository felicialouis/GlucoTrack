package edu.uph.m23si3.glucotrack;

import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import edu.uph.m23si3.glucotrack.Model.InsulinNotes;
import io.realm.Realm;

public class AddInsulinNotesActivity extends AppCompatActivity {

    private TextView txvSeeRecommendation;
    private String selectedDate;
    private Button btnSave;
    private EditText edtDose, edtNote;
    private int editingId = -1;
    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_insulin_notes);

        realm = Realm.getDefaultInstance();

        selectedDate = getIntent().getStringExtra("selected_date");

        if (selectedDate == null) {
            // fallback kalau null = hari ini
            selectedDate = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(new java.util.Date());
        }

        // View binding
        txvSeeRecommendation = findViewById(R.id.txvSeeRecommendation);
        btnSave = findViewById(R.id.btnSave);
        edtDose = findViewById(R.id.edtDose);
        edtNote = findViewById(R.id.edtNote);

        // Mode edit
        Intent intent = getIntent();
        if (intent.hasExtra("edit_id")) {
            editingId = intent.getIntExtra("edit_id", -1);
            InsulinNotes existing = realm.where(InsulinNotes.class)
                    .equalTo("id", editingId)
                    .findFirst();

            if (existing != null) {
                edtDose.setText(String.valueOf(existing.getDose()));
                edtNote.setText(existing.getNotes());
            }
        }

        txvSeeRecommendation.setOnClickListener(v -> showRecommendationDialog());

        btnSave.setOnClickListener(v -> {
            String doseStr = edtDose.getText().toString().trim();
            String note = edtNote.getText().toString().trim();

            if (doseStr.isEmpty()) {
                edtDose.setError("This field must be filled");
                return;
            }

            int dose = Integer.parseInt(doseStr);
            if (note.isEmpty()) note = "(None)";
            String email = getLoggedInUserEmail();

            if (email == null) {
                Toast.makeText(this, "User is not found", Toast.LENGTH_SHORT).show();
                return;
            }

            String time = getCurrentTimeString();
            saveInsulinNoteToRealm(email, dose, note, time);

            Toast.makeText(this, "Insulin Note is saved!", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private void showRecommendationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.see_recommendation_modal, null);
        builder.setView(view);
        AlertDialog dialog = builder.create();

        Spinner spinner = view.findViewById(R.id.spinnerCondition);
        TextView txvDose = view.findViewById(R.id.txvDose);
        ImageView btnClose = view.findViewById(R.id.btnClose);
        Button btnUse = view.findViewById(R.id.btnUseRecommendation);

        String[] items = {
                "Based on current condition",
                "Based on breakfast",
                "Based on lunch",
                "Based on dinner"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                switch (items[pos]) {
                    case "Based on current condition": txvDose.setText("20"); break;
                    case "Based on breakfast": txvDose.setText("30"); break;
                    case "Based on lunch": txvDose.setText("40"); break;
                    case "Based on dinner": txvDose.setText("50"); break;
                }
            }

            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        btnClose.setOnClickListener(v -> dialog.dismiss());
        btnUse.setOnClickListener(v -> {
            edtDose.setText(txvDose.getText().toString());
            dialog.dismiss();
        });

        dialog.show();
    }

    private void saveInsulinNoteToRealm(String userEmail, int dose, String notes, String time) {
        realm.executeTransactionAsync(r -> {
            long timestamp = System.currentTimeMillis();

            if (editingId != -1) {
                // Edit mode
                InsulinNotes existing = r.where(InsulinNotes.class)
                        .equalTo("id", editingId)
                        .findFirst();

                if (existing != null) {
                    existing.setDose(dose);
                    existing.setNotes(notes);
                    existing.setTime(time);
                    existing.setDate(selectedDate);
                    existing.setTimestamp(timestamp);
                    return;
                }
            }

            // Tambah baru
            Number currentId = r.where(InsulinNotes.class).max("id");
            int nextId = (currentId == null) ? 1 : currentId.intValue() + 1;

            InsulinNotes newNote = r.createObject(InsulinNotes.class, nextId);
            newNote.setUserEmail(userEmail);
            newNote.setDose(dose);
            newNote.setNotes(notes);
            newNote.setTime(time);
            newNote.setDate(selectedDate);
            newNote.setTimestamp(timestamp);
        }, () -> {
            finish();
        }, error -> {
            Toast.makeText(this, "Fail to save: " + error.getMessage(), Toast.LENGTH_LONG).show();
        });
    }

    private String getLoggedInUserEmail() {
        SharedPreferences prefs = getSharedPreferences("user_session", MODE_PRIVATE);
        return prefs.getString("userId", null);
    }

    private String getCurrentTimeString() {
        LocalTime now = LocalTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return now.format(formatter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (realm != null && !realm.isClosed()) {
            realm.close();
        }
    }
}
