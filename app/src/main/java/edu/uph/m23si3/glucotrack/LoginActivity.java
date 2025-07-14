package edu.uph.m23si3.glucotrack;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import edu.uph.m23si3.glucotrack.Model.Account;
import edu.uph.m23si3.glucotrack.Model.UserProfile;
import io.realm.Realm;

public class LoginActivity extends AppCompatActivity {

    EditText edtEmail, edtPassword;
    Button btnLogin;
    TextView txvSignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        txvSignup = findViewById(R.id.txvSignup);

        btnLogin.setOnClickListener(v -> {
            String email = edtEmail.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Both fields are required.", Toast.LENGTH_SHORT).show();
                return;
            }

            Realm realm = Realm.getDefaultInstance();

            Account account = realm.where(Account.class)
                    .equalTo("email", email)
                    .findFirst();

            if (account != null) {
                if (account.getPassword().equals(password)) {
                    // Simpan sesi login ke SharedPreferences
                    SharedPreferences prefs = getSharedPreferences("user_session", MODE_PRIVATE);
                    prefs.edit().putString("userId", email).apply();

                    // Cek dan buat UserProfile jika belum ada
                    realm.executeTransaction(r -> {
                        UserProfile profile = r.where(UserProfile.class)
                                .equalTo("email", email)
                                .findFirst();

                        if (profile == null) {
                            profile = r.createObject(UserProfile.class, email);
                            profile.setNama("");
                            profile.setAge(null);
                            profile.setGender("Male");
                            profile.setDiabetesType("Type 1");
                            profile.setTarget(null);
                            profile.setWeight(null);
                            profile.setInsulin(false);
                        }
                    });

                    showToast("Login succeed!");
                    toHome();
                } else {
                    showToast("Password is incorrect!");
                }
            } else {
                showToast("Your email hasn't been registered yet, please do Sign Up.");
            }

            realm.close();
        });


        txvSignup.setOnClickListener(v -> toSignup());
    }

    private void toHome() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    private void toSignup() {
        startActivity(new Intent(this, SignupActivity.class));
    }

    private void showToast(String message) {
        Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.BOTTOM | Gravity.RIGHT, 0, 0);
        toast.show();
    }
}
