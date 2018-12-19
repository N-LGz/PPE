package com.nemge.ppe;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SQLiteActivity extends AppCompatActivity {
    SQLiteDatabasePPE db;
    EditText nameInput, passwordInput;
    Button button_add;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sqlite);

        db = new SQLiteDatabasePPE(this);

        nameInput = findViewById(R.id.Name_EditText);
        passwordInput= findViewById(R.id.Password_EditText);
        button_add = findViewById(R.id.Data_Button);
        Log.d("blabla", "message");
        AddData();
    }

    public void AddData(){
        button_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isInserted = db. insertData(nameInput.getText().toString(), passwordInput.getText().toString());
                if(isInserted){
                    Toast.makeText(SQLiteActivity.this, "Data is inserted sucessfully", Toast.LENGTH_LONG).show();
                }
                else{
                    Toast.makeText(SQLiteActivity.this, "Data not inserted", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
