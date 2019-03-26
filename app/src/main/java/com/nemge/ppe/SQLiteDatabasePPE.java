package com.nemge.ppe;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.Calendar;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class SQLiteDatabasePPE extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "PPE.db";
    //USERS TABLE
    public static final String TABLE_USERS = "users_table";
    public static final String COL_ID = "id";
    public static final String COL_NAME = "name";
    public static final String COL_PASSWORD = "password";

    //DOSES TABLE
    public static final String TABLE_DOSES = "doses_table";
    public static final String COL_DATE = "date_hours";

    public SQLiteDatabasePPE(Context context){
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(android.database.sqlite.SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_USERS + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, NAME TEXT, PASSWORD TEXT) ");
        db.execSQL("CREATE TABLE " + TABLE_DOSES + " (DATE_HOURS DATETIME) ");
    }

    @Override
    public void onUpgrade(android.database.sqlite.SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DOSES);
        onCreate(db);
        autofillBDD();
    }

    public void autofillBDD(){
        insertNewUsers("Geoffrey", "1234");
        insertNewUsers("Aurelio", "eternite");
        insertNewUsers("admin", "admin" );

        insertNewDose("20190116 10:30:00");
        insertNewDose("20190116 10:31:00");
        insertNewDose("20190116 10:32:00");
        insertNewDose("20190116 11:30:00");
        insertNewDose("20190116 12:30:00");
        insertNewDose("20190116 15:30:00");
        insertNewDose("20190116 15:31:00");
        insertNewDose("20190116 18:30:00");//8 for day 1
        insertNewDose("20190117 10:30:00");
        insertNewDose("20190117 11:30:00");
        insertNewDose("20190117 12:30:00");
        insertNewDose("20190117 12:31:00");
        insertNewDose("20190117 13:30:00");
        insertNewDose("20190117 14:30:00");//6 for day 2
        insertNewDose("20190118 10:30:00");
        insertNewDose("20190118 11:30:00");
        insertNewDose("20190118 12:30:00");
        insertNewDose("20190118 12:31:00");
        insertNewDose("20190118 13:30:00");
        insertNewDose("20190118 14:30:00");//6 for day 3
        insertNewDose("20190119 10:30:00");
        insertNewDose("20190119 11:30:00");
        insertNewDose("20190119 12:30:00");//3 for day 4

        //SQL pour chopper les données de cette semaine
        //SELECT COUNT(*) FROM TABLE_USERS WHERE COL_DATE >= "20190117 00:00:00" AND COL_DATE < "20190117 23:59:59"

    }

    public boolean insertNewUsers(String name, String password){
        android.database.sqlite.SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_NAME, name);
        contentValues.put(COL_PASSWORD, password);
        long result = db.insert(TABLE_USERS, null, contentValues);
        if(result==-1){
            return false;
        }
        else{
            return true;
        }
    }

    public void deleteAllUsers(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE " + TABLE_USERS);
        db.execSQL("CREATE TABLE " + TABLE_USERS + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, NAME TEXT, PASSWORD TEXT) ");
        db.close();
    }

    public Cursor getAllUsers(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor result = db.rawQuery("SELECT * FROM " + TABLE_USERS, null);
        return result;
    }

    public Cursor getUser(String name){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor result = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE COL_NAME = " + name, null);
        return result;
    }

    public boolean insertNewDoseNow(){
        android.database.sqlite.SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        Calendar cal = Calendar.getInstance();
        Date date=cal.getTime();
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        String formattedDate=dateFormat.format(date);

        contentValues.put(COL_DATE, formattedDate);
        long result = db.insert(TABLE_USERS, null, contentValues);
        if(result==-1){
            return false;
        }
        else{
            return true;
        }
    }

    public boolean insertNewDose(String date){
        android.database.sqlite.SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COL_DATE, date);
        long result = db.insert(TABLE_USERS, null, contentValues);
        if(result==-1){
            return false;
        }
        else{
            return true;
        }
    }

    public void deleteAllDoses(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE " + TABLE_DOSES);
        db.execSQL("CREATE TABLE " + TABLE_DOSES + " (DATE_HOURS DATETIME) ");
        db.close();
    }

}
