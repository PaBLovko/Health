package by.bsuir.health.controller;

import java.io.IOException;

import by.bsuir.health.bean.Pulse;
import by.bsuir.health.exeption.bluetooth.BluetoothException;

/**
 * @author Pablo on 08.03.2021
 * @project Health
 */
public class ThreadController {
    public void disconnection(Pulse pulse)throws BluetoothException,IOException{
        if (pulse != null){
            pulse.cancelTimer();
            if (pulse.getBluetoothConnector().isConnected())
                pulse.getBluetoothConnector().disconnect();
            if (pulse.getConnectedThread() != null && pulse.getConnectedThread().isConnected())
                pulse.getConnectedThread().disconnect();
        }
    }
}
