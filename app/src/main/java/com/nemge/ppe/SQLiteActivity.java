package com.nemge.ppe;

import android.database.Cursor;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SQLiteActivity extends AppCompatActivity {
    SQLiteDatabasePPE db;
    EditText nameInput, passwordInput;
    Button button_add, button_show, button_clear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sqlite);

        db = new SQLiteDatabasePPE(this);

        nameInput = findViewById(R.id.Name_EditText);
        passwordInput= findViewById(R.id.Password_EditText);
        button_add = findViewById(R.id.Data_Button);
        AddData();
        button_show = findViewById(R.id.Show_Button);
        ViewData();
        button_clear = findViewById(R.id.Clear_Button);
        clearAllData();
    }

    public void AddData(){
        button_add.setOnClickListener(v -> {
            boolean isInserted = db.insertNewUsers(nameInput.getText().toString(), passwordInput.getText().toString());
            if(isInserted){
                Toast.makeText(SQLiteActivity.this, "Data is inserted sucessfully", Toast.LENGTH_LONG).show();
            }
            else{
                Toast.makeText(SQLiteActivity.this, "Data not inserted", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void ViewData(){
        button_show.setOnClickListener(v -> {
            Cursor data = db.getAllUsers();
            if(data.getCount()==0){
                showMessage("Data", "No data found");
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
        });
    }

    public void clearAllData(){
        button_clear.setOnClickListener(v -> db.deleteAllUsers());
    }

    public void showMessage(String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }
}
