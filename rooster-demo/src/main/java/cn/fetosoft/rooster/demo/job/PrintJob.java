package cn.fetosoft.rooster.demo.job;

import cn.fetosoft.rooster.core.RoosterConfig;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @Title：打印定时
 * @Author：guobingbing
 * @Date 2020/1/22 16:50
 * @Description
 * @Version
 */
public class PrintJob implements Job {

	/**
	 * 日志
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(PrintJob.class);

	@Autowired
	private RoosterConfig roosterConfig;

	/**
	 *
	 */
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		JobDetail jobDetail = context.getJobDetail();
		JobDataMap map = jobDetail.getJobDataMap();
		try {
			TimeUnit.SECONDS.sleep(2);
			//throw new JobExecutionException("The job is error!");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		String date = DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss");
		LOGGER.info("Task named ()/{}-{} start!!! >>> {}", roosterConfig.getZkClusterPath(),
				map.getString("code"), map.getString("name"), date);
	}
}
