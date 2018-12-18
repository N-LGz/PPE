package com.nemge.ppe;

import android.content.Context;
import android.database.Cursor;
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

        Users UserfromBdd = userBdd.getUserWithName(user.getName());

        if(UserfromBdd != null){
            Toast.makeText(this, UserfromBdd.toString(), Toast.LENGTH_LONG).show();
        }

        if(UserfromBdd == null){
            Toast.makeText(this, "CeT utilisateur n'existe pas", Toast.LENGTH_LONG).show();
        }
        else{
            Toast.makeText(this, "OK", Toast.LENGTH_LONG).show();
        }
        userBdd.close();
    }
}
