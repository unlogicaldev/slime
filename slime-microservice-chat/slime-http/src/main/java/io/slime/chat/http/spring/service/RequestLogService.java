package io.slime.chat.http.spring.service;

import io.vertx.core.http.ServerWebSocket;
import io.vertx.ext.web.handler.sockjs.SockJSSocket;

public interface RequestLogService {
	public void print(String message);
	void logWebSocketConnection(ServerWebSocket webSocket);
	void logWebSocketConnection(SockJSSocket sockJSSocket);
}
