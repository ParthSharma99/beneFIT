package tech.iosd.benefit.Model;

public class VideoPlayerItem {

    public static final int TYPE_FOLLOW=1000;//sets
    public static final int TYPE_REPETITIVE =1001;//sets+reps

    //type = repetitive workflow
    // Tutorial ----> singleVideo (repeated for total reps) ----> Rest --\
    //                         ^----(looped for number of sets)----------/

    //type = follow workflow
    // Tutorial ----> Rest ------------\
    //    ^----Looped for total sets---/


    //common..............
    private int type,sets;// type for managing control, sets for number of repetitions
    private String videoName;//for display
    //rest info
    private int restTimeSec;//rest time period

    private int totalReps;//

    private String introVideo,singleRepVideo;

    private int currentSet,currentRep;
    private Boolean introComp = false, isResting = false;//for seeing state, intro/tutorial OR singleVidLoop

    public VideoPlayerItem() {
    }

    public VideoPlayerItem(Exercise exercise){

        if(exercise.getExercise().getFlow().equals("repetitive")){
            this.type = TYPE_REPETITIVE;
        }else if(exercise.getExercise().getFlow().equals("follow")){
            this.type = TYPE_FOLLOW;
        }

        this.sets = exercise.getSets();
        this.videoName = exercise.getExercise().getName();
        this.restTimeSec = exercise.getRest();
        this.totalReps = exercise.getReps();


        this.introVideo = exercise.getExercise().get_id()+".mp4";
        this.singleRepVideo = exercise.getExercise().get_id()+"_a.mp4";


        //defaults
        this.currentRep = 0;
        this.currentSet = 0;
    }

    public int incrementCurrentSet(){
        currentSet++;
        currentRep=0;
        return currentSet;
    }
    public int incrementCurrentRep(){
        currentRep++;
        return currentRep;
    }

    public int getSetsRemaining(){
        return (sets - currentSet);
    }

    public int getRepsRemaining(){
        return (totalReps - currentRep);
    }

    public Boolean getIntroComp() {
        return introComp;
    }

    public Boolean getResting() {
        return isResting;
    }

    public void setResting(Boolean resting) {
        isResting = resting;
    }

    public void setIntroComp(Boolean introComp) {
        this.introComp = introComp;
    }

    public int getCurrentSet() {
        return currentSet;
    }

    public void setCurrentSet(int currentSet) {
        this.currentSet = currentSet;
    }

    public int getCurrentRep() {
        return currentRep;
    }

    public void setCurrentRep(int currentRep) {
        this.currentRep = currentRep;
    }

    public String getIntroVideo() {
        return introVideo;
    }

    public void setIntroVideo(String introVideo) {
        this.introVideo = introVideo;
    }

    public String getSingleRepVideo() {
        return singleRepVideo;
    }

    public void setSingleRepVideo(String singleRepVideo) {
        this.singleRepVideo = singleRepVideo;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getSets() {
        return sets;
    }

    public void setSets(int sets) {
        this.sets = sets;
    }

    public String getVideoName() {
        return videoName;
    }

    public void setVideoName(String videoName) {
        this.videoName = videoName;
    }

    public int getRestTimeSec() {
        return restTimeSec;
    }

    public void setRestTimeSec(int restTimeSec) {
        this.restTimeSec = restTimeSec;
    }

    public int getTotalReps() {
        return totalReps;
    }

    public void setTotalReps(int totalReps) {
        this.totalReps = totalReps;
    }
}
