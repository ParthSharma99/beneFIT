package tech.iosd.benefit.Model;

/**
 * Created by SAM33R on 06-06-2018.
 */

public class ResponseForMeasurementsUpdate {
    String success;
    String message;

    public class Measure {
//        public int age;
        public int height;
        public int weight;
        public int waist;
        public int neck;
        public int hip;

        public int getWeight() {
            return weight;
        }

        public void setWeight(int weight) {
            this.weight = weight;
        }

//        public int getAge() {
//            return age;
//        }
//
//        public void setAge(int age) {
//            this.age = age;
//        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public int getWaist() {
            return waist;
        }

        public void setWaist(int waist) {
            this.waist = waist;
        }

        public int getNeck() {
            return neck;
        }

        public void setNeck(int neck) {
            this.neck = neck;
        }

        public int getHip() {
            return hip;
        }

        public void setHip(int hip) {
            this.hip = hip;
        }
    }

    public Measure data;

    public void setMessage(String message) {
        this.message = message;
    }



    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }
}
