package tech.iosd.benefit;

import android.*;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import tech.iosd.benefit.OnBoardingFragments.GetStarted;

public class OnBoardingActivity extends AppCompatActivity
{
    FragmentManager fm;
    private static final int PERMISSION_REQUEST_CODE = 1;
    String[] permissions = { android.Manifest.permission.CAMERA,
        android.Manifest.permission.ACCESS_FINE_LOCATION,
        android.Manifest.permission.ACCESS_COARSE_LOCATION,
        android.Manifest.permission.READ_EXTERNAL_STORAGE,
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);
        getPermissions();
        fm = getSupportFragmentManager();
        fm.beginTransaction().replace(R.id.onboarding_content, new GetStarted()).commit();
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode,resultCode,data);
        Toast.makeText(getApplicationContext(),"23",Toast.LENGTH_LONG).show();
    }

    public void getPermissions()
    {    
        if (!checkPermission(permissions[0])) {
            Log.e("TAG",permissions[0]);
            requestPermission(permissions[0]);
        } else if (!checkPermission(permissions[1])) {
            Log.e("TAG",permissions[1]);
            requestPermission(permissions[1]);
        } else if (!checkPermission(permissions[2])) {
            Log.e("TAG",permissions[2]);
            requestPermission(permissions[2]);
        } else if (!checkPermission(permissions[3])) {
            Log.e("TAG",permissions[3]);
            requestPermission(permissions[3]);
        } else if (!checkPermission(permissions[4])) {
            Log.e("TAG",permissions[4]);
            requestPermission(permissions[4]);
        }else {
            Toast.makeText(OnBoardingActivity.this, "All permission granted", Toast.LENGTH_LONG).show();
        }
    }
    private boolean checkPermission(String permission) {
        int result = ContextCompat.checkSelfPermission(OnBoardingActivity.this, permission);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }
    private void requestPermission(String permission) {

        if (ContextCompat.checkSelfPermission(OnBoardingActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(OnBoardingActivity.this, new String[]{permission}, PERMISSION_REQUEST_CODE);
        } else {
            Log.e("TAG","Not say request");
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                Log.e("TAG", "val " + grantResults[0]);
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getPermissions();
                    Toast.makeText(OnBoardingActivity.this, "Permissions are required to run app", Toast.LENGTH_LONG).show();

                } else {
                    //Toast.makeText(getContext(), "Bye bye", Toast.LENGTH_LONG).show();
                    getPermissions();
                    
                    Toast.makeText(OnBoardingActivity.this, "Permissions are required to run app", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }
}
