package tech.iosd.benefit.Model;

/**
 * Created by SAM33R on 28-06-2018.
 */

public class Exercise {
    private String _id;
    private int reps;
    private int rest,sets,mets,timeTaken;
    private ExerciseWithoutID exercise;

    public Exercise(String _id, int reps, int rest, int mets, int timeTaken, ExerciseWithoutID exercise) {
        this._id = _id;
        this.reps = reps;
        this.rest = rest;
        this.mets = mets;
        this.timeTaken = timeTaken;
        this.exercise = exercise;
    }

    public int getSets() {
        return sets;
    }

    public void setSets(int sets) {
        this.sets = sets;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public int getReps() {
        return reps;
    }

    public void setReps(int reps) {
        this.reps = reps;
    }

    public int getRest() {
        return rest;
    }

    public void setRest(int rest) {
        this.rest = rest;
    }

    public ExerciseWithoutID getExercise() {
        return exercise;
    }

    public void setExercise(ExerciseWithoutID exercise) {
        this.exercise = exercise;
    }

    public int getMets() {
        return mets;
    }

    public void setMets(int mets) {
        this.mets = mets;
    }

    public int getTimeTaken() {
        return timeTaken;
    }

    public void setTimeTaken(int timeTaken) {
        this.timeTaken = timeTaken;
    }


    public class ExerciseWithoutID{
        private String _id;
        private int sno;
        private String name;
        private String intensity;
        private float mets;
        private float timeTaken;
        private String base;
        private String type;
        private boolean videoA;
        private boolean videoB;
        private int __v;
        private String flow;
        private int totalNoVideo=1;
        public boolean isDownloaded =false;
        public boolean isDownloading = false;
        public Integer progess;

        public String getFlow() {
            return flow;
        }

        public void setFlow(String flow) {
            this.flow = flow;
        }

        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
        }

        public int getSno() {
            return sno;
        }

        public void setSno(int sno) {
            this.sno = sno;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getIntensity() {
            return intensity;
        }

        public void setIntensity(String intensity) {
            this.intensity = intensity;
        }

        public float getMets() {
            return mets;
        }

        public void setMets(float mets) {
            this.mets = mets;
        }

        public float getTimeTaken() {
            return timeTaken;
        }

        public void setTimeTaken(float timeTaken) {
            this.timeTaken = timeTaken;
        }

        public String getBase() {
            return base;
        }

        public void setBase(String base) {
            this.base = base;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public boolean isVideoA() {
            return videoA;
        }

        public void setVideoA(boolean videoA) {
            this.videoA = videoA;
        }

        public boolean isVideoB() {
            return videoB;
        }

        public void setVideoB(boolean videoB) {
            this.videoB = videoB;
        }

        public int get__v() {
            return __v;
        }

        public void set__v(int __v) {
            this.__v = __v;
        }

        public int getTotalNoVideo()
        {
            if(videoA&&videoB)
                totalNoVideo=3;
            else if(videoB||videoA)
                totalNoVideo=2;
            return totalNoVideo;
        }

        public void setTotalNoVideo(int totalNoVideo) {
            this.totalNoVideo = totalNoVideo;
        }
    }

}
