package cn.fetosoft.rooster.core;

import org.quartz.spi.JobFactory;
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
}
