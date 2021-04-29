package by.bsuir.health.bean;

import android.os.Handler;
import android.os.Looper;
import android.text.method.MovementMethod;
import android.text.method.ScrollingMovementMethod;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import by.bsuir.health.controller.ViewController;
import by.bsuir.health.dao.preference.PrefModel;
import by.bsuir.health.ui.ViewActivity;

/**
 * @author Pablo on 31.01.2021
 * @project Health
 */
public class Pulse {

    private final Handler handler;
    private Runnable timer;
    private BluetoothConnector bluetoothConnector;
    private BluetoothConnector.ConnectedThread connectedThread;
    private PrefModel preference;
    private ViewActivity viewActivity;
    private Chart chart;
    private Filter filter;
    private boolean doAnalysis;
    private SignalAnalysis signalAnalysis;

    public Pulse(BluetoothConnector bluetoothConnector, ViewActivity viewActivity,
                 PrefModel preference) {
        this.bluetoothConnector = bluetoothConnector;
        this.connectedThread = null;
        this.preference = preference;
        this.viewActivity = viewActivity;
        this.handler = new Handler(Looper.getMainLooper());
        this.filter = new Filter(preference.isFilterMode());
        this.doAnalysis = true;
        this.signalAnalysis = new SignalAnalysis();
    }

    public void setConnectedThread(BluetoothConnector.ConnectedThread connectedThread) {
        this.connectedThread = connectedThread;
    }

    public PrefModel getPreference() {
        return preference;
    }

    public BluetoothConnector getBluetoothConnector() {
        return bluetoothConnector;
    }

    public BluetoothConnector.ConnectedThread getConnectedThread() {
        return connectedThread;
    }

    public void startTimer() {
        chart = new Chart(viewActivity.getGvGraph());
        chart.settings(preference.getPointsCount());
        handler.postDelayed(timer = new Runnable() {
            @Override
            public void run() {
                if (connectedThread.getLastSensorValues() != null) {
                    String lastSensorValues = connectedThread.getLastSensorValues().trim();
                    Map<String, String> dataSensor = parseData(lastSensorValues);
                    if (dataSensor != null && isDataContainsKey(dataSensor) &&
                            isDataNotNull(dataSensor)) {
                        int graph = Integer.parseInt(dataSensor.get("graph"));
                        int command = Integer.parseInt(dataSensor.get("command"));
                        int data = filter.step(graph);
                        if (checkCommand(command)){
                            chart.addData(data, preference.getPointsCount());
                        } else{
                            new ViewController().viewToastShow(viewActivity.getActivity(),
                                    "Mismatch of modes");
                            cancelTimer();
                            doAnalysis = false;
                            return;
                        }
                    }
                }
                handler.postDelayed(this, 0);
            }
        },0);
    }

    public void counter(){
        if (!doAnalysis) return;
        final Timer timer1 = new Timer();
        timer1.schedule(new TimerTask() {
            @Override
            public void run() {
                cancelTimer();
                signalAnalysis.setData(chart.getData());
                signalAnalysis.analyseData();
                MovementMethod movementMethod = new ScrollingMovementMethod();
                viewActivity.setEtConsoleAndMovementMethod(signalAnalysis.getMode()+" "+
                        signalAnalysis.getPulse()+" "+
                        signalAnalysis.getNumOfExtrasystole(), movementMethod);
            }
        },preference.getDelayTimer());
    }

    public void cancelTimer() {
        if (handler != null) {
            handler.removeCallbacks(timer);
        }
    }

    public void clearChart(){
        chart = new Chart(viewActivity.getGvGraph());
        chart.settings(preference.getPointsCount());
        viewActivity.setEtConsoleAndMovementMethod("", new ScrollingMovementMethod());
    }

    private boolean checkCommand(int command){
        switch (command){
            case 0:
                return preference.getOperationMode().equals(viewActivity.getOperatingModePulse());
            case 1:
                return preference.getOperationMode().equals(viewActivity.getOperatingModeSpo());
            case 2:
                return preference.getOperationMode().equals(viewActivity.getOperatingModeCardio());
            default:
                return false;
        }
    }

    private boolean isDataNotNull(Map<String, String> dataSensor){
        return dataSensor.get("command") != null &&
                dataSensor.get("graph") != null &&
                dataSensor.get("data") != null;
    }

    private boolean isDataContainsKey(Map<String, String> dataSensor){
        return dataSensor.containsKey("command") &&
                dataSensor.containsKey("graph") &&
                dataSensor.containsKey("data");
    }

    private Map<String, String> parseData(String data) {    // temp:37|humidity:80
        if (data.indexOf('|') > 0) {
            Map<String, String> map = new HashMap<>();
            String[] pairs = data.split("\\|");
            for (String pair: pairs) {
                String[] keyValue = pair.split(":");
                map.put(keyValue[0], keyValue[1]);
            }
            return map;
        }
        return null;
    }

    public SignalAnalysis getSignalAnalysis() {
        return signalAnalysis;
    }

    public Chart getChart() {
        return chart;
    }
}
