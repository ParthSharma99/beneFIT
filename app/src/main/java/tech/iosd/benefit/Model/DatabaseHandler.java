package tech.iosd.benefit.Model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import tech.iosd.benefit.Utils.Constants;

/**
 * Created by SAM33R on 07-06-2018.
 */

public class DatabaseHandler extends SQLiteOpenHelper {
    private Context context;

    public DatabaseHandler(Context context) {
        super(context, Constants.DATABASE_NAME, null, Constants.DATABASE_VERSION);
        this.context= context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_CONTACT_TABLE = "create table " + Constants.TABLE_NAME + " ( " + Constants.TOKEN
                + " string primary key, " + Constants.AGE +" int, " +Constants.WEIGHT +  " int, "+ Constants.HEIGHT + " int, "+
                Constants.HIP_SIZE + " int, "+ Constants.WAIST_SIZE + " int, "+Constants.NECK_SIZE + " int, "+
                Constants.GENDER +" string)";

        sqLiteDatabase.execSQL(CREATE_CONTACT_TABLE);

        //SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constants.TOKEN,"");
        contentValues.put(Constants.AGE,0);
        contentValues.put(Constants.WEIGHT,0);
        contentValues.put(Constants.HEIGHT,0);
        contentValues.put(Constants.HIP_SIZE,0);
        contentValues.put(Constants.WAIST_SIZE,0);
        contentValues.put(Constants.NECK_SIZE,0);
        contentValues.put(Constants.GENDER,"");








        sqLiteDatabase.insert(Constants.TABLE_NAME, null, contentValues);
        //db.close();




    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        sqLiteDatabase.execSQL("drop table if exists " + Constants.TABLE_NAME);
        onCreate(sqLiteDatabase);

    }

    // CRUD OPeration

    public void addUser (Response response){

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Constants.TOKEN,response.token.token);

        db.update(Constants.TABLE_NAME, values,null,null);
        db.close();
    }

    public boolean updateUser(int age, int height, int weight , String gender) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constants.AGE,age);
        contentValues.put(Constants.HEIGHT,height);
        //Toast.makeText(context,"hgt: "+ responseForUpdate.data.getHeight(),Toast.LENGTH_SHORT).show();

        contentValues.put(Constants.WEIGHT,weight);
        contentValues.put(Constants.GENDER,gender);
        db.update(Constants.TABLE_NAME, contentValues, "token is not null",null);
        return true;
    }
    public boolean updateUserMeasurements( ResponseForMeasurementsUpdate responseForUpdate) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
//        contentValues.put(Constants.AGE,responseForUpdate.data.getAge());
        contentValues.put(Constants.HEIGHT,responseForUpdate.data.getHeight());
        contentValues.put(Constants.WEIGHT,responseForUpdate.data.getWeight());
//        Toast.makeText(context,"We: "+ responseForUpdate.data.getHeight(),Toast.LENGTH_SHORT).show();
        Log.d("Measurement", "updateUserMeasurements: Weight:" + responseForUpdate.data.getWeight());
        Log.d("Measurement", "updateUserMeasurements: Height:" + responseForUpdate.data.getHeight());
//        Log.d("Measurement", "updateUserMeasurements: Age:" + responseForUpdate.data.getAge());


        contentValues.put(Constants.WAIST_SIZE,responseForUpdate.data.getWaist());
        contentValues.put(Constants.NECK_SIZE,responseForUpdate.data.getNeck());
        contentValues.put(Constants.HIP_SIZE,responseForUpdate.data.getHip());

        db.update(Constants.TABLE_NAME, contentValues, "token is not null",null);
        return true;
    }


    public boolean isLoggedIn(){
        if(getUserToken() != null && getUserToken().equalsIgnoreCase("")){
            return false;
        }else {
            return true;
        }

    }
    public void userLogOut(){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Constants.TOKEN,"");

        db.update(Constants.TABLE_NAME, values,null,null);
        db.close();
    }

    public int getUserHeight(){
        SQLiteDatabase db = this.getReadableDatabase();
        String argument = "select " + Constants.HEIGHT + " from "+Constants.TABLE_NAME;


        Cursor cursor = db.rawQuery(argument,null);

        if (cursor != null)
            cursor.moveToFirst();

        int height =  cursor.getInt(0);
        Toast.makeText(context,"hgt: "+ height,Toast.LENGTH_SHORT).show();
        return height;
    }
    public int getUserWeight(){
        SQLiteDatabase db = this.getReadableDatabase();
        try{

        }catch (Exception e){

        }
        Cursor cursor = db.rawQuery("select " + Constants.WEIGHT + " from "+Constants.TABLE_NAME,null);

        if (cursor != null)
            cursor.moveToFirst();

        int weight =  cursor.getInt(0);
        return weight;
    }

    public int getUserAge(){
        SQLiteDatabase db = this.getReadableDatabase();
        String argument = "select " + Constants.AGE + " from "+Constants.TABLE_NAME;


        Cursor cursor = db.rawQuery(argument,null);

        if (cursor != null)
            cursor.moveToFirst();

        int age =  cursor.getInt(0);
        return age;
    }
    public String getUserGender(){
        SQLiteDatabase db = this.getReadableDatabase();
        String argument = "select " + Constants.GENDER + " from "+Constants.TABLE_NAME;


        Cursor cursor = db.rawQuery(argument,null);

        if (cursor != null)
            cursor.moveToFirst();

        String gender =  cursor.getString(0);
        return gender;
    }

    public int getUserWaist(){
        SQLiteDatabase db = this.getReadableDatabase();
        String argument = "select " + Constants.WAIST_SIZE + " from "+Constants.TABLE_NAME;


        Cursor cursor = db.rawQuery(argument,null);

        if (cursor != null)
            cursor.moveToFirst();

        int waist =  cursor.getInt(0);
        return waist;
    }
    public int getUserHip(){
        SQLiteDatabase db = this.getReadableDatabase();
        String argument = "select " + Constants.HIP_SIZE + " from "+Constants.TABLE_NAME;


        Cursor cursor = db.rawQuery(argument,null);

        if (cursor != null)
            cursor.moveToFirst();

        int hip =  cursor.getInt(0);
        return hip;
    }
    public int getUserNeck(){
        SQLiteDatabase db = this.getReadableDatabase();
        String argument = "select " + Constants.NECK_SIZE + " from "+Constants.TABLE_NAME;


        Cursor cursor = db.rawQuery(argument,null);

        if (cursor != null)
            cursor.moveToFirst();

        int neck =  cursor.getInt(0);
        return neck;
    }
    public String getUserToken(){

        SQLiteDatabase db = this.getReadableDatabase();

        try {

            Cursor cursor = db.rawQuery("select " + Constants.TOKEN + " from "+Constants.TABLE_NAME,null);



            if (cursor != null)
                cursor.moveToFirst();

            String token =  cursor.getString(0);      // Contacts(Integer.parseInt(cursor.getString(0)), cursor.getString(1), cursor.getString(2));
            return token;
        }catch (Exception e){

            e.printStackTrace();
            return null;
        }
    }
    public void logOut(){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constants.TOKEN,"");


        db.update(Constants.TABLE_NAME, contentValues, null,null);

    }

}
