package tech.iosd.benefit.DashboardFragments;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import tech.iosd.benefit.R;

public class TrackMyActivity extends Fragment implements View.OnClickListener
{
    Context ctx;
    FragmentManager fm;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.dashboard_track_my_activity, container, false);
        ctx = rootView.getContext();
        fm = getFragmentManager();

        rootView.findViewById(R.id.dashboard_track_my_activity_running).setOnClickListener(this);
        rootView.findViewById(R.id.dashboard_track_my_activity_walking).setOnClickListener(this);
        rootView.findViewById(R.id.dashboard_track_my_activity_ride).setOnClickListener(this);
        rootView.findViewById(R.id.dashboard_track_my_activity_aerobics).setOnClickListener(this);
        rootView.findViewById(R.id.dashboard_track_my_activity_swimming).setOnClickListener(this);
        rootView.findViewById(R.id.dashboard_track_my_activity_skipping).setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onClick(View view)
    {
        TrackMyActivityRun trackMyActivity=new TrackMyActivityRun();
        TrackMyActivityCallisthenics trackMyActivityCallisthenics = new TrackMyActivityCallisthenics();
        Bundle args=new Bundle();
        switch (view.getId())
        {
            case R.id.dashboard_track_my_activity_running:
                args.putString("TRACK_TYPE","RUNNING");
                trackMyActivity.setArguments(args);
                fm.beginTransaction().replace(R.id.dashboard_content, trackMyActivity).addToBackStack(null).commit();
                break;
            case R.id.dashboard_track_my_activity_walking:
                args.putString("TRACK_TYPE","WALKING");
                trackMyActivity.setArguments(args);
                fm.beginTransaction().replace(R.id.dashboard_content, trackMyActivity).addToBackStack(null).commit();
                break;
            case R.id.dashboard_track_my_activity_ride:
                args.putString("TRACK_TYPE","RIDE");
                trackMyActivity.setArguments(args);
                fm.beginTransaction().replace(R.id.dashboard_content, trackMyActivity).addToBackStack(null).commit();
                break;
            case R.id.dashboard_track_my_activity_aerobics:
                args.putString("ACTIVITY_TYPE","AEROBICS");
                trackMyActivityCallisthenics.setArguments(args);
                fm.beginTransaction().replace(R.id.dashboard_content,trackMyActivityCallisthenics ).addToBackStack(null).commit();
                break;
            case R.id.dashboard_track_my_activity_swimming:
                args.putString("ACTIVITY_TYPE","SWIMMING");
                trackMyActivityCallisthenics.setArguments(args);
                fm.beginTransaction().replace(R.id.dashboard_content,trackMyActivityCallisthenics).addToBackStack(null).commit();
                break;
            case R.id.dashboard_track_my_activity_skipping:
                args.putString("ACTIVITY_TYPE","SKIPPING");
                trackMyActivityCallisthenics.setArguments(args);
                fm.beginTransaction().replace(R.id.dashboard_content, trackMyActivityCallisthenics).addToBackStack(null).commit();
                break;
        }
    }
}
