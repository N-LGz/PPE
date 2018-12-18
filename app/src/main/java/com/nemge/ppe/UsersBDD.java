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
        //On créer la BDD et sa table
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
        //Création d'un ContentValues (fonctionne comme une HashMap)
        ContentValues values = new ContentValues();
        //on lui ajoute une valeur associé à une clé (qui est le nom de la colonne dans laquelle on veut mettre la valeur)
        values.put(COL_NAME, user.getName());
        values.put(COL_PASSWORD, user.getPassword());
        //on insère l'objet dans la BDD via le ContentValues
        return bdd.insert(USERS_TABLE, null, values);
    }


    public int updateUser(int id, Users user){
        //La mise à jour d'un livre dans la BDD fonctionne plus ou moins comme une insertion
        //il faut simple préciser quelle livre on doit mettre à jour grâce à l'ID
        ContentValues values = new ContentValues();
        values.put(COL_NAME, user.getName());
        values.put(COL_PASSWORD, user.getPassword());
        return bdd.update(USERS_TABLE, values, COL_ID + " = " +id, null);
    }

    public int removeUserWithID(int id){
        //Suppression d'un livre de la BDD grâce à l'ID
        return bdd.delete(USERS_TABLE, COL_ID + " = " +id, null);
    }

    public Cursor getUserWithName(String name){
        //Récupère dans un Cursor les valeur correspondant à un livre contenu dans la BDD (ici on sélectionne le livre grâce à son titre)
        Cursor c = bdd.query(USERS_TABLE, new String[] {COL_ID, COL_NAME, COL_PASSWORD}, COL_NAME + " LIKE \"" + name +"\"", null, null, null, null);
        return (Cursor) cursorToUser(c);
    }

    private Users cursorToUser(Cursor c){
        //si aucun élément n'a été retourné dans la requête, on renvoie null
        if (c.getCount() == 0)
            return null;

        //Sinon on se place sur le premier élément
        c.moveToFirst();
        //On créé un livre
        Users user = new Users();
        //on lui affecte toutes les infos grâce aux infos contenues dans le Cursor
        user.setId(c.getInt(NUM_COL_ID));
        user.setName(c.getString(NUM_COL_NAME));
        user.setPassword(c.getString(NUM_COL_PASSWORD));
        //On ferme le cursor
        c.close();

        //On retourne le livre
        return user;
    }
}
