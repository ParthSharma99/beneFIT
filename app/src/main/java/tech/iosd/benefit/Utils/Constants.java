package tech.iosd.benefit.Utils;

import android.util.Base64;
import android.util.Log;

import java.io.UnsupportedEncodingException;

/**
 * Created by SAM33R on 25-05-2018.
 */

public class Constants {

    public static final String BASE_URL = "http://13.59.14.56:5000/api/v1/";
    //public static final String BASE_URL = "http://192.168.0.107:5000/api/v1/";
    public static final String TOKEN = "token";
    public static final String WEIGHT = "weight";
    public static final String HEIGHT = "height";
    public static final String GENDER = "gender";
    public static final String AGE = "age";
    public static final String WAIST_SIZE = "waist";
    public static final String NECK_SIZE = "neck";
    public static final String HIP_SIZE = "hip";
    public static final int RC_SIGN_IN = 400;
    public static final String GPS_UPDATE = "tech.ios.benefit.android.action.broadcast";
    public static final String GPS_CONNECTED = "gpsconnected";
    public static final String GPS_IS_UPDATED = "gpsisupdated";
    public static final String GPS_ONLY_LOCATION_CHANGE = "gpsonlylocationchange";





    public static final int DATABASE_VERSION= 1;
    public static final String DATABASE_NAME = "userDB";
    public static final String TABLE_NAME = "profileLoginedUser";

    public static final float BOWL_GRAM = 40;
    public static final float KATORI_GRAM = 40;

    public static final int ADD_DATA_REQUEST_OK = 300;
    public static final String BREAKFAST = "breakfast";
    public static final String MID_MORNING = "midmornig";
    public static final String LUNCH = "lunch";
    public static final String DINNER = "dinner";
    public static final String SNACKS = "snacks";

 //   public static final String CHAT_SERVER_URL = "http://13.59.14.56:5000/";//global server
   public static final String CHAT_SERVER_URL = "http://192.168.0.107:5000";//local server







}
