package edu.uph.m23si3.glucotrack.ui.home;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
    private ImageView imgNotification;
    private Button btnPump;
    private RecyclerView rvInsulinNotes;
    private InsulinNotesAdapter adapter;

    private Realm realm;
    private RealmResults<InsulinNotes> insulinNotesList;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        HomeViewModel homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        realm = Realm.getDefaultInstance();

        imgNotification = binding.imgNotification;
        imgNotification.setOnClickListener(v -> toNotification());

        btnPump = binding.btnPump;
        btnPump.setOnClickListener(v -> toAddInsulinNotes());

        rvInsulinNotes = binding.rvInsulinNotes;
        rvInsulinNotes.setLayoutManager(new LinearLayoutManager(getContext()));

        // Load data dari Realm
        String userEmail = getLoggedInUserEmail();
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
}
