package by.bsuir.health.bean;

import android.os.Handler;
import android.os.Looper;
import android.text.method.MovementMethod;
import android.text.method.ScrollingMovementMethod;

import com.jjoe64.graphview.series.DataPoint;

import java.util.HashMap;
import java.util.Map;

import by.bsuir.health.dao.preference.PrefModel;
import by.bsuir.health.ui.ViewActivity;

/**
 * @author Pablo on 31.01.2021
 * @project Health
 */
public class Pulse {

    private Handler handler;
    private Runnable timer;
    private int xLastValue;
    private BluetoothConnector bluetoothConnector;
    private BluetoothConnector.ConnectedThread connectedThread;
    private PrefModel preference;
    private ViewActivity viewActivity;

    public Pulse(BluetoothConnector bluetoothConnector, ViewActivity viewActivity,
                 PrefModel preference) {
        this.xLastValue = 0;
        this.bluetoothConnector = bluetoothConnector;
        this.connectedThread = null;
        this.preference = preference;
        this.viewActivity = viewActivity;
        this.handler = new Handler(Looper.getMainLooper());
    }

//    public Pulse(BluetoothConnector bluetoothConnector,
//                 BluetoothConnector.ConnectedThread connectedThread,
//                 PrefModel preference, ViewActivity viewActivity) {
//        this.xLastValue = 0;
//        this.bluetoothConnector = bluetoothConnector;
//        this.connectedThread = connectedThread;
//        this.preference = preference;
//        this.viewActivity = viewActivity;
//        this.handler = new Handler(Looper.getMainLooper());
//    }

    public void setBluetoothConnector(BluetoothConnector bluetoothConnector) {
        this.bluetoothConnector = bluetoothConnector;
    }

    public void setConnectedThread(BluetoothConnector.ConnectedThread connectedThread) {
        this.connectedThread = connectedThread;
    }

    public PrefModel getPreference() {
        return preference;
    }

    public void setPreference(PrefModel preference) {
        this.preference = preference;
    }

    public ViewActivity getViewActivity() {
        return viewActivity;
    }

    public void setViewActivity(ViewActivity viewActivity) {
        this.viewActivity = viewActivity;
    }

    public BluetoothConnector getBluetoothConnector() {
        return bluetoothConnector;
    }

    public BluetoothConnector.ConnectedThread getConnectedThread() {
        return connectedThread;
    }

    public void startTimer() {
        cancelTimer();
        handler.postDelayed(timer = new Runnable() {
            @Override
            public void run() {
                if (connectedThread.getLastSensorValues() != null) {
                    String lastSensorValues = connectedThread.getLastSensorValues();
                    MovementMethod movementMethod = new ScrollingMovementMethod();
                    viewActivity.setEtConsoleAndMovementMethod(lastSensorValues, movementMethod);
                    Map dataSensor = parseData(lastSensorValues);
                    if (dataSensor != null){
                        if (dataSensor.containsKey("Temp") && dataSensor.containsKey("rand")) {
                            int pulseValue = Integer.parseInt(dataSensor.get("Temp").toString());
                            int rand = Integer.parseInt(dataSensor.get("rand").toString().trim());
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
                            //Toast.makeText(MainActivity.this, "Millis: " + dataSensor.get("millis"), Toast.LENGTH_SHORT).show();
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
//            viewActivity.clearSeries(preference.getPointsCount());
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
