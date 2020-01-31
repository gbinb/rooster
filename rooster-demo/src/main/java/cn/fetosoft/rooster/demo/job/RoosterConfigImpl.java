package cn.fetosoft.rooster.demo.job;

import cn.fetosoft.rooster.core.RoosterConfig;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 配置类
 * @author guobingbing
 * @create 2020/1/27 11:26
 */
@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "rooster")
public class RoosterConfigImpl implements RoosterConfig {

	/**
	 * zk配置
	 */
	private Zookeeper zookeeper;

	@Override
	public String getZkHost() {
		return this.zookeeper.getHost();
	}

	@Override
	public String getZkTaskPath() {
		return this.zookeeper.getTaskPath();
	}

	@Override
	public String getZkClusterPath() {
		return this.zookeeper.getClusterPath();
	}

	/**
	 * 是否启用订阅服务
	 */
	@Override
	public boolean isEnableSub() {
		return this.zookeeper.isEnableSub();
	}

	@Setter
	@Getter
	public static class Zookeeper{

		private String host;

		private String taskPath;

		private String clusterPath;

		/**
		 * 是否启用订阅服务
		 */
		private boolean enableSub;
	}
}
