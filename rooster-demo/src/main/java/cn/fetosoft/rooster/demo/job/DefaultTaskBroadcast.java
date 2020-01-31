package cn.fetosoft.rooster.demo.job;

import cn.fetosoft.rooster.broadcast.zookeeper.AbstractZookeeperBroadcast;
import cn.fetosoft.rooster.core.TaskAction;
import cn.fetosoft.rooster.core.TaskInfo;
import cn.fetosoft.rooster.demo.data.TaskDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author guobingbing
 * @create 2020/1/29 14:53
 */
@Component("defaultTaskBroadcast")
public class DefaultTaskBroadcast extends AbstractZookeeperBroadcast {

	@Autowired
	private TaskDAO taskDAO;

	/**
	 * 获取初始启动的任务，用于系统启动时便启动任务
	 *
	 * @return
	 */
	@Override
	protected List<TaskInfo> getInitStartTasks() {
		List<TaskInfo> tasks = new ArrayList<>();
		List<Map<String, Object>> list = taskDAO.getTasks(null, TaskAction.START.getCode());
		if(!CollectionUtils.isEmpty(list)){
			for(Map<String, Object> map : list){
				tasks.add(taskDAO.mapToTask(map));
			}
		}
		return tasks;
	}
}
