package edu.uph.m23si3.glucotrack;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import edu.uph.m23si3.glucotrack.Adapter.RecommendationAdapter;
import edu.uph.m23si3.glucotrack.Model.Recommendation;
import io.realm.Realm;

public class RecommendationActivity extends AppCompatActivity {

    private ListView listRecommendation;
    private Realm realm;
    private List<Recommendation> recList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommendation);

        listRecommendation = findViewById(R.id.listRecommendation);

        realm = Realm.getDefaultInstance();

        // Cek dulu apakah data kosong, jika iya, insert secara async
        if (realm.where(Recommendation.class).findAll().isEmpty()) {
            realm.executeTransactionAsync(bgRealm -> {
                bgRealm.insert(new Recommendation("TYPE 1 (~130g)", "Oatmeal", "Greek yogurt", "Nasi merah", "Sup tahu"));
                bgRealm.insert(new Recommendation("TYPE 2 (~120g)", "Smoothie", "Edamame", "Quinoa salad", "Sapo tahu"));
                bgRealm.insert(new Recommendation("TYPE 3 (~125g)", "Ubi", "Almond", "Nasi merah", "Capcay"));
            }, () -> {
                // onSuccess: Setelah data berhasil dimasukkan, ambil dan tampilkan
                setupList();
            }, error -> {
                // onError
                error.printStackTrace();
            });
        } else {
            setupList();
        }
    }

    private void setupList() {
        // Ambil data dari Realm (copy ke memory agar tidak auto update)
        recList = realm.copyFromRealm(realm.where(Recommendation.class).findAll());

        RecommendationAdapter adapter = new RecommendationAdapter(this, recList);
        listRecommendation.setAdapter(adapter);

        listRecommendation.setOnItemClickListener((adapterView, view, position, id) -> {
            Recommendation selected = recList.get(position);

            Intent resultIntent = new Intent();
            resultIntent.putExtra("breakfast", selected.getBreakfast());
            resultIntent.putExtra("snack", selected.getSnack());
            resultIntent.putExtra("lunch", selected.getLunch());
            resultIntent.putExtra("dinner", selected.getDinner());

            setResult(RESULT_OK, resultIntent);
            finish();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (realm != null && !realm.isClosed()) {
            realm.close();
        }
    }
}
