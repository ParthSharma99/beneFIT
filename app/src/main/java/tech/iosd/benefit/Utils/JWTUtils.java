package tech.iosd.benefit.Utils;

import android.util.Base64;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.UnsupportedEncodingException;

public class JWTUtils {

    String userEmail;
    String userName;

    public class PrivateData{
        String name;
        String email;
        String id;
        int iat;
        int exp;




        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }




    public static void decoded(String JWTEncoded) throws Exception {
        String gsonStr =  new String ();
        Gson gson_ = new GsonBuilder().create();

        try {
            String[] split = JWTEncoded.split("\\.");
            Log.d("JWT_DECODED", "Header: " + getJson(split[0]));
            Log.d("JWT_DECODED", "Body: " + getJson(split[1]));
             gsonStr =getJson(split[1]);//getJson(split[1]).







        } catch (UnsupportedEncodingException e) {
            //Error
            Log.d("JWT_DECODED", "error");

        }

        PrivateData gson = gson_.fromJson(gsonStr, PrivateData.class);

        Log.d("JWT_DECODED", "email: "+ gson.getEmail() );


    }

    private static String getJson(String strEncoded) throws UnsupportedEncodingException {
        byte[] decodedBytes = Base64.decode(strEncoded, Base64.URL_SAFE);
        return new String(decodedBytes, "UTF-8");
    }
}