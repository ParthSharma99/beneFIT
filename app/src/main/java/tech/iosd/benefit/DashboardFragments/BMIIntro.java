package tech.iosd.benefit.DashboardFragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import tech.iosd.benefit.Model.DatabaseHandler;
import tech.iosd.benefit.R;

public class BMIIntro extends Fragment implements View.OnClickListener
{
    Context ctx;
    FragmentManager fm;
    private TextView bmiTextView;
    private TextView bmiMessageTextView;
    private TextView bmiMessageDetailedTextView;

    private double height, weight;
    private SharedPreferences mSharedPreferences;
    private Double bmi;

    private DatabaseHandler db ;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState)
    {


        View rootView = inflater.inflate(R.layout.dashboard_bmi_intro, container, false);
        bmiTextView = rootView.findViewById(R.id.dashboard_bmi_intro_bmi_textview);
        bmiMessageTextView = rootView.findViewById(R.id.dashboard_bmi_intro_bmi_message_textview);
        bmiMessageDetailedTextView = rootView.findViewById(R.id.dashboard_bmi_intro_bmi_message_detailed_textview);
        DatabaseHandler db = new DatabaseHandler(getContext());

        height = db.getUserHeight();
        weight = db.getUserWeight();
        height=height/100;

        bmi = weight /(height*height);
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
