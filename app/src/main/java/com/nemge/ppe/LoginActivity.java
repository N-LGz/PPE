package com.nemge.ppe;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {
    SQLiteDatabasePPE db;
    EditText name_log, password_log;
    Button button_log;
    Button button_insert;
    Button button_show;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        db = new SQLiteDatabasePPE(this);

        name_log = findViewById(R.id.name_login);
        password_log = findViewById(R.id.password_login);
        button_log = findViewById(R.id.button_login);
        Login();

        button_insert = findViewById(R.id.button_insert);
        AddData();

        button_show = findViewById(R.id.button_show);
        ViewData();
    }

    public void ViewData(){
        button_show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cursor data = db.getAllUsers();
                if(data.getCount()==0){
                    return;
                }
                else{
                    StringBuffer buffer = new StringBuffer();
                    while(data.moveToNext()){
                        buffer.append("ID : " + data.getString(0)+ "\n ");
                        buffer.append("NAME : " + data.getString(1)+ "\n ");
                        buffer.append("PASSWORD : " + data.getString(2)+ "\n ");
                    }
                    showMessage("Data", buffer.toString());
                }
            }
        });
    }

    public void AddData(){
        button_insert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isInserted = db.insertNewUsers(name_log.getText().toString(), password_log.getText().toString());
                if(isInserted){
                    Toast.makeText(LoginActivity.this, "Data is inserted sucessfully", Toast.LENGTH_LONG).show();
                }
                else{
                    Toast.makeText(LoginActivity.this, "Data not inserted", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void showMessage(String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }

    public void Login() {
        button_log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cursor data = db.getAllUsers();
                String name_user;
                String password_user;
                data.moveToFirst();
                do {
                    name_user = data.getString(1);
                    password_user = data.getString(2);
                    if (name_log.getText().toString().equals(name_user) && password_log.getText().toString().equals(password_user)) {
                        Intent home = new Intent(LoginActivity.this, HomeActivity.class);
                        startActivity(home);
                        Toast.makeText(LoginActivity.this, "Welcome, " + name_log.getText().toString() + "!", Toast.LENGTH_LONG).show();
                    }
                    else {
                        Toast.makeText(LoginActivity.this, "Authentication failed", Toast.LENGTH_LONG).show();
                    }
                }while(data.moveToNext());
            }
        });
    }
}