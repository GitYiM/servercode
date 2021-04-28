package unnet.weixin.netdisk.utils;

public class StorageException extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = -7107489265729773149L;

    public StorageException(String message) {
        super(message);
    }

    public StorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
