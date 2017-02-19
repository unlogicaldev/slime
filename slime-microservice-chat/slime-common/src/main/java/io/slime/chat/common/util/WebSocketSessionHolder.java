package io.slime.chat.common.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.vertx.ext.web.handler.sockjs.SockJSSocket;

public abstract class WebSocketSessionHolder {
	public static final String USER_KEY = "userId";
	
	private static final Map<String, SockJSSocket> sessions = new HashMap<>();
	
	public static void add(String userId, SockJSSocket session) {
		sessions.put(userId, session);
	}
	
	public static SockJSSocket remove(String userId) {
		return sessions.remove(userId);
	}
	
	public static Map<String, SockJSSocket> getMap() {
		return sessions;
	}
	
	
	public static List<String> getUsers() {
		return new ArrayList<String>(sessions.keySet());
	}
	
	public static SockJSSocket get(String userId) {
		return sessions.get(userId);
	}
	
	public static boolean exists(String userId) {
		return get(userId) != null;
	}
}
