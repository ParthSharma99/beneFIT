package tech.iosd.benefit.Model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBDowloadList extends SQLiteOpenHelper{
    private Context context;
    public static final String DATABASE_NAME = "downloadlist.db";
    public static final String TABLE_NAME = "downloads";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";

    public DBDowloadList(Context context){
        super(context,DATABASE_NAME,null,1);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "create table downloads " +
                        "(id integer primary key, name text)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS downloads");
        onCreate(db);
    }

    public void insert(String name){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_NAME, name);
        db.insert(TABLE_NAME, null, contentValues);
    }

    public void delete(String name){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME,
                "name = ? ",
                new String[] { name });
    }

    public boolean isInDataBase(String name) {
        try {
            SQLiteDatabase sqldb = this.getReadableDatabase();
            String Query = "Select * from " + TABLE_NAME + " where " + COLUMN_NAME + " = \'" + name + "\'";
            Cursor cursor = sqldb.rawQuery(Query, null);
            if (cursor.getCount() <= 0) {
                cursor.close();
                return false;
            }
            cursor.close();
            return true;
        }catch (Exception ignored){
        }
        return false;
    }

}
