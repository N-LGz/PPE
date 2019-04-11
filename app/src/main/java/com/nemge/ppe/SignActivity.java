package com.nemge.ppe;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SignActivity extends AppCompatActivity {

    SQLite db;
    EditText name, firstname, age, mail, password;
    Button register;
    String recup1, recup2, recup3, recup4, recup5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign);

        db = new SQLite(this);

        name = findViewById(R.id.name_entry);
        firstname = findViewById(R.id.firstname_entry);
        age = findViewById(R.id.age_entry);
        mail = findViewById(R.id.mail_sign);
        password = findViewById(R.id.password_sign);
        register = findViewById(R.id.signup_button);

        AddData();
    }

    public void AddData(){
        register.setOnClickListener(v -> {
            try {

                recup1 = name.getText().toString();
                recup2 = firstname.getText().toString();
                recup3 = age.getText().toString();
                recup4 = mail.getText().toString();
                recup5 = password.getText().toString();

                if (recup1.equals("") || recup2.equals("") || recup3.matches("") || recup4.matches("")
                        || recup5.matches(""))
                {
                    Toast.makeText(this, "Vous devez renseigner tous les champs !", Toast.LENGTH_SHORT).show();
                    return;
                }
                else {
                    boolean isInserted = db.insertNewUsers(name.getText().toString(), firstname.getText().toString(), age.getText().toString(),mail.getText().toString(), password.getText().toString());
                    if(isInserted){
                        Toast.makeText(SignActivity.this, "Votre compte a été crée avec succès !", Toast.LENGTH_LONG).show();
                        Cursor data = db.getAllUsers();
                        data.moveToLast();
                        String register = data.getString(1);
                        Intent sub = new Intent(this, MainActivity.class);
                        sub.putExtra("register", register);
                        startActivity(sub);
                    }
                    else{
                        Toast.makeText(SignActivity.this, "Problème lors de l'inscription", Toast.LENGTH_LONG).show();
                    }
                }
                
            }catch(NullPointerException e)
            {

            }
        });
    }
}
