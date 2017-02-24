package io.slime.chat.sockjs.subscribe;

import io.slime.chat.common.redis.RedisSockjsClient;
import io.vertx.core.AbstractVerticle;

public class MessageSubscribeVerticle extends AbstractVerticle{

	@Override
	public void start() throws Exception {

		System.out.println("MessageSubscribeVerticle Start [" + this.deploymentID() + "]");
		RedisSockjsClient.pingClientInstance(this.deploymentID(), true);
		
		vertx.executeBlocking(future -> {
			/**
			 * TODO MESSAGE SUBSCRIBE
			 */
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
