package tech.iosd.benefit.Model;

public class PostTrackActivity
{
    private String date;
    private String activity;
    private float time;
    private float calories;
    private float distance;

    public PostTrackActivity(String date,
             String activity,
             float time,
             float calories,
             float distance)
    {
        this.date = date;
        this.activity = activity;
        this.time = time;
        this.calories = calories;

        this.distance = distance;
    }
    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public float getCalories() {
        return calories;
    }

    public void setCalories(float calories) {
        this.calories = calories;
    }

    public float getTime() {
        return time;
    }

    public void setTime(float time) {
        this.time = time;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
