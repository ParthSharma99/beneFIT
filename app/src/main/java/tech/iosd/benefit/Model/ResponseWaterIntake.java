package tech.iosd.benefit.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ResponseWaterIntake {

    public ArrayList<Data> data;
    private String message;
    private Boolean success;

    public ArrayList<Data> getData() {
        return data;
    }

    public void setData(ArrayList<Data> data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public static class Data {

        @SerializedName("__v")
        private Long _V;
        @Expose
        private String _id;
        @Expose
        private String client;
        @Expose
        private String date;
        @Expose
        private int target;
        @Expose
        private int consumed;

        public Long get_V() {
            return _V;
        }

        public void set_V(Long _V) {
            this._V = _V;
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

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public int getTarget() {
            return target;
        }

        public int getConsumed() {
            return consumed;
        }

        public void setTarget(int target) {
            this.target = target;
        }

        public void setConsumed(int consumed) {
            this.consumed = consumed;
        }
    }

}
