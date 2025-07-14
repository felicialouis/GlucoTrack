package edu.uph.m23si3.glucotrack.ui.report;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import edu.uph.m23si3.glucotrack.Model.GlucoseTrack; // ✅ Tambahan
import edu.uph.m23si3.glucotrack.Model.InsulinNotes;
import edu.uph.m23si3.glucotrack.NotificationActivity;
import edu.uph.m23si3.glucotrack.R;
import io.realm.Realm;
import io.realm.RealmResults;

public class ReportFragment extends Fragment {

    private ReportViewModel mViewModel;
    private Realm realm;
    private TextView tvAvgInsulin, tvSelectedDate;
    private TextView tvAvgSugar;
    private TextView tvStartDate, tvEndDate;
    private TextView todayText;
    private Calendar startDate, endDate;
    private Calendar selectedDate;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_report, container, false);

        realm = Realm.getDefaultInstance();
        tvAvgInsulin = view.findViewById(R.id.tvAvgInsulin);
        tvSelectedDate = view.findViewById(R.id.tvSelectedDate);
        tvAvgSugar = view.findViewById(R.id.tvAvgSugar);
        tvStartDate = view.findViewById(R.id.tvStartDate);
        tvEndDate = view.findViewById(R.id.tvEndDate);
        todayText = view.findViewById(R.id.todayText);


        startDate = Calendar.getInstance();
        endDate = Calendar.getInstance();
        startDate.add(Calendar.DAY_OF_MONTH, -6);
        updateRangeTextViews();

        tvStartDate.setOnClickListener(v -> {
            showDatePicker(startDate, selected -> {
                startDate = selected;
                updateRangeTextViews();
            });
        });

        tvEndDate.setOnClickListener(v -> {
            showDatePicker(endDate, selected -> {
                endDate = selected;
                updateRangeTextViews();
            });
        });

        ImageView imgNotification = view.findViewById(R.id.imgNotification);
        imgNotification.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), NotificationActivity.class);
            startActivity(intent);
        });

        ImageView profileImage = view.findViewById(R.id.profile);
        profileImage.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.nav_host_fragment_activity_main,
                            new edu.uph.m23si3.glucotrack.ui.profile.ProfileFragment())
                    .addToBackStack(null)
                    .commit();
        });

        LinearLayout calendarContainer = view.findViewById(R.id.datePickerContainer);
        Calendar today = Calendar.getInstance();

        setTanggal(today);
        loadAvgInsulinByDate(today);
        loadAvgSugarByDate(today);

        calendarContainer.setOnClickListener(v -> {
            int year = today.get(Calendar.YEAR);
            int month = today.get(Calendar.MONTH);
            int day = today.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                    (view1, selectedYear, selectedMonth, selectedDay) -> {
                        Calendar selectedDate = Calendar.getInstance();
                        selectedDate.set(selectedYear, selectedMonth, selectedDay);
                        setTanggal(selectedDate);
                        loadAvgInsulinByDate(selectedDate);
                        loadAvgSugarByDate(selectedDate);
                    }, year, month, day);
            datePickerDialog.show();
        });

        return view;
    }
    private void setTanggal(Calendar date) {
        selectedDate = date;

        String[] bulan = {"Januari", "Februari", "Maret", "April", "Mei", "Juni",
                "Juli", "Agustus", "September", "Oktober", "November", "Desember"};
        int day = date.get(Calendar.DAY_OF_MONTH);
        int month = date.get(Calendar.MONTH);
        int year = date.get(Calendar.YEAR);
        String tanggalFormatted = day + " " + bulan[month] + " " + year;
        tvSelectedDate.setText(tanggalFormatted);

        // Set text "Today" atau tanggal sesuai pilihan
        Calendar today = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String selectedDateStr = sdf.format(date.getTime());
        String todayStr = sdf.format(today.getTime());

        if (selectedDateStr.equals(todayStr)) {
            todayText.setText("Today");
        } else {
            todayText.setText(tanggalFormatted);
        }
    }

    private void loadAvgInsulinByDate(Calendar date) {
        String userEmail = getLoggedInUserEmail();
        if (userEmail == null) {
            tvAvgInsulin.setText("-");
            return;
        }

        String selectedDateStr = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date.getTime());

        RealmResults<InsulinNotes> notes = realm.where(InsulinNotes.class)
                .equalTo("userEmail", userEmail)
                .equalTo("date", selectedDateStr)
                .findAll();

        if (notes.isEmpty()) {
            tvAvgInsulin.setText("0");
            return;
        }

        double total = 0;
        for (InsulinNotes note : notes) {
            total += note.getDose();
        }

        tvAvgInsulin.setText(String.format(Locale.getDefault(), "%.1f", total));
    }


    private void loadAvgSugarByDate(Calendar date) {
        String selectedDateStr = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date.getTime());

        GlucoseTrack track = realm.where(GlucoseTrack.class)
                .equalTo("date", selectedDateStr)
                .findFirst();

        if (track == null) {
            tvAvgSugar.setText("0");
            return;
        }

        int totalGlucose = track.getTotalGlucose();
        tvAvgSugar.setText(String.valueOf(totalGlucose));
    }


    private void updateRangeTextViews() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        tvStartDate.setText(sdf.format(startDate.getTime()));
        tvEndDate.setText(sdf.format(endDate.getTime()));
    }

    private interface DateSelectedCallback {
        void onDateSelected(Calendar date);
    }

    private void showDatePicker(Calendar initialDate, DateSelectedCallback callback) {
        int year = initialDate.get(Calendar.YEAR);
        int month = initialDate.get(Calendar.MONTH);
        int day = initialDate.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(requireContext(),
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    Calendar selected = Calendar.getInstance();
                    selected.set(selectedYear, selectedMonth, selectedDay);
                    callback.onDateSelected(selected);
                }, year, month, day);
        dialog.show();
    }

    private String getLoggedInUserEmail() {
        SharedPreferences prefs = requireContext().getSharedPreferences("user_session", getContext().MODE_PRIVATE);
        return prefs.getString("userId", null);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (realm != null && !realm.isClosed()) {
            realm.close();
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ReportViewModel.class);
    }
}
