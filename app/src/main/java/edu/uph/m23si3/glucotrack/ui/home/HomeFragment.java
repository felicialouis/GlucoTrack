package edu.uph.m23si3.glucotrack.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import edu.uph.m23si3.glucotrack.Adapter.InsulinNotesAdapter;
import edu.uph.m23si3.glucotrack.AddInsulinNotesActivity;
import edu.uph.m23si3.glucotrack.Model.InsulinNotes;
import edu.uph.m23si3.glucotrack.NotificationActivity;
import edu.uph.m23si3.glucotrack.Utils.InsulinNotesStorage;
import edu.uph.m23si3.glucotrack.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    private ImageView imgNotification;
    private Button btnPump;
    private ArrayList<InsulinNotes> insulinNotesArrayList;
    private RecyclerView rvInsulinNotes;
    private InsulinNotesAdapter adapter;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        HomeViewModel homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        imgNotification = binding.imgNotification;
        imgNotification.setOnClickListener(v -> toNotification());

        btnPump = binding.btnPump;
        btnPump.setOnClickListener(v -> toAddInsulinNotes());

        rvInsulinNotes = binding.rvInsulinNotes;
        rvInsulinNotes.setLayoutManager(new LinearLayoutManager(getContext()));

        // Load data dari SharedPreferences
        insulinNotesArrayList = InsulinNotesStorage.loadNotes(getContext());

        // Inisialisasi adapter
        adapter = new InsulinNotesAdapter(insulinNotesArrayList, getContext());
        rvInsulinNotes.setAdapter(adapter);

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();

        // Reload data dari SharedPreferences setiap kali kembali ke fragment ini
        insulinNotesArrayList.clear();
        insulinNotesArrayList.addAll(InsulinNotesStorage.loadNotes(getContext()));
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void toAddInsulinNotes() {
        Intent intent = new Intent(getContext(), AddInsulinNotesActivity.class);
        startActivity(intent);
    }

    private void toNotification() {
        Intent intent = new Intent(getContext(), NotificationActivity.class);
        startActivity(intent);
    }
}
