package edu.uph.m23si3.glucotrack.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

import edu.uph.m23si3.glucotrack.Model.InsulinNotes;
import edu.uph.m23si3.glucotrack.R;

public class InsulinNotesAdapter extends ArrayAdapter<InsulinNotes> {
    private ArrayList<InsulinNotes> insulinNotesArrayList;
    Context context;

    public InsulinNotesAdapter(ArrayList<InsulinNotes> insulinNotesArrayList, Context context) {
        super(context, R.layout.insulin_notes_card);
        this.insulinNotesArrayList = insulinNotesArrayList;
        this.context = context;
    }

    private static class MyViewHolder{
        TextView txvNumberOfUnitOfInsulin, txvNotes, txvTime;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        InsulinNotes insulinNotes = getItem(position);
        final View result;

        MyViewHolder myViewHolder;

        if (convertView==null) {
            myViewHolder = new MyViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.insulin_notes_card, parent, false);

            myViewHolder.txvNumberOfUnitOfInsulin = (TextView) convertView.findViewById(R.id.txvNumberOfUnitOfInsulin);
            myViewHolder.txvNotes = (TextView) convertView.findViewById(R.id.txvNotes);
            myViewHolder.txvTime = (TextView) convertView.findViewById(R.id.txvTime);

            convertView.setTag(myViewHolder);
        } else{
            myViewHolder = (MyViewHolder) convertView.getTag();
        }

        result = convertView;
        myViewHolder.txvNumberOfUnitOfInsulin.setText(insulinNotes.getNumberOfUnitOfInsulin());
        myViewHolder.txvNotes.setText(insulinNotes.getNotes());
        myViewHolder.txvTime.setText(insulinNotes.getTime().toString());

        return result;
    }
}
