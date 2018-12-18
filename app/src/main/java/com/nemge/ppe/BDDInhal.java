package com.nemge.ppe;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BDDInhal extends SQLiteOpenHelper {
    public static String USER_ID = "id";
    public static String USER_NAME = "name";
    public static String USER_PASSWORD = "password";

    public static String USER_TABLE_NAME = "Users";

    public static String USER_TABLE_CREATE =
            "CREATE TABLE " + USER_TABLE_NAME + " (" +

                    USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +

                    USER_NAME + " TEXT, " +

                    USER_PASSWORD + " TEXT);";

    public static String USER_TABLE_DROP = "DROP TABLE IF EXISTS " + USER_TABLE_NAME + ";";

    public BDDInhal(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(USER_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(USER_TABLE_DROP);
        onCreate(db);
    }
}
