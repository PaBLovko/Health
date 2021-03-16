package by.bsuir.health.bean;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;

import by.bsuir.health.controller.BluetoothConnectorController;
import by.bsuir.health.controller.ViewController;
import by.bsuir.health.dao.preference.PrefModel;
import by.bsuir.health.exeption.bluetooth.BluetoothException;
import by.bsuir.health.service.OnItemChangedListener;
import by.bsuir.health.ui.ViewActivity;
/**
 * @author Pablo on 15.03.2021
 * @project Health
 */
public class AsyncTaskConnect extends AsyncTask<Void, Void, Pulse> {
    private static final List<OnItemChangedListener> listeners = new ArrayList<>();
    private BluetoothDevice device;
    private ViewActivity viewActivity;
    private PrefModel preference;
    private BluetoothConnector bluetoothConnector;
    private Pulse pulse;
    private Activity activity;

    public AsyncTaskConnect(BluetoothDevice device, ViewActivity viewActivity,
                            PrefModel preference, BluetoothConnector bluetoothConnector,
                            Pulse pulse, Activity activity) {
        this.device = device;
        this.viewActivity = viewActivity;
        this.preference = preference;
        this.bluetoothConnector = bluetoothConnector;
        this.pulse = pulse;
        this.activity = activity;
    }

    public void setDevice(BluetoothDevice device) {
        this.device = device;
    }

    public static void addItemListener(OnItemChangedListener l) {
        listeners.add(l);
    }

    @Override
    protected Pulse doInBackground(Void... params){
        try {
            BluetoothConnector.ConnectedThread connectedThread =
                    new BluetoothConnectorController().connectToExisting(bluetoothConnector,
                            device);
            preference.saveMacAddress(device.getAddress());
            pulse = new Pulse(bluetoothConnector, connectedThread, preference,viewActivity);
            pulse.startTimer();
        } catch (BluetoothException e) {
            e.printStackTrace();
            new ViewController().viewWarning(activity, viewActivity, e.getMessage());
            this.cancel(true);
        }
        return pulse;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        viewActivity.setBtnEnableSearchStart();
        viewActivity.setPbProgressNoVisibility();
        viewActivity.getProgressDialog().show();
    }

    @Override
    protected void onCancelled(Pulse pulse) {
        super.onCancelled();
        viewActivity.getProgressDialog().dismiss();
    }

    @Override
    protected void onPostExecute(Pulse pulse) {
        super.onPostExecute(pulse);
        viewActivity.getProgressDialog().dismiss();
        viewActivity.showFrameLedControls();
        for (OnItemChangedListener l : listeners)
            l.OnItemChanged();
    }
}
