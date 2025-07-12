package edu.uph.m23si3.glucotrack.Model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class InsulinNotes extends RealmObject {
    @PrimaryKey
    private int id;
    private String userEmail;
    private int dose;
    private String notes;
    private String time;
    private long timestamp;
    private String date;

    public InsulinNotes() {
        // Required empty constructor by Realm
    }

    public InsulinNotes(int id, String userEmail, int dose, String notes, String time, long timestamp, String date) {
        this.id = id;
        this.userEmail = userEmail;
        this.dose = dose;
        this.notes = notes;
        this.time = time;
        this.timestamp = timestamp;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public int getDose() {
        return dose;
    }

    public void setDose(int dose) {
        this.dose = dose;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
