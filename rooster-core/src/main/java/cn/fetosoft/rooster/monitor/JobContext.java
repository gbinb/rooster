package cn.fetosoft.rooster.monitor;

import cn.fetosoft.rooster.core.TaskInfo;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * @author guobingbing
 * @create 2020/2/1 9:43
 */
public class JobContext implements Serializable {

	/**
	 * task info
	 */
	private TaskInfo taskInfo;

	private Date fireTime;

	private Date prevFireTime;

	private Date scheduledFireTime;

	private long runTime;

	/**
	 * User's own data
	 */
	private Map<String, Object> dataMap;

	private boolean isException = false;

	private String errorMsg;

	public TaskInfo getTaskInfo() {
		return taskInfo;
	}

	public void setTaskInfo(TaskInfo taskInfo) {
		this.taskInfo = taskInfo;
	}

	public Date getFireTime() {
		return fireTime;
	}

	public void setFireTime(Date fireTime) {
		this.fireTime = fireTime;
	}

	public Date getPrevFireTime() {
		return prevFireTime;
	}

	public void setPrevFireTime(Date prevFireTime) {
		this.prevFireTime = prevFireTime;
	}

	public Date getScheduledFireTime() {
		return scheduledFireTime;
	}

	public void setScheduledFireTime(Date scheduledFireTime) {
		this.scheduledFireTime = scheduledFireTime;
	}

	public long getRunTime() {
		return runTime;
	}

	public void setRunTime(long runTime) {
		this.runTime = runTime;
	}

	public Map<String, Object> getDataMap() {
		return dataMap;
	}

	public void setDataMap(Map<String, Object> dataMap) {
		this.dataMap = dataMap;
	}

	public boolean isException() {
		return isException;
	}

	public void setException(boolean exception) {
		isException = exception;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
}
