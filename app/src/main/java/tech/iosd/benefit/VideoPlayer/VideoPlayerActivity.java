package tech.iosd.benefit.VideoPlayer;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import tech.iosd.benefit.R;

/**
 * Created by Prerak Mann on 28/06/18.
 */
public class VideoPlayerActivity extends Activity implements SurfaceHolder.Callback, MediaPlayer.OnPreparedListener, VideoControllerView.MediaPlayerControl, MediaPlayer.OnCompletionListener, TextToSpeech.OnInitListener {

    SurfaceView videoSurface;
    MediaPlayer player, player2;
    VideoControllerView controller;
    TextView dura, dura2, NoOfSets, middleCount;
    Boolean count;
    int screenTime, screenTime2;
    CountDownTimer countDownTimer;
    public static final String TAG = "chla";
    ProgressDialog progressDialog;
    Button skipIntroBtn;
    int noOfSets = 2;
    String videoName;
    int IntroReal;
    int videoNo;
    int currentSet = 0;
    int tottalReps = 4;
    private TextToSpeech tts;
    int flag = 0;

    CheckBox soundOn;
    Boolean isSoundOn = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);
        dura = findViewById(R.id.duration);
        dura2 = findViewById(R.id.duration2);
        skipIntroBtn = findViewById(R.id.btn_skip_intro);
        skipIntroBtn.setOnClickListener(skipIntroListner);
        NoOfSets = findViewById(R.id.no_of_sets);
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

        // player = new MediaPlayer();
        controller = new VideoControllerView(this);
        startNext();

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

    private void showFeedBack() {//end of workout
        //cleaning up
        if (player != null)
            player.stop();

        controller.hide();
        controller.setEnabled(false);

        tts.stop();
        tts.shutdown();

        //removing all views and placing feedback view in its place
        LinearLayout linearLayout = findViewById(R.id.video_container);
        linearLayout.removeAllViews();
        View view = getLayoutInflater().inflate(R.layout.feed_back, linearLayout, false);
        linearLayout.addView(view);

        Button restart = (Button)view.findViewById(R.id.restart);
        restart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recreate();
            }
        });

        NumberPicker numberPicker = (NumberPicker)view.findViewById(R.id.numberPicker);
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(10);
        numberPicker.setValue(4);
        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                //set value here
            }
        });

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        controller.show();
        player.pause();
        dura.setVisibility(View.INVISIBLE);
        dura2.setVisibility(View.INVISIBLE);
        NoOfSets.setVisibility(View.INVISIBLE);
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        return false;
    }


    // Implement SurfaceHolder.Callback
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        player.setDisplay(holder);
        player.prepareAsync();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
    }
    // End SurfaceHolder.Callback


    // Implement MediaPlayer.OnPreparedListener
    @Override
    public void onPrepared(MediaPlayer mp) {
        Log.d(TAG, "onPrepared: 1");
        controller.setMediaPlayer(this);
        controller.setAnchorView((FrameLayout) findViewById(R.id.videoSurfaceContainer), IntroReal, noOfSets, videoName);
        player.start();
        if (progressDialog != null)
            progressDialog.dismiss();
        Log.d(TAG, "onPrepared: " + getDuration());
        int gy = getDuration();

        if (IntroReal == 1) {
            if (videoNo == 0 || videoNo == 1) {
                gy = gy * noOfSets;
            } else if (videoNo == 2) {
                gy = gy * tottalReps;
                noOfSets = tottalReps;
            }
        }
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        countDownTimer = new CountDownTimer(gy, 1000) {//geriye sayma

            public void onTick(long millisUntilFinished) {

                NumberFormat f = new DecimalFormat("00");
                long hour = (millisUntilFinished / 3600000) % 24;
                long min = (millisUntilFinished / 60000) % 60;
                long sec = (millisUntilFinished / 1000) % 60;
                if (IntroReal == 0) {
                    dura.setVisibility(View.VISIBLE);
                    dura2.setVisibility(View.GONE);
                    NoOfSets.setVisibility(View.INVISIBLE);
                    dura.setText(videoName + "\n" + "Starts in " + f.format(hour) + ":" + f.format(min) + ":" + f.format(sec));
                    skipIntroBtn.setVisibility(View.VISIBLE);
                } else if (IntroReal == 1) {
                    if (videoNo == 0 || videoNo == 1) {
                        dura.setVisibility(View.GONE);
                        skipIntroBtn.setVisibility(View.GONE);
                        dura2.setVisibility(View.VISIBLE);
                        NoOfSets.setVisibility(View.VISIBLE);
                        NoOfSets.setText("Remaining Sets : " + String.valueOf(noOfSets));
                        dura2.setText("Total Time Remaining : \n" + f.format(hour) + ":" + f.format(min) + ":" + f.format(sec));
                    } else {
                        dura.setVisibility(View.GONE);
                        skipIntroBtn.setVisibility(View.GONE);
                        dura2.setVisibility(View.VISIBLE);
                        NoOfSets.setVisibility(View.VISIBLE);
                        dura2.setText(String.valueOf(currentSet) + "/" + String.valueOf(tottalReps));
                        NoOfSets.setText("Total Time Remaining : \n" + f.format(hour) + ":" + f.format(min) + ":" + f.format(sec));
                    }
                }
            }

            public void onFinish() {
                dura.setText("00:00:00");
                dura2.setText("00:00:00");
                if (IntroReal == 0 && videoNo == 0) {
                    // if (flag != 1) {
                    IntroReal = 1;
                    videoNo = 0;
                    //  }
                    // flag = 0;
                } else if (IntroReal == 1 && videoNo == 0) {
                    IntroReal = 0;
                    videoNo = 1;
                } else if (IntroReal == 0 && videoNo == 1) {
                    IntroReal = 1;
                    videoNo = 1;
                } else if (IntroReal == 1 && videoNo == 1) {
                    IntroReal = 0;
                    videoNo = 2;
                } else if (IntroReal == 0 && videoNo == 2) {
                    IntroReal = 1;
                    videoNo = 2;
                } else if (IntroReal == 1 && videoNo == 2) {
                    cancel();
                    return;
                }
                startNext();
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
        dura.setVisibility(View.INVISIBLE);
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        dura2.setVisibility(View.GONE);
        NoOfSets.setVisibility(View.INVISIBLE);
        player.pause();
    }

    @Override
    public void start() {
        player.start();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        countDownTimer = new CountDownTimer(screenTime, 1000) {                     //geriye sayma

            public void onTick(long millisUntilFinished) {

                NumberFormat f = new DecimalFormat("00");
                long hour = (millisUntilFinished / 3600000) % 24;
                long min = (millisUntilFinished / 60000) % 60;
                long sec = (millisUntilFinished / 1000) % 60;
                if (IntroReal == 0) {
                    dura.setVisibility(View.VISIBLE);
                    dura2.setVisibility(View.GONE);
                    NoOfSets.setVisibility(View.INVISIBLE);
                    dura.setText(videoName + "\n" + "Starts in " + f.format(hour) + ":" + f.format(min) + ":" + f.format(sec));
                    skipIntroBtn.setVisibility(View.VISIBLE);
                } else if (IntroReal == 1) {
                    if (videoNo == 0 || videoNo == 1) {
                        dura.setVisibility(View.GONE);
                        skipIntroBtn.setVisibility(View.GONE);
                        dura2.setVisibility(View.VISIBLE);
                        NoOfSets.setVisibility(View.VISIBLE);
                        NoOfSets.setText("Remaining Sets : " + String.valueOf(noOfSets));
                        dura2.setText("Total Time Remaining : \n" + f.format(hour) + ":" + f.format(min) + ":" + f.format(sec));
                    } else {
                        dura.setVisibility(View.GONE);
                        skipIntroBtn.setVisibility(View.GONE);
                        dura2.setVisibility(View.VISIBLE);
                        NoOfSets.setVisibility(View.VISIBLE);
                        dura2.setText(String.valueOf(currentSet) + "/" + String.valueOf(tottalReps));
                        NoOfSets.setText("Total Time Remaining : \n" + f.format(hour) + ":" + f.format(min) + ":" + f.format(sec));
                    }
                }
            }

            public void onFinish() {
                dura.setText("00:00:00");
                if (IntroReal == 0 && videoNo == 0) {
                    IntroReal = 1;
                    videoNo = 0;
                } else if (IntroReal == 1 && videoNo == 0) {
                    IntroReal = 0;
                    videoNo = 1;
                } else if (IntroReal == 0 && videoNo == 1) {
                    IntroReal = 1;
                    videoNo = 1;
                } else if (IntroReal == 1 && videoNo == 1) {
                    IntroReal = 0;
                    videoNo = 2;
                } else if (IntroReal == 0 && videoNo == 2) {
                    IntroReal = 1;
                    videoNo = 2;
                } else if (IntroReal == 1 && videoNo == 2) {
                    cancel();
                    return;
                }
                startNext();
            }
        }.start();
    }

    @Override
    public void setOnScreenTime(int time) {
        if (IntroReal == 1) {
            int tempTotalDuration = getDuration();
            int ku = tempTotalDuration - time;
            screenTime = tempTotalDuration * noOfSets - ku + 1000;
        } else {
            screenTime = time + 1000;
        }
    }

    @Override
    public void nextVideo() {
        if (player != null) {
            if (IntroReal == 0 && videoNo == 0) {
                IntroReal = 1;
                videoNo = 0;
            } else if (IntroReal == 1 && videoNo == 0) {
                IntroReal = 0;
                videoNo = 1;
            } else if (IntroReal == 0 && videoNo == 1) {
                IntroReal = 1;
                videoNo = 1;
            } else if (IntroReal == 1 && videoNo == 1) {
                IntroReal = 0;
                videoNo = 2;
            } else if (IntroReal == 0 && videoNo == 2) {
                IntroReal = 1;
                videoNo = 2;
            } else if (IntroReal == 1 && videoNo == 2) {
                showFeedBack();
                return;
            }
            startNext();
        }
    }


    @Override
    public void prevVideo() {
        if (player != null) {
            if (IntroReal == 0 && videoNo == 0) {
                IntroReal = 0;
                videoNo = 2;
            } else if (IntroReal == 1 && videoNo == 0) {
                IntroReal = 0;
                videoNo = 2;
            } else if (IntroReal == 0 && videoNo == 1) {
                IntroReal = 0;
                videoNo = 0;
            } else if (IntroReal == 1 && videoNo == 1) {
                IntroReal = 0;
                videoNo = 0;
            } else if (IntroReal == 0 && videoNo == 2) {
                IntroReal = 0;
                videoNo = 1;
            } else if (IntroReal == 1 && videoNo == 2) {
                IntroReal = 0;
                videoNo = 1;
            }
            startNext();
        }
    }

    public void startNext() {
        if (player == null) {
            player = new MediaPlayer();
            try {
                videoName = "Stack PushUp intro";
                IntroReal = 0;
                videoNo = 0;
                Uri video = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.stackpushupsingle);
                player.setDataSource(this, video);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (IntroReal == 1 && videoNo == 0) {
            player.reset();
            try {
                videoName = "Stack PushUp";
                if (countDownTimer != null) {
                    countDownTimer.cancel();
                }
                Uri video = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.stackpushupsingle);
                player.setDataSource(this, video);
                player.prepareAsync();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (IntroReal == 0 && videoNo == 1) {
            player.reset();
            try {
                videoName = "Superman PushUp intro";
                if (countDownTimer != null) {
                    countDownTimer.cancel();
                }
                Uri video = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.stackpushupsingle);
                player.setDataSource(this, video);
                player.prepareAsync();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (IntroReal == 1 && videoNo == 1) {
            player.reset();
            try {
                videoName = "Superman Pushup";
                if (countDownTimer != null) {
                    countDownTimer.cancel();
                }
                Uri video = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.stackpushupsingle);
                player.setDataSource(this, video);
                player.prepareAsync();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (IntroReal == 0 && videoNo == 2) {
            player.reset();
            try {
                videoName = "Stack PushUp";
                if (countDownTimer != null) {
                    countDownTimer.cancel();
                }
                Uri video = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.stackpushupsingle);
                player.setDataSource(this, video);
                player.prepareAsync();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (IntroReal == 1 && videoNo == 2) {
            player.reset();
            try {
                videoName = "SuperMan PushUp";
                if (countDownTimer != null) {
                    countDownTimer.cancel();
                }
                Uri video = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.stackpushupsingle);
                player.setDataSource(this, video);
                player.prepareAsync();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Please Wait");
            progressDialog.setMessage("Loading ... ");

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
            player.setOnCompletionListener(this);

        } catch (IllegalArgumentException | SecurityException | IllegalStateException e) {
            e.printStackTrace();
        }
    }


    //mediaplayer implement methods
    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        if (IntroReal == 1) {
            if (videoNo == 0 || videoNo == 1) {
                if (noOfSets > 0) {
                    player.seekTo(0);
                    player.start();
                    noOfSets--;
                } else {
                    if (videoNo == 0) {
                        IntroReal = 0;
                        videoNo = 1;
                        noOfSets = 2;
                        startNext();
                    }
                    if (videoNo == 1) {
                        IntroReal = 0;
                        videoNo = 2;
                        noOfSets = 2;
                        currentSet = 0;
                        startNext();
                    }
                }
            } else if (videoNo == 2) {
                currentSet = currentSet + 1;
                flag = 1;
                if (currentSet <= tottalReps) {
                    if (currentSet != 4) {
                        player.seekTo(0);
                        player.start();
                        noOfSets--;
                    } else {
                        showFeedBack();
                    }
                    middleCount.setVisibility(View.VISIBLE);
                    middleCount.setText(String.valueOf(currentSet));
                    if (isSoundOn) {
                        tts.speak(String.valueOf(currentSet), TextToSpeech.QUEUE_FLUSH, null);
                    }
                    YoYo.with(Techniques.ZoomIn).duration(2000).playOn(middleCount);
                    YoYo.with(Techniques.FadeOut).duration(1000).delay(2000).playOn(middleCount);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            middleCount.setVisibility(View.INVISIBLE);
                        }
                    }, 3000);
                }
            }
        } else if (IntroReal == 0) {
            IntroReal = 1;
            if (videoNo == 0) {
                videoNo = 1;
            } else if (videoNo == 1) {
                videoNo = 2;
            }
            startNext();
        }
    }

    private View.OnClickListener skipIntroListner = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            dura.setVisibility(View.INVISIBLE);
            IntroReal = 1;
            startNext();
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
}

// End VideoMediaController.MediaPlayerControl


