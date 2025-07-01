package edu.uph.m23si3.glucotrack.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import edu.uph.m23si3.glucotrack.Model.Recommendation;
import edu.uph.m23si3.glucotrack.R;

public class RecommendationAdapter extends BaseAdapter {

    private Context context;
    private List<Recommendation> data;

    public RecommendationAdapter(Context context, List<Recommendation> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Recommendation getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup parent) {
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_reccomendation, parent, false);
        }

        TextView txtType = view.findViewById(R.id.txtType);
        TextView txtDetails = view.findViewById(R.id.txtDetails);

        Recommendation rec = getItem(i);

        txtType.setText(rec.getType());
        String details = "Breakfast: " + rec.getBreakfast() +
                "\nSnack: " + rec.getSnack() +
                "\nLunch: " + rec.getLunch() +
                "\nDinner: " + rec.getDinner();

        txtDetails.setText(details);

        return view;
    }
}
