package by.bsuir.health;

import android.os.Handler;
import android.text.method.MovementMethod;
import android.text.method.ScrollingMovementMethod;

import com.jjoe64.graphview.series.DataPoint;

import java.util.HashMap;
import java.util.Map;

import by.bsuir.health.bluetooth.BluetoothConnector;
import by.bsuir.health.preference.PrefModel;

/**
 * @author Pablo on 31.01.2021
 * @project Health
 */
public class Pulse {

    private String lastSensorValues;
    private Handler handler;
    private Runnable timer;
    private int xLastValue;
    private BluetoothConnector.ConnectedThread connectedThread;
    private PrefModel preference;
    private ViewActivity viewActivity;
    private MovementMethod movementMethod;
    private Map dataSensor;

    public Pulse(BluetoothConnector.ConnectedThread connectedThread,
                 PrefModel preference, ViewActivity viewActivity) {
        this.xLastValue = 0;
        this.connectedThread = connectedThread;
        this.lastSensorValues = connectedThread.getLastSensorValues();
        this.preference = preference;
        this.viewActivity = viewActivity;
        this.movementMethod = new ScrollingMovementMethod();
        this.handler = new Handler();
    }

    public void startTimer() {
        cancelTimer();
        handler.postDelayed(timer = new Runnable() {
            @Override
            public void run() {
                if (connectedThread.getLastSensorValues() != null) {
                    lastSensorValues = connectedThread.getLastSensorValues();
                    viewActivity.setEtConsoleAndMovementMethod(lastSensorValues, movementMethod);
                    dataSensor = parseData(lastSensorValues);
                    if (dataSensor != null){
                        if (dataSensor.containsKey("Temp") && dataSensor.containsKey("rand")) {
                            int pulseValue = Integer.parseInt(dataSensor.get("Temp").toString());
                            int rand = Integer.parseInt(dataSensor.get("rand").toString().trim());
                            //TODO cardio
                            if (preference.getOperationMode().equals(
                                    viewActivity.getOperatingModePulse())) {
                                viewActivity.getSeriesPulse().appendData(
                                        new DataPoint(xLastValue, pulseValue),
                                        true, preference.getPointsCount());
                            } else if (preference.getOperationMode().equals(
                                    viewActivity.getOperatingModeSpo())) {
                                viewActivity.getSeriesSpo().appendData(
                                        new DataPoint(xLastValue, pulseValue),
                                        true, preference.getPointsCount());
                            } else if (preference.getOperationMode().equals(
                                    viewActivity.getOperatingModeCardio())) {
                                viewActivity.getSeriesCardio().appendData(
                                        new DataPoint(xLastValue, pulseValue),
                                        true, preference.getPointsCount());
                            }
                            xLastValue++;
                            //                        Toast.makeText(MainActivity.this, "Millis: " + dataSensor.get("millis"), Toast.LENGTH_SHORT).show();
                        }
//                        xLastValue++;
                    }
                }
                handler.postDelayed(this, preference.getDelayTimer());
            }
        }, preference.getDelayTimer());
    }

    public void cancelTimer() {
        if (handler != null) {
            handler.removeCallbacks(timer);
        }
    }

    private Map parseData(String data) {    // temp:37|humidity:80
        if (data.indexOf('|') > 0) {
            HashMap map = new HashMap();
            String[] pairs = data.split("\\|");
            for (String pair: pairs) {
                String[] keyValue = pair.split(":");
                map.put(keyValue[0], keyValue[1]);
            }
            return map;
        }
        return null;
    }
}
