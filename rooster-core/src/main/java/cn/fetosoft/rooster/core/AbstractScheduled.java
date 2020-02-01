package cn.fetosoft.rooster.core;

import cn.fetosoft.rooster.monitor.JobExecListener;
import cn.fetosoft.rooster.monitor.JobExecuteMonitor;
import cn.fetosoft.rooster.monitor.SchedulerMonitor;
import cn.fetosoft.rooster.monitor.TaskListener;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.annotation.Resource;

/**
 * @Title：定时服务
 * @Author：guobingbing
 * @Date 2017/12/6 15:46
 * @Description
 * @Version
 */
public abstract class AbstractScheduled implements ScheduledService, InitializingBean, DisposableBean, ApplicationContextAware {

	/**
	 * 记录日志
	 */
	private static final Logger logger = LoggerFactory.getLogger(AbstractScheduled.class);
	private static String JOB_GROUP_NAME = "ROOSTER_JOBGROUP";
	private static String TRIGGER_GROUP_NAME = "ROOSTER_TRIGGERGROUP";
	private ApplicationContext applicationContext;

	@Resource(name = "roosterScheduledFactory")
	private Scheduler scheduler;

	@Autowired(required = false)
	private TaskListener taskListener;

	@Autowired(required = false)
	private JobExecListener jobExecListener;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	/**
	 * 系统启动时自动恢复定时任务
	 * @author guobingbing
	 * @date 2020/1/22 14:19
	 * @param
	 * @return void
	 */
	@Override
	public void afterPropertiesSet() throws Exception{
		this.addScheduledMonitor();
	}

	/**
	 * Add and start the task
	 * @author guobingbing
	 * @date 2020/1/22 11:46
	 * @param taskInfo
	 * @return cn.fetosoft.rooster.core.Result
	 */
	@Override
	public Result start(TaskInfo taskInfo) {
		Result result = Result.FAIL;
		try{
			boolean isClusterExec = this.isClusterExec(taskInfo);
			if(!isClusterExec){
				logger.info("Start on other nodes! >>> {}", taskInfo.getClusterIP());
				return Result.NONE;
			}
			Class jobClass = Class.forName(taskInfo.getJobClass());
			JobDetail jobDetail = JobBuilder.newJob(jobClass)
					.withIdentity(taskInfo.getCode(), JOB_GROUP_NAME).build();
			if(scheduler.checkExists(jobDetail.getKey())){
				logger.info("The job named {}-{} exists!", taskInfo.getCode(), taskInfo.getName());
				result.setMsg("The job named " + taskInfo.getCode() + " exists!!");
				return result;
			}
			logger.info("Ready to add and start task >>>{} >>>{} >>>{}", taskInfo.getCode(), taskInfo.getName(), taskInfo.getClusterIP());
			jobDetail.getJobDataMap().putAll(taskInfo.getParamsMap());
			ScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(taskInfo.getExpression());
			Trigger trigger = TriggerBuilder.newTrigger().withIdentity(taskInfo.getCode(), TRIGGER_GROUP_NAME)
					.withSchedule(scheduleBuilder).build();
			scheduler.scheduleJob(jobDetail, trigger);
			if(!scheduler.isShutdown()){
				scheduler.start();
				result = Result.SUCCESS;
				logger.info("The task start success >>>{} >>>{} >>>{}", taskInfo.getCode(), taskInfo.getName(), taskInfo.getClusterIP());
			}
		}catch(Exception e){
			result.setMsg(e.getMessage());
			logger.error("start", e);
		}
		return result;
	}

