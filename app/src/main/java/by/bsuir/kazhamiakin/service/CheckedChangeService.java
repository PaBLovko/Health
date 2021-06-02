package by.bsuir.kazhamiakin.service;

import android.support.v4.app.FragmentActivity;
import android.widget.CompoundButton;

import java.util.ArrayList;
import java.util.List;

import by.bsuir.kazhamiakin.controller.BluetoothConnectorController;
import by.bsuir.kazhamiakin.controller.CheckedChangeController;
import by.bsuir.kazhamiakin.ui.ViewActivity;

/**
 * @author Pablo on 07.03.2021
 * @project Health
 */
public class CheckedChangeService implements CompoundButton.OnCheckedChangeListener{

    public static final int BUZZER = 30;
    public static final int LED = 31;
    private static final List<OnSwitchChangedListener> listeners = new ArrayList<>();

    private final ViewActivity viewActivity;
    private final FragmentActivity fragmentActivity;


    public CheckedChangeService(ViewActivity viewActivity, FragmentActivity fragmentActivity) {
        this.viewActivity = viewActivity;
        this.fragmentActivity = fragmentActivity;
    }

    public static void addItemListener(OnSwitchChangedListener l) {
        listeners.add(l);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView.equals(viewActivity.getSwitchEnableBt())) {
            new BluetoothConnectorController().enableBt(fragmentActivity, isChecked);
            if (!isChecked) viewActivity.showFrameMessage();
        } else if (buttonView.equals(viewActivity.getSwitchBuzzer())) {
            String command = new CheckedChangeController().enableCheckBox(BUZZER, isChecked);
            for (OnSwitchChangedListener l : listeners)
                l.OnSwitchChanged(command);
        }
        else if (buttonView.equals(viewActivity.getSwitchLed())) {
            String command = new CheckedChangeController().enableCheckBox(LED, isChecked);
            for (OnSwitchChangedListener l : listeners)
                l.OnSwitchChanged(command);
        }
    }
}
