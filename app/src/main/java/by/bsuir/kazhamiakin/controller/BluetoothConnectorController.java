package by.bsuir.kazhamiakin.controller;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.FragmentActivity;

import by.bsuir.kazhamiakin.bean.BluetoothConnector;
import by.bsuir.kazhamiakin.exeption.bluetooth.BluetoothException;
import by.bsuir.kazhamiakin.exeption.bluetooth.ConnectionBluetoothException;
import by.bsuir.kazhamiakin.service.ReceiverService;

import static by.bsuir.kazhamiakin.ui.ViewActivity.REQ_ENABLE_BT;

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
            BluetoothConnector bluetoothConnector, BluetoothDevice device) throws BluetoothException{
        bluetoothConnect(bluetoothConnector, device);
        BluetoothConnector.ConnectedThread connectedThread =
                new BluetoothConnector.ConnectedThread(bluetoothConnector.getSocket());
        connectedThread.connect();
        connectedThread.start();
        return connectedThread;
    }

    public void bluetoothConnect(BluetoothConnector bluetoothConnector, BluetoothDevice device)
            throws BluetoothException {
        if(isDevice(device.getName()))
            bluetoothConnector.connect(device);
        else throw new ConnectionBluetoothException("Not connected to this device");
    }

    public void addReceiver(ContextWrapper contextWrapper, ReceiverService receiverService) {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        contextWrapper.registerReceiver(receiverService, filter);
    }
}
