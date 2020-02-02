package cn.fetosoft.rooster.core;

/**
 * @author guobingbing
 * @create 2020/2/2 11:42
 */
public class TaskException extends Exception {

	public TaskException(){
		super();
	}

	public TaskException(String message) {
		super(message);
	}

	public TaskException(String message, Throwable cause) {
		super(message, cause);
	}

}
