package edu.uph.m23si3.glucotrack.Model;

import java.util.ArrayList;
import java.util.Arrays;

import edu.uph.m23si3.glucotrack.R;

public class Notification {
    private int imgType;
    private String title;
    private String desc;
    private String time;

    public Notification(int imgType, String title, String desc, String time) {
        this.imgType = imgType;
        this.title = title;
        this.desc = desc;
        this.time = time;
    }

    public static ArrayList<Notification> notifications = new ArrayList<>(Arrays.asList(
            new Notification(R.drawable.reminder, "Don't Forget to Eat Your First Meal Today!", "We notice that you haven't eat anything today, we are so concern and hope that you are okay ^3^", "3h ago"),
            new Notification(R.drawable.alert, "High Blood Sugar!", "You reach a very high blood sugar: 289 mg/dl", "1d ago"),
            new Notification(R.drawable.alert, "Low Blood Sugar!", "We are so concern with your blood sugar: 78 mg/dl. Recommended to eat high glucose food immediately!", "2d ago"),
            new Notification(R.drawable.reminder, "Your CGM Lifetime is Running Out!", "Your CGM lifetime remains 1 day. Prepare your next CGM unit:)", "1w ago")
    ));

    public int getImgType() {
        return imgType;
    }

    public void setImgType(String type) {
        this.imgType = imgType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String desc) {
        this.time = time;
    }
}
