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
import edu.uph.m23si3.glucotrack.R;
import io.realm.Realm;

public class ProfileFragment extends Fragment {

    private static final int REQUEST_IMAGE_PICK = 1001;

    private Spinner spinnerGender, spinnerDiabetes;
    private Switch switchInsulin;
    private EditText edtNama, edtEmail, edtAge, edtTarget;
    private TextView profileName, txtInsulinStatus;
    private ImageView imgProfile, editIcon;
    private FrameLayout frameProfile;

    private Account account;
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
        frameProfile = view.findViewById(R.id.frame_profile);
        imgProfile = view.findViewById(R.id.profile_image);
        editIcon = view.findViewById(R.id.edit_icon);

        realm = Realm.getDefaultInstance();

        SharedPreferences session = requireContext().getSharedPreferences("user_session", Context.MODE_PRIVATE);
        String email = session.getString("userId", null);

        if (email == null) {
            redirectToLogin();
            return null;
        }

        account = realm.where(Account.class).equalTo("email", email).findFirst();

        if (account == null) {
            redirectToLogin();
            return null;
        }

        edtNama.setText(account.getNama());
        edtEmail.setText(account.getEmail());   // dari login
        edtEmail.setEnabled(false);             // agar tidak bisa diedit
        edtAge.setText(account.getAge());
        edtTarget.setText(account.getTarget());
        profileName.setText(account.getNama());

        TextView txtInsulinStatus = view.findViewById(R.id.txtInsulinStatus);

        switchInsulin.setChecked(account.isInsulin());
        txtInsulinStatus.setText(account.isInsulin() ? "Yes" : "No");

        switchInsulin.setOnCheckedChangeListener((btn, checked) -> {
            realm.executeTransaction(r -> account.setInsulin(checked));
            txtInsulinStatus.setText(checked ? "Yes" : "No");
        });

        edtNama.addTextChangedListener(createRealmWatcher("nama", true));
        edtAge.addTextChangedListener(createRealmWatcher("age", false));
        edtTarget.addTextChangedListener(createRealmWatcher("target", false));

        // Spinner Gender
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, new String[]{"Male", "Female", "Other"});
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGender.setAdapter(genderAdapter);
        int genderIndex = genderAdapter.getPosition(account.getGender());
        spinnerGender.setSelection(genderIndex);
        spinnerGender.setOnItemSelectedListener(generateSpinnerListener("gender"));

        // Spinner Diabetes Type
        ArrayAdapter<String> diabetesAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, new String[]{"Type 1", "Type 2", "Gestational", "Other"});
        diabetesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDiabetes.setAdapter(diabetesAdapter);
        int diabetesIndex = diabetesAdapter.getPosition(account.getDiabetesType());
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

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == getActivity().RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            imgProfile.setImageURI(imageUri);
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
                            account.setNama(s.toString());
                            if (updateName) profileName.setText(s.toString());
                            break;
                        case "age":
                            account.setAge(s.toString());
                            break;
                        case "target":
                            account.setTarget(s.toString());
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
                    if (fieldName.equals("gender")) account.setGender(selected);
                    else if (fieldName.equals("diabetesType")) account.setDiabetesType(selected);
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
