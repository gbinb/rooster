package cn.fetosoft.rooster.demo.controller;

import cn.fetosoft.rooster.core.Result;
import cn.fetosoft.rooster.core.TaskAction;
import cn.fetosoft.rooster.core.TaskException;
import cn.fetosoft.rooster.core.TaskInfo;
import cn.fetosoft.rooster.demo.data.TaskDAO;
import cn.fetosoft.rooster.demo.job.PrintJob;
import cn.fetosoft.rooster.broadcast.TaskBroadcast;
import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * 任务控制
 * @author guobingbing
 * @create 2020/1/26 17:37
 */
@Controller
@RequestMapping("/task")
public class TaskController {

	@Resource
	private TaskBroadcast taskBroadcast;

	@Autowired
	private TaskDAO taskDAO;

	/**
	 * 首页
	 * @return
	 */
	@RequestMapping
	public String index(){
		return "task/index";
	}

	/**
	 * 从mysql中查询任务
	 * @return
	 */
	@RequestMapping("/fromMysql")
	@ResponseBody
	public String getTasksFromMysql(HttpServletRequest request){
		String code = request.getParameter("code");
		String data = "{}";
		List<Map<String, Object>> list = taskDAO.getTasks(code, -1);
		if(!CollectionUtils.isEmpty(list)){
			for(Map<String, Object> map : list){
				if(map.get("start_time")!=null){
					map.put("start_time", DateFormatUtils.format((Date) map.get("start_time"), "yyyy-MM-dd HH:mm:ss"));
				}
				if(map.get("stop_time")!=null){
					map.put("stop_time", DateFormatUtils.format((Date) map.get("stop_time"), "yyyy-MM-dd HH:mm:ss"));
				}
			}
			data = JSON.toJSONString(list);
		}
		return data;
	}

	/**
	 * 查询所有注册的节点
	 * @return
	 */
	@RequestMapping("/getClusters")
	@ResponseBody
	public String getClusters(){
		StringBuilder sb = new StringBuilder();
		List<String> clusters = taskBroadcast.getRegisterdClusters();
		clusters.forEach(s -> {
			sb.append(s).append("，");
		});
		if(sb.length()>0){
			sb.delete(sb.length()-1, sb.length());
		}
		return sb.toString();
	}

	/**
	 * 新增并启动任务
	 */
	@RequestMapping("/add")
	@ResponseBody
	public String add(HttpServletRequest request){
		Map<String, Object> insertMap = new HashMap<>();
		Enumeration<String> enumeration = request.getParameterNames();
		while (enumeration.hasMoreElements()){
			String name = enumeration.nextElement();
			insertMap.put(name, request.getParameter(name));
		}
		return taskDAO.insert(insertMap).toString();
	}

	/**
	 * 修改任务
	 * @return
	 */
	@RequestMapping("/modify")
	@ResponseBody
	public String modify(HttpServletRequest request){
		Map<String, Object> updateMap = new HashMap<>();
		Enumeration<String> enumeration = request.getParameterNames();
		while (enumeration.hasMoreElements()){
			String name = enumeration.nextElement();
			updateMap.put(name, request.getParameter(name));
		}
		return taskDAO.udpate(updateMap).toString();
	}

	/**
	 * 删除任务
	 * @return
	 */
	@RequestMapping("/remove")
	@ResponseBody
	public String remove(@RequestParam String code){
		List<Map<String, Object>> list = taskDAO.getTasks(code, -1);
		if(CollectionUtils.isEmpty(list)){
			return Result.FAIL.setMsg("The task named " + code + " not exists!").toString();
		}
		Map<String, Object> map = list.get(0);
		TaskInfo taskInfo = taskDAO.mapToTask(map);
		Result result = Result.SUCCESS;
		if(taskInfo.getAction()==TaskAction.START.getCode()){
			taskInfo.setAction(TaskAction.STOP.getCode());
			try {
				result = taskBroadcast.broadcast(taskInfo);
			} catch (TaskException e) {
				result = Result.FAIL.setMsg(e.getMessage());
			}
		}
		if(result==Result.SUCCESS){
			result = taskDAO.delete(code);
		}
		return result.toString();
	}

	/**
	 * 启动任务
	 * @return
	 */
	@RequestMapping("/start")
	@ResponseBody
	public String start(@RequestParam String code){
		Result result = null;
		try {
			TaskInfo taskInfo = this.getTaskByCode(code);
			taskInfo.setAction(TaskAction.START.getCode());
			result = taskBroadcast.broadcast(taskInfo);
		} catch (Exception e) {
			result = Result.FAIL.setMsg(e.getMessage());
		}
		return result.toString();
	}

	/**
	 * 停止任务
	 * @return
	 */
	@RequestMapping("/stop")
	@ResponseBody
	public String stop(@RequestParam String code){
		Result result = null;
		try {
			TaskInfo taskInfo = this.getTaskByCode(code);
			taskInfo.setAction(TaskAction.STOP.getCode());
			result = taskBroadcast.broadcast(taskInfo);
			taskDAO.updateTaskStatus(taskInfo.getCode(), TaskAction.STOP);
		} catch (Exception e) {
			result = Result.FAIL.setMsg(e.getMessage());
		}
		return result.toString();
	}


	private TaskInfo getTaskByCode(String code) throws Exception{
		List<Map<String, Object>> list = taskDAO.getTasks(code, -1);
		if(CollectionUtils.isEmpty(list)){
			throw new Exception("The task named " + code + " not exists!");
		}
		Map<String, Object> map = list.get(0);
		return taskDAO.mapToTask(map);
	}
}
