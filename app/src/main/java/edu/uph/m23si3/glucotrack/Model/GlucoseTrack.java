package edu.uph.m23si3.glucotrack.Model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class GlucoseTrack extends RealmObject {
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

}
