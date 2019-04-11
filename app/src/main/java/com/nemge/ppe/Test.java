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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Test extends AppCompatActivity {

    private static final String FILE_NAME_ONE = "test.txt";

    TextView original, changed, result;
    Button btnconvert, btnload, btnsave;
    EditText test;

    ArrayList<String> arrayDate = new ArrayList<String>();
    ArrayList<String> arrayDose = new ArrayList<String>();

    int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        setSupportActionBar(findViewById(R.id.home_toolbar));
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);

        btnconvert = findViewById(R.id.btnconvert);
        btnsave = findViewById(R.id.btnsave);
        btnload = findViewById(R.id.btnload);
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

    public void convertHub(View view) {

        load(view);
        int dose = 0;
        ArrayList<String> arrayQuery = new ArrayList<String>();

        for (int j = 0; j<arrayDose.size(); j++){
            dose = Integer.parseInt(arrayDose.get(j));
            arrayQuery.add(convertDate(arrayDate.get(j)));
        }

        try {
            original.setText(String.valueOf(dose));
            changed.setText(arrayQuery.get(arrayQuery.size()-1));
        } catch (ArrayIndexOutOfBoundsException e)
        {

        }


        /*original.setText(String.valueOf(i/2));
        changed.setText(String.valueOf(arrayDate.size()));
        result.setText(String.valueOf(arrayDose.size()));*/
    }

    public void save(View view) {
        String text = test.getText().toString();
        FileOutputStream fos = null;

        try {
            fos = openFileOutput(FILE_NAME_ONE, MODE_PRIVATE);
            fos.write(text.getBytes());

            test.getText().clear();
            Toast.makeText(this,"Saved to " + getFilesDir() + "/" + FILE_NAME_ONE, Toast.LENGTH_LONG ).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void load(View view) {
        FileInputStream fis = null;
        String path = Environment.getExternalStorageDirectory().toString()+"/bluetooth"+ File.separator + FILE_NAME_ONE;
        File file = new File(path);

        try {
            /*fis = openFileInput(FILE_NAME_ONE);
            InputStreamReader isr = new InputStreamReader(fis);*/
            BufferedReader br = new BufferedReader(new FileReader(file));
            StringBuilder sb = new StringBuilder();
            String text;
            i = 0;

            while ((text = br.readLine()) != null) {
                sb.append(text).append("\n");
                if (i%2 == 0){
                    arrayDate.add(text);
                }
                else {
                    arrayDose.add(text);
                }
                i++;
            }
            test.setText(sb.toString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if(fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public int convertDoses(String dose) {

        return Integer.parseInt(dose);
    }

    public String convertDate(String date) {

        String query = "";
        String[] arrayOfString = date.split(" ", 0);
        String[] year = arrayOfString[3].split(",", 0);
        query = query + year[0] + "-";//Year

        Date month = null;//put your month name here
        try {
            month = new SimpleDateFormat("MMM", Locale.FRENCH).parse(arrayOfString[2]);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(month);
        int monthNumber=cal.get(Calendar.MONTH);
        String interMonth = Integer.toString(monthNumber);//A AJOUTER
        if(interMonth.length() == 1) {
            query = query + "0" + interMonth + "-";//Month//A CHANGER
        }
        else {
            query = query + interMonth + "-";//Month//A CHANGER
        }
        //query = query + Integer.toString(monthNumber) + "-";//Month

        if(arrayOfString[1].length() == 1) {
            query = query + "0" + arrayOfString[1] + " ";//Day
        }
        else {
            query = query + arrayOfString[1] + " ";//Day//A CHANGER
        }
        query = query + arrayOfString[4];//hh:mm:ss

        return query;
    }
}
