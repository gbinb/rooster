package cn.fetosoft.rooster.core;

/**
 * @Title：定时服务接口
 * @Author：guobingbing
 * @Date 2020/1/22 10:30
 * @Description
 * @Version
 */
public interface ScheduledService {

	/**
	 * 新增并启动任务
	 * @author guobingbing
	 * @date 2020/1/22 11:00
	 * @param taskInfo
	 * @return cn.fetosoft.rooster.core.Result
	 */
	Result start(TaskInfo taskInfo);

	/**
	 * 变更并重启任务
	 * @author guobingbing
	 * @date 2020/1/22 11:04
	 * @param taskInfo
	 * @return cn.fetosoft.rooster.core.Result
	 */
	Result modify(TaskInfo taskInfo);

	/**
	 * 停止任务
	 * @author guobingbing
	 * @date 2020/1/22 11:05
	 * @param taskInfo
	 * @return cn.fetosoft.rooster.core.Result
	 */
	Result stop(TaskInfo taskInfo);
}
