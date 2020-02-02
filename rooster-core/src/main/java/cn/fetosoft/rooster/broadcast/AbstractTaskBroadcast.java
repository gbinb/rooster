package cn.fetosoft.rooster.broadcast;

import cn.fetosoft.rooster.core.Result;
import cn.fetosoft.rooster.core.TaskAction;
import cn.fetosoft.rooster.core.TaskException;
import cn.fetosoft.rooster.core.TaskInfo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.CollectionUtils;
import java.util.List;

/**
 * 注册抽象类
 * @author guobingbing
 * @create 2020/1/27 11:14
 */
public abstract class AbstractTaskBroadcast implements TaskBroadcast, InitializingBean, DisposableBean {

	/**
	 * 日志
	 */
	private static final Logger logger = LoggerFactory.getLogger(AbstractTaskBroadcast.class);

	/**
	 * 注册
	 *
	 * @param taskInfo
	 * @return
	 */
	@Override
	public Result broadcast(TaskInfo taskInfo) throws TaskException {
		Result result = Result.FAIL;
		try {
			boolean checkPass = false;
			if(StringUtils.isBlank(taskInfo.getCode())){
				throw new TaskException("Task code cannot be empty!");
			}
			if(taskInfo.getAction()==null){
				throw new TaskException("Task action cannot be null!");
			}
			if(TaskAction.STOP.getCode() == taskInfo.getAction()){
				checkPass = true;
			}else{
				if(StringUtils.isBlank(taskInfo.getExpression())){
					throw new TaskException("Task expression cannot be empty!");
				}
				if(StringUtils.isBlank(taskInfo.getJobClass())){
					throw new TaskException("Task jobClass cannot be empty!");
				}
				if(StringUtils.isBlank(taskInfo.getClusterIP())){
					throw new TaskException("Task clusterIP cannot be empty!");
				}
				checkPass = true;
			}
			if(checkPass) {
				result = this.doBroadcast(taskInfo);
			}
		} catch (TaskException e) {
			throw e;
		} catch (Exception e) {
			result.setMsg(e.getMessage());
			logger.error("broadcast", e);
		}
		return result;
	}

	/**
	 *
	 * @throws Exception
	 */
	@Override
	public final void afterPropertiesSet() throws Exception{
		this.start();
		List<TaskInfo> tasks = this.getInitStartTasks();
		if(!CollectionUtils.isEmpty(tasks)){
			for(TaskInfo task : tasks){
				this.doBroadcast(task);
			}
		}
	}

	/**
	 * 销毁资源
	 * @throws Exception
	 */
	@Override
	public final void destroy() throws Exception {
		this.close();
	}

	/**
	 * 获取初始启动的任务，用于系统启动时便启动任务
	 * @return
	 */
	protected abstract List<TaskInfo> getInitStartTasks();

	/**
	 * 注册启动的任务
 	 * @param taskInfo
	 * @return
	 */
	protected abstract Result doBroadcast(TaskInfo taskInfo) throws Exception;

	/**
	 *
	 */
	protected void start() throws Exception{}

	/**
	 *
	 */
	protected abstract void close();
}