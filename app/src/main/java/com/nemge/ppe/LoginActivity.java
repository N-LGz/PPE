package com.nemge.ppe;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {
    SQLiteDatabasePPE db;
    EditText name_log, password_log;
    Button button_log, button_register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        db = new SQLiteDatabasePPE(this);

        name_log = findViewById(R.id.name_login);
        password_log = findViewById(R.id.password_login);
        button_log = findViewById(R.id.button_login);
        Login();

        button_register = findViewById(R.id.button_sign);
        NewAccount();
    }

    public void Login() {
        button_log.setOnClickListener(v -> {
            Cursor data = db.getAllUsers();
            String name_user;
            String password_user;
            data.moveToFirst();
            do {
                name_user = data.getString(1);
                password_user = data.getString(5);
                if (name_log.getText().toString().equals(name_user) && password_log.getText().toString().equals(password_user)) {
                    Intent main = new Intent(LoginActivity.this, MainActivity.class);
                    main.putExtra("name", name_user);
                    startActivity(main);
                    Toast.makeText(LoginActivity.this, "Welcome, " + name_log.getText().toString() + "!", Toast.LENGTH_LONG).show();
                }
            }while(data.moveToNext());
        });
    }

    public void NewAccount(){
        button_register.setOnClickListener(v ->{
            Intent signup = new Intent(this, SignActivity.class);
            startActivity(signup);
        });
    }
}