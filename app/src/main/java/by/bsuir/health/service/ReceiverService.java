package by.bsuir.health.service;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;
import java.util.List;

import by.bsuir.health.bean.AsyncTaskConnect;
import by.bsuir.health.bean.BluetoothConnector;
import by.bsuir.health.bean.ListAdapter;
import by.bsuir.health.bean.Pulse;
import by.bsuir.health.controller.BluetoothConnectorController;
import by.bsuir.health.controller.ViewController;
import by.bsuir.health.dao.preference.PrefModel;
import by.bsuir.health.exeption.bluetooth.BluetoothException;
import by.bsuir.health.ui.ViewActivity;

import static by.bsuir.health.ui.ViewActivity.BT_SEARCH;

/**
 * @author Pablo on 09.03.2021
 * @project Health
 */
public class ReceiverService extends BroadcastReceiver {

    private static final List<OnItemChangedListener> listeners = new ArrayList<>();

    private ViewActivity viewActivity;
    private BluetoothConnector bluetoothConnector;
    private PrefModel preference;
    private ListAdapter listAdapter;
    private Activity activity;
    private Pulse pulse;
    private AsyncTaskConnect asyncTaskConnect;

    public ReceiverService(ViewActivity viewActivity, BluetoothConnector bluetoothConnector,
                           PrefModel preference, ListAdapter listAdapter, Activity activity,
                           Pulse pulse) {
        this.viewActivity = viewActivity;
        this.bluetoothConnector = bluetoothConnector;
        this.preference = preference;
        this.listAdapter = listAdapter;
        this.activity = activity;
        this.pulse = pulse;
        this.asyncTaskConnect = null;
    }

    public Pulse getPulse() {
        return pulse;
    }

    public void setPulse(Pulse pulse) {
        this.pulse = pulse;
    }

    public AsyncTaskConnect getAsyncTaskConnect() {
        return asyncTaskConnect;
    }

    public static void addItemListener(OnItemChangedListener l) {
        listeners.add(l);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        if (action != null) {
            switch (action) {
                case BluetoothAdapter.ACTION_DISCOVERY_STARTED:
                    try {
                        viewActivity.setBtnEnableSearchStop();
                        viewActivity.setPbProgressVisibility();
                        listAdapter = new BluetoothConnectorController().getListAdapter(
                                context, bluetoothConnector, BT_SEARCH);
                        viewActivity.setListDevices(listAdapter);
                    } catch (BluetoothException e) {
                        e.printStackTrace();
                    }
                    break;
                case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                    viewActivity.setBtnEnableSearchStart();
                    viewActivity.setPbProgressNoVisibility();
                    break;
                case BluetoothDevice.ACTION_FOUND:
                    BluetoothDevice device =
                            intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    if (device != null) {
                        bluetoothConnector.add(device);
                        listAdapter.notifyDataSetChanged();
                    }
                    if(preference.isLastConnectDevice()){
                        assert device != null;
                        if(device.getAddress().equals(preference.getSharedPreferences().getString(
                                PrefModel.KEY_MAC_ADDRESS, ""))){
                            try {
                                listAdapter = new BluetoothConnectorController().getListAdapter(
                                        context, bluetoothConnector, BT_SEARCH);
                                viewActivity.setListDevices(listAdapter);
                                asyncTaskConnect = new AsyncTaskConnect(device,viewActivity, preference,
                                        bluetoothConnector, pulse, activity);
                                asyncTaskConnect.execute();
                            }catch (BluetoothException e){
                                e.printStackTrace();
                                new ViewController().viewWarning(activity, viewActivity,
                                        e.getMessage());
                            }

//                            viewActivity.setBtnEnableSearchStart();
//                            viewActivity.setPbProgressNoVisibility();
//                            try {
//                                listAdapter = new BluetoothConnectorController().getListAdapter(
//                                        context, bluetoothConnector, BT_SEARCH);
//                                viewActivity.setListDevices(listAdapter);
//                                viewActivity.getProgressDialog().show();
//                                BluetoothConnector.ConnectedThread connectedThread =
//                                        new BluetoothConnectorController().connectToExisting(
//                                                bluetoothConnector, device);
//                                preference.saveMacAddress(device.getAddress());
//                                viewActivity.getProgressDialog().dismiss();
//                                viewActivity.showFrameLedControls();
//                                pulse = new Pulse(bluetoothConnector, connectedThread, preference,
//                                        viewActivity);
//                                pulse.startTimer();
//                                for (OnItemChangedListener l : listeners)
//                                    l.OnItemChanged();
//                            } catch (BluetoothException e) {
//                                e.printStackTrace();
//                                new ViewController().viewWarning(activity, viewActivity,
//                                        e.getMessage());
//                            }
                        }
                    }
                    break;
            }
        }
    }
}
