package cn.fetosoft.rooster.demo.job;

import cn.fetosoft.rooster.monitor.JobContext;
import cn.fetosoft.rooster.monitor.JobExecListener;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author guobingbing
 * @create 2020/2/1 11:38
 */
@Component
public class JobMonitorDemo implements JobExecListener {

	/**
	 * log
	 */
	private static final Logger logger = LoggerFactory.getLogger(JobMonitorDemo.class);

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
	}
}
