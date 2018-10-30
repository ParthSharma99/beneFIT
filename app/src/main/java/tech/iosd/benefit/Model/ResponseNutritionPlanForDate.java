package tech.iosd.benefit.Model;

import java.util.ArrayList;

public class ResponseNutritionPlanForDate
{
    private boolean success;
    private ArrayList<Data> data;

    public ResponseNutritionPlanForDate(boolean success, ArrayList<Data> data) {
        this.success = success;

        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public ArrayList<Data> getData() {
        return data;
    }

    public void setData(ArrayList<Data> data) {
        this.data = data;
    }


    public class Data{

        private String _id;
        private String client;
        private int type;
        private ResponseNutrition nutrition;
        public Data(String _id, String client, int type, ResponseNutrition nutrition) {
            this._id = _id;
            this.client = client;
            this.type = type;
            this.nutrition = nutrition;
        }

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

        public ResponseNutrition getNutrition() {
            return nutrition;
        }

        public void setNutrition(ResponseNutrition nutrition) {
            this.nutrition= nutrition;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }
    }
}
