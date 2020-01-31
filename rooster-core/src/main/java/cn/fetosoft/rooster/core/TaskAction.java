package cn.fetosoft.rooster.core;

/**
 * @Title：定时器行为
 * @Author：guobingbing
 * @Date 2020/1/22 10:49
 * @Description
 * @Version
 */
public enum TaskAction {

	/**
	 * 启动
	 */
	START(1),

	/**
	 * 停止
	 */
	STOP(0),

	/**
	 * 修改
	 */
	MODIFY(2);

	private int code;

	TaskAction(int code){
		this.code = code;
	}

	public int getCode(){
		return this.code;
	}
}
