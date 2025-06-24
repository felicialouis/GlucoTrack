package edu.uph.m23si3.glucotrack.Model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Account extends RealmObject {

    @PrimaryKey
    private String email;

    private String password;

    public Account() {
        // Diperlukan oleh Realm
    }

    public Account(String email, String password) {
        this.email = email;
        this.password = password;
    }

    // Getter dan Setter
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
