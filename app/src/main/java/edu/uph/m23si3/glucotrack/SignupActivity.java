package edu.uph.m23si3.glucotrack;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
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
import io.realm.Realm;

public class SignupActivity extends AppCompatActivity {

    EditText edtEmail, edtPassword;
    Button btnSignup;
    TextView txvLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        btnSignup = findViewById(R.id.btnSignup);
        txvLogin = findViewById(R.id.txvLogin);

        btnSignup.setOnClickListener(v -> {
            String email = edtEmail.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                showToast("Both fields should be filled!");
                return;
            }

            Realm realm = Realm.getDefaultInstance();

            try {
                Account existingAccount = realm.where(Account.class)
                        .equalTo("email", email)
                        .findFirst();

                if (existingAccount != null) {
                    showToast("Your account has been registered, please log in.");
                    return;
                }

                // Simpan akun baru
                realm.executeTransactionAsync(r -> {
                    Account newAccount = r.createObject(Account.class, email);
                    newAccount.setPassword(password);
                }, () -> {
                    // ✅ Berhasil (kembali ke UI thread)
                    runOnUiThread(() -> {
                        showToast("Sign in complete! please do your log in.");
                        toLogin();
                    });
                }, error -> {
                    // ❌ Gagal
                    runOnUiThread(() -> showToast("Error saat membuat akun: " + error.getMessage()));
                });


            } catch (Exception e) {
                showToast("Terjadi kesalahan: " + e.getMessage());
            } finally {
                realm.close();
            }
        });


        txvLogin.setOnClickListener(v -> toLogin());
    }

    private void toLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void showToast(String msg) {
        Toast toast = Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.BOTTOM | Gravity.RIGHT, 0, 0);
        toast.show();
    }
}
