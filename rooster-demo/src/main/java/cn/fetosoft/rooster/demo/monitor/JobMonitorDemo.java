package cn.fetosoft.rooster.demo.monitor;

import cn.fetosoft.rooster.demo.controller.MonitorEvent;
import cn.fetosoft.rooster.monitor.JobContext;
import cn.fetosoft.rooster.monitor.JobExecListener;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * 对job运行的监控
 * @author guobingbing
 * @create 2020/2/1 11:38
 */
@Component
public class JobMonitorDemo implements JobExecListener, ApplicationContextAware {

	/**
	 * log
	 */
	private static final Logger logger = LoggerFactory.getLogger(JobMonitorDemo.class);
	private ApplicationContext applicationContext;

	@Override
	public void beforeExec(JobContext jobContext) {
		logger.info("beginRunJob --- {} --- {}", jobContext.getTaskInfo().getCode(),
				DateFormatUtils.format(jobContext.getFireTime(), "yyyy-MM-dd HH:mm:ss"));
	}

	@Override
	public void afterExec(JobContext jobContext) {
		logger.info("endRunJob --- {} --- {} --- {}", jobContext.getTaskInfo().getCode(),
				DateFormatUtils.format(jobContext.getFireTime(), "yyyy-MM-dd HH:mm:ss"), jobContext.getRunTime());
		if(jobContext.isException()){
			logger.error(jobContext.getErrorMsg());
		}

		MonitorEvent event = new MonitorEvent(jobContext.getTaskInfo().getCode());
		event.setFireTime(DateFormatUtils.format(jobContext.getFireTime(), "yyyy-MM-dd HH:mm:ss"));
		event.setRunTime(jobContext.getRunTime());
		applicationContext.publishEvent(event);
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
}
