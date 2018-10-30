package tech.iosd.benefit;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.v7.widget.LinearLayoutManager;
import android.view.ViewGroup;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.support.design.widget.Snackbar;
import android.widget.Toast;

import rx.android.schedulers.AndroidSchedulers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import retrofit2.adapter.rxjava.HttpException;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import tech.iosd.benefit.Adapters.WorkoutSaveActivityAdapter;
import tech.iosd.benefit.DashboardFragments.SaveWorkout;
import tech.iosd.benefit.Model.DatabaseHandler;
import tech.iosd.benefit.Model.PostFreeWorkoutActivity;
import tech.iosd.benefit.Model.ResponseForSuccess;
import tech.iosd.benefit.Model.VideoPlayerItem;
import tech.iosd.benefit.Network.NetworkUtil;

import static android.content.Context.MODE_PRIVATE;


public class SaveWorkoutActivity extends AppCompatActivity implements View.OnClickListener {
    Button saveActivity;
    TextView workoutName;
    Button happy, neutral, sad;
    private CompositeSubscription mSubscriptions;
    TextView caloriesBurntTextView, timeTaken, totalExercises;
    private ArrayList<String> exercises = new ArrayList<>();
    private WorkoutSaveActivityAdapter adapter;
    private RecyclerView recyclerView;
    boolean isFreeWorkout = false;
    SimpleDateFormat dateFormat;
    ArrayList<PostFreeWorkoutActivity.Progress> progresses;
    PostFreeWorkoutActivity postFreeWorkoutActivity;
    Gson gson;
    SharedPreferences sharedPreferences;
    private String selectedDate;
    SharedPreferences sharedPreferences1;
    VideoPlayerItem videoItem;
    private DatabaseHandler db;
    Context ctx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.workout_save_activity);
        exercises = getIntent().getStringArrayListExtra("VIDEO_ITEM");
        recyclerView = findViewById(R.id.save_workouts_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mSubscriptions = new CompositeSubscription();
        ctx = this;
        isFreeWorkout = getIntent().getExtras().getBoolean("FREEWORKOUT", false);
        adapter = new WorkoutSaveActivityAdapter(exercises, SaveWorkoutActivity.this);
        recyclerView.setAdapter(adapter);
        postFreeWorkoutActivity = new PostFreeWorkoutActivity();
        saveActivity = findViewById(R.id.complete_save_activity);
        workoutName = findViewById(R.id.workout_name);
        gson = new Gson();
        sharedPreferences1 = getSharedPreferences("SAVE_EXERCISE", MODE_PRIVATE);
        progresses = new ArrayList<>();
        db = new DatabaseHandler(ctx);
        String endTime = sharedPreferences1.getString("END_TIME", "");
        float endTimeMillis = sharedPreferences1.getFloat("END_TIME_MILLIS", 0);
        String startTime = sharedPreferences1.getString("START_TIME", "");
        String workoutId = sharedPreferences1.getString("WORKOUT_ID", "");
        Toast.makeText(ctx, workoutId, Toast.LENGTH_SHORT).show();
        float startTimeMillis = sharedPreferences1.getFloat("START_TIME_MILLIS", 0);
        float totalTimeWorkout = ((endTimeMillis - startTimeMillis) / 60000);
        dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        selectedDate = dateFormat.format(Calendar.getInstance().getTime());
        caloriesBurntTextView = findViewById(R.id.calories_burnt_save_activity);
        timeTaken = findViewById(R.id.time_taken_save_activity);
        timeTaken.setText(String.format("%d", (int) totalTimeWorkout));
        totalExercises = findViewById(R.id.total_exercises_save_activity);
        workoutName.setText("HIGH INTENSITY INTERVAL TRAINING");
        SharedPreferences sharedPreferences1 = getSharedPreferences("SAVE_EXERCISE", MODE_PRIVATE);
        totalExercises.setText(exercises.size() + "");
        int caloriesBurnt = sharedPreferences1.getInt("CaloriesBurnt", 0);
        caloriesBurntTextView.setText(String.format("%d", caloriesBurnt));


        saveActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFreeWorkout) {
                    for (int x = 0; x < exercises.size(); x++) {
                        sharedPreferences = SaveWorkoutActivity.this.getSharedPreferences("SAVE_EXERCISE", MODE_PRIVATE);
                        PostFreeWorkoutActivity.Progress progress = new PostFreeWorkoutActivity.Progress();
                        videoItem = gson.fromJson(exercises.get(x), VideoPlayerItem.class);
                        if (videoItem.getVideoName() != null)
                            progress.setExercise(videoItem.getId());
                        int sets = sharedPreferences.getInt("SetNo" + videoItem.getVideoName(), 1);
                        int reps = sharedPreferences.getInt("RepsNo" + videoItem.getVideoName(), 10);
                        int weight = sharedPreferences.getInt("Weight" + videoItem.getVideoName(), 0);
                        progress.setReps(reps);
                        progress.setWeight(weight);
                        progress.setSet(sets);
                        progresses.add(progress);
                    }
                    PostFreeWorkoutActivity.Stats stats = new PostFreeWorkoutActivity.Stats();
                    stats.setCalories(caloriesBurnt);
                    stats.setStart_time(startTime);
                    stats.setEnd_time(endTime);
                    postFreeWorkoutActivity.setDate(selectedDate);
                    postFreeWorkoutActivity.setProgress(progresses);
                    postFreeWorkoutActivity.setStats(stats);
                    postFreeWorkoutActivity.setWorkout(workoutId);
                    mSubscriptions.add(NetworkUtil.getRetrofit(db.getUserToken()).sendFreeWorkoutActivity(postFreeWorkoutActivity, db.getUserToken())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe(this::handleResponsepostFreeWorkoutActivity, this::handleError));
                    Log.d("logJason", (new Gson()).toJson(postFreeWorkoutActivity));
                } else {
                    for (int x = 0; x < exercises.size(); x++) {
                        sharedPreferences = SaveWorkoutActivity.this.getSharedPreferences("SAVE_EXERCISE", MODE_PRIVATE);
                        PostFreeWorkoutActivity.Progress progress = new PostFreeWorkoutActivity.Progress();
                        videoItem = gson.fromJson(exercises.get(x), VideoPlayerItem.class);
                        if (videoItem.getVideoName() != null)
                            progress.setExercise(videoItem.getId());
                        int sets = sharedPreferences.getInt("SetNo" + videoItem.getVideoName(), 1);
                        int reps = sharedPreferences.getInt("RepsNo" + videoItem.getVideoName(), 10);
                        int weight = sharedPreferences.getInt("Weight" + videoItem.getVideoName(), 0);
                        progress.setReps(reps);
                        progress.setWeight(weight);
                        progress.setSet(sets);
                        progresses.add(progress);
                    }
                    PostFreeWorkoutActivity.Stats stats = new PostFreeWorkoutActivity.Stats();
                    stats.setCalories(caloriesBurnt);
                    stats.setStart_time(startTime);
                    stats.setEnd_time(endTime);
                    postFreeWorkoutActivity.setDate(selectedDate);
                    postFreeWorkoutActivity.setProgress(progresses);
                    postFreeWorkoutActivity.setStats(stats);
                    postFreeWorkoutActivity.setWorkout(workoutId);
                    mSubscriptions.add(NetworkUtil.getRetrofit(db.getUserToken()).sendWorkoutActivity(postFreeWorkoutActivity, db.getUserToken())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe(this::handleResponsepostFreeWorkoutActivity, this::handleError));
                    Log.d("logJason", (new Gson()).toJson(postFreeWorkoutActivity));
                }
                sharedPreferences1.edit().putInt("CaloriesBurnt", 0).apply();
                finish();
            }

            private void showSnackBarMessage(String message) {

                //Snackbar.make(getView(),message, Snackbar.LENGTH_LONG).show();
                Toast.makeText(ctx, message, Toast.LENGTH_SHORT).show();
            }

            private void handleError(Throwable error) {
                if (error instanceof HttpException) {

                    Gson gson = new GsonBuilder().create();
                    showSnackBarMessage("Network Error !");
                    Log.d("error77", error.getMessage());

                    try {

                        String errorBody = ((HttpException) error).response().errorBody().string();
                /*Response response = gson.fromJson(errorBody,Response.class);
                showSnackBarMessage(response.getMessage());*/

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.d("error77", error.getMessage());

                    showSnackBarMessage("Network Error !");
                }
            }

            private void handleResponsepostFreeWorkoutActivity(ResponseForSuccess responseForSuccess) {
                showSnackBarMessage(responseForSuccess.getMessage());
            }
        });

        happy = findViewById(R.id.happy);
        sad = findViewById(R.id.sad);
        neutral = findViewById(R.id.neutral);
        happy.setOnClickListener(this);
        neutral.setOnClickListener(this);
        sad.setOnClickListener(this);
    }

    boolean happySelected = false, neutralSelected = false, sadSelected = false;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.happy: {
                happy.setBackgroundResource(R.drawable.ic_happy_on);
                neutral.setBackgroundResource(R.drawable.ic_neutral_off);
                sad.setBackgroundResource(R.drawable.ic_sad_off);
                break;
            }

            case R.id.neutral: {
                happy.setBackgroundResource(R.drawable.ic_happy_off);
                neutral.setBackgroundResource(R.drawable.ic_neutral_on);
                sad.setBackgroundResource(R.drawable.ic_sad_off);
                break;
            }

            case R.id.sad: {
                happy.setBackgroundResource(R.drawable.ic_happy_off);
                neutral.setBackgroundResource(R.drawable.ic_neutral_off);
                sad.setBackgroundResource(R.drawable.ic_sad_on);
                break;
            }
        }
    }
}
