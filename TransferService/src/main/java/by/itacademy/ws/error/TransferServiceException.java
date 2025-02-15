package by.itacademy.ws.error;

public class TransferServiceException extends RuntimeException {

    public TransferServiceException(String message) {
        super(message);
    }

    public TransferServiceException(Throwable cause) {
        super(cause);
    }
}
