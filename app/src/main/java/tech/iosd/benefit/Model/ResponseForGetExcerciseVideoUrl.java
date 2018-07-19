package tech.iosd.benefit.Model;

/**
 * Created by SAM33R on 29-06-2018.
 */

public class ResponseForGetExcerciseVideoUrl {
    private  boolean success;
    private String message;
    private String data;

    public ResponseForGetExcerciseVideoUrl(boolean success, String message, String data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
    public class Data{
        String tutorial;
        String videoA;
        String videoB;

        public String getTutorial() {
            return tutorial;
        }

        public void setTutorial(String tutorial) {
            this.tutorial = tutorial;
        }

        public String getVideoA() {
            return videoA;
        }

        public void setVideoA(String videoA) {
            this.videoA = videoA;
        }

        public String getVideoB() {
            return videoB;
        }

        public void setVideoB(String videoB) {
            this.videoB = videoB;
        }
    }
}
