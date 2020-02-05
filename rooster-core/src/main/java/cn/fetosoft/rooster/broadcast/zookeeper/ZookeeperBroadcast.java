package cn.fetosoft.rooster.broadcast.zookeeper;

import cn.fetosoft.rooster.broadcast.AbstractTaskBroadcast;
import cn.fetosoft.rooster.broadcast.InitBroadcastTask;
import cn.fetosoft.rooster.core.*;
import cn.fetosoft.rooster.utils.NetUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.apache.zookeeper.server.ZooTrace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Title：zookeeper客户端
 * @Author：guobingbing
 * @Date 2017/3/31 11:18
 * @Description
 * @Version
 */
@Component("zookeeperBroadcast")
public class ZookeeperBroadcast extends AbstractTaskBroadcast {
	/**
	 * 记录日志
	 */
	private static final Logger logger = LoggerFactory.getLogger(ZookeeperBroadcast.class);

	/**
	 * 线程池
	 */
	private static final ExecutorService THREAD_POOL = new ThreadPoolExecutor(1, 5, 5*60, TimeUnit.SECONDS, new ArrayBlockingQueue<>(1000));

	/**
	 * Zk客户端
	 */
	private CuratorFramework client = null;

	private TreeCache treeCache;

	@Autowired
	private ScheduledService scheduledService;

	@Autowired
	private RoosterConfig roosterConfig;

	@Autowired(required = false)
	private InitBroadcastTask initBroadcastTask;

	/**
	 * 创建zk连接
	 */
	private void createClientAndRegist() throws Exception{
		if(client==null) {
			String host = this.roosterConfig.getZkHost();
			RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
			client = CuratorFrameworkFactory.newClient(host, retryPolicy);
			client.start();
			logger.info("The CuratorFramework is connected! >>> {}", host);

			//将本机信息注册到zk中
			String localIp = NetUtil.getLocalIP().get(0);
			String clusterPath = this.roosterConfig.getZkClusterPath() + "/" + NetUtil.ipToLong(localIp);
			client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(clusterPath);
			client.setData().forPath(clusterPath, localIp.getBytes("utf-8"));
		}
	}

	/**
	 * 注册
	 *
	 * @param taskInfo
	 * @return
	 */
	@Override
	protected Result doBroadcast(TaskInfo taskInfo) throws Exception {
		Result result = Result.FAIL;
		String clusterPath = this.roosterConfig.getZkClusterPath() + "/" + NetUtil.ipToLong(taskInfo.getClusterIP());
		if(this.client.checkExists().forPath(clusterPath)==null){
			result.setMsg("The cluster with ip " + taskInfo.getClusterIP() + " not exists!");
			return result;
		}

		String path = this.roosterConfig.getZkTaskPath() + "/" + taskInfo.getCode();
		if(taskInfo.getAction() == TaskAction.START.getCode() && this.client.checkExists().forPath(path)!=null) {
			result.setMsg("The node named " + taskInfo.getCode() + " exists!");
			return result;
		}
		if(taskInfo.getAction() == TaskAction.MODIFY.getCode() && this.client.checkExists().forPath(path)==null){
			result.setMsg("The node named " + taskInfo.getCode() + " not exists!");
			return result;
		}

		if (client.checkExists().forPath(path) == null) {
			client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(path);
		}
		Stat stat = this.client.setData().forPath(path, taskInfo.toString().getBytes("utf-8"));
		if (stat.getCzxid() > 0) {
			logger.info("The czxid is {} of doBroadcast! >>> {}", stat.getCzxid(), taskInfo.getCode());
			result = Result.SUCCESS;
		}
		logger.info("The node data is create success! >>> {} >>> {}", taskInfo.getCode(), taskInfo.getAction());
		return result;
	}

	/**
	 * 订阅zk节点变化
	 * @author guobingbing
	 * @date 2020/1/27 14:50
	 * @param
	 * @return void
	 */
	private void subScription(){
		if(this.roosterConfig.isEnableSub()){
			String nodePath = this.roosterConfig.getZkTaskPath();
			try{
				if(client.checkExists().forPath(nodePath)==null) {
					client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(nodePath);
				}
				this.treeCache = new TreeCache(client, nodePath);
				treeCache.getListenable().addListener(new TreeCacheListener() {
					@Override
					public void childEvent(CuratorFramework curatorFramework, TreeCacheEvent event) throws Exception {
						TreeCacheEvent.Type type = event.getType();
						if(type == TreeCacheEvent.Type.NODE_ADDED || type == TreeCacheEvent.Type.NODE_UPDATED)
						{
							String taskJSON = new String(event.getData().getData(), "utf-8");
							if(taskJSON.startsWith("{") && taskJSON.endsWith("}")) {
								JSONObject jsonObject = JSONObject.parseObject(taskJSON);
								TaskInfo taskInfo = JSONObject.toJavaObject(jsonObject, TaskInfo.class);
								taskInfo.addAllParams(jsonObject.getJSONObject("paramsMap"));
								int action = jsonObject.getInteger("action");
								if (action == TaskAction.START.getCode()) {
									scheduledService.start(taskInfo);
								} else if (action == TaskAction.MODIFY.getCode()) {
									scheduledService.modify(taskInfo);
								} else if (action == TaskAction.STOP.getCode()) {
									Result result = scheduledService.stop(taskInfo);
									if(result == Result.SUCCESS){
										curatorFramework.delete().forPath(nodePath + "/" + taskInfo.getCode());
									}
								}
							}
						}
					}
				});
				treeCache.start();
				logger.info("The task subScription is create success!-----------------");
			}catch(Exception e){
				logger.error("nodeListener", e);
			}
		}
	}

	/**
	 * 初始化
	 */
	@Override
	public void start() throws Exception {
		this.createClientAndRegist();
		this.subScription();
	}

	/**
	 * 销毁资源
	 */
	@Override
	public void close() {
		try{
			if(client!=null) {
				if(treeCache!=null){
					treeCache.close();
				}
				ZooTrace.logTraceMessage(logger, ZooTrace.getTextTraceLevel(), "Run shutdown now.");
				client.close();
				logger.info("CuratorFramework run shutdown now!");
			}
			if(!THREAD_POOL.isShutdown()){
				THREAD_POOL.shutdown();
				logger.info("Shutdown the threadpool!");
			}
		}catch(Exception e){
			logger.error("destroy", e);
		}
	}

	@Override
	protected List<TaskInfo> getInitStartTasks() {
		if(initBroadcastTask!=null){
			return initBroadcastTask.getStartTasks();
		}
		return null;
	}

	@Override
	public List<TaskInfo> getRegisterdTasks(){
		List<TaskInfo> tasks = new ArrayList<>();
		String path = roosterConfig.getZkTaskPath();
		try {
			List<String> list = this.client.getChildren().forPath(path);
			for(String dataPath : list){
				String data = new String(this.client.getData().forPath(path + "/" + dataPath), "utf-8");
				JSONObject json = JSON.parseObject(data);
				TaskInfo taskInfo = JSON.toJavaObject(json, TaskInfo.class);
				taskInfo.addAllParams(json.getJSONObject("paramsMap"));
				tasks.add(taskInfo);
			}
		} catch (Exception e) {
			logger.error("getRegisterdTasks", e);
		}
		return tasks;
	}
}
