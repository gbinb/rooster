package cn.fetosoft.rooster.store;

import cn.fetosoft.rooster.core.Result;
import cn.fetosoft.rooster.core.TaskInfo;

/**
 * 任务信息持久化操作
 * @author guobingbing
 * @create 2020/1/27 10:17
 */
public interface TaskPersistenceService {

	/**
	 * 新增
	 * @param taskInfo
	 * @return
	 */
	Result add(TaskInfo taskInfo);

	/**
	 * 修改
	 * @param taskInfo
	 * @return
	 */
	Result modify(TaskInfo taskInfo);

	/**
	 * 删除
	 * @param taskInfo
	 * @return
	 */
	Result remove(TaskInfo taskInfo);

	/**
	 * 启动任务
	 * @param taskInfo
	 * @return
	 */
	Result start(TaskInfo taskInfo);

	/**
	 * 停止任务
	 * @param taskInfo
	 * @return
	 */
	Result stop(TaskInfo taskInfo);
}
