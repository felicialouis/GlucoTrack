package edu.uph.m23si3.glucotrack.ui.foods;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

import edu.uph.m23si3.glucotrack.CalenderActivity;
import edu.uph.m23si3.glucotrack.NotificationActivity;
import edu.uph.m23si3.glucotrack.R;

public class FoodsFragment extends Fragment {

    private ImageView imgNotification, tombolkalender;
    private EditText edtBreakfast, edtLunch, edtDinner, edtSnack;
    private TextView txtHasilBreakfast, txtHasilLunch, txtHasilDinner, txtHasilSnack, txtTotal;
    private Button btnSave;

    private HashMap<String, Integer> glucoseMap;

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_foods, container, false);

        // Inisialisasi View
        imgNotification = view.findViewById(R.id.imgNotification);
        tombolkalender = view.findViewById(R.id.tombolkalender);

        edtBreakfast = view.findViewById(R.id.edtBreakfast);
        edtLunch = view.findViewById(R.id.edtLunch);
        edtDinner = view.findViewById(R.id.edtDinner);
        edtSnack = view.findViewById(R.id.edtSnack);

        txtHasilBreakfast = view.findViewById(R.id.txtHasilBreakfast);
        txtHasilLunch = view.findViewById(R.id.txtHasilLunch);
        txtHasilDinner = view.findViewById(R.id.txtHasilDinner);
        txtHasilSnack = view.findViewById(R.id.txtHasilSnack);
        txtTotal = view.findViewById(R.id.txtTotal);

        btnSave = view.findViewById(R.id.btnSave);

        // Glukosa Map
        glucoseMap = new HashMap<>();
        glucoseMap.put("Nasi", 60);
        glucoseMap.put("Roti", 40);
        glucoseMap.put("Sayur", 0);
        glucoseMap.put("Telur", 0);
        glucoseMap.put("Apel", 20);

        // Tombol Simpan & Hitung Glukosa
        btnSave.setOnClickListener(v -> updateGlucoseDisplay());

        // Tombol Kalender: Kirim total glukosa ke CalenderActivity
        tombolkalender.setOnClickListener(v -> {
            int totalGlucose = getGlucoseFromInput(edtBreakfast, txtHasilBreakfast, "Breakfast")
                    + getGlucoseFromInput(edtLunch, txtHasilLunch, "Lunch")
                    + getGlucoseFromInput(edtDinner, txtHasilDinner, "Dinner")
                    + getGlucoseFromInput(edtSnack, txtHasilSnack, "Snack");

            Intent intent = new Intent(getContext(), CalenderActivity.class);
            intent.putExtra("totalGlucose", totalGlucose);
            startActivity(intent);
        });

        return view;
    }

    private void updateGlucoseDisplay() {
        int breakfastGlu = getGlucoseFromInput(edtBreakfast, txtHasilBreakfast, "Breakfast");
        int lunchGlu = getGlucoseFromInput(edtLunch, txtHasilLunch, "Lunch");
        int dinnerGlu = getGlucoseFromInput(edtDinner, txtHasilDinner, "Dinner");
        int snackGlu = getGlucoseFromInput(edtSnack, txtHasilSnack, "Snack");

        int total = breakfastGlu + lunchGlu + dinnerGlu + snackGlu;
        txtTotal.setText("Total Glukosa: " + total);
    }

    private int getGlucoseFromInput(EditText editText, TextView resultView, String mealType) {
        String food = editText.getText().toString().trim();
        if (glucoseMap.containsKey(food)) {
            int glu = glucoseMap.get(food);
            resultView.setText("Glukosa: " + glu);
            return glu;
        } else {
            resultView.setText("Glukosa: 0");
            Toast.makeText(getContext(), mealType + ": Makanan tidak dikenal", Toast.LENGTH_SHORT).show();
            return 0;
        }
    }
}
