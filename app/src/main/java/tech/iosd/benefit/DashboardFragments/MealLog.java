package tech.iosd.benefit.DashboardFragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.aigestudio.wheelpicker.WheelPicker;
import com.github.paolorotolo.expandableheightlistview.ExpandableHeightListView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import devs.mulham.horizontalcalendar.HorizontalCalendar;
import devs.mulham.horizontalcalendar.utils.HorizontalCalendarListener;
import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import tech.iosd.benefit.Model.BodyForMealLog;
import tech.iosd.benefit.Model.DatabaseHandler;
import tech.iosd.benefit.Model.MealLogForOneMeal;
import tech.iosd.benefit.Model.MealLogFood;
import tech.iosd.benefit.Model.Response;
import tech.iosd.benefit.Model.ResponseForFoodSearch;
import tech.iosd.benefit.Model.ResponseForGetMeal;
import tech.iosd.benefit.Model.ResponseForSuccess;
import tech.iosd.benefit.Network.NetworkUtil;
import tech.iosd.benefit.R;
import tech.iosd.benefit.Model.ResponseForGetMeal.Food;
import tech.iosd.benefit.Utils.Constants;

import static android.app.Activity.RESULT_OK;

public class MealLog extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener
{
    //private class MealUpdateThenUpload;
    ProgressDialog progressDialog;


    public Calendar selDate;
    SimpleDateFormat dateFormat;

    String[] months = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
    ArrayList<String> ingredientTyp = new ArrayList<>();
    ArrayList<String> ingredientsQty;
    Context ctx;
    View rootView;
    FragmentManager fm;
    ExpandableHeightListView breakfastListView,midMorningListView,lunchListView,snackListView,dinnerListView;
    ArrayList<String> breakfastIngredients,midMorningIngredients,lunchIngredients,snacksIngredients,dinnerIngredients;
    ArrayAdapter<String> breakfastAdapter,midMorningAdapter,lunchAdapter,snacksAdapter,dinnerAdapter;
    ArrayList<MealLogFood > listItems;
    tech.iosd.benefit.Adapters.MealLog adapter ;

    TextView dialogCarbs, dialogProtien, dialogCalorie, dialogFats;
    TextView breakfastCarbs, breakfastProtien,breakfastCalorie, breakfastFats;
    TextView midmorningCarbs, midmorningProtien,midmorningCalorie, midmorningFats;
    TextView lunchCarbs, lunchProtien,lunchCalorie, lunchFats;
    TextView snacksCarbs, snacksProtien,snacksCalorie, snacksFats;
    TextView dinnerCarbs, dinnerProtien,dinnerCalorie, dinnerFats;






    private CompositeSubscription mSubscriptions;
    private CompositeSubscription mSubscriptionsSearch;



