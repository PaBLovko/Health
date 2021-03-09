package by.bsuir.health.controller;

import java.io.IOException;

import by.bsuir.health.bean.BluetoothConnector;
import by.bsuir.health.exeption.bluetooth.BluetoothException;

import static by.bsuir.health.service.CheckedChangeService.BUZZER;
import static by.bsuir.health.service.CheckedChangeService.LED;

/**
 * @author Pablo on 07.03.2021
 * @project Health
 */
public class CheckedChangeController {

    public void enableCheckBox(BluetoothConnector bluetoothConnector,
                               BluetoothConnector.ConnectedThread connectedThread,
                               int box, boolean state) throws BluetoothException, IOException {
        if (bluetoothConnector.isConnected()) {
            String command = "";
            switch (box) {
                case BUZZER:
                    command = (state) ? "buzzer on#" : "buzzer off#";
                    break;
                case LED:
                    command = (state) ? "led on#" : "led off#";
                    break;
            }
            connectedThread.write(command.getBytes());
        }
    }
}
