package cn.fetosoft.rooster.core;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import org.apache.commons.lang3.StringUtils;
import org.quartz.Job;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @Title：任务信息
 * @Author：guobingbing
 * @Date 2020/1/22 10:31
 * @Description
 * @Version
 */
public class TaskInfo implements Serializable {

	/**
	 * 任务编号，须唯一
	 */
	private String code = StringUtils.EMPTY;

	/**
	 * 名称
	 */
	private String name = StringUtils.EMPTY;

	/**
	 * 状态 1-运行 2-停止，3-删除
	 */
	private Integer action;

	/**
	 * 运行任务的时间表达式
	 */
	private String expression = StringUtils.EMPTY;

	/**
	 * 任务描述
	 */
	private String description = StringUtils.EMPTY;

	/**
	 * JOB实现类
	 */
	private String jobClass = StringUtils.EMPTY;

	/**
	 * 扩展参数
	 */
	private Map<String, Object> paramsMap = new HashMap<>(32);

	/**
	 * 节点主机IP
	 */
	private String clusterIP = StringUtils.EMPTY;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
		this.paramsMap.put("code", code);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
		this.paramsMap.put("name", name);
	}

	public Integer getAction() {
		return action;
	}

	public void setAction(Integer action) {
		this.action = action;
	}

	public String getExpression() {
		return expression;
	}

	public void setExpression(String expression) {
		this.expression = expression;
		this.paramsMap.put("expression", expression);
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getJobClass() {
		return jobClass;
	}

	public void setJobClass(String jobClass) {
		this.jobClass = jobClass;
		this.paramsMap.put("jobClass", jobClass);
	}

	public String getClusterIP() {
		return clusterIP;
	}

	public void setClusterIP(String clusterIP) {
		this.clusterIP = clusterIP;
		this.paramsMap.put("clusterIP", clusterIP);
	}

	/**
	 * 设置参数
	 * @param name
	 * @param value
	 */
	public TaskInfo addParam(String name, Object value){
		this.paramsMap.put(name, value);
		return this;
	}

	/**
	 * 添加更新参数
	 * @param map
	 */
	public TaskInfo addAllParams(Map<String, Object> map){
		this.paramsMap.putAll(map);
		return this;
	}

	public Map<String, Object> getParamsMap(){
		return this.paramsMap;
	}

	@Override
	public String toString(){
		return JSON.toJSONString(this);
	}

	/**
	 * Create task from extended parameters
	 * @return
	 */
	public void buildTask(){
		this.code = (String) paramsMap.get("code");
		this.name = (String) paramsMap.get("name");
		this.expression = (String) paramsMap.get("expression");
		this.jobClass = (String) paramsMap.get("jobClass");
		this.clusterIP = (String) paramsMap.get("clusterIP");
	}
}
