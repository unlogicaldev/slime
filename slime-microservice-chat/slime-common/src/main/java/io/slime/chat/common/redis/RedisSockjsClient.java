package io.slime.chat.common.redis;

import java.util.Iterator;
import java.util.List;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.handler.sockjs.BridgeEvent;
import redis.clients.jedis.Jedis;

public class RedisSockjsClient extends AbstractRedisClient {

	public RedisSockjsClient(String host, int port, String key) throws Exception {
		System.out.println("REDIS CONNECTION INIT !!! " + host + ":" + port);
		new JedisConnectionPool(host, port);
		serverKey = key;
		pingDaemonServer();
	}

	public RedisSockjsClient(String[] urls, String key) throws Exception {
		new JedisConnectionPool(urls);
		serverKey = key;
		pingDaemonServer();
	}

	public static void pingDaemonServer() {
		Jedis jedis = null;
		try {
			jedis = JedisConnectionPool.getJedisConnection();
			jedis.select(10);
			JsonObject status = new JsonObject();
			status.put("status", true);
			status.put("time", System.currentTimeMillis());
			jedis.hset(RedisKeyStore.CLIENT.getMaster(), serverKey, status.toString());
			jedis.zadd(RedisKeyStore.CLIENT + ":STATUS", 0, serverKey);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (jedis != null)
				JedisConnectionPool.close(jedis);
		}
	}
	
	public static void pingClientInstance(String deplomentId, boolean isD) {
		Jedis jedis = null;
		try {
			jedis = JedisConnectionPool.getJedisConnection();
			jedis.select(10);
			JsonObject status = new JsonObject();
			status.put("status", isD);
			status.put("time", System.currentTimeMillis());
			HSET(RedisKeyStore.CLIENT.name(), serverKey + ":" + deplomentId, status.toString(), jedis);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (jedis != null)
				JedisConnectionPool.close(jedis);
		}
	}
	
	public static boolean clientManageFunction(String resStr, Vertx vertx) {
		boolean ret_ = true;
		try {
			if (resStr.indexOf("PING:") > -1) {
				if (resStr.indexOf("PING:INSTANCE") > -1) {
					boolean isD = false;
					String dID = resStr.split(":")[2];
					Iterator<String> it = vertx.deploymentIDs().iterator();
					while (it.hasNext()) {
						String id_ = it.next();
						System.out.println(id_ + "$$$");
						if (dID.equals(id_)) {
							isD = true;
							break;
						}
					}
					if (isD)
						pingClientInstance(dID, isD);
				} else if (resStr.indexOf("PING:MASTER") > -1) {
					pingDaemonServer();
				}
				ret_ = false;
			} else {

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret_;
	}
	public static boolean daemonManageFunction(String resStr, Vertx vertx) {
		boolean ret_ = true;
		if (resStr.indexOf("PING:") > -1) {
			if (resStr.indexOf("PING:INSTANCE") > -1) {
				boolean isD = false;
				String dID = resStr.split(":")[2];
				Iterator<String> it = vertx.deploymentIDs().iterator();
				while (it.hasNext()) {
					String dID_ = it.next();
					System.out.println(dID_ + "$$$");
					if (dID.equals(dID_)) {
						isD = true;
						break;
					}
				}
				if (isD)
					RedisSockjsClient.pingClientInstance(dID, isD);
			} else if (resStr.indexOf("PING:MASTER") > -1) {
				RedisSockjsClient.pingDaemonServer();
			}
			ret_ = false;
		} else {

		}
		return ret_;
	}
	
	

	public static List<String> getClientNotify(int timeout) {
		try {
			return BRPOP(timeout, RedisKeyStore.CLIENT + ":" + serverKey);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String getClientNotifyD() {
		try {
			return RPOP(RedisKeyStore.CLIENT + ":" + serverKey);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static void PUBLISH(String address, JsonObject msg) {
		msg.put("address", address);
		Jedis jedis = null;
		try {
			jedis = JedisConnectionPool.getJedisConnection();
			jedis.lpush(RedisKeyStore.PUBLISH.getMaster(), msg.toString());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (jedis != null)
				JedisConnectionPool.close(jedis);
		}
	}

	public static void REGISTER(BridgeEvent event) {
		Jedis jedis = null;
		try {
			JsonObject rawMessage = event.getRawMessage();
			String address = rawMessage.getString("address");
			jedis = JedisConnectionPool.getJedisConnection(10);
			jedis.zincrby(RedisKeyStore.REGIST + ":CHANNELS:" + serverKey, -1, address);
			jedis.zincrby(RedisKeyStore.REGIST + ":SERVERS:" + address, -1, serverKey);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (jedis != null)
				JedisConnectionPool.close(jedis);
		}
	}

}
