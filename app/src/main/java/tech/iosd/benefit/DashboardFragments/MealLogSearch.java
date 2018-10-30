package tech.iosd.benefit.DashboardFragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.exceptions.CompositeException;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import tech.iosd.benefit.Model.DatabaseHandler;
import tech.iosd.benefit.Model.MealLogFood;
import tech.iosd.benefit.Model.ResponseForFoodSearch;
import tech.iosd.benefit.Network.NetworkUtil;
import tech.iosd.benefit.R;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class MealLogSearch extends Fragment implements tech.iosd.benefit.Adapters.MealLog.AdapterCallback {

    private RecyclerView recyclerView;
    private tech.iosd.benefit.Adapters.MealLog adapter;
    ArrayList<MealLogFood > listItems;
    private CompositeSubscription mSubscriptionsSearch;
    DatabaseHandler db;
    Bundle bundle;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_meal_log_search, container, false);
        mSubscriptionsSearch =  new CompositeSubscription();
        db = new DatabaseHandler(getContext());
        listItems =  new ArrayList<>();
        bundle = new Bundle();
        bundle =  getArguments();
        recyclerView = rootView.findViewById(R.id.dialog_picker_ingredient_add_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new tech.iosd.benefit.Adapters.MealLog(rootView.getContext(), listItems, getActivity(), this);
        //recyclerView.setAdapter(adapter);

        EditText foodName = rootView.findViewById(R.id.dialog_picker_ingredient_add_food_name);
        foodName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void afterTextChanged(Editable edit) {
                if (edit.length() != 0) {
                    String name =  foodName.getText().toString();
                    //Toast.makeText(getContext(),name,Toast.LENGTH_LONG).show();
                    getSearchResult(name);                        }
            }
        });


        return rootView;
    }
    private void getSearchResult(String name) {

        mSubscriptionsSearch.add(NetworkUtil.getRetrofit(db.getUserToken()).getFoodList(name,db.getUserToken())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse,this::handleError));

    }
    private void handleResponse(ResponseForFoodSearch response) {

        //Toast.makeText(getActivity().getApplicationContext(),token,Toast.LENGTH_SHORT).show();
        listItems.clear();
        for(int i =0; i<response.getData().size(); i++){
            listItems.add(response.getData().get(i));
        }

        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();


        //Toast.makeText(getActivity().getApplicationContext(),"mq"+String.valueOf(adapter.getItemCount()),Toast.LENGTH_SHORT).show();




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

        }
    }


    @Override
    public void newItemSelected(int position) {
        Toast.makeText(getContext(),"new pos: "+ position,Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getContext(), MealLogSearch.class);
        intent.putExtra("meal",(new Gson()).toJson(listItems.get(position)) );

        intent.putExtra("mealType" , bundle.getString("mealType"));
        getTargetFragment().onActivityResult(getTargetRequestCode(), RESULT_OK, intent);
        getFragmentManager().popBackStack();
    }

}
