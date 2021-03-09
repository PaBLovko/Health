package by.bsuir.health.dao.preference;

import java.io.IOException;

import by.bsuir.health.exeption.bluetooth.BluetoothException;


/**
 * @author Pablo on 14.01.2021
 * @project Health
 */
public interface OnDelayChangedListener {
    void OnDelayChanged() throws BluetoothException, IOException;
}
