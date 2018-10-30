package tech.iosd.benefit.VideoPlayer;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

import tech.iosd.benefit.R;

public class VideoFormView extends FrameLayout
{
    TextView questionForm,formName;
    Button submitForm;
    NumberPicker numberPicker;
    private Context mContext;
    private boolean mFromXml;
    private View mRoot;
    private ViewGroup mAnchor;
    private boolean mShowing;
    public VideoFormView(@NonNull Context context)
    {
        super(context);
        mContext = context;
    }

    public VideoFormView(@NonNull Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);
        mRoot = null;
        mContext = context;
        mFromXml = true;
    }

    @Override
    public void onFinishInflate()
    {
        super.onFinishInflate();
        if (mRoot != null)
            initFormView(mRoot);
    }
    private int noOfRep=0;
    private String vidName="";
    private int noOfSets=0;
    boolean displaySets=false;
    boolean isWeight=false;

    public void setAnchorView(ViewGroup view, int NoOfSets,String namek,int noOfReps,boolean displaySet,boolean isWeight) {
        mAnchor = view;
        noOfSets = NoOfSets;
        vidName = namek;
        noOfRep=noOfReps;
        displaySets=displaySet;
        this.isWeight=isWeight;
        LayoutParams frameParams = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );

        removeAllViews();
        View v = makeFormView();
        addView(v, frameParams);

    }

    protected View makeFormView() {
        LayoutInflater inflate = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mRoot = inflate.inflate(R.layout.feed_back, null);

        initFormView(mRoot);

        return mRoot;
    }

    public void hide() {
        if (mAnchor == null) {
            return;
        }

        try {
            mAnchor.removeView(this);
            //   onScreenFormShow();
        } catch (IllegalArgumentException ex) {
            Log.w("Form View", "already removed");
        }
        mShowing = false;
    }

    private void initFormView(View v)
    {
        questionForm=v.findViewById(R.id.form_question);
        formName=v.findViewById(R.id.form_name);
        numberPicker=v.findViewById(R.id.numberPicker);
        submitForm=v.findViewById(R.id.submit_form);
        formName.setText(vidName);
        if(noOfSets>1 && displaySets)
        {
            questionForm.setText("How many Sets were you able to do");
            numberPicker.setMaxValue(noOfSets);
            numberPicker.setMinValue(0);
            numberPicker.setEnabled(true);
        }
        else if(isWeight)
        {
            formName.setText(vidName);
            questionForm.setText("How much Weight (in kg) were you able to pick");
            numberPicker.setMaxValue(120);
            numberPicker.setMinValue(2);
            numberPicker.setValue(15);
            numberPicker.setEnabled(true);
        }
        else if(noOfRep>0)
        {
            formName.setText(vidName);
            questionForm.setText("How many Reps were you able to do");
            numberPicker.setMaxValue(noOfRep);
            numberPicker.setMinValue(0);
            numberPicker.setEnabled(true);
        }
    }
    public void show()
    {
        if (!mShowing&&mAnchor != null)
        {
            LayoutParams tlp = new LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    Gravity.CENTER_HORIZONTAL
            );
            mAnchor.addView(this, tlp);
            mShowing = true;
        }
    }
}
