package tech.iosd.benefit.DashboardFragments;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.design.widget.Snackbar;

import rx.android.schedulers.AndroidSchedulers;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import retrofit2.adapter.rxjava.HttpException;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import tech.iosd.benefit.Model.DatabaseHandler;
import tech.iosd.benefit.Model.PostTrackActivity;
import tech.iosd.benefit.Model.ResponseForSuccess;
import tech.iosd.benefit.Network.NetworkUtil;
import tech.iosd.benefit.R;

public class TrackActivityCompleted extends Fragment
{
    Context ctx;
    FragmentManager fm;
    private CompositeSubscription mSubscriptions;
    String calorie,duration,distance,avgPaceText,trackType;
    TextView calorieBurnt,totalDuration,totalDistance,avgPace;
    ImageView backIcon;
    float durationSec;
    private String selectedDate;
    SimpleDateFormat dateFormat;
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            calorie = getArguments().getString("CALORIE","0");
            duration = getArguments().getString("DURATION","00:00");
            distance = getArguments().getString("DISTANCE","0.0");
            Log.d("Tracking Debug", "onCreate: Distance " + distance );
            avgPaceText = getArguments().getString("AVG_PACE","0.0");
            trackType=getArguments().getString("TRACK_TYPE","RUNNING");
            durationSec=getArguments().getFloat("DURATION_SEC",0);
        } else {
            return;
        }
    }
    Button saveActivity;
    private DatabaseHandler db ;
    private void showSnackBarMessage(String message) {

        if (getView() != null) {

            Snackbar.make(getView(),message, Snackbar.LENGTH_LONG).show();


        }
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.layout_track_my_activity_done, container, false);
        ctx = rootView.getContext();
        fm = getFragmentManager();
        calorieBurnt=rootView.findViewById(R.id.complete_calories_track_my_activity);
        totalDuration=rootView.findViewById(R.id.complete_track_activity_duration);
        totalDistance=rootView.findViewById(R.id.complete_dashboard_track_my_activity_distance_textview);
        avgPace=rootView.findViewById(R.id.complete_track_activity_avg_pace);
        backIcon=rootView.findViewById(R.id.completed_back_icon);
        saveActivity=rootView.findViewById(R.id.complete_save_activity);
        mSubscriptions = new CompositeSubscription();
        calorieBurnt.setText(calorie);
        totalDuration.setText(duration);
        totalDistance.setText(distance);
        dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        selectedDate = dateFormat.format(Calendar.getInstance().getTime());
        avgPace.setText(avgPaceText);
        db = new DatabaseHandler(getContext());
        Log.d( "date" ,selectedDate);
        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fm.popBackStack();
            }
        });
        saveActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                PostTrackActivity postTrackActivity=new PostTrackActivity(selectedDate,trackType,durationSec,Float.parseFloat(calorie),Float.parseFloat(distance));
                mSubscriptions.add(NetworkUtil.getRetrofit(db.getUserToken()).sendActivityDetails(postTrackActivity,db.getUserToken())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(this::handleResponsesendActivityDetails,this::handleError));
                fm.popBackStack();
            }

            private void handleResponsesendActivityDetails(ResponseForSuccess responseForSuccess)
            {
                showSnackBarMessage(responseForSuccess.getMessage());
            }

            private void handleError(Throwable error)
            {
                if (error instanceof HttpException) {

                    Gson gson = new GsonBuilder().create();
                    showSnackBarMessage("Network Error !");
                    Log.d("error77",error.getMessage());

                    try {

                        String errorBody = ((HttpException) error).response().errorBody().string();
                /*Response response = gson.fromJson(errorBody,Response.class);
                showSnackBarMessage(response.getMessage());*/

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.d("error77",error.getMessage());

                    showSnackBarMessage("Network Error !");
                }
            }
        });
        return rootView;
    }
}
