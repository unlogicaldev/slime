package io.slime.chat.sockjs.subscribe;

import java.util.List;

import io.slime.chat.common.redis.RedisSockjsClient;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;

public class MessageSubscribeVerticle extends AbstractVerticle{

	@Override
	public void start() throws Exception {
		System.out.println("MessageSubscribeVerticle Start [" + this.deploymentID() + "]");
		RedisSockjsClient.pingClientInstance(this.deploymentID(), true);
		
		vertx.executeBlocking(future -> {
			int i = 0;
			while (i < 10) {
				String resStr = null;
				if(i > 0){
					List<String> res = RedisSockjsClient.getClientNotify(10); // 3 --> Channel Store
					if(res != null && res.size() > 0){
						resStr = res.get(1);
					}
				}else{
					resStr = RedisSockjsClient.getClientNotifyD(); // 3 --> Channel Store
				}
				
				if(resStr != null && !resStr.trim().equals("")){
					System.out.println("["+Thread.currentThread().getId()+"]");
					System.out.println(resStr);
					
					/*
					 * Manage Functions
					 */
					if(RedisSockjsClient.clientManageFunction(resStr, vertx)){

						/**
						 * publish Message
						 */
						JsonObject msg = new JsonObject(resStr);
						if(msg.getValue("address") != null){
							String address = msg.getString("address");
							msg.remove("address");
							vertx.eventBus().publish(address, msg);
						}
						
					}
					i = 0;
				}else{
					i++;
				}
			}
			future.complete(true);
		}, result -> {
			try {
				this.stop();
				this.start();
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}
}
