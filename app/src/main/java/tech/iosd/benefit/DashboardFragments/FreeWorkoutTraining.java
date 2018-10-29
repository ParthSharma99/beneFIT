package tech.iosd.benefit.DashboardFragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.thin.downloadmanager.DefaultRetryPolicy;
import com.thin.downloadmanager.DownloadRequest;
import com.thin.downloadmanager.DownloadStatusListener;
import com.thin.downloadmanager.DownloadStatusListenerV1;
import com.thin.downloadmanager.RetryError;
import com.thin.downloadmanager.RetryPolicy;
import com.thin.downloadmanager.ThinDownloadManager;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import rx.android.schedulers.AndroidSchedulers;
import retrofit2.adapter.rxjava.HttpException;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import tech.iosd.benefit.Adapters.DashboardWorkoutAdapter;
import tech.iosd.benefit.Model.DBDowloadList;
import tech.iosd.benefit.Model.DatabaseHandler;
import tech.iosd.benefit.Model.Exercise;
import tech.iosd.benefit.Model.ResponseForGetExcerciseVideoUrl;
import tech.iosd.benefit.Model.ResponseWorkoutFree;
import tech.iosd.benefit.Model.VideoPlayerItem;
import tech.iosd.benefit.Network.NetworkUtil;
import tech.iosd.benefit.R;
import tech.iosd.benefit.VideoPlayer.VideoPlayerActivity;

import static android.content.Context.MODE_PRIVATE;

public class FreeWorkoutTraining extends Fragment implements DashboardWorkoutAdapter.onItemClickListener
{
    Context ctx;
    FragmentManager fm;
    SharedPreferences sharedPreferences1;
    private ProgressDialog progressDialog;
    private CompositeSubscription compositeSubscription, mcompositeSubscription;
    private DatabaseHandler db;
    private RecyclerView recyclerView;
    private DashboardWorkoutAdapter adapter;
    private ArrayList<Exercise> exercises = new ArrayList<>();

    private ArrayList<Boolean>  secondPresent = new ArrayList<>();
    Calendar c = Calendar.getInstance();
    SimpleDateFormat df;
    private String type;
    ProgressBar progressBar;
    private Button startWorkout;
    private ThinDownloadManager downloadManager;
    private int currentPosition;
    TextView description_free_workouts;
    private  AlertDialog.Builder mBuilder;
    private AlertDialog downloadDialog;
    private View mView;
    private TextView progressTV;
    private TextView numberOfCurrentVideo;
    private int noOfDiffId =0;
    private int noOfCurrentVideUser=0;
    boolean allVideoDownloaded = true;
    private int position;
    public ImageView i1;
    public TextView tcal, texc, tmin;
    int time =0;
    float calory = 0;
    public  ArrayList<Integer> photo = new ArrayList<>();


    DBDowloadList dbDowloadList;

    private boolean isDownloading;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        currentPosition = 0;
        isDownloading = false;
        if (args != null) {
            position = getArguments().getInt("POSITION");
        } else {
            Toast.makeText(getActivity(), "arguments is null " , Toast.LENGTH_LONG).show();
        }

