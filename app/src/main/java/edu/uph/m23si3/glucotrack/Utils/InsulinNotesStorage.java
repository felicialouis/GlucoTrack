package edu.uph.m23si3.glucotrack.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

import edu.uph.m23si3.glucotrack.Model.InsulinNotes;

public class InsulinNotesStorage {
    private static final String PREF_NAME = "insulin_prefs";

    private static Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .registerTypeAdapter(LocalTime.class, new LocalTimeAdapter())
            .create();

    public static void saveNotes(Context context, ArrayList<InsulinNotes> notesList) {
        SharedPreferences session = context.getSharedPreferences("user_session", Context.MODE_PRIVATE);
        String userId = session.getString("userId", null);
        if (userId == null) return;

        SharedPreferences prefs = context.getSharedPreferences("insulin_prefs", Context.MODE_PRIVATE);
        String json = gson.toJson(notesList);
        prefs.edit().putString("KEY_NOTES_" + userId, json).apply();
    }

    public static ArrayList<InsulinNotes> loadNotes(Context context) {
        SharedPreferences session = context.getSharedPreferences("user_session", Context.MODE_PRIVATE);
        String userId = session.getString("userId", null);
        if (userId == null) return new ArrayList<>();

        SharedPreferences prefs = context.getSharedPreferences("insulin_prefs", Context.MODE_PRIVATE);
        String json = prefs.getString("KEY_NOTES_" + userId, null);
        if (json == null) return new ArrayList<>();

        Type type = new TypeToken<ArrayList<InsulinNotes>>() {}.getType();
        return gson.fromJson(json, type);
    }
}
