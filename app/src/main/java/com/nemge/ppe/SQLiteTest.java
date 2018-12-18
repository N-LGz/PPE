package com.nemge.ppe;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SQLiteTest extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sqlite_test);

        UsersBDD userBdd = new UsersBDD(this);
    }
}
