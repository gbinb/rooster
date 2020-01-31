package cn.fetosoft.rooster.store;

import cn.fetosoft.rooster.core.Result;
import cn.fetosoft.rooster.core.TaskInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 任务持久化操作
 * @author guobingbing
 * @create 2020/1/27 10:29
 */
@Component
public abstract class AbstractTaskPersistence implements TaskPersistenceService {

	/**
	 * 日志
	 */
	private static final Logger logger = LoggerFactory.getLogger(AbstractTaskPersistence.class);

	/**
	 * 新增
	 *
	 * @param taskInfo
	 * @return
	 */
	@Override
	public Result add(TaskInfo taskInfo) {

		return null;
	}

	/**
	 * 修改
	 *
	 * @param taskInfo
	 * @return
	 */
	@Override
	public Result modify(TaskInfo taskInfo) {
		return null;
	}

	/**
	 * 删除
	 *
	 * @param taskInfo
	 * @return
	 */
	@Override
	public Result remove(TaskInfo taskInfo) {
		return null;
	}

	/**
	 * 启动任务
	 *
	 * @param taskInfo
	 * @return
	 */
	@Override
	public Result start(TaskInfo taskInfo) {
		return null;
	}

	/**
	 * 停止任务
	 *
	 * @param taskInfo
	 * @return
	 */
	@Override
	public Result stop(TaskInfo taskInfo) {
		return null;
	}

	protected abstract Result doAdd(TaskInfo taskInfo);
}
