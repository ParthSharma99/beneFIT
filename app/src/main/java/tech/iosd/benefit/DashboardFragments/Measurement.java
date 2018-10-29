package tech.iosd.benefit.DashboardFragments;

import android.support.design.widget.Snackbar;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.view.LineChartView;
import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import tech.iosd.benefit.Model.DatabaseHandler;
import tech.iosd.benefit.Model.Response;
import tech.iosd.benefit.Model.ResponseForMesurementsHistory;
import tech.iosd.benefit.Network.NetworkUtil;
import tech.iosd.benefit.R;

import tech.iosd.benefit.Model.ResponseForMesurementsHistory.Data;

public class Measurement extends Fragment
{
    Context ctx;
    FragmentManager fm;
    DatabaseHandler db;

    private TextView bmiTextView;
    private TextView bmiMessageTextView;
    private TextView bmiMessageDetailedTextView;
    private TextView fatPercentageTectView;

    private double height;
    private double weight;
    private double age;
    private double waist;
    private double neck;
    private double hip;
    private String gender;

    private double bmi;
    private double  fatPercentage;

    private CompositeSubscription mSubscriptions;

    List<PointValue> bmi_entries;
    LineChartView bmi_chart;
    LineChartView fat_chart;
    LineChartView basal_chart;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.dashboard_measurement, container, false);
        ctx = rootView.getContext();
        fm = getFragmentManager();

        mSubscriptions = new CompositeSubscription();


        bmiTextView = rootView.findViewById(R.id.dashboard_mesurements_bmi_textview);
        bmiMessageTextView = rootView.findViewById(R.id.dashboard_mesurements_bmi_message_textview);
        bmiMessageDetailedTextView = rootView.findViewById(R.id.dashboard_mesurements_bmi_message_detailetextview);
        fatPercentageTectView = rootView.findViewById(R.id.dashboard_mesurements_fat_percentage_textview);

        db = new DatabaseHandler(getContext());

        gender = db.getUserGender();
        hip = db.getUserNeck();
        neck = db.getUserNeck();
        waist = db.getUserWaist();
        age = db.getUserAge();
        height = db.getUserHeight();
        weight = db.getUserWeight();

        bmi = calculateBMI(height,weight);

        if(bmi<18.5){
            bmiMessageTextView.setText(R.string.bmi_underweight);
            bmiMessageDetailedTextView.setText(R.string.bmi_underweight_details);
        }else if(bmi<24.99){
            bmiMessageTextView.setText(R.string.bmi_overweight);
            bmiMessageDetailedTextView.setText(R.string.bmi_overweight_details);
        }else {
            bmiMessageTextView.setText(R.string.bmi_obesity);
            bmiMessageDetailedTextView.setText(R.string.bmi_obesity_details);
        }

        bmiTextView.setText(String.format("%.2f", bmi));


        fatPercentage = getFatPercentage(bmi, age, gender);

        fatPercentageTectView.setText(String.format("%.2f",fatPercentage));
        Random rand = new Random();
        bmi_chart = rootView.findViewById(R.id.dashboard_measurement_bmi_graph);
        basal_chart = rootView.findViewById(R.id.dashboard_measurement_basal_graph);
        fat_chart = rootView.findViewById(R.id.dashboard_measurement_fat_graph);

        getUserHistory(db.getUserToken());

        rootView.findViewById(R.id.dashboard_measurement_back).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                getFragmentManager().popBackStack();
            }
        });

        return rootView;
    }

    private double getFatPercentage(double bmi, double age, String gender) {
        if(gender.equals("male")){
            return  (1.20 * bmi) + (0.23 * age) - 10.8 - 5.4;
        }else{
            return  (1.20 * bmi) + (0.23 * age) - 5.4;

        }
    }

    private double calculateBMI(double height, double weight) {
        height = height/100;
        return weight /(height*height);
    }

    private void getUserHistory(String token){
        mSubscriptions.add(NetworkUtil.getRetrofit(token).getMeasurementsHistory(token)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse,this::handleError));
    }

    private void handleResponse(ResponseForMesurementsHistory response) {

        /*showSnackBarMessage(response.getMessage()+"\nUpdating profile...");
        ArrayList<>
        String token = response.token.token;*/
        //Toast.makeText(getActivity().getApplicationContext(),token,Toast.LENGTH_SHORT).show();
        showSnackBarMessage("updating graph");
        updateGraphBMI(response.data );
        updateGraphFat(response.data);
        updateGraphBMR(response.data);

    }

    private void updateGraphFat(ArrayList<Data> data) {
        fat_chart.setInteractive(false);
        List<PointValue> fat_entries = new ArrayList<>();
       // for(int i=0; i<10; i++) fat_entries.add((new PointValue(i, rand.nextInt(30))));
        showSnackBarMessage("size"+data.size());
        for(int i=0; i<data.size(); i++){
            double bmiTemp = calculateBMI(data.get(i).getHeight(),data.get(i).getWeight());
            double fatTemp = getFatPercentage(bmiTemp, db.getUserAge(),db.getUserGender());
            fat_entries.add((new PointValue(i+1,(float)fatTemp)));
           // showSnackBarMessage("bmi i"+ i+ " " +bmiTemp);
            //Toast.makeText(getContext(),"i "+i +" hgt"+ data.get(i).getHeight()+" wgt "+ data.get(i).getWeight()+"\nbmi "+ bmiTemp,Toast.LENGTH_SHORT).show();
        }
        Line fat_line = new Line(fat_entries).setColor(Color.BLUE).setCubic(true);
        List<Line> fat_lines = new ArrayList<>();
        fat_line.setHasPoints(false);
        fat_line.setFilled(true);
        fat_lines.add(fat_line);
        LineChartData fat_data = new LineChartData();
        Axis fat_axisX = new Axis().setHasLines(true).setName("Axis X");
        Axis fat_axisY = new Axis().setHasLines(true).setName("Axis Y");
        fat_data.setLines(fat_lines);
        fat_data.setAxisXBottom(fat_axisX);
        fat_data.setAxisYLeft(fat_axisY);
        fat_chart.setLineChartData(fat_data);
    }
    private float calculateBMR(double height, double weight, int age){

        if(gender.equals("male")){
            return (float)(66.47 + (13.7*weight) + (5*height) - (6.8*age));
        }
        return (float)(655.1 + (9.6*weight) + (1.8*height) - (4.7*age));
    }

    private void updateGraphBMR(ArrayList<Data> data){


        basal_chart.setInteractive(false);
        List<PointValue> basal_entries = new ArrayList<>();
        for(int i=0; i<data.size(); i++){
            double bmrTemp = calculateBMR(data.get(i).getHeight(),data.get(i).getWeight(),db.getUserAge());
            basal_entries.add((new PointValue(i+1,(float)bmrTemp)));
            }



        Line basal_line = new Line(basal_entries).setColor(Color.BLUE).setCubic(true);
        List<Line> basal_lines = new ArrayList<>();
        basal_line.setHasPoints(false);
        basal_line.setFilled(true);
        basal_lines.add(basal_line);
        LineChartData basal_data = new LineChartData();
        Axis basal_axisX = new Axis().setHasLines(true).setName("Axis X");
        Axis basal_axisY = new Axis().setHasLines(true).setName("Axis Y");
        basal_data.setLines(basal_lines);
        basal_data.setAxisXBottom(basal_axisX);
        basal_data.setAxisYLeft(basal_axisY);
        basal_chart.setLineChartData(basal_data);


        }

    private void updateGraphBMI(ArrayList<Data> data) {

        bmi_chart.setInteractive(false);
        bmi_entries = new ArrayList<>();
        showSnackBarMessage("size"+data.size());
        for(int i=0; i<data.size(); i++){
            double bmiTemp = calculateBMI(data.get(i).getHeight(),data.get(i).getWeight());
            bmi_entries.add((new PointValue(i+1,(float)bmiTemp)));
          //  showSnackBarMessage("bmi i"+ i+ " " +bmiTemp);
           // Toast.makeText(getContext(),"i "+i +" hgt"+ data.get(i).getHeight()+" wgt "+ data.get(i).getWeight()+"\nbmi "+ bmiTemp,Toast.LENGTH_SHORT).show();
        }
        Line bmi_line = new Line(bmi_entries).setColor(Color.BLUE).setCubic(true);
        List<Line> bmi_lines = new ArrayList<>();
        bmi_line.setHasPoints(false);
        bmi_line.setFilled(true);
        bmi_lines.add(bmi_line);
        LineChartData bmi_data = new LineChartData();
        Axis bmi_axisX = new Axis().setHasLines(true).setName("Axis X");
        Axis bmi_axisY = new Axis().setHasLines(true).setName("Axis Y");
        bmi_data.setLines(bmi_lines);
        bmi_data.setAxisXBottom(bmi_axisX);
        bmi_data.setAxisYLeft(bmi_axisY);
        bmi_chart.setLineChartData(bmi_data);
    }

    private void handleError(Throwable error) {


        if (error instanceof HttpException) {

            Gson gson = new GsonBuilder().create();

            try {

                String errorBody = ((HttpException) error).response().errorBody().string();
                Response response = gson.fromJson(errorBody,Response.class);
                showSnackBarMessage(response.getMessage());

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Log.d("error77",error.getMessage());

            showSnackBarMessage("Network Error !");
        }
    }

    private void showSnackBarMessage(String message) {

        if (getView() != null) {

            Snackbar.make(getView(),message, Snackbar.LENGTH_SHORT).show();
        }
    }
}
