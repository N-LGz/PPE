package com.nemge.ppe;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, ChartsFragment.onFragmentInteractionListener {

    private DrawerLayout mDrawerLayout;
    private NavigationView navView;
    private BottomNavigationView botView;
    private Button day, week, month, year, prev, next;
    private GraphView graph;

    private int NumGraph = 1;

    private BarGraphSeries SeriesDay, SeriesWeek, SeriesMonth;

    public int[] tabJour1 = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23};
    public int[] tabJour2 = new int[]{0, 1, 1, 0, 0, 0, 1, 0, 0, 1, 0, 1, 0, 1, 0, 2, 1, 1, 1, 0, 0, 2, 1, 0, 0, 2, 0, 0};
    public int[] tabJour3 = new int[]{0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 0, 0, 0};

    public int[] tabSemaine1 = new int[]{2, 2, 3, 3, 4, 4, 5};
    public int[] tabSemaine2 = new int[]{2, 2, 7, 7, 4, 4, 5};
    public int[] tabSemaine3 = new int[]{5, 5, 7, 7, 4, 4, 2};

    public int[] tabMois1 = new int[]{10,12,14,16};
    public int[] tabMois2 = new int[]{18,14,14,16};
    public int[] tabMois3 = new int[]{8,20,14,12};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setSupportActionBar(findViewById(R.id.home_toolbar));
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);

        loadFragment(new CountFragment());

        mDrawerLayout =  findViewById(R.id.drawer_layout);

        navView = findViewById(R.id.navigation_view);
        navView.setNavigationItemSelectedListener(this);

        botView = findViewById(R.id.navigation);

        configureBottomView();
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
            case R.id.nav_disconnect:
                Intent intentLog = new Intent(this, LoginActivity.class);
                startActivity(intentLog);
                break;
        }
        this.mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void configureBottomView(){
        botView.setOnNavigationItemSelectedListener(item -> updateMainFragment(item.getItemId()));
    }

    private Boolean updateMainFragment(Integer integer){
        Fragment fragment = null;
        switch (integer) {
            case R.id.menu_doses:
                fragment = new CountFragment();
                break;

            case R.id.menu_charts:
                fragment = new ChartsFragment();
                break;
        }
        return loadFragment(fragment);
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

    public void TimeGraph(View view) {
        switch (view.getId()) {

            case R.id.day:
                if (NumGraph == 1) {
                    graph.removeAllSeries();
                    SeriesDay = new BarGraphSeries<>(generateDataDay(tabJour1));
                    SeriesDay.setTitle("Lundi");
                    graph.addSeries(SeriesDay);
                } else if (NumGraph == 2) {
                    graph.removeAllSeries();
                    SeriesDay = new BarGraphSeries<>(generateDataDay(tabJour2));
                    SeriesDay.setTitle("Mardi");
                    graph.addSeries(SeriesDay);
                } else {
                    graph.removeAllSeries();
                    SeriesDay = new BarGraphSeries<>(generateDataDay(tabJour3));
                    SeriesDay.setTitle("Mercredi");
                    graph.addSeries(SeriesDay);
                }
                break;

            case R.id.week:
                if (NumGraph == 1) {
                    graph.removeAllSeries();
                    SeriesWeek = new BarGraphSeries<>(generateDataWeek(tabSemaine1));
                    SeriesWeek.setTitle("Semaine 1");
                    graph.addSeries(SeriesWeek);
                } else if (NumGraph == 2) {
                    graph.removeAllSeries();
                    SeriesWeek = new BarGraphSeries<>(generateDataWeek(tabSemaine2));
                    SeriesWeek.setTitle("Semaine 2");
                    graph.addSeries(SeriesWeek);
                } else {
                    graph.removeAllSeries();
                    SeriesWeek = new BarGraphSeries<>(generateDataWeek(tabSemaine3));
                    SeriesWeek.setTitle("Semaine 3");
                    graph.addSeries(SeriesWeek);
                }
                break;

            case R.id.month:
                if (NumGraph == 1) {
                    graph.removeAllSeries();
                    SeriesMonth = new BarGraphSeries<>(generateDataMonth(tabMois1));
                    SeriesMonth.setTitle("Janvier");
                    graph.addSeries(SeriesMonth);
                } else if (NumGraph == 2) {
                    graph.removeAllSeries();
                    SeriesMonth = new BarGraphSeries<>(generateDataMonth(tabMois2));
                    SeriesMonth.setTitle("Février");
                    graph.addSeries(SeriesMonth);
                } else {
                    graph.removeAllSeries();
                    SeriesMonth = new BarGraphSeries<>(generateDataMonth(tabMois3));
                    SeriesMonth.setTitle("Mars");
                    graph.addSeries(SeriesMonth);
                }
                break;

            default:
                break;
        }
        graph.getLegendRenderer().setVisible(true);
        graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
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

    public void sendID(){
        graph = findViewById(R.id.graph);

        day = findViewById(R.id.day);
        week = findViewById(R.id.week);
        month = findViewById(R.id.month);
        year = findViewById(R.id.year);

        prev = findViewById(R.id.previous);
        next = findViewById(R.id.next);
    }
}
