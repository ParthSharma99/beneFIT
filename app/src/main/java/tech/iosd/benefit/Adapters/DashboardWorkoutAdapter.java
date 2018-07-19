package tech.iosd.benefit.Adapters;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.thin.downloadmanager.ThinDownloadManager;

import java.util.ArrayList;

import rx.subscriptions.CompositeSubscription;
import tech.iosd.benefit.Model.DatabaseHandler;
import tech.iosd.benefit.Model.Exercise;
import tech.iosd.benefit.R;

/**
 * Created by SAM33R on 28-06-2018.
 */

public class DashboardWorkoutAdapter extends RecyclerView.Adapter<DashboardWorkoutAdapter.ViewHolder> {

    ArrayList<Exercise> exercises;
    Activity activity;
    CompositeSubscription compositeSubscription;
    DatabaseHandler db;
    int currentPosition =0;
    private Object video;
    private ThinDownloadManager downloadManager;

    public DashboardWorkoutAdapter(ArrayList<Exercise> exercises, Activity activity) {
        this.exercises = exercises;
        this.activity = activity;
        compositeSubscription =  new CompositeSubscription();
        db = new DatabaseHandler(activity.getApplicationContext());
        downloadManager =  new ThinDownloadManager();
    }

    public ArrayList<Exercise> getExercises() {
        return exercises;
    }

    public void setExercises(ArrayList<Exercise> exercises) {
        this.exercises = exercises;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = activity.getLayoutInflater();
        View view =inflater.inflate(R.layout.dashboard_my_workouts_list_item, parent, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);
        DashboardWorkoutAdapter.ViewHolder viewHolder = new DashboardWorkoutAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Exercise e = exercises.get(position);
        if(e!=null) {
            if (exercises.get(position).getExercise() != null)
                holder.name.setText(exercises.get(position).getExercise().getName());

            holder.details.setText(e.getReps() + " reps");
            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(activity.getApplicationContext(), "starting download", Toast.LENGTH_SHORT).show();
                /*File file = new File(activity.getCacheDir().toString());
                if(file.exists()){

                }
                else{

                }*/
                    //getExcercise(exercises.get(position).getExercise().get_id());
                }
            });
            if(e.getExercise().isDownloaded){
                holder.progress.setVisibility(View.GONE);
                holder.tick.setVisibility(View.VISIBLE);
            }
            if (e.getExercise().isDownloading){
                holder.tick.setVisibility(View.GONE);
                holder.progress.setVisibility(View.VISIBLE);
                holder.progress.setProgress(e.getExercise().progess);
            }
        }


    }

    @Override
    public int getItemCount() {
        return exercises.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView name, details, note;
        public View view;
        public ImageButton tick;
        public ProgressBar progress;

        public ViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.dashboard_my_workouts_list_item_name);
            details = (TextView) itemView.findViewById(R.id.dashboard_my_workouts_list_item_sets_reps);
            view =  itemView.findViewById(R.id.dashboard_my_workouts_list_item_full_view);
            note =  (TextView) itemView.findViewById(R.id.dashboard_my_workouts_list_item_note);
            progress = itemView.findViewById(R.id.progress);
            tick = (ImageButton) itemView.findViewById(R.id.tick);
        }
    }

}
