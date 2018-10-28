package tech.iosd.benefit.DashboardFragments;

import android.app.Activity;
import android.content.Intent;
import android.renderscript.Element;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.auth.api.signin.GoogleSignIn;

import com.gelitenight.waveview.library.WaveView;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.ColumnChartView;
import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import tech.iosd.benefit.DashboardActivity;
import tech.iosd.benefit.Model.DatabaseHandler;
import tech.iosd.benefit.Model.PostWaterIntake;
import tech.iosd.benefit.Model.ResponseForSuccess;
import tech.iosd.benefit.Model.ResponseWaterIntake;
import tech.iosd.benefit.Model.ResponseForWaterHistory;
import tech.iosd.benefit.Network.NetworkUtil;
import tech.iosd.benefit.R;
import tech.iosd.benefit.WaveHelper;

public class TrackAndLog extends Fragment implements View.OnClickListener
{
    public int waterConsumed = 5;
    public int waterTarget = 8;
    public static final String TAG = "StepCounter";
    private WaveHelper mWaveHelper;
    private boolean stepsTab = true;
    private View stepsTabView;
    private View stepsWaterView;
    private View stepsTabViewIndicator;
    private View stepsWaterViewIndicator;
    private ImageView stepsIcon;
    private TextView stepsText;
    private ImageView waterIcon;
    private TextView waterText;
    private WaveView waveView;
    private TextView waterConsumedTxt;
    private TextView waterTargetTxt;
    private CompositeSubscription mSubscriptions;
    private ColumnChartView water_chart;
    private ColumnChartView steps_chart;


