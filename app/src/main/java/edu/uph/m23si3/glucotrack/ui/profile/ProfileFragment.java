package edu.uph.m23si3.glucotrack.ui.profile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
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
import edu.uph.m23si3.glucotrack.Model.Account;
import edu.uph.m23si3.glucotrack.Model.UserProfile;
import edu.uph.m23si3.glucotrack.R;
import edu.uph.m23si3.glucotrack.ui.barcode.BarcodeActivity;
import io.realm.Realm;

public class ProfileFragment extends Fragment {

    private static final int REQUEST_IMAGE_PICK = 1001;

    private Spinner spinnerGender, spinnerDiabetes;
    private Switch switchInsulin;
    private EditText edtNama, edtEmail, edtAge, edtTarget, edtWeight;
    private TextView profileName, txtInsulinStatus;
    private ImageView imgProfile, editIcon, scanIcon, logout;
    private FrameLayout frameProfile;

    private UserProfile profile;
    private Realm realm;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        spinnerGender = view.findViewById(R.id.spinner_gender);
        spinnerDiabetes = view.findViewById(R.id.spinner_diabetes);
        switchInsulin = view.findViewById(R.id.switch_insulin);
        profileName = view.findViewById(R.id.profile_name);
        edtNama = view.findViewById(R.id.edtNama);
        edtEmail = view.findViewById(R.id.edtEmail);
        edtAge = view.findViewById(R.id.edtAge);
        edtTarget = view.findViewById(R.id.edtTarget);
        edtWeight = view.findViewById(R.id.edtWeight);
        frameProfile = view.findViewById(R.id.frame_profile);
        imgProfile = view.findViewById(R.id.profile_image);
        editIcon = view.findViewById(R.id.edit_icon);
        scanIcon = view.findViewById(R.id.scan_icon);
        logout = view.findViewById(R.id.logout);

        realm = Realm.getDefaultInstance();

        SharedPreferences session = requireContext().getSharedPreferences("user_session", Context.MODE_PRIVATE);
        String email = session.getString("userId", null);

        if (email == null) {
            redirectToLogin();
            return null;
        }

        profile = realm.where(UserProfile.class).equalTo("email", email).findFirst();

        if (profile == null) {
            redirectToLogin();
            return null;
        }

        edtNama.setText(profile.getNama());
        edtEmail.setText(profile.getEmail());
        edtEmail.setEnabled(false);
        edtAge.setText(profile.getAge() != null ? String.valueOf(profile.getAge()) : "");
        edtTarget.setText(profile.getTarget() != null ? String.valueOf(profile.getTarget()) : "");
        edtWeight.setText(profile.getWeight() != null ? String.valueOf(profile.getWeight()) : "");
        profileName.setText(profile.getNama());

        txtInsulinStatus = view.findViewById(R.id.txtInsulinStatus);

        switchInsulin.setChecked(profile.isInsulin());
        txtInsulinStatus.setText(profile.isInsulin() ? "Yes" : "No");

        switchInsulin.setOnCheckedChangeListener((btn, checked) -> {
            realm.executeTransaction(r -> profile.setInsulin(checked));
            txtInsulinStatus.setText(checked ? "Yes" : "No");
        });

        edtNama.addTextChangedListener(createRealmWatcher("nama", true));
        edtAge.addTextChangedListener(createRealmWatcher("age", false));
        edtTarget.addTextChangedListener(createRealmWatcher("target", false));
        edtWeight.addTextChangedListener(createRealmWatcher("weight", false));

        // Spinner Gender
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, new String[]{"Male", "Female", "Other"});
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGender.setAdapter(genderAdapter);
        int genderIndex = genderAdapter.getPosition(profile.getGender());
        spinnerGender.setSelection(genderIndex);
        spinnerGender.setOnItemSelectedListener(generateSpinnerListener("gender"));

        // Spinner Diabetes Type
        ArrayAdapter<String> diabetesAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, new String[]{"Type 1", "Type 2", "Gestational", "Other"});
        diabetesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDiabetes.setAdapter(diabetesAdapter);
        int diabetesIndex = diabetesAdapter.getPosition(profile.getDiabetesType());
        spinnerDiabetes.setSelection(diabetesIndex);
        spinnerDiabetes.setOnItemSelectedListener(generateSpinnerListener("diabetesType"));

        // Open image picker when clicking image or icon
        View.OnClickListener imageClickListener = v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, REQUEST_IMAGE_PICK);
        };
        imgProfile.setOnClickListener(imageClickListener);
        editIcon.setOnClickListener(imageClickListener);

        scanIcon.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), BarcodeActivity.class);
            startActivity(intent);
        });

        logout.setOnClickListener(v -> {
            new android.app.AlertDialog.Builder(requireContext())
                    .setTitle("Confirm Logout")
                    .setMessage("Are you sure you want to logout?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        session.edit().clear().apply();
                        Toast.makeText(requireContext(), "Logged out successfully", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(requireContext(), LoginActivity.class));
                        requireActivity().finish();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == getActivity().RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            imgProfile.setImageURI(imageUri);
            realm.executeTransaction(r -> profile.setProfileImageUri(imageUri.toString()));
        }
    }

    private void redirectToLogin() {
        Toast.makeText(requireContext(), "Silakan login terlebih dahulu.", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(requireContext(), LoginActivity.class));
        requireActivity().finish();
    }

    private TextWatcher createRealmWatcher(String fieldName, boolean updateName) {
        return new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}

            @Override public void onTextChanged(CharSequence s, int st, int b, int c) {
                realm.executeTransaction(r -> {
                    switch (fieldName) {
                        case "nama":
                            profile.setNama(s.toString());
                            if (updateName) profileName.setText(s.toString());
                            break;

                        case "age":
                            if (s.toString().trim().isEmpty()) {
                                profile.setAge(null);
                            } else {
                                try {
                                    profile.setAge(Integer.parseInt(s.toString()));
                                } catch (NumberFormatException e) {
                                    profile.setAge(null);
                                }
                            }
                            break;

                        case "target":
                            if (s.toString().trim().isEmpty()) {
                                profile.setTarget(null);
                            } else {
                                try {
                                    profile.setTarget(Integer.parseInt(s.toString()));
                                } catch (NumberFormatException e) {
                                    profile.setTarget(null);
                                }
                            }
                            break;

                        case "weight":
                            if (s.toString().trim().isEmpty()) {
                                profile.setWeight(null);
                            } else {
                                try {
                                    profile.setWeight(Integer.parseInt(s.toString()));
                                } catch (NumberFormatException e) {
                                    profile.setWeight(null);
                                }
                            }
                            break;
                    }
                });
            }

            @Override public void afterTextChanged(Editable s) {}
        };
    }


    private AdapterView.OnItemSelectedListener generateSpinnerListener(String fieldName) {
        return new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                String selected = parent.getItemAtPosition(pos).toString();
                realm.executeTransaction(r -> {
                    if (fieldName.equals("gender")) profile.setGender(selected);
                    else if (fieldName.equals("diabetesType")) profile.setDiabetesType(selected);
                });
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        };
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (realm != null) realm.close();
    }
}
