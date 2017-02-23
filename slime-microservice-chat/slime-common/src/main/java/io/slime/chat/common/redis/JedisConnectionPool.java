package io.slime.chat.common.redis;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisConnectionException;

public class JedisConnectionPool {

	private static JedisPool jedisPoolA;
	private static String host;
	private static int port;

	private static boolean status = true;
	private static JedisPool jedisPoolB;

	private static List<JedisPool> jedisPools = new ArrayList<JedisPool>();
	private static String[] arrAddress;

	public static void close(Jedis jedis) {
		if (jedis != null) {
			if (jedis != null && jedis.isConnected()) {
				jedis.close();
			}
		}
	}

	public JedisConnectionPool() {
		if (jedisPoolA == null) {
			String host = "localhost";
			int port = 6379;
			try {
				initJedisPool(host, port);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @param host
	 *            redis.server.ip
	 * @param port
	 *            redis.server.port
	 */
	public JedisConnectionPool(String h, int p) throws Exception {
		if (jedisPoolA == null) {
			host = h;
			port = p;
			try {
				JedisPool pool = initJedisPool(host, port);
				if (pool != null) {
					jedisPoolA = pool;
				} else {
					throw new Exception("Redis Connection Error - " + h + ":" + p);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public JedisConnectionPool(String[] urls) throws Exception {
		if (jedisPoolA == null) {
			arrAddress = urls;
			for (String url : urls) {
				String h = url.split("\\:")[0];
				int p = Integer.parseInt(url.split("\\:")[1]);
				JedisPool pool = initJedisPool(h, p);
				if (pool != null) {
					jedisPools.add(pool);
				} else {
					continue;
				}
			}

			jedisPoolA = jedisPools.get(0);
			jedisPools.remove(0);
			if (jedisPools.size() > 0) {
				jedisPoolB = jedisPools.get(0);
				startTimmerForHA();
			}
		}
	}

	/**
	 * @Method Name : getJedisConnection
	 * @date : 2016. 7. 13.
	 * @author : goodrhys
	 * @history :
	 *          -----------------------------------------------------------------------
	 *          변경일 작성자 변경내용 ----------- -------------------
	 *          --------------------------------------- 2016. 7. 13. goodrhys 최초
	 *          작성
	 *          -----------------------------------------------------------------------
	 * 
	 * @return
	 */
	public static Jedis getJedisConnection() { // koushik: removed static
		try {
			if (jedisPoolA == null) {
				String host = "localhost";
				int port = 6379;

				try {
					initJedisPool(host, port);
				} catch (Exception e) {
					e.printStackTrace();
					return null;
				}
			}
			// System.out.println(jedisPoolA);
			// System.out.println(host);
			// System.out.println(jedisPoolA.getNumActive());
			// System.out.println(jedisPoolA.getNumIdle());
			// System.out.println(jedisPoolA.isClosed());
			// System.out.println(jedisPoolA.getNumWaiters());
			// System.out.println(jedisPoolA.toString());
			return jedisPoolA.getResource();
		} catch (JedisConnectionException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Jedis getJedisConnection(int db) { // koushik: removed static
		try {
			if (jedisPoolA == null) {
				String host = "localhost";
				int port = 6379;

				initJedisPool(host, port);
			}
			// System.out.println(jedisPool);
			// System.out.println(jedisPool.getNumActive());
			// System.out.println(jedisPool.getNumIdle());
			// System.out.println(jedisPool.isClosed());
			// System.out.println(jedisPool.getNumWaiters());
			// System.out.println(jedisPool.toString());
			Jedis jedis = jedisPoolA.getResource();
			jedis.select(db);

			return jedis;
		} catch (Exception e) {
			if (jedisPoolA == null) {
				System.out.println(jedisPoolA);
				System.out.println(jedisPoolA.getNumActive());
				System.out.println(jedisPoolA.getNumIdle());
				System.out.println(jedisPoolA.isClosed());
				System.out.println(jedisPoolA.getNumWaiters());
				System.out.println(jedisPoolA.toString());
			}
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * @Method Name : initJedisPool
	 * @date : 2016. 7. 13.
	 * @author : goodrhys
	 * @history :
	 *          -----------------------------------------------------------------------
	 *          변경일 작성자 변경내용 ----------- -------------------
	 *          --------------------------------------- 2016. 7. 13. goodrhys 최초
	 *          작성
	 *          -----------------------------------------------------------------------
	 * 
	 * @param host
	 * @param port
	 */
	private static JedisPool initJedisPool(String host, int port) throws Exception {
		JedisPool r = null;
		JedisPoolConfig config = new JedisPoolConfig();
		config.setMaxTotal(20);
		config.setMaxIdle(10);
		config.setMinIdle(10);
		config.setMaxWaitMillis(30000);
		try {
			r = new JedisPool(config, host, port);
			Jedis j = r.getResource();
			j.close();
		} catch (JedisConnectionException e) {
			r = null;
			e.printStackTrace();
		} catch (Exception e) {
			r = null;
			e.printStackTrace();
		}

		return r;
	}

	private static JedisPool initJedisPool(String host, int port, int maxCnt) throws Exception {
		JedisPool r = null;
		JedisPoolConfig config = new JedisPoolConfig();
		config.setMaxTotal(maxCnt);
		config.setMaxIdle(10);
		config.setMinIdle(10);
		config.setMaxWaitMillis(30000);
		try {
			r = new JedisPool(config, host, port);
			Jedis j = r.getResource();
			j.close();
		} catch (JedisConnectionException e) {
			r = null;
			e.printStackTrace();
		} catch (Exception e) {
			r = null;
			e.printStackTrace();
		}

		return r;
	}

	public static void changePool() {
		if (status) {
			status = false;
			Jedis jedis = null;
			try {
				jedis = jedisPoolB.getResource();
				String ping = jedis.ping();
				if (ping != null && ping.equals("PONG")) {

					// TODO reset redis now !

					jedisPoolA = jedisPoolB;
					if (jedisPools.size() > 0) {
						jedisPoolB = jedisPools.get(0);
						jedisPools.remove(0);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (jedis != null)
					jedis.close();
			}
		}
	}

	private void startTimmerForHA() {
		int delay = 5000;// delay for 5 sec.
		int interval = 1000; // iterate every sec.
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			public void run() {
				Jedis jedis = null;
				try {
					jedis = JedisConnectionPool.getJedisConnection();
					String ping = jedis.ping();
					// System.out.println("22222");
					changeRedisPool();
				} catch (Exception e) {
					changeRedisPool();
				} finally {
					if (jedis != null)
						JedisConnectionPool.close(jedis);
				}
			}
		}, delay, interval);
	}

	private void changeRedisPool() {
		// System.out.println("111111");
	}
}