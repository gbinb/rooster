package cn.fetosoft.rooster.core;

/**
 * @Title：执行结果
 * @Author：guobingbing
 * @Date 2020/1/16 16:58
 * @Description
 * @Version
 */
public enum Result {

	/**
	 * 成功
	 */
	SUCCESS("SUCCESS", "成功"),

	/**
	 * 失败
	 */
	FAIL("ERROR", "失败"),

	/**
	 * 无操作
	 */
	NONE("NONE", "无操作");

	private String code;

	private String msg;

	Result(String code, String msg){
		this.code = code;
		this.msg = msg;
	}

	public Result setMsg(String msg){
		this.msg = msg;
		return this;
	}

	public String getCode(){
		return this.code;
	}

	public String getMsg(){
		return this.msg;
	}

	@Override
	public String toString(){
		return "{\"code\":\"" + code + "\",\"msg\":\"" + msg + "\"}";
	}
}
