package cn.fetosoft.rooster.core;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.spi.JobFactory;
import org.springframework.scheduling.SchedulingException;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;

/**
 * @author guobingbing
 * @create 2020/1/31 9:57
 */
@Component("roosterScheduledFactory")
public class RoosterScheduledFactory extends SchedulerFactoryBean {

	@Resource(name = "roosterJobFactory")
	@Override
	public void setJobFactory(JobFactory jobFactory) {
		super.setJobFactory(jobFactory);
	}

	/**
	 * Shut down the Quartz scheduler on bean factory shutdown,
	 * stopping all scheduled jobs.
	 */
	@Override
	public void destroy() throws SchedulerException {
		Scheduler scheduler = this.getScheduler();
		if (scheduler != null) {
			scheduler.clear();
			super.destroy();
		}
	}

	@Override
	public void stop() throws SchedulingException {
	}
}
