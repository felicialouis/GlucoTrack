package edu.uph.m23si3.glucotrack.ui.profile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import edu.uph.m23si3.glucotrack.LoginActivity;
import edu.uph.m23si3.glucotrack.R;

public class ProfileFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1001;

    private Spinner spinnerGender, spinnerDiabetes;
    private Switch switchInsulin;
    private EditText edtNama, edtEmail, edtAge, edtTarget;
    private TextView profileName;
    private ImageView imgProfile;
    private FrameLayout frameProfile;
    private SharedPreferences prefs;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Inisialisasi view
        spinnerGender = view.findViewById(R.id.spinner_gender);
        spinnerDiabetes = view.findViewById(R.id.spinner_diabetes);
        switchInsulin = view.findViewById(R.id.switch_insulin);
        profileName = view.findViewById(R.id.profile_name);
        edtNama = view.findViewById(R.id.edtNama);
        edtEmail = view.findViewById(R.id.edtEmail);
        edtAge = view.findViewById(R.id.edtAge);
        edtTarget = view.findViewById(R.id.edtTarget);
        frameProfile = view.findViewById(R.id.frame_profile);
        imgProfile = view.findViewById(R.id.profile_image);

        prefs = requireContext().getSharedPreferences("user_profile", Context.MODE_PRIVATE);

        // Load data dari shared preferences
        edtNama.setText(prefs.getString("nama", ""));
        profileName.setText(prefs.getString("nama", ""));
        edtEmail.setText(prefs.getString("email", ""));
        edtAge.setText(prefs.getString("age", ""));
        edtTarget.setText(prefs.getString("target", ""));

        String savedImg = prefs.getString("profile_image", null);
        if (savedImg != null) {
            imgProfile.setImageURI(Uri.parse(savedImg));
        }

        // Auto-save EditText + update nama header
        edtNama.addTextChangedListener(createAutoSaveWatcher("nama", true));
        edtEmail.addTextChangedListener(createAutoSaveWatcher("email", false));
        edtAge.addTextChangedListener(createAutoSaveWatcher("age", false));
        edtTarget.addTextChangedListener(createAutoSaveWatcher("target", false));

        // Setup spinner Gender
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, new String[]{"Male", "Female", "Other"});
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGender.setAdapter(genderAdapter);
        spinnerGender.setSelection(prefs.getInt("gender", 0));
        spinnerGender.setOnItemSelectedListener(generateSpinnerListener("gender", "gender_text"));

        // Setup spinner Diabetes
        ArrayAdapter<String> diabetesAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, new String[]{"Type 1", "Type 2", "Gestational", "Other"});
        diabetesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDiabetes.setAdapter(diabetesAdapter);
        spinnerDiabetes.setSelection(prefs.getInt("diabetes_pos", 0));
        spinnerDiabetes.setOnItemSelectedListener(generateSpinnerListener("diabetes_pos", "diabetes_text"));

        // Setup switch insulin
        boolean insulinStatus = prefs.getBoolean("insulin", false);
        switchInsulin.setChecked(insulinStatus);
        switchInsulin.setText(insulinStatus ? "Yes" : "No");
        switchInsulin.setOnCheckedChangeListener((btn, checked) -> {
            prefs.edit().putBoolean("insulin", checked).apply();
            switchInsulin.setText(checked ? "Yes" : "No");
        });

        // Photo click to pick image
        frameProfile.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });

        return view;
    }
    private AdapterView.OnItemSelectedListener generateSpinnerListener(String key, String keyText) {
        return new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                prefs.edit()
                        .putInt(key, pos)
                        .putString(keyText, parent.getItemAtPosition(pos).toString())
                        .apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // tidak digunakan
            }
        };
    }

    private TextWatcher createAutoSaveWatcher(String key, boolean updateName) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int st, int c, int a) {
            }

            @Override
            public void onTextChanged(CharSequence s, int st, int b, int c) {
                prefs.edit().putString(key, s.toString()).apply();
                if (updateName) profileName.setText(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true); // pastikan ini
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_profile, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.mBersihkanForm) {
            edtNama.setText("");
            edtEmail.setText("");
            edtAge.setText("");
            edtTarget.setText("");
            spinnerGender.setSelection(0);
            spinnerDiabetes.setSelection(0);
            switchInsulin.setChecked(false);
            imgProfile.setImageResource(R.drawable.profile_image);
            prefs.edit().clear().apply();
            Toast.makeText(requireContext(), "Form dibersihkan", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (item.getItemId() == R.id.mLogout) {
            prefs.edit().clear().apply();
            Intent intent = new Intent(requireContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            requireActivity().finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}

