package tech.iosd.benefit.VideoPlayer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.Fragment;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.gson.Gson;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import tech.iosd.benefit.DashboardFragments.SaveWorkout;
import tech.iosd.benefit.Model.DatabaseHandler;
import tech.iosd.benefit.Model.VideoPlayerItem;
import tech.iosd.benefit.R;
import tech.iosd.benefit.SaveWorkoutActivity;

/**
 * Created by Prerak Mann on 28/06/18.
 */
public class VideoPlayerActivity extends Activity implements SurfaceHolder.Callback, MediaPlayer.OnPreparedListener, VideoControllerView.MediaPlayerControl, MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener, TextToSpeech.OnInitListener {

    SharedPreferences sharedPreferences1;
    SurfaceView videoSurface;
    Calendar c = Calendar.getInstance();
    SimpleDateFormat df;
    MediaPlayer player;
    VideoControllerView controller;
    FragmentManager fm;
    VideoFormView videoFormView;
    TextView dura2, setsCounter, middleCount, restCounter, repsCounter;
    int screenTime;
    private DatabaseHandler db;
    CountDownTimer countDownTimer;
    public static final String TAG = "chla";
    ProgressDialog progressDialog;
    Button skipIntroBtn, skipRestBtn;
    private TextToSpeech tts;
    CheckBox soundOn;
    boolean isFreeWorkout=true;
    Boolean isSoundOn = true;
    Gson gson = new Gson();

