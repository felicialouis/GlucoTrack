package edu.uph.m23si3.glucotrack.ui.foods;

import android.annotation.SuppressLint;
import android.app.Activity;
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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

import edu.uph.m23si3.glucotrack.CalenderActivity;
import edu.uph.m23si3.glucotrack.R;
import edu.uph.m23si3.glucotrack.RecommendationActivity;

public class FoodsFragment extends Fragment {

    private static final int REQUEST_CODE_RECOMMENDATION = 100;

    private ImageView imgNotification, tombolkalender;
    private EditText edtBreakfast, edtLunch, edtDinner, edtSnack;
    private TextView txtHasilBreakfast, txtHasilLunch, txtHasilDinner, txtHasilSnack, txtTotal;
    private Button btnSave, btnRecommendation;
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
        btnRecommendation = view.findViewById(R.id.btnRecommendation);

        // Glukosa Map
        glucoseMap = new HashMap<>();
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

        // Tombol Recommendation: buka halaman rekomendasi
        btnRecommendation.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), RecommendationActivity.class);
            startActivityForResult(intent, REQUEST_CODE_RECOMMENDATION);
        });

        return view;
    }

    // Menerima hasil dari RecommendationActivity
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_RECOMMENDATION && resultCode == Activity.RESULT_OK && data != null) {
            String breakfast = data.getStringExtra("breakfast");
            String lunch = data.getStringExtra("lunch");
            String dinner = data.getStringExtra("dinner");
            String snack = data.getStringExtra("snack");

            edtBreakfast.setText(breakfast);
            edtLunch.setText(lunch);
            edtDinner.setText(dinner);
            edtSnack.setText(snack);

            updateGlucoseDisplay();
        }
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
