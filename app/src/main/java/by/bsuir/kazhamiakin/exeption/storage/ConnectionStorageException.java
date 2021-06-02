package by.bsuir.kazhamiakin.exeption.storage;

/**
 * @author Pablo on 29.01.2021
 * @project Health
 */
public class ConnectionStorageException extends StorageException {
    public ConnectionStorageException(Throwable throwable) {
        super(throwable);
    }
    public ConnectionStorageException(String message) {
        super(message);
    }
}
