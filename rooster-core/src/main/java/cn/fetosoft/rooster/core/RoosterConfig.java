package cn.fetosoft.rooster.core;


/**
 * 配置类
 * @author guobingbing
 * @create 2020/1/27 11:26
 */
public interface RoosterConfig {

	String getZkHost();

	String getZkTaskPath();

	String getZkClusterPath();

	/**
	 * 是否启用订阅服务
	 */
	boolean isEnableSub();
}
