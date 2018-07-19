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
    String p1 = android.Manifest.permission.CAMERA, p2 = android.Manifest.permission.ACCESS_FINE_LOCATION,
            p3 = android.Manifest.permission.ACCESS_COARSE_LOCATION, p4 = android.Manifest.permission.READ_EXTERNAL_STORAGE,
            p5 = android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ;

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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode,resultCode,data);
        Toast.makeText(getApplicationContext(),"23",Toast.LENGTH_LONG).show();

    }

    public void getPermissions() {
    /* Check and Request permission */
        if (!checkPermission(p1)) {
            Log.e("TAG",p1);
            requestPermission(p1);
        } else if (!checkPermission(p2)) {
            Log.e("TAG",p2);
            requestPermission(p2);
        } else if (!checkPermission(p3)) {
            Log.e("TAG",p3);
            requestPermission(p3);
        } else if (!checkPermission(p4)) {
            Log.e("TAG",p4);
            requestPermission(p4);
        } else if (!checkPermission(p5)) {
            Log.e("TAG",p5);
            requestPermission(p5);
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
