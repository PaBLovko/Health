package by.bsuir.health.controller;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.FragmentActivity;

import by.bsuir.health.R;
import by.bsuir.health.bean.BluetoothConnector;
import by.bsuir.health.bean.ListAdapter;
import by.bsuir.health.exeption.bluetooth.BluetoothException;
import by.bsuir.health.exeption.bluetooth.ConnectionBluetoothException;
import by.bsuir.health.service.ReceiverService;

import static by.bsuir.health.ui.ViewActivity.BT_BOUNDED;
import static by.bsuir.health.ui.ViewActivity.BT_SEARCH;
import static by.bsuir.health.ui.ViewActivity.REQ_ENABLE_BT;

/**
 * @author Pablo on 07.03.2021
 * @project Health
 */
public class BluetoothConnectorController{

    public void enableBt(FragmentActivity fragmentActivity, boolean flag) {
        if (flag) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            fragmentActivity.startActivityForResult(intent, REQ_ENABLE_BT);
        } else BluetoothConnector.getBluetoothAdapter().disable();
    }

    private boolean isDevice(String device){
        String nameDevice = "HC-05";
        return device.equals(nameDevice);
    }

    public BluetoothConnector.ConnectedThread connectToExisting(
            BluetoothConnector bluetoothConnector,BluetoothDevice device) throws BluetoothException{
        bluetoothConnect(bluetoothConnector, device);
        BluetoothConnector.ConnectedThread connectedThread =
                new BluetoothConnector.ConnectedThread(bluetoothConnector.getSocket());
        connectedThread.start();
        return connectedThread;
    }

    public void bluetoothConnect(BluetoothConnector bluetoothConnector, BluetoothDevice device)
            throws BluetoothException {
        if(isDevice(device.getName()))
            bluetoothConnector.connect(device);
        else throw new ConnectionBluetoothException("Not connected to this device");
    }

    public ListAdapter getListAdapter(Context context, BluetoothConnector bluetoothConnector,
                                      int type) throws BluetoothException {
        bluetoothConnector.clear();
        int iconType = R.drawable.ic_bluetooth_bounded_device;
        switch (type) {
            case BT_BOUNDED:
                bluetoothConnector.setBluetoothDevices(BluetoothConnector.getBondedDevices());
                iconType = R.drawable.ic_bluetooth_bounded_device;
                break;
            case BT_SEARCH:
                iconType = R.drawable.ic_bluetooth_search_device;
                break;
        }
        return new ListAdapter(context, bluetoothConnector.getBluetoothDevices(), iconType);
    }

    public void addReceiver(ContextWrapper contextWrapper, ReceiverService receiverService) {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        contextWrapper.registerReceiver(receiverService, filter);
    }
}
