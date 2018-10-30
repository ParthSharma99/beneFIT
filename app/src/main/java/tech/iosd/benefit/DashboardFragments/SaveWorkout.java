package tech.iosd.benefit.DashboardFragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;

import android.support.v4.app.FragmentManager;
import android.view.ViewGroup;

import java.util.ArrayList;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import tech.iosd.benefit.Adapters.DashboardWorkoutAdapter;
import tech.iosd.benefit.Adapters.WorkoutSaveActivityAdapter;
import tech.iosd.benefit.Model.Exercise;
import tech.iosd.benefit.R;

public class SaveWorkout extends Fragment
{
    private ArrayList<String> exercises = new ArrayList<>();
    private WorkoutSaveActivityAdapter adapter;
    private RecyclerView recyclerView;
    Context ctx;
    FragmentManager fm;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
           View rootView = inflater.inflate(R.layout.workout_save_activity, container, false);
//        ctx = rootView.getContext();
//        Bundle bundle = getArguments();
//        exercises= (ArrayList<String>) bundle.getSerializable("VIDEO_ITEM");
//        recyclerView =  rootView.findViewById(R.id.save_workouts_recycler_view);
//        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
//        adapter = new WorkoutSaveActivityAdapter(exercises,getActivity());
//        recyclerView.setAdapter(adapter);
        return rootView;
    }
}
