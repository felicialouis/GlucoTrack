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
import java.util.HashMap;

import edu.uph.m23si3.glucotrack.Model.GlucoseTrack;
import edu.uph.m23si3.glucotrack.Model.InsulinNotes;
import edu.uph.m23si3.glucotrack.Model.UserProfile;
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
        String userEmail = getLoggedInUserEmail();
        UserProfile profile = realm.where(UserProfile.class)
                .equalTo("email", userEmail)
                .findFirst();

        // Cek jika weight atau target belum diisi
        if (profile == null || profile.getWeight() == null || profile.getWeight() <= 0 || profile.getTarget() == null || profile.getTarget() <= 0) {
            Toast.makeText(this, "Belum bisa menggunakan fitur, lengkapi profil anda!", Toast.LENGTH_LONG).show();
            return;
        }

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
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                String condition = items[pos];

                // Ambil data profil
                int weight = profile.getWeight();
                int target = profile.getTarget();

                // Ambil glukosa dari GlucoseTrack
                GlucoseTrack track = realm.where(GlucoseTrack.class)
                        .equalTo("date", selectedDate)
                        .findFirst();

                if (track == null) {
                    txvDose.setText("0");
                    Toast.makeText(AddInsulinNotesActivity.this, "Belum ada data makanan pada tanggal ini.", Toast.LENGTH_SHORT).show();
                }

                int glucose = 0;
                if (track != null) {
                    switch (condition) {
                        case "Based on current condition":
                            glucose = 0;
                            break;
                        case "Based on breakfast":
                            if (track.getBreakfast() == null || track.getBreakfast().isEmpty()) {
                                Toast.makeText(AddInsulinNotesActivity.this, "You haven't input breakfast.", Toast.LENGTH_SHORT).show();
                                txvDose.setText("0");
                                return;
                            }
                            glucose = getGlucoseValue(track.getBreakfast());
                            break;
                        case "Based on lunch":
                            if (track.getLunch() == null || track.getLunch().isEmpty()) {
                                Toast.makeText(AddInsulinNotesActivity.this, "You haven't input lunch.", Toast.LENGTH_SHORT).show();
                                txvDose.setText("0");
                                return;
                            }
                            glucose = getGlucoseValue(track.getLunch());
                            break;
                        case "Based on dinner":
                            if (track.getDinner() == null || track.getDinner().isEmpty()) {
                                Toast.makeText(AddInsulinNotesActivity.this, "You haven't input dinner.", Toast.LENGTH_SHORT).show();
                                txvDose.setText("0");
                                return;
                            }
                            glucose = getGlucoseValue(track.getDinner());
                            break;
                    }
                }

                // Hitung dosis
                double carbRatio = 500.0 / (weight * 0.5);
                double correctionFactor = 1800.0 / (weight * 0.5);
                double dose = (glucose / carbRatio) + ((248 - target) / correctionFactor);
                int roundedDose = (int) Math.round(dose);

                txvDose.setText(String.valueOf(roundedDose));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
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

    private int getGlucoseValue(String foodName) {
        if (foodName == null) return 0;

        // Mapping makanan dan nilai glukosa – kamu bisa pindahkan ke tempat shared kalau perlu
        HashMap<String, Integer> glucoseMap = new HashMap<>();
        glucoseMap.put("Nasi", 60);
        glucoseMap.put("Roti", 40);
        glucoseMap.put("Sayur", 0);
        glucoseMap.put("Telur", 0);
        glucoseMap.put("Apel", 20);
        glucoseMap.put("Oatmeal", 50);
        glucoseMap.put("Greek yogurt", 10);
        glucoseMap.put("Nasi merah", 45);
        glucoseMap.put("Sup tahu", 15);
        glucoseMap.put("Smoothie", 25);
        glucoseMap.put("Edamame", 5);
        glucoseMap.put("Quinoa salad", 30);
        glucoseMap.put("Sapo tahu", 20);
        glucoseMap.put("Ubi", 35);
        glucoseMap.put("Almond", 5);
        glucoseMap.put("Capcay", 15);

        return glucoseMap.getOrDefault(foodName, 0);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (realm != null && !realm.isClosed()) {
            realm.close();
        }
    }
}
