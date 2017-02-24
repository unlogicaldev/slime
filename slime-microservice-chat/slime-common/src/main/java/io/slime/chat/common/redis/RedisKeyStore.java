package io.slime.chat.common.redis;

/**
 * <pre>
 * com.ontheit.redis.common 
 *    |_ RedisKeyStore.java
 * 
 * </pre>
 * 
 * @date : 2016. 7. 13. 오전 11:06:23
 * @version :
 * @author : goodrhys
 */
public enum RedisKeyStore {

	BIZWORKS("BIZWORKS:MASTER"), CHANNEL("CHANNEL:MASTER"), CLIENT("CLIENT:MASTER"), FAVORITE(
			"FAVORITE:MASTER"), INSTANCE("CLIENT:INSTANCE:MASTER"), DAEMON("DAEMON:MASTER"), MAIL("MAIL:MASTER"), IP(
					"IP:MASTER"), MESSAGE("MESSAGE:MASTER"), MESSAGEDM("MESSAGEDM:MASTER"), MANAGE(
							"MANAGE:MASTER"), NOTIFY("NOTIFY:MASTER"), NOTIFY_MAIL("NOTIFY:MAIL:MASTER"), NOTIFY_FCM(
									"NOTIFY:FCM:MASTER"), NOTIFY_FCM_NOTIFY("NOTIFY:FCM:NOTIFY:MASTER"), NOTIFY_SYSTEM(
											"NOTIFY:SYSTEM:MASTER"), NOTIFY_IMAGE(
													"NOTIFY:IMAGE:MASTER"), NOTIFY_MOBILE_UPLOAD(
															"NOTIFY:UPLOAD:MOBILE"), NOTIFY_USER(
																	"NOTIFY:USER:MASTER"), PROFILE(
																			"PROFILE:MASTER"), PUBLISH(
																					"REQUEST:PUBLISH:MASTER"), PUSH_01(
																							"PUSH:01:MASTER"), PUSH_02(
																									"PUSH:02:MASTER"), PUSH_03(
																											"PUSH:03:MASTER"), REQUEST(
																													"REQUEST:MASTER"), REQUEST_AUTH(
																															"REQUEST:AUTH:MASTER"), REGIST(
																																	"REQUEST:REGIST"), USER(
																																			"USER:MASTER"), UPLOAD_EDM(
																																					"UPLOAD:EDM:MASTER"), USERAUTH(
																																							"USER:AUTH:MASTER"), USEREMAIL(
																																									"USER:EMAIL:MASTER"), USERSESSION(
																																											"USER:SESSION:MASTER"), USERSTATUS(
																																													"USER:STATUS:MASTER"), VERTX(
																																															"VERTX:MASTER"), LOG(
																																																	"LOG:MASTER"), SEARCH(
																																																			"SEARCH:MASTER"), SYSTEM_PROP(
																																																					"SYSTEM:PROP:MASTER"), SYSTEM_CONFIG_MSG(
																																																							"SYSTEM:CONFIG:MSG"), SYSTEM_LEGACY_URL(
																																																									"SYSTEM:LEGACY:URL"), AGENT(
																																																											"SEARCH:MASTER"), IMAGEINFO(
																																																													"IMAGE:INFO:"), IMAGEDATA(
																																																															"IMAGE:DATA:"), IMAGEWAIT(
																																																																	"IMAGE:WAIT:");

	private String master;

	RedisKeyStore(String master) {
		this.master = master;
	}

	public String getMaster() {
		return this.master;
	}
}
