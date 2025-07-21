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

        // Hapus data lama terlebih dahulu
        realm.executeTransaction(bgRealm -> {
            bgRealm.delete(Recommendation.class);
        });

        // Masukkan data terbaru
        realm.executeTransactionAsync(bgRealm -> {
            // TYPE 1 (~85g)
            bgRealm.insert(new Recommendation(
                    "TYPE 1 (~105g)",
                    "Roti gandum isi telur dan alpukat",
                    "Greek yogurt dengan chia seed",
                    "Nasi merah + ayam kukus + tumis bayam",
                    "Sup tahu dan sayur bening"
            ));

            // TYPE 2 (~70g)
            bgRealm.insert(new Recommendation(
                    "TYPE 2 (~70g)",
                    "Salad sayuran dengan ayam panggang (15g)",
                    "Sup kacang merah dengan sayuran (20g)",
                    "Ikan panggang dengan brokoli (30g)",
                    "Omelet sayuran dengan keju rendah lemak (5g)"
            ));

            // TYPE 3 (~65g)
            bgRealm.insert(new Recommendation(
                    "TYPE 3 (~65g)",
                    "Greek yogurt dengan buah dan madu (25g)",
                    "Ayam panggang dengan kacang hijau (12g)",
                    "Tahu kukus dengan sayuran (10g)",
                    "Smoothie bayam dan alpukat (18g)"
            ));
        }, this::setupList, Throwable::printStackTrace);
    }

    private void setupList() {
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