    ArrayList<String> videoPlayerItemList = new ArrayList<>();
    int currentItem;
    View restView;//for hiding layouts at different points
    private VideoPlayerItem videoItem = null;//has all details of items

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);
        db = new DatabaseHandler(this);
        videoPlayerItemList = getIntent().getStringArrayListExtra("videoItemList");
        isFreeWorkout=getIntent().getExtras().getBoolean("FREEWORKOUT",false);
        currentItem = 0;
        sharedPreferences1 = getSharedPreferences("SAVE_EXERCISE", MODE_PRIVATE);
        setVideoItem();

        initViews();

        //player is initiated in method surface created
    }
    private void initViews(){
        restView = (View) findViewById(R.id.restView);
        sharedPreferences = getSharedPreferences("SAVE_EXERCISE", MODE_PRIVATE);
        restCounter = (TextView) findViewById(R.id.restCounter);
        repsCounter = (TextView) findViewById(R.id.repsTextView);
        skipRestBtn = (Button) findViewById(R.id.skipRest);
        fm = getFragmentManager();
        skipRestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(countDownTimer!=null)
                    countDownTimer.cancel();//stopping rest counter
                videoItem.setResting(false);
                startVideo();
            }
        });

        dura2 = findViewById(R.id.duration2);
        skipIntroBtn = findViewById(R.id.btn_skip_intro);
        skipIntroBtn.setOnClickListener(skipIntroListner);
        setsCounter = findViewById(R.id.no_of_sets);
        middleCount = findViewById(R.id.countInBetweenScreen);
        tts = new TextToSpeech(this, this);
        soundOn = findViewById(R.id.muteCheckBox);

        videoSurface = (SurfaceView) findViewById(R.id.videoSurface);
        SurfaceHolder videoHolder = videoSurface.getHolder();
        videoHolder.addCallback(this);

        soundOn.setChecked(isSoundOn);
        soundOn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    player.setVolume(1, 1);
                    isSoundOn = true;
                } else {
                    player.setVolume(0, 0);
                    isSoundOn = false;
                }
            }
        });

        controller = new VideoControllerView(this);
        videoFormView=new VideoFormView(this);

        //this prevents mediaplayerview to open when form is being displayed
        videoFormView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        //player is initialised after surface is created (onSurfaceCreated method)

    }

    @Override
    public void onBackPressed()
    {
        progressDialog.dismiss();
        super.onBackPressed();
    }
    private void startNextInList(){
        if(currentItem < videoPlayerItemList.size() -1 ){
            currentItem++;
            setVideoItem();
            startVideo();
        }else {
            Intent intent=new Intent(this, SaveWorkoutActivity.class);
            intent.putExtra("VIDEO_ITEM", videoPlayerItemList);
            Bundle bundle=new Bundle();
            bundle.putBoolean("FREEWORKOUT",isFreeWorkout);
            intent.putExtras(bundle);
            c = Calendar.getInstance();
            df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            String fomattedTime=df.format(c.getTime());
            Toast.makeText(this, fomattedTime, Toast.LENGTH_SHORT).show();
            sharedPreferences1.edit().putString("END_TIME",fomattedTime).apply();
            long time= System.currentTimeMillis();
            sharedPreferences1.edit().putFloat("END_TIME_MILLIS",time).apply();

            startActivity(intent);
            finish();
        }
    }
    private void startPreviousInList(){
        if(currentItem >0){
            currentItem--;
            setVideoItem();
            startVideo();
        }else {
            finish();
        }
    }

    private void setVideoItem(){
        if(videoPlayerItemList.size()>currentItem)
        videoItem = gson.fromJson(videoPlayerItemList.get(currentItem),VideoPlayerItem.class);
    }


    private void showStopDialog(){
        pause();

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Stop Workout?");
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                start();
            }
        });
        alert.setCancelable(false);
        alert.show();
    }
    private void showNextDialog(int next1prev0){
        pause();

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        if(next1prev0==1)
            alert.setTitle("Skip to Next Exercise?");
        else
            alert.setTitle("Go back to previous Exercise?");

        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(next1prev0==1)
                {
                    showFormScreen();
                }
                else
                    startPreviousInList();
            }
        });
        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                start();
            }
        });
        alert.setCancelable(false);
        alert.show();
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        hideNavAndStatus();
    }

    private void hideNavAndStatus() {
        //hides navigationbar and statusbar
        videoSurface.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    private void showRestScreen() {
        //cleaning up
        if (player != null) {
            player.stop();
        }
        NumberFormat f = new DecimalFormat("00");
        //hide other layouts
        controller.hide();
        controller.setEnabled(false); //controller disabled so that it doesn't show on touch
        hideAllViews();
        //show rest screen
        //restView.setVisibility(View.VISIBLE);
        videoItem.setResting(true);
        videoFormView.hide();
        showWeightFormScreen();

//        if (videoFormView!=null)
//            videoFormView.hide();
//        videoItem.setResting(false);
//        startVideo();
        //set timer to stop resting
//        if(countDownTimer!=null)
//            countDownTimer.cancel();
//        countDownTimer = new CountDownTimer(videoItem.getRestTimeSec()*1000,1000) {
//            @Override
//            public void onTick(long millisUntilFinished) {
//                int sec = (int)millisUntilFinished/1000;
//                int min = (int)millisUntilFinished/60000;
//                YoYo.with(Techniques.ZoomIn).duration(800).playOn(restCounter);
//                restCounter.setText(f.format(min) + ":" + f.format(sec));
//            }
//
//            @Override
//            public void onFinish() {//skip button
//                videoItem.setResting(false);
//                startVideo();
//            }
//        }.start();
    }
    boolean displaySets=true;
    boolean isWeight=true;
    boolean isReps=true;
    boolean result=false;
    private void showWeightFormScreen()
    {
        displaySets=false;
        controller.setEnabled(false);
        if (player != null) {
            player.stop();
        }
        if(isWeight&&videoItem.getTypeExercise().equals("Dumbell/Barbell/Gym"))
        {
            Log.d("Entering..","weight");
            //Toast.makeText(VideoPlayerActivity.this, ""+videoFormView.numberPicker.getValue(), Toast.LENGTH_SHORT).show();
            //sharedPreferences.edit().putInt("Weight"+videoItem.getVideoName(),videoFormView.numberPicker.getValue()).apply();
            videoFormView.setAnchorView((FrameLayout) findViewById(R.id.videoSurfaceContainer), videoItem.getSets(), videoItem.getVideoName(), videoItem.getTotalReps(), displaySets,isWeight);
            isWeight=false;
            videoFormView.submitForm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    Toast.makeText(VideoPlayerActivity.this, ""+videoFormView.numberPicker.getValue(), Toast.LENGTH_SHORT).show();
                    sharedPreferences.edit().putInt("Weight"+videoItem.getVideoName(),videoFormView.numberPicker.getValue()).apply();
                    videoFormView.hide();
                    showWeightFormScreen();
                }
            });
            videoFormView.show();
        }
        else if(isReps&&videoItem.getTotalReps()>0)
        {
            Log.d("Entering..","reps");
            isReps=false;
            videoFormView.setAnchorView((FrameLayout) findViewById(R.id.videoSurfaceContainer), videoItem.getSets(), videoItem.getVideoName(), videoItem.getTotalReps(), displaySets,false);
            videoFormView.submitForm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    Toast.makeText(VideoPlayerActivity.this, ""+videoFormView.numberPicker.getValue(), Toast.LENGTH_SHORT).show();
                    sharedPreferences.edit().putInt("RepsNo"+videoItem.getVideoName(),videoFormView.numberPicker.getValue()).apply();
                    videoFormView.hide();
                    videoItem.setResting(false);
                    startVideo();
                }
            });
            videoFormView.show();
        }
        else
        {
            SharedPreferences sharedPreferences1 = getSharedPreferences("SAVE_EXERCISE", MODE_PRIVATE);
            int reps=sharedPreferences1.getInt("RepsNo"+videoItem.getVideoName(),10);
            int caloriesBurnt=sharedPreferences1.getInt("CaloriesBurnt",0);
            float mets=videoItem.getMets();
            float timeTaken=videoItem.getTimeTaken();
            int weightUser=db.getUserWeight();
            Log.d("timeTaken",timeTaken+"");
            Log.d("mets",mets+"");
            caloriesBurnt=(int)(caloriesBurnt+(mets*weightUser*((reps*timeTaken)/3600)));
            Log.d("caloriesBurnt",""+caloriesBurnt);
            sharedPreferences.edit().putInt("CaloriesBurnt",caloriesBurnt).apply();
            result=true;
            isWeight=true;
            displaySets=true;
            isReps=true;
        }
    }

    SharedPreferences sharedPreferences;
    private void showFormScreen()
    {
        controller.setEnabled(false);
        if (player != null) {
            player.stop();
        }
        if(isWeight&&videoItem.getTypeExercise().equals("Dumbell/Barbell/Gym"))
        {
            //Toast.makeText(VideoPlayerActivity.this, ""+videoFormView.numberPicker.getValue(), Toast.LENGTH_SHORT).show();
            //sharedPreferences.edit().putInt("Weight"+videoItem.getVideoName(),videoFormView.numberPicker.getValue()).apply();
            videoFormView.setAnchorView((FrameLayout) findViewById(R.id.videoSurfaceContainer), videoItem.getSets(), videoItem.getVideoName(), videoItem.getTotalReps(), displaySets,isWeight);
            isWeight=false;
            videoFormView.submitForm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    Toast.makeText(VideoPlayerActivity.this, ""+videoFormView.numberPicker.getValue(), Toast.LENGTH_SHORT).show();
                    sharedPreferences.edit().putInt("Weight"+videoItem.getVideoName(),videoFormView.numberPicker.getValue()).apply();
                    videoFormView.hide();
                    showFormScreen();
                }
            });
            videoFormView.show();
        }
        else if(displaySets && videoItem.getSets()>1)
        {
            videoFormView.setAnchorView((FrameLayout) findViewById(R.id.videoSurfaceContainer), videoItem.getSets(), videoItem.getVideoName(), videoItem.getTotalReps(), displaySets,false);
            displaySets=false;
            videoFormView.submitForm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    Toast.makeText(VideoPlayerActivity.this, ""+videoFormView.numberPicker.getValue(), Toast.LENGTH_SHORT).show();
                    sharedPreferences.edit().putInt("SetNo"+videoItem.getVideoName(),videoFormView.numberPicker.getValue()).apply();
                    videoFormView.hide();
                    showFormScreen();
                }
            });
            videoFormView.show();
        }
        else if(isReps&&videoItem.getTotalReps()>0)
        {
           isReps=false;
            videoFormView.setAnchorView((FrameLayout) findViewById(R.id.videoSurfaceContainer), videoItem.getSets(), videoItem.getVideoName(), videoItem.getTotalReps(), displaySets,false);
            videoFormView.submitForm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    Toast.makeText(VideoPlayerActivity.this, ""+videoFormView.numberPicker.getValue(), Toast.LENGTH_SHORT).show();
                    sharedPreferences.edit().putInt("RepsNo"+videoItem.getVideoName(),videoFormView.numberPicker.getValue()).apply();
                    videoFormView.hide();
                    showFormScreen();
                }
            });
            videoFormView.show();
        }
        else
        {
            Log.d("reahed else","reached else");
            SharedPreferences sharedPreferences1 = getSharedPreferences("SAVE_EXERCISE", MODE_PRIVATE);
            int sets=sharedPreferences1.getInt("SetNo"+videoItem.getVideoName(),1);
            int reps=sharedPreferences1.getInt("RepsNo"+videoItem.getVideoName(),10);
            int caloriesBurnt=sharedPreferences1.getInt("CaloriesBurnt",0);
            float mets=videoItem.getMets();
            float timeTaken=videoItem.getTimeTaken();
            int weightUser=db.getUserWeight();
            Log.d("timeTaken",timeTaken+"");
            Log.d("mets",""+mets);
            caloriesBurnt=(int)(caloriesBurnt+(mets*weightUser*((sets*reps*timeTaken)/3600))) ;
            Log.d("caloriesBurnt",""+caloriesBurnt);
            sharedPreferences.edit().putInt("CaloriesBurnt",caloriesBurnt).apply();
            isWeight=true;
            displaySets=true;
            isReps=true;
            startNextInList();
        }
    }

    private void hideAllViews() {
        //introviews
        skipIntroBtn.setVisibility(View.GONE);
        //allTimeViews
        soundOn.setVisibility(View.GONE);
        dura2.setVisibility(View.GONE);
        setsCounter.setVisibility(View.GONE);
        //repsview
        middleCount.setVisibility(View.GONE);
        repsCounter.setVisibility(View.GONE);
        //restview
        restView.setVisibility(View.GONE);
    }

    private void showAllTimeViews(){
        soundOn.setVisibility(View.VISIBLE);
        dura2.setVisibility(View.VISIBLE);
    }
    private void showRepsViews(){
        repsCounter.setVisibility(View.VISIBLE);
        setsCounter.setVisibility(View.VISIBLE);
    }
    private void showIntroViews(){

        skipIntroBtn.setVisibility(View.VISIBLE);
    }
    private void showTutorialView(){

        setsCounter.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!videoItem.getResting()) {//touch controls only if user is not on rest screen
            controller.show();
            player.pause();
            if (countDownTimer != null) {
                countDownTimer.cancel();
            }

            hideAllViews();
        }
        return false;
    }

    // Implement SurfaceHolder.Callback
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.w("pkmn", "surface created");
        player = new MediaPlayer();
        player.setDisplay(holder);
        startVideo();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
    }
    // End SurfaceHolder.Callback

    // Implement MediaPlayer.OnPreparedListener


    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        if(progressDialog!=null)
            progressDialog.cancel();
        Toast.makeText(this,"Error Playing, file may be corrupt",Toast.LENGTH_LONG).show();
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        //show relevant views here
        hideAllViews();
        showAllTimeViews();

        NumberFormat f = new DecimalFormat("00");

        controller.setMediaPlayer(this);
        controller.setAnchorView((FrameLayout) findViewById(R.id.videoSurfaceContainer), videoItem.getSets(), videoItem.getVideoName());
        player.start();
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        Log.d(TAG, "onPrepared: " + getDuration());
        int duration = getDuration();

        if (videoItem.getType() == VideoPlayerItem.TYPE_FOLLOW) {
            showTutorialView();
            duration = duration * videoItem.getSetsRemaining();
            setsCounter.setText("Sets: " + videoItem.getCurrentSet() + "/" + videoItem.getSets());
        } else if (videoItem.getType() == VideoPlayerItem.TYPE_REPETITIVE) {
            if (videoItem.getIntroComp()) {//if it not intro-video
                duration = duration * videoItem.getTotalReps();
                showRepsViews();
                setsCounter.setText("Sets: " + videoItem.getCurrentSet() + "/" + videoItem.getSets());
            } else {
                showIntroViews();
            }
        }

        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        countDownTimer = new CountDownTimer(duration, 1000) {//geriye sayma

            public void onTick(long millisUntilFinished) {
                long hour = (millisUntilFinished / 3600000) % 24;
                long min = (millisUntilFinished / 60000) % 60;
                long sec = (millisUntilFinished / 1000) % 60;

                if (videoItem.getType() == VideoPlayerItem.TYPE_FOLLOW) {
                    dura2.setText("Total Time Remaining : \n" + f.format(hour) + ":" + f.format(min) + ":" + f.format(sec));
                } else if (videoItem.getType() == VideoPlayerItem.TYPE_REPETITIVE) {
                    if (videoItem.getIntroComp()) {//if intro is completed
                        //TODO some problem in videoItem.getItroCmp time is ot displayed properly so changed the reps counter textview position
                        dura2.setText("Total Time Remaining : \n" + f.format(hour) + ":" + f.format(min) + ":" + f.format(sec));
                    } else {//for intro video
                        dura2.setText(videoItem.getVideoName() + "\n" + "Starts in " + f.format(hour) + ":" + f.format(min) + ":" + f.format(sec));
                    }
                }
            }

            public void onFinish() {//keep empty
            }

        }.start();
    }
