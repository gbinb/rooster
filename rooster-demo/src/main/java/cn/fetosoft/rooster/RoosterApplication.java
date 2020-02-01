package cn.fetosoft.rooster;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * @Title：启动类
 * @Author：guobingbing
 * @Date 2020/1/22 16:35
 * @Description
 * @Version
 */
@RestController
@EnableWebSocket
@ServletComponentScan
@SpringBootApplication
public class RoosterApplication {

	/**
	 * 日志
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(RoosterApplication.class);

	@RequestMapping
	public String welcome(){
		return "Hello world!";
	}

	@Bean
	public ServerEndpointExporter serverEndpointExporter() {
		return new ServerEndpointExporter();
	}

	public static void main(String[] args) {
		SpringApplication application = new SpringApplication(RoosterApplication.class);
		application.run(args);
		LOGGER.info("The application is start!!!");
	}
}
