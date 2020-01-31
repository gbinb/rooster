package cn.fetosoft.rooster.monitor;

import cn.fetosoft.rooster.core.TaskInfo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.quartz.*;
import org.quartz.listeners.SchedulerListenerSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @Title：任务状态监控
 * @Author：guobingbing
 * @Date 2020/1/22 17:50
 * @Description
 * @Version
 */
public final class SchedulerMonitor extends SchedulerListenerSupport {

	private static final Logger logger = LoggerFactory.getLogger(SchedulerMonitor.class);
	private static final Map<String, TaskInfo> taskMap = new HashMap<>();
	private Scheduler scheduler;
	private TaskListener taskListener;

	public SchedulerMonitor(Scheduler scheduler, TaskListener taskListener){
		this.scheduler = scheduler;
		this.taskListener = taskListener;
	}

	@Override
	public void jobScheduled(Trigger trigger) {
		TaskInfo taskInfo = null;
		SchedulerException exception = null;
		try {
			JobDataMap map = this.scheduler.getJobDetail(trigger.getJobKey()).getJobDataMap();
			JSONObject jsonObject = new JSONObject();
			jsonObject.putAll(map);
			taskInfo = JSON.toJavaObject(jsonObject, TaskInfo.class);
			taskInfo.addAllParams(jsonObject);
			taskMap.put(trigger.getJobKey().getName(), taskInfo);
		} catch (SchedulerException e) {
			exception = e;
			logger.error("jobScheduled", e);
		} finally {
			if(taskListener!=null){
				taskListener.start(taskInfo, exception);
			}
		}
	}

	@Override
	public void jobDeleted(JobKey jobKey) {
		TaskInfo taskInfo = null;
		SchedulerException exception = null;
		try {
			taskInfo = taskMap.get(jobKey.getName());
			if(taskInfo==null){
				throw new SchedulerException("The task named "+jobKey.getName()+" not exists!");
			}
		} catch (SchedulerException e) {
			exception = e;
			logger.error("jobDeleted", e);
		} finally {
			if(taskListener!=null){
				taskListener.stop(taskInfo, exception);
			}
		}
	}
}
