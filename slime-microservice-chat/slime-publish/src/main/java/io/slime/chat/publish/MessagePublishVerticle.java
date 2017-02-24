package io.slime.chat.publish;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import io.slime.chat.common.redis.JedisConnectionPool;
import io.slime.chat.common.redis.RedisDaemonClient;
import io.slime.chat.common.redis.RedisKeyStore;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;
import redis.clients.jedis.Jedis;

@Component
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
					List<String> res = RedisDaemonClient.getNotifyMsgMaster(5);
					if(res != null && res.size() > 0){
						resStr = res.get(1);
					}
				}else{
					resStr = RedisDaemonClient.getNotifyMsgMasterD();
				}
				
				if(resStr != null && !resStr.equals("")){
					System.out.println("[ work something 111"+deploymentID+"]");
					System.out.println(resStr);

					/*
					 * Manage Functions
					 */
					if(RedisDaemonClient.daemonManageFunction(resStr, vertx)){

						Jedis jedis = JedisConnectionPool.getJedisConnection(8);
						try{
							
							String[] str = resStr.split("_");
							String message_id = str[0];
							int start = Integer.parseInt(str[1]);
							int end = (str.length > 2) ? Integer.parseInt(str[2]) : 199;
							
							String msgString = jedis.hget(RedisKeyStore.MESSAGE.getMaster(), message_id);
							if(msgString != null){
								JsonObject msg = new JsonObject(msgString);
								System.out.println("MESSAGE:"+msgString);
							}
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
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
	}

	private void clientNotify(Map<String, List<String>> pushMap, JsonObject u_, int clientType) {
		if(u_.getInteger("status", 0) > 0){
			if(pushMap.get("CLIENT:"+u_.getString("serverKey")) == null){
				pushMap.put("CLIENT:"+u_.getString("serverKey"), new ArrayList<String>());
			}
			pushMap.get("CLIENT:"+u_.getString("serverKey")).add("client.api."+u_.getString("UUID")+"|"+clientType);
		}else{
			System.out.println("client:" + clientType + ":" + u_.toString());
			if(clientType > 1){
				//TODO APNS, GCN
			}
		}
	}
}
