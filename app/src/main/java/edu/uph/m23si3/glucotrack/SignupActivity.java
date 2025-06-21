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

public class SignupActivity extends AppCompatActivity {

    EditText edtEmail, edtPassword;
    Button btnSignup;
    TextView txvLogin;
    ArrayList<Account> accounts;

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

        accounts = Account.accounts;

        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        btnSignup = findViewById(R.id.btnSignup);
        txvLogin = findViewById(R.id.txvLogin);

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = edtEmail.getText().toString().trim();
                String password = edtPassword.getText().toString().trim();

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Harap isi email dan password!", Toast.LENGTH_SHORT).show();
                    return; // Stop proses
                }

                boolean emailFound = false;

                for (Account account : accounts) {
                    if (account.getEmail().equals(email)) {
                        emailFound = true;
                        Toast toast = Toast.makeText(getApplicationContext(), "Anda sudah punya akun, silahkan Login.", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.BOTTOM | Gravity.RIGHT, 0, 0);
                        toast.show();
                    }
                }

                if (!emailFound) {
                    Account account = new Account(edtEmail.getText().toString(), edtPassword.getText().toString());
                    accounts.add(account);

                    // Simpan data
                    SharedPreferences preferences = getSharedPreferences("user_pref", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();

                    editor.putString("email", email);
                    editor.putString("password", password);
                    editor.apply();

                    Toast toast = Toast.makeText(getApplicationContext(), "Akun Anda berhasil dibuat, silahkan Login!", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.BOTTOM | Gravity.RIGHT, 0, 0);
                    toast.show();
                    toLogin();
                }
            }
        });

        txvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toLogin();
            }
        });
    }

    public void toLogin(){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}