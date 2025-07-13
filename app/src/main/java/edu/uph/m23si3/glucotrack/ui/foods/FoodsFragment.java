package edu.uph.m23si3.glucotrack.ui.foods;

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
import io.realm.Realm;
import io.realm.RealmResults;

public class FoodsFragment extends Fragment {

    private ImageView btnViewHistory, imgNotification;
    private TextView todayText;
    private AutoCompleteTextView autoBreakfast, autoLunch, autoDinner, autoSnack;
    private TextView txtHasilBreakfast, txtHasilLunch, txtHasilDinner, txtHasilSnack, txtTotal;
    private Button btnSave, btnRecommendation;

    private HashMap<String, Integer> glucoseMap;
    private Realm realm;
    private String selectedDate;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_foods, container, false);

        // Cek login session
        SharedPreferences session = requireContext().getSharedPreferences("user_session", Context.MODE_PRIVATE);
        String userEmail = session.getString("userId", null);
        if (userEmail == null) {
            redirectToLogin();
            return null;
        }

        // Init Realm
        realm = Realm.getDefaultInstance();

        // Bind views
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

        // Default selectedDate = today
        selectedDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        updateTodayText();

        // Glucose Map
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

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, new ArrayList<>(glucoseMap.keySet()));
        setupAutoComplete(autoBreakfast, adapter);
        setupAutoComplete(autoLunch, adapter);
        setupAutoComplete(autoDinner, adapter);
        setupAutoComplete(autoSnack, adapter);

        // Kalender
        btnViewHistory.setOnClickListener(v -> showDatePicker());

        // Save button
        btnSave.setOnClickListener(v -> saveToRealm());

        // Recommendation
        btnRecommendation.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Fitur rekomendasi belum diterapkan di versi ini", Toast.LENGTH_SHORT).show();
        });

        return view;
    }

    private void updateTodayText() {
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        if (selectedDate.equals(today)) {
            todayText.setText("Today");
        } else {
            todayText.setText(selectedDate);
        }
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

        txtTotal.setText("Glucose Total: " + total);

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

        Toast.makeText(getContext(), "Glucose saved for " + selectedDate, Toast.LENGTH_SHORT).show();
    }

    private int getGlucose(AutoCompleteTextView input, TextView resultView) {
        String food = input.getText().toString().trim();
        int glu = glucoseMap.containsKey(food) ? glucoseMap.get(food) : 0;
        resultView.setText("Glucose: " + glu);
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

            txtHasilBreakfast.setText("Glucose: " + glucoseMap.getOrDefault(track.getBreakfast(), 0));
            txtHasilLunch.setText("Glucose: " + glucoseMap.getOrDefault(track.getLunch(), 0));
            txtHasilDinner.setText("Glucose: " + glucoseMap.getOrDefault(track.getDinner(), 0));
            txtHasilSnack.setText("Glucose: " + glucoseMap.getOrDefault(track.getSnack(), 0));

            txtTotal.setText("Glucose total: " + track.getTotalGlucose());
        } else {
            autoBreakfast.setText("");
            autoLunch.setText("");
            autoDinner.setText("");
            autoSnack.setText("");

            txtHasilBreakfast.setText("Glucose: 0");
            txtHasilLunch.setText("Glucose: 0");
            txtHasilDinner.setText("Glucose: 0");
            txtHasilSnack.setText("Glucose: 0");

            txtTotal.setText("Glucose Total: 0");
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
}