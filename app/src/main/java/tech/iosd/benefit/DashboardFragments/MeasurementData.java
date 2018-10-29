package tech.iosd.benefit.DashboardFragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.aigestudio.wheelpicker.WheelPicker;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import tech.iosd.benefit.Model.DatabaseHandler;
import tech.iosd.benefit.Model.Measurements;
import tech.iosd.benefit.Model.Response;
import tech.iosd.benefit.Model.ResponseForMeasurementsUpdate;
import tech.iosd.benefit.Model.UserForLogin;
import tech.iosd.benefit.Network.NetworkUtil;
import tech.iosd.benefit.R;
import tech.iosd.benefit.Utils.Constants;

public class MeasurementData extends Fragment implements View.OnClickListener {
    public Boolean isHeightFtSelected = true;
    public Boolean isWaistCmSelected = true;
    public Boolean isNeckCmSelected = true;
    public Boolean isHipCmSelected = true;
    public Boolean isKgSelected = true;
    Context ctx;
    FragmentManager fm;
    Button heightFt;
    Button heightCm;
    Button waistIn;
    Button waistCm;
    Button neckIn;
    Button neckCm;
    Button hipIn;
    Button hipCm;
    Button saveBtn;
    Button weightKg;
    Button weightLbs;
    EditText ageField;
    TextView weightField;
    TextView heightField;
    TextView waistField;
    TextView neckField;
    TextView hipField;
    FloatingActionButton btnMale;
    FloatingActionButton btnFemale;
    FloatingActionButton genderSelector;
    int heightPickerPos = 0;
    int waistPickerPos = 0;
    int neckPickerPos = 0;
    int hipPickerPos = 0;
    int weightPickerPos = 0;
    List<String> heightsCM;
    List<String> heightsFT;
    List<String> waistIN;
    List<String> waistCM;
    List<String> neckIN;
    List<String> neckCM;
    List<String> hipIN;
    List<String> hipCM;
    List<String> weightsKG;
    List<String> weightsLBS;

