package io.slime.chat.common.redis;

import java.util.Iterator;
import java.util.List;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import redis.clients.jedis.Jedis;

public class RedisDaemonClient extends AbstractRedisClient {

	public RedisDaemonClient(String host, int port, String key) throws Exception {
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

	/**
	 * @Method Name : getMailMaster
	 * @date : 2016. 7. 14.
	 * @author : goodrhys
	 * @history : master mail notify store
	 *          -----------------------------------------------------------------------
	 *          변경일 작성자 변경내용 ----------- -------------------
	 *          --------------------------------------- 2016. 7. 14. goodrhys 최초
	 *          작성
	 *          -----------------------------------------------------------------------
	 * 
	 * @param timeout
	 * @return
	 */
	public static List<String> getMailMaster(int timeout) {
		try {
			return BRPOP(timeout, RedisKeyStore.NOTIFY_MAIL.getMaster());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String getMailMasterD() {
		try {
			return RPOP(RedisKeyStore.NOTIFY_MAIL.getMaster());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static List<String> getImageUploadMaster(int timeout) {
		try {
			return BRPOP(timeout, RedisKeyStore.NOTIFY_MOBILE_UPLOAD.getMaster());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String getImageUploadMasterD() {
		try {
			return RPOP(RedisKeyStore.NOTIFY_MOBILE_UPLOAD.getMaster());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static List<String> getImageMaster(int timeout) {
		try {
			return BRPOP(timeout, RedisKeyStore.NOTIFY_IMAGE.getMaster());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String getImageMasterD() {
		try {
			return RPOP(RedisKeyStore.NOTIFY_IMAGE.getMaster());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * @Method Name : getQequestMaster
	 * @date : 2016. 7. 22.
	 * @author : goodrhys
	 * @history : master request store
	 *          -----------------------------------------------------------------------
	 *          변경일 작성자 변경내용 ----------- -------------------
	 *          --------------------------------------- 2016. 7. 22. goodrhys 최초
	 *          작성
	 *          -----------------------------------------------------------------------
	 * 
	 * @param timeout
	 * @return
	 */
	public static List<String> getRequestMaster(int timeout) {
		try {
			return BRPOP(timeout, RedisKeyStore.REQUEST.getMaster());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String getRequestMasterD() {
		try {
			return RPOP(RedisKeyStore.REQUEST.getMaster());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static List<String> getRequestAuthMaster(int timeout) {
		try {
			return BRPOP(timeout, RedisKeyStore.REQUEST_AUTH.getMaster());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String getRequestAuthMasterD() {
		try {
			return RPOP(RedisKeyStore.REQUEST_AUTH.getMaster());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static List<String> getRequestMsgMaster(int timeout) {
		try {
			return BRPOP(timeout, RedisKeyStore.PUBLISH.getMaster());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String getRequestMsgMasterD() {
		try {
			return RPOP(RedisKeyStore.PUBLISH.getMaster());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static List<String> getPushMsgMaster(int timeout) {
		try {
			return BRPOP(timeout, RedisKeyStore.PUSH_01.getMaster());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String getPushMsgMasterD() {
		try {
			return RPOP(RedisKeyStore.PUSH_01.getMaster());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static List<String> getNotifySystemMsgMaster(int timeout) {
		Jedis jedis = null;
		try {
			jedis = JedisConnectionPool.getJedisConnection();
			return BRPOP(timeout, RedisKeyStore.NOTIFY_SYSTEM.getMaster(), jedis);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if (jedis != null)
				JedisConnectionPool.close(jedis);
		}
	}

	public static String getNotifySystemMsgMasterD() {
		Jedis jedis = null;
		try {
			jedis = JedisConnectionPool.getJedisConnection();
			return RPOP(RedisKeyStore.NOTIFY_SYSTEM.getMaster(), jedis);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if (jedis != null)
				JedisConnectionPool.close(jedis);
		}
	}

	public static List<String> getNotifySystemMsgNMaster(int timeout) {
		Jedis jedis = null;
		try {
			jedis = JedisConnectionPool.getJedisConnection(8);
			return BRPOP(timeout, RedisKeyStore.NOTIFY_SYSTEM.getMaster() + ":N", jedis);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if (jedis != null)
				JedisConnectionPool.close(jedis);
		}
	}

	public static String getNotifySystemMsgNMasterD() {
		Jedis jedis = null;
		try {
			jedis = JedisConnectionPool.getJedisConnection(8);
			return RPOP(RedisKeyStore.NOTIFY_SYSTEM.getMaster() + ":N", jedis);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if (jedis != null)
				JedisConnectionPool.close(jedis);
		}
	}

	public static List<String> getNotifyMsgDMMaster(int timeout) {
		Jedis jedis = null;
		try {
			jedis = JedisConnectionPool.getJedisConnection(8);
			return BRPOP(timeout, RedisKeyStore.MESSAGEDM + ":NOTIFY", jedis);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if (jedis != null)
				JedisConnectionPool.close(jedis);
		}
	}

	public static String getNotifyMsgDMMasterD() {
		Jedis jedis = null;
		try {
			jedis = JedisConnectionPool.getJedisConnection(8);
			return RPOP(RedisKeyStore.MESSAGEDM + ":NOTIFY", jedis);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if (jedis != null)
				JedisConnectionPool.close(jedis);
		}
	}

	public static List<String> getNotifyMsgMaster(int timeout) {
		Jedis jedis = null;
		try {
			jedis = JedisConnectionPool.getJedisConnection(8);
			return BRPOP(timeout, RedisKeyStore.MESSAGE + ":NOTIFY", jedis);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if (jedis != null)
				JedisConnectionPool.close(jedis);
		}
	}

	public static String getNotifyMsgMasterD() {
		Jedis jedis = null;
		try {
			jedis = JedisConnectionPool.getJedisConnection(8);
			return RPOP(RedisKeyStore.MESSAGE + ":NOTIFY", jedis);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if (jedis != null)
				JedisConnectionPool.close(jedis);
		}
	}

	public static List<String> getBizworksMaster(int timeout) {
		Jedis jedis = null;
		try {
			jedis = JedisConnectionPool.getJedisConnection();
			return BRPOP(timeout, RedisKeyStore.BIZWORKS + ":NOTIFY", jedis);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if (jedis != null)
				JedisConnectionPool.close(jedis);
		}
	}

	public static String getBizworksMasterD() {
		Jedis jedis = null;
		try {
			jedis = JedisConnectionPool.getJedisConnection();
			return RPOP(RedisKeyStore.BIZWORKS + ":NOTIFY", jedis);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if (jedis != null)
				JedisConnectionPool.close(jedis);
		}
	}

	public static List<String> getFCMNotifyMaster(int timeout) {
		Jedis jedis = null;
		try {
			jedis = JedisConnectionPool.getJedisConnection();
			return BRPOP(timeout, RedisKeyStore.NOTIFY_FCM_NOTIFY.getMaster(), jedis);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if (jedis != null)
				JedisConnectionPool.close(jedis);
		}
	}

	public static String getFCMNotifyMasterD() {
		Jedis jedis = null;
		try {
			jedis = JedisConnectionPool.getJedisConnection();
			return RPOP(RedisKeyStore.NOTIFY_FCM_NOTIFY.getMaster(), jedis);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if (jedis != null)
				JedisConnectionPool.close(jedis);
		}
	}

	// getFCMMaster
	public static List<String> getFCMMaster(int timeout) {
		Jedis jedis = null;
		try {
			jedis = JedisConnectionPool.getJedisConnection();
			return BRPOP(timeout, RedisKeyStore.NOTIFY_FCM.getMaster(), jedis);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if (jedis != null)
				JedisConnectionPool.close(jedis);
		}
	}

	public static String getFCMMasterD() {
		Jedis jedis = null;
		try {
			jedis = JedisConnectionPool.getJedisConnection();
			return RPOP(RedisKeyStore.NOTIFY_FCM.getMaster(), jedis);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if (jedis != null)
				JedisConnectionPool.close(jedis);
		}
	}

	public static List<String> getRequestChannelMaster(int timeout, int DB) {
		Jedis jedis = null;
		try {
			jedis = JedisConnectionPool.getJedisConnection(DB);
			return BRPOP(timeout, RedisKeyStore.PUBLISH.getMaster(), jedis);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if (jedis != null)
				JedisConnectionPool.close(jedis);
		}
	}

	public static String getRequestChannelMasterD(int DB) {
		Jedis jedis = null;
		try {
			jedis = JedisConnectionPool.getJedisConnection(DB);
			return RPOP(RedisKeyStore.PUBLISH.getMaster(), jedis);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if (jedis != null)
				JedisConnectionPool.close(jedis);
		}
	}

	public static List<String> getNotifyMaster(int timeout) {
		Jedis jedis = null;
		try {
			jedis = JedisConnectionPool.getJedisConnection();
			return BRPOP(timeout, RedisKeyStore.NOTIFY.getMaster());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if (jedis != null)
				JedisConnectionPool.close(jedis);
		}
	}

	public static List<String> getManage(int timeout) {
		Jedis jedis = null;
		try {
			jedis = JedisConnectionPool.getJedisConnection();
			return BRPOP(timeout, RedisKeyStore.MANAGE.getMaster());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if (jedis != null)
				JedisConnectionPool.close(jedis);
		}
	}

	public static List<String> getUserMaster(int timeout) {
		Jedis jedis = null;
		try {
			jedis = JedisConnectionPool.getJedisConnection();
			return BRPOP(timeout, RedisKeyStore.NOTIFY_USER.getMaster());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if (jedis != null)
				JedisConnectionPool.close(jedis);
		}
	}

	public static String getUserMasterD() {
		Jedis jedis = null;
		try {
			jedis = JedisConnectionPool.getJedisConnection();
			return RPOP(RedisKeyStore.NOTIFY_USER.getMaster());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if (jedis != null)
				JedisConnectionPool.close(jedis);
		}
	}

	public static List<String> getRequestSrarchMaster(int timeout) {
		Jedis jedis = null;
		try {
			jedis = JedisConnectionPool.getJedisConnection();
			return BRPOP(timeout, RedisKeyStore.SEARCH.getMaster());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if (jedis != null)
				JedisConnectionPool.close(jedis);
		}
	}

	public static String getRequestSearchMasterD() {
		Jedis jedis = null;
		try {
			jedis = JedisConnectionPool.getJedisConnection();
			return RPOP(RedisKeyStore.SEARCH.getMaster(), jedis);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if (jedis != null)
				JedisConnectionPool.close(jedis);
		}
	}

	public static List<String> getAgentString(int timeout) {
		Jedis jedis = null;
		try {
			jedis = JedisConnectionPool.getJedisConnection(10);
			return BRPOP(timeout, RedisKeyStore.AGENT + ":" + serverKey);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if (jedis != null)
				JedisConnectionPool.close(jedis);
		}
	}

	public static void SET_BYTE_IMAGE_ARRAY(String key, int index, String[] byteString) {
		Jedis jedis = null;
		try {
			jedis = JedisConnectionPool.getJedisConnection(9);
			jedis.rpush(RedisKeyStore.IMAGEDATA.getMaster() + key + ":" + index, byteString);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (jedis != null)
				JedisConnectionPool.close(jedis);
		}
	}

	public static String GET_BYTE_IMAGE(String key, int start, int end) {
		Jedis jedis = null;
		String result = null;
		try {
			jedis = JedisConnectionPool.getJedisConnection(9);
			result = jedis.lrange(key, start, end).get(0);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (jedis != null)
				JedisConnectionPool.close(jedis);
		}
		return result;
	}

	public static void SET_BYTE_IMAGE_INFO(String key, String json) {
		Jedis jedis = null;
		try {
			jedis = JedisConnectionPool.getJedisConnection(9);

			if (jedis.llen("IMAGELIST") > 10000) {
				String delInfo = RedisKeyStore.IMAGEINFO.getMaster() + jedis.rpop("IMAGELIST");
				JsonObject delObj = new JsonObject(jedis.get(delInfo));
				JsonArray delArr = delObj.getJsonArray("resultArray");
				for (Object o : delArr) {
					JsonObject o_ = (JsonObject) o;
					String k = RedisKeyStore.IMAGEDATA.getMaster() + o_.getString("key");
					jedis.del(k);
				}
				jedis.del(delInfo);
			}
			jedis.lpush("IMAGELIST", key);

			jedis.set(RedisKeyStore.IMAGEINFO.getMaster() + key, json);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (jedis != null)
				JedisConnectionPool.close(jedis);
		}
	}

	public static String GET_BYTE_IMAGE_INFO(String key) {
		Jedis jedis = null;
		String result = null;
		try {
			jedis = JedisConnectionPool.getJedisConnection(9);
			result = jedis.get(RedisKeyStore.IMAGEINFO.getMaster() + key);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (jedis != null)
				JedisConnectionPool.close(jedis);
		}
		return result;
	}
}
