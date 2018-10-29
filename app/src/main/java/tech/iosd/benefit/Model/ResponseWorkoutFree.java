package tech.iosd.benefit.Model;

import java.util.ArrayList;

public class ResponseWorkoutFree
{
    private boolean success;
    public String message;
    private ArrayList<Data>  data;

    public ResponseWorkoutFree(boolean success,  ArrayList<Data> data) {
        this.success = success;
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public  ArrayList<Data> getData() {
        return data;
    }

    public void setData( ArrayList<Data> data) {
        this.data = data;
    }

    public class Data {
        private String _id;
        private String name;
        private String search_name;
        private String image;
        private String description;
        private int videoCount;



        private ArrayList<Exercise> exercises;

        public Data(String _id, String name, String search_name, String description , String image, int videoCount, ArrayList<Exercise> exercises) {
            this._id = _id;
            this.name = name;
            this.search_name = search_name;
            this.description = description;
            this.videoCount = videoCount;
            this.exercises = exercises;
            this.image = image;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getSearch_name() {
            return search_name;
        }

        public void setSearch_name(String search_name) {
            this.search_name = search_name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public ArrayList<Exercise> getExercises() {
            return exercises;
        }

        public void setExercises(ArrayList<Exercise> exercises) {
            this.exercises = exercises;
        }

        public int getVideoCount() {
            return videoCount;
        }

        public void setVideoCount(int videoCount) {
            this.videoCount = videoCount;
        }
    }
}