    private String gender;
    private int height;
    private int weight;
    private int waistSize;
    private int neckSize;
    private int hipSize;
    private int age;
    private DatabaseHandler db;
    private CompositeSubscription mSubscriptions;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dashboard_setup_measurement, container, false);
        ctx = rootView.getContext();
        fm = getFragmentManager();

        db = new DatabaseHandler(getContext());

        mSubscriptions = new CompositeSubscription();
        weightsKG = new ArrayList<>();
        weightsLBS = new ArrayList<>();
        for (int i = 20; i <= 200; i++) {
            weightsKG.add(Integer.toString(i));
            double lbs = round(i * 2.20462262185, 1);
            weightsLBS.add(Double.toString(lbs));
        }

        weightKg = rootView.findViewById(R.id.dashboard_measurement_setup_weight_kg);
        weightLbs = rootView.findViewById(R.id.dashboard_measurement_setup_weight_lbs);
        weightField = rootView.findViewById(R.id.dashboard_measurement_setup_weight);

        heightsCM = new ArrayList<>();
        heightsFT = new ArrayList<>();
        for (int i = 120; i <= 220; i++) {
            heightsCM.add(Integer.toString(i));
            double inches = (i / 2.54);
            int feet = (int) inches / 12;
            inches = round(inches - (feet * 12), 1);
            heightsFT.add(feet + "’ " + inches + "”");
        }
        waistCM = new ArrayList<>();
        waistIN = new ArrayList<>();
        for (int i = 50; i <= 100; i++) {
            waistCM.add(Integer.toString(i));
            double inches = (i / 2.54);
            int feet = (int) inches / 12;
            inches = round(inches - (feet * 12), 1);
            waistIN.add(feet + "’ " + inches + "”");
        }
        neckCM = new ArrayList<>();
        neckIN = new ArrayList<>();
        for (int i = 20; i <= 60; i++) {
            neckCM.add(Integer.toString(i));
            double inches = (i / 2.54);
            int feet = (int) inches / 12;
            inches = round(inches - (feet * 12), 1);
            neckIN.add(feet + "’ " + inches + "”");
        }
        hipCM = new ArrayList<>();
        hipIN = new ArrayList<>();
        for (int i = 80; i <= 130; i++) {
            hipCM.add(Integer.toString(i));
            double inches = (i / 2.54);
            int feet = (int) inches / 12;
            inches = round(inches - (feet * 12), 1);
            hipIN.add(feet + "’ " + inches + "”");
        }

        heightFt = rootView.findViewById(R.id.dashboard_measurement_setup_height_ft);
        heightCm = rootView.findViewById(R.id.dashboard_measurement_setup_height_cm);
        waistCm = rootView.findViewById(R.id.dashboard_measurement_setup_waist_cm);
        waistIn = rootView.findViewById(R.id.dashboard_measurement_setup_waist_in);
        neckCm = rootView.findViewById(R.id.dashboard_measurement_setup_neck_cm);
        neckIn = rootView.findViewById(R.id.dashboard_measurement_setup_neck_in);
        hipCm = rootView.findViewById(R.id.dashboard_measurement_setup_hip_cm);
        hipIn = rootView.findViewById(R.id.dashboard_measurement_setup_hip_in);
        saveBtn = rootView.findViewById(R.id.dashboard_measurement_setup_save);
        saveBtn.setAlpha(0.2f);
        saveBtn.setEnabled(false);

        ageField = rootView.findViewById(R.id.dashboard_measurement_setup_age);
        heightField = rootView.findViewById(R.id.dashboard_measurement_setup_height);
        waistField = rootView.findViewById(R.id.dashboard_measurement_setup_waist);
        neckField = rootView.findViewById(R.id.dashboard_measurement_setup_neck);
        hipField = rootView.findViewById(R.id.dashboard_measurement_setup_hip);
        heightFt.setOnClickListener(this);
        heightCm.setOnClickListener(this);
        weightKg.setOnClickListener(this);
        weightLbs.setOnClickListener(this);
        waistCm.setOnClickListener(this);
        waistIn.setOnClickListener(this);
        neckCm.setOnClickListener(this);
        neckIn.setOnClickListener(this);
        hipCm.setOnClickListener(this);
        hipIn.setOnClickListener(this);

        btnMale = rootView.findViewById(R.id.dashboard_measurement_setup_male);
        btnFemale = rootView.findViewById(R.id.dashboard_measurement_setup_female);
        genderSelector = rootView.findViewById(R.id.dashboard_measurement_setup_gender);
        btnMale.setBackgroundTintList(getResources().getColorStateList(R.color.FABIndicatorBGNotSelected));
        btnFemale.setBackgroundTintList(getResources().getColorStateList(R.color.FABIndicatorBGSelected));
        btnMale.setRippleColor(getResources().getColor(R.color.FABIndicatorBGSelected));
        btnFemale.setRippleColor(getResources().getColor(R.color.FABIndicatorBGNotSelected));
        btnMale.setColorFilter(getResources().getColor(R.color.FABIndicatorSelected));
        btnFemale.setColorFilter(getResources().getColor(R.color.FABIndicatorNotSelected));
        btnMale.setOnClickListener(this);
        btnFemale.setOnClickListener(this);
        saveBtn.setOnClickListener(this);
        heightField.setOnClickListener(this);
        waistField.setOnClickListener(this);
        neckField.setOnClickListener(this);
        hipField.setOnClickListener(this);
        weightField.setOnClickListener(this);
        ageField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                checkFields();
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        heightField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                checkFields();
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        waistField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                checkFields();
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        neckField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                checkFields();
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        hipField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                checkFields();
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        weightField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                checkFields();
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        height = db.getUserHeight();
        weight = db.getUserWeight();
        age = db.getUserAge();
        gender = db.getUserGender();
        waistSize= db.getUserWaist();
        hipSize = db.getUserHip();
        neckSize = db.getUserNeck();

        Toast.makeText(getContext(), height + " " + weight, Toast.LENGTH_SHORT).show();
        if (gender.equalsIgnoreCase("male")) {
            btnFemale.setBackgroundTintList(getResources().getColorStateList(R.color.FABIndicatorBGNotSelected));
            btnMale.setBackgroundTintList(getResources().getColorStateList(R.color.FABIndicatorBGSelected));
            btnFemale.setRippleColor(getResources().getColor(R.color.FABIndicatorBGSelected));
            btnMale.setRippleColor(getResources().getColor(R.color.FABIndicatorBGNotSelected));
            btnFemale.setColorFilter(getResources().getColor(R.color.FABIndicatorSelected));
            btnMale.setColorFilter(getResources().getColor(R.color.FABIndicatorNotSelected));
            genderSelector.setImageResource(R.drawable.male_img);
        }

        heightPickerPos = height - 120;
        weightPickerPos = weight - 20;
        waistPickerPos = waistSize - 50;
        neckPickerPos = neckSize - 20;
        hipPickerPos = hipSize - 80;

        isHeightFtSelected = false;
        heightFt.setBackground(getResources().getDrawable(R.drawable.button_style_off));
        heightFt.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        heightCm.setBackground(getResources().getDrawable(R.drawable.button_style_on));
        heightCm.setTextColor(getResources().getColor(R.color.white));

        heightField.setText(heightsCM.get(heightPickerPos));
        weightField.setText(weightsKG.get(weightPickerPos));
        waistField.setText(waistCM.get(waistPickerPos));
        neckField.setText(neckCM.get(neckPickerPos));
        hipField.setText(hipCM.get(hipPickerPos));



        ageField.setText(String.valueOf(age));
