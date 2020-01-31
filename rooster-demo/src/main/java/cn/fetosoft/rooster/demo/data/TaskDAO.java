package cn.fetosoft.rooster.demo.data;

import cn.fetosoft.rooster.core.Result;
import cn.fetosoft.rooster.core.TaskAction;
import cn.fetosoft.rooster.core.TaskInfo;
import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author guobingbing
 * @create 2020/1/30 14:55
 */
@Repository
public class TaskDAO {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	/**
	 * map to task
	 * @param map
	 * @return
	 */
	public TaskInfo mapToTask(Map<String, Object> map){
		TaskInfo taskInfo = new TaskInfo();
		taskInfo.setCode((String) map.get("code"));
		taskInfo.setName((String) map.get("name"));
		taskInfo.setExpression((String) map.get("expression"));
		taskInfo.setDescription((String)map.get("description"));
		taskInfo.setJobClass((String)map.get("jobClass"));
		taskInfo.setClusterIP((String)map.get("clusterIP"));
		taskInfo.setAction((Integer) map.getOrDefault("status", 0));
		String params =(String) map.get("params");
		if(StringUtils.isNotBlank(params)){
			taskInfo.addAllParams(JSON.parseObject(params));
		}
		return taskInfo;
	}

	/**
	 *
	 * @return
	 */
	public List<Map<String, Object>> getTasks(String code, int status){
		boolean isWhere = false;
		StringBuilder sb = new StringBuilder();
		if(StringUtils.isNotBlank(code)){
			if(isWhere){
				sb.append(" and ");
			}
			sb.append(" code='").append(code).append("'");
			isWhere = true;
		}
		if(status>=0){
			if(isWhere){
				sb.append(" and ");
			}
			sb.append(" status=" + status);
			isWhere = true;
		}
		if(isWhere){
			sb.insert(0, " where ");
		}
		return jdbcTemplate.queryForList("select * from tasks "+sb.toString()+" order by start_time desc");
	}

	/**
	 * add
	 * @param param
	 * @return
	 */
	public Result insert(Map<String, Object> param){
		String sql = "insert into tasks(code,name,description,expression,jobClass,clusterIP,params,status,create_time) "
				+ " values(?,?,?,?,?,?,?,?,?)";
		int flag = jdbcTemplate.update(sql, param.get("code"),
				param.get("name")==null?"":param.get("name"),
				param.get("description")==null?"":param.get("description"),
				param.get("expression")==null?"":param.get("expression"),
				param.get("jobClass")==null?"":param.get("jobClass"),
				param.get("clusterIP")==null?"":param.get("clusterIP"),
				param.get("params")==null?"":param.get("params"),
				0, new Date());
		return flag>0?Result.SUCCESS:Result.FAIL;
	}

	/**
	 *
	 * @param param
	 * @return
	 */
	public Result udpate(Map<String, Object> param){
		String sql = "update tasks set name=?,description=?,expression=?,jobClass=?,clusterIP=?,params=? where code=?";
		int flag = jdbcTemplate.update(sql,
				param.get("name")==null?"":param.get("name"),
				param.get("description")==null?"":param.get("description"),
				param.get("expression")==null?"":param.get("expression"),
				param.get("jobClass")==null?"":param.get("jobClass"),
				param.get("clusterIP")==null?"":param.get("clusterIP"),
				param.get("params")==null?"":param.get("params"),
				param.get("code"));
		return flag>0?Result.SUCCESS:Result.FAIL;
	}

	/**
	 * 更新状态
	 * @param code
	 * @param action
	 * @return
	 */
	public Result updateTaskStatus(String code, TaskAction action){
		String sql = "update tasks set status=?";
		if(action==TaskAction.START){
			sql += ",start_time=?";
		}else{
			sql += ",stop_time=?";
		}
		sql += " where code=?";
		int flag = jdbcTemplate.update(sql, action.getCode(), new Date(), code);
		return flag>0?Result.SUCCESS:Result.FAIL;
	}

	/**
	 *
	 * @param code
	 * @return
	 */
	public Result delete(String code){
		String sql = "delete from tasks where code=?";
		int flag = jdbcTemplate.update(sql, code);
		return flag>0?Result.SUCCESS:Result.FAIL;
	}
}