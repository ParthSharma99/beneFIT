package tech.iosd.benefit.DashboardFragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ScaleDrawable;
import android.location.LocationManager;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import tech.iosd.benefit.Model.DatabaseHandler;
import tech.iosd.benefit.Model.MapsMarker;
import tech.iosd.benefit.Model.Stopwatch;
import tech.iosd.benefit.R;
import tech.iosd.benefit.Services.GPSTracker;
import tech.iosd.benefit.Utils.Constants;

import static android.content.Context.LOCATION_SERVICE;
import static tech.iosd.benefit.Utils.Constants.PAUSE_TIMER;
import static tech.iosd.benefit.Utils.Constants.RESUME_TIMER;
import static tech.iosd.benefit.Utils.Constants.START_TIMER;
import static tech.iosd.benefit.Utils.Constants.STOP_TIMER;
import static tech.iosd.benefit.Utils.Constants.UPDATE_TIMER;

public class TrackMyActivityRun extends Fragment implements View.OnClickListener {
    private Context ctx;
    private FragmentManager fm;
    private MapView mMapView;
    CountDownTimer countDownTimer;
    private GoogleMap googleMap;
    private View startLayout;
    private View pauseLayout;
    private View stopLayout;
    private View discardBtn;
    private View resumeBtn;
    private View doneBtn;

    public static TextView calorie_burnt;
    private GPSTracker myService;
    private ArrayList<LatLng> points;
    private Polyline polyline;

    long startTime;
    boolean isServiceConnected = false;
    private LocationManager locationManager;
    private boolean isgoogleMap = false;
    //boolean mServiceBound = false;

    private TextView distance;
    double distaceBeforePause = 0;
    double distace_paused = 0;
    double lastDistance = 0;
    private boolean fistPuase = true;
    private ProgressDialog progressDialog;
    private ArrayList<MapsMarker> mapsMarkers;
    private double currentLatitude, currentLongitude;
    int currentPolyLine = -1;
    private ArrayList<LatLngArray> latLngArray;

    TextView duration, avgPace;

    private class LatLngArray {
        ArrayList<LatLng> latLngsArrayList;

        public LatLngArray() {
            latLngsArrayList = new ArrayList<>();
        }

        public ArrayList<LatLng> getLatLngsArrayList() {
            return latLngsArrayList;
        }

        public void setLatLngsArrayList(ArrayList<LatLng> latLngsArrayList) {
            this.latLngsArrayList = latLngsArrayList;
        }
    }


    private ServiceConnection sc = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            GPSTracker.LocalBinder binder = (GPSTracker.LocalBinder) service;
            myService = binder.getService();
            GPSTracker.LocalBinder myBinder = (GPSTracker.LocalBinder) service;
            myService = myBinder.getService();
            myService.setmContext(ctx);
            Toast.makeText(getActivity().getApplicationContext(), "service connceted", Toast.LENGTH_LONG).show();

            locationManager = (LocationManager) getContext().getSystemService(LOCATION_SERVICE);

            isServiceConnected = true;
            myService.setPaused(true);

            if (isgoogleMap) {

                LatLng myLocation = new LatLng(myService.getLatitude(), myService.getLongitude());

                CameraUpdate center =
                        CameraUpdateFactory.newLatLng(myLocation);
                CameraUpdate zoom = CameraUpdateFactory.zoomTo(20);

                googleMap.moveCamera(center);
                googleMap.animateCamera(zoom);
                //googleMap.setMyLocationEnabled(true);
                progressDialog.hide();
                //gpsTracker.showSettingsAlert();

            }

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

