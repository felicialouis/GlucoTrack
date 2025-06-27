package edu.uph.m23si3.glucotrack.Model;

public class Recommendation {
    private String type;
    private String breakfast;
    private String snack;
    private String lunch;
    private String dinner;

    public Recommendation(String type, String breakfast, String snack, String lunch, String dinner) {
        this.type = type;
        this.breakfast = breakfast;
        this.snack = snack;
        this.lunch = lunch;
        this.dinner = dinner;
    }

    public String getType() {
        return type;
    }

    public String getBreakfast() {
        return breakfast;
    }

    public String getSnack() {
        return snack;
    }

    public String getLunch() {
        return lunch;
    }

    public String getDinner() {
        return dinner;
    }
}
