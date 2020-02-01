package cn.fetosoft.rooster.demo.job;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * @author guobingbing
 * @create 2020/2/1 15:25
 */
@Component
@ServerEndpoint("/websocket/jobMonitor")
public class JobLiveMonitor implements DisposableBean {

	private final Logger logger = LoggerFactory.getLogger(JobLiveMonitor.class);
	private final static BlockingQueue<String> QUEUE = new ArrayBlockingQueue<>(100);

	private static volatile boolean isConn = false;

	public void put(String data){
		if(isConn) {
			QUEUE.offer(data);
		}
	}

	private Session session;

	@OnOpen
	public void openSession(Session session){
		this.session = session;
		try {
			isConn = true;
			while (isConn) {
				String data = QUEUE.poll();
				if(StringUtils.isNotBlank(data)) {
					System.out.println(data);
					session.getBasicRemote().sendText(data);
				}
			}
		} catch (Exception e) {
			logger.error("sendText", e);
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