            isServiceConnected = false;
        }
    };

    void bindService() {
        if (isServiceConnected == true)
            return;

        Thread t = new Thread() {
            public void run() {
                Intent i = new Intent(getContext(), GPSTracker.class);
                getContext().startService(i);

                getContext().bindService(i, sc, Context.BIND_AUTO_CREATE);
                startTime = System.currentTimeMillis();
                //isServiceConnected = true;
            }
        };

        t.start();

    }

    void unbindService() {
        if (isServiceConnected == false)
            return;
        Intent i = new Intent(getContext(), GPSTracker.class);
        getContext().stopService(i);
        getContext().unbindService(sc);
        isServiceConnected = false;
        Toast.makeText(getActivity().getApplicationContext(), "service disconnceted", Toast.LENGTH_LONG).show();

    }

    //This method leads you to the alert dialog box.
    void checkGps() {
        locationManager = (LocationManager) getContext().getSystemService(LOCATION_SERVICE);

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {


            showGPSDisabledAlertToUser();
        }
    }

    //This method configures the Alert Dialog box.
    private void showGPSDisabledAlertToUser() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setMessage("Enable GPS to use application")
                .setCancelable(false)
                .setPositiveButton("Enable GPS",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent callGPSSettingIntent = new Intent(
                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(callGPSSettingIntent);
                            }
                        });
        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    public static String trackType;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            trackType = getArguments().getString("TRACK_TYPE");
        } else {
            Toast.makeText(getActivity(), "arguments is null ", Toast.LENGTH_LONG).show();
        }
    }

    private DatabaseHandler db;
    public static int userWeight;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading GPS and Maps.");
        progressDialog.setCancelable(false);
        progressDialog.show();
        View rootView = inflater.inflate(R.layout.dashboard_track_my_activity_run, container, false);

        //polylineArrays =  new ArrayList<>();
        latLngArray = new ArrayList<>();
        db = new DatabaseHandler(getContext());
        ctx = rootView.getContext();
        distance = rootView.findViewById(R.id.dashboard_track_my_activity_distance_textview);
        calorie_burnt = rootView.findViewById(R.id.calories_track_my_activity);
        duration = rootView.findViewById(R.id.track_activity_duration);
        avgPace = rootView.findViewById(R.id.track_activity_avg_pace);
        bindService();
        mapsMarkers = new ArrayList<>();
        userWeight = db.getUserWeight();
        fm = getFragmentManager();

        startLayout = rootView.findViewById(R.id.dashboard_track_my_activity_running_start);
        pauseLayout = rootView.findViewById(R.id.dashboard_track_my_activity_running_pause);
        stopLayout = rootView.findViewById(R.id.dashboard_track_my_activity_running_stop);
        discardBtn = rootView.findViewById(R.id.dashboard_track_my_activity_running_discard);
        resumeBtn = rootView.findViewById(R.id.dashboard_track_my_activity_running_resume);
        doneBtn = rootView.findViewById(R.id.dashboard_track_my_activity_running_done);

        distance = (TextView) rootView.findViewById(R.id.dashboard_track_my_activity_distance_textview);

        rootView.findViewById(R.id.back_icon).setOnClickListener(this);
        discardBtn.setOnClickListener(this);
        resumeBtn.setOnClickListener(this);
        doneBtn.setOnClickListener(this);
        startLayout.setOnClickListener(this);
        pauseLayout.setOnClickListener(this);

        mMapView = rootView.findViewById(R.id.track_running_map);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume();

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                isgoogleMap = true;
                googleMap = mMap;

                if (isServiceConnected) {
                    progressDialog.hide();
                    LatLng myLocation = new LatLng(myService.getLatitude(), myService.getLongitude());

                    CameraUpdate center =
                            CameraUpdateFactory.newLatLng(myLocation);
                    CameraUpdate zoom = CameraUpdateFactory.zoomTo(20);

                    googleMap.moveCamera(center);
                    googleMap.animateCamera(zoom);
                    //googleMap.setMyLocationEnabled(true);
                }

                // For showing a move to my location button
                if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    googleMap.setMyLocationEnabled(true);

                }

               /* if(isServiceConnected ){
                    LatLng myLocation = new LatLng(myService.getLatitude(), myService.getLongitude());

                    CameraUpdate center=
                            CameraUpdateFactory.newLatLng(myLocation);
                    CameraUpdate zoom=CameraUpdateFactory.zoomTo(15);

                    googleMap.moveCamera(center);
                    googleMap.animateCamera(zoom);
                }*/


            }
        });


        IntentFilter intentFilter = new IntentFilter(Constants.GPS_UPDATE);

        BroadcastReceiver mReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                //     Toast.makeText(context,"activity",Toast.LENGTH_LONG).show();

            /*

            Intent stopIntent = new Intent(MainActivity.this,
                        BroadcastService.class);
                stopService(stopIntent);
                */
                Bundle bundle = intent.getExtras();
                String key = bundle.getString("key");
                //Toast.makeText(getContext(),"broadccastint "+key,Toast.LENGTH_SHORT).show();

                if (intent.getAction().equals(Constants.GPS_UPDATE)) {
                    //intent.getExtras();
                    if (key.equalsIgnoreCase(Constants.GPS_IS_UPDATED)) {
                        currentLatitude = myService.getLatitude();
                        currentLongitude = myService.getLongitude();
                        redrawLine();
                    } else if (key.equalsIgnoreCase(Constants.GPS_CONNECTED)) {
                        currentLatitude = myService.getLatitude();
                        currentLongitude = myService.getLongitude();
                        LatLng myLocation = new LatLng(currentLatitude, currentLongitude);

                        CameraUpdate center =
                                CameraUpdateFactory.newLatLng(myLocation);
                        CameraUpdate zoom = CameraUpdateFactory.zoomTo(18);
                        googleMap.moveCamera(center);
                        googleMap.animateCamera(zoom);
                    } else if (key.equalsIgnoreCase(Constants.GPS_ONLY_LOCATION_CHANGE)) {
                        currentLatitude = myService.getLatitude();
                        currentLongitude = myService.getLongitude();
                        LatLng myLocation = new LatLng(currentLatitude, currentLongitude);

                        CameraUpdate center =
                                CameraUpdateFactory.newLatLng(myLocation);
                        CameraUpdate zoom = CameraUpdateFactory.zoomTo(18);
                        googleMap.moveCamera(center);
                        googleMap.animateCamera(zoom);
                    }
                    //Toast.makeText(getContext(),"activity",Toast.LENGTH_LONG).show();

                }
            }
        };
        getActivity().getApplicationContext().registerReceiver(mReceiver, intentFilter);


        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();


        mMapView.onDestroy();

        if (isServiceConnected == true) {
            //myService.stopLocationUpdates();
            unbindService();
            myService.onDestroy();

        }


    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_icon:

                fm.popBackStack();
                break;
            case R.id.dashboard_track_my_activity_running_start: {
                mHandler.sendEmptyMessage(START_TIMER);
                startLayout.setVisibility(View.GONE);
                pauseLayout.setVisibility(View.VISIBLE);
                stopLayout.setVisibility(View.GONE);
                myService.setPaused(false);
                mapsMarkers.add(new MapsMarker(myService.getLatitude(), myService.getLongitude(), true));
                CircleOptions circleOptions = null;
                currentPolyLine++;
                latLngArray.add(new LatLngArray());
                points = latLngArray.get(currentPolyLine).getLatLngsArrayList();
                circleOptions = new CircleOptions().center(new LatLng(myService.getLatitude(), myService.getLongitude())).radius(3).fillColor(Color.argb(255, 255, 82, 82)).strokeColor(Color.argb(100, 67, 1, 1)).strokeWidth(4).zIndex(2.0f);

                latLngArray.get(currentPolyLine).getLatLngsArrayList().add(myService.getLatLng());

                googleMap.addCircle(circleOptions);

                startRunning();
                break;
            }
            case R.id.dashboard_track_my_activity_running_pause: {
                mHandler.sendEmptyMessage(PAUSE_TIMER);
                startLayout.setVisibility(View.GONE);
                pauseLayout.setVisibility(View.GONE);
                stopLayout.setVisibility(View.VISIBLE);
                discardBtn.setVisibility(View.VISIBLE);
                //mapsMarkers.add(new MapsMarker(myService.getLatitude(),myService.getLongitude(),false));
                currentPolyLine++;
                latLngArray.add(new LatLngArray());


                myService.setPaused(true);
                break;
            }
            case R.id.dashboard_track_my_activity_running_resume: {
                mHandler.sendEmptyMessage(RESUME_TIMER);
                //gpsTracker.setPaused(false);
                Toast.makeText(getActivity().getApplicationContext(), "rrere", Toast.LENGTH_LONG).show();
                startLayout.setVisibility(View.GONE);
                pauseLayout.setVisibility(View.VISIBLE);
                stopLayout.setVisibility(View.GONE);
                discardBtn.setVisibility(View.GONE);
                myService.setPaused(false);
                // mapsMarkers.add(new MapsMarker(myService.getLatitude(),myService.getLongitude(),true));

                break;

            }

            case R.id.dashboard_track_my_activity_running_discard: {
                mHandler.sendEmptyMessage(STOP_TIMER);
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
                View mView = getActivity().getLayoutInflater().inflate(R.layout.dialog_accept, null);
                TextView dialogMsg = mView.findViewById(R.id.dialog_message);
                Button dialogAccept = mView.findViewById(R.id.dialog_accept);
                Button dialogCancel = mView.findViewById(R.id.dialog_cancel);
                mBuilder.setView(mView);
                final AlertDialog dialog = mBuilder.create();
                dialog.show();

                dialogMsg.setText(R.string.alert_discard_activity);
                dialogAccept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        startLayout.setVisibility(View.VISIBLE);
                        pauseLayout.setVisibility(View.GONE);
                        stopLayout.setVisibility(View.GONE);
                        discardBtn.setVisibility(View.GONE);
                        myService.stoptacking();
                        points.clear();
                        distance.setText(String.valueOf(0));
                        currentPolyLine = -1;
                        latLngArray = new ArrayList<>();
                        googleMap.clear();

                    }
                });
                dialogCancel.setOnClickListener(new View.OnClickListener() {
                    @Override

                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                break;
            }
            case R.id.dashboard_track_my_activity_running_done: {
                mHandler.sendEmptyMessage(STOP_TIMER);
                myService.stoptacking();
                points.clear();
//                distance.setText(String.valueOf(0));
                currentPolyLine = -1;
                latLngArray = new ArrayList<>();
                googleMap.clear();
                TrackActivityCompleted trackActivityCompleted = new TrackActivityCompleted();
                Bundle args = new Bundle();
                args.putString("DISTANCE", distance.getText().toString());
                args.putString("CALORIE", calorie_burnt.getText().toString());
                args.putString("DURATION", duration.getText().toString());
                args.putString("AVG_PACE", avgPace.getText().toString());
                args.putFloat("DURATION_SEC", (float) timer.getTotalElapsedTimeSecs());
                args.putString("TRACK_TYPE", trackType);
                trackActivityCompleted.setArguments(args);
                fm.beginTransaction().replace(R.id.dashboard_content, trackActivityCompleted).addToBackStack(null).commit();
            }
        }
    }

    double avgSpeed = 0.0;
    Stopwatch timer = new Stopwatch();
    final int REFRESH_RATE = 1000;
    private String durationBeforePause = "00:00";
    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case START_TIMER:
                    timer.start(); //start timer
                    mHandler.sendEmptyMessage(UPDATE_TIMER);
                    break;

                case UPDATE_TIMER:
                    duration.setText(timer.toString());
                    double distaceTravelled = Double.parseDouble(distance.getText().toString());
                    double timeWorkout = ((timer.getTotalElapsedTimeSecs()) / (60.0 * 60.0));
                    if (timeWorkout != 0)
                        avgSpeed = (double) distaceTravelled / timeWorkout;
                    if (avgSpeed != 0) {
                        avgPace.setText(String.format("%.1f", avgSpeed));
                    } else {
                        avgPace.setText("0");
                    }
                    mHandler.sendEmptyMessageDelayed(UPDATE_TIMER, REFRESH_RATE);
                    break;
                case PAUSE_TIMER:
                    timer.pause();
                    duration.setText(timer.toString());
                    break;
                case RESUME_TIMER:
                    timer.resume();
                    duration.setText(timer.toString());
                    break;
                case STOP_TIMER:
                    mHandler.removeMessages(UPDATE_TIMER);
                    timer.stop();//stop timer
                    duration.setText(timer.toString());
                    break;

                default:
                    break;
            }
        }
    };

    long curr_time, prev_time = 0;
    int totalCaloriesBurnt = 0;

    private void calculateAndShowDistace() {
        if (curr_time != 0) {
            prev_time = curr_time;
            curr_time = System.currentTimeMillis();
        } else {
            prev_time = System.currentTimeMillis();
            curr_time = System.currentTimeMillis();
        }
        Double disLocal = 0.0;

        for (int i = 0; i <= currentPolyLine; i++) {


            disLocal = disLocal + SphericalUtil.computeLength(latLngArray.get(i).getLatLngsArrayList());
        }
        distance.setText(String.format("%.1f", (disLocal) / 1000));
        if ((disLocal / 1000) != 0 && (curr_time - prev_time) != 0) {

            double totalDistance = disLocal / 1000;
            long timeDiff = curr_time - prev_time;
            double timeInHrs = ((double) timeDiff) / (1000 * 60 * 60);
            double speed = (totalDistance / timeInHrs) * (0.621371);
            double mets = 0.0;
            switch (trackType) {
                case "RUNNING":
                    mets = getMetsRunning(speed);
                    break;
                case "WALKING":
                    mets = getMetsWalk(speed);
                    break;
                case "RIDE":
                    mets = getMetsRide(speed);
                    break;
            }
            totalCaloriesBurnt = (int) (totalCaloriesBurnt + (timeInHrs * mets * userWeight));
        }
        calorie_burnt.setText(totalCaloriesBurnt + "");

    }

    private double getMetsRunning(double speed) {
        double metsRun = 0.0;
        if (speed > 2 && speed < 5.4)
            metsRun = 2.8;
        else if (speed >= 2 && speed < 5.5)
            metsRun = 2.8;
        else if (speed == 5.5)
            metsRun = 3.5;
        else if (speed > 5.5 && speed <= 9.3)
            metsRun = 4.5;
        else if (speed > 9.3 && speed <= 9.9)
            metsRun = 5.8;
        else if (speed > 9.9 && speed <= 11.9)
            metsRun = 6.8;
        else if (speed > 11.9 && speed <= 13.9)
            metsRun = 8;
        else if (speed > 13.9 && speed <= 15.9)
            metsRun = 10;
        else if (speed > 15.9 && speed <= 19)
            metsRun = 12;
        else if (speed > 19)
            metsRun = 16;
        return metsRun;
    }

    private double getMetsWalk(double speed) {
        double metsRun = 0.0;
        if (speed <= 2)
            metsRun = 2;
        else if (speed > 2 && speed < 2.5)
            metsRun = 2.8;
        else if (speed == 2.5)
            metsRun = 3;
        else if (speed > 2.5 && speed <= 3.4)
            metsRun = 3.5;
        else if (speed > 3.4 && speed <= 3.9)
            metsRun = 4.3;
        else if (speed > 3.9 && speed <= 4.4)
            metsRun = 5;
        else if (speed > 4.4 && speed < 5)
            metsRun = 7;
        else if (speed > 5)
            metsRun = 8.3;
        return metsRun;
    }

    private double getMetsRide(double speed) {
        double metsRun = 0.0;
        if (speed < 4)
            metsRun = 6;
        else if (speed >= 4.1 && speed <= 4.6)
            metsRun = 7;
        else if (speed > 4.6 && speed <= 5.1)
            metsRun = 8.3;
        else if (speed > 5.1 && speed <= 5.9)
            metsRun = 9;
        else if (speed > 5.9 && speed <= 6.5)
            metsRun = 9.8;
        else if (speed > 6.5 && speed <= 6.9)
            metsRun = 10.5;
        else if (speed > 6.9 && speed <= 7.4)
            metsRun = 11;
        else if (speed > 7.4 && speed <= 7.9)
            metsRun = 11.5;
        else if (speed > 7.9 && speed <= 8.5)
            metsRun = 11.8;
        else if (speed > 8.5 && speed <= 8.9)
            metsRun = 12.3;
        else if (speed > 8.9 && speed <= 9.9)
            metsRun = 12.8;
        else if (speed > 9.9 && speed <= 10.9)
            metsRun = 14.5;
        else if (speed > 10.9 && speed <= 11.9)
            metsRun = 16;
        else if (speed > 11.9 && speed <= 12.9)
            metsRun = 19;
        else if (speed > 12.9 && speed < 14)
            metsRun = 19.8;
        else if (speed >= 14)
            metsRun = 23;
        return metsRun;
    }


    private void startRunning() {

        if (myService.isGoogleAPIConnected) {
            currentLatitude = myService.getLatitude();
            currentLongitude = myService.getLongitude();
            LatLng myLocation = new LatLng(currentLatitude, currentLongitude);

            CameraUpdate center =
                    CameraUpdateFactory.newLatLng(myLocation);
            CameraUpdate zoom = CameraUpdateFactory.zoomTo(18);
            googleMap.moveCamera(center);
            googleMap.animateCamera(zoom);
        }


        String stringLongitude = String.valueOf(myService.getLongitude());
        myService.setPaused(false);


        LatLng sydney = new LatLng(myService.getLatitude(), myService.getLongitude());
        Drawable drawable = getResources().getDrawable(R.drawable.marker_green);
        drawable.setBounds(0, 0, (int) (drawable.getIntrinsicWidth() * 0.5),
                (int) (drawable.getIntrinsicHeight() * 0.5));
        ScaleDrawable sd = new ScaleDrawable(drawable, 0, 0.5f, 0.5f);
        //googleMap.addMarker(new MarkerOptions().position(sydney).title("Starting Point").snippet("You started your running journey from this point.").icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_green)));

        CameraPosition cameraPosition = new CameraPosition.Builder().target(sydney).zoom(18).build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(18);
        googleMap.animateCamera(zoom);

       /* points.clear();
        myService.setPoints(points);*/


    }

    private void updateMarkers() {
        for (int i = 0; i < mapsMarkers.size(); i++) {
            Toast.makeText(getContext(), String.valueOf(i), Toast.LENGTH_SHORT).show();
            LatLng latLng = new LatLng(mapsMarkers.get(i).getLatitude(), mapsMarkers.get(i).getLongitude());
            CircleOptions circleOptions = null;
            if (mapsMarkers.get(i).isTypeStart()) {
                circleOptions = new CircleOptions().center(latLng).radius(3).fillColor(Color.argb(255, 255, 82, 82)).strokeColor(Color.argb(100, 67, 1, 1)).strokeWidth(4).zIndex(2.0f);

            } else {
                circleOptions = new CircleOptions().center(latLng).radius(3).fillColor(Color.argb(255, 82, 255, 82)).strokeColor(Color.argb(100, 1, 67, 1)).strokeWidth(4).zIndex(2.0f);
            }
            googleMap.addCircle(circleOptions);

            /*googleMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_green)));*/
        }
    }

    private void redrawLine() {

        //points = myService.getPoints();

        double distance_number;

        distance_number = SphericalUtil.computeLength(points);

        //lastDistance = distance_number - distace_paused;
        //distance_number = distance_number - distace_paused;
        //distace_total = distance_number;
        if (!myService.isPaused()) {

            googleMap.clear();
            LatLng latLng = new LatLng(myService.getLatitude(), myService.getLongitude());


            CameraUpdate center =
                    CameraUpdateFactory.newLatLng(new LatLng(myService.getLatitude(), myService.getLongitude()));

            googleMap.moveCamera(center);

            //distance.setText(String.format("%.2f", gpsTracker.getDistance()));
            if (!fistPuase) {
                distace_paused = distace_paused + (distance_number - distaceBeforePause);

            }
            fistPuase = true;
            for (int i = 0; i <= currentPolyLine; i++) {
                PolylineOptions options = new PolylineOptions().width(8).color(getResources().getColor(R.color.polyline_fill)).geodesic(true).zIndex(1.5f);
           /* for (int i = 0; i < points.size(); i++) {
                LatLng point = points.get(i);
                options.add(point);
            }*/

                if (i == currentPolyLine) {
                    latLngArray.get(i).getLatLngsArrayList().add(myService.getLatLng());
                }

                options.addAll(latLngArray.get(i).getLatLngsArrayList());


                polyline = googleMap.addPolyline(options); //add Polyline
                options.color(R.color.polyline_stroke).width(12).zIndex(1.4f);
                googleMap.addPolyline(options);


            }


            updateMarkers();
            CircleOptions circleOptions = null;
            circleOptions = new CircleOptions().center(latLng).radius(3).fillColor(Color.argb(255, 82, 255, 82)).strokeColor(Color.argb(100, 1, 67, 1)).strokeWidth(4).zIndex(2.0f
            );
            googleMap.addCircle(circleOptions);


            //distance.setText(String.format("%.1f",(distance_number-distace_paused)/1000));
            calculateAndShowDistace();

        } else {
            if (fistPuase) {
                fistPuase = false;
                distaceBeforePause = distance_number;
            }


        }


    }


}