    private DatabaseHandler db ;
    RecyclerView recyclerView;
    int position = -1;
    MealLogForOneMeal mealLogBreakfast;
    MealLogForOneMeal mealLogMidmorning;
    MealLogForOneMeal mealLogLunch;
    MealLogForOneMeal mealLogSnacks;
    MealLogForOneMeal mealLogdinner;
    private String selectedDate;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState)
    {
        progressDialog =  new ProgressDialog(getContext());
        progressDialog.setMessage("Please Wait..");

        progressDialog.setCancelable(true);
        rootView = inflater.inflate(R.layout.dashboard_meal_log, container, false);
        ctx = rootView.getContext();
        fm = getFragmentManager();
        dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);

        mSubscriptions = new CompositeSubscription();
        mSubscriptionsSearch = new CompositeSubscription();

        mealLogBreakfast = new MealLogForOneMeal(getContext());
        mealLogMidmorning = new MealLogForOneMeal(getContext());
        mealLogLunch = new MealLogForOneMeal(getContext());
        mealLogSnacks = new MealLogForOneMeal(getContext());
        mealLogdinner = new MealLogForOneMeal(getContext());

        db = new DatabaseHandler(getContext());


        listItems = new ArrayList<>();
        MealLogFood mealLogFood =  new MealLogFood();
        mealLogFood.setName("Please search a Food item");
        listItems.add(mealLogFood);

        breakfastCalorie = rootView.findViewById(R.id.meal_log_breakfast_calorie);
        breakfastProtien = rootView.findViewById(R.id.meal_log_breakfast_protien);
        breakfastCarbs = rootView.findViewById(R.id.meal_log_breakfast_carb);
        breakfastFats = rootView.findViewById(R.id.meal_log_breakfast_fat);

        midmorningCalorie = rootView.findViewById(R.id.meal_log_midmorning_calorie);
        midmorningProtien = rootView.findViewById(R.id.meal_log_midmorning_protien);
        midmorningCarbs = rootView.findViewById(R.id.meal_log_midmorning_carb);
        midmorningFats = rootView.findViewById(R.id.meal_log_midmorning_fat);

        lunchCalorie = rootView.findViewById(R.id.meal_log_lunch_calorie);
        lunchProtien = rootView.findViewById(R.id.meal_log_lunch_protien);
        lunchCarbs = rootView.findViewById(R.id.meal_log_lunch_carb);
        lunchFats = rootView.findViewById(R.id.meal_log_lunch_fat);

        snacksCalorie = rootView.findViewById(R.id.meal_log_snacks_calorie);
        snacksProtien = rootView.findViewById(R.id.meal_log_snacks_protien);
        snacksCarbs = rootView.findViewById(R.id.meal_log_snacks_carb);
        snacksFats = rootView.findViewById(R.id.meal_log_snacks_fat);

        dinnerCalorie = rootView.findViewById(R.id.meal_log_dinner_calorie);
        dinnerProtien = rootView.findViewById(R.id.meal_log_dinner_protien);
        dinnerCarbs = rootView.findViewById(R.id.meal_log_dinner_carb);
        dinnerFats = rootView.findViewById(R.id.meal_log_dinner_fat);



        ingredientsQty = new ArrayList<>();
        for (int i = 1; i < 100; i++)
            ingredientsQty.add(Integer.toString(i));
        ingredientTyp.add("gram");
        ingredientTyp.add("piece");
        ingredientTyp.add("bowl");
        ingredientTyp.add("katori");
        ingredientTyp.add("serve");

        snackListView = rootView.findViewById(R.id.my_nutrition_snacks);



        breakfastListView = rootView.findViewById(R.id.my_nutrition_breakfast);
        breakfastIngredients = new ArrayList<>();
        breakfastAdapter = new ArrayAdapter<>(ctx, R.layout.listview_text, breakfastIngredients);
        breakfastListView.setAdapter(breakfastAdapter);
        breakfastListView.setOnItemClickListener(this);
        breakfastListView.setExpanded(true);
        rootView.findViewById(R.id.my_nutrition_breakfast_add).setOnClickListener(this);

        midMorningListView = rootView.findViewById(R.id.my_nutrition_mid_morning);
        midMorningIngredients = new ArrayList<>();
        final ArrayAdapter<String> midMorningAdapter = new ArrayAdapter<>(ctx, R.layout.listview_text, midMorningIngredients);
        midMorningListView.setAdapter(midMorningAdapter);
        midMorningListView.setOnItemClickListener(this);
        midMorningListView.setExpanded(true);
        rootView.findViewById(R.id.my_nutrition_mid_morning_add).setOnClickListener(this);

        lunchListView = rootView.findViewById(R.id.my_nutrition_lunch);
        lunchIngredients = new ArrayList<>();
        final ArrayAdapter<String> lunchAdapter = new ArrayAdapter<>(ctx, R.layout.listview_text, lunchIngredients);
        lunchListView.setAdapter(lunchAdapter);
        lunchListView.setOnItemClickListener(this);
        lunchListView.setExpanded(true);
        rootView.findViewById(R.id.my_nutrition_lunch_add).setOnClickListener(this);

        snackListView = rootView.findViewById(R.id.my_nutrition_snacks);
        snacksIngredients = new ArrayList<>();
        final ArrayAdapter<String> snacksAdapter = new ArrayAdapter<>(ctx, R.layout.listview_text, snacksIngredients);
        snackListView.setAdapter(snacksAdapter);
        snackListView.setOnItemClickListener(this);
        snackListView.setExpanded(true);
        rootView.findViewById(R.id.my_nutrition_snack_add).setOnClickListener(this);

        dinnerListView = rootView.findViewById(R.id.my_nutrition_dinner);
        dinnerIngredients = new ArrayList<>();
        final ArrayAdapter<String> dinnerAdapter = new ArrayAdapter<>(ctx, R.layout.listview_text, dinnerIngredients);
        dinnerListView.setAdapter(dinnerAdapter);
        dinnerListView.setOnItemClickListener(this);
        dinnerListView.setExpanded(true);
        rootView.findViewById(R.id.my_nutrition_dinner_add).setOnClickListener(this);

        Calendar startDate = Calendar.getInstance();
        startDate.add(Calendar.MONTH, -1);

        Calendar endDate = Calendar.getInstance();
        endDate.add(Calendar.MONTH, 1);
        selectedDate = dateFormat.format(Calendar.getInstance().getTime());

        HorizontalCalendar horizontalCalendar = new HorizontalCalendar.Builder(rootView, R.id.meal_log_calendar)
                .range(startDate, endDate)
                .datesNumberOnScreen(7)
                .mode(HorizontalCalendar.Mode.DAYS)
                .configure()
                .formatMiddleText("EEEEE\n").sizeMiddleText(12)
                .formatBottomText("dd").sizeBottomText(18)
                .showTopText(false)
                .end()
                .build();

        final TextView lbl_year = rootView.findViewById(R.id.meal_log_calendar_year);
        final TextView lbl_month = rootView.findViewById(R.id.meal_log_calendar_month);
        lbl_year.setText(String.valueOf(Calendar.getInstance().get(Calendar.YEAR)));
        lbl_month.setText(months[Calendar.getInstance().get(Calendar.MONTH)]);

        horizontalCalendar.setCalendarListener(new HorizontalCalendarListener()
        {
            @Override
            public void onDateSelected(Calendar date, int position)
            {
                if(!progressDialog.isShowing()){
                    progressDialog.show();
                    Toast.makeText(getContext(),"proges on on date change",Toast.LENGTH_SHORT).show();

                }else {
                    progressDialog.dismiss();
                    progressDialog.show();
                    if(!progressDialog.isShowing()){
                        progressDialog.show();
                        Toast.makeText(getContext(),"proges on on date change",Toast.LENGTH_SHORT).show();

                    }else {
                        Toast.makeText(getContext(),"unable to on progress",Toast.LENGTH_SHORT).show();

                    }

                }
                //progressDialog.show();
                selDate = date;
                selectedDate = dateFormat.format(date.getTime());
                Toast.makeText(getContext(),selectedDate,Toast.LENGTH_SHORT).show();
                lbl_year.setText(String.valueOf(selDate.get(Calendar.YEAR)));
                lbl_month.setText(months[selDate.get(Calendar.MONTH)]);

                breakfastIngredients.clear();
                midMorningIngredients.clear();
                lunchIngredients.clear();
                snacksIngredients.clear();
                dinnerIngredients.clear();

                mealLogBreakfast.onDateChange();
                mealLogLunch.onDateChange();
                mealLogMidmorning.onDateChange();
                mealLogSnacks.onDateChange();
                mealLogdinner.onDateChange();

                breakfastAdapter.notifyDataSetChanged();
                midMorningAdapter.notifyDataSetChanged();
                lunchAdapter.notifyDataSetChanged();
                snacksAdapter.notifyDataSetChanged();
                dinnerAdapter.notifyDataSetChanged();

                getMealData(Constants.BREAKFAST);
                getMealData(Constants.MID_MORNING);
                getMealData(Constants.LUNCH);
                getMealData(Constants.SNACKS);
                getMealData(Constants.DINNER);
                updateUI(Constants.DINNER);
                updateUI(Constants.BREAKFAST);
                updateUI(Constants.MID_MORNING);
                updateUI(Constants.LUNCH);
                updateUI(Constants.SNACKS);

            }
        });


        getMealData(Constants.BREAKFAST);
        updateUI(Constants.BREAKFAST);
        getMealData(Constants.MID_MORNING);
        updateUI(Constants.MID_MORNING);
        getMealData(Constants.LUNCH);
        updateUI(Constants.LUNCH);
        getMealData(Constants.SNACKS);
        updateUI(Constants.SNACKS);
        getMealData(Constants.DINNER);
        updateUI(Constants.DINNER);


        return rootView;
    }

    private void getMealData(String meal) {
        if(!progressDialog.isShowing()){
            progressDialog.show();
        }

            mSubscriptions.add(NetworkUtil.getRetrofit(db.getUserToken()).getFoodMeal(selectedDate,meal,db.getUserToken())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(this::handleResponseGetMeal,this::handleErrorGetMeal));



    }

    private void handleResponseGetMeal(ResponseForGetMeal response) {



        if(response.isSuccess()){
            if (response.getData().getType().equalsIgnoreCase(Constants.BREAKFAST)){
                for (int i = 0; i< response.getData().getFood().size(); i++){
                    breakfastIngredients.add(response.getData().getFood().get(i).getQuantity() + " " +response.getData().getFood().get(i).getUnit()+" "+ response.getData().getFood().get(i).getItem().getName());
                    Toast.makeText(getContext(),"default :"+response.getData().getFood().get(i).getItem().getDefaultSize(),Toast.LENGTH_LONG).show();
                    mealLogBreakfast.addMeal(new Food(response.getData().getFood().get(i).getQuantity(),response.getData().getFood().get(i).getItem()),response.getData().getFood().get(i).getQuantity(),response.getData().getFood().get(i).getUnit());

                    updateUI(Constants.BREAKFAST);
                    breakfastAdapter.notifyDataSetChanged();

                }
            }else if(response.getData().getType().equalsIgnoreCase(Constants.MID_MORNING)){
                for (int i = 0; i< response.getData().getFood().size(); i++) {
                    midMorningIngredients.add(response.getData().getFood().get(i).getQuantity() + " " +response.getData().getFood().get(i).getUnit()+" "+ response.getData().getFood().get(i).getItem().getName());
                    Toast.makeText(getContext(),"1" + " " +response.getData().getFood().get(i).getUnit()+" "+ response.getData().getFood().get(i).getItem().getName()+" size: "+ midMorningIngredients.size(),Toast.LENGTH_SHORT).show();
                    mealLogMidmorning.addMeal(new ResponseForGetMeal.Food( response.getData().getFood().get(i).getQuantity(),response.getData().getFood().get(i).getItem()),response.getData().getFood().get(i).getQuantity(),response.getData().getFood().get(i).getUnit());
                    midMorningAdapter.notifyDataSetChanged();
                    updateUI(Constants.MID_MORNING);
                    //uploadMealLogToServer(Constants.MID_MORNING);

                }

            }else if(response.getData().getType().equalsIgnoreCase(Constants.LUNCH)){
                for (int i = 0; i< response.getData().getFood().size(); i++) {
                    lunchIngredients.add(response.getData().getFood().get(i).getQuantity()  + " " +response.getData().getFood().get(i).getUnit()+" "+ response.getData().getFood().get(i).getItem().getName());
                    Toast.makeText(getContext(), response.getData().getFood().get(i).getQuantity() + " " +response.getData().getFood().get(i).getUnit()+" "+ response.getData().getFood().get(i).getItem().getName()+" size: "+ midMorningIngredients.size(),Toast.LENGTH_SHORT).show();
                    mealLogLunch.addMeal(new ResponseForGetMeal.Food( response.getData().getFood().get(i).getQuantity(),response.getData().getFood().get(i).getItem()),response.getData().getFood().get(i).getQuantity(),response.getData().getFood().get(i).getUnit());
                    lunchAdapter.notifyDataSetChanged();
                    updateUI(Constants.LUNCH);
                    //uploadMealLogToServer(Constants.LUNCH);

                }

            }else if(response.getData().getType().equalsIgnoreCase(Constants.SNACKS)){
                for (int i = 0; i< response.getData().getFood().size(); i++) {
                    snacksIngredients.add(response.getData().getFood().get(i).getQuantity()  + " " +response.getData().getFood().get(i).getUnit()+" "+ response.getData().getFood().get(i).getItem().getName());
                    mealLogSnacks.addMeal(new ResponseForGetMeal.Food( response.getData().getFood().get(i).getQuantity(),response.getData().getFood().get(i).getItem()),response.getData().getFood().get(i).getQuantity(),response.getData().getFood().get(i).getUnit());
                    snacksAdapter.notifyDataSetChanged();
                    updateUI(Constants.SNACKS);
                   // uploadMealLogToServer(Constants.SNACKS);

                }

            }else if(response.getData().getType().equalsIgnoreCase(Constants.DINNER)){
                for (int i = 0; i< response.getData().getFood().size(); i++) {
                    dinnerIngredients.add(response.getData().getFood().get(i).getQuantity()  + " " +response.getData().getFood().get(i).getUnit()+" "+ response.getData().getFood().get(i).getItem().getName());
                    mealLogdinner.addMeal(new ResponseForGetMeal.Food( response.getData().getFood().get(i).getQuantity(),response.getData().getFood().get(i).getItem()),response.getData().getFood().get(i).getQuantity(),response.getData().getFood().get(i).getUnit());
                    dinnerAdapter.notifyDataSetChanged();
                    updateUI(Constants.DINNER);
                   // uploadMealLogToServer(Constants.DINNER);
                    if(progressDialog.isShowing()){
                        progressDialog.dismiss();

                        progressDialog.hide();

                    }

                }

            }

        }else {
            if(progressDialog.isShowing()){
                progressDialog.dismiss();

                progressDialog.hide();

            }
        }









    }

    private void handleErrorGetMeal(Throwable error) {

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

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
    {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
        View mView = getActivity().getLayoutInflater().inflate(R.layout.dialog_picker_ingredient, null);
        TextView dialogTitle = mView.findViewById(R.id.dialog_picker_ingredient_title);
        Button dialogModify = mView.findViewById(R.id.dialog_modify);
        Button dialogRemove = mView.findViewById(R.id.dialog_remove);
        final WheelPicker wheelPickerQty = mView.findViewById(R.id.dialog_picker_ingredient_qty);
        final WheelPicker wheelPickerTyp = mView.findViewById(R.id.dialog_picker_ingredient_type);

        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();
        wheelPickerQty.setData(ingredientsQty);
        wheelPickerTyp.setData(ingredientTyp);
        wheelPickerTyp.setSelectedItemPosition(1);

        final int pos = i;

        Toast.makeText(getContext(),String.valueOf(pos),Toast.LENGTH_SHORT).show();

        switch (adapterView.getId())
        {
            case R.id.my_nutrition_breakfast:
            {
                dialogTitle.setText(breakfastIngredients.get(pos));
                String unit = mealLogBreakfast.getMeal().get(pos).getUnit();
                int quantity = mealLogBreakfast.getMeal().get(pos).getQuantity();
                if(unit.equalsIgnoreCase("gram")){

                    wheelPickerTyp.setSelectedItemPosition(0);

                }else if(unit.equalsIgnoreCase("piece")){

                    wheelPickerTyp.setSelectedItemPosition(1);

                }else if(unit.equalsIgnoreCase("bowl")){

                    wheelPickerTyp.setSelectedItemPosition(2);

                }else if(unit.equalsIgnoreCase("katori")){

                    wheelPickerTyp.setSelectedItemPosition(3);

                }else {

                    wheelPickerTyp.setSelectedItemPosition(4);

                }
                wheelPickerQty.setSelectedItemPosition(quantity-1);

                dialogModify.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {

                        int quantity = wheelPickerQty.getCurrentItemPosition()+ 1;
                        String unit = ingredientTyp.get(wheelPickerTyp.getCurrentItemPosition());

                        breakfastIngredients.set(pos,""+quantity + " " +unit+" "+ mealLogBreakfast.getMeal().get(pos).getItem().getName());

                        mealLogBreakfast.updateMealAt(mealLogBreakfast.getMeal().get(pos),quantity,unit,pos);

                        uploadMealLogToServer(Constants.BREAKFAST);
                        updateUI(Constants.BREAKFAST);


                        breakfastAdapter.notifyDataSetChanged();
                        dialog.dismiss();
                    }
                });
                dialogRemove.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {

                        breakfastIngredients.remove(pos);
                        mealLogBreakfast.removeMealAt(pos);

                        breakfastAdapter.notifyDataSetChanged();

                        uploadMealLogToServer(Constants.BREAKFAST);
                        updateUI(Constants.BREAKFAST);

                        dialog.dismiss();
                    }
                });
                break;
            }
            case R.id.my_nutrition_mid_morning:
            {
                dialogTitle.setText(midMorningIngredients.get(pos));
                String unit = mealLogMidmorning.getMeal().get(pos).getUnit();
                int quantity = mealLogMidmorning.getMeal().get(pos).getQuantity();
                if(unit.equalsIgnoreCase("gram")){

                    wheelPickerTyp.setSelectedItemPosition(0);

                }else if(unit.equalsIgnoreCase("piece")){

                    wheelPickerTyp.setSelectedItemPosition(1);

                }else if(unit.equalsIgnoreCase("bowl")){

                    wheelPickerTyp.setSelectedItemPosition(2);

                }else if(unit.equalsIgnoreCase("katori")){

                    wheelPickerTyp.setSelectedItemPosition(3);

                }else {

                    wheelPickerTyp.setSelectedItemPosition(4);

                }
                wheelPickerQty.setSelectedItemPosition(quantity-1);

                dialogModify.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        int quantity = wheelPickerQty.getCurrentItemPosition()+ 1;
                        String unit = ingredientTyp.get(wheelPickerTyp.getCurrentItemPosition());

                        midMorningIngredients.set(pos,""+quantity + " " +unit+" "+ mealLogMidmorning.getMeal().get(pos).getItem().getName());

                        mealLogMidmorning.updateMealAt(mealLogMidmorning.getMeal().get(pos),quantity,unit,pos);

                        uploadMealLogToServer(Constants.MID_MORNING);
                        updateUI(Constants.MID_MORNING);
                        midMorningAdapter.notifyDataSetChanged();
                        dialog.dismiss();
                    }
                });
                dialogRemove.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        midMorningIngredients.remove(pos);
                        mealLogMidmorning.removeMealAt(pos);

                        midMorningAdapter.notifyDataSetChanged();
                        uploadMealLogToServer(Constants.MID_MORNING);
                        updateUI(Constants.MID_MORNING);
                        dialog.dismiss();
                    }
                });
                break;
            }
            case R.id.my_nutrition_lunch:
            {
                dialogTitle.setText(lunchIngredients.get(pos));
                String unit = mealLogLunch.getMeal().get(pos).getUnit();
                int quantity = mealLogLunch.getMeal().get(pos).getQuantity();
                if(unit.equalsIgnoreCase("gram")){

                    wheelPickerTyp.setSelectedItemPosition(0);

                }else if(unit.equalsIgnoreCase("piece")){

                    wheelPickerTyp.setSelectedItemPosition(1);

                }else if(unit.equalsIgnoreCase("bowl")){

                    wheelPickerTyp.setSelectedItemPosition(2);

                }else if(unit.equalsIgnoreCase("katori")){

                    wheelPickerTyp.setSelectedItemPosition(3);

                }else {

                    wheelPickerTyp.setSelectedItemPosition(4);

                }
                wheelPickerQty.setSelectedItemPosition(quantity-1);

                dialogModify.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        int quantity = wheelPickerQty.getCurrentItemPosition()+ 1;
                        String unit = ingredientTyp.get(wheelPickerTyp.getCurrentItemPosition());

                        lunchIngredients.set(pos,""+quantity + " " +unit+" "+ mealLogLunch.getMeal().get(pos).getItem().getName());
                        mealLogLunch.updateMealAt(mealLogLunch.getMeal().get(pos),quantity,unit,pos);

                        uploadMealLogToServer(Constants.LUNCH);
                        updateUI(Constants.LUNCH);

                        lunchAdapter.notifyDataSetChanged();
                        dialog.dismiss();
                    }
                });
                dialogRemove.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        lunchIngredients.remove(pos);
                        mealLogLunch.removeMealAt(pos);
                        lunchAdapter.notifyDataSetChanged();
                        uploadMealLogToServer(Constants.LUNCH);
                        updateUI(Constants.LUNCH);
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
                        int quantity = wheelPickerQty.getCurrentItemPosition()+ 1;
                        String unit = ingredientTyp.get(wheelPickerTyp.getCurrentItemPosition());

                        snacksIngredients.set(pos,""+quantity + " " +unit+" "+ mealLogSnacks.getMeal().get(pos).getItem().getName());
                        mealLogSnacks.updateMealAt(mealLogSnacks.getMeal().get(pos),quantity,unit,pos);

                        uploadMealLogToServer(Constants.SNACKS);
                        updateUI(Constants.SNACKS);


                        snacksAdapter.notifyDataSetChanged();
                        dialog.dismiss();
                    }
                });
                dialogRemove.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        snacksIngredients.remove(pos);
                        mealLogSnacks.removeMealAt(pos);

                        snacksAdapter.notifyDataSetChanged();

                        uploadMealLogToServer(Constants.SNACKS);
                        updateUI(Constants.SNACKS);
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
                        int quantity = wheelPickerQty.getCurrentItemPosition()+ 1;
                        String unit = ingredientTyp.get(wheelPickerTyp.getCurrentItemPosition());

                        dinnerIngredients.set(pos,""+quantity + " " +unit+" "+ mealLogdinner.getMeal().get(pos).getItem().getName());
                        mealLogdinner.updateMealAt(mealLogdinner.getMeal().get(pos),quantity,unit,pos);

                        uploadMealLogToServer(Constants.DINNER);
                        updateUI(Constants.DINNER);

                        dinnerAdapter.notifyDataSetChanged();
                        dialog.dismiss();
                    }
                });
                dialogRemove.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        dinnerIngredients.remove(pos);
                        mealLogdinner.removeMealAt(pos);
                        dinnerAdapter.notifyDataSetChanged();
                        uploadMealLogToServer(Constants.DINNER);
                        updateUI(Constants.DINNER);
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
                MealLogSearch mealLogSearch =  new MealLogSearch();
                mealLogSearch.setTargetFragment(this,Constants.ADD_DATA_REQUEST_OK);
                Bundle bundle = new Bundle();
                String mealType =  Constants.BREAKFAST;
                bundle.putString("mealType",mealType);
                mealLogSearch.setArguments(bundle);
                fm.beginTransaction().addToBackStack(MealLog.class.getName()).replace(R.id.dashboard_content, mealLogSearch,"tag").commit();

                break;
            }
            case R.id.my_nutrition_mid_morning_add:
            {
                MealLogSearch mealLogSearch =  new MealLogSearch();
                mealLogSearch.setTargetFragment(this,Constants.ADD_DATA_REQUEST_OK);
                Bundle bundle = new Bundle();
                bundle.putString("mealType",Constants.MID_MORNING);
                mealLogSearch.setArguments(bundle);
                fm.beginTransaction().addToBackStack(MealLog.class.getName()).replace(R.id.dashboard_content, mealLogSearch,"tag").commit();

                break;
            }
            case R.id.my_nutrition_lunch_add:
            {
                MealLogSearch mealLogSearch =  new MealLogSearch();
                mealLogSearch.setTargetFragment(this,Constants.ADD_DATA_REQUEST_OK);
                Bundle bundle = new Bundle();
                bundle.putString("mealType",Constants.LUNCH);
                mealLogSearch.setArguments(bundle);
                fm.beginTransaction().addToBackStack(MealLog.class.getName()).replace(R.id.dashboard_content, mealLogSearch,"tag").commit();

                break;
            }
            case R.id.my_nutrition_snack_add:
            {
                MealLogSearch mealLogSearch =  new MealLogSearch();
                mealLogSearch.setTargetFragment(this,Constants.ADD_DATA_REQUEST_OK);
                Bundle bundle = new Bundle();
                bundle.putString("mealType",Constants.SNACKS);
                mealLogSearch.setArguments(bundle);
                fm.beginTransaction().addToBackStack(MealLog.class.getName()).replace(R.id.dashboard_content, mealLogSearch,"tag").commit();

                break;
            }
            case R.id.my_nutrition_dinner_add:
            {
                MealLogSearch mealLogSearch =  new MealLogSearch();
                mealLogSearch.setTargetFragment(this,Constants.ADD_DATA_REQUEST_OK);
                Bundle bundle = new Bundle();
                bundle.putString("mealType",Constants.DINNER);
                mealLogSearch.setArguments(bundle);
                fm.beginTransaction().addToBackStack(MealLog.class.getName()).replace(R.id.dashboard_content, mealLogSearch,"tag").commit();

                break;
            }

        }
    }
    private void updateUI(String meal)  {
       /* ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("working..");
        progressDialog.setCancelable(false);
        progressDialog.show();*/

        if(meal.equalsIgnoreCase(Constants.BREAKFAST)){
            breakfastCalorie.setText(String.valueOf(mealLogBreakfast.getMealCalorie()));
            breakfastProtien.setText(String.valueOf(mealLogBreakfast.getMealProtien()));
            breakfastFats.setText(String.valueOf(mealLogBreakfast.getMealFat()));
            breakfastCarbs.setText(String.valueOf(mealLogBreakfast.getMealCarbs()));
        }else  if(meal.equalsIgnoreCase(Constants.MID_MORNING)){
            midmorningCalorie.setText(String.valueOf(mealLogMidmorning.getMealCalorie()));
            midmorningProtien.setText(String.valueOf(mealLogMidmorning.getMealProtien()));
            midmorningFats.setText(String.valueOf(mealLogMidmorning.getMealFat()));
            midmorningCarbs.setText(String.valueOf(mealLogMidmorning.getMealCarbs()));
        }else  if(meal.equalsIgnoreCase(Constants.LUNCH)){
            lunchCalorie.setText(String.valueOf(mealLogLunch.getMealCalorie()));
            lunchProtien.setText(String.valueOf(mealLogLunch.getMealProtien()));
            lunchFats.setText(String.valueOf(mealLogLunch.getMealFat()));
            lunchCarbs.setText(String.valueOf(mealLogLunch.getMealCarbs()));
        }else  if(meal.equalsIgnoreCase(Constants.SNACKS)){
            snacksCalorie.setText(String.valueOf(mealLogSnacks.getMealCalorie()));
            snacksProtien.setText(String.valueOf(mealLogSnacks.getMealProtien()));
            snacksFats.setText(String.valueOf(mealLogSnacks.getMealFat()));
            snacksCarbs.setText(String.valueOf(mealLogSnacks.getMealCarbs()));
        }else  if(meal.equalsIgnoreCase(Constants.DINNER)){
            dinnerCalorie.setText(String.valueOf(mealLogdinner.getMealCalorie()));
            dinnerProtien.setText(String.valueOf(mealLogdinner.getMealProtien()));
            dinnerFats.setText(String.valueOf(mealLogdinner.getMealFat()));
            dinnerCarbs.setText(String.valueOf(mealLogdinner.getMealCarbs()));
        }



    }

    private void uploadMealLogToServer(String meal){
        ArrayList<BodyForMealLog.Food> food1 =  new ArrayList<>();
        if(!progressDialog.isShowing()){
            progressDialog.show();

        }

        if(meal.equalsIgnoreCase(Constants.BREAKFAST)){
            for(int i = 0; i< mealLogBreakfast.getMeal().size(); i++){
                String id = mealLogBreakfast.getMeal().get(i).getItem().getId();
                int quantity = mealLogBreakfast.getMeal().get(i).getQuantity();
                food1.add(new BodyForMealLog.Food(id,quantity,mealLogBreakfast.getMeal().get(i).getUnit()));
                Toast.makeText(getContext()," "+mealLogBreakfast.getMeal().get(i).getItem().getSize().serve+" "+mealLogBreakfast.getMeal().get(i).getItem().getSize().gram,Toast.LENGTH_SHORT).show();
                Log.d("error77",String.valueOf(i));
//                Log.d("error77",id);


            }

            Log.d("error77","breakfast found");
        }else if(meal.equalsIgnoreCase(Constants.MID_MORNING)){
            for(int i = 0; i< mealLogMidmorning.getMeal().size(); i++){
                String id = mealLogMidmorning.getMeal().get(i).getItem().getId();
                int quantity = mealLogMidmorning.getMeal().get(i).getQuantity();
                food1.add(new BodyForMealLog.Food(id,quantity,mealLogMidmorning.getMeal().get(i).getUnit()));
                Log.d("error77"," "+mealLogMidmorning.getMeal().get(i).getItem().getSize().serve+" "+mealLogMidmorning.getMeal().get(i).getItem().getSize().gram);
                Log.d("error77",String.valueOf(mealLogMidmorning.getMeal().size()));
//                Log.d("error77",id);


            }

            Log.d("error77","midmorning found");
        }else if(meal.equalsIgnoreCase(Constants.LUNCH)){
            for(int i = 0; i< mealLogLunch.getMeal().size(); i++){
                String id = mealLogLunch.getMeal().get(i).getItem().getId();
                int quantity = mealLogLunch.getMeal().get(i).getQuantity();
                food1.add(new BodyForMealLog.Food(id,quantity,mealLogLunch.getMeal().get(i).getUnit()));
                Log.d("error77"," "+mealLogLunch.getMeal().get(i).getItem().getSize().serve+" "+mealLogLunch.getMeal().get(i).getItem().getSize().gram);
                Log.d("error77",String.valueOf(mealLogLunch.getMeal().size()));
//                Log.d("error77",id);


            }

            Log.d("error77","lunch found");
        }else if(meal.equalsIgnoreCase(Constants.SNACKS)){
            for(int i = 0; i< mealLogSnacks.getMeal().size(); i++){
                String id = mealLogSnacks.getMeal().get(i).getItem().getId();
                int quantity = mealLogSnacks.getMeal().get(i).getQuantity();
                food1.add(new BodyForMealLog.Food(id,quantity,mealLogSnacks.getMeal().get(i).getUnit()));
                Log.d("error77"," "+mealLogSnacks.getMeal().get(i).getItem().getSize().serve+" "+mealLogSnacks.getMeal().get(i).getItem().getSize().gram);
                Log.d("error77",String.valueOf(mealLogSnacks.getMeal().size()));
//                Log.d("error77",id);


            }

            Log.d("error77","lunch found");
        }else if(meal.equalsIgnoreCase(Constants.DINNER)){
            for(int i = 0; i< mealLogdinner.getMeal().size(); i++){
                String id = mealLogdinner.getMeal().get(i).getItem().getId();
                int quantity = mealLogdinner.getMeal().get(i).getQuantity();
                food1.add(new BodyForMealLog.Food(id,quantity,mealLogdinner.getMeal().get(i).getUnit()));
                Log.d("error77"," "+mealLogdinner.getMeal().get(i).getItem().getSize().serve+" "+mealLogdinner.getMeal().get(i).getItem().getSize().gram);
                Log.d("error77",String.valueOf(mealLogdinner.getMeal().size()));
//                Log.d("error77",id);


            }

            Log.d("error77","lunch found");
        }

        BodyForMealLog bodyForMealLog = new BodyForMealLog(selectedDate,meal, food1);
        mSubscriptions.add(NetworkUtil.getRetrofit(db.getUserToken()).sendFoodMeal(bodyForMealLog,db.getUserToken())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponseSendMealLog,this::handleError));
    }

    private void handleResponseSendMealLog(ResponseForSuccess responseForSuccess){
        showSnackBarMessage("Data saved to server.");
        if(progressDialog.isShowing()){
            progressDialog.dismiss();

            progressDialog.hide();

        }
        if(responseForSuccess.isSuccess()){
            if (responseForSuccess.getMealType().equalsIgnoreCase(Constants.BREAKFAST)){
                updateUI(Constants.BREAKFAST);

            }else if(responseForSuccess.getMealType().equalsIgnoreCase(Constants.MID_MORNING)){
                updateUI(Constants.MID_MORNING);

            }else if(responseForSuccess.getMealType().equalsIgnoreCase(Constants.LUNCH)){
                updateUI(Constants.LUNCH);

            }else if(responseForSuccess.getMealType().equalsIgnoreCase(Constants.SNACKS)){
                updateUI(Constants.SNACKS);

            }else if(responseForSuccess.getMealType().equalsIgnoreCase(Constants.DINNER)){
                updateUI(Constants.DINNER);

            }
        }
        if (progressDialog.isShowing()){
            progressDialog.dismiss();

            progressDialog.hide();

        }
    }



    private void handleError(Throwable error) {


        if (error instanceof HttpException) {

            Gson gson = new GsonBuilder().create();
            showSnackBarMessage("Network Error !");
            Log.d("error77",error.getMessage());

            try {

                String errorBody = ((HttpException) error).response().errorBody().string();
                /*Response response = gson.fromJson(errorBody,Response.class);
                showSnackBarMessage(response.getMessage());*/

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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode== Constants.ADD_DATA_REQUEST_OK){
                //progressDialog =  new ProgressDialog(getContext());
                //progressDialog.setMessage("Please Wait..");
                if(progressDialog.isShowing()){
                    Log.d("error77","dialog was on activity result");
                    progressDialog.dismiss();

                    progressDialog.hide();


                }



                String gson = data.getExtras().getString("meal");
                MealLogFood newFood = (new Gson()).fromJson(gson,MealLogFood.class);
                String mealType = data.getExtras().getString("mealType");

                if(mealType.equalsIgnoreCase(Constants.BREAKFAST)){
                    breakfastIngredients.add("1" + " " +newFood.getUnit()+" "+ newFood.getName());
                    mealLogBreakfast.addMeal(new ResponseForGetMeal.Food( 1,newFood),+1,newFood.getUnit());

                    breakfastAdapter.notifyDataSetChanged();

                    updateUI(Constants.BREAKFAST);
                    uploadMealLogToServer(Constants.BREAKFAST);
                }else if(mealType.equalsIgnoreCase(Constants.MID_MORNING)){
                    midMorningIngredients.add("1" + " " +newFood.getUnit()+" "+ newFood.getName());
                    mealLogMidmorning.addMeal(new ResponseForGetMeal.Food( 1,newFood),+1,newFood.getUnit());

                    midMorningAdapter.notifyDataSetChanged();

                    updateUI(Constants.MID_MORNING);
                    uploadMealLogToServer(Constants.MID_MORNING);
                }else if(mealType.equalsIgnoreCase(Constants.LUNCH)){
                    lunchIngredients.add("1" + " " +newFood.getUnit()+" "+ newFood.getName());
                    mealLogLunch.addMeal(new ResponseForGetMeal.Food( 1,newFood),+1,newFood.getUnit());

                    lunchAdapter.notifyDataSetChanged();

                    updateUI(Constants.LUNCH);
                    uploadMealLogToServer(Constants.LUNCH);
                }else if(mealType.equalsIgnoreCase(Constants.SNACKS)){
                    snacksIngredients.add("1" + " " +newFood.getUnit()+" "+ newFood.getName());
                    mealLogSnacks.addMeal(new ResponseForGetMeal.Food( 1,newFood),+1,newFood.getUnit());

                    snacksAdapter.notifyDataSetChanged();

                    updateUI(Constants.SNACKS);
                    uploadMealLogToServer(Constants.SNACKS);
                }else if(mealType.equalsIgnoreCase(Constants.DINNER)){
                    dinnerIngredients.add("1" + " " +newFood.getUnit()+" "+ newFood.getName());
                    mealLogdinner.addMeal(new ResponseForGetMeal.Food( 1,newFood),+1,newFood.getUnit());

                    dinnerAdapter.notifyDataSetChanged();

                    updateUI(Constants.DINNER);
                    uploadMealLogToServer(Constants.DINNER);
                }





            }
        }
    }


}
