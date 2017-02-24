package io.slime.chat.sockjs.eventbridge.chain;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import io.slime.chat.common.redis.RedisSockjsClient;
import io.slime.chat.common.spring.SpringConfiguration;
import io.slime.chat.common.spring.service.RequestLogService;
import io.slime.chat.common.util.VertxHolder;
import io.slime.chat.common.util.WebSocketSessionHolder;
import io.slime.chat.sockjs.eventbridge.EventBridgeChainException;
import io.slime.chat.sockjs.eventbridge.EventBridgeChainHandler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.handler.sockjs.BridgeEvent;
import io.vertx.ext.web.handler.sockjs.BridgeEventType;
import io.vertx.ext.web.handler.sockjs.SockJSSocket;

public class LoginChainHandler implements EventBridgeChainHandler {
	private static final Logger logger = LoggerFactory.getLogger(LoginChainHandler.class);
	
	@Autowired
	private RequestLogService requestLogService = (RequestLogService) SpringConfiguration.getBean("requestLogService");

	@Override
	public boolean handle(BridgeEvent event) throws EventBridgeChainException {
		boolean isHandle = Boolean.FALSE;
		
		if(BridgeEventType.SEND == event.type()) {
			Vertx vertx = VertxHolder.getVertx();
			SockJSSocket sockJSSocket = event.socket();
			
			Map<String, Object> rawMessage = event.getRawMessage().getMap();
			
			
			String replyAddress = (String) rawMessage.get("replyAddress");
			String address = (String) rawMessage.get("address");
			
			if("vertx.basicauthmanager.login".equals(address)) {
				@SuppressWarnings("unchecked")
				Map<String, String> credential = (Map<String, String>) rawMessage.get("body");
				String userId = credential.get("username");
				//String password = credential.get("password");
				
				if(userId == null || "".equals(userId)) {
					logger.warn("Connection rejected");
					sockJSSocket.close();
					
					throw new EventBridgeChainException(true, "No user attached");
				}
				else {
					
					boolean exists = WebSocketSessionHolder.exists(userId);
					if(exists) {
						throw new EventBridgeChainException(true, "User already registered");
					}
					
					sockJSSocket.headers().set(WebSocketSessionHolder.USER_KEY, userId);
					requestLogService.logWebSocketConnection(sockJSSocket);
					
					WebSocketSessionHolder.add(userId, sockJSSocket);

					System.out.println(vertx);
					System.out.println(vertx.eventBus());
					System.out.println(userId);

					// publish there is a new user coming
					vertx.eventBus().publish("topic/chat/user",
				         new JsonObject()
							.put("userId", userId));
					
					// get all online and send back to 
					JsonObject json = new JsonObject()
		                .put("type", "login") // optional
		                .put("address", replyAddress)
		                .put("body", 
	                		new JsonObject()
	                			.put("result", true)
	                			.put("list", WebSocketSessionHolder.getUsers()));
					String data = json.toString();
					
					sockJSSocket.write(Buffer.buffer(data));
					
					isHandle = Boolean.TRUE;
				}
				
			}
			
		}
		return isHandle;
	}

}
