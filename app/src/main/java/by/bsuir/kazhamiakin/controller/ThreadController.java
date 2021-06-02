package by.bsuir.kazhamiakin.controller;

import java.io.IOException;

import by.bsuir.kazhamiakin.bean.Pulse;
import by.bsuir.kazhamiakin.exeption.bluetooth.BluetoothException;

/**
 * @author Pablo on 08.03.2021
 * @project Health
 */
public class ThreadController {
    public void disconnection(Pulse pulse)throws BluetoothException,IOException{
        if (pulse != null){
            pulse.cancelTimer();
            pulse.clearChart();
            if (pulse.getConnectedThread() != null && pulse.getConnectedThread().isConnected())
                pulse.getConnectedThread().disconnect();
            if (pulse.getBluetoothConnector().isConnected())
                pulse.getBluetoothConnector().disconnect();
        }
    }
}
