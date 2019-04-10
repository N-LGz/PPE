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
    public static final String COL_FIRSTNAME = "firstname";
    public static final String COL_AGE = "age";
    public static final String COL_MAIL = "mail";
    public static final String COL_PASSWORD = "password";

    public SQLiteDatabasePPE(Context context){
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(android.database.sqlite.SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_USERS + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, NAME TEXT, FIRSTNAME TEXT, AGE TEXT, MAIL TEXT, PASSWORD TEXT) ");
    }

    @Override
    public void onUpgrade(android.database.sqlite.SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    public boolean insertNewUsers(String name, String firstname, String age, String mail, String password){
        android.database.sqlite.SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_NAME, name);
        contentValues.put(COL_FIRSTNAME, firstname);
        contentValues.put(COL_AGE, age);
        contentValues.put(COL_MAIL, mail);
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
        db.execSQL("CREATE TABLE " + TABLE_USERS + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, NAME TEXT, FIRSTNAME TEXT, AGE TEXT, MAIL TEXT, PASSWORD TEXT) ");
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
}
