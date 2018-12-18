package com.nemge.ppe;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class SQLiteTest extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sqlite_test);

        UsersBDD userBdd = new UsersBDD(this);
        Users user = new Users(1, "Michel", "michel1");
        userBdd.open();
        userBdd.insertUser(user);

        Users userfromBdd = (Users) userBdd.getUserWithName(user.getName());

        if(userfromBdd != null){
            //On affiche les infos du livre dans un Toast
            Toast.makeText(this, userfromBdd.toString(), Toast.LENGTH_LONG).show();
        }

        if(userfromBdd == null){
            //On affiche un message indiquant que le livre n'existe pas dans la BDD
            Toast.makeText(this, "Ce livre n'existe pas dans la BDD", Toast.LENGTH_LONG).show();
        }
        //Si le livre existe (mais normalement il ne devrait pas)
        else{
            //on affiche un message indiquant que le livre existe dans la BDD
            Toast.makeText(this, "Ce livre existe dans la BDD", Toast.LENGTH_LONG).show();
        }

        userBdd.close();
    }
}
