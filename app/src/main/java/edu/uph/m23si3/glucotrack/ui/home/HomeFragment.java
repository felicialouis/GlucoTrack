package edu.uph.m23si3.glucotrack.ui.home;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import edu.uph.m23si3.glucotrack.Adapter.InsulinNotesAdapter;
import edu.uph.m23si3.glucotrack.AddInsulinNotesActivity;
import edu.uph.m23si3.glucotrack.Model.InsulinNotes;
import edu.uph.m23si3.glucotrack.NotificationActivity;
import edu.uph.m23si3.glucotrack.databinding.FragmentHomeBinding;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    private ImageView imgNotification, btnViewHistory;
    private Button btnPump;
    private RecyclerView rvInsulinNotes;
    private InsulinNotesAdapter adapter;
    private TextView txvTanggalInsulin;
    private String tanggalYangSedangDilihat;
    private Realm realm;
    private RealmResults<InsulinNotes> insulinNotesList;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        HomeViewModel homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Cek login session
        String userEmail = getLoggedInUserEmail();
        if (userEmail == null) {
            redirectToLogin();
            return null;
        }

        realm = Realm.getDefaultInstance();

        imgNotification = binding.imgNotification;
        imgNotification.setOnClickListener(v -> toNotification());

        btnPump = binding.btnPump;
        btnPump.setOnClickListener(v -> toAddInsulinNotes());

        btnViewHistory = binding.btnViewHistory;
        btnViewHistory.setOnClickListener(v -> showDatePicker());

        txvTanggalInsulin = binding.txvTanggalInsulin;
        String todayDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        tanggalYangSedangDilihat = todayDate;
        updateTanggalInsulinText(todayDate); // <- gunakan pengecekan today

        rvInsulinNotes = binding.rvInsulinNotes;
        rvInsulinNotes.setLayoutManager(new LinearLayoutManager(getContext()));

        // Load data dari Realm
        insulinNotesList = realm.where(InsulinNotes.class)
                .equalTo("userEmail", userEmail)
                .sort("timestamp", Sort.DESCENDING)
                .findAll();

        adapter = new InsulinNotesAdapter(insulinNotesList, getContext());
        rvInsulinNotes.setAdapter(adapter);

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (realm != null && !realm.isClosed()) {
            realm.close();
        }
        binding = null;
    }

    private void toAddInsulinNotes() {
        Intent intent = new Intent(getContext(), AddInsulinNotesActivity.class);
        intent.putExtra("selected_date", tanggalYangSedangDilihat); // Kirim tanggal yang sedang dilihat
        startActivity(intent);
    }

    private void toNotification() {
        Intent intent = new Intent(getContext(), NotificationActivity.class);
        startActivity(intent);
    }

    private String getLoggedInUserEmail() {
        SharedPreferences prefs = requireContext().getSharedPreferences("user_session", getContext().MODE_PRIVATE);
        return prefs.getString("userId", null);
    }

    private void showDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(requireContext(), (view, selectedYear, selectedMonth, selectedDay) -> {
            String selectedDate = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay);
            loadInsulinNotesForDate(selectedDate);
        }, year, month, day);

        dialog.show();
    }

    private void loadInsulinNotesForDate(String date) {
        tanggalYangSedangDilihat = date;
        updateTanggalInsulinText(date); // <- gunakan method baru

        RealmResults<InsulinNotes> filteredNotes = realm.where(InsulinNotes.class)
                .equalTo("userEmail", getLoggedInUserEmail())
                .equalTo("date", date)
                .sort("timestamp", Sort.DESCENDING)
                .findAll();

        adapter.updateData(filteredNotes);
    }

    private void updateTanggalInsulinText(String date) {
        Calendar calNow = Calendar.getInstance();
        int yearNow = calNow.get(Calendar.YEAR);
        int monthNow = calNow.get(Calendar.MONTH);
        int dayNow = calNow.get(Calendar.DAY_OF_MONTH);

        String[] parts = date.split("-");
        int yearPicked = Integer.parseInt(parts[0]);
        int monthPicked = Integer.parseInt(parts[1]) - 1; // Calendar bulan dimulai dari 0
        int dayPicked = Integer.parseInt(parts[2]);

        if (yearNow == yearPicked && monthNow == monthPicked && dayNow == dayPicked) {
            txvTanggalInsulin.setText("Today");
        } else {
            txvTanggalInsulin.setText("Showing " + date + " Insulin Notes");
        }
    }

    private void redirectToLogin() {
        Intent intent = new Intent(requireContext(), edu.uph.m23si3.glucotrack.LoginActivity.class);
        startActivity(intent);
        requireActivity().finish(); // supaya tidak bisa kembali ke Home setelah redirect
    }
}