        photo.add(R.drawable.abs);
        photo.add(R.drawable.fw1);
        photo.add(R.drawable.iron);
        photo.add(R.drawable.legs);
        photo.add(R.drawable.cardio);
        photo.add(R.drawable.fw1);
        photo.add(R.drawable.funcfit);
        photo.add(R.drawable.cardio);
        photo.add(R.drawable.iron);
        photo.add(R.drawable.funcfit);

    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState)
    {

        View rootView = inflater.inflate(R.layout.free_workout_training, container, false);
        ctx = rootView.getContext();
        fm = getFragmentManager();
        progressDialog =  new ProgressDialog(getContext());
        progressDialog.setCancelable(false);
        progressDialog.setMessage("working..");
        sharedPreferences1 = getContext().getSharedPreferences("SAVE_EXERCISE", MODE_PRIVATE);

        type = "tutorial";

        compositeSubscription = new CompositeSubscription();
        mcompositeSubscription =  new CompositeSubscription();

        downloadManager =  new ThinDownloadManager();

        db = new DatabaseHandler(getContext());
        Log.d("token1",db.getUserToken());
        recyclerView =  rootView.findViewById(R.id.free_workout_list_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        adapter = new DashboardWorkoutAdapter(exercises,getActivity(),this);

        mBuilder = new AlertDialog.Builder(getActivity());
        mView = getActivity().getLayoutInflater().inflate(R.layout.dialog_download, null);
        exercises = new ArrayList<>();
        progressTV =  mView.findViewById(R.id.percentage_tv);
        progressBar = mView.findViewById(R.id.main_progressbar);
        numberOfCurrentVideo = mView.findViewById(R.id.currentfileDownload);
        mBuilder.setView(mView);
        downloadDialog = mBuilder.create();
        description_free_workouts=rootView.findViewById(R.id.free_workout_description);
        startWorkout = rootView.findViewById(R.id.dashboard_free_workouts_start_workout);
        tcal = rootView.findViewById(R.id.tvcalory);
        texc = rootView.findViewById(R.id.tvexc);
        tmin = rootView.findViewById(R.id.tvmin);
        i1 = rootView.findViewById(R.id.ivPhoto);
        startWorkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                downloadDialog.show();
                sharedPreferences1.edit().putInt("CaloriesBurnt",0).apply();
                //thindownloadmanager is multi-threaded..
                // lets say you have 2 same download requests it just might
                // happen that we download from same url at the same time
                // which would lead to creation of a file which
                // is corrupt, so we need to check if its downloading or not
                // thindownloadmanager has no checks to tell how many requests
                // are pending.. so we need to implement an 'isDownloading' boolean
                // to check the same
                if(!isDownloading) {//so that we dont put 2 requests at the same time..
                    isDownloading = true;
                    downloadFiles();
                }
            }
        });
        getWorkoutData();
        dbDowloadList = new DBDowloadList(getActivity());
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
    private void getWorkoutData(){
        if(!progressDialog.isShowing()){
            progressDialog.show();
        }

        Log.d("token",db.getUserToken());
        compositeSubscription.add(NetworkUtil.getRetrofit(db.getUserToken()).getWorkoutFree(db.getUserToken())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponseGetWorkoutFree,this::handleErrorGetWorkoutFree));
    }
    int videoCount;
    private void handleResponseGetWorkoutFree(ResponseWorkoutFree responseWorkoutFree)
    {
        progressDialog.hide();
        if (!responseWorkoutFree.isSuccess())
        {
            adapter.notifyDataSetChanged();
            return;
            //Download completes here
        }
        videoCount = responseWorkoutFree.getData().get(position).getVideoCount();
        description_free_workouts.setText(responseWorkoutFree.getData().get(position).getDescription());
        sharedPreferences1.edit().putString("WORKOUT_ID",responseWorkoutFree.getData().get(position).get_id()).apply();
        Toast.makeText(ctx,responseWorkoutFree.getData().get(position).get_id()+"" , Toast.LENGTH_SHORT).show();
        Log.d("error77"," " +responseWorkoutFree.getData().get(position).getExercises().size());
        exercises = responseWorkoutFree.getData().get(position).getExercises();
        for (int i =0 ; i<exercises.size();i++){

        }
        adapter.setExercises(exercises);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        noOfDiffId = getNumberOfDifferntId();

        time = calculateTime(exercises.get(position).getReps(), exercises.get(position).getTimeTaken(),
                exercises.get(position).getRest(), exercises.get(position).getSets());


        calory = calculateCalory(exercises.get(position).getReps(), exercises.get(position).getExercise().getTimeTaken(),
                exercises.get(position).getExercise().getMets(), exercises.get(position).getSets());

        tcal.setText(String.valueOf((int)calory));
        tmin.setText(String.valueOf(time));
        texc.setText(String.valueOf(exercises.size()));
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

    private void handleErrorGetWorkoutFree(Throwable error) {
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

    private void handleResponseSendMealLog(ResponseForGetExcerciseVideoUrl reponse)
    {
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
            isDownloading = false;
            return;
        }
        if (currentPosition>=exercises.size() && exercises.size()!=0){
            isDownloading = false;
            //  downloadDialog.hide();
            if(allVideoDownloaded){
                Toast.makeText(ctx, "First", Toast.LENGTH_SHORT).show();
                startWorkout.setText("START WORKOUT");
                startWorkout.setOnClickListener(startClickListener);
                downloadDialog.dismiss();

            }else {
                isDownloading = false;
                downloadDialog.dismiss();
                showSnackBarMessage("All files not downloaded.\nPlease try again.");
            }
            return;
        }

//        File file = null ;
//        if (type.equals("a")){
//            if (exercises.get(currentPosition).getExercise().isVideoA()){
//                file = new File(getActivity().getFilesDir().toString()+"/videos/"+exercises.get(currentPosition).getExercise().get_id()+"_a.mp4");
//            }else {
//                type = "b";
//                downloadFiles();
//                return;
//            }
//
//        }else if (type.equals("b")){
//            if (exercises.get(currentPosition).getExercise().isVideoB()){
//                file = new File(getActivity().getFilesDir().toString()+"/videos/"+exercises.get(currentPosition).getExercise().get_id()+"_b.mp4");
//            }else {
//                type = "tutorial";
//                currentPosition++;
//                downloadFiles();
//                return;
//            }
//
//        }else{
//            file = new File(getActivity().getFilesDir().toString()+"/videos/"+exercises.get(currentPosition).getExercise().get_id()+".mp4");
//
//        }

        String name = exercises.get(currentPosition).getExercise().get_id();
        if(type.equals("a") || type.equals("b"))
            name = exercises.get(currentPosition).getExercise().get_id()+"_"+type;

        if(dbDowloadList.isInDataBase(name)){
            adapter.notifyDataSetChanged();
            Toast.makeText(getContext(),"Present in db "+type+(currentPosition+1),Toast.LENGTH_SHORT).show();
            Log.d("files",getActivity().getFilesDir().toString()+"/videos/");
            if (type.equals("tutorial")){
                type = "a";
                downloadFiles();

            }else if (type.equals("a")){
                type="b";
                downloadFiles();


            }else if (type.equals("b"))
            {
                exercises.get(currentPosition).getExercise().isDownloaded=true;
                adapter.notifyItemChanged(currentPosition);
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
            }else if (type.equals("a")){
                if(exercises.get(currentPosition).getExercise().isVideoA()){
                    getExcercise(exercises.get(currentPosition).getExercise().get_id(),type);
                }
                else{
                    type="b";
                    downloadFiles();
                }

            }else if (type.equals("b")){
                if(exercises.get(currentPosition).getExercise().isVideoB()){
                    getExcercise(exercises.get(currentPosition).getExercise().get_id(),type);
                }
                else {
                    type ="tutorial";
                    currentPosition++;
                    downloadFiles();
                }
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
            if(!dbDowloadList.isInDataBase(e.getExercise().get_id())){
                comp = false;
                exComp = false;
            }
            if(e.getExercise().isVideoA() && (!dbDowloadList.isInDataBase(e.getExercise().get_id()+"_a"))){
                comp = false;
                exComp = false;
            }
            if(e.getExercise().isVideoB() && (!dbDowloadList.isInDataBase(e.getExercise().get_id()+"_b"))){
                comp = false;
                exComp = false;
            }

            if(exComp){
                //show tick on exercise
                e.getExercise().isDownloaded =true;
                adapter.notifyItemChanged(exercises.indexOf(e));
            }
        }
        if(comp && exercises.size()!=0){
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
            Bundle bundle=new Bundle();
            bundle.putBoolean("FREEWORKOUT",true);
            intent.putExtras(bundle);
            intent.putExtra("videoItemList",videoPlayerItemList);
            c = Calendar.getInstance();
            df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            String fomattedTime=df.format(c.getTime());
            Toast.makeText(ctx,fomattedTime, Toast.LENGTH_LONG).show();
            sharedPreferences1.edit().putString("START_TIME",fomattedTime).apply();
            long time= System.currentTimeMillis();
            sharedPreferences1.edit().putFloat("START_TIME_MILLIS",time).apply();
            sharedPreferences1.edit().putInt("CaloriesBurnt",0).apply();
            startActivity(intent);
        }
    };

    public void getVideo(String data) {
        noOfCurrentVideUser++;

        Uri downloadUri = Uri.parse(data);
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
                .setDownloadContext(getActivity().getApplicationContext())
                .setDownloadResumable(false)
                .setDeleteDestinationFileOnFailure(true)
                .setStatusListener(new DownloadStatusListenerV1() {
                    @Override
                    public void onDownloadComplete(DownloadRequest downloadRequest) {
                        //currentPosition++;

                        //add file to database depending on type(done in if-else statements)

                        Toast.makeText(getActivity().getApplicationContext(),"completed download"+(currentPosition+1)+"type"+type,Toast.LENGTH_SHORT).show();
                        allVideoDownloaded = allVideoDownloaded && true;
                        if (type.equals("tutorial")){
                            dbDowloadList.insert(exercises.get(currentPosition).getExercise().get_id());
                            type="a";
                            downloadFiles();

                        }else if (type.equals("a")){
                            dbDowloadList.insert(exercises.get(currentPosition).getExercise().get_id()+"_a");
                            type="b";
                            downloadFiles();

                        }else  if (type.equals("b")){
                            dbDowloadList.insert(exercises.get(currentPosition).getExercise().get_id()+"_b");
                            type="tutorial";
                            exercises.get(currentPosition).getExercise().isDownloaded =  true;
                            adapter.notifyItemChanged(currentPosition);
                            currentPosition++;
                            downloadFiles();
                        }
                    }

                    @Override
                    public void onDownloadFailed(DownloadRequest downloadRequest, int errorCode, String errorMessage) {
                        if(getActivity()!=null)
                            Toast.makeText(getActivity().getApplicationContext(),"failed error"+errorMessage,Toast.LENGTH_SHORT).show();
                        Log.d("error77",errorMessage+"\n"+"of number"+((int)currentPosition)+"\nof id: "+exercises.get(currentPosition).getExercise().get_id());
                        currentPosition++;

                        allVideoDownloaded = false;

                        downloadFiles();
                    }

                    @Override
                    public void onProgress(DownloadRequest downloadRequest, long totalBytes, long downloadedBytes, int progress) {
                        double p = (double)downloadedBytes/totalBytes*100;
                        //Toast.makeText(getActivity().getApplicationContext(),"total"+ totalBytes+"dnld "+downlaodedBytes+"progress "+p,Toast.LENGTH_SHORT).show();
                        progressBar.setProgress((int)p);
                        progressTV.setText(String.format("%.2f", (float)p));
                        numberOfCurrentVideo.setText(String.valueOf(noOfCurrentVideUser)+"/"+videoCount);
                        exercises.get(currentPosition).getExercise().isDownloading = true;
                        exercises.get(currentPosition).getExercise().progess = (int)p;
                    }
                });
//                .setDownloadListener(new DownloadStatusListener() {
//                    @Override
//                    public void onDownloadComplete(int id) {
//
//                        //currentPosition++;
//
//                        //add file to database depending on type(done in if-else statements)
//
//                        Toast.makeText(getActivity().getApplicationContext(),"completed download"+(currentPosition+1)+"type"+type,Toast.LENGTH_SHORT).show();
//                        allVideoDownloaded = allVideoDownloaded && true;
//                        if (type.equals("tutorial")){
//                            dbDowloadList.insert(exercises.get(currentPosition).getExercise().get_id());
//                            type="a";
//                            downloadFiles();
//
//                        }else if (type.equals("a")){
//                            dbDowloadList.insert(exercises.get(currentPosition).getExercise().get_id()+"_a");
//                            type="b";
//                            downloadFiles();
//
//                        }else  if (type.equals("b")){
//                            dbDowloadList.insert(exercises.get(currentPosition).getExercise().get_id()+"_b");
//                            type="tutorial";
//                            exercises.get(currentPosition).getExercise().isDownloaded =  true;
//                            adapter.notifyItemChanged(currentPosition);
//                            currentPosition++;
//                            downloadFiles();
//                        }
//                    }
//
//                    @Override
//                    public void onDownloadFailed(int id, int errorCode, String errorMessage) {
//                        Toast.makeText(getActivity().getApplicationContext(),"failed error"+errorMessage,Toast.LENGTH_SHORT).show();
//                        Log.d("error77",errorMessage+"\n"+"of number"+((int)currentPosition)+"\nof id: "+exercises.get(currentPosition).getExercise().get_id());
//                        currentPosition++;
//
//                        allVideoDownloaded = false;
//
//                        downloadFiles();
//
//                    }
//
//                    @Override
//                    public void onProgress(int id, long totalBytes, long downlaodedBytes, int progress) {
//                        double p = (double)downlaodedBytes/totalBytes*100;
//                        //Toast.makeText(getActivity().getApplicationContext(),"total"+ totalBytes+"dnld "+downlaodedBytes+"progress "+p,Toast.LENGTH_SHORT).show();
//                        progressBar.setProgress((int)p);
//                        progressTV.setText(String.format("%.2f", (float)p));
//                        numberOfCurrentVideo.setText(String.valueOf(noOfCurrentVideUser)+"/"+videoCount);
//                        exercises.get(currentPosition).getExercise().isDownloading = true;
//                        exercises.get(currentPosition).getExercise().progess = (int)p;
//
//                    }
//
//                });
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
            bundle.putBoolean("FREEWORKOUT",true);
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
        else
        {
            Toast.makeText(ctx, "Download the Workout", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onStop() {
        downloadManager.cancelAll();
        super.onStop();
    }
}
