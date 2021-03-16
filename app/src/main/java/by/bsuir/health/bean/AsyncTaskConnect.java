package by.bsuir.health.bean;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;

import by.bsuir.health.controller.BluetoothConnectorController;
import by.bsuir.health.controller.ViewController;
import by.bsuir.health.exeption.bluetooth.BluetoothException;
import by.bsuir.health.service.OnConnectChangedListener;
import by.bsuir.health.ui.ViewActivity;
/**
 * @author Pablo on 15.03.2021
 * @project Health
 */
public class AsyncTaskConnect extends AsyncTask<Void, Void, BluetoothConnector.ConnectedThread> {
    private static final List<OnConnectChangedListener> listeners = new ArrayList<>();
    private BluetoothDevice device;
    private ViewActivity viewActivity;
    private BluetoothConnector bluetoothConnector;
    private Pulse pulse;
    private Activity activity;

    public AsyncTaskConnect(BluetoothDevice device, ViewActivity viewActivity,
                            BluetoothConnector bluetoothConnector, Pulse pulse, Activity activity) {
        this.device = device;
        this.viewActivity = viewActivity;
        this.bluetoothConnector = bluetoothConnector;
        this.pulse = pulse;
        this.activity = activity;
    }

    public static void addItemListener(OnConnectChangedListener l) {
        listeners.add(l);
    }

    @Override
    protected BluetoothConnector.ConnectedThread doInBackground(Void... params){
        BluetoothConnector.ConnectedThread connectedThread = null;
        try {
            connectedThread = new BluetoothConnectorController().connectToExisting(
                    bluetoothConnector, device);
            pulse.getPreference().saveMacAddress(device.getAddress());
            pulse.setConnectedThread(connectedThread);
            pulse.startTimer();
        } catch (BluetoothException e) {
            e.printStackTrace();
            new ViewController().viewWarning(activity, viewActivity, e.getMessage());
            this.cancel(true);
        }
        return connectedThread;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        viewActivity.setBtnEnableSearchStart();
        viewActivity.setPbProgressNoVisibility();
        viewActivity.getProgressDialog().show();
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        viewActivity.getProgressDialog().dismiss();
    }

    @Override
    protected void onPostExecute(BluetoothConnector.ConnectedThread connectedThread) {
        super.onPostExecute(connectedThread);
        viewActivity.getProgressDialog().dismiss();
        viewActivity.showFrameLedControls();
        for (OnConnectChangedListener l : listeners)
            l.OnConnectChanged(connectedThread);
    }
}
