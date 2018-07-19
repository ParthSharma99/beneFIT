package tech.iosd.benefit.OnBoardingFragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import tech.iosd.benefit.DashboardActivity;
import tech.iosd.benefit.DashboardFragments.ChoosePlan;
import tech.iosd.benefit.Model.DatabaseHandler;
import tech.iosd.benefit.Model.Response;
import tech.iosd.benefit.Model.ResponseForUpdate;
import tech.iosd.benefit.Model.UserDetails;
import tech.iosd.benefit.Model.UserFacebookLogin;
import tech.iosd.benefit.Model.UserForLogin;
import tech.iosd.benefit.Model.UserGoogleLogin;
import tech.iosd.benefit.Model.UserProfileUpdate;
import tech.iosd.benefit.Network.NetworkUtil;
import tech.iosd.benefit.R;
import tech.iosd.benefit.Utils.Constants;
import tech.iosd.benefit.Utils.Validation;

import static android.app.Activity.RESULT_CANCELED;
import static tech.iosd.benefit.Utils.Constants.RC_SIGN_IN;

public class Login extends Fragment implements View.OnClickListener
{
    public String loginUsername = "";
    public String loginPassword = "";

    TextView signupBtn;
    TextView forgetPass;
    EditText usernameField;
    EditText passField;
    Button loginBtn;

    Context ctx;
    FragmentManager fm;
    View rootView;

    private CompositeSubscription mSubscriptions;
    private CompositeSubscription mSubscriptionsGoogle;

    private DatabaseHandler db;
    private GoogleSignInClient mGoogleSignInClient;

