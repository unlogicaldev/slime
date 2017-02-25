package io.slime.chat.publish;

import java.util.Date;
import java.util.List;
import java.util.Set;

import io.slime.chat.common.redis.JedisConnectionPool;
import io.slime.chat.common.redis.RedisDaemonClient;
import io.slime.chat.common.redis.RedisKeyStore;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;
import redis.clients.jedis.Jedis;

public class MessagePublishVerticle extends AbstractVerticle{
	
	@Override
	public void start() throws Exception {
		System.out.println("MessagePublishVerticle Start [" + Thread.currentThread().getId() + "]");
		String deploymentID = this.deploymentID();
		RedisDaemonClient.pingDaemonInstance(deploymentID, true);
				
		vertx.executeBlocking(future -> {
			int i = 0;
			while (i < 5) {
				String resStr = null;
				if(i > 0){
					List<String> res = RedisDaemonClient.getPublishMaster(5);
					if(res != null && res.size() > 0){
						resStr = res.get(1);
					}
				}else{
					resStr = RedisDaemonClient.getPublishMasterD();
				}
				
				if(resStr != null && !resStr.equals("")){
					System.out.println("[ work something 111"+deploymentID+"]");
					System.out.println(resStr);

					/*
					 * Manage Functions
					 */
					if(RedisDaemonClient.daemonManageFunction(resStr, vertx)){

						final Jedis jedis = JedisConnectionPool.getJedisConnection(10);
						try{
							JsonObject msg = new JsonObject(resStr);
							String address = msg.getString("address");
							Set<String> set = jedis.zrangeByScore(RedisKeyStore.REGIST + ":SERVERS:" + address, "-inf", "(0");
							
							jedis.select(0);
							set.forEach(action -> {
								System.out.println("publish sockjs server : " +action);
								jedis.lpush("CLIENT:"+action, msg.toString());
							});
						}catch(Exception e){
							e.printStackTrace();
						}finally{
							if(jedis != null) JedisConnectionPool.close(jedis);
						}
					}
					i = 0;
				}else{
					System.out.println("[ daemon "+deploymentID+" is alive noe : "+new Date()+"]");
					i++;
				}
			}
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
