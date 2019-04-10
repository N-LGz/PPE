package com.nemge.ppe;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LabelFormatter;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.nemge.ppe.Database.UserRepository;
/*import com.nemge.ppe.Local.UserDAO;
import com.nemge.ppe.Local.UserDAO_Impl;*/
import com.nemge.ppe.Local.UserDAO;

import com.nemge.ppe.Local.UserDataSource;
import com.nemge.ppe.Local.UserDatabase;
import com.nemge.ppe.Model.User;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, CountFragment.onFragmentInteractionListener, ChartsFragment.onFragmentInteractionListener, BDDFragment.OnFragmentInteractionListener {

    private DrawerLayout mDrawerLayout;
    private NavigationView navView;
    private BottomNavigationView botView;
    private GraphView graph;
    private TextView show;
    private RadioButton buttonDay, buttonMonth, buttonYear;

    String str = "";
    String date = "";
    String date_convertie="";
    String doses = "";
    String file = "";

    String waitingText;
    String[] moreTest = new String[5];

    int tabDay[] = new int[24];
    int tabMonth[] = new int[12];
    int tabYear[] = new int[2];

    private static final String FILE_NAME_ONE = "test.txt";

    private final CountFragment count = new CountFragment();;
    private final ChartsFragment charts = new ChartsFragment();
    private final BDDFragment bdd = new BDDFragment();
    final FragmentManager fm = getSupportFragmentManager();
    Fragment active = count;

    private ListView lstUsers;
    List<User> userList = new ArrayList<>();
    ArrayAdapter adapter;

    private CompositeDisposable compositeDisposable;
    private UserRepository userRepository;

    private LineGraphSeries SeriesDay, SeriesMonth;
    private BarGraphSeries SeriesYear;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setSupportActionBar(findViewById(R.id.home_toolbar));
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);

        fm.beginTransaction().add(R.id.fragment_container, bdd, "3").hide(bdd).commit();
        fm.beginTransaction().add(R.id.fragment_container, charts, "2").hide(charts).commit();
        fm.beginTransaction().add(R.id.fragment_container, count, "1").commit();

        mDrawerLayout =  findViewById(R.id.drawer_layout);

        navView = findViewById(R.id.navigation_view);
        navView.setNavigationItemSelectedListener(this);

        botView = findViewById(R.id.navigation);
        configureBottomView();

        compositeDisposable = new CompositeDisposable();

        UserDatabase userDatabase = UserDatabase.getInstance(this);//Create database
        userRepository = UserRepository.getInstance(UserDataSource.getInstance(userDatabase.userDAO()));

        Intent intent = getIntent();
        if (intent != null){
            if (intent.hasExtra("doses") && intent.hasExtra("date") ){
                str = intent.getStringExtra("doses");
                date = intent.getStringExtra ("date");
            }
        }
    }

    /*
    public String[] KeepDate(String date[])
    {
        String date_convert[] = {"","",""};
        for(int i=0; i<date.length-1; i++)
        {

        }
        return date_convert;
    }
    */

    public void Dose()
    {
        try {

            ///1ère étape: réception et traitement du fichier
            file = LoadFile();
            SaveFile(file);
            date = convertDate();
            doses = String.valueOf(convertDoses());

            //2ème étape: ajout de la date à la BDD
            AddToBDD();
            AddData();

            //3ème étape: mise à jour des graphes et de l'afficheur
            show.setText(doses);

            SeriesDay = new LineGraphSeries<>(generateDataDay(tabDay));
            SeriesDay.setTitle("Aujourd'hui");

            SeriesMonth = new LineGraphSeries<>(generateDataMonth(tabMonth));
            SeriesMonth.setTitle("Ce mois-ci");

            SeriesYear = new BarGraphSeries<>(generateDataYear(tabYear));
            SeriesYear.setTitle("Cette année");

        } catch(NullPointerException e){
            Toast.makeText(MainActivity.this, "ERROR : no file found!", Toast.LENGTH_LONG).show();
        }

    }

    public int convertMonth(String[] tabFromTo) {

        int result = 0;

        Object[] inter = userList.toArray();
        String tabMonth[] = new String[userList.size()];

        //txtv.setText(inter[0].toString());

        for (int i = 0; i < userList.size(); i++){
            tabMonth[i] = inter[i].toString();
        }

        //txtv.setText(tabMonth[0]);

        //String[] date = tabMonth[0].split("-", 0);
        //txtv.setText(date[2]);

        for (int i = 0; i<tabMonth.length; i++) {
            String[] date = tabMonth[i].split("-", 0);
            String[] day = date[2].split(" ", 0);
            String[] time = day[1].split(":", 0);


            switch (tabFromTo.length) {
                case 2 :
                    if (date[0].equals(tabFromTo[0]) && date[1].equals(tabFromTo[1])){
                        result++;
                    }
                    break;
                case 3 :
                    if (date[0].equals(tabFromTo[0]) && date[1].equals(tabFromTo[1]) && day[0].equals(tabFromTo[2])){
                        result++;
                    }
                    break;
                case 4 :
                    if (date[0].equals(tabFromTo[0]) && date[1].equals(tabFromTo[1]) && day[0].equals(tabFromTo[2])
                            && time[0].equals(tabFromTo[3])) {
                        result++;
                    }
                    break;
                default:
                    Toast.makeText(MainActivity.this, "Taille du tableau non conforme", Toast.LENGTH_SHORT).show();
            }
        }
        return result;

    }

    private void deleteUser(User user) {

        Disposable disposable = (Disposable) io.reactivex.Observable.create(new ObservableOnSubscribe<Object>()
        {
            @Override
            public void subscribe(ObservableEmitter<Object> e) throws Exception
            {
                userRepository.deleteUser(user);
                e.onComplete();
            }
        })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer() {
                    @Override
                    public void accept(Object o) throws Exception {
                        //Toast.makeText(TestBDD.this, "User added !", Toast.LENGTH_SHORT).show();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(MainActivity.this, "" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {
                        loadData();//Refresh data
                    }
                });
        compositeDisposable.add(disposable);

    }

    private void updateUser(User user) {

        Disposable disposable = (Disposable) io.reactivex.Observable.create(new ObservableOnSubscribe<Object>()
        {
            @Override
            public void subscribe(ObservableEmitter<Object> e) throws Exception
            {
                userRepository.updateUser(user);
                e.onComplete();
            }
        })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer() {
                    @Override
                    public void accept(Object o) throws Exception {
                        //Toast.makeText(TestBDD.this, "User added !", Toast.LENGTH_SHORT).show();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(MainActivity.this, "" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {
                        loadData();//Refresh data
                    }
                });
        compositeDisposable.add(disposable);

    }

    private void loadData() {

        Disposable disposable = userRepository.getAllUsers()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<List<User>>() {
                    @Override
                    public void accept(List<User> users) throws Exception {
                        Toast.makeText(MainActivity.this, "LoadData", Toast.LENGTH_SHORT).show();
                        onGetAllUserSuccess(users);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(MainActivity.this, ""+throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        compositeDisposable.add(disposable);

    }

    private void onGetAllUserSuccess(List<User> users) {

        userList.clear();
        userList.addAll(users);
        adapter.notifyDataSetChanged();

    }

    private void deleteAllUsers() {

        Disposable disposable = (Disposable) io.reactivex.Observable.create(new ObservableOnSubscribe<Object>()
        {
            @Override
            public void subscribe(ObservableEmitter<Object> e) throws Exception
            {
                userRepository.deleteAllUsers();
                e.onComplete();
            }
        })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer()
                {
                    @Override
                    public void accept(Object o) throws Exception
                    {
                        //Toast.makeText(TestBDD.this, "User added !", Toast.LENGTH_SHORT).show();
                    }
                }, new Consumer<Throwable>()
                {
                    @Override
                    public void accept(Throwable throwable) throws Exception
                    {
                        Toast.makeText(MainActivity.this, "" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }, new Action()
                {
                    @Override
                    public void run() throws Exception {
                        loadData();//Refresh data
                    }
                });
        compositeDisposable.add(disposable);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.nav_refresh:
                show.setText("200");
                break;
            case R.id.nav_update:
                Dose();
                break;
            case R.id.nav_clear:
                deleteAllUsers();
                break;
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.update, menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo menuInfo) {

        super.onCreateContextMenu(contextMenu, view, menuInfo);
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        contextMenu.setHeaderTitle("Select action:");
        contextMenu.add(Menu.NONE, 0, Menu.NONE, "Update");
        contextMenu.add(Menu.NONE, 1, Menu.NONE, "Delete");

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        final User user = userList.get(info.position);
        switch (item.getItemId()) {
            case 0: //Update
            {
                EditText edtName = new EditText(MainActivity.this);
                edtName.setText(user.getName());
                edtName.setHint("Enter your name:");
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Edit")
                        .setMessage("Edit user name")
                        .setView(edtName)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (TextUtils.isEmpty(edtName.getText().toString())) {
                                    return;
                                }
                                else {
                                    user.setName(edtName.getText().toString());
                                    updateUser(user);
                                }
                            }
                        }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create().show();
            }
            break;
            case 1 ://Delete
            {
                new AlertDialog.Builder(MainActivity.this)
                        .setMessage("Do you want to delete" + user.toString())
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteUser(user);
                            }
                        }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create().show();
            }
            break;
        }
        return true;

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        switch (menuItem.getItemId()) {
            case R.id.nav_profile:
                Intent intentProfile = new Intent(this, ProfileActivity.class);
                startActivity(intentProfile);
                break;
            case R.id.nav_tutorial:
                Intent intentTutorial = new Intent(this, TutorialActivity.class);
                startActivity(intentTutorial);
                break;
            case R.id.nav_notifications:
                Intent intentNotifications = new Intent(this, NotificationsActivity.class);
                startActivity(intentNotifications);
                break;
            case R.id.nav_bluetooth:
                Intent intentBT = new Intent(this, BluetoothDevicesActivity.class);
                startActivity(intentBT);
                break;
            case R.id.nav_admin:
                Intent intentAdmin = new Intent(this, AdminActivity.class);
                startActivity(intentAdmin);
                break;
            case R.id.nav_disconnect:
                Intent intentLog = new Intent(this, LoginActivity.class);
                startActivity(intentLog);
                break;
            case R.id.nav_test:
                Intent intentTest = new Intent(this, Test.class);
                startActivity(intentTest);
                break;
        }
        this.mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;

    }

    private void configureBottomView(){

        botView.setOnNavigationItemSelectedListener(item -> updateMainFragment(item.getItemId()));

    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        compositeDisposable.clear();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {

        super.onRestoreInstanceState(savedInstanceState);
        // Restauration des données du contexte utilisateur
        show.setText(savedInstanceState.getString(str));

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);
        // Sauvegarde des données du contexte utilisateur
        outState.putString(str, show.getText().toString());

    }

    private Boolean updateMainFragment(Integer integer) {

        switch (integer) {
            case R.id.menu_doses:
                fm.beginTransaction().hide(active).show(count).commit();
                active = count;
                return true;

            case R.id.menu_charts:
                fm.beginTransaction().hide(active).show(charts).commit();
                active = charts;
                return true;

            case R.id.menu_bdd:
                fm.beginTransaction().hide(active).show(bdd).commit();
                active = bdd;
                return true;
        }
        return false;

    }

    public void AddData() {

        testDay();
        testMonth();
        //testYear();

    }

    public void testDay() {
        for(int i = 0; i<tabDay.length; i++)
        {
            tabDay[i] = 0;
        }
        android.database.Cursor c = UserDatabase.getInstance(this).query("SELECT count(name), strftime('%H', name) FROM users WHERE date(name, 'start of day') = '2019-02-11' GROUP BY strftime('%H', name)", new Object[]{});
        while(c.moveToNext()) {
            tabDay[Integer.parseInt(c.getString(1))] = c.getShort(0);
        }
    }

    public void testMonth() {

        int doses = 0;
        int S =0;

        for(int i = 0; i<tabMonth.length; i++)
        {
            tabMonth[i] = 0;
        }
        android.database.Cursor d = UserDatabase.getInstance(this).query("SELECT count(name), strftime('%m'" +//%H formateur pour indiquer l'heure Ex: %M pour mois
                ", name) FROM users WHERE date(name, 'start of year')" +//start of day ex: start of month
                " = '2019-01-01' GROUP BY strftime('%m', name)", new Object[]{});
        while(d.moveToNext()) {
            tabMonth[Integer.parseInt(d.getString(1))] = d.getShort(0);
        }

        for(int i = 0; i<tabYear.length; i++)
        {
            tabYear[i] = 0;
        }
        android.database.Cursor e = UserDatabase.getInstance(this).query("SELECT count(name), strftime('%m'" +//%H formateur pour indiquer l'heure Ex: %M pour mois
                ", name) FROM users WHERE date(name, 'start of year')" +//start of day ex: start of month
                " = '2019-01-01' GROUP BY strftime('%m', name)", new Object[]{});
        while(e.moveToNext()) {
            doses = e.getShort(0);
            S = doses + S;
            tabYear[0] = S;
        }
    }

    public void testYear() {
        for(int i = 0; i<tabYear.length; i++)
        {
            tabYear[i] = 0;
        }
        android.database.Cursor d = UserDatabase.getInstance(this).query("SELECT count(name), strftime('%Y'" +//%H formateur pour indiquer l'heure Ex: %m pour mois
                ", name) FROM users WHERE date(name, 'start of year')" +//start of day ex: start of month
                " = '2018-01-01' GROUP BY strftime('%Y', name)", new Object[]{});
        while(d.moveToNext()) {
            tabYear[Integer.parseInt(d.getString(1))] = d.getShort(0);
        }
    }

    public DataPoint[] generateDataDay(int[] tab) {
        int count = 24;
        DataPoint[] values = new DataPoint[count];
        for (int i=0; i<count; i++) {
            int y = tab[i];
            DataPoint v = new DataPoint(i, y);
            values[i] = v;
        }
        return values;
    }

    public DataPoint[] generateDataMonth(int[] tab) {
        int count = 12;
        DataPoint[] values = new DataPoint[count];
        for (int i=0; i<count; i++) {
            int y = tab[i];
            DataPoint v = new DataPoint(i, y);
            values[i] = v;
        }
        return values;
    }

    private DataPoint[] generateDataYear(int[] tab) {
        int count = 2;
        DataPoint[] values = new DataPoint[count];
        for (int i=0; i<count; i++) {
            double y = tab[i];
            DataPoint v = new DataPoint(i, y);
            values[i] = v;
        }
        return values;
    }

    public String LoadFile() {

        String path = Environment.getExternalStorageDirectory().toString()+"/bluetooth"+ File.separator + "test.txt";
        File file = new File(path);
        StringBuilder sb = new StringBuilder();

        if (file.isFile() && file.getPath().endsWith(".txt")) {
            try {
                BufferedReader br = new BufferedReader(new FileReader(file));
                String line;
                int i = 0;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                    moreTest[i] = line;
                    i++;
                }
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

    public void SaveFile(String s) {

        String text = s;
        FileOutputStream fos = null;

        try {
            fos = openFileOutput(FILE_NAME_ONE, MODE_PRIVATE);
            fos.write(text.getBytes());
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

    public int convertDoses() {

        String query = "";
        int a;
        LoadFile();
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
        if(arrayOfString[1].length() == 1) {
            query = query + "0" + Integer.toString(monthNumber) + "-";//Month
        }
        else {
            query = query + Integer.toString(monthNumber) + "-";//Month
        }
        query = query + Integer.toString(monthNumber) + "-";//Month

        if(arrayOfString[1].length() == 1) {
            query = query + "0" + arrayOfString[1] + " ";//Day
        }
        else {
            query = query + arrayOfString[1];//Day
        }
        query = query + arrayOfString[4];//hh:mm:ss

        //On renvoie le nombre de doses qu'il reste, envoyé par la raspberry

        a = Integer.parseInt(moreTest[1]);
        return a;

    }

    public String convertDate() {

        String query = "";
        int a = 0;
        LoadFile();
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
        if(arrayOfString[1].length() == 1) {
            query = query + "0" + Integer.toString(monthNumber) + "-";//Month
        }
        else {
            query = query + Integer.toString(monthNumber) + "-";//Month
        }
        //query = query + Integer.toString(monthNumber) + "-";//Month

        if(arrayOfString[1].length() == 1) {
            query = query + "0" + arrayOfString[1] + " ";//Day
        }
        else {
            query = query + arrayOfString[1];//Day
        }
        query = query + arrayOfString[4];//hh:mm:ss
        //On renvoit le nombre de doses qu'il reste, envoyé par la raspberry
        a = Integer.parseInt(moreTest[1]);
        return query;

    }

    public void AddToBDD(){

        Disposable disposable = (Disposable) io.reactivex.Observable.create(new ObservableOnSubscribe<Object>()
        {
            @Override
            public void subscribe(ObservableEmitter<Object> e) throws Exception
            {
                //User user = new User("2018-03-08 10:20:45");
                userRepository.insertUser(new User(date));
                e.onComplete();
            }
        })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer() {
                    @Override
                    public void accept(Object o) throws Exception {
                        Toast.makeText(MainActivity.this, "User added !", Toast.LENGTH_SHORT).show();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(MainActivity.this, "" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {
                        loadData();//Refresh data
                    }
                });

    }

    @Override
    public void sendID() {

        graph = findViewById(R.id.graph);
        buttonDay = findViewById(R.id.button_day);
        buttonMonth = findViewById(R.id.button_month);
        buttonYear = findViewById(R.id.button_year);

        buttonDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    graph.removeAllSeries();
                    graph.addSeries(SeriesDay);
                    graph.getLegendRenderer().setVisible(true);

                } catch(NullPointerException e)
                {

                }
            }
        });

        buttonMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    graph.removeAllSeries();
                    graph.addSeries(SeriesMonth);
                    graph.getLegendRenderer().setVisible(true);

                } catch (NullPointerException e) {

                }
            }
        });

        buttonYear.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                try {

                    graph.removeAllSeries();
                    graph.addSeries(SeriesYear);
                    graph.getLegendRenderer().setVisible(true);

                } catch (NullPointerException e) {

                }
            }
        });

    }

    @Override
    public void sendID2() {

        show = findViewById(R.id.title_count);
        show.setText("???");

    }

    @Override
    public void sendID3() {

        lstUsers = findViewById(R.id.lstUsers);
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, userList);
        registerForContextMenu(lstUsers);
        lstUsers.setAdapter(adapter);

    }
}
