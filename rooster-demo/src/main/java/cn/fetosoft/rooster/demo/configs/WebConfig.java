package cn.fetosoft.rooster.demo.configs;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author guobingbing
 * @create 2020/1/26 18:58
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

	/**
	 * Http message converter http message converter.
	 *
	 * @return the http message converter
	 */
	@Bean
	public HttpMessageConverter httpMessageConverter() {
		return new MappingJackson2HttpMessageConverter(new JsonMapper());
	}

	@Override
	public void configureMessageConverters(List<HttpMessageConverter<?>> converters){
		Iterator<HttpMessageConverter<?>> iterator = converters.iterator();
		while(iterator.hasNext()){
			HttpMessageConverter<?> converter = iterator.next();
			if(converter instanceof MappingJackson2HttpMessageConverter){
				iterator.remove();
			}
		}

		// 1、需要定义一个convert转换消息的对象;
		FastJsonHttpMessageConverter fastJsonHttpMessageConverter = new FastJsonHttpMessageConverter();
		// 2、添加fastJson的配置信息，比如：是否要格式化返回的json数据;
		FastJsonConfig fastJsonConfig = new FastJsonConfig();
		fastJsonConfig.setSerializerFeatures(
				SerializerFeature.PrettyFormat,
				// list null -> []
				SerializerFeature.WriteNullListAsEmpty,
				SerializerFeature.WriteDateUseDateFormat,
				// String null -> ""
				SerializerFeature.WriteNullStringAsEmpty,
				// Number null -> 0
				SerializerFeature.WriteNullNumberAsZero,
				// Boolean null -> false
				SerializerFeature.WriteNullBooleanAsFalse,
				//禁止循环引用
				SerializerFeature.DisableCircularReferenceDetect
		);
		// 3、处理中文乱码问题
		List<MediaType> fastMediaTypes = new ArrayList<>();
		fastMediaTypes.add(MediaType.APPLICATION_JSON);
		// 4、在convert中添加配置信息.
		fastJsonHttpMessageConverter.setSupportedMediaTypes(fastMediaTypes);
		fastJsonHttpMessageConverter.setFastJsonConfig(fastJsonConfig);
		// 5、将convert添加到converters当中.
		converters.add(fastJsonHttpMessageConverter);
	}

	@Override
	public void configurePathMatch(PathMatchConfigurer configurer) {
		// 系统对外暴露的 URL 不会识别和匹配 .* 后缀
		// 系统不区分 URL 的最后一个字符是否是斜杠 /
		configurer.setUseSuffixPatternMatch(false)
				.setUseTrailingSlashMatch(true);
	}

	@Override
	public void configureDefaultServletHandling(final DefaultServletHandlerConfigurer configurer) {
		// 等价于<mvc:default-servlet-handler />, 对静态资源文件的访问, 将无法 mapping 到 Controller 的 path 交给 default servlet handler 处理
		configurer.enable();
	}

	/**
	 * Validator local validator factory bean.
	 * @return the local validator factory bean
	 */
	@Bean
	public LocalValidatorFactoryBean validator() {
		return new LocalValidatorFactoryBean();
	}

	/**
	 * Gets method validation post processor.
	 * @return the method validation post processor
	 */
	@Bean
	public MethodValidationPostProcessor getMethodValidationPostProcessor() {
		MethodValidationPostProcessor processor = new MethodValidationPostProcessor();
		processor.setValidator(validator());
		return processor;
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		//默认静态资源处理
		registry.addResourceHandler("/static/**").addResourceLocations("classpath:/META-INF/resources/static/");
	}
}
