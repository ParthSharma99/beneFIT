package tech.iosd.benefit.DashboardFragments;

import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import com.aigestudio.wheelpicker.WheelPicker;
import com.github.paolorotolo.expandableheightlistview.ExpandableHeightListView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import android.support.design.widget.Snackbar;
import android.widget.Toast;

import devs.mulham.horizontalcalendar.HorizontalCalendar;
import devs.mulham.horizontalcalendar.utils.HorizontalCalendarListener;
import retrofit2.adapter.rxjava.HttpException;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import tech.iosd.benefit.Model.DatabaseHandler;
import tech.iosd.benefit.Model.Response;
import tech.iosd.benefit.Model.ResponseNutritionPlanForDate;
import tech.iosd.benefit.Network.NetworkUtil;
import tech.iosd.benefit.R;

//public class MyNutrition extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener
public class MyNutrition extends Fragment
{
    TextView breakfastCalories,breakfastFat,breakfastProtein,breakfastCarbs;
    TextView midMorningCalories,midMorningFat,midMorningProtein,midMorningCarbs;
    TextView lunchCalories,lunchFat,lunchProtein,lunchCarbs;
    TextView snackCalories,snackFat,snackProtein,snackCarbs;
    TextView dinnerCalories,dinnerFat,dinnerProtein,dinnerCarbs;
    public Calendar selDate;

    String[] months = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
    ArrayList<String> ingredientTyp = new ArrayList<>();
    ArrayList<String> ingredientsQty;
    Context ctx;
    View rootView;
    FragmentManager fm;
    ExpandableHeightListView breakfastListView,midMorningListView,lunchListView,snackListView,dinnerListView;
    ArrayList<String> breakfastIngredients,midMorningIngredients,lunchIngredients,snacksIngredients,dinnerIngredients;
    ArrayAdapter<String> breakfastAdapter,midMorningAdapter,lunchAdapter,snackAdapter,dinnerAdapter;