	/**
	 * 修改执行任务
	 * @author guobingbing
	 * @date 2020/1/22 13:54
	 * @param taskInfo
	 * @return cn.fetosoft.rooster.core.Result
	 */
	@Override
	public Result modify(TaskInfo taskInfo){
		Result result = Result.FAIL;
		try{
			boolean isClusterExec = this.isClusterExec(taskInfo);
			if(!isClusterExec){
				logger.info("Modify on other nodes! >>> {}", taskInfo.getClusterIP());
				return Result.NONE;
			}
			logger.info("Ready to modify and start task >>>{} >>>{} >>>{}", taskInfo.getCode(), taskInfo.getName(), taskInfo.getClusterIP());
			TriggerKey triggerKey = TriggerKey.triggerKey(taskInfo.getCode(), TRIGGER_GROUP_NAME);
			CronTrigger cronTrigger = (CronTrigger) scheduler.getTrigger(triggerKey);
			if(cronTrigger!=null) {
				//判断表达式是否无变化
				if(!cronTrigger.getCronExpression().equalsIgnoreCase(taskInfo.getExpression())) {
					result = this.stop(taskInfo);
					if(result == Result.SUCCESS) {
						result = this.start(taskInfo);
						logger.info("The task modify success >>>{} >>>{} >>>{}", taskInfo.getCode(), taskInfo.getName(), taskInfo.getClusterIP());
					}
				}
			}
		}catch(Exception e){
			result.setMsg(e.getMessage());
			logger.error("modify", e);
		}
		return result;
	}

	/**
	 * 停止任务
	 * @author guobingbing
	 * @date 2020/1/22 13:55
	 * @param taskInfo
	 * @return cn.fetosoft.rooster.core.Result
	 */
	@Override
	public Result stop(TaskInfo taskInfo){
		Result result = Result.FAIL;
		try{
			boolean isClusterExec = this.isClusterExec(taskInfo);
			if(!isClusterExec){
				logger.info("Stop on other nodes! >>> {}", taskInfo.getClusterIP());
				return Result.NONE;
			}
			logger.info("Ready to stop task >>>{} >>>{} >>>{}", taskInfo.getCode(), taskInfo.getName(), taskInfo.getClusterIP());
			TriggerKey triggerKey = TriggerKey.triggerKey(taskInfo.getCode(), TRIGGER_GROUP_NAME);
			//停止触发器
			scheduler.pauseTrigger(triggerKey);
			//移除触发器
			scheduler.unscheduleJob(triggerKey);
			scheduler.deleteJob(JobKey.jobKey(taskInfo.getCode(), JOB_GROUP_NAME));
			result = Result.SUCCESS;
			logger.info("The task stop success >>>{} >>>{} >>>{}", taskInfo.getCode(), taskInfo.getName(), taskInfo.getClusterIP());
		}catch(Exception e){
			result.setMsg(e.getMessage());
			logger.error("stop", e);
		}
		return result;
	}

	/**
	 * 增加定时监控
	 * @author guobingbing
	 * @date 2020/1/22 16:04
	 * @param
	 * @return void
	 */
	private void addScheduledMonitor(){
		try {
			if(this.taskListener!=null){
				SchedulerMonitor schedulerMonitor = new SchedulerMonitor(this.scheduler, this.taskListener);
				this.scheduler.getListenerManager().addSchedulerListener(schedulerMonitor);
			}
			if(this.jobExecListener!=null) {
				JobListener jobListener = new JobExecuteMonitor(this.jobExecListener);
				this.scheduler.getListenerManager().addJobListener(jobListener);
			}
		} catch (SchedulerException e) {
			logger.error("addScheduledMonitor", e);
		}
	}

	/**
	 * destroy
	 * @author guobingbing
	 * @date 2020/1/31 16:42
	 * @param
	 * @return void
	 */
	@Override
	public void destroy() throws Exception{
		if(scheduler!=null && !scheduler.isShutdown()){
			scheduler.clear();
			scheduler.shutdown();
		}
	}

	/**
	 * 判断是否本身节点
	 * @author guobingbing
	 * @date 2017/12/11 18:57
	 * @param taskInfo
	 * @return boolean
	 */
	protected abstract boolean isClusterExec(TaskInfo taskInfo);
}
