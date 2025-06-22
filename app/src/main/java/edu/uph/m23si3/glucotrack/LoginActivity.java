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

import java.util.ArrayList;

import edu.uph.m23si3.glucotrack.Model.Account;
import edu.uph.m23si3.glucotrack.ui.home.HomeFragment;

public class LoginActivity extends AppCompatActivity {

    EditText edtEmail, edtPassword;
    Button btnLogin;
    TextView txvSignup;
    ArrayList<Account> accounts;
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

        accounts = Account.accounts;

        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        txvSignup = findViewById(R.id.txvSignup);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = edtEmail.getText().toString().trim();
                String password = edtPassword.getText().toString().trim();

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Harap isi email dan password!", Toast.LENGTH_SHORT).show();
                    return; // Stop proses
                }

                SharedPreferences preferences = getSharedPreferences("user_pref", MODE_PRIVATE);
                String savedEmail = preferences.getString("email", "");
                String savedPassword = preferences.getString("password", "");

                boolean emailFound = false;

                if (email.equals(savedEmail) && password.equals(savedPassword)){
                    emailFound = true;

                    SharedPreferences prefs = getSharedPreferences("user_session", MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("userId", email); // <- INI SELALU DIEKSEKUSI, MAUPUN GAGAL LOGIN
                    editor.apply();

                    toHome();
                } else {
                    for (Account account : accounts) {
                        if (account.getEmail().equals(email)) {
                            emailFound = true;
                            if (account.getPassword().equals(password)) {

                                SharedPreferences prefs = getSharedPreferences("user_session", MODE_PRIVATE);
                                SharedPreferences.Editor editor = prefs.edit();
                                editor.putString("userId", email); // <- INI SELALU DIEKSEKUSI, MAUPUN GAGAL LOGIN
                                editor.apply();

                                toHome();
                            } else {
                                Toast toast = Toast.makeText(getApplicationContext(), "Masukkan password yang benar!", Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.BOTTOM | Gravity.RIGHT, 0, 0);
                                toast.show();
                            }
                            break; // sudah ketemu, stop loop
                        }
                    }
                }

                if (!emailFound) {
                    Toast toast = Toast.makeText(getApplicationContext(), "Email anda belum terdaftar, silahkan Sign Up terlebih dahulu.", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.BOTTOM | Gravity.RIGHT, 0, 0);
                    toast.show();
                }
            }
        });

        txvSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toSignup();
            }
        });
    }

    public void toHome(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void toSignup(){
        Intent intent = new Intent(this, SignupActivity.class);
        startActivity(intent);
    }
}