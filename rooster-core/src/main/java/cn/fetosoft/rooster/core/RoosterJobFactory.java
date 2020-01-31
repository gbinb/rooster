package cn.fetosoft.rooster.core;

import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.scheduling.quartz.AdaptableJobFactory;
import org.springframework.stereotype.Component;

/**
 * @author guobingbing
 * @create 2020/1/31 9:52
 */
@Component("roosterJobFactory")
public class RoosterJobFactory extends AdaptableJobFactory {

	@Autowired
	private AutowireCapableBeanFactory autowireCapableBeanFactory;

	@Override
	protected Object createJobInstance(TriggerFiredBundle bundle) throws Exception {
		//调用父类的方法
		Object jobInstance = super.createJobInstance(bundle);
		//进行注入
		autowireCapableBeanFactory.autowireBean(jobInstance);
		return jobInstance;
	}
}
