package edu.uph.m23si3.glucotrack.Model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class GlucoseTrack extends RealmObject {
    private String breakfast;
    private String lunch;
    private String dinner;
    private String snack;
    @PrimaryKey
    private String date; // Format: dd-MM-yyyy

    private int totalGlucose;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getTotalGlucose() {
        return totalGlucose;
    }

    public void setTotalGlucose(int totalGlucose) {
        this.totalGlucose = totalGlucose;
    }

    public String getBreakfast() { return breakfast; }
    public void setBreakfast(String breakfast) { this.breakfast = breakfast; }

    public String getLunch() { return lunch; }
    public void setLunch(String lunch) { this.lunch = lunch; }

    public String getDinner() { return dinner; }
    public void setDinner(String dinner) { this.dinner = dinner; }

    public String getSnack() { return snack; }
    public void setSnack(String snack) { this.snack = snack; }

}