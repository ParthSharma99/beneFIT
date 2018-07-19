package tech.iosd.benefit.DashboardFragments;

import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.aigestudio.wheelpicker.WheelPicker;
import com.github.paolorotolo.expandableheightlistview.ExpandableHeightListView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import devs.mulham.horizontalcalendar.HorizontalCalendar;
import devs.mulham.horizontalcalendar.utils.HorizontalCalendarListener;
import tech.iosd.benefit.R;

//public class MyNutrition extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener
public class MyNutrition extends Fragment

{
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


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState)
    {
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

        final TextView lbl_year = rootView.findViewById(R.id.my_nutrition_calendar_year);
        final TextView lbl_month = rootView.findViewById(R.id.my_nutrition_calendar_month);

        horizontalCalendar.setCalendarListener(new HorizontalCalendarListener()
        {
            @Override
            public void onDateSelected(Calendar date, int position)
            {
                selDate = date;
                lbl_year.setText(String.valueOf(selDate.get(Calendar.YEAR)));
                lbl_month.setText(months[selDate.get(Calendar.MONTH)]);
            }
        });


        breakfastListView = rootView.findViewById(R.id.my_nutrition_breakfast);
        midMorningListView = rootView.findViewById(R.id.my_nutrition_mid_morning);
        lunchListView = rootView.findViewById(R.id.my_nutrition_lunch);
        snackListView = rootView.findViewById(R.id.my_nutrition_snacks);
        dinnerListView = rootView.findViewById(R.id.my_nutrition_dinner);

        initListsAndAdapters();

        //add dummy data for now
        List<String> dummyList = new ArrayList<>();
        dummyList.add("2 egg whites");
        dummyList.add("4 rotis");
        dummyList.add("1 glass of mix fruit juice");
        dummyList.add("Handful of nuts");

        breakfastIngredients.addAll(dummyList);
        midMorningIngredients.addAll(dummyList);
        lunchIngredients.addAll(dummyList);
        snacksIngredients.addAll(dummyList);
        dinnerIngredients.addAll(dummyList);
        //

        updateLists();

        return rootView;
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
