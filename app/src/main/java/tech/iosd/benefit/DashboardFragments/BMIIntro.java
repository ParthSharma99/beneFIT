package tech.iosd.benefit.DashboardFragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import tech.iosd.benefit.R;
import tech.iosd.benefit.Utils.Constants;

public class BMIIntro extends Fragment implements View.OnClickListener
{
    Context ctx;
    FragmentManager fm;
    private TextView bmiTextView;
    private double height, weight;
    private SharedPreferences mSharedPreferences;
    private Double bmi;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.dashboard_bmi_intro, container, false);
        bmiTextView = rootView.findViewById(R.id.dashboard_bmi_intro_bmi_textview);

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        height = mSharedPreferences.getInt(Constants.HEIGHT,0);
        weight = mSharedPreferences.getInt(Constants.WEIGHT,0);
        height=height/100;

        bmi = weight /(height*height);
        bmiTextView.setText(String.format("%.2f", bmi));



        ctx = rootView.getContext();
        fm = getFragmentManager();

        rootView.findViewById(R.id.dashboard_bmi_intro_want_expert).setOnClickListener(this);
        rootView.findViewById(R.id.dashboard_bmi_intro_self).setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.dashboard_bmi_intro_want_expert:
            {
                fm.popBackStack();
                break;
            }
            case R.id.dashboard_bmi_intro_self:
            {
                fm.popBackStack();
                break;
            }
        }
    }
}
