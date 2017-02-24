package io.slime.chat.common.redis;

import java.lang.management.ManagementFactory;
import java.util.List;

import redis.clients.jedis.Jedis;

public class AbstractRedisClient {

	protected static String serverKey;

	public static String getServerKey() {
		return serverKey;
	}

	protected static List<String> BRPOP(int timeout, String key) throws Exception {
		Jedis jedis = null;
		try {
			jedis = JedisConnectionPool.getJedisConnection();
			return BRPOP(timeout, key, jedis);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if (jedis != null)
				JedisConnectionPool.close(jedis);
		}
	}

	protected static List<String> BRPOP(int timeout, String key, Jedis jedis) throws Exception {
		List<String> list = jedis.brpop(timeout, key);
		return list;
	}

	protected static String RPOP(String key) throws Exception {
		Jedis jedis = null;
		try {
			jedis = JedisConnectionPool.getJedisConnection();
			return RPOP(key, jedis);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if (jedis != null)
				JedisConnectionPool.close(jedis);
		}
	}

	protected static String RPOP(String key, Jedis jedis) throws Exception {
		String str = jedis.rpop(key);
		return str;
	}

	protected static void LPUSH(String key, String message, Jedis jedis) {
		jedis.lpush(key, message);
	}

	protected static void HSET(String key, String field, String message, Jedis jedis) throws Exception {
		jedis.hset(key, field, message);
	}

	protected static String SESSION(String key) throws Exception {
		return "";
	}

	protected static String HGET(String key, String field) throws Exception {
		Jedis jedis = null;
		try {
			jedis = JedisConnectionPool.getJedisConnection();
			return HGET(jedis, key, field);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if (jedis != null)
				JedisConnectionPool.close(jedis);
		}
	}

	protected static String HGET(Jedis jedis, String key, String field) throws Exception {
		String r = jedis.hget(key, field);
		return r;
	}

	protected static String HGETDEL(String key, String field) throws Exception {
		Jedis jedis = null;
		try {
			jedis = JedisConnectionPool.getJedisConnection();
			return HGETDEL(key, field, jedis);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if (jedis != null)
				JedisConnectionPool.close(jedis);
		}
	}

	protected static String HGETDEL(String key, String field, Jedis jedis) throws Exception {
		String r = jedis.hget(key, field);
		jedis.hdel(key, field);
		return r;
	}

	public static String getPID() {
		return ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
	}

	public AbstractRedisClient() {
		super();
	}

	protected static void LOG_CNT(Jedis jedis, String key, String field) {
		jedis.select(10);
		jedis.zincrby(RedisKeyStore.LOG + ":" + key, -1D, field);
	}

	protected static void LOG_MSG(Jedis jedis, String key, String msg) {
		jedis.select(10);
		jedis.lpush(RedisKeyStore.LOG + ":" + key, msg);
	}
}