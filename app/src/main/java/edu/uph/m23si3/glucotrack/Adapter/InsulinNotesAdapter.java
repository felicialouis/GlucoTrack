package edu.uph.m23si3.glucotrack.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Locale;

import edu.uph.m23si3.glucotrack.AddInsulinNotesActivity;
import edu.uph.m23si3.glucotrack.Model.InsulinNotes;
import edu.uph.m23si3.glucotrack.R;
import io.realm.Realm;
import io.realm.RealmResults;

public class InsulinNotesAdapter extends RecyclerView.Adapter<InsulinNotesAdapter.ViewHolder> {

    private RealmResults<InsulinNotes> notesList;
    private Context context;

    public InsulinNotesAdapter(RealmResults<InsulinNotes> notesList, Context context) {
        this.notesList = notesList;
        this.context = context;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txvInsulin, txvNote, txvTime;
        ImageView imgMenu;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txvInsulin = itemView.findViewById(R.id.txvInsulin);
            txvNote = itemView.findViewById(R.id.txvNote);
            txvTime = itemView.findViewById(R.id.txvTime);
            imgMenu = itemView.findViewById(R.id.imgMenu);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.insulin_notes_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        InsulinNotes note = notesList.get(position);

        holder.txvInsulin.setText(String.valueOf(note.getDose()));
        holder.txvNote.setText(note.getNotes());

        String time = note.getTime();
        holder.txvTime.setText(time);

        holder.imgMenu.setOnClickListener(v -> showPopupMenu(v, note.getId()));
    }

    @Override
    public int getItemCount() {
        return notesList.size();
    }

    private void showPopupMenu(View view, int noteId) {
        PopupMenu popupMenu = new PopupMenu(context, view);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.insulin_note_menu, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();

            if (id == R.id.menuEdit) {
                Intent intent = new Intent(context, AddInsulinNotesActivity.class);
                intent.putExtra("edit_id", noteId); // kirim ID ke AddInsulinNotesActivity
                context.startActivity(intent);
                return true;
            } else if (id == R.id.menuDelete) {
                confirmDelete(noteId);
                return true;
            }

            return false;
        });

        popupMenu.show();
    }

    private void confirmDelete(int noteId) {
        new AlertDialog.Builder(context)
                .setTitle("Delete Note")
                .setMessage("Are you sure you want to delete this note?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    Realm realm = Realm.getDefaultInstance();
                    realm.executeTransactionAsync(r -> {
                        InsulinNotes note = r.where(InsulinNotes.class).equalTo("id", noteId).findFirst();
                        if (note != null) {
                            note.deleteFromRealm();
                        }
                    }, () -> {
                        Toast.makeText(context, "Note deleted", Toast.LENGTH_SHORT).show();
                        notifyDataSetChanged();
                    }, error -> {
                        Toast.makeText(context, "Fail to delete: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    });

                    realm.close();
                    notifyDataSetChanged(); // refresh list
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    public void updateData(RealmResults<InsulinNotes> newNotes) {
        this.notesList = newNotes;
        notifyDataSetChanged();
    }
}