    static Context ctx;
    FragmentManager fm;
    private static final int REQUEST_OAUTH_REQUEST_CODE = 0x1001;
    private DatabaseHandler db;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.dashboard_track_and_log, container, false);
        ctx = rootView.getContext();
        fm = getFragmentManager();
        db = new DatabaseHandler(ctx);
        mSubscriptions = new CompositeSubscription();
        waveView = rootView.findViewById(R.id.dashboard_track_indicator_tab_water_wave);

        waveView.setShapeType(WaveView.ShapeType.CIRCLE);
        waveView.setWaveColor(
                Color.parseColor("#88b8f1ed"),
                Color.parseColor("#FF2984FF"));
        mWaveHelper = new WaveHelper(waveView);
        waveView.setWaterLevelRatio((float)waterConsumed/waterTarget);
        mWaveHelper.start();

        stepsTabView = rootView.findViewById(R.id.dashboard_track_indicator_tab_steps);
        stepsWaterView = rootView.findViewById(R.id.dashboard_track_indicator_tab_water);
        stepsTabViewIndicator = rootView.findViewById(R.id.dashboard_track_indicator_steps);
        stepsWaterViewIndicator = rootView.findViewById(R.id.dashboard_track_indicator_water);
        stepsIcon = rootView.findViewById(R.id.steps_icon);
        stepsText = rootView.findViewById(R.id.steps_txt);
        waterIcon = rootView.findViewById(R.id.water_icon);
        waterText = rootView.findViewById(R.id.water_txt);
        waterConsumedTxt = rootView.findViewById(R.id.dashboard_track_water_consumed);
        waterTargetTxt = rootView.findViewById(R.id.dashboard_track_water_target);
        waterConsumedTxt.setText(String.valueOf(waterConsumed));
        waterTargetTxt.setText(String.valueOf(waterTarget));

        rootView.findViewById(R.id.dashboard_track_steps_tab_btn).setOnClickListener(this);
        rootView.findViewById(R.id.dashboard_track_water_tab_btn).setOnClickListener(this);
        rootView.findViewById(R.id.dashboard_track_my_activity).setOnClickListener(this);
        rootView.findViewById(R.id.dashboard_track_meal_log).setOnClickListener(this);
        rootView.findViewById(R.id.dashboard_track_water_add).setOnClickListener(this);
        rootView.findViewById(R.id.dashboard_track_water_subtract).setOnClickListener(this);

        steps_chart = rootView.findViewById(R.id.dashboard_track_steps_graph);
        water_chart = rootView.findViewById(R.id.dashboard_track_water_graph);
        readData();

        List<Column> steps_columns = new ArrayList<>();
        List<SubcolumnValue> steps_values;
        for (int i = 0; i < 5; ++i) {

            steps_values = new ArrayList<>();
            for (int j = 0; j < 3; ++j) {
                steps_values.add(new SubcolumnValue((float) Math.random() * 50f + 5, ChartUtils.COLOR_BLUE));
            }

            Column column = new Column(steps_values);
            steps_columns.add(column);
        }
        ColumnChartData steps_data = new ColumnChartData(steps_columns);
        Axis steps_axisX = new Axis();
        Axis steps_axisY = new Axis().setHasLines(true);
        steps_axisX.setName("Axis X");
        steps_axisY.setName("Axis Y");
        steps_data.setAxisXBottom(steps_axisX);
        steps_data.setAxisYLeft(steps_axisY);
        steps_chart.setColumnChartData(steps_data);
        //make water intake graph
        getUserHistory(db.getUserToken());
        return rootView;
    }


    long total=0;
    private void readData() {
        try {
            Fitness.getHistoryClient(ctx, GoogleSignIn.getLastSignedInAccount(ctx))
                    .readDailyTotal(DataType.TYPE_STEP_COUNT_DELTA)
                    .addOnSuccessListener(
                            new OnSuccessListener<DataSet>() {
                                @Override
                                public void onSuccess(DataSet dataSet) {
                                    total =
                                            dataSet.isEmpty()
                                                    ? 0
                                                    : dataSet.getDataPoints().get(0).getValue(Field.FIELD_STEPS).asInt();
                                    stepsText.setText(String.format("%d", total));
                                }
                            })
                    .addOnFailureListener(
                            new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "There was a problem getting the step count.", e);
                                }
                            });
        }
        catch (Exception exception)
        {
            Toast.makeText(ctx, "Please Provide Permissions", Toast.LENGTH_SHORT).show();
        }

    }
    public static void subscribe(Context context)
    {
        Fitness.getRecordingClient(context, GoogleSignIn.getLastSignedInAccount(context))
                .subscribe(DataType.TYPE_STEP_COUNT_CUMULATIVE)
                .addOnCompleteListener(
                        new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.i(TAG, "Successfully subscribed!");
                                } else {
                                    Log.w(TAG, "There was a problem subscribing.", task.getException());
                                }
                            }
                        });
    }
    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.dashboard_track_steps_tab_btn:
            {
                if(!stepsTab)
                {
                    stepsTabView.setVisibility(View.VISIBLE);
                    stepsTabViewIndicator.setVisibility(View.VISIBLE);
                    stepsWaterView.setVisibility(View.GONE);
                    stepsWaterViewIndicator.setVisibility(View.INVISIBLE);
                    readData();
                    stepsText.setTextColor(getResources().getColor(R.color.black));
                    stepsIcon.setBackgroundResource(R.drawable.ic_steps_on_24dp);
                    waterText.setTextColor(getResources().getColor(R.color.warm_grey));
                    waterIcon.setBackgroundResource(R.drawable.ic_water_off_24dp);
                    stepsTab = true;
                }
                break;
            }
            case R.id.dashboard_track_water_tab_btn:
            {
                if(stepsTab)
                {
                    stepsWaterView.setVisibility(View.VISIBLE);
                    stepsWaterViewIndicator.setVisibility(View.VISIBLE);
                    stepsTabView.setVisibility(View.GONE);
                    stepsTabViewIndicator.setVisibility(View.INVISIBLE);
                    waterText.setTextColor(getResources().getColor(R.color.black));
                    waterIcon.setBackgroundResource(R.drawable.ic_water_on_24dp);
                    stepsText.setTextColor(getResources().getColor(R.color.warm_grey));
                    stepsIcon.setBackgroundResource(R.drawable.ic_steps_off_24dp);
                    stepsTab = false;
                }
                break;
            }
            case R.id.dashboard_track_my_activity:
            {
                fm.beginTransaction().replace(R.id.dashboard_content, new TrackMyActivity()).addToBackStack(null).commit();
                break;
            }
            case R.id.dashboard_track_meal_log:
            {
                fm.beginTransaction().replace(R.id.dashboard_content, new MealLog()).addToBackStack(null).commit();
                break;
            }
            case R.id.dashboard_track_water_add:
            {
                waterConsumed++;
                waveView.setWaterLevelRatio((float)waterConsumed/waterTarget);
                waterConsumedTxt.setText(String.valueOf(waterConsumed));
                waterTargetTxt.setText(String.valueOf(waterTarget));

                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
                String selectedDate = dateFormat.format(Calendar.getInstance().getTime());
                //post the water intake

                PostWaterIntake postWaterIntake=new PostWaterIntake(selectedDate,waterTarget,waterConsumed);
                mSubscriptions.add(NetworkUtil.getRetrofit(db.getUserToken()).sendWaterIntake(postWaterIntake,db.getUserToken())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(this::handleResponseSendWaterIntake,this::handleError));


                break;
            }
            case R.id.dashboard_track_water_subtract:
            {

                waterConsumed--;
                waveView.setWaterLevelRatio((float)waterConsumed/waterTarget);
                waterConsumedTxt.setText(String.valueOf(waterConsumed));
                waterTargetTxt.setText(String.valueOf(waterTarget));
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
                String selectedDate = dateFormat.format(Calendar.getInstance().getTime());
                //post the water intake

                PostWaterIntake postWaterIntake = new PostWaterIntake(selectedDate,waterTarget,waterConsumed);
                mSubscriptions.add(NetworkUtil.getRetrofit(db.getUserToken()).sendWaterIntake(postWaterIntake,db.getUserToken())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(this::handleResponseSendWaterIntake,this::handleError));


                }

                break;
            }
        }


    private void getUserHistory(String token){
        mSubscriptions.add(NetworkUtil.getRetrofit(token).getWaterIntakeHistory(token)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse,this::handleError));
    }

    private void handleResponse(ResponseForWaterHistory response) {
        Log.d("DHRUV", "handleResponse: " + "SUCCESS");
        Toast.makeText(ctx,"Updating Graph!",Toast.LENGTH_SHORT).show();
        updateGraphwaterIntake(response.getData());

    }

    private void updateGraphwaterIntake(Map<String , String > data) {

        if(data.isEmpty())
           Toast.makeText(ctx,"No data",Toast.LENGTH_SHORT).show();

        List<Column> water_columns = new ArrayList<>();
        List<SubcolumnValue> water_values;

        for (Map.Entry<String, String> entry : data.entrySet()) {
            Log.d("error77", "updateGraphwaterIntake: " + entry.getKey() + " = " + entry.getValue());
            water_values = new ArrayList<>();
            for (int j = 0; j < 1; j++) {
                water_values.add(new SubcolumnValue(Integer.parseInt(entry.getValue()), ChartUtils.COLOR_BLUE));
            }
            Column column = new Column(water_values);
            water_columns.add(column);
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        String selectedDate = dateFormat.format(Calendar.getInstance().getTime());

        Log.d("error77", "Date Today: " + selectedDate);
        if(data.get(selectedDate) != null){
            Log.d("error77", "Value Today: " + data.get(selectedDate));
            waterConsumedTxt.setText(data.get(selectedDate));
            Float water = Float.parseFloat(data.get(selectedDate)) ;
            waveView.setWaterLevelRatio(water/waterTarget);
        }



//
//        for (int i = 0; i < data.size(); i++) {
//
//            water_values = new ArrayList<>();
//            for (int j = 0; j < 1; j++) {
//                water_values.add(new SubcolumnValue(data.get(i).getConsumed(), ChartUtils.COLOR_BLUE));
//            }
//
//            Column column = new Column(water_values);
//            water_columns.add(column);
//        }

        ColumnChartData water_data = new ColumnChartData(water_columns);
        Axis water_axisX = new Axis();
        Axis water_axisY = new Axis().setHasLines(true);
        water_axisX.setName("Consumed");
        water_axisY.setName("Date");
        water_data.setAxisXBottom(water_axisX);
        water_data.setAxisYLeft(water_axisY);
        water_chart.setColumnChartData(water_data);

    }

    private void handleResponseSendWaterIntake(ResponseForSuccess responseForSuccess)
    {
        Toast.makeText(ctx,responseForSuccess.getMessage(),Toast.LENGTH_SHORT).show();
    }

    private void handleError(Throwable error)
    {
        Toast.makeText(ctx,"Error",Toast.LENGTH_SHORT).show();
        Log.d("DHRUV", "handleResponse: " + "ERROR");

        if (error instanceof HttpException) {

            Gson gson = new GsonBuilder().create();
            Log.d("error77",error.getMessage());

            try {

                String errorBody = ((HttpException) error).response().errorBody().string();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Log.d("error77",error.getMessage());

        }
    }
}

