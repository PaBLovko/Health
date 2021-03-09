package by.bsuir.health.exeption.storage;

/**
 * @author Pablo on 29.01.2021
 * @project Health
 */
public class StorageException extends Exception {
    public StorageException(Throwable throwable) {
        super(throwable);
    }
    public StorageException(String message) {
        super(message);
    }
    public StorageException() {}
}