package edu.uph.m23si3.glucotrack;

import android.app.Application;
import io.realm.Realm;
import io.realm.RealmConfiguration;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // Inisialisasi Realm sekali di seluruh aplikasi
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder()
                .name("glucotrack.realm") // Nama file Realm
                .deleteRealmIfMigrationNeeded() // Opsional: untuk development
                .build();

        Realm.setDefaultConfiguration(config);
    }
}
