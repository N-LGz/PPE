package com.nemge.ppe;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AdminActivity extends AppCompatActivity {
    SQLiteDatabasePPE db;
    EditText name, password;
    Button button_view_all, button_clear_all, button_add, button_del;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        this.setSupportActionBar((Toolbar)this.findViewById(R.id.home_toolbar));
        ActionBar actionbar = this.getSupportActionBar();
        if (actionbar != null) {
            actionbar.setDisplayHomeAsUpEnabled(true);
        }

        db = new SQLiteDatabasePPE(this);
        button_view_all = findViewById(R.id.button_view_data);
        ViewData();
        button_add = findViewById(R.id.button_add_user);
        AddData();
        button_clear_all = findViewById(R.id.button_clear_data);
        clearAllData();
    }

    public void ViewData(){
        button_view_all.setOnClickListener(new View.OnClickListener() {
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

    public void showMessage(String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }

    public void AddData(){
        button_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isInserted = db.insertNewUsers(name.getText().toString(), password.getText().toString());
                if(isInserted){
                    Toast.makeText(AdminActivity.this, "Data is inserted sucessfully", Toast.LENGTH_LONG).show();
                }
                else{
                    Toast.makeText(AdminActivity.this, "Data not inserted", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void clearAllData(){
        button_clear_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.deleteAllUsers();
            }
        });
    }
}
