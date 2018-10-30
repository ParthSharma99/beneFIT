package tech.iosd.benefit.Adapters;

import android.app.Activity;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.content.SharedPreferences;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.google.gson.Gson;
import java.util.ArrayList;
import android.support.v7.widget.RecyclerView;
import rx.subscriptions.CompositeSubscription;
import tech.iosd.benefit.Model.DatabaseHandler;
import tech.iosd.benefit.Model.Exercise;
import tech.iosd.benefit.Model.VideoPlayerItem;
import tech.iosd.benefit.R;
import tech.iosd.benefit.SaveWorkoutActivity;

import static android.content.Context.MODE_PRIVATE;

public class WorkoutSaveActivityAdapter extends RecyclerView.Adapter<WorkoutSaveActivityAdapter.ViewHolder>
{
    ArrayList<String> exercises;
    SaveWorkoutActivity activity;
    Gson gson = new Gson();
    public WorkoutSaveActivityAdapter(ArrayList<String> exercises, SaveWorkoutActivity activity)
    {
        this.exercises = exercises;
        this.activity = activity;
    }

    public ArrayList<String> getExercises() {
        return exercises;
    }

    public void setExercises(ArrayList<String> exercises) {
        this.exercises = exercises;
    }
    SharedPreferences sharedPreferences;
    View view;
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = activity.getLayoutInflater();
        view =inflater.inflate(R.layout.save_activity_list_item, parent, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);
        WorkoutSaveActivityAdapter.ViewHolder viewHolder = new WorkoutSaveActivityAdapter.ViewHolder(view);
        return viewHolder;
    }
    VideoPlayerItem videoItem;
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        sharedPreferences = view.getContext().getSharedPreferences("SAVE_EXERCISE", MODE_PRIVATE);
        if(exercises.size()>position)
            videoItem = gson.fromJson(exercises.get(position),VideoPlayerItem.class);

        if(videoItem!=null) {
            if (videoItem.getVideoName() != null)
                holder.name.setText(videoItem.getVideoName());
            int sets=sharedPreferences.getInt("SetNo"+videoItem.getVideoName(),1);
            int reps=sharedPreferences.getInt("RepsNo"+videoItem.getVideoName(),10);
            int weight=sharedPreferences.getInt("Weight"+videoItem.getVideoName(),0);
            if(weight!=0)
                holder.weight.setText("Weight(kg) = "+weight);
            else
                holder.weight.setText("");
            holder.details.setText("Set "+sets+" - "+reps+" Reps");
        }


    }
    @Override
    public int getItemCount() {
        return exercises.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView name, details,weight;
        public View view;
        public ViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.save_workouts_list_item_name);
            details = (TextView) itemView.findViewById(R.id.save_workouts_list_item_sets_reps);
            weight = (TextView) itemView.findViewById(R.id.save_workouts_list_item_sets_weight);
            view =  itemView.findViewById(R.id.save_workouts_list_item_full_view);
        }
    }
}
