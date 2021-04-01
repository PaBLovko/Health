package by.bsuir.health.bean;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import by.bsuir.health.controller.ViewController;
import by.bsuir.health.dao.DatabaseDimension;
import by.bsuir.health.dao.DatabaseHelper;
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

    public Pulse(BluetoothConnector bluetoothConnector, ViewActivity viewActivity,
                 PrefModel preference) {
        this.bluetoothConnector = bluetoothConnector;
        this.connectedThread = null;
        this.preference = preference;
        this.viewActivity = viewActivity;
        this.handler = new Handler(Looper.getMainLooper());
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
//                    MovementMethod movementMethod = new ScrollingMovementMethod();
                    Map<String, String> dataSensor = parseData(lastSensorValues);
                        if (dataSensor != null && isDataContainsKey(dataSensor) &&
                                isDataNotNull(dataSensor)) {
                            int graph = Integer.parseInt(dataSensor.get("graph"));
                            int data = Integer.parseInt(dataSensor.get("data"));
                            int command = Integer.parseInt(dataSensor.get("command"));
//                            String pulse = "Пульс: "+data;
//                            viewActivity.setEtConsoleAndMovementMethod(pulse, movementMethod);
                            if (checkCommand(command)){
                                chart.addData(graph, preference.getPointsCount());
                            } else{
                                new ViewController().viewToastShow(viewActivity.getActivity(),
                                        "Mismatch of modes");
                                cancelTimer();
                                return;
                            }
                        }
                }
                handler.postDelayed(this, 0);
            }
        },0);
    }

    public void counter(){
        final Timer timer1 = new Timer();
        timer1.schedule(new TimerTask() {
            @Override
            public void run() {
                cancelTimer();
                SignalAnalysis signalAnalysis = new SignalAnalysis(chart.getData());
//                chart.setsignalAnalysis.analyseData();
                Date date = new Date();
                @SuppressLint("SimpleDateFormat")
                SimpleDateFormat formatForDate = new SimpleDateFormat("dd.MM.yyyy");
                @SuppressLint("SimpleDateFormat")
                SimpleDateFormat formatForTime = new SimpleDateFormat("HH:mm E");
                DatabaseHelper.SaveToDB(formatForDate.format(date),formatForTime.format(date),
                        chart.getData(), signalAnalysis.analyseData(), signalAnalysis.getPulse(),
                        signalAnalysis.getNumOfExtrasystole());
                List<DatabaseDimension> dataDBList = DatabaseHelper.getList();
            }
        },preference.getDelayTimer());
    }

    public void cancelTimer() {
        if (handler != null) handler.removeCallbacks(timer);
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
}
