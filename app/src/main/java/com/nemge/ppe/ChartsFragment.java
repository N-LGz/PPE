package com.nemge.ppe;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.ValueDependentColor;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.time.DayOfWeek;
import java.util.Calendar;
import java.util.Date;

public class ChartsFragment extends Fragment {

    int doses = 0;
    int i = 0;

    Calendar current = Calendar.getInstance();
    Date date;
    private BarGraphSeries<DataPoint> mSeries1;

    //Tableau de douille
    int tabJour= new int[];

    public ChartsFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_charts, container, false);

        GraphView graph = view.findViewById(R.id.graph);

        mSeries1 = new BarGraphSeries<>(generateData());
        graph.addSeries(mSeries1);

        mSeries1.setTitle("Doses");
        graph.getLegendRenderer().setVisible(true);
        graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);

        graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(getActivity()));
        graph.getGridLabelRenderer().setNumHorizontalLabels(2); // only 4 because of the space

        // set manual x bounds to have nice steps
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setBackgroundColor(322);

        graph.getViewport().setScalable(true);
        graph.getViewport().setScalableY(true);

        graph.getViewport().setScrollable(true); // enables horizontal scrolling


        // as we use dates as labels, the human rounding to nice readable numbers
        // is not necessary
        graph.getGridLabelRenderer().setHumanRounding(false);

        return view;
    }

    private DataPoint[] generateData() {
        int y = 0;
        date = current.getTime();
        Calendar calendar = Calendar.getInstance();
        int count = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)-calendar.get(Calendar.DAY_OF_MONTH)+1;
        DataPoint[] values = new DataPoint[count];
        for (int i=0; i<count; i++) {
            Date x = calendar.getTime();
            if(x.getTime()==date.getTime()){
                y++;
            }
            DataPoint v = new DataPoint(x, y);
            values[i] = v;
            calendar.add(Calendar.DATE, 1);
        }
        return values;
    }
}