    public CallbackManager callbackManager;
    private LoginButton loginButton;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState)
    {
        rootView = inflater.inflate(R.layout.onboarding_login, container, false);
        ctx = rootView.getContext();
        fm = getFragmentManager();

        callbackManager = CallbackManager.Factory.create();

        db = new DatabaseHandler(getContext());


        loginBtn = rootView.findViewById(R.id.get_started_login_btn);
        signupBtn = rootView.findViewById(R.id.get_started_login_signup_btn);
        forgetPass = rootView.findViewById(R.id.get_started_login_forget_pass);
        usernameField = rootView.findViewById(R.id.get_started_login_username);
        passField = rootView.findViewById(R.id.get_started_login_pass);


        loginBtn.setAlpha(0.2f);
        loginBtn.setEnabled(false);

        usernameField.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {
                loginUsername = usernameField.getText().toString();
                if(!loginUsername.equals("") && !loginPassword.equals(""))
                {
                    loginBtn.setAlpha(1.0f);
                    loginBtn.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) { }
        });
        passField.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {
                loginPassword = passField.getText().toString();
                if(!loginUsername.equals("") && !loginPassword.equals(""))
                {
                    loginBtn.setAlpha(1.0f);
                    loginBtn.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) { }
        });

        loginBtn.setOnClickListener(this);
        rootView.findViewById(R.id.get_started_login_fb).setOnClickListener(this);
        rootView.findViewById(R.id.get_started_login_google).setOnClickListener(this);
        forgetPass.setOnClickListener(this);
        signupBtn.setOnClickListener(this);



        mSubscriptions = new CompositeSubscription();
        mSubscriptionsGoogle = new CompositeSubscription();


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(getString(R.string.server_client_id))
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);



        loginButton = (LoginButton) rootView.findViewById(R.id.login_button);

        List< String > permissionNeeds = Arrays.asList( "email");
        loginButton.setReadPermissions(permissionNeeds);
        loginButton.registerCallback(callbackManager,
                new FacebookCallback < LoginResult > () {
                @Override
                public void onSuccess(LoginResult loginResult) {

                    showSnackBarMessage("Connected to facebook.");
                    String accessToken = loginResult.getAccessToken()
                            .getToken();
                    Log.d("token",accessToken);

                    GraphRequest request = GraphRequest.newMeRequest(
                            loginResult.getAccessToken(),
                            new GraphRequest.GraphJSONObjectCallback() {@Override
                            public void onCompleted(JSONObject object,
                                                    GraphResponse response) {

                                Log.d("error77",
                                        response.toString());
                                Log.d("error77",
                                        object.toString());


                                try {
                                    String email = object.getString("email");
                                    checkForFacebookToken(object.getString("first_name"),email,accessToken);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    showSnackBarMessage("unable to get email from facebook.\nTry again or login with email.");
                                }

                            }
                            });
                    Bundle bundle = new Bundle();
                    bundle.putString("fields", "email,first_name");
                    request.setParameters(bundle);
                    request.executeAsync();
                }

                    @Override
                    public void onCancel() {
                    showSnackBarMessage("Request cancelled.");
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        System.out.println("onError");
                        showSnackBarMessage( exception.getCause().toString());
                    }
                });

        loginButton.setFragment(this);

        return rootView;
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.get_started_login_btn:
            {
                TextView invalidUsername = rootView.findViewById(R.id.get_started_login_invalid);
                TextView invalidPass = rootView.findViewById(R.id.get_started_pass_invalid);

                if(Validation.validateFields(usernameField.getText().toString())&&Validation.validateFields(passField.getText().toString())){
                    loginProcess(new UserForLogin(usernameField.getText().toString(), passField.getText().toString()));
                    showSnackBarMessage("Connecting to server...");
                }else {
                    showSnackBarMessage("Enter valid details.");
                }




                break;
            }
            case R.id.get_started_login_signup_btn:
            {
                Signup signup = new Signup();
                signup.setArguments(getArguments());
                fm.beginTransaction().replace(R.id.onboarding_content, signup)
                        .addToBackStack(null)
                        .commit();
                break;
            }
            case R.id.get_started_login_fb:
            {
                facebookSignIn();
                break;
            }
            case R.id.get_started_login_google:
            {
                googleSignIn();


                break;
            }
            case R.id.get_started_login_forget_pass:
            {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
                View mView = getActivity().getLayoutInflater().inflate(R.layout.dialog_forget_password, null);
                Button dialogCancel = mView.findViewById(R.id.dialog_cancel);
                final Button dialogAccept = mView.findViewById(R.id.dialog_accept);
                dialogAccept.setAlpha(0.2f);
                dialogAccept.setEnabled(false);

                final EditText dialogEmail = mView.findViewById(R.id.dialog_email);
                TextView dialogEmailInvalid = mView.findViewById(R.id.dialog_email_invalid);
                mBuilder.setView(mView);
                final AlertDialog dialog = mBuilder.create();
                dialog.show();

                dialogEmail.addTextChangedListener(new TextWatcher()
                {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
                    {
                        if(!dialogEmail.getText().toString().equals(""))
                        {
                            dialogAccept.setAlpha(1.0f);
                            dialogAccept.setEnabled(true);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable editable) { }
                });
                dialogCancel.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        dialog.dismiss();
                    }
                });
                dialogAccept.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        dialog.dismiss();
                        showForgetPassSuccessMessage();
                    }
                });
                break;

            }
        }
    }

    private void facebookSignIn() {


        loginButton.performClick();

    }

    private void googleSignIn() {
        mGoogleSignInClient.signOut().addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                    }
                });
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        callbackManager.onActivityResult(requestCode, resultCode, data);
        //callbackManager.onActivityResult(requestCode, resultCode, data);



        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (resultCode != RESULT_CANCELED && requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            Log.d("googler"," " +"name : "+ account.getEmail()+" Token: "+account.getIdToken().toString());
            checkForValidToken(account.getGivenName(),account.getEmail(),account.getIdToken());

        } catch (ApiException e) {

            Toast.makeText(getActivity().getApplicationContext(), "signInResult:failed code=" + e.getStatusCode(),Toast.LENGTH_LONG).show();

        }
    }

    void showForgetPassSuccessMessage()
    {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
        View mView = getActivity().getLayoutInflater().inflate(R.layout.dialog_message, null);
        TextView dialogMsg = mView.findViewById(R.id.dialog_message);
        Button dialogDone = mView.findViewById(R.id.dialog_done);
        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();

        dialogMsg.setText("A password reset message was sent to your email address...");
        dialogDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                dialog.dismiss();
            }
        });
    }

    private void loginProcess(UserForLogin userForLogin) {

        mSubscriptions.add(NetworkUtil.getRetrofit().login(userForLogin)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse,this::handleError));
    }

    private void handleResponse(Response response) {

        Log.d("respone",response.toString());


        try{
            if (response.getSuccess()){
                db.addUser(response);

                Toast.makeText(getContext(),"successfull operation",Toast.LENGTH_SHORT).show();
                Log.d("login"," Token: "+response.token.token);
                updateProfile(response.token.token);
                showSnackBarMessage("Sending user details..");


            }else if(!response.getSuccess()){
                showSnackBarMessage(response.getMessage());
            }



        }catch (Exception e){
            Toast.makeText(getContext(),"error\nreinstall app or contact developer",Toast.LENGTH_SHORT).show();
            Toast.makeText(getContext(),e.getMessage().toString(),Toast.LENGTH_SHORT).show();

        }





    }

    private void handleError(Throwable error) {

        Log.d("error77",error.getMessage());


        if (error instanceof HttpException) {

            Gson gson = new GsonBuilder().create();

            try {

                if(((HttpException) error).code()==401){
                    showSnackBarMessage("Sorry you don't have an account.\nCreate one and continue.");

                }
                else {
                    String errorBody = ((HttpException) error).response().errorBody().string();
                    Response response = gson.fromJson(errorBody,Response.class);
                    showSnackBarMessage(response.getMessage().toString());
                }


            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {

            Log.e("error77",error.getMessage());
            showSnackBarMessage("Network Error !");
        }
    }


    private void checkForValidToken(String name,String email, String token){
        mSubscriptionsGoogle.add(NetworkUtil.getRetrofit().loginGoogle(new UserGoogleLogin(name, email,token))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse,this::handleError));
    }
    private void checkForFacebookToken(String name,String email, String token){
        mSubscriptionsGoogle.add(NetworkUtil.getRetrofit().loginFacebook(new UserFacebookLogin(name, email,token))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse,this::handleError));
    }

    private void updateProfile(String token){
        Bundle bundle = getArguments();
        if(bundle == null){
            Toast.makeText(getActivity().getApplicationContext(),"Please make your profile first.",Toast.LENGTH_SHORT).show();
            fm.beginTransaction().replace(R.id.onboarding_content, new SetupProfile())
                    .addToBackStack(null)
                    .commit();
        }
        int age = Integer.valueOf(bundle.getString("age"));
        int height = Integer.valueOf(bundle.getString("height"));
        int weight = Integer.valueOf(bundle.getString("weight"));
        String gender = bundle.getString("gender");
        //Toast.makeText(getActivity().getApplicationContext(),"Age: "+ String.valueOf(age)+"\nHieght; "+String.valueOf(height)+"\nWeight: "+String.valueOf(weight)+"\nGender: "+gender,Toast.LENGTH_LONG).show();
        UserProfileUpdate userProfileUpdate = new UserProfileUpdate(age, height, weight,gender);
        mSubscriptions.add(NetworkUtil.getRetrofit(token).update(token,userProfileUpdate)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponseUpdate,this::handleErrorUpdate));
    }


    private void handleResponseUpdate(ResponseForUpdate responseForUpdate) {


        showSnackBarMessage("Update successful.");
        Activity activity = getActivity();
        if(activity != null)
        {
            try{
                Bundle bundle =  getArguments();
               // ResponseForUpdate r = new ResponseForUpdate();
                int age = Integer.valueOf(bundle.getString("age"));
                int height = Integer.valueOf(bundle.getString("height"));
                int weight = Integer.valueOf(bundle.getString("weight"));
                String gender = bundle.getString("gender");
               /* r.setAge(age);
                r.setHeight(height);
                r.setWeight(weight);
                r.setGender(gender);*/
                db.updateUser(age,height,weight,gender);

                Toast.makeText(getContext(),"successfull operation",Toast.LENGTH_SHORT).show();

            }catch (Exception e){
                Toast.makeText(getContext(),"error\nreinstall app or contact developer",Toast.LENGTH_SHORT).show();
                Toast.makeText(getContext(),"error: "+ e.getMessage(),Toast.LENGTH_SHORT).show();
                Log.e("DBERROR",e.getMessage());

            }




            Intent myIntent = new Intent(activity, DashboardActivity.class);

            startActivity(myIntent);
            getActivity().finish();
        }


    }
    private void handleErrorUpdate(Throwable error) {

        db.userLogOut();

        if (error instanceof HttpException) {

            Gson gson = new GsonBuilder().create();

            try {

                String errorBody = ((HttpException) error).response().errorBody().string();
                Response response = gson.fromJson(errorBody,Response.class);
                showSnackBarMessage(response.getMessage().toString());

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Log.e("error77",error.getMessage());

            showSnackBarMessage("Network Error !");
        }
    }

    private void showSnackBarMessage(String message) {

        if (getView() != null) {

            Snackbar.make(getView(),message, Snackbar.LENGTH_SHORT).show();
        }
    }
}