// End MediaPlayer.OnPreparedListener


    // Implement VideoMediaController.MediaPlayerControl
    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public int getCurrentPosition() {
        return player.getCurrentPosition();
    }

    @Override
    public int getDuration() {
        return player.getDuration();
    }

    @Override
    public boolean isPlaying() {
        return player.isPlaying();
    }

    @Override
    public void seekTo(int i) {
        player.seekTo(i);
    }

    @Override
    public boolean isFullScreen() {
        return true;
    }

    @Override
    public void toggleFullScreen() {
    }

    @Override
    public void pause() {
        hideAllViews();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        player.pause();
    }

    @Override
    public void start() {//resuming player here
        showAllTimeViews();

        if (videoItem.getType() == VideoPlayerItem.TYPE_FOLLOW) {
            if (!videoItem.getIntroComp()) {
                showTutorialView();
            }
        } else if (videoItem.getType() == VideoPlayerItem.TYPE_REPETITIVE) {
            if (videoItem.getIntroComp()) {
                showRepsViews();
            } else {
                showIntroViews();
            }
        }

        NumberFormat f = new DecimalFormat("00");
        player.start();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        countDownTimer = new CountDownTimer(screenTime, 1000) {//resuming from screenTime                     //geriye sayma

            public void onTick(long millisUntilFinished) {
                long hour = (millisUntilFinished / 3600000) % 24;
                long min = (millisUntilFinished / 60000) % 60;
                long sec = (millisUntilFinished / 1000) % 60;

                if (videoItem.getType() == VideoPlayerItem.TYPE_FOLLOW) {
                    dura2.setText("Total Time Remaining : \n" + f.format(hour) + ":" + f.format(min) + ":" + f.format(sec));
                } else if (videoItem.getType() == VideoPlayerItem.TYPE_REPETITIVE) {
                    if (videoItem.getIntroComp()) {//if intro is completed
                        repsCounter.setText("Reps:"+String.valueOf(videoItem.getCurrentRep()) + "/" + String.valueOf(videoItem.getTotalReps()));
                        dura2.setText("Total Time Remaining : \n" + f.format(hour) + ":" + f.format(min) + ":" + f.format(sec));
                    } else {//for intro video
                        dura2.setText(videoItem.getVideoName() + "\n" + "Starts in " + f.format(hour) + ":" + f.format(min) + ":" + f.format(sec));
                    }
                }
            }

            public void onFinish() {//keep empty
            }

        }.start();
    }

    @Override
    public void setOnScreenTime(int time) {


        if (videoItem.getType() == VideoPlayerItem.TYPE_FOLLOW) {
            int tempTotalDuration = getDuration();
            int ku = tempTotalDuration - time;
            screenTime = tempTotalDuration * videoItem.getSetsRemaining() - ku + 1000;
        } else if (videoItem.getType() == VideoPlayerItem.TYPE_REPETITIVE) {
            int tempTotalDuration = getDuration();
            int ku = tempTotalDuration - time;
            screenTime = tempTotalDuration * videoItem.getRepsRemaining() - ku + 1000;
        }

    }

    @Override
    public void nextVideo() {
        if (player != null) {
            if (videoItem.getType() == VideoPlayerItem.TYPE_FOLLOW) {
//                Toast.makeText(VideoPlayerActivity.this, "Nothing to go forward to", Toast.LENGTH_SHORT).show();
                showNextDialog(1);
            } else if (videoItem.getType() == VideoPlayerItem.TYPE_REPETITIVE) {
                if (videoItem.getIntroComp()) {//if intro is completed
//                    Toast.makeText(VideoPlayerActivity.this, "Nothing to go forward to", Toast.LENGTH_SHORT).show();
                    showNextDialog(1);
                } else {//if intro is not complete
                    hideAllViews();
                    videoItem.setIntroComp(true);
                    startVideo();
                }
            }
        }
    }

    @Override
    public void prevVideo() {
        if (player != null) {
            if (videoItem.getType() == VideoPlayerItem.TYPE_FOLLOW) {
//                Toast.makeText(VideoPlayerActivity.this, "Nothing to go back to", Toast.LENGTH_SHORT).show();
                showNextDialog(0);
            } else if (videoItem.getType() == VideoPlayerItem.TYPE_REPETITIVE) {
                if (videoItem.getIntroComp()) {//if intro is completed go back to intro video
                    hideAllViews();
                    showIntroViews();
                    videoItem.setIntroComp(false);
                    startVideo();
                } else {
//                    Toast.makeText(VideoPlayerActivity.this, "Nothing to go back to", Toast.LENGTH_SHORT).show();
                    showNextDialog(0);
                }
            }

        }
    }

    @Override
    public void Stop() {
        showStopDialog();
    }

    public void startVideo() {
        try {
            progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Please Wait");
            progressDialog.setMessage("STARTING... ");

            progressDialog.setCancelable(false);
            progressDialog.show();
            progressDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    hideNavAndStatus();
                }
            });
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
            player.setOnPreparedListener(this);
            player.setOnErrorListener(this);
            player.setOnCompletionListener(this);

        } catch (IllegalArgumentException | SecurityException | IllegalStateException e) {
            e.printStackTrace();
        }

        player.reset();//so that we can re-initialise player
        if (videoItem.getType() == VideoPlayerItem.TYPE_FOLLOW) {
            if (videoItem.getIntroComp()) {//show rest screen
                showRestScreen();
            } else {//show intro
                try {
                    if (countDownTimer != null) {
                        countDownTimer.cancel();
                    }
//                    Uri video = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.stackpushupsingle);//dummy for now
//                    player.setDataSource(this, video);
                    String path = VideoPlayerActivity.this.getFilesDir().toString()+"/videos/" + videoItem.getIntroVideo();
                    player.setDataSource(path);

                    player.prepareAsync();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this,e.toString(),Toast.LENGTH_LONG).show();
                    progressDialog.cancel();
                }
            }

        } else if (videoItem.getType() == VideoPlayerItem.TYPE_REPETITIVE) {
            if (videoItem.getIntroComp()) {//start single * reps mode here
                try {
                    if (countDownTimer != null) {
                        countDownTimer.cancel();
                    }
//                    Uri video = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.stackpushupsingle);//dummy for now
//                    player.setDataSource(this, video);
                    String path = VideoPlayerActivity.this.getFilesDir().toString()+"/videos/" + videoItem.getSingleRepVideo();
                    player.setDataSource(path);

                    player.prepareAsync();

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this,e.toString(),Toast.LENGTH_LONG).show();
                    progressDialog.cancel();
                }
            } else {//show intro
                try {
                    if (countDownTimer != null) {
                        countDownTimer.cancel();
                    }
//                    Uri video = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.stackpushupsingle);//dummy for now
//                    player.setDataSource(this, video);
                    String path = VideoPlayerActivity.this.getFilesDir().toString()+"/videos/" + videoItem.getIntroVideo();
                    player.setDataSource(path);

                    player.prepareAsync();
                } catch (Exception e) {
                    e.printStackTrace();

                    Toast.makeText(this,e.toString(),Toast.LENGTH_LONG).show();
                    progressDialog.cancel();
                }
            }
        }
    }


    //mediaplayer implement methods
    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        if (videoItem.getType() == VideoPlayerItem.TYPE_FOLLOW) {
            if (videoItem.incrementCurrentSet() < videoItem.getSets()) {
                //show rest screen
                showRestScreen();
            } else {
                mediaPlayer.pause();
                showFormScreen();
                //Toast.makeText(VideoPlayerActivity.this, "End of sets", Toast.LENGTH_SHORT).show();
            }
        } else if (videoItem.getType() == VideoPlayerItem.TYPE_REPETITIVE) {
            if (videoItem.getIntroComp())
            {//if intro is complete
                if (videoItem.incrementCurrentRep() <videoItem.getTotalReps())
                {
                    player.seekTo(0);
                    player.start();
                    repsCounter.setText("Reps:"+videoItem.getCurrentRep()+"/"+videoItem.getTotalReps());
                    repsCounter.setVisibility(View.VISIBLE);
                    middleCount.setVisibility(View.VISIBLE);
                    middleCount.setText(String.valueOf(videoItem.getCurrentRep()));
                    if (isSoundOn) {
                        tts.speak(String.valueOf(videoItem.getCurrentRep()), TextToSpeech.QUEUE_FLUSH, null);
                    }
                    //zoom in , fadeout and then remove
                    YoYo.with(Techniques.ZoomIn).duration(1000).playOn(middleCount);
                    YoYo.with(Techniques.FadeOut).duration(1000).delay(1000).playOn(middleCount);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            middleCount.setVisibility(View.INVISIBLE);
                        }
                    }, 2000);
                } else {
                    tts.speak(String.valueOf(videoItem.getCurrentRep()), TextToSpeech.QUEUE_FLUSH, null);
                    if (videoItem.incrementCurrentSet() < videoItem.getSets()) {
                        showRestScreen();
                    } else {
                        mediaPlayer.pause();
                        showFormScreen();
                        //Toast.makeText(VideoPlayerActivity.this, "sets finished", Toast.LENGTH_SHORT).show();
                    }
                }
            } else {//for intro video
                videoItem.setIntroComp(true);
                startVideo();
            }
        }
    }

    private View.OnClickListener skipIntroListner = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            hideAllViews();
            videoItem.setIntroComp(true);
            startVideo();
        }
    };

    //text to speech listener
    @Override
    public void onInit(int i) {
        if (i == TextToSpeech.SUCCESS) {
            int result = tts.setLanguage(Locale.US);
            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            }
        } else {
            Log.e("TTS", "Initilization Failed!");
        }
    }

    @Override
    public void onDestroy() {
        // Don't forget to shutdown tts!
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }
    @Override
    public void onStop()
    {
        if(player.isPlaying())
        player.pause();
        super.onStop();
    }
    @Override
    public void onPause()
    {
        if(player.isPlaying())
        player.pause();
        super.onPause();
    }
}



// End VideoMediaController.MediaPlayerControl


