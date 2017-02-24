package io.slime.chat.common.redis;

import java.util.Iterator;
import java.util.List;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import redis.clients.jedis.Jedis;

public class RedisDaemonClient extends AbstractRedisClient {

	public RedisDaemonClient(String host, int port, String key) throws Exception {
		System.out.println("REDIS CONNECTION INIT !!! " + host + ":" + port);
		new JedisConnectionPool(host, port);
		serverKey = key;
		pingDaemonServer();
	}

	public RedisDaemonClient(String[] urls, String key) throws Exception {
		new JedisConnectionPool(urls);
		serverKey = key;
		pingDaemonServer();
	}

	public static void pingDaemonServer() {
		Jedis jedis = null;
		JsonObject status = new JsonObject();
		status.put("status", true);
		status.put("time", System.currentTimeMillis());
		try {
			jedis = JedisConnectionPool.getJedisConnection(10);
			HSET(RedisKeyStore.DAEMON.getMaster(), serverKey, status.toString(), jedis);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (jedis != null)
				JedisConnectionPool.close(jedis);
		}
	}

	public static void pingDaemonInstance(String deplomentId, boolean isD) {
		Jedis jedis = null;
		JsonObject status = new JsonObject();
		status.put("status", isD);
		status.put("time", System.currentTimeMillis());
		try {
			jedis = JedisConnectionPool.getJedisConnection();
			jedis.select(10);
			HSET(RedisKeyStore.DAEMON.name(), serverKey + ":" + deplomentId, status.toString(), jedis);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (jedis != null && jedis.isConnected())
				JedisConnectionPool.close(jedis);
		}
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
					RedisDaemonClient.pingDaemonInstance(dID, isD);
			} else if (resStr.indexOf("PING:MASTER") > -1) {
				RedisDaemonClient.pingDaemonServer();
			}
			ret_ = false;
		} else {

		}
		return ret_;
	}

	public static List<String> getPublishMaster(int timeout) {
		Jedis jedis = null;
		try {
			jedis = JedisConnectionPool.getJedisConnection();
			return BRPOP(timeout, RedisKeyStore.PUBLISH.getMaster(), jedis);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if (jedis != null)
				JedisConnectionPool.close(jedis);
		}
	}

	public static String getPublishMasterD() {
		Jedis jedis = null;
		try {
			jedis = JedisConnectionPool.getJedisConnection();
			return RPOP(RedisKeyStore.PUBLISH.getMaster(), jedis);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if (jedis != null)
				JedisConnectionPool.close(jedis);
		}
	}
}
