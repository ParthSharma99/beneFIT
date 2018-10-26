package tech.iosd.benefit.DashboardFragments;

import android.app.AlertDialog;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.aigestudio.wheelpicker.WheelPicker;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import tech.iosd.benefit.Model.PostTrackActivity;
import tech.iosd.benefit.Model.ResponseForSuccess;
import tech.iosd.benefit.Network.NetworkUtil;
import tech.iosd.benefit.R;
import tech.iosd.benefit.Model.DatabaseHandler;

public class TrackMyActivityCallisthenics extends Fragment
{
    Context ctx;
    FragmentManager fm;
    TextView duration,calories;
    private DatabaseHandler db;
    WheelPicker wheelPickerHour;
    WheelPicker wheelPickerMinute;
    WheelPicker wheelPickerSecond;
    int type_of_activity;
    Button save_activity;
    String activity_name;
    boolean hasClicked = false;
    private CompositeSubscription mSubscriptions;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.dashboard_track_my_activity_callisthenics, container, false);
        ctx = rootView.getContext();
        fm = getFragmentManager();
        db = new DatabaseHandler(ctx);
        duration=rootView.findViewById(R.id.duration_callisthenics);
        calories=rootView.findViewById(R.id.calories_callisthenics);
        save_activity=rootView.findViewById(R.id.save_callisthenics);
        Bundle bundle = this.getArguments();
        mSubscriptions = new CompositeSubscription();
        activity_name ="AEROBICS";
        if (bundle != null) {
            activity_name = bundle.getString("ACTIVITY_TYPE", "AEROBICS");
        }
        if(activity_name.equals("SKIPPING"))
            type_of_activity=3;
        else if(activity_name.equals("SWIMMING"))
            type_of_activity=2;
        else
            type_of_activity=1;
        rootView.findViewById(R.id.dashboard_track_my_activity_callisthenics_edit_time).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                hasClicked=true;
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
                View mView = getActivity().getLayoutInflater().inflate(R.layout.dialog_picker_time, null);
                Button dialogDone = mView.findViewById(R.id.dialog_done);
                wheelPickerHour = mView.findViewById(R.id.dialog_picker_hour);
                wheelPickerMinute = mView.findViewById(R.id.dialog_picker_minute);
                wheelPickerSecond = mView.findViewById(R.id.dialog_picker_seconds);
                mBuilder.setView(mView);
                final AlertDialog dialog = mBuilder.create();
                dialog.show();

                List<String> data_hour = new ArrayList<>();
                for (int i = 1; i <= 24; i++)
                    data_hour.add( i > 9 ? Integer.toString(i) : "0" + i );
                wheelPickerHour.setData(data_hour);
                List<String> data_minute = new ArrayList<>();
                for (int i = 0; i <= 60; i++)
                    data_minute.add( i > 9 ? Integer.toString(i) : "0" + i );
                wheelPickerMinute.setData(data_minute);
                List<String> data_second = new ArrayList<>();
                for (int i = 0; i <= 60; i++)
                    data_second.add( i > 9 ? Integer.toString(i) : "0" + i );
                wheelPickerSecond.setData(data_second);

                dialogDone.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        if (duration!=null)
                        duration.setText(data_hour.get(wheelPickerHour.getCurrentItemPosition())+":"
                                    +data_minute.get(wheelPickerMinute.getCurrentItemPosition())+":"+
                                    data_second.get(wheelPickerSecond.getCurrentItemPosition()));
                        changeCalories();
                        dialog.dismiss();

                    }
                });
            }
        });

        save_activity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
                 String selectedDate = dateFormat.format(Calendar.getInstance().getTime());

                PostTrackActivity postTrackActivity=new PostTrackActivity(selectedDate,activity_name,getTimeInSecs(),Float.parseFloat(getCalories()),0);
                mSubscriptions.add(NetworkUtil.getRetrofit(db.getUserToken()).sendActivityDetails(postTrackActivity,db.getUserToken())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(this::handleResponseSendActivityDetails,this::handleError));
                fm.popBackStack();
            }
            private void handleResponseSendActivityDetails(ResponseForSuccess responseForSuccess)
            {
                Toast.makeText(ctx,"Success",Toast.LENGTH_SHORT).show();
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

    private void showSnackBarMessage(String message) {

        if (getView() != null) {

            Snackbar.make(getView(),message, Snackbar.LENGTH_LONG).show();


        }
    }

    private void changeCalories(){
        calories.setText(getCalories());
    }
    private String getCalories(){

        double METS=5.5;
        if(type_of_activity==2)
            METS= 5.8;
        else if(type_of_activity==3)
            METS= 8.5;
        int total= getTimeInSecs()/3600;
        int weight= db.getUserWeight();
        double calories_value= METS*total*weight;
        DecimalFormat df = new DecimalFormat("#.##");
        String formatted = df.format(calories_value);
        return formatted;
    }
    private int getTimeInSecs(){
        int hours,mins,secs;
        if(hasClicked) {
            hours = wheelPickerHour.getCurrentItemPosition() + 1;
            mins = wheelPickerMinute.getCurrentItemPosition() + 1;
            secs = wheelPickerSecond.getCurrentItemPosition() + 1;
        }
        else{
            hours =0;
            mins= 2;
            secs= 50;
        }
        return hours*3600 + mins*60 + secs;
    }
}
