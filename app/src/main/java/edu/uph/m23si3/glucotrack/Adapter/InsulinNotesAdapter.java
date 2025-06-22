package edu.uph.m23si3.glucotrack.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import edu.uph.m23si3.glucotrack.AddInsulinNotesActivity;
import edu.uph.m23si3.glucotrack.Model.InsulinNotes;
import edu.uph.m23si3.glucotrack.R;
import edu.uph.m23si3.glucotrack.Utils.InsulinNotesStorage;

public class InsulinNotesAdapter extends BaseAdapter {

    private ArrayList<InsulinNotes> notesList;
    private Context context;

    public InsulinNotesAdapter(ArrayList<InsulinNotes> notesList, Context context) {
        this.notesList = notesList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return notesList.size();
    }

    @Override
    public Object getItem(int position) {
        return notesList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    static class ViewHolder {
        TextView txvInsulin, txvNote, txvTime;
        ImageView imgMenu;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        InsulinNotes note = notesList.get(position);
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.insulin_notes_card, parent, false);
            holder = new ViewHolder();
            holder.txvInsulin = convertView.findViewById(R.id.txvInsulin);
            holder.txvNote = convertView.findViewById(R.id.txvNote);
            holder.txvTime = convertView.findViewById(R.id.txvTime);
            holder.imgMenu = convertView.findViewById(R.id.imgMenu);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.txvInsulin.setText(String.valueOf(note.getNumberOfUnitOfInsulin()));
        holder.txvNote.setText(note.getNotes());
        holder.txvTime.setText(note.getTime().format(DateTimeFormatter.ofPattern("hh:mm a")));

        holder.imgMenu.setOnClickListener(v -> showPopupMenu(v, position));

        return convertView;
    }

    private void showPopupMenu(View view, int position) {
        PopupMenu popupMenu = new PopupMenu(context, view);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.insulin_note_menu, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();

            if (id == R.id.menuEdit) {
                Intent intent = new Intent(context, AddInsulinNotesActivity.class);
                intent.putExtra("edit_position", position);
                context.startActivity(intent);
                return true;
            } else if (id == R.id.menuDelete) {
                confirmDelete(position);
                return true;
            }

            return false;
        });
        popupMenu.show();
    }

    private void confirmDelete(int position) {
        new AlertDialog.Builder(context)
                .setTitle("Hapus Catatan")
                .setMessage("Yakin ingin menghapus catatan ini?")
                .setPositiveButton("Hapus", (dialog, which) -> {
                    notesList.remove(position);
                    InsulinNotesStorage.saveNotes(context, notesList);
                    notifyDataSetChanged();
                    Toast.makeText(context, "Catatan dihapus", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Batal", null)
                .show();
    }
}
