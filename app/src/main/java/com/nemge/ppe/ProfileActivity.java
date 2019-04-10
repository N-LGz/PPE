package com.nemge.ppe;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class ProfileActivity extends AppCompatActivity {

    SQLiteDatabasePPE db;
    TextView name, firstname, age, mail;

    String username = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        setSupportActionBar(findViewById(R.id.home_toolbar));
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);

        db = new SQLiteDatabasePPE(this);

        name = findViewById(R.id.name_bdd);
        firstname = findViewById(R.id.firstname_bdd);
        age = findViewById(R.id.age_bdd);
        mail = findViewById(R.id.mail_bdd);

        Intent intent = getIntent();
        if (intent != null){
            if (intent.hasExtra("name") ){
                username = intent.getStringExtra("name");
                ViewData(username);
            }
        }
    }

    public void ViewData(String iname)
    {
        Cursor data = db.getUser(iname);
        if(data.getCount()==0){
            return;
        }
        else
        {
            name.setText(data.getString(1));
            firstname.setText(data.getString(2));
            age.setText(data.getString(3));
            mail.setText(data.getString(4));
        }
    }
}
