package tech.iosd.benefit.DashboardFragments;


import android.os.Bundle;
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
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import tech.iosd.benefit.Model.BodyForChangePassword;
import tech.iosd.benefit.Model.DatabaseHandler;
import tech.iosd.benefit.Model.Response;
import tech.iosd.benefit.Network.NetworkUtil;
import tech.iosd.benefit.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyAccountChangePassword extends Fragment {

    EditText oldPassword, newPassword;
    Button sumbit;
    private CompositeSubscription mSubscriptions;
    private DatabaseHandler db;


    public MyAccountChangePassword() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_account_change_password, container, false);

        mSubscriptions =  new CompositeSubscription();

        oldPassword =  view.findViewById(R.id.change_password_old_password);
        newPassword =  view.findViewById(R.id.change_password_new_password);
        sumbit =  view.findViewById(R.id.change_password_sumbit);
        sumbit.setEnabled(false);
        sumbit.setAlpha(0.8f);
        db =  new DatabaseHandler(getContext());



        oldPassword.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {
                if(!oldPassword.getText().toString().equals("") && !newPassword.getText().toString().equals(""))
                {
                    sumbit.setAlpha(1.0f);
                    sumbit.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) { }
        });

        newPassword.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {
                if(!oldPassword.getText().toString().equals("") && !newPassword.getText().toString().equals(""))
                {
                    sumbit.setAlpha(1.0f);
                    sumbit.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) { }
        });

        sumbit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePassword();
            }
        });



        return view;
    }

    private void changePassword() {
        mSubscriptions.add(NetworkUtil.getRetrofit(db.getUserToken()).changePassword(db.getUserToken(),new BodyForChangePassword(oldPassword.getText().toString(),newPassword.getText().toString()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse,this::handleError));


    }

    private void handleResponse(Response response) {

        if (response.getSuccess()){
            Toast.makeText(getActivity().getApplicationContext(),"Password change success! "+response.mesage,Toast.LENGTH_SHORT).show();
            showSnackBarMessage("Password change success!");
            FragmentManager fm = getFragmentManager();
            fm.popBackStack();
        }else {
            showSnackBarMessage(response.getMessage());

        }


    }

    private void handleError(Throwable error) {


        if (error instanceof HttpException) {

            Gson gson = new GsonBuilder().create();

            try {

                String errorBody = ((HttpException) error).response().errorBody().string();
                Response response = gson.fromJson(errorBody,Response.class);
                showSnackBarMessage(response.getMessage());

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

}

