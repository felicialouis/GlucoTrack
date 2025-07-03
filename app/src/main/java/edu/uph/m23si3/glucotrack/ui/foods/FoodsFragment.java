package edu.uph.m23si3.glucotrack.ui.foods;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import edu.uph.m23si3.glucotrack.CalenderActivity;
import edu.uph.m23si3.glucotrack.R;
import edu.uph.m23si3.glucotrack.RecommendationActivity;

public class FoodsFragment extends Fragment {

    private ImageView imgNotification, tombolkalender;
    private AutoCompleteTextView autoBreakfast, autoLunch, autoDinner, autoSnack;
    private TextView txtHasilBreakfast, txtHasilLunch, txtHasilDinner, txtHasilSnack, txtTotal;
    private Button btnSave, btnRecommendation;
    private HashMap<String, Integer> glucoseMap;

    private ActivityResultLauncher<Intent> recommendationLauncher;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Activity Result untuk menerima hasil rekomendasi
        recommendationLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Intent data = result.getData();
                        String breakfast = data.getStringExtra("breakfast");
                        String lunch = data.getStringExtra("lunch");
                        String dinner = data.getStringExtra("dinner");
                        String snack = data.getStringExtra("snack");

                        autoBreakfast.setText(breakfast);
                        autoLunch.setText(lunch);
                        autoDinner.setText(dinner);
                        autoSnack.setText(snack);

                        updateGlucoseDisplay();
                    }
                }
        );
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_foods, container, false);

        imgNotification = view.findViewById(R.id.imgNotification);
        tombolkalender = view.findViewById(R.id.tombolkalender);

        autoBreakfast = view.findViewById(R.id.autoBreakfast);
        autoLunch = view.findViewById(R.id.autoLunch);
        autoDinner = view.findViewById(R.id.autoDinner);
        autoSnack = view.findViewById(R.id.autoSnack);

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

        // Set adapter untuk semua AutoCompleteTextView
        ArrayList<String> foodList = new ArrayList<>(glucoseMap.keySet());
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, foodList);

        setupAutoComplete(autoBreakfast, adapter);
        setupAutoComplete(autoLunch, adapter);
        setupAutoComplete(autoDinner, adapter);
        setupAutoComplete(autoSnack, adapter);

        // Tombol Simpan
        btnSave.setOnClickListener(v -> updateGlucoseDisplay());

        // Tombol Kalender
        tombolkalender.setOnClickListener(v -> {
            int totalGlucose = getGlucose(autoBreakfast, txtHasilBreakfast)
                    + getGlucose(autoLunch, txtHasilLunch)
                    + getGlucose(autoDinner, txtHasilDinner)
                    + getGlucose(autoSnack, txtHasilSnack);

            Intent intent = new Intent(getContext(), CalenderActivity.class);
            intent.putExtra("totalGlucose", totalGlucose);
            startActivity(intent);
        });

        // Tombol Rekomendasi
        btnRecommendation.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), RecommendationActivity.class);
            recommendationLauncher.launch(intent);
        });

        return view;
    }

    private void setupAutoComplete(AutoCompleteTextView view, ArrayAdapter<String> adapter) {
        view.setAdapter(adapter);
        view.setThreshold(0); // Tampilkan dropdown tanpa harus mengetik
        view.setOnClickListener(v -> view.showDropDown());
    }

    private void updateGlucoseDisplay() {
        int breakfastGlu = getGlucose(autoBreakfast, txtHasilBreakfast);
        int lunchGlu = getGlucose(autoLunch, txtHasilLunch);
        int dinnerGlu = getGlucose(autoDinner, txtHasilDinner);
        int snackGlu = getGlucose(autoSnack, txtHasilSnack);

        int total = breakfastGlu + lunchGlu + dinnerGlu + snackGlu;
        txtTotal.setText("Total Glukosa: " + total);
    }

    private int getGlucose(AutoCompleteTextView input, TextView resultView) {
        String food = input.getText().toString().trim();
        int glu = glucoseMap.containsKey(food) ? glucoseMap.get(food) : 0;
        resultView.setText("Glukosa: " + glu);
        return glu;
    }
}
