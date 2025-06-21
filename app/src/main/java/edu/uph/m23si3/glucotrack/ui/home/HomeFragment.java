package edu.uph.m23si3.glucotrack.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

import edu.uph.m23si3.glucotrack.Adapter.InsulinNotesAdapter;
import edu.uph.m23si3.glucotrack.AddInsulinNotesActivity;
import edu.uph.m23si3.glucotrack.Model.InsulinNotes;
import edu.uph.m23si3.glucotrack.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    Button btnPump;
    ListView listView;
    ArrayList<InsulinNotes> insulinNotesArrayList;
    private static InsulinNotesAdapter adapter;


    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        btnPump = binding.btnPump;
        btnPump.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toAddInsulinNotes();
            }
        });

        listView = binding.listView;
        insulinNotesArrayList = new ArrayList<>();

        // Inisialisasi Insulin Notes
        InsulinNotes insulinNotes1 = new InsulinNotes(30, "Ini insulin yang di pump sebelum makan pagi, pagi ini kondisi lumayan oke, jadi aku makan agak banyak deh.", LocalDate.of(2025, 6, 21), LocalTime.of(8,16));
        InsulinNotes insulinNotes2 = new InsulinNotes(25, "Ini insulin yang di pump sebelum makan siang, lunch hari ini agak dikit, cuman makan omelet sm terong bakar, jadi pump dikit aja hehehe", LocalDate.of(2025, 6, 21), LocalTime.of(13,2));
        InsulinNotes insulinNotes3 = new InsulinNotes(50, "Ini insulin yang di pump sebelum makan malam, malam ini ada family dinner, jadi agak kalap hehe", LocalDate.of(2025, 6, 21), LocalTime.of(19,46));

        insulinNotesArrayList.add(insulinNotes1);
        insulinNotesArrayList.add(insulinNotes2);
        insulinNotesArrayList.add(insulinNotes3);

        // Inisialisasi adapter dan pasang ke listView
        adapter = new InsulinNotesAdapter(insulinNotesArrayList, getContext());
        listView.setAdapter(adapter);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void toAddInsulinNotes(){
        Intent intent = new Intent(getContext(), AddInsulinNotesActivity.class);
        startActivity(intent);
    }
}