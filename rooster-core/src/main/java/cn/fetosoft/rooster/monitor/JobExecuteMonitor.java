package cn.fetosoft.rooster.monitor;

import cn.fetosoft.rooster.core.TaskInfo;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Title：job监控
 * @Author：guobingbing
 * @Date 2020/1/22 15:50
 * @Description
 * @Version
 */
public class JobExecuteMonitor implements JobListener {

	/**
	 * 日志
	 */
	private static final Logger logger = LoggerFactory.getLogger(JobExecuteMonitor.class);

	private JobExecListener jobExecListener;

	public JobExecuteMonitor(JobExecListener jobExecListener){
		this.jobExecListener = jobExecListener;
	}

	/**
	 *
	 */
	@Override
	public String getName() {
		return "jobRunningMonitor";
	}

	/**
	 *
	 */
	@Override
	public void jobToBeExecuted(JobExecutionContext context) {
		if(jobExecListener!=null){
			JobContext jobContext = new JobContext();
			JobDataMap dataMap = context.getJobDetail().getJobDataMap();
			TaskInfo taskInfo = new TaskInfo();
			taskInfo.addAllParams(dataMap).buildTask();
			jobContext.setTaskInfo(taskInfo);
			jobContext.setFireTime(context.getFireTime());
			jobContext.setPrevFireTime(context.getPreviousFireTime());
			jobContext.setScheduledFireTime(context.getScheduledFireTime());
			context.put(context.getFireInstanceId(), jobContext);
			try{
				jobExecListener.beforeExec(jobContext);
			}catch(Exception e){
				logger.error(e.getMessage());
			}
		}
	}

	/**
	 *
	 */
	@Override
	public void jobExecutionVetoed(JobExecutionContext context) {
	}

	/**
	 *
	 */
	@Override
	public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
		if(jobExecListener!=null){
			if(context.get(context.getFireInstanceId())!=null){
				JobContext jobContext = (JobContext) context.get(context.getFireInstanceId());
				jobContext.setRunTime(context.getJobRunTime());
				if(jobException!=null) {
					jobContext.setException(true);
					jobContext.setErrorMsg(jobException.getMessage());
				}
				try{
					jobExecListener.afterExec(jobContext);
				}catch(Exception e){
					logger.error(e.getMessage());
				}
			}
		}
	}
}
