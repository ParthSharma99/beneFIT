package tech.iosd.benefit.Model;

public class PostWaterIntake {
    String date;
    int target;
    int consumed;

    public PostWaterIntake(String date, int target, int consumed) {
        this.date = date;
        this.target = target;
        this.consumed = consumed;
    }

    public String getDate() {
        return date;
    }

    public int getTarget() {
        return target;
    }

    public int getConsumed() {
        return consumed;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTarget(int target) {
        this.target = target;
    }

    public void setConsumed(int consumed) {
        this.consumed = consumed;
    }
}
