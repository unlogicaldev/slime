package io.slime.chat.publish;

import io.slime.chat.common.redis.RedisDaemonClient;
import io.vertx.core.AbstractVerticle;

public class MessagePublishVerticle extends AbstractVerticle{
	
	@Override
	public void start() throws Exception {
		System.out.println("MessagePublishVerticle Start [" + Thread.currentThread().getId() + "]");
		String deploymentID = this.deploymentID();
		RedisDaemonClient.pingDaemonInstance(deploymentID, true);
				
		vertx.executeBlocking(future -> {
			/**
			 * TODO MESSAGE PUBLISH TO SOCKJS SERVERS
			 */
			future.complete(deploymentID);
		}, result -> {
			System.out.println("[ end verticle  : "+ result.result() +"]");
			try {
				
				this.stop();
				this.start();
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}
}
