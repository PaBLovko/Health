package by.bsuir.health.bluetooth.exception;

/**
 * @author Pablo on 29.01.2021
 * @project Health
 */
public class BluetoothException extends Exception {
    public BluetoothException(Throwable throwable) {
        super(throwable);
    }
    public BluetoothException(String message) {
        super(message);
    }
    public BluetoothException() {
    }
}