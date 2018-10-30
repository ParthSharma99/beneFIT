package tech.iosd.benefit.Model;

import java.util.ArrayList;

public class PostFreeWorkoutActivity
{
    private String date;
    private String workout;
    private ArrayList<Progress> progress;
    private Stats stats;

    public PostFreeWorkoutActivity()
    {

    }

    public Stats getStats() {
        return stats;
    }

    public void setStats(Stats stats) {
        this.stats = stats;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getWorkout() {
        return workout;
    }

    public void setWorkout(String workout) {
        this.workout = workout;
    }

    public ArrayList<Progress> getProgress() {
        return progress;
    }

    public void setProgress(ArrayList<Progress> progress) {
        this.progress = progress;
    }

    public static class Progress
    {
        private String exercise;
        private int set;
        private int reps;
        private int weight;
        public String getExercise() {
            return exercise;
        }

        public void setExercise(String exercise) {
            this.exercise = exercise;
        }

        public int getSet() {
            return set;
        }

        public void setSet(int set) {
            this.set = set;
        }

        public int getReps() {
            return reps;
        }

        public void setReps(int reps) {
            this.reps = reps;
        }

        public int getWeight() {
            return weight;
        }

        public void setWeight(int weight) {
            this.weight = weight;
        }
    }
    public static class Stats
    {
        private int calories;
        private String start_time;
        private String end_time;
        private String minutes;

        public int getCalories() {
            return calories;
        }

        public void setCalories(int calories) {
            this.calories = calories;
        }

        public String getStart_time() {
            return start_time;
        }

        public void setStart_time(String start_time) {
            this.start_time = start_time;
        }

        public String getEnd_time() {
            return end_time;
        }

        public void setEnd_time(String end_time) {
            this.end_time = end_time;
        }

        public String getMinutes() {
            return minutes;
        }

        public void setMinutes(String minutes) {
            this.minutes = minutes;
        }
    }
}

