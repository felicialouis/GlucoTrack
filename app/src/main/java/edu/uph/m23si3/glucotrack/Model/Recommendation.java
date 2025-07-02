package edu.uph.m23si3.glucotrack.Model;

import io.realm.RealmObject;

public class Recommendation extends RealmObject {
    private String type;
    private String breakfast;
    private String snack;
    private String lunch;
    private String dinner;

    // Wajib: constructor kosong
    public Recommendation() {}

    // Optional: constructor tambahan untuk kemudahan
    public Recommendation(String type, String breakfast, String snack, String lunch, String dinner) {
        this.type = type;
        this.breakfast = breakfast;
        this.snack = snack;
        this.lunch = lunch;
        this.dinner = dinner;
    }

    // Getter & Setter (wajib)
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getBreakfast() { return breakfast; }
    public void setBreakfast(String breakfast) { this.breakfast = breakfast; }

    public String getSnack() { return snack; }
    public void setSnack(String snack) { this.snack = snack; }

    public String getLunch() { return lunch; }
    public void setLunch(String lunch) { this.lunch = lunch; }

    public String getDinner() { return dinner; }
    public void setDinner(String dinner) { this.dinner = dinner; }
}
