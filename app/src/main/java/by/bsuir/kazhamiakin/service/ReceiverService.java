package by.bsuir.kazhamiakin.service;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import by.bsuir.kazhamiakin.bean.AsyncTaskConnect;
import by.bsuir.kazhamiakin.bean.BluetoothConnector;
import by.bsuir.kazhamiakin.bean.Pulse;
import by.bsuir.kazhamiakin.controller.ViewController;
import by.bsuir.kazhamiakin.dao.preference.PrefModel;
import by.bsuir.kazhamiakin.exeption.bluetooth.BluetoothException;
import by.bsuir.kazhamiakin.ui.ListAdapter;
import by.bsuir.kazhamiakin.ui.ViewActivity;

import static by.bsuir.kazhamiakin.ui.ViewActivity.BT_SEARCH;

/**
 * @author Pablo on 09.03.2021
 * @project Health
 */
public class ReceiverService extends BroadcastReceiver {

    private ViewActivity viewActivity;
    private BluetoothConnector bluetoothConnector;
    private ListAdapter listAdapter;
    private Pulse pulse;
    private AsyncTaskConnect asyncTaskConnect;

    public ReceiverService(ViewActivity viewActivity, BluetoothConnector bluetoothConnector,
                           ListAdapter listAdapter,  Pulse pulse) {
        this.viewActivity = viewActivity;
        this.bluetoothConnector = bluetoothConnector;
        this.listAdapter = listAdapter;
        this.pulse = pulse;
        this.asyncTaskConnect = null;
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
                        listAdapter = new ViewController().getListAdapter(
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
                    PrefModel preference = pulse.getPreference();
                    if(preference.isLastConnectDevice()){
                        assert device != null;
                        if(device.getAddress().equals(preference.getSharedPreferences().getString(
                                PrefModel.KEY_MAC_ADDRESS, ""))){
                            try {
                                listAdapter = new ViewController().getListAdapter(
                                        context, bluetoothConnector, BT_SEARCH);
                                viewActivity.setListDevices(listAdapter);
                                asyncTaskConnect = new AsyncTaskConnect(device, bluetoothConnector,
                                        pulse, viewActivity);
                                asyncTaskConnect.execute();
                            }catch (BluetoothException e){
                                e.printStackTrace();
                                new ViewController().viewWarning(viewActivity.getActivity(),
                                        viewActivity, e.getMessage());
                            }
                        }
                    }
                    break;
            }
        }
    }
}
