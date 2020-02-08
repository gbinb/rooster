package cn.fetosoft.rooster.core;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.core.type.AnnotatedTypeMetadata;
import java.io.IOException;
import java.util.Properties;

/**
 * @author guobingbing
 * @create 2020/2/8 10:45
 */
public class ScheduledCondition implements Condition {

	@Override
	public boolean matches(ConditionContext conditionContext, AnnotatedTypeMetadata annotatedTypeMetadata) {
		Environment env = conditionContext.getEnvironment();
		String defaultScheduled = env.getProperty("defaultScheduled");
		if(StringUtils.isNotBlank(defaultScheduled)){
			return Boolean.parseBoolean(defaultScheduled);
		}
		try {
			Properties properties = PropertiesLoaderUtils.loadAllProperties("rooster.properties");
			defaultScheduled = properties.getProperty("defaultScheduled");
			if(StringUtils.isNotBlank(defaultScheduled)){
				return Boolean.parseBoolean(defaultScheduled);
			}
		} catch (IOException e) {
		}
		return true;
	}
}
