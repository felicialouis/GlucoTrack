package edu.uph.m23si3.glucotrack.Model;

import java.util.ArrayList;
import java.util.Arrays;

public class Account {
    private String email;
    private String password;

    public Account(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public static ArrayList<Account> accounts = new ArrayList<>(Arrays.asList(
            new Account("felicialouis@gmail.com", "1234"),
            new Account("angelinawijaya@gmail.com", "1234"),
            new Account("erikaangelia@gmail.com", "1234")
    ));

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
