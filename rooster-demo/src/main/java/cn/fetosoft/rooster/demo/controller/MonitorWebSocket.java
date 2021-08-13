package cn.fetosoft.rooster.demo.controller;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationListener;
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
public class MonitorWebSocket implements DisposableBean, ApplicationListener<MonitorEvent> {

	private final Logger logger = LoggerFactory.getLogger(MonitorWebSocket.class);
	private final static BlockingQueue<MonitorEvent> QUEUE = new ArrayBlockingQueue<>(100);
	private static volatile boolean isConn = false;
	private static volatile String jobCode = null;
	private Session session;

	@OnOpen
	public void openSession(Session session, @PathParam("jobCode") String code){
		this.session = session;
		jobCode = code;
		isConn = true;
		Thread thread = new Thread(new SendTextTask(session), "sendText");
		thread.start();
	}

	@Override
	public void onApplicationEvent(MonitorEvent event) {
		if(isConn && event.getCode().equals(jobCode)) {
			QUEUE.offer(event);
		}
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
					MonitorEvent event = QUEUE.poll();
					if(event!=null) {
						tsession.getBasicRemote().sendText(JSON.toJSONString(event));
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
