package com.nemge.ppe;

import android.content.Intent;
import android.content.pm.PackageManager;
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

public class TestActivity extends AppCompatActivity {

    private static final String FILE_NAME_ONE = "test.txt";

    TextView original, changed, result;
    Button btnconvert, btnsave, btnload;

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
        btnload = findViewById(R.id.btnload);
        btnsave = findViewById(R.id.btnsave);
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

    public void save(View view) {

        String text = test.getText().toString();
        FileOutputStream fos = null;

        try {
            fos = openFileOutput(FILE_NAME_ONE, MODE_PRIVATE);
            fos.write(text.getBytes());

            test.getText().clear();
            Toast.makeText(this, "Saved to " + getFilesDir() + "/" + FILE_NAME_ONE, Toast.LENGTH_LONG).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public int convert(View view) {

        String query = "";
        int a = 0;
        LoadFile(view);
        original.setText(waitingText);
        //changed.setText(moreTest[0]);
        //changed.setText(moreTest[1]);
        String[] arrayOfString = moreTest[0].split(" ", 0);
        //changed.setText(arrayOfString[0]);
        String[] year = arrayOfString[3].split(",", 0);
        query = query + year[0] + "-";
        Date date = null;//put your month name here
        try {
            date = new SimpleDateFormat("MMM", Locale.FRENCH).parse(arrayOfString[2]);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int monthNumber=cal.get(Calendar.MONTH);

        query = query + Integer.toString(monthNumber) + "-";
        if(arrayOfString[1].length() == 1) {
            query = query + "0" + arrayOfString[1] + " ";
        }
        else {
            query = query + arrayOfString[1];
        }
        query = query + arrayOfString[4];
        changed.setText(query);
        //On renvoit le nombre de doses qu'il reste, envoyé par la raspberry
        a = Integer.parseInt(moreTest[1]);
        result.setText(String.valueOf(a));
        return a;
    }

    public void Send(View view){
        Intent intent = new Intent(this, TestBDD.class);
        intent.putExtra("date", changed.getText());
        startActivity(intent);
    }

}

