package edu.uph.m23si3.glucotrack;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import edu.uph.m23si3.glucotrack.Adapter.RecommendationAdapter;
import edu.uph.m23si3.glucotrack.Model.Recommendation;

public class RecommendationActivity extends AppCompatActivity {

    private ListView listRecommendation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommendation);

        listRecommendation = findViewById(R.id.listRecommendation);

        List<Recommendation> recList = new ArrayList<>();
        recList.add(new Recommendation("TYPE 1 (~130g)", "Oatmeal", "Greek yogurt", "Nasi merah", "Sup tahu"));
        recList.add(new Recommendation("TYPE 2 (~120g)", "Smoothie", "Edamame", "Quinoa salad", "Sapo tahu"));
        recList.add(new Recommendation("TYPE 3 (~125g)", "Ubi", "Almond", "Nasi merah", "Capcay"));

        RecommendationAdapter adapter = new RecommendationAdapter(this, recList);
        listRecommendation.setAdapter(adapter);

        listRecommendation.setOnItemClickListener((adapterView, view, position, id) -> {
            Recommendation selected = recList.get(position);

            // Kirim data kembali ke FoodsFragment
            Intent resultIntent = new Intent();
            resultIntent.putExtra("breakfast", selected.getBreakfast());
            resultIntent.putExtra("snack", selected.getSnack());
            resultIntent.putExtra("lunch", selected.getLunch());
            resultIntent.putExtra("dinner", selected.getDinner());

            setResult(RESULT_OK, resultIntent);
            finish(); // tutup activity dan kembali ke fragment
        });
    }
}

