package by.bsuir.health.controller;

import static by.bsuir.health.service.CheckedChangeService.BUZZER;
import static by.bsuir.health.service.CheckedChangeService.LED;

/**
 * @author Pablo on 07.03.2021
 * @project Health
 */
public class CheckedChangeController {

    public String enableCheckBox(int box, boolean state){
        String command = null;
//        if (bluetoothConnector.isConnected()) {
            switch (box) {
                case BUZZER:
                    command = (state) ? "buzzer on#" : "buzzer off#";
                    break;
                case LED:
                    command = (state) ? "led on#" : "led off#";
                    break;
            }
//            connectedThread.write(command.getBytes());
//        }
        return command;
    }
}
