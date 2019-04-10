package com.nemge.ppe;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Test extends AppCompatActivity {

    private static final String FILE_NAME_ONE = "test.txt";

    TextView original, changed, result;
    Button btnconvert;
    EditText test;

    String waitingText;
    String[] moreTest = new String[5];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        setSupportActionBar(findViewById(R.id.home_toolbar));
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);

        btnconvert = findViewById(R.id.btnconvert);
        original = findViewById(R.id.txtoriginal);
        changed = findViewById(R.id.txtchanged);
        test = findViewById(R.id.edittest);
        result = findViewById(R.id.resultConvert);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permission granted", Toast.LENGTH_LONG);
        }
        else {
            Toast.makeText(this, "Permission not granted", Toast.LENGTH_LONG);
        }
    }

    public String LoadFile(View view){
        String path = Environment.getExternalStorageDirectory().toString()+"/bluetooth"+ File.separator + "test.txt";
        File file = new File(path);
        StringBuilder sb = new StringBuilder();

        if (file.isFile() && file.getPath().endsWith(".txt")) {
            try {
                BufferedReader br = new BufferedReader(new FileReader(file));
                String line;
                int i = 0;
                while((line = br.readLine()) != null) {
                    sb.append(line);
                    moreTest[i] = line;
                    i++;
                }
                test.setText(sb.toString());
                waitingText = sb.toString();
                br.close();
                file.delete();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            sb.append("LUL");
        }
        return sb.toString();
    }



    public String convert(View view) {

        String query = "";

        //J'ai virer le loadfile pour tester plus simplement
        moreTest[0] = test.getText().toString();


        original.setText(moreTest[0]);
        String[] arrayOfString = moreTest[0].split(" ", 0);
        String[] year = arrayOfString[3].split(",", 0);
        query = query + year[0] + "-";//Year

        Date date = null;//put your month name here
        try {
            date = new SimpleDateFormat("MMM", Locale.FRENCH).parse(arrayOfString[2]);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int monthNumber=cal.get(Calendar.MONTH);
        String interMonth = Integer.toString(monthNumber);//A AJOUTER
        if(interMonth.length() == 1) {
            query = query + "0" + interMonth + "-";//Month//A CHANGER
        }
        else {
            query = query + interMonth + "-";//Month//A CHANGER
        }

        if(arrayOfString[1].length() == 1) {
            query = query + "0" + arrayOfString[1] + " ";//Day
        }
        else {
            query = query + arrayOfString[1] + " ";//Day//A CHANGER
        }
        query = query + arrayOfString[4];//hh:mm:ss
        changed.setText(query);

        return query;
    }
}
