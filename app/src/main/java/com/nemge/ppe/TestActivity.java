package com.nemge.ppe;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.Integer.*;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TestActivity extends AppCompatActivity {

    private static final String FILE_NAME_ONE = "test.txt";
    private static final String FILE_NAME_TWO = "convert.txt";
    Button btnconvert, btnsave, btnload;
    TextView original, changed;
    EditText test;
    String waitingText;
    String[] moreTest = new String[5];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        btnconvert = findViewById(R.id.btnconvert);
        btnload = findViewById(R.id.btnload);
        btnsave = findViewById(R.id.btnsave);
        original = findViewById(R.id.txtoriginal);
        changed = findViewById(R.id.txtchanged);
        test = findViewById(R.id.edittest);

    }

    public int convert(View view) {
        String query = "";

        load(view);
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
        return Integer.parseInt(moreTest[1]);
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

        try {
            fis = openFileInput(FILE_NAME_ONE);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String text;

            int i = 0;

            while ((text = br.readLine()) != null) {
                sb.append(text).append("\n");
                moreTest[i] = text;
                i++;
            }

            test.setText(sb.toString());
            waitingText = sb.toString();

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

}
