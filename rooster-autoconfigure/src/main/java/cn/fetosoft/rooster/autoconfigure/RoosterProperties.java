package cn.fetosoft.rooster.autoconfigure;

import cn.fetosoft.rooster.core.RoosterConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author guobingbing
 * @create 2020-05-28 09:44
 */
@ConfigurationProperties(prefix = "rooster")
public class RoosterProperties implements RoosterConfig {

	private String zkHost;

	private String zkTaskPath;

	private String zkClusterPath;

	private boolean enableSub = true;

	@Override
	public String getZkHost() {
		return zkHost;
	}

	public void setZkHost(String zkHost) {
		this.zkHost = zkHost;
	}

	@Override
	public String getZkTaskPath() {
		return zkTaskPath;
	}

	public void setZkTaskPath(String zkTaskPath) {
		this.zkTaskPath = zkTaskPath;
	}

	@Override
	public String getZkClusterPath() {
		return zkClusterPath;
	}

	public void setZkClusterPath(String zkClusterPath) {
		this.zkClusterPath = zkClusterPath;
	}

	@Override
	public boolean isEnableSub() {
		return enableSub;
	}

	public void setEnableSub(boolean enableSub) {
		this.enableSub = enableSub;
	}
}

