package unnet.weixin.netdisk.entity;

public class RestResult<T> {

	private int code;  // 返回状态码：成功(0)， 失败(其他)
	private String message;  // 状态码对应信息
	private T data;  // 包装的对象

	public RestResult() {
		this.code = 0;
		this.message = "success";
		this.data = null;
	}

	public RestResult(int code, String msg) {
		this();
		this.code = code;
		this.message = msg;
	}

	public RestResult(int code, String msg, T data) {
		this();
		this.code = code;
		this.message = msg;
		this.data = data;
	}

	public RestResult(T data) {
		this();
		this.data = data;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

}
