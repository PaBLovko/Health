package by.bsuir.health.service;

import android.support.v4.app.FragmentActivity;
import android.widget.CompoundButton;

import java.io.IOException;

import by.bsuir.health.bean.BluetoothConnector;
import by.bsuir.health.controller.BluetoothConnectorController;
import by.bsuir.health.controller.CheckedChangeController;
import by.bsuir.health.exeption.bluetooth.BluetoothException;
import by.bsuir.health.ui.ViewActivity;

/**
 * @author Pablo on 07.03.2021
 * @project Health
 */
public class CheckedChangeService implements CompoundButton.OnCheckedChangeListener{

    public static final int BUZZER = 30;
    public static final int LED = 31;

    private final ViewActivity viewActivity;
    private final FragmentActivity fragmentActivity;
    private BluetoothConnector bluetoothConnector;
    private BluetoothConnector.ConnectedThread connectedThread;


    public CheckedChangeService(ViewActivity viewActivity, FragmentActivity fragmentActivity,
                                BluetoothConnector bluetoothConnector,
                                BluetoothConnector.ConnectedThread connectedThread) {
        this.viewActivity = viewActivity;
        this.fragmentActivity = fragmentActivity;
        this.bluetoothConnector = bluetoothConnector;
        this.connectedThread = connectedThread;
    }

    public void setBluetoothConnector(BluetoothConnector bluetoothConnector) {
        this.bluetoothConnector = bluetoothConnector;
    }

    public void setConnectedThread(BluetoothConnector.ConnectedThread connectedThread) {
        this.connectedThread = connectedThread;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView.equals(viewActivity.getSwitchEnableBt())) {
            new BluetoothConnectorController().enableBt(fragmentActivity, isChecked);
            if (!isChecked) viewActivity.showFrameMessage();
        } else if (buttonView.equals(viewActivity.getSwitchBuzzer())) {
            try {
                new CheckedChangeController().enableCheckBox(bluetoothConnector, connectedThread,
                        BUZZER, isChecked);
            } catch (BluetoothException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if (buttonView.equals(viewActivity.getSwitchLed())) {
            try {
                new CheckedChangeController().enableCheckBox(bluetoothConnector, connectedThread,
                        LED, isChecked);
            } catch (BluetoothException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
