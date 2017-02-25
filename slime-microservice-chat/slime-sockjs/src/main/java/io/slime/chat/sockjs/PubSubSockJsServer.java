package io.slime.chat.sockjs;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import io.slime.chat.common.redis.RedisSockjsClient;
import io.slime.chat.common.spring.SpringConfiguration;
import io.slime.chat.common.util.VertxHolder;
import io.slime.chat.sockjs.eventbridge.EventBridgeChain;
import io.slime.chat.sockjs.eventbridge.EventBridgeChainResponse;
import io.slime.chat.sockjs.subscribe.MessageSubscribeVerticle;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.sockjs.BridgeEvent;
import io.vertx.ext.web.handler.sockjs.BridgeEventType;
import io.vertx.ext.web.handler.sockjs.BridgeOptions;
import io.vertx.ext.web.handler.sockjs.PermittedOptions;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;

@Component
public class PubSubSockJsServer extends AbstractVerticle {
	private static final Logger logger = LoggerFactory.getLogger(PubSubSockJsServer.class);
	private int serverPort = 8383;

	public static boolean isStart = false;
	
	private EventBridgeChain eventBridgeChain = (EventBridgeChain) SpringConfiguration.getBean(EventBridgeChain.class);

	 @Override
	  public void start() throws Exception{

		if( VertxHolder.getVertx() == null ) VertxHolder.setVertx(vertx);
		logger.info("Pub/Sub SockJs Server started on port {}", serverPort);
		
		Router router = Router.router(vertx);
		SockJSHandler socketHandler = SockJSHandler.create(vertx)
			.bridge(new BridgeOptions()
					.addOutboundPermitted(new PermittedOptions().setAddressRegex("^topic/.*"))
					
					// use to set client ping timeout -> will result closing connection if no ping sent
					/*.setPingTimeout(60000)*/,
					new Handler<BridgeEvent>() {
						@Override
						public void handle(BridgeEvent event) {
							boolean isResult = true;
							String message = "Oops!";
							
							System.out.println(event.type());
							
							EventBridgeChainResponse processInChain = eventBridgeChain.processInChain(event);
							if(processInChain.isProcesssed()) {
								if(processInChain.getException() != null) {
									isResult = false;
									logger.error(processInChain.getException().getMessage(), 
											processInChain.getException());
									message = processInChain.getException().getMessage();
								}
							}
							
						
							
							if(isResult) {
								
								if(event.type().equals(BridgeEventType.REGISTER)){
									System.out.println(event.getRawMessage());
									RedisSockjsClient.REGISTER(event);
								}
								
								
								event.complete(isResult);
							}
							else {
								event.fail(new Exception(message));
							}
							
						}
					});
		
		
		router.route("/sockjs/*").handler(
	    	socketHandler
		);

		/*
		 * subscribe published message
		 */
		vertx.deployVerticle(new MessageSubscribeVerticle(), new DeploymentOptions().setWorker(true).setMultiThreaded(true));
		vertx.deployVerticle(new MessageSubscribeVerticle(), new DeploymentOptions().setWorker(true).setMultiThreaded(true));
		vertx.deployVerticle(new MessageSubscribeVerticle(), new DeploymentOptions().setWorker(true).setMultiThreaded(true));

	    HttpServer server = vertx.createHttpServer().requestHandler(router::accept);
	    
	    
	    server.listen(serverPort);

	}
}
