package tech.iosd.benefit.DashboardFragments;

import android.app.Activity;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.thin.downloadmanager.DefaultRetryPolicy;
import com.thin.downloadmanager.DownloadRequest;
import com.thin.downloadmanager.DownloadStatusListener;
import com.thin.downloadmanager.ThinDownloadManager;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import devs.mulham.horizontalcalendar.HorizontalCalendar;
import devs.mulham.horizontalcalendar.utils.HorizontalCalendarListener;
import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import tech.iosd.benefit.Adapters.DashboardWorkoutAdapter;
import tech.iosd.benefit.Model.DatabaseHandler;
import tech.iosd.benefit.Model.Exercise;
import tech.iosd.benefit.Model.MealLogFullDay;
import tech.iosd.benefit.Model.ResponseForGetExcerciseVideoUrl;
import tech.iosd.benefit.Model.ResponseForWorkoutForDate;
import tech.iosd.benefit.Model.VideoPlayerItem;
import tech.iosd.benefit.Network.NetworkUtil;
import tech.iosd.benefit.R;
import tech.iosd.benefit.VideoPlayer.VideoPlayerActivity;

import static android.content.Context.MODE_PRIVATE;

public class MyWorkout extends Fragment implements DashboardWorkoutAdapter.onItemClickListener
{
    public Calendar selDate;
    Calendar c = Calendar.getInstance();
    SimpleDateFormat df;
    String[] months = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
    Context ctx;
    FragmentManager fm;
    private int totalVideos;
    SharedPreferences sharedPreferences1;
    private String selectedDate;
    private SimpleDateFormat dateFormat;
    private ProgressDialog progressDialog;
    private CompositeSubscription compositeSubscription, mcompositeSubscription;
    private DatabaseHandler db;
    private RecyclerView recyclerView;
    private DashboardWorkoutAdapter adapter;
    private ArrayList<Exercise>  exercises = new ArrayList<>();
    private ArrayList<Boolean>  secondPresent = new ArrayList<>();

    private String type;

    private Button startWorkout;
    private ThinDownloadManager downloadManager;
    private int currentPosition =0;

