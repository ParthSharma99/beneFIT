package tech.iosd.benefit.Model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;



public class ResponseFood
{
    private int quantity;
    private String unit;
    private NutritionFood food;

    public ResponseFood(int quantity, String unit,NutritionFood food) {
        this.quantity = quantity;
        this.unit = unit;
        this.food=food;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public NutritionFood getFood() {
        return food;
    }

    public void setFood(NutritionFood food) {
        this.food = food;
    }

    public class NutritionFood
    {
        private String _id;
        private String name;
        private float calories;
        private float proteins;
        private float fats;
        private float carbs;
        private float fibre;
        private float sugar;
        private String unit;

        private Size size;

        public class Size{
            //only one of the size will be used depending what is send from the server
            public int piece;
            public int bowl;
            public int katori;
            public int serve;
            public int gram;

            public Size() {
                this.piece = 0;
                this.bowl = 0;
                this.katori = 0;
                this.serve = 0;
                this.gram = 0;
            }
        }

        public Size getSize() {
            return size;
        }

        public String getId() {
            return _id;
        }

        public void setId(String id) {
            this._id = id;
        }

        public float getCalories() {
            return calories;
        }

        public void setCalories(float calories) {
            this.calories = calories;
        }

        public float getProteins() {
            return proteins;
        }

        public void setProteins(float proteins) {
            this.proteins = proteins;
        }

        public float getFats() {
            return fats;
        }

        public void setFats(float fats) {
            this.fats = fats;
        }

        public float getCarbs() {
            return carbs;
        }

        public void setCarbs(float carbs) {
            this.carbs = carbs;
        }

        public float getFibre() {
            return fibre;
        }

        public void setFibre(float fibre) {
            this.fibre = fibre;
        }

        public float getSugar() {
            return sugar;
        }

        public void setSugar(Integer sugar) {
            this.sugar = sugar;
        }

        public String getUnit() {
            if(size.gram != 0){
                return "gram";
            }else if(size.bowl!= 0){
                return "bowl";
            }else if(size.katori!= 0){
                return "katori";
            }else if(size.piece!= 0){
                return "piece";
            }else{
                return "serve";
            }
        }
        public int getDefaultSize(){
            if(size.gram != 0){
                return size.gram;
            }else if(size.bowl != 0){
                return size.bowl;
            }else if(size.katori!= 0){
                return size.katori;
            }else if(size.piece!= 0){
                return size.piece;
            }else{
                return size.serve;
            }
        }

        public void setUnit(String unit) {
            this.unit = unit;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}

