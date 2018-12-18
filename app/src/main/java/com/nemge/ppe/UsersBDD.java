package com.nemge.ppe;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class UsersBDD {
    private static final int VERSION_BDD = 1;
    private static final String NOM_BDD = "users.db";

    private static final String USERS_TABLE = "users_table";
    private static final String COL_ID = "ID";
    private static final int NUM_COL_ID = 0;
    private static final String COL_NAME = "NAME";
    private static final int NUM_COL_NAME = 1;
    private static final String COL_PASSWORD = "Password";
    private static final int NUM_COL_PASSWORD = 2;

    private SQLiteDatabase bdd;
    private BDDInhal bdd_inhal;

    public UsersBDD(Context context){
        bdd_inhal = new BDDInhal(context, NOM_BDD, null, VERSION_BDD);
    }

    public void open(){
        bdd = bdd_inhal.getWritableDatabase();
    }

    public void close(){
        bdd.close();
    }

    public SQLiteDatabase getBDD(){
        return bdd;
    }


    public long insertUser(Users user) {
        ContentValues values = new ContentValues();
        values.put(COL_NAME, user.getName());
        values.put(COL_PASSWORD, user.getPassword());
        return bdd.insert(USERS_TABLE, null, values);
    }


    public int updateUser(int id, Users user){
        ContentValues values = new ContentValues();
        values.put(COL_NAME, user.getName());
        values.put(COL_PASSWORD, user.getPassword());
        return bdd.update(USERS_TABLE, values, COL_ID + " = " +id, null);
    }

    public int removeUserWithID(int id){
        return bdd.delete(USERS_TABLE, COL_ID + " = " +id, null);
    }

    public Users getUserWithName(String name){
        Cursor c = bdd.query(USERS_TABLE, new String[] {COL_ID, COL_NAME, COL_PASSWORD}, COL_NAME + " LIKE \"" + name +"\"", null, null, null, null);
        return cursorToUser(c);
    }

    private Users cursorToUser(Cursor c){
        if (c.getCount() == 0) {
            return null;
        }
        else{
            c.moveToFirst();
            Users user = new Users();

            user.setId(c.getInt(NUM_COL_ID));
            user.setName(c.getString(NUM_COL_NAME));
            user.setPassword(c.getString(NUM_COL_PASSWORD));

            c.close();
            return user;
        }
    }
}
