package tech.iosd.benefit;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptionsExtension;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DataReadResponse;
import com.google.android.gms.fitness.result.DataReadResult;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import tech.iosd.benefit.DashboardFragments.Chat;
import tech.iosd.benefit.DashboardFragments.MyAccountChangePassword;
import tech.iosd.benefit.Model.DatabaseHandler;
import tech.iosd.benefit.Model.VideoPlayerItem;
import tech.iosd.benefit.Utils.JWTUtils;
import tech.iosd.benefit.VideoPlayer.VideoPlayerActivity;

public class AccountFragment extends Fragment implements
    GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener{
    private static final int GOOGLE_FIT_PERMISSIONS_REQUEST_CODE = 11;
    Context ctx;
    FragmentManager fm;

    TextView googleFit, changePassword;

    private DatabaseHandler db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_account, container, false);
        ctx = rootView.getContext();
        fm = getFragmentManager();
        googleFit = rootView.findViewById(R.id.googleFit);
        db = new DatabaseHandler(getContext());
        changePassword = rootView.findViewById(R.id.account_change_password);
        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fm.beginTransaction().replace(R.id.dashboard_content, new MyAccountChangePassword()).addToBackStack(null).commit();
            }
        });

        try {
            Log.d("JWT_DECODED",db.getUserToken());

            JWTUtils.decoded(db.getUserToken());
        } catch (Exception e) {
            e.printStackTrace();
        }

        TextView videoplayerTest = rootView.findViewById(R.id.videoplayerTest);
        videoplayerTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VideoPlayerItem videoItem;
                //adding dummy data to videoplayeritem fot testing.........
                videoItem = new VideoPlayerItem();
                videoItem.setType(VideoPlayerItem.TYPE_REPETITIVE);
                videoItem.setSets(4);
                videoItem.setVideoName("StackPushUp Repetitive");
                videoItem.setRestTimeSec(10);
                videoItem.setTotalReps(5);
                videoItem.setIntroVideo("android.resource://");
                videoItem.setSingleRepVideo("android.resource://");
                videoItem.setCurrentRep(0);
                videoItem.setCurrentSet(0);

                Gson gson = new Gson();
                ArrayList<String> videoItemList = new ArrayList<>();
                videoItemList.add(gson.toJson(videoItem));
                videoItem.setType(VideoPlayerItem.TYPE_FOLLOW);
                videoItemList.add(gson.toJson(videoItem));

                Intent intent = new Intent(getContext(), VideoPlayerActivity.class);
                intent.putExtra("videoItemList",videoItemList);
                startActivity(intent);
            }
        });


        //permissions required from user...
        FitnessOptions fitnessOptions = FitnessOptions.builder()
                .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.AGGREGATE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.TYPE_DISTANCE_DELTA,FitnessOptions.ACCESS_READ)
                .addDataType(DataType.TYPE_HEIGHT,FitnessOptions.ACCESS_READ)
                .addDataType(DataType.TYPE_HEIGHT,FitnessOptions.ACCESS_WRITE)
                .addDataType(DataType.TYPE_WEIGHT,FitnessOptions.ACCESS_READ)
                .addDataType(DataType.TYPE_WEIGHT,FitnessOptions.ACCESS_WRITE)
                .build();

        if (GoogleSignIn.hasPermissions(GoogleSignIn.getLastSignedInAccount(getActivity()), fitnessOptions)) {
            //removing plus icon
                    removeGoogleFitIcon();
        }
        googleFit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!GoogleSignIn.hasPermissions(GoogleSignIn.getLastSignedInAccount(getActivity()), fitnessOptions)) {
                    GoogleSignIn.requestPermissions(
                            getActivity(), // your activity
                            GOOGLE_FIT_PERMISSIONS_REQUEST_CODE,
                            GoogleSignIn.getLastSignedInAccount(getActivity()),
                            fitnessOptions);
                } else {
                    accessGoogleFit();
                }
            }
        });


        // Inflate the layout for this fragment
        return rootView;
    }

    private void removeGoogleFitIcon(){
        googleFit.setCompoundDrawables(null,null,null,null);
    }

    private void accessGoogleFit() {
        GoogleSignInOptionsExtension fitnessOptions = FitnessOptions.builder()
                        .addDataType(DataType.TYPE_ACTIVITY_SAMPLES, FitnessOptions.ACCESS_READ)
                        .build();
        GoogleSignInAccount gsa = GoogleSignIn.getAccountForExtension(getActivity(), fitnessOptions);
        Task<Void> response = Fitness.getRecordingClient(getActivity(), gsa)
                .subscribe(DataType.TYPE_ACTIVITY_SAMPLES);
        // play services starts recording stuff now ( ^ ^ )


        Fitness.getHistoryClient(getContext(),GoogleSignIn.getLastSignedInAccount(getContext()))
                .readDailyTotal(DataType.TYPE_STEP_COUNT_DELTA)
                .addOnSuccessListener(new OnSuccessListener<DataSet>() {
                    @Override
                    public void onSuccess(DataSet dataSet) {
                        DateFormat dateFormat = DateFormat.getDateInstance();
                        DateFormat timeFormat = DateFormat.getTimeInstance();
                        String x = "";
                        for(DataPoint dp : dataSet.getDataPoints()){
                            x += "Steps Today: "+ dp.getValue(Field.FIELD_STEPS) + "\n";
                        }

                        Toast.makeText(getContext(),x,Toast.LENGTH_SHORT).show();
                    }
                });
        Fitness.getHistoryClient(getContext(),GoogleSignIn.getLastSignedInAccount(getContext()))
                .readDailyTotal(DataType.TYPE_CALORIES_EXPENDED)
                .addOnSuccessListener(new OnSuccessListener<DataSet>() {
                    @Override
                    public void onSuccess(DataSet dataSet) {
                        DateFormat dateFormat = DateFormat.getDateInstance();
                        DateFormat timeFormat = DateFormat.getTimeInstance();
                        String x = "";
                        for(DataPoint dp : dataSet.getDataPoints()){
                            x += "Calories Burnt: "+ dp.getValue(Field.FIELD_CALORIES) + "\n";
                        }
                        Toast.makeText(getContext(),x,Toast.LENGTH_SHORT).show();

                    }
                });
        Fitness.getHistoryClient(getContext(),GoogleSignIn.getLastSignedInAccount(getContext()))
                .readDailyTotal(DataType.TYPE_DISTANCE_DELTA)
                .addOnSuccessListener(new OnSuccessListener<DataSet>() {
                    @Override
                    public void onSuccess(DataSet dataSet) {
                        DateFormat dateFormat = DateFormat.getDateInstance();
                        DateFormat timeFormat = DateFormat.getTimeInstance();
                        String x = "";
                        for(DataPoint dp : dataSet.getDataPoints()){
                            x += "Distance Today: "+ dp.getValue(Field.FIELD_DISTANCE) + "\n";
                        }
                        Toast.makeText(getContext(),x,Toast.LENGTH_SHORT).show();
                    }
                });



        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        long endTime = cal.getTimeInMillis();
        cal.add(Calendar.WEEK_OF_YEAR, -1);
        long startTime = cal.getTimeInMillis();
        Log.w("t",String.valueOf(startTime));

        DataReadRequest readRequest = new DataReadRequest.Builder()
                .aggregate(DataType.TYPE_STEP_COUNT_DELTA, DataType.AGGREGATE_STEP_COUNT_DELTA)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .bucketByTime(7,TimeUnit.DAYS)
                .build();

        Fitness.getHistoryClient(getContext(),GoogleSignIn.getLastSignedInAccount(getContext()))
                .readData(readRequest)
                .addOnSuccessListener(new OnSuccessListener<DataReadResponse>() {
                    @Override
                    public void onSuccess(DataReadResponse dataReadResponse) {
                        DateFormat dateFormat = DateFormat.getDateInstance();
                        DateFormat timeFormat = DateFormat.getTimeInstance();
                        String x = "";

                        for(Bucket bucket: dataReadResponse.getBuckets()){
                            x += "Bucket \n";
                            for(DataSet dataSet: bucket.getDataSets()){
                                x+="  Dataset \n";
                                for(DataPoint dp: dataSet.getDataPoints()){
                                    x += "\nStart: "+dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)) + " " + timeFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS));
                                    x += "\nEnd: "+dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS)) + " " + timeFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS));
                                    x += "\n Steps: "+ dp.getValue(Field.FIELD_STEPS);
                                }
                            }
                        }
                        Toast.makeText(getContext(),x,Toast.LENGTH_LONG).show();
                    }
                });
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Toast.makeText(getContext(),"GoogleFit Connected",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(getContext(),"GoogleFit Connection Suspended",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(getContext(),"GoogleFit Connection Failed",Toast.LENGTH_SHORT).show();
    }

    //runs only if authorization is required
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == GOOGLE_FIT_PERMISSIONS_REQUEST_CODE) {
                removeGoogleFitIcon();
                accessGoogleFit();
            }
        }
    }
}
