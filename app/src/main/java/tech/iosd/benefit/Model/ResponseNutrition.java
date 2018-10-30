package tech.iosd.benefit.Model;

import java.util.ArrayList;

public class ResponseNutrition
{
    private String id;
    private String description;
    private ArrayList<ResponseFood> foods;

    public ResponseNutrition(String id, String description,ArrayList<ResponseFood> foods) {
        this.id = id;
        this.description = description;
        this.foods=foods;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<ResponseFood> getFoods() {
        return foods;
    }

    public void setFoods(ArrayList<ResponseFood> foods) {
        this.foods = foods;
    }
}

