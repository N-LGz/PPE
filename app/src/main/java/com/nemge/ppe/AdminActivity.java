package com.nemge.ppe;

import android.database.Cursor;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AdminActivity extends AppCompatActivity {
    SQLite db;
    EditText name, password;
    Button button_view_all, button_clear_all, button_add, button_del;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        setSupportActionBar(findViewById(R.id.home_toolbar));
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);

        db = new SQLite(this);
        name = findViewById(R.id.enter_name2);
        password = findViewById(R.id.enter_password1);

        button_view_all = findViewById(R.id.button_view_data);
        ViewData();

        button_add = findViewById(R.id.button_add_user);
        //AddData();

        button_clear_all = findViewById(R.id.button_clear_data);
        clearAllData();
    }

    public void ViewData(){
        button_view_all.setOnClickListener(v -> {
            Cursor data = db.getAllUsers();
            if(data.getCount()==0){
                return;
            }
            else{
                StringBuffer buffer = new StringBuffer();
                while(data.moveToNext()){
                    buffer.append("ID : " + data.getString(0)+ "\n ");
                    buffer.append("NAME : " + data.getString(1)+ "\n ");
                    buffer.append("FIRSTNAME : " + data.getString(2)+ "\n ");
                    buffer.append("AGE : " + data.getString(3)+ "\n ");
                    buffer.append("EMAIL : " + data.getString(4)+ "\n ");
                    buffer.append("PASSWORD : " + data.getString(5)+ "\n ");
                }
                showMessage("Data", buffer.toString());
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

    /*public void AddData(){
        button_add.setOnClickListener(v -> {
            boolean isInserted = db.insertNewUsers(name.getText().toString(), password.getText().toString());
            if(isInserted){
                Toast.makeText(AdminActivity.this, "Data is inserted sucessfully", Toast.LENGTH_LONG).show();
            }
            else{
                Toast.makeText(AdminActivity.this, "Data not inserted", Toast.LENGTH_LONG).show();
            }
        });
    }*/

    public void clearAllData(){
        button_clear_all.setOnClickListener(v -> db.deleteAllUsers());
    }
}
