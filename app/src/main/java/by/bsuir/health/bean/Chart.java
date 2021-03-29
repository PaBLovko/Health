package by.bsuir.health.bean;

import android.graphics.Color;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;

import javax.inject.Inject;

/**
 * @author Pablo on 29.03.2021
 * @project Health
 */
public class Chart{
    private GraphView graphView;
    private ArrayList<Integer> dataOfEntry;
    private LineGraphSeries<DataPoint> series;
    private int xLastValue;

    @Inject
    public Chart(GraphView graphView){
        this.graphView = graphView;
        this.series = new LineGraphSeries<>();
        this.dataOfEntry = new ArrayList<>();
        this.xLastValue = 0;
    }

    public void settings(int x){
        deleteData();
        setCustomChart(x);
    }

    public void addData(int graph, int pointCount) {
        dataOfEntry.add(graph);
        series.appendData(new DataPoint(xLastValue++, graph), true, pointCount);
    }

    private void setCustomChart(int MaxX){
        this.series.setColor(Color.RED);
        this.graphView.addSeries(series);
        this.graphView.getViewport().setMinX(0);
        this.graphView.getViewport().setMaxX(MaxX);
        this.graphView.getViewport().setXAxisBoundsManual(true);
    }

    public void deleteData(){
        graphView.removeAllSeries();
    }

    public ArrayList<Integer> getData(){
        return dataOfEntry;
    }
}

