package cn.fetosoft.rooster.demo.configs;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author guobingbing
 * @create 2020/1/26 18:58
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		//默认静态资源处理
		registry.addResourceHandler("/static/**").addResourceLocations("/static/");
	}
}
