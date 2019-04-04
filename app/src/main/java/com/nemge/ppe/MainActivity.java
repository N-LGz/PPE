package com.nemge.ppe;

import android.content.Intent;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, CountFragment.onFragmentInteractionListener, ChartsFragment.onFragmentInteractionListener {

    private DrawerLayout mDrawerLayout;
    private NavigationView navView;
    private BottomNavigationView botView;
    private Button day, week, month, year, prev, next;
    private GraphView graph;
    private TextView show;
    private Button button;

    String str = "200";
    int doses = 0;
    int value = 0;
    private int NumGraph = 1;

    private final CountFragment count = new CountFragment();;
    private final ChartsFragment charts = new ChartsFragment();
    final FragmentManager fm = getSupportFragmentManager();
    Fragment active = count;



    private BarGraphSeries SeriesDay, SeriesWeek, SeriesMonth;

    public int[] tabJour1 = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23};
    public int[] tabJour2 = new int[]{0, 1, 1, 0, 0, 0, 1, 0, 0, 1, 0, 1, 0, 1, 0, 2, 1, 1, 1, 0, 0, 2, 1, 0, 0, 2, 0, 0};
    public int[] tabJour3 = new int[]{0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 0, 0, 0};
    public int[] tabJour4 = new int[]{};

    public int[] tabSemaine9 = new int[]{2, 2, 3, 3, 4, 4, 5};
    public int[] tabSemaine10 = new int[]{2, 2, 7, 7, 4, 4, 5};
    public int[] tabSemaine11 = new int[]{5, 5, 7, 0, 0, 0, 0};

    public int[] tabMois1 = new int[]{10,12,14,16};
    public int[] tabMois2 = new int[]{18,14,14,16};
    public int[] tabMois3 = new int[]{8,20,0,0};

    public String[] jours = new String[]{"Lundi", "Mardi", "Mercredi" , "Jeudi", "Vendredi", "Samedi", "Dimanche"};
    public String[] semaines = new String[]{"Semaine 1", "Semaine 2", "Semaine 3", "Semaine 4"};
    public String[] heures = new String[]{"1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16","17","18","19","20","22","23"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setSupportActionBar(findViewById(R.id.home_toolbar));
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);

        fm.beginTransaction().add(R.id.fragment_container, charts, "2").hide(charts).commit();
        fm.beginTransaction().add(R.id.fragment_container, count, "1").commit();

        mDrawerLayout =  findViewById(R.id.drawer_layout);

        navView = findViewById(R.id.navigation_view);
        navView.setNavigationItemSelectedListener(this);

        botView = findViewById(R.id.navigation);
        configureBottomView();

        Intent intent = getIntent();
        if (intent != null){
            if (intent.hasExtra("doses")){
                str = intent.getStringExtra("doses");
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
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
        }
        this.mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void configureBottomView(){
        botView.setOnNavigationItemSelectedListener(item -> updateMainFragment(item.getItemId()));
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
            Update();
            if(doses>=0){
                show.setText(String.valueOf(doses));
            }
        });
    }

    public void TimeGraph(View view) {
        switch (view.getId()) {

            case R.id.day:
                if (NumGraph == 1) {
                    graph.removeAllSeries();
                    SeriesDay = new BarGraphSeries<>(generateDataDay(tabJour1));
                    SeriesDay.setTitle("Lundi");
                    graph.addSeries(SeriesDay);
                    StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(graph);
                    staticLabelsFormatter.setHorizontalLabels(heures);
                    graph.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);

                } else if (NumGraph == 2) {
                    graph.removeAllSeries();
                    SeriesDay = new BarGraphSeries<>(generateDataDay(tabJour2));
                    SeriesDay.setTitle("Mardi");
                    graph.addSeries(SeriesDay);
                    StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(graph);
                    staticLabelsFormatter.setHorizontalLabels(heures);
                    graph.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);

                } else {
                    graph.removeAllSeries();
                    SeriesDay = new BarGraphSeries<>(generateDataDay(tabJour3));
                    SeriesDay.setTitle("Mercredi");
                    graph.addSeries(SeriesDay);
                    StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(graph);
                    staticLabelsFormatter.setHorizontalLabels(heures);
                    graph.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);
                }
                break;

            case R.id.week:
                if (NumGraph == 1) {
                    graph.removeAllSeries();
                    SeriesWeek = new BarGraphSeries<>(generateDataWeek(tabSemaine9));
                    SeriesWeek.setTitle("Semaine 9");
                    graph.addSeries(SeriesWeek);
                    StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(graph);
                    staticLabelsFormatter.setHorizontalLabels(jours);
                    graph.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);
                    graph.getViewport().setXAxisBoundsManual(true);
                    graph.getViewport().setMinX(0);
                    graph.getViewport().setMaxX(6);

                } else if (NumGraph == 2) {
                    graph.removeAllSeries();
                    SeriesWeek = new BarGraphSeries<>(generateDataWeek(tabSemaine10));
                    SeriesWeek.setTitle("Semaine 10");
                    graph.addSeries(SeriesWeek);
                    StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(graph);
                    staticLabelsFormatter.setHorizontalLabels(jours);
                    graph.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);

                } else {
                    graph.removeAllSeries();
                    SeriesWeek = new BarGraphSeries<>(generateDataWeek(tabSemaine11));
                    SeriesWeek.setTitle("Semaine 11");
                    graph.addSeries(SeriesWeek);
                    StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(graph);
                    staticLabelsFormatter.setHorizontalLabels(jours);
                    graph.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);
                }
                break;

            case R.id.month:
                if (NumGraph == 1) {
                    graph.removeAllSeries();
                    SeriesMonth = new BarGraphSeries<>(generateDataMonth(tabMois1));
                    SeriesMonth.setTitle("Janvier");
                    graph.addSeries(SeriesMonth);
                    StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(graph);
                    staticLabelsFormatter.setHorizontalLabels(semaines);
                    graph.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);

                } else if (NumGraph == 2) {
                    graph.removeAllSeries();
                    SeriesMonth = new BarGraphSeries<>(generateDataMonth(tabMois2));
                    SeriesMonth.setTitle("Février");
                    graph.addSeries(SeriesMonth);
                    StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(graph);
                    staticLabelsFormatter.setHorizontalLabels(semaines);
                    graph.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);

                } else {
                    graph.removeAllSeries();
                    SeriesMonth = new BarGraphSeries<>(generateDataMonth(tabMois3));
                    SeriesMonth.setTitle("Mars");
                    graph.addSeries(SeriesMonth);
                    StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(graph);
                    staticLabelsFormatter.setHorizontalLabels(semaines);
                    graph.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);
                }
                break;

            default:
                break;
        }
        graph.getLegendRenderer().setVisible(true);
        graph.getViewport().setScrollable(true); // enables horizontal scrolling
        graph.getViewport().setScalable(true); // enables horizontal zooming and scrolling
        graph.getLegendRenderer().setFixedPosition(0,0);
        graph.getLegendRenderer().setWidth(200);
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getGridLabelRenderer().setNumHorizontalLabels(2);
    }

    public void DateChange(View view){
        switch(view.getId()){
            case R.id.previous:
                NumGraph--;
                if(NumGraph<1){
                    NumGraph=3;
                }
                break;
            case R.id.next:
                NumGraph++;
                if(NumGraph>3){
                    NumGraph=1;
                }
                break;
            default:
                break;
        }
    }

    public DataPoint[] generateDataDay(int[] tab) {
        int count = 24;
        DataPoint[] values = new DataPoint[count];
        for (int i=0; i<count; i++) {
            double x = i;
            double y = tab[i];
            DataPoint v = new DataPoint(x, y);
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
        int count = 4;
        DataPoint[] values = new DataPoint[count];
        for (int i=0; i<count; i++) {
            double x = i;
            double y = tab[i];
            DataPoint v = new DataPoint(x, y);
            values[i] = v;
        }
        return values;
    }

    public void Update(){
        tabSemaine11[3] = value;
        tabMois3[2] = value;
    }

    public void sendID() {
        graph = findViewById(R.id.graph);

        day = findViewById(R.id.day);
        week = findViewById(R.id.week);
        month = findViewById(R.id.month);
        year = findViewById(R.id.year);

        prev = findViewById(R.id.previous);
        next = findViewById(R.id.next);
    }

    @Override
    public void sendID2() {
        show = findViewById(R.id.title_count);
        button = findViewById(R.id.button_count);
        discount();
        Update();
    }
}
