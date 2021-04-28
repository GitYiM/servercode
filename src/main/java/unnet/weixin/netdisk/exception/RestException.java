package unnet.weixin.netdisk.exception;


public class RestException extends RuntimeException {

    private static final long serialVersionUID = 9192059935840254980L;

    private final ErrorCodeAndMsg errorCodeAndMsg;

    public RestException(ErrorCodeAndMsg errorCodeAndMsg) {
        this.errorCodeAndMsg = errorCodeAndMsg;
    }

    public ErrorCodeAndMsg getErrorCodeAndMsg() {
        return errorCodeAndMsg;
    }
}