    ProgressDialog progressDialog;
    private DatabaseHandler db ;
    SimpleDateFormat dateFormat;
    String selectedDate;
    private CompositeSubscription compositeSubscription;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState)
    {

        progressDialog =  new ProgressDialog(getContext());
        progressDialog.setMessage("Please Wait..");

        progressDialog.setCancelable(true);
        rootView = inflater.inflate(R.layout.dashboard_my_nutrition, container, false);
        ctx = rootView.getContext();
        fm = getFragmentManager();

        Calendar startDate = Calendar.getInstance();
        startDate.add(Calendar.MONTH, -1);

        Calendar endDate = Calendar.getInstance();
        endDate.add(Calendar.MONTH, 1);

        HorizontalCalendar horizontalCalendar = new HorizontalCalendar.Builder(rootView, R.id.my_nutrition_calendar)
                .range(startDate, endDate)
                .datesNumberOnScreen(7)
                .mode(HorizontalCalendar.Mode.DAYS)
                .configure()
                .formatMiddleText("EEEEE\n").sizeMiddleText(12)
                .formatBottomText("dd").sizeBottomText(26)
                .showTopText(false)
                .end()
                .build();
        db = new DatabaseHandler(getContext());
        compositeSubscription=new CompositeSubscription();
        final TextView lbl_year = rootView.findViewById(R.id.my_nutrition_calendar_year);
        final TextView lbl_month = rootView.findViewById(R.id.my_nutrition_calendar_month);
        breakfastCalories=rootView.findViewById(R.id.breakfast_calories);
        breakfastCarbs=rootView.findViewById(R.id.breakfast_carbs);
        breakfastProtein=rootView.findViewById(R.id.breakfast_protein);
        breakfastFat=rootView.findViewById(R.id.breakfast_fats);

        midMorningCalories=rootView.findViewById(R.id.mid_morning_calorie);
        midMorningCarbs=rootView.findViewById(R.id.mid_morning_carbs);
        midMorningProtein=rootView.findViewById(R.id.mid_morning_protein);
        midMorningFat=rootView.findViewById(R.id.mid_morning_fats);

        lunchCalories=rootView.findViewById(R.id.lunch_calories);
        lunchCarbs=rootView.findViewById(R.id.lunch_carbs);
        lunchProtein=rootView.findViewById(R.id.lunch_protein);
        lunchFat=rootView.findViewById(R.id.lunch_fats);

        snackCalories=rootView.findViewById(R.id.snacks_calories);
        snackCarbs=rootView.findViewById(R.id.snacks_carbs);
        snackProtein=rootView.findViewById(R.id.snacks_protein);
        snackFat=rootView.findViewById(R.id.snacks_fats);

        dinnerCalories=rootView.findViewById(R.id.dinner_calories);
        dinnerCarbs=rootView.findViewById(R.id.dinner_carbs);
        dinnerProtein=rootView.findViewById(R.id.dinner_protein);
        dinnerFat=rootView.findViewById(R.id.dinner_fats);

        dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        selectedDate = dateFormat.format(Calendar.getInstance().getTime());
        horizontalCalendar.setCalendarListener(new HorizontalCalendarListener()
        {
            @Override
            public void onDateSelected(Calendar date, int position)
            {
                selDate = date;
                selectedDate = dateFormat.format(date.getTime());
                lbl_year.setText(String.valueOf(selDate.get(Calendar.YEAR)));
                lbl_month.setText(months[selDate.get(Calendar.MONTH)]);
                progressDialog.show();
                getNutritionPlan(selectedDate);
                initListsAndAdapters();
            }
        });
        breakfastListView = rootView.findViewById(R.id.my_nutrition_breakfast);
        midMorningListView = rootView.findViewById(R.id.my_nutrition_mid_morning);
        lunchListView = rootView.findViewById(R.id.my_nutrition_lunch);
        snackListView = rootView.findViewById(R.id.my_nutrition_snacks);
        dinnerListView = rootView.findViewById(R.id.my_nutrition_dinner);

        initListsAndAdapters();

//        //add dummy data for now
//        List<String> dummyList = new ArrayList<>();
//        dummyList.add("2 egg whites");
//        dummyList.add("4 rotis");
//        dummyList.add("1 glass of mix fruit juice");
//        dummyList.add("Handful of nuts");
//
//        midMorningIngredients.addAll(dummyList);
//        lunchIngredients.addAll(dummyList);
//        snacksIngredients.addAll(dummyList);
//        dinnerIngredients.addAll(dummyList);
//        //

        updateLists();

        return rootView;
    }

    public  void getNutritionPlan(String date)
    {
        if(!progressDialog.isShowing()){
            progressDialog.show();
        }

        compositeSubscription.add(NetworkUtil.getRetrofit(db.getUserToken()).getNutritionPlanForDate(date,db.getUserToken())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponseGetNutrition,this::handleErrorGetNutrition));

    }
    float calories,fat,carbs,protein;
    private void handleResponseGetNutrition(ResponseNutritionPlanForDate response)
    {
        List<String> breakFastList = new ArrayList<>();
        List<String> midMorningList = new ArrayList<>();
        List<String> lunchList = new ArrayList<>();
        List<String> snackList = new ArrayList<>();
        List<String> dinnerList = new ArrayList<>();
        breakfastFat.setText("-");breakfastProtein.setText("-");breakfastCarbs.setText("-");breakfastCalories.setText("-");
        midMorningFat.setText("-");midMorningProtein.setText("-");midMorningCarbs.setText("-");midMorningCalories.setText("-");
        lunchFat.setText("-");lunchProtein.setText("-");lunchCarbs.setText("-");lunchCalories.setText("-");
        snackFat.setText("-");snackProtein.setText("-");snackCarbs.setText("-");snackCalories.setText("-");
        dinnerFat.setText("-");dinnerProtein.setText("-");dinnerCarbs.setText("-");dinnerCalories.setText("-");
        if(response.isSuccess())
        {
            for (int i = 0; i < response.getData().size(); i++)
            {
                calories=0;fat=0;carbs=0;protein=0;
                if (response.getData().get(i).getType() == 0)
                {
                    for (int x = 0; x < response.getData().get(i).getNutrition().getFoods().size(); x++)
                    {
                        calories=calories+response.getData().get(i).getNutrition().getFoods().get(x).getFood().getCalories();
                        fat=fat+response.getData().get(i).getNutrition().getFoods().get(x).getFood().getFats();
                        carbs=carbs+response.getData().get(i).getNutrition().getFoods().get(x).getFood().getCarbs();
                        protein=protein+response.getData().get(i).getNutrition().getFoods().get(x).getFood().getProteins();
                        breakFastList.add(response.getData().get(i).getNutrition().getFoods().get(x).getQuantity() + " " + response.getData().get(i).getNutrition().getFoods().get(x).getFood().getUnit() + " " + response.getData().get(i).getNutrition().getFoods().get(x).getFood().getName());
                    }
                    breakfastFat.setText(fat+"");
                    breakfastProtein.setText(protein+"");
                    breakfastCarbs.setText(carbs+"");
                    breakfastCalories.setText(calories+"");
                    breakfastIngredients.addAll(breakFastList);
                }
                if (response.getData().get(i).getType() == 1)
                {
                    for (int x = 0; x < response.getData().get(i).getNutrition().getFoods().size(); x++)
                    {
                        calories=calories+response.getData().get(i).getNutrition().getFoods().get(x).getFood().getCalories();
                        fat=fat+response.getData().get(i).getNutrition().getFoods().get(x).getFood().getFats();
                        carbs=carbs+response.getData().get(i).getNutrition().getFoods().get(x).getFood().getCarbs();
                        protein=protein+response.getData().get(i).getNutrition().getFoods().get(x).getFood().getProteins();
                        midMorningList.add(response.getData().get(i).getNutrition().getFoods().get(x).getQuantity() + " " + response.getData().get(i).getNutrition().getFoods().get(x).getFood().getUnit() + " " + response.getData().get(i).getNutrition().getFoods().get(x).getFood().getName());
                    }
                    midMorningFat.setText(fat+"");
                    midMorningProtein.setText(protein+"");
                    midMorningCarbs.setText(carbs+"");
                    midMorningCalories.setText(calories+"");
                    midMorningIngredients.addAll(midMorningList);
                }
                if (response.getData().get(i).getType() == 2) {
                    for (int x = 0; x < response.getData().get(i).getNutrition().getFoods().size(); x++)
                    {
                        calories=calories+response.getData().get(i).getNutrition().getFoods().get(x).getFood().getCalories();
                        fat=fat+response.getData().get(i).getNutrition().getFoods().get(x).getFood().getFats();
                        carbs=carbs+response.getData().get(i).getNutrition().getFoods().get(x).getFood().getCarbs();
                        protein=protein+response.getData().get(i).getNutrition().getFoods().get(x).getFood().getProteins();
                        lunchList.add(response.getData().get(i).getNutrition().getFoods().get(x).getQuantity() + " " + response.getData().get(i).getNutrition().getFoods().get(x).getFood().getUnit() + " " + response.getData().get(i).getNutrition().getFoods().get(x).getFood().getName());
                    }
                    lunchFat.setText(fat+"");
                    lunchProtein.setText(protein+"");
                    lunchCarbs.setText(carbs+"");
                    lunchCalories.setText(calories+"");
                    lunchIngredients.addAll(lunchList);
                }
                if (response.getData().get(i).getType() == 3)
                {
                    for (int x = 0; x < response.getData().get(i).getNutrition().getFoods().size(); x++)
                    {
                        calories=calories+response.getData().get(i).getNutrition().getFoods().get(x).getFood().getCalories();
                        fat=fat+response.getData().get(i).getNutrition().getFoods().get(x).getFood().getFats();
                        carbs=carbs+response.getData().get(i).getNutrition().getFoods().get(x).getFood().getCarbs();
                        protein=protein+response.getData().get(i).getNutrition().getFoods().get(x).getFood().getProteins();
                        snackList.add(response.getData().get(i).getNutrition().getFoods().get(x).getQuantity() + " " + response.getData().get(i).getNutrition().getFoods().get(x).getFood().getUnit() + " " + response.getData().get(i).getNutrition().getFoods().get(x).getFood().getName());
                    }
                    snackFat.setText(fat+"");
                    snackProtein.setText(protein+"");
                    snackCarbs.setText(carbs+"");
                    snackCalories.setText(calories+"");
                    snacksIngredients.addAll(snackList);
                }
                if (response.getData().get(i).getType() == 4) {
                    for (int x = 0; x < response.getData().get(i).getNutrition().getFoods().size(); x++)
                    {
                        calories=calories+response.getData().get(i).getNutrition().getFoods().get(x).getFood().getCalories();
                        fat=fat+response.getData().get(i).getNutrition().getFoods().get(x).getFood().getFats();
                        carbs=carbs+response.getData().get(i).getNutrition().getFoods().get(x).getFood().getCarbs();
                        protein=protein+response.getData().get(i).getNutrition().getFoods().get(x).getFood().getProteins();
                        dinnerList.add(response.getData().get(i).getNutrition().getFoods().get(x).getQuantity() + " " + response.getData().get(i).getNutrition().getFoods().get(x).getFood().getUnit() + " " + response.getData().get(i).getNutrition().getFoods().get(x).getFood().getName());
                    }
                    dinnerFat.setText(fat+"");
                    dinnerProtein.setText(protein+"");
                    dinnerCarbs.setText(carbs+"");
                    dinnerCalories.setText(calories+"");
                    dinnerIngredients.addAll(dinnerList);
                }

            }
            updateLists();
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
    private void handleErrorGetNutrition(Throwable error) {

        Log.d("error77",error.getMessage());


        if (error instanceof HttpException) {

            Gson gson = new GsonBuilder().create();

            try {

                String errorBody = ((HttpException) error).response().errorBody().string();
                Response response = gson.fromJson(errorBody,Response.class);
                showSnackBarMessage(response.getMessage());
                fm.popBackStack();

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Log.d("error77",error.getMessage());

            showSnackBarMessage("Network Error !");
        }
    }

    private void showSnackBarMessage(String message) {

        if (getView() != null) {

            Snackbar.make(getView(),message, Snackbar.LENGTH_LONG).show();
            if (progressDialog.isShowing()){
                progressDialog.dismiss();

                progressDialog.hide();

            }
            //progressDialog.hide();

        }
    }

    private void initListsAndAdapters(){
        breakfastIngredients = new ArrayList<>();
        midMorningIngredients = new ArrayList<>();
        lunchIngredients = new ArrayList<>();
        snacksIngredients = new ArrayList<>();
        dinnerIngredients = new ArrayList<>();

        breakfastAdapter = new ArrayAdapter<>(ctx,R.layout.listview_text_nutritition,breakfastIngredients);
        midMorningAdapter = new ArrayAdapter<>(ctx,R.layout.listview_text_nutritition,midMorningIngredients);
        lunchAdapter = new ArrayAdapter<>(ctx,R.layout.listview_text_nutritition,lunchIngredients);
        snackAdapter = new ArrayAdapter<>(ctx,R.layout.listview_text_nutritition,snacksIngredients);
        dinnerAdapter = new ArrayAdapter<>(ctx,R.layout.listview_text_nutritition,dinnerIngredients);

        breakfastListView.setAdapter(breakfastAdapter);
        midMorningListView.setAdapter(midMorningAdapter);
        lunchListView.setAdapter(lunchAdapter);
        snackListView.setAdapter(snackAdapter);
        dinnerListView.setAdapter(dinnerAdapter);

        breakfastListView.setExpanded(true);
        midMorningListView.setExpanded(true);
        lunchListView.setExpanded(true);
        snackListView.setExpanded(true);
        dinnerListView.setExpanded(true);

    }

    private void updateLists(){
        breakfastAdapter.notifyDataSetChanged();
        midMorningAdapter.notifyDataSetChanged();
        lunchAdapter.notifyDataSetChanged();
        snackAdapter.notifyDataSetChanged();
        dinnerAdapter.notifyDataSetChanged();
    }

/*
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
    {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
        View mView = getActivity().getLayoutInflater().inflate(R.layout.dialog_picker_ingredient, null);
        TextView dialogTitle = mView.findViewById(R.id.dialog_picker_ingredient_title);
        Button dialogModify = mView.findViewById(R.id.dialog_modify);
        Button dialogRemove = mView.findViewById(R.id.dialog_remove);
        final WheelPicker wheelPickerQty = mView.findViewById(R.id.dialog_picker_ingredient_qty);
        //final WheelPicker wheelPickerTyp = mView.findViewById(R.id.dialog_picker_ingredient_typ);
        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();
        wheelPickerQty.setData(ingredientsQty);
        //wheelPickerTyp.setData(ingredientTyp);

        final int pos = i;

        switch (adapterView.getId())
        {
            case R.id.my_nutrition_breakfast:
            {
                dialogTitle.setText(breakfastIngredients.get(pos));
                dialogModify.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        breakfastIngredients.set(pos, wheelPickerQty.getData().get(wheelPickerQty.getCurrentItemPosition()) + " " + wheelPickerTyp.getData().get(wheelPickerTyp.getCurrentItemPosition()));
                        final ArrayAdapter<String> breakfastAdapter = new ArrayAdapter<>(ctx, R.layout.listview_text, breakfastIngredients);
                        breakfastListView.setAdapter(breakfastAdapter);
                        dialog.dismiss();
                    }
                });
                dialogRemove.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        breakfastIngredients.remove(pos);
                        final ArrayAdapter<String> breakfastAdapter = new ArrayAdapter<>(ctx, R.layout.listview_text, breakfastIngredients);
                        breakfastListView.setAdapter(breakfastAdapter);
                        breakfastListView.getLayoutParams().height = 110 * breakfastIngredients.size();
                        dialog.dismiss();
                    }
                });
                break;
            }
            case R.id.my_nutrition_mid_morning:
            {
                dialogTitle.setText(midMorningIngredients.get(pos));
                dialogModify.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        midMorningIngredients.set(pos, wheelPickerQty.getData().get(wheelPickerQty.getCurrentItemPosition()) + " " + wheelPickerTyp.getData().get(wheelPickerTyp.getCurrentItemPosition()));
                        final ArrayAdapter<String> midMorningAdapter = new ArrayAdapter<>(ctx, R.layout.listview_text, midMorningIngredients);
                        midMorningListView.setAdapter(midMorningAdapter);
                        dialog.dismiss();
                    }
                });
                dialogRemove.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        midMorningIngredients.remove(pos);
                        final ArrayAdapter<String> midMorningAdapter = new ArrayAdapter<>(ctx, R.layout.listview_text, midMorningIngredients);
                        midMorningListView.setAdapter(midMorningAdapter);
                        midMorningListView.getLayoutParams().height = 110 * midMorningIngredients.size();
                        dialog.dismiss();
                    }
                });
                break;
            }
            case R.id.my_nutrition_lunch:
            {
                dialogTitle.setText(lunchIngredients.get(pos));
                dialogModify.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        lunchIngredients.set(pos, wheelPickerQty.getData().get(wheelPickerQty.getCurrentItemPosition()) + " " + wheelPickerTyp.getData().get(wheelPickerTyp.getCurrentItemPosition()));
                        final ArrayAdapter<String> lunchAdapter = new ArrayAdapter<>(ctx, R.layout.listview_text, lunchIngredients);
                        lunchListView.setAdapter(lunchAdapter);
                        dialog.dismiss();
                    }
                });
                dialogRemove.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        lunchIngredients.remove(pos);
                        final ArrayAdapter<String> lunchAdapter = new ArrayAdapter<>(ctx, R.layout.listview_text, lunchIngredients);
                        lunchListView.setAdapter(lunchAdapter);
                        lunchListView.getLayoutParams().height = 110 * lunchIngredients.size();
                        dialog.dismiss();
                    }
                });
                break;
            }
            case R.id.my_nutrition_snacks:
            {
                dialogTitle.setText(snacksIngredients.get(pos));
                dialogModify.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        snacksIngredients.set(pos, wheelPickerQty.getData().get(wheelPickerQty.getCurrentItemPosition()) + " " + wheelPickerTyp.getData().get(wheelPickerTyp.getCurrentItemPosition()));
                        final ArrayAdapter<String> snacksAdapter = new ArrayAdapter<>(ctx, R.layout.listview_text, snacksIngredients);
                        snackListView.setAdapter(snacksAdapter);
                        dialog.dismiss();
                    }
                });
                dialogRemove.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        snacksIngredients.remove(pos);
                        final ArrayAdapter<String> snacksAdapter = new ArrayAdapter<>(ctx, R.layout.listview_text, snacksIngredients);
                        snackListView.setAdapter(snacksAdapter);
                        snackListView.getLayoutParams().height = 110 * snacksIngredients.size();
                        dialog.dismiss();
                    }
                });
                break;
            }
            case R.id.my_nutrition_dinner:
            {
                dialogTitle.setText(dinnerIngredients.get(pos));
                dialogModify.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        dinnerIngredients.set(pos, wheelPickerQty.getData().get(wheelPickerQty.getCurrentItemPosition()) + " " + wheelPickerTyp.getData().get(wheelPickerTyp.getCurrentItemPosition()));
                        final ArrayAdapter<String> dinnerAdapter = new ArrayAdapter<>(ctx, R.layout.listview_text, dinnerIngredients);
                        dinnerListView.setAdapter(dinnerAdapter);
                        dialog.dismiss();
                    }
                });
                dialogRemove.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        dinnerIngredients.remove(pos);
                        final ArrayAdapter<String> dinnerAdapter = new ArrayAdapter<>(ctx, R.layout.listview_text, dinnerIngredients);
                        dinnerListView.setAdapter(dinnerAdapter);
                        dinnerListView.getLayoutParams().height = 110 * dinnerIngredients.size();
                        dialog.dismiss();
                    }
                });
                break;
            }
        }
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.my_nutrition_breakfast_add:
            {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
                View mView = getActivity().getLayoutInflater().inflate(R.layout.dialog_picker_ingredient_add, null);
                Button dialogAdd = mView.findViewById(R.id.dialog_add);
                Button dialogCancel = mView.findViewById(R.id.dialog_cancel);
                final WheelPicker wheelPickerQty = mView.findViewById(R.id.dialog_picker_ingredient_qty);
                final WheelPicker wheelPickerTyp = mView.findViewById(R.id.dialog_picker_ingredient_typ);
                mBuilder.setView(mView);
                final AlertDialog dialog = mBuilder.create();
                dialog.show();
                wheelPickerQty.setData(ingredientsQty);
                wheelPickerTyp.setData(ingredientTyp);

                dialogAdd.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        breakfastIngredients.add(wheelPickerQty.getData().get(wheelPickerQty.getCurrentItemPosition()) + " " + wheelPickerTyp.getData().get(wheelPickerTyp.getCurrentItemPosition()));
                        final ArrayAdapter<String> breakfastAdapter = new ArrayAdapter<>(ctx, R.layout.listview_text, breakfastIngredients);
                        breakfastListView.setAdapter(breakfastAdapter);
                        breakfastListView.getLayoutParams().height = 110 * breakfastIngredients.size();
                        dialog.dismiss();
                    }
                });
                dialogCancel.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        dialog.dismiss();
                    }
                });
                break;
            }
        }
    }*/
}
