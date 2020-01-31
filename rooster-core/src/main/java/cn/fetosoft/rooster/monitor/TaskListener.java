package cn.fetosoft.rooster.monitor;

import cn.fetosoft.rooster.core.TaskInfo;
import org.quartz.SchedulerException;

/**
 * 任务监控
 * @author guobingbing
 * @create 2020/1/30 9:42
 */
public interface TaskListener {

	/**
	 * 任务启动
	 * @param taskInfo
	 * @param e
	 */
	void start(TaskInfo taskInfo, SchedulerException e);

	/**
	 * 任务停止
	 * @param taskInfo
	 * @param e
	 */
	void stop(TaskInfo taskInfo, SchedulerException e);
}
