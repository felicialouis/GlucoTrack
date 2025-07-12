package edu.uph.m23si3.glucotrack.ui.report;

import android.app.DatePickerDialog;
import android.content.Intent;
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

import java.util.Calendar;

import edu.uph.m23si3.glucotrack.NotificationActivity;
import edu.uph.m23si3.glucotrack.R;

public class ReportFragment extends Fragment {

    private ReportViewModel mViewModel;

    public static ReportFragment newInstance() {
        return new ReportFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_report, container, false);

        // Notifikasi
        ImageView imgNotification = view.findViewById(R.id.imgNotification);
        imgNotification.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), NotificationActivity.class);
            startActivity(intent);
        });

        // Profile
        ImageView profileImage = view.findViewById(R.id.profile);
        profileImage.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.nav_host_fragment_activity_main,
                            new edu.uph.m23si3.glucotrack.ui.profile.ProfileFragment())
                    .addToBackStack(null)
                    .commit();
        });

        // Kalender
        LinearLayout calendarContainer = view.findViewById(R.id.datePickerContainer);
        TextView tvSelectedDate = view.findViewById(R.id.tvSelectedDate);

        // Set default tanggal hari ini
        Calendar today = Calendar.getInstance();
        setTanggal(tvSelectedDate, today);

        // Saat klik date picker
        calendarContainer.setOnClickListener(v -> {
            int year = today.get(Calendar.YEAR);
            int month = today.get(Calendar.MONTH);
            int day = today.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                    (view1, selectedYear, selectedMonth, selectedDay) -> {
                        Calendar selectedDate = Calendar.getInstance();
                        selectedDate.set(selectedYear, selectedMonth, selectedDay);
                        setTanggal(tvSelectedDate, selectedDate);
                    }, year, month, day);
            datePickerDialog.show();
        });

        return view;
    }

    private void setTanggal(TextView textView, Calendar date) {
        String[] bulan = {"Januari", "Februari", "Maret", "April", "Mei", "Juni",
                "Juli", "Agustus", "September", "Oktober", "November", "Desember"};
        int day = date.get(Calendar.DAY_OF_MONTH);
        int month = date.get(Calendar.MONTH);
        int year = date.get(Calendar.YEAR);
        String tanggalFormatted = day + " " + bulan[month] + " " + year;
        textView.setText(tanggalFormatted);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ReportViewModel.class);
    }
}
