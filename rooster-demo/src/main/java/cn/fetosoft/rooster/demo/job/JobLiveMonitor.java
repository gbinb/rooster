package cn.fetosoft.rooster.demo.job;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * @author guobingbing
 * @create 2020/2/1 15:25
 */
@Component
@ServerEndpoint(value = "/websocket/jobMonitor/{jobCode}")
public class JobLiveMonitor implements DisposableBean {

	private final Logger logger = LoggerFactory.getLogger(JobLiveMonitor.class);
	private final static BlockingQueue<String> QUEUE = new ArrayBlockingQueue<>(100);
	private static volatile boolean isConn = false;
	private static volatile String jobCode = null;
	private Session session;

	public void put(String code, String data){
		if(isConn && code.equals(jobCode)) {
			QUEUE.offer(data);
		}
	}

	@OnOpen
	public void openSession(Session session, @PathParam("jobCode") String code){
		this.session = session;
		jobCode = code;
		isConn = true;
		Thread thread = new Thread(new SendTextTask(session), "sendText");
		thread.start();
	}

	class SendTextTask implements Runnable{

		private Session tsession;

		public SendTextTask(Session session){
			this.tsession = session;
		}

		@Override
		public void run() {
			logger.info("Start sending messages >>>>>>>>>>>>>");
			try{
				while (isConn) {
					String data = QUEUE.poll();
					if(StringUtils.isNotBlank(data)) {
						tsession.getBasicRemote().sendText(data);
					}
				}
			}catch(IOException e){
				logger.error("sendText", e);
			}
			if(!isConn){
				logger.info("Stop sending messages >>>>>>>>>>>>>");
			}
		}
	}

	@OnMessage
	public void acceptMessage(String message){
		logger.info("Accept>>>" + message);
	}

	@OnClose
	public void closeSession(CloseReason closeReason){
		this.close();
		logger.info(closeReason.getReasonPhrase());
	}

	@OnError
	public void errorHandler(Throwable e){
		this.close();
		logger.info("websocket error ：" + e.getMessage());
	}

	private void close(){
		try{
			isConn = false;
			jobCode = null;
			if(session!=null){
				session.close();
			}
		}catch(Exception e){
			logger.error("destory", e);
		}
	}

	@Override
	public void destroy() throws Exception {
		this.close();
	}
}
