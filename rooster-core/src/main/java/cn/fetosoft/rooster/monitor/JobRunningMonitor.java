package cn.fetosoft.rooster.monitor;

import org.quartz.JobDetail;
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
public class JobRunningMonitor implements JobListener {

	/**
	 * 日志
	 */
	private static final Logger logger = LoggerFactory.getLogger(JobRunningMonitor.class);

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
		logger.info("11111");
	}

	/**
	 *
	 */
	@Override
	public void jobExecutionVetoed(JobExecutionContext context) {
		logger.info("22222");
	}

	/**
	 *
	 */
	@Override
	public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
		logger.info("33333");
	}
}
