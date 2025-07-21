package edu.uph.m23si3.glucotrack.ui.foods;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import edu.uph.m23si3.glucotrack.Model.GlucoseTrack;
import edu.uph.m23si3.glucotrack.NotificationActivity;
import edu.uph.m23si3.glucotrack.R;
import edu.uph.m23si3.glucotrack.RecommendationActivity;
import io.realm.Realm;

public class FoodsFragment extends Fragment {

    private ImageView btnViewHistory, imgNotification;
    private TextView todayText;
    private AutoCompleteTextView autoBreakfast, autoLunch, autoDinner, autoSnack;
    private TextView txtHasilBreakfast, txtHasilLunch, txtHasilDinner, txtHasilSnack, txtTotal;
    private Button btnSave, btnRecommendation;

    private HashMap<String, Integer> glucoseMap;
    private Realm realm;
    private String selectedDate;
    private ActivityResultLauncher<Intent> recommendationLauncher;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_foods, container, false);

        SharedPreferences session = requireContext().getSharedPreferences("user_session", Context.MODE_PRIVATE);
        String userEmail = session.getString("userId", null);
        if (userEmail == null) {
            redirectToLogin();
            return null;
        }

        realm = Realm.getDefaultInstance();

        btnViewHistory = view.findViewById(R.id.btnViewHistory);
        todayText = view.findViewById(R.id.todayText);
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
        imgNotification = view.findViewById(R.id.imgNotification);
        imgNotification.setOnClickListener(v -> toNotification());

        selectedDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        updateTodayText();

        glucoseMap = new HashMap<>();
        // Tambahan makanan dari RecommendationActivity
        glucoseMap.put("Roti gandum isi telur dan alpukat", 35);
        glucoseMap.put("Greek yogurt dengan chia seed", 10);
        glucoseMap.put("Nasi merah + ayam kukus + tumis bayam", 45);
        glucoseMap.put("Sup tahu dan sayur bening", 15);
        glucoseMap.put("Salad sayuran dengan ayam panggang (15g)", 15);
        glucoseMap.put("Sup kacang merah dengan sayuran (20g)", 20);
        glucoseMap.put("Ikan panggang dengan brokoli (30g)", 30);
        glucoseMap.put("Omelet sayuran dengan keju rendah lemak (5g)", 5);
        glucoseMap.put("Greek yogurt dengan buah dan madu (25g)", 25);
        glucoseMap.put("Ayam panggang dengan kacang hijau (12g)", 12);
        glucoseMap.put("Tahu kukus dengan sayuran (10g)", 10);
        glucoseMap.put("Smoothie bayam dan alpukat (18g)", 18);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, new ArrayList<>(glucoseMap.keySet()));
        setupAutoComplete(autoBreakfast, adapter);
        setupAutoComplete(autoLunch, adapter);
        setupAutoComplete(autoDinner, adapter);
        setupAutoComplete(autoSnack, adapter);

        btnViewHistory.setOnClickListener(v -> showDatePicker());
        btnSave.setOnClickListener(v -> saveToRealm());

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

        btnRecommendation.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), RecommendationActivity.class);
            recommendationLauncher.launch(intent);
        });

        return view;
    }

    private void updateTodayText() {
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        todayText.setText(selectedDate.equals(today) ? "Today" : selectedDate);
    }

    private void showDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(getContext(), (view, year, month, dayOfMonth) -> {
            selectedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth);
            updateTodayText();
            loadGlucoseDataForDate(selectedDate);
        },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void setupAutoComplete(AutoCompleteTextView view, ArrayAdapter<String> adapter) {
        view.setAdapter(adapter);
        view.setThreshold(0);
        view.setOnClickListener(v -> view.showDropDown());
    }

    private void saveToRealm() {
        String breakfast = autoBreakfast.getText().toString().trim();
        String lunch = autoLunch.getText().toString().trim();
        String dinner = autoDinner.getText().toString().trim();
        String snack = autoSnack.getText().toString().trim();

        int total = getGlucose(autoBreakfast, txtHasilBreakfast)
                + getGlucose(autoLunch, txtHasilLunch)
                + getGlucose(autoDinner, txtHasilDinner)
                + getGlucose(autoSnack, txtHasilSnack);

        txtTotal.setText("Glukosa total: " + total);

        realm.executeTransaction(r -> {
            GlucoseTrack existing = r.where(GlucoseTrack.class)
                    .equalTo("date", selectedDate)
                    .findFirst();

            if (existing != null) {
                existing.setTotalGlucose(total);
                existing.setBreakfast(breakfast);
                existing.setLunch(lunch);
                existing.setDinner(dinner);
                existing.setSnack(snack);
            } else {
                GlucoseTrack newTrack = r.createObject(GlucoseTrack.class, selectedDate);
                newTrack.setTotalGlucose(total);
                newTrack.setBreakfast(breakfast);
                newTrack.setLunch(lunch);
                newTrack.setDinner(dinner);
                newTrack.setSnack(snack);
            }
        });

        Toast.makeText(getContext(), "Data glukosa berhasil disimpan untuk " + selectedDate, Toast.LENGTH_SHORT).show();
    }

    private int getGlucose(AutoCompleteTextView input, TextView resultView) {
        String food = input.getText().toString().trim();
        int glu = glucoseMap.getOrDefault(food, 0);
        resultView.setText("Glukosa: " + glu);
        return glu;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (realm != null && !realm.isClosed()) {
            realm.close();
        }
    }

    private void loadGlucoseDataForDate(String date) {
        GlucoseTrack track = realm.where(GlucoseTrack.class)
                .equalTo("date", date)
                .findFirst();

        if (track != null) {
            autoBreakfast.setText(track.getBreakfast());
            autoLunch.setText(track.getLunch());
            autoDinner.setText(track.getDinner());
            autoSnack.setText(track.getSnack());

            txtHasilBreakfast.setText("Glukosa: " + glucoseMap.getOrDefault(track.getBreakfast(), 0));
            txtHasilLunch.setText("Glukosa: " + glucoseMap.getOrDefault(track.getLunch(), 0));
            txtHasilDinner.setText("Glukosa: " + glucoseMap.getOrDefault(track.getDinner(), 0));
            txtHasilSnack.setText("Glukosa: " + glucoseMap.getOrDefault(track.getSnack(), 0));

            txtTotal.setText("Glukosa total: " + track.getTotalGlucose());
        } else {
            autoBreakfast.setText("");
            autoLunch.setText("");
            autoDinner.setText("");
            autoSnack.setText("");

            txtHasilBreakfast.setText("Glukosa: 0");
            txtHasilLunch.setText("Glukosa: 0");
            txtHasilDinner.setText("Glukosa: 0");
            txtHasilSnack.setText("Glukosa: 0");

            txtTotal.setText("Glukosa total: 0");
        }
    }

    private void redirectToLogin() {
        Intent intent = new Intent(requireContext(), edu.uph.m23si3.glucotrack.LoginActivity.class);
        startActivity(intent);
        requireActivity().finish();
    }

    private void toNotification() {
        Intent intent = new Intent(getContext(), NotificationActivity.class);
        startActivity(intent);
    }

    private void updateGlucoseDisplay() {
        int breakfastGlu = getGlucose(autoBreakfast, txtHasilBreakfast);
        int lunchGlu = getGlucose(autoLunch, txtHasilLunch);
        int dinnerGlu = getGlucose(autoDinner, txtHasilDinner);
        int snackGlu = getGlucose(autoSnack, txtHasilSnack);

        int total = breakfastGlu + lunchGlu + dinnerGlu + snackGlu;
        txtTotal.setText("Glukosa total: " + total);
    }
}
