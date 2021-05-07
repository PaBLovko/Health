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
        this.graphView.getViewport().setScalable(true);
        this.graphView.getViewport().setScalableY(true);
        this.graphView.getViewport().setScrollable(true);
        this.graphView.getViewport().setScrollableY(true);
    }

    private void deleteData(){
        graphView.removeAllSeries();
    }

    public ArrayList<Integer> getData(){
        return dataOfEntry;
    }

    public GraphView getGraphView() {
        return graphView;
    }

    public void setGraphView(GraphView graphView) {
        this.graphView = graphView;
    }

    public ArrayList<Integer> getDataOfEntry() {
        return dataOfEntry;
    }

    public void setDataOfEntry(ArrayList<Integer> dataOfEntry) {
        this.dataOfEntry = dataOfEntry;
    }

    public LineGraphSeries<DataPoint> getSeries() {
        return series;
    }

    public void setSeries(LineGraphSeries<DataPoint> series) {
        this.series = series;
    }

    public int getXLastValue() {
        return xLastValue;
    }

    public void setXLastValue(int xLastValue) {
        this.xLastValue = xLastValue;
    }
}

