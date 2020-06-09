package cn.fetosoft.rooster.core;

import cn.fetosoft.rooster.utils.NetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author guobingbing
 * @create 2020/1/29 14:19
 */
public class DefaultScheduled extends AbstractScheduled {

	private static final Logger logger = LoggerFactory.getLogger(DefaultScheduled.class);
	private static final Set<Long> ipSet = new HashSet<>(16);

	static {
		List<String> localIP = NetUtil.getLocalIP();
		for(String host : localIP){
			logger.info("localhost ------ {}", host);
			ipSet.add(NetUtil.ipToLong(host));
		}
	}

	/**
	 * 判断是否本身节点
	 *
	 * @param taskInfo
	 * @return boolean
	 * @author guobingbing
	 * @date 2017/12/11 18:57
	 */
	@Override
	protected boolean isClusterExec(TaskInfo taskInfo) {
		return ipSet.contains(NetUtil.ipToLong(taskInfo.getClusterIP()));
	}
}
