package edu.uph.m23si3.glucotrack;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import edu.uph.m23si3.glucotrack.Model.GlucoseTrack;
import io.realm.Realm;

public class CalenderActivity extends AppCompatActivity {

    EditText editDay, editMonth, editYear;
    Button btnTrack;
    TextView txtTotalKarbo;

    Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calender);

        // Ambil default instance dari Realm (sudah diatur di MyApplication)
        realm = Realm.getDefaultInstance();

        editDay = findViewById(R.id.editDay);
        editMonth = findViewById(R.id.editMonth);
        editYear = findViewById(R.id.editYear);
        btnTrack = findViewById(R.id.btnTrack);
        txtTotalKarbo = findViewById(R.id.txtTotalKarbo); // textview di bawah button

        // Dapatkan totalGlucose dari intent
        int totalGlucose = getIntent().getIntExtra("totalGlucose", 0);

        if (totalGlucose > 0) {
            Toast.makeText(this, "Data diterima dari sebelumnya: " + totalGlucose, Toast.LENGTH_SHORT).show();
        }

        btnTrack.setOnClickListener(v -> {
            String day = editDay.getText().toString().trim();
            String month = editMonth.getText().toString().trim();
            String year = editYear.getText().toString().trim();

            if (day.isEmpty() || month.isEmpty() || year.isEmpty()) {
                Toast.makeText(this, "Lengkapi tanggal!", Toast.LENGTH_SHORT).show();
                return;
            }

            String tanggal = day + "-" + month + "-" + year;

            if (totalGlucose > 0) {
                saveGlucoseToRealm(tanggal, totalGlucose);
            }

            // Ambil data berdasarkan tanggal
            GlucoseTrack data = realm.where(GlucoseTrack.class)
                    .equalTo("date", tanggal)
                    .findFirst();

            if (data != null) {
                txtTotalKarbo.setText("Total Glukosa pada " + tanggal + ": " + data.getTotalGlucose());
            } else {
                txtTotalKarbo.setText("Tidak ada data untuk tanggal ini.");
            }
        });
    }

    private void saveGlucoseToRealm(String date, int glucose) {
        realm.executeTransaction(r -> {
            GlucoseTrack record = realm.createObject(GlucoseTrack.class, date);
            record.setTotalGlucose(glucose);
        });
        Toast.makeText(this, "Data disimpan ke Realm!", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (realm != null && !realm.isClosed()) {
            realm.close();
        }
    }
}