//        heightField.setText(String.valueOf(height));
//        weightField.setText(String.valueOf(weight));

        return rootView;
    }

    void checkFields() {
        String height = heightField.getText().toString();
        String weight = weightField.getText().toString();
        String waist = waistField.getText().toString();
        String neck = neckField.getText().toString();
        String hip = hipField.getText().toString();
        String age = ageField.getText().toString();
        if (!age.equals("") && !height.equals("") && !waist.equals("") && !neck.equals("") && !hip.equals("") && !weight.equals("")) {
            saveBtn.setAlpha(1.0f);
            saveBtn.setEnabled(true);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.dashboard_measurement_setup_save: {

                //converting all field to standards units

                isHeightFtSelected = false;
                heightFt.setBackground(getResources().getDrawable(R.drawable.button_style_off));
                heightFt.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                heightCm.setBackground(getResources().getDrawable(R.drawable.button_style_on));
                heightCm.setTextColor(getResources().getColor(R.color.white));
                heightField.setText(heightsCM.get(heightPickerPos));

                isWaistCmSelected = true;
                waistCm.setBackground(getResources().getDrawable(R.drawable.button_style_on));
                waistCm.setTextColor(getResources().getColor(R.color.white));
                waistIn.setBackground(getResources().getDrawable(R.drawable.button_style_off));
                waistIn.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                waistField.setText(waistCM.get(waistPickerPos));

                isNeckCmSelected = true;
                neckCm.setBackground(getResources().getDrawable(R.drawable.button_style_on));
                neckCm.setTextColor(getResources().getColor(R.color.white));
                neckIn.setBackground(getResources().getDrawable(R.drawable.button_style_off));
                neckIn.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                neckField.setText(neckCM.get(neckPickerPos));

                isHipCmSelected = true;
                hipCm.setBackground(getResources().getDrawable(R.drawable.button_style_on));
                hipCm.setTextColor(getResources().getColor(R.color.white));
                hipIn.setBackground(getResources().getDrawable(R.drawable.button_style_off));
                hipIn.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                hipField.setText(hipCM.get(hipPickerPos));

                isKgSelected = true;
                weightKg.setBackground(getResources().getDrawable(R.drawable.button_style_on));
                weightKg.setTextColor(getResources().getColor(R.color.white));
                weightLbs.setBackground(getResources().getDrawable(R.drawable.button_style_off));
                weightLbs.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                weightField.setText(weightsKG.get(weightPickerPos));

                Log.d("AddMeasurement", "onClick: Height" + heightField.getText().toString());
                Log.d("AddMeasurement", "onClick: Weight" + weightField.getText().toString());

                Measurements measurements = new Measurements(
                        Integer.valueOf(ageField.getText().toString()),
                        Integer.valueOf(heightField.getText().toString()),
                        Integer.valueOf(waistField.getText().toString()),
                        Integer.valueOf(neckField.getText().toString()),
                        Integer.valueOf(hipField.getText().toString()),
                        Integer.valueOf(weightField.getText().toString())
                );

                updateUser(measurements, db.getUserToken());

                break;
            }
            case R.id.dashboard_measurement_setup_female: {
                btnMale.setBackgroundTintList(getResources().getColorStateList(R.color.FABIndicatorBGNotSelected));
                btnFemale.setBackgroundTintList(getResources().getColorStateList(R.color.FABIndicatorBGSelected));
                btnMale.setRippleColor(getResources().getColor(R.color.FABIndicatorBGSelected));
                btnFemale.setRippleColor(getResources().getColor(R.color.FABIndicatorBGNotSelected));
                btnMale.setColorFilter(getResources().getColor(R.color.FABIndicatorSelected));
                btnFemale.setColorFilter(getResources().getColor(R.color.FABIndicatorNotSelected));
                genderSelector.setImageResource(R.drawable.female_img);
                break;
            }
            case R.id.dashboard_measurement_setup_male: {
                btnFemale.setBackgroundTintList(getResources().getColorStateList(R.color.FABIndicatorBGNotSelected));
                btnMale.setBackgroundTintList(getResources().getColorStateList(R.color.FABIndicatorBGSelected));
                btnFemale.setRippleColor(getResources().getColor(R.color.FABIndicatorBGSelected));
                btnMale.setRippleColor(getResources().getColor(R.color.FABIndicatorBGNotSelected));
                btnFemale.setColorFilter(getResources().getColor(R.color.FABIndicatorSelected));
                btnMale.setColorFilter(getResources().getColor(R.color.FABIndicatorNotSelected));
                genderSelector.setImageResource(R.drawable.male_img);
                break;
            }
            case R.id.dashboard_measurement_setup_height_cm: {
                isHeightFtSelected = false;
                heightFt.setBackground(getResources().getDrawable(R.drawable.button_style_off));
                heightFt.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                heightCm.setBackground(getResources().getDrawable(R.drawable.button_style_on));
                heightCm.setTextColor(getResources().getColor(R.color.white));
                heightField.setText(heightsCM.get(heightPickerPos));
                break;
            }
            case R.id.dashboard_measurement_setup_height_ft: {
                isHeightFtSelected = true;
                heightFt.setBackground(getResources().getDrawable(R.drawable.button_style_on));
                heightFt.setTextColor(getResources().getColor(R.color.white));
                heightCm.setBackground(getResources().getDrawable(R.drawable.button_style_off));
                heightCm.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                heightField.setText(heightsFT.get(heightPickerPos));
                break;
            }
            case R.id.dashboard_measurement_setup_waist_in: {
                isWaistCmSelected = false;
                waistCm.setBackground(getResources().getDrawable(R.drawable.button_style_off));
                waistCm.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                waistIn.setBackground(getResources().getDrawable(R.drawable.button_style_on));
                waistIn.setTextColor(getResources().getColor(R.color.white));
                waistField.setText(waistIN.get(waistPickerPos));
                break;
            }
            case R.id.dashboard_measurement_setup_waist_cm: {
                isWaistCmSelected = true;
                waistCm.setBackground(getResources().getDrawable(R.drawable.button_style_on));
                waistCm.setTextColor(getResources().getColor(R.color.white));
                waistIn.setBackground(getResources().getDrawable(R.drawable.button_style_off));
                waistIn.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                waistField.setText(waistCM.get(waistPickerPos));
                break;
            }
            case R.id.dashboard_measurement_setup_weight_lbs: {
                isKgSelected = false;
                weightKg.setBackground(getResources().getDrawable(R.drawable.button_style_off));
                weightKg.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                weightLbs.setBackground(getResources().getDrawable(R.drawable.button_style_on));
                weightLbs.setTextColor(getResources().getColor(R.color.white));
                weightField.setText(weightsLBS.get(weightPickerPos));
                break;
            }
            case R.id.dashboard_measurement_setup_weight_kg: {
                isKgSelected = true;
                weightKg.setBackground(getResources().getDrawable(R.drawable.button_style_on));
                weightKg.setTextColor(getResources().getColor(R.color.white));
                weightLbs.setBackground(getResources().getDrawable(R.drawable.button_style_off));
                weightLbs.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                weightField.setText(weightsKG.get(weightPickerPos));
                break;
            }
            case R.id.dashboard_measurement_setup_neck_in: {
                isNeckCmSelected = false;
                neckCm.setBackground(getResources().getDrawable(R.drawable.button_style_off));
                neckCm.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                neckIn.setBackground(getResources().getDrawable(R.drawable.button_style_on));
                neckIn.setTextColor(getResources().getColor(R.color.white));
                neckField.setText(neckIN.get(neckPickerPos));
                break;
            }
            case R.id.dashboard_measurement_setup_neck_cm: {
                isNeckCmSelected = true;
                neckCm.setBackground(getResources().getDrawable(R.drawable.button_style_on));
                neckCm.setTextColor(getResources().getColor(R.color.white));
                neckIn.setBackground(getResources().getDrawable(R.drawable.button_style_off));
                neckIn.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                neckField.setText(neckCM.get(neckPickerPos));
                break;
            }
            case R.id.dashboard_measurement_setup_hip_in: {
                isHipCmSelected = false;
                hipCm.setBackground(getResources().getDrawable(R.drawable.button_style_off));
                hipCm.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                hipIn.setBackground(getResources().getDrawable(R.drawable.button_style_on));
                hipIn.setTextColor(getResources().getColor(R.color.white));
                hipField.setText(hipIN.get(hipPickerPos));
                break;
            }
            case R.id.dashboard_measurement_setup_hip_cm: {
                isHipCmSelected = true;
                hipCm.setBackground(getResources().getDrawable(R.drawable.button_style_on));
                hipCm.setTextColor(getResources().getColor(R.color.white));
                hipIn.setBackground(getResources().getDrawable(R.drawable.button_style_off));
                hipIn.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                hipField.setText(hipCM.get(hipPickerPos));
                break;
            }
            case R.id.dashboard_measurement_setup_height: {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
                View mView = getActivity().getLayoutInflater().inflate(R.layout.dialog_picker_height, null);
                Button dialogDone = mView.findViewById(R.id.dialog_done);
                final WheelPicker wheelPickerHeight = mView.findViewById(R.id.dialog_picker_height);
                mBuilder.setView(mView);
                final AlertDialog dialog = mBuilder.create();
                dialog.show();
                wheelPickerHeight.setData(isHeightFtSelected ? heightsFT : heightsCM);
                wheelPickerHeight.setSelectedItemPosition(heightPickerPos);

                dialogDone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        heightPickerPos = wheelPickerHeight.getCurrentItemPosition();
                        heightField.setText(wheelPickerHeight.getData().get(heightPickerPos).toString());
                    }
                });
                break;
            }
            case R.id.dashboard_measurement_setup_waist: {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
                View mView = getActivity().getLayoutInflater().inflate(R.layout.dialog_picker_waist, null);
                Button dialogDone = mView.findViewById(R.id.dialog_done);
                final WheelPicker wheelPickerWaist = mView.findViewById(R.id.dialog_picker_waist);
                mBuilder.setView(mView);
                final AlertDialog dialog = mBuilder.create();
                dialog.show();
                wheelPickerWaist.setData(isWaistCmSelected ? waistCM : waistIN);
                wheelPickerWaist.setSelectedItemPosition(waistPickerPos);

                dialogDone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        waistPickerPos = wheelPickerWaist.getCurrentItemPosition();
                        waistField.setText(wheelPickerWaist.getData().get(waistPickerPos).toString());
                    }
                });
                break;
            }
            case R.id.dashboard_measurement_setup_neck: {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
                View mView = getActivity().getLayoutInflater().inflate(R.layout.dialog_picker_neck, null);
                Button dialogDone = mView.findViewById(R.id.dialog_done);
                final WheelPicker wheelPickerNeck = mView.findViewById(R.id.dialog_picker_neck);
                mBuilder.setView(mView);
                final AlertDialog dialog = mBuilder.create();
                dialog.show();
                wheelPickerNeck.setData(isNeckCmSelected ? neckCM : neckIN);
                wheelPickerNeck.setSelectedItemPosition(neckPickerPos);

                dialogDone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        neckPickerPos = wheelPickerNeck.getCurrentItemPosition();
                        neckField.setText(wheelPickerNeck.getData().get(neckPickerPos).toString());
                    }
                });
                break;
            }
            case R.id.dashboard_measurement_setup_weight: {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
                View mView = getActivity().getLayoutInflater().inflate(R.layout.dialog_picker_weight, null);
                Button dialogDone = mView.findViewById(R.id.dialog_done);
                final WheelPicker wheelPickerWeight = mView.findViewById(R.id.dialog_picker_weight);
                mBuilder.setView(mView);
                final AlertDialog dialog = mBuilder.create();
                dialog.show();
                wheelPickerWeight.setData(isKgSelected ? weightsKG : weightsLBS);
                wheelPickerWeight.setSelectedItemPosition(weightPickerPos);

                dialogDone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        weightPickerPos = wheelPickerWeight.getCurrentItemPosition();
                        weightField.setText(wheelPickerWeight.getData().get(weightPickerPos).toString());
                    }
                });
                break;
            }
            case R.id.dashboard_measurement_setup_hip: {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
                View mView = getActivity().getLayoutInflater().inflate(R.layout.dialog_picker_hip, null);
                Button dialogDone = mView.findViewById(R.id.dialog_done);
                final WheelPicker wheelPickerHip = mView.findViewById(R.id.dialog_picker_hip);
                mBuilder.setView(mView);
                final AlertDialog dialog = mBuilder.create();
                dialog.show();
                wheelPickerHip.setData(isHipCmSelected ? hipCM : hipIN);
                wheelPickerHip.setSelectedItemPosition(hipPickerPos);

                dialogDone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        hipPickerPos = wheelPickerHip.getCurrentItemPosition();
                        hipField.setText(wheelPickerHip.getData().get(hipPickerPos).toString());
                    }
                });
                break;
            }
        }
    }

    private void updateUser(Measurements updateMeasurements, String token) {

        showSnackBarMessage("Sending user details..");

        mSubscriptions.add(NetworkUtil.getRetrofit(token).updateMeasurements(token, updateMeasurements)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse, this::handleError));
    }

    private void handleResponse(ResponseForMeasurementsUpdate response) {


        db.updateUserMeasurements(response);

        fm.beginTransaction().replace(R.id.dashboard_content, new Measurement())
                .addToBackStack(null)
                .commit();


        //updateProfile(response.token.token);


    }

    private void handleError(Throwable error) {


        if (error instanceof HttpException) {

            Gson gson = new GsonBuilder().create();

            try {


                {
                    String errorBody = ((HttpException) error).response().errorBody().string();
                    ResponseForMeasurementsUpdate response = gson.fromJson(errorBody, ResponseForMeasurementsUpdate.class);
                    // showSnackBarMessage(response.getMessage());
                }


            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {

            showSnackBarMessage("Network Error !");
        }
    }

    private void showSnackBarMessage(String message) {

        if (getView() != null) {

            Snackbar.make(getView(), message, Snackbar.LENGTH_SHORT).show();
        }
    }

    public double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
