package cn.fetosoft.rooster.demo.job;

import cn.fetosoft.rooster.core.TaskAction;
import cn.fetosoft.rooster.core.TaskInfo;
import cn.fetosoft.rooster.demo.data.TaskDAO;
import cn.fetosoft.rooster.monitor.TaskListener;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author guobingbing
 * @create 2020/1/30 10:12
 */
@Component
public class TaskListenerDemo implements TaskListener {

	@Autowired
	private TaskDAO taskDAO;

	/**
	 * 任务启动
	 *
	 * @param taskInfo
	 * @param e
	 */
	@Override
	public void start(TaskInfo taskInfo, SchedulerException e) {
		System.out.println("Start------------------" + taskInfo.getCode());
		taskDAO.updateTaskStatus(taskInfo.getCode(), TaskAction.START);
	}

	/**
	 * 任务停止
	 *
	 * @param taskInfo
	 * @param e
	 */
	@Override
	public void stop(TaskInfo taskInfo, SchedulerException e) {
		System.out.println("Stop------------------" + taskInfo.getCode());
		taskDAO.updateTaskStatus(taskInfo.getCode(), TaskAction.STOP);
	}
}
