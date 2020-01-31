package cn.fetosoft.rooster.core;

import cn.fetosoft.rooster.utils.NetUtil;
import org.springframework.stereotype.Component;

/**
 * @author guobingbing
 * @create 2020/1/29 14:19
 */
@Component
public class DefaultScheduled extends AbstractScheduled {

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
		String localIP = NetUtil.getLocalIP().get(0);
		if(NetUtil.ipToLong(localIP)==NetUtil.ipToLong(taskInfo.getClusterIP())) {
			return true;
		}else{
			return false;
		}
	}

}