    private  AlertDialog.Builder mBuilder;
    private AlertDialog downloadDialog;
    private View mView;
    private ProgressBar progressBar;
    private TextView progressTV;
    private TextView numberOfCurrentVideo;
    private ProgressBar pbar;
    private int noOfDiffId =0;
    private int noOfCurrentVideUser=0;
    boolean allVideoDownloaded = true;
    public TextView tcal2, texc2, tmin2;
    int time =0;
    float calory = 0;
    public ImageView i1;
    public  TextView t1,t2,t3,t4, t5;
    private LinearLayout linear_layout_to_be_hidden;
    private ImageView RestImageView;
    private Button download_button;
    private TextView rest_text_view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.dashboard_my_workouts, container, false);
        ctx = rootView.getContext();
        fm = getFragmentManager();
        progressDialog =  new ProgressDialog(getContext());
        progressDialog.setCancelable(false);
        progressDialog.setMessage("working..");

        type = "tutorial";

        compositeSubscription = new CompositeSubscription();
        mcompositeSubscription =  new CompositeSubscription();

        downloadManager =  new ThinDownloadManager();
        sharedPreferences1 = getContext().getSharedPreferences("SAVE_EXERCISE", MODE_PRIVATE);

        db = new DatabaseHandler(getContext());

        recyclerView =  rootView.findViewById(R.id.dashboard_my_workouts_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        adapter = new DashboardWorkoutAdapter(exercises,getActivity(),this);

        mBuilder = new AlertDialog.Builder(getActivity());
        mView = getActivity().getLayoutInflater().inflate(R.layout.dialog_download, null);
        progressBar = mView.findViewById(R.id.main_progressbar);
        pbar = mView.findViewById(R.id.pbar);
        progressTV =  mView.findViewById(R.id.percentage_tv);
        linear_layout_to_be_hidden= mView.findViewById(R.id.workout_linear_layout_to_be_hidden);
        RestImageView = mView.findViewById(R.id.workout_rest_imageView);
        download_button= mView.findViewById(R.id.dashboard_my_workouts_start_workout);
        rest_text_view = mView.findViewById(R.id.rest_day_text);
//        linear_layout_to_be_hidden.setVisibility(View.VISIBLE);
  //      download_button.setVisibility(View.VISIBLE);

        numberOfCurrentVideo = mView.findViewById(R.id.currentfileDownload);
        mBuilder.setView(mView);
        downloadDialog = mBuilder.create();

        startWorkout = rootView.findViewById(R.id.dashboard_my_workouts_start_workout);
        t1 = rootView.findViewById(R.id.tvdes1);
        t2 = rootView.findViewById(R.id.tvdes2);
        t3 = rootView.findViewById(R.id.tvmain);
        t4 = rootView.findViewById(R.id.tvinten);
        t5 = rootView.findViewById(R.id.tvintenmode);



        startWorkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                downloadDialog.show();
               // downloadDialog.setCancelable(false);
                SharedPreferences sharedPreferences1 = getContext().getSharedPreferences("SAVE_EXERCISE", MODE_PRIVATE);
                sharedPreferences1.edit().putInt("CaloriesBurnt",0).apply();
                downloadFiles();
            }
        });

        Calendar startDate = Calendar.getInstance();
        startDate.add(Calendar.MONTH, -1);

        Calendar endDate = Calendar.getInstance();
        endDate.add(Calendar.MONTH, 1);

        HorizontalCalendar horizontalCalendar = new HorizontalCalendar.Builder(rootView, R.id.my_workout_calendar)
                .range(startDate, endDate)
                .datesNumberOnScreen(7)
                .mode(HorizontalCalendar.Mode.DAYS)
                .configure()
                .formatMiddleText("EEEEE\n").sizeMiddleText(12)
                .formatBottomText("dd").sizeBottomText(26)
                .showTopText(false)
                .end()
                .build();

        final TextView lbl_year = rootView.findViewById(R.id.my_workout_calendar_year);
        final TextView lbl_month = rootView.findViewById(R.id.my_workout_calendar_month);
        tcal2 = rootView.findViewById(R.id.tvcalory1);
        texc2 = rootView.findViewById(R.id.tvexc1);
        tmin2= rootView.findViewById(R.id.tvmin1);
        dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);

        selectedDate = dateFormat.format(Calendar.getInstance().getTime());

        horizontalCalendar.setCalendarListener(new HorizontalCalendarListener()
        {
            @Override
            public void onDateSelected(Calendar date, int position)
            {
                selDate = date;
                selectedDate = dateFormat.format(date.getTime());
                lbl_year.setText(String.valueOf(date.get(Calendar.YEAR)));
                lbl_month.setText(months[date.get(Calendar.MONTH)]);
                progressDialog.show();
                startWorkout.setText("Download Workout");
                startWorkout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        downloadDialog.show();
                        sharedPreferences1.edit().putInt("CaloriesBurnt",0).apply();
                        // downloadDialog.setCancelable(false);
                        downloadFiles();
                    }
                });
                getWorkoutData(selectedDate);
            }
        });
        getWorkoutData(selectedDate);
        return rootView;
    }

    private int getNumberOfDifferntId(){
        ArrayList <String> stringForCheck =  new ArrayList<>();
        int value =0;
        for (int i = 0; i<exercises.size();i++){
            String id = exercises.get(i).getExercise().get_id();
            value++;
            for(int j =0;j<stringForCheck.size();j++){
                if(stringForCheck.size() == 0){
                    stringForCheck.add(id);
                }else if(stringForCheck.get(j).equals(id)){
                    break;
                }else if(j== stringForCheck.size()-1){
                    stringForCheck.add(id);
                }
            }
        }

        return stringForCheck.size();
    }
    private void showSnackBarMessage(String message) {

        if (getView() != null) {

            Snackbar.make(getView(),message, Snackbar.LENGTH_LONG).show();

        }
    }


    private void getWorkoutData(String date){
        if(!progressDialog.isShowing()){
            progressDialog.show();
        }

        Log.d("token",db.getUserToken());
        compositeSubscription.add(NetworkUtil.getRetrofit(db.getUserToken()).getWorkoutforDate(date,db.getUserToken())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponseGetMeal,this::handleErrorGetMeal));
    }

    private void handleResponseGetMeal(ResponseForWorkoutForDate responseForWorkoutForDate) {
            progressDialog.hide();
            if(!responseForWorkoutForDate.isSuccess()){
                Log.d("error77", "handleResponseGetMeal: " + "eroor");
                Toast.makeText(ctx,"Error loading workouts!",Toast.LENGTH_SHORT).show();
     //           return;
      //      }
      //  if (responseForWorkoutForDate.getData().getWorkout().getExercises().isEmpty()){

            // Ye Tab Call Hota Hai Jab Server Pe Workout Nahi Hota.
            // Yaha Pe Rest Day Wala Aayega. Cool?
            //hide the layout containing info regarding workouts since there ain't any
                Activity act = (Activity)ctx;
                act.runOnUiThread(new Runnable(){
                    @Override
                    public void run() {
                        linear_layout_to_be_hidden.setVisibility(LinearLayout.GONE);
                        download_button.setVisibility(View.GONE);
                        rest_text_view.setVisibility(View.VISIBLE);
                        RestImageView.setVisibility(View.VISIBLE);

                    } });
            exercises.clear();
       //    adapter.notifyDataSetChanged();
           recyclerView.getAdapter().notifyDataSetChanged();
            tcal2.setText("0");
            tmin2.setText("0");
            texc2.setText("0");
            startWorkout.setText(" ");
            //i1.setImageResource(R.drawable.nowork);

            t1.setText(" ");
            t2.setText(" ");
            t3.setText("");
            t4.setText(" ");
            t5.setText(" ");


            return;
            //Download completes here
        }
        Activity act = (Activity)ctx;
        act.runOnUiThread(new Runnable(){
            @Override
            public void run() {
                linear_layout_to_be_hidden.setVisibility(LinearLayout.VISIBLE);
                RestImageView.setVisibility(View.GONE);
                rest_text_view.setVisibility(View.GONE);
                download_button.setVisibility(View.VISIBLE);

            } });
        totalVideos=responseForWorkoutForDate.getData().getVideoCount();
        sharedPreferences1.edit().putString("WORKOUT_ID",responseForWorkoutForDate.getData().get_id()).apply();
        Log.d("error77"," " +responseForWorkoutForDate.getData().getWorkout().getExercises().size());
        exercises = responseForWorkoutForDate.getData().getWorkout().getExercises();
        for (int i =0 ; i<exercises.size();i++){

            time = calculateTime(exercises.get(i).getReps(), exercises.get(i).getTimeTaken(),
                    exercises.get(i).getRest(), exercises.get(i).getSets());


            calory = calculateCalory(exercises.get(i).getReps(), exercises.get(i).getExercise().getTimeTaken(),
                    exercises.get(i).getExercise().getMets(), exercises.get(i).getSets());
        }
        adapter.setExercises(exercises);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        noOfDiffId = getNumberOfDifferntId();



        tcal2.setText(String.valueOf((int)calory));
        tmin2.setText(String.valueOf(time));
        texc2.setText(String.valueOf(exercises.size()));


        checkFiles();

    }

    int calculateTime(int reps,  int timeTaken, int rest, int sets){
        int t = 0;
        t += ((reps*timeTaken) +rest) * sets;
        return  t;
    }

    float calculateCalory(int reps, float timeTaken, float mets, int sets){
        float cal = 0;
        int personWeight=0;
        Log.d("CAL", "calculateCalory: Reps " + String.valueOf(mets));
        Log.d("CAL", "calculateCalory: Reps " + String.valueOf(sets));
        Log.d("CAL", "calculateCalory: Reps " + String.valueOf(timeTaken));
        Log.d("CAL", "calculateCalory: Reps " + String.valueOf(reps));

        cal += (reps*timeTaken*mets*sets);
        personWeight = db.getUserWeight();
        Log.d("CAL", "calculateCalory: " + String.valueOf(personWeight));
        // Log.d("CAL", "calculateCalory: " + String.valueOf(cal));

        cal = cal*personWeight;
        //  Log.d("CAL", "calculateCalory: " + String.valueOf(cal));

        cal = cal/36;
        Log.d("CAL", "calculateCalory: " + String.valueOf(cal));
        return cal;
    }

    private void handleErrorGetMeal(Throwable error) {
        progressDialog.hide();
//        pbar.setVisibility(View.GONE);
        Log.d("error77",error.getMessage());


        if (error instanceof HttpException) {

            Gson gson = new GsonBuilder().create();

            try {

                String errorBody = ((HttpException) error).response().errorBody().string();
//                Response response = gson.fromJson(errorBody,Response.class);
                //showSnackBarMessage(response.getMessage());
               // Log.d("error77",error.getMessage());

                fm.popBackStack();

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
           // Log.d("error77",error.getMessage());

           // showSnackBarMessage("Network Error !");
        }
    }


    private void getExcercise(String url,String type){
        mcompositeSubscription.add(NetworkUtil.getRetrofit(db.getUserToken()).getExerciseVideoUrl(url,db.getUserToken(),type)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponseSendMealLog,this::handleError));
    }

    private void handleResponseSendMealLog(ResponseForGetExcerciseVideoUrl reponse) {
        String url = reponse.getData();
        Toast.makeText(getActivity().getApplicationContext(),"url fetch success",Toast.LENGTH_SHORT).show();
        getVideo(url);
    }
    private void handleError(Throwable error) {


        if (error instanceof HttpException) {

            Gson gson = new GsonBuilder().create();
            //showSnackBarMessage("Network Error !");
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

            //showSnackBarMessage("Network Error !");
        }
    }
    private void downloadFiles() {
        Log.d("download","type: "+ type+"position: "+currentPosition);
        if(exercises.size()==0 || exercises==null)
        {
            downloadDialog.dismiss();
            Toast.makeText(ctx, "Workout Not Available", Toast.LENGTH_SHORT).show();
            return;
        }
        if (currentPosition>=exercises.size()){
          //  downloadDialog.hide();
            if(allVideoDownloaded){
                startWorkout.setText("START WORKOUT");
                startWorkout.setOnClickListener(startClickListener);

            }else {
                showSnackBarMessage("All files not downloaded.\nPlease try again.");
            }
            return;
        }
        File file = null ;
        if (type.equals("a")){
            if (exercises.get(currentPosition).getExercise().isVideoA()){
                file = new File(getActivity().getFilesDir().toString()+"/videos/"+exercises.get(currentPosition).getExercise().get_id()+"_a.mp4");
            }else {
                type = "b";
                downloadFiles();
                return;
            }

        }else if (type.equals("b")){
            if (exercises.get(currentPosition).getExercise().isVideoB()){
                file = new File(getActivity().getFilesDir().toString()+"/videos/"+exercises.get(currentPosition).getExercise().get_id()+"_b.mp4");
            }else {
                type = "tutorial";
                currentPosition++;
                downloadFiles();
                return;
            }

        }else{
             file = new File(getActivity().getFilesDir().toString()+"/videos/"+exercises.get(currentPosition).getExercise().get_id()+".mp4");

        }
        if(file.exists()){
            adapter.notifyDataSetChanged();
            Toast.makeText(getContext(),"file arleady presenet "+type+(currentPosition+1),Toast.LENGTH_SHORT).show();
            Log.d("files",getActivity().getFilesDir().toString()+"/videos/");
            if (type.equals("tutorial")){
                type = "a";
                downloadFiles();

            }else if (type.equals("a")){
                type="b";
                downloadFiles();


            }else if (type.equals("b")){
                type="tutorial";
                currentPosition++;
                downloadFiles();

            }
            //currentPosition++;
           /* if(currentPosition<exercises.size()){
                getExcercise(exercises.get(currentPosition).getExercise().get_id());
            }*/
        }
        else{
            if (type.equals("tutorial")){
                getExcercise(exercises.get(currentPosition).getExercise().get_id(),type);

               // type = "a";
            }else if (type.equals("a")){
                if(exercises.get(currentPosition).getExercise().isVideoA()){
                    getExcercise(exercises.get(currentPosition).getExercise().get_id(),type);
                }
               // type="b";

            }else if (type.equals("b")){
                if(exercises.get(currentPosition).getExercise().isVideoB()){
                    getExcercise(exercises.get(currentPosition).getExercise().get_id(),type);
                }
               // type="tutorial";


            }
           // getExcercise(exercises.get(currentPosition).getExercise().get_id(),type);
        }
    }

    private void checkFiles(){
        Boolean comp = true;
        for(Exercise e:exercises) {
            Boolean exComp = true;
            //get all exercise names
            File file ,filea ,fileb;
            file = new File(getActivity().getFilesDir().toString()+"/videos/"+exercises.get(currentPosition).getExercise().get_id()+".mp4");
            filea = new File(getActivity().getFilesDir().toString()+"/videos/"+exercises.get(currentPosition).getExercise().get_id()+"_a.mp4");
            fileb = new File(getActivity().getFilesDir().toString()+"/videos/"+exercises.get(currentPosition).getExercise().get_id()+"_b.mp4");

            //check if files exist and put tick on those present
            if(!file.exists()){
                comp = false;
                exComp = false;
            } else if(e.getExercise().isVideoA() && !filea.exists()){
                comp = false;
                exComp = false;
            } else if(e.getExercise().isVideoB() && !fileb.exists()){
                comp = false;
                exComp = false;
            }

            if(exComp){
                //show tick on exercise
                e.getExercise().isDownloaded =true;
                adapter.notifyItemChanged(exercises.indexOf(e));
            }
        }
        if(comp){
            startWorkout.setText("START WORKOUT");
            startWorkout.setOnClickListener(startClickListener);
        }


    }

    private View.OnClickListener startClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Gson gson = new GsonBuilder().create();
            ArrayList<String> videoPlayerItemList = new ArrayList<>();
            for(int i =0 ; i<exercises.size();i++){
                videoPlayerItemList.add(gson.toJson(new VideoPlayerItem(exercises.get(i))));
            }
            Intent intent = new Intent(getContext(), VideoPlayerActivity.class);
            intent.putExtra("videoItemList",videoPlayerItemList);
            Bundle bundle=new Bundle();
            bundle.putBoolean("FREEWORKOUT",false);
            intent.putExtras(bundle);
            c = Calendar.getInstance();
            df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            String fomattedTime=df.format(c.getTime());
            Toast.makeText(ctx, fomattedTime, Toast.LENGTH_LONG).show();
            sharedPreferences1.edit().putString("START_TIME",fomattedTime).apply();
            long time= System.currentTimeMillis();
            sharedPreferences1.edit().putFloat("START_TIME_MILLIS",time).apply();
            sharedPreferences1.edit().putInt("CaloriesBurnt",0).apply();
            startActivity(intent);
        }
    };

    public void getVideo(String data) {
        noOfCurrentVideUser++;
        boolean firtVideo =true;
        String url = data;
        Uri downloadUri = Uri.parse(url);
        Uri destinationUri =  null;
        if (type.equals("tutorial")){
            destinationUri = Uri.parse(getActivity().getFilesDir().toString()+"/videos/"+exercises.get(currentPosition).getExercise().get_id()+".mp4");

        }else if (type.equals("a")){
            destinationUri = Uri.parse(getActivity().getFilesDir().toString()+"/videos/"+exercises.get(currentPosition).getExercise().get_id()+"_a.mp4");

        }else if (type.equals("b")){
            destinationUri = Uri.parse(getActivity().getFilesDir().toString()+"/videos/"+exercises.get(currentPosition).getExercise().get_id()+"_b.mp4");

        }
        DownloadRequest downloadRequest = new DownloadRequest(downloadUri)
                .setRetryPolicy(new DefaultRetryPolicy())
                .setDestinationURI(destinationUri).setPriority(DownloadRequest.Priority.HIGH)
                .setDownloadContext(getActivity().getApplicationContext())//Optional
                .setDownloadListener(new DownloadStatusListener() {
                    @Override
                    public void onDownloadComplete(int id) {

                        //currentPosition++;
                        Toast.makeText(getActivity().getApplicationContext(),"completed download"+(currentPosition+1)+"type"+type,Toast.LENGTH_SHORT).show();
                        allVideoDownloaded = allVideoDownloaded && true;
                        if (type.equals("tutorial")){
                            type="a";
                            downloadFiles();

                        }else if (type.equals("a")){
                            type="b";
                            downloadFiles();

                        }else  if (type.equals("b")){
                            type="tutorial";
                            downloadFiles();
                            currentPosition++;
                            exercises.get(currentPosition-1).getExercise().isDownloaded =  true;
                            adapter.notifyItemChanged(currentPosition-1);

                        }

                    }

                    @Override
                    public void onDownloadFailed(int id, int errorCode, String errorMessage) {
                        Toast.makeText(getActivity().getApplicationContext(),"failed error in logs TAG error77 ",Toast.LENGTH_SHORT).show();
                        Log.d("error77",errorMessage+"\n"+"of number"+((int)currentPosition+1)+"\nof id: "+exercises.get(currentPosition).getExercise().get_id());
                        currentPosition++;
                        allVideoDownloaded = false;

                        downloadFiles();

                    }
                    @Override
                    public void onProgress(int id, long totalBytes, long downlaodedBytes, int progress) {
                        double p = (double)downlaodedBytes/totalBytes*100;
                        progressBar.setProgress((int)p);
                        progressTV.setText(String.format("%.2f", (float)p));
                        numberOfCurrentVideo.setText(String.valueOf(noOfCurrentVideUser)+"/"+totalVideos);
                        exercises.get(currentPosition).getExercise().isDownloading = true;
                        exercises.get(currentPosition).getExercise().progess = (int)p;
                        adapter.notifyItemChanged(currentPosition);

                    }
                });
        int downloadId = downloadManager.add(downloadRequest);


    }

    @Override
    public void onClick(int position)
    {

        if(exercises.get(position).getExercise().isDownloaded)
        {
            Gson gson = new GsonBuilder().create();
            ArrayList<String> videoPlayerItemList = new ArrayList<>();
            videoPlayerItemList.add(gson.toJson(new VideoPlayerItem(exercises.get(position))));
            Intent intent = new Intent(getContext(), VideoPlayerActivity.class);
            intent.putExtra("videoItemList", videoPlayerItemList);
            Bundle bundle=new Bundle();
            bundle.putBoolean("FREEWORKOUT",false);
            intent.putExtras(bundle);
            c = Calendar.getInstance();
            df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            String fomattedTime=df.format(c.getTime());
            Toast.makeText(ctx, fomattedTime, Toast.LENGTH_LONG).show();
            sharedPreferences1.edit().putString("START_TIME",fomattedTime).apply();
            long time= System.currentTimeMillis();
            sharedPreferences1.edit().putFloat("START_TIME_MILLIS",time).apply();
            sharedPreferences1.edit().putInt("CaloriesBurnt",0).apply();
            sharedPreferences1.edit().putInt("CaloriesBurnt",0).apply();
            startActivity(intent);
        }
        else
        {
            Toast.makeText(ctx, "Download the Workout", Toast.LENGTH_SHORT).show();
        }
    }
}
