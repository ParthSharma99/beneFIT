package tech.iosd.benefit.DashboardFragments;

import android.app.ProgressDialog;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.ArrayList;
import rx.android.schedulers.AndroidSchedulers;
import retrofit2.adapter.rxjava.HttpException;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import tech.iosd.benefit.Adapters.DashboardWorkoutAdapter;
import tech.iosd.benefit.Adapters.FreeWorkoutAdapter;
import tech.iosd.benefit.Model.DatabaseHandler;
import android.support.v7.widget.LinearLayoutManager;
import tech.iosd.benefit.Model.ResponseForWorkoutForDate;
import tech.iosd.benefit.Model.ResponseWorkoutFree;
import tech.iosd.benefit.Network.NetworkUtil;
import tech.iosd.benefit.R;

public class Workout extends Fragment implements FreeWorkoutAdapter.onItemClickListener
{
    public boolean isMyWorkoutLocked = false;
    Context ctx;
    FragmentManager fm;
    TextView myWorkoutTxt;
    ImageView myWorkoutLock;
    ImageView myWorkoutProceed;
    CardView myWorkoutCard;
    CardView freeWorkoutCard;
    ImageView highIntensityTraining;
    ImageView functionallyFit;
    ImageView legedTube;
    ImageView cardioCrunch;
    ResponseWorkoutFree freeWorkouts ;
    private ProgressDialog progressDialog;
    private CompositeSubscription compositeSubscription;
    private DatabaseHandler db;
    private RecyclerView recyclerView;
    private FreeWorkoutAdapter adapter;
    ArrayList<String> freeWorkoutName=new ArrayList<>();
    ArrayList<String> freeWorkoutImages=new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.dashboard_workout, container, false);
        ctx = rootView.getContext();
        fm = getFragmentManager();
        progressDialog=new ProgressDialog(getContext());
        progressDialog.setMessage("Loading..");
        myWorkoutTxt = rootView.findViewById(R.id.dashboard_workout_my_txt);
        myWorkoutLock = rootView.findViewById(R.id.dashboard_workout_my_lock);
        myWorkoutProceed = rootView.findViewById(R.id.dashboard_workout_my_proceed);
        myWorkoutCard = rootView.findViewById(R.id.dashboard_workout_my_workouts);
        freeWorkoutCard = rootView.findViewById(R.id.dashboard_workout_free_workouts);
        compositeSubscription = new CompositeSubscription();
        db = new DatabaseHandler(getContext());

        recyclerView =  rootView.findViewById(R.id.dashboard_free_workouts_recycler_view);
        adapter = new FreeWorkoutAdapter(getActivity(),freeWorkoutName , freeWorkoutImages,this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
        setMyWorkoutLockCondition(isMyWorkoutLocked);

        if(freeWorkouts == null){
            fetchFreeWorkOuts();
        }

        myWorkoutCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isMyWorkoutLocked)
                    fm.beginTransaction().replace(R.id.dashboard_content, new MyWorkoutLocked()).addToBackStack(null).commit();
                else
                    fm.beginTransaction().replace(R.id.dashboard_content, new MyWorkout()).addToBackStack(null).commit();
            }
        });
        return rootView;
    }

    private void fetchFreeWorkOuts()
    {
        if(!progressDialog.isShowing()){
            progressDialog.show();
        }

        Log.d("token",db.getUserToken());
        compositeSubscription.add(NetworkUtil.getRetrofit(db.getUserToken()).getWorkoutFree(db.getUserToken())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponseGetFreeWorkouts,this::handleErrorGetFreeWorkouts));
    }

    private void handleErrorGetFreeWorkouts(Throwable error)
    {
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

    private void handleResponseGetFreeWorkouts(ResponseWorkoutFree responseWorkoutFree)
    {
        if(responseWorkoutFree.isSuccess())
        {

            freeWorkouts = responseWorkoutFree ;

            freeWorkoutName.clear();
            freeWorkoutImages.clear();
            for(int x=0;x<responseWorkoutFree.getData().size();x++)
            {
                freeWorkoutName.add(responseWorkoutFree.getData().get(x).getName());
                freeWorkoutImages.add(responseWorkoutFree.getData().get(x).getSearch_name());
            }
            adapter.notifyDataSetChanged();
            if(progressDialog.isShowing()){
                progressDialog.dismiss();

                progressDialog.hide();

            }
        }
        else
        {
            if(progressDialog.isShowing()){
                progressDialog.dismiss();

                progressDialog.hide();

            }
        }
    }

    void setMyWorkoutLockCondition(boolean isLocked) {
        if (!isLocked) {
            myWorkoutLock.setVisibility(View.INVISIBLE);
            myWorkoutProceed.setVisibility(View.VISIBLE);
            myWorkoutTxt.setTextColor(Color.BLACK);
        } else {
            myWorkoutLock.setVisibility(View.VISIBLE);
            myWorkoutProceed.setVisibility(View.INVISIBLE);
            myWorkoutTxt.setTextColor(Color.parseColor("9d9d9d"));
            Bundle bundle=new Bundle();
        }
    }

    @Override
    public void onClick(int position)
    {
        FreeWorkoutTraining freeWorkoutTraining=new FreeWorkoutTraining();
        Bundle args = new Bundle();
        args.putInt("POSITION", position);
        freeWorkoutTraining.setArguments(args);
        fm.beginTransaction().replace(R.id.dashboard_content, freeWorkoutTraining).addToBackStack(null).commit();
    }
}
