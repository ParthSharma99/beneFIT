package tech.iosd.benefit.Model;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Random;

/**
 * Created by SAM33R on 06-06-2018.
 */

public class Measurements {
    String date;
    int age;
    int height;
    int waist;
    int neck;
    int hip;
    int weight;

    public Measurements(int age, int height, int waist, int neck, int hip, int weight) {

        // Generate Date
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        String selectedDate = dateFormat.format(Calendar.getInstance().getTime());
        Log.d("Measurements", "Measurements: Date: " + selectedDate);

        this.age = age;
        this.height = height;
        this.waist = waist;
        this.neck = neck;
        this.hip = hip;
        this.date = selectedDate;
        this.weight = weight;

    }

    public int getAge() {
        return age;
    }

    public void setAge(int weight) {
        this.age = weight;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWaist() {
        return waist;
    }

    public void setWaist(int waist) {
        this.waist = waist;
    }

    public int getNeck() {
        return neck;
    }

    public void setNeck(int neck) {
        this.neck = neck;
    }

    public int getHip() {
        return hip;
    }

    public void setHip(int hip) {
        this.hip = hip;
    }
}
