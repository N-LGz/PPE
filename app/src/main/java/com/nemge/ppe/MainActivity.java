package com.nemge.ppe;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Parcelable;
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
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.nemge.ppe.Database.UserRepository;
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

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, CountFragment.onFragmentInteractionListener, ChartsFragment.onFragmentInteractionListener, BDDFragment.OnFragmentInteractionListener {

    private DrawerLayout mDrawerLayout;
    private NavigationView navView;
    private BottomNavigationView botView;
    private GraphView graphDay, graphMonth, graphYear;
    private TextView show;
    private Button button;

    String str = "200";
    String date = "";

    int tabDay[] = new int[24];
    int tabMonth[] = new int[31];
    int tabYear[] = new int[12];

    int doses = 0;
    int value = 0;

    private final CountFragment count = new CountFragment();;
    private final ChartsFragment charts = new ChartsFragment();
    private final BDDFragment bdd = new BDDFragment();
    final FragmentManager fm = getSupportFragmentManager();
    Fragment active = count;

    private ListView lstUsers;
    private FloatingActionButton fab, fabchart;
    private TextView txtv;

    List<User> userList = new ArrayList<>();
    ArrayAdapter adapter;

    private CompositeDisposable compositeDisposable;
    private UserRepository userRepository;



    private LineGraphSeries SeriesDay, SeriesMonth, SeriesYear;


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
        if (intent != null) {
            if (intent.hasExtra("doses")) {
                str = intent.getStringExtra("doses");
                date = intent.getStringExtra("date");
            }
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

        txtv.setText(Integer.toString(result));
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
            case R.id.update:
                AddData();

                SeriesDay = new LineGraphSeries<>(generateDataDay(tabDay));
                SeriesDay.setTitle("Today");

                SeriesMonth = new LineGraphSeries<>(generateDataMonth(tabMonth));
                SeriesMonth.setTitle("Mois");

                //SeriesYear = new LineGraphSeries<>(generateDataYear(tabYear));
                //SeriesYear.setTitle("Year");

                graphDay.removeAllSeries();
                graphDay.addSeries(SeriesDay);
                graphDay.getLegendRenderer().setVisible(true);

                graphMonth.removeAllSeries();
                graphMonth.addSeries(SeriesMonth);
                graphMonth.getLegendRenderer().setVisible(true);
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
            case R.id.nav_settings:
                Intent intentSettings = new Intent(this, SettingsActivity.class);
                startActivity(intentSettings);
                break;
            case R.id.nav_bluetooth:
                Intent intentBT = new Intent(this, BluetoothDevicesActivity.class);
                startActivity(intentBT);
                break;
            case R.id.nav_disconnect:
                Intent intentLog = new Intent(this, LoginActivity.class);
                startActivity(intentLog);
                break;
            case R.id.nav_test:
                Intent intent = new Intent(this, TestActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_test_bdd:
                Intent intentBDD = new Intent(this, TestBDD.class);
                startActivity(intentBDD);
                break;
            case R.id.nav_clear:
                deleteAllUsers();
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

    private Boolean updateMainFragment(Integer integer){
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

    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    public void discount() {
        show.setText(str);
        doses = Integer.parseInt(str);
        button.setOnClickListener(v -> {
            doses--;
            value++;
            //Update();
            if(doses>=0){
                show.setText(String.valueOf(doses));
            }
        });
    }

    public void AddData(){
        /*int Ad, Md, Dd;
        String m, j, h;
        /// Ajout des mois au tableau de l'année
        for(int i = 0; i<tabYear.length; i++)
        {
            if(i<10)
            {
                m = "0" + String.valueOf(i);
            }
            else
            {
                m = String.valueOf(i);
            }
            String tabM[] = {"2019",m};
            Ad = convertMonth(tabM);
            tabYear[i] = Ad;
        }
        ///Ajout des jours au tableau du mois
        for(int i = 0; i<tabMonth.length; i++)
        {
            if(i<10)
            {
                j = "0" + String.valueOf(i);

            }
            else
            {
                j = String.valueOf(i);

            }
            String tabJ[] = {"2019","04",j};
            Md = convertMonth(tabJ);
            tabMonth[i] = Md;
        }
        ///Ajout des heures au tableau des jours
        for(int i = 0; i<tabDay.length; i++)
        {
            if(i<10)
            {
                h = "0" + String.valueOf(i);

            }
            else
            {
                h = String.valueOf(i);

            }
            String tabH[] = {"2019","04","08",h};
            Dd = convertMonth(tabH);
            tabDay[i] = Dd;
        }*/

        testDay();
        testMonth();
        //testYear();
    }

    public void testDay()
    {
        for(int i = 0; i<tabDay.length; i++)
        {
            tabDay[i] = 0;
        }
        android.database.Cursor c = UserDatabase.getInstance(this).query("SELECT count(name), strftime('%H', name) FROM users WHERE date(name, 'start of day') = '2018-04-08' GROUP BY strftime('%H', name)", new Object[]{});
        while(c.moveToNext()) {
            tabDay[Integer.parseInt(c.getString(1))] = c.getShort(0);
        }
    }

    public void testMonth(){
        for(int i = 0; i<tabMonth.length; i++)
        {
            tabMonth[i] = 0;
        }
        android.database.Cursor d = UserDatabase.getInstance(this).query("SELECT count(name), strftime('%m'" +//%H formateur pour indiquer l'heure Ex: %m pour mois
                ", name) FROM users WHERE date(name, 'start of month')" +//start of day ex: start of month
                " = '2018-04-01' GROUP BY strftime('%m', name)", new Object[]{});
        while(d.moveToNext()) {
            tabMonth[Integer.parseInt(d.getString(1))] = d.getShort(0);
        }
    }

    public void testYear(){
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

    public DataPoint[] generateDataWeek(int[] tab) {
        int count = 7;
        DataPoint[] values = new DataPoint[count];
        for (int i=0; i<count; i++) {
            double x = i;
            double y = tab[i];
            DataPoint v = new DataPoint(x, y);
            values[i] = v;
        }
        return values;
    }

    public DataPoint[] generateDataMonth(int[] tab) {
        int count = 31;
        DataPoint[] values = new DataPoint[count];
        for (int i=0; i<count; i++) {
            int y = tab[i];
            DataPoint v = new DataPoint(i, y);
            values[i] = v;
        }
        return values;
    }

    private DataPoint[] generateDataYear() {
        int count = 12;
        DataPoint[] values = new DataPoint[count];
        for (int i=0; i<count; i++) {
            double x = i;
            double y = Math.sin(i*2);
            DataPoint v = new DataPoint(x, y);
            values[i] = v;
        }
        return values;
    }

    public void sendID() {

        graphDay = findViewById(R.id.graphDay);
        graphMonth = findViewById(R.id.graphMonth);
        graphYear = findViewById(R.id.graphYear);
    }

    public void sendID2() {

        show = findViewById(R.id.title_count);
        button = findViewById(R.id.button_count);
        discount();
        //Update();
    }

    @Override
    public void sendID3() {

        lstUsers = findViewById(R.id.lstUsers);
        fab = findViewById(R.id.fab);
        txtv = findViewById(R.id.txtv);
        fabchart = findViewById(R.id.fabchart);
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, userList);
        registerForContextMenu(lstUsers);
        lstUsers.setAdapter(adapter);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Disposable disposable = (Disposable) io.reactivex.Observable.create(new ObservableOnSubscribe<Object>()
                {
                    @Override
                    public void subscribe(ObservableEmitter<Object> e) throws Exception
                    {
                        User user = new User("2018-06-15 02:20:45");
                        userRepository.insertUser(user);
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
        });

        fabchart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tabFromTo[] = {"2018", "04", "08", "12"};
                int doses = convertMonth(tabFromTo);
            }
        });

    }
}
