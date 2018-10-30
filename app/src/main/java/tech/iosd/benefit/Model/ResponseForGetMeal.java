package tech.iosd.benefit.Model;

import java.util.ArrayList;

/**
 * Created by SAM33R on 15-06-2018.
 */

public class ResponseForGetMeal {
    private boolean success;
    private String message;
    public Data data;
    private Calories calories;
    private int __v;

    public Calories getCalories() {
        return calories;
    }

    public void setCalories(Calories calories) {
        this.calories = calories;
    }

    public class Data {
        public String _id;
        private String client;
        private String date;
        private String type;

        public ArrayList<Food> food;


        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
        }

        public String getClient() {
            return client;
        }

        public void setClient(String client) {
            this.client = client;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public ArrayList<Food> getFood() {
            return food;
        }

        public void setFood(ArrayList<Food> food) {
            this.food = food;
        }
    }
    public static class Calories
    {
        private int workout;
        private int tracking;

        public int getWorkout() {
            return workout;
        }

        public void setWorkout(int workout) {
            this.workout = workout;
        }

        public int getTracking() {
            return tracking;
        }

        public void setTracking(int tracking) {
            this.tracking = tracking;
        }
    }
    public static class Food {
        private int quantity;
        private String unit;
        private int unitInt;
        private MealLogFood item;
        private String id;


        public Food(int quantity, MealLogFood item) {
            this.quantity = quantity;
            this.item = item;
        }

        public String getUnit() {
            return unit;
        }

        public void setUnit(String unit) {
            this.unit = unit;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public MealLogFood getItem() {
            return item;
        }

        public void setItem(MealLogFood food) {
            this.item = food;
        }
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }



    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    public boolean removeMealAt(int position){
        if (position<0 || position > data.getFood().size() ){
            return false;
        }else {
            data.getFood().remove(position);
            return true;
        }
    }
}
