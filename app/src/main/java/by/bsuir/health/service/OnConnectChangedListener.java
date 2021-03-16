package by.bsuir.health.service;

import by.bsuir.health.bean.BluetoothConnector;

public interface OnConnectChangedListener {
    void OnConnectChanged(BluetoothConnector.ConnectedThread connectedThread);
}

