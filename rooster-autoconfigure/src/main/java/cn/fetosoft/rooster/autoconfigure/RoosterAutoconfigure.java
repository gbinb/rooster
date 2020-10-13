package cn.fetosoft.rooster.autoconfigure;

import cn.fetosoft.rooster.core.DefaultScheduled;
import cn.fetosoft.rooster.core.ScheduledService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author guobingbing
 * @create 2020-05-28 10:40
 */
@Configuration
@ConditionalOnClass({DefaultScheduled.class})
@EnableConfigurationProperties(RoosterProperties.class)
public class RoosterAutoconfigure {

	@Bean
	public ScheduledService createScheduled(){
		return new DefaultScheduled();
	}
}
