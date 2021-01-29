package by.bsuir.health.bluetooth.exception;

/**
 * @author Pablo on 29.01.2021
 * @project Health
 */
public class ConnectionBluetoothException extends BluetoothException {
    public ConnectionBluetoothException(Throwable throwable) {
        super(throwable);
    }
    public ConnectionBluetoothException(String message) {
        super(message);
    }
}
