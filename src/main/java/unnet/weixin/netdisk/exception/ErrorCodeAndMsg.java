package unnet.weixin.netdisk.exception;

public enum ErrorCodeAndMsg {

    GET_SUCCESS(1, "请求成功");

    private int code;

    private String msg;

    ErrorCodeAndMsg(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
