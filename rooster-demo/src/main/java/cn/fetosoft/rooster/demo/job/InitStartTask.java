package cn.fetosoft.rooster.demo.job;

import cn.fetosoft.rooster.broadcast.InitBroadcastTask;
import cn.fetosoft.rooster.core.TaskAction;
import cn.fetosoft.rooster.core.TaskInfo;
import cn.fetosoft.rooster.demo.data.TaskDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author guobingbing
 * @create 2020/1/29 14:53
 */
@Component
public class InitStartTask implements InitBroadcastTask {

	@Autowired
	private TaskDAO taskDAO;

	@Override
	public List<TaskInfo> getStartTasks() {
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
